package mobnav.gps;
/**
 *
 * @author 2cool
 */


import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;

import mobnav.canvas.Interface;
import mobnav.canvas.Location;
import mobnav.canvas.MobNav1;
import mobnav.canvas.MyCanvas;
import mobnav.canvas.Point;
import mobnav.canvas.Settings;
import mobnav.canvas.Storage;
import mobnav.math.Aprox4;
import mobnav.tracks.TrackRecording;

public class BTGPS{

    //MyCanvas c;
   
//   static private Random r=new Random(1);
 //  static public NMEA NMEA=new NMEA();
   //static private Location ol=new Location();
	static private int errorConnectionCount=0;
   static public int max_hdop_error=4;
   public static boolean hdop_ok=true;
   static private Location []ol={new Location(),new Location(), new Location(), new Location()};
   static private long otime[]=new long[4];
   static private int oli=0;
   static private double  speed4,aprox4Height=0,average_speed=0,average_speedF=0;
   
   
   static private double maxSpeed4=0;
   static private long moving_timeMsec,stoped_timeMsec=1;
   static private double odometr=0;
   static public long moving_timeMsec(){return moving_timeMsec;}
   static public void set_movint_timeMsec(final long t){moving_timeMsec=t;}
   static public long stoped_timeMsec(){return stoped_timeMsec;}
   static public void set_stoped_timeMsec(final long t){stoped_timeMsec=t;}
   static public double getDistance(){return odometr;}
   static public void setDistance(final double d){odometr=d;}
   
   static public double maxSpeed4(){return maxSpeed4;}
   static public void setMaxSpeed4(final float s){maxSpeed4=s;}
   
   static private Aprox4 heightA=new Aprox4();
   
//   static private Aprox4 ddistA=new Aprox4();
   static private double elevation=0;
   
   static public double averageSpeed(){return average_speed;}
   static public double average_speedF(){return average_speedF;}

   
   
   
  
   static public int GetElevation(){return (int)elevation;}
   
   static public double GetAproxHeight(){return aprox4Height;}
   

   static long dt=0;
   static double oldDist=0;
  

   
   
   static private  int scnt=3;
   static private double[] speedA=new double[4];
   static private byte[] serialData;
   static private byte[] data;
   static private int lengthavai=0;
   static private InputStream is;
   static private String trackFileName="file:///e:/test.nmea";
   static private boolean readData=false;
// ##########################################################################
// ##########################################################################
static public void reset(int resetMask){
	if ((resetMask&1)>0){
		odometr=0;
		moving_timeMsec=stoped_timeMsec=1;  //иначе деление на ноль
	}
	if ((resetMask&2)>0)
		average_speed=average_speedF=0;
	if ((resetMask&4)>0)
		maxSpeed4=0;		
}
static void updSpeedA4(){
	int mask=0;
	if (speedA[scnt&3]>=NMEA.speedK)
		mask|=1;
	if (speedA[(scnt-1)&3]>=speedA[scnt&3])
		mask|=2;
	if (speedA[(scnt-2)&3]>=speedA[(scnt-1)&3])
		mask|=4;
	if (speedA[(scnt-3)&3]>=speedA[(scnt-2)&3])
		mask|=8;
	
	if (mask==0 || mask==15){
		speed4=NMEA.speedK;
	}else  	   
	    speed4=(speedA[0]+speedA[1]+speedA[2]+speedA[3]+NMEA.speedK)*0.2;
	scnt++;
	speedA[scnt&3]=NMEA.speedK;	
    if (maxSpeed4<speed4)
    	maxSpeed4=speed4;
    
    
}

static public boolean isOn(){return readData;}

static private void stop(){
    readData=false;
    while (active==true){
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {}
    }
    speed4=0;
    
    comR=null;
    aprox4Height=0;
}
static boolean TEST=true;
static boolean TEST(){
     try{
            
     fc = (FileConnection) Connector.open(trackFileName);//"file:///e:/Untitled.png");
    TEST=fc.exists();
    fc.close();

        }catch (Exception ex) { }
     return TEST;
}
static COMMReader comR;
static public void start()  {
    if (readData==false){       
        NMEA.nodata=true;
       
        if (TEST==false){
            comR=new COMMReader();
            comR.start();
        }else
            FILEReader.start();
    }
}
 // ##########################################################################
static public boolean active=false;
/*
static private byte[] resizeBuf(byte[]smalBuf,int newSize){
    byte[]newBuf=new byte[newSize];
    System.arraycopy(smalBuf, 0, newBuf, 0, smalBuf.length);  
    return newBuf;
}
*/






static class COMMReader extends Thread {
	String error="";
        public void run() {
            StreamConnection connection = null;            
            InputStream in=null;
        try{
            try{
            active=true;
            //StreamConnection connection = (StreamConnection)Connector.open(btConnectionURL);
            connection = (StreamConnection)Connector.open(url);//"btspp://001C88301110:1;authenticate=false;encrypt=false;master=false");
            in = connection.openInputStream();
            readData = true;
                      
            int sleepT=100;
            int readed;
            Settings.get_new_gps=false;
            while(readData){              
                long time=System.currentTimeMillis();
                int avai;
                if ((avai=in.available())>0){                     
                    serialData=new byte[avai];                                                                
                    readed=in.read(serialData);
                    while (avai>readed){
                        readed+=in.read(serialData, readed, avai-readed);
                    }
                    GPS_NMEA_Parser();                     
                    
                }       
                errorConnectionCount=0;
                long dt=System.currentTimeMillis()-time;                
                try{
                	Thread.sleep(sleepT-dt);
                }catch(Exception e) {}                                                                                                
            }
                           
            }finally{
                if (in!=null)
                    in.close();
                if (connection!=null)
                    connection.close();  
            }            
        }catch(Exception ioe){
        	error=ioe.toString();
            //ioe.printStackTrace();
        	errorConnectionCount++;
        }
        active=false;
        readData=false;
         
         off();
         if (errorConnectionCount>1){
        	 errorConnectionCount=0;
        	 MyCanvas.SetErrorText("GPS Disconected!!! "+error);
         }else if (Interface.manualGPSOff==false && error.indexOf("Denie")==-1)
        	 on();                 	
        synchronized(MyCanvas.update){
            MyCanvas.update.notify();
        }
        try {
            Manager.playTone(83, 3000, 100);
        } catch (MediaException ex) {}
        stop();

    }
 }
  // ##########################################################################

static private int counter=0;
static public int counter(){return counter;}
static private double dH;
static public double getdHaightA(){return dH;}
//static private long time;
static int errors=0;
public static long time;



static int GPS_NMEA_Parser(){
try{
    NMEA.init(serialData);
   
    
   // if (NMEA.nodata)
    //	return 0;
    
   if (GGA.fixQuality!=0 && (time=NMEA.time.msec())!=-1){  
	    if (counter<4){ 
	        aprox4Height=GGA.height;
	        ol[oli&3].Set(NMEA.l);
	        otime[oli&3]=time;
	        oli++;
	        elevation=0;
	        speedA[counter]=NMEA.speedK;
	        counter++;
	        return 0;
	    }
	    counter++;
	   // System.out.println(counter);
	
	    //UpdateHeight();
	    
	    
	    
	   
	    updSpeedA4();
	    //speed4=NMEA.l.getDistance(ol[oli&3])/(double)Time.delta(time, otime[oli&3]);
	    //if (maxSpeed4<speed4)
	    	//maxSpeed4=speed4;
	    
	    ddistance= NMEA.l.getDistance(ol[(oli-1)&3]);     
	 // ddistanceA=ddistA.get(ddistance);     
	    odometr+=ddistance;
	    
	    
	    	long deltaMsec=Time.deltaMsec(time,otime[(oli-1)&3]);
		    if (speed4<0.3)
		    	stoped_timeMsec+=deltaMsec;
		    else
		    	moving_timeMsec+=deltaMsec;		    
		    average_speed=odometr*1000/(double)moving_timeMsec;
		    average_speedF=odometr*1000/(double)(moving_timeMsec+stoped_timeMsec);
	    	
	    	   
	    
	    
	    
	    ol[oli&3].Set(NMEA.l);
	    otime[oli&3]=time;
	    oli++;
   //          System.out.println(MyCanvas.LONG_SLEEP_TIME+" "+MyCanvas.sleep_time) ;        
   }
}catch(Exception e) {MyCanvas.SetErrorText("GPS "+e.toString());} 
    if (MyCanvas.sleep_time==0 && 	MobNav1.display.getCurrent()==MobNav1.gCanvas)
        synchronized(MyCanvas.update){
            //System.out.println("update BlueTooth");
            MyCanvas.update.notify();
        }
   // if (hdop_ok=(GGA.fixQuality!=0 && NMEA.HDOP < max_hdop_error))
        synchronized(TrackRecording.update){
        	TrackRecording.update.notify();
        	
        }
    return 1;
   
}

//  public double oldLongitude(){return lon[(ctr-1)&BS];}
//  public double oldLatitude(){return lat[(ctr-1)&BS];}
  static private void UpdateHeight(){
    double ah=heightA.get(GGA.height);
    dH=ah-aprox4Height;
    elevation+=dH;    
    aprox4Height=ah;
  }

  static public Location l(){return NMEA.l;}
  static public Point gp(){return NMEA.gp;}
  static public double GetHeight(){return GGA.height;}


  //public double longitude(){return lon;}
  //public double latitude(){return lat; }
  
  //public double longitudeA(){return lona; }
  //public double latitudeA(){return lata; }
  static public double speed4A(){return speed4;}
  static public double speed(){return NMEA.speedK;}
  static private double ddistance=0;
  static private double ddistanceA=0;
  static public double GetDDistanceA(){return ddistanceA;}

  static public double GetDDistance(){return ddistance;}

  // ##########################################################################
  static private FileConnection fc=null;
  /*
  static private void CloseFile(){
        try {
            is.close();
            fc.close();
        } catch (IOException ex) { }
  }
  */
  static private  void OpenFile(){
        try{
            data=new byte[1000];
             fc = (FileConnection) Connector.open(trackFileName);//"file:///e:/Untitled.png");
            is = fc.openInputStream();

        }catch (IOException ex) {
            System.out.println(trackFileName+" --- "+ex);
        }
    }
  static private String url;
  static public void SetUrl(final String url_){url=url_;}
   // ##########################################################################
static private Thread FILEReader =new Thread(new Runnable() {
        public void run() {
          active=true;
          OpenFile();                      
          int b=0;
            readData = true;
           // int cnt=0;
            int i=0;
            while(readData == true){    
            	System.out.println("XXX");
             try{
                //b!=-1) {
            	 if (lengthavai>0){
            		 data[0]=data[lengthavai-5];
            		 data[1]=data[lengthavai-4];
            		 data[2]=data[lengthavai-3];
            		 data[3]=data[lengthavai-2];
            		 data[4]=data[lengthavai-1];
            		 data[5]=data[lengthavai];
            		 i=6;
            		 System.out.println("copped");
            	 }else{
            		 i=0;
            	 }
                                
                while (i<data.length && (b=is.read())!=-1){
                    data[i]=(byte)b;
                    if ((i>=6) &&
                        data[i-2]==data[3] &&
                        data[i-1]==data[4] &&
                        data[i-0]==data[5]
                         )
                    {
                    	System.out.println("coppedOKK");
                        lengthavai=i;
                        break;
                    }
                    i++;
                }
                i=0;
                serialData=new byte[lengthavai-6];                
                System.arraycopy(data, 0, serialData, 0, lengthavai-6);  
                //if (++cnt<10)
                System.out.println(serialData.length);
                GPS_NMEA_Parser();
                
                Thread.sleep(1000);
            } catch(Exception e) {
                MyCanvas.SetErrorText("GPS fail "+e);
            }
          }
          active=false;
    }
  });
// ##########################################################################
static public void on(){	
   if (isOn()==false){
       if (TEST()==true)
           start();
       else
           if (Storage.btGPSUrl==null || Settings.get_new_gps){
                Interface.Connect2BlueToothGPS();                
           }else{
               SetUrl(Storage.btGPSUrl);
               start();
           }
     //  GPS_ONOFF.ReName(s_gps_off);
    }
}
static public void off(){
	
   Interface.btu=null;
   stop();
  // GPS_ONOFF.ReName(s_gps_on);
}


 


}
 