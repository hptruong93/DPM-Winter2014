package testing;

import java.io.IOException;

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
import utilities.Hardware;
import utilities.Point;

/**
 * IntegrationTest.java
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
 * Integration testing of everything.
 * Receiver.java has to be run previously on the slave brick.
 * 
 * ** Stage 1:
 * Once this starts, it will immediately try to connect to the slave brick through RS485
 * 
 * Two beeps followed by two beeps --> Successfully connected
 * Two beeps --> Error in connection
 * 
 * ** Stage 2:
 * After it connects successfully, the robot will wait for user to press Enter (the orange button).
 * After that, the robot will do us sensor localization, and light localization.
 * At the end of localization, the robot should be at (0,0) and have angle 0 (facing the x axis in the
 * increasing direction).
 * 
 * Then, the robot will wait for user to press Enter (the orange button).
 * 
 * ** Stage 3:
 * After user pressed Enter, the robot will attempt to move to the points specified in array of Point named
 * places in startSearch() method. These points must not contain obstacles (but there can be obstacle on the way).
 * 
 * @author ptruon4
 *
 */

public class IntegrationTest {

	private static Odometer odometer;
	private static WheelDriver wheelDriver;
	private static ColorSensor leftColor;
	private static ColorSensor rightColor;
	
	public static double t = 30.48;
	
	public static Point p1 = new Point(7 * t,-1 * t);
	public static Point p2 = new Point(5 * t, t);
	

	
	public static void main(String[] args) {
		//Start of stage 1
		initSlave();
		//End of stage 1
		
		Sound.twoBeeps();
		Sound.twoBeeps();
		
		//Start of stage 2
//		while (Button.waitForAnyPress() != Button.ID_ENTER)
//			;

		//		blueToothInit(); //This can be added later on but it's not working so I would not test it
		basicInit();
		localize();

		odometryCorrectionInit();
		startSearch();
		
		
		
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;

		System.exit(0);
	}

//	private static void blueToothInit() {
//		
//	}
	
	private static void basicInit() {
		odometer = new Odometer(Hardware.leftWheel, Hardware.rightWheel);
		Display mainDisplay = new Display(odometer);
		wheelDriver = new WheelDriver(odometer, Hardware.leftWheel, Hardware.rightWheel);

		odometer.start();
		mainDisplay.start();

		wheelDriver.setEnable(true);
		wheelDriver.setSpeed(WheelDriver.SPEED_NORMAL);
		wheelDriver.stopRobot();
		wheelDriver.start();
	}

	private static void localize() {
		 WheelDriverAdapter adapter = new WheelDriverAdapter(wheelDriver);
		 USLocalizerCombined lc = new USLocalizerCombined(odometer, adapter,
		 new UltrasonicSensor(Hardware.leftUltraSonic),
		 USLocalizerCombined.LocalizationType.FALLING_EDGE);
		 lc.doLocalization();
		 
		 wheelDriver.turnTo(Math.toRadians(180+45));
 		 wheelDriver.backward(15);
 		 while(wheelDriver.isNavigating());
		 wheelDriver.turnTo(Math.PI);
 		 
		 leftColor = new ColorSensor(Hardware.colorPortLeft); 
		 ContinuousColorPoller poller = new ContinuousColorPoller(leftColor, odometer);
		 LightLocalizer ll= new LightLocalizer(odometer, wheelDriver, poller);
		 ll.doLocalization();
	}

	private static void odometryCorrectionInit() {
		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
//		leftColor = new ColorSensor(Hardware.colorPortLeft); //No need. This should have been done previously by the localizer
		rightColor = new ColorSensor(Hardware.colorPortRight);
		ColorPollerWithSide pollerLeft = new ColorPollerWithSide(odometryCorrection, leftColor, ColorPollerWithSide.LEFT_SIDE);
		ColorPollerWithSide pollerRight = new ColorPollerWithSide(odometryCorrection, rightColor, ColorPollerWithSide.RIGHT_SIDE);

		pollerLeft.setEnable(true);
		pollerRight.setEnable(true);
		pollerLeft.start();
		pollerRight.start();
	}
	
	private static void startSearch() {
		Point[] places = { findZonePoint(p1, p2) };
		wheelDriver.setSpeed(WheelDriver.SPEED_NORMAL);
//		double t = 30.48;
		CoordinateProvider coordinate = new CoordinateProvider(places);
		CoordinateManager manager = new CoordinateManager(coordinate);

		FlagReaching reachingFlag = new FlagReaching(odometer, wheelDriver, coordinate, manager, null);

		DualUSPoller fixedFront = new DualUSPoller(reachingFlag, new UltrasonicSensor[] { new UltrasonicSensor(Hardware.leftUltraSonic),
				new UltrasonicSensor(Hardware.rightUltraSonic) });

		reachingFlag.setUS(fixedFront);

		fixedFront.setEnable(true);
		fixedFront.start();
	}

	private static Point findZonePoint(Point p1, Point p2) {
		Point[] points = new Point[4];
		
		double[] signs1 = {2 * t, 0 * t, -2 * t, 0 * t};
		double[] signs2 = {0 * t, -2 * t, 0 * t, 2 * t};
		
		double dist = 10000;
		int index = 0;
		
		for (int i = 0; i < 4; i++) {
			double midx = (p1.getX() + p2.getX()) / 2 + signs1[i];   
			double midy = (p1.getY() + p2.getY()) / 2 + signs2[i]; 
			
			points[i] = new Point(midx, midy);
		}
		
		for (int i = 0; i < 4; i++) {
			double temp = points[i].distance(new Point(0,0));
			if (temp < dist) {
				dist = temp;
				index = i;
			}
		}
		
		
		return points[index];
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