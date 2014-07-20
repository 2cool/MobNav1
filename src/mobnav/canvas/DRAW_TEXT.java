package mobnav.canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;


public class DRAW_TEXT {
	private Graphics g=null;
	private int width;
	Point pos=new Point(0,0);
	
	static public void drawWBString(final Graphics g, final  Font f, final String str, final int x,final int y){
	        g.setFont(f);
	        g.setColor(-1);
	        g.drawString(str, x-1, y-1, 0);
	        g.drawString(str, x+1, y+1, 0);
	        g.setColor(0);
	        g.drawString(str, x, y, 0);
	    }
	public DRAW_TEXT(final Graphics g, final int width){
		this.g=g;
		this.width=width;
		//this.height=height;	
	
	}
	public void reset(){
		pos.x=pos.y=0;
	}
	public int getSubStringI(final String str, final int size){
		int len=str.length();
		int end=len>>1;
		
		if (NAVI.font.substringWidth(str, 0, end)<size){
			while (++end<=len && NAVI.font.substringWidth(str, 0, end)<size);
			return end-1;
		}else if (end>1)
			return getSubStringI(str.substring(0,end),size);
		else
			return 0;

	}
	public void drawLeft(final String str){
		if (pos.x>0)
			pos.y+=NAVI.fontH;
		drawWBString(g, NAVI.font, str, 0, pos.y);				
		pos.x=NAVI.font.stringWidth(str)+NAVI.fontH;
	}
	public void drawRight(final String str){
		drawWBString(g, NAVI.font, str, width-NAVI.font.stringWidth(str), pos.y);
		pos.x=0;
		pos.y+=NAVI.fontH;
		
	}
	public void draw(final String str){
		int w=NAVI.font.stringWidth(str);		
		if (pos.x+w<=width){
			if (pos.x==0){
				drawWBString(g, NAVI.font, str, pos.x, pos.y);				
				pos.x+=w+NAVI.fontH;
			}else{
				drawWBString(g, NAVI.font, str, width-w, pos.y);
				//pos.y+=NAVI.fontH;
				pos.x=width;
				
			}
		}else{ 
			pos.y+=NAVI.fontH;
			pos.x=0;
			if (w<=width){			
				drawWBString(g, NAVI.font, str, pos.x, pos.y);				
				pos.x+=w+NAVI.fontH;			
			}else{	
				int subStrEnd=0,subStrBeg;
				do{
					subStrBeg=subStrEnd;
					subStrEnd=str.length();
					subStrEnd=subStrBeg+getSubStringI(str.substring(subStrBeg,subStrEnd),width);
					drawWBString(g, NAVI.font, str.substring(subStrBeg, subStrEnd),0,pos.y);
					pos.y+=NAVI.fontH;					
				}while(subStrEnd<str.length()-1);
				pos.x=0;		
		}
	}
		
	}
	
	
}
