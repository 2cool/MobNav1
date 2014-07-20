package mobnav.canvas;

public class DistFromDot2Line2 {
	
	static public double dist_p_p_2(final Point a, final Point b){
		  double dx=a.x-b.x;
		  double dy=a.y-b.y;
		  return dx*dx+dy*dy;	  
		}
	public int flag=0;	
	public double dist_from_dot_2_p0_2=0;
	public double dist_from_p0_2_p1_2=0;

	//вычисляет  растояние от точки dot к прямой в квадрате
	public double get(final Point dot, final Point p0, final Point p1){
		    double a1, a2, b1, b2, a, b, c;
		    
		    a=dist_p_p_2(dot,p0);
		    dist_from_dot_2_p0_2=a;
		    b=dist_p_p_2(dot,p1);
		    c=dist_p_p_2(p0,p1);
		    dist_from_p0_2_p1_2=c;
		    
		    if(a>=b+c) {
		    	flag=1;
		    	return /*Math.sqrt*/(b);
		    }
		    if(b>=a+c){ 
		    	flag=-1;
		    	return /*Math.sqrt*/(a);
		    }
		    flag=0;
		    a1=p0.x-dot.x; 
		    a2=p0.y-dot.y; 
		    b1=p1.x-dot.x; 
		    b2=p1.y-dot.y; 
		    	    	 
		    double n=(a1*b2-b1*a2);
		    return /*Math.sqrt*/(n*n/c);
		}	
}
