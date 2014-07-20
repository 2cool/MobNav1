package mobnav.canvas;
/**
 *
 * @author 2cool
 */

//import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.*;

import mobnav.gps.BTGPS;
import mobnav.gps.GGA;
import mobnav.gps.NMEA;
import mobnav.math.math;

public class Labels {
	
	
static public boolean dist_sort=false;
static private Stack st = new Stack();
static private Stack stx = new Stack();
static private Stack sty = new Stack();
static public Point min=new Point(100000000,1000000000);

static private Label lighted=null;
static public String selectedStr=null;
static public String minname=null;
static private Label active=null;
static public Label getActive(){return active;}

static public Point foundP=null;
static public String foundName=null;


static public void setActve(final int index){
	if (index!=-1 && index<st.size())
		active=(Label)st.elementAt(index);
}

static public int getActiveIndex(){
	if (active==null)
		return -1;
	else
		return st.indexOf(active);
}

// --------------------------------------------------
static public Label findNear(final Point p){
    
        int dist;
	if (NAVI.mapScroling==false)
		dist=(int)NAVI.map.getDistInPixels(Settings.MIN_DIS_TO_SHOW_LABEL_NAME_IN_METERS);
    else
        dist=Settings.MIN_DIS_TO_SHOW_LABEL_NAME_IN_PIXELS;
	dist=NAVI.map.Scale(dist, MAP.MAX_ZOOM);
	int mx=getXIndex(p.x);	
	if (mx>=stx.size())
		mx=stx.size()-1;
	int my=getYIndex(p.y);
	if (my>=sty.size())
		my=sty.size()-1;
        if (    mx>0 && my>0 &&
                stx.elementAt(mx)==sty.elementAt(my) && 
                Math.abs(((Label)stx.elementAt(mx)).p().x-p.x)<=dist &&
                Math.abs(((Label)sty.elementAt(my)).p().y-p.y)<=dist 
                )    
            return (Label)stx.elementAt(mx);
            


	long mind=Long.MAX_VALUE-1;
	Label l=null;
	int ti=mx;
	long dx,dy,t;
	//-----------------------------------------
	while (ti>=0){
		Point tp=((Label)stx.elementAt(ti)).p();
		if ((dx=Math.abs(tp.x-p.x))<=dist){
			if ((dy=Math.abs(tp.y-p.y))<=dist){
				if ((t=dx*dx+dy*dy)<mind){
					mind=t;
					l=(Label)stx.elementAt(ti);
				}
			}			
		}else 
			break;
		ti--;
	}
	//----------------------------------------
	ti=mx+1;
	while (ti<stx.size()){
		Point tp=((Label)stx.elementAt(ti)).p();
		if ((dx=Math.abs(tp.x-p.x))<=dist){
			if ((dy=Math.abs(tp.y-p.y))<=dist){
				if ((t=dx*dx+dy*dy)<mind){
					mind=t;
					l=(Label)stx.elementAt(ti);
				}
			}			
		}else 
			break;
		ti++;
	}
	//----------------------------------------
	//-----------------------------------------
	ti=my;
	while (ti>=0){
		Point tp=((Label)sty.elementAt(ti)).p();
		if ((dy=Math.abs(tp.y-p.y))<=dist){
			if ((dx=Math.abs(tp.x-p.x))<=dist){
				if ((t=dx*dx+dy*dy)<mind){
					mind=t;
					l=(Label)sty.elementAt(ti);
				}
			}			
		}else 
			break;
		ti--;
	}
	//----------------------------------------
	ti=my+1;
	while (ti<sty.size()){
		Point tp=((Label)stx.elementAt(ti)).p();
		if ((dy=Math.abs(tp.y-p.y))<=dist){
			if ((dx=Math.abs(tp.x-p.x))<=dist){
				if ((t=dx*dx+dy*dy)<mind){
					mind=t;
					l=(Label)sty.elementAt(ti);
				}
			}			
		}else 
			break;
		ti++;
	}
	//----------------------------------------	
	return l;	
}

//   $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$


static public boolean active(){return active!=null;}

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public int index(Label l){
    int i=0;
    for (;i<st.size(); i++)
        if (((Label)st.elementAt(i)).equals(l))
            return i;                
        
    return -1;
}
//=============================================================================
//=============================================================================
static private void getLabelsList(final List list, final Stack labelsSt, final String fn){
    list.setTitle(FILE.getName(fn,false));
	 BufferedRead in=null;
	    try{
	    try {
	    	list.deleteAll();
	    	labelsSt.removeAllElements();
	        in = new BufferedRead(fn);
	        int fsize=(int)in.fileSize();
	        Loading.start(FILE.getName(fn, false)+Interface.isLoading, true, false,rd );
	        String s;
	        in.readString().length();
	        in.readString().length();
	        in.readString().length();
	        in.readString().length();
	        Location myLoc=new Location(NAVI.getGP());
	        Stack dst=new Stack();
	        if (fsize>1000)
            	MAP.freeMemory();
	        
	         while ( (s=in.readUTF_8String())!=null && s.length()>0){ 
	        	 try{
		        	 if (Loading.CANCELED())
		                 break;                 	             	       
		            String [] sm=TEXT.split(s,',');
		            int h=(sm[14].length()>0)?Integer.parseInt(sm[14]):-777;
		            Label lab=new Label(new Location(sm[2],sm[3]),h,sm[10], sm[1]);	            
		            if (dist_sort==false){
		            	labelsSt.push(lab);
		            	Image icn=loaded(lab)==true?Interface.deliteIcon:Interface.addIcon;
		            	list.append(sm[1], icn);
		            }else{                                   
			            int dist=(int)lab.loc().getDistance(myLoc);		            
			            int index=math.getIndex(dst,dist);
			            if (index==dst.size()){ 
			            	dst.push(new Integer(dist));
			            	labelsSt.push(lab);
			            }else{
			            	dst.insertElementAt(new Integer(dist), index);
			            	labelsSt.insertElementAt(lab, index);
			            } 
		            }
		            Loading.done=((int)in.readed()<<7)/fsize;
	        	 }catch (Exception ex) {System.out.println(ex);}   
	        }
	         if (dist_sort)
		         for (int i=0; i<dst.size(); i++){
		        	 Label l=(Label)labelsSt.elementAt(i);
		        	 Image icn=loaded(l)==true?Interface.deliteIcon:Interface.addIcon;
		        	 String dist=((Integer)dst.elementAt(i)).toString();
		        	 if (dist.length()>3){
		        		 dist=dist.substring(0,dist.length()-3)+"km";
		        	 }else
		        		 dist+="m";
		        	 list.append(dist+" "+l.name(),icn);
		         }
	    } catch (Exception ex) {System.out.println(ex);}          
	    }finally{if (in!=null)in.close();}

}
//=============================================================================
static private Displayable rd;
static public void loadFromFile(final String fn){
    final Command sellectAll4Add=     	new Command(Interface.s_sellectAll4Add,Command.ITEM,2);
    final Command desellectAll4Add=   	new Command(Interface.s_desellectAll4Add,Command.ITEM,2);
    final Command sellectAll4Del=     	new Command(Interface.s_sellectAll4Del,Command.ITEM,2);
    final Command desellectAll4Del=   	new Command(Interface.s_desellectAll4Del,Command.ITEM,2);
    final Command doIt=      		  	new Command(Interface.s_OK,Command.ITEM,1);
    final Command sort=    	      		new Command(Interface.s_sort,Command.ITEM,1);
    
    rd=Interface.returnDsplayale;
    final List list= new List(FILE.getName(fn,false), List.MULTIPLE);
    
    final Stack labelsSt=new Stack();   
    list.setCommandListener(new CommandListener() {
        public void commandAction(Command c, Displayable d) {
            boolean sel;
            if (c==sort){            	
            	new Thread(new Runnable() {
                    public void run() {
		            	dist_sort^=true;
		            	getLabelsList(list,labelsSt,fn);
		            	Loading.stop();
		            	MobNav1.display.setCurrent(list);
                    }}).start();
            	return;
            }else if ((sel=c==sellectAll4Add) || c==desellectAll4Add){                
                for (int i=0; i<list.size(); i++)
                    if (list.getImage(i)==Interface.addIcon)
                    	list.setSelectedIndex(i, sel);
                return;
            }else if ((sel=c==sellectAll4Del) || c==desellectAll4Del){                
                for (int i=0; i<list.size(); i++)
                    if (list.getImage(i)!=Interface.addIcon)
                    	list.setSelectedIndex(i, sel);
                return;
            }else if (c==doIt){
                for (int i=0; i<list.size(); i++){
                    if (list.isSelected(i))
                    	if (list.getImage(i)==Interface.addIcon){
                            //System.out.println("_ADD "+i);
                            add((Label)labelsSt.elementAt(i),false);
                    	}else{
                            
                    		removeEqual2((Label)labelsSt.elementAt(i));
                        }
                }
                
                
                    
                Interface.returnDsplayale=rd;
                MobNav1.display.setCurrent(rd);                
            }else{
            	Interface.returnDsplayale=rd;
            	FILE.showCurrDir();
            }
            labelsSt.removeAllElements();
            list.deleteAll();
            Loading.stop();
        }});
    
    getLabelsList(list,labelsSt,fn);
    
       
    list.addCommand(sellectAll4Add);
    list.addCommand(desellectAll4Add);
    list.addCommand(sellectAll4Del);
    list.addCommand(desellectAll4Del);
    list.addCommand(doIt);
    list.addCommand(sort);
    
    //list.addCommand(MyI.ok);
    list.setSelectCommand(doIt);    
    list.addCommand(Interface.back);
    
    Interface.returnDsplayale=list;

}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public boolean loaded(final Label l){
	int tx=getXIndex(l.p().x);
	Label nl;	
	while (tx<stx.size() && (nl=(Label)stx.elementAt(tx)).p().x==l.p().x){
		if (nl.equals(l))
			return true;
		else
			tx++;
}	
	return false;
	
	
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public boolean saveToFile(){
    boolean ok=true;
    FileConnection fcc=null;
    OutputStream os=null;
    try{
    try {
    
            
            fcc=(FileConnection) Connector.open(Storage.getAutoSaveDir(FILE.WPT_E)+Storage.date2Name(true)+FILE.WPT_E);
            try {
                if (fcc.exists())                    
                    fcc.delete();
                fcc.create();
            } catch (Exception ex) {}
            
            os=fcc.openOutputStream();
            os.write("OziExplorer Waypoint File Version 1.1\n".getBytes("UTF-8"));
            os.write("WGS 84\n".getBytes("UTF-8"));
            os.write("Reserved 2\n".getBytes("UTF-8"));
            os.write("Reserved 3\n".getBytes("UTF-8"));

            
            for (int i=0; i<st.size(); i++){
                os.write("-1,".getBytes());
                Label l=(Label)st.elementAt(i);
                os.write(l.name().replace(',', '.').getBytes("UTF-8"));
               
                os.write((","+l.loc().getLat()+","+l.loc().getLon()).getBytes());                 
                os.write((",,0,1,,,,"+ l.coment.replace(',', '.') +",,,0,"+Integer.toString(l.height)+ ",,,,,,,,,,\n").getBytes());
            }
    }finally{
        if (os!=null)
            os.close();
        if (fcc!=null)
            fcc.close();         
    }                      
    } catch (Exception ex) {
        ok=false;
        MyCanvas.SetErrorText("Labels SaveToFile error "+ex.toString());
            //System.out.println("Labels SaveToFile error "+ex.toString());
    }
    
    return ok;
}





//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public void setList(){    
    labels.deleteAll();
    if (st!=null && st.size()>0){
    	if (dist_sort){
    		Location myLoc=new Location(NAVI.getGP());
    		Stack dst=new Stack();
    		labelsSt=new Stack();
    		int i=0;
    		while (i<st.size()){
    			Label lab=(Label)st.elementAt(i++);
    			int dist=(int)lab.loc().getDistance(myLoc);
    			int index=math.getIndex(dst,dist);
    			if (index==dst.size()){ 
    				dst.push(new Integer(dist));
    				labelsSt.push(lab);
    			}else{
    				dst.insertElementAt(new Integer(dist), index);
    				labelsSt.insertElementAt(lab, index);
    			} 
    		}
    		 if (dist_sort)
		         for (i=0; i<dst.size(); i++){
		        	 Label l=(Label)labelsSt.elementAt(i);		        	 
		        	 labels.append(((Integer)dst.elementAt(i)).toString()+"|"+l.name(),null);
		         }    		
    	}else{
    		labelsSt=st;
    		int i=0;
	        while (i<st.size()){
	            Label l=(Label)st.elementAt(i++);
	            labels.append(l.name(), null);
	        }
    	}
    }else
        labels= null;   
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public String   getName(final int index){    
    return ((Label)st.elementAt(index)).name();
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public Point   getPoint(final int index){return ((Label)st.elementAt(index)).p();}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
/*
static private int next=0;
static public byte[] getFirst(){next=0;return getNext();}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public byte[] getNext(){
    if (next>=st.size())
        return null;
    Label l=(Label)st.elementAt(next++);
    return l.getBytes();
}
*/
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
/*
static private final String labRec="label___";
static public void load(){
    try {
        RecordStore rs = RecordStore.openRecordStore(labRec, true);
        byte[]b;
        int ri=1;
        while ((b=rs.getRecord(ri++))!=null){
            if (b.length==1){
                break;
            }else{
                add(new Label(b),false);
            }
        }
        rs.closeRecordStore();
    } catch (RecordStoreException ex) {        }            
}
*/
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

static public String get(){
	if (st.size()>0){
		String s="";
		for (int i=0; i<st.size(); i++){
			s+=((Label)st.elementAt(i)).toString()+'\t';	
		}
		return s;
	}	
	return "null";
}
static public void set(final String str){
	if (!str.endsWith("null")){
	String []a=TEXT.split(str, '\t');
	for (int i=0; i<a.length; i++){
		add( new Label( new Location(a[i++],a[i++]) ,Integer.parseInt(a[i++]),a[i++],a[i] ),false) ;
	}
	}
}
/*
static public void save(){
      try {
        RecordStore rs = RecordStore.openRecordStore(labRec, true);
        byte [] b=getFirst();
        int ir=1;      
        int size=rs.getNumRecords();
        if (b!=null){             
            while (b!=null){       
                if (size>=ir)
                    rs.setRecord(ir, b,0,b.length);
                else
                    rs.addRecord(b,0,b.length);     
                ir++;
                b=getNext();
            }
        }
        b=new byte[1];
        if (size>=ir)
                rs.setRecord(ir, b,0,1);
            else
                rs.addRecord(b,0,1);    
        rs.closeRecordStore();

      } catch (RecordStoreException ex) {}                
}
*/
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public boolean isLoaded(){return st.size()>0;}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$




static public int getXIndex(final int x){	
    int first = 0,n=stx.size();
    if (n==0)
    	return 0;
    int last = n,mid;     
    if (((Label)stx.elementAt(0)).p().x > x)    
         return 0;     
    else if (((Label)stx.elementAt(n-1)).p().x < x)    
         return n;   
 
    while (first < last){
        mid = first + ((last - first) >>1); 
        int midx=((Label)stx.elementAt(mid)).p().x;
        if (x < midx)        
            last = mid;        
        else if (x > midx)        	
            first = mid + 1;
        else
        	return mid;
    }
    return last;   
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public int getYIndex(final int y){	
    int first = 0,n=sty.size();
    if (n==0)
    	return 0;
    int last = n,mid;     
    if (((Label)sty.elementAt(0)).p().y > y)    
         return 0;     
    else if (((Label)sty.elementAt(n-1)).p().y < y)    
         return n;   
 
    while (first < last){
        mid = first + ((last - first) >>1);
        int midy=((Label)sty.elementAt(mid)).p().y;
        if (y < midy)        
            last = mid;        
        else if (y > midy)       	
            first = mid + 1;
        else 
        	return mid;
    }
    return last;   
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$


static private void removeAll(){		
	if (st.size()>0){
	    Alert a = new Alert("",null,null,AlertType.CONFIRMATION);
	    a.setString(Interface.s_save+"?");
	    a.addCommand(Interface.cancel);
	    a.addCommand(Interface.yes);
	    a.addCommand(Interface.no);
	    a.setCommandListener(new CommandListener(){
	        public void commandAction(Command c, Displayable d){  
	        	if (c==Interface.cancel)
	        		MobNav1.display.setCurrent(menu); 
	        	else if (c==Interface.no || Labels.saveToFile()){
		        		st.removeAllElements();
		        	    stx.removeAllElements();
		        	    sty.removeAllElements();
		        	    MAP.redraw++;                 	            	
            			MobNav1.display.setCurrent(MobNav1.gCanvas);
	        	}
	        }});
	    MobNav1.display.setCurrent(a); 
	}  
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$




static private void refreshLabMenu(final Label l){
	 if (LIGHTED!=-1 && lighted==l){
	        menu.delete(LIGHTED);
	        LIGHTED=-1;
	        lighted=null;
	        cnt--;     
	        if (ACTIVE!=-1)
	            ACTIVE--;
	    }
	    if (ACTIVE!=-1 && active==l){
	        menu.delete(ACTIVE);
	        ACTIVE=-1;
	        active=null;
	        cnt--;
	    }
}

//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public int removeEqual2(final Label l){
    int ind=-1;
             
	int xi=getXIndex(l.p().x);
	while (xi<stx.size()){
		if (l.equals((Label)stx.elementAt(xi))){ 
                    ind=st.indexOf(stx.elementAt(xi));
                  //  System.out.println("REMOVE "+ind);
                    refreshLabMenu((Label)st.elementAt(ind));
                    st.removeElementAt(ind);
                    stx.removeElementAt(xi);
			break;
		}
		xi++;
	}
	int yi=getYIndex(l.p().y);	
	while (yi<sty.size()){
            if (l.equals((Label)sty.elementAt(yi))){
                    sty.removeElementAt(yi);
                    break;
            }
            yi++;
	}
	MAP.redraw++;
    return ind;
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

static public void remove(final Label l){
	if (l==active)
		active=null;
	if (l==lighted)
		lighted=null;
	stx.removeElement(l);
	sty.removeElement(l);
	st.removeElement(l);	              
	MAP.redraw++;
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

static public void drawIcons(Graphics g){
    int i=0;
    while (i<st.size()){
        Label l=(Label)st.elementAt(i++);
        l.draw(g,false);
    }
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static private long oldTime=0;
static public void draw(final Graphics g){					
		if (BTGPS.isOn() && GGA.fixQuality!=0){
			if (NAVI.mapScroling){
				long ctime=System.currentTimeMillis();
				if (ctime-oldTime>200){
					oldTime=ctime;
					int dist=(int)new Location(NAVI.getGP()).getDistance(BTGPS.l());
			        selectedStr=TEXT.Distance(dist);
				}
			}else if (active!=null){
				int dist=(int)new Location(NAVI.getGP()).getDistance(active.loc());
		        selectedStr=TEXT.Distance(dist)+" до "+active.name();			
			}else
				selectedStr="";
	}else
		selectedStr="";
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static boolean mn=false;
static public void search(final MAP map){
	minname="";    
    lighted=findNear(NAVI.getGP());
	if (lighted!=null){
		minname=lighted.name();
	}
    mn=minname.length()>0;
        
}
//---------------------------------------------------------------------------
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$








static public void set(String name,String coment, Location loc, int height, int index){
    if (loc.init() && name.length()>0){
    	if (index<0)
    		add(new Label(loc,height,coment, name),true);
    	else
    		set(new Label(loc,height,coment, name),index);

    }

}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

static private void edit(final Label l, final Displayable ret){
	
    //final Location loc=lab_loc;
    String str;
    if (foundP!=null && Math.abs(l.p().x-foundP.x)<3 && Math.abs(l.p().y-foundP.y)<3)
    	str=foundName;
    else{ 
    	if (l.name().length()==0){
    		str=Interface.s_labelName+Integer.toString(++Storage.defaultNameNumb);
	    }else
	        str=l.name();
        if (str.length()>255)
        	str=str.substring(0,255);                        
    }
        
        
        
        
        
		final   TextField name=new TextField(Interface.s_label, str, 256, TextField.ANY);
		final   TextField coment=new TextField(Interface.s_coment, l.coment, 256, TextField.ANY);
		final   TextField lat= new TextField(Interface.s_latitude,l.loc().latitude(),20,TextField.ANY);
		final   TextField lon= new TextField(Interface.s_longitude,l.loc().longtitude(),20,TextField.ANY);
		String heightS= (l.height==Label.noHeight)?"":Integer.toString(l.height);
		final   TextField height=new TextField(Interface.s_height,heightS,6,TextField.NUMERIC);
	   Form f = new Form(Interface.s_names_label);
	   f.append(name);
	   f.append(coment);
	   f.append(lat);
	   f.append(lon);
	   f.append(height);

		f.addCommand(Interface.back);
		f.addCommand(Interface.ok);

		f.setCommandListener(new CommandListener() {
	        public void commandAction(Command c, Displayable s) {
	        if (c==Interface.ok){
	        	
	                String nameS=name.getString();
	                String comentS=coment.getString();
	                Location lab_loc=new Location(lat.getString(),lon.getString());
	                int heightN=Label.noHeight;
	                try{
	                if (height.getString().length()>0)
	                	heightN=Integer.parseInt(height.getString());
	                }catch (Exception ex) {heightN=Label.noHeight;}  
	                if (lab_loc.init() && nameS.length()>0 && 
	                		( !nameS.equals(l.name()) || !lab_loc.equals(l.loc()))){                    
	                    //List();                                        
	                    int ind;        	                    
	                    set(nameS,comentS, lab_loc,heightN,ind=st.indexOf(l)); 
	                    if (ret==labels)
	                    	labels.set(labels.getSelectedIndex(), nameS, null);
	                    if (menu!=null){
	                        if (LIGHTED!=-1 && lighted==l){
	                            menu.set(LIGHTED, nameS, Interface.labelIcon);
	                            lighted=(Label)st.elementAt(ind);
	                        }
	                        if (ACTIVE!=-1 && active==l){
	                            menu.set(ACTIVE, nameS, Interface.labelIcon);
	                            active=(Label)st.elementAt(ind);
	                        }
	                    }    
	                    if (! (l.loc().latitude().equals(lat.getString()) && l.loc().longtitude().equals(lon.getString()))){
	                    	if (ind<0)
	                    		ind=st.size()-1;
	                    	proceed((Label)st.elementAt(ind),MobNav1.gCanvas);
	                    }else
	                    	MobNav1.display.setCurrent(ret);    	
	                }
	            }else	            	
	            	MobNav1.display.setCurrent(ret);
	        }
    });
    MobNav1.display.setCurrent(f);
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

static private void go2(final Label l){
      //returnDsplayale.lab_loc.SetActive(index);
      NAVI.SetGP(l.p());
      MobNav1.display.setCurrent(MobNav1.gCanvas);
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static private void direct2(final Label l){
      active=l;
      MyCanvas.direct_to_label=true;
      MobNav1.display.setCurrent(MobNav1.gCanvas);
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static private void select(final Label l){
	active=l;
	MyCanvas.direct_to_label=false;
  //    returnDsplayale.placeMark=returnDsplayale.lab_loc.getPoint(index);
      MobNav1.display.setCurrent(MobNav1.gCanvas);
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static private void delite(final Label l, final Displayable ret){ 
          
	refreshLabMenu(l);
    remove(l);
    if (labels==ret)
        labels.delete(labels.getSelectedIndex());       
    MobNav1.display.setCurrent(ret);
}




//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$





//=========================================================================================


static void proceed (final Label l,final Displayable ret ){
    final String currFile = l.name();
    final List lab_loc= new List(currFile, List.IMPLICIT);
    lab_loc.append(Interface.s_direction, Interface.directionIcon);              //0
    lab_loc.append(Interface.s_select, Interface.selectIcon);                 //1
    lab_loc.append(Interface.s_go_to, Interface.returnIcon);                  //2
    lab_loc.append(Interface.s_delite,Interface.deliteIcon);        //3
    lab_loc.append(Interface.s_edit,Interface.editIcon);            //4
    if (MyCanvas.GetMode()==MyCanvas.AtoB){
        lab_loc.append(Interface.s_route_from, Interface.Aicon);    //5
        lab_loc.append(Interface.s_route_to, Interface.Bicon);      //6
        lab_loc.append (Interface.s_route_thro,Interface.viaIcon ); //7
    }else if (GGA.fixQuality>0){
    	MODE_A_B.A=NMEA.l.GetPoint();
    	lab_loc.append("Проложить маршрут", Interface.trackIcon);//5
    }
    

    lab_loc.setSelectCommand(Interface.select);
    lab_loc.addCommand(Interface.back);
    lab_loc.setCommandListener(new CommandListener() {
        public void commandAction(Command c, Displayable d) {
           if (c==Interface.select){
                List curr = (List)d;
                final int ind = curr.getSelectedIndex();
                if (!(ind==3 || ind==4))
                	labels=null;
                switch (ind){
                    case 0:direct2(l);break;
                    case 1:select(l);break;
                    case 2:go2(l);break;
                    case 3:delite(l,(labels==null)?ret:labels);break;
                    case 4:
                    	//if (index>=0 && index<st.size())
                    		Labels.edit(l,(labels==null)?ret:labels);
                    	break;
                    case 5:
                    	if (MyCanvas.GetMode()==MyCanvas.AtoB){
                    		MODE_A_B.SetPointA(l.p());
                    		MobNav1.display.setCurrent(MobNav1.gCanvas);
                    		break;
                    	}else{                    		
                    		MODE_A_B.get_AB_Track();
                    	}
                    case 6:MODE_A_B.SetPointB(l.p());MobNav1.display.setCurrent(MobNav1.gCanvas);break;
                    case 7:MODE_A_B.SetPointVia(l.p());MobNav1.display.setCurrent(MobNav1.gCanvas);break;
                }
            }else if (c==Interface.back){
                //,LIGHTED,ACTIVE,cnt;
                //labMenu.
                MobNav1.display.setCurrent((labels==null)?ret:labels);
           }
           
        }
    });
    MobNav1.display.setCurrent(lab_loc);
}
// ---------------------------------------------------------


// ############################### L I S T ###########################################

static List labels=null;
static Stack labelsSt=null;
static public void list(){		
	labels=new List("",List.IMPLICIT);	
    setList();
    if (labels!=null){
        labels.setSelectedIndex(0, true);
        labels.setSelectCommand(Interface.select);
        labels.addCommand(Interface.back);
        final Command sort=	new Command(Interface.s_sort,Command.ITEM,1);
        labels.addCommand(sort);
        labels.setCommandListener(new CommandListener() {
            public void commandAction(Command c, Displayable d) {
            	if (c==sort){
            		new Thread(new Runnable() {
                        public void run() {
            		dist_sort^=true;            		
            		setList();
            	 
            		//MobNav1.display.setCurrent(labels);
                        }}).run();
            	}else if (c==Interface.select){
            	   int i=((List)d).getSelectedIndex();
            	  
                    proceed((Label)labelsSt.elementAt(i),MobNav1.gCanvas);
                }else if (c==Interface.back){  
                    MobNav1.display.setCurrent(menu);
               }
            }
        });
        MobNav1.display.setCurrent(labels);
    }else{
        Form f=new Form("");
        f.append(Interface.s_lists_empty);
        f.addCommand(Interface.back);
        f.setCommandListener(new CommandListener() {
            public void commandAction(Command c, Displayable d) {               
                MobNav1.display.setCurrent(menu);               
            }
        });
        MobNav1.display.setCurrent(f);
    }   
	
}
static public int getHeight(){
	int h= Label.noHeight;
	if (NAVI.mapScroling==false && BTGPS.isOn() && GGA.fixQuality!=0)
		h=(int)GGA.height;
	return h;
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static public void add(){
    edit(new Label( new Location(NAVI.getGP()),getHeight(), "", ""),MobNav1.gCanvas); 
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

static final public int GOOGLE=0,OPENSTREET=1;
static private void selectSearchProviders(){
	List searchPL=new List(Interface.s_search,List.IMPLICIT);
	searchPL.append("Google",Interface.googleIcon);
	searchPL.append("OpenStreet",Interface.openStreetIcon);
	
	searchPL.setSelectCommand(Interface.select);                      
	searchPL.addCommand(Interface.back);
	
	searchPL.setCommandListener(new CommandListener() {
        public void commandAction(Command c, Displayable d) {
           if (c==Interface.select){              
                switch(((List)d).getSelectedIndex()){                	
                    case 0:SearchPOP.search(menu,GOOGLE);
                        break;
                    case 1:SearchPOP.search(menu,OPENSTREET);
                    	break;
                    
                }
                
            }else if (c==Interface.back){
                MobNav1.display.setCurrent(menu);
           }
        }
});
							
	MobNav1.display.setCurrent(searchPL);
}
// ######################################################################################
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static private int FINDME,LIGHTED,ACTIVE,cnt;
static private List menu=null;


static public void menu(){  
    cnt=0;
    FINDME=LIGHTED=ACTIVE=-1;
    
    
    
    menu=new List(Interface.s_labels,List.IMPLICIT);
    if (NAVI.mapScroling){// && GGA.fixQuality!=0){
        FINDME=cnt++;
        menu.append(Interface.s_findMe, Interface.findMe);                   
    }

    if (lighted!=null){       
        menu.append(lighted.name(), Interface.labelIcon);
        LIGHTED=cnt++;
    }
    if (active!=null && active!=null){
       menu.append(active.name(),Interface.labelIcon);
        ACTIVE=cnt++;
    }
    menu.append(Interface.s_search,Interface.inetIcon);             //0
    //список ранее искаемых )))                        
    menu.append(Interface.s_list, Interface.labelIcon);                     //1
    menu.append(Interface.s_addLabel, Interface.addIcon);                   //2
    menu.append(Interface.s_browser, Interface.browserIcon);                //3
    menu.append(Interface.s_cancel,Interface.cancelIcon);                   //4
    menu.append(Interface.s_save,Interface.saveIcon);                       //5
    menu.append(Interface.s_clear, Interface.deliteIcon);                   //6
    
    if (MyCanvas.GetMode()==MyCanvas.AtoB){
        menu.append(Interface.s_route_from, Interface.Aicon);               //7
        menu.append(Interface.s_route_to, Interface.Bicon);                 //8
        menu.append (Interface.s_route_thro,Interface.viaIcon );            //9
    }
    
    menu.setSelectCommand(Interface.select);                      
    menu.addCommand(Interface.back);

    menu.setCommandListener(new CommandListener() {
        public void commandAction(Command c, Displayable d) {
           if (c==Interface.select){
               int ind=((List)d).getSelectedIndex();
               if (ind==FINDME){
                   NAVI.findMe();
                   MobNav1.display.setCurrent(MobNav1.gCanvas);
               }else if (ind==LIGHTED){
                   proceed(lighted,menu);
                   
               }else if (ind==ACTIVE){
                   proceed(active,menu);
               }

                switch(ind-cnt){
                    
                    case 0:selectSearchProviders();
                        break;
                    case 1:list();break;
                    case 2:add();Interface.update_menu=true;MyCanvas.Update();break;
                    case 3:Interface.icon=Interface.labelIcon; 
                            FILE.Browser(menu,FILE.WPT_E);
                            break;
                    case 4:active=null;MobNav1.display.setCurrent(MobNav1.gCanvas);break; 
                    case 5:Labels.saveToFile();MobNav1.display.setCurrent(MobNav1.gCanvas);break;
                    case 6:
                    	removeAll();
                    	break;
                    case 7:MODE_A_B.SetPointA(NAVI.getGP());MobNav1.display.setCurrent(MobNav1.gCanvas);break;
                    case 8:MODE_A_B.SetPointB(NAVI.getGP());MobNav1.display.setCurrent(MobNav1.gCanvas);break;
                    case 9:MODE_A_B.SetPointVia(NAVI.getGP());MobNav1.display.setCurrent(MobNav1.gCanvas);break;
                }
                
            }else if (c==Interface.back){
                MobNav1.display.setCurrent(MobNav1.gCanvas);
           }
        }
});
    MobNav1.display.setCurrent(menu);
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static private int findTwinsXIndex(final Label l){
	int i=getXIndex(l.p().x);
	boolean theSame=false;
	Label nl;
	
	while (i<stx.size() && (nl=(Label)stx.elementAt(i)).p().x==l.p().x){
		if (theSame=nl.equals(l))
			break;
		else
			i++;
	}
	return (theSame)?i:-1;
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
/*
static private int findTwinsYIndex(final Label l){
	int i=getYIndex(l.p().y);
	boolean theSame=false;
	Label nl;
	
	while (i<sty.size() && (nl=(Label)sty.elementAt(i)).p().y==l.p().y){
		if (theSame=nl.equals(l))
			break;
		else
			i++;
	}
	return (theSame)?i:-1;
}
*/
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static private void set(final Label l, final int index){
	if (index<st.size()){
		Label old=(Label)st.elementAt(index);
		if (l.loc().equals(old.loc()))
			old.rename(l.name());
		else{
			int ix=stx.indexOf(old);
			int iy=sty.indexOf(old);
			if (ix>=0 && iy>=0){
				st.setElementAt(l, index);
				stx.removeElementAt(ix);
				sty.removeElementAt(iy);
				ix=getXIndex(l.p().x);				
				if (ix==stx.size() )					
					stx.addElement(l);
				else	    		
		    		stx.insertElementAt(l, ix);	    		    			    			    
	    		iy=getYIndex(l.p().y);
		    	if (iy==sty.size())
		    		sty.addElement(l);
		    	else
		    		sty.insertElementAt(l, iy);		    		
			}							
		}
		MAP.redraw++;     								
	}
}
//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
static private int add(final Label l, final boolean test4twins){
	if (st.size()==0){
		st.push(l);
		stx.push(l);
		sty.push(l);
		
	}else{
		int i=findTwinsXIndex(l);	
		if (test4twins==false || i<0 ){					
			st.addElement(l);
			i=getXIndex(l.p().x);
	    	if (i<stx.size())	    		
	    		stx.insertElementAt(l, i);
	    	else	    		
	    		stx.addElement(l);	    	
	    	i=getYIndex(l.p().y);
	    	if (i<sty.size())
	    		sty.insertElementAt(l, i);
	    	else
	    		sty.addElement(l);			
		}				
	}
	MAP.redraw++;      
	return st.size()-1;
}


}