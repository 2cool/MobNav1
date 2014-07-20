package mobnav.gps;

public class Sequence{ 
	 private String str=null;
	 private int substr_beg,substr_end;
	 public boolean EOS;
	 public  Sequence(final String s){
		 str=s;
		 substr_end=str.indexOf(',');
		 EOS=false;
	 }
	 //****************************************************************************
	 public String getNext(){
	    substr_beg=substr_end+1;
	    substr_end=str.indexOf(',', substr_beg);
	    if (substr_end==-1){
	        EOS=true;
	        return str.substring(substr_beg, str.length()).trim(); 
	     }
	    if (substr_beg==substr_end)
	    	return "0";
	    return str.substring(substr_beg, substr_end).trim(); 
	 }
}