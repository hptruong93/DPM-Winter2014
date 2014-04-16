package navigator;

import java.util.Stack;

import data.DataProvider;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import main.FinalProject;
import objectHandler.ObjectDetector;
import objectHandler.ObjectHandler;
import odometry.Odometer;
import odometry.WheelDriver;
import odometry.WheelDriver.DestinationInfo;
import pollerWithHandler.ClawTouchOfFaith;
import pollerWithHandler.ColorPoller;
import utilities.FieldInfo;
import utilities.Hardware;
import utilities.Point;
import utilities.Util;

/**
 * FlagFinder.java
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
 * This navigator will investigate multiple points to find the flag. The
 * navigator is an active navigator. It controls the sensors (instead of being
 * controlled by the sensors as a usual navigator). Therefore it does not need a coordinateProvider
 * or manager.
 * 
 * 
 */
public class FlagFinder extends Navigator {
	public static int FLAG_COLOR = ObjectDetector.BLUE;

	private RegulatedMotor sensorArm;
	private UltrasonicSensor left, right;
	private ColorPoller colorPoller;
	private ObjectDetector detector;
	private ObjectHandler foamGrabber;

	private Point entrance;
	private Point objectPosition;
	private ClawTouchOfFaith touch;

	/**
	 * Constructor that includes all needed tools for the FlagFinder
	 * @param odometer robot's odometer
	 * @param wheelDriver robot's driver
	 * @param detector Object detector that will detect the flag
	 * @param objectHandler Object handler that will grab the flag
	 * @param colorPoller color poller that will be used to detect the block
	 * @param leftUS left ultrasonic sensor
	 * @param rightUS right ultrasonic sensor
	 * @param sensorArm arm motor that controls the color sensor.
	 */
	public FlagFinder(Odometer odometer, WheelDriver wheelDriver, ObjectDetector detector, ObjectHandler objectHandler, ColorPoller colorPoller,
			UltrasonicSensor leftUS, UltrasonicSensor rightUS, RegulatedMotor sensorArm) {
		super(odometer, wheelDriver, null, null, null);

		this.sensorArm = sensorArm;

		this.left = leftUS;
		this.right = rightUS;

		try {
			leftUS.off();
			rightUS.off();
		} catch (Exception e) {
		}

		// Color poller must have detector set as dataprocessor
		this.colorPoller = colorPoller;
		this.detector = detector;
		this.foamGrabber = objectHandler;

		touch = new ClawTouchOfFaith(Hardware.arm);
	}

	/**
	 * Wrapper to adapt this structure with navigator
	 */
	@Override
	public void handleData(DataProvider data) {
		try {
			run();
		} catch (InterruptedException e) {
			Sound.twoBeeps();
			Sound.twoBeeps();
			Sound.twoBeeps();
			Sound.twoBeeps();
		}
	}

	/**
	 * The finder picks a random point in the zone. It then goes to that point, and
	 * during the navigation, if it sees any obstacle, it will test at -45, 0 and 45 degree
	 * to ensure that the obstacle is in front of it, and then start investigating the object using
	 * detector. If the obstacle is not the interested flag, it would grab the obstacle and bring it out of
	 * the zone, then return and repeat the process. Otherwise it will just bring the robot to the position
	 * where this method was first invoked.
	 * 
	 * @throws InterruptedException
	 */
	private void run() throws InterruptedException {
		wheelDriver.setStopThreshold(WheelDriver.SMALL_STOP_THRESHOLD);
		Stack<Point> places;
		DestinationInfo currentDest = null;

		entrance = odometer.getPosition();

		while (true) {
			places = generateVisitPath();
			while (!places.isEmpty()) {
				wheelDriver.setEnable(true);
				if (currentDest == null) {
					Point current = places.pop();
					wheelDriver.travelTo(current);
				} else {
					wheelDriver.retrieveDestination(currentDest);
					currentDest = null;
				}

				while (wheelDriver.isNavigating()) {
					wheelDriver.setEnable(true);

					int leftReading = ping(left);
					int rightReading = ping(right);

					if ((leftReading < ObjectDetector.RANGE || rightReading < ObjectDetector.RANGE)) {
						// && !odometer.isSeeingWall(leftReading)) {
						FinalProject.pollerLeft.setEnable(false);
						FinalProject.pollerRight.setEnable(false);
						wheelDriver.setEnable(false);

						double angle = odometer.getTheta();
						while (wheelDriver.isTurning())
							;
						wheelDriver.turnTo(angle);
						angle = odometer.getTheta();

						Thread.sleep(100);
						currentDest = wheelDriver.saveDestination();
						wheelDriver.stopRobot();

						if (leftReading < ObjectDetector.RANGE) {
							objectPosition = odometer.getPosition().getFrontPoint(angle, leftReading / Math.sqrt(2));
						} else if (rightReading < ObjectDetector.RANGE) {
							objectPosition = odometer.getPosition().getFrontPoint(angle, rightReading / Math.sqrt(2));
						}

						wheelDriver.turnTo(objectPosition);
						wheelDriver.backward(2);

						Hardware.hand.rotate(-90);
						int faith = testOfFaith();
						Hardware.hand.rotate(90);

						if (faith >= 0) {
							if (faith != 0) {
								wheelDriver.forcedForward(4);
							} else {
								wheelDriver.forcedForward(4);
							}

							if (processObject()) {
								wheelDriver.setStopThreshold(WheelDriver.LARGE_STOP_THRESHOLD);
								FinalProject.pollerLeft.setEnable(true);
								FinalProject.pollerRight.setEnable(true);
								return;
							}
						} else {

						}

						wheelDriver.setEnable(true);
						FinalProject.pollerLeft.setEnable(true);
						FinalProject.pollerRight.setEnable(true);
					}
				}
			}
		}
	}

	/**
	 * Process the object in front to see what color it is. If the object is not the interested flag,
	 * it will bring the object to the position where the robot started the search and dispose it there. 
	 * @return if the interested flag was captured
	 * @throws InterruptedException
	 */
	private boolean processObject() throws InterruptedException {
		final int[] rotation = new int[] { -25, -23, -22 };
		int detected = ObjectDetector.UNKNOWN;
		int sum = 0, index = 0;

		while (index < rotation.length) {
			sensorArm.rotate(rotation[index]);
			sum -= rotation[index];
			detected = detect();
			if (detected != ObjectDetector.WHITE && detected != ObjectDetector.UNKNOWN) {
				break;
			}

			index++;
		}
		sensorArm.rotate(sum);

		wheelDriver.forcedForward(8);
		wheelDriver.backward(3);
		while (wheelDriver.isNavigating())
			;

		handleObject();
		if (detected != FLAG_COLOR) {
			disposeObject();
		}

		return detected == FLAG_COLOR;
	}

	/**
	 * Get the object color using the color detector
	 * @return the color of the object identified by the detector
	 * @throws InterruptedException
	 */
	private int detect() throws InterruptedException {
		colorPoller.setEnable(true);
		while (true) {
			int type = detector.getType();

			if (type != ObjectDetector.DETECTING) {
				colorPoller.setEnable(false);
				detector.resetType();
				return type;
			}

			Thread.sleep(50);
		}
	}

	/**
	 * Capture the object in front using the ObjectHandler
	 * @throws InterruptedException
	 */
	private void handleObject() throws InterruptedException {
		foamGrabber.handleObject();

		while (!foamGrabber.finishCurrentTask()) {
			Thread.sleep(100);
		}
	}

	/**
	 * Generate the path that the robot will take to search for flag.
	 * Currently using a random path, so this only return a random point.
	 * @see run() method description for the full search algorithm
	 * @return the generated path that the robot should go to in order to search for the flag
	 */
	private Stack<Point> generateVisitPath() {
		Stack<Point> output = new Stack<Point>();

		while (true) {
			Point random = new Point(Util.randBetween(FieldInfo.LOWER_LEFT.getX(), FieldInfo.UPPER_RIGHT.getX()), Util.randBetween(FieldInfo.LOWER_LEFT.getY(),
					FieldInfo.UPPER_RIGHT.getY()));
			if (FieldInfo.isInside(random)) {
				output.addElement(random);
				break;
			}
		}
		return output;
	}

	/**
	 * Bring the currently captured object to the entrance point (where the robot was when run() was invoked)
	 * and dispose it there.
	 */
	private void disposeObject() {
		Point current = odometer.getPosition();
		double currentAngle = odometer.getTheta();

		wheelDriver.backward(5);
		wheelDriver.turnTo(entrance);
		wheelDriver.forcedForward(current.distance(entrance));
		foamGrabber.releaseObject();
		wheelDriver.turnTo(current);
		wheelDriver.forcedForward(Math.max(odometer.getPosition().distance(current) - 3, 0));
		wheelDriver.turnTo(currentAngle);
	}

	/**
	 * Turn to -45, 0, and 45 degree and use the claw to perform TestOfFaith to see if a
	 * flag is there.
	 * @return the index of the rotation array at which the TestOfFaith return false 
	 */
	private int testOfFaith() {
		final int[] rotation = new int[] { 0, -40, 80 };

		for (int i = 0; i < rotation.length; i++) {
			wheelDriver.turn(Math.toRadians(rotation[i]));
			if (!touch.testOfFaith()) {
				Sound.beep();
				return i;
			} else {
				Sound.twoBeeps();
			}
		}

		return -1;
	}

	/**
	 * Provide a safe way to ping the us sensor
	 * @param us the us sensor that is going to ping
	 * @return the value read by the us sensor
	 * @throws InterruptedException
	 */
	private int ping(UltrasonicSensor us) throws InterruptedException {
		try {
			us.ping();
			Thread.sleep(100);
			return us.getDistance();
		} catch (Exception e) {
			Sound.beep();
			Sound.beep();
			Sound.beep();
			return 255;
		}
	}

	@Override
	protected void prepareNextDestination() {
		// There is no next destination
	}
}