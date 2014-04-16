package localizer;

import java.util.ArrayList;

import odometry.Odometer;
import odometry.WheelDriver;
import pollerContinuous.ContinuousPoller;

/**
 * Localizer.java
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

public abstract class Localizer {

	protected static final int THRESHOLD = 90;
	protected Odometer odometer;
	protected WheelDriver navigator;
	protected ContinuousPoller continuousPoller;
	
	public Localizer(Odometer odometer, WheelDriver navigator, ContinuousPoller continuousPoller) {
		this.odometer = odometer;
		this.navigator = navigator;
		this.continuousPoller = continuousPoller;
	}
	
	/**
	 * The robot will identify its orientation and position (depends on type of localizer), then
	 * it will turn to 0 degree and run to (0,0) if position can be determined.
	 */
	public abstract void doLocalization();
	
	/**
	 * 
	 * @param results data of the ultrasonic sensor
	 * @return if the robot is facing the wall
	 */
	protected final boolean isFacingSpace(ArrayList<Integer> results) {
		if (results.size() == 0) {
			return false;
		} else {
			if (results.get(0) < THRESHOLD) {
				return false;
			}
		}
		return true;
	}
}
