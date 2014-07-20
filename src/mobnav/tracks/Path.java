package mobnav.tracks;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Stack;

import javax.microedition.rms.RecordStore;

import mobnav.canvas.DistFromDot2Line2;
import mobnav.canvas.FILE;
import mobnav.canvas.Location;
import mobnav.canvas.MyCanvas;
import mobnav.canvas.NAVI;
import mobnav.canvas.Point;
import mobnav.canvas.PredictTime;
import mobnav.canvas.Settings;
import mobnav.canvas.TEXT;
import mobnav.gps.BTGPS;
import mobnav.math.math;


//---------------------------------------------------------------------------

//---------------------------------------------------------------------------
public class Path {
	private PredictTime predictTime=new PredictTime();
	public int predTime=-1;
	static public boolean inverse=false;	
	public String hint=null;
	//private double nodeAngle;
	static public Track track=null;
	public boolean close(final Track t){
		if (t==track){
			if (track.path!=null){
				t.path.removeAllElements();
				t.path=null;
			}			
			return true;
		}			
		return false;		
	}
	
	//---------------------------------------------------------------------------
	public String text(){return null;};
	
	
	//---------------------------------------------------------------------------
	public double getNode(){ //  целая часть это нод. Дробная это положение относительно длины нода
		try{
		double ret=-1;
		
		if (nodes!=null && nodes.size()==1){
			PathNode n=(PathNode)nodes.elementAt(0);
			ret=n.i;
			ret+=n.dist/elementAt(n.i).dist;
		}
		return ret;
		}catch (Exception ex){
			System.out.println("getNode ERROR "+ex.toString());
			return -1;
			}
	}
	//---------------------------------------------------------------------------
	public void setInverse(final boolean inv){
		if (inverse!=inv){
			predictTime.reset();
			inverse=inv;		
			if (nodes!=null)
				nodes.removeAllElements();
			nodes=null;
		}
	}
	//---------------------------------------------------------------------------		
	
	public static double max_DISTinM=50;
	
	static private  int max_dis_in_p2=0;  //максимальная дистанция отклонениея от трека в квадрате
	
	private static long lastUpdDistTime=0;
	
	//---------------------------------------------------------------------------
	 private void updateMinDistInPixels(){
        if (MyCanvas.time-lastUpdDistTime>500000){ 
        	lastUpdDistTime=MyCanvas.time;
            double dist=Location.getDistInPoints(Location.GetLocation(gp),max_DISTinM);//;///(int)Location.GetLocation(gp).getRuler(MIN_DISTinM, MAP.MAX_ZOOM);
            max_dis_in_p2=(int)(dist*dist);
        }
    }
	 
	 public Path(final Track t,final Stack path,final boolean inverse){		 
		 if (t.path==null)
			 t.path=path;
		 Path.inverse=inverse;
		 t.pathInUse_++;
		 track=t;
		 t.pathInUse_++;
	 }
	//---------------------------------------------------------------------------
	public Path(){
		if (track.path==null){
			track.path=new Stack();			
	    	RecordStore rs=null;
	    	ByteArrayInputStream bais=null;
	    	DataInputStream dis=null;
		    try{
		    	try {
					rs = RecordStore.openRecordStore(FILE.hash(track.fname()), true);					
					dis=new DataInputStream(bais=new ByteArrayInputStream(rs.getRecord(track.trackNumber+1)));
					int i=0;
					int fileSize=bais.available()-20; 
					while (i<fileSize){			       
						TrackNode tn=new TrackNode(dis);
						//System.out.println("height "+tn.fheight);
						track.path.addElement(tn);
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
		track.pathInUse_++;
    }
	 // ##############################################################################
	     
		
	//---------------------------------------------------------------------------
	 static public TrackNode elementAt(final int i){
		 if (inverse==false)
			 return (TrackNode)track.path.elementAt(i);
		 else{
			 TrackNode n1=(TrackNode)track.path.elementAt(track.path.size()-1-i);
			 TrackNode n0=(TrackNode)track.path.elementAt(track.path.size()-2-i);
			 return new TrackNode(n1.gp,n0.dist,n1.fheight,-1,null);			
		 }
	 }
	//---------------------------------------------------------------------------			
	//private int  distFromBeg=0;
	

	DistFromDot2Line2 dfd=new DistFromDot2Line2();

	
	//------------------------------------------------------------------------
	private boolean testNode(final PathNode pn){
		int nodeI=pn.i;
		TrackNode tn0=elementAt(nodeI);
		TrackNode tn1=elementAt(nodeI+1);				
		double dist2=dfd.get(gp, tn0.gp, tn1.gp);		
		if (dist2<=max_dis_in_p2 && (nodeI==track.path.size()-2 || dfd.flag<=0)){
			double dist=0;
			if (dfd.flag==0)
				dist=(int)Math.sqrt(dfd.dist_from_dot_2_p0_2-dist2);
			pn.correct=dist-pn.dist>-max_DISTinM;			
			pn.dist=Math.max(pn.dist, dist);	
			return true;								
		}else
			return false;
	}
	//------------------------------------------------------------------------
	
	
	private boolean findNextNode(final PathNode pn){
				
		boolean ok=testNode(pn);		
		if (ok==false){						
			int psize=track.path.size();			
			pn.correct=false;
			double distPass=-pn.dist;
			for (int j=pn.i+1; j<psize-2; j++){
				double nodeDist=elementAt(j-1).dist;
				distPass+=nodeDist;
				if (distPass*rdtime<Settings.maxSpeed){
					pn.i=j;
					pn.dist=0;
					pn.dist_from_beg+=nodeDist;
					ok=testNode(pn);
					if (ok)									
						break;					
				}else
					break;
			}
		}
		return pn.correct;
	}
	//----------------------------------------------------------------------------------------
	// тесируему узлы найденные предварительно findNodes если findNodes нашел больше одного узла
	private long oldTime=-1;
	private double rdtime=1;
	private void testNodes(){
		
		for (int i=0; i<nodes.size(); i++){	
			PathNode pn=(PathNode)nodes.elementAt(i);
			if (findNextNode(pn)==false)
				nodes.removeElementAt(i--);
		}
		for (int i=1; i<nodes.size(); i++){
			if (((PathNode)nodes.elementAt(0)).i==((PathNode)nodes.elementAt(i)).i)
				nodes.removeElementAt(i--);
		}
		
	}
	//-----------------------------------------------------------------------------
	//private int lastNode=-1;
	private Stack findNodes(){
		Stack nodes=new Stack();
		
		
		double distFromBeg=0;

		int psize=track.path.size();
		int lastNode=-1;
		for (int i=0; i<psize-2; i++){	
			PathNode pn=new PathNode(i,distFromBeg);
			boolean ok=testNode(pn);
			if (ok){
				if (lastNode==-1 || lastNode+1!=i){
					nodes.addElement(pn);
					//System.out.println("nodes "+i);
				}
				lastNode=i;
			}			
			distFromBeg+=elementAt(i).dist;					
		}			
		if (nodes.size()==0)
			nodes=null;
		return nodes;
	}

	//-----------------------------------------------------------------------------------
	private int testNode(){
		PathNode n=((PathNode)nodes.elementAt(0));	
		if (findNextNode(n)){		
				
			//Point p0=elementAt(n.i).gp;
			//Point p1=elementAt(n.i+1).gp;
			//nodeAngle=math.GetAngle(p1.x-p0.x, p1.y-p0.y);
			return (int)(n.dist+n.dist_from_beg);		
		}else{
			nodes.removeAllElements();
			nodes=null;
			
			return -1;
		}
	
	}
	//---------------------------------------------------------------------------------
	public Stack nodes=null;
	private Point gp;
	
	//-------------------------------------------------------------------------------------
	private boolean busy=false;
	public int getDist2End(final Point gp,final boolean hint){
		try{
		if (busy==false){
			busy=true;
		 new Thread(new Runnable() {
	            public void run() {	
	            	int dist=getDistT(gp);
	            	if (dist!=-1){
	            		dist2end=track.trackLength-dist;
	            		predTime=(int)predictTime.predictTime2End(dist2end);
	            		if (hint)
	            			hint();
	            	}else
	            		dist2end=-1;
	            	
	            	busy=false;
	            }}).start();
		}
		}catch (Exception ex){
			System.out.println("getDist2End error "+ex.toString());
			busy=false;
			}
		 return dist2end;
	}
	//-------------------------------------------------------------------------------------
	public int dist2end=-1;
	private int getDistT(final Point gp){		
		this.gp=gp;
		
		
			
		updateMinDistInPixels();
		
		if (nodes==null){
			oldTime=MyCanvas.time;
			nodes=findNodes();			
		}else{
			rdtime=1000.0/(double)(MyCanvas.time-oldTime);
			oldTime=MyCanvas.time;
			if (nodes.size()>1){
				testNodes();
				if (nodes.size()==0)
					nodes=null;			
			}else
				return testNode();
		}
		
		return -1;
	}
	
	
	//#################################################################################
	
	// ##############################################################################
	static public long startTime=0;
	static private int runNodeN=0;
	static public  Point runGP=new Point();
    static public void setRunDot(){

    	int timePast=(int)(System.currentTimeMillis()-startTime)>>7;    	
    	while (
    			runNodeN<track.path.size()-1 && 
    			(elementAt(runNodeN)).dtime<=timePast && 
    			(elementAt(runNodeN+1)).dtime<timePast	
    			){
    		runNodeN++;    		
    	} 
    	if (runNodeN<track.path.size()-1){
	    	TrackNode t0=elementAt(runNodeN);
	    	TrackNode t1=elementAt(runNodeN+1);
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
    
    
    
    
    private void createHint(final TrackNode tn){
    	
    }
    ////////////////////////////////////////////////////
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
//////////////////////////////////////////////////////////////////////////////////
    static private int timePredict=5;
    private int predictNode(){
    	double node=getNode();
    	int i=(int)Math.floor(node);
    	TrackNode tn=(TrackNode)track.path.elementAt(i);
    	if (node>=0){
	    	double dist=BTGPS.speed4A()*timePredict-tn.dist*(node-i);
	    	int size=track.path.size()-1;
	    	while (i<size && dist>0){
	    		tn=(TrackNode)track.path.elementAt(++i);
	    		dist-=tn.dist;
	    	}
	    	return i;
    	}
    	return -1;
    }
////////////////////////////////////////////////////////////////////////////////////
	public void hint(){	
		int i=predictNode();
		if (i>=0){
			TrackNode tn=(TrackNode)track.path.elementAt(i);
			if (track.hint && inverse==false ){				
				while (i>0 && tn.txt==null)
					tn=(TrackNode)track.path.elementAt(--i);				
				if (tn.txt!=null){					
					if (hint==null || !hint.startsWith(tn.txt)){
						hint=tn.txt;
						TEXT.beep();						
					}
					return;
				}								
			}else createHint(tn);
		}
		
		hint=null;
	}
/*
if (lastNodeI>=0 && lastPredNodeI<tc.tns.size()-1){
    Point v=((elementAt(lastPredNodeI+1))).gp.Sub(((elementAt(lastPredNodeI))).gp);
 //   System.out.println("v="+v.toString());
    double ang=math.GetAngle(v.x, v.y);   
 //   System.out.println(lastNodeI+" "+lastPredNodeI);
 //   System.out.println("nodeAng="+nodeAngle*180/Math.PI);
 //   System.out.println("nextAng="+ang*180/Math.PI);
    ang-=nodeAngle;
    
      
    
    String turn;
    
    
    
    ang=ang*180/Math.PI;
    
    if (ang>180)
        ang-=360;
    if (ang<-180)
        ang+=360;
    
    if (ang<0){
        turn="left";
        ang=-ang;
    }else
        turn="right";
    
    
    
    if (ang>4 && ang<45)
        return "slight "+turn;
    else if (ang>=45 && ang <100)
        return "turn "+turn;
    if (ang>=100)
        return "sharp "+turn;                        
}

return null;
		outText="";
		*/
	
	
}
