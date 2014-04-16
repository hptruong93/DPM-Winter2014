package localizer;

import java.util.ArrayList;

import odometry.Odometer;
import odometry.WheelDriver;
import pollerContinuous.ContinuousUSPoller;
import utilities.Display;

/**
 * USLocalizerDoubleEdge.java
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

public class USLocalizerDoubleEdge extends Localizer {

	private double left, back;
	
	public USLocalizerDoubleEdge(Odometer odometer, WheelDriver navigator, ContinuousUSPoller poller) {
		super(odometer, navigator, poller);
	}

	@Override
	public void doLocalization() {
		continuousPoller.start();
		continuousPoller.setEnable(true);

		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}

		continuousPoller.setEnable(false);

		boolean isFacingSpace = isFacingSpace(continuousPoller.getResults());

		detectWall(isFacingSpace);
	}

	/**
	 * Using both falling edge and rising edge detection to get the angle at which the robot sees the left wall
	 * and the back wall. The robot will turn until it see an edge (either rising or falling) and detect if it is the left
	 * or the back wall. The robot will then turn the other way until it sees the other wall.
	 * 
	 * In the rising edge implementation (class RisingEdge), the robot will only detect wall if there is a rising edge.
	 * In the falling edge implementation (class RisingEdge), the robot will only detect wall if there is a falling edge.
	 * @param isFacingSpace if the robot is facing space or wall at the initial position
	 */
	private void detectWall(boolean isFacingSpace) {
		odometer.setTheta(0);

		continuousPoller.clearData();
		continuousPoller.setEnable(true);

		navigator.turnClockWise(WheelDriver.SPEED_VERY_SLOW);
		process(false);
		
		continuousPoller.setEnable(false);
		continuousPoller.clearData();

		navigator.turnCounterClockWise(WheelDriver.SPEED_VERY_SLOW);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		continuousPoller.clearData();
		continuousPoller.setEnable(true);

		process(true);
		
		navigator.stopRobot();
		continuousPoller.setEnable(false);
		continuousPoller.clearData();
		
		Display.printDebug(Math.toDegrees(left) + "", 4);
		Display.printDebug(Math.toDegrees(back) + "", 5);
		
		double hundredEighty = left + (back - left) / 4;

		if (isFacingSpace) {
			odometer.setTheta(Math.toRadians(180) - hundredEighty + odometer.getTheta());
		} else {
			odometer.setTheta(Math.toRadians(90) - hundredEighty + odometer.getTheta());
		}
		navigator.setSpeed(WheelDriver.SPEED_SLOW);
		navigator.turnTo(Math.toRadians(0));
	}

	/**
	 * Detect the angle at which the robot sees the left wall or the back wall base on the rotation of the robot.
	 * This uses both falling edge and rising edge detection:
	 * 	In counter-clockwise rotation: falling edge means left wall and rising edge means back wall
	 *  In clockwise rotation: falling edge means back wall and rising edge means left wall
	 * @param isCounterClockWise if the robot is rotating counter clock wise or not
	 */
	private void process(boolean isCounterClockWise) {
		while (true) {
			boolean quitting = false;
			ArrayList<Integer> results = continuousPoller.getResults();
			ArrayList<Double> angle = continuousPoller.getAngles();

			int count = 0;

			if (results.size() - count > 2) {
				for (int i = count; i < results.size() - 2; i++) {
					int current = results.get(i);
					int next = results.get(i + 1);
					int nextnext = results.get(i + 2);

					if (isCounterClockWise) {
						if (current > THRESHOLD && next < THRESHOLD && nextnext < THRESHOLD) {// Left wall
							left = angle.get(i);
							return;
						} else if (current < THRESHOLD && next > THRESHOLD && nextnext > THRESHOLD) {// Back wall
							back = angle.get(i);
							return;
						}
						
					} else {
						if (current > THRESHOLD && next < THRESHOLD && nextnext < THRESHOLD) {// Back wall
							back = angle.get(i);
							return;
						} else if (current < THRESHOLD && next > THRESHOLD && nextnext > THRESHOLD) {// Left wall
							left = angle.get(i);
							return;
						}
					}
				}
				count = results.size() - 1;
			}

			if (quitting)
				break;

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}
}