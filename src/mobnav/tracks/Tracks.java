package mobnav.tracks;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */
//import java.io.IOException;
//import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Stack;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.*;
    //-----------------------------------------------------------------------
//import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStore;

import mobnav.canvas.BufferedRead;
import mobnav.canvas.FILE;
import mobnav.canvas.Graph;
import mobnav.canvas.Interface;
import mobnav.canvas.Loading;
import mobnav.canvas.MAP;
import mobnav.canvas.MobNav1;
import mobnav.canvas.MyCanvas;
import mobnav.canvas.NAVI;
import mobnav.canvas.Point;
import mobnav.canvas.Storage;
import mobnav.canvas.TEXT;
//import java.io.DataInputStream;
import java.io.DataOutputStream;

//----------------------------------------------------------------------------
class TrackUpDown{
	private float maxH,minH,dUp,dDown;
	private int errorsUp,errorsDown;
	private static final int MAX_ERRORS=5;
	public TrackUpDown(final float h){
		maxH=minH=h;
		dUp=dDown=0;
		errorsUp=errorsDown=0;		
	}
	public int getUp(){
		return (int)dUp;
	}
	public int getDown(){
		return (int)dDown;
	}
	public void add(final float h){
		if (maxH<h){
			dUp+=h-maxH;
			maxH=h;
			errorsUp=0;
		}else 
			if (errorsUp<MAX_ERRORS)				
				errorsUp++;
			else{
				maxH=h;
				errorsUp=0;
			}		
		if (minH>h){
			dDown+=h-minH;
			minH=h;
			errorsDown=0;
		}else
			if (errorsDown<MAX_ERRORS)
				errorsDown++;
			else{
				minH=h;
				errorsDown=0;
			}								
	}
}
//----------------------------------------------------------------------------



public class Tracks {
	public static boolean dont_show_path_info=false,graph=false;
	public static Path path=null;
	public static Path runner=null;	
	public static TrackConductorDriver tc=null,wtc=null;
    private static Stack tracks=new Stack();
   // private static int trackI=0;
        
    static public boolean isLoaded(final Object o){return tracks.indexOf(o)!=-1;}
    public  static int predictTime2End=-1;
    public static int runner_predictTime2End=-1;
    public static int dist2Runner=Integer.MAX_VALUE;
    static String []conductText={null,null};
    static public void conduct_reset(){
    	 dist2Runner=Integer.MAX_VALUE;
    	 conductText[0]=null;
    	 Path.runGP.Set(0,0);
    	 predictTime2End=-1;runner_predictTime2End=-1;    	
    }
    public static void refresh(){
        if (opened!=null){
           opened.deleteAll();
           for (int i=0; i<Tracks.tracks.size(); i++){ 
        	   Track t=(Track)(tracks.elementAt(i));
        	   String name=FILE.getName(t.fname(),false);
        	   if (t.noRename)
        		   name+="#"+(t.trackNumber>>1);
               opened.append(name, null);
               // opened.append(Storage.GetFileNameShort(((Track)(tracks.elementAt(i))).fname()), null);
           }
        }
}
    static public String[]getOpenedFN(){
      
        if (tracks.size()==0)
            return new String[]{null};
        String []s=new String [tracks.size()];
        for (int i=0; i< tracks.size(); i++)
            s[i]=Tracks.getFNameAt(i);
        return s;        
    }
    
    
    
    
    
    public static  int Conduct(final Point gp,  String txt[]){
    	/*
    	if (TrackConductor.active!=null && (TrackConductor.conduct || TrackConductor.running)){
      try{       
         synchronized(monitor){ 
            if (inWork==false){           
                inWork=true;
                outtextOld=outtxt;
                dist2endOld=dist2end; 
                 new Thread(new Runnable() {
                public void run() { 
                    
                    Point tGp=new Point(BTGPS.gp());
                    Point trGP=new Point(TrackConductor.runGP);
                    
                    
                	if (TrackConductor.running){
                		if (wtc==null){
                			wtc=new TrackConductorDriver(TrackConductor.active);
                			runner_toEnd=new PredictTime();
                		}else{
                			if (wtc.set(TrackConductor.active))
                					runner_toEnd.reset();
                		}
                		runner_dist2end=wtc.distToEnd(trGP);
                		runner_predictTime2End=(int)(runner_toEnd.predictTime2End(runner_dist2end));
                		//System.out.println("Dist2Run "+runner_dist2end);
                	}                	
	            	if (TrackConductor.conduct || TrackConductor.running){	
		            	if (tc==null){
		            		tc=new TrackConductorDriver(TrackConductor.active);
		            		toEnd=new PredictTime();
		            	}else{
		            		if (tc.set(TrackConductor.active))
		            			toEnd.reset();
		            	}
		                dist2end=tc.distToEnd(tGp);
		                predictTime2End=(int)(toEnd.predictTime2End(dist2end));
		               // System.out.println("Dist2End "+dist2end);
		                if (TrackConductor.conduct)
		                	outtxt=tc.Conductor(tGp);         
	            	}
	            		            	
		            if (dist2end>0 && runner_dist2end>0){
		            	dist2Runner=dist2end-runner_dist2end;
		            	if (dist2Runner>0){
		            		//predictTime2Runner=(int)toRunner.predictTime2End(dist2Runner);
		            	}else if (dist2Runner<0){
		            		//predictTime2Runner=(int)toMe.predictTime2End(-dist2Runner);
		            	}
		            }
                    synchronized(monitor){ 
                        inWork=false;
                    }                                          
                }}).start();  
                 
            }        
         }                                   
         txt[0]=outtextOld;
         return dist2endOld;
       }catch (Exception e){outtxt=null;return -1;}
    	}
    	else{
    		txt[0]=null;
    		return -1;
    	}
    	*/
    	return -1;
    }
    //-----------------------------------------------------------------------
    public static int getIndex(final String fn){
        int i=0;
        for (;i<tracks.size(); i++)
            if (getFNameAt(i).equals(fn))
                break;
        if (i==tracks.size())
        	i=-1;
        return i;
    }
    public static String getFNameAt(final int i){
        if (i<tracks.size()){
            return ((Track)tracks.elementAt(i)).fname();
        }else
            return null;
        
    }
    public static int getTrackNumber(final int i){
        if (i<tracks.size()){
            return ((Track)tracks.elementAt(i)).trackNumber;
        }else
            return -1;
        
    }
   
    static public  void add(final Track t){
        if (t!=null)
            tracks.addElement(t);
       // active=t;
//        trackI++;    
    }
    static public int size(){return tracks.size();}
    
    public static Track Get(final int i){
        if (i>=tracks.size())
            return null;
        else
            return (Track)tracks.elementAt(i);
    }
    private static int trackI=0;
    public static Track GetFirst(){
        if (tracks.size()>0){
            trackI=0;
            return GetNext();
        }else 
            return null;
    }
    public static  Track GetNext(){
        if (trackI==tracks.size())
            return null;
        else
            return (Track)tracks.elementAt(trackI++);
    }
    public static  void SetScale(final MAP map){
        for (int i=0; i<tracks.size(); i++)
            ((Track)tracks.elementAt(i)).setScale(map);

    }
    public static void remove(final Track t){
    	int i=tracks.indexOf(t);  
    	if (i>=0 && i<tracks.size()){
	    	FILE.dontShowRemove(t.fname());	
	    	if (path!=null && path.close(t))
	    		path=runner=null;	    			    	  	
	        tracks.removeElementAt(i);
	        MAP.redraw++;
	        Interface.update_menu=true;
	        //tracks.rem
    	}
    }
    public static void removeAll(){
    	while (tracks.size()>0)
    		remove((Track)tracks.elementAt(0));        
    }
    
   public static void removeAt(final int i){
	   if (i<=0 && i<tracks.size()){
		   Track t=(Track)tracks.elementAt(i);
		   FILE.dontShowRemove(t.fname());
		   if (path!=null && path.close(t)){
	   			path=runner=null;
	   			dont_show_path_info=graph=false;
		   }	   		
	        tracks.removeElementAt(i);        
	        MAP.redraw++;
	        Interface.update_menu=true; 
	   }
   }
   
   public static void RemoveLast(){removeAt(tracks.size()-1);}
   
   
   
   
   
   
   
   
   static private List opened=null;
  
   private static void unload(final int i){
	   if (i != -1 && i<tracks.size()){	
		   
		   removeAt(i);
           refresh();    
           if (tracks.size()==0){
        	  // MyI.OPENED_TRACKS.Off();
               MobNav1.display.setCurrent(MobNav1.gCanvas);               
           }
	   }	
   }
    public static void unload(final Track t){
	   int i=tracks.indexOf(t);
	   unload(i);   
   }
   public static void unload(final String fn){
	   int i=getIndex(fn);
	   unload(i);	   
   }
   public static void opened(){
    opened=new List("Маршруты",List.IMPLICIT);
    refresh();
   
    
    opened.setSelectCommand(Interface.select);    
    opened.addCommand(Interface.back);
    opened.addCommand(Interface.unload);
    opened.setCommandListener(new CommandListener() {
        public void commandAction(Command c, Displayable d) {
           int i=((List)d).getSelectedIndex();
           Track selected= ((Track)tracks.elementAt(i));
           if (c==Interface.select){                    
                selected.menu(opened);               
            }else if (c==Interface.unload){
                //selected.Close();
               // if (tracks.elementAt(i) ==active)
               //     active=null;
                removeAt(i);
                opened.delete(i);           
                if (tracks.size()==0)
                    MobNav1.display.setCurrent(MobNav1.gCanvas);
            }else if (c==Interface.back){                
                MobNav1.display.setCurrent(MobNav1.gCanvas);
           }
        }
});
    MobNav1.display.setCurrent(opened);
    
   }
    
   static private Exception writeInt(final OutputStream os, final int i){
	     try{    
	         byte[] buf=new byte[4];
	         buf[0]=(byte)(i&0xff);
	         buf[1]=(byte)((i>>8)&0xff);
	         buf[2]=(byte)((i>>16)&0xff);
	         buf[3]=(byte)((i>>24)&0xff);
	         os.write(buf);
	     }catch (Exception ex) {return ex;}
	    return null;
	}
	 static public void writeFloat(final OutputStream os, final float f){
	      int i=Float.floatToIntBits(f);
	      writeInt(os,i);      
	 }
	 static public Exception writeXY(final OutputStream os, final Point p){
	     try{ 
	         byte[] buf=new byte[8];
	         buf[0]=(byte)(p.x&0xff);
	         buf[1]=(byte)((p.x>>8)&0xff);
	         buf[2]=(byte)((p.x>>16)&0xff);
	         buf[3]=(byte)((p.x>>24)&0xff);
	         buf[4]=(byte)(p.y&0xff);
	         buf[5]=(byte)((p.y>>8)&0xff);
	         buf[6]=(byte)((p.y>>16)&0xff);
	         buf[7]=(byte)((p.y>>24)&0xff);
	         os.write(buf);
	     }catch (Exception ex) {return ex;}
	     return null;
	 }
	 
	
	
	 static int getInt(byte[]b, int i){
		 return b[i]+(b[i+1]<<8)+(b[i+2]<<16)+(b[i+3]<<24);
	 }
	 static private int index=0;
	 
	 //-----------------------------------------------------------
	 static public String pathFName=null;
	 static public int pathTrackNumber=0;
	 static public boolean runnerF=false;
	 
	 
	 
	 
	 static public void loadPLT(final String fn)throws Exception{
		 
		 
		
		 
		 
		 FileConnection fic=null;
	     BufferedRead br=null;	
	     RecordStore rs=null;
	     ByteArrayOutputStream bout = null;
	     DataOutputStream dos=null;
		 
	try{
		 boolean exit=false;
		
		 fic = (FileConnection) Connector.open(fn);
		 
		 if (fic.exists()==false)
			 return;
		 
		 
	     byte[] set="65280,4".getBytes();
		 
		String hashfn=FILE.hash(fn);
		//try{
			rs = RecordStore.openRecordStore(hashfn, true);
			//RecordStore.deleteRecordStore(hashfn);
		//}catch (Exception ex){}
		int nTracks=rs.getNumRecords();
		if (nTracks<2){		
			//------------------------------------------------------------------
			Loading.renameString(Interface.isConvering);
			System.out.println("track is converting ");
			//int nTracks=0;
			//rs.addRecord(set, cnt++, set.length);  //настройки
			//rs.addRecord(null, cnt++, 0);  //track
				
			bout = new ByteArrayOutputStream(); 
			dos=new DataOutputStream(bout);
		    //Location l=new Location();//-------------------------------------------
	        fic = (FileConnection) Connector.open(fn);
	        br=new BufferedRead(fic.openInputStream());
	        final long fileSize=fic.fileSize();
	        String s;
	        long len=12;
	        len+=br.readString().length();
	        len+=br.readString().length();
	        len+=br.readString().length();
	        len+=br.readString().length();
	        len+=br.readString().length();
	        len+=br.readString().length();		        
	       
	      
	        
	        
	        
	        TrackNode tn=null;
	        String []str={null,null};
	        s=br.readString();
	        len+=s.length()+2;
	        str[0]=new String(s.getBytes(),"UTF-8");
	        int cnt=0;
	        do{
		        tn=new TrackNode(str[cnt&1],null);	            	
		        cnt++;
		        while (!(exit=(s=br.readString())==null)){
		            if (Loading.CANCELED()){
		            	rs.closeRecordStore();	    		
		    			RecordStore.deleteRecordStore(hashfn);
		    			FILE.dontShowRemove(fn);
		                return;             
		            }
		            len+=s.length()+2;
		            int old=(cnt+1)&1;
		            str[cnt&1]=new String(s.getBytes(),"UTF-8");	
		            tn=new TrackNode(str[cnt&1],str[old]);
		            tn.save(dos);
		            if (TrackNode.trBreak==false)			            
			            cnt++;				            				          				            				            				          
		            else			            	
		            	break;		            
		            Loading.done=(int)((len<<7)/fileSize);		            		            		            		            		            		            
		        }
		        if (TrackNode.trBreak==false){
		        	tn=new TrackNode(null,str[(cnt+1)&1]);
		        	tn.save(dos);		        	
		        }
		        TrackNode.save_statistics(dos);
		        			        	            	
            	rs.addRecord(set, 0, set.length);  //настройки
    	        rs.addRecord(bout.toByteArray(),0,bout.size());
    	        bout.reset();	            		            	                                                                                                             		                
    
		}while(exit==false);
	    
	}
		if (fic!=null){
			fic.close();
		 	fic=null;
		}
		
		//-------------------------------------------------------------------
		//------------------------------------------------------------------
        nTracks=rs.getNumRecords();
        boolean noRename=nTracks>2;
        rs.closeRecordStore();
        rs=null;
        System.out.println("track is loading");
        int i=(Storage.trackIndexes2Load==null || Storage.trackIndexes2Load.length<=index)?-1:Storage.trackIndexes2Load[index];
        index++;
        
        
        if (i==-1){
	        for (i=0; i<nTracks; i+=2){
	        	Track t=new Track(fn,1+i,noRename,null);
	        	
	        	if (Storage.autoloadMode==false && noRename==false)
	        		t.menu(MobNav1.gCanvas);
	        	tracks.addElement(t);	        	 	
	        }
	        if (noRename)
	        	opened();
        }else{
        	int trackNumber=i-' ';
        	
        	Stack path=null;
        	boolean pathF=false;
        	if (pathF=pathFName!=null && pathFName.endsWith(fn) && pathTrackNumber==trackNumber){
        		pathFName=null;
        		path=new Stack();        		
        	}
        	
        	Track t=new Track(fn,trackNumber,noRename,path);
        	Path.track=(path!=null)?t:null;        		
        	if (pathF)
        		Tracks.path=new Path(); 
        	if (runnerF){
        		Tracks.runner=new Path();        		
        		NAVI.startRunningButton();
        	}
        	if (graph && path!=null)
        		Graph.init(MyCanvas.max_x, MyCanvas.max_y);
        	tracks.addElement(t);	        
        }
        if (nTracks>0){
        	MAP.redraw++;
		    Interface.TrackMenuON();
		    Interface.update_menu=true;
        }
	 }finally{
    	 if (br!=null)
    		 br.close();
         if (fic!=null)
             fic.close();
         if (rs!=null)
        	 rs.closeRecordStore();
         if (bout!=null)
        	 bout.close();
         if (dos!=null)
        	 dos.close();	         
     }	 
 
}
//--------------------------------------------------------------------------------------
		static public void conductor_(){
			if (Tracks.size()==0)
				return;
			
		   // if (mapScroling){
		   //     conductText[0]=null;
		   //     return;
		   // }
		            // ################################################################
		   // route(g,10,100,0);
		    if (MyCanvas.time-MyCanvas.lastTime>=1000){
		            MyCanvas.lastTime=MyCanvas.time;               
		           // double direction=math.GetAngle(Storage.screenTrack.normX, -Storage.screenTrack.normY);
		           int res=Conduct(NAVI.gp, conductText);
		          // if (res!=-2)
		          //      conductLen=res;                   
		    }
		  
		   // System.out.println("to end "+len);
		    if (conductText[0]!=null){                                
		        if ((conductText[1]==null || !conductText[0].equals(conductText[1]))){
		            conductText[1]=conductText[0];                   
		            if (MyCanvas.time-MyCanvas.lastTimeConductorBeep>5000){
		                MyCanvas.lastTimeConductorBeep=MyCanvas.time;
		                
			               TEXT.beep();
		                
		            }                    
		        }  
		        //if (conductText[0].equals("turn left")){
		       //     g.setClip(0, 130, 16, 16);
		       //     g.drawImage(MyI.routIcons, 0, -30, 0);
		       // }   
		
		
		       
		    }
		            
		            
		            // ################################################################
		}


}
