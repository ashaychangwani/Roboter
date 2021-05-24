package klaue.roboter.gui.dialog;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import klaue.roboter.actions.AutoAction;
import klaue.roboter.actions.EventType;
import klaue.roboter.actions.MouseAction;
import klaue.roboter.gui.NumberOnlyFilter;
import layout.TableLayout;
import layout.TableLayoutConstants;

public class MousePanel extends EventPanel implements NativeKeyListener {
	private static final long serialVersionUID = 2464527657257956159L;

	JLabel lblMouseButton = new JLabel("Mouse button to press:");
	JLabel lblMousePosition = new JLabel("Position to click");
	JLabel lblMousePosition2 = new JLabel("Position to click");
	JLabel lblCurMousePos = new JLabel("Current mouse position:");
	JLabel lblCurPosX = new JLabel("");
	JLabel lblCurPosY = new JLabel("");
	JLabel lblPosHelp = new JLabel("Set to position 0x0 to click where the mouse is");

	JRadioButton btnLeft = new JRadioButton("Left");
	JRadioButton btnMiddle = new JRadioButton("Middle");
	JRadioButton btnRight = new JRadioButton("Right");

	JTextField txtXPos_1 = new JTextField("0");
	JTextField txtYPos_1 = new JTextField("0");
	JTextField txtXPos_2 = new JTextField("0");
	JTextField txtYPos_2 = new JTextField("0");

	MousePosReporter mousePositionReporter = null;
	Preferences prefs = Preferences.userRoot().node("klaue/roboter");
	int hotkeyMousePos_1 =  this.prefs.getInt("hotkey_mousepos", NativeKeyEvent.VC_F7);
	int hotkeyMousePos_2 =  this.prefs.getInt("hotkey_mousepos", NativeKeyEvent.VC_F8);

	public MousePanel(AutoAction aa) {
		this();
		setAutoAction(aa);
	}

	public MousePanel() {
		GlobalScreen.addNativeKeyListener(this);

		double size[][] =
            {{TableLayoutConstants.PREFERRED, 20, TableLayoutConstants.PREFERRED, 20, TableLayoutConstants.PREFERRED, 20, TableLayoutConstants.PREFERRED, TableLayoutConstants.FILL}, // column widths
             {TableLayoutConstants.PREFERRED, 20, TableLayoutConstants.PREFERRED, 20, TableLayoutConstants.PREFERRED, 20, TableLayoutConstants.PREFERRED}}; // row heights
		this.setLayout(new TableLayout(size));

		this.add(this.lblMouseButton, "0, 0");
		this.btnLeft.setSelected(true);
		ButtonGroup grp = new ButtonGroup();
		grp.add(this.btnLeft);
		grp.add(this.btnMiddle);
		grp.add(this.btnRight);
		JPanel pnlBut = new JPanel();
		pnlBut.setLayout(new BoxLayout(pnlBut, BoxLayout.X_AXIS));
		pnlBut.add(this.btnLeft);
		pnlBut.add(Box.createHorizontalStrut(10));
		pnlBut.add(this.btnMiddle);
		pnlBut.add(Box.createHorizontalStrut(10));
		if (GlobalScreen.isNativeHookRegistered()) {
			this.lblMousePosition.setText(this.lblMousePosition.getText() + " (" + NativeKeyEvent.getKeyText(this.hotkeyMousePos_1) + "):");
		} else {
			this.lblMousePosition.setText(this.lblMousePosition.getText() + ":");
		}
		if (GlobalScreen.isNativeHookRegistered()) {
			this.lblMousePosition2.setText(this.lblMousePosition2.getText() + " (" + NativeKeyEvent.getKeyText(this.hotkeyMousePos_2) + "):");
		} else {
			this.lblMousePosition2.setText(this.lblMousePosition2.getText() + ":");
		}

		this.add(this.lblMousePosition, "0, 2");
		this.add(this.lblMousePosition2, "0, 3");
		this.txtXPos_1.setPreferredSize(new Dimension(50, this.txtXPos_1.getPreferredSize().height));
		((AbstractDocument)this.txtXPos_1.getDocument()).setDocumentFilter(new NumberOnlyFilter());
		this.add(this.txtXPos_1, "2, 2");
		this.add(new JLabel("x"), "4, 2");
		this.txtYPos_1.setPreferredSize(new Dimension(50, this.txtYPos_1.getPreferredSize().height));
		((AbstractDocument)this.txtYPos_1.getDocument()).setDocumentFilter(new NumberOnlyFilter());
		this.add(this.txtYPos_1, "6, 2");

		this.txtXPos_2.setPreferredSize(new Dimension(50, this.txtXPos_2.getPreferredSize().height));
		((AbstractDocument)this.txtXPos_2.getDocument()).setDocumentFilter(new NumberOnlyFilter());
		this.add(this.txtXPos_2, "2, 3");
		this.add(new JLabel("x"), "4, 3");
		this.txtYPos_2.setPreferredSize(new Dimension(50, this.txtYPos_2.getPreferredSize().height));
		((AbstractDocument)this.txtYPos_2.getDocument()).setDocumentFilter(new NumberOnlyFilter());
		this.add(this.txtYPos_2, "6, 3");

		this.add(this.lblCurMousePos, "0, 4");
		this.add(this.lblCurPosX, "2, 4");
		this.add(new JLabel("x"), "4, 4");
		this.add(this.lblCurPosY, "6, 4");

		this.add(this.lblPosHelp, "0, 6, 7, 6");

		// add mouse pos reporter
		this.mousePositionReporter = new MousePosReporter(this.lblCurPosX, this.lblCurPosY);
		new Thread(this.mousePositionReporter).start();
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		this.mousePositionReporter.cancel();
	}


	@Override
	public AutoAction getAutoAction() {
		if (this.txtXPos_1.getText().trim().isEmpty() || this.txtYPos_1.getText().trim().isEmpty() || this.txtXPos_2.getText().trim().isEmpty() || this.txtYPos_2.getText().trim().isEmpty()) return null;

		int mouseKey = 0;
		if (this.btnLeft.isSelected()) {
			mouseKey = InputEvent.BUTTON1_MASK;
		} else if (this.btnMiddle.isSelected()) {
			mouseKey = InputEvent.BUTTON2_MASK;
		} else if (this.btnRight.isSelected()) {
			mouseKey = InputEvent.BUTTON3_MASK;
		}
		int x1 = Integer.parseInt(this.txtXPos_1.getText().trim());
		int y1 = Integer.parseInt(this.txtYPos_1.getText().trim());
		int x2 = Integer.parseInt(this.txtXPos_2.getText().trim());
		int y2 = Integer.parseInt(this.txtYPos_2.getText().trim());
		Point position = new Point(x1,y1);
		Point position2 = new Point(x2,y2);

		return new MouseAction(mouseKey, position, position2);
	}

	@Override
	public EventType getType() {
		return EventType.MOUSE;
	}

	@Override
	public void setAutoAction(AutoAction aa) {
		if (aa == null || aa.getType() != EventType.MOUSE) return;
		setMouseAction((MouseAction)aa);
	}
		
	public void setMouseAction(MouseAction ma) {
		if (ma == null) return;
		
		switch(ma.getKey()) {
			default:
			case InputEvent.BUTTON1_MASK:
				this.btnLeft.setSelected(true);
				break;
			case InputEvent.BUTTON2_MASK:
				this.btnMiddle.setSelected(true);
				break;
			case InputEvent.BUTTON3_MASK:
				this.btnRight.setSelected(true);
				break;
		}
		
		Point mousePos[] = ma.getMousePositionMatrix();
		this.txtXPos_1.setText(Integer.toString(mousePos[0].x));
		this.txtYPos_1.setText(Integer.toString(mousePos[0].y));
		this.txtXPos_2.setText(Integer.toString(mousePos[1].x));
		this.txtYPos_2.setText(Integer.toString(mousePos[1].y));
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nke) {
		if (nke.getKeyCode() == this.hotkeyMousePos_1) {
			Point p = MouseInfo.getPointerInfo().getLocation();
			this.txtXPos_1.setText(Integer.toString((int)p.getX()));
			this.txtYPos_1.setText(Integer.toString((int)p.getY()));
		}
		else if (nke.getKeyCode() == this.hotkeyMousePos_2) {
			Point p = MouseInfo.getPointerInfo().getLocation();
			this.txtXPos_2.setText(Integer.toString((int)p.getX()));
			this.txtYPos_2.setText(Integer.toString((int)p.getY()));
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {}
}

class MousePosReporter implements Runnable {
	boolean canceled = false;
	JLabel xLabel = null;
	JLabel yLabel = null;
	
	public MousePosReporter(JLabel xLabel, JLabel yLabel) {
		this.xLabel = xLabel;
		this.yLabel = yLabel;
	}
	
	@Override
	public void run() {
		while(true) {
			if (this.canceled) break;
			
			Point p = MouseInfo.getPointerInfo().getLocation();
			this.xLabel.setText(Integer.toString((int)p.getX()));
			this.yLabel.setText(Integer.toString((int)p.getY()));
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void cancel() {
		this.canceled = true;
	}
}
