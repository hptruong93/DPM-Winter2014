package testing;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import localizer.LightLocalizer;
import odometry.Odometer;
import odometry.WheelDriver;
import pollerContinuous.ContinuousColorPoller;
import utilities.Display;
import utilities.Hardware;

/**
 * TestLightLocalization.java
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
 * Testing the light localization.
 * 
 * Two beeps followed by two beeps --> The robot is ready to start
 * 
 * After it starts successfully, the robot will wait for user to press Enter
 * (the orange button). After that, the robot will do light sensor localization.
 * 
 * The robot will beep twice once finished localizing. In the end, the robot
 * should be at (0,0) facing angle 0 (along the x axis in the increasing direction) 
 * 
 * @author ptruon4
 * 
 */
public class TestLightLocalization {

	private static Odometer odometer;
	private static WheelDriver wheelDriver;
	private static ColorSensor leftColor;

	public static void main(String[] args) {
		basicInit();
		
		Sound.twoBeeps();
		Sound.twoBeeps();
		while (Button.waitForAnyPress() != Button.ID_ENTER)
			;
		localize();
		
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
		
		leftColor = new ColorSensor(Hardware.colorPortLeft);
		
		wheelDriver.setEnable(true);
		wheelDriver.setSpeed(WheelDriver.SPEED_SLOW);
		wheelDriver.stopRobot();
		wheelDriver.start();
	}

	private static void localize() {
		ContinuousColorPoller poller = new ContinuousColorPoller(leftColor, odometer);
		LightLocalizer ll= new LightLocalizer(odometer, wheelDriver, poller);
		ll.doLocalization();
	}
}