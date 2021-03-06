package testing;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import odometry.Odometer;
import odometry.WheelDriver;
import utilities.Display;
import utilities.Hardware;
import utilities.Point;

/**
 * TestWheelDriver.java
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
 * Testing the WheelDriver.
 * 
 * Two beeps followed by two beeps --> Ready to start
 * 
 * After it starts successfully, the robot will wait for user to press Enter
 * (the orange button). After that, the robot will move to a point specified in testing() method.
 * 
 * Note: this does not include odometry correction or obstacle avoidance
 * 
 * @author ptruon4
 * 
 */

public class TestWheelDriver {

	private static Odometer odometer;
	private static WheelDriver wheelDriver;


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
		
		wheelDriver.setEnable(true);
		wheelDriver.setSpeed(WheelDriver.SPEED_SLOW);
		wheelDriver.stopRobot();
		wheelDriver.start();
	}

	private static void testing() {
		wheelDriver.travelTo(new Point(90,90));
		while (wheelDriver.isNavigating());
	}
}