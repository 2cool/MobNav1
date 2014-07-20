package mobnav.canvas;

import javax.microedition.lcdui.Graphics;

public interface MODE {
	BUTON_SOFT_KEY leftsk();
	BUTON_SOFT_KEY rightsk();
	BUTON_SOFT_KEY centrsk();    
	BUTON_TOUCH_SCR plus();
	BUTON_TOUCH_SCR minus();
	BUTON_TOUCH_SCR add();
    BUTON_TOUCH_SCR light();
    BUTON_TOUCH_SCR next();
	void paint(Graphics g);
	void keyPressed(int keyCode); 
	void keyReleased(int keyCode);
	void keyRepeated(int keyCode);
	void pointerPressed(int x, int y);
	void pointerDragged(int x, int y);
	void pointerReleased(int x, int y);
	

	public void dublClick(int x, int y);
	public void click(int x, int y);
	public void longClick(int x, int y);


}