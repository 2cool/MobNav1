package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */

//import java.io.UnsupportedEncodingException;

import java.util.Stack;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;

import mobnav.gps.BTGPS;
import mobnav.gps.NMEA;
import mobnav.tracks.Path;
import mobnav.tracks.Track;
import mobnav.tracks.TrackRecording;
import mobnav.tracks.Tracks;

public class Settings {
	static public final int COLORS[]={0, 0x0000ff, 0x00ff00, 0x00ffff, 0xff0000, 0xff00ff, 0xffff00, 0xfefefe,0xff9933,0xCCCC33,0x99CC33,0x0099cc,0x0066cc,0x80,0xd90093,0xcc0066,0xcc0033};
static public boolean get_new_gps=false;
static public int TIMEZONE=3;   
static public int TRIANGLE_ALPHA,DIRECTION_COLOR;
static public int TRACK_IN_MEMORY_RGB,FONT_I,D_ANGLE_2_CHANGE;
static public float SCROLING_MAP_BREAK,TRACK_OPACITY;
static public long SET_LIGHTS_DTIME;//
static public boolean DO_BEEP ;
static public int rotate_map=0;//0-0,1-90,2-180,3-270-4=auto
static public int locaView=2;
static public int working_screensMask=-1;
static public long exitTime=0;
static public String accept_language="ru,en-us";

public static double maxSpeed=55.5; //~200 кm/ч


// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
static public final int 	MAX_YAW_IN_METERS_FOR_TRACK=2;
static public final int 	MAX_YAW_IN_METERS_FOR_MEMORY_TRACK=4;
static public final int   	MIN_DIS_TO_SHOW_LABEL_NAME_IN_PIXELS=15;
static public final double  MIN_DIS_TO_SHOW_LABEL_NAME_IN_METERS=100;


static private final int FONT_SIZE[]={Font.SIZE_SMALL,Font.SIZE_MEDIUM,Font.SIZE_LARGE};

static public void setDefMapColorSet(){
	Settings.DIRECTION_COLOR=		0xFF;
    Settings.TRACK_IN_MEMORY_RGB=	0xff0000;
}

static private int getInt(final Stack s, final int ind,int radix){	
	return Integer.parseInt((String)s.elementAt(ind), radix);
}
static private long getLong(final Stack s, final int ind){
	return Long.parseLong((String)s.elementAt(ind));
}
static private float getFloat(final Stack s, final int ind){
	return Float.parseFloat((String)s.elementAt(ind));
}
static private boolean getBoolean(final Stack s, final int ind){
	return ((String)s.elementAt(ind)).equals("1");
}
public static final char separator = '\n';


//-------------------------------------------------------------------------------------------
static public String get(){
	try{
	String s="";	
	s+=Integer.toString(FONT_I)+							separator;
	s+=((DO_BEEP)?"1":"0")+					separator;
	s+=Float.toString(SCROLING_MAP_BREAK)+			separator;
	s+=Long.toString(SET_LIGHTS_DTIME)+			separator;
	s+=Integer.toString(rotate_map)+					separator;
	s+=Integer.toString(D_ANGLE_2_CHANGE)+			separator;	
	s+=((Labels.dist_sort)?"1":"0")+					separator;	
	s+=Integer.toString(TIMEZONE)+						separator;	
	s+=Integer.toHexString(TRIANGLE_ALPHA)+		separator;	
	s+=Float.toString(TRACK_OPACITY)+				separator;
	s+=Integer.toString(locaView)+						separator;  //10
	s+=Integer.toString(Track.max_zoom)+			separator;
	s+=Integer.toString(NAVI.getGP().x)+				separator;    
    s+=Integer.toString(NAVI.getGP().y)+				separator;
    s+=Integer.toString(NAVI.map.getZoom())+		separator;
    s+=((MAP.offline)?"1":"0")+						separator;
    s+=Integer.toString(Storage.defaultNameNumb)+		separator;
    s+=Storage.dMapDirName+							separator;
    s+=Storage.dTrackDirName+							separator;                   
    s+=Storage.dLabelsDirName+						separator;
    s+=Integer.toString(BTGPS.max_hdop_error)+	separator;  //20
    s+=Integer.toString(MyCanvas.lights)+					separator;
    
	s+=RESENTLY_OPENED_LIST.GetList()+								separator; //22
	s+=Storage.getTracksFNs()+										separator; //23
	s+=Storage.getTrackIndexes2Load()+								separator; //24
	
	
	if (Storage.btGPSUrl!=null){									//25
        s+=Storage.btGPSUrl+										separator;
	    if (Storage.btGPSName=="")
	        Storage.btGPSName="last";
	    s+=Storage.btGPSName+										separator;
    }else{
       s+="null"+separator+"null"+separator;
    }
	s+=FILE.getBrowserHistory()+									separator; //26
    s+=Labels.get()+												separator;  //27        
    s+=SearchPOP.save()+											separator; //28
    
    s+=(BTGPS.isOn()?"1":"0")+									separator;
	s+=(NAVI.mapScroling?"1":"0")+				separator;	
		
	s+=Integer.toString(Labels.getActiveIndex())+		separator;
	s+=(MyCanvas.direct_to_label?"1":"0")+				separator;
	s+=Float.toString(NMEA.accuracy_of_GPS_Device)+		separator;
	s+=Integer.toString(working_screensMask, 2)+					separator;
	s+=(TrackRecording.records?"1":"0")+				separator;
	s+=Integer.toString(MAP.x2bit)+							separator;
	s+=Long.toString(System.currentTimeMillis())+		separator;
	s+=((TrackRecording.fileName.length()==0)?"null":TrackRecording.fileName)+separator;  //38
	
	s+=Float.toString((float)BTGPS.getDistance())+				separator;
	s+=Long.toString(BTGPS.stoped_timeMsec())+			separator;
	s+=Long.toString(BTGPS.moving_timeMsec())+			separator;
	s+=Float.toString((float)BTGPS.maxSpeed4())+		separator;
	s+=Integer.toString(MyCanvas.cur_screen)+				separator;
	
	if (Path.track==null){	
		s+="null"+separator;
	}else{
		Track t=Path.track;
		s+=t.fname()+									separator;
		s+=Integer.toString(t.trackNumber)+				separator;
		s+=((Tracks.dont_show_path_info)?"1":"0")+		separator;
		s+=((Tracks.runner!=null)?"1":"0")+				separator;
		s+=((Path.inverse)?"1":"0")+					separator;
		s+=((Tracks.graph)?"1":"0")+					separator;
		s+=Long.toString(Path.startTime)+				separator;
		s+=Integer.toString(Graph.zoom)+				separator;
	}
	
	s+=OZI_PLT.save()+separator;
	s+=BTGPS.l().getLat()+separator;
	s+=BTGPS.l().getLon()+separator;
	s+=BTGPS.time+separator;
	s+=accept_language+separator;
	
	//TrackConductor.active
	//TrackConductor.inverse
	return s;
	}catch (Exception ex) {System.out.println("Storage.set Error "+ex.toString());return null;}
}
//-------------------------------------------------------------------------------------------
static public void set(final String set){
	try{
	Stack s=TEXT.split(set, ""+separator);	
	int i=0;
	//DIRECTION_COLOR=	getInt(s,i++,16);
	//TRACK_IN_MEMORY_RGB=getInt(s,i++,16);
	FONT_I=				getInt(s,i++,10);    
	DO_BEEP=		getBoolean(s,i++);
	SCROLING_MAP_BREAK=	getFloat(s,i++);
	SET_LIGHTS_DTIME=	getInt(s,i++,10);
	rotate_map	=		getInt(s,i++,10);
	D_ANGLE_2_CHANGE=	getInt(s,i++,10);
	Labels.dist_sort=	getBoolean(s,i++);
	
	TIMEZONE=			getInt(s,i++,10);
	TRIANGLE_ALPHA=		getInt(s,i++,16);
	TRACK_OPACITY=		getFloat(s,i++);
	locaView=			getInt(s,i++,10);			//10
	Track.max_zoom=		getInt(s,i++,10);	
	NAVI.SetGP( new Point( getInt(s,i++,10), getInt(s,i++,10)));
	int zoom=getInt(s,i++,10);
	if (NAVI.map==null)
		MAP.setZoom2Load(zoom);
	else
		NAVI.map.setZoom(zoom);
	MAP.offline=		getBoolean(s,i++);
	Storage.defaultNameNumb=getInt(s,i++,10);
	Storage.dMapDirName = (String)s.elementAt(i++);
    Storage.dTrackDirName= (String)s.elementAt(i++);                     
    Storage.dLabelsDirName= (String)s.elementAt(i++);
    BTGPS.max_hdop_error=getInt(s,i++,10);  //20
    MyCanvas.lights=getInt(s,i++,10);    			
	    //----------------
	String str=(String)s.elementAt(i++);   //22
    if (! str.endsWith("null")){ 
        RESENTLY_OPENED_LIST.Load(str);	 
        String ts=RESENTLY_OPENED_LIST.GetFirst();
        Storage.file2load(ts);	                	                                       
    }else
    	Storage.file2load(Storage.world_map);    
    //----------------	
    str=(String)s.elementAt(i++);   //23
    if (! str.endsWith("null"))
        Storage.trackFNs=str; 
    str=(String)s.elementAt(i++);//24
    if (! str.endsWith("null"))
    	Storage.trackIndexes2Load=str.getBytes();
    //----------------
	str=(String)s.elementAt(i++); 
    if (! str.endsWith("null")){
        Storage.btGPSUrl=str;
	    //------------------
	str=(String)s.elementAt(i++);
	if (! str.endsWith("null"))
	    Storage.btGPSName=str;       
    }else
    	i++;
	//--------------------------
   
    FILE.setBrowserHistory((String)s.elementAt(i++));
	Labels.set((String)s.elementAt(i++));
	str=(String)s.elementAt(i++);
	SearchPOP.load(str);			
	if (getBoolean(s,i++))
		Interface.turnGps(true);	
	/*MyCanvas.mapScroling=*/getBoolean(s,i++);
	
	Labels.setActve(getInt(s,i++,10));
	MyCanvas.direct_to_label=getBoolean(s,i++);
	
	NMEA.accuracy_of_GPS_Device=getFloat(s,i++);
	working_screensMask=getInt(s,i++,2);
	TrackRecording.records=getBoolean(s,i++);
	MAP.x2bit=getInt(s,i++,10);
	exitTime=getLong(s,i++);
	str=(String)s.elementAt(i++);
	if (! str.endsWith("null"))
		TrackRecording.fileName=str;
	
	BTGPS.setDistance(getFloat(s,i++));
	BTGPS.set_stoped_timeMsec(getLong(s,i++));
	BTGPS.set_movint_timeMsec(getLong(s,i++));
	BTGPS.setMaxSpeed4(getFloat(s,i++));
	MyCanvas.cur_screen=getInt(s,i++,10);
	if (MyCanvas.cur_screen>=MyCanvas.MAX_SCREENS)
		MyCanvas.cur_screen=MyCanvas.navi;
					
  	//MyCanvas.reDrawMap();
	
	
	str=(String)s.elementAt(i++);
	if (!str.endsWith("null")){
		Tracks.pathFName=str;
		Tracks.pathTrackNumber=getInt(s,i++,10);
		Tracks.dont_show_path_info=getBoolean(s,i++);
		Tracks.runnerF=getBoolean(s,i++);
		Path.inverse=getBoolean(s,i++);
		Tracks.graph=getBoolean(s,i++);
		Path.startTime=getLong(s,i++);
		Graph.zoom=getInt(s,i++,10);
	}
	

//	TrackConductor.running=getBoolean(s,i++);
//	TrackConductor.runNodeN=getInt(s,i++,10);
	str=(String)s.elementAt(i++);
	if (! str.endsWith("null"))
		OZI_PLT.lastTrack=str;
	OZI_PLT.lastLac=new Location((String)s.elementAt(i++),(String)s.elementAt(i++));
	OZI_PLT.lastTime=getLong(s,i++);
	
	accept_language=(String)s.elementAt(i++);
			
	}catch (Exception ex) {System.out.println("Storage.set Error "+ex.toString());}
	
	
}



static public void SetDefault(){
	
	MAP.offline=		false;
	
    DIRECTION_COLOR=	0xff;
    TRACK_IN_MEMORY_RGB=0xff0000;
    FONT_I=				2;
    DO_BEEP=		true;
    SCROLING_MAP_BREAK=	0.95f;
    SET_LIGHTS_DTIME=	5000;
    rotate_map	=		4;
    D_ANGLE_2_CHANGE=	10;
    Labels.dist_sort=	false;
    
    TIMEZONE=			3;
    TRIANGLE_ALPHA=		0x77ffffff; 
    TRACK_OPACITY=		0.5f;
}

static public Point GetPointInMaxZoom(final Point globP){
        return MAP.getMapPoint(globP, Track.max_zoom);
    }


static public int fontSize(){return FONT_SIZE[FONT_I];}

public static int FindColorIndex(int colors){
    int i=0;
    while (i<COLORS.length && colors!=COLORS[i])
        i++;
    return (i<COLORS.length)?i:i-1;
}







        

final private static int ISIZE=16;
public static Image[] setColorImages(int[] rgb){
    Image[] img=new Image[rgb.length];
    int[] data=new int[ISIZE*ISIZE];
    for (int i=0; i<rgb.length; i++){    
         int c=0;
         while (c<256)
            data[c++]=rgb[i];
         img[i]=Image.createRGBImage(data,ISIZE,ISIZE,false);
    }
    return img;
}

static public final int MAX_TRACK_WIDTH=10;

public static Image GetImage(int len, int rgb, int width, float opacity){
    System.out.println(opacity);
    int orgb=(int)(255*opacity);
    orgb=(orgb<<24)+rgb;
    int[] data=new int[len*MAX_TRACK_WIDTH];
    int begY=(MAX_TRACK_WIDTH-width)>>1;
    for (int x=0; x<len; x++)
        for (int y=0; y<MAX_TRACK_WIDTH; y++)
            data[y*len+x]=(y>=begY && y<begY+width)?orgb:0;
   return Image.createRGBImage(data,len,MAX_TRACK_WIDTH,true);
}


//#########################################################################################

static public void AllSettings(){
 Form set = new Form(Interface.s_settings);
 int size=COLORS.length;
 String[] s=new String[size];
 int i=0;
 while (i<size)
     s[i++]="";
 Image[] colorsA=setColorImages(COLORS);
 final ChoiceGroup directionColors=new ChoiceGroup(Interface.s_directionRGB, ChoiceGroup.POPUP, s,colorsA);
 directionColors.setSelectedIndex(FindColorIndex(DIRECTION_COLOR), true);
 set.append(directionColors); 
 final ChoiceGroup trackInMemory=new ChoiceGroup(Interface.s_mem_trackRGB, ChoiceGroup.POPUP, s,colorsA);
 trackInMemory.setSelectedIndex(FindColorIndex(TRACK_IN_MEMORY_RGB), true);
 set.append(trackInMemory);
 s=new String[]{Interface.s_no,Interface.s_yes};
 final ChoiceGroup zoomMap=new ChoiceGroup("MAP X2", ChoiceGroup.POPUP,s,null);
 zoomMap.setSelectedIndex(MAP.x2bit, true);
 set.append(zoomMap);

 s=new String[]{"online","offline"};
 final ChoiceGroup onlyCashe=new ChoiceGroup("Карты:", ChoiceGroup.POPUP, s,null);
 onlyCashe.setSelectedIndex(MAP.offline?1:0, true);
 set.append(onlyCashe);
 s=new String[]{"47.910480°N", "47°54.6288'N", "47°54'37.72\"N","47.910480"};
 final ChoiceGroup locVid=new ChoiceGroup("Вид", ChoiceGroup.POPUP,s,null);
 locVid.setSelectedIndex(Settings.locaView, true);
 set.append(locVid); 
 s=new String[]{Interface.s_no,Interface.s_yes};
 final ChoiceGroup beep=new ChoiceGroup("Пищать?", ChoiceGroup.POPUP,s,null);
 beep.setSelectedIndex(DO_BEEP?1:0, true);
 set.append(beep); 
 set.addCommand(Interface.ok);
 set.addCommand(Interface.exit);
 
 
 final Gauge maxZoom = new Gauge("Точность Трека", true, Track.dmax_zoom, Track.max_zoom);
 set.append(maxZoom);     
   
  final ChoiceGroup autoRotateMap=new ChoiceGroup(Interface.s_dorotateMAP, ChoiceGroup.POPUP);
  autoRotateMap.append("Север", null);
  autoRotateMap.append("Запад", null);
  autoRotateMap.append("Юг", null);
  autoRotateMap.append("Восток", null);
  autoRotateMap.append("Автоматически", null);
  autoRotateMap.setSelectedIndex(rotate_map, true);
  set.append(autoRotateMap);

  final ChoiceGroup fontSize=new ChoiceGroup(Interface.s_fontSize, ChoiceGroup.POPUP);
  fontSize.append("Маленький", null);
  fontSize.append("Средний", null);
  fontSize.append("Большой", null);
  fontSize.setSelectedIndex(FONT_I, true);
  set.append(fontSize);

 final Gauge dangle2change=new Gauge("поворот карты", true, 10, D_ANGLE_2_CHANGE);
 set.append(dangle2change);

 final Gauge setLightsDTime=new Gauge("мигать каждые сек.", true, 10, (int)(SET_LIGHTS_DTIME/1000));
 set.append(setLightsDTime);

 final Gauge scrolingMapBreak=new Gauge("Тормоз %", true,100,(int)(100-SCROLING_MAP_BREAK*100));
 if (MyCanvas.touchPhone)
	 set.append(scrolingMapBreak);
 //final get_new_gps
 
 
 if (Storage.btGPSName!=null && Storage.btGPSUrl!=null){
	 s=new String[]{"другой",Storage.btGPSName};
 }else{
	 s=new String[]{"другой"};	 
 } 
 final ChoiceGroup newGPSResiver=new ChoiceGroup("Подключится к ", ChoiceGroup.POPUP, s,null);
 final boolean newGPS=s.length==1;
 if (!newGPS){
	 newGPSResiver.setSelectedIndex(Storage.btGPSUrl==null || get_new_gps?0:1, true);
	 set.append(newGPSResiver);
 }
 final   TextField timeZone=new TextField("UTC/GMT", Integer.toString(TIMEZONE), 3, TextField.NUMERIC);
 set.append(timeZone);
 final   TextField accuracy=new TextField("Accuracy of GPS Device",Float.toString(NMEA.accuracy_of_GPS_Device),18,TextField.ANY);
 set.append(accuracy);
 
 
 final ChoiceGroup screens=new ChoiceGroup("Отображать:", ChoiceGroup.BUTTON);
 screens.append("Карта", null);
 screens.append("Спутники", null);
 screens.append("Инфо 1", null);
 screens.append("Инфо 2", null);
 screens.append("График Высоты", null);
 i=0;
 for (int mask=1; i<screens.size(); i++,mask<<=1)
	 screens.setSelectedIndex(i, (working_screensMask&mask)>0);	 
 set.append(screens);
 final   TextField max_hdom2t=new TextField("max hdop", Integer.toString(BTGPS.max_hdop_error), 2, TextField.NUMERIC);
 set.append(max_hdom2t);
 final   TextField saveS=new TextField("tap 1 to SAVE settings", "", 1, TextField.ANY);
 set.append(saveS);
 final   TextField loadS=new TextField("tap 1 to LOAD settings", "", 1, TextField.ANY);
 set.append(loadS);
 set.setCommandListener(new CommandListener() {
     public void commandAction(Command c, Displayable s) {
    	 boolean error=false;
    	 try{    		 
         if (c==Interface.ok){
        	 if (loadS.getString()!=null && loadS.getString().equals("1"))
            	 Storage.loadStateFromfile();
        	 else{	        	 
	             DIRECTION_COLOR=COLORS[directionColors.getSelectedIndex()];
	             TRACK_IN_MEMORY_RGB=COLORS[trackInMemory.getSelectedIndex()];                
	             if (Track.max_zoom!=maxZoom.getValue()){
	             	Track.max_zoom=maxZoom.getValue();
	             	Storage.reloadTracks();
	             }                
	             rotate_map=autoRotateMap.getSelectedIndex();
	             FONT_I=fontSize.getSelectedIndex();
	             D_ANGLE_2_CHANGE=dangle2change.getValue();
	             SET_LIGHTS_DTIME=setLightsDTime.getValue()*1000;
	             SCROLING_MAP_BREAK=(1-(float)scrolingMapBreak.getValue()*0.01f);                                               
	             MAP.offline=onlyCashe.isSelected(1);
	             MAP.x2bit=zoomMap.getSelectedIndex();
	             DO_BEEP=beep.isSelected(1);
	             get_new_gps=newGPS || newGPSResiver.isSelected(0);
	             Settings.locaView=locVid.getSelectedIndex();
	             TIMEZONE=Integer.parseInt(timeZone.getString());
	             NMEA.accuracy_of_GPS_Device=Float.parseFloat(accuracy.getString());
	             BTGPS.max_hdop_error=Integer.parseInt(max_hdom2t.getString());

	             if (saveS.getString()!=null && saveS.getString().equals("1"))
	            	 Storage.saveState2file();
	            MAP.redraw++;
        	 }
         }
    	 }catch (Exception e){error=true;}
         if (error==false){
        	 if ( ((1<<MyCanvas.cur_screen)&working_screensMask) == 0){
        		 int mask=working_screensMask;
        		 MyCanvas.cur_screen=0;
        		 while ((mask&1)==0)
        			 MyCanvas.cur_screen++;
        	 }
        	 MobNav1.display.setCurrent(MobNav1.gCanvas);
         }
     }
 });
 set.setItemStateListener(new ItemStateListener(){

         public void itemStateChanged(Item item) {
        	 
        	if (item==screens){
        		int smask=0;
        		for (int i=0,mask=1; i<screens.size(); i++,mask<<=1)
	            	 if (screens.isSelected(i))
	            		 smask|=mask;
        		if (smask>0)
        			working_screensMask=smask;
        		else{
        			for (int i=0,mask=1; i<screens.size(); i++,mask<<=1)
        				 screens.setSelectedIndex(i, (working_screensMask&mask)>0);	         			         			
        		}
        	}
        	        	
            if(item == maxZoom){
                 if (maxZoom.getValue()<6)
                     maxZoom.setValue(6);                   
             }
         }
     });
 MobNav1.display.setCurrent(set);
}


}
