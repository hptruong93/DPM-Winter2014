package navigator;

import lejos.nxt.NXTRegulatedMotor;
import odometry.Odometer;
import odometry.WheelDriver;
import utilities.Hardware;
import data.DataProcessor;

/**
 * Navigator.java
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
 * The navigator is the main brain of the robot.
 * It will pass data processing information on to different components,
 * from the processed result, it will decide what the robot should do to adapt
 * to the environment.
 *
 */
public abstract class Navigator implements DataProcessor {
	protected NXTRegulatedMotor leftWheel, rightWheel;
	protected WheelDriver wheelDriver;
	protected Odometer odometer;
	protected CoordinateProvider coordinates;
	protected CoordinateManager manager;
	protected Navigator nextActivity;
	
	/**
	 * @param odometer the odometer for navigator to use
	 * @param driver the wheel driver that drives the robot as instructed by the navigator
	 * @param coordinates coordinates that the robot should reach.
	 */
	public Navigator(Odometer odometer, WheelDriver driver, CoordinateProvider coordinates, CoordinateManager manager, Navigator nextActivity) {
		this.leftWheel = Hardware.leftWheel;
		this.rightWheel = Hardware.rightWheel;
		this.odometer = odometer;
		this.wheelDriver = driver;
		this.coordinates = coordinates;
		this.manager = manager;
		this.nextActivity = nextActivity;
	}
	
	/**
	 * Describe what will happen when the navigator has finished its tasks.
	 */
	protected abstract void prepareNextDestination();
	
	/**
	 * Set the next activity for this navigator.
	 * This should only be used to set up circular reference if needed
	 * @param next nextActivity after this
	 */
	public void setNextActivity(Navigator next) {
		this.nextActivity = next;
	}
}
