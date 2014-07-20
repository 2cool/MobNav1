package mobnav.canvas;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
//import javax.microedition.media.Manager;
//import javax.microedition.media.MediaException;

import mobnav.gps.BTGPS;
import mobnav.gps.GGA;
import mobnav.gps.NMEA;
import mobnav.math.math;
import mobnav.math.rect;
import mobnav.tracks.Path;
import mobnav.tracks.TrackRecording;
import mobnav.tracks.Tracks;


public class NAVI 	extends MODE_DEF{

	static private int pppX;
	static private int pppY;
	static private int ppY;
	static private int ppX;
	static private float dxSpeed;
	static private float dySpeed;
	static public Point gp=new Point(-1,-1);
	static Point dir=new Point();
	static private int angle=0;
	static Point gp_on_scr;
	///////////////////////////////////////////////////////////////////////////
	static int ZOOMD[]={	
		10,
		20,
	    50,
	    100,
	    200,
	    500,
	    1000,
	    2000,
	    5000,
	    10000,
	    20000,
	    50000,
	    100000,
	    200000,
	    500000,
	    1000000,
	    2000000,
	    5000000,
	    10000000,
	    20000000
	};
	static public  Font font=null;
	static public  int fontH;
	// ############################################################################
	static public boolean mapDrawed=false;
	static int addzoom=0;
	static  public MAP map=null;
	static private boolean xynavigation=true;
	static public void setArowNavigation(){
		if (MyCanvas.touchPhone==false)
			xynavigation=false;
	}
	static public  int ANGLE_CHANGE_COUNT_MAX=2;
	static Point gpsLabel=new Point();
	static double direction=0;
	static public boolean  mapScroling=true;//false;
	//static private Point gpsPlaceMark=new Point();
	static Location mapLocation=new Location();
	static boolean mapMovingKeyIsPressed=false;
	// ##########################################################################
	static int ddtime;
	static Point lastGP=new Point();
	static double directorY=1;
	// ########################################################################
	static double directorX=0;
	static double dirAngle=0;
	static double dirAngleDelna=0.03;

	static Point cross;
	static public Point dp=new Point();
	static int addX;
	static int addY;
	static float dya;
	static float dxa;
	static double acumX;
	static double acumY;
	static final long DTIME=250;

	private static int oldFontSize=-1000;
	static public void setFont(final Graphics g){
		if (oldFontSize!=Settings.fontSize()){
			oldFontSize=Settings.fontSize();
			font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, oldFontSize);
			g.setFont(font);
		    fontH=font.getHeight();		    
		}
	}
	// ############################################################################
	public void paint(final Graphics g){
		
		if (! (mapDrawed=map!=null))
			return;
		if (MyCanvas.cur_screen==MyCanvas.menu){
			MAP.drawMap(g); 
		}else{
			gp=gp.add(dir.rotate90(angle));
			dir.x=dir.y=0;
			
			setFont(g);
			
		    try{   
				shiftAcceleration();
				Terminator();
				cross=LoadGPSPos();	    		    		    	
		    	int nextZoom=map.getNextZoom(addzoom);
		    	MAP.updDistK(gp,nextZoom);
		        if (mapDrawed=addzoom==0){
		        	map.paint(g);
		            RESENTLY_OPENED_LIST.getFileNameToLoad();
		        }else
		        	scale();                      
		       // tempVTG(g);
		        drawRuler(nextZoom);
		     }catch (OutOfMemoryError e)
		     {
		    	 MAP.freeMemory();
		       //  Storage.mapError("MAPER "+e.toString());
		       //  return;
		     }
		     catch (Exception e)
		     {
		    	 System.out.println("LoadMapError MAPER "+e.toString());
		         Storage.mapError("MAPER "+e.toString(),false);
		         return;
		     }
		    if (mapDrawed)
		    	drawRest();
			}	    
	}				
	// ##########################################################################
	

	public boolean key(final int keyCode){
	    boolean upd=true;
	    if (MenuStr.KeyPressed(keyCode)==false)
	        switch (keyCode) {
	            case Canvas.KEY_STAR:upd=false; Labels.add();   Interface.update_menu=true;break;
	            case Canvas.KEY_POUND:upd=false;MyCanvas.ChangeLights();break;

	            //LeftSoftKey
	            case -7:upd=false;Labels.menu();/*fire();*/break;
	            case -6:MyCanvas.SetMode(MyCanvas.menu);break;
	            case -5:
	            case Canvas.FIRE:
	            	if (centersk==null)
		            	xynavigation^=true;
		            else{	            	
		            	centersk=null;
		            	xynavigation=true;
		            	if (Tracks.runner!=null)
		            		Tracks.runner.startRunning();
		            }
	            
	            break;
	                                                                    
	            default :naviMapKeys(keyCode);
	        }    
	    if (upd)
	        synchronized(MyCanvas.update){MyCanvas.update.notify();}
	    return false;
	}
	// ##########################################################################

	//-----------------------------------------------------
	//static public Tracks tracks=new Tracks();
	static public Point getGP(){return gp;}

	static public void SetGP(final Point p){
	    mapScroling=true;
	    gp.x=p.x;
	    gp.y=p.y;
	}

	static void tochShiftAcceleration(){
	    dxa+=dxSpeed;
	    dya+=dySpeed;
	    Point p=map.relevantR(new Point((int)dxa,(int)dya));
	    dir.x+=p.x;
	    dir.y-=p.y;
	
	    dxa-=Math.floor(dxa);
	    dya-=Math.floor(dya);
	    dxSpeed*=Settings.SCROLING_MAP_BREAK;
	    dySpeed*=Settings.SCROLING_MAP_BREAK;
	   // _ystem.out.println(dxSpeed);
	    if (Math.abs(dxSpeed+dxa)<1 && Math.abs(dySpeed+dya)<1){
	        dxSpeed=dySpeed=0;
	       
	    }
	}

	static void drawCompas_(int i){
	    MyCanvas.g.setClip(cross.x-4, cross.y-44, 9, 8);   //N
	    MyCanvas.g.drawImage(Interface.newsImg, -(i&15)*9+cross.x-4, cross.y-44, 0);
	    
	    MyCanvas.g.setClip(cross.x+36, cross.y-4, 9, 8);   //E
	    MyCanvas.g.drawImage(Interface.newsImg, -((i>>4)&15)*9+cross.x+36, cross.y-4, 0);
	    
	    MyCanvas.g.setClip(cross.x-4, cross.y+36, 9, 8);   //S
	    MyCanvas.g.drawImage(Interface.newsImg, -((i>>8)&15)*9+cross.x-4, cross.y+36, 0);
	    
	    MyCanvas.g.setClip(cross.x-44, cross.y-4, 9, 8);   //W
	    MyCanvas.g.drawImage(Interface.newsImg, -(i>>12)*9+cross.x-44, cross.y-4, 0);
	}

	//--------------------------------------------------------------
	static void drawComaps(){          
	    switch (MAP.getRotateAngle()){
	        case Sprite.TRANS_ROT90:drawCompas_(0x2103);break;
	        case Sprite.TRANS_ROT180:drawCompas_(0x1032);break;
	        case Sprite.TRANS_ROT270:drawCompas_(0x0321);break;
	        default:drawCompas_(0x3210);
	    }    
	    MyCanvas.g.setClip(0, 0, MyCanvas.max_x, MyCanvas.max_y);
	}

	static  void  DrawSpeedTriangle(){
	    if (BTGPS.speed4A()>1.1){//4km/h     
	        int len=(int)(BTGPS.speed4A()*3.6);
	        if (len>MyCanvas.min2)
	            len=MyCanvas.min2;
	        if (len<32)
	            len=32;
	          updateDirection(); 
	      //  math.DrawTriangle(g,gpsLabel,Storage.screenTrack.normX, -Storage.screenTrack.normY,map.getRotateAngle(), len,Settings.TRACK_IN_MEMORY_RGB);
	          math.DrawTriangle(MyCanvas.g,gpsLabel,Math.sin(direction), -Math.cos(direction),MAP.getRotateAngle(), len,Settings.TRACK_IN_MEMORY_RGB);
	    }
	}
	
	static public void drawRest(){	
		MyCanvas.fastResponse=MyCanvas.sleep_time==MyCanvas.SLEEP_TIME_SHORT || MyCanvas.cur_screen==MyCanvas.menu;	
	    drawComaps();
	    DrawTrackInMemory();  	           
	    if (MyCanvas.fastResponse==false){
	    	DirectionalMarker();
	    	if (GGA.fixQuality>0)
	    		DrawSpeedTriangle();
	    	//Tracks.conductor();
	    	Path.drawRunnerMark();
	    }
	    Circuit.drawFinishLine();
	    if (mapScroling)
	        DrawCross();          
	    DrawGPSLocationPlace(gpsLabel);
	    DrawDirector();  
	    
	    //Conductor();
	    TEXT.textUP(mapLocation,MyCanvas.g,false); 
	
	}

	//static private boolean updated=false;
	static void shiftAcceleration(){
	    tochShiftAcceleration();
	    if (mapMovingKeyIsPressed){
	        long t2=System.currentTimeMillis();
	        ddtime++;       
	        double a=1+(double)ddtime*0.05;
	        if (a>16)
	            a=16;
	        double dtime=(t2-MyCanvas.keyPressedTime);
	        if (dtime>DTIME){
	            if (xynavigation){
	                dir.x+=a*(double)(addX);//<<shift);
	                dir.y+=a*(double)(addY);//<<shift);
	            }else{
	                if (addX>0)
	                     SetDirAngle(dirAngle-=dirAngleDelna);
	                else if (addX<0)
	                    SetDirAngle(dirAngle+=dirAngleDelna);
	                double t=a*directorX*addY+acumX;
	                acumX=t-Math.floor(t);
	                dir.x-=Math.floor(t);//<<shift);
	                t=a*directorY*addY+acumY;
	                acumY=t-Math.floor(t);                        
	                dir.y+=Math.floor(t);//<<shift);
	            }
	        }
	    }
	    if (MyCanvas.keyPressed==false && lastGP.equals(gp))
	        MyCanvas.stopUpdCanvas();
	    lastGP.x=gp.x;lastGP.y=gp.y;
	
	        
	}

	static public void naviMapKeys(final int keyCode){ 	
		key=addX=addY=0;     	    
	    ddtime=0; 
		Point add=new Point(1,1);
		if (map.getZoom()<=16)
	    	add=map.relevantR(add);
	    
	    switch (keyCode) {
	        case Canvas.KEY_NUM1:addzoom++;break;
	        case -1:
	        case Canvas.UP:
	            map_Up(add.y); break; //up
	        case -2:
	        case Canvas.DOWN:
	            map_Down(add.y);break;  //down
	        case -3:
	        case Canvas.LEFT:
	            map_Left(add.x);break; //  <-
	        case -4:
	        case Canvas.RIGHT:
	            map_Right(add.x);break;  //  ->
	        case Canvas.KEY_NUM3:addzoom--;break;
	    }    
	}

	static void SetDirAngle(final double angle){
	    directorY=Math.cos(angle);
	    directorX=Math.sin(angle);    
	}

	static void DrawDirector(){
	    if (xynavigation==false && mapScroling && MyCanvas.touchPhone==false){
	        int x=MyCanvas.max_x>>1;
	        int y=MyCanvas.max_y>>1;
	        int dirX=(int)(directorX*50);
	        int dirY=(int)(directorY*50);
	        MyCanvas.g.setColor(0);
	        MyCanvas.g.drawLine(x, y, x-dirX ,y-dirY ); 
	        MyCanvas.g.setColor(-1);
	        MyCanvas.g.drawLine(x-dirX ,y-dirY, x-(dirX<<1) ,y-(dirY<<1));
	    }
	}

	static public void findMe(){
	    Interface.turnGps(true);
		mapScroling=false;
		gp.Set(NMEA.gp);	
		gp_on_scr=MAP.getMapPointNear(gp,map.getZoom());
		gpsLabel.Set(cross);
		mapLocation=(mapScroling)?Location.GetLocation(gp):NMEA.l;   
	}
	// ########################################################################

	static void map_Right(int add){
	    mapMovingKeyIsPressed=true;
	    key++;
	    if (xynavigation)
	        dir.x+=add;
	    else
	        SetDirAngle(dirAngle-=dirAngleDelna);
	    addX =add;
	    setMapInScrollingMode();
	}
	// ########################################################################

	static void map_Left(int add){
	    mapMovingKeyIsPressed=true;
	    key++;
	    if (xynavigation)
	        dir.x-=add;
	    else
	        SetDirAngle(dirAngle+=dirAngleDelna);
	    addX=-add;
	    setMapInScrollingMode();
	}

	static void map_Up(int add){
	    mapMovingKeyIsPressed=true;
	    key++;
	    if (xynavigation)dir.y+=add;
	    addY=add;
	    setMapInScrollingMode();
	}

	// ########################################################################
	static void map_Down(int add){
	    mapMovingKeyIsPressed=true;
	    key++;
	    if (xynavigation)dir.y-=add;
	    addY=-add;
	    setMapInScrollingMode();
	}

	// ########################################################################
	static void SetMap(final int x, final int y){
		 setMapInScrollingMode();
		    Point p=map.relevantR(new Point(x-(MyCanvas.max_x>>1),(MyCanvas.max_y>>1)-y));
		    dir.x+=p.x;
		    dir.y+=p.y;
		  
	}
	static void DragMap(final int x, final int y){
	    setMapInScrollingMode();
	    Point p=map.relevantR(new Point(ppX-x,y-ppY));
	    dir.x+=p.x;
	    dir.y+=p.y;
	    ppX=x;
	    ppY=y;
	}

	static double SpeedShift(){
		if (mapScroling || Settings.rotate_map<4)// || dontShift==true)
			MyCanvas.spshcnt=0;
		else{
			int step=5;
			int max=MyCanvas.max_x>>1;
			if (map.getZoom()<15){
				step=2;
				max>>=1;
			}
		    if (BTGPS.hdop_ok && BTGPS.speed4A() > 4)
		        MyCanvas.spshcnt+=step;
		    else
		        MyCanvas.spshcnt-=10;
		    if (MyCanvas.spshcnt<0)
		        MyCanvas.spshcnt=0;
		    if (MyCanvas.spshcnt>max)        
		        MyCanvas.spshcnt=max;
		}
	    return MyCanvas.spshcnt;
	}

	// ##########################################################################
	static void DrawGPSLocationPlace(Point gpsLabel){
		if (GGA.fixQuality!=0){
		    MyCanvas.g.setColor(0xff0000);  
		    int r=3;
		    //g.drawArc(gpsLabel.x-r, gpsLabel.y-r, r*2, r*2, 0, 360);
		    MyCanvas.g.fillArc(gpsLabel.x-r, gpsLabel.y-r, r*2, r*2, 0, 360);
		    r=(int)(MAP.getDistInPixels(NMEA.hError*0.5,map.getZoom()));
		    r<<=MAP.x2bit;
		    MyCanvas.g.drawArc(gpsLabel.x-r, gpsLabel.y-r, r*2, r*2, 0, 360);
	 	}else{
	 		MyCanvas.g.setColor(0);  
		    int r=5;
		    //g.drawArc(gpsLabel.x-r, gpsLabel.y-r, r*2, r*2, 0, 360);
		   // g.fillArc(gpsLabel.x-r, gpsLabel.y-r, r*2, r*2, 0, 360);
		    MyCanvas.g.drawRect(gpsLabel.x-r, gpsLabel.y-r, r*2, r*2);
		    
	 	}
	}

	static public void drawRuler(final int zoom){
	    
		
	    
	    int in=MAP.MAX_ZOOM-zoom;
	    int i=1,m;
	    double x2=(MAP.x2bit==1)?2:1;
	    double druler=x2*MAP.getDistInPixels(m=ZOOMD[in],zoom);
	    while (in-i>=0 && druler>((double)MyCanvas.max_x*0.55)){            
	        druler=x2*MAP.getDistInPixels(m=ZOOMD[in-i],zoom);            
	        i++;
	    }
	    if (i==1)
	        while (in+i<ZOOMD.length && druler<((double)MyCanvas.max_x*0.25)){
	
	            druler=x2*MAP.getDistInPixels(m=ZOOMD[in+i],zoom);
	            i++;
	    }        
	       
	    int  ruler=(int)druler;
	    double st=druler*0.1;
	    
	    int x=MyCanvas.max_x-ruler;
	    int y=MyCanvas.max_y-7-30;       
	    int tx=x;
	    double a=0;
	
	    for (i=0; i<10; i++){
	        MyCanvas.g.setColor(((i&1)==0)?0:0xffffff);
	        double ist=Math.ceil(a+st);
	        a=+st-ist;
	        int iist=(int)ist;
	        MyCanvas.g.fillRect(tx,y+1, iist-1,4);
	        tx+=iist;
	    }
	        
	    Font f=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
	    String str=GetDistStr(m);
	    str+=" z"+Integer.toString(zoom); 
	    DRAW_TEXT.drawWBString(MyCanvas.g,f,str, MyCanvas.max_x-f.stringWidth(str), y-f.getHeight());
	}

	// ##########################################################################
	static public String GetDistStr(double dist){
	    if (dist<1000)
	        return Integer.toString((int)dist)+"m";
	    else{
	        return Integer.toString((int)(dist*0.001))+"km";
	    }
	}

	static void DrawCross(){
	    if (MyCanvas.cur_screen==MyCanvas.manual_track_make){
	        MyCanvas.g.setColor(0xff0000);
	        MyCanvas.g.drawLine(cross.x-10, cross.y, cross.x+10, cross.y);
	        MyCanvas.g.drawLine(cross.x, cross.y-10, cross.x, cross.y+10);
	    }else{
	        MyCanvas.g.setColor(0x0);
	        MyCanvas.g.drawLine(cross.x-10, cross.y-10, cross.x+10, cross.y+10);
	        MyCanvas.g.drawLine(cross.x-10, cross.y+10, cross.x+10, cross.y-10);
	        MyCanvas.g.setColor(0xffffff);
	        MyCanvas.g.drawLine(cross.x-5, cross.y-5, cross.x+5, cross.y+5);
	        MyCanvas.g.drawLine(cross.x-5, cross.y+5, cross.x+5, cross.y-5);
	    }
	}

	// ##########################################################################
	static void Terminator(){
	    if (MAP.Terminator(gp))
	        StopMapSelfMoving();
	}

	static void DirectionalMarker(){
	   
	   if (MyCanvas.direct_to_label &&   Labels.active()){
	        Point p=Labels.getActive().p().Sub(gp);
	        if (Math.abs(p.x)>1 || Math.abs(p.y)>1){
	            double vx=p.x;
	            double vy=-p.y;
	            double rl=1/Math.sqrt(vx*vx+vy*vy);
	            vx*=rl;
	            vy*=rl;
	            math.DrawTriangle(MyCanvas.g,mapScroling?cross:cross,vx, vy,MAP.getRotateAngle(),MyCanvas.min2>>1,Settings.DIRECTION_COLOR);
	         }
	    }
	}

	

	static double updateDirection(){
	    if (!NMEA.gp.equals(MyCanvas.oldGp)){
	        MyCanvas.gpa[MyCanvas.gpai&3]=MyCanvas.mid(NMEA.gp,MyCanvas.oldGp);    
	        MyCanvas.gpai++;
	
	        MyCanvas.oldGp.x=BTGPS.gp().x;
	        MyCanvas.oldGp.y=BTGPS.gp().y;
	        //if (true)return direction;
	        if (MyCanvas.gpai>=4){
	            int i=(MyCanvas.gpai-4);
	            Point a0=MyCanvas.mid(MyCanvas.gpa[(i+1)&3],MyCanvas.gpa[i&3]);
	            Point a1=MyCanvas.mid(MyCanvas.gpa[(i+2)&3],MyCanvas.gpa[(i+1)&3]);
	            Point a2=MyCanvas.mid(MyCanvas.gpa[(i+3)&3],MyCanvas.gpa[(i+2)&3]);
	            a0=MyCanvas.mid(a1,a0);
	            a1=MyCanvas.mid(a2,a1);
	            //a0=mid(a1,a0);
	            a0=a1.Sub(a0);
	            direction= math.getAngle(a0.x, a0.y);
	            //System.out.println(MyCanvas.direction/Math.PI*180);
	        }
	    }
	    return direction;
	}

	public static boolean onCanvas(final Point p1,final Point p2){
		PointD dp1=new PointD(p1);
		PointD dp2=new PointD(p2);
		int r=math.cohen_sutherland(
				new rect(0,0,MyCanvas.max_x,MyCanvas.max_y),
				dp1,dp2);
		if (r==0){
			p1.set(dp1);
			p2.set(dp2);
			return true;
		}
		return false;
	}
	//public static boolean onCanvas(Point p1,Point p2){
		//math.rect rds=new math.rect(0,0,0,0);
		/*
		//if (true)return true;
		if (p2.x<max_x && p2.x>=0 && p2.y<max_y && p2.y>=0 || p1.x<max_x && p1.x>=0 && p1.y<max_y && p1.y>=0)
			return true;
		else{
			
			double dx=p2.x-p1.x;
			if (dx==0 && p1.x>=0 && p1.x<max_x &&){
				
			}
			double dy=p2.y-p1.y;
				
		}*/

	static void DrawTrackInMemory(){	
	        Point p[]=new Point[2];
	        Point t=OZI_PLT.GetFirstPoint();
	        if (t==null)
	            return;
	        p[0]=getPoint(t);
	        int i=0;
	        MyCanvas.g.setColor(Settings.TRACK_IN_MEMORY_RGB);
	        while ((t=OZI_PLT.GetNextPoint())!=null){
	            p[(++i)&1]=getPoint(t);
	            Point p0=new Point(p[0]);
	            Point p1=new Point(p[1]);
	            if (onCanvas(p0,p1))
	            	MyCanvas.g.drawLine(p0.x, p0.y, p1.x, p1.y);          
	        }                                          
	}

	static void rotateMap(){ 
		if (Settings.rotate_map<4){
			MyCanvas.autoRotateMapReset();
			angle=MyCanvas.rotate[Settings.rotate_map];
		}else if (mapScroling ==false && GGA.fixQuality>0 && BTGPS.speed4A() >= 0.8){
		    double ang=Math.toDegrees(TrackRecording.on_screen.getDirectionAngle());//BluetoothGPS.angle);
		    double angMin;
		    int a;
		
		    angMin=Math.min(ang, 360-ang);
		    MyCanvas.subAngle=0;
		    a=Sprite.TRANS_NONE;
		    if (Math.abs(ang-90)<angMin){
		        angMin=Math.abs(ang-90);
		        MyCanvas.subAngle=90;
		        a=Sprite.TRANS_ROT270;
		    }
		    if (Math.abs(ang-180)<angMin){
		        angMin=Math.abs(ang-180);
		        MyCanvas.subAngle=180;
		        a=Sprite.TRANS_ROT180;
		    }
		    if (Math.abs(ang-270)<angMin){
		        angMin=Math.abs(ang-270);
		        MyCanvas.subAngle=270;
		        a=Sprite.TRANS_ROT90;
		    }
		   // System.out.println(subAngle+" "+(ang-270)+ " "+angMin);
		    if (angle!=a && Math.abs(Math.abs(ang-MyCanvas.oldSubAngle)-Math.abs(ang-MyCanvas.subAngle))>Settings.D_ANGLE_2_CHANGE){
		        if (++MyCanvas.angleChangeCount>ANGLE_CHANGE_COUNT_MAX || BTGPS.speed4A()>5){
		            MyCanvas.oldSubAngle=MyCanvas.subAngle;
		            angle=a;
		            MyCanvas.angleChangeCount=0;
		        }
		    }else{
		        MyCanvas.angleChangeCount=0;
		    }
		}
	}

	static public void scale(){ 
	    if (addzoom!=0){
	        int dzoom=map.getNextZoom(addzoom)-map.getZoom();        
	        if (dzoom!=0){
	            if (addzoom==dzoom && System.currentTimeMillis()-MyCanvas.keyPressedTime<500){
	                Image im=map.zoomMap(dzoom); 
	                int x=0,y=0;
	                if (dzoom<0){
		                x=(MyCanvas.max_x-(MyCanvas.max_x>>-dzoom))>>1;
		                y=(MyCanvas.max_y-(MyCanvas.max_y>>-dzoom))>>1;
	                }
	                if (x!=0){
	                    MyCanvas.g.setColor(-1);
	                    MyCanvas.g.fillRect(0, 0, MyCanvas.max_x, MyCanvas.max_y);
	                }
	                MyCanvas.g.drawRegion(im, 0, 0, im.getWidth(), im.getHeight(), angle, x, y, 0); 
	                 synchronized(MyCanvas.update){
	            MyCanvas.update.notify();}
	            }else{
	               // StopUpdCanvas();
	                addzoom=0;
	               // System.out.println("ffffff");
	                map.setZoom(map.getZoom()+dzoom);
	                synchronized(MyCanvas.update){MyCanvas.update.notify();}
	            }
	        }else
	            addzoom=0;
	    }
	    
	}

	//---------------------------------------------------------------
	static public Point getPoint(final Point tgp){
		Point p=MAP.getMapPointNear(tgp, map.getZoom()).Sub(gp_on_scr);    	
		p=p.rotate90(angle);
		p.x<<=MAP.x2bit;
		p.y<<=MAP.x2bit;
		p=p.add(cross);	
		
		return p;
	}
//добавить приближение по двойному клику
	static Point LoadGPSPos(){
		
		
			
		rotateMap();
		map.rotate(angle);	
		cross.x=MyCanvas.max_x>>1;
		cross.y=MyCanvas.max_y>>1;
		double sp=SpeedShift();	        
	    dp=new Point((int)(TrackRecording.on_screen.normX*sp),(int)(TrackRecording.on_screen.normY*sp));
	    
	    if (angle==0){
	    	cross.x-=dp.x;
	    	cross.y+=dp.y;
	    }
	    if (angle==Sprite.TRANS_ROT180){
	    	cross.x+=dp.x;
	    	cross.y-=dp.y;
	    }
	    if (angle==Sprite.TRANS_ROT90){
	    	cross.x-=dp.y;
	    	cross.y-=dp.x;
	    }
	    if (angle==Sprite.TRANS_ROT270){
	    	cross.x+=dp.y;
	    	cross.y+=dp.x;
	    }
	    
	    
	    
	  
	    if (mapScroling || GGA.fixQuality==0){     	
	    	gp_on_scr=MAP.getMapPointNear(gp,map.getZoom());
	    	gpsLabel=getPoint(NMEA.gp);
			mapLocation=new Location(gp);		
		}else
			findMe();          
	   return cross;
	}
	
			
	BUTON_SOFT_KEY leftsk=new BUTON_SOFT_KEY("Меню",null,-6,0xeeeeee);
	BUTON_SOFT_KEY rightsk=new BUTON_SOFT_KEY("Метки",null,-7,0xeeeeee);
	static BUTON_SOFT_KEY centersk=null;
	
	
	static public void startRunningButton(){
		if (centersk==null && Tracks.runner!=null && Path.startTime==0){
			centersk=new BUTON_SOFT_KEY("Start",null,Canvas.FIRE,0xeeeeee);
			xynavigation=true;
		}else
			if (Tracks.runner==null)
				centersk=null;
		
	}
	
	static final int TRIMOR=5;
	static int dragLastY;
	static int dragLastX;
	static int dySpeedt;
	static int dxSpeedt;
	// ##########################################################################
	static int key=0;
	   	
	public BUTON_SOFT_KEY leftsk(){return leftsk;}
	public BUTON_SOFT_KEY rightsk(){return rightsk;}
	public BUTON_SOFT_KEY centrsk(){return centersk;}
    
	public BUTON_TOUCH_SCR plus(){return BUTON.PLS;}
	public BUTON_TOUCH_SCR minus(){return BUTON.MIN;}
	public BUTON_TOUCH_SCR add(){return BUTON.ADD;}
    public BUTON_TOUCH_SCR light(){return BUTON.LIGHT;}
    public BUTON_TOUCH_SCR next(){return BUTON.NEXT;}
	static void StartMapSelfMoving(){
	    
	     if ((dxSpeedt|dySpeedt)!=0){
	        dxSpeed=dxSpeedt;
	        dySpeed=dySpeedt;
	        dxa=dya=0;
	        MyCanvas.startUpdCanvas();
	     } else
	        StopMapSelfMoving();
	    
	     
	}
	static void setMapInScrollingMode(){
	    if (mapScroling==false){
	        gp.Set(NMEA.gp);   
		    mapScroling=true;
		    //StopMapSelfMoving();
		    synchronized(MyCanvas.update){        
		            MyCanvas.update.notify();                
		        }
	     }
	}
	static boolean MapIsSelfMoving(){return dxSpeed!=0 || dySpeed!=0;}
	static void StopMapSelfMoving(){
	    dxSpeed=dySpeed=dxa=dya=0;
	}
	static void TestIfMapCanBeginSelfMove(final int x, final int y){
	    try{
	   dxSpeedt=dragLastX-x;
	   dySpeedt=dragLastY-y;
	  
	   if ((dxSpeedt*dxSpeedt+dySpeedt*dySpeedt)<4)
	       dxSpeedt=dySpeedt=0;
	   dragLastX=x;
	   dragLastY=y;
	   } catch (ArithmeticException ex){dxSpeedt=dySpeedt=0;}
	}
	static boolean PointWasNotDragged(final int x, final int y){		
	      return Math.abs(x-pppX)<TRIMOR && Math.abs(y-pppY)<TRIMOR || (System.currentTimeMillis()-MyCanvas.pointerTime)>200;
	 }
	
	
	static private boolean buttonPresed=false;
	
	
	
	
	
	public void slideX(boolean f){}
	//###################################################################################
	public void pointerPressed(int x, int y){
		super.pointerPressed(x, y);
		 if (map==null) 
			 return;
		 if (!(buttonPresed=BUTON.screenPressed(x, y, MyCanvas.mode[MyCanvas.cur_screen]))){
			 pppX=ppX=x;
		     pppY=ppY=y;
		 }
	}
	//###################################################################################
	public void pointerDragged(int x, int y){
		super.pointerDragged(x, y);
		if (map==null ) 
			return;
		if (buttonPresed==true)
			BUTON.pointerDragged(x,y,MyCanvas.mode[MyCanvas.cur_screen]);
		else{	
			setMapInScrollingMode();	 
			MyCanvas.startUpdCanvas();
			TestIfMapCanBeginSelfMove(x,y);
		    DragMap(x,y);
		}
	}
	//###################################################################################
	public void pointerReleased(int x, int y){
		super.pointerReleased(x, y);
		if (map==null) 
			return;
		if (buttonPresed)
			BUTON.screenRelissed(x,y,MyCanvas.mode[MyCanvas.cur_screen]);
		else
			if (PointWasNotDragged(x,y)){
                if (MapIsSelfMoving())
				   StopMapSelfMoving();
                            	         
	       }else 
	           StartMapSelfMoving();												
		ppX=ppY=-1;
	}
	//###################################################################################
	
	public void dublClick(int x, int y){
		addzoom++;
		SetMap(x,y);
	}
	

	
	public void keyPressed(final int keyCode){
		if (map==null) 
			return;
	    if (key(keyCode))
	        synchronized(MyCanvas.update){MyCanvas.update.notify();}
	}
	public void keyReleased(final int keyCode){
		if (map==null ) 
			return;
		mapMovingKeyIsPressed=false;		 		
	}
	public void keyRepeated(final int keyCode){
		if (map==null ) 
			return;
		MyCanvas.startUpdCanvas();
	}
	
	
	//-----------------------------------------------------------------------------
	
	
	
	
	
	
	

}
