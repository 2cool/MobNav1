package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */
import javax.microedition.lcdui.*;

import mobnav.math.math;

public class Comapss {

static void FillTriangle(Graphics g,Point p1, Point p2, Point p3){
    g.fillTriangle(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
}
static double t=0;
static public void draw(Graphics g,double theta,Point p){
    int w=10;
    int h=(int)(1.6180339887*5*w);

    Point p0=new Point();
    Point p1=new Point(-w,0);
    Point p2=new Point(0,-h);
    Point p3=new Point(w,0);
    Point p4=new Point(0,h);
    g.setColor(0);
    math.Circle(g, p, h);
   
    theta=t+=0.1;
    double cos=Math.cos(theta),sin=Math.sin(theta);

    p0=p0.add(p);
    p1=p1.Rotate(cos, sin).add(p);
    p2=p2.Rotate(cos, sin).add(p);
    p3=p3.Rotate(cos, sin).add(p);
    p4=p4.Rotate(cos, sin).add(p);
   
    g.setColor(0xff00);
    math.fillCircle(g, p.add(new Point(h,0)), w);
    g.setColor(0xff);
    FillTriangle(g,p1,p2,p3);
    g.setColor(0xff0000);
    FillTriangle(g,p1,p4,p3);
   
    g.setColor(0xaa00);
    p1=new Point(-w+3,-w);
    p2=new Point(0,-h);
    p3=new Point(w-3,-w);
    FillTriangle(g,p1.add(p),p2.add(p),p3.add(p));
    g.setColor(0xffffff);
    math.fillCircle(g, p, w-2);

}

}
