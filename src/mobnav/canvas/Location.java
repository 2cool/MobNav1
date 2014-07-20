package mobnav.canvas;

import mobnav.math.math;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//Point01,xy,    0,    0,in, deg,85,3.067727,N,180,0.000000,W, grid,   ,           ,           ,N
//Point02,xy,255,255,in, deg,85,3.067727,S,180,0.000000,E, grid,   ,
/**
 *
 * @author 2cool
 */

public class Location{
        private double lat=0;
        private double lon=0;
        private boolean init=false;
public void    reset(){init=false;}
public boolean equals(final Location l){return lat==l.lat && lon==l.lon;}
public static final double D_R     = (Math.PI / 180.0);
public static final double R_D     = (180.0 / Math.PI);
public static final double R_MAJOR = 6378137.0;
//public static final double R_MINOR = 6378137.0;//6356752.314245;
//public static final double RATIO   = 1;//(R_MINOR/R_MAJOR);
//public static final double ECCENT  = 0;//(Math.sqrt(1.0 - (RATIO * RATIO)));
//public static final double COM     = 0;//(0.5 * ECCENT);

    public  String getLat(){
    	String ds=Double.toString(lat);
        return ds.substring(0,Math.min(10, ds.length()));
    }
    public  String getLon(){
    	String ds=Double.toString(lon);
        return ds.substring(0,Math.min(10, ds.length()));
    }
    public  String latitude(){return getGeoStr("SN",lat);}
    public  String longtitude(){return getGeoStr("WE",lon);}
    public Location(final double lat, final double lon){this.lat=lat;this.lon=lon;init=true;}
    public Location(){}
    public Location(final Point p){lon=merc_lon(p.x);lat=merc_lat(p.y);init=true;}
    public Location(final Location l){lon=l.lat;lat=l.lat;init=l.init;}
    public Location(final String lat, final String lon){    
        try{
            this.lat=Double.parseDouble(lat.trim());
            this.lon=Double.parseDouble(lon.trim());
        }catch (Exception e){
	        this.lat=parseGeoStr(lat.trim());
	        this.lon=parseGeoStr(lon.trim());
        }
        init=!(Double.isNaN(this.lat) || Double.isNaN(this.lon));
    }
    public boolean init(){return init;}
    public Location Get(){return this;}
    public void Set(final double lat, final double lon){this.lat=lat;this.lon=lon;}
    public void Set(final Point p){lon=merc_lon(p.x);lat=merc_lat(p.y);}
    public void Set(final Location l){lat=l.lat;lon=l.lon;init=l.init;}
    public String toString(){return "Lat="+lat+" Lon="+lon;}
    // ##########################################################################
/*
 * Mercator transformation
 * accounts for the fact that the earth is not a sphere, but a spheroid
 */




// ##########################################################################

static public double deg_rad (double ang) {
        return ang * D_R;
}
 static double rad_deg (double ang) {
        return ang * R_D;
}
// ##########################################################################
static public double merc_x (double lon) {
        lon = Math.min (180, Math.max (lon, -180));
        return R_MAJOR * deg_rad (lon);
}
//public Location(final Point p){lon=merc_lon(p.x);lat=merc_lat(p.y);}
static public  Location GetLocation(Point p){return new Location(p);}
final public  Point GetPoint(){return new Point((int)merc_x(lon),(int)merc_y(lat));}
static public Point GetPoint(final Location l){return l.GetPoint();}

// ##########################################################################
static public final double latMax=85.051128783333333333333333333333;
static public final double lonMax=180;
//static final double latMax=89.5;

static public double merc_y (double lat) {
        lat = Math.min (latMax, Math.max (lat, -latMax));
        double phi = deg_rad(lat);
        double ts = Math.tan(0.5 * (Math.PI * 0.5 - phi));
        return 0 - R_MAJOR * math.log(ts);
}
public double merc_y () {
    return merc_y(this.lat);
}
// ##########################################################################
static private Location old=null;
static private double rd,dy;
static public double getDistInPoints(final Location l,final double dist){//0.02 градус
	if (old==null || Math.abs(old.lat-l.lat)>0.04){
	    Location l1=new Location(l.lat+((l.lat<0)?0.04:-0.04),l.lon);
	    rd=1/l.getDistance(l1);
	    dy=Math.abs(l.merc_y()-l1.merc_y());
	}
	return dy*rd*dist;
	
}
// ##########################################################################
static double merc_lon (double x) {
        return rad_deg(x) / R_MAJOR;
}
// ##########################################################################
static  double merc_lat (double y) {
        double ts = math.exp ( -y / R_MAJOR);
        double phi = math.M_PI2 - 2 * math.atan(ts);
        double dphi = 1.0;
        int i;
        for (i = 0; Math.abs(dphi) > 0.000000001 && i < 15; i++) {
                dphi = math.M_PI2 - 2 * math.atan (ts) - phi;
                phi += dphi;
        }
        return rad_deg (phi);
}

// ##########################################################################
static public final double mmm=111120;
static public final double rmmm=1.0/111120;
static public final double pid180=0.01745329251994329576923690768489;

static public double getYdistance(final Location l1,final Location l0){
    return Math.abs((l1.lat-l0.lat)*mmm);
}




static public double getXdistance(final Location l1,final Location l0){
    return Math.abs((l1.lon-l0.lon)*mmm*Math.cos(l0.lat*pid180));
}
static public double getDistance(final Point gp0, final Point gp1){
    return getDistance(new Location(gp0),new Location(gp1));
}
 static public double getDistance(final Location l1,final Location l0){
       //одна минута = 1852 мили
       ///SQRT(((B2-B1)*60*1852)^2+((C2-C1)*60*1852*COS(B1*PI()/180))^2) .
       double lat=((l1.lat-l0.lat)*mmm);
       double t=mmm*Math.cos(l0.lat*pid180);
       double lon=Math.abs((l1.lon-l0.lon)*t);
       double lon0=Math.abs(360*t);
       lon=Math.min(lon, Math.abs(lon0-lon));
       return Math.sqrt(lat*lat+lon*lon);
   }
public final   double getDistance(final Location l){
        return getDistance(this,l);
   }


///--------------------------------------------------------------------------------------
static String getGeoStr(String str,double d){  //str= "NS or EW"
	if (Settings.locaView==3){
		String r=Double.toString(d);
		return r.substring(0,Math.min(10, r.length()));
	}
    int sign=(d<0)?0:1;
    String ss=str.substring(sign, sign+1);  
    String s="";
    d=Math.abs(d);
    if  (Settings.locaView>0){
        double integer=Math.floor(d);
        s+=TEXT.ndigits("0",(int)integer);
        double min=(d-integer)*60.0;
        s+="°";
        if (Settings.locaView>1){
            integer=Math.floor(min);
            s+=TEXT.ndigits("0",(int)integer);
            double sec=(min-integer)*60;
            s+="'";
            String ds=Double.toString(sec);
            s+=ds.substring(0, Math.min(5,ds.length()));   
            s+="\"";
            return s+ss;
        }else{
        	String ds=Double.toString(min);
            s+=ds.substring(0, Math.min(7,ds.length()));
            s+="'";
            return s+ss;
        }
    }else{
    	String ds=Double.toString(d);
        return s+=ds.substring(0,Math.min(9, ds.length()))+"°"+ss;
    }
}
///--------------------------------------------------------------------------------------
public static double parseGeoStr(String str){
	try{
		int grad=str.indexOf("°");
		double d;
		if (grad>=0){
			d=Double.parseDouble(str.substring(0,grad));
			int min=str.indexOf("'");
			if (min>=0){
				d+=Double.parseDouble(str.substring(grad+1,min))*(1.0/60);
				int sec=str.indexOf("\"");
				if (sec>=0)
					d+=Double.parseDouble(str.substring(min+1,sec))*(1.0/3600);
			}		
			 if (str.indexOf(0)=='S' || str.indexOf(0)=='W')
			    	d=-d;
			}else
				d=Double.parseDouble(str);
		return d;
	}catch (Exception e){MyCanvas.SetErrorText("ParseGeoStr err");return Double.NaN;}
	
}
}

//Lat=47.95944398824761 Lon=33.41212732375114
//Lat=4.09275 Lon=3.045444444444444
