package mobnav.math;
/**
 *
 * @author 2cool
*/

import java.util.Stack;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

import mobnav.canvas.Point;
import mobnav.canvas.PointD;
import mobnav.canvas.Settings;
public class math {



static public final double M_PI =Math.PI;
static public final double M_PI12= M_PI/12.0;
static public final double M_PI6= M_PI/6.0;
static public final double M_PI2= M_PI/2.0;

public static void Circle(Graphics g,Point p, int r){g.drawArc(p.x-r, p.y-r, r<<1, r<<1, 0, 360);}
public static void fillCircle(Graphics g,Point p, int r){g.fillArc(p.x-r, p.y-r, r<<1, r<<1, 0, 360);}

public static void Circle(Graphics g,int x, int y, int r){g.drawArc(x-r, y-r, r<<1, r<<1, 0, 360);}
public static void Elipse(Graphics g,int x, int y, int rx, int ry){g.drawArc(x-rx, y-ry, rx<<1, ry<<1, 0, 360);}
public static void fillCircle(Graphics g,int x, int y, int r){g.fillArc(x-r, y-r, r<<1, r<<1, 0, 360);}


/* square root of 3 */
static public final double SQRT3 = Math.sqrt(3);



// ##########################################################################

public static boolean IntersectCircleLine(Point center,double radius,Point p1,Point p2)
{
  double x01=(double)p1.x-(double)center.x;
  double y01=(double)p1.y-(double)center.y;
  double x02=(double)p2.x-(double)center.x;
  double y02=(double)p2.y-(double)center.y;

  double dx=x02-x01;
  double dy=y02-y01;

  double a=dx*dx+dy*dy;
  double b=2.0*(x01*dx+y01*dy);
  double c=x01*x01+y01*y01-radius*radius;

  if(-b<0)
      return (c<0);
  if(-b<(2.0*a))
      return (4.0*a*c-b*b<0);
  return (a+b+c<0);
}










// ##########################################################################
static public double   getAngle(double dx, double dy){
    if (dy==0)
        return (dx>0) ?M_PI2:M_PI+M_PI2;

 double angle=math.atan(Math.abs(dx/dy));

    if (dy<0)
        angle=Math.PI-angle;
    if (dx<0)
        angle=Math.PI*2-angle;

    return angle;
}
// ##########################################################################
static public    double atan(double x) {
  int sta=0,sp=0;
  double x2,a;
  /* check up the sign change */
  if(x<0.F) {x=-x; sta|=1;}
  /* check up the invertation */
  if(x>1.F) {x=1.F/x; sta|=2;}
  /* process shrinking the domain until x<PI/12 */
  while(x>M_PI12) {
    sp++; a=x+SQRT3; a=1.0/a; x*=SQRT3; x-=1.0; x*=a;
  }
  /* calculation core */
  x2=x*x; a=x2+1.4087812; a=0.55913709/a; a+=0.60310579;
  a-=0.05160454*x2; a*=x;
  /* process until sp=0 */
  while(sp>0) {a+=M_PI6; sp--;}
  /* invertation took place */
  if((sta&2)!=0) a=M_PI2-a;
  /* sign change took place */
  if((sta&1)!=0) a=-a;
  return(a);
}
// ##########################################################################
final static public double LOGdiv2 = -0.6931471805599453094;
// ##########################################################################
public static double log(double x) {
        if (x < 0) 
            return Double.NaN;
        if (x == 1) 
            return 0d;
        if (x == 0) 
            return Double.NEGATIVE_INFINITY;
        if (x > 1) {
            x = 1 / x;
            return -1 * _log(x);
        }
        return _log(x);
    }
private static double _log(double x) {
        double f = 0.0;
        // Make x to close at 1
        int appendix = 0;
        while (x > 0 && x < 1) {
            x = x * 2;
            appendix++;
        }
        //
        x*=0.5;
        //x = x / 2;
        appendix--;
        //
        double y1 = x - 1;
        double y2 = x + 1;
        double y = y1 / y2;
        //
        double k = y;
        y2 = k * y;
        //
/*        for (long i = 1; i < 50; i += 2) {
            f = f + (k / (double)i);
            k = k * y2;
        }
        */        
        f = f + k;          k = k * y2;
        f = f + k*(1d/  3d);k = k * y2;
        f = f + k*(1d/  5d);k = k * y2;
        f = f + k*(1d/  7d);k = k * y2;
        f = f + k*(1d/  9d);k = k * y2;
        f = f + k*(1d/ 11d);k = k * y2;
        f = f + k*(1d/ 13d);k = k * y2;
        f = f + k*(1d/ 15d);k = k * y2;
        f = f + k*(1d/ 17d);k = k * y2;
        f = f + k*(1d/ 19d);k = k * y2;
        f = f + k*(1d/ 21d);k = k * y2;
        f = f + k*(1d/ 23d);k = k * y2;
        f = f + k*(1d/ 25d);k = k * y2;
        f = f + k*(1d/ 27d);k = k * y2;
        f = f + k*(1d/ 29d);k = k * y2;
        f = f + k*(1d/ 31d);k = k * y2;
        f = f + k*(1d/ 33d);k = k * y2;
        f = f + k*(1d/ 35d);k = k * y2;
        f = f + k*(1d/ 37d);k = k * y2;
        f = f + k*(1d/ 39d);k = k * y2;
        f = f + k*(1d/ 41d);k = k * y2;
        f = f + k*(1d/ 43d);k = k * y2;
        f = f + k*(1d/ 45d);k = k * y2;
        f = f + k*(1d/ 47d);k = k * y2;
        f = f + k*(1d/ 49d);k = k * y2;
        //
        f = f * 2;
        for (int i = 0; i < appendix; i++) {
            f = f + (LOGdiv2);
        }
        //
        return f;
    }
 // ##########################################################################
 public static double pow(double val, int pow) {
        double res = 1;
        for (int i = 0; i < Math.abs(pow); i++) {
            res = res * val;
        }
        if (pow < 0) {
            res = 1 / res;
        }
        return res;
    }
// ##########################################################################
public static int fact(int val) {
        int res = 1;
        for (int i = 1; i <= val; i++) {
            res = res * i;
        }
        return res;
    }
// ##########################################################################
public static double exp(double x) {
        double res = 1;
        double tx = Math.abs(x);
        for (int i = 1; i < 13; i++) {
            res = res + pow(tx, i) / fact(i);
        }
        if (x < 0) {
            res = 1 / res;
        }
        return res;
    }
// ##########################################################################
public static double pow(double x, double y){
    return  exp(y * log(x));
}
// ##########################################################################
public final static double goldS=1.0/1.6180339887;
public static void DrawTriangle(
        final Graphics g,
        final Point p,
        final double normX,
        final double normY,
        final int mapAngle90,
              double len,
        final int color){
    int min2=120;
   Image triangle;
   if (len>min2)
       len=min2;
   double k=0.5*goldS*len;
   if (k>10)
       k=10;
   int x0=(int)(normX*len);
   int y0=(int)(normY*len);
   int x1=(int)(normY*k);
   int y1=(int)(-normX*k);
   int x2=-x1;
   int y2=-y1;
    int xMax=(int)Math.max(Math.max(x1, x2),x0);
    int xMin=(int)Math.min(Math.min(x1, x2),x0);
    int yMax=(int)Math.max(Math.max(y1, y2),y0);
    int yMin=(int)Math.min(Math.min(y1, y2),y0);
    int idx=xMax-xMin;
    int idy=yMax-yMin;
    if (idx<2 || idy<2)
        return;
    Image image = Image.createImage(idx, idy);
    Graphics gg = image.getGraphics();
    x0-=xMin;
    x1-=xMin;
    x2-=xMin;
    y0-=yMin;
    y1-=yMin;
    y2-=yMin;
    gg.setColor(color);
    gg.fillTriangle(x0,y0,x1,y1,x2,y2);
    int[] rgb = new int [image.getWidth() * image.getHeight()];
    image.getRGB(rgb, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
    for(int i = 0; i < rgb.length; ++i)
        rgb[i]&=(rgb[i]==0xffffffff)?0x0:Settings.TRIANGLE_ALPHA;
    triangle=Image.createRGBImage(rgb, image.getWidth(), image.getHeight(), true);
    int sx=xMin,sy=yMin;
    switch(mapAngle90){
        case Sprite.TRANS_ROT90:sx=-yMax;sy=xMin;break;
        case Sprite.TRANS_ROT180:sx=-xMax;sy=-yMax;break;
        case Sprite.TRANS_ROT270:sx=yMin;sy=-xMax;break;
    }
    g.drawRegion(triangle,0,0,triangle.getWidth(),triangle.getHeight(),mapAngle90,p.x+sx,p.y+sy,0);
   }
/*
public static String [] parsString(String str, String s2m, final int size){
     int p=0,n=0;
     String[] s=new String[size];
     p=0;n=0;
     int oldP=0;
     while (n<size && p<=str.length()-s2m.length())
        if (str.regionMatches(true, p++, s2m, 0, s2m.length())==true){
            s[n++]=str.substring(oldP, p-1);
            oldP=p;
        }
     return s;
}
*/
public static int approached (double d){
    double na=Math.ceil(d);
    double nl=Math.floor(d);
    return (int)(((na-d)>(d-nl))?nl:na);
}
//вычисляет  растояние от точки dot к прямой
/*
public static double distFromDot2Line_(final Point p0, final Point p1, final Point dot){
	   double a =   p0.y - p1.y;
	   double b = p1.x -   p0.x;
	   double c =   p0.x * p1.y - p1.x * p0.y;
	   double l=1.0/Math.sqrt(a*a+b*b);
	   return Math.abs((a * dot.x + b * dot.y + c)*l);
	}
/*
static double _Point2SegmentDistance( Point P, Point P0, Point P1 )
{
      Point v = P1.Sub(P0);
      Point w=P.Sub(P0);//
      double c1=dot(w,v);
      double c2=dot(v,v);
      if ( c1  <= 0 )
            return d(P, P0);
      if ( c2  <= c1 )
            return d(P, P1);
      double b = c1 / c2; 
      Point Pb =new Point(P0.x+(int)(b*v.x),P0.y+(int)(b*v.y));// P0 + bv
      return d(P, Pb);
}
*/
//--------------------------------------------
static public double dist_p_p_2(final Point a, final Point b){
	  double dx=a.x-b.x;
	  double dy=a.y-b.y;
	  return dx*dx+dy*dy;	  
	}

//вычисляет  растояние от точки dot к прямой
static public double distFromDot2Line(final Point dot, final Point p0, final Point p1){
	    double a1, a2, b1, b2, a, b, c;
	    
	    a=dist_p_p_2(dot,p0);
	    b=dist_p_p_2(dot,p1);
	    c=dist_p_p_2(p0,p1);
	    
	    if(a>=b+c) {
	    	return Math.sqrt(b);
	    }
	    if(b>=a+c){ 
	    	return Math.sqrt(a);
	    }
	    a1=p0.x-dot.x; 
	    a2=p0.y-dot.y; 
	    b1=p1.x-dot.x; 
	    b2=p1.y-dot.y; 
	    	    	 
	    double n=(a1*b2-b1*a2);
	    return Math.sqrt(n*n/c);
	}

//-------------------------------------------------

static double dot(Point u, Point v){return u.x*v.x+u.y*v.y;}
static double norm(Point v){return Math.sqrt(dot(v,v));}
static double d(Point u, Point v){return norm(u.Sub(v));}























static final int LEFT  = 1;  /* двоичное 0001 */
static final int RIGHT = 2;  /* двоичное 0010 */
static final int BOT   = 4;  /* двоичное 0100 */
static final int TOP   = 8;  /* двоичное 1000 */

static int vcode(rect r, PointD p) {
	return 	((p.x < r.x_min) ? LEFT : 0)  +  /* +1 если точка левее прямоугольника */ 
         	((p.x > r.x_max) ? RIGHT : 0) +  /* +2 если точка правее прямоугольника */
         	((p.y < r.y_min) ? BOT : 0)   +  /* +4 если точка ниже прямоугольника */  
         	((p.y > r.y_max) ? TOP : 0);     /* +8 если точка выше прямоугольника */
}

/* если отрезок ab не пересекает прямоугольник r, функция возвращает -1;
   если отрезок ab пересекает прямоугольник r, функция возвращает 0 и отсекает
   те части отрезка, которые находятся вне прямоугольника */
	public static int cohen_sutherland (final rect r, final PointD a, final PointD b){
        int code_a, code_b, code;
        PointD c; 
        code_a = vcode(r, a);
        code_b = vcode(r, b);
        while ((code_a | code_b)!=0) {                
                if ((code_a & code_b)!=0)
                        return -1;
                if (code_a!=0) {
                        code = code_a;
                        c = a;
                } else {
                        code = code_b;
                        c = b;
                }
                if ((code & LEFT)!=0) {
                        c.y += (a.y - b.y) * (r.x_min - c.x) / (a.x - b.x);
                        c.x = r.x_min;
                } else if ((code & RIGHT)!=0) {
                        c.y += (a.y - b.y) * (r.x_max - c.x) / (a.x - b.x);
                        c.x = r.x_max;
                }
                else if ((code & BOT)!=0) {
                        c.x += (a.x - b.x) * (r.y_min - c.y) / (a.y - b.y);
                        c.y = r.y_min;
                } else if ((code & TOP)!=0) {
                        c.x += (a.x - b.x) * (r.y_max - c.y) / (a.y - b.y);
                        c.y = r.y_max;
                }
                if (code == code_a)
                        code_a = vcode(r,a);
                else
                        code_b = vcode(r,b);
        }
        return 0;
	}
	//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	static public int getIndex(final Stack sint, final int num){
		int first = 0,n=sint.size();
		if (n==0)
			return 0;
		if (num<((Integer)sint.elementAt(0)).intValue())
			return 0;
		if (num>((Integer)sint.elementAt(n-1)).intValue())
			return n;
		
		    int last = n,mid;     
		    while (first < last){
		        mid = first + ((last - first) >>1); 
		        int mids=((Integer)sint.elementAt(mid)).intValue();	        
		        if (num < mids)        
		            last = mid;        
		        else if (num > mids)        	
		            first = mid + 1;
		        else
		        	return mid;
		    }
		    return last;   
		}

}

