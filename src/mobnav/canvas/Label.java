package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */

//import java.io.*;
import javax.microedition.lcdui.*;



public class Label{
static public final int noHeight=-777;
    private String name;
    public String coment="";
    private Point p;
    private Location loc;
    public int		height=noHeight;
    public Point p(){return p;}
    public String name(){return name;}
    public Location loc(){return loc;}
    public String rename(final String n){
        if (n!=null && n.length()>0)
            name=n;
        return name;
    }
    private final char dev='\t';
    public String toString(){
    	return 
    	loc.getLat()+dev+
    	loc.getLon()+dev+
    	Integer.toString(height)+dev+
    	coment+dev+
    	name;
    }
    public Label(final String s){
    	int i0=s.indexOf(dev);
    	int i1=i0+s.substring(i0+1).indexOf(dev);  
    	int i2=i1+s.substring(i1+1).indexOf(dev); 
    	int i3=i2+s.substring(i2+1).indexOf(dev); 
    	  loc=new Location(s.substring(0,i0),s.substring(i0+1,i1));
          p=loc.GetPoint();
          height=Integer.parseInt(s.substring(i1+1,i2));
          coment=s.substring(i2+1,i3);
          name=s.substring(i3+1);
    }

    public boolean equals(final Label l){
        return (l.loc.equals(loc) && l.name.equals(name)) && l.height==height && l.coment.equals(coment);
   }

   
    public Label ( final Location l, int height,String coment,String name){
    	loc=l;
    	this.name=name; 
    	this.coment=coment;
    	this.height=height;
    	this.p=Location.GetPoint(l);
    	
    	}
    public Label (String name, String coment,Point p, int height){
    	this.coment=coment;
    	this.height=height;
    	this.name=name; 
    	this.p=p;
    	loc=new Location(p);
    }
    
    public void draw(final Graphics g,  boolean drawActive){  
        Point l=MAP.get_relevant_frame_P(p);
        if (l.x>0 && l.x<g.getClipWidth()+15 && l.y>0 && l.y<g.getClipHeight()+15)
            g.drawImage(Interface.labelIcon, l.x-16, l.y-16, 0);                                                                                        
    }
 }
