package mobnav.canvas;
/**
 *
 * @author 2cool
 */
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

import mobnav.math.math;
import mobnav.tracks.Track;
import mobnav.tracks.Tracks;


//-########################################################################

//-########################################################################
public class MAP{
    // ########################################################################
static public int redraw=0;
static public int x2bit=0;
static public boolean offline=true;
private int     nMap=0;
private static Point   mapSize=null;
private Point   mapP=new Point();
static final int maxVal=Integer.MIN_VALUE-10;
static private Point	oldGP=new Point(maxVal,maxVal);
private Point   oldP=new Point(maxVal,maxVal);
public final static int  tileSize=128,tileShift=7,tileMask=-128;//8-tileShift;
public int mapTileSize=256,t2t=1;
public static  String EMPTY="EMPTY"+FILE.MAP_E;
public static  String INET="INET";
private static  Point scrSizeInTiles = new Point();
private static Point dShift=new Point();
public Point dShift(){return dShift;}
private  InputStream is=null;
public static  Image out_of_map=null,loading=null;
static public  Image frame=null;
private  static Image[] tileXside,tileYside;
private static int zoom2load=-1;
public static void setZoom2Load(final int zoom){zoom2load=zoom;}
private static int oldTilesOff[]=null;
static private Image t256[];
private int tn=0;
static private int angle=0;
private MAP_SET []mset;
private static MAP_SET ms;

private static boolean no_map=true;
private static boolean inet_map=false;

//private  int ctiles=0;
private int maps;
public long ntiles=0;
//private double rntiles;

static int cnt=0;
static int imgBufSize=0;


public String fname="";
// ###########################################################################
public static final double  EPSG_900913=       2.0037508342789244E7;
static final double  r2EPSG_900913=     0.5/EPSG_900913;
static final double  r128dEPSG_900913=  128.0/EPSG_900913;



static private Point mgp=new Point();

static public  Point getRelevantMapP(final Point gp){  
    Point tmgp=MAP.getMapPointNear(gp, ms.zoom);  
    return tmgp.Sub(mgp).add(cr);            
}



static public boolean Terminator(Point gp){
    int n=(int)EPSG_900913;
    boolean ret=false;
    if (gp.x>n){
        gp.x-=n*2;
     }else if (gp.x<-n){
        gp.x+=n*2;
    }
    if (gp.y>n){
        gp.y=n;
        ret=true;
    }else if (gp.y<-n){
        gp.y=-n;
        ret=true;
    }
    return ret;

 }


static public Point GetLocation(final Point mp,final int zoom){
	   double x=mp.x;
	   double y=mp.y;
	   double rd=1.0/(1<<(zoom+7));
	   x*=rd;
	   y*=rd;
	   x=(x-1)*EPSG_900913;
	   y=(1-y)*EPSG_900913;
	   if (x<-EPSG_900913)
		   x+=EPSG_900913;
	   else if (x>EPSG_900913)
		   x=EPSG_900913-x;
	   return new Point((int)x, (int)y);
	}
	


static public  Point get_relevant_frame_P(final Point gp){  
    return getRelevantMapP(gp).add(dShift);            
}
	static public Point getMapPointNear(final Point gp, final int zoom){
		Point p=getMapPoint(gp,zoom);///////////
	      if (Math.abs(gp.x-oldGP.x)>EPSG_900913){	    	  	    	     	 
	    	  	int n=256<<zoom;
				int x0=Math.abs(p.x-mgp.x);
				int xa=Math.abs(x0-n);
				if (xa<x0){
					if (p.x<mgp.x)
						p.x+=n;
					else
						p.x-=n;
				}
	      }
	      return p;
	}

	
	static public Point getMapPoint(final Point gp, final int zoom){
	    double x=EPSG_900913+gp.x;
	    double y=EPSG_900913-gp.y;
	    double z=1<<zoom;
	    x=x*z*r128dEPSG_900913;
	    y=y*z*r128dEPSG_900913;
	    Point p=new Point (math.approached(x),math.approached(y));	    	    
	    return p;
	}
	static private final double testDist=1000,r_testDist=0.001;
	static private int oldzoom=1000;
	static private int oldRulerGPY=0;
	static private double toPixelsZoom=1;
	static private double toMettersZoom=1;

	static public double getDistInPixels(final double dist, final int zoom){
		int dz=oldzoom-zoom;
		
		if (dz>=0)
			return (toPixelsZoom*dist)*(double)(1<<dz);
		else
			return (toPixelsZoom*dist)*(double)(1>>(-dz));		
	}
	public double getDistInPixels(final double dist){
		return toPixelsZoom*dist;
	}
	public double getDistInMetters(final int pixels){
		return toMettersZoom*(double)pixels;
	}
	static public void updDistK(final Point gp,final int zoom){
	    if (oldzoom!=zoom || Math.abs(oldRulerGPY-gp.y)>60000){
	        oldRulerGPY=gp.y; 
	        oldzoom=zoom;  	        
	        double y=Location.getDistInPoints(Location.GetLocation(gp),testDist);	        
	        toPixelsZoom=(1<<zoom)*r128dEPSG_900913*y*r_testDist;	       	       	        	      
	        toMettersZoom=1.0/toPixelsZoom;
	    }
	}
    
// ###################################################################################
public boolean error=false;

private static void createLoadingTile(){
	if (loading==null || only_cashe!=offline){
		only_cashe=offline;
		loading=Image.createImage(tileSize, tileSize);
		Graphics g=loading.getGraphics();
		g.setColor(0xa0a0a0);	
		 Font font=Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	     g.setFont(font);	     
	     String str=(only_cashe)?"offline":"loading...";
	     g.drawString(str, (tileSize-font.stringWidth(str))>>1, (tileSize-font.getHeight())>>1, 0);		
	}
		
}

private static boolean only_cashe=false;
private static  void createBlankTile(){
	//create out of map image
    if (out_of_map==null){
        int[] rd=new int[tileSize*tileSize];
        int k=0;
        while (k<tileSize*tileSize)
            if (( k&7)==0 && ((k>>tileShift)&7)==0)
                rd[k++]=0x999999;
            else
                rd[k++]=0xFFFFFF;
        out_of_map= Image.createRGBImage(rd, tileSize, tileSize, false);                     
    }
}
private static void fill(){
	Graphics g=frame.getGraphics();
	for (int y=0; y<mapSize.y; y+=tileSize)
		for (int x=0; x<mapSize.y; x+=tileSize)
			g.drawImage(loading, x, y, 0);
}
static private int oldX2bit=x2bit^1;
static private int angleold=100;
static private int canvasMax_x=0,canvasMax_y=0;
private static boolean frame_init(){	
	createLoadingTile();
	boolean ret;
	if (ret=(angle!=angleold || x2bit!=oldX2bit || frame==null || canvasMax_x!=MyCanvas.max_x || canvasMax_y!=MyCanvas.max_y)){
		angleold=angle;		
		oldX2bit=x2bit;
		canvasMax_x=MyCanvas.max_x;
		canvasMax_y=MyCanvas.max_y;
		freeMemory();
		if (angle==Sprite.TRANS_ROT90 || angle==Sprite.TRANS_ROT270){
			max_x=MyCanvas.max_y;
			max_y=MyCanvas.max_x;
		}else{
			max_x=MyCanvas.max_x;
			max_y=MyCanvas.max_y;
		}
		max_x>>=x2bit;
		max_y>>=x2bit;

		createBlankTile();		
		scrSizeInTiles.x=2+(max_x>>tileShift);
	    scrSizeInTiles.y=2+(max_y>>tileShift);
	    tiles=scrSizeInTiles.x*scrSizeInTiles.y;
	    mapSize=new Point(scrSizeInTiles.x<<tileShift,scrSizeInTiles.y<<tileShift);
	    frame=Image.createImage(mapSize.x,mapSize.y);
	    System.out.println("map size is "+mapSize.x+" X "+mapSize.y+"; angle="+angle);
	  //  if (!no_map){
	        tileXside=new Image[scrSizeInTiles.x];
	        tileYside=new Image[scrSizeInTiles.y];
	   // }
	    STEPY=tileSize/(scrSizeInTiles.y+1);//30;//scrSizeInTiles.x
	    STEPX=tileSize/(scrSizeInTiles.x+1);//42;
	  fill();   
	  
	}
	return ret;
	 
}

///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////
public  MAP(final String mapFileName){  
	
	fname=mapFileName;
    mapError="";
    cnt++;
  
        ;        
        if (error=load(mapFileName))
            return;
        try{        	     	
	        frame_init();        	    		      
            angle=0;                       
            //------------------
            if (t2t!=0){
                int like=(scrSizeInTiles.x*scrSizeInTiles.y+scrSizeInTiles.x+scrSizeInTiles.y+1)>>2;
                imgBufSize=Storage.mem_test-like;
                if (imgBufSize>like)
                    imgBufSize=like;
                t256=new Image[imgBufSize];
                oldTilesOff=new int[imgBufSize];
                for (int i=0; i<imgBufSize; i++){
                    oldTilesOff[i]=-1;
                    t256[i]=null;
                }
            }
            
            //------------------                        
        if (zoom2load >= 0){
    		this.setZoom(zoom2load);
    		zoom2load=-1;
        }
            //------------------------------------------------------------                        
      }catch (OutOfMemoryError e){Storage.mapError("Map OutOfMemory"+e,false);}
       catch (Exception e){Storage.mapError("Map Error "+e.toString(),false);}
}
// ###########################################################################
public int getZoom(){return ms.zoom;}
public boolean Contains(final Point gp){
    Point p0=GetLocation(mset[0].ss[0].p0.Mul(mapTileSize, mapTileSize),mset[0].zoom);
    Point p1=GetLocation(mset[0].ss[0].p1.Mul(mapTileSize, mapTileSize),mset[0].zoom);
    return gp.in_yInverse(p0, p1);
}

class MAP_SET{
    public int zoom;
 //   int shift;
    private SUB_SET[]ss;
    private int last[]={0,1,2,3};
    private int lastI=0;
    //public int last=0;
    // -----------------------------------------------------------------------

//----------------------------------------------------
    public Image getTile(final Point mt){ //  #############  
        if (no_map)
            return out_of_map;
        else if (inet_map) 
            return InetMaps.getTile(mt.x,mt.y,zoom);
        if (ss==null)
    		return null;
        Image t;        
        if (ss.length>4){
             if ((t=ss[last[lastI]].getTile(mt))!=null)
                return t;
             lastI++;
             lastI&=3;
              if ((t=ss[last[lastI]].getTile(mt))!=null)
                return t;
             lastI++;
             lastI&=3;
              if ((t=ss[last[lastI]].getTile(mt))!=null)
                return t;
             lastI++;
             lastI&=3;
              if ((t=ss[last[lastI]].getTile(mt))!=null)
                return t;
             for (int n=0; n<ss.length; n++){
                 if (n!=last[0] && n!=last[1] && n!=last[2] && n!=last[3] &&
                         (t=ss[n].getTile(mt))!=null){
                    last[lastI]=n; //2
                    return t;
                 }
            }
        }else{        	
            if ((t=ss[last[0]].getTile(mt))!=null)
                return t;
            for (int n=0; n<ss.length; n++)
                if ( n!=last[0] && (t=ss[n].getTile(mt))!=null){
                    last[0]=n;
                    return t;
                }
            }
            return null;
    }

    // -----------------------------------------------------------------------
    public void loadNull(final int z){
		zoom=z;                       
		ntiles=0;             
		ss=null;		      
    }
    public void loadMap() throws IOException{
        zoom=(int)is.read();
        begin_of_indexes++;
        int nsubsets=readInteger();
        begin_of_indexes+=4;
        ss=new SUB_SET[nsubsets];
        for (int n=0; n< nsubsets; n++){
            (ss[n]=new SUB_SET()).load(this);           
        }
       
    }
    }

class SUB_SET{
    public Point p0,p1;
//    public int[] tilesOff;
    public long tilesIndexes;
   
    // -----------------------------------------------------------------------
    /*
    private boolean Include(final Point p){
        return p.x>=p0.x && p.x<p1.x && p.y>=p0.y && p.y<p1.y;
    }
    */
    // -----------------------------------------------------------------------
    private int  getTileIndex(final Point p){
        int y=p.y-p0.y;
        int x=p.x-p0.x;
        return y*(p1.x-p0.x)+x;
    }
    // -----------------------------------------------------------------------
    private Image fillTile() throws IOException{
        byte []b=new byte[3];
        is.read(b,0,3);
        int rgb=((0xff&b[0])<<16)|((0xff&b[1])<<8)|(0xff&b[2]);
        Image ti=Image.createImage(tileSize,tileSize);
        Graphics g=ti.getGraphics();
        g.setColor(rgb);
        g.fillRect(0, 0, tileSize, tileSize);
        return ti;
    }
    // ########################################################################
private Image getTile128(final Point p){
    
    
    
    Image ret=null;
    if (p.x>=p0.x && p.x<p1.x && p.y>=p0.y && p.y<p1.y){
        try{
        long i=getTileIndex(p);
        is.reset();
        is.skip(tilesIndexes+(i<<2));
        int tilesOff0=readInteger();
        int tilesOff1=readInteger();
        is.reset();
        is.skip(tilesOff0);
        int tsize=(int)(tilesOff1-tilesOff0);
        //if (tsize==0)
          //  return null;
        if (tsize==3){
           ret= fillTile();
        }else{
            byte [] b = new byte[tsize];
            int length = is.read(b, 0, tsize);
           // System.out.println("tsize="+tsize);
            ret= Image.createImage(b, 0, length);
        }
        }catch (IOException ex) {ret=null;MyCanvas.SetErrorText("IMG_ERROR "+ex);}
    }
   return ret;
}
// ############################################################################
    public Image getTile(final Point mt){
        if (t2t==0)
            return getTile128(mt);
       Point p=new Point(mt.x>>t2t, mt.y>>t2t);
       
       if (p.x>=p0.x && p.x<p1.x && p.y>=p0.y && p.y<p1.y){ 
            try{            
                long i=getTileIndex(p);
                is.reset();
                is.skip(tilesIndexes+(i<<2));
                int tilesOff0=readInteger();
                Image t256l=null;
                int n=0;
                while (imgBufSize>n){
                    if (tilesOff0==oldTilesOff[n]){
                        t256l=t256[n];
                        break;
                    }   
                    n++;
                }
                if (t256l==null){
                    if (++tn==imgBufSize)
                        tn=0;
                    int tilesOff1=readInteger();
                    is.reset();
                    is.skip(tilesOff0);
                    int tsize=(int)(tilesOff1-tilesOff0);
                    if (tsize==0)
                        return null;
                    if (tsize==3){
                       return fillTile();
                    }else{
                        byte [] b = new byte[tsize];
                        int length = is.read(b, 0, tsize); 
                        t256l=Image.createImage(b, 0, length);
                        oldTilesOff[tn]=tilesOff0;
                        t256[tn]=t256l;
                    }                                            
                }
                int x=(p.x<<8)-(mt.x<<tileShift);
                int y=(p.y<<8)-(mt.y<<tileShift);
                Image ti=Image.createImage(tileSize,tileSize);
                ti.getGraphics().drawImage(t256l, x, y, 0);
                return ti;
            }catch (IOException ex) {MyCanvas.SetErrorText("IMG_ERROR "+ex);}
        }
       return null;
    }
    // -----------------------------------------------------------------------
   
    // -----------------------------------------------------------------------

    public void load(final MAP_SET ms) throws IOException{
        int x,y;
        x=readInteger();
        y=readInteger();
        p0=new Point(x,y);
        x=readInteger();
        y=readInteger();
        begin_of_indexes+=16;
        p1=new Point(x,y);
        int sstiles=(p1.x-p0.x)*(p1.y-p0.y);
        tilesIndexes=begin_of_indexes;
        begin_of_indexes+=((sstiles+1)<<2);
        is.reset();
        is.skip(begin_of_indexes);
    }

}
// ############################################################################
public static final int MAX_ZOOM=19;

private boolean no_Map(){
	Interface.RETURN2MAP.Off();
     mapTileSize=tileSize;
     t2t=0;                    
    maps=MAX_ZOOM+1;
    ntiles=0;
    mset=new MAP_SET[maps];
    for (int i=0,zoom=0; zoom<=MAX_ZOOM; i++,zoom++){
        ms=mset[i]=new MAP_SET();
        ms.zoom=zoom;
        ms.ss=null;
   }
   nMap=0;
   update();
   return false;
}
public void closeMapInputStream(){
        try {
            if (is!=null)
                is.close();
        } catch (Exception ex) {}
}


//###########################################################################

public String mapError="";
long begin_of_indexes;
private boolean load(String mapFileName) {
    
    boolean error=false;
    try{    
        if (no_map=mapFileName.equals(EMPTY))
            return no_Map();
         if (inet_map=mapFileName.startsWith(INET, 0)){
        	 String name=mapFileName.substring(INET.length(),mapFileName.length()-FILE.MAP_E.length());
        	 for (int i=0; i<InetMaps.im.length; i++){
        		 if (name.equals(InetMaps.im[i].getName())){
        			 InetMaps.type=i;
        			 break;
        		 }
        	 }
            return no_Map();
         }

         Interface.RETURN2MAP.On();
        if (mapFileName.charAt(0)=='/'){
            is = getClass().getResourceAsStream(mapFileName);
        }else{
            FileConnection fc = (FileConnection) Connector.open(mapFileName);
            is = fc.openInputStream();
        }
        if (is.markSupported())
            is.mark(0xffffffff);
        int foo=                readInteger();
        
        if ((foo&0xFFFFF)!=0x2C001){
            mapError="NOT A MAP";
            return error=true;
        }
        int set=foo>>20;
        if ((set&1)==1){
            mapTileSize=128;        
            t2t=0;
        }else{
            mapTileSize=256;
            t2t=1;
        }                          
        maps =                      (char)is.read();
        ntiles=                     readInteger();
      //  rntiles=1.0/(double)ntiles;
        begin_of_indexes=           readInteger();
       // System.out.println("BEGIN "+begin_of_indexes);
        is.reset();
        is.skip(begin_of_indexes);
        mset=new MAP_SET[MAX_ZOOM+1];
      //  BufferedRead br=new BufferedRead(is);
        int i=0;
        for (; i<maps; i++){
            ms=mset[i]=new MAP_SET();
            ms.loadMap();
       }
        
       int z=mset[maps-1].zoom+1;
       maps+=MAX_ZOOM-z+1;
       for (; z<=MAX_ZOOM; z++,i++){
    	   ms=mset[i]=new MAP_SET();
    	   ms.loadNull(z);
       }
    	  
       ms=mset[0];
       update();

    } catch (OutOfMemoryError e){
        mapError="MAP_LOADING_ERROR - OUT_OF_MEMORY";
        error=true;
    }
    catch (Exception ex){
        mapError="MAP_LOADING ERROR "+ex;
        error=true;
    }
    return error;
}
// ############################################################################


public Point center(){
	Point p0=new Point(ms.ss[0].p0);
	Point p1=new Point(ms.ss[0].p1);
	for (int i=1; i<ms.ss.length;i++){
		SUB_SET m=ms.ss[i];
		if (p0.x>m.p0.x)
			p0.x=m.p0.x;
		if (p0.y>m.p0.y)
			p0.y=m.p0.y;
		if (p1.x<m.p1.x)
			p1.x=m.p1.x;
		if (p1.y<m.p1.y)
			p1.y=m.p1.y;		
	}
    
    Point p=p0.add((p1.Sub(p0)).Mul(0.5, 0.5));
    return  GetLocation(p.Mul(mapTileSize, mapTileSize), ms.zoom);

   
}
// #############################################################################
// #############################################################################
//public int sizeX(){return m.size.x;}
//public int sizeY(){return m.size.y;}
public int getScale(){return nMap;}

public void setZoom(int zoom){
	if (zoom>MAX_ZOOM)
		zoom=MAX_ZOOM;
    if (zoom!=ms.zoom)
        for (int i=0; i<maps; i++)
            if (mset[i].zoom==zoom){
                nMap=i;
                update();
                break;
            }
    
}

public int getNextZoom(final int addZoom){
    int nmap=nMap+addZoom;
    if (nmap>maps-1)
        nmap=maps-1;
    if (nmap<0)
        nmap=0;
    return (mset[nmap].zoom>MAX_ZOOM)?MAX_ZOOM:mset[nmap].zoom;

}
public void setScale(int n){
   if (n<0)
       n=0;
   if (n>=maps)
       n=maps-1;
    if (ms.zoom!=mset[n].zoom){
        nMap=n;
        update();
    }
}
/*
public boolean increaseScale(){
    if (nMap<maps-1){
        nMap++;
        reDraw_();
      //  System.out.println(nMap);
        return true;
    }

    return false;
}
public boolean reduceScale(){
    if (nMap>0){
        nMap--;
        reDraw_();
       // System.out.println(nMap);
        return true;
    }
    return false;
}
 * 
 */
public boolean rotate(final int angle){
    
        if (MAP.angle!=angle){
            MAP.angle=angle;
            update();
            return true;
    
    }
    return false;
}
static public int getRotateAngle(){return angle;}

// ################################################################

//public Point imgSize(){return imgSize;}
public  MAP(final Point size, Location loc, double dY,double dX){}

// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
public static void freeMemory(){
    resetPrefetch();   
    tileXside=new Image[scrSizeInTiles.x];
    tileYside=new Image[scrSizeInTiles.y];
    for (int i=0; i<imgBufSize && oldTilesOff!=null; i++){
            oldTilesOff[i]=-1;
            t256[i]=null;                
    }
}
//---------------------------------------------------------------------------
// ################################################################
// ################################################################
// ################################################################
private Image zoomTile(Image simg, int zoom, Point b){
    if (zoom>=tileShift){
        int rgb[]={0};
        simg.getRGB(rgb, 0, tileSize, b.x, b.y, 1, 1);
        Image out=Image.createImage(tileSize, tileSize);
        Graphics g=out.getGraphics();
        g.setColor(rgb[0]);
        g.fillRect(0, 0, tileSize, tileSize);
        return out;
    }
    Image img=Image.createImage(tileSize>>zoom, tileSize);    
    Graphics g=img.getGraphics();
    int len=tileSize>>zoom;
    for (int y=b.y; y<b.y+len; y++){
        Image strimg=Image.createImage(simg,b.x, y, len, 1, 0) ;
        for (int n=0; n< (1<<zoom); n++)
            g.drawImage(strimg, 0, ((y-b.y)<<zoom)+n, 0);
    }
    Image out=Image.createImage(tileSize, tileSize);
    g=out.getGraphics();
    for (int x=0; x<len; x++){
        Image climg=Image.createImage(img, x, 0, 1, tileSize, 0);
        for (int n=0; n<(1<<zoom); n++)
            g.drawImage(climg, (x<<zoom)+n, 0, 0);
    }
    return out;
}

   // ################################################################
    // загружаем экрантый тайл tx,ty тйлом карты mtx,
private Image getTileZ(int n, final int mtx, final int mty){
    while (n>=0){
        int zoom=ms.zoom-mset[n].zoom;
        int x=mtx>>zoom;
        int y=mty>>zoom;
        Point p=new Point(mtx-(x<<zoom),mty-(y<<zoom));
        p.x*=tileSize>>zoom;
        p.y*=tileSize>>zoom;
        Image ti=mset[n].getTile(new Point(x,y));
        if (ti!=null){

            return zoomTile(ti,zoom,p);
        }
        n--;
    }
    return out_of_map;

}
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
private Image getTile( int mtX, final int mtY){

	int n=(2<<ms.zoom)-1;	//360 градусов соединяем с 0 грудусами )
	if (mtX > n)
		mtX = 0;
	else if (mtX < 0)
		mtX = n;
	
    Image ti=ms.getTile(new Point(mtX,mtY));
    if (ti!=loading || !oldGP2.equals(NAVI.getGP()))
    	drawed++;
    
    if (ti==null){
        ti=getTileZ(nMap-1,mtX,mtY);
    }

    Track t=Tracks.GetFirst();
    
    if (t!=null && MyCanvas.showTrack){
        Image tti=Image.createImage(tileSize, tileSize);
        Graphics g=tti.getGraphics();
        g.drawImage(ti, 0,0,0);
        do{
            t.paint(g,mtX<<tileShift,mtY<<tileShift);
        }while( (t=Tracks.GetNext())!=null) ;                                        
        return tti;
    }

    return ti;
}
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
private  void getTileImg(final Image img,final int tx,final int ty,final int count){
   // System.out.println("setTIleImg "+tx+","+ty);
  //  synchronized(tileS){
        frame.getGraphics().drawImage(img,tx<<tileShift, ty<<tileShift, 0);
  //  }
    if (++tilesCount == count)
        synchronized(MyCanvas.update){ MyCanvas.update.notify(); }
  }
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
private  void drawTile(final int tx,final int ty,final int mtx, final int mty,final int count){

        Image ti=getTile(mtx,mty);
       
        
     //   synchronized(tileS){
            frame.getGraphics().drawImage(ti,tx<<tileShift, ty<<tileShift, 0);
     //   }
        if (++tilesCount == count)
            synchronized(MyCanvas.update){ MyCanvas.update.notify(); }

}
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
private void reloadAll(final int xx,final int xy, final int yx, final int yy){        
    freeMemory();    
    Tracks.SetScale(this);      
    tilesCount=0;
    for (int y=0;y<scrSizeInTiles.y;y++)
        for (int x=0; x<scrSizeInTiles.x;x++)
            drawTile(x,y,mapXt()+x*xx+y*xy,mapYt()+x*yx+y*yy,scrSizeInTiles.y*scrSizeInTiles.x);  
    Labels.drawIcons(frame.getGraphics());
}
   // ################################################################

   // ################################################################

   // ################################################################

// ################################################################
private void reloadTitles(){
    oldP.x=mapP.x&tileMask;
    oldP.y=mapP.y&tileMask;
    dShift.x = (mapP.x-oldP.x);
    dShift.y = (mapP.y-oldP.y);
    reloadAll(1,0,0,1);
}


 public int negangle(){
	 switch (angle){	 
	 case Sprite.TRANS_ROT270:return Sprite.TRANS_ROT90;
	 case Sprite.TRANS_ROT90:return Sprite.TRANS_ROT270;
	 default:return angle;
	 }
 }
// ##################################################################
private void ShiftImg(){
//    synchronized(tileS){
   // System.out.println("ShiftImg");
     int x0=0,x1=0,y0=0,y1=0,sx=mapSize.x,sy=mapSize.y;
    if (tileSize <= dShift.x){
         x0=tileSize;
         sx-=tileSize;
    }else if (dShift.x < 0){
       sx-=tileSize;
       x1=tileSize;
    }
    if (tileSize <= dShift.y){
        y0=tileSize;
        sy-=tileSize;
    }else if (dShift.y < 0){
        sy-=tileSize;
        y1=tileSize;
    } 
    
     frame.getGraphics().copyArea(x0,y0,sx ,sy,x1,y1, 0);     
     if (x0!=x1 && y0!=y1){
        // ShiftPrefetch(x1-x0, y1-y0);
         resetPrefetch();
         System.out.println("resetPrefetch");
    }
  //  }
}
// ################################################################
private int tilesCount;
private void    shiftTiles2Bottom(){
  // System.out.println("bottom");
    tilesCount=0;
     for (int x=0; x<scrSizeInTiles.x; x++)
         if (x+1<=(-index.x))
            getTileImg(tileXside[x],x,0,scrSizeInTiles.x);
         else
            drawTile(x,0,mapXt()+x,mapYt(),scrSizeInTiles.x);
    dShift.y += tileSize;
    resetPrefetch();
}
Point shiftPrefetch=new Point();
// ################################################################
private void    shiftTiles2Up(){
   // System.out.println("up");
    tilesCount=0;
    for (int x=0;x<scrSizeInTiles.x; x++)
        if (x+1<=index.x)
          getTileImg(tileXside[x], x,scrSizeInTiles.y-1,scrSizeInTiles.x);
        else
          drawTile(x,scrSizeInTiles.y-1,  mapXt()+x,mapYt()+scrSizeInTiles.y-1,scrSizeInTiles.x);

    dShift.y -= tileSize;
    //resetPrefetch();
 }
// ################################################################
private void    shiftTiles2Left(){
   // System.out.println("left");
    tilesCount=0;
     for (int y=0; y<scrSizeInTiles.y; y++)
         if (y+1<=index.y)
            getTileImg(tileYside[y],scrSizeInTiles.x-1,y,scrSizeInTiles.y);
         else
            drawTile(scrSizeInTiles.x-1,y,mapXt()+scrSizeInTiles.x-1,mapYt()+y,scrSizeInTiles.y);
    dShift.x -= tileSize;
   // resetPrefetch();
}
// #############################################################################
private void    shiftTiles2Right(){
    //System.out.println("right");
    tilesCount=0;
    for (int y=0;y<scrSizeInTiles.y; y++)
        if (y+1<=(-index.y))
            getTileImg(tileYside[y],0,y,scrSizeInTiles.y);
        else
            drawTile(0,y,mapXt(),mapYt()+y,scrSizeInTiles.y);
        dShift.x += tileSize;
        //resetPrefetch();
}
// ################################################################

final double step=EPSG_900913/128.0; //128 это 256/2 и к размеру тайла отношения не имеет
public   Point relevantR(Point p){
	Point np=new Point((int)(p.x*step)>>ms.zoom,(int)(p.y*step)>>ms.zoom);
	return np;}

public   int   Scale(int x, final int maxZoom){
    int z=maxZoom-ms.zoom;
    if (z<0)
        return x>>(-z);
    else
        return x<<z;
}
public   int   rScale(int x, final int maxZoom){ 
	int z=maxZoom-ms.zoom;
    if (z<0)
        return x<<(-z);
    else
        return x>>z;
}
public   Point Scale(Point p, final int maxZoom){
    int z=maxZoom-ms.zoom;
    if (z<0){
    	z=-z;
        return new Point(p.x>>z,p.y>>z);
    }else
        return new Point(p.x<<z,p.y<<z);
    }
public   Point rScale(Point p, final int maxZoom){
    int z=maxZoom-ms.zoom;
    if (z<0){
    	z=-z;
        return new Point(p.x<<z,p.y<<z);
    }else
        return new Point(p.x>>z,p.y>>z);
    }

// ################################################################
public void close(){if (is!=null)try {is.close();} catch (IOException ex) {}}


//85,3.067727,N,180,0.000000,W, grid,   ,           ,           ,N
//85,3.067727,S,180,0.000000,E, gr



// ################################################################

private void update(){
    ms=mset[nMap];
    oldGP.x=oldP.x=oldGP.y=oldP.y=maxVal;
    frame_init();
    //createLoadingTile();
}
// ################################################################
private int readInteger() throws IOException{
    byte[]buf=new byte[4];
   
        is.read(buf);

        int mask=0xff;
        return mask&(int)buf[3]|(mask&(int)buf[2])<<8|(mask&(int)buf[1])<<16|(mask&(int)buf[0])<<24;
    }
/*
private long readLong() throws IOException{
    long l=0;
     byte[] buf=new byte[8];
    is.read(buf);
    long mask=0xff;
    l=  (long)buf[0]&mask;
    l|=((long)buf[1]&mask)<<8;
    l|=((long)buf[2]&mask)<<16;
    l|=((long)buf[3]&mask)<<24;
    l|=((long)buf[4]&mask)<<32;
    l|=((long)buf[5]&mask)<<40;
    l|=((long)buf[6]&mask)<<48;
    l|=((long)buf[7]&mask)<<56;

    return l;

}

private double readDouble() throws IOException{
    return Double.longBitsToDouble(readLong());
 }
 */
// ################################################################
//public Point getGlobPoint(final Location l){return glob.Sub(l.GetPoint());}
//public Point getGlobPoint_(final Point p){return m.glob.Add(p.Mul(m.d1X, m.d1Y));}
//private Point getPoint(final Location l, MAPP m){return (l.GetPoint().Sub(m.glob)).Mul(m.dX,m.dY);}
//public Point getPoint(final Location l){return (l.GetPoint().Sub(glob)).Mul(dX,dY);}//
//public Point getPoint(final Point p){return (p.Sub(glob)).Mul(dX, dY);}
//public Location GetLocation(final Point p){
//    return  new Location(getGlob(p));
//}
//public Point getGlob(final Point p){
//    return glob.Add(p.Mul(d1X, d1Y));
//}
final int X=0;
final int Y=0;
// ###########################################################
// ##################################################################
static private void resetPrefetch(){
    //System.out.println("resetPre");
    vector.y=vector.x=index.y=index.x=0;
}
private void resetPrefetchX(){
   // System.out.println("resetPreX");
    vector.y=index.x=0;
}
private void resetPrefetchY(){
   // System.out.println("resetPreY");
    vector.x=index.y=0;
}
// #############################################################################
private int mapYt(){return mapP.y>>tileShift;}
private int mapXt(){return mapP.x>>tileShift;}
private  static int STEPY;//=30;//scrSizeInTiles.x
private static  int STEPX;//=42;
static private Point index=new Point();
private void prefetch(){ //if (true)return;
   // System.out.println("prefetch in "+index.x+" "+index.y);
    try{
    // if (false)
    if ( Math.abs(index.x)<scrSizeInTiles.x ){
        if (vector.y>STEPY){//down
            vector.y=0;
            if (index.x<0)
                resetPrefetchX();
         //   System.out.println("+dShift.y="+dShift.y);
            tileXside[index.x]=getTile(mapXt()+index.x++ ,mapYt()+scrSizeInTiles.y);
        }else if (vector.y<-STEPY){//up
            vector.y=0;
            if (index.x>0)
                resetPrefetchX();
         //   System.out.println("-dShift.y="+dShift.y);
            tileXside[-index.x]=getTile(mapXt()-index.x--,mapYt()-1);
        }
    }
    //if (false)
    if ( Math.abs(index.y)<scrSizeInTiles.y){
        if (vector.x>STEPX){//rigth
            vector.x=0;
            if (index.y<0)
                resetPrefetchY();
            tileYside[index.y]=getTile(mapXt()+scrSizeInTiles.x,mapYt()+index.y++);
        }else if (vector.x<-STEPX){//left
            vector.x=0;
            if (index.y>0)
                resetPrefetchY();
            tileYside[-index.y]=getTile(mapXt()-1,mapYt()-index.y--);
        }
    }
    
    }catch (Exception e){MyCanvas.SetErrorText("prefetch "+e.toString());}
   // System.out.println("prefetch out "+index.x+" "+index.y);
}

// #########################################################################
private static Point vector=new Point();
private void tilesShift(){
    try{
    boolean prefetch=true;
    if (tileSize <= dShift.x){
        prefetch=false;
        ShiftImg();
        shiftTiles2Left();
    } else if (dShift.x < 0){
        if (prefetch)
            ShiftImg();
        prefetch=false;
        shiftTiles2Right();
    }
    if (tileSize <= dShift.y){
        if (prefetch)
            ShiftImg();
        prefetch=false;
        shiftTiles2Up();
    } else if (dShift.y < 0){
        if (prefetch)
            ShiftImg();
        prefetch=false;
        shiftTiles2Bottom();
    }
    if (prefetch)
        prefetch();
    else{
       resetPrefetch();
       Labels.drawIcons(frame.getGraphics());
    }
    }catch (Exception e){MyCanvas.SetErrorText("shift"+e.toString());}
}
private boolean updatePosition(){
    boolean ret=false;
    
    mapP=getMapPoint(NAVI.getGP(),ms.zoom);
   // System.out.println(mapP.x);
     
    mapP.x-=cr.x;
    mapP.y-=cr.y;
    
    int dx=mapP.x - oldP.x;
    int dy=mapP.y - oldP.y;        
    dShift.x += dx;               
    dShift.y += dy;

    if (
        dShift.x>=(tileSize<<1)  ||
        dShift.y>=(tileSize<<1)  ||
        dShift.x<=(-tileSize)    ||
        dShift.y<=(-tileSize)  //  ||
        //((tileSize<=dShift.x || dShift.x<0) && (tileSize<=dShift.y || dShift.y<0))
    ){
        reloadTitles();
        resetPrefetch();
        ret=true;
    } else{
        vector.x+=dx;
        vector.y+=dy;
        tilesShift();
     }
    
    
    oldP.x = mapP.x;
    oldP.y = mapP.y;
    return ret;
}

private boolean the_scale_is_changed=false;
public Image zoomMap(int zoom){
	the_scale_is_changed=true;
	freeMemory();
	if (MAP.x2bit==1){
		zoom++;
		if (zoom==0)
			return Image.createImage(frame,dShift.x, dShift.y, max_x, max_y, 0);								
	}
//    synchronized(tileS){      
    boolean zoomf=zoom<0;
    zoom=Math.abs(zoom);
    int zx=max_x>>zoom;
    if (zx<1)
        zx=1;
    int zy=max_y>>zoom;
    if (zy<1)
        zy=1;
    int mx=1<<zoom;
    if (mx>max_x)
        mx=max_x;
    int my=1<<zoom;
    if (my>max_y)
        my=max_y;
    
    
    
    
    if (zoomf){
        //zoom=-zoom;        
    //    int zx=MAX_X>>zoom;
      //  int zy=MAX_Y>>zoom;
        Image t=Image.createImage(zx,max_y);
        Graphics gt=t.getGraphics();
        
        for (int x=0; x<zx; x++)
           gt.drawRegion(frame, dShift.x+x*mx, dShift.y, 1, max_y, 0, x, 0, 0); 
        
        Image mi=Image.createImage(zx, zy);
        gt=mi.getGraphics();  
        for (int y=0; y<zy; y++)
            gt.drawRegion(t,  0, y*my,  zx, 1, 0, 0, y, 0);
        return mi;                
    }else if (zoom>0){
        //int zx=MAX_X>>zoom;
        //int zy=MAX_Y>>zoom;
        Image img=Image.createImage(zx, max_y);    
        Graphics g=img.getGraphics();
        int xpos=(max_x-zx)>>1;
        int ypos=(max_y-zy)>>1; 
        int y;
        Image strimg=null;
        
        for (y=ypos; y<(ypos+zy); y++){
            strimg=Image.createImage(frame,xpos+dShift.x, y+dShift.y, zx, 1, 0) ;
        
            for (int n=0; n< my; n++)
                g.drawImage(strimg, 0, ((y-ypos)*my)+n, 0);
        }
        Image out=Image.createImage(max_x, max_y);
        g=out.getGraphics();
        for (int x=0; x<zx; x++){
            Image climg=Image.createImage(img, x, 0, 1, max_y, 0);
            for (int n=0; n<mx; n++)
                g.drawImage(climg, x*mx+n, 0, 0);
        }
        return out;      
    }
    return null;
  //  }
}
boolean tileonScreen(final int x, final int y, final int z){    
    int dx=x-mapXt();
    int dy=y-mapYt();
    return ms.zoom==z && dx>=-2 && dx<=scrSizeInTiles.x && dy>=-2 && dy<=scrSizeInTiles.y;
}
//final private Object tileS=new Integer(0);
// ########################## PAINT #################################
public boolean inProc=false;
private int drawed=0;
public static Point oldGP2=new Point(maxVal,maxVal);
private static int tiles=0;


public static boolean force=false;
public void redrawtt(Graphics g){	
	if (frame_init())
		paint(g);
	else{
		drawMap(g);
		force=true;
	}
}
static public Point cr=new Point();
static public int max_x=-1, max_y=-1;
static public void drawMap(Graphics g){		
	if (MAP.x2bit==1){										
	    Image img=Image.createImage(max_x, max_y<<1);    
	    Graphics ig=img.getGraphics();
	    for (int y=0; y<max_y; y++){
	        Image strimg=Image.createImage(frame,dShift.x, dShift.y+y, max_x, 1, 0) ;
	        int yy=y+y;
	        ig.drawImage(strimg, 0, yy,   0);
	        ig.drawImage(strimg, 0, yy+1, 0);
	    }
	    Point p=new Point(1,0).rotate90(angle);

	    for (int x=0; x<max_x; x++){
	        Image climg=Image.createImage(img, x, 0, 1, max_y<<1, 0);
	        int xx=x<<1;
	        p=new Point(xx,0).rotate90(angle);
	        g.drawRegion(climg, 0, 0, 1, max_y<<1, angle, p.x, p.y, 0);
	        p=new Point(xx+1,0).rotate90(angle);
	        g.drawRegion(climg, 0, 0, 1, max_y<<1, angle, p.x, p.y, 0);	        
	    }	
}else
	g.drawRegion(frame, dShift.x,dShift.y, max_x, max_y, angle, 0,0, 0);
}
// #################################################################################
 public   void paint(Graphics g){
	 if (redraw>0){
		 update();
		 redraw=0;
	 }
	 

     cr.x=((max_x>>1)-(NAVI.dp.x>>MAP.x2bit));
     cr.y=((max_y>>1)+(NAVI.dp.y>>MAP.x2bit));

		
		drawed=0;
		inProc=true;  
	    if (only_cashe)
	    	force=true;

	    if (! oldGP.equals(NAVI.getGP())){
	    	if (oldGP.x!=maxVal)
	    		force=true;
	    	oldGP.Set(NAVI.getGP());  
	    	mgp=MAP.getMapPoint(oldGP, ms.zoom);
	        updatePosition();
	        Labels.search(this);
	     }
	    //System.out.println(drawed==tiles);
	    if (force || the_scale_is_changed==false || drawed==tiles){
	    	the_scale_is_changed=false;
	       // synchronized(tileS){
	    	drawMap(g);
	       // }        
	        Labels.draw(g);    
	     }
	    force=false;
	    inProc=false;
	    if (inet_map && no_map==false){
	    	int x=(MyCanvas.max_x>>1)-17;
	    	g.drawImage(InetMaps.im[InetMaps.type].getIcon(), x, 1, 0);
	    	if (only_cashe){
	    		g.setColor(0xff0000);
	    		g.drawLine(x, 1, x+16, 17);
	    	}
	    }
	    oldGP2.Set(NAVI.getGP());
}

// #########################################################################

}
