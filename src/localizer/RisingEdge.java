package localizer;

import java.util.ArrayList;

import odometry.Odometer;
import odometry.WheelDriver;
import lejos.nxt.Sound;
import pollerContinuous.ContinuousUSPoller;

/**
 * RisingEdge.java
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
 * This class works using the Rising Edge detections: only detect rising edges.
 * @see USLocalizerDoubleEdge for detail explanations.
 * 
 */
public class RisingEdge extends Localizer {

	private double left, back;
	
	public RisingEdge(Odometer odometer, WheelDriver navigator, ContinuousUSPoller poller) {
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

	private void detectWall(boolean isFacingSpace) {
		odometer.setTheta(0);

		continuousPoller.clearData();
		continuousPoller.setEnable(true);

		navigator.turnClockWise(WheelDriver.SPEED_VERY_SLOW);
		process(false);
		
		navigator.stopRobot();
		continuousPoller.setEnable(false);
		continuousPoller.clearData();
		continuousPoller.setEnable(true);
		
		navigator.turnCounterClockWise(WheelDriver.SPEED_VERY_SLOW);
		process(true);
		
		continuousPoller.setEnable(false);
		continuousPoller.clearData();
		
		Sound.beep();
		
		double hundredEighty = left + (back - left) / 4;

		if (isFacingSpace) {
			odometer.setTheta(Math.toRadians(180) - hundredEighty + odometer.getTheta());
		} else {
			odometer.setTheta(Math.toRadians(90) - hundredEighty + odometer.getTheta());
		}
		navigator.setSpeed(WheelDriver.SPEED_SLOW);
		navigator.turnTo(Math.toRadians(0));
	}

	private void process(boolean isCounterClockWise) {
		while (true) {
			boolean quitting = false;
			ArrayList<Integer> results = continuousPoller.getResults();
			ArrayList<Double> angle = continuousPoller.getAngles();

			int count = 0;

			if (results.size() - count > 2) {
				for (int i = count; i < results.size() - 1; i++) {
					int current = results.get(i);
					int next = results.get(i + 1);

					if (isCounterClockWise) {
						if (current < THRESHOLD && next > THRESHOLD) {// Back wall
							back = angle.get(i);
							return;
						}
					} else {
						if (current < THRESHOLD && next > THRESHOLD) {// Left wall
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
	
	public static double debug, debug1, debug2;
}