package mobnav.canvas;
import java.util.Stack;

import javax.microedition.lcdui.Displayable;


public class DisplayList {
	static Stack st=new Stack();
	
	
	static public void setCurrent(final Displayable d){
		st.push(MobNav1.display.getCurrent());
		MobNav1.display.setCurrent(d);		
	}
	
	static public void setBack(){
		if (st.size()>0){
			MobNav1.display.setCurrent((Displayable)st.pop());
		}else
			MobNav1.display.setCurrent(MobNav1.gCanvas);	
	}
	

}
