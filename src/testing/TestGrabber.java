package testing;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.RS485;
import lejos.nxt.remote.RemoteNXT;
import objectHandler.ObjectGrabber;
import odometry.Odometer;
import odometry.WheelDriver;
import pollerWithHandler.ColorPollerWithSide;
import utilities.Display;
import utilities.Hardware;

/**
 * TestGrabber.java
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
 * Testing the grabber. Receiver.java has to be run previously on the slave
 * brick. Once this starts, it will immediately try to connect to the slave
 * brick through RS485
 * 
 * Two beeps followed by two beeps --> Successfully connected Two beeps -->
 * Error in connection
 * 
 * After it connects successfully, the robot will wait for user to press Enter
 * (the orange button). After that, the robot will attempt to grab the object
 * and bring it up, and then move the hand back.
 * 
 * @author ptruon4
 * 
 */
public class TestGrabber {

	private static Odometer odometer;
	private static WheelDriver wheelDriver;
	public static ColorPollerWithSide pollerLeft, pollerRight;

	public static void main(String[] args) {
		initSlave();
		basicInit();

		Sound.twoBeeps();
		Sound.twoBeeps();

		testing();

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;

		System.exit(0);
	}

	private static void testing() {
		ObjectGrabber grabber = new ObjectGrabber(Hardware.arm, Hardware.hand, wheelDriver);
		while (true) {
			if (Button.waitForAnyPress() == Button.ID_LEFT) {
				grabber.handleObject();
			} else if (Button.waitForAnyPress() == Button.ID_RIGHT) {
				grabber.releaseObject();
			}
		}
	}

	private static void basicInit() {
		odometer = new Odometer(Hardware.leftWheel, Hardware.rightWheel);
		Display mainDisplay = new Display(odometer);
		wheelDriver = new WheelDriver(odometer, Hardware.leftWheel, Hardware.rightWheel);

		mainDisplay.start();
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