package mobnav.canvas;
/*
You need to change the #text attribute to change the content of element - e.g.
[code]
SVGElement text = (SVGElement) doc.getElementById("text_id");
text.setTrait("#text", "new text");
[/code]
For more information please see https://meapplicationdevelopers.dev.java.net/uiLabs/changing_text.html

Eva

 * 
 * 
 * 
 * 
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */
import javax.microedition.lcdui.*;

import mobnav.tracks.Track;

import org.w3c.dom.svg.*;

public class SVG_Path {
    

    public Point pMin=new Point(),pMax=new Point();
    Point p[]={new Point(), new Point()};
    private int bxy[]={0,0};
    private boolean moveTo=true;
    private int i=0,n=0; 
    
    private SVGElement path;
    private SVGPath d;
    private int toDraw=0;
    private  Point old=new Point();

   static private  int LAST_MAX_N_REACHED=0;
    
    
public SVG_Path(){
     path = (SVGElement)(l2d.image.getDocument().createElementNS( "http://www.w3.org/2000/svg", "path" ));
     d = l2d.rootElement.createSVGPath();
     
}    

public void moveTo(final Point p){
        LAST_MAX_N_REACHED++;
        old.Set(p);     
        d.moveTo( p.x, p.y );
    }
static final int width=5;
    public void lineTo(final Point p){
        if (/*n<3600 &&*/ (Math.abs(p.x-old.x)>width)||(Math.abs(p.y-old.y)>width)){
            LAST_MAX_N_REACHED++;
            toDraw++;
            d.lineTo( p.x, p.y );
            old.Set(p);
        }
    }

    public void append(){
        if (l2d.node!=null)
            l2d.rootElement.removeChild(l2d.node);
        l2d.node=l2d.rootElement.appendChild(path);
    }


    public void SetColor(final int color){
            int rgb=color;
            path.setRGBColorTrait( "stroke", l2d.rootElement.createSVGRGBColor(rgb>>16, (rgb>>8)&255, rgb&255 ) );
    }
    public void setWidth(final float width){
        path.setFloatTrait( "stroke-width", width );
    }


    public int End(final int color, final float width){
        if (toDraw>0){
            path.setPathTrait( "d", d );
            SetColor(color);
            setWidth(width);
            path.setTrait( "fill", "none" );
        }
       return toDraw;
    }
    public void mTranslate(final float x,final float y){setMatrix(getMatrix().mTranslate(x,y));}
    public void setStrokeWidth(final float width){path.setFloatTrait( "stroke-width", width );}
    public SVGMatrix getMatrix(){return path.getMatrixTrait("transform");}
    public void setMatrix(SVGMatrix matrix){path.setMatrixTrait("transform", matrix);}
    public void rotate(float angle){
        SVGMatrix matrix = path.getMatrixTrait("transform");
        matrix.mRotate(angle);
        path.setMatrixTrait("transform", matrix);
    }
    public void scale (float scale){
        SVGMatrix matrix = path.getMatrixTrait("transform");
        matrix.mScale(scale);
        path.setMatrixTrait("transform", matrix);
    }
    public void translate(float x, float y){
        SVGMatrix matrix = path.getMatrixTrait("transform");
        matrix.mTranslate(x, y);
        path.setMatrixTrait("transform", matrix);

    }









    
private  int setP(final Point ip, int i){
      boolean inMap = ip.x>=pMin.x && ip.x<pMax.x && ip.y>=pMin.y && ip.y<pMax.y;
      p[i]=ip.Sub(pMin);
      int ret= (inMap)?1:0;
     return ret;
 }

  private Point GetP(final Point p0, final Point p1){

    Point max=pMax.Sub(pMin);
    if (p0.in(new Point(0,0), max))
        return p0;
    if (p1.x * (- p0.x) > 0)
        return new Point(0,     (p1.y-p0.y)*(     -p0.x)/(p1.x-p0.x)+p0.y);
    else if ((p1.x - max.x) * (max.x - p0.x) > 0)
        return new Point(max.x, (p1.y-p0.y)*(max.x-p0.x)/(p1.x-p0.x)+p0.y);
    else if (p1.y  * (- p0.y) > 0)
        return new Point((p1.x-p0.x)*(     -p0.y)/(p1.y-p0.y)+p0.x, 0);
    else
        return new Point((p1.x-p0.x)*(max.y-p0.y)/(p1.y-p0.y)+p0.x,  max.y);
}
public void Add(Point ip){
    if (n==0)
        bxy[i]=setP(ip,i);
    else{
        i^=1;
        bxy[i]=setP(ip,i);
        int res=bxy[0]+bxy[1];
        if (res==0)
            moveTo=true;
        else if (res>0){
                if (moveTo){
                    moveTo=false;
                   // moveTo(p[i^1]);
                    moveTo(GetP(p[i^1],p[i]));
                }
                int dx=Math.abs(p[0].x-p[1].x);
                if ( dx>Math.abs(dx-(pMax.x-pMin.x))){
                	System.out.println("ned korection");
                }
                lineTo(p[i]);
            }
    }
    n++;
}


static float s_r_scale=0,scale=0;
static private boolean noDraw;
static final float pscale=16;
public void SetScale(final MAP map, float width){
    float r_scale=(float)(map.Scale(1,Track.max_zoom));
    
    if (r_scale==0){
    	r_scale=1.0f/(map.rScale(1,Track.max_zoom));
    }
    if (noDraw=r_scale>Track.maxSize())
        return;
    if (r_scale!=s_r_scale){
        s_r_scale=r_scale;
        scale=1.0f/r_scale;
    }
    if (r_scale<=pscale)
        width*= r_scale;
    else
        width=(width*scale*pscale<1)?r_scale:width*pscale;

    setStrokeWidth(width );
    l2d.scale(scale);

}

public void paint(Graphics g, int mx,int my){
    if ( ! noDraw){
    try{
        append();
        Point t0=new Point(mx,my);
        Point t1=t0.add(new Point(MAP.tileSize,MAP.tileSize));
        Point p0=NAVI.map.rScale(pMin,Track.max_zoom);
        Point p1=NAVI.map.rScale(pMax,Track.max_zoom);

        int minx=t0.x;
        if  (minx<p0.x)
            minx=p0.x;
        int maxx=t1.x;
        if (maxx>p1.x)
            maxx=p1.x;

        int miny=t0.y;
        if  (miny<p0.y)
            miny=p0.y;
        int maxy=t1.y;
        if (maxy>p1.y)
            maxy=p1.y;
        if (maxx>minx && maxy>miny){        	
           // g.setClip(minx&127, miny&127, maxx-minx, maxy-miny);
            append();
            l2d.setXY(p0.x-mx, p0.y-my);
            l2d.paint(g,0,0);
        }
        
    }catch(Exception ex){System.out.println("data "+ex);}
                                                
    }
}



}
