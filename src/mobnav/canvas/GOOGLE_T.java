package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */


import java.io.*;

import javax.microedition.io.*;
import javax.microedition.io.file.FileConnection;

import mobnav.tracks.Track;
import mobnav.tracks.TrackNode;

import java.util.Stack;
//import javax.microedition.io.file.FileConnection;






public class GOOGLE_T {
  //  static final String s_break =  "&lt;b&gt;";
 //   static final String s_breakEnd="&lt;/b&gt;";
   // &lt;div style="font-size:0.9em"&gt;
    
   

	static final int STATUS=0,POLYLINE=1,DISTANCE=3,HTML_INSTRUCTIONS=5,SUMMARY=7;
    static final FIND []tag={
    		new FIND("<status>"),
    		new FIND("<polyline>"),
    		new FIND("</p"),
    		new FIND("<distance>"),
    		new FIND("</v"),
    		new FIND("<html_instructions>"),
    		new FIND("</h"),
    		new FIND("<summary>"),
    		new FIND("</s")};
static final byte NO=0, WITHOUTSPECES=1, ALL=2;
   // static Stack locs=new Stack();



static public String tname=null;
    static public String get(
            Location a, 
            Location b,
            String mode,            
            Stack waypoints, 
            boolean optimize, 
            int restrictions
            ){

       // int tlen=0;         
        String url = "http://maps.googleapis.com/maps/api/directions/xml?";
        url+= "origin="+ a.getLat() + "," + a.getLon();
        url+= "&destination="+ b.getLat() + "," + b.getLon();
        if (waypoints!=null){
            url+="&waypoints=";
            if (optimize)
                url+="optimize%3Atrue";
            for (int i=0; i<waypoints.size(); i++){
                Location l=Location.GetLocation((Point)waypoints.elementAt(i));
                url+= "%7C"+l.getLat() + "," + l.getLon();
            }        
        }
        if (! mode.equals("driving"))url+= "&mode="+mode;
        if (restrictions>0)
            url+="&avoid="+((restrictions==1)?"tolls":"highways");
        
        url+= "&sensor=true";
    //System.out.println(url);
        
        
     /*   
        url="http://bash.org.ru/";
        try {
            Thread.currentThread().sleep(10000);
            if (true)return 0;
        } catch (InterruptedException ex) {}
        
       */ 
        
        
        
    InputStream is = null;
    HttpConnection c = null;
    FileConnection fcc=null;
    OutputStream os=null;
    String fn=null;
    tname=null;
    try{
        try{
        	fn=Storage.getAutoSaveDir(FILE.PLT_E)+"googleT_"+Storage.date2Name(true)+FILE.PLT_E;
        	fcc=(FileConnection) Connector.open(fn);
    	    try{
    	        fcc.create();
    	    }catch(Exception ex){System.out.println("error "+fn+" "+ex.toString());}
    	    os=fcc.openOutputStream();    
    	    OZI_PLT.WriteOZIHeader(os);  
    	    
    	                         
                c = (HttpConnection)Connector.open(url);
                c.setRequestProperty( "User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)" );   
              //  c.setRequestProperty( "User-Agent","Profile/MIDP-1.0 Configuration/CLDC-1.0" ); 
                 c.setRequestProperty("Accept-Language",Settings.accept_language);
                is = c.openInputStream();                        
           // int len =(int)c.getLength() ;
            final int BUFS=1000;
            byte []buf=new byte[BUFS];
            int bufi=0;
            int ch=0;
            char bt=0;
           boolean status=false;
           byte read=NO;
          // int lastDist=0;
          
           Stack locs=new Stack(); 
           String txt =null;
           
           
            do{
                if (Loading.CANCELED())
                    return null;
                ch=is.read();                
                if (ch!=0xa){
                    bt=(char)ch;
                    if (status==false && tag[STATUS].find(bt)){
                        status=true;
                        buf[0]=(byte)is.read();
                        buf[1]=(byte)is.read();
                        if (buf[0]!='O' || buf[1]!='K'){
                            int i=2;
                            while((buf[i]=(byte)is.read())!='<')
                                i++;
                            String error=new String(buf,0,i,"UTF-8");
                            MyCanvas.SetErrorText(error);
                        }                                           
                    }else if (tag[POLYLINE].find(bt)){
                        read=WITHOUTSPECES;                      
                    }else if (tag[POLYLINE+1].find(bt) && bufi>0){
                        read=NO;
                        String pointS=new String(buf,0,bufi-1,"UTF-8");
                        pointS=pointS.substring(9,pointS.length()-1);
                        locs.removeAllElements();
                        DecodePoly(pointS,locs);                       
                        bufi=0;
                    }
                   /* else if (tag[DISTANCE].find(bt)){
                        read=WITHOUTSPECES;
                     }else if (tag[DISTANCE+1].find(bt) && bufi>0){
                        read=NO;                        
                        String distS=new String(buf,0,bufi-1,"UTF-8"); 
                        distS=distS.substring(8,distS.length()-1);
                        lastDist=Integer.parseInt(distS);
                        tlen+=lastDist;
                        bufi=0;
                    }  */        
                     else if (tag[HTML_INSTRUCTIONS].find(bt)){
                         read=ALL;
                     }else if (tag[HTML_INSTRUCTIONS+1].find(bt) && bufi>0){
                         read=NO;
                        txt=new String(buf,0,bufi-1,"UTF-8"); 
                        txt=txt.substring(1, txt.length()-1);
                        txt=TEXT.RemoveHTMLTAG(txt);                      
                      //  tlen+=lastDist;
                        bufi=0;                        
                     }else if (tag[SUMMARY].find(bt)){
                         read=ALL;                        
                     }else if (tag[SUMMARY+1].find(bt) && bufi>0){
                         read=NO;
                         tname=new String(buf,0,bufi-1,"UTF-8");
                         tname=tname.substring(1, tname.length()-1);
                         String []na=TEXT.split(tname, '/');
                         
                         if (na.length>=4){
                             tname=na[0]+"--"+na[na.length-2];
                         }                         
                         bufi=0;
                     }
                    //---------------
                    //---------------
                     if (txt!=null && locs.size()>0){
                         int locsI=0;               

                         while(locsI<(locs.size())-1){                             
                            // tc.Add(p0, (Point)locs.elementAt(locsI), (short)777, txt);   
                             TrackNode tn=new TrackNode((Point)locs.elementAt(locsI), Track.altitude_not_valid, txt);
                             OZI_PLT.writeOZIPoint(os, tn);
                             locsI++;            
                         }
                         txt=null;
                     }                    
                    
                     
                    if (read!=NO && bufi<BUFS)
                        if (bt!=' ' || read==ALL)
                            buf[bufi++]=(byte)bt;                
                }                                                
                MobNav1.inetIn++;
                
            }while (ch!=-1) ;
            
            if (locs.size()>0){
            	 TrackNode tn=new TrackNode((Point)locs.elementAt(locs.size()-1), Track.altitude_not_valid, txt);
                 OZI_PLT.writeOZIPoint(os, tn);                
            }  
            os.flush();
            os.close();os=null;
           
           // tlen=lastDist;//последния дистанция походу длина всего трека
            //tlen>>=1;                    
        }finally{
               
               if (is!=null)
                   is.close();
               if (is!=null)
                   c.close();
               if (os!=null)
            	   os.close();
               if (fcc!=null)
            	   fcc.close();                                                               
        }
   } catch (Exception ex) {
	   MyCanvas.SetErrorText(ex.toString());
	   fn=null;
   }catch (OutOfMemoryError ex){
	   MyCanvas.SetErrorText(ex.toString());
	   fn=null;
    } 
    return fn;
}
    
 //--------------------------------------------------------------------------------
    
    static int i=0;
    static private void DecodePoly(String encoded, Stack locs){        
       // encoded="a~l~Fjk~uOnzh@vlbBtc~@tsE`vnApw{A`dw@~w\\|tNtqf@l{Yd_Fblh@rxo@b}@xxSfytAblk@xxaBeJxlcBb~t@zbh@jc|Bx}C`rv@rw|@rlhA~dVzeo@vrSnc}Axf]fjz@xfFbw~@dz{A~d{A|zOxbrBbdUvpo@`cFp~xBc`Hk@nurDznmFfwMbwz@bbl@lq~@loPpxq@bw_@v|{CbtY~jGqeMb{iF|n\\~mbDzeVh_Wr|Efc\\x`Ij{kE}mAb~uF{cNd}xBjp]fulBiwJpgg@|kHntyArpb@bijCk_Kv~eGyqTj_|@`uV`k|DcsNdwxAott@r}q@_gc@nu`CnvHx`k@dse@j|p@zpiAp|gEicy@`omFvaErfo@igQxnlApqGze~AsyRzrjAb__@ftyB}pIlo_BflmA~yQftNboWzoAlzp@mz`@|}_@fda@jakEitAn{fB_a]lexClshBtmqAdmY_hLxiZd~XtaBndgC";
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len-1){
            int b, shift = 0, result = 0;
            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (index<len && b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            if (index<len){
                do{
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (index<len && b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;
                Point p=Location.GetPoint(new Location(lat / 1e5, lng / 1e5));
                
                i++;   
                //System.out.println(p.toString());
                locs.addElement(p);
            }
            
        }
         
    }
 //-------------------------------------------------------------------------------- 
  
//----------------------------------------------------------------------
  
    
    
    
    
    

}
