package mobnav.canvas;

public class PredictTime {
	
	private final static double dontMove_SPEED_MetrPerSec=0.5;
	static private final long CNT=10000;
	
	 public void reset(){
		 startTime=-1; 
		 sumLen2end=cnt=0;		 
		 oldTime=System.currentTimeMillis();		 
		 averSpeed=-1;		
		 ret=-1;
		 System.out.println("PredictTime RESET");
	  }
	    //-------------------------------------------------------------------
	  private long startTime=-1,oldTime=System.currentTimeMillis(),lastScanTime;	  
	  private double  pathLen, averSpeed=-1,sumLen2end,cnt=0,ret=-1,lastScanDist;
	  
	  public double predictTime2End(final int len2end){
		  long t=System.currentTimeMillis();
		  if (len2end>0){
			  //System.out.println("to end "+len2end);
			  sumLen2end+=len2end;
			  cnt++;				  			  
			  if (t-oldTime>CNT){
				  oldTime=t;
				  double averLen2End=(sumLen2end/cnt);
				  sumLen2end=cnt=0;
				  if (startTime==-1){
					  lastScanTime=startTime=t;
					  lastScanDist=pathLen=averLen2End;
				  }else{					 
					  long dt=t-lastScanTime;
					  double  dp=lastScanDist-averLen2End;
					  if (dp/dt>0.001*dontMove_SPEED_MetrPerSec){					  
						  dt=t-startTime;				  				 
						  averSpeed= 1000.0*(pathLen-averLen2End)/dt;
						  if (averSpeed>0){
							  ret=averLen2End/averSpeed;
							  //System.out.println("averLen2End "+averLen2End);
							  //System.out.println("averSpeed "+averSpeed);
						  }
					  }else
						  startTime+=dt;
					  lastScanTime=t;
					  lastScanDist=averLen2End;					  
				  }		
			  }
		  }else
			  oldTime=t;
		  return ret;
	  }

}
