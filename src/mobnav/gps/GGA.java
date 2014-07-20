package mobnav.gps;

public class GGA  implements SENTENCE{

	 static public boolean exist=false;
	   static public float height;
	   static public int satellites,fixQuality;
	   public   void parse(final Sequence st){
	       try{ 
	    	   exist=true;
	    	  NMEA.fix_time_UTC=st.getNext();
	          NMEA.setLocation(st);
	          fixQuality=NMEA.ParseInt(st.getNext());
	          satellites = NMEA.ParseInt(st.getNext());

	          NMEA.HDOP=(float)NMEA.ParseDouble(st.getNext());
	          height=(float)NMEA.ParseDouble(st.getNext());
	          NMEA.nodata=false;
	      }catch (Exception e){NMEA.nodata=true;System.out.println("GGA error "+e);}
	   }
    } 