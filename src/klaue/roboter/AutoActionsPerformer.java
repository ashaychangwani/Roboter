package klaue.roboter;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

import java.util.Random;

import klaue.roboter.actions.ActionsPacket;
import klaue.roboter.actions.AutoAction;
import klaue.roboter.actions.EventType;

public class AutoActionsPerformer implements Runnable {
	ActionsPacket actionsPacket = null;
	boolean stopped = true;
	boolean abort = false;

	Robot robot = new Robot();
	
	Random rand = new Random();
	public AutoActionsPerformer(ActionsPacket packet) throws AWTException {
		if (packet == null) return;
		this.actionsPacket = packet;
	}
	
	@Override
	public void run() {
		this.stopped = false;
		
		sleepFor(this.actionsPacket.preDelay);
		
		if (this.actionsPacket.repetitions > 0) {
			for (int i = 0; i < this.actionsPacket.repetitions; ++i) {
				oneLoop(i == this.actionsPacket.repetitions - 1);
				if (this.abort) break;
			}
		} else {
			// endless repetition
			while(true) {
				oneLoop(false);
				if (this.abort) break;
			}
		}
		this.stopped = true;
		this.abort = false;
	}
	
	private void oneLoop(boolean lastLoop) {
		
		
		for (int j = 0; j < this.actionsPacket.getList().size(); ++j) {
			AutoAction ao;
			if(this.actionsPacket.random)
				ao = this.actionsPacket.getList().get(rand.nextInt(this.actionsPacket.getList().size()));
			else
				ao = this.actionsPacket.getList().get(j);
			if (this.abort) break;
			
			Point position = null;
			Point prePosition = null;
			boolean moveMouse = false;
			if (ao.getType() == EventType.MOUSE) {
				position = ao.getMousePosition();
				if (position.x != 0 || position.y != 0) {
					moveMouse = true;
					if (this.actionsPacket.returnMouse) {
						prePosition = MouseInfo.getPointerInfo().getLocation();
					}
				}
			}
			
			if (moveMouse) {
				this.robot.mouseMove(position.x, position.y);
			}
			
			if (ao.getType() == EventType.MOUSE) {
				try{
					this.robot.keyPress(3675);
					this.robot.keyRelease(3675);
					this.robot.mousePress(ao.getKey());
					this.robot.mouseRelease(ao.getKey());
				}
				catch(Exception e){
					e.printStackTrace();
				}
			} else {
				try{
					this.robot.keyPress(ao.getKey());
					this.robot.keyRelease(ao.getKey());
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
			}
			
			if (prePosition != null) { // only not null if should be returned
				this.robot.mouseMove(prePosition.x, prePosition.y);
			}

			// don't sleep for the very last one
			if (j != this.actionsPacket.getList().size() - 1 || !lastLoop) {
				int dMin = ao.getDelay();
				int dMax = ao.getDelayMax();
				Random r = new Random();
				sleepFor(r.nextInt(dMax-dMin) + dMin);
			}
		}
	}
	
	/**
	 * sleep for a max of delay in increments of 100 ms to catch abort
	 * @param delay
	 */
	private void sleepFor(int delay) {
		int rest = (delay > 100) ? delay % 100 : delay;
		int fullParts = (delay > 100) ? delay / 100 : 0;
		
		int[] parts = new int[fullParts + (rest > 0 ? 1 : 0)];
		for (int i = 0; i < fullParts; ++i) {
			parts[i] = 100;
		}
		if (rest > 0) parts[fullParts] = rest;
		
		for (int part : parts) {
			if (this.abort) break;
			try {
				Thread.sleep(part);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isRunning() {
		if (this.abort) return false; // fake it till you make it
		return !this.stopped;
	}
	
	public void abort() {
		this.abort = true;
	}

}
