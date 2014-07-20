package mobnav.tracks;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import mobnav.canvas.Circuit;
import mobnav.canvas.FILE;
import mobnav.canvas.Interface;
import mobnav.canvas.Location;
import mobnav.canvas.MobNav1;
import mobnav.canvas.OZI_PLT;
import mobnav.canvas.Point;
import mobnav.canvas.Settings;
import mobnav.canvas.Storage;
import mobnav.gps.BTGPS;
import mobnav.gps.GGA;
import mobnav.gps.NMEA;



public class TrackRecording {
	static public String fileName="";
	static public String tFName=null;
    static public final Object update=new Integer(0);
	static private FileConnection fcc=null;
    static public boolean records=           true;
    static private String st="";
    static public SmartTrack on_screen,on_disk;
    static private int diskTrackNodes=0;
    static private double diskTrackLen=0;
    static private OutputStream os=null;
	static private final int TRACK_BUFFER_SIZE=10;//256;
	//---------------------------------------------------------------------------
	static private int dDist=0;
	static private int dTime=0;
	static private int dSpeed=0;
	static private int dHeight=0;
	static private boolean delta=false;
	
	static private int oldDist=0;
	static private long oldTime=0;
	static private int oldSpeed=0;
	static private int oldHeight=0;
	
	//---------------------------------------------------------------------------------------
	static private int getIndex(final String s[], final int val){
		int i=0;
		while (i<s.length && val!=Integer.parseInt(s[i]))i++;
		return i;
	}
	//---------------------------------------------------------------------------------------
	static public void settings(){
		 Form set = new Form(Interface.s_trackRecSet);
		
		 String[] s=new String[]{"0","10","100"};
		 final ChoiceGroup c_dDist=new ChoiceGroup("Дист. метров"	, ChoiceGroup.POPUP, s,null);
		 c_dDist.setSelectedIndex(getIndex(s,dDist), true);
		 set.append(c_dDist); 
		 
		 s=new String[]{"0","1","2","3","5","10","60","600"};
		 final ChoiceGroup c_dTime=new ChoiceGroup("Время секунд"	, ChoiceGroup.POPUP, s,null);
		 c_dTime.setSelectedIndex(getIndex(s,dTime/1000), true);
		 set.append(c_dTime); 
		 
		s=new String[]{"0","5","10","20"};
		 final ChoiceGroup c_dSpeed=new ChoiceGroup("Скорость км/ч"	, ChoiceGroup.POPUP, s,null);
		 c_dSpeed.setSelectedIndex(getIndex(s,dSpeed), true);
		 set.append(c_dSpeed); 
		 
		 s=new String[]{"0","2", "5","10","20"};
		 final ChoiceGroup c_dHeight=new ChoiceGroup("Высота метров m.", ChoiceGroup.POPUP, s,null);		 
		 c_dHeight.setSelectedIndex(getIndex(s,dHeight), true);
		 set.append(c_dHeight);  
		 
		 
		 
		 set.addCommand(Interface.ok);
		 set.addCommand(Interface.exit);
		 
		
		 set.setCommandListener(new CommandListener() {
		     public void commandAction(Command c, Displayable s) {
   		 
		         if (c==Interface.ok){
		        	dHeight=Integer.parseInt(c_dHeight.getString(c_dHeight.getSelectedIndex()));
		        	dDist=Integer.parseInt(c_dDist.getString(c_dDist.getSelectedIndex()));
		        	dSpeed=Integer.parseInt(c_dSpeed.getString(c_dSpeed.getSelectedIndex()));
		        	dTime=1000*Integer.parseInt(c_dTime.getString(c_dTime.getSelectedIndex()));
		        	delta=dHeight>0 || dDist>0 || dSpeed>0 || dTime>0;
		         }		    	
		        MobNav1.display.setCurrent(MobNav1.gCanvas);		         
		     }
		 });		 
		 MobNav1.display.setCurrent(set);
		
		
	}
	// ###########################################################################
	static public void SAVE_STATE_IF_ERROR_END_EXIT(){
	    stop();
	   // Labels.save();
	    MobNav1.mn.destroyApp(true);
	}
//---------------------------------------------------------------------------
static public void exit(){
	boolean rec=records;
	String fn=fileName;
	stop();
	fileName=fn;
	records=rec;
}
//---------------------------------------------------------------------------------------
static private String tempFName(final String fName){
	final String tAdd="old_";
	int i=1+fName.lastIndexOf('/');
	if (i==0)
		return tAdd+fName;
	else
		return fName.substring(0,i)+tAdd+fName.substring(i,fName.length());
}
//---------------------------------------------------------------------------------------
static public void stop(){
	boolean error=false;
	records=false;
	while(inP){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
	}
	inP=true;
	if (os!=null)
      try {        	  
          if (st.length()>0){
        	  st+=get_plt_string();
        	  os.write(st.getBytes());
        	  diskTrackNodes++;
          }              
          os.close();
          FILE.dontShowRemove(fileName);
      } catch (Exception ex) {error=true;}
      if (fcc!=null)
          try {
              if (diskTrackNodes==0 && contin==false){
                  fcc.delete();
                  System.out.println("track delited");
              }
              fcc.close();
              fcc=null;
          } catch (Exception ex) {System.out.println("File Close Error +"+ex.toString());error=true;}
          if (error==false && contin==true){
        	  try {
        		if (tFName!=null){
					FileConnection ifc=(FileConnection) Connector.open(Storage.getAutoSaveDir(tFName)+tFName);
					if (ifc.exists()){
						ifc.delete();
						ifc.close();
					}
        		}
			} catch (Exception e) {System.out.println("ERROR "+e.toString());}
          }
          inP=false;                   
     fileName="";
}
//---------------------------------------------------------------------------
static private boolean contin=false;
static public boolean inP=false;
static private void copy_file_to_tempFile_end_open_2_write(final FileConnection ifc) throws IOException{
	 tFName=tempFName(FILE.getName(fileName, true));
	 try{
		 ifc.rename(tFName);
	 }catch(Exception ex){}
		InputStream is=ifc.openInputStream();
		fcc=(FileConnection) Connector.open(fileName);         		 	        	
      fcc.create();      	       
      os=fcc.openOutputStream(); 
		byte []b=new byte[1024];
		int readed;
		while ((readed=is.read(b))>0)
			os.write(b, 0, readed);  
		b=null;
		is.close();
		ifc.close();
		newTrack='0';
		contin=true;
}
//---------------------------------------------------------------------------
static public void startW(){
	while(inP)
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
	inP=true;
    on_disk=new SmartTrack(Settings.MAX_YAW_IN_METERS_FOR_TRACK);
	records=true;
	fcc=null;
    st="";
    diskTrackNodes=0;
    diskTrackLen=0;
    os=null;
   // OZI_PLT.trackI=0;
    boolean err=false;
    try{
        String dir=Storage.getAutoSaveDir(FILE.PLT_E);        
        if (fileName.length()>0){        	
    		 FileConnection ifc=(FileConnection) Connector.open(fileName);
    		 try{
    		 if (ifc.exists()){
    			 copy_file_to_tempFile_end_open_2_write(ifc);
    		 }else{
    			 ifc.close();
    			 fileName="";
    		 }        		        	        
        }catch(Exception ex){
        	err=true;
    		System.out.println("File Error "+fileName+ex.toString());
    		fileName="";
    		ifc.close();        		
        }
        }else
        	tFName=null;
        if (fileName.length()==0){
        	newTrack='1';
        	contin=false;
        	fileName=dir+Storage.date2Name(true)+".plt";
	        fcc=(FileConnection) Connector.open(fileName);
	        try{	        	
	        	fcc.create();
	        }catch(Exception ex){
	        	err=true;
	        	System.out.println("ERROR cant create "+ex.toString());
	        }
	        os=fcc.openOutputStream();        
	        OZI_PLT.WriteOZIHeader(os);
	        
	        
      }
      if (err==false)
    	  FILE.dontShowAdd(fileName);      
    }catch (Exception ex) {}  
    inP=false;
}
//---------------------------------------------------------------------------
static private Location []la=new Location[2];	
static private char newTrack='1';

static private String get_plt_string(){
	String s=BTGPS.l().getLat()+',';
    s+=BTGPS.l().getLon()+','+newTrack+',';    
    s+=Integer.toString((int)(BTGPS.GetHeight()))+',';//*3.28084))+',';
    s+=Double.toString(NMEA.time.tDateTime_UTS())+','+NMEA.time.getUTS(':',true)+','+NMEA.time.dateUTS('-')+"\n";
    return s;
}
static private long cTime=0;
static private int cSpeed=0,cDist=0,cHeight=0;
//---------------------------------------------------------------------------------------
static private boolean upd(){
	if (delta){
		cTime=System.currentTimeMillis();
		cSpeed=(int)(BTGPS.speed4A()*3.6);
		cDist=(int)BTGPS.getDistance();
		dHeight=(int)BTGPS.GetHeight();
		return(
				(dHeight>0 && Math.abs(cHeight-oldHeight)>=dHeight) ||
				(dTime>0 && cTime-oldTime>=dTime)||
				(dSpeed>0 && Math.abs(cSpeed-oldSpeed)>=dSpeed)||
				(dDist>0 && Math.abs(cDist-oldDist)>=dDist)
				);
	}else 
		return false;
}
//---------------------------------------------------------------------------------------
static private void write(final Point p){	
	inP=true;
	//stt=get_plt_string();
	//stt+=Long.toString(System.currentTimeMillis())+",,\n";
    //if (false)
    try {
    	


        if ( on_disk.drawNext(p)==1 || upd()){
        	//System.out.println("write to dist "+dTime);
        	oldTime=cTime;
			oldSpeed=cSpeed;
			oldDist=cDist;
			oldHeight=cHeight;
            st+=get_plt_string();
            la[diskTrackNodes&1]=new Location(BTGPS.l());	
            if (diskTrackNodes>0)
            	diskTrackLen+=Location.getDistance(la[0],la[1]);	                        	                                                	                        	                        	                        	                     
            if (st.length()>=TRACK_BUFFER_SIZE){
                os.write(st.getBytes());
                st="";
            }
            diskTrackNodes++;
        }
    } catch (Exception ex) {}
    inP=false;
}

/////////////////////////////////////////////////////////////////////////////////////////////////

static private final int maxDistanceToStartNewTrack=100;
static private final long maxTimePeriod=3600000*12;//12 часов   чтобы начать новый трек
//тут думай
static boolean create;


///////////////////////////////////////////////////////////////////////////////////
static public void update(){
	if (Path.startTime>0 && Tracks.runner!=null && Path.track.pathInUse_>0){
    	Path.setRunDot();
		Tracks.runner.getDist2End(new Point(Path.runGP),false);
	}
	Point gps=BTGPS.gp();
	if (BTGPS.hdop_ok=(GGA.fixQuality!=0 && NMEA.HDOP < BTGPS.max_hdop_error)){
		if (create){
			create=false;
			startW();
		}
		
		if (Circuit.startTime>0)
			Circuit.curPos(gps);//new PointD(BTGPS.l().GetPoint()));
		if (Tracks.path!=null && Path.track.pathInUse_>0){
			Tracks.path.getDist2End(gps,true);							
		}
		//if  (Tracks.runner!=null && Tracks.runner.loaded()){
			
		//}
		if (OZI_PLT.lastTrack!=null){
			if (BTGPS.l().getDistance(OZI_PLT.lastLac)<maxDistanceToStartNewTrack &&
				BTGPS.time-OZI_PLT.lastTime<maxTimePeriod){
				OZI_PLT.load(BTGPS.l());								
				newTrack='0';
			}else
				newTrack='1';
			OZI_PLT.lastTrack=null;
			OZI_PLT.lastLac=null;
		}												
        Point p=BTGPS.l().GetPoint();
        
        int ret=on_screen.drawNext(p);
       // System.out.println(ret);
    	if (ret==1) {                    
            OZI_PLT.track[(OZI_PLT.trackI)&OZI_PLT.maxTrackA]=p;
            OZI_PLT.trackI++;
        }else if (ret==0)
            OZI_PLT.track[(OZI_PLT.trackI-1)&OZI_PLT.maxTrackA]=p;
        if (records){
        	write(p);
        	newTrack='0';
        }
	}

}
///////////////////////////////////////////////////////////////////////////////////

static public void start(){
	 on_screen=new SmartTrack(Settings.MAX_YAW_IN_METERS_FOR_MEMORY_TRACK);
	if (records)
		create=true;
		
	new Thread(new Runnable() {
		public void run() {
			try{
				while (true){
					try{synchronized(update){update.wait(0);}}catch(Exception e) {}
					//System.out.println("GPS UPDATE");
					
					update();		                       
				}
			}catch (Exception ex) {}			           
		}
	}).start();
}
}