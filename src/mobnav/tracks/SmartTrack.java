package mobnav.tracks;

import mobnav.canvas.Location;
import mobnav.canvas.Point;
import mobnav.math.math;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */
public class SmartTrack {
    private int trackI=0;
    private int iEnd=0;
    private static final int BUFS=300;
    private int MAX_IN_METERS=20;
    private double max_dist_in_p;
    private Point[] trackBuf=new Point[BUFS];
    private int errCnt=0;
    private static final int maxErrCnt=3;
// #############################################################################
    
   
    
public SmartTrack(int maxDistInMeters){
    MAX_IN_METERS=maxDistInMeters;
 
}
// #############################################################################
/*
private double GetAngle(final Point P[], int end){
    double x=P[end].x-P[end-2].x;
    double y=P[end].y-P[end-2].y;
    double len=Math.sqrt(x*x+y*y);
    double rlen=1.0/len;
    x*=rlen;
    y*=rlen;
    return angle=math.GetAngle(x, y);
}
*/
private double getDist2(final Point P[], int end){
    int x=P[end].x-P[0].x;
    int y=P[end].y-P[0].y;
    return x*x+y*y;

}



















private double lastDist=0;
private double angle=0;
public double getDirectionAngle(){return angle;}

public int drawNext(final Point gp){	
    max_dist_in_p=(int)Location.getDistInPoints(Location.GetLocation(gp),MAX_IN_METERS);
    trackBuf[iEnd]=gp;
    int ret=-1;
   if (iEnd>=2){
       double max=Curvature(trackBuf, iEnd);
       angle=math.getAngle(normX, normY);
       if (iEnd>=5){
           double dist=getDist2(trackBuf,iEnd);
          if (lastDist>dist){
              if (lastDist-dist>=max_dist_in_p*max_dist_in_p)
                max=max_dist_in_p;
          } else
              lastDist=dist;
       }
       if (max>=max_dist_in_p){
           if (++errCnt>maxErrCnt){
                ret=1;
                iEnd=0;
                errCnt=0;
                lastDist=0;
           }
       }else{
           errCnt=0;
           if (iEnd>=BUFS-1){
                ret=1;
                iEnd=0;
                lastDist=0;
           } else{
               ret=0;
                iEnd++;
           }
       }
    }else{
        if (trackI<=1){
            ret=1;
            trackI++;
       }
        iEnd++;
    }
    return ret;
}
// #############################################################################




public double normY=-1,normX=0;
private double Curvature( Point P[], int end){
    double a =   P[0].y - P[end].y;
    double b = P[end].x -   P[0].x;
    double c =   P[0].x * P[end].y - P[end].x * P[0].y;
    double l=1.0/Math.sqrt(a*a+b*b);
    normX=b*l;
    normY=-a*l;
    if (c>=0)
        l=-l;

    int mid=end>>1;
    return Math.abs((a * P[mid].x + b * P[mid].y + c)*l);
}
// #############################################################################

}

















