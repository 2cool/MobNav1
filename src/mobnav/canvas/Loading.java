package mobnav.canvas;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;


public class Loading  extends MODE_DEF{
	
	
	public void keyPressed(int keyCode){
		key(keyCode);
	} 
	public void keyReleased(int keyCode){}
	public void keyRepeated(int keyCode){}
	public void pointerPressed(int x, int y){
		BUTON.screenPressed(x, y, MyCanvas.mode[MyCanvas.loading]);
	}
	public void pointerDragged(int x, int y){
		BUTON.pointerDragged(x,y,MyCanvas.mode[MyCanvas.cur_screen]);
	}
	public void pointerReleased(int x, int y){
		BUTON.screenRelissed(x,y,MyCanvas.mode[MyCanvas.loading]);
	}
	
	
	
	
	
	BUTON_SOFT_KEY rightsk=new BUTON_SOFT_KEY("Отмена",null,-7,0xeeeeee);
	
	public BUTON_SOFT_KEY leftsk(){return null;}
	public BUTON_SOFT_KEY rightsk(){return cancelRequest?rightsk:null;}
	public BUTON_SOFT_KEY centrsk(){return null;}
    
	public BUTON_TOUCH_SCR plus(){return null;}
	public BUTON_TOUCH_SCR minus(){return null;}
	public BUTON_TOUCH_SCR add(){return null;}
    public BUTON_TOUCH_SCR light(){return null;}
    public BUTON_TOUCH_SCR next(){return null;}
    
    
    
    static private int shk=1;
    static private int doneLen=0;
    static public int done=0;
    static private boolean LOADING=false;
    static private boolean LOADING_CANCELED=false;
    static private String loadingStringMsg="?";
    
	public void paint(final Graphics g){
		if (MAP.frame!=null)
		MAP.drawMap(g);
		
        if (LOADING_CANCELED==true && done==-1){
            LOADING=false;
            Interface.turnGps(false);

        }
        int len=MyCanvas.max_x-21;
        if (done<0){
            if (doneLen>=len)
                shk=-1;
            else if (doneLen<=0)
                shk=1;
            doneLen+=(1*shk);
        }else{
            doneLen=(len*done)>>7;
        }
        int y=MyCanvas.max_y>>1;
        MyCanvas.GreyField(0,y-45,MyCanvas.max_x-1,80);
        MyCanvas.GreyField(0,y-45,MyCanvas.max_x-1,80);
        g.setColor(0xffffff);
        g.drawRect(0,y-45,MyCanvas.max_x-1,80);
        g.drawString(loadingStringMsg, 10, y-40, 0);
        MyCanvas.GreyField(10, y, doneLen, 20);
        MyCanvas.GreyField(10, y, doneLen, 20);
        g.drawRect(10, y, len, 20);
        g.drawLine(10+doneLen, y, 10+doneLen, y+20);           	    		
	}
	static public boolean isLoadingMode(){return LOADING;}
	static public boolean CANCELED(){
		boolean ret=LOADING_CANCELED;
		if (ret)
			LOADING_CANCELED=false;
		return ret;
		}
	static public void renameString(String text){loadingStringMsg=text;}
	
	
	
	
	static private boolean cancelRequest=false;
	static public void start(String text, boolean cancelRt, boolean cicle,Displayable ret){
		Loading.cancelRequest=cancelRt;
		MyCanvas.SetMode(MyCanvas.loading);
		MobNav1.display.setCurrent(MobNav1.gCanvas);

	    LOADING_CANCELED=false;
	    
	    MyCanvas.startUpdCanvas();
	    done=(cicle)?-1:0;
	    LOADING=true;
	    loadingStringMsg=text;
	}
	static void stop(){
	    if (LOADING==true){
	    	LOADING_CANCELED=true; 
	        LOADING=false;
	        MyCanvas.SetOldMode();  
	        MyCanvas.stopUpdCanvas();
	        //MobNav1.display.setCurrent(dis);
	    }

	}

	public boolean key(final int keyCode){
		if (keyCode==-7){
			 Alert a = new Alert(Interface.s_cancel+"?");//,"Are you sure?",null,AlertType.CONFIRMATION);
		        a.setString(Interface.s_to_cancel);
		        a.addCommand(Interface.yes);
		        a.addCommand(Interface.no);
		        a.setCommandListener(new CommandListener(){
		            public void commandAction(Command c, Displayable d){
		                if (c == Interface.yes){
		                	stop();		                                        
		                }                
		                MobNav1.display.setCurrent(MobNav1.gCanvas);
		                synchronized(MyCanvas.update){
		                	MyCanvas.update.notify();
		                }
		            }});
		        MobNav1.display.setCurrent(a);
		        return true;
		}else
			return false;
	}

}





