package localizer;

import java.util.ArrayList;

import odometry.Odometer;
import odometry.WheelDriver;
import pollerContinuous.ContinuousColorPoller;
import utilities.Display;
import utilities.Hardware;
import utilities.Util;

/**
 * LightLocalizer.java
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

public class LightLocalizer extends Localizer {

	private ArrayList<Integer> results;
	private ArrayList<Double> angle;

	public LightLocalizer(Odometer odometer, WheelDriver navigator, ContinuousColorPoller poller) {
		super(odometer, navigator, poller);
	}

	@Override
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees

		odometer.setTheta(0);

		// navigator.setSpeed(Navigator.SPEED_VERY_SLOW);
		navigator.setSpeed(WheelDriver.SPEED_SLOW);

		continuousPoller.clearData();
		continuousPoller.setEnable(true);
		continuousPoller.start();

		navigator.turn(Math.toRadians(90));
		navigator.turn(Math.toRadians(90));
		navigator.turn(Math.toRadians(90));
		navigator.turn(Math.toRadians(90));

		continuousPoller.setEnable(false);
		processData();

		navigator.setSpeed(WheelDriver.SPEED_SLOW);
		navigator.travelTo(0, 0);
		while (navigator.isNavigating());
		navigator.turnTo(0);
	}

	/**
	 * Determine the four angle at which the light sensor meets the grid.
	 * The first and third time the light sensor meets the grid is the y axis.
	 * The second and fourth time the light sensor meets the grid is the x axis.
	 * Base on those, calculate the robot's position and orientation.
	 * Finish by moving the robot to (0,0) and turn to 0 rad.
	 */
	private void processData() {
		results = continuousPoller.getResults();
		angle = continuousPoller.getAngles();
		
		results = shallowCopyInteger(results);
		angle = shallowCopyDouble(angle);

		simplify();

		double firsty, secondy, firstx, secondx;

		firsty = angle.get(0);
		secondy = angle.get(2);
		firstx = angle.get(1);
		secondx = angle.get(3);

		Display.printDebug(Util.doubleToString(Math.toDegrees(angle.get(0)), 2) + "", 3);
		Display.printDebug(Util.doubleToString(Math.toDegrees(angle.get(1)), 2) + "", 4);
		Display.printDebug(Util.doubleToString(Math.toDegrees(angle.get(2)), 2) + "", 5);
		Display.printDebug(Util.doubleToString(Math.toDegrees(angle.get(3)), 2) + "", 6);

		
		float offsetAcross = Hardware.COLOR_SENSOR_TO_CENTRE_ACROSS;
		float offsetFront = Hardware.COLOR_SENSOR_TO_CENTRE_UP;
		double d = Math.sqrt(offsetAcross*offsetAcross + offsetFront*offsetFront) ;
		double dy = secondy - firsty;
		double dx = secondx - firstx;

		double x = -d * Math.cos(dy / 2);
		double y = -d * Math.cos(dx / 2);
		double theta = -firsty - (dy) / 2 - Math.atan(offsetAcross/offsetFront);
		
		navigator.setEnable(false);

		odometer.setX(x);
		odometer.setY(y);
		odometer.setTheta(theta);
		
		navigator.stopRobot();
		navigator.setEnable(true);

		continuousPoller.clearData();//*/
	}

	/**
	 * Get the 4 angles at which the light sensor meets the dark grid.
	 * Using median to filter out outlier
	 */
	private void simplify() {
		ArrayList<Double> output = new ArrayList<Double>();

		int start = 0;
		for (int i = 1; i < angle.size(); i++) {
			if (!Util.close(angle.get(i - 1), angle.get(i), Math.toRadians(10))) {
				output.add(median(angle, start, i));
				start = i;
			} else if (i == angle.size() - 1) {
				output.add(median(angle, start, angle.size()));
			}
		}

		Display.printDebug("Count: " + output.size(), 7);
		angle = output;
	}

	/**
	 * @param input
	 *            array of data
	 * @param startIndex
	 *            where the median calculation starts
	 * @param endIndex
	 *            where the median calculation ends
	 * @return median of the elements from startIndex to endIndex
	 */
	private static double median(ArrayList<Double> input, int startIndex, int endIndex) {
		return input.get((startIndex + endIndex) / 2);
	}

	/**
	 * 
	 * @param input array to be copy
	 * @return a shallow copy of the ArrayList input
	 */
	private static ArrayList<Integer> shallowCopyInteger(ArrayList<Integer> input) {
		ArrayList<Integer> output = new ArrayList<Integer>();
		for (int i = 0; i < input.size(); i++) {
			output.add(input.get(i));
		}
		return output;
	}

	/**
	 * 
	 * @param input array to be copy
	 * @return a shallow copy of the ArrayList input
	 */
	private static ArrayList<Double> shallowCopyDouble(ArrayList<Double> input) {
		ArrayList<Double> output = new ArrayList<Double>();
		for (int i = 0; i < input.size(); i++) {
			output.add(input.get(i));
		}
		return output;
	}
}