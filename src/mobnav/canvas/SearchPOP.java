package mobnav.canvas;
import java.util.Stack;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;

import mobnav.math.math;

// -------------------------------------------------------------------
public class SearchPOP {
	static private int getIndex(final String s0, final String s1){
		String lc0=s0.toLowerCase();
		String lc1=s1.toLowerCase();
		int i=0;
		while (i<lc0.length() && i<lc1.length() && lc0.charAt(i)==lc1.charAt(i))
			i++;
		return i;
	}
    // -------------------------------------------------------------------
	static private int sort(final Stack st, final String str){
		int ret=0;
		for (int i=0; i<tf.size(); i++){
			int index=getIndex((String)tf.elementAt(i),str);
			if (index>0){
				ret++;
				index=(index<<16)+i;
			}
			int index1=math.getIndex(st, index);
			
			
			if (index1>=st.size())
				st.addElement(new Integer(index));
			else
				st.insertElementAt(new Integer(index), index1);
		}
		return ret;
	}
    // -------------------------------------------------------------------
    static private Stack tf=new Stack();
    // -------------------------------------------------------------------
    static public void clearHistory(){
    	tf.removeAllElements();
    }
    // -------------------------------------------------------------------

 // -------------------------------------------------------------------
    static private void add(final String st){
    	if (tf.indexOf(st)==-1){
	    	if (tf.size()>=512)
	    		tf.removeElementAt(0);
	    	tf.addElement(st);
    	}
    }
    // -------------------------------------------------------------------
    static public void load(final String s){
    	if (!s.endsWith("null"))
    		return;
    	tf=TEXT.split(s, "\t");
    }
    // -------------------------------------------------------------------
    static public String save(){
    	if (tf.size()==0)
    		return "null";
    	String s="";
    	for (int i=0; i<tf.size(); i++)
    		s+=((String)tf.elementAt(i))+'\t';
    	return s;
    }
    // -------------------------------------------------------------------
    public static void search(final Displayable retd,final int searchProvider){  	
	    final Form form = new Form(searchProvider==0?"Google":"OpenStreet");
	    //ChoiceGroup(label,type,elements,image)
	    final   TextField name=new TextField("", "", 256, TextField.ANY);
	    final ChoiceGroup pop = new ChoiceGroup ("", Choice.EXCLUSIVE,
	                           new String[] {""}, null);
	    pop.deleteAll();
	    form.append(name);
	    form.addCommand(Interface.select);
	    form.addCommand(Interface.exit);
	    form.setCommandListener(new CommandListener() {
	        public void commandAction(Command c, Displayable s) {
	        	if (c==Interface.select && name.getString().length()>0){
	        		String str=name.getString();
	        		add(str);
	        		if (searchProvider==Labels.GOOGLE)
	        			GOOGLE_FIND.Get(str,form);
	        		else
	        			OPENSTREET_FIND.Get(str,form);  
	        	}else
	        		MobNav1.display.setCurrent(retd);  
	        }});
	    form.append(pop);
	    form.setItemStateListener(new ItemStateListener(){

			public void itemStateChanged(Item arg0) {
				if (arg0==pop && pop.size()>0){
					//String []a=TEXT.split(name.getString(),' ');
					String s=name.getString();
					int i=0,j=0;
					while ((i=s.substring(j).indexOf(" "))!=-1)
						j+=i+1;
					if (j>0)
						s=s.substring(0,j)+" ";
					else
						s="";
					name.setString(s+pop.getString(pop.getSelectedIndex()));
				}							
				if (name.getString().length()>0){
					pop.deleteAll();					
					String st=name.getString();
					while (true){
						Stack tst=new Stack();
						if (sort(tst,st)>0){																
							int n=tst.size()-1;
							for (int i=0; i< tst.size(); i++){
								int index=((Integer)tst.elementAt(n-i)).intValue();
								if (index>0)
									pop.append((String)tf.elementAt(index&0xffff), null);
							}
						}
						String []a=TEXT.split(st, ' ');
						if (a.length==1)
							break;
						else
							st=a[a.length-1];													
					}										
				}				
			}});	    
	    MobNav1.display.setCurrent(form);
    }

}
