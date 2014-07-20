package mobnav.canvas;
/**
 *
 * @author 2cool
 */
import javax.microedition.lcdui.*;

import java.util.Stack;


public class Menu extends MODE_DEF{
    
   private           static   boolean touchPhone=false;
   private          static   Font font=null;
   private           static   Menu workingMenu=null;
   private final    static   int border=5;
   private final    static   int cellspacing=3;
   private final    static   int iconSize=18;  //iconSize 16X16
   private          static   int fontHeight=0;
   private final    Stack st = new Stack();
   private          Menu parent=null;
   private          int x=5, y=0,selected=0,menuWidth, stringHeigth, menuHeigth,menuHeigth2draw,begPos=0,sliderLen=0;
   private          double sliderAdd=0;
   private          boolean up=false,down=false;
   private static Menu _this=null;
   static public boolean touchPhone(){return touchPhone;}
   private void setTouchPhone(){
	   if (touchPhone==false){
		   SetUpMenu();
	   }
	   
   }
   
  BUTON_SOFT_KEY rightsk=new BUTON_SOFT_KEY("Выход",null,-6,0xeeeeee);
  BUTON_SOFT_KEY leftsk=new BUTON_SOFT_KEY("Назад",null,Canvas.LEFT,0xeeeeee);
   public BUTON_SOFT_KEY leftsk(){return leftsk;}
	public BUTON_SOFT_KEY rightsk(){return rightsk;}
	public BUTON_SOFT_KEY centrsk(){return null;}
   
	public BUTON_TOUCH_SCR plus(){return null;}
	public BUTON_TOUCH_SCR minus(){return null;}
	public BUTON_TOUCH_SCR add(){return null;}
   public BUTON_TOUCH_SCR light(){return null;}
   public BUTON_TOUCH_SCR next(){return null;}
   
   public MenuStr add(MenuStr mStr){
        _this=this;
        st.push(mStr);
        return mStr;
    }
    //-------------------------------------------------------------------------
    public Menu(){
        font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
    }
    public Menu(Font font_){
        font=font_;
    }
    private void  upK(){workingMenu.upK_();}
   private void upK_(){
        if (selected>0)
            selected--;
        else
            selected=st.size()-1;
   }
  /* private void up(){workingMenu.up_();}
   private void up_(){
       if (selected>0)
            selected--;
       else
           up=true;
       if (begPos>selected)
           begPos--;
   }*/
   private  void downK(){workingMenu.downK_();}
   private void downK_(){
       if (selected<(st.size()-1))
           selected++;
       else
           selected=0;
   }
  // private void down(){workingMenu.down_();}
  // private void down_(){
  //     if (selected<(st.size()-1))
  //         selected++;
  //     else
  //         down=true;
  // }

   private void enterSubMenu(){
       MenuStr m=((MenuStr)st.elementAt(selected));
       if (m.subMen!=null){
           workingMenu=m.subMen;
           workingMenu.parent=this;
         //  workingMenu.visible=true;
           if (workingMenu.firstTime){
               workingMenu.x=x+10;
               workingMenu.y=y+selected*stringHeigth;
           }
        }
   }
   //-------------------------------------------------------------------------
   private boolean liveSubMenu(){
       if (workingMenu.parent!=null){
          // workingMenu.visible=false;
           Menu t=workingMenu;
           workingMenu=workingMenu.parent;
           t.parent=null;
           return true;
       }
       return false;
   }
   private void closeAllSubMenu(){
       Menu m=this;
       if (m.parent!=null){
            m=m.parent;
            parent=null;
            m.closeAllSubMenu();
       }else
           workingMenu=this;
   }
   //-------------------------------------------------------------------------
   private MenuStr get(){return workingMenu.get_();}
   private MenuStr get_(){
       MenuStr m=((MenuStr)st.elementAt(selected));

        if (m.visible==false)
            return null;
        if (m.subMen!=null){
            enterSubMenu();
            return null;
        }else{
            closeAllSubMenu();
            return m;
        }
   }
   //-------------------------------------------------------------------------
  // private int oldPos;

   private int getPos(final int x, final int y){	
	   if (y > this.y+this.menuHeigth2draw)
		   return -1;
       int dy=y-this.y-border;
       if (dy<0)
           return -1;
       int pos= begPos+dy/stringHeigth;
       if (pos>=st.size())
    	   pos=st.size()-1;
       if (
               x>this.x &&
               menuWidth+this.x>x &&
               pos>=0
          )
           return pos;
       else
           return -1;
   }

   //-------------------------------------------------------------------------
   public void pointerReleased(final int x, final int y){
	   if (!BUTON.screenRelissed(x,y,this)){
	    	MyCanvas.busy=false;
	        DoReleasedMenu(x,y);
   	}
   }
 //  private int pointerReleasedd(final int x, final int y){
 //      return workingMenu.pointerReleased_(x,y);
//   }
   public void pointerDragged(final int x, final int y){
	   synchronized(MyCanvas.update){MyCanvas.update.notify();}
       workingMenu.pointerDragged_(x,y);
   }
   public void pointerPressed(final int x, final int y){
	   setTouchPhone();
	   BUTON.screenPressed(x,y,this);
       workingMenu.pointerPressed_(x,y);
  }
   //------------------------------------------------------------------------- 
   private int pointerReleased_(final int x, final int y){
       int pos=getPos(x,y);
        if (draged==false && pos!=-1)// && selected==pos && oldPos==selected+begPos)
            return 1;
        return 0;
   }
   //-------------------------------------------------------------------------
   private boolean draged=false;
   private void pointerDragged_(final int x, final int y){
         if (menuHeigth==menuHeigth2draw)
             return;
         int gp=getPos(x,y);
         if (gp<0)
        	 return;
         int add=selected-gp;
         draged|=(add!=0);
        begPos+=add;
        if (up=(begPos<0))
            begPos=0;
        int max=st.size()-(MAX_Y-border)/stringHeigth;
        if (down=(begPos>max))
            begPos=max;
   }
   //-------------------------------------------------------------------------
   
   private boolean pointerPressed_(final int x, final int y){
       draged=false;
        int pos=getPos(x,y);
        boolean ret;
        if (ret=(x>this.x &&  menuWidth+this.x>x &&   pos>=0))
              selected=pos;
         return ret;
   }
   //-------------------------------------------------------------------------
   private  int MAX_Y;//=MyCanvas.MAX_Y;
   private void SetUpMenu(){
      MAX_Y=MyCanvas.max_y-BUTON_SOFT_KEY.height;
       firstTime=false;
       if (fontHeight==0)
           fontHeight=font.getHeight();
       stringHeigth=(touchPhone)?50:fontHeight+cellspacing;
        if (stringHeigth<iconSize)
            stringHeigth=iconSize;
        menuHeigth=stringHeigth*st.size()+border;
        menuHeigth2draw=menuHeigth;
   sliderLen=0;
        sliderAdd=0;
       // begPos=0;

        if (menuHeigth>MAX_Y){
            y=0;
            menuHeigth2draw=MAX_Y;
            sliderLen=MAX_Y*MAX_Y/menuHeigth;            
            sliderAdd=(double)(MAX_Y-sliderLen)/(double)(st.size()-(double)(MAX_Y/stringHeigth))-border;
        }else
            if (parent ==null){
                y=MAX_Y-menuHeigth-border;
                if (y<0) y=0;
            }else{
                if (menuHeigth+y>MAX_Y){
                    y=MAX_Y-menuHeigth-border;
                }
            }

        menuWidth=100;
        int i=0;
        while (i<st.size()){
            MenuStr m=((MenuStr)st.elementAt(i));
            menuWidth=Math.max(menuWidth, font.charsWidth(m.name.toCharArray(), 0, m.name.length()));
            i++;
        }
        menuWidth+=border*4+iconSize;
        if (x+menuWidth+10>MyCanvas.max_x)
        	menuWidth=MyCanvas.max_x-x-10;
   }
private boolean firstTime=true;
   //-------------------------------------------------------------------------
  private  void paint_(Graphics g){
	  
            // BUTON.SetSoftKeyButon(true, "Назад",null,Canvas.LEFT,0x9900);
   // BUTON.SetSoftKeyButon(false, "Отмена",null,-6,0xaaaa00);

        if (firstTime)
            SetUpMenu();

       int y=this.y;
       if (up)
           y+=5;
       if (down)
    	   y-=5;
       g.setFont(font);
       if (workingMenu==null)
           workingMenu=this;
        int white=(workingMenu==this)?0xffffff:0x999999;
        int icon=(workingMenu==this)? 0:0x888888;
        int black=(workingMenu==this)?0x0:0x444444;
        int green=(workingMenu==this)?0:0xaa00;
        int grey=0x777777;
    
       if (begPos>selected)
            begPos=selected;
        else {
            int mh=(1+selected - begPos)*stringHeigth+border;
            if (mh>MAX_Y)
                begPos=1+selected-MAX_Y/stringHeigth;
        }
        g.setColor(white);
        g.fillRect(x+iconSize+border-3, y+1, menuWidth+8-iconSize, menuHeigth2draw+border-1);
        g.setColor(icon);
        g.fillRect(x+1, y+1, iconSize+1, menuHeigth2draw+border-1);
      //  g.drawLine(x+1, y,  x+iconSize+border-3, y+menuHeigth2draw+border);
        
        
       
  
       
        int i=0;
        while (i<st.size()-begPos){
            int yPos=border+y+stringHeigth*i;
            if (yPos>MAX_Y)
                break;
            MenuStr m=((MenuStr)st.elementAt(i+begPos));
            if (i+begPos==selected){
                g.setColor(green);
                g.fillRect(x+iconSize,yPos,menuWidth-iconSize,stringHeigth-1); 
                g.setColor(m.visible?white:grey);
            }else
            	g.setColor(m.visible?black:grey);
            
            if (workingMenu==this && m.icon!=null)
                g.drawImage(m.icon, x+2,yPos+((stringHeigth-iconSize)>>1), 0);
            g.drawString(m.name,x+border+iconSize,yPos+((stringHeigth-fontHeight)>>1),0);

            if (m.subMen!=null){
                int trX=menuWidth+x-2;
                int trY=(stringHeigth>>1)+y+stringHeigth*i+(cellspacing);
                int t=10;
                g.fillTriangle(
                       trX-t, trY-(t>>1),
                       trX-t, trY+(t>>1),
                       trX, trY);
                }
            i++;
        }
        g.setColor(black);
        int h=menuHeigth2draw+border;
        if (h+y>MAX_Y)
        	h=MAX_Y-y;
        int ypos =y+border+(int)(begPos*sliderAdd);
               
        if (sliderLen!=0){
        	g.setColor(white);
        	g.fillRoundRect(x+menuWidth+1,ypos,7,sliderLen,7,7);
        	g.setColor(black); 
            g.drawRoundRect(x+menuWidth+1,ypos,7,sliderLen,7,7);
        }
        g.drawRect(x, y, menuWidth+10, h-1);

        up=down=false;
    }
  public void paint (final Graphics g){
	  MAP.drawMap(g);
	  g.setClip(0, 0, MyCanvas.max_x, MyCanvas.max_y-BUTON_SOFT_KEY.height);
      Interface.GPS_MENU_TEST();
      if (workingMenu==null){
          if (_this!=null)
              _this.paint_(g);
      }else
          rpaint(g,workingMenu);
      g.setClip(0, 0, MyCanvas.max_x, MyCanvas.max_y);
    }
  private void rpaint(final Graphics g, final Menu m){
        if (m.parent!=null)
            rpaint(g,m.parent);
        m.paint_(g);
    }
 
//########################################################################
  public boolean key(final int keyCode){
     switch (keyCode) {

  	   case Canvas.UP:
  	   case -1: 
  		   upK();
  		   break; //up	 
  		   
  	   case Canvas.LEFT:
  	   case -3:
  	   case -6:
  		   if (liveSubMenu()) 
  			   break;	
  		   
  	   case -7:
  		   MyCanvas.SetOldMode();
  		   break;
  		   
  	   case Canvas.RIGHT:
  	   case -4:
  	   case -5:
  	   case Canvas.FIRE:
  	        MenuStr ret=get();
  	        if (ret != null)
  	        	Interface.MenuDo(ret,true);
  	        break;
  	        
  	   case Canvas.DOWN:
  	   case -2: 
  		   downK();
  		   break;  //down
      }
     return true;
  }
  
  //---------------------------------------------------
  public void keyPressed(final int keyCode){
	    if (key(keyCode))
	        synchronized(MyCanvas.update){MyCanvas.update.notify();}
 }
	public void keyReleased(final int keyCode){}
	public void keyRepeated(final int keyCode){
		keyPressed(keyCode);
	}
	public static void tochPhoneMenu(){
	    if (touchPhone==false){
	        touchPhone=true;
	    }
	//    Menu.show();
	}
	private void DoReleasedMenu(final int x, final int y){
	    int ret=workingMenu.pointerReleased_(x,y);
	    switch (ret){
	        case -1:MyCanvas.SetOldMode();break;
	        case  1:MenuStr i;if ((i=get())!=null)Interface.MenuDo(i,true);break;
	        default:;
	    }
	  }
  
  
  
}
