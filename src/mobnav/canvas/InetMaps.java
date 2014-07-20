package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;

import java.util.Stack;
import javax.microedition.io.file.*;



 /////////////////////////////////////////////////////////////////////////////////////////
 interface INET_MAPS {	
	static public int cnt=0;
	static public String png=".png",jpg=".jpg";
	public String getReferer();
	public String getName();
	public String getExt();
	public Image getIcon();
	public String getUrl(final int x,final int y,final int z);
	
}
 ///////////////////////////////////////////////////////////////////////////////////////// 
class GoogleMap implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return null;}
	public String getName(){return "googleMaps";}	
	public String getExt(){return png;}
	public Image getIcon(){return Interface.googleIcon;}
	public String getUrl(final int x,final int y,final int z){
		cnt++;
		String url="http://mt" + Integer.toString(cnt & 1)+".google.com/vt/lyrs=m@146000000&hl=ru&x=";
	    url+=Integer.toString(x)+"&y="+Integer.toString(y)+"&z="+Integer.toString(z)+"&s="+"Galileo".substring(0, cnt&7);
	    return url;
	}
}
/////////////////////////////////////////////////////////////////////////////////////////
class GoogleEarth implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return null;}
	public String getName(){return "googleEarth";}	
	public String getExt(){return jpg;}
	public Image getIcon(){return Interface.googleIcon;}
	
	public String getUrl(final int x,final int y,final int z){
		cnt++;
		String url="http://khm"+Integer.toString(cnt&1)+".google.com/kh/v=78&x=";
		url+=Integer.toString(x)+"&y="+Integer.toString(y)+"&z="+Integer.toString(z)+"&s="+"Galileo".substring(0, cnt&7);  
		return url;
	}
}
/////////////////////////////////////////////////////////////////////////////////////////
class GoogleTerrian implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return null;}
	public String getName(){return "GoogleTerrian";}	
	public String getExt(){return jpg;}
	public Image getIcon(){return Interface.googleIcon;}
	public String getUrl(final int x,final int y,final int z){
		cnt++;
		String url="http://mt"+Integer.toString(cnt&1)+".google.com/vt/lyrs=t@127,r@160000000&hl=en&src=api&x=";
		url+=Integer.toString(x)+"&y="+Integer.toString(y)+"&z="+Integer.toString(z)+"&s="+"Galileo".substring(0, cnt&7);
		return url;
	}
}
/////////////////////////////////////////////////////////////////////////////////////////
class MicrosoftEarthAndLabels implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return null;}
	public String getName(){return "Bing Bird's eye";}	
	public String getExt(){return jpg;}
	public Image getIcon(){return Interface.bingIcon;}
	public String getUrl(final int xx,final int yy,final int z){
		int x=xx;
		int y=yy;
		cnt++;
        if (z == 0)
            return new GoogleEarth().getUrl(x, y, z);
        String s = "";
        for (int i = 0; i < z; i++)
        {
            s = Integer.toString(((y & 1) * 2) + (x & 1)) + s;
            x >>= 1;
            y >>= 1;
        }
        return "http://ecn.t" + Integer.toString(cnt&3) + ".tiles.virtualearth.net/tiles/h" + s + ".jpeg?g=668&mkt=en-us&n=z";    
	}
}
//----------------------------------------------------------------------------------------------
class MicrosoftMap implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return null;}
	public String getName(){return "Bing Road";}	
	public String getExt(){return jpg;}
	public Image getIcon(){return Interface.bingIcon;}
	public String getUrl(final int xx,final int yy,final int z){
		int x=xx;
		int y=yy;
		cnt++;
        if (z == 0)
            return new GoogleEarth().getUrl(x, y, z);
        String s = "";
        for (int i = 0; i < z; i++)
        {
            s = Integer.toString(((y & 1) * 2) + (x & 1)) + s;
            x >>= 1;
            y >>= 1;
        }
        //http://ecn.t3.tiles.virtualearth.net/tiles/r1123?g=784&mkt=en-us&lbl=l1&stl=h&shading=hill&n=z
        return "http://ecn.t" + Integer.toString(cnt&3) + ".tiles.virtualearth.net/tiles/r" + s + "?g=784&mkt=en-us&lbl=l1&stl=h&shading=hill&n=z";    
	}
}


/////////////////////////////////////////////////////////////////////////////////////////
class TopoKrym implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return "http://mapim.com.ua/crimea/interesnye-mesta-v-krymu/map";}
	public String getName(){return "TopoKrym";}	
	public String getExt(){return jpg;}
	public Image getIcon(){return Interface.openStreetIcon;}
	public String getUrl(final int x,final int y,final int z){
		cnt++;
	    String s=Integer.toString((cnt&7)+1);
	    return "http://gmap"+s+".mapim.com.ua/images/l"+Integer.toString(z)+"/"+Integer.toString(x)+"_"+Integer.toString(y)+"_"+Integer.toString(z)+jpg;
	}
}
/////////////////////////////////////////////////////////////////////////////////////////
class OpenCycleMap implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return null;}
	public String getName(){return "OpenCycleMap";}	
	public String getExt(){return png;}
	public Image getIcon(){return Interface.openStreetIcon;}
	static final char[]abca={'a','b','c','a'};
	public String getUrl(final int x,final int y,final int z){
		cnt++;
		//http://b.tile.opencyclemap.org/cycle/6/39/26.png
	    return "http://"+abca[cnt&3]+".tile.opencyclemap.org/cycle/"+Integer.toString(z)+"/"+Integer.toString(x)+"/"+Integer.toString(y)+png;
	}
}
/////////////////////////////////////////////////////////////////////////////////////////
class OpenStreetMap implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return null;}
	public String getName(){return "OpenStreetMap";}	
	public String getExt(){return png;}
	public Image getIcon(){return Interface.openStreetIcon;}
	static final char[]abca={'a','b','c','a'};
	public String getUrl(final int x,final int y,final int z){
		cnt++;
	    return "http://"+abca[cnt&3]+".tile.openstreetmap.org/"+Integer.toString(z)+"/"+Integer.toString(x)+"/"+Integer.toString(y)+png;
	}
}
/////////////////////////////////////////////////////////////////////////////////////////
class YahooMap implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return null;}
	public String getName(){return "YahooMap";}	
	public String getExt(){return png;}
	public Image getIcon(){return Interface.yahooIcon;}
	public String getUrl(final int x,final int y,final int z){
		cnt++;
		int s;
		if (z>1){
			s=1<<(z-1);
			s--;
		}else
			s=y<<1;				
		//http://maps2.yimg.com/hx/tl?b=1&v=4.3&.intl=en&x=3&y=1&z=5&r=1
		return "http://maps"+Integer.toString(1+(cnt&1))+".yimg.com/hx/tl?b=1&v=4.3&.intl=en&x="+Integer.toString(x)+"&y="+Integer.toString(s-y)+"&z="+Integer.toString(z+1)+"&r=1";
	}
}
/////////////////////////////////////////////////////////////////////////////////////////
class YahooSatelitte implements INET_MAPS{
	public static int cnt;
	public String getReferer(){return null;}
	public String getName(){return "YahooSatelitte";}	
	public String getExt(){return jpg;}
	public Image getIcon(){return Interface.yahooIcon;}
	public String getUrl(final int x,final int y,final int z){
		cnt++;
		int s;
		if (z>1){
			s=1<<(z-1);
			s--;
		}else
			s=y<<1;				
		//http://maps2.yimg.com/hx/tl?b=1&v=4.3&.intl=en&x=3&y=1&z=5&r=1
		return "http://maps"+Integer.toString(1+(cnt&1))+".yimg.com/ae/ximg?v=1.9&t=a&s=256&.intl=en&x="+Integer.toString(x)+"&y="+Integer.toString(s-y)+"&z="+Integer.toString(z+1)+"&r=1";
	}
}

/////////////////////////////////////////////////////////////////////////////////////////
public class InetMaps {
	
static final private GoogleMap 				gm=new GoogleMap();
static final private GoogleEarth 				ge=new GoogleEarth();	
static final private GoogleTerrian 			gt=new GoogleTerrian();	
static final private MicrosoftEarthAndLabels 	bbe=new MicrosoftEarthAndLabels();
static final private TopoKrym					tk=new TopoKrym();
static final private OpenCycleMap				ocm=new OpenCycleMap();
static final private OpenStreetMap			osm=new OpenStreetMap();
static final private YahooMap					ym=new YahooMap();
static final private YahooSatelitte			ys=new YahooSatelitte();
static final private MicrosoftMap				mm=new MicrosoftMap();
static final public INET_MAPS[] im={gm,ocm,osm,mm,ym,ge,bbe,ys,gt,tk};

static public int type=0;


//static public final Image []ICONS={MyI.googleIcon,MyI.googleIcon,MyI.openStreetIcon,MyI.openStreetIcon,MyI.openStreetIcon,MyI.bingIcon,MyI.googleIcon,MyI.yahooIcon};
//static public final String []DIR={"GoogleMap","GoogleEarth","Topo_Krym","OpenCycleMap", "OpenStreetMap","Bing Bird's eye","GoogleTerian","Yahoo"};
//static public final String []EXT={png,jpg,jpg,png,png,jpg,jpg,png};    
/////////////////////////////////////////////////////////////////////////////////////////
static public byte[] downLoadTile(int x, int y, int z){	
    done=false; 
   
    InputStream is = null;
    HttpConnection c = null;
    try{
        try{
            c = (HttpConnection)Connector.open(im[type].getUrl(x, y, z));
            c.setRequestProperty( "User-Agent","Profile/MIDP-1.0 Configuration/CLDC-1.0" ); 
            c.setRequestProperty("Accept-Language",Settings.accept_language);
            if (im[type].getReferer()!=null)
                c.setRequestProperty("Referer",im[type].getReferer());
            is = c.openInputStream();
         

            
            
         /*   int lenF =(int)c.getLength() ;
            int len=lenF;
            if (len>0){
                byte []buf=new byte[len];
                int off=0,readed=0;
                do{
                    readed=is.read(buf, off, len);
                    MobNav1.inetIn+=readed;
                    if (readed==len)
                        return buf;
                    len-=readed;
                    off+=readed;
                    System.out.println("READED="+readed);
                }while (readed>0);
                
               // return Image.createImage(buf, 0, len);
                
            }
            
            
            
            
            */
            
            
            
            int len =(int)c.getLength() ;
            
            if (len>0){
                byte []buf=new byte[len];
                int readed=is.read(buf);
                MobNav1.inetIn+=readed;
                if (readed==len)
                    return buf;
               // return Image.createImage(buf, 0, len);
            }
        }finally{
               if (is!=null)
                   is.close();
               if (c!=null)
                   c.close();
        }
   } catch (IOException ex) {}
    return null;
}
static private String root="file:///e:/tiles/";
static private final int MAX_FILES_IN_DIR_SH = 10;



/////////////////////////////////////////////////////////////////////////////////////////
static private String GetTileFName(final int x, final int y, final int z){
    FileConnection fc=null;  
    try {
        try{             
            String fn=root;
            fc = (FileConnection) Connector.open(fn);
            if (! fc.exists())
                fc.mkdir();
            fc.close();
            fn+=im[type].getName()+"/";
             fc = (FileConnection) Connector.open(fn);
            if (! fc.exists())
                fc.mkdir();
            fc.close();
            fn+=Integer.toString(z)+"/";
            fc = (FileConnection) Connector.open(fn);
            if (! fc.exists())
                fc.mkdir();
            fc.close();
            fn+=Integer.toString(x>>MAX_FILES_IN_DIR_SH)+"_"+Integer.toString(y>>MAX_FILES_IN_DIR_SH)+"/";
            fc = (FileConnection) Connector.open(fn);
            if (! fc.exists())
                fc.mkdir();
            fc.close();
            fc=null;
       return fn+=Integer.toString(z)+"_"+Integer.toString(x)+"_"+Integer.toString(y)+im[type].getExt();
        }finally{
            if (fc!=null)
                fc.close();
        } 
    } catch (Exception ex) {}
     
     
    
    return null;
}
/////////////////////////////////////////////////////////////////////////////////////////

private static final Object stMon=new Integer(0);
private static Stack st=new Stack();
private static boolean []downloaderRunning={false,false};


/////////////////////////////////////////////////////////////////////////////////////////
static public int GetStSize(){
    synchronized(stMon){
        return st.size();
    }
}
/////////////////////////////////////////////////////////////////////////////////////////
static private final int MAX_DOWNLOAD_ERRORS=3;
static private void StartDownloader(final int thr){

 new Thread(new Runnable() {
            
    public void run() {
        
        TILE tb=null;
        while (true){   
            synchronized(stMon){
                int size=st.size();
                if (size==0){   
                    downloaderRunning[thr]=false;
                    if (done==false)
                    	Update();
                    return;
                }else{
                    int i=0;
                    for (; i<size; i++){         
                        if (((TILE)st.elementAt(i)).inProcess==false){
                            ((TILE)st.elementAt(i)).inProcess=true;
                            tb=new TILE((TILE)st.elementAt(i));
                            break;
                        }
                    }if (tb==null){
                        downloaderRunning[thr]=false;                        
                        return;
                    }
                }
            }  
            FileConnection fc=null;
            OutputStream out=null;
             boolean removeFromST=true,  exist=false;
             
             try{
                    fc=(FileConnection) Connector.open(tb.fn);
                    exist=fc.exists();
                 
                     if (exist){
                         fc.close();
                     }
                 } catch (IOException ex) {}    
             
             if (exist==false && NAVI.map.tileonScreen(tb.x,tb.y,tb.z)){                                 
                byte []b=downLoadTile(tb.x>>1,tb.y>>1,tb.z);   
                if (b!=null  ){
                    try {
                        
                        try{
                            //fc=(FileConnection) Connector.open(tb.fn);                          
                            fc.create();
                            out=fc.openOutputStream();
                            out.write(b);
                        }finally{
                            if (out!=null)
                                out.close();
                            if (fc!=null)
                                fc.close();
                        }        
                       // System.out.println("Thread="+thr+" Write "+tb.fn);
                    } catch (IOException ex) {
                        removeFromST=true;System.out.println("Thread="+thr+" ERROR "+tb.fn+ex.toString());
                    }
                }else{
                    System.out.println("ERROR b==null");
                    removeFromST=false;
                }
                    
             }
             synchronized(stMon){
                 try{
                 int size=st.size();                                     
                 for (int i=0; i<size; i++)
                     if ( tb.fn.endsWith(((TILE)st.elementAt(i)).fn) ){
                         if (removeFromST){
                             st.removeElementAt(i);
                             size--;
                             break;
                         }else{
                             if ( ++((TILE)st.elementAt(i)).error>MAX_DOWNLOAD_ERRORS    ){
                                 st.removeElementAt(i);
                                 size--;
                                 MyCanvas.SetErrorText("No TIle");  
                             }else{                                 
                                 ((TILE)st.elementAt(i)).inProcess=false;
                                 System.out.println("pred ERROR");
                                 //removeFromST=true;                                                                  
                             }    
                         }
                     }
                 } catch (Exception ex) {
                        MyCanvas.SetErrorText("No TIle+");
                    }
             }
        }
                                
 }}).start();
}
/////////////////////////////////////////////////////////////////////////////////////////
static boolean done=true;
static private void Update(){
		 done=true;
         new Thread(new Runnable() {
            public void run() {                
             while (MyCanvas.sleep_time==MyCanvas.SLEEP_TIME_SHORT || NAVI.map.inProc){
                  try {  
                    Thread.sleep(100);
                  } catch (InterruptedException ex) {} 
             }
             MAP.redraw++;
            synchronized(MyCanvas.update){ System.out.println("_UPD_");MyCanvas.update.notify(); }
            
        }}).start();
    
}
/////////////////////////////////////////////////////////////////////////////////////////

static public Image getTile(final int x_, int y_, final int z){

    int x=x_>>1;
    int y=y_>>1;
        
    if (y<0 || y>=(1<<z))
        return MAP.out_of_map;
    FileConnection fc=null;
    final String fn=GetTileFName(x,y,z);
            boolean exist=false;            
            try{
                fc=(FileConnection) Connector.open(fn); 
                exist=fc.exists();
                
            } catch (Exception ex) {exist=false;System.out.println("TILE EXIST"+ex.toString());}
                        
            if (exist){
                InputStream in=null;
                try{
                    try{                       
                        in=fc.openInputStream();
                        byte []b=new byte[in.available()];
                        int size=in.read(b);
                        Image img= Image.createImage(b, 0, size);                                                                        
                         if (img!=null){                            
                            Image si=Image.createImage(128, 128);
                            si.getGraphics().drawImage(img, ((x<<1)-x_)<<7, ((y<<1)-y_)<<7,0);          
                            return si;                            
                        }                                         
                    }finally{
                        in.close(); 
                        fc.close();
                    }
                }catch (Exception ex){System.out.println("Load Tile Error "+ex.toString());}
            }else if (MAP.offline==false){
                try{fc.close();}catch (Exception ex){System.out.println("CLOSE");}
                synchronized(stMon){                    
                    int i,size=st.size();
                    final TILE nt=new TILE(x_,y_,z,0,fn);
                    for (i=0; i<size; i++)
                        if (((TILE)st.elementAt(i)).fn.endsWith(fn)){                            
                            break;
                        }
                    if (i==size)
                        st.push(nt);
                                                                                 
                  for (int threadN=0; threadN<downloaderRunning.length; threadN++)
                    if (downloaderRunning[threadN]==false){
                        downloaderRunning[threadN]=true;
                        StartDownloader(threadN);
                        break;
                    }
                }
            }
    return MAP.loading;
}
/////////////////////////////////////////////////////////////////////////////////////////

}

class TILE{
    public int x, y, z,hash,error=0;
    public String fn;
    public boolean inProcess=false;
    
    public TILE(final TILE t){
        inProcess=t.inProcess;
        x=t.x;
        y=t.y;
        z=t.z;
        hash=t.hash;
        fn=t.fn;
        error=t.error;
    }
    public TILE(int x, int y, int z, int hash,String fn){
        this.x=x;
        this.y=y;
        this.z=z;
        this.hash=hash;
        this.fn=fn;//new String(fn);
    }
 
}