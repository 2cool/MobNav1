package mobnav.tracks;



	class TestNode{/*
		static public double len=1;
		
		
		
		
		
		
		
		
		 private Point intermediateNormal(final Point norm0, final Point norm1){
		    	int x=(norm0.x+norm1.x)>>1;
		    	int y=(norm0.y+norm1.y)>>1;
		    	return new Point ((int)Math.sqrt(x),(int)Math.sqrt(y));
		    }
//--------------------------------------------------------------------------		 
		 static public  Point getLeftNormal(final Point p0, final Point p1){
		        Point v=p1.Sub(p0);
		        double d=len/Math.sqrt(v.x*v.x+v.y*v.y);
		        return new Point(-(int)(v.y*d),(int)(v.x*d));
		 }
//--------------------------------------------------------------------------
		 
		 static public boolean pnl(Point lp1,Point lp2)// тестовая точка = начало координат
			{
			    int tmp = (lp2.y-lp1.y)*lp1.x-(lp2.x-lp1.x)*lp1.y ;
			    return tmp>=0;//слева от прямой или лежит на прямой
			 
			}  
//--------------------------------------------------------------------------
		Point norm0,p0d,p0u,p;
		public boolean get(final Point p0, final Point p1, final Point p2){  //начать с середины
			norm0=getLeftNormal(p0,p1);
			p0d=new Point(p0.x-norm0.x,p0.y-norm0.y);         
            p0u=new Point(p0.x+norm0.x,p0.y+norm0.y);
			Point norm1=getLeftNormal(p1,p2);
			Point inorm=intermediateNormal(norm0, norm1);
			Point p1d=new Point(p1.x-inorm.x,p1.y-inorm.y);         
            Point p1u=new Point(p1.x+inorm.x,p1.y+inorm.y);
            
            boolean ret= (pnl(p0d,p1d) && pnl(p1d,p1u) && pnl(p1u,p0u) && pnl(p0u,p0d));
            norm0=norm1;
            p0d=p1d;
            p0u=p1u;
            p=p2;
            return ret;

		}
		public boolean get(final Point p1){  //Продолжаем
			Point norm1=getLeftNormal(p,p1);
			Point inorm=intermediateNormal(norm0, norm1);
			Point p1d=new Point(p1.x-inorm.x,p1.y-inorm.y);         
            Point p1u=new Point(p1.x+inorm.x,p1.y+inorm.y);
            boolean ret= (pnl(p0d,p1d) && pnl(p1d,p1u) && pnl(p1u,p0u) && pnl(p0u,p0d));
            norm0=norm1;
            p0d=p1d;
            p0u=p1u;
            p=p1;
			return ret;
		}
		
		*/
	}


public class TrackConductorDriver {/*
	static private final int min_DISTinM=100;
	static private  int min_dis_in_p;
	static private long oldTime=0;
	static private final int SLEEP_TIME=5;
	static private final int STEP=32;
	private TrackConductor tc=null;
	public boolean inverse=false;
	public boolean conduct=false;
	public boolean running=false;
    public TrackConductor active =null;
    private int lastNodeI=-1,lastPredNodeI=-1;
    private int lastLen2end=-1,len2EndOld=10000000,dist2End=-1;
    private double lastLen2nextNode;
    private Location lastNextNodeLoc;
    private int lastNode4len=-1;
    private int lastLen2End=0;
    private int minLen2End=1000000000;
    private String outText=null;	 
    private int text4PredNodeI=-1;
    private final int predictTime=4;    // sec
    private final int predLen=50;       //metr
    private double nodeAngle=0;
    
    
	
	public TrackConductorDriver(final TrackConductor tc){		
		this.tc=tc;
		inverse(TrackConductor.inverse);		
	}
   public boolean set(final TrackConductor tc){
	   boolean ret=false;
	   if (this.tc==null || tc!=this.tc){
		   	this.tc=tc;
		   	ret=true;
	   }
	   ret|=inverse(TrackConductor.inverse);
	   return ret;
   }
	
	
	 
	    
	    public boolean inverse(final boolean in){
	    	boolean ret=false;
	        if (inverse!=in){
	            reset();
	            inverse=in;
	            ret=true;
	        }
	        return ret;
	    }

	    private TrackNode elementAt(final int i){return (TrackNode)tc.tns.elementAt((inverse)?tc.tns.size()-1-i:i);}
	   // public TrackNode elementAt(final int i){return (TrackNode)tc.tns.elementAt(i);}
	    public float  len(final int i){return ((TrackNode)tc.tns.elementAt((inverse)?tc.tns.size()-2-i:i)).dist;}

	    public int size(){return tc.tns.size();}
	    
	    
	    
	   
	    
	    private  void reset(){
	    	dist2End=-1;
	        lastNode4len=lastNode_=lastNodeI=lastLen2end=lastPredNodeI=-1;         
	        lastLen2End=0;
	        minLen2End=len2EndOld=1000000000;

	    }
//	    public trackNode elementAt(final int i){return (trackNode)t.elementAt(i);}
	    //------------------------------------------------------------------
	   	    
	   
	    
	    	    //------------------------------------------------------------------
	   
	    //------------------------------------------------------------------
	    public String toConduct(final Point gp, final int speed, final double direction){
	        return null;
	    }
	    
	 static public boolean pnl(Point lp1,Point lp2)// тестовая точка = начало координат
	{
	    int tmp = (lp2.y-lp1.y)*lp1.x-(lp2.x-lp1.x)*lp1.y ;
	    return tmp>=0;//слева от прямой или лежит на прямой
	 
	}  

	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	    //-------------------------------------------------------------------
	   
	    private static void updateMinDistInPixels(final Point gp){
	        if (MyCanvas.time-oldTime>50000){
	            oldTime=MyCanvas.time;
	            min_dis_in_p=(int)Location.getDistInPoints(Location.GetLocation(gp),min_DISTinM);//;///(int)Location.GetLocation(gp).getRuler(MIN_DISTinM, MAP.MAX_ZOOM);
	        }
	    }
	        
	    public  Point GetLeftNormal(final Point p0, final Point p1, final double len){
	        Point v=p1.Sub(p0);
	        double d=len/Math.sqrt(v.x*v.x+v.y*v.y);

	       // int x=(int)(v.y*d);
	       // int y=(int)(v.x*d);
	        
	        return new Point(-(int)(v.y*d),(int)(v.x*d));
	    }
	    /*
	    private void GetNormalsPoints(Point ns[], final Point p0, final Point p1, final double len){
	        Point v=p1.Sub(p0);
	        double d=len/Math.sqrt(v.x*v.x+v.y*v.y);

	        int x=(int)(v.y*d);
	        int y=(int)(v.x*d);
	        ns[1]=new Point(p0.x-x,p0.y+y);
	        ns[0]=new Point(p0.x+x,p0.y-y);        
	    }
	    
	    
	   
	    
	    
	    
	    
	   // p0d=первая нижняя точка в многоугольнике
	    private boolean testNode(final Point gp, final int lastNode){
	        
	        if (lastNode==0){
	             Point p0=((elementAt(lastNode))).gp.Sub(gp);
	             Point p1=((elementAt(lastNode+1))).gp.Sub(gp);

	             Point norm0=GetLeftNormal(p0,p1,min_dis_in_p);
	             Point p0d=new Point(p0.x-norm0.x,p0.y-norm0.y);         
	             Point p0u=new Point(p0.x+norm0.x,p0.y+norm0.y);
	             p0=p1;
	             p1=((elementAt(lastNode+1))).gp.Sub(gp);
	             Point norm1=GetLeftNormal(p0,p1,min_dis_in_p);         
	             Point norm=new Point((norm1.x+norm0.x)>>1, (norm1.y+norm0.y)>>1);        
	             Point p1d=new Point(p0.x-norm.x,p0.y-norm.y);
	             Point p1u=new Point(p0.x+norm.x,p0.y+norm.y); 
	             return (pnl(p0d,p1d) && pnl(p1d,p1u) && pnl(p1u,p0u) && pnl(p0u,p0d));            
	        }else if (lastNode==tc.tns.size()-2){
	            Point p0=((elementAt(lastNode-1))).gp.Sub(gp);
	             Point p1=((elementAt(lastNode))).gp.Sub(gp);
	             Point norm0=GetLeftNormal(p0,p1,min_dis_in_p);
	             p0=p1;
	             p1=((elementAt(lastNode+1))).gp.Sub(gp);
	             Point norm1=GetLeftNormal(p0,p1,min_dis_in_p);         
	             Point norm=new Point((norm1.x+norm0.x)>>1, (norm1.y+norm0.y)>>1);
	             Point p0d=new Point(p0.x-norm.x,p0.y-norm.y);         
	             Point p0u=new Point(p0.x+norm.x,p0.y+norm.y);         
	             Point p1d=new Point(p1.x-norm1.x,p1.y-norm1.y);
	             Point p1u=new Point(p1.x+norm1.x,p1.y+norm1.y); 
	             return (pnl(p0d,p1d) && pnl(p1d,p1u) && pnl(p1u,p0u) && pnl(p0u,p0d));                    
	        }else{
	             Point p0=((elementAt(lastNode-1))).gp.Sub(gp);
	             Point p1=((elementAt(lastNode))).gp.Sub(gp);
	             Point norm0=GetLeftNormal(p0,p1,min_dis_in_p);
	             p0=p1;
	             p1=((elementAt(lastNode+1))).gp.Sub(gp);
	             Point norm1=GetLeftNormal(p0,p1,min_dis_in_p);         
	             Point norm=new Point((norm1.x+norm0.x)>>1, (norm1.y+norm0.y)>>1);
	             Point p0d=new Point(p0.x-norm.x,p0.y-norm.y);         
	             Point p0u=new Point(p0.x+norm.x,p0.y+norm.y); 
	             norm0=norm1;
	             p0=p1;
	             p1=((elementAt(lastNode+2))).gp.Sub(gp);
	             norm1=GetLeftNormal(p0,p1,min_dis_in_p); 
	             norm=new Point((norm1.x+norm0.x)>>1, (norm1.y+norm0.y)>>1);        
	             Point p1d=new Point(p0.x-norm.x,p0.y-norm.y);
	             Point p1u=new Point(p0.x+norm.x,p0.y+norm.y); 
	             return (pnl(p0d,p1d) && pnl(p1d,p1u) && pnl(p1u,p0u) && pnl(p0u,p0d));
	        }
	              
	    }
	    

	    
	    private int lastNode_=-1;
	    
	    
	    private int getNodeFullSearch(final Point gp){
	        Point p0d=null, p0u=null,p1d=null,p1u=null,norm0=null,norm1=null,p0=null,p1=null;
	        int tsize=tc.tns.size();
	        int i=0;
	        
	        for(;i<tsize; i++){
	            if (i==0){
	                p0=((elementAt(i))).gp.Sub(gp);
	                p1=((elementAt(i+1))).gp.Sub(gp);
	              //  if ((double)p0.x*(double)p1.x+(double)p0.y*(double)p1.y<MIN_DIST*MIN_DIST)
	              //          ;
	                norm1=GetLeftNormal(p0,p1,min_dis_in_p);
	                p0d=new Point(p0.x-norm1.x,p0.y-norm1.y);
	                p0u=new Point(p0.x+norm1.x,p0.y+norm1.y); 
	               
	                Point norm=GetLeftNormal(p1,((elementAt(i+2))).gp.Sub(gp),min_dis_in_p);
	                norm.x=(norm1.x+norm.x)>>1;
	                norm.y=(norm1.y+norm.y)>>1;
	                
	                
	                p1d=new Point(p1.x-norm.x,p1.y-norm.y);
	                p1u=new Point(p1.x+norm.x,p1.y+norm.y); 
	                if (pnl(p0d,p1d) && pnl(p1d,p1u) && pnl(p1u,p0u) && pnl(p0u,p0d))                       
	                    break;                
	            //    p1u=p0u;
	            //    p1d=p0d;
	                
	            }else if (i==tsize-1){
	                p1=((elementAt(i))).gp.Sub(gp);
	                p1d=new Point(p1.x-norm0.x,p1.y-norm0.y);
	                p1u=new Point(p1.x+norm0.x,p1.y+norm0.y); 
	                if (pnl(p0d,p1d) && pnl(p1d,p1u) && pnl(p1u,p0u) && pnl(p0u,p0d)){ 
	                    i--;
	                    break;                    
	                }

	            }else{
	               // Point p0=((trackNode)(t.elementAt(i))).gp.Sub(gp);
	                p1=((elementAt(i+1))).gp.Sub(gp);
	                norm1=GetLeftNormal(p0,p1,min_dis_in_p);
	                Point norm=new Point((norm1.x+norm0.x)>>1, (norm1.y+norm0.y)>>1);
	                p1u=new Point(p0.x+norm.x,p0.y+norm1.y);
	                p1d=new Point(p0.x-norm.x,p0.y-norm1.y); 
	                if (pnl(p0d,p1d) && pnl(p1d,p1u) && pnl(p1u,p0u) && pnl(p0u,p0d)){                       
	                    i--;
	                    break;                                                        
	                }
	            }
	            p0=p1;
	            norm0=norm1;
	            p0d=p1d;
	            p0u=p1u;
	            if ((i&STEP)==STEP){
	                try {Thread.sleep(SLEEP_TIME);} catch (InterruptedException ex) {}
	            }
	            
	        }
	        return i;
	    }
	  
	    
	    private int getNode(final Point gp){
	        updateMinDistInPixels(gp);
	        if (lastNode_>=0 && lastNode_<tc.tns.size()-1){
	            if (testNode(gp,lastNode_)){
	                //System.out.println("theSame "+lastNode_);
	                return lastNode_; 
	            }
	            if (lastNode_<=tc.tns.size()-3){
	                if (testNode(gp,lastNode_+1)){
	                  //  System.out.println("theSame+1 "+(lastNode_+1));
	                    return ++lastNode_; 
	                }
	            }
	            if (lastNode_>=1)
	                if (testNode(gp,lastNode_-1)){
	                    //System.out.println("theSame-1 "+(lastNode_-1));
	                    return --lastNode_;       
	                }                                    
	        }                
	        int node=getNodeFullSearch(gp); 
	        //System.out.println("testall "+i);
	        if (node<tc.tns.size()-1){	             
	            lastNode_=node;                        
	        }else{
	            reset();           
	        }
	        return lastNode_;

	    }
	    
// #################################################################################################
	    private int oldDist2End=-1;
	    private int distCon=0;
	    private long oldTestTime=0;
	    
	  public int distToEnd( Point gp){  
		  if (dist2End==0)
			  return 0;
	        Location lgp=Location.GetLocation(gp);
	        
	        
	        
	        int node=getNode(gp);
	        //System.out.println("node="+node);
	        if (node!=lastNodeI && node>=0 && node+1<tc.tns.size()){
	             Point v=((elementAt(node+1))).gp.Sub(((elementAt(node))).gp);
	            nodeAngle=math.GetAngle(v.x, v.y);
	            lastNodeI=node;
	        }

	        
	        if (lastNodeI==-1)
	            dist2End=-1;
	        else if ((lastNodeI+1)<tc.tns.size()){                                              
	            lastLen2end=getLenToEnd(lastNodeI+1); 
	            lastNextNodeLoc=Location.GetLocation(   (elementAt(lastNodeI+1)).gp   );
	            lastLen2nextNode=Location.getDistance(lgp,lastNextNodeLoc); 
	            dist2End=lastLen2end+(int)lastLen2nextNode;
	        }else
	            dist2End=0;
	        if (dist2End>0){
	        	if (dist2End<minLen2End)
	        		minLen2End=dist2End;
	        }else 
	        	if (minLen2End<min_DISTinM){
	        		dist2End=0;
	        }
	        
	        if (oldDist2End==-1){
	        	oldDist2End=dist2End;
	        	oldTestTime=MyCanvas.time;
	        }else{
	        	double dDist=Math.abs(dist2End-oldDist2End);
	        	double speed=dDist/(MyCanvas.time-oldTestTime);
	        	oldDist2End=dist2End;
	        	oldTestTime=MyCanvas.time;
	        	if (speed>0.05){  //200 км час
	        		dist2End=-1;
	        		lastNodeI=-1;
	        		System.out.println("TRACK_CONDUCTOR_ERROR");
	        	}
	        	
	        }
	        //поправить.вопервых ехать надо только вперед. если назад то идет все нафиг
	       // а значит уже можно отсеч движение по тойже дороге но назад
	        //надо учитывать скорость и тогда можно примерно понять где должен быть ездок
	        return dist2End;
	    }
	  
	  
	    //-------------------------------------------------------------------
	 
	  
	  
	  
	    //-------------------------------------------------------------------
	    private int getLenToEnd(final int node){  
	        try{    
	            if (lastNode4len<0 || lastNode4len>=tc.tns.size()){          
	                if ((node<<1)>tc.tns.size()){
	                    lastLen2End=0;
	                    for (int i=node; i<tc.tns.size()-1; i++){
	                        lastLen2End+= len(i); 
	                        if ((i&STEP)==STEP){
	                            try {Thread.sleep(SLEEP_TIME);} catch (InterruptedException ex) {}
	                        }
	                    }
	                }else{
	                    lastLen2End=tc.len;
	                    for (int i=0; i<node; i++){
	                        lastLen2End-= len(i); 
	                        if ((i&STEP)==STEP){
	                            try {Thread.sleep(SLEEP_TIME);} catch (InterruptedException ex) {}
	                        }
	                    }
	                }
	            }else if (node>lastNode4len){  
	                for (int i=lastNode4len; i<node; i++)
	                    lastLen2End-=len(i); 
	            }else if (node<lastNode4len){
	                for (int i=node; i<lastNode4len; i++)
	                    lastLen2End+=len(i); 
	            }

	            return lastLen2End;

	        }catch (Exception ex){System.out.println(ex.toString());}
	        return -1;
	        
	    }
	    //-------------------------------------------------------------------
	   // private double lastddot=0;;
	    //-------------------------------------------------------------------
	    
	    //------------------------------------------------------------------
	     double dAngle(final double a1, final double a2){     
	        double da=Math.abs(a1-a2);
	        return Math.min(da,Math.abs(da-Math.PI*2));
	}
	    //------------------------------------------------------------------
	  //  private double GetNodeAngle(final int i){
	   //     Point dp=((trackNode)t.elementAt(i+1)).gp.Sub(((trackNode)t.elementAt(i)).gp);
	   //     return math.GetAngle(dp.x, dp.y);
	//}
	    //------------------------------------------------------------------

	   
	    private int GetPredictNode(int node){ 

	        double speed=BTGPS.speed4A();
	        double dist=speed*predictTime+predLen;
	        if (dist>lastLen2nextNode){
	        
	            double nodslen=lastLen2nextNode;
	            while ((++node)<tc.tns.size()-1 && (nodslen+=len(node))<dist){          
	            }   
	        }
	        if (node>=tc.tns.size()-1)
	            node=tc.tns.size()-2;
	        return node;
	    }
	    //------------------------------------------------------------------

	    private String ConductText(){
	        //lastPredNodei
	                //lastNodei
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
	    }
	    //------------------------------------------------------------------
	    //запускать после lenToEnd

	    public String Conductor(final Point gp){
	    	if (dist2End==0)
	    		return outText="end";
	        if (lastNodeI==-1)
	            return outText="Back to the track!";
	       
	        
	        //если растояние до конца трека не уменьшается вот тогда турн бак
	     //   System.out.println(len2End+" "+len2EndOld);
	        if (dist2End-len2EndOld>100){
	                len2EndOld=dist2End;
	                return "turn back";
	            
	        }else if (dist2End<len2EndOld){
	            len2EndOld=dist2End;
	        }
	        
	        
	        
	        
	        int predNode=GetPredictNode(lastNodeI);
	        if (predNode==lastNodeI)
	            if (lastPredNodeI>lastNodeI){
	                return outText;
	            }else
	                return outText=null;                    
	        else if (predNode>lastNodeI){
	            lastPredNodeI=lastNodeI+1;
	            if (tc.conducted && inverse==false)                                
	                outText= ((TrackNode)tc.tns.elementAt(lastPredNodeI)).txt;                                                                                    
	            else{      
	                if (text4PredNodeI!=lastPredNodeI){
	                    text4PredNodeI=lastPredNodeI;
	                    outText=ConductText(); 
	                }
	            }                        
	        }
	        return outText;
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	  
	*/
	
	
	
	
}
