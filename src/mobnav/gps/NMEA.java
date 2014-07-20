package mobnav.gps;

import mobnav.canvas.Location;
import mobnav.canvas.Point;
import mobnav.canvas.Sattelites;


/**
 *
 * @author 2cool
 */

//**********************************************************************

// **********************************************************************

//**********************************************************************

//**********************************************************************
 //**********************************************************************
   

   
// **********************************************************************   
   class GLL implements SENTENCE{
	   static public boolean exist=false;
	   public void parse (final Sequence st){
		   try{
		  NMEA.setLocation(st);
		  NMEA.fix_time_UTC=st.getNext();
		  NMEA.status=st.getNext().startsWith("V");
		  if (st.EOS==false)
			  NMEA.mode_indicator=st.getNext().charAt(0);
		   }catch (Exception e){
	    	   System.out.println("GLL error "+e);
	       	}
	   }
   }
   class VTG implements SENTENCE{
	   static public boolean exist=false;
	   static public float track_made_good_magnetic;//degr
	   public void parse (final Sequence st){
		   try{
		   exist=true;
		   NMEA.track_made_good_true=(float)NMEA.ParseDouble(st.getNext());
		   st.getNext();
		   track_made_good_magnetic=(float)NMEA.ParseDouble(st.getNext());
		   st.getNext();
		   NMEA.speedN=(float)(NMEA.ParseDouble(st.getNext()));
		   NMEA.speedK=(float)(NMEA.ParseDouble(st.getNext()));
		   NMEA.mode_indicator=st.getNext().charAt(0);	
		   }catch (Exception e){
	    	   System.out.println("VTG error "+e);
	       	}
	   }
   }
// **********************************************************************
   class ZDA implements SENTENCE{
	   static public boolean exist=false;
	   public void parse (final Sequence st){
		   try{
		   exist=true;
		   NMEA.fix_time_UTC=st.getNext();
		   NMEA.day=NMEA.ParseInt(st.getNext());
		   NMEA.month=NMEA.ParseInt(st.getNext());
		   NMEA.year=NMEA.ParseInt(st.getNext());
		   NMEA.zone_hours=NMEA.ParseInt(st.getNext());
		   NMEA.zone_minutes=NMEA.ParseInt(st.getNext());
		   }catch (Exception e){
	    	   System.out.println("ZDA error "+e);
	       	}
	   }
   }
// **********************************************************************
   class _N implements SENTENCE{
	   public void parse (final Sequence st){
		   st.getNext();
	   }
   }
//**********************************************************************

   // #########################################################################
public class NMEA {
	static public final int NOT_SET=-111111;
	
	static public Time time=new Time();	
		
	static public float magnetic_variation=NOT_SET;;
	static public float HDOP=NOT_SET;
	static public float track_made_good_true=NOT_SET;  //in degree
	static public float speedK=NOT_SET;  //Kilometers per hour
	static public float speedN=NOT_SET;  //knots
	static public float accuracy_of_GPS_Device=3,hError=99.99f;	
  
	static public int day=NOT_SET,month=NOT_SET,year=NOT_SET;
    static public Location l=new Location();
    static public Point gp=new Point();
    static public boolean nodata=true;
	static public String fix_time_UTC=null;
	static int zone_hours=NOT_SET;// -13..13
    static public int zone_minutes=NOT_SET;// 0..59
	static public boolean status=false;	
	static public char mode_indicator;
	static private void reset(){
		l.reset();
		speedK=NOT_SET;
		GSV.data=null;
	}
 // **********************************************************************
    
     static private GSA 				gsa=new GSA();
     static private GSV 				gsv=new GSV();	
     static private GGA 				gga=new GGA();	
     static private RMC 				rmc=new RMC();
     static private MCHN				mcn=new MCHN();
     static private GLL					gll=new GLL();
     static private VTG					vtg=new VTG();
     static private ZDA					zda=new ZDA();
     static private _N					_=new _N();
     static public SENTENCE[]sentence={_,gga,_,_,_,_,_,gll,rmc,vtg,gsv,_,mcn,_,_,_,_,_,_,_,_,gsa,_,zda,_,_,_,_,_,_,_,_};
/*
      TKC 12
      GGA 1
     GLL 7
      GSA 21
      GSV 10
      RMC 8
     VTG 9
     ZDA 23
*/
//****************************************************************************
    
    static void init(byte[]b){        	    	       
    try{    
    	reset();
        int star=-1,dollar=-1,i=0;
        String data=new String(b);
        System.out.println(data);
        int len=data.length();
        while (i<len && (dollar=data.substring(i).indexOf('$'))!=-1){
        	dollar+=i;
        	i=dollar+1;
        	star=data.substring(i).indexOf('*');
        	if (star==-1)
        		break;
        	star+=i;        	
           
         /*  byte XOR=0;           
           while(i<star)
        	   XOR^=gsv_data.charAt(i++);           
           String schS=gsv_data.substring(i+1,i+3);
           int checkSum=Byte.parseByte(schS,16); 
            */
           i=star+3;                      
          // if (checkSum==XOR){
                String st=data.substring(dollar+3,dollar+6); 

                int hash=st.hashCode()&31;                                                              
                sentence[hash].parse(new Sequence(data.substring(dollar+3,star)));                                   	                
           // }else{              
             //   break;
            //}
            
        }  
        if (NMEA.HDOP==0)
        	NMEA.HDOP=GSA.VDOP=GSA.PDOP=99.99f;	
        hError=NMEA.HDOP*accuracy_of_GPS_Device;
        time.nmea();
        if (year!=NOT_SET){
        	
        }
        GSV.error= GSV.total!=GSV.cnt;
        if (speedK==NOT_SET)
        	speedK=speedN*0.54f;
        }catch (Exception e){
        	System.out.println("NMEA INIT error "+e.toString());
        	GSV.error=nodata=true;
        	}
    }


//****************************************************************************
 static  public double ParseDouble(final String s){
    try{
        if (s.length()==0)
            return 0;
        return Double.parseDouble(s);
    }catch (NumberFormatException e){nodata=true;return 0;}
 }
//****************************************************************************
 static public int ParseInt(final String s){
    try{
        if (s.length()==0)
            return 0;
        return Integer.parseInt(s);
    }catch (NumberFormatException e){nodata=true;return 0;}
 }

 //****************************************************************************
 static  public void setLocation(final Sequence st){
    if (l.init()==false){
        double td=ParseDouble(st.getNext());
        double latitude=Math.floor(td*0.01);
        latitude+=(td-latitude*100.0)*(1.0/60.0);
        if (st.getNext().equalsIgnoreCase("S"))
            latitude=-latitude;
        td=ParseDouble(st.getNext());
        double longitude=Math.floor(td*0.01);
        longitude+=(td-longitude*100.0)*(1.0/60.0);
        if (st.getNext().equalsIgnoreCase("W"))
            longitude=-longitude;
        l=new Location(latitude,longitude);
        gp=l.GetPoint();
     }else{
        st.getNext();st.getNext();st.getNext();st.getNext();
     }
 }
//****************************************************************************


 
 }

