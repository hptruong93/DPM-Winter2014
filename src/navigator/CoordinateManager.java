package navigator;

import java.util.ArrayList;

import lejos.nxt.Sound;
import navigator.pathPlanner.RRT;
import utilities.Display;
import utilities.Point;
import data.DataProcessor;
import data.DataProvider;
import data.DualUSData;
import data.LocalizingData;
import data.LocalizingData.LocalizePackage;

/**
 * CoordinateManager.java
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
 * Manage the navigation coordinates of the robot. The manager will handle data
 * related to obstacles coordinates and decide where the robot should move next.
 * The CoordinateProvider is just an information holder of where the robot
 * should move.
 * 
 * 
 */
public class CoordinateManager implements DataProcessor {

	public static final int DANGER_ZONE_RADIUS = 25;

	private RRT pathPlanner;
	private CoordinateProvider coordinates;
	private boolean newObjectIdentified;

	public CoordinateManager(CoordinateProvider coordinates) {
		pathPlanner = new RRT();
		this.coordinates = coordinates;
		newObjectIdentified = false;
	}

	public CoordinateManager(CoordinateProvider coordinates, Point[] prohibitedZone) {
		pathPlanner = new RRT();

		for (Point p : prohibitedZone) {
			pathPlanner.addObstacle(p);
		}

		this.coordinates = coordinates;
		newObjectIdentified = false;
	}

	/**
	 * This is invoked when the robot identifies new obstacle in front. The
	 * manager will update RRT obstacle and create a new path for the robot.
	 */
	@Override
	public void handleData(DataProvider data) {
		try {
			LocalizePackage values = ((LocalizingData) data).getData();
			int distance = values.getDistance();
			double angle = values.getAngle();
			Point sensorPosition = values.getSensorPosition();

			Point objectPosition;
			if (values.getSide() == DualUSData.LEFT_SIDE) {
				objectPosition = sensorPosition.getFrontPoint(angle - Math.toRadians(30), distance);
			} else if (values.getSide() == DualUSData.RIGHT_SIDE) {
				objectPosition = sensorPosition.getFrontPoint(angle + Math.toRadians(30), distance);
			} else {
				throw new IllegalStateException("What sensor is this?");
			}

			if (addObject(objectPosition)) {
				ArrayList<Point> finalDestination = coordinates.finalDestinations();
				coordinates.clearDestinations(null);

				Sound.twoBeeps();
				ArrayList<Point> newPath = pathPlanner.planPath(values.getRobotPosition().getFrontPoint(angle, -20), finalDestination);
				if (newPath == null) {
					System.exit(1);
				}
				Sound.twoBeeps();

				for (int i = 0; i < newPath.size(); i++) {
					coordinates.addCoordinate(newPath.get(i));
				}

				newObjectIdentified = true;
			}
		} catch (Exception e) {
			Display.printDebug("CM " + e.getClass(), 7);
			Sound.beepSequence();
		}
	}

	/**
	 * If the handler is called, it will try to add the obstacle to the list. If
	 * adding succeed (see method addObject), this method would have to return
	 * true afterwards. The method would also reset newObjectIdentified to false
	 * once queried.
	 * 
	 * @return if the manager detects a new object.
	 */
	protected boolean newObjectIdentified() {
		boolean output = newObjectIdentified;
		newObjectIdentified = false;
		return output;
	}

	/**
	 * Filter the list of possible destination and find out the valid and closest possible destination
	 * for the robot
	 * @param robotPosition current position of the robot
	 * @return the valid and closest possible destination 
	 */
	protected Point getClearDestination(Point robotPosition) {
		ArrayList<Point> possibleDest = coordinates.finalDestinations();
		double min = Double.MAX_VALUE;
		Point output = null;

		for (Point p : possibleDest) {
			if (pathPlanner.validPoint(p)) {
				if (robotPosition.distance(p) < min) {
					output = p;
					min = robotPosition.distance(p);
				}
			}
		}
		return output;
	}

	/**
	 * Add object to the list of known objects.
	 * 
	 * @param position
	 *            position of the object considering.
	 * @return if an object is a known object
	 */
	private boolean addObject(Point position) {
		return pathPlanner.addObstacle(position);
	}
}