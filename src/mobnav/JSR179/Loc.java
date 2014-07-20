package mobnav.JSR179;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
/*
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

*/

public class Loc{// implements  LocationListener,Runnable{
	/*
	static Criteria criteria;
	static LocationProvider lp;
	static Thread t;
	public static QualifiedCoordinates c=null;
	
	
	public Loc(){
		criteria=new Criteria();
		t=new Thread(this);
		t.start();
	}
	 public void run() 
	    {
	    	try{
	    		setCriteria();
	    		lp = LocationProvider.getInstance(criteria);
	    		lp.setLocationListener(this, -1, -1, -1);
	    		//displayLocationForm();
	    	}catch(Exception e)
	    	{
	    		 Alert alert = new Alert("Error", "Could not retrieve location for the given criteria!", null, AlertType.ERROR);
	    		// display.setCurrent(alert);
	    	}    
	    }
	
	static public void setCriteria()
    {  	
    	
    	switch (0) 
    	{
        	case 0: criteria.setSpeedAndCourseRequired(true); break;
        	case 1: criteria.setSpeedAndCourseRequired(false); break;
    	}
    	switch (0) 
    	{
        	case 0: criteria.setCostAllowed(true); break;
        	case 1: criteria.setCostAllowed(false); break;
    	}
    	switch (3) 
    	{
        	case 0: criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW); break;
        	case 1: criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_MEDIUM); break;
        	case 2: criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH); break;
        	case 3: criteria.setPreferredPowerConsumption(Criteria.NO_REQUIREMENT); break;
    	}
    	switch (0) 
    	{
        	case 0: criteria.setAltitudeRequired(true); break;
        	case 1: criteria.setAltitudeRequired(false); break;
    	}
		criteria.setHorizontalAccuracy(0);
		criteria.setVerticalAccuracy(0);

		criteria.setPreferredResponseTime(0); 
    }
	
	
	public void locationUpdated(LocationProvider arg0, Location loc) 
    {
		
      	String extrainfovalue="N/A";
    	if(loc.isValid())
    	{
        	c = loc.getQualifiedCoordinates();
        	
    		extrainfovalue=loc.getExtraInfo("application/X-jsr179-location-nmea");

    		if(extrainfovalue==null)
    			extrainfovalue="N/A";
    		else
    			extrainfovalue="Available in NMEA format!";
    	}
    	
    	
    }
	public void providerStateChanged(LocationProvider provider, int newState) {
		// TODO Auto-generated method stub
		
	}
	
	
	*/
}
