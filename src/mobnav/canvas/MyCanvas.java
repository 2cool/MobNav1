package mobnav.canvas;
/**
 *
 * @author 2cool
 */



import javax.microedition.lcdui.game.*;
import com.nokia.mid.ui.DeviceControl;
import javax.microedition.lcdui.*;

import mobnav.gps.BTGPS;
import mobnav.gps.GSV;
import mobnav.tracks.TrackRecording;
import mobnav.tracks.Tracks;





public class MyCanvas extends Canvas implements Runnable {
		
	static public boolean touchPhone= false;
	static boolean keyPressed=false;
	
	
	
static public int lights=0;
static public void ChangeLights(){
    if ((lights+=50)>100)
        lights=0;
    DeviceControl.setLights(0, lights);
}


static public boolean direct_to_label=	false;

//----------------------------------------------------------------------------

//----------------------------------------------------------------------------
public MobNav1 mn;
//static private final int DRAW_MAPER=0,DRAW_SATTELITES=1,DRAW_INFORMATION=2,DRAW_INFORMATION1=3;

//static public int screenMODE=0;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@



static public int max_x,max_y;
static final public int SLEEP_TIME_SHORT=33;
static private int[] greyData;
static int min2;
static private String error=null;
static public boolean keeprunning = false;
static private long errorShowTime;

final static public Object update=new Integer(0);

static private boolean startUpdCanvas;
static public boolean showTrack=true;


static public void SetErrorText(String str){
     // _ystem.out.println(str);
     synchronized(update){
                errorShowTime=System.currentTimeMillis();
                error=str;
                update.notify();
            }
        
}

// ###########################################################################
static public void startUpdCanvas(){
        if (startUpdCanvas==false){
            synchronized(update){
                startUpdCanvas=true;
                update.notify();
            }
    }
}
//###########################################################################
static public void stopUpdCanvas(){
    if (startUpdCanvas){
        synchronized(update){
            startUpdCanvas=false;
                update.notify();                
            }
        
    }
    
}
// ###########################################################################
// ###########################################################################

static public long sleep_time=SLEEP_TIME_SHORT;
static public boolean busy=false;
static public boolean forseRepaint=false;

private final static int FOREVAR=0,ONE_SEC=1000;
private static boolean iRepaint=false;



public static final int navi=0,sattelites=1,turist=2,sport=3, graph=4,AtoB=5, manual_track_make=6,menu=7,loading=8;
static MODE []mode;
static public final int MAX_SCREENS=5;

public void run() {    

	//boolean test=onCanvas(new Point(340,-100),new Point(-100, 420));
	double t=(double)getWidth()/240.0;
	mode=new MODE[]{
			new NAVI(),
			new Sattelites(),
			new LCD(),
			new LCDD((int)(57*t),(int)(22*t),(int)(14*t)),				
			new Graph(),
			new MODE_A_B(),
			//new CancelRequest(),
			//new Empty(),
			new MAKE_MANUAL_TRACK(),
			Interface.navMenu,
			new Loading()
			};	
	
	boolean repaint=false;
    try{              
    TrackRecording.start();
    
 
    while(keeprunning) {
    	if (sleep_time==0 && repaint==true)
        	sleep_time=500;
        try {
            synchronized(update){update.wait(sleep_time);}
        }catch(Exception e) {System.out.println("update0001");}
        
        sleep_time=(BTGPS.isOn()==false)?ONE_SEC:FOREVAR;
        if (cur_screen==loading)
        	sleep_time=500;
        else if (startUpdCanvas || NAVI.mapDrawed==false)
        	sleep_time=SLEEP_TIME_SHORT;
        
       // sleep_time=(startUpdCanvas || cur_screen==loading ||  NAVI.mapDrawed==false)?((cur_screen==loading)?500:SLEEP_TIME_SHORT):FOREVAR;
        
            if (sleep_time==FOREVAR || sleep_time==ONE_SEC)
            	services();  
            if (busy==false || forseRepaint){
            	forseRepaint=repaint=false;
            	iRepaint=true;
            	repaint();            	
            }else
            	repaint=true;

        
    }
    }catch (Exception ex){SetErrorText("run "+ex.toString());}
}
// ###########################################################################
 

static private int count=0,errorCnt=0,MAX_ERRCNT=10;
static private int oldModeI=0;
static int cur_screen=0;
static private byte[]oldmode=new byte[12];
static public boolean allRepaint=true;
public static void SetOldMode(){
	allRepaint=true;
    if (oldModeI>0){
        cur_screen=oldmode[--oldModeI];
        if (cur_screen==navi)
        	MAP.force=true;
    }
    System.out.println("old: setMode "+cur_screen);
}
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
public static void SetMode(int m){
	allRepaint=true;
    if (cur_screen!=m){
    	if(m==navi)
    		MAP.force=true;
        oldmode[oldModeI++]=(byte)cur_screen;
        cur_screen=m;
    }
    System.out.println("setMode "+cur_screen);
}
public static int GetMode(){return cur_screen;}
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
static public void nextScreenMode(){changeScreenMode(1);}
static public void prevScreenMode(){changeScreenMode(-1);}
static private void changeScreenMode(final int add){
	allRepaint=true;
	do{
		cur_screen+=add;
		if (cur_screen<0)
			cur_screen=graph;
		if (cur_screen==graph && Tracks.graph==false)
			cur_screen+=add;
		if (cur_screen>=MAX_SCREENS)   
			cur_screen^=cur_screen;	
		
	}while ((Settings.working_screensMask&(1<<cur_screen)) == 0);
	System.out.println("next: setMode "+cur_screen);
}
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
public static void Update(){
     Storage.load();    
     Interface.UpdateMenu();
}

/*
public void tempVTG(){
    if (NMEA.tmg!=0){
        int y=(int)(Math.cos(NMEA.tmg)*100);
        int x=(int)(Math.sin(NMEA.tmg)*100);
        g.setColor(0);
        g.drawLine(max_x>>1, max_y>>1, (max_x>>1)+x, (max_y>>1)-y);
        
    }
    
}
*/
////////////////////////////////////////////////////////////////////////
static Point []gpa=new Point[4];
static int gpai;
static Point oldGp=new Point();
static Point mid(final Point p1, final Point p0){
    return new Point((p1.x+p0.x)>>1,(p1.y+p0.y)>>1);
}
static long setLightsTime=0;
static private void services(){
        time=System.currentTimeMillis();

        
        if (sleep_time!=SLEEP_TIME_SHORT){
            if (BTGPS.isOn() && time-setLightsTime>=Settings.SET_LIGHTS_DTIME){
                    DeviceControl.setLights(0, lights);
                    setLightsTime=time;
            }
        }else
            setLightsTime=time;
        
            
}


// ################################ P A I N T ###########################
static public long time=0;
static public long lastTime=0;

// ###################################################################
public static long lastTimeConductorBeep=0;
// ###########################################################################
static final private int greyDataMaxH=20;
static int keyXSize,keyYSize;
    public MyCanvas(MobNav1 mn){
        //touchPhone= this.hasPointerMotionEvents() |  hasPointerMotionEvents();
        //System.out.println("MyCanvas");
        this.mn=mn;
        this.setFullScreenMode(true);
        max_x=getWidth();
        max_y=getHeight();
        keyXSize=max_x/3;
        keyYSize=max_y/5;
        NAVI.cross=new Point(max_x>>1,max_y>>1);
        min2=Math.min(max_x, max_y)>>1;
        greyData=new int[max_x*greyDataMaxH];
        int i=0;
        while (i<max_x*greyDataMaxH)
            greyData[i++]=0x33000000;
       
        
        
       // this.repaint();
    }
// ########## S T A R T ####################################
 public void start() {
 	keeprunning = true;
	Thread runner = new Thread(this);
	runner.start();
 }
// ##########################################################################
static public void GreyField(final int x, int y, int w, int h){
    if (x>max_x || y>max_y)
        return;
    if (x+w>max_x)
        w=max_x-x;
    while (h > greyDataMaxH){
        MyCanvas.g.drawRGB(greyData, 0, w, x, y, w, greyDataMaxH, true);
        h-=greyDataMaxH;
        y+=greyDataMaxH;
    }
    if (h>0)
        MyCanvas.g.drawRGB(greyData, 0, w, x, y, w, h, true);
}
// ##########################################################################
protected void showNotify(){repaint();}
// ##########################################################################

// ##########################################################################





//static private int shk=1;

// ##########################################################################
static public void Banner(final String str, final int bgColor, final int textColor,final Font font){
	if (str.length()==0)
		return;
    MyCanvas.g.setFont(font);
    int ls=font.charsWidth(str.toCharArray(), 0, str.length());
    int h=font.getHeight();
    int nstr=1+ls/max_x;           
    MyCanvas.g.setColor(bgColor);
    int y0=(max_y>>1)-((h*nstr)>>1);
    int y1= (max_y>>1)+((h*nstr)>>1);
    int x1,x0;
    if (ls>max_x){
        x0=0;
        x1=max_x;
    }else{
        x0=(max_x>>1)-(ls>>1);
        x1=(max_x>>1)+(ls>>1);
    }
    MyCanvas.g.fillRect(x0-5,y0-5,x1-x0+10,y1-y0+10);
    MyCanvas.g.setColor(textColor);
    int len=str.length()/nstr;
    int i=0,p=0;
    while (nstr>i){
        MyCanvas.g.drawString(str.substring(p, p+len), x0,y0, 0);
        y0+=h;
        p+=len;
        i++;
    }
}
static public void ErrorBanner(){
    if (error!=null){
        if (System.currentTimeMillis()-errorShowTime<4000){
        	Banner(error,0xff0000,0xffffff,Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));            
            return;
        }else
            error=null;
    }
}
// ##########################################################################
int makingTrackColor=0xff0000;
public static Graphics g=null;
//static public boolean dontShift=false;
 // ##########################################################################
static double spshcnt=0;
// ########################################################################


// ########################################################################
static long pointerTime=0;
protected void pointerPressed(int x, int y) {
	setLightsTime=pointerTime=System.currentTimeMillis();
    keyPressed=true;
    touchPhone=true;
    keyPressedTime=System.currentTimeMillis();
    super.pointerPressed(x, y);
    mode[cur_screen].pointerPressed(x,y);
    forseRepaint=true;
    synchronized(update){
        update.notify();
    }
}
// ########################################################################
protected void pointerDragged(int x, int y) {  
	setLightsTime=pointerTime=System.currentTimeMillis();
   super.pointerDragged(x, y);   
    mode[cur_screen].pointerDragged(x, y);        
}
// ########################################################################
protected void pointerReleased(int x, int y) {
    super.pointerReleased(x, y);
    forseRepaint=true;      
    mode[cur_screen].pointerReleased(x, y);
    keyPressed=false;
    synchronized(update){
        update.notify();
    }   
}




static long keyPressedTime=0;
protected void keyPressed(final int keyCode) {
    keyPressed=true;
    try{
	    super.keyPressed(keyCode);
	    forseRepaint=true;
	    mode[cur_screen].keyPressed(keyCode);      
	    setLightsTime=keyPressedTime=System.currentTimeMillis();              
    }catch (Exception ex){SetErrorText("keyPressed "+ex.toString());};
 }

// ########################################################################
protected void keyReleased(int keyCode) {    
    super.keyReleased(keyCode);
    mode[cur_screen].keyReleased(keyCode);
    keyPressed=false;
}
// ########################################################################
protected void keyRepeated(int keyCode) {
    super.keyRepeated(keyCode);
    mode[cur_screen].keyRepeated(keyCode);    
    setLightsTime=System.currentTimeMillis();         
}

// ##########################################################################
static double oldSubAngle=0;
static int angleChangeCount=0;
static int subAngle=0;
static double anng=0;
static double danng=0.1;
static public void autoRotateMapReset(){
	oldSubAngle=0;
	angleChangeCount=0;
	subAngle=0;
	anng=0;
	danng=0.1;
}
static public boolean fastResponse=false;




static public Point crossLabel=new Point();
//##########################################################################
static int SPEED_SHIFT_MIN=30;

static int[]rotate={Sprite.TRANS_NONE,Sprite.TRANS_ROT90,Sprite.TRANS_ROT180,Sprite.TRANS_ROT270};
//############################################################################
//static int tttt=0;
public void paint(Graphics g){
	if (iRepaint==false)
		allRepaint=true;
	iRepaint=false;
//	System.out.println(tttt++);
	busy=true;
	try{
		MyCanvas.g=g;
	    Update();	    
		
	    GSV.draw=cur_screen==sattelites;
       	 mode[cur_screen].paint(g);	
       	 
       	 count++;

         g.setColor(0);
         errorCnt=0; 
         BUTON.paint(g,mode[cur_screen]);

	}catch (Exception e){
   	 System.out.println("Pain Error "+e);
	 MAP.redraw++;
     if (++errorCnt>MAX_ERRCNT){
    	 TrackRecording.SAVE_STATE_IF_ERROR_END_EXIT();
         MyCanvas.SetErrorText("paint "+e+" ");
     }
 }

 catch (OutOfMemoryError e){
//     SetErrorText("no memory");
	 MAP.redraw++;
     if (++errorCnt>MAX_ERRCNT){
         TrackRecording.SAVE_STATE_IF_ERROR_END_EXIT();
         Storage.mapError("Out Of Memory",false);                     
     }
    
 }
 ErrorBanner(); 
 busy=false;
 
}
}

