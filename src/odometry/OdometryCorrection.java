package odometry;

import utilities.FieldInfo;
import utilities.LineUtil;
import utilities.Point;
import utilities.Util;
import data.DataProcessor;
import data.DataProvider;
import data.DualColorData;
import data.DualColorData.DualColorPackage;

/**
 * OdometryCorrection.java
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
 * The odometry correction uses the two color sensors on the sides to fix the
 * robot's coordinate.
 * 
 * 
 */
public class OdometryCorrection implements DataProcessor {

	private static final int CORRECTION_THRESHOLD = 7;
	private static final int CORRECTION_PERIOD = 1000;
	private Odometer odometer;
	private int[] last;
	private long lastTime;

	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		last = new int[2];
	}

	/**
	 * This methods expects a DualColorData so that it knows which side the
	 * reading is from. This methods will fix the robot's x and y coordinate.
	 * This method should not, and need not be thread-safe. Concurrent
	 * modifications should be allowed since odometer already has
	 * synchronization & thread-safe operations.
	 */
	@Override
	public void handleData(DataProvider data) {
		DualColorPackage colorReading = ((DualColorData) data).getData();

		int side = colorReading.getSide();

		// Display.printDebug("V: " + (value - last[side]), 2);

		Point sensorPosition = odometer.getColorSensorPosition(side);

		double[] xCLAD = LineUtil.findClosest(sensorPosition.getX());
		double[] yCLAD = LineUtil.findClosest(sensorPosition.getY());

		// Debug code
		// Display.printDebug("line X: " + xCLAD[0], 6);
		// Display.printDebug("line Y: " + yCLAD[0], 7);

		/**
		 * The robot will choose the closest grid line in both x and y position, and pick the
		 * closest line to fix its coordinate. Centre coordinate is fixed by the exact same amount
		 * that the sensor coordinate is fixed.
		 */
		if (System.currentTimeMillis() - lastTime > CORRECTION_PERIOD && isLine(colorReading)) {
			double xCenter, yCenter;

			xCenter = odometer.getX();
			yCenter = odometer.getY();

			if (xCLAD[1] < yCLAD[1]) {
				double newX = xCenter + xCLAD[0] * FieldInfo.TILE_WIDTH - sensorPosition.getX();

				if (Util.close(newX, xCenter, CORRECTION_THRESHOLD)) {
					odometer.setX(newX);
				}
				lastTime = System.currentTimeMillis();
			} else {
				double newY = yCenter + yCLAD[0] * FieldInfo.TILE_WIDTH - sensorPosition.getY();
				if (Util.close(newY, yCenter, CORRECTION_THRESHOLD)) {
					odometer.setY(newY);
				}
				lastTime = System.currentTimeMillis();
			}
		}
		update(colorReading);
	}

	/**
	 * Check if reading is a line. This is implemented by comparing the current
	 * reading to the previously read value by the sensor. If the value
	 * increases by FieldInfo.LIGHT_READING_LINE_TRESHOLD, the newly read value
	 * is a line.
	 * 
	 * @param reading
	 *            color provided by the color sensor
	 * @return if the color indicates a grid line or not
	 */
	private boolean isLine(DualColorPackage reading) {
		return (reading.getReading() - last[reading.getSide()]) < FieldInfo.LIGHT_READING_LINE_TRESHOLD;
	}

	/**
	 * Update past reading
	 * 
	 * @param value
	 *            DualColorPackage received from the color sensor
	 */
	private void update(DualColorPackage value) {
		last[value.getSide()] = value.getReading();
	}
}
