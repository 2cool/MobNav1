package mobnav.canvas;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import mobnav.gps.BTGPS;
import mobnav.gps.GGA;
import mobnav.gps.NMEA;
import mobnav.gps.Time;


public class LCDD extends MODE_DEF {

	 interface N {	
			public void f();			
		}
	static private int fireDownCnt=0;
	public void keyPressed(int keyCode){
		if (keyCode==-5 || keyCode==Canvas.FIRE){
			fireDownCnt=1;
		}else{
			fireDownCnt=0;
			key(keyCode);
		}
		
	
	
	} 
	final static int MAX_BLINKS=7;
	public void keyReleased(int keyCode){fireDownCnt=0;}
	public void keyRepeated(int keyCode){
		if ((keyCode==-5 || keyCode==Canvas.FIRE) && ++fireDownCnt>MAX_BLINKS){
			BTGPS.reset(resetMask);
			resetMask=0;
		}
		 synchronized(MyCanvas.update){
			 MyCanvas.update.notify();
	    }
	}
	
	static  long time=0;
	static final long timeDelay=2000;
	public void pointerPressed(int x, int y){
		oldX=x;
		oldY=y;
		time=System.currentTimeMillis();
		draged=false;
		fireDownCnt=0;
	}
	public void pointerDragged(int x, int y){};//}
	public void pointerReleased(int x, int y){
		if (fireDownCnt==0 && time>0){			
			int dx=x-oldX;
			int dy=y-oldY;
			if (dx<0)dx=-dx;
			if (dy<0)dy=-dy;
			if (dx>20 || dy>20)
				if (x<oldX)
					MyCanvas.nextScreenMode();
				else
					MyCanvas.prevScreenMode();			
			else
				change();											
		}
		time=0;
		fireDownCnt=0;
		MyCanvas.allRepaint=true;
	}
		
	private Graphics g=null;
	private static double k=1.538461538461538;
	private int x=0,y=0;
	private double sx,sy;
	private int x1,x2,y1,y2,x3,y3,w,h,x4,y4,h1,h2d;
	
	
    
    public boolean key(final int keyCode){
    	 boolean upd=true;
 	    if (MenuStr.KeyPressed(keyCode)==false)
 	        switch (keyCode) {
 	            case Canvas.KEY_POUND:upd=false;MyCanvas.ChangeLights();break;

 	           
 	            
 	            
 		        case -1:
 		        case Canvas.UP:
 		            ; break; //up
 		        case -2:
 		        case Canvas.DOWN:
 		        	change();
 		        	upd=MyCanvas.allRepaint=true;
 		            break;  //down
 		        case -3:
 		        case Canvas.LEFT:
 		            ;break; //  <-
 		        case -4:
 		        case Canvas.RIGHT:
 		            ;break;  //  ->
 	        }    
 	    if (upd)
 	        synchronized(MyCanvas.update){MyCanvas.update.notify();}
 	    return false;
	}
	private static void change(){
		if(resetMask==0)
     		resetMask++;
     	else{
     		resetMask<<=1;
     		if (resetMask==8)
     			resetMask=7;
     		if (resetMask>7)
     			resetMask=0;
     	} 		             			
	}
	private static void mount(final Graphics g, final int x, final int y, final int xs, final int ys){
		
		g.fillTriangle(x, y+ys, x+(xs>>1), y, x+(int)(xs*0.8),y+ys);
		g.fillTriangle(x+xs,y+ys,x+(int)(xs*0.8),y+(int)(ys*0.4),x+(int)(xs*0.3),y+ys);
	}
	public LCDD(int big, int mid, int sml){				
		LCDD.big=new LCDD(big);//(57);
		LCDD.mid=new LCDD(mid);//(22);
		LCDD.sml=new LCDD(sml);//(14);
		setup();
	}
	public LCDD(int s){
		this.sx=s;
		this.sy=sx*k;
		x1=(int)(0.2564f*sx);
		y1=0;
		w=(int)(0.49038461538461538461538461538462*sx);
		h=(int)(0.16875*sy);
		x2=(int)(0.12820512820512820512820512820513*sx);
		y2=(int)(0.083f*sy);
		x3=(int)(0.872f*sx);
		y3=(int)(0.0833333333333333333f*sy);
		h1=(int)(0.25f*sy);
		x4=(int)(0.12820512820512820512820512820513*sx);
		y4=(int)(0.5*sy);
		h2d=h>>1;
									
	}
	
	public void drawH0(){	
		int xx1=x+x1;
		int yh=y+h;
		g.fillRect(xx1,y+y1,w,h);		
		g.fillTriangle(x+x2+1, y+y2, xx1, y+y1, xx1, yh-1);
		g.fillTriangle(x+x3-1, y+y3, xx1+w, y, xx1+w, yh-1);
	}
	public void drawV1(){
		int yh=y+h;
		int yhh1=y+h+h1;
		g.fillRect(x, yh, h, h1);
		g.fillTriangle(x, yh,x+x2,y+y2+1,x+h-1,yh);
		g.fillTriangle(x+x4,y+y4-1,  x+h-1, yhh1,  x, yhh1);
	}

	private void drawV3(){
		x+=h+w+1;
		drawV1();
		x-=h+w+1;
	}
	private void drawV5(){
		y+=h+h1+1;
		drawV1();
		y-=h+h1+1;
	}
	private void drawV7(){
		y+=h+h1+1;
		x+=h+w+1;
		drawV1();
		x-=h+w+1;
		y-=h+h1+1;
	}
	private void drawH2(){
		y+=h+h1+1;
		drawH0();
		y-=h+h1+1;
	}
	private void drawH4(){
		int k=(h+h1+1)<<1;
		y+=k;
		drawH0();
		y-=k;
	}


	class n2 implements N{
		public void f(){
			drawH0();
			drawH2();
			drawV3();
			drawH4();
			drawV5();
			x+=sx+h2d;
		}
	}
	class n3 implements N{
		public void f(){
			drawH0();
			drawV3();
			drawH2();
			drawH4();
			drawV7();
			x+=sx+h2d;
		}
	}

	class n4 implements N{
		public void f(){
			drawV1();
			drawH2();
			drawV3();
			drawV7();
			x+=sx+h2d;
		}
	}
	class n5 implements N{
		public void f(){
			drawH0();
			drawV1();
			drawH2();
			drawH4();
			drawV7();
			x+=sx+h2d;
		}
	}
	class n6 implements N{
		public void f(){
			drawH0();
			drawV1();
			drawH2();
			drawH4();
			drawV5();
			drawV7();
			x+=sx+h2d;
		}
	}
	class n7 implements N{
		public void f(){
			drawH0();
			drawV3();
			drawV7();
			x+=sx+h2d;
		}
	}
	class n8 implements N{
		public void f(){
			drawH0();
			drawV1();
			drawH2();
			drawV3();
			drawH4();
			drawV5();
			drawV7();
			x+=sx+h2d;
		}
	}
	class n9 implements N{
		public void f(){
			drawH0();
			drawV1();
			drawH2();
			drawV3();
			drawH4();
			drawV7();
			x+=sx+h2d;
		}
	}
	class n0 implements N{
		public void f(){
			drawH0();
			drawV1();
			drawV3();
			drawH4();
			drawV5();
			drawV7();	
			x+=sx+h2d;
		}
	}
	class n1 implements N{
		public void f(){
			drawV3();
			drawV7();
			x+=sx+h2d;
		}
	}
	private void ul(){
		drawH4();
		x+=sx+h2d;
	}
	private void minusChar(){
		drawH2();
		x+=sx+h2d;
	}
	private void grad(){
		g.drawArc(sml.x-1, sml.y, 3, 3, 0, 360);
		x+=h2d+3;
	}
	private void min(){
		g.drawLine(sml.x-1,sml.y, sml.x-1, sml.y+3);
		x+=h2d;
	}
	private void sec(){
		g.drawLine(sml.x-1,sml.y, sml.x-1, sml.y+3);
		g.drawLine(sml.x+1,sml.y, sml.x+1, sml.y+3);
		x+=h2d+2;
	}
	n0 nn0=new n0();
	n1 nn1=new n1();
	n2 nn2=new n2();
	n3 nn3=new n3();
	n4 nn4=new n4();
	n5 nn5=new n5();
	n6 nn6=new n6();
	n7 nn7=new n7();
	n8 nn8=new n8();
	n9 nn9=new n9();
	
	N[]fn={nn0,nn1,nn2,nn3,nn4,nn5,nn6,nn7,nn8,nn9};
	private void dot(){
		int r=h>>1;
		if (r<=4){
			int yy=(int)(y+sy-3);				
			g.fillRect(x-2, yy, 3, 3);
		}else{
			int xx=(int)(x-(r>>1));
			int yy=(int)(y+sy-r);
			g.fillArc(xx, yy, r, r, 0, 360);
		}
		x+=h2d;
		
	}
	private void ddot(){
		int r=h>>1;
		if (r<=4){
			int yy=y+(int)(sy*0.5);
			g.fillRect(x-2, yy+4, 3, 3);
			g.fillRect(x-2, yy-4, 3, 3);
		}else{
			int xx=(int)(x-(r>>1));
			int yy=y+(int)(sy*0.5);
			g.fillArc(xx, yy+r+r, r, r, 0, 360);
			g.fillArc(xx, yy-r-r, r, r, 0, 360);
		}
		x+=h2d;
	}
	private void ws(){
		x+=sx+h2d;
	}

	public void print(Graphics g, String s){
		this.g=g;
		//g.setColor(0);
		//s=" 60";
	//	int tx=x;
		//int ty=y;
		for (int i=0; i<s.length(); i++){
			char n=s.charAt(i);
			if (n>='0' && n<='9'){
				fn[(int)(n-'0')].f();
			}else 
				
				switch (n){
				case '.':dot();break;
				case ':':ddot();break;
				case ' ':ws();break;
				case '_':ul();break;
				case '-':minusChar();break;
				case '°':grad();break;
				case '\'':min();break;
				case '"':sec();break;
				}
				
			
		}
		
		//x=tx;
		//y=ty;
	}
	static private double k_y=(double)MyCanvas.max_y/320.0;
	static private double k_x=(double)MyCanvas.max_x/240.0;
	static private void setup(){//if (true)return;
		

		speedH= (int)(91.0*k_y);
		odoY=	(int)(93.0*k_y);
		odoH=(int)(38.0*k_y);
		timerY=(int)(133.0*k_y);
		timerW=(int)(132.0*k_x);
		timerH=(int)(48.0*k_y);
		
		avgX=(int)(134.0*k_x);
		avgW=(int)(106.0*k_x);
		
		timeY=(int)(183.0*k_y);
		timeH=(int)(48.0*k_y);
		heightY=(int)(233.0*k_y);
		heightH=(int)(38.0*k_y);
		locY=(int)(273.0*k_y);
		locH=(int)(47.0*k_y);
		
		odoY1=(int)(k_y*96);
		timerY1=(int)(k_y*130);
		avgX1=(int)(k_x*136);
		timeY1=(int)(k_y*180);
		
		heightM=(int)(k_y*239);
		heightY1=(int)(k_y*237);
		heightX1=(int)(k_x*52);
		locY1=(int)(k_y*275);
		locX1=(int)(k_x*82);
	}

	static private int speedH=91;
	static private int odoY=93, odoH=38;
	static private int timerY=133, timerW=132, timerH=48;
	static private int avgX=134, avgW=106;
	static private int timeY=183, timeH=48;
	static private int heightY=233,heightH=38;
	static private int locY=273,locH=47;
	static private int odoY1,timerY1,avgX1,timeY1,heightM,heightX1,heightY1,locX1,locY1;
	
	static private int oldSpeed=101010101;
	static private void drawSpeed(Graphics g, final int bgColor,final int textColor){
		int speed=(int)((GGA.fixQuality!=0)?BTGPS.speed4A()*3.6:-1);
		if (speed<=999 && (oldSpeed!=speed || MyCanvas.allRepaint)){
			oldSpeed=speed;
			g.setColor(bgColor);
			g.fillRect(0, 0, MyCanvas.max_x, speedH);
			Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
			g.setFont(f);
			g.setColor(textColor);
			big.x=2;
			big.y=2;				
			String ss;
			if (speed==-1)
				ss="";
			else
				ss=Integer.toString(speed);
			int n=3-ss.length();
			ss="   ".substring(0,n)+ss;
			//ss="888";
			big.print(g,ss);			
			int tx=big.x+((MyCanvas.max_x-big.x-f.stringWidth(kph))>>1);
			g.drawString("kph", tx, big.y+(int)big.sy-f.getHeight(), 0);
		}
	}
	//-------------------------------------------------------
	static private double oldDist=-100;
	static private void drawODO(Graphics g, final int bgColor,final int textColor){
		double dist=BTGPS.getDistance()*0.001;
		if (dist<=9999 && (MyCanvas.allRepaint || oldDist!=dist)){
			oldDist=dist;
			g.setColor(bgColor);
			g.fillRect(0, odoY, MyCanvas.max_x, odoH);	
			Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);	
			g.setFont(f);
			g.setColor(textColor);
			
			mid.x=2;mid.y=odoY1;
			g.drawString("ODO", mid.x, mid.y, 0);
			mid.x+=f.stringWidth("ODO");
			
			String sd=Double.toString(dist);
			int d=sd.indexOf('.');
			if (d>0){
			sd=sd.substring(0,d+2);
			}else
				sd+=".0";
			int n=7-sd.length();
			sd="       ".substring(0,n)+sd;
			//0000.0	
			mid.x+=f.getHeight();
			//sd="8888.8";
			mid.print(g,sd);
			g.drawString("km", mid.x, mid.y+(int)mid.sy-f.getHeight(), 0);
		}
		
	}
	//-------------------------------------------------------	
	static private void drawTimer(Graphics g, final int bgColor,final int textColor){
		
		g.setColor(bgColor);
		g.fillRect(0, timerY, timerW, timerH);
		Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);	
		g.setFont(f);
		g.setColor(textColor);
		
		
		mid.y=timerY1;
		mid.x=2;
		Time t=new Time((BTGPS.moving_timeMsec()+BTGPS.stoped_timeMsec()));
		g.drawString("TIMER", mid.x, mid.y, 0);
		mid.y+=f.getHeight()-1;
		mid.print(g, t.hoursUTS()+":"+t.minutes());
		sml.x=mid.x; sml.y=mid.y+(int)(mid.sy-sml.sy);			
		sml.print(g,t.seconds());
	}
	//-------------------------------------------------------
	static private int oldAvg=-1010;
	static private void drawAvg(Graphics g, final int bgColor,final int textColor){
		int avg=(int)(BTGPS.average_speedF()*3.6);
		if (avg<=999 && (MyCanvas.allRepaint || oldAvg!=avg)){
			oldAvg=avg;
			g.setColor(bgColor);
			g.fillRect(avgX, timerY, avgW, timerH);	
			Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);	
			g.setFont(f);
			g.setColor(textColor);				
			mid.y=timerY1;
			mid.x=avgX1;
			g.drawString("AVG", mid.x, mid.y, 0);
			mid.y+=f.getHeight()-1;
			String savg=Integer.toString(avg);
			int n=3-savg.length();
			savg="   ".substring(0,n)+savg;
			mid.print(g,savg);
			g.drawString(kph, mid.x, mid.y+(int)mid.sy-f.getHeight(), 0);
		}
	}
	//-------------------------------------------------------
	
	static private void drawTime(Graphics g, final int bgColor,final int textColor){
		g.setColor(bgColor);
		g.fillRect(0, timeY, timerW, timeH);	
		Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);	
		g.setFont(f);
		g.setColor(textColor);					
		String h,m,s;
		if (BTGPS.isOn()){  
			h=NMEA.time.hours();
			m=NMEA.time.minutes();
			s=NMEA.time.seconds();
        }else{
            Date date=new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            h=TEXT.ndigits("0", calendar.get(Calendar.HOUR_OF_DAY));
            m=TEXT.ndigits("0", calendar.get(Calendar.MINUTE));
            s=TEXT.ndigits("0", calendar.get(Calendar.SECOND));
        } 																		
		mid.y=timeY1;
		mid.x=2;
		g.drawString("TIME", mid.x, mid.y, 0);
		mid.y+=f.getHeight()-1;
		mid.print(g, h+":"+m);
		sml.x=mid.x; sml.y=mid.y+(int)(mid.sy-sml.sy);			
		sml.print(g,s);
		
		
	}
		
	
	//-------------------------------------------------------
	static private int oldMax=-200;
	static private void drawMax(Graphics g, final int bgColor,final int textColor){
		int max=(int)(BTGPS.maxSpeed4()*3.6);
		if (max<999 && (MyCanvas.allRepaint || max!=oldMax)){
			oldMax=max;
			g.setColor(bgColor);
			g.fillRect(avgX, timeY, avgW, timerH);	
			
			Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);	
			g.setFont(f);
			g.setColor(textColor);
			mid.y=timeY1;
			mid.x=avgX1;
			g.drawString("MAX", mid.x, mid.y, 0);
			mid.y+=f.getHeight()-1;
			String smax=Integer.toString(max);
			int n=3-smax.length();
			smax="   ".substring(0,n)+smax;
			mid.print(g,smax);
			g.drawString(kph, mid.x, mid.y+(int)mid.sy-f.getHeight(), 0);
		}
	}
	//-------------------------------------------------------
	static int oldHei=-20003;
	static private void drawHeight(Graphics g, final int bgColor,final int textColor){
		int hei=(int)GGA.height;
		if (hei<=9999 && (MyCanvas.allRepaint || hei!=oldHei)){
			oldHei=hei;
			g.setColor(bgColor);
			g.fillRect(0, heightY, MyCanvas.max_x, heightH);	
			Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);	
			g.setFont(f);	
			g.setColor(textColor);
						
			mount(g,5,heightM,29,26);
			mid.y=heightY1;
			mid.x=heightX1;
			String m="";
			
			if (hei<0){
				m="-";
				hei=-hei;
			}
			
			String height=m+Integer.toString(hei);
			int n=4-height.length();
			height="    ".substring(0,n)+height;
			mid.print(g,height);	
			g.drawString("M", mid.x+16, mid.y+(int)mid.sy-f.getHeight(), 0);
		}
	}
	//-------------------------------------------------------

	static private void drawLocation(Graphics g, final int bgColor,final int textColor){
		g.setColor(bgColor);
		g.fillRect(0, locY, MyCanvas.max_x, locH);	
		
		Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);	
		g.setFont(f);		
	
		
		Location l=BTGPS.l();
		String lat=l.latitude();
		String latS=lat.substring(lat.length()-1,lat.length());
		lat=lat.substring(0,lat.length()-1);
		int n=13-lat.length();
		lat="             ".substring(0,n)+lat;
		String lon=l.longtitude();
		String lonS=lon.substring(lon.length()-1,lon.length());
		lon=lon.substring(0,lon.length()-1);
		n=13-lon.length();
		lon="             ".substring(0,n)+lon;
		
		sml.y=locY1;
		sml.x=locX1;
		
		sattelite(g,sml.x-45,sml.y+15,7);
			g.setColor(textColor);
		sml.print(g,lat);//"888°88'88.88\"");
		g.drawString(latS, sml.x, sml.y+(int)sml.sy-f.getHeight(), 0);
		sml.x=locX1;
		sml.y+=sml.sy+2;
		sml.print(g,lon);//" 88°88'88.88\"");
		g.drawString(lonS, sml.x, sml.y+(int)sml.sy-f.getHeight(), 0);
					
	}
	//-------------------------------------------------------
	private static String kph="kph";
	static private LCDD big=null,mid,sml;
	static private int resetMask=0;
	
	private long tempTime=0;
	public void paint(Graphics g){
								
		MyCanvas.allRepaint|=fireDownCnt>0;
		if (MyCanvas.allRepaint){			
			g.setColor(0);
		    g.fillRect(0, 0, MyCanvas.max_x		, MyCanvas.max_y);
		}
		drawSpeed(g,-1,0);
		if ((fireDownCnt&1)==0 && (resetMask&1)>0)drawODO(g,0,-1); else drawODO(g,-1,0);  //1 or 16
		if ((fireDownCnt&1)==0 && (resetMask&1)>0)drawTimer(g,0,-1);else drawTimer(g,-1,0);//2 or 16
		if ((fireDownCnt&1)==0 && (resetMask&2)>0)drawAvg(g,0,-1);else drawAvg(g,-1,0);  //4 or 16
		drawTime(g,-1,0);
		if ((fireDownCnt&1)==0 && (resetMask&4)>0)drawMax(g,0,-1);else drawMax(g,-1,0);  //8 or 16
		drawHeight(g,-1,0);
		drawLocation(g,-1,0);
		MyCanvas.allRepaint=false;
		
		long t=System.currentTimeMillis();
		if (time>0 && t-time>500){
			
			if (t > tempTime+500){	
				tempTime=t;
				
				if (++fireDownCnt>MAX_BLINKS){
					BTGPS.reset(resetMask);
					resetMask=0;
					time=0;
					
				}
		        MyCanvas.allRepaint=true;	
		        synchronized(MyCanvas.update){ MyCanvas.update.notify();}
	        
			}
			
			
			
			
			
		}
		
		 
	}
	
	
	
	
	static void sattelite(final Graphics g, int x, int y, int r){
		g.setColor(GGA.fixQuality==0?0x888888:0);
		int r2=r<<1;
		for (int i=4; i<=5; i++){
			g.drawRect(x-r*i, y-r, r2, r2);
			g.drawRect(x-r*i+1, y-r+1, r2-2, r2-2);
			
			g.drawRect(x-r*i+r*7-1, y-r, r2, r2);
			g.drawRect(x-r*i+1+r*7-1, y-r+1, r2-2, r2-2);
		}
		g.drawLine(x-r2, y, x+r2, y);
		g.drawLine(x-r2, y-1, x+r2, y-1);
		g.fillArc(x-r, y-r, r2, r2, 0, 360);
		int add=r>>1;
		for (int j=0; j<4; j++){
			r+=add;
		for (int i=0; i<2; i++){			
			g.drawArc(x-r, y-r, r*2, r*2, -45, -90);
			r++;
		}
		}
		
		
		
		
		
		
		
		
		
		
		
	}
}
