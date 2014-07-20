package mobnav.gps;

//import mobnav.canvas.Sattelites;

//**********************************************************************

public class GSV implements SENTENCE{	   
	   static public boolean exist=false;	  
	   static public GSV_DATA[] data=null; 	   
	   static public int cnt=0;
	   static public int total=0;
	   static public boolean error=true;
	   static public boolean draw=   false;
	   public void parse(Sequence st){
		exist=true;
	   	if (draw){
	       try{
	       st.getNext();
	       st.getNext();
	       if (data==null){
	           cnt=0;
	           total=NMEA.ParseInt(st.getNext());
	           data=new GSV_DATA[total];
	       }else
	    	   st.getNext();
	       while (st.EOS==false && cnt<total)
	           data[cnt++]=new GSV_DATA(st);        	          	          
	       error=false;
	       }catch (Exception e){
	    	   error=true;System.out.println("GSV error "+e);
	       	}
	   	}else{
	   		data=null;
	   		cnt=0;
	   		total=0;
	   	}	
	   }

	}