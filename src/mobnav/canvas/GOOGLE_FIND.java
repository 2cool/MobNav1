package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */

import java.util.Stack;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;

import mobnav.gps.GGA;
import mobnav.gps.NMEA;

public class GOOGLE_FIND {        
    static private Stack st=null;
    
    
    static private void List(final String str, final Displayable retd){
        
        final List l= new List(str, List.IMPLICIT);
        for (int i=0; i<st.size(); i+=2)
            l.append((String)st.elementAt(i), null);
        l.addCommand(Interface.exit);
      //  l.addCommand(MyI.ok);
        l.setSelectCommand(Interface.ok);
        l.setCommandListener(new CommandListener() {
        public void commandAction(Command c, Displayable s) {
            if (c==Interface.ok){
                if (st.size()==1)
                    MobNav1.display.setCurrent(retd);
                else{
                   int i=l.getSelectedIndex();
                   list(l,(String)st.elementAt(i<<1),((Location)(st.elementAt((i<<1)+1))).GetPoint());
                   //MyCanvas.SetGP(((Location)(st.elementAt((i<<1)+1))).GetPoint());
                   //MobNav1.display.setCurrent(MobNav1.gCanvas);
                }
            }else{
                MobNav1.display.setCurrent(retd);
            }
            
  
        }
    });
        
        
       MobNav1.display.setCurrent(l);                                
    }
    
    static private void list(final Displayable retd,final String str, final Point p){
    	System.out.println("LIST "+str);
        final Form f = new Form("");
        f.append(str);
        if (GGA.fixQuality>0){
        	f.addCommand(Interface.trackTo);
        	MODE_A_B.A=NMEA.l.GetPoint();
        }
        f.addCommand(Interface.ok);
        f.addCommand(Interface.exit);
        f.setCommandListener(new CommandListener() {
        public void commandAction(Command c, Displayable s) {
            if (c==Interface.ok){   
                if (p==null)
                    MobNav1.display.setCurrent(retd); 
                else{
                	Labels.foundP=new Point(p);
                	Labels.foundName=str;
                    NAVI.SetGP(p);
                    MobNav1.display.setCurrent(MobNav1.gCanvas);                
                }
            }else if (c==Interface.trackTo){
            	MODE_A_B.B=new Point(p);
            	MODE_A_B.get_AB_Track();
         
            	
            }else
                MobNav1.display.setCurrent(retd);            
        }
    });
    MobNav1.display.setCurrent(f);
        
    }


	
    
    

    
  
    
   
    static public void Get(final String str,final Displayable retd){
 
        final FIND []tag=new FIND[5];
        tag[0]=new FIND("<address>");
        tag[1]=new FIND("</a");
        tag[2]=new FIND("<coordinates>");
        tag[3]=new FIND("</c");
        tag[4]=new FIND("<code>");

        Location l=new Location(NAVI.getGP());
        String url="http://maps.google.com/maps/geo?q="+TEXT.urlEncode(str)+"&output=xml&oe=utf8&sensor=false";
        url+="&gl=ua";
        url+="&ll="+l.getLat()+","+l.getLon()+"&spn=0.247048,0.294914";
       // url="http://maps.google.com/maps/geo?q=Krivoi+rog,+Ukrainskaya+2&output=xml&oe=utf8&sensor=true";
        System.out.println(url);
        InputStream is = null;
        HttpConnection c = null;
        boolean read=false;
        st=new Stack();
        System.out.println("google find");
        try{
            try{ 
                final int BUFS=1000;
                byte []buf=new byte[BUFS];    
                
                c = (HttpConnection)Connector.open(url);
                //c.setRequestProperty( "User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; ru; rv:1.9.2.12) Gecko/20101026 MRA 5.7 (build 03686) Firefox/3.6.12 sputnik 2.3.0.76");
                //c.setRequestProperty( "User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)" );   
                c.setRequestProperty( "User-Agent","Profile/MIDP-1.0 Configuration/CLDC-1.0" ); 
                c.setRequestProperty("Accept-Language",Settings.accept_language);
                                                 
                is = c.openInputStream();
                int bufI=0,ch; 
                char bt;
                
                do{
                   // if (MyCanvas.LOADING_CANCELED()){
                   //     st.removeAllElements();                        
                    //    return ;
                   // }
                    ch=is.read();
                    if (ch!=0xa && ch!=0x20){
                        bt=(char)ch;
                       if (tag[0].find(bt)){
                            read=true;
                            bufI=0;
                        }else if (read && tag[1].find(bt)) {
                            String address=new String(buf,1,bufI-3,"UTF-8");
                            address=TEXT.replace(address, "&apos;", "'");
                            System.out.println(new String(buf));
                            st.addElement(address);
                            read=false;                                  
                        }else if (tag[2].find(bt)) {
                            read=true;
                            bufI=0;
                        }else if (read && tag[3].find(bt)){
                            String coordinates=new String(buf,1,bufI-3,"UTF-8");
                            String []locs=TEXT.split(coordinates, ',');
                            l=new Location(Double.parseDouble(locs[1]),Double.parseDouble(locs[0]));
                            st.addElement(l);
                            read=true;
                            bufI=0;
                        }else if (tag[4].find(bt)){
                            buf[0]=(byte)is.read();
                            buf[1]=(byte)is.read();
                            buf[2]=(byte)is.read();
                            int code=Integer.parseInt(new String(buf,0,3));
                            if (code!=200){
                               // MobNav1.display.setCurrent(retd);
                                String error="";
                                switch (code){
                                    case 500: error="SERVER ERROR";break;
                                    case 601: error="MISSING QUERY";break;
                                    case 602: error="UNKNOWN ADDRESS";break;
                                    case 603: error="UNAVAILABLE ADDRESS";break;
                                    case 610: error="BAD KEY";break;
                                    case 620: error="TOO MANY QUERIES";break;
                                }
                                st.addElement(error);
                               //MyCanvas.SetErrorText(error);
                                List(str,retd);
                                return;                                
                            }
                        }
                       // if (read==true && bufI<BUFS)
                       //     buf[bufI++]=(byte)bt;                             
                    }
                    if (read==true && bufI<BUFS){
                        buf[bufI++]=(byte)ch;  
                      //System.out.println(ch);
                    }
                        
                }while(ch!=-1) ;                                                                                
            }finally{
               if (is!=null)
                   is.close();
               if (is!=null)
                   c.close();
        }
      } catch (Exception ex) {System.out.println(ex.toString());}//MyCanvas.SetErrorText(ex.toString());}
     // System.out.println("LIST");
        List(str,retd);
    }    
         
        
}
