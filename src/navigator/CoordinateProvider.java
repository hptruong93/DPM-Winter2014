package navigator;

import java.util.ArrayList;
import java.util.Iterator;

import lejos.nxt.Sound;
import utilities.Display;
import utilities.Hardware;
import utilities.Point;
import utilities.Util;

/**
 * CoordinateProvider.java
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
 * Coordinate holder: containing information about where the robot should
 * travel. This container is managed (updated) by Coodinate Manager, and queried
 * (read) by the Navigator
 * 
 * 
 */
public class CoordinateProvider {

	private ArrayList<Point> visited;
	private Point lastObservingPoint;
	private ArrayList<Point> observingPoints;
	private ArrayList<Point> investigatingPoints;
	private ArrayList<Point> possibleDestinations;
	private int initialCount;
	private boolean isInvestigating;

	public CoordinateProvider(Point[] data) {
		observingPoints = new ArrayList<Point>();
		investigatingPoints = new ArrayList<Point>();
		visited = new ArrayList<Point>();
		for (Point aPoint : data) {
			observingPoints.add(aPoint);
		}
		initialCount = observingPoints.size();
		lastObservingPoint = new Point(0, 0);
		possibleDestinations = null;
	}

	public CoordinateProvider(Point[] data,
			ArrayList<Point> possibleDestinations) {
		observingPoints = new ArrayList<Point>();
		investigatingPoints = new ArrayList<Point>();
		visited = new ArrayList<Point>();
		for (Point aPoint : data) {
			observingPoints.add(aPoint);
		}
		initialCount = observingPoints.size();
		lastObservingPoint = new Point(0, 0);

		this.possibleDestinations = new ArrayList<Point>();
		for (int i = 0; i < possibleDestinations.size(); i++) {
			this.possibleDestinations.add(possibleDestinations.get(i));
		}
	}

	/**
	 * Add a point where the robot should come and investigate
	 * 
	 * @param newComer
	 *            the point where the robot suspects there is an object
	 * @return if adding succeed
	 */
	protected boolean addInvestigatingPoints(Point newComer) {
		return investigatingPoints.add(newComer);
	}

	/**
	 * Add a new observing point for the robot. The new point must not be
	 * visited before by the robot, and must not be in one of the observing
	 * points that the robot is having. Otherwise the robot disregard the new
	 * point.
	 * 
	 * @param newComer
	 *            the new observing point
	 * @return if adding succeed
	 */
	protected boolean addCoordinate(Point newComer) {
		for (Point oldDest : visited) {
			if (oldDest.theSame(newComer)) {
				return false;
			}
		}

		for (Point current : observingPoints) {
			if (current.theSame(newComer)) {
				return false;
			}
		}

		lastObservingPoint = newComer;
		observingPoints.add(newComer);
		return true;
	}

	/**
	 * Add a point where the robot has visited. This point has to be grid lines
	 * intersection (or at least close).
	 * 
	 * @param visit
	 *            the point where the robot has visited
	 */
	protected void addVisited(Point visit) {
		Point adding = Point.getGridClosest(visit);
		if (!visited.contains(visit)) {
			visited.add(adding);
		}
	}

	/**
	 * Retrieve but does not remove a traveling point from the stack.
	 * 
	 * @return the point retrieved. This method does not check if the stack is
	 *         empty. It is the client's responsibility to do so.
	 */
	protected Point nextCoordinate() {
		return observingPoints.get(observingPoints.size() - 1);
	}

	/**
	 * Get the final destination stored in the stack, but does not remove it
	 * 
	 * @return the last item in observingPoints stack
	 */
	protected ArrayList<Point> finalDestinations() {
		ArrayList<Point> output = new ArrayList<Point>();

		if (possibleDestinations == null) {
			if (!observingPoints.isEmpty()) {
				output.add(observingPoints.get(0));
			} else {
				output.add(lastObservingPoint);
			}
		} else {
			return possibleDestinations;
		}
		return output;
	}

	/**
	 * Remove a point from the traveling stack points. The investigating points
	 * will have higher priority than the observing points, and therefore if
	 * there is any investigating point it will be removed first
	 * 
	 * @return The point removed
	 */
	protected Point removeFirst() {
		if (observingPoints.isEmpty()) {
			return null;
		} else {
			if (!investigatingPoints.isEmpty()) {
				Sound.twoBeeps();
				isInvestigating = true;
				return investigatingPoints
						.remove(investigatingPoints.size() - 1);
			} else {
				isInvestigating = false;
				lastObservingPoint = observingPoints
						.get(observingPoints.size() - 1);
				return observingPoints.remove(observingPoints.size() - 1);
			}
		}
	}

	/**
	 * Clear all investigating points and observing points. Add a single
	 * finalDestination point for the robot to navigate to. If the
	 * finalDestination is null, no point will be added.
	 * 
	 * @param finalDestination
	 *            the destination of the final coordinate that the robot must
	 *            travel to.
	 */
	protected void clearDestinations(Point finalDestination) {
		this.isInvestigating = false;
		this.observingPoints.clear();
		this.investigatingPoints.clear();

		if (finalDestination != null) {
			this.addCoordinate(finalDestination);
			possibleDestinations.clear();
			possibleDestinations.add(finalDestination);
		}
	}

	/**
	 * 
	 * @return if the robot has any investigating point at the moment
	 */
	protected boolean hasInvestigatingPoint() {
		return !investigatingPoints.isEmpty();
	}

	/**
	 * 
	 * @return if removeFirst has returned an investigating point in the most
	 *         recent call, meaning the robot is investigating something
	 */
	protected boolean isInvestigating() {
		return isInvestigating;
	}

	/**
	 * 
	 * @return the last observing point removed from stack
	 */
	protected Point getLastObservingPoint() {
		return lastObservingPoint;
	}

	/**
	 * 
	 * @return number of visited locations
	 */
	protected int visitedPlaces() {
		return initialCount - observingPoints.size();
	}

	/**
	 * 
	 * @param occupied
	 *            the point where the object is occupying
	 * @return if there is any observing point that should be removed since the
	 *         object is at that location.
	 */
	protected boolean removeRelevant(Point occupied) {
		boolean output = false;
		Display.printDebug(Util.doubleToString(occupied.getX(), 2) + ", A"
				+ Util.doubleToString(occupied.getY(), 2), 6);
		for (Iterator<Point> it = observingPoints.iterator(); it.hasNext();) {
			Point destination = it.next();

			if (destination.distance(occupied) < CoordinateManager.DANGER_ZONE_RADIUS
					+ Hardware.ROBOT_RADIUS) {
				output = true;
				it.remove();
			}
		}
		return output;
	}
}