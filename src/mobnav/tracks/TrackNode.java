package mobnav.tracks;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */

import java.io.*;

import mobnav.canvas.Location;
import mobnav.canvas.OZI_PLT;
import mobnav.canvas.Point;
import mobnav.canvas.TEXT;


public class TrackNode {
   
    public float fheight;
    public Point gp;
    public float dist;
    public String txt=null;
    public int dtime=-1;   // one unit= 1/7,8125   sec
    
    static private double trackLength=0;
    static private Point pMin,pMax;
    static private TrackUpDown tud;
    static private float minH,maxH;
    static private String []old_sm=null;
    static private Location old_loc=null;
    static private String old_text=null;
    static public  boolean trBreak;
    static private double startTime=0;
   // static private Point oldGp=null;
    static public int loaded;    
    //static private int lastTime=0;
    static private Point lastGp=null;
    static private Location lastLoc=null;
    static private String lastText=null;
    static private float lastHeight=0;
    //------------------------------------------------------------

    //------------------------------------------------------------
    /*
    public TrackNode(DataInputStream dis) throws IOException{
        trBreak=dis.readBoolean();
        gp=new Point();
        gp.x=dis.readInt();
        gp.y=dis.readInt();
        len=dis.readFloat();
        fheight=dis.readFloat();
        int txtLen=dis.readShort();
        if (txtLen>0){
            byte []b=new byte[txtLen];
            dis.read(b);
            txt=new String(b,"UTF-8");
        }else
            txt=null;
    }*/
  //--------------------------------------------------------------------------------------- 	
    public void Save(DataOutputStream dos) throws IOException{
        dos.writeBoolean(trBreak);
        dos.writeInt(gp.x);
        dos.writeInt(gp.y);
        dos.writeFloat(dist);
        dos.writeFloat(fheight);        
        if (txt!=null && txt.length()>0){
            byte[]b=txt.getBytes("UTF-8");
            dos.writeShort(b.length);
            dos.write(b);
        }else
            dos.writeShort(0);
    }
    //------------------------------------------------------------
    public TrackNode( final TrackNode tn){
    	gp=tn.gp;
    	fheight=tn.fheight;
    	dist=tn.dist;
    	dtime=tn.dtime;
    	txt=tn.txt;    	
    }
    public TrackNode (final Point p, final float len, final float height, final int dtime, final String txt){
    	this.gp=new Point(p);
    	this.fheight=height;
    	this.dist=len;
    	this.dtime=dtime;
    	this.txt=txt;
    }
    public TrackNode(final Point p, final float len){
        this.gp=new Point(p);
        this.fheight=Track.altitude_not_valid;
        this.txt=null;
        this.dist=len;
    }
    //------------------------------------------------------------
    public TrackNode(final Point gp, final double height, final String txt){
    	trBreak=false;
    	this.gp=gp;
    	this.fheight=(float)height;
    	this.txt=txt;
    }
    public TrackNode(final Point gp0, final Point gp1, final short height, final String txt){
        
        Location l1=Location.GetLocation(gp1);
       // System.out.println("trackNodeEnter");
        if (!(trBreak=gp0==null)){
           this.gp=lastGp;
           this.fheight=lastHeight;
           this.txt=lastText;
           this.dist=(float)Location.getDistance(lastLoc,l1);   
        }
        lastGp=new Point(gp1.x, gp1.y);
        lastLoc=l1;
        lastHeight=height;
        lastText=txt;                               
        
    }        
    //------------------------------------------------------------
    public TrackNode(){
        trBreak=false;
         gp=lastGp;
         this.fheight=lastHeight;
         this.txt=lastText;
         dist=0;
        
    } 
    
    
    
    
  //--------------------------------------------------------------------------------------- 	
    	public  TrackNode(final DataInputStream dis) throws IOException{
    		 int px=dis.readInt();
    	     int py=dis.readInt();	  
    	     this.gp=new Point(px,py);
    	   
    	     dist=dis.readFloat();
    	     fheight=dis.readFloat();
    	     dtime=dis.readInt();         
    	               
    	     int textLen=dis.readInt();
    	     loaded=24+textLen;
    	     txt=null;
    	     if (textLen>0){
    	    	 byte []b=new byte[textLen];
    	    	 dis.read(b);
    	    	 txt=new String(b,"UTF-8");
    	     }    	     
    	}
    	//---------------------------------------------------------------------------------------
    	
    	private void setText(final String text){    		
            if (old_text!=null && old_text.endsWith(text))
            	txt=null;
            else
            	txt=old_text=text;			
    	}
    	//---------------------------------------------------------------------------------------
    	//загрузка Ozi explorer plt построчно
    	
    	public TrackNode(final String pltStr1,final String pltStr0){
    		
    		if (pltStr0==null ){
    			trBreak=false;
    			trackLength=0;
    			old_sm=TEXT.split(pltStr1, ',');
    			old_loc=new Location(Double.parseDouble(old_sm[0].trim()),Double.parseDouble(old_sm[1].trim()));
    			Point p=old_loc.GetPoint();
    			pMin=new Point(p);
    			pMax=new Point(p);
    			float h=(old_sm[3].length()==0)?Track.altitude_not_valid:Float.parseFloat(old_sm[3].trim());
    			tud=new TrackUpDown(h);
            	minH=maxH=h;
    			double time=(old_sm[4].length()==0)?0:Double.parseDouble(old_sm[4].trim());
    			startTime=time;
            	dtime=0;            	
    		}else if (pltStr1!=null){    	
    			gp=old_loc.GetPoint();
    			
    			double time=(old_sm[4].length()==0)?0:Double.parseDouble(old_sm[4].trim());
    			dtime=(int)((time-startTime)*3600*24*7.8125);
    			fheight=(old_sm[3].length()==0)?Track.altitude_not_valid:Float.parseFloat(old_sm[3].trim());			            			            
    			tud.add(fheight);
    			txt=null;
    			if (old_sm[5].startsWith(OZI_PLT.marker))
    				setText(old_sm[5].substring(OZI_PLT.marker.length(),old_sm[5].length()));
    			 	                			
    			old_sm=TEXT.split(pltStr1, ',');
    			trBreak=Integer.parseInt(old_sm[2].trim())==1;
    			Location loc=new Location(Double.parseDouble(old_sm[0].trim()),Double.parseDouble(old_sm[1].trim()));
    			trackLength+=dist=(float)loc.getDistance(old_loc);
    			old_loc=loc;
    			setMinMax();
    		}else{
    			gp=old_loc.GetPoint();
    			
    			trBreak=false;//Integer.parseInt(old_sm[2].trim())==1;
    			double time=(old_sm[4].length()==0)?0:Double.parseDouble(old_sm[4].trim());
    			dtime=(int)((time-startTime)*3600*24*7.8125);
    			fheight=(old_sm[3].length()==0)?Track.altitude_not_valid:Float.parseFloat(old_sm[3].trim());
    			tud.add(fheight);
    			dist=0;
    			
    			txt=null;
    			if (old_sm[5].startsWith(OZI_PLT.marker))
    				setText(old_sm[5].substring(OZI_PLT.marker.length(),old_sm[5].length()));     
    			setMinMax();	
	
    		}
    		
    		
    		
    	

    	}
    	//---------------------------------------------------------------------------------------    	
    	public void save (final DataOutputStream dos) throws IOException{
    		dos.writeInt(gp.x);
        	dos.writeInt(gp.y);	            		            	        	          
            dos.writeFloat(dist);
        	dos.writeFloat(fheight);
        	dos.writeInt(dtime);   
        	if (txt==null)
        		dos.writeInt(0);
        	else{
        		byte []b=txt.getBytes("UTF-8");
        		dos.writeInt(b.length);
        		dos.write(b);
        	}
    	} 
    	//--------------------------------------------------------------------------------------- 	
    	static public void save_statistics (final DataOutputStream dos) throws IOException{
    		dos.writeInt(pMax.x);
        	dos.writeInt(pMax.y);
        	dos.writeInt(pMin.x);
        	dos.writeInt(pMin.y);
        	dos.writeInt((int)trackLength);
        	dos.writeInt((int)maxH);	
        	dos.writeInt((int)minH);
        	dos.writeInt(tud.getUp());
        	dos.writeInt(tud.getDown());    		
    	}
    	
   //--------------------------------------------------------------------------------------- 	
    	private void setMinMax(){
    		 if (pMin.x>gp.x)
                 pMin.x=gp.x;	            	
             else if (pMax.x<gp.x)
                 pMax.x=gp.x;
             if (pMin.y>gp.y)
                 pMin.y=gp.y;
             else if (pMax.y<gp.y)
                 pMax.y=gp.y;
             
             if (maxH<fheight)
         		maxH=fheight;
         	else if (minH>fheight)
         		minH=fheight;
    	}
    	
}
//------------------------------------------------------------