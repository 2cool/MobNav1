package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.rms.*;

import java.util.Date;
import java.util.Calendar;
//import java.util.Stack;

import javax.microedition.lcdui.*;

import mobnav.tracks.Tracks;

public class Storage {

static public String trackFNs=null;
static public byte[] trackIndexes2Load=null;
static public String  dMapDirName="/", dTrackDirName="/"  ,dLabelsDirName="/"   ,btGPSUrl=null ; 
static public String btGPSName=null;
static private final String rec="sDataRec_s__";
static final private String autosaveDir ="autosave/";
static public final String world_map="/world.map";
static private String []file2load_={null,null,null,null};//"btspp://001C88301110:1;authenticate=false;encrypt=false;master=false",null};
static private int file2loadi=0;
static public int defaultNameNumb=0;
private static final String settingsF="file://localhost/E:/mobNav.set";

static private final int r_MOBNAV_WAS_CLOSED=1;
static private final int r_SETTINGS			=2;
static private final int r_MEM_TEST			=3;

static private final int r_SIZE=4;

static public String UTF8="UTF-8";
static public int mem_test=0;



///---------------------------------------------------------------------------------
static public String getTracksFNs(){
	  int n=Tracks.size();
	  if (n>0){
	      String sb="";
	      for (int i=0; i<n; i++)
	          sb+=Tracks.getFNameAt(i);
	      return sb;
	  }else
		  return "null";
}
static public String getTrackIndexes2Load(){
	String out="null";
	int n=Tracks.size();
	byte[]b=null;
	if (n>0){
		b=new byte[n];	    
	    for (int i=0; i<n; i++)
	       b[i]=(byte)(' '+Tracks.getTrackNumber(i));
	    out=new String(b);
	}
	
    return out;
}
// ---------------------------------------------------------------------------------
static public void reloadTracks(){
	String fns=getTracksFNs();
	Tracks.removeAll();
    trackFNs=fns;	
}
// ---------------------------------------------------------------------------------
static public void setDefDirName(final String dir, final String fnn){
    String fn=fnn.toLowerCase();  	
    String dirName=dir;
    String autosave="/"+autosaveDir;
    if (dirName.toLowerCase().endsWith(autosave)){
	    while (dirName.toLowerCase().endsWith(autosave))
	        dirName=dirName.substring(0,dirName.length()-autosave.length());
	    dirName+="/";
    }
   //dirName=dir;
   if (fn.endsWith(FILE.MAP_E))
	   dMapDirName=dirName;
   else if (fn.endsWith(FILE.PLT_E))
	   dTrackDirName=dirName;
   else if (fn.endsWith(FILE.WPT_E))
	   dLabelsDirName=dirName;
}
// ---------------------------------------------------------------------------------
static public String getPath(final String fname){
    if (fname==null)    
        return "";
    String []fn=TEXT.split(fname,'/');
    if (fn.length==1)
    	return null;
    return fname.substring(0,fname.length()-fn[fn.length-1].length());
}
// ---------------------------------------------------------------------------------
static public void file2load(final String f2l){
	file2load_[file2loadi++]=f2l;
}
///---------------------------------------------------------------------------------
static private String oldMapFile=null;
static public void load(){
	if (Loading.isLoadingMode()==false){
		String f2l;
		loadTracks();
		if (file2loadi>0 && file2load_[file2loadi-1]!=null){		
			f2l=file2load_[--file2loadi];
			System.out.println("Loading "+f2l);
			
			if (f2l.endsWith(FILE.MAP_E)){
				FILE.dontShowRemove(oldMapFile);
				FILE.dontShowAdd(f2l);
				oldMapFile=f2l;
				loadMapFile(f2l);
			}
			else if (f2l.endsWith(FILE.PLT_E)){//треки
				FILE.dontShowAdd(f2l);
				loadPLTTrackT(f2l);

			}else if (f2l.endsWith(FILE.WPT_E))//метки				
				addLabelsFile(f2l);									
		}
		//if (file2loadi==0)
		//	trackIndexes2Load=null;
	}
}
///---------------------------------------------------------------------------------
public static boolean autoloadMode=false;
private static int lts_I=0,lts_P0=-1,lts_P1=-1;
static private void loadTracks(){
    if (NAVI.map!=null && (autoloadMode=trackFNs!=null)){  
        for (; lts_I<trackFNs.length(); lts_I++){
            if (trackFNs.charAt(lts_I)==':' && trackFNs.charAt(lts_I+1)=='/' && trackFNs.charAt(lts_I+2)=='/'){
                if (lts_P0==-1)
                    lts_P0=lts_I-4;
                else{
                    lts_P1=lts_I-4;                      
                    file2load(trackFNs.substring(lts_P0,lts_P1));             
                    lts_P0=lts_P1;
                    lts_I++;
                    return;
                }                
            }
        }
        if (lts_P0>=0){
        	file2load(trackFNs.substring(lts_P0,trackFNs.length()));
        }
        trackFNs=null;
        
    }
}
///---------------------------------------------------------------------------------
static public int memoryTest(){
    int i=0;
    try{      
            Image []img=new Image[32];
            while (i<23){
                img[i]=Image.createImage(256, 256);
                i++;
            }
        } catch (OutOfMemoryError e) {
        }
    if (i==0)
        System.out.println(" M E M O R Y   T E S T   E R R O R  ");
        return i;
    }
///---------------------------------------------------------------------------------
static public boolean isItFileName(final String fname){
    return (fname.length()>20 &&  fname.substring(0,17).equals("file://localhost/"));
}
///---------------------------------------------------------------------------------
private static boolean clearMemory=true;
private static final  Object dcm=new Integer(0);
private static boolean Request2ClearMemory(){
    Alert a = new Alert("Clear all settings?");//,"Are you sure?",null,AlertType.CONFIRMATION);
    a.setString("Clear all settings?");
    a.addCommand(Interface.yes);
    a.addCommand(Interface.no);
    a.setCommandListener(new CommandListener(){
        public void commandAction(Command c, Displayable d){
            clearMemory=  c == Interface.yes;
            	             
            MobNav1.display.setCurrent(MobNav1.gCanvas);
            synchronized(dcm){
            	dcm.notify();
            }
            
        }});
    MobNav1.display.setCurrent(a);
    synchronized(dcm){
    	try {
			dcm.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }    
    return clearMemory;
}
///---------------------------------------------------------------------------------

static void loadState(){
	
 try {
       RecordStore rs = RecordStore.openRecordStore(rec, true);
      try{ 
       byte []b=null;
       if (rs.getNextRecordID()>1 && (b=rs.getRecord(r_MOBNAV_WAS_CLOSED))!=null && (new String(b).equals("YES") || ! Request2ClearMemory())){
			rs.setRecord(r_MOBNAV_WAS_CLOSED, "NO".getBytes(), 0, "NO".length());    
               b=rs.getRecord(r_SETTINGS);
               if (b!=null)
            	   Settings.set(new String(b,UTF8));
              mem_test=Integer.parseInt(new String(rs.getRecord(r_MEM_TEST)));
              if (mem_test==0)
                  mem_test=memoryTest();
              System.out.println("memTest "+mem_test);
              rs.closeRecordStore();
              rs=null;
       }else{
    	   RecordStore.deleteRecordStore(rec);  
    	   rs.closeRecordStore();
    	   rs=null;
    	   setDefault();
         } 
      }finally{
    	  if (rs!=null)
    		  rs.closeRecordStore();
      }
    } catch (Exception ex) {                
        System.out.println("LoadState error "+ex.toString());
        setDefault();
    }
}
static private void setDefault(){	 	     	   
       mem_test=memoryTest();
       if (mem_test==0)
           mem_test=memoryTest();
       System.out.println("memTest new "+mem_test);            
       file2load(world_map);	
}
///---------------------------------------------------------------------------------
static public void clearRecrodStore(){
	String []list=RecordStore.listRecordStores();
	for (int i=0; i<list.length; i++){
		try {
			RecordStore.deleteRecordStore(list[i]);
		} catch (RecordStoreNotFoundException e) {
		} catch (RecordStoreException e) {
		}
	}
}

///---------------------------------------------------------------------------------

static public void saveState2file(){
	saveState();
	FileConnection fc=null;
	OutputStream os=null;
	try {
		try{		
		fc = (FileConnection) Connector.open(settingsF);
		if (fc.exists())
			fc.delete();
		fc.create();		
        os = fc.openOutputStream();
        os.write(Settings.get().getBytes("UTF-8"));
       
		
		}finally{
			if (os!=null)
				os.close();
			if (fc!=null)
				fc.close();
		}
	} catch (Exception e) {
		System.out.println("ERROR SAVESTATE2FILE "+e.toString());
	} 	
}
///---------------------------------------------------------------------------------
static public void loadStateFromfile(){
	Settings.SetDefault();
	FileConnection fc=null;
	InputStream is=null;
	try {
		try{		
		fc = (FileConnection) Connector.open(settingsF);
		if (!fc.exists())
			return;
		byte[]b=new byte[(int)fc.fileSize()];
        is = fc.openInputStream();
        is.read(b);
		String set=new String(b,"UTF-8");
		Settings.set(set);
		is.close();
		fc.close();
		
		//Storage.loadState();
		//loadState();
		}finally{
			if (is!=null)
				is.close();
			if (fc!=null)
				fc.close();
		}
	} catch (Exception e) {
		System.out.println("ERROR LOADSTATE2FILE "+e.toString());
	} 	
}
///---------------------------------------------------------------------------------
static public void saveState(){
        System.out.println("recordStoreSave");
        SaveMapSettings();
        try {
         RecordStore rs = RecordStore.openRecordStore(rec, true);
         if (rs.getNextRecordID()<=1)          
             for (int i=0; i<r_SIZE; i++)
                 rs.addRecord(null, 0, 0);
         
         
        try {
        byte[]b;        
        rs.setRecord(r_SETTINGS, b=Settings.get().getBytes(UTF8),0,b.length);        
        } catch (UnsupportedEncodingException ex) {System.out.println("write UTF-8 ERROR");}
        String i2s=Integer.toString(mem_test);
        rs.setRecord(r_MEM_TEST,i2s.getBytes(),0,i2s.length());
        rs.setRecord(r_MOBNAV_WAS_CLOSED, "YES".getBytes(), 0, "YES".length());    
        rs.closeRecordStore();
        //-------------------------------------------------------------------------------
      //  Labels.save();
       
        } catch (RecordStoreException ex) {
        	System.out.println("RECORD STORE SAVE ERROR "+ex);
        	}

    }
///---------------------------------------------------------------------------------
static public String getDefDir(final String fnn){
	String fn=fnn.toLowerCase(); 
	String ddir=dMapDirName;
	if (fn.endsWith(FILE.PLT_E))
		ddir=dTrackDirName;
	else if (fn.endsWith(FILE.WPT_E))
		ddir=dLabelsDirName;
	return ddir;
}
///---------------------------------------------------------------------------------
static public String getAutoSaveDir(final String fn){
	String ddir=getDefDir(fn);
			
    //file://localhost/E:/tohome.plt.trk";
    String dir="file://";
    if (ddir.equals("/"))
        dir+="localhost/E:/"+autosaveDir;
    else
        dir+=ddir+autosaveDir;
  //  String dir="file:///"+dirName[dir4Type]+autosaveDir;
    System.out.println("autosaveDir= "+dir);
    FileConnection dirc;
        try {
            dirc = (FileConnection) Connector.open(dir);
       
    if (dirc.exists()==false){
        dirc.mkdir();
        dirc.close();
    }
     } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    return dir;
}
///---------------------------------------------------------------------------------
static public String date2Name(boolean sHort){
    String name;
    Date date=new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    String r="";
    name = Integer.toString(calendar.get(Calendar.YEAR));
    if (sHort){    	
    	if (name.length()==4)
    		name=name.substring(2,4);
    }else
    	r=".";
    name+= r+TEXT.ndigits("0",calendar.get(Calendar.MONTH)+1);
    name+= r+TEXT.ndigits("0",calendar.get(Calendar.DAY_OF_MONTH));
    name+= "-"+TEXT.ndigits("0",calendar.get(Calendar.HOUR_OF_DAY));
    name+= TEXT.ndigits("0",calendar.get(Calendar.MINUTE));
    name+=TEXT.ndigits("0",calendar.get(Calendar.SECOND));    
    return name;
}
///---------------------------------------------------------------------------------

static int zoom=-1;
//static public String GetLabelsFileName(){return (fileName[LABELS]!=null)?fileName[LABELS]:"";}



static void SaveMapSettings(){

if (NAVI.map!=null){
	try {
		String s=
			//"0,"+
			Integer.toString(Settings.DIRECTION_COLOR,16)+","+
	        Integer.toString(Settings.TRACK_IN_MEMORY_RGB,16)+","+
	        Integer.toString(0xff00/*Settings.TRACK_RGB*/,16)+","+
	        Integer.toString(0,16)+","+
	        Float.toString(Settings.TRACK_OPACITY)+","+
	        Float.toString(5/*Settings.TRACK_WIDTH*/)+",";
		RecordStore rs = RecordStore.openRecordStore(NAVI.map.fname, true); 
		if (rs.getNumRecords()==0 )
			rs.addRecord(null, 0, 0);			
		rs.setRecord(1, s.getBytes(), 0, s.length()); 
  	    rs.closeRecordStore();
    } catch (Exception ex) { System.out.println("SaveMapSettings error "+ex.toString());  }
}

}
static void LoadMapSettings(final MAP map){

    try {
    	if (map!=null){
	    	RecordStore rs = RecordStore.openRecordStore(map.fname, true); 
	    	if (rs.getNumRecords()>0){
	    		String b=new String (rs.getRecord(1));
	    		rs.closeRecordStore();
	    		String []s=TEXT.split(new String(b), ','); 
	    		Settings.DIRECTION_COLOR=Integer.parseInt(s[0], 16);
	            Settings.TRACK_IN_MEMORY_RGB=Integer.parseInt(s[1], 16);
	//          Settings.TRACK_RGB=Integer.parseInt(s[2], 16);
	//          Settings.LABEL_RGB=Integer.parseInt(s[3],16);
	            Settings.TRACK_OPACITY=(float)Double.parseDouble(s[4]);
	//          Settings.TRACK_WIDTH=(float)Double.parseDouble(s[5]);            
	    	}else
	    		Settings.setDefMapColorSet();
    	}
    } catch (Exception ex) { System.out.println("LoadMapSettings error "+ex.toString());   }    
}
///---------------------------------------------------------------------------------
private static MAP map1=null;
static void loadMapFile(final String f2l){
    try{
   if (NAVI.map==null || !f2l.equals(NAVI.map.fname)){
	   Loading.start(FILE.getName(f2l, false)+Interface.isLoading, false, false,MobNav1.gCanvas);
      // if (MyCanvas.GetGP().x!=-1 && MyCanvas.map!=null)
        if (NAVI.map!=null){
            zoom=NAVI.map.getZoom();
            NAVI.map.closeMapInputStream();
            SaveMapSettings();
       }
        NAVI.map=null;                                              
        new Thread(new Runnable() {
            public void run() {
                    map1=new MAP(f2l);
                   if (map1.error){ 
                	   String err=map1.mapError;
                	   FILE.delete(f2l,true);
                	   map1=null;
                       mapError(err,true);                        
                   } else {
                	   RESENTLY_OPENED_LIST.add(map1.fname);
                        LoadMapSettings(map1);
                       map1.setZoom(zoom);                                         
                       NAVI.map=map1;            
                       map1=null;
                    }               
                Loading.stop();               
            }
        }).start();
    }     
    }catch (OutOfMemoryError e){mapError("Map Error "+e.toString(),false);}
    catch (Exception e){mapError("Map Error "+e.toString(),false);}
}
///---------------------------------------------------------------------------------
static private int mapError=0;
static public final int MAX_MAP_ERROR=5;
static public void mapError(final String err,final boolean forse){
	mapError++;
	if (forse || ++mapError>=MAX_MAP_ERROR){
		if (NAVI.map!=null && NAVI.map.fname.equals(world_map))
			Tracks.removeAll();
		else
			loadMapFile(world_map);
		mapError=0;        
	}       
    MyCanvas.SetErrorText(err);
}
///---------------------------------------------------------------------------------
static public void loadPLTTrackT(final String fName){   
	System.out.println(fName);
    Loading.start(FILE.getName(fName, false)+Interface.isLoading, true, false,MobNav1.gCanvas);
 new Thread(new Runnable() {
 public void run() {
    try{
        
        Tracks.loadPLT(fName);

        Loading.stop();
     }catch (OutOfMemoryError e){ System.out.println(e.toString());trackError("Недостаточно памяти",fName);  }
      catch (NullPointerException e){ System.out.println(e.toString());trackError("ошибкаNULL "+e,fName);   }
      catch (Exception e){ System.out.println(e.toString());trackError("ошибкаEx "+e,fName);   }

}}).start();
}
///---------------------------------------------------------------------------------
static public void addLabelsFile(final String l2l){
    if (l2l!=null){
        Labels.loadFromFile(l2l);        
    }
}
///---------------------------------------------------------------------------------
static public void trackError(String err,String tfn){
	 
	try {
		RecordStore.deleteRecordStore(FILE.hash(tfn));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	Loading.stop();
    //RemoveTrackFN(fileName[TRACK]);
   // td=null;
    MyCanvas.SetErrorText(err);
}
///---------------------------------------------------------------------------------
}
