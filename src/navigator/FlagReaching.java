package navigator;

import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import main.FinalProject;
import objectHandler.ObjectDetector;
import objectHandler.ObjectGrabber;
import odometry.Odometer;
import odometry.WheelDriver;
import odometry.WheelDriver.DestinationInfo;
import pollerWithHandler.ColorPoller;
import pollerWithHandler.DualUSPoller;
import utilities.Display;
import utilities.FieldInfo;
import utilities.Hardware;
import utilities.Point;
import data.DataProcessor;
import data.DataProvider;
import data.DualUSData;
import data.DualUSData.DualUSPackage;
import data.LocalizingData;

/**
 * FlagReaching.java
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
 * This navigator will investigate multiple points to find the foam and avoid
 * the obstacles.
 * 
 * 
 */
public class FlagReaching extends Navigator implements DataProcessor {

	private static final int OBSTACLE_DETECTION_RANGE = 20;
	private DualUSPoller usPoller;
	private boolean flagCaptured;
	private ObjectGrabber grabber;
	
	public FlagReaching(Odometer odometer, WheelDriver wheelDriver, CoordinateProvider coordinates, CoordinateManager manager, Navigator nextActivity) {
		super(odometer, wheelDriver, coordinates, manager, nextActivity);
		this.manager = new CoordinateManager(coordinates);
		// Remember to set usPoller after since pollers have to have reference
		// to this first. Circular reference!
	}

	/**
	 * The robot will go to the destinations and avoid obstacles on the way.
	 * If it has no more destination and has not captured the flag, it will initialize a FlagFinder to find the flag.
	 * If it has no more destinations and has captured the flag, it will attempt at dropping the flag at the current position.
	 * The program shall terminate after that point.
	 */
	@Override
	public void handleData(DataProvider data) {
		try {
			DualUSPackage usData = ((DualUSData) data).getData();
			int distance = usData.getReading();

			if (!wheelDriver.isNavigating()) {
				Point next = coordinates.removeFirst();
				if (next != null) {
					wheelDriver.travelTo(next);
				} else if (!flagCaptured) {
					shutDown();
					
					grabber = new ObjectGrabber(Hardware.arm, Hardware.hand, wheelDriver);
					ObjectDetector detector = new ObjectDetector();
					ColorPoller front = new ColorPoller(detector, new ColorSensor(Hardware.colorPortFront));
					front.setEnable(false);
					front.start();
					
					FlagFinder finder = new FlagFinder(odometer, wheelDriver, detector, grabber, front, FinalProject.leftUS, FinalProject.rightUS, Hardware.frontSensorMotor);
					finder.handleData(null);
					
					flagCaptured = true;
					
					Point clearCorner = manager.getClearDestination(odometer.getPosition());
					wheelDriver.turnTo(clearCorner);
					
					wheelDriver.forcedForward(odometer.getPosition().distance(clearCorner));
					prepareNextDestination();
				} else {
					wheelDriver.turnTo(FieldInfo.DROP_OFF_CORNER);
					wheelDriver.turn(Math.PI);
					wheelDriver.backward(odometer.getPosition().distance(FieldInfo.DROP_OFF_CORNER));
					grabber.releaseObject();
					System.exit(0);
				}
			} else {
				if (distance < OBSTACLE_DETECTION_RANGE && !odometer.isSeeingWall(distance)) {
					DestinationInfo currentDest = wheelDriver.saveDestination();
					
					wheelDriver.stopRobot();
					manager.handleData(new LocalizingData(usData, odometer.getUSSensorPosition(usData.getSide()), odometer.getPosition(), odometer.getTheta()));
					wheelDriver.setEnable(true);
					
					if (manager.newObjectIdentified()) {
						wheelDriver.backward(20);
						Point next = coordinates.removeFirst();
						if (next != null) {
							wheelDriver.travelTo(next);
						} else {
							wheelDriver.stopRobot();
						}
					} else {
						wheelDriver.retrieveDestination(currentDest);
					}
				}
			}
		} catch (Exception e) {
			Display.printDebug("FR " + e.toString(), 7);
			Sound.beepSequence();
			return;
		}
	}

	/**
	 * Before calling this method, client has to shut down the search navigator
	 * by calling shutdown(). This method would set the robot destination to the
	 * final destination and activate an instance of FinalDestinationNavigator.
	 * All activity of current SearchNavigator must be suspended and must have
	 * stopped at the end of the method.
	 */
	@Override
	protected void prepareNextDestination() {
		coordinates.clearDestinations(FieldInfo.DROP_OFF_ZONE);
		usPoller.setEnable(true);
	}

	/**
	 * Stop all activities of the Navigator. Disabling all sensors.
	 */
	private void shutDown() {
		this.usPoller.setEnable(false);
		wheelDriver.stopRobot();
	}

	/**
	 * Set the us poller that will control the navigator
	 * @param poller us poller that will control this navigator
	 */
	public void setUS(DualUSPoller poller) {
		this.usPoller = poller;
	}
}