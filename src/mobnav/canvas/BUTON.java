package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */

import javax.microedition.lcdui.*;
//import java.util.Stack;

import mobnav.canvas.*;

class BUTON_TOUCH_SCR{
    static public MODE mode;
    private static int sn=1;
    private MenuStr ms;
    private int keyCode,n,x=0,y=0;
    private Image icon;   
    public BUTON_TOUCH_SCR(final MenuStr ms,final int keyCode, final Image icon){
        this.ms=ms;
        this.keyCode=keyCode;
        this.n=sn++;
        this.icon=icon;
        
        
    }
    static public void  Paint(final Graphics g, final BUTON_TOUCH_SCR c){
        if (c!=null)
            c.Pain(g);
    }
    public void Pain(final Graphics g){

        x=g.getClipWidth()-Interface.buttonIcon.getWidth()-4;
        y=Interface.buttonIcon.getHeight()+((Interface.buttonIcon.getHeight()+8)*n);
        g.drawImage(Interface.buttonIcon, x, y, 0);
        g.drawImage(icon, x+2, y+1, 0);        
    }
    static public boolean Pressed(final BUTON_TOUCH_SCR c,final int x, final int y){
        if (c!=null){
            c.relissed(x, y);
        }
        return false;
    }
    
    
   public boolean pressed(final int x, final int y){
	boolean ret;
   	if (ret=(x>=this.x && y>=this.y && x<=(this.x+Interface.buttonIcon.getWidth()) && y<=(this.y+Interface.buttonIcon.getHeight())))
   		BUTON.presedButton=this;   	
   	return ret;
   }
    
    
    public boolean relissed(final int x, final int y){
        if  (this==BUTON.presedButton && x>=this.x && y>=this.y && x<=(this.x+Interface.buttonIcon.getWidth()) && y<=(this.y+Interface.buttonIcon.getHeight())){
        	mode.keyPressed(this.keyCode);                                           
            return true;
       }
       if (ms!=null){
           Interface.MenuDo(ms,false);
           return true;
       }
    
    return false;
       
    }
    
    
}
//####################################################################################
class BUTON_SOFT_KEY{
	 static public MODE mode;
    String txt;
    MenuStr ms;
    private int keyCode;
    private int color;
    private int x,y,width;
    public static int height;

    public BUTON_SOFT_KEY (final String txt,final MenuStr ms,final int keyCode,final int color){
         this.txt=txt;
         this.ms=ms;
         this.keyCode=keyCode; 
         this.color=color;
    }
    public boolean pressed(final int x, final int y){
    	boolean ret;
    	if ( ret=(x>=this.x && y>=this.y && x<=(this.x+this.width) && y<=(this.y+height)))
    		BUTON.presedButton=this;
    	return ret;
    }
    public boolean relissed(final int x, final int y){
        if (this==BUTON.presedButton &&  x>=this.x && y>=this.y && x<=(this.x+this.width) && y<=(this.y+height)){
        	mode.keyPressed(this.keyCode);                               
                return true;
         }
           if (ms!=null){
               Interface.MenuDo(ms,false);
               return true;
           }
        
        return false;
       
    }
    static private int fontH=0;
    public static int height(){
    	fontH=BUTON.font.getHeight();
    	height=(MyCanvas.touchPhone)?tochButonYSize:fontH+4;
    	return height;
    }
    private int strWidth=0;
    static private final int  tochButonXSize=50;
    static private final int tochButonYSize=30;
    private int width(final String txt){
    	strWidth=BUTON.font.stringWidth(txt);
    	width=strWidth+BUTON.border;
    	if (MyCanvas.touchPhone && width<tochButonXSize)
    		width=tochButonXSize;
    	return width;
    }
public void RightSoftKeyPaint(Graphics g){    
    width(txt);
    x=MyCanvas.max_x-width;
    y=MyCanvas.max_y-height;
    Paint(g);  
}

public void LeftSoftKeyPaint(Graphics g){	
    width(txt);
    x=0;
    y=MyCanvas.max_y-height;
    Paint(g);       
}
public void CentrSoftKeyPaint(Graphics g){
    int strWidth=width(txt);
    x=(MyCanvas.max_x-strWidth)>>1;
    y=MyCanvas.max_y-height;
    Paint(g);                              
}
    public void Paint(Graphics g){
        g.setColor(color);
        g.fillRect(x, y, width-1, height-1);
        g.setColor(0);
        g.drawRect(x, y, width-1, height-1);
        if (BUTON.presedButton==this){
	      g.setColor( (color>>1)&0x7f7f7f );
	        g.drawLine(x+1, y+1, x+width-1, y+1);
	        g.drawLine(x+1, y+2, x+1, y+height-2);
	        g.setColor(0xc7f59d); 
	        g.drawLine(x+1, y+height-1, x+width-1, y+height-1);
	        g.drawLine(x+width-1,y+2,x+width-1,y+height-2);
	        g.setColor(0);
        }
        g.drawString(txt, x+((width-strWidth)>>1), y+((height-fontH)>>1), 0);
}
}






public class BUTON {
	
	 static final BUTON_TOUCH_SCR PLS=new BUTON_TOUCH_SCR(null,MyCanvas.KEY_NUM1,Interface.plusIcon);
	 static final BUTON_TOUCH_SCR MIN=new BUTON_TOUCH_SCR(null,MyCanvas.KEY_NUM3,Interface.minusIcon);
	 static final BUTON_TOUCH_SCR ADD=new BUTON_TOUCH_SCR(null,MyCanvas.KEY_STAR,Interface.addSIcon);
	 static final BUTON_TOUCH_SCR LIGHT=new BUTON_TOUCH_SCR(null,MyCanvas.KEY_POUND,Interface.sunIcon);
	 static final BUTON_TOUCH_SCR NEXT=new BUTON_TOUCH_SCR(null,MyCanvas.KEY_NUM2,Interface.nextIcon);
	    
	
	
	
	
	
  static final int MOUDS=6;
  
  
  





public static Font font;
static public void paint(Graphics g,final MODE mode){
    font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
    g.setFont(font);
  
    if (MyCanvas.touchPhone){
    	if (mode.plus()!=null)mode.plus().Pain(g);
    	if (mode.minus()!=null)mode.minus().Pain(g);
    	if (mode.add()!=null)mode.add().Pain(g);
    	if (mode.light()!=null)mode.light().Pain(g);
    	if (mode.next()!=null)mode.next().Pain(g);
       
        
    }
    BUTON_SOFT_KEY.height();
    
    if (mode.leftsk()!=null)
        mode.leftsk().LeftSoftKeyPaint(g);
    if (mode.rightsk()!=null)
    	mode.rightsk().RightSoftKeyPaint(g);
    if (mode.centrsk()!=null)
    	mode.centrsk().CentrSoftKeyPaint(g);
    
}
static public int border=8;

static public Object presedButton=null;
static public  boolean screenPressed(final int x, final int y, final MODE mode){

	BUTON_TOUCH_SCR.mode=BUTON_SOFT_KEY.mode=mode;
    if (MyCanvas.touchPhone){
        
        if (mode.light()!=null && mode.light().pressed(x, y)){
            return true;
        }if (mode.add()!=null && mode.add().pressed(x, y))
            return true;
        if (mode.plus()!=null && mode.plus().pressed(x, y))
            return true;
        if (mode.minus()!=null && mode.minus().pressed(x, y))
            return true;
        if (mode.leftsk()!=null && mode.leftsk().pressed(x,y)){
                return true;
        }if (mode.rightsk()!=null && mode.rightsk().pressed(x,y)){
            return true;
        }if (mode.centrsk()!=null && mode.centrsk().pressed(x, y)){
            return true;
        } 
        if (mode.next()!=null && mode.next().pressed(x, y))
            return true;
    }
    presedButton=null;
    return false;            
	
	
}




static public  boolean screenRelissed(final int x, final int y, final MODE mode){
	BUTON_TOUCH_SCR.mode=BUTON_SOFT_KEY.mode=mode;
    if (MyCanvas.touchPhone){
        
        if (mode.light()!=null && mode.light().relissed(x, y))
            return true;
        if (mode.add()!=null && mode.add().relissed(x, y))
            return true;
        if (mode.plus()!=null && mode.plus().relissed(x, y))
            return true;
        if (mode.minus()!=null && mode.minus().relissed(x, y))
            return true;
        if (mode.leftsk()!=null && mode.leftsk().relissed(x,y)){
                return true;
        }if (mode.rightsk()!=null && mode.rightsk().relissed(x,y)){
            return true;
        }if (mode.centrsk()!=null && mode.centrsk().relissed(x, y)){
            return true;
        } 
        if (mode.next()!=null && mode.next().relissed(x, y))
            return true;
    }
    presedButton=null;
    return false;            
}


static public void pointerDragged(final int x, final int y, final MODE mode){
	if (presedButton!=null){
		Object t=presedButton;
		screenPressed(x,y,mode);
		if (t!=presedButton)
			presedButton=null;
	}
}

}
