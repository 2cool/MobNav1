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

public class MenuStr{
   private static MenuStr[]mss={null,null,null,null,null,null,null,null,null,null,null,null};
   public boolean line=false;
   public String name;
 //  public int val;
   public  Menu subMen;
   public boolean visible;
   Image icon;
   //int keyCode=MyCanvas.NOKEY;

   private void Set(Image icon,String name, boolean visible, Menu subMen){
       this.name=name;
 //      this.val=val;
       this.visible=visible|(subMen!=null);
       this.subMen=subMen;
       this.icon=icon;
    }

   
   static public boolean KeyPressed(final int keyCode){
       int i=getIndex(keyCode);
       boolean ret=false;
       if (ret=mss[i]!=null)
           Interface.MenuDo(mss[i],false);       
       return ret;             
   }
   static private int getIndex(final int keyCode){
       int i=0;
       if (keyCode>=Canvas.KEY_NUM0 && keyCode<=Canvas.KEY_NUM9)
           i=keyCode-Canvas.KEY_NUM0;                  
       else if (keyCode==Canvas.KEY_STAR)
           i=10;
       else if (keyCode==Canvas.KEY_POUND)
           i=11;
       return i;
   }
   MenuStr(final int keyCode, String name, boolean visible, Menu subMen){
       int i=getIndex(keyCode);    
       
       Set(Interface.bIcon[i],name,visible,subMen);
       mss[i]=this;           
   }
   MenuStr(String name, boolean visible, Menu subMen){
       Set(null,name,visible,subMen);
   }
    MenuStr(Image icon,String name,boolean visible, Menu subMen){
        Set(icon,name,visible,subMen);
    }
   public void On(){visible=true;}
   public void Off(){visible=false;}
   public void ReName(final String neName){name=neName;}
   public void ReName(final Image img, final String neName){icon=img;name=neName;}
   public void ReSet(Image icon,String name, boolean visible,Menu subMen){
        Set(icon,name,visible,subMen);
    }

}
