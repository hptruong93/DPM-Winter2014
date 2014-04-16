package pollerWithHandler;

import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import data.ColorData;
import data.DataProcessor;

/**
 * ColorPoller.java
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
 * Color poller that will pass ColorData to its DataProcessor
 *
 */
public class ColorPoller extends StaticPollerWithHandler {

	private static final int PERIOD = 50;
	protected ColorSensor colorSensor;
	
	public ColorPoller(DataProcessor dataProcessor, ColorSensor colorSensor) {
		super(dataProcessor);
		this.colorSensor = colorSensor;
		colorSensor.setFloodlight(Color.RED);
	}
	
	@Override
	protected int getPeriod() {
		return PERIOD;
	}

	@Override
	public ColorData getData() {
		return new ColorData(colorSensor.getRawColor());
	}
}