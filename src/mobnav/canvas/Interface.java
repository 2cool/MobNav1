package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

import mobnav.gps.BTGPS;
import mobnav.tracks.Track;
import mobnav.tracks.TrackRecording;
import mobnav.tracks.Tracks;

public class Interface {
	
static  String	s_circuit="",s_circuit_stop="";
public static String s_m="";
static String s_toEnd="";
static String s_toLeader="";
static String s_lists_empty="";
public static String s_settings="";
public static String s_trackRecSet="";
public static String s_label="";
public static  String s_coment="",s_latitude="",s_longitude="",s_height="",s_names_label="",s_labelName="";
public static  String s_save="",s_yes="",s_no="",s_to_exit="",s_to_cancel="",s_delite="",s_select="";
public static  String s_back="",s_go_to="",s_route_from="",s_route_to="",s_route_thro="",s_direction="",s_exit="";
public static  String s_rename="",s_load="",s_edit="",s_list="",s_to_start="";
public static  String s_to_end="",s_browser="",s_return_2_map="",s_load_world_map="",s_no_map="";
public static  String s_start_recording_track="",s_stop_recording_track="";
public static  String s_create_track="",s_hide_track="",s_show_track="",s_unload="";
public static  String s_set_label="",s_select_labels="",s_deselect_label="",s_gps_on="";
public static  String s_gps_off="",s_map,s_track="",s_labels="",s_directionRGB="",s_mem_trackRGB="";
public static  String s_labelsRGB="",s_dostretchMAP="",s_dorotateMAP="",s_fontSize="";
public static  String s_сonvert="",s_pointA="",s_pointB="",s_OK="",s_sort="";
public static String s_information="";
public static  String s_kmh="",s_modes="",s_sellectAll4Del="",s_sellectAll4Add="",s_desellectAll4Del="";
public static  String s_desellectAll4Add="";
public static  String s_search="",s_findMe="",s_addLabel="",s_cancel="",s_clear="",isLoading="",isConvering="";

static private void initComands(){
	
	delite  =     new Command(s_delite,Command.ITEM,2);
	unload  =     new Command(s_unload,Command.ITEM,2);
	rename  =     new Command(s_rename,Command.ITEM,2);
	no =          new Command(s_no, Command.ITEM,1);
	back =       new Command(s_back, Command.BACK,1);
	select  =     new Command(s_select, Command.ITEM,3);
	exit    =     new Command(s_exit, Command.EXIT,3);
	ok  =         new Command("OK", Command.OK,1);
	yes=          new Command(s_yes,Command.EXIT,3);
	save=         new Command(s_save,Command.ITEM,2);
	cancel=		new Command(s_cancel,Command.ITEM,1);
	trackTo=		new Command("Проложить", Command.ITEM,1);
}

static public Command delite,unload,rename,no,back,select,exit,ok,yes,save,cancel,trackTo;

static public Image runningIcon,unloadIcon,nextIcon,inetIcon,bingIcon,openStreetIcon,sunIcon,addSIcon,directionIcon,cancelIcon, saveIcon,buttonIcon,selectIcon,noMapIcon,worldMapIcon,returnIcon,addIcon,browserIcon,settingsIcon,folderIcon,mapIcon,gpsIcon,trackIcon,labelIcon,exitIcon,editIcon,deliteIcon,icon;
static public Image []bIcon;
static public Image findMe, findMe_done, recordIcon,stopIcon,yahooIcon,newsImg,infoIcon,checkIcon, uncheckIcon,googleIcon,plusIcon, minusIcon,Aicon,Bicon,viaIcon,toEndIcon,toStartIcon;



static void ExitRequest(){
   Alert a = new Alert("!!!");//,"Are you sure?",null,AlertType.CONFIRMATION);
   a.setString(s_to_exit);

    a.addCommand(yes);
    a.addCommand(no);
    a.setCommandListener(new CommandListener(){
        public void commandAction(Command c, Displayable d){
            if (c == yes)
                MobNav1.mn.destroyApp(true);
            else if (c == no)
                MobNav1.display.setCurrent(MobNav1.gCanvas);
        }});
    MobNav1.display.setCurrent(a);
}






static  public Object devicesListMon=new Integer(0);
static public  List devicesList;
public static BTUtility btu;
static String btConnectionURL=null;
static public void Connect2BlueToothGPS(){
     new Thread(new Runnable() {
    public void run() {
            devicesList = new List("Select a Bluetooth Device", Choice.IMPLICIT, new String[0], new Image[0]);
            devicesList.addCommand(select);
            devicesList.setSelectCommand(select);
            devicesList.addCommand(exit);
            devicesList.setCommandListener(new CommandListener() {
                public void commandAction(Command c, Displayable s) {
                    if (c == select){
                    	Storage.btGPSName=devicesList.getString(devicesList.getSelectedIndex());
                        Loading.renameString("GPS Connecting");
                        btu.start();
                        MobNav1.display.setCurrent(MobNav1.gCanvas);
                    }
                    else if (c==exit){
                        Loading.stop();
                        MobNav1.display.setCurrent(MobNav1.gCanvas);
                        btConnectionURL=null;
                        synchronized(devicesListMon){devicesListMon.notify();}
                    }
                }
            });
            
            btu=new BTUtility();
            while (devicesList.size()==0){
            	if (Loading.CANCELED()){
            		return;
            	}
	            synchronized(devicesListMon){
	                    try {
	                        devicesListMon.wait(1000);
	                    } catch (InterruptedException ex) {}	                    
	            }
            }
            MobNav1.display.setCurrent(devicesList);
            synchronized(devicesListMon){
                 try {
                        devicesListMon.wait();
                    } catch (InterruptedException ex) {}
            }
            if (btConnectionURL!=null && btConnectionURL.startsWith("btspp://")){
                Storage.btGPSUrl=btConnectionURL;
                BTGPS.SetUrl(btConnectionURL);
                BTGPS.start();
            }
            Loading.stop();
      }
      }).start();
     
     
     Loading.start("GPS Searching", true, true,MobNav1.gCanvas);
}


// ###########################################################################
// ########################################################################
static public MenuStr CLEAR_MAP_LIST,NEXT_MAP,SETTINGS,GPS_ONOFF,BROWSER_MAP,RETURN2MAP,LOAD_MAP_WORLD,NO_MAP;
static public  MenuStr TRACK_ONOFF =         new MenuStr(s_hide_track,true,null);

static MenuStr []ONLINE_MAPS;//GOOGLE_MAP,GOOGLE_EARTH,TOPO_KRYM,OSM_KRYM,OPEN_STREET_MAP;
static MenuStr ADD_LABEL,CREATE_TRACK,MAP_M,TRACK_M,LABELS_M,SETTINGS_M,OPENED_TRACKS,BROWSER_TRACK;
static  MenuStr MAKETRACK         =new MenuStr("Проложить",true,null);
static MenuStr INET_TRACKING,RECORDING_TRACK,TRACK_SETTINGS,ЕЗДА_ПО_КРУГУ;

//static MenuStr SELECT_LABEL;
static  MenuStr BROWSER_LABELS;


static  MenuStr EXIT,MODES;
static public boolean update_menu=false;
static public void GPS_MENU_TEST(){
    GPS_ONOFF.ReName(BTGPS.isOn()?s_gps_off:s_gps_on);
}
public void loadLenguage(){
	
	//File
	InputStream is = getClass().getResourceAsStream("/rus.txt");

    BufferedRead br=new BufferedRead(is);
    
	
	try {
	s_circuit=br.readUTF_8String();	
	s_circuit_stop=br.readUTF_8String();
	 s_m=br.readUTF_8String();
	 s_toEnd=br.readUTF_8String();
	 s_toLeader=br.readUTF_8String();
	 s_lists_empty=br.readUTF_8String();
	 s_settings=br.readUTF_8String();
	 s_trackRecSet=br.readUTF_8String();
	  s_label=br.readUTF_8String();
	  s_coment=br.readUTF_8String();
	  s_latitude=br.readUTF_8String();
	  s_longitude=br.readUTF_8String();
	  s_height=br.readUTF_8String();
	  s_names_label=br.readUTF_8String();
	  s_labelName=br.readUTF_8String();
	  s_save=br.readUTF_8String();
	  s_yes=br.readUTF_8String();
	  s_no=br.readUTF_8String();
	  s_to_exit=br.readUTF_8String();
	  s_to_cancel=br.readUTF_8String();
	  s_delite=br.readUTF_8String();
	 s_select=br.readUTF_8String();
	 s_back=br.readUTF_8String();
	 s_go_to=br.readUTF_8String();
	 s_route_from=br.readUTF_8String();
	 s_route_to=br.readUTF_8String();
	 s_route_thro=br.readUTF_8String();
	 s_direction=br.readUTF_8String();
	 s_exit=br.readUTF_8String();
	 s_rename=br.readUTF_8String();
	 s_load=br.readUTF_8String();
	 s_edit=br.readUTF_8String();
	 s_list=br.readUTF_8String();
	 s_to_start=br.readUTF_8String();
	 s_to_end=br.readUTF_8String();
	 s_browser=br.readUTF_8String();
	 s_return_2_map=br.readUTF_8String();
	 s_load_world_map=br.readUTF_8String();
	 s_no_map=br.readUTF_8String();
	 s_start_recording_track=br.readUTF_8String();
	 s_stop_recording_track=br.readUTF_8String();
	 s_create_track=br.readUTF_8String();
	 s_hide_track=br.readUTF_8String();
	 s_show_track=br.readUTF_8String();
	 s_unload=br.readUTF_8String();
	 s_set_label=br.readUTF_8String();
	 s_select_labels=br.readUTF_8String();
	 s_deselect_label=br.readUTF_8String();
	 s_gps_on=br.readUTF_8String();
	 s_gps_off=br.readUTF_8String();
	 s_map=br.readUTF_8String();
	 s_track=br.readUTF_8String();
	 s_labels=br.readUTF_8String();
	 s_directionRGB=br.readUTF_8String();
	 s_mem_trackRGB=br.readUTF_8String();
	 s_labelsRGB=br.readUTF_8String();
	 s_dostretchMAP=br.readUTF_8String();
	 s_dorotateMAP=br.readUTF_8String();
	 s_fontSize=br.readUTF_8String();
	 s_сonvert=br.readUTF_8String();
	 s_pointA=br.readUTF_8String();
	 s_pointB=br.readUTF_8String();
	 s_OK=br.readUTF_8String();
	 s_sort=br.readUTF_8String();
	 s_information=br.readUTF_8String();
	 s_kmh=br.readUTF_8String();
	 s_modes=br.readUTF_8String();
	 s_sellectAll4Del =br.readUTF_8String();
	 s_sellectAll4Add =br.readUTF_8String();
	 s_desellectAll4Del =br.readUTF_8String();
	 s_desellectAll4Add =br.readUTF_8String();
	 Track.s_conduct   =br.readUTF_8String();
	 Track.s_inverse   =br.readUTF_8String();
	 s_search=br.readUTF_8String();
	 Track.s_running=br.readUTF_8String();
	 Track.s_find=br.readUTF_8String();
	 s_findMe=br.readUTF_8String();
	 s_addLabel=br.readUTF_8String();
	 s_cancel=br.readUTF_8String();
	 s_clear=br.readUTF_8String();
	 isLoading=br.readUTF_8String();
	 isConvering=br.readUTF_8String();
	 Track.len=br.readUTF_8String();
	 Track.nods=br.readUTF_8String();
	 Track.перепад=br.readUTF_8String();
	 Track.s_maxH=br.readUTF_8String();
	 Track.s_minH=br.readUTF_8String();
	 Track.s_allUp=br.readUTF_8String();
	 Track.s_allDown=br.readUTF_8String();
	 System.out.println(Track.s_allDown);
	 
	 initComands();
	 
	} catch (Exception e) {System.out.println("load lenguage "+e.toString());}
br.close();

}

static public void LoadIcons(){
    try {
    	findMe=			Image.createImage("/icons/findMe.png");
    	recordIcon=		Image.createImage("/icons/record.png");
    	stopIcon=		Image.createImage("/icons/stop.png");
    	runningIcon=	Image.createImage("/icons/runningIcon.png");
    	LCD.dig=		Image.createImage("/icons/digit.png");
    	unloadIcon=     Image.createImage("/icons/unload.png");
        nextIcon=       Image.createImage("/icons/next.png");
    	inetIcon=		Image.createImage("/icons/inetIcon.png");
    	bingIcon=		Image.createImage("/icons/bingIcon.png");
        sunIcon=		Image.createImage("/icons/sun.png");
        addSIcon=		Image.createImage("/icons/add.png");
        openStreetIcon=	Image.createImage("/icons/openStreet.png"); 
        directionIcon=	Image.createImage("/icons/direction.png");
        cancelIcon=		Image.createImage("/icons/cancel.png");
        saveIcon=Image.createImage("/icons/save.png");
        yahooIcon=Image.createImage("/icons/yahoo.png");
        newsImg=Image.createImage("/icons/NEWS.png");
        //compas=Image.createImage("/icons/compas.png");
     //   routIcons=Image.createImage("/icons/tt2.png");
        infoIcon=Image.createImage("/icons/infoIcon.png");
        uncheckIcon=Image.createImage("/icons/delite.png");
        checkIcon=Image.createImage("/icons/check.png");
        googleIcon=Image.createImage("/icons/googleIcon.png");
        toEndIcon=Image.createImage("/icons/toEndIcon.png");
        toStartIcon=Image.createImage("/icons/toStartIcon.png");
        viaIcon=Image.createImage("/icons/dd-via.png");
        Aicon=Image.createImage("/icons/A.png");
        Bicon=Image.createImage("/icons/B.png");
        bIcon=new Image[12];
        for (int i=0; i<10; i++)
            bIcon[i]=Image.createImage("/icons/"+Integer.toString(i)+".png");        
        bIcon[10]=Image.createImage("/icons/star.png");
        bIcon[11]=Image.createImage("/icons/#.png");   
        buttonIcon=Image.createImage("/icons/button.png");
        plusIcon=Image.createImage("/icons/+.png");
        minusIcon=Image.createImage("/icons/-.png");
        selectIcon=Image.createImage("/icons/selectIcon.png");
        noMapIcon=Image.createImage("/icons/noMapIcon.png");
        worldMapIcon=Image.createImage("/icons/worldMapIcon.png");
        returnIcon=Image.createImage("/icons/returnIcon.png");
        addIcon=Image.createImage("/icons/addIcon.png");
        browserIcon=Image.createImage("/icons/browserIcon.png");
        settingsIcon=Image.createImage("/icons/settingsIcon.png");
        folderIcon = Image.createImage("/icons/folderIcon.png");
        mapIcon=Image.createImage("/icons/mapIcon.png");
        gpsIcon=Image.createImage("/icons/gpsIcon.png");
        trackIcon=Image.createImage("/icons/trackIcon.png");
        labelIcon=Image.createImage("/icons/labelIcon.png");
        exitIcon=Image.createImage("/icons/exitIcon.png");
        deliteIcon=Image.createImage("/icons/delite.png");
        editIcon=Image.createImage("/icons/edit.png");
    } catch (IOException ex) {}
}
static public Menu navMenu;
static public void SetUpMenu(){
        
    
        Menu mapMenu=new Menu(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
        
        Menu onlineMaps=new Menu();
        

        
        ONLINE_MAPS=new MenuStr[InetMaps.im.length];
        for (int i=0; i<InetMaps.im.length;i++)
            onlineMaps.add(ONLINE_MAPS[i]=new MenuStr(InetMaps.im[i].getIcon(),InetMaps.im[i].getName(),true,null));

       
        mapMenu.add(BROWSER_MAP =  new MenuStr(browserIcon,s_browser,true,null));
        mapMenu.add(new MenuStr(inetIcon,"Online",true,onlineMaps));
        mapMenu.add(NO_MAP=new MenuStr(noMapIcon,s_no_map,true,null));
        mapMenu.add(NEXT_MAP=new MenuStr(Canvas.KEY_NUM2,"next map",RESENTLY_OPENED_LIST.Size()>1,null));
        mapMenu.add(CLEAR_MAP_LIST=new MenuStr(deliteIcon,"Clear maps list", RESENTLY_OPENED_LIST.Size()>1,null));
        mapMenu.add(RETURN2MAP =   new MenuStr(returnIcon,s_return_2_map,false,null));
        mapMenu.add(LOAD_MAP_WORLD=new MenuStr(worldMapIcon,s_load_world_map,true,null));       
        

      //  Menu trackSubMenu=new Menu();       
      //  trackSubMenu.add(TRACK_ONOFF);
      //  trackSubMenu.add(SETTINGS_M=new MenuStr(settingsIcon,s_settings,true,null)); 
        

        Menu trackMenu=new Menu();
        
        trackMenu.add(BROWSER_TRACK=new MenuStr(browserIcon,s_browser,true,null));
        trackMenu.add(MAKETRACK);
        trackMenu.add(INET_TRACKING =new MenuStr(Canvas.KEY_NUM4,"Через интернет", true, null));
        trackMenu.add(RECORDING_TRACK=new MenuStr(TrackRecording.records?stopIcon:recordIcon,TrackRecording.records?s_stop_recording_track:Interface.s_start_recording_track,true,null));
        trackMenu.add(ЕЗДА_ПО_КРУГУ=new MenuStr(null,s_circuit,false,null));
        trackMenu.add(TRACK_SETTINGS=new MenuStr(settingsIcon,s_trackRecSet,true,null));
        trackMenu.add(OPENED_TRACKS=new MenuStr(s_list,false,null));


        navMenu=new Menu();

        navMenu.add(MAP_M=new MenuStr(mapIcon,s_map,true,mapMenu));
        navMenu.add(TRACK_M=new MenuStr(trackIcon,s_track,true,trackMenu));
        navMenu.add(LABELS_M=new MenuStr(labelIcon,s_labels,true,null));//labelMenu));

        navMenu.add(GPS_ONOFF = new MenuStr(gpsIcon,s_gps_on,true,null));
        navMenu.add(SETTINGS=new MenuStr(settingsIcon,s_settings,true,null));
        navMenu.add(MODES=new MenuStr(Canvas.KEY_NUM5,s_modes,true,null));
        navMenu.add(EXIT= new MenuStr(exitIcon,s_exit,true,null));

    }

// ##########################################################################

static public void TrackMenuON(){
    if (Tracks.size()>0){
          MyCanvas.showTrack=true;
          TRACK_ONOFF.ReSet(null, s_hide_track, true, null);
          OPENED_TRACKS.On();
        }
}
static void UpdateMenu(){

    //добавить жпс актив
	ЕЗДА_ПО_КРУГУ.visible=BTGPS.active;
    if (update_menu){
        update_menu=false;
        if (Tracks.size()>0){
            OPENED_TRACKS.On();
        }else{
            OPENED_TRACKS.Off();
        }
       
    }
}
// ########################################################################

// ########################################################################
















//


private static void InetMap( int type){              
	MAP.redraw++;
    Storage.file2load(MAP.INET+InetMaps.im[type].getName()+FILE.MAP_E);

}


public static void MenuDo( MenuStr ms, boolean setOldMode){

    if (setOldMode)
        MyCanvas.SetOldMode();
    
    
    if (ms==BROWSER_TRACK)
        BrowserTrack();
    
    else if (ms==TRACK_SETTINGS)
    	TrackRecording.settings();
    
    else if (ms==ЕЗДА_ПО_КРУГУ){
    	if (Circuit.startTime==0){
    		ЕЗДА_ПО_КРУГУ.ReName(s_circuit_stop);
    		Circuit.start(BTGPS.gp());
    	}else{
    		Circuit.reset();
    		ЕЗДА_ПО_КРУГУ.ReName(s_circuit);
    	}
    }else if (ms==CLEAR_MAP_LIST){
    	RESENTLY_OPENED_LIST.Clear();
    	NEXT_MAP.Off();
    	CLEAR_MAP_LIST.Off();
    }
    else if (ms==NEXT_MAP){
    	RESENTLY_OPENED_LIST.getNext();
    }
    else if (ms==RECORDING_TRACK){
    	if (TrackRecording.records){
	    	TrackRecording.stop();
	    	RECORDING_TRACK.ReName(recordIcon,Interface.s_start_recording_track);
    	}else{
    		RECORDING_TRACK.ReName(stopIcon,Interface.s_stop_recording_track);
    		TrackRecording.startW();
    	}
    }
    else if (ms==OPENED_TRACKS)
        Tracks.opened();
            
    else if (ms==INET_TRACKING){
        
        MODE_A_B.Set();
        //System.out.println("bebebe");
        
    }else if (ms==MAKETRACK){
        MyCanvas.SetMode(MyCanvas.manual_track_make);                   
            MAKE_TRACK.CreateTrackM();               
    }
    else if (ms==LABELS_M)
        Labels.menu();
  //  else if (ms==TRACK_INFO)
    //    Settings.TrackInfo();
   // else if (ms== TO_START)
     //   MyCanvas.gp=MyCanvas.t.track_beg;
   // else if (ms==TO_END)
      //  MyCanvas.gp=MyCanvas.t.track_end;
    else if (ms==SETTINGS)
        Settings.AllSettings();
   // else if(ms == BROWSER_LABELS)
   //     GetLabelsFileName();
    else if (ms==NO_MAP)
        Storage.file2load(MAP.EMPTY);
  //  else if (ms ==SETTINGS_M)
   //     Settings.TrackSettings();
//    else if (ms ==SELECT_LABEL)
  //      List();
 //   else if (ms==UNLOAD_TRACK)
   //     Storage.unloadTrack = true;

    else if (ms ==  ADD_LABEL)
        Labels.add();
    else if (ms ==  RETURN2MAP)    	
        NAVI.SetGP(NAVI.map.center());
    else if (ms ==  LOAD_MAP_WORLD)
        Storage.file2load(Storage.world_map);
   // else if (ms==INET_MAP)
   //     Storage.SetMapFileN(MAP.INET);           
    else if (ms ==  TRACK_ONOFF)
        TrackON_OFF();
    else if (ms ==  EXIT)
        ExitRequest();
    else if (ms ==  GPS_ONOFF){
    	turnGps(!BTGPS.isOn());                
    }
    else if (ms==MODES){
        MyCanvas.nextScreenMode();
    }        
    else if (ms ==  BROWSER_MAP)
        GetMapFileName();

    else for (int i=0; i<InetMaps.im.length; i++){
        if (ms==ONLINE_MAPS[i]){
            InetMap(i);
        break;
        }
    }
   
   update_menu=true;
}
// ########################################################################
static private void TrackON_OFF(){
    MyCanvas.showTrack^=true;
    MAP.redraw++;
    TRACK_ONOFF.ReName(MyCanvas.showTrack?s_hide_track:s_show_track);
}
// ########################################################################
static private void BrowserTrack(){
    icon=trackIcon;
    FILE.Browser(MobNav1.gCanvas,FILE.PLT_E);
    
}

// ########################################################################
static public Displayable returnDsplayale=MobNav1.gCanvas;


static private void GetMapFileName(){
    MyCanvas.showTrack=true;
    icon=mapIcon;
    FILE.Browser(MobNav1.gCanvas,FILE.MAP_E);
}
// ########################################################################
static public void turnGps(final boolean on){
	Interface.manualGPSOff=!on;
	if (on)
		BTGPS.on();
	else
		BTGPS.off();
}

// ########################################################################
static public boolean manualGPSOff=false;







}
