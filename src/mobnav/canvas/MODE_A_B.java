package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */


import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.*;

import mobnav.tracks.Track;
import mobnav.tracks.Tracks;

import java.util.Stack;
public class MODE_A_B extends MODE_DEF{
	
	public void keyPressed(int keyCode){
		 if (key(keyCode))
		        synchronized(MyCanvas.update){MyCanvas.update.notify();}
		 else
			 NAVI.naviMapKeys(keyCode);
	} 
	public void keyReleased(int keyCode){
		NAVI.mapMovingKeyIsPressed=false;
	}
	public void keyRepeated(int keyCode){
		if (NAVI.map==null ) 
			return;
		MyCanvas.startUpdCanvas();
	}
	
	
	
public void pointerPressed(int x, int y){
	 MyCanvas.mode[MyCanvas.navi].pointerPressed(x, y);
}
public void pointerDragged(int x, int y){
	 MyCanvas.mode[MyCanvas.navi].pointerDragged(x, y);	
}
public void pointerReleased(int x, int y){
	 MyCanvas.mode[MyCanvas.navi].pointerReleased(x, y);	
}
	
	
public static Point A=null,B=null;
private static Stack waypoints;
static Track abt=null;
static private String []routeModea={"driving","walking","bicycling"};

static private boolean optimize=false;
static private int searchMode=1;
static private int restrictions=0;

static private int routeModei=0;


static BUTON_SOFT_KEY leftsk=new BUTON_SOFT_KEY(Interface.s_pointA,null,-6,0xeeeeee);
static BUTON_SOFT_KEY rightsk=new BUTON_SOFT_KEY("Отмена",null,-7,0xeeeeee);
static BUTON_SOFT_KEY centrsk=new BUTON_SOFT_KEY("Метки",null,Canvas.FIRE,0xeeeeee);
public BUTON_SOFT_KEY leftsk(){return leftsk;}
public BUTON_SOFT_KEY rightsk(){return rightsk;}
public BUTON_SOFT_KEY centrsk(){return centrsk;}

public BUTON_TOUCH_SCR plus(){return BUTON.PLS;}
public BUTON_TOUCH_SCR minus(){return BUTON.MIN;}
public BUTON_TOUCH_SCR add(){return BUTON.ADD;}
public BUTON_TOUCH_SCR light(){return BUTON.LIGHT;}
public BUTON_TOUCH_SCR next(){return BUTON.NEXT;}



static public boolean FromHereToB_WithoutWaypoints(){return B!=null && waypoints==null;}
static public void SetPointA(final Point p){
    A=new Point(p); 
    leftsk.txt=(B!=null)?Interface.s_OK:Interface.s_pointB;

}
static public void SetPointB(final Point p){
    B=new Point(p); 
    leftsk.txt=(A!=null)?Interface.s_OK:Interface.s_pointA;
}
static public void SetPointVia(final Point p){
    if (waypoints==null)
        waypoints=new Stack();
    if (waypoints!=null && waypoints.size()==8)
        MyCanvas.SetErrorText("too many waypoints");
    else
        waypoints.addElement(new Point(p)); 
}


static public void get_AB_Track(){
	waypoints=null;
	searchMode=0;
	getTrack();
}
static private void getTrack(){
    if (searchMode==1)
        MyCanvas.SetOldMode();
    Loading.start("ROUTING", true, true,MobNav1.gCanvas);
    new Thread(new Runnable() {
	    public void run() {
	      //  Stack locs=new Stack();
	        String fname = GOOGLE_T.get(new Location(A), new Location(B),routeModea[routeModei],waypoints,optimize,restrictions);
	        Loading.stop();        
	        if (fname!=null){	        	
	        	Storage.autoloadMode = searchMode!=1;   		        		       
	        	Storage.loadPLTTrackT(fname);
	        }else
	        	MyCanvas.SetErrorText("ERROR");	     
	    }}
    ).start();  
}


static public void fromA2B(int TrackProvider, int searchMode, int routeMode, boolean optimize, int restrictions){
    MODE_A_B.searchMode=searchMode;
    MODE_A_B.restrictions=restrictions;
    MODE_A_B.optimize=optimize;
    MODE_A_B.routeModei=routeMode;
    if (searchMode==1){
        A=B=null;
        if (waypoints!=null)
            waypoints.removeAllElements();
        waypoints=null;
        leftsk.txt=Interface.s_pointA;
        MyCanvas.SetMode(MyCanvas.AtoB);
    }else{
        A=new Point(NAVI.getGP());
        if (abt!=null){
        	Tracks.remove(abt);
        	abt=null;
        }
        getTrack();        
    }
}


 public void paint(Graphics g){	
	MyCanvas.mode[MyCanvas.navi].paint(g);
	if (abt==null || Tracks.isLoaded(abt)){		
	   if (A!=null){
	       Point p=NAVI.getPoint(A);
	       g.drawImage(Interface.Aicon,p.x-10,p.y-33,0);
	   } 
	   if (B!=null){
	        Point p=NAVI.getPoint(B);
	        g.drawImage(Interface.Bicon,p.x-10,p.y-33,0);
	   }
	    if (waypoints!=null && waypoints.size()>0){
	        int len=waypoints.size();
	        for (int i=0; i<len; i++){                    
	            Point p=NAVI.getPoint((Point)waypoints.elementAt(i));
	            g.drawImage(Interface.viaIcon,p.x-5,p.y-5,0);
	        }          
	    }
	}else{
		abt=null;
		A=B=null;
		waypoints=null;
	}
}


    public boolean key (final int keyCode){  
    	System.out.println("mode ab key");
    switch (keyCode) {
//-6 -7
        //LeftSoftKey
        case -7:A=B=null;waypoints=null;MyCanvas.SetOldMode();break;
        //case -6:;break;   
        case -6:
            if (leftsk.txt.endsWith(Interface.s_pointA)){
                SetPointA(NAVI.getGP());
            }else if (leftsk.txt.endsWith(Interface.s_pointB)){
                SetPointB(NAVI.getGP());
            }else if (A!=null && B!=null)
                getTrack();          
            break;
           
        case -5:
        case MyCanvas.FIRE:Labels.menu();break;                                               
        default:NAVI.naviMapKeys(keyCode);            
    }    
    return true;
}
    
    
    static void Set(){

        if (MyCanvas.GetMode()==MyCanvas.navi){
            Form f=new Form("Маршрут");

            Image []img=new Image[1];
            img[0]=Interface.googleIcon;
            final ChoiceGroup trackingProvider=new ChoiceGroup("", ChoiceGroup.POPUP, new String[]{"Google"},img);
            f.append(trackingProvider);
            
            final ChoiceGroup searchMode=new ChoiceGroup("Travel Mode",ChoiceGroup.POPUP,new String[]{"From here to B","From A to B"},null);
            if (FromHereToB_WithoutWaypoints())        
                f.append(searchMode); 
            else
                searchMode.setSelectedIndex(1, true);

            final ChoiceGroup travelMode=new ChoiceGroup("Travel Mode",ChoiceGroup.POPUP,new String[]{"Авто","Пешком","Велосипед"},null);         
            f.append(travelMode);
            final ChoiceGroup optimize=new ChoiceGroup("Optimize directions",ChoiceGroup.POPUP,new String[]{Interface.s_no,Interface.s_yes},null);
            f.append(optimize);     
            final ChoiceGroup restrictions = new ChoiceGroup("Restrictions",ChoiceGroup.POPUP,new String[]{Interface.s_no,"avoid tolls","avoid highways"},null);
            f.append(restrictions);       
            f.addCommand(Interface.exit);
            f.addCommand(Interface.ok);

            f.setCommandListener(new CommandListener() {
                    public void commandAction(Command c, Displayable s) {
                        if (c==Interface.exit)
                            MobNav1.display.setCurrent(MobNav1.gCanvas);
                        else if (c==Interface.ok){
                                fromA2B(
                                        trackingProvider.getSelectedIndex(),
                                        searchMode.getSelectedIndex(),
                                        travelMode.getSelectedIndex(),
                                        optimize.getSelectedIndex()==1,
                                        restrictions.getSelectedIndex());
                                MobNav1.display.setCurrent(MobNav1.gCanvas);
                            }
                    }
                });
                MobNav1.display.setCurrent(f);
        }
    }
    
}
