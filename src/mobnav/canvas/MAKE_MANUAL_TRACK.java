package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */


//import javax.microedition.lcdui.game.*;
//import com.nokia.mid.ui.DeviceControl;
import javax.microedition.lcdui.*;

import mobnav.math.math;

public class MAKE_MANUAL_TRACK extends MODE_DEF{
	
	
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

	static BUTON_SOFT_KEY leftsk=new BUTON_SOFT_KEY(Interface.s_OK,null,-6,0xeeeeee);
	static BUTON_SOFT_KEY centrsk=new BUTON_SOFT_KEY("Put",null,Canvas.FIRE,0xeeeeee);
	static BUTON_SOFT_KEY rightsk=new BUTON_SOFT_KEY("Отмена",null,-7,0xeeeeee);
	   	
	public BUTON_SOFT_KEY leftsk(){return leftsk;}
	public BUTON_SOFT_KEY rightsk(){return rightsk;}
	public BUTON_SOFT_KEY centrsk(){return centrsk;}
    
	public BUTON_TOUCH_SCR plus(){return BUTON.PLS;}
	public BUTON_TOUCH_SCR minus(){return BUTON.MIN;}
	public BUTON_TOUCH_SCR add(){return BUTON.ADD;}
    public BUTON_TOUCH_SCR light(){return BUTON.LIGHT;}
    public BUTON_TOUCH_SCR next(){return BUTON.NEXT;}
    
    public boolean key(final int keyCode){
    switch (keyCode) {
        case -7:MyCanvas.SetOldMode();break;
        case -6:MyCanvas.SetOldMode();MAKE_TRACK.EndCreateTrackM();break;   
        case -5:
        case MyCanvas.FIRE:
            MakeTrack();
            break;                                               
        default:NAVI.naviMapKeys(keyCode);                    
    }   
    return true;
}
// ########################################################################
static int oldworkWithPointI=-2;
static byte cntM=0;
public static int workWithPointI=-1;
static boolean resetM=false;
static Point oldgp=new Point(-1,-1);
static void MakeTrack(){
    if (resetM){
        workWithPointI=-1;
        resetM=false;
        cntM=0;      
        return;
    }    
    if (workWithPointI>=0){
        if (oldworkWithPointI==workWithPointI){
            cntM=2;           
        }else{           
            oldgp=new Point(NAVI.getGP());
            cntM=1;
            oldworkWithPointI=workWithPointI;
        }
    }else{        
        cntM=0;            
        MAKE_TRACK.AddPointM(NAVI.getGP());        
    }
    //if (cntM==1)
    if (cntM==2){           
        MAKE_TRACK.RemovePointM(workWithPointI);        
    }   
    workWithPointI=-1;
}
private static boolean inSqere(Point p){return Math.abs(p.x-(MyCanvas.max_x>>1))<5 && Math.abs(p.y-(MyCanvas.max_y>>1))<5;}
private static boolean somePointActive=false;
private static int DOF(final Point[]p, final int i){
    int R=3;
    
    if (inSqere(p[i&1])){  
        if (i==0){
        	centrsk.txt="Put";
            somePointActive =false;
        }
        workWithPointI=i;
        R=5;
        somePointActive=true;        
        if (oldworkWithPointI==workWithPointI){
            if (cntM==0 || cntM==1 && resetM==false)
            	centrsk.txt="Kill";
        }else if (cntM==0)
        	centrsk.txt="Move";                
        
    }
    return R;
}    
    
 private static void DrawMakeTrack(Graphics g){
	 centrsk.txt="Put";
     somePointActive=false;
        workWithPointI=-1;
        Point p[]=new Point[2];
        Point t=MAKE_TRACK.GetFirstPointM();        
        if (t==null)
            return;
        g.setColor(Settings.TRACK_IN_MEMORY_RGB);
        p[0]=NAVI.getPoint(t);
        int R=DOF(p,0);
        math.fillCircle(g, p[0], R);
        int i=0;                 
        while ((t=MAKE_TRACK.GetNextPointM())!=null){                      
            p[(++i)&1]=NAVI.getPoint(t);   
            Point p0=new Point(p[0]);
            Point p1=new Point(p[1]);
            if (NAVI.onCanvas(p0,p1) ){
            	g.drawLine(p0.x, p0.y, p1.x, p1.y);
                R=DOF(p,i);
                math.fillCircle(g, p[i&1], R);
            }
        }                         
        if (somePointActive==false){
            oldworkWithPointI=-2;
        }            
}
//------------------------------------------------------------------------------

public  void paint(Graphics g){
	NAVI.setArowNavigation();
	MyCanvas.mode[MyCanvas.navi].paint(g);
    if (MyCanvas.GetMode()==MyCanvas.manual_track_make){
        if ((oldgp.x!=NAVI.getGP().x || oldgp.y!=NAVI.getGP().y) && cntM==1){
            resetM=true;
            MAKE_TRACK.SetPointM(NAVI.getGP(), workWithPointI);
        }
        DrawMakeTrack(g);
    }
    
    
    
    
    
}

 
 
}