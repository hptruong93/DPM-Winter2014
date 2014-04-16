package localizer;
import java.util.ArrayList;

import odometry.Odometer;
import odometry.WheelDriver;
import lejos.nxt.Sound;
import pollerContinuous.ContinuousUSPoller;
import utilities.Util;

/**
 * USLocalizer360Rotation.java
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

public class USLocalizer360Rotation extends Localizer {
	public static double debug;

	public USLocalizer360Rotation(Odometer odometer, WheelDriver navigator, ContinuousUSPoller poller) {
		super(odometer, navigator, poller);
	}

	/**
	 * The robot would turn 360 degree and detect the two minimums based on the angles at
	 * which it sees the walls. Then the robot will deduce its original angle and turn to 0. 
	 */
	@Override
	public void doLocalization() {
		navigator.setSpeed(WheelDriver.SPEED_VERY_SLOW);
		continuousPoller.setEnable(false);
		continuousPoller.start();

		while (true) {
			continuousPoller.setEnable(true);

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}

			continuousPoller.setEnable(false);

			if (!isFacingSpace(continuousPoller.getResults())) {
				continuousPoller.clearData();
				navigator.turn(Math.toRadians(90));
			} else {
				odometer.setTheta(Math.toRadians(90));
				break;
			}
		}

		Sound.beep();
		
		continuousPoller.clearData();
		continuousPoller.setEnable(true);
		navigator.turn(Math.toRadians(180));
		navigator.turn(Math.toRadians(180));
		continuousPoller.setEnable(false);
		double hundredEighty = USLocalizer360Rotation.detectAngle(continuousPoller.getResults(), continuousPoller.getAngles());
		debug = Math.toDegrees(hundredEighty);

		continuousPoller.clearData();
		
		odometer.setTheta(Util.fix2Pi(Math.toRadians(270) - hundredEighty));
		
		continuousPoller.setEnable(false);
		
		navigator.setSpeed(WheelDriver.SPEED_SLOW);
		navigator.turnTo(Math.toRadians(0));
		navigator.stopRobot();
	}

	/**
	 * This method uses the idea that the minimums will be the position where the robot directly faces the wall.
	 * The minimum is determined from the two edges (falling edge & rising edge).
	 * @param readValues ultrasonic sensor values
	 * @param angle angle at which ultrasonic sensor values are read. Same indexing with readVaues array
	 * @return the index at which the robot is directly facing the left wall (180 degree from x axis)
	 */
	private static double detectAngle(ArrayList<Integer> readValues, ArrayList<Double> angle) {
		int leftBound = 0, rightBound = 0;

		for (int i = 1; i < readValues.size() - 1; i++) {
			int prev = readValues.get(i - 1);
			int after = readValues.get(i + 1);
			int current = readValues.get(i);

			if (prev > THRESHOLD && current < THRESHOLD) {
				leftBound = i;
			}
			if (current < THRESHOLD && after > THRESHOLD) {
				rightBound = i;
			}
		}
		int index = leftBound + (rightBound - leftBound) / 4;
		try {
			double output = angle.get(index);
			return output;
		} catch (Exception e) {
			return index;
		}
	}
}