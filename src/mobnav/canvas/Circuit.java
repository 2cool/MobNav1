package mobnav.canvas;

import mobnav.math.math;

public class Circuit {
	
	
	
	
	
	
	static public  Point startGP,finish=null;   
	static private Point curse=null;
	static public long startTime=0,startTimeFull=0,bestLapTime=Long.MAX_VALUE,time=0;
	static private int MAX_DIST,left=0,right=0;
	static public int iBestLapTime=0,iTime=0,lapsDone=0;
	static final private int MAX_DIST_IN_M=50;		
	static final private int MAX_ZONE_CNT=5;
	static private boolean side=false;
// -------------------------------------------------------------------------------		
	
	static public void drawFinishLine(){
		if (Circuit.finish!=null){
			Point p_s=NAVI.getPoint(Circuit.startGP);
			Point p_f0=NAVI.getPoint(Circuit.finish);
			Point p_f1=new Point(p_s.x-p_f0.x+p_s.x, p_s.y-p_f0.y+p_s.y);
			MyCanvas.g.setColor(0);
			MyCanvas.g.drawLine(p_s.x, p_s.y, p_f0.x, p_f0.y);
			MyCanvas.g.drawLine(p_s.x, p_s.y, p_f1.x, p_f1.y);
		}
	}
	// -------------------------------------------------------------------------------	
	static public void reset(){
		startTime=0;
		finish=null;
		bestLapTime=Long.MAX_VALUE;
		lapsDone=0;
		left=right=iBestLapTime=iTime=0;	
		side=false;
	}
// -------------------------------------------------------------------------------
	static public void start(final Point gp){
		reset();
		MAX_DIST=(int)Location.getDistInPoints(Location.GetLocation(gp),MAX_DIST_IN_M);				
		startTimeFull=startTime=System.currentTimeMillis();
		startGP=new Point(gp);	
	}
// -------------------------------------------------------------------------------	
	
	static public void curPos(final Point gp){
		long cTime=System.currentTimeMillis();
		time=cTime-startTime;
		iTime=(int)(0.001*(double)time);
		if (finish==null && gp.dist(startGP) >MAX_DIST){
			curse=new Point(gp);
			finish=new Point(startGP.x+gp.y-startGP.y,startGP.y+startGP.x-gp.x);			
		}
		if (finish!=null){
			int res=Point.pointNearLine(startGP, finish, gp);		
			if (res>0){
				left++;
				right=0;
						
				if (side && math.distFromDot2Line(gp,startGP, curse)<=MAX_DIST){
					lapsDone++;					
					
					startTime=cTime;
					if (bestLapTime>time){
						TEXT.beep();
						bestLapTime=time;
					}					
					iBestLapTime=(int)(0.001*(double)bestLapTime);					
				}
				side=false;
			}else if (res<0 ){
				right++;
				left=0;
				if (side==false && right>=MAX_ZONE_CNT){
					side=true;				
				}
			}						
		}		
	}								
}
