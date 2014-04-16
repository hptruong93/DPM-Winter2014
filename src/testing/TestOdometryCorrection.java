package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import odometry.Odometer;
import odometry.OdometryCorrection;
import odometry.WheelDriver;
import pollerWithHandler.ColorPollerWithSide;
import utilities.Display;
import utilities.Hardware;
import utilities.Point;

/**
 * TestOdometryCorrection.java
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
 * Testing the Odometry correction.
 * 
 * Two beeps followed by two beeps --> Ready to start
 * 
 * After it starts successfully, the robot will wait for user to press Enter
 * (the orange button). After that, the robot will do a test of odometry correction.
 * 
 * The robot will move to a point specified in testing() method, and move back to (0,0)
 * During the moving process, odometry correction will happen. If the odometry correction performs
 * well, the robot should return to (0,0)
 * 
 * @author ptruon4
 * 
 */

public class TestOdometryCorrection {

	private static Odometer odometer;
	private static WheelDriver wheelDriver;
	private static ColorSensor leftColor;
	private static ColorSensor rightColor;


	public static void main(String[] args) {
		basicInit();

		Sound.twoBeeps();
		Sound.twoBeeps();
		while (Button.waitForAnyPress() != Button.ID_ENTER)
			;
		 testing();

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;

		System.exit(0);
	}

	private static void basicInit() {
		odometer = new Odometer(Hardware.leftWheel, Hardware.rightWheel);
		Display mainDisplay = new Display(odometer);
		wheelDriver = new WheelDriver(odometer, Hardware.leftWheel, Hardware.rightWheel);

		odometer.start();
		mainDisplay.start();

		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
		leftColor = new ColorSensor(Hardware.colorPortLeft);
		rightColor = new ColorSensor(Hardware.colorPortRight);
		ColorPollerWithSide pollerLeft = new ColorPollerWithSide(odometryCorrection, leftColor, ColorPollerWithSide.LEFT_SIDE);
		ColorPollerWithSide pollerRight = new ColorPollerWithSide(odometryCorrection, rightColor, ColorPollerWithSide.RIGHT_SIDE);

		pollerLeft.setEnable(true);
		pollerRight.setEnable(true);
		pollerLeft.start();
		pollerRight.start();

		wheelDriver.setEnable(true);
		wheelDriver.setSpeed(WheelDriver.SPEED_SLOW);
		wheelDriver.stopRobot();
		wheelDriver.start();
	}

	private static void testing() {
		wheelDriver.travelTo(new Point(30,30));
		while (wheelDriver.isNavigating());
		wheelDriver.travelTo(new Point(0,0));
	}
}