package mobnav.canvas;
/*
 *
 * @author 2cool
 */
import javax.microedition.lcdui.game.*;
final public class Point {
  public  int x, y;
    private int t;
    public boolean in(final Point p0, final Point p1){
        return x>=p0.x && x<p1.x && y>=p0.y && y<p1.y;
    }
    public boolean in_yInverse(final Point p0, final Point p1){
        return x>=p0.x && x<p1.x && y>=p1.y && y<p0.y;
    }
    public void set(final int x, final int y){this.x=x;this.y=y;}
    public void set(final PointD p){x=(int)p.x;y=(int)p.y;}
    public boolean in(final int ox, final int oy){return x>=0 && y>=0 && x<ox && y<oy;}
    public String getString(){return "x="+x+"; y="+y+";";}
    public void println(){System.out.println("x="+x+"; y="+y+";");}
    public boolean equals(final Point p){return x==p.x && y==p.y;}
    public Point(final Point p){x=p.x;y=p.y;}
    public Point(final int x, final int y){this.x=x;this.y=y;}
    public Point(){x=y=0;}
    public Point Set(Point p){x=p.x;y=p.y;return this;}
    public Point Set(int x, int y){this.x=x;this.y=y;return this;}
   // public Point Sub(Point p1, Point p2){return new Point(p1.x-p2.x,p1.t-p2.t);}
    public Point Sub(final Point p){return new Point(x-p.x,y-p.y);}
    public Point add(final Point p){return new Point(x+p.x,y+p.y);}
    public Point Xor(final Point p){return new Point(x^p.x,y^p.y);}
   // public Point Add(final int x, final int y){return new Point(this.x+=x,this.y+=y);}
    public Point Mul(final double dx, final double dy){return new Point((int)(x*dx),(int)(y*dy));}
    public Point Div(final double dx, final double dy){return new Point((int)(x/dx),(int)(y/dy));}
    public String toString(){return x+" "+y;}
    public Point Rotate(final double theta){return Rotate(Math.cos(theta),Math.sin(theta));}
    public Point Rotate(final double cos, final double sin){
        int tx = (int)(x*cos - y*sin);
        int ty = (int)(y*cos + x*sin);
        x=tx;
        y=ty;
        return this;
    }
    public Point rotate90(int angle){
        switch (angle){
            case Sprite.TRANS_ROT90:t=x;x=-y;y=t;break;
            case Sprite.TRANS_ROT180:x=-x;y=-y;break;
            case Sprite.TRANS_ROT270:t=x;x=y;y=-t;
        }
        return this;       
    }
	/// <summary>
	/// Определение положения точки относительно прямой
	/// </summary>
	/// <param name="line_point1">первая точка задающая линию</param>
	/// <param name="line_point2">вторая точка задающая линию</param>
	/// <param name="testPoint">
	/// точка, положение которой необходимо узнать
	/// </param>
	/// <returns>
	///  1 - точка слева от прямой
	///  0 - точка принадлежит прямой
	/// -1 - точка справа от прямой
	/// </returns>
	static public int pointNearLine(Point line_point1,Point line_point2,Point testPoint){
	    return (line_point2.x-line_point1.x)*(testPoint.y-line_point1.y) -
	              (line_point2.y-line_point1.y)*(testPoint.x-line_point1.x);	 	   	     
}
	 public double dist(final Point p){
	    	double dx=x-p.x;
	    	double dy=y-p.y;
	    	return Math.sqrt(dx*dx+dy*dy);
	    }
}

