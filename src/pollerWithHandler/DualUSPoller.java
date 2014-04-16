package pollerWithHandler;

import lejos.nxt.UltrasonicSensor;
import data.DataProcessor;
import data.DataProvider;
import data.DualUSData;

/**
 * DualUSPoller.java
 * ECSE 211 - TEAM 16
 *
 * Hoai Phuoc Truong - 260526454
 * Francis O'brien - 260582444
 * Alex Reiff - 260504962
 * Juan Morency Trudel - 260481762
 * Vlad Kesler - 260501714
 * Henry Wang - 260580986
 * 
 * Date : 09 April 2014
 */

/**
 * This class is created to deal with the fact that two us sensors cannot poll at the same time
 * without interference. This class provides an API for two us sensors to ping in turns so that
 * no interference may occur and the reading is reliable.
 *
 */
public class DualUSPoller extends PollerWithHandler {
	
	private static final int PERIOD = 30;
	private static final int FIRST_S_TURN = 0;
	private static final int SECOND_S_TURN = 1;
	private int turn;
	private UltrasonicSensor[] sensors;
	
	public DualUSPoller(DataProcessor controller, UltrasonicSensor[] sensors) {
		super(controller);
		this.sensors = sensors;
		
		for (UltrasonicSensor sensor : sensors) {
			try {
				sensor.off();
			} catch (Exception e) {} //In case sensor is already off
		}
		
		turn = FIRST_S_TURN;
	}
	
	/**
	 * Use the appropriate sensor (based on turn) to acquire reading. 
	 */
	@Override
	public DataProvider getData() {
		int data = 255, side;
		
		sensors[turn].ping();
		try {
			Thread.sleep(30);
			data = sensors[turn].getDistance();
		} catch (InterruptedException e) {
			//Does not expected to be interrupted
		} catch (Exception e) {//Error occurs
			data = 255;
		}
		
		
		if (turn == FIRST_S_TURN) {
			side = DualUSData.LEFT_SIDE;
		} else if (turn == SECOND_S_TURN) {
			side = DualUSData.RIGHT_SIDE;
		} else {
			throw new IllegalStateException("Invalid sensor turn!");
		}
		
		return new DualUSData(data, side);
	}

	/**
	 * Change to the next us sensor that is about to ping in the next run.
	 */
	@Override
	public void nextState() {
		turn = (turn + 1) % (sensors.length);
	}

	@Override
	protected int getPeriod() {
		return PERIOD;
	}
}