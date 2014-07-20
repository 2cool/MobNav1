package mobnav.gps;

public class GSV_DATA{		   
    public int prn=-1;
    public int elevation;
    public int azimuth;
    public int snr=-1;
    private double  x=2,y=2;
    public String to_string(){return "PRN "+prn+" ele "+elevation+" azi "+azimuth+" snr "+snr;}
    private void setXY(){
 	   if (prn==0){
 		   x=0;y=1;
 	   }else{
	           double el=Math.toRadians(elevation);
	           el=Math.cos(el);
	           double az=Math.toRadians(azimuth);
	           x=el*Math.sin(az);
	           y=el*Math.cos(az);
 	   }
    }
   public double GetX(){if (x>1)setXY();return x;}
   public double GetY(){if (y>1)setXY();return y;}
   public GSV_DATA(final Sequence st){
 	  prn=NMEA.ParseInt(st.getNext());
       elevation=NMEA.ParseInt(st.getNext());
       azimuth=NMEA.ParseInt(st.getNext());
       snr=NMEA.ParseInt(st.getNext());
   }
}