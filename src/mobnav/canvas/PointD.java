package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */
public class PointD {
 public  double x, y;
 
    public PointD normalize(){
    	try{
    	double d=1.0/Math.sqrt(x*x+y*y);
    	return new PointD(x*d,y*d);
    	}catch (Exception ex){return new PointD(0,0);}
    }
    public double dist(final PointD p){
    	double dx=x-p.x;
    	double dy=y-p.y;
    	return Math.sqrt(dx*dx+dy*dy);
    }
    public double angle(final PointD v){
    	return (x*v.x+y*v.y)/(Math.sqrt(x*x+y*y)*Math.sqrt(v.x*v.x+v.y*v.y));
    }
    public PointD(final Point p){x=p.x;y=p.y;}
    public boolean in(final PointD p0, final PointD p1){
        return x>=p0.x && x<p1.x && y>=p0.y && y<p1.y;
    }
    public boolean in_yInverse(final PointD p0, final PointD p1){
        return x>=p0.x && x<p1.x && y>=p1.y && y<p0.y;
    }
    public boolean in(final double ox, final double oy){return x>=0 && y>=0 && x<ox && y<oy;}
    public String getString(){return "x="+x+"; y="+y+";";}
    public void println(){System.out.println("x="+x+"; y="+y+";");}
    public boolean equals(final PointD p){return x==p.x && y==p.y;}
    public PointD(final PointD p){x=p.x;y=p.y;}
    public PointD(final double x, final double y){this.x=x;this.y=y;}
    public PointD(){x=y=0;}
    public PointD Set(PointD p){x=p.x;y=p.y;return this;}
    public PointD Set(double x, double y){this.x=x;this.y=y;return this;}
   // public PointD Sub(PointD p1, PointD p2){return new PointD(p1.x-p2.x,p1.t-p2.t);}
    public PointD Sub(final PointD p){return new PointD(x-p.x,y-p.y);}
    public PointD Add(final PointD p){return new PointD(x+p.x,y+p.y);}
    //public PointD Xor(final PointD p){return new PointD(x^p.x,y^p.y);}
   // public PointD Add(final double x, final double y){return new PointD(this.x+=x,this.y+=y);}
    public PointD Mul(final double dx, final double dy){return new PointD((double)(x*dx),(double)(y*dy));}
    public PointD Div(final double dx, final double dy){return new PointD((double)(x/dx),(double)(y/dy));}
    public String toString(){return x+" "+y;}
    public PointD Rotate(final double theta){return Rotate(Math.cos(theta),Math.sin(theta));}
    public PointD Rotate(final double cos, final double sin){
        double tx = (double)(x*cos - y*sin);
        double ty = (double)(y*cos + x*sin);
        x=tx;
        y=ty;
        return this;
    }
  
}
