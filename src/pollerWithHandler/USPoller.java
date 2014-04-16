package pollerWithHandler;

import lejos.nxt.UltrasonicSensor;
import data.DataProcessor;
import data.UltraSonicData;

/**
 * USPoller.java
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
 * Ultrasonic Poller that will pass UltraSonic data
 *
 */
public class USPoller extends StaticPollerWithHandler {
	private static final int PERIOD = 20;
	private UltrasonicSensor sensor;
	
	public USPoller(DataProcessor controller, UltrasonicSensor sensor) {
		super(controller);
		this.sensor = sensor;
	}

	@Override
	protected int getPeriod() {
		return PERIOD;
	}

	@Override
	public UltraSonicData getData() {
		return new UltraSonicData(sensor.getDistance());
	}
}
