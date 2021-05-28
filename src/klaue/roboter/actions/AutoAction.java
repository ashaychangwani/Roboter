package klaue.roboter.actions;

import java.awt.Point;
import java.io.Serializable;
import java.util.Random;


public abstract class AutoAction implements Serializable {
	private static final long serialVersionUID = -4577387308909228537L;
	
	EventType type = null;
	Point mousePosition1 = new Point(0, 0);
	Point mousePosition2 = new Point(0, 0);
	int key = 0; // can be keycode of normal key or mouse button code
	int delayMin = 0;
	int delayMax = 0;
	Random r = new Random();
	
	/**
	 * 
	 * @param type the type of event
	 * @param key the keycode of normal key (KeyEvent.xxx) or of the mouse button (InputEvent.xxx)
	 */
	public AutoAction(EventType type, int key) {
		this.type = type;
		this.key = key;
	}

	public Point getMousePosition() {
		int high = Math.max(mousePosition1.x,mousePosition2.x);
		int low = Math.min(mousePosition1.x,mousePosition2.x);
		int x = r.nextInt(high-low) + low;
		
		high = Math.max(mousePosition1.y,mousePosition2.y);
		low = Math.min(mousePosition1.y,mousePosition2.y);
		
		if(mousePosition2.x == 0 && mousePosition2.y == 0)
			return new Point(0,0);
		 
		int y = r.nextInt(high-low) + low;

		return new Point(x,y);
	}
	public Point[] getMousePositionMatrix() {
		return new Point[]{new Point(mousePosition1.x,mousePosition1.y), new Point(mousePosition2.x,mousePosition2.y)};
	}
	public void setMousePosition(Point mousePosition1,Point mousePosition2) {
		this.mousePosition1 = mousePosition1;
		this.mousePosition2 = mousePosition2;
	}

	public int getDelay() {
		return this.delayMin;
	}
	
	public int getDelayMax(){
		return this.delayMax;
	}

	public void setDelay(int delayMin, int delayMax) {
		this.delayMin = delayMin;
		this.delayMax = delayMax;
	}

	public EventType getType() {
		return this.type;
	}

	public int getKey() {
		return this.key;
	}
	
	public abstract String getDescription();
}
