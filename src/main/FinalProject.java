package main;

import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RS485;
import lejos.nxt.remote.RemoteNXT;
import localizer.LightLocalizer;
import localizer.USLocalizerCombined;
import navigator.CoordinateManager;
import navigator.CoordinateProvider;
import navigator.FlagReaching;
import odometry.Odometer;
import odometry.OdometryCorrection;
import odometry.WheelDriver;
import odometry.WheelDriverAdapter;
import pollerContinuous.ContinuousColorPoller;
import pollerWithHandler.ColorPollerWithSide;
import pollerWithHandler.DualUSPoller;
import utilities.Display;
import utilities.FieldInfo;
import utilities.Hardware;
import utilities.Point;
import utilities.Util;
import bluetooth.BlueToothAgent;
import data.DualColorData;

/**
 * FinalProject.java
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
 * Main class to start the system.
 *
 */
public class FinalProject {

	private static Odometer odometer;
	private static WheelDriver wheelDriver;
	private static ColorSensor leftColor, rightColor;
	public static ColorPollerWithSide pollerLeft, pollerRight;
	public static UltrasonicSensor leftUS, rightUS;
	
	/**
	 * Entry to start the system
	 * Several components need to be started in order
	 * 1) Slave
	 * 2) Bluetooh (may not need in some tests)
	 * 3) Basic init @see basicInit()
	 * 4) Localization (may not need in some tests) @see localize()
	 * 5) Odometer transformation @see FieldInfo.odometerTransformation()
	 * 6) Odometry correction (may not need in some tests) @see odometryCorrectionInit()
	 * 7) Searching algorithm (may not need in some tests) @see startSearch()
	 * @param args not needed as this would be initialized by nxt OS.
	 */
	public static void main(String[] args) {
		initSlave();
		Sound.twoBeeps();

		BlueToothAgent.init();
		basicInit();

		Sound.twoBeeps();
		Sound.twoBeeps();

		localize();
		FieldInfo.odometerTransformation(odometer, FieldInfo.STARTING_CORNER);
		odometryCorrectionInit();
		startSearch();

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}

	/**
	 * Initialize the following components in order:
	 * 1) Odometer
	 * 2) Debug display (can be removed if not needed)
	 * 3) WheelDriver
	 * 
	 * Also start these components of the system
	 */
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

	/**
	 * Initialize the odometry correction component of the system.
	 * This involves instantiating two ColorPollerWithSide and start them
	 */
	private static void odometryCorrectionInit() {
		OdometryCorrection correction = new OdometryCorrection(odometer);
		if (leftColor == null) {
			leftColor = new ColorSensor(Hardware.colorPortLeft);
		}
		rightColor = new ColorSensor(Hardware.colorPortRight);

		pollerLeft = new ColorPollerWithSide(correction, leftColor, DualColorData.LEFT_SIDE);
		pollerRight = new ColorPollerWithSide(correction, rightColor, DualColorData.RIGHT_SIDE);

		pollerLeft.setEnable(true);
		pollerRight.setEnable(true);
		pollerLeft.start();
		pollerRight.start();
	}

	/**
	 * Do US localization and then navigate blindly to an appropriate position for light localization.
	 * Light localization follows and bring the robot to (0,0), and facing 0 degree
	 */
	private static void localize() {
		WheelDriverAdapter adapter = new WheelDriverAdapter(wheelDriver);
		USLocalizerCombined lc = new USLocalizerCombined(odometer, adapter, new UltrasonicSensor(Hardware.leftUltraSonic),
				USLocalizerCombined.LocalizationType.FALLING_EDGE);
		lc.doLocalization();

		wheelDriver.turnTo(Math.toRadians(45 + 180));
		wheelDriver.backward(15);
		wheelDriver.turnTo(Math.PI - (Math.PI / 8));

		leftColor = new ColorSensor(Hardware.colorPortLeft);
		ContinuousColorPoller poller = new ContinuousColorPoller(leftColor, odometer);
		LightLocalizer ll = new LightLocalizer(odometer, wheelDriver, poller);
		ll.doLocalization();
	}

	/**
	 * Start flag search. This involves these components in order:
	 * 1) CoordinateProvider
	 * 2) CoordinateManager
	 * 3) FlagReaching
	 * 4) DualUSPoller
	 * 
	 * Note that the FlagFinder will be lazily instantiated by the FlagReaching instance
	 */
	private static void startSearch() {
		leftUS = new UltrasonicSensor(Hardware.leftUltraSonic);
		rightUS = new UltrasonicSensor(Hardware.rightUltraSonic);

		wheelDriver.setStopThreshold(WheelDriver.LARGE_STOP_THRESHOLD);
		wheelDriver.setSpeed(WheelDriver.SPEED_NORMAL);

		ArrayList<Point> possibleDestinations = FieldInfo.findZoneCorner(FieldInfo.LOWER_LEFT, FieldInfo.UPPER_RIGHT);
		Point[] places = { Util.closest(odometer.getPosition(), possibleDestinations) };

		CoordinateProvider coordinate = new CoordinateProvider(places, possibleDestinations);
		CoordinateManager manager = new CoordinateManager(coordinate, new Point[] { FieldInfo.PROHIBITED_ZONE });

		FlagReaching flagReacher = new FlagReaching(odometer, wheelDriver, coordinate, manager, null);

		DualUSPoller fixedFront = new DualUSPoller(flagReacher, new UltrasonicSensor[] { leftUS, rightUS });

		flagReacher.setUS(fixedFront);

		fixedFront.setEnable(true);
		fixedFront.start();
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