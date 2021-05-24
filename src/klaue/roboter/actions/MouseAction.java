package klaue.roboter.actions;

import java.awt.Point;

public class MouseAction extends AutoAction {
	private static final long serialVersionUID = 5603943243425418893L;

	public MouseAction(int key, Point pos, Point pos2) {
		super(EventType.MOUSE, key);
		setMousePosition(pos,pos2);
	}

	@Override
	public String getDescription() {
		return "A mouse click";
	}

}
