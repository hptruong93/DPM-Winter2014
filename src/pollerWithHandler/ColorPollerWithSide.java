package pollerWithHandler;

import lejos.nxt.ColorSensor;
import data.DataProcessor;
import data.DualColorData;

/**
 * ColorPollerWithSide.java
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
 * This is created to have same implementation for both color sensors on two sides.
 *
 */
public class ColorPollerWithSide extends StaticPollerWithHandler {

	public static final int LEFT_SIDE = 0;
	public static final int RIGHT_SIDE = 1;
	private static final int PERIOD = 40;
	
	private int side;
	protected ColorSensor colorSensor;
	
	/**
	 * Constructor
	 * @param dataProcessor @see Poller.java
	 * @param colorSensor Color sensor that will provide data
	 * @param side LEFT_SIDE or RIGHT_SIDE
	 */
	public ColorPollerWithSide(DataProcessor dataProcessor, ColorSensor colorSensor, int side) {
		super(dataProcessor);
		this.colorSensor = colorSensor;
		this.side = side;
		colorSensor.setFloodlight(true);
	}
	
	@Override
	protected int getPeriod() {
		return PERIOD;
	}

	@Override
	public DualColorData getData() {
		return new DualColorData(colorSensor.getRawLightValue(), side);
	}
}