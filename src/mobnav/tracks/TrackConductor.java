package mobnav.tracks;
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
import javax.microedition.io.file.*;
import javax.microedition.rms.RecordStore;

//import javax.microedition.lcdui.*;
import java.util.Stack;
//------------------------------------------------------------------
public class TrackConductor {
	/*
    public static boolean inverse=false;
    public static boolean conduct=false;
    public static boolean running=false;
    public static boolean graph=false;
    
    
    public boolean conducted=false;
    public Point pMax=null,pMin=null;
    public String name=null;
    public Stack tns=new Stack();
    public Object elementAtI(final int i){return tns.elementAt((inverse)?tns.size()-1-i:i);}
    public TrackNode elementAt(final int i){return (TrackNode)tns.elementAt(i);}
    public float  len(final int i){return ((TrackNode)tns.elementAt((inverse)?tns.size()-2-i:i)).dist;}
    public static TrackConductor active =null;
    public int len=0;
  
    public int size(){return tns.size();}
    
   
 // ##############################################################################
    public TrackConductor(final Track t){
    	
    	RecordStore rs=null;
    	ByteArrayInputStream bais=null;
    	DataInputStream dis=null;
    	try{try {
			rs = RecordStore.openRecordStore(FILE.hash(t.fname()), true);					
			dis=new DataInputStream(bais=new ByteArrayInputStream(rs.getRecord(t.trackNbr+1)));
			int i=0;
			int fileSize=bais.available()-20; 
			while (i<fileSize){
		       
				TrackNode tn=new TrackNode(dis);
		       	         Add(tn);//new TrackNode(new Point(px,py),oldNLen,height,dtime,text));
		         i+=TrackNode.loaded;
		     }
    	} catch (Exception e) {}
    }finally{
        try{
            if (dis!=null)
                dis.close();
            if (bais!=null)
           	 bais=null;
            if (rs!=null)
           	 rs.closeRecordStore();            
        }catch (Exception e){}
    }
    }
 // ##############################################################################
    
   
 // ##############################################################################
    public TrackConductor(){}
 // ##############################################################################
    public void removeElementAt(final int i){        
        tns.removeElementAt(i);
    }
 // ##############################################################################
    public int Add(final TrackNode tn){
    	tns.addElement(tn);
    	return this.len+=tn.dist;
    }
 // ##############################################################################
    public int Add(final Point p, final float nlen){
        TrackNode tn=new TrackNode(p,nlen);
        tns.addElement(tn);
        len+=((TrackNode)tns.elementAt(tns.size()-1)).dist;   
        return len;
    }
 // ##############################################################################
    public int End(){
        tns.addElement(new TrackNode());
        return len;
    }
 // ##############################################################################
    public String toConduct(final Point gp, final int speed, final double direction){
        return null;
    }
 // ##############################################################################
  public static boolean pnl(Point lp1,Point lp2)// тестовая точка = начало координат
{
    int tmp = (lp2.y-lp1.y)*lp1.x-(lp2.x-lp1.x)*lp1.y ;
    return tmp>=0;//слева от прямой или лежит на прямой
 
}  
//##############################################################################
    //-------------------------------------------------------------------        
    static public  Point GetLeftNormale(final Point p0, final Point p1, final double len){
        Point v=p1.Sub(p0);
        double d=len/Math.sqrt(v.x*v.x+v.y*v.y);
        return new Point(-(int)(v.y*d),(int)(v.x*d));
    }
 // ##############################################################################
    static  double dAngle(final double a1, final double a2){     
        double da=Math.abs(a1-a2);
        return Math.min(da,Math.abs(da-Math.PI*2));
}
     static public int runNodeN=0;
    public static long startTime=0;
    
    public static Point runGP=new Point();
 // ##############################################################################
    public void setRunDot(){

    	int timePast=(int)(System.currentTimeMillis()-startTime)>>7;    	
    	while (
    			runNodeN<tns.size()-1 && 
    			((TrackNode)tns.elementAt(runNodeN)).dtime<=timePast && 
    			((TrackNode)tns.elementAt(runNodeN+1)).dtime<timePast	
    			){
    		runNodeN++;    		
    	} 
    	if (runNodeN<tns.size()-1){
	    	TrackNode t0=(TrackNode)tns.elementAt(runNodeN);
	    	TrackNode t1=(TrackNode)tns.elementAt(runNodeN+1);
	    	double k=(double)(timePast-t0.dtime)/(double)(t1.dtime-t0.dtime);
	    	double x=k*(t1.gp.x-t0.gp.x)+t0.gp.x;
	    	double y=k*(t1.gp.y-t0.gp.y)+t0.gp.y;
	    	runGP.set((int)x, (int)y); 
    	}
    }
 // ##############################################################################
    
    public static void stopRunning(final boolean stop){
    	if (stop)
    		startTime=0;
    }
 // ##############################################################################
    public void startRunning(){
    	runNodeN=0;    	    	
    	startTime=System.currentTimeMillis();    	
    }
 // ##############################################################################
    
    public static void drawRunnerMark(){	
		if (runGP.x!=0 && runGP.x!=0 ){
			Point p=NAVI.getPoint(runGP);						
			MyCanvas.g.setColor(0);
			math.Circle(MyCanvas.g, p.x,p.y, 10);
			MyCanvas.g.drawLine(p.x-12, p.y, p.x-8, p.y);
			MyCanvas.g.drawLine(p.x+8, p.y, p.x+12, p.y);
			MyCanvas.g.drawLine(p.x,p.y-12, p.x, p.y-8);
			MyCanvas.g.drawLine(p.x,p.y+8, p.x, p.y+12);
			MyCanvas.g.drawRect(p.x-1, p.y-1, 2, 2);
		}
	}
    
    
    */
 }
    //------------------------------------------------------------------
