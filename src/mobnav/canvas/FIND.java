package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */
public class FIND {
 String text;
    int     i=0,len=0;;
    public FIND(final String str){text=str;i=0;len=text.length();}
    public boolean find(final char b){       
        if (text.charAt(i)==b){
            i++;
            if (i==len){
                i=0;
                return true;                    
            }
        }else
            i=0;        
    return false;
    }
}
