package mobnav.tracks;
/**
 *
 * @author 2cool
 */
import java.io.*;
import java.util.Stack;


import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

import mobnav.canvas.FILE;
import mobnav.canvas.GOOGLE_T;
import mobnav.canvas.Graph;
import mobnav.canvas.Interface;
import mobnav.canvas.Loading;
import mobnav.canvas.Location;
import mobnav.canvas.MAP;
import mobnav.canvas.MobNav1;
import mobnav.canvas.MyCanvas;
import mobnav.canvas.NAVI;
import mobnav.canvas.Point;
import mobnav.canvas.SVG_Path;
import mobnav.canvas.Settings;
import mobnav.canvas.Storage;
import mobnav.canvas.TEXT;
import mobnav.canvas.l2d;

// ##############################################################################
public class Track  {
	
	public boolean hint=false;//подсказки
	public Stack path=null;	
	public int   pathInUse_=0;
	public static final int altitude_not_valid=-777;
	public static String s_conduct="",s_inverse="",s_running="",s_find="";
	public boolean noRename=false; // если в файле трека несколько треков. то в меню трека нет подменю переименовать
	private Point track_beg=null, track_end=null;
	public Point track_beg(){return track_beg;}
	public Point track_end(){return track_end;}
	private  String fname=null;
	public String fname(){return fname;}
	static private  final int maxBits=14;
	static private  final int maxSize=1<<maxBits;
	static public int maxSize(){return maxSize;}
	private  SVG_Path [][] track=null;
	
	
	static public final int    dmax_zoom=19;
	static public int			max_zoom=17;
	
	private  int xsize,ysize;
	private  int   rgb=                   0xff00;
	private  float width=                 5;
	public boolean timeTags=false;
	
	public static Graph graph=null;



//file://localhost/E:/boyarka24.plt.trk


	// ##############################################################################
private  void setTrackSettings(){
    l2d.setOpacity();
        for (int y=0; y<ysize; y++)
            for (int x=0; x<xsize; x++){
                track[x][y].SetColor(rgb);
                track[x][y].setWidth(width);
            }
}
//##############################################################################
public int trackLength=0;
public int trackNodes=0;
public int maxH=0,minH=0,dUp=0,dDown=0;
public void add2Track(final Point pnt){
    trackNodes++;
    track_end=new Point(pnt);
    if (track_beg==null)
        track_beg=new Point(pnt);

    Point p=MAP.getMapPoint(new Point(pnt), max_zoom);
    for (int y=0; y<ysize; y++)
        for (int x=0; x<xsize; x++)
            track[x][y].Add(p);
}
//##############################################################################
private void saveTrackSet1(){
    try {        	
    	RecordStore rs = RecordStore.openRecordStore(FILE.hash(fname), true);
    	try{
        	byte []b=(
        			Integer.toString(rgb)+','+
        			Float.toString(width)
        			
        	).getBytes();
        	rs.setRecord(trackNumber, b, 0, b.length);
    	}finally{
    		rs.closeRecordStore();
    	}
    } catch (Exception ex) {System.out.println("TRACK Set Save Error"+ex.toString());}
}
//##############################################################################
private void loadTrackSet(){//if (true)return;
    try {        
		RecordStore rs = RecordStore.openRecordStore(FILE.hash(fname), true); 
		try{			
			byte []b=rs.getRecord(trackNumber);
			if (b!=null){         		
				String []s=TEXT.split(new String(b), ',');		    
			    rgb=Integer.parseInt(s[0]);
			    width=Float.parseFloat(s[1]); 			   
			}
		}finally{
			rs.closeRecordStore();
		}        
   } catch (Exception ex) {System.out.println("TRACK Set Load Error"+ex.toString());}
   
}
//##############################################################################
private void endTrack(){  	
    loadTrackSet();
    int n=0;
    for (int y=0; y<ysize; y++)
        for (int x=0; x<xsize; x++)
            n+=track[x][y].End(rgb,width);
    if (n==0){
        MyCanvas.SetErrorText("Track: nothing to draw");
        aborted=true;
    }
}
//##############################################################################
private void createTrack(Point pMax_, Point pMin_){
    
    trackNodes=0;
    l2d.Set();
    pMax=MAP.getMapPoint(pMax_, max_zoom);   
    pMin=MAP.getMapPoint(pMin_, max_zoom);

    if (pMin.x>pMax.x){
        pMin.x^=pMax.x;
        pMax.x^=pMin.x;
        pMin.x^=pMax.x;
    }
    if (pMin.y>pMax.y){
        pMin.y^=pMax.y;
        pMax.y^=pMin.y;
        pMin.y^=pMax.y;
    }
    pMin.x&=(-MAP.tileSize);
    pMin.y&=(-MAP.tileSize);
    pMax.x&=(-MAP.tileSize);
    pMax.x+=MAP.tileSize;
    pMax.y&=(-MAP.tileSize);
    pMax.y+=MAP.tileSize;
    xsize=((pMax.x-pMin.x)>>maxBits)+1;
    ysize=((pMax.y-pMin.y)>>maxBits)+1;        
    track=new SVG_Path[xsize][ysize];
     for (int y=0; y<ysize; y++)
        for (int x=0; x<xsize; x++){
            SVG_Path t= track[x][y]=new SVG_Path();
            Point rel=new Point(x<<maxBits,y<<maxBits);                
            t.pMin.x=rel.x+pMin.x;
            t.pMax.x=t.pMin.x+(1<<maxBits);
            t.pMin.y=rel.y+pMin.y;
            t.pMax.y=t.pMin.y+(1<<maxBits);
        }     
}
//public void drawTrack(Stack locs, int len, String tf_name){
//##############################################################################
static private Location []tl={new Location(),new Location()};
static private int ti;
//static private Point oldP;

public int trackNumber=0;

//################# C O N S T R U C T O R #############################################################
public Track(final String fname, final int tnBr, final boolean noRename, final Stack path) throws RecordStoreFullException, RecordStoreNotFoundException, RecordStoreException, IOException{
	
		
	if (GOOGLE_T.tname!=null && GOOGLE_T.tname.length()>2){
  		newName=GOOGLE_T.tname;        	     			        			        		        		
		GOOGLE_T.tname=null;
	 }	
	this.noRename=noRename;
	int nodes=0;
	this.fname=fname;
	trackNumber=tnBr;
    loadTrackSet();
	RecordStore rs=null;
	ByteArrayInputStream bais=null;
	DataInputStream dis=null;
	try{
	String hashfn=FILE.hash(fname);
	aborted=false;
	
	
	//this.trackConductor=null; 
	
	rs = RecordStore.openRecordStore(hashfn, false);	
	dis=new DataInputStream(bais=new ByteArrayInputStream(rs.getRecord(tnBr+1)));
	

	bais.reset();		
	 int fileSize=bais.available()-36; 
	 bais.mark(1000000000);
	 bais.skip(fileSize);
	     
     pMax.x=dis.readInt();
     pMax.y=dis.readInt();
     pMin.x=dis.readInt();
     pMin.y=dis.readInt();
     trackLength=dis.readInt();
     maxH=dis.readInt();
     minH=dis.readInt();
     dUp=dis.readInt();
     dDown=dis.readInt();
     createTrack(pMax,pMin);
     
     dis.close();
     bais.close();
     rs.closeRecordStore();	
     rs = RecordStore.openRecordStore(hashfn, false);
 	dis=new DataInputStream(bais=new ByteArrayInputStream(rs.getRecord(tnBr+1)));
 	int i=0;
 	Loading.renameString(Interface.isLoading);
	
	int oldDTime=0,timeErrors=0;
	while (i<fileSize){
         if (Loading.CANCELED()){  
         	aborted=true;
         	FILE.dontShowRemove(fname);
             return;             
         }
         TrackNode tn=new TrackNode(dis); 
         hint|=tn.txt!=null;       
         add2Track(tn.gp);            
         if (tn.dtime==0 || tn.dtime<=oldDTime)
        	 timeErrors++;         
         oldDTime=tn.dtime;
         if (path!=null)
        	 path.addElement(tn);
         i+=TrackNode.loaded;
         Loading.done=i*128/(int)fileSize;//индикатор загрузки карты
         nodes++;
     }

     int n=0;
     for (int y=0; y<ysize; y++)
         for (int x=0; x<xsize; x++)
             n+=track[x][y].End(rgb,width);
     
     timeTags=timeErrors<=1;                                   
     if (n==0){
         MyCanvas.SetErrorText("Track: nothing to draw");
         aborted=true;
     } 
   //  pathLoaded=path!=null;
    	 
     }finally{
         try{
             if (dis!=null)
                 dis.close();
             if (bais!=null)
            	 bais=null;
             if (rs!=null)
            	 rs.closeRecordStore();            
         }catch (Exception e){aborted=true;}
     }     		
}



//##############################################################################
public void end1(){	 	
	endTrack(); 

	MAP.redraw++;
}

//################# C O N S T R U C T O R #############################################################
public Track(final String fname, final Location nl){

	ti=0;
	Point p=(tl[ti&1]=nl).GetPoint();
	ti++;
	pMin.Set(p);
    pMax.Set(p);
    
  
    trackNodes=0;
    trackLength=0;
}
//##############################################################################


public static boolean aborted=false;
//##############################################################################
 public void setScale(final MAP map){
      for (int y=0; y<ysize; y++)
            for (int x=0; x<xsize; x++)
                track[x][y].SetScale(map,width);
 }
//################# P A I N T #############################################################
 public void paint(Graphics g, int mx,int my){
     Point p= NAVI.map.Scale(new Point(mx,my),max_zoom);
     Point p1=NAVI.map.Scale(new Point(mx+MAP.tileSize, my+MAP.tileSize),max_zoom);
   //  System.out.println("mx my "+mx+" "+my);
     int x=(p.x-pMin.x)>>maxBits;
     int y=(p.y-pMin.y)>>maxBits;
     int x1=(p1.x-pMin.x)>>maxBits;
     int y1=(p1.y-pMin.y)>>maxBits;
     if (x<0 && x1<0 || x>=xsize && x1>=xsize || y<0 && y1<0 || y>=ysize && y1>=ysize){
         return;
     }
     
     if (x<0)x=0;     
     if (y<0)y=0;
     if (x1<0)x1=0;
     if (y1<0)y1=0;
     if (x1!=x || y1!=y){
         if (x1>=xsize)
             x1=xsize-1;
         if (y1>=ysize)
             y1=ysize-1;
         for (; y<=y1; y++)
             for (int xx=x; xx<=x1; xx++)
                    track[xx][y].paint(g,mx,my);                              
     }else
        track[x][y].paint(g,mx,my);
 }
 public Point pMin=new Point(), pMax=new Point();

//####### S E T T I N G S #######################################################################

public void settings(final Displayable ret){  
    final Form set = new Form(TEXT.Distance(trackLength));
    final Gauge trackWidth = new Gauge("Track Width", true, 10, (int)width);
    final Gauge trackOpacity = new Gauge("Track Opacityk %", true, 100, (int)(100-100*Settings.TRACK_OPACITY));
    int size=Settings.COLORS.length;
    String[] s=new String[size];
    int i=0;
    while (i<size)
        s[i++]="";
    Image[] colorsA=Settings.setColorImages(Settings.COLORS);
    
    final Image img=Settings.GetImage(set.getWidth(),rgb,(int)width,Settings.TRACK_OPACITY);//101-TRACK_OPACITY*100);
    final ImageItem it1=new ImageItem("",img,ImageItem.LAYOUT_CENTER,"");
    final ImageItem it2=new ImageItem("",img,ImageItem.LAYOUT_CENTER,"");
    final ChoiceGroup trackColors=new ChoiceGroup("track color", ChoiceGroup.POPUP, s,colorsA);
    trackColors.setSelectedIndex(Settings.FindColorIndex(rgb), true);
    
    
    set.append(trackColors);
    set.append(it1);
    set.append(trackWidth);
    set.append(it2);
    set.append(trackOpacity);
    //set.append(it3);
    
    set.addCommand(Interface.ok);
    set.addCommand(Interface.exit);
    set.setCommandListener(new CommandListener() {                                                            
            public void commandAction(Command c, Displayable s) {
            if (c==Interface.ok){
                boolean saveSet=false;
                if (width!=trackWidth.getValue()){
                    saveSet=true;
                    width=trackWidth.getValue();
                }                
                if (rgb!=Settings.COLORS[trackColors.getSelectedIndex()]){
                    saveSet=true;
                    rgb=Settings.COLORS[trackColors.getSelectedIndex()];      
                }
                Settings.TRACK_OPACITY=1-((float)trackOpacity.getValue())*0.01f;
                if (saveSet){
                	setTrackSettings();
                    saveTrackSet1();                               
                    MAP.redraw++;
                 }
            }
            MobNav1.display.setCurrent(ret);
        }
    });
                          
    set.setItemStateListener(new ItemStateListener(){

            public void itemStateChanged(Item item) {                               
                if (item==trackWidth){
                    if (trackWidth.getValue()<1)
                        trackWidth.setValue(1);
                }
                if (item==trackOpacity)
                    if (trackOpacity.getValue()>90)
                        trackOpacity.setValue(90);
                Image img=Settings.GetImage(
                        set.getWidth(),
                        Settings.COLORS[trackColors.getSelectedIndex()],
                        trackWidth.getValue(),
                        1-((float)trackOpacity.getValue())*0.01f
                        );
                it1.setImage(img);
                it2.setImage(img);
            }
        });
    MobNav1.display.setCurrent(set);
 }
//########################## I N F O ####################################################
static final double foot2m=0.3048;
static public String len="",nods="",перепад="",s_maxH="",s_minH="",s_allUp="",s_allDown="";
public void info(final Displayable ret){
    Form f = new Form(FILE.getName(fname,false));
    f.append(len+TEXT.Distance(trackLength));
    f.append("\n"+nods+trackNodes); 
    if (maxH!=altitude_not_valid || minH!=altitude_not_valid){
    	if (dUp>1)    			
    		f.append("\n"+s_allUp+(int)(dUp*foot2m)+Interface.s_m);
    	if (dDown<-1)
    		f.append("\n"+s_allDown+(int)(-dDown*foot2m)+Interface.s_m);    	 	    
	    f.append("\n"+перепад+(int)((maxH-minH)*foot2m)+Interface.s_m);
	    f.append("\n"+s_maxH+(int)(maxH*foot2m)+Interface.s_m);
	    f.append("\n"+s_minH+(int)(minH*foot2m)+Interface.s_m);
	   
    }
    if (hint && path!=null){
    	f.append("\n... "+Interface.s_direction+" ...");    	
		int i=0,n=path.size();
		while (i<n){
			TrackNode tn=(TrackNode)path.elementAt(i);
			if (tn.txt!=null )
				f.append("\n"+tn.txt+"\n.");
			i++;
		}    		    	
    }
    f.addCommand(Interface.exit);
    f.setCommandListener(new CommandListener() {                                                            
            public void commandAction(Command c, Displayable s) {
            MobNav1.display.setCurrent(ret);
        }
    });
    MobNav1.display.setCurrent(f);
}




//##############################################################################
static private List menu=null;




//##############################################################################


public String getFName(String name){
    return Storage.getPath(fname)+name+FILE.PLT_E;
}
//############# R E N A M E #################################################################
 private String newName=null;
 public void rename(final Displayable d){        
    final String name=(newName!=null)?newName:FILE.getName(fname,false);
    newName=null;
    int fxs=Math.max(255, name.length());    
    final   TextField tf_name=new TextField("",name, fxs, TextField.ANY);
    Form f = new Form(Interface.s_names_label);
    f.append(tf_name);
    //f.addCommand(MyI.exit);
    f.addCommand(Interface.ok);
    f.addCommand(Interface.exit);
    f.setCommandListener(new CommandListener() {
        public void commandAction(Command c, Displayable s) {
            if (c==Interface.ok){            	
				 String newName=tf_name.getString();
				 if (FILE.rename(fname,newName+FILE.PLT_E)){
					 fname=getFName(newName);					 
					 menu.setTitle(FILE.getName(fname,false));					 	
					 MobNav1.display.setCurrent(d);
				 }
            }else if(c==Interface.exit)
            	MobNav1.display.setCurrent(d);
                       
        }
    });
    MobNav1.display.setCurrent(f);
  }
//############ D E L I T E ##################################################################
 private void delite(final Displayable ret, final Displayable ret2menu){
	 Alert a = new Alert(FILE.getName(fname,true),Interface.s_delite+"?",null, AlertType.WARNING);
	    a.addCommand(Interface.yes);
	    a.addCommand(Interface.no);
	    a.setCommandListener(new CommandListener(){
	        public void commandAction(Command c, Displayable d){
	            if (c == Interface.yes){	           
			 		   FILE.delete(fname,false);
			     	   Tracks.unload(fname);
			     	   //if (Tracks.size()>0)
			     	   MobNav1.display.setCurrent(ret); 
	            }else
	            	MobNav1.display.setCurrent(ret2menu);
				     }}); 
	    
	    
	 MobNav1.display.setCurrent(a);
 }
//##############################################################################
 //private  static boolean pathF=false,runnerF=false,inverseF=false;
 ///////////////////////////////////////////////////////////////////////
 private Path conduct(Path p, final boolean inverse){
	 
	 if (p==null){
		 Path.track=this;
		 p=new Path();
		 
	 }else 
		 p.setInverse(inverse);
	
	 return p;	 	 
 }
 /////////////////////////////////////////////////////////////////////
 private static void clearPath(final Track t){
	 if (t.path!=null)
		 t.path.removeAllElements();
	 t.path=null;
	 Tracks.path=Tracks.runner=null;
 }

 /* думаю это хуета
 
 по нажатии на график высот переходить сразу на график. если график высот трека не тотже что 
 для других двух то подгружаем его временно для просмотра и не вызываем найти нод.
 можно сделать так что если путь находится отдельно в класе граф то тогда не вызываем нод.
 также надо его както удалять при переключении на другой экран
 */
 /////////////// C O N D U C T //////////////////////////////////////
 private void conduct(){
	 
	 boolean set=(runnerF!=false || pathF!=false || graphF!=false);
	 boolean notMe=Tracks.path!=null && Path.track!=this;
	 if (set && notMe)
		 clearPath(Path.track);			 
	 if (set==false && notMe==false)
		 clearPath(this);
	 if (set==true){
		 Tracks.dont_show_path_info=!pathF;		 
		 Tracks.path=conduct(Tracks.path,inverseF);
		 if (runnerF){
			 boolean runnerWasNull=Tracks.runner==null;			
			 Tracks.runner=conduct(Tracks.runner,inverseF);
			 if (runnerWasNull && Tracks.runner!=null)
				 Path.startTime=0;
			 else
				 Path.stopRunning(true);
		 }else
			 Tracks.runner=null;		
	 }
	 if (graphF)
		 Graph.init(MyCanvas.max_x, MyCanvas.max_y);
	 Tracks.graph=graphF;
	 NAVI.startRunningButton();
 }
 
//#################### M E N U ##########################################################

 private int []menuI=new int[11];
 
 private  static boolean pathF=false,runnerF=false,inverseF=false,graphF=false;
   public void menu(final Displayable ret){	
	   if (Tracks.path!=null && Path.track==this){
		   pathF=Tracks.path!=null && Tracks.dont_show_path_info==false;
		   inverseF=(Path.inverse);
		   runnerF=Tracks.runner!=null;
		   graphF=Tracks.graph;
	   }else
		   pathF=inverseF=runnerF=graphF=false;
	   
	  // this_=this;      
       int cnt=0;
   // final Track curTrack=this;
     String fns=FILE.getName(fname,false);
     //fns=fns.substring(0, fns.length()-MyI.TRK_E.length());
     menu=new List(fns,List.IMPLICIT); 
     menuI[cnt++]=0;
     menu.append(Interface.s_settings, Interface.settingsIcon); //0 
     menuI[cnt++]=1;
     menu.append(Interface.s_information, Interface.infoIcon);  //1
     menuI[cnt++]=2;
     menu.append(Interface.s_to_start, Interface.toStartIcon);  //2
     menuI[cnt++]=3;
     menu.append(Interface.s_to_end, Interface.toEndIcon);      //3
       
     if (noRename==false){
    	 menu.append(Interface.s_rename,Interface.editIcon);    //4
    	 menuI[cnt++]=4;
     }       
     menu.append(Interface.s_unload,Interface.unloadIcon);	    //5
     menuI[cnt++]=5;

     if (noRename==false){
	     menu.append(Interface.s_delite, Interface.deliteIcon);		//6
	     menuI[cnt++]=6;
     }
     
    // menu.append(MyI., null)
    // if (trackConductor!=null){
	//conduct=TrackConductor.active!=null && TrackConductor.active==this.trackConductor;
    menu.append(s_conduct, (pathF)?Interface.checkIcon:Interface.cancelIcon); //7
    menuI[cnt++]=7;
    menu.append(s_inverse, (inverseF)?Interface.checkIcon:Interface.cancelIcon);//8
    menuI[cnt++]=8;
    
    if (timeTags && inverseF==false){
    	menu.append(s_running,  (runnerF)?Interface.checkIcon:Interface.cancelIcon);//9
    	menuI[cnt++]=9;
       // menu.append("Set Beg", MyI.Aicon);
       // menu.append("Set End", MyI.Bicon);
     }
   
    if (maxH!=altitude_not_valid || minH!=altitude_not_valid){
    	menu.append("График высот",(graphF)?Interface.checkIcon:Interface.cancelIcon);
    	menuI[cnt++]=10;
    }
    
    menu.setSelectCommand(Interface.select);
    menu.addCommand(Interface.exit);
    menu.addCommand(Interface.back);    
    menu.setCommandListener(new CommandListener() {
        public void commandAction(Command c, Displayable d) {
                   
           if (c==Interface.select){ 
                int i=((List)d).getSelectedIndex();   
               switch (menuI[i]){               		
                   case 0:settings(menu);break;
                   case 1:conduct();info(menu);break;
                   case 2:NAVI.SetGP(track_beg());conduct();MobNav1.display.setCurrent(MobNav1.gCanvas);break;
                   case 3:NAVI.SetGP(track_end());conduct();MobNav1.display.setCurrent(MobNav1.gCanvas);break;                      
                   case 4:                	   
                       rename(menu);                                                 
                       break;
                   case 5:
                	   Tracks.unload(fname);
                	   if (Tracks.size()>0)
                		   MobNav1.display.setCurrent(ret);
                	   break;
                   case 6:delite(ret,menu);break;              	                   	                   	                   	                          
                   case 7:
                	   pathF^=true;
                       menu.set(i, s_conduct, (pathF)?Interface.checkIcon:Interface.cancelIcon);                       
                       break;
                   case 8: 
                       inverseF^=true;
                       menu.set(i,s_inverse, (inverseF)?Interface.checkIcon:Interface.cancelIcon);                       
                       break;                      
                   case 9:
                	   runnerF^=true;                	   
                	   menu.set(i, s_running,(runnerF)?Interface.checkIcon:Interface.cancelIcon);
                	   break;
                   case 10:  //график высот
                	/*   conduct(true);
                	   Graph.init(this_,0,0,240,320);                	   
                	   MyCanvas.SetMode(MyCanvas.graph);
                	   MobNav1.display.setCurrent(MobNav1.gCanvas);*/
                	   graphF^=true;
                	   menu.set(i, "График высот",(graphF)?Interface.checkIcon:Interface.cancelIcon);
                	   break;
               }
            }else if (c==Interface.back){
            	conduct();
                Tracks.refresh();
                MobNav1.display.setCurrent(ret);
           }else if (c==Interface.exit){
        	   conduct();
               MobNav1.display.setCurrent(MobNav1.gCanvas);
           }
        }
});
    MobNav1.display.setCurrent(menu);
    
}
// ##############################################################################


}
//java.lang.NullPointerException - if image is null.
//java.lang.IllegalStateException - if target is not bound.
