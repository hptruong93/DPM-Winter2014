package testing;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RS485;
import lejos.nxt.remote.RemoteNXT;
import localizer.USLocalizerCombined;
import odometry.Odometer;
import odometry.WheelDriver;
import odometry.WheelDriverAdapter;
import utilities.Display;
import utilities.Hardware;

/**
 * TestUSLocalizer.java
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
 * Testing the US localizer. Receiver.java has to be run previously on the slave
 * brick. Once this starts, it will immediately try to connect to the slave
 * brick through RS485
 * 
 * Two beeps followed by two beeps --> Successfully connected 
 * Two beeps --> Error in connection
 * 
 * After it connects successfully, the robot will wait for user to press Enter
 * (the orange button). After that, the robot will do US sensor localization.
 * 
 * The robot will beep twice once finished localizing. In the end, the robot
 * should be at angle 0 (facing the x axis)
 * 
 * @author ptruon4
 * 
 */

public class TestUSLocalizer {

	private static Odometer odometer;
	private static WheelDriver wheelDriver;

	public static void main(String[] args) {
		initSlave();
		basicInit();
		
		Sound.twoBeeps();
		Sound.twoBeeps();
		while (Button.waitForAnyPress() != Button.ID_ENTER)
			;
		localize();

		Sound.twoBeeps();
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

	private static void localize() {
		WheelDriverAdapter adapter = new WheelDriverAdapter(wheelDriver);
		USLocalizerCombined lc = new USLocalizerCombined(odometer, adapter, new UltrasonicSensor(Hardware.leftUltraSonic),
				USLocalizerCombined.LocalizationType.RISING_EDGE);
		lc.doLocalization();
		wheelDriver.turnTo(0);
	}

	/**
	 * Init the slave ports. 1 is the master nxt & 2 is the slave nxt. See
	 * Hardware.init() for slave ports.
	 */
	private static void initSlave() {
		try {
			RemoteNXT nxt = new RemoteNXT("TEAM16-2", RS485.getConnector());
			Thread.sleep(1000);
			Hardware.init(nxt);
		} catch (IOException ex) {
			System.exit(1);
		} catch (InterruptedException e) {
			System.exit(1);
		}
	}
}