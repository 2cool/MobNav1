package mobnav.gps;

import mobnav.canvas.Settings;
import mobnav.canvas.TEXT;




public class Time {
	public static final int notSet=-100;
	public int hours=notSet,minutes,seconds,year=notSet,month,day,msec;
	public Time(){};
	public void nmea(){
		if (NMEA.fix_time_UTC!=null)
			set(NMEA.fix_time_UTC);
		if (NMEA.year!=NMEA.NOT_SET){
			year=NMEA.year;
			if (year<2000)
				year+=2000;
			month=NMEA.month;
			day=NMEA.day;
		}
	}
	int []daysa={0,31,59,90,120,151,181,212,243,273,304,334};//,365};
	
	public double tDateTime_UTS(){
		int yars = year - 1900;
        double days = yars * 365 + (yars >> 2);            
        days += daysa[month - 1]   + 1+day + (hours + (double)minutes * (1.0/60) + (double)seconds * (1.0/3600) + (double)msec * (1.0/3600000)) * (1.0/24);
        if (month > 2 && (year & 0x11) == 0)
            days++;

        return days;
	}
	public Time(final Time t){
		msec=t.msec;
		seconds=t.seconds;
		minutes=t.minutes;
		hours=t.hours;
		day=t.day;
		month=t.month;
		year=t.year;
	}
	public long msec(){
		return sec()*1000+msec;
	}
	public long sec(){
		if (hours>=0)
			return ((long)seconds+(long)minutes*60+(long)hours*3600);
		else
			return -1;
		}
	public Time(int h, int m, int s,int ms){
		msec=ms;
		seconds=s;
		minutes=m;
		hours=h;

	}
	public Time(int h, int m, int s){
		msec=0;
		seconds=s;
		minutes=m;
		hours=h;
		//timeZone();
	}
	private int timeZone(){
		int hours=this.hours;
		hours+=Settings.TIMEZONE;
		if (hours>23){
			hours-=23;
		}
		if (hours<0)
			hours=24+hours;
		return hours;
	}
	static public long deltaMsec(final long msec , final long oldMsec){
		long ret=msec-oldMsec;
		if (ret<0)
			ret+=3600000*24;
		return ret;
	}
	static public long delta(final long sec , final long oldSec){
		long ret=sec-oldSec;
		if (ret<0)
			ret+=3600*24;
		return ret;
	}
	
	public Time(double msecF){
		double sec=Math.floor(msecF*(1.0/1000));
		msec=(int)(msecF-sec*1000);
		double h=Math.floor(sec*(1.0/3600));
		double m=Math.floor((sec-h*3600)*(1.0/60));
		double s=sec-h*3600-m*60;
		seconds=(int)s;
		minutes=(int)m;
		hours=(int)h;
	}
	public void set(String tm){
		try{
		String []t=TEXT.split(tm, '.');	
		msec=(t.length>1 && t[1].length()>0)?Integer.parseInt(t[1]):0;
		hours=Integer.parseInt(t[0].substring(0, 2));				

		
		minutes=Integer.parseInt(t[0].substring(2, 4));
		seconds=Integer.parseInt(t[0].substring(4, t[0].length()));
		}catch (NumberFormatException ex){hours=-100;}
	}
	public String getUTS(final char d,final boolean sec){// timed){
		if (hours==notSet)
			return "";	
		String s=TEXT.ndigits("0",hours)+d+TEXT.ndigits("0",minutes);
		if (sec)
			s+=d+TEXT.ndigits("0",seconds);
		return s;	   
	}
	public String get(final char d,final boolean sec){// timed){
		if (hours==notSet)
			return "";	
		String s=TEXT.ndigits("0",timeZone())+d+TEXT.ndigits("0",minutes);
		if (sec)
			s+=d+TEXT.ndigits("0",seconds);
		return s;	   
	}
	public String getUTC(final char d,final boolean sec){// timed){
		if (hours==notSet)
			return "";	
		String s=TEXT.ndigits("0",hours)+d+TEXT.ndigits("0",minutes);
		if (sec)
			s+=d+TEXT.ndigits("0",seconds);
		return s;	   
	}
	public static String toString(int sec,final boolean secF){
		if (sec<0)
			return "--:--";
		int h=(int)Math.floor(sec/3600);
		sec-=h*3600;
		int m=(int)Math.floor(sec/60);
		sec-=m*60;
		String ret=TEXT.ndigits("0",h)+':'+TEXT.ndigits("0",m);
		if (secF)
			ret+=":"+TEXT.ndigits("0", sec);					
		return ret;
		
	}
	public String dateUTS(final char d){
		if (year==notSet)
			return "";
		return Integer.toString(day)+d+Integer.toString(month)+d+Integer.toString(year);
		
	}
	public String hours(){return TEXT.ndigits("0",timeZone());}
	public String hoursUTS(){return TEXT.ndigits("0",hours);}
	public String minutes(){return TEXT.ndigits("0",minutes);}
	public String seconds(){return TEXT.ndigits("0", seconds);}
	}


