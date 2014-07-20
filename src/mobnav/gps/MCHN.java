package mobnav.gps;

public class MCHN implements SENTENCE{
	static public int []chn=new int[32];
	static public int chn_stat=0;
	static public int n=0;
	static public boolean exist=false;
	static private int parseChn(final String str){
		int c=0;
		c|=NMEA.ParseInt(str.substring(0,2));
		if (c>0)
			n++;
		c|=(NMEA.ParseInt((str.substring(2,4))))<<8;
		int i=NMEA.ParseInt((str.substring(4,5)));	
		chn_stat+=1<<(i<<3);
		c|=i<<16;
		return c;
	}
	public void parse (Sequence st){//PMTKCHN
		exist=true;
		chn_stat=0;
		try{
		int i=0;	
		chn[0]=parseChn(st.getNext());	
		do{
			chn[++i]=parseChn(st.getNext());
		}while (i<31 && st.EOS==false);
		
		}catch (Exception e){
	    		System.out.println("MCHN error "+e);
	    }		
	}
}