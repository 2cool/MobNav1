package mobnav.canvas;



import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import mobnav.gps.BTGPS;
import mobnav.gps.GGA;
import mobnav.gps.GSA;
import mobnav.gps.GSV;
import mobnav.gps.NMEA;
import mobnav.math.math;


public class Sattelites extends MODE_DEF{

	//#############################################################################################3
	
	static private int getIndex(int n){
		int m=0;
		for (int i=0; i<GSV.data.length; i++)
			if (GSV.data[i].prn<n)
				m++;
		return m;
	}
	
	
	//--------------------------------------------------------------------
	
	
				
	public void paint(final Graphics g){	
		
	    g.setColor(0);
	    g.fillRect(0, 0, MyCanvas.max_x, MyCanvas.max_y);
	    int x2=MyCanvas.max_x>>1;
	    int y2=MyCanvas.max_y>>1;
	    g.setColor(-1);
	   // g.drawString("Спутники", 20, 0, 0);
	    Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	    g.setFont(f);
	    int rs=f.getHeight();
	    rs=(int)(Math.sqrt(rs*2*rs)*0.5);
	    int r=Math.min(x2, y2)-rs-rs;
	    y2=r+rs+2;
	    math.Circle(g,x2,y2,r);
	    if (BTGPS.active==false)
			return;
	    int h=MyCanvas.max_y-y2-r-5-f.getHeight();
	    int ix=0;
	    int w=MyCanvas.max_x/12;
	    if (GSV.draw){
	        
	        if (GSV.error)
	            return;
	        
	        
	        
	      //  if (BTGPS.speed4A()>0.8){//3km/h
	      //  cos=Math.cos(-BTGPS.angle);
	      //  sin=Math.sin(-BTGPS.angle);
	      //  }
	        for (int i=0; i<GSV.total; i++){
	            double x=r*GSV.data[i].GetX();
	            double y=-r*GSV.data[i].GetY();

	            Point p=new Point((int)x,(int)y);
	            int snr=GSV.data[i].snr;
	            int prn_n=GSV.data[i].prn;
	            int color=0x808080;
	            int red=0,green=0;
	            boolean inUse=false;
	            if (snr>0){
	            	for (int n=0; n<GGA.satellites; n++)
	            		if (inUse=GSA.prn[n]==prn_n)
	            			break;
	                 if (inUse){                          
	                                           
		            	if (snr<=30)
		            		red=248<<16;
		            	else 
		            		if (snr<=40)            		
		            			red=216<<16;
		            	if (snr>10 && snr<=20)
		            		green=128<<8;
		            	else if (snr>20)
		            		green=248<<8;
		            	color=red|green;
	                 }
	            }
		            
	 
	            g.setColor(color);
	           // p.Rotate(cos,sin);
	            p.x+=x2;
	            p.y+=y2;
	            
	                 math.fillCircle(g,p.x, p.y, rs);
	                 int yh=(h*GSV.data[i].snr)/40;
	                 if (yh>h)
	                	 yh=h;
	                 ix=w*getIndex(prn_n);
	                 g.fillRect(ix, MyCanvas.max_y-yh, w, yh);                 
	                g.setColor(-1);
	               // g.drawRect(ix, MyCanvas.max_y-yh, w, yh);
	                String st =Integer.toString(GSV.data[i].prn);
	                g.drawString(st, ix+((w-f.stringWidth(st))>>1), MyCanvas.max_y-yh-f.getHeight(), 0);
	                
	                st=Integer.toString(snr);
	                g.setColor(0);
	                g.drawString(st, p.x-(f.stringWidth(st)>>1), p.y-(f.getHeight()>>1), 0);
	                
	           if (NMEA.nodata==false){    
		           g.setColor(-1);
		           String str=Double.toString(NMEA.hError);
		           int ind=str.indexOf('.');
		           str=str.substring(0,ind+2);
		           
		           g.drawString("err: "+str+" m", 0, 0, 0);
		           str="VDOP"+GSA.VDOP;
		           g.drawString("PDOP: "+GSA.PDOP, 0, f.getHeight(), 0);
		           g.drawString(str,MyCanvas.max_x-f.stringWidth(str), 0, 0);
	           }
	           
	        }
	    }
	    //_ystem.out.println("OK");
	}




}
