package mobnav.canvas;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Stack;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;


public class FILE {
	static private String exp="";
	//static private String []dontShow=null;
	static public String dir;
	static private Displayable od;
	static private List ocurr;// = (List)d;
	//-------------------------------------------------------------------------------
	static private void saveCurPos(final Displayable d){
	    od=d;
	    ocurr = (List)d;
	    setIndex(currDirName,ocurr.getSelectedIndex());
	}
	static public String getDir(final String fname){
		return fname.substring(0, fname.lastIndexOf('/')+1);
	}
	//-------------------------------------------------------------------------------
	static private String getFullName(final String browserName){
		   return "file://localhost/" + currDirName + browserName;
		}
	//-------------------------------------------------------------------------------
	static public boolean exists(final String fname){
        boolean ret=false;
       try {
            FileConnection fc = (FileConnection) Connector.open(fname);
            ret=fc.exists();
            fc.close();
        } catch (IOException ex) {return false;}
          catch (IllegalArgumentException ex){return false;}
        return ret;
    }
	//-------------------------------------------------------------------------------
  static void get(String browserName){
    browserName=getFullName(browserName);
    Storage.file2load(browserName);
    Storage.setDefDirName(currDirName,browserName);
    MyCanvas.Update();
     MobNav1.display.setCurrent(Interface.returnDsplayale);
  }
static public String hash(final String s){return ""+s.hashCode();}

	static public boolean rename(final String fn, final String newName){
		 if (! getName(fn,true).equals(newName)){
			 FileConnection fc=null;
			 RecordStore rs=null,nrs=null;
			try{
				try{
		     String newFName=Storage.getPath(fn)+newName;
			 fc = (FileConnection) Connector.open(fn);
	         fc.rename(newName);
	         fc.close();
	         fc=null;
	         RESENTLY_OPENED_LIST.Rename(fn,newFName);
	         String hc=hash(fn);
	         rs = RecordStore.openRecordStore(hc, true); 
	         nrs= RecordStore.openRecordStore(hash(newFName), true); 
	         
	         for (int i=1; i<=rs.getNumRecords(); i++){	        	 	        				
		     	byte []b=rs.getRecord(i);
		     	int id=nrs.addRecord(null,0,0);
		     	nrs.setRecord(id,b, 0, b.length);
		     	//rs.setRecord(i, b, 0, b.length);
		     }
	           	
         	rs.closeRecordStore();
         	rs=null;
         	RecordStore.deleteRecordStore(hc);	         		         	
         	nrs.closeRecordStore();
         	nrs=null;
	         		         	                   
	         }finally{
	             if (fc!=null)
	                 fc.close();
	             if (rs!=null)
	             	rs.closeRecordStore();
	             if (nrs!=null)
	            	 nrs.closeRecordStore();
	         }
	     } catch (Exception ex) {
	    	 System.out.println("FILE RENAME ERROR "+ex.toString());
	    	 return false;
	    	 }
		 }
		 return true;
	}
	static void rename(final String browserName){

	    final   TextField name=new TextField("", browserName.substring(0, browserName.length()-exp.length()), 50, TextField.ANY);
	    Form f = new Form(Interface.s_names_label);
	    f.append(name);
	    f.addCommand(Interface.exit);
	    f.addCommand(Interface.ok);
	    f.setCommandListener(new CommandListener() {
	        public void commandAction(Command c, Displayable s) {
	            if (c==Interface.ok){	               	               
                    String fn=getFullName(browserName);	                   
                    String newName=name.getString()+ exp;
                    rename(fn,newName);	                   
	            }
	            showCurrDir();
	        }
	    });
	    MobNav1.display.setCurrent(f);
	  }
	  // ###########################################################################
	 

public static void delete(final String fname,final boolean onlySettings){		  		 
	FileConnection fc=null;
	 try {
	     try{
	    	 if (onlySettings==false){
			    fc = (FileConnection) Connector.open(fname);
			    if (fc.exists()){
			    	fc.delete();	    
			    	fc.close();
			    }
	    	 }
	    RESENTLY_OPENED_LIST.Remove(fname);
	    try{
	    RecordStore.deleteRecordStore(hash(fname));
	    }catch(Exception ex){}
	     }finally{
	         if (fc!=null)
	             fc.close();
	     }
	} catch (Exception ex) {System.out.println("FILE DEL ERROR "+ex.toString());}		            
}
//----------------------------------------------------------------
	  static void deleteBrowserName(final String browserName){
	    Alert a = new Alert(browserName,Interface.s_delite+"?",null,AlertType.WARNING);
	    
	   // a.setString(MyI.s_delite+"?");
	    a.addCommand(Interface.yes);
	    a.addCommand(Interface.no);
	    a.setCommandListener(new CommandListener(){
	        public void commandAction(Command c, Displayable d){
	            if (c == Interface.yes){	            		            		            		            	 	                 	            
	                FileConnection fc=null;
	                 try {
	                     try{
	                    String fn=getFullName(browserName);
	                    fc = (FileConnection) Connector.open(fn);
	                    fc.delete();
	                    fc.close();
	                    RESENTLY_OPENED_LIST.Remove(fn);
	                    try{
	                    RecordStore.deleteRecordStore(hash(fn));
	                    }catch(Exception ex){}
	                     }finally{
	                         if (fc!=null)
	                             fc.close();
	                     }
	                } catch (Exception ex) {System.out.println("FILE DEL ERROR "+ex.toString());}	                
	            }
	            showCurrDir();
	        }});
	        MobNav1.display.setCurrent(a);
	  }

	// ############################################################################
	////----------------------------------------------------------------------
	private final static String MEGA_ROOT = "/";
	  //private String currDirName="E:";
	 //static private String currDirName="E:/";//MEGA_ROOT;
	static private String currDirName=MEGA_ROOT;
	  private final static String UP_DIRECTORY = "..";

	  private final static String SEP_STR = "/";
	  private final static char   SEP = '/';

	  //----------------------------------------------------------------------
	  static private String history="";
	  static public String getBrowserHistory(){
		if (history==null || history.length()==0)
			return "null";
		else
			return history;
	  }
	  static public void setBrowserHistory(final String s){	
		  if (!s.endsWith("null"))
			  history=s;
			  
	 }
	  static private int getIndex(String cdn){	
		  cdn+=FILE.exp;
		  if (history.length()>0){
			  int i=history.indexOf(cdn);
			  if (i!=-1){
				  i+=cdn.length();	
				  try{
				  return Integer.parseInt(history.substring(i,i+3));
				  }catch(NumberFormatException e){history=null;return 0;}
			  }
		  }
		  return 0;
	  }
	  //----------------------------------------------------------------------
	  static private void setIndex(String cdn, final int i){
		  cdn+=FILE.exp;
		  if (history.length()>0){
			  int n=history.indexOf(cdn);
			  if (n!=-1){
				  history=history.substring(0,n+cdn.length())+TEXT.ndigits("00",i)+history.substring(n+3);
			  }else
				  history+=cdn+TEXT.ndigits("00",i);			  
		  }else
			  history=cdn+TEXT.ndigits("00",i);
	  }
	  
	  
	  
	  static private Stack dontShowS=new Stack();
	  static public void dontShowAdd(final String fname){
		  if (fname!=null && fname.length()>0)
			  dontShowS.addElement(fname);
	  }
	  static public void dontShowRemove(final String fname){
		  if (fname!=null && fname.length()>0){
			  int i=dontShowS.indexOf(fname);
			  if (i>=0)
				  dontShowS.removeElementAt(i);		
		  }
	  }
	  //----------------------------------------------------------------------
	 static  public void Browser(final Displayable dis, final String exp){
		    FILE.exp=exp;

		 
	     Interface.returnDsplayale=dis;
	      currDirName=Storage.getDefDir(exp);//mn.s.dirName[mn.s.fileNameI];
	      showCurrDir();
	  }
	 static public  void showCurrDir()  {

	       //Temp();if (true)return;
	    Enumeration e;
	    FileConnection currDir = null;
	    List browser;

	    try    {
	      if (MEGA_ROOT.equals(currDirName)){
		        e = FileSystemRegistry.listRoots();
		        browser = new List(currDirName, List.IMPLICIT);
	      } else  {
		        currDir = (FileConnection)Connector.open("file://localhost/" + currDirName);
		        e = currDir.list();
		        browser = new List(currDirName, List.IMPLICIT);
		        browser.append(UP_DIRECTORY,null);
	      }
	      Stack st = new Stack();
	      while (e.hasMoreElements())
	          st.push((String)e.nextElement());
	      int i=0;
	      //сперва файлы
	      while (i<st.size()){
	            String browserName=((String)st.elementAt(i++));
	            if (browserName.charAt(browserName.length()-1) != SEP && browserName.toLowerCase().endsWith(exp)){
	                int dsi=0;            
	                for (; dsi<dontShowS.size(); dsi++){ 
	                	String fname=(String)dontShowS.elementAt(dsi);
	                    if (fname.endsWith(currDirName+browserName))
	                        break;
	                }
	                if (dsi==dontShowS.size())                                        
	                       browser.append( browserName.substring(0,browserName.length()-exp.length()),Interface.icon);            
	            }
	      }
	     
	      i=0;
	      //потом фолдеры
	      while (i<st.size()){
	            String browserName=((String)st.elementAt(i++));
	            if (browserName.charAt(browserName.length()-1) == SEP)
	                browser.append(browserName,Interface.folderIcon);
	      }
	      browser.setSelectCommand(Interface.select);
	      browser.addCommand(Interface.exit);
	      browser.addCommand(Interface.back);
	      browser.setCommandListener(bcl);
	      if (currDir != null)
	        currDir.close();
	      
	      
	      int in=getIndex(currDirName);
	      if (in>=browser.size())
	    	  in=browser.size()-1;
	      
	      
	      browser.setSelectedIndex(in, true);	     
	      MobNav1.display.setCurrent(browser);
	    }
	    catch (IOException ioe) {System.out.println("BROWSER ERROR "+ioe.toString());}
	  }
	 static private void Continue(){
		// int i=ocurr.getSelectedIndex();
		// setIndex(currDirName,i);
	      final String currFile = ocurr.getString(ocurr.getSelectedIndex());

	     List fMenu = new List(currFile, List.IMPLICIT);
	     fMenu.append(Interface.s_load, Interface.icon);
	     fMenu.append(Interface.s_rename, Interface.editIcon);
	     fMenu.append(Interface.s_delite, Interface.deliteIcon);
	     fMenu.setSelectCommand(Interface.select);
	     fMenu.setCommandListener(bclc);
	     fMenu.addCommand(Interface.back);

	     MobNav1.display.setCurrent(fMenu);
	 }


	  static CommandListener bclc=new CommandListener(){
	      public void commandAction(Command c, Displayable d){
	        if (c == Interface.select){
	             List curr = (List)d;
	            final int ind = curr.getSelectedIndex();
	            switch (ind){
	                case 0:BrowserView(od);break;
	                case 1:BrowserRename(od);break;
	                case 2:BrowserDelite(od);break;
	            }
	          }else if (c==Interface.back)
	              showCurrDir();


	      }};



	   //----------------------------------------------------------------------
	  static private void traverseDirectory(String browserName)  {
	    if (currDirName.equals(MEGA_ROOT))  {
	      if (browserName.equals(UP_DIRECTORY)){
	          MobNav1.display.setCurrent(Interface.returnDsplayale);
	        return;
	        }
	      currDirName = browserName;
	    }else if (browserName.equals(UP_DIRECTORY)) {
	      int i = currDirName.lastIndexOf(SEP, currDirName.length()-2);
	      if (i != -1)
	            currDirName = currDirName.substring(0, i+1);
	      else
	            currDirName = MEGA_ROOT;
	    }else
	        currDirName = currDirName + browserName;
	    showCurrDir();
	  }
	  static private void BrowserView(Displayable d){
	     // saveCurPos(d);
	      //oindex=0;
	      //List curr = (List)d;
	      final String currFile = ocurr.getString(ocurr.getSelectedIndex());
	      
	      new Thread(new Runnable() {
	        public void run() {
	          if (currFile.endsWith(SEP_STR) ||  currFile.equals(UP_DIRECTORY))
	            traverseDirectory(currFile);
	          else
	            get(currFile+exp);
	        }
	      }).start();
	   }

	  static void BrowserRename(Displayable d){
	        final String currFile = ocurr.getString(ocurr.getSelectedIndex())+exp;
	        rename(currFile);
	    }
	  static void BrowserDelite(Displayable d){
	        final String currFile = ocurr.getString(ocurr.getSelectedIndex())+exp;
	        deleteBrowserName(currFile);
	  }
	   //----------------------------------------------------------------------
	  static CommandListener bcl=new CommandListener(){
	      public void commandAction(Command c, Displayable d){
	        if (c == Interface.back){
	            traverseDirectory(UP_DIRECTORY);
	        } else if (c == Interface.exit)
	            MobNav1.display.setCurrent(Interface.returnDsplayale);
	        else if (c == Interface.select){
	        	saveCurPos(d);
	             List curr = (List)d;
	            final String currFile = curr.getString(curr.getSelectedIndex());
	            boolean dir,upDir;
	            if (dir=currFile.endsWith(SEP_STR))
	                BrowserView(d);
	            if (upDir=currFile.endsWith(UP_DIRECTORY)){
	                traverseDirectory(UP_DIRECTORY);
	            }
	            if (dir==false && upDir==false){
	                
	                Continue();
	            }
	        }
	      }};
	//----------------------------------------------------------------------
	static public String PLT_E=".plt";
	static public String WPT_E=".wpt";
	static public String MAP_E=".map";
	// ---------------------------------------------------------------------------------
	static public String getName(final String fname,final boolean ext){
	    if (fname==null)
	        return "";
	   String []fn=TEXT.split(fname,'/');
	   String sh=fn[fn.length-1]; 
	   if (sh.startsWith(MAP.INET, 0))
		   sh=sh.substring(MAP.INET.length(),sh.length());
	   if (ext==false){
		   int beg=0,end;
		   while ((end=sh.substring(beg).indexOf('.'))!=-1){
			   beg+=end+1;
		   }
		   if (beg>0)
			   sh=sh.substring(0,beg-1);	   
	   }
		   
	   return sh;
	}


}
