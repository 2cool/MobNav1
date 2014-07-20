package mobnav.canvas;
//import java.io.*;
import javax.microedition.lcdui.*;

import mobnav.gps.BTGPS;
import mobnav.gps.GGA;
import mobnav.gps.NMEA;
import mobnav.gps.Time;

import java.util.Calendar;
import java.util.Date;


public class LCD extends MODE_DEF{// implements MODE{
	
	
	
	
	static public boolean init=false;
	static private int step_x=0,step_y=0;
	static private int MAX_X, MAX_Y;
	static private Font f=null;
	static final private int fh=36;
	static final private int fw=15;
	static public Image dig=null;
	

	
	
    
	
	static public void init(int MAX_X, int MAX_Y){
		init=true;
		LCD.MAX_X=MAX_X;
		LCD.MAX_Y=MAX_Y;
		f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		step_y=MAX_Y/5;
		step_x=MAX_X>>1;
	}
	static public int getWidth(final char ch){
		if (ch=='.')
			return 7;
		else if (ch=='h' || ch=='m' || ch=='s' || ch=='k')				
			return fw-4;
		else
			return fw+2;
		
	}
	static public int getWidth(final String s){
		int x=0;
		for (int n=0; n<s.length(); n++)
			x+=getWidth(s.charAt(n));					
		return x;
	}
	
	
	static final private char hrs='h';
	static final private char min='m';
	static final private char sec='s';
	static final private char kh='k';
	//static final private char mh='l';
	static final private char km='z';
	
	static  public int draw(final Graphics g, final int x, final int y, final char ch){
		if (ch>='0' && ch<='9'){
			int iy=(ch-'0')*fh;
			g.drawRegion(dig, 0, iy, fw, fh, 0, x, y, 0);
			return fw+2;
		}else if (ch==hrs){
			g.drawRegion(dig, 0,360,fw-2,fh ,0,x,y ,0);
		}else if (ch==min){
			g.drawRegion(dig, 0,396,fw-2,fh-3,0,x,y+1,0);
		}else if (ch==sec){
			g.drawRegion(dig, 0,429,fw-2,fh-3,0,x,y+1,0);
		}else if (ch==kh){
			g.drawRegion(dig, 0,462,fw-2,fh-3,0,x,y+1,0);
		}else if (ch==km){
			g.drawRegion(dig, 0,525,fw-2,fh-3,0,x,y+1,0);
		}else{
			g.setColor(0);
			if (ch==':'){				
				g.fillRect(x, y+7, 5, 5);
				g.fillRect(x, y+23, 5, 5);
			}else if (ch=='.'){				
				g.fillRect(x+1, y+fh-5, 5, 5);
				return 7;
			}else if (ch=='_'){
				g.fillRect(x+1, y+fh-5, fw, 5);
				return fw+2;
			}else
				return fw;			
		}
		return fw-4;
	}
	static public void draw(final Graphics g, int x, final int y, final String s){
		for (int n=0; n<s.length(); n++){
			x+=draw(g,x,y,s.charAt(n));			
		}
		
		
		
	}
	private static void drawF(Graphics g, int x, int y,String s){
		x*=step_x;
		y*=step_y;
		y=y+step_y-fh-6;
		int w=getWidth(s);
		x+=(step_x-w)>>1;
		draw(g,x,y,s);
	}
	private static void drawDist(Graphics g, int x, int y, final double dist){
		String sd=Double.toString(dist*0.001);
		int d=sd.indexOf('.');
		int add=5-d;
		if (d+add>sd.length())
			add=sd.length()-d;
		if (add==1)
			add=0;
		sd=sd.substring(0,d+add)+km;
		drawF(g,x,y,sd);
	}
	private static void drawSpeed(Graphics g, int x,  int y,final double speed){	
		String ss;
		if (speed==-1)
			ss="__._"+kh;
		else{
			ss=Double.toString(speed);
			ss=ss.substring(0,ss.indexOf('.')+2)+kh;
		}
		drawF(g,x,y,ss);		
	}
	
	
	
	private static void drawTime(Graphics g, int x,  int y,final Time t,final boolean hours,final boolean UTC){
;
		String s=(UTC)?t.getUTC(':',!hours):t.get(':', !hours);
		if (s.length()==5){
        	
        	drawF(g,x,y,s);        	
        }else{
        	       	
        	drawF(g,x,y,s.substring(3, 8)); 

        }
	}
DRAW_TEXT dt=null;
	public void paint(Graphics g){
		
		if (init==false)
			init(MyCanvas.max_x, MyCanvas.max_y);
		
		

		g.setFont(f);		
		g.setColor(0xffffff);
		g.fillRect(0, 0, MAX_X, MAX_Y);


		
		
		
		drawSpeed(g,0,0,(GGA.fixQuality!=0)?BTGPS.speed4A()*3.6:-1);			
		
						
		///////1
		Time time;
		if (BTGPS.isOn()){  
			time=NMEA.time;                    
        }else{
            Date date=new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            double dtime=3600*calendar.get(Calendar.HOUR_OF_DAY);
            dtime+=60*calendar.get(Calendar.MINUTE);
            dtime+=calendar.get(Calendar.SECOND);
            dtime*=1000;
            time=new Time(dtime);
        }  
		drawTime(g,1,0,time,true,false);
		//2
		time=new Time(BTGPS.moving_timeMsec());
		drawTime(g,0,1,time,false,true);
		
		//3
		time=new Time(BTGPS.stoped_timeMsec());
		drawTime(g,0,2,time,false,true);
		//4
		double odom=BTGPS.getDistance();
		drawDist(g,0,3,odom);					
		drawSpeed(g,1,1,(GGA.fixQuality!=0)?BTGPS.averageSpeed()*3.6:-1);
		
		
			
			
			g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
			g.setColor(0);
			g.drawString("Speed", 4, 0, 0);					//00
			g.drawString("Time", (MAX_X>>1)+4, 0, 0);		//10
			g.drawString("Moving Time", 4, step_y, 0);		//01
			g.drawString("Stoped", 4, step_y+step_y, 0);	//02
			g.drawString("Trip Odom", 4, step_y*3, 0);		//03
			g.drawString("Moving Avg", (MAX_X>>1)+4, step_y, 0);		//03
			g.setColor(0x777777);
			
			g.fillRect((MAX_X>>1)-1, 0, 2, MAX_Y);
			for (int y=step_y-1; y<MAX_Y-step_y; y+=step_y)
				g.fillRect(0,y-1,MAX_X,2);	
			
			
			
			
			
			
		//	int x=(MAX_X>>1)+(((MAX_X>>1)-(4*24+6))>>1);
			//g.drawString(time.get(":"),x,y,0);
			
			//drawTime(g,1,2,BTGPS.moving_time*0.001);//BTGPS.nmea.time.msec()*0.001);
		
	    //drawDigit(g, 0, 24, '8', true);
	    //drawDigit(g, 24, 24, '8', false);
			
			
			//if (BTGPS.isOn()==false)
			// synchronized(MyCanvas.update){
		         //   MyCanvas.update.notify();
		     //   }
		
	}

}

