package mobnav.canvas;
import java.io.InputStream;
import java.util.Stack;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;

import mobnav.gps.GGA;
import mobnav.gps.NMEA;

// http://www.openstreetmap.org/geocoder/search_osm_nominatim?maxlat=44.48511276126&maxlon=34.135098728715&minlat=44.48511276126&minlon=34.135098728715&query=Lenina%2C+krivoy+rog
// http://www.openstreetmap.org/geocoder/search_geonames?maxlat=44.48511276126&maxlon=34.135098728715&minlat=44.48511276126&minlon=34.135098728715&query=Lenina%2C+krivoy+rog
public class OPENSTREET_FIND {
	
	 static private Stack st=null;
	    
	    
	    static private void List(final String str, final Displayable retd){
	        
	        final List l= new List(str, List.IMPLICIT);
	        for (int i=1; i<st.size(); i+=2)
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
	                   list(l,(String)st.elementAt((i<<1)+1),((Location)(st.elementAt((i<<1)))).GetPoint());
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
	 
	        final FIND []tag=new FIND[7];
	        tag[0]=new FIND("returnfalse;\">");
	        tag[1]=new FIND("</a");
	        tag[2]=new FIND("setPosition(");
	        tag[3]=new FIND(",null");
	        tag[4]=new FIND("search_more");
	        tag[5]=new FIND("search_results_entry\">");
	        tag[6]=new FIND("<a");

	        Location l=new Location(NAVI.getGP());
	     // //http://www.openstreetmap.org/geocoder/search_osm_nominatim?maxlat=44.48511276126&maxlon=34.135098728715&minlat=44.48511276126&minlon=34.135098728715&query=Lenina%2C+krivoy+rog
	        String url="http://www.openstreetmap.org/geocoder/search_osm_nominatim?maxlat="+l.getLat()+"&maxlon="+l.getLon()+"&minlat="+l.getLat()+"&minlon="+l.getLon()+"&query="+TEXT.urlEncode(str);


	        System.out.println(url);
	        InputStream is = null;
	        HttpConnection c = null;
	        boolean read=false;
	        st=new Stack();
	        String addressApendix="";
	        System.out.println("openstreeet find");
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
	                  //  if (MyCanvas.LOADING_CANCELED()){
	                  //      st.removeAllElements();                        
	                  //      return ;
	                  //  }
	                    ch=is.read();
	                    if (ch!=0xa && ch!=0x20){
	                        bt=(char)ch;
	                        
	                        if (tag[5].find(bt)){
	                            read=true;
	                            bufI=0;
	                        }else if (read && tag[6].find(bt)) {
	                        	addressApendix=new String(buf,1,bufI-2,"UTF-8");
	                        	addressApendix=TEXT.replace(addressApendix, "&apos;", "'");	                            
	                            read=false;    
	                           // System.out.println("@@@@@Address@@@@="+address);
	                        }else if (tag[0].find(bt)){
	                            read=true;
	                            bufI=0;
	                        }else if (read && tag[1].find(bt)) {
	                        	
	                            String address=new String(buf,1,bufI-3,"UTF-8");
	                            address=TEXT.replace(address, "&apos;", "'");
	                            //System.out.println(new String(buf));	                            
	                            st.addElement(addressApendix+" "+address);
	                            read=false;    
	                           // System.out.println("@@@@@Address@@@@="+address);
	                        }else if (tag[2].find(bt)) {
	                            read=true;
	                            bufI=0;
	                        }else if (read && tag[3].find(bt)){
	                            String coordinates=new String(buf,1,bufI-5,"UTF-8");
	                            String []locs=TEXT.split(coordinates, ',');
	                            l=new Location(Double.parseDouble(locs[0]),Double.parseDouble(locs[1]));
	                            st.addElement(l);
	                            read=true;
	                            bufI=0;
	                            //System.out.println("@@@@Coordinates@@@@="+coordinates);
	                        }else if (tag[4].find(bt)){
	                        	System.out.println("SEARCH MORE");
	                        	break;
	                        }
	                     //   if (read==true && bufI<BUFS)
	                       //     buf[bufI++]=(byte)bt;                             
	                    }
	                    if (read==true && bufI<BUFS)
                            buf[bufI++]=(byte)ch;  
	                        
	                }while(ch!=-1) ;                                                                                
	            }finally{
	               if (is!=null)
	                   is.close();
	               if (is!=null)
	                   c.close();
	        }
	      } catch (Exception ex) {System.out.println(ex.toString());}//MyCanvas.SetErrorText(ex.toString());}
	      
	        List(str,retd);
	    }    
}
