package mobnav.canvas;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */

import java.util.Stack;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;


public class MAKE_TRACK {
static private final    Stack tr = new Stack();    
static int cnt=0;
static public double len=0;
public static  void CreateTrackM(){
    len=0;
    cnt=0;
    tr.removeAllElements();
}
static public void EndCreateTrackM(){
    saveTrackManual();
    tr.removeAllElements();
    
}
static public void AddPointM(Point gp){
    tr.addElement(new Point(gp));
    if (cnt>0){
        len+=Location.getDistance((Point)tr.elementAt(cnt-1), (Point)tr.elementAt(cnt));
        System.out.println(len);
    }
    cnt++;    
}
static public void RemovePointM(final int i){
    if (cnt>1){
         if (i==0)
             len-=Location.getDistance((Point)tr.elementAt(0), (Point)tr.elementAt(1));
         else if (i==cnt-1)
             len-=Location.getDistance((Point)tr.elementAt(i-1), (Point)tr.elementAt(i));
         else{             
                len-=Location.getDistance((Point)tr.elementAt(i-1), (Point)tr.elementAt(i));             
                len-=Location.getDistance((Point)tr.elementAt(i+1), (Point)tr.elementAt(i));
                len+=Location.getDistance((Point)tr.elementAt(i+1), (Point)tr.elementAt(i-1));             
         }
    }
    tr.removeElementAt(i);
    cnt--;   
    System.out.println(len);
}
static public void SetPointM(Point gp, int i){ 
    System.out.println("set");
     if (i>=1){
        len-=Location.getDistance((Point)tr.elementAt(i-1), (Point)tr.elementAt(i));
        len+=Location.getDistance((Point)tr.elementAt(i-1),gp);
     }
     if (i<cnt-1){
         len-=Location.getDistance((Point)tr.elementAt(i+1), (Point)tr.elementAt(i));
         len+=Location.getDistance((Point)tr.elementAt(i+1), gp);
     }
     tr.setElementAt(new Point(gp), i);
     System.out.println(len);
}
static int trI=0;
static public Point GetFirstPointM(){
    trI=1;
    return ((tr.size()>0)?(Point)tr.elementAt(0):null);
    
}
static public Point GetNextPointM(){   
    return ((trI>=tr.size())?null:(Point)tr.elementAt(trI++));
}
static public String fn;
static public void saveTrackManual(){
    
try{
    Point p=GetFirstPointM();    
    if (p==null)
        return;
    fn=Storage.getAutoSaveDir(FILE.PLT_E)+"man_"+Storage.date2Name(true)+FILE.PLT_E;
    FileConnection fcc=(FileConnection) Connector.open(fn);
    try{
        fcc.create();
    }catch(Exception ex){System.out.println("error "+fn+" "+ex.toString());
       // fcc=(FileConnection) Connector.open("file:///e:/testTrack.plt");
    }
    OutputStream os=fcc.openOutputStream();    
    OZI_PLT.WriteOZIHeader(os);     
    OZI_PLT.WriteOZIPoint(os,p);
    while ((p=GetNextPointM())!=null)
        OZI_PLT.WriteOZIPoint(os,p);    
    os.close();
    fcc.close();  
    Storage.loadPLTTrackT(fn);
    }catch (Exception ex) {MyCanvas.SetErrorText(ex.toString());
}    

}    
    
    
    
    
    
    
    
    
    
    

}
