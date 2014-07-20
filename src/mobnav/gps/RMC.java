package mobnav.gps;

public class RMC implements SENTENCE{
	 static public boolean exist=false; 	 
	 public void parse(final Sequence st){
	     try{
	    	 exist=true;
	    	NMEA.fix_time_UTC=st.getNext();
	    	NMEA.status=st.getNext().startsWith("V");    	
	        NMEA.setLocation(st);
	        NMEA.speedN=(float)(NMEA.ParseDouble(st.getNext()));
	        NMEA.track_made_good_true=(float)NMEA.ParseDouble(st.getNext());
	        String date=st.getNext();
	        NMEA.day=NMEA.ParseInt(date.substring(0,2));
	        NMEA.month=NMEA.ParseInt(date.substring(2,4));
	        NMEA.year=NMEA.ParseInt(date.substring(4,6));
	        NMEA.magnetic_variation=(float)NMEA.ParseDouble(st.getNext());
	       char c=st.getNext().charAt(0);
	       if (c=='W')
	    	   NMEA.magnetic_variation=360-NMEA.magnetic_variation;
	      }catch (Exception e){NMEA.nodata=true;System.out.println("RMC error "+e);}    
	 }
} 