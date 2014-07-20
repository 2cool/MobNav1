package mobnav.canvas;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;


public class MODE_DEF  implements MODE{
	public BUTON_SOFT_KEY leftsk(){return null;};
	public BUTON_SOFT_KEY rightsk(){return null;};
	public BUTON_SOFT_KEY centrsk(){return null;};    
	public BUTON_TOUCH_SCR plus(){return null;};
	public BUTON_TOUCH_SCR minus(){return null;};
	public BUTON_TOUCH_SCR add(){return null;};
	public BUTON_TOUCH_SCR light(){return null;};
    public BUTON_TOUCH_SCR next(){return null;};
    public void paint(Graphics g){};
    public void keyPressed(int keyCode){key(keyCode);}; 
    public void keyReleased(int keyCode){};
    public void keyRepeated(int keyCode){};
    public static boolean draged=false;
	public static int oldX,oldY;
	public static long pressTime=-1;
	static final int TRIMOR=10;
	static final long longClickDelay=500;
	public void pointerPressed(int x, int y){
		Menu.tochPhoneMenu();
		oldX=x;oldY=y;
		draged=false;
		pressTime=System.currentTimeMillis();	

		
	}
	public void pointerDragged(int x, int y){
		if (!isClick(x,y,oldX,oldY)){
			System.out.println("draged");
			draged=true;		
		}
	}

	int oldClickX=0;
	int oldClickY=0;
	long oldClickTime=0;
	private static boolean isClick(final int x1, final int y1, final int x2, final int y2){
		return (Math.abs(x1-x2)<TRIMOR && Math.abs(y1-y2)<TRIMOR);;		
	}
	public void pointerReleased(int x, int y){
		if (isClick(x,y,oldX,oldY)){
			if (System.currentTimeMillis()-pressTime < longClickDelay){				
				if (pressTime-oldClickTime<longClickDelay && isClick(oldClickX,oldClickY, oldX, oldY)){
					oldClickTime=0;
					click(oldX,oldY);
					System.out.println("click");
					dublClick(oldX,oldY);	
					System.out.println("duble click");
				}else{
					oldClickTime=pressTime;
					oldClickX=oldX;
					oldClickY=oldY;
					click(oldX,oldY);
					System.out.println("click");
				}
			}else{
				System.out.println("longclick");
				longClick(x,y);
			}
		}else
			slideX(x<oldX);
		pressTime=0;
	}
	
	//---------------------------------------
	public void slideX(boolean right){
		if (right)
			MyCanvas.nextScreenMode();
		else
			MyCanvas.prevScreenMode();
	}
	//-----------------------------------------
	public void draged(int x, int y){}
	public void click(int x, int y){};
	public void longClick(int x, int y){};
	public void dublClick(int x, int y){}
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean key(final int keyCode){
   	 boolean upd=true;
	    if (MenuStr.KeyPressed(keyCode)==false)
	        switch (keyCode) {
	            case Canvas.KEY_POUND:upd=false;MyCanvas.ChangeLights();break;
	        }    
	    if (upd)
	        synchronized(MyCanvas.update){MyCanvas.update.notify();}
	    return false;
	}

}
