package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */
import java.util.Calendar;
import java.util.Date;
import javax.microedition.lcdui.*;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;

import mobnav.gps.BTGPS;
import mobnav.gps.GGA;
import mobnav.gps.GSA;
import mobnav.gps.NMEA;
import mobnav.gps.Time;
import mobnav.math.math;
import mobnav.tracks.TrackRecording;
import mobnav.tracks.Tracks;

import java.util.Stack;

public class TEXT {   
    
    boolean loc=false;
    static int []lLen={0,0,0,0};
    static int []rLen={0,0,0,0}; 
    static final int space=10;  
    static public void beep(){
    	if (Settings.DO_BEEP){
		    try {
		        Manager.playTone(83, 3000, 100);
		    } catch (MediaException ex) {}
    	}
    }
   
    static public void INFO(final Location l, Graphics g){

        String str; 
        int x=8,y=0;
        Font font=NAVI.font;
        if (font==null)
             font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        
        g.setFont(font);
        g.setColor(0);          
      //  if (BTGPS.Running())
        {
           
            g.drawString(l.longtitude(), x, y,0);
            y+=font.getHeight();
            
            g.drawString(l.latitude(), x, y,0);  
            y+=font.getHeight();
            
            str=Integer.toString((int)(BTGPS.speed4A()*3.6))+Interface.s_kmh;            
            g.drawString(str,x, y,0);        
            y+=font.getHeight();    
            
           
        }
        
        if (BTGPS.isOn()){                  
            str= NMEA.time.get(':',true);
        }else{
            Date date=new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            str=ndigits("0",calendar.get(Calendar.HOUR_OF_DAY));
            str+=":"+ndigits("0",calendar.get(Calendar.MINUTE));
        }        
        g.drawString("Время: "+str, x, y,0);
        y+=font.getHeight();
        //str="Прошло: "+BTGPS.getTimePast();        
        g.drawString(str, x, y,0);
        y+=font.getHeight();
        
        
        g.drawString("Пройденно: "+Distance((int)BTGPS.getDistance()), x,y,0);
        y+=font.getHeight();
                                        
        final double h=BTGPS.GetAproxHeight();
        str=Double.toString(h);
        int c=Integer.toString((int)h).length();
        str=str.substring(0, c+2)+"m";
        g.drawString("Высота: "+str, x, y, 0);                            
        y+=font.getHeight(); 
        
        g.drawString("Поднялись на: "+Distance(BTGPS.GetElevation()), x,y,0);
        y+=font.getHeight();
        
        g.drawString("Угол Подьема: "+elevation(), x,y,0);
        y+=font.getHeight();

                
         
        
    }
    
    
    
static private String elevation(){
    String elAngleS="?";
   // if (true)return;
    //final double h=BTGPS.GetAproxHeight();
    if (GGA.fixQuality==1){  
       // System.out.println(BTGPS.getdHaightA()+" "+BTGPS.GetDDistanceA());
            double elAngle=Math.toDegrees(math.getAngle(BTGPS.getdHaightA(),BTGPS.GetDDistanceA())); 
            if (elAngle!=0){
                if (elAngle>180)
                    elAngle-=360;
                elAngleS=(elAngle<=0)?"":"+";
                elAngleS+=Integer.toString((int)elAngle)+"°";
                              
               //TEXT.drawWBString(g, font,elAngleS, 0 , 70); 
                }
    }
    return elAngleS;
    //System.out.println(BTGPS.GetAproxHeight()-GGA.height);
}
    
//-------------------------------------------------------------------------------
    static DRAW_TEXT dt=null;
    static public void textUP(final Location l, Graphics g, boolean all){
    	if (dt==null)
			dt=new DRAW_TEXT(g,MyCanvas.max_x);
		dt.reset();
		String str="";
		if (BTGPS.isOn()){			//SATELITES
			//SATELITES
			str=Integer.toString(GSA.used);	          
		}
		if (TrackRecording.records)
		    str+=" R";
		dt.drawLeft(str);
		dt.drawRight(NMEA.time.get(':',true));
		//HEIGHT
		str="";
		if (GGA.fixQuality!=0){
		    final double h=BTGPS.GetHeight();//.GetAproxHeight();
		    str=Double.toString(h);
		    int c=Integer.toString((int)h).length();
		    str=str.substring(0, c+2)+"m";
		    dt.drawLeft(str);   		  
		}
		str="";
		if (GGA.fixQuality!=0 && MyCanvas.GetMode()!=MyCanvas.manual_track_make){//SPEED
		    int speedA4=(int)(BTGPS.speed4A()*3.6);
		    if (speedA4>=0)
		    	str=Integer.toString(speedA4)+Interface.s_kmh;		        
		}   
		dt.drawRight(str);
//TRECK LENGTH
      if (MyCanvas.GetMode()==MyCanvas.manual_track_make)                     
    	  dt.drawLeft(Distance( (int)MAKE_TRACK.len ) );
      
      if (Labels.minname.length()>0)
    	  dt.drawLeft(Labels.minname);
      
      if (Labels.selectedStr.length()>0)
    	  dt.drawLeft(Labels.selectedStr);
      
     if (MyCanvas.fastResponse==false){
    	 if (Tracks.path!=null && Tracks.dont_show_path_info==false){    	        	      
	    	  if (Tracks.path.dist2end!=-1){ 
			      dt.drawLeft(Interface.s_toEnd+Distance(Tracks.path.dist2end));
			      dt.drawRight(Time.toString(Tracks.path.predTime,false));
	    	  }
		      if (Tracks.path.hint!=null){
			      Stack st=TEXT.Perenos(Tracks.path.hint,NAVI.font,MyCanvas.max_x);	    
			      for (int i=0; i<st.size(); i++)
			         dt.drawLeft((String)st.elementAt(i));                                
		      }
	      }           
	      if (Tracks.runner!=null && Tracks.runner.dist2end!=-1 && Tracks.path.dist2end!=-1){
	    	 str=TEXT.Distance(Tracks.path.dist2end-Tracks.runner.dist2end);
	    	 dt.drawLeft(Interface.s_toLeader +str);
	    	 dt.drawRight(Time.toString(Tracks.runner.predTime,false));
	      }
	      
	      if (Circuit.iTime>0){
	    	  if (Circuit.iBestLapTime>0)
	    		  dt.drawLeft("best time: "+Time.toString(Circuit.iBestLapTime,true));
	    	  dt.drawLeft(Time.toString(Circuit.iTime,true));
	    	  dt.drawRight(Integer.toString(Circuit.lapsDone));
	    	  
	      }
     }
      
      
    }
  /*  
    static public int textUP1(final Location l, Graphics g,boolean all){
    	//textUP1(l,g,all); 	if (true)return 200;
    	//all=true;
        int y=NAVI.fontH<<1;
        lLen[0]=lLen[1]=lLen[2]=lLen[3]=0;
        rLen[0]=rLen[1]=rLen[2]=rLen[3]=MyCanvas.max_x;

        
//LATITUDE
        int ls;
        String str;
        g.setColor(0xffffff);     
        if (all){   
            str=l.latitude();
            lLen[0]+=NAVI.font.stringWidth(str);
            drawWBString(g,NAVI.font,str, 1, 0);
            
//LONGTITUDE
            str=l.longtitude();
            lLen[1]+=NAVI.font.stringWidth(str);
            drawWBString(g,NAVI.font,str, 1, NAVI.fontH);
        }
        
// TIME
        if (MyCanvas.fastResponse==false){

			str= NMEA.time.get(':',true);
			ls=NAVI.font.stringWidth(str);
			if (str.length()>0 && lLen[0]+ls<MyCanvas.max_x){                      
			    drawWBString(g,NAVI.font,str, MyCanvas.max_x-ls, 0);
			    rLen[0]-=ls;
			}
			
			
			//SPEED
			if (GGA.fixQuality!=0 && MyCanvas.GetMode()!=MyCanvas.manual_track_make){
			    int speedA4=(int)(BTGPS.speed4A()*3.6);
			    if (true && speedA4>0){
			        str=Integer.toString(speedA4)+Interface.s_kmh;
			        ls=NAVI.font.stringWidth(str);
			        if (lLen[1]+ls<MyCanvas.max_x){                     
			            drawWBString(g,NAVI.font,str,MyCanvas.max_x-ls, NAVI.fontH);
			            rLen[1]-=ls;                      
			        }
			    }
			//HEIGHT
			if (GGA.fixQuality!=0){
			    final double h=BTGPS.GetHeight();//.GetAproxHeight();
			    str=Double.toString(h);
			    int c=Integer.toString((int)h).length();
			    str=str.substring(0, c+2)+"m";
			        ls=NAVI.font.stringWidth(str);
			        if (ls+lLen[1]+space<rLen[1]){                     
			            drawWBString(g,NAVI.font,str, (lLen[1]+=space),NAVI.fontH);
			            lLen[1]=+ls;
			        }
			    }
			}
			//////////////        
			if (BTGPS.isOn()){
				//SATELITES
				str=Integer.toString(GSA.used);
				ls=NAVI.font.stringWidth(str);
				if (ls+lLen[0]+space<rLen[0]){                 
				    drawWBString(g,NAVI.font,str, (lLen[0]+=space), 0);
				    lLen[0]+=ls;         
				}
				
				// TRACK RECORDS SYMBOL
				if (TrackRecording.records){
				    str="R";
					ls=NAVI.font.stringWidth(str);
					g.setColor((GGA.fixQuality==0)?0x0:0xff0000);
					if (ls+lLen[0]+space<rLen[0]){                       
					    g.drawString("R",(lLen[0]+=space), 0, 0);
					                lLen[0]+=ls;
		            }
					g.setColor(0xffffff);
				}	
			}
        }
        
//TRECK LENGTH
          if (MyCanvas.GetMode()==MyCanvas.manual_track_make){
               
                
                str=Distance((int)MAKE_TRACK.len);
                ls=NAVI.font.stringWidth(str);
                if (lLen[1]+ls<MyCanvas.max_x){
                    drawWBString(g,NAVI.font,str,MyCanvas.max_x-ls, NAVI.fontH);
                    rLen[1]-=ls;                      
                }
          }
          
          
        
        
        
        
        return y;
    }
    */
    /*
    static public String[]___split(final String str, char ch){    
        Stack st=new Stack();
        if (str.length()==0)if (st.size()==0){
            String[]s=new String[1];
            s[0]="";
            return s;
        }
        int beg=-1,end=-1;
        for (int i=0; i<str.length(); i++){
            if (str.charAt(i) == ch){
                if (beg>=0 && beg<=end){
                    st.addElement(str.substring(beg,end+1));
                    //System.out.println(str.substring(beg,end+1));
                    beg=-1;
                }
            }else 
                if (beg==-1)
                    beg=end=i;
                else
                    end=i;
        }
        if (beg>=0){
            st.addElement(str.substring(beg,end+1));
            //System.out.println(str.substring(beg,end+1));
        }
        
        if (st.size()==0){
            String[]s=new String[1];
            s[0]="";
            return s;
        }else{
           String []s=new String[st.size()];
           int i=st.size();
           while (st.size()>0)
               s[--i]=(String)st.pop();         
            return s;
        }
    }    
    */
    
    
static public String[]split(final String str, char ch){        
    if (str.length()==0){    	
        String[]s={""};
        return s;
    }
    int beg=0,end;    
    Stack st=new Stack(); 
    do{
    	end=str.indexOf(ch,beg);
    	if (end==-1){
    		st.addElement(str.substring(beg));
    		break;
    	}else{
		   // if (beg<end)
		    	st.addElement(str.substring(beg,end));
		    beg=end+1;
    	}
    }while (beg<str.length());
    String []s=new String[st.size()];    
    int i=0,size=st.size();
    while (i<size){
        s[i]=(String)st.elementAt(i);
        i++;
    }
     return s;
}


static public Stack split(final String str, final String ptn){

	    int beg=0,end;    
	    Stack st=new Stack(); 
	    do{
	    	end=str.substring(beg).indexOf(ptn);
	    	if (end==-1){
	    		st.addElement(str.substring(beg));
	    		break;
	    	}else{
			    end+=beg;
			    st.addElement(str.substring(beg,end));
			    beg=end+ptn.length();
	    	}
	    }while (beg<str.length());
	    return st;
}
/*
static public Stack __split(final String str, final String ptn){
	Stack st=new Stack();
	int beg=0;
	int ptnl=ptn.length();
	int strl=str.length();
	int i=0;
	while (beg<strl-ptnl){
		for (i=beg; i<=strl-ptnl; i++)
			if (str.substring(i,i+ptnl).equals(ptn)){
				if (i>0)
					st.push(str.substring(beg,i));
				beg=i+ptnl;
				break;			
			}
		if (i>strl-ptnl)
			break;
	}
	if (beg<strl)
		st.push(str.substring(beg,strl));
	return st;
}
	*/
static public String[] SplitStr(final String str, final String ptn){	
	Stack st=split(str,ptn);
	if (st.size()==0)
		return null;
	String[] b=new String[st.size()];
	for (int i=0; i<st.size(); i++)
		b[i]=(String)st.elementAt(i);	
	return b;	
}
/*    
static private String[]Splite(final String str, char ch){
   System.out.println("Spli0 "+str);
   Stack st=new Stack();
   int i=str.length(),p=0,pold=0;
   
   while (p<i){
       if (str.charAt(p)==ch){
           if (p-1>pold)
               st.push(str.substring(pold, p));
           pold=p+1;
       }
       p++;
   }
   if (p-1>pold)
         st.push(str.substring(pold, p));
   i=st.size();
   String[]s;
   if (i==0){
       s=new String[1];
       s[0]=str;       
   }else{
       s=new String[i];
       while (st.size()>0){
           s[--i]=(String)st.pop();
         //  System.out.println(s[i]);
       }
   }
   System.out.println("Spli1 "+s[s.length-1]);
   return s;          
}
*/
static public String Distance(int metters){
        double m=metters;
        String str="";
        int t6=0;
        if (m>=1000000){
            t6=(int)(m*0.000001);
            str+=t6+".";
            m-=t6*1000000;
        }
        int t3=0;
        if (m>=1000){
            t3=(int)(m*0.001);
            if (t6==0)
                str+=t3;
            else
                str+=ndigits("00",t3);
            str+=".";
            m-=t3*1000;
        }
        if (t3==0)
            str+=(int)m;
        else
            str+=ndigits("00",(int)m);
        return str+Interface.s_m;
    }
// ########################################################################
static public String ndigits(final String zeros,final int i){
	String si=Integer.toString(i);
	return (zeros+si).substring(si.length()-1);	
}

// ########################################################################


public static String replace(String str, String oldS, String newS){    
    for (int i=0; i<=str.length()-oldS.length(); i++)
        if (str.regionMatches(false, i, oldS, 0, oldS.length())){
            String o=str.substring(0,i);
            o+=newS;
            o+=str.substring(i+oldS.length(),str.length());
            str=o;
            i+=newS.length();
        }
    
    return str;
}

 static public String RemoveHTMLTAG(String str){
        int i=0;
        String lt="&lt;";
        while (i<=str.length()-8){
            if (str.regionMatches(false, i, lt, 0, 4)){
                String gt="&gt;";
                int j=i+4;
                while (j<=str.length()-4){
                    if (str.regionMatches(false, j, gt, 0, 4)){
                            String out=str.substring(0,i)+" "+str.substring(j+4,str.length());                           
                            str=out;
                            break;
                    }
                    j++;
                }                
            }else
                i++;
        }
    return str;
}
// ##############################################################################
    public static String urlEncode(String s) {
    StringBuffer sbuf = new StringBuffer();
    int len = s.length();
    for (int i = 0; i < len; i++) {
        int ch = s.charAt(i);
        if ('A' <= ch && ch <= 'Z') { // 'A'..'Z'
            sbuf.append((char)ch);
        } else if ('a' <= ch && ch <= 'z') { // 'a'..'z'
            sbuf.append((char)ch);
        } else if ('0' <= ch && ch <= '9') { // '0'..'9'
            sbuf.append((char)ch);
        } else if (ch == ' ') { // space
            sbuf.append('+');
        } else if (ch == '-' || ch == '_'   //these characters don't need encoding
                || ch == '.' || ch == '*') {
            sbuf.append((char)ch);
        } else if (ch <= 0x007f) { // other ASCII
            sbuf.append(hex(ch));
        } else if (ch <= 0x07FF) { // non-ASCII <= 0x7FF
            sbuf.append(hex(0xc0 | (ch >> 6)));
            sbuf.append(hex(0x80 | (ch & 0x3F)));
        } else { // 0x7FF < ch <= 0xFFFF
            sbuf.append(hex(0xe0 | (ch >> 12)));
            sbuf.append(hex(0x80 | ((ch >> 6) & 0x3F)));
            sbuf.append(hex(0x80 | (ch & 0x3F)));
        }
    }
    return sbuf.toString();
}
static private int si=0; 
static private int ei=0;
static String getFirstWord(final String str){
   si=0;
   return getNextWord(str);
}    
static String getNextWord(final String str){
    while (str.length()>si && str.charAt(si) ==' ')si++;
    ei=si+1;
    while (str.length()>ei && str.charAt(ei) !=' ')ei++;
    String ret=str.substring(si, ei);
    si=ei+1;
    return ret;
   
}

static public Stack Perenos(String str, Font font, int strLen){
   Stack st=new Stack();
   String s="";
   int i=0;
   while (true){
       while (i<str.length() && font.stringWidth(s+str.charAt(i))<strLen)
            s+=str.charAt(i++);
       if (s.length()>0)
           st.addElement(s);
       s="";
       if (i>=str.length())        
           return st;
   }

} 
//get the encoded value of a single symbol, each return value is 3 characters long
static String hex(int sym)
 {
     return(hex.substring(sym*3, sym*3 + 3));
 }
 
// Hex constants concatenated into a string, messy but efficient
final static String hex = 
"%00%01%02%03%04%05%06%07%08%09%0a%0b%0c%0d%0e%0f%10%11%12%13%14%15%16%17%18%19%1a%1b%1c%1d%1e%1f" + 
"%20%21%22%23%24%25%26%27%28%29%2a%2b%2c%2d%2e%2f%30%31%32%33%34%35%36%37%38%39%3a%3b%3c%3d%3e%3f" + 
"%40%41%42%43%44%45%46%47%48%49%4a%4b%4c%4d%4e%4f%50%51%52%53%54%55%56%57%58%59%5a%5b%5c%5d%5e%5f" + 
"%60%61%62%63%64%65%66%67%68%69%6a%6b%6c%6d%6e%6f%70%71%72%73%74%75%76%77%78%79%7a%7b%7c%7d%7e%7f" + 
"%80%81%82%83%84%85%86%87%88%89%8a%8b%8c%8d%8e%8f%90%91%92%93%94%95%96%97%98%99%9a%9b%9c%9d%9e%9f" +
"%a0%a1%a2%a3%a4%a5%a6%a7%a8%a9%aa%ab%ac%ad%ae%af%b0%b1%b2%b3%b4%b5%b6%b7%b8%b9%ba%bb%bc%bd%be%bf" +
"%c0%c1%c2%c3%c4%c5%c6%c7%c8%c9%ca%cb%cc%cd%ce%cf%d0%d1%d2%d3%d4%d5%d6%d7%d8%d9%da%db%dc%dd%de%df" +
"%e0%e1%e2%e3%e4%e5%e6%e7%e8%e9%ea%eb%ec%ed%ee%ef%f0%f1%f2%f3%f4%f5%f6%f7%f8%f9%fa%fb%fc%fd%fe%ff";
 

}
