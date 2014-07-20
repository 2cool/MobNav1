package mobnav.gps;

public class GSA implements SENTENCE{
	 static public boolean exist=false;
	 static public char  mode; 
	 static public int []prn=new int[12];
	 static public int cur_mode=0,used=0;
	 static public float PDOP,VDOP,hEfrror=99.99f;
	 public void parse(Sequence st){
		try{
			exist=true;
			mode=st.getNext().charAt(0);
			cur_mode=NMEA.ParseInt(st.getNext());	
			used=0;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			prn[used]=NMEA.ParseInt(st.getNext());
			if (prn[used]>0)
				used++;
			PDOP=(float)NMEA.ParseDouble(st.getNext());
			NMEA.HDOP=(float)NMEA.ParseDouble(st.getNext());		
			VDOP=(float)NMEA.ParseDouble(st.getNext());	
			
		}catch (Exception e){NMEA.nodata=true;System.out.println("GSA error "+e.toString());}
	}

}