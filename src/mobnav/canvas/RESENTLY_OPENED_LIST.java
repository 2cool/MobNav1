package mobnav.canvas;
import java.util.Stack;

public class RESENTLY_OPENED_LIST {
	static private int nextIndex=-1;
	static public String GetFirst(){return (list.size()>0)?(String)list.elementAt(0):null;}
	static private long time=0;
	static private String curName=null;
	static private Stack list=new Stack();
	static public void Clear(){		
		
		list.removeAllElements();	
		add(NAVI.map.fname);

	}
	static int Size(){System.out.println("SIZE="+list.size());return list.size();}
	//------------------------------------------------------------------------------
	static public void Rename(final String oldName, final String newName){
		for (int i=0; i<list.size();i++)
			if (oldName.equals(list.elementAt(i))){
				list.setElementAt(newName, i);
				if (curName!=null && curName.equals(oldName))
					curName=oldName;				
			}
	}
	static public void Remove(final String name){
		for (int i=0; i<list.size();i++)
			if (name.equals(list.elementAt(i))){
				list.removeElementAt(i);
				if (curName!=null && curName.equals(name))
					curName=null;
				if (list.size()<=1)
		        	Interface.NEXT_MAP.Off();
			}
	}
	static public String GetList(){
		if (list.size()==0)
			return "null";
		else{
			String l="";
			for (int i=0; i<list.size(); i++)
				l+=((String)list.elementAt(i))+'\t';
			return l;
		}		
	}
	static public void Load(final String l){
		list=TEXT.split(l, "\t");		
	}
	static public void add(final String name){		
		//if (!name.equals(Storage.world_map)){
			for (int index=0; index<list.size(); index++)
				if (((String)list.elementAt(index)).equals(name)){
					list.removeElementAt(index);				
					break;
				}
			list.insertElementAt(name, 0);
			nextIndex=0;
			if (list.size()>1){
	        	Interface.NEXT_MAP.On();
	        	Interface.CLEAR_MAP_LIST.On();
			}
		
	}	
	//------------------------------------------------------------------------
	static public void getNext(){			
		if (nextIndex==-1 || list.size()<1)
			return;
		do{
			if (++nextIndex>=list.size())
				nextIndex^=nextIndex;
		}while(list.size()>1 && NAVI.map.fname.equals((String)list.elementAt(nextIndex)));
		time=System.currentTimeMillis();
		curName=(String)list.elementAt(nextIndex);
		synchronized(MyCanvas.update){
			MyCanvas.update.notify();
        }
	}
	//------------------------------------------------------------------------
	static public void getFileNameToLoad(){
		if (curName!=null){
			if (System.currentTimeMillis()-time>2000){
				Storage.file2load(curName);		
				curName=null;
			}else{
				MyCanvas.Banner(" "+FILE.getName(curName,false)+" ",-1,0,NAVI.font);				
			}
			synchronized(MyCanvas.update){
				MyCanvas.update.notify();
            }
		}
		
	}
	
	
}
