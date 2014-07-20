package mobnav.canvas;
/**
 *
 * @author 2cool
 */
import javax.microedition.m2g.*;
import org.w3c.dom.*;
import org.w3c.dom.svg.*;


import javax.microedition.lcdui.*;

public class l2d {

public static Node node=null;
static private ScalableGraphics   sg = ScalableGraphics.createInstance();
static public final SVGImage  image = (SVGImage)( SVGImage.createEmptyImage( null ) );
static public final SVGSVGElement rootElement = (SVGSVGElement)(image.getDocument().getDocumentElement());
static private final Document  document = image.getDocument();
static private final SVGSVGElement root =(SVGSVGElement) document.getDocumentElement();
static private SVGPoint svgp=root.getCurrentTranslate();

static public void SetViewport(final int x, final int y){
      image.setViewportWidth(x);
      image.setViewportHeight(y);
}
    static public void setOpacity(){
        sg.setTransparency(Settings.TRACK_OPACITY);
    }
   static  public void Set(){                
        setOpacity();
        image.setViewportWidth(MAP.tileSize);
        image.setViewportHeight(MAP.tileSize);	                      
    }




    static public void scale(final float scale){
         root.setCurrentScale(scale);    //root.getCurrentScale());
     }
     static public void setXY(final float svgpx,final float svgpy){
         svgp.setX(svgpx);
         svgp.setY(svgpy);
     }
    static public void paint(Graphics g,int x, int y){
        sg.bindTarget(g);     
        sg.render(x,y, image);
        sg.releaseTarget();
    }
   
}
