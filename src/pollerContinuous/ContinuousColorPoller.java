package pollerContinuous;

import lejos.nxt.ColorSensor;
import odometry.Odometer;
import utilities.FieldInfo;

/**
 * ContinuousColorPoller.java
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
 * Continuous poller with color sensor as data reader.
 * This uses the initial reading as the "light" threshold. After that, any threshold lower than
 * initial value + 2*FieldInfo.LIGHT_READING_LINE_TRESHOLD will be considered as grid line.
 * 
 *  This poller only collects data at which it sees a gridline.
 * 
 */
public class ContinuousColorPoller extends ContinuousPoller {

	private static final int PERIOD = 10;

	protected ColorSensor colorSensor;
	private int threshold;

	public ContinuousColorPoller(ColorSensor colorSensor, Odometer odometer) {
		super(odometer);
		this.colorSensor = colorSensor;
		threshold = colorSensor.getRawLightValue() + 2*FieldInfo.LIGHT_READING_LINE_TRESHOLD;
	}

	@Override
	protected void pollActivities() {
		synchronized (lock) {
			int value = readValue();

			if (!isLight(value)) {
				results.add(value);
				angle.add(odometer.getTheta());
			}
		}
	}

	@Override
	protected int getPeriod() {
		return PERIOD;
	}

	@Override
	protected int readValue() {
		return colorSensor.getRawLightValue();
	}

	/**
	 * 
	 * @param reading
	 *            the value read using colorSensor.getRawLightValue() method
	 * @return if the value indicates space (light color)
	 */
	private boolean isLight(int reading) {
		if (reading < threshold) {
			return false;
		} else {
			return true;
		}
	}
}