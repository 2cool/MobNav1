package mobnav.math;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 2cool
 */
public class Aprox4 {
                
private double aprox[]={0,0,0,0};
private int aproxI=0;
private double olda=0;

public double get(final double a){
    double ret=a;
    if (aproxI>0){
        aprox[aproxI&3]=(a+olda)*0.5;
        if (aproxI>=3){
            int i=aproxI-3;
            double a0=(aprox[(i+1)&3]+aprox[i&3])*0.5;
            double a1=(aprox[(i+2)&3]+aprox[(i+1)&3])*0.5;
            double a2=(aprox[(i+3)&3]+aprox[(i+2)&3])*0.5;
            a0=(a1+a0)*0.5;
            a1=(a2+a1)*0.5;
            ret= (a1+a0)*0.5;
        }    
    }
    olda=a;
    aproxI++;
    return ret;    
}


}
