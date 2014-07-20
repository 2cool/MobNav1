package mobnav.canvas;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import mobnav.tracks.Path;
import mobnav.tracks.PathNode;
import mobnav.tracks.TrackNode;
import mobnav.tracks.Tracks;


public class Graph 	extends MODE_DEF{
	
	
	
	
	
	
	
	
	
	public void keyPressed(int keyCode){key(keyCode);} 

	
	
	static private double MAX_X=240, MAX_Y=320,UPY;
	//static BUTON_SOFT_KEY rightsk=new BUTON_SOFT_KEY("+",null,-7,0xeeeeee);
	
	
	
    public boolean key(final int keyCode){
    	 boolean upd=true;
 	    if (MenuStr.KeyPressed(keyCode)==false)
 	        switch (keyCode) {
 	            case Canvas.KEY_POUND:upd=false;MyCanvas.ChangeLights();break;
 	            case Canvas.KEY_NUM1:incZoom();break;
 	            case Canvas.KEY_NUM3:decZoom();break;
 	            case -3:
 		        case Canvas.LEFT:
 		            shiftX++;break; //  <-
 		        case -4:
 		        case Canvas.RIGHT:
 		            shiftX--;break;  //  ->
 	            
 	        }    
 	    if (upd)
 	        synchronized(MyCanvas.update){MyCanvas.update.notify();}
 	    return false;
	}
	

	
    static public int zoom;
	//static private Track t=null;
	//static private int node=0;
	static private double kY,kX;
	
	
	private static int maxy=320;
	
    static public void init( final int maxx,final int maxy){    	
    	MAX_X=maxx;
    	double k=0.1;
     	MAX_Y=Graph.maxy=maxy;   	
    	UPY=MAX_Y*k;
    	MAX_Y-=UPY+UPY;

    	//Graph.t=t;
    	
    	//Graph.node=node;
    	setZoom(Graph.zoom);    	 
    	
    	//tcd=new TrackConductorDriver(t.trackConductor);
    }
    static public void incZoom(){
    	if (zoom<MAP.MAX_ZOOM)
    		setZoom(++zoom);
    	
    	
    }
    static public void decZoom(){
    	if (zoom>0)
    		setZoom(--zoom);
    }
   static  public void setZoom(final int zoom){	  
	   	if (Graph.zoom<zoom)
		   shiftX>>=Graph.zoom-zoom;
		else
			shiftX<<=zoom-Graph.zoom;
			   
			   
    	Graph.zoom=zoom;
    	kY=(MAX_Y-5)/(Path.track.maxH-Path.track.minH);
    	kX=MAX_X/Path.track.trackLength;
    	for (int z=0; z<zoom; z++)    
    		kX*=2;    	
    }
	static private Point get(final double dist, final double height){
		return new Point((int)(dist*kX)-shiftX,(int)(UPY+height*kY)-Path.track.minH);
	}
	//private static  double curDist=-1;
	

	private static int shiftX=0;
	
	
	
	private void draw(final Graphics g, final Point p[]){
		int y=(int)(MAX_Y+UPY);
		g.setColor(0);
		g.fillTriangle(p[0].x, p[0].y, p[1].x, p[1].y, p[0].x,y);
		g.fillTriangle(p[1].x, y, p[1].x, p[1].y, p[0].x,y);
		g.setColor(0x00ff00);
		g.drawLine(p[0].x, p[0].y, p[1].x, p[1].y);
	}
	
	public void paint2(Graphics g){

		
		double node=	Tracks.path.getNode();
		if (node==-1)
			return;
		
		
		
		int xnode=-1,inode=(int)Math.floor(node);
		double pos_height;
		
		TrackNode tn0=(TrackNode)Path.track.path.elementAt(inode);
		TrackNode tn1=(TrackNode)Path.track.path.elementAt(inode+1);
		pos_height=tn0.fheight+(tn0.fheight-tn1.fheight)*(node-inode);
		
		PathNode pn=(PathNode)Tracks.path.nodes.elementAt(0);		
		int maxx=(int)MAX_X;
		Point []p=new Point[2];		
		p[0]=get(pn.dist_from_beg+pn.dist,pos_height);		
		Point pos=new Point(p[0]);
		int i=1;
		int size=Path.track.path.size();
		double dist_form_beg=pn.dist_from_beg+tn0.dist;
		int inode1=inode;
		while(p[i^1].x<maxx && ++inode<size){			
			TrackNode tn=(TrackNode)Path.track.path.elementAt(inode);
			p[i]=get(dist_form_beg,tn.fheight);
			if (p[0].x!=p[1].x){
				draw(g,p);
				i^=1;
			}					
		}
		inode=inode1;
		p[0].Set(pos);
		i=1;
		while(p[i^1].x>=0 && --inode<size){			
			TrackNode tn=(TrackNode)Path.track.path.elementAt(inode);
			p[i]=get(dist_form_beg,tn.fheight);
			if (p[0].x!=p[1].x){
				draw(g,p);
				i^=1;
			}					
		}
		
		
		
		
		
		
		
	}
	
	
	public void paint(Graphics g){

		//if (Tracks.graph)
		
		int maxx=(int)MAX_X;
		Point []p=new Point[2];
		MyCanvas.g.setColor(0xffffff);
		MyCanvas.g.fillRect(0, 0, maxx, maxy);
		TrackNode tn=Path.elementAt(0);
		
		p[0]=get(0,tn.fheight-Path.track.minH);
		double dist=tn.dist;
		
		
		MyCanvas.g.setColor(0);
		int y=(int)(MAX_Y+UPY);
		g.setColor(0);
		if (NAVI.font==null)
			NAVI.setFont(g);
		g.setFont(NAVI.font);
		g.drawString(""+Path.track.maxH+Interface.s_m, 0, (int)UPY-NAVI.fontH, 0);
		g.drawString(""+Path.track.minH+Interface.s_m, 0, y+2, 0);
		int n=0;
		Point pnt;
		double node=	Tracks.path.getNode();
		int xnode=-1,inode=(int)Math.floor(node);
		for (int x=1; x<maxx && n<Path.track.trackNodes; x++){
			double maxH=-101010;	
			do{				
				tn=Path.elementAt(n);
				maxH=Math.max(maxH, tn.fheight-Path.track.minH);
				pnt=get(dist,maxH);	
				if (inode!=-1)
					if (n==inode)
						xnode=pnt.x;
					else if (n-1==inode){
						xnode+=(int)((double)(pnt.x-xnode)*(node-inode));
					}
				dist+=tn.dist;				
				
				n++;					
			}while (n<Path.track.trackNodes && pnt.x<x);
			
			p[x&1]=pnt;				
			int x0=p[0].x-shiftX;
			int x1=p[1].x-shiftX;
			g.setColor(0);
			g.fillTriangle(x0, p[0].y, x1, p[1].y, x0,y);
			g.fillTriangle(x1, y, x1, p[1].y, x0,y);
			g.setColor(0x00ff00);
			g.drawLine(x0, p[0].y, x1, p[1].y);
			//System.out.println(x0+","+x1);
			if (x1>240 || x0>240)
				break;
			
		}
		g.setColor(0xff0000);
		xnode=-shiftX;
		g.drawLine(xnode, 0, xnode, maxy);
		
		
	}
}
