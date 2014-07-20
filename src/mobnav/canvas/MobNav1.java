package mobnav.canvas;
/**
 *
 * @author 2cool
 */
//import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
//import javax.microedition.pim.ContactList;
//import javax.microedition.pim.PIM;
//import javax.microedition.pim.PIMException;

import mobnav.tracks.TrackRecording;



  // System.out.println(g.getClipHeight());

//byte[] serialData;
public class MobNav1 extends MIDlet{//  implements CommandListener{
    
 static public long inetIn=0;

// ##########################################################################
  static public Display display;// = Display.getDisplay(this);
  static public MyCanvas  gCanvas;
  static public MobNav1 mn;
// ##########################################################################
    
// ##########################################################################
   /*
    //-----------------------------------------------------------------------
private void call(String number) {
    
    ContactList contactList = null;
    String name="";
        try {
            contactList = (ContactList) PIM.getInstance().openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY);//, name);
            String[] allContactLists = PIM.getInstance().listPIMLists(PIM.CONTACT_LIST);
            int i=0;
            
        } catch (PIMException ex) {
            ex.printStackTrace();
        }
    
    
try {
MobNav1.this.platformRequest("tel:" + number);
} catch (ConnectionNotFoundException ex) {
// TODO: Exception handling
}
}

   * 
   */



//static void MyCanvas(){display.setCurrent(gCanvas);}
  
  
  
  
  
  
  
  
  
  
  
  
  double ddd;
public String[]bb=null;
    public void startApp()throws MIDletStateChangeException {
     try{
    	
    	 /*
    	 
    	 Point p=new Point(60,10);
    	 Point p0=new Point(0,0);
    	 Point p1=new Point(40,0);
    	 
    	 ddd=math.distFromDot2Line(p,p0,p1);    	     	
    	// ddd=math.distFromDot2Line(p0,p1,p);
    	// ddd=math.Point2SegmentDistance(p, p0,p1);
    	 
    	 
    	
    	 
    	 
    	 
    	 p=new Point(50,10);
    	 p0=new Point(60,0);
    	 p1=new Point(100,0);
    	 
    	 ddd=math.distFromDot2Line(p,p0,p1);     	     	
    	// ddd=math.distFromDot2Line(p0,p1,p);
    	// ddd=math.Point2SegmentDistance(p, p0,p1);
    	 
    	 ddd=ddd;
    	 
    	 
    	 
    	 
    	 
    	 
    	 
    	 
    	 
    	 
    	 */
    	 
    	     
    	 Interface.LoadIcons();
     //    call("0973807646");
        Settings.SetDefault();
        mn=this;
        display= Display.getDisplay(this);
        new Interface().loadLenguage();
        Storage.loadState();        
        
        Interface.update_menu=true;
        Interface.SetUpMenu();
        gCanvas = new MyCanvas(this);
        	        
        gCanvas.start();
        display.setCurrent(gCanvas);

        }catch (Exception ex){
            System.out.println(ex.toString());
        }
  }
    
// ################################### #######################################
    public void pauseApp() {
            
    }
// ##########################################################################    
    public void destroyApp(boolean unconditional) {
        TrackRecording.exit();
        Storage.saveState();
        Interface.turnGps(false);
        MyCanvas.keeprunning=false;
        display.setCurrent(null);
        notifyDestroyed();
    }
// ##########################################################################

// ##########################################################################
String twoDigits(int i){
        return ((i<10)?"0":"")+Integer.toString(i);
}
// ##########################################################################




}

