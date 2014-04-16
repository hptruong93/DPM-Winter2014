package navigator.pathPlanner;

import java.util.ArrayList;

import utilities.Point;

/**
 * PathPlanner.java
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
 * Interface for a general path planner
 *
 */
public interface PathPlanner {
	/**
	 * Plan a valid path from start to any valid point in end set
	 * @param start starting point
	 * @param end set of possible ending point
	 * @return the ArrayList of point representing the found path in reverse order (end -> ... -> start)
	 */
	public ArrayList<Point> planPath(Point start, ArrayList<Point> end);
	
	/**
	 * Add an obstacle into the path planner
	 * @param position position of the new obstacle
	 * @return if adding succeeded
	 */
	public boolean addObstacle(Point position);
	
	/**
	 * Check if a point is valid given the known obstacles
	 * @param p the point to check
	 * @return if the point does not collide with any obstacle
	 */
	public boolean validPoint(Point p);
}
