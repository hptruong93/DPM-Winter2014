package navigator.pathPlanner;

import java.util.ArrayList;
import java.util.Random;

import utilities.FieldInfo;
import utilities.Point;
import utilities.Util;

/**
 * RRT.java
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
 * Rapidly exploring random tree implementation.
 * 
 * 
 */
public class RRT implements PathPlanner {

	private static final int RADIUS = 15;
	private final ArrayList<Point> obstacles;
	private final Random randomAgent;

	public RRT() {
		obstacles = new ArrayList<Point>();
		randomAgent = new Random(System.currentTimeMillis());
	}

	/**
	 * Using Rapidly Exploring Random Tree to find a path from start to any of
	 * the Point in a set of end points
	 * 
	 * @param start
	 *            starting point
	 * @param end
	 *            set of possible end points
	 * @return path from start to only one point in the end points set
	 */
	public ArrayList<Point> planPath(Point start, ArrayList<Point> end) {
		for (Point ending : end) {
			if (validPoint(ending)) {
				return planPath(start, ending);
			}
		}

		return null;
	}

	/**
	 * Using Rapidly Exploring Random Tree to find a path from start to end,
	 * given that every point on the path does not lie inside an obstacle.
	 * 
	 * This uses the method rrt() and backtrack the parent of the destination to
	 * generate a path from starting point to end point. Ending point of this
	 * method must be checked by validPoint().
	 * 
	 * @param start
	 *            starting point
	 * @param end
	 *            terminal point
	 * @return the ArrayList representing the path in the reverse order (end -->
	 *         start). Return null if cannot find a path
	 */
	private ArrayList<Point> planPath(Point start, Point end) {
		ArrayList<Point> output = new ArrayList<Point>();

		Graph found = rrt(start, end);

		int current = found.sizeV() - 1;
		while (true) {
			if (current < 0) {
				return null;
			} else {
				output.add(found.getVertex(current));
				if (current == 0) {
					return simplify(output);
				} else {
					current = found.getParent(current);
				}
			}
		}
	}

	/**
	 * Join two paths into one. Path1 must be followed by path2 (i.e. path1's
	 * destination must be path2's start)
	 * 
	 * @param path1
	 *            first path
	 * @param path2
	 *            second path
	 * @return the smoothen joint path (order is preserved).
	 */
	public ArrayList<Point> joinPath(ArrayList<Point> path1, ArrayList<Point> path2) {
		if (!path2.get(path2.size() - 1).theSame(path1.get(0))) {
			return null;
		}

		for (int i = 1; i < path1.size(); i++) {
			path2.add(path1.get(i));
		}

		return simplify(path2);
	}

	/**
	 * Simplify a path. For example, if path is A -> B -> C, this will reduce
	 * the path to A -> C if the path A -> C is valid.
	 * 
	 * @param path
	 *            the input path. This may or may not be a minimum path
	 * @return a simplified path based on the known obstacles on the map
	 */
	private ArrayList<Point> simplify(ArrayList<Point> path) {
		if (path == null || path.size() < 2) {
			return null;
		}

		if (path.size() == 2) {
			return path;
		}

		ArrayList<Point> output = new ArrayList<Point>();

		Point prev = path.get(0);
		Point end = path.get(1);
		output.add(prev);
		output.add(end);

		int index = 2;
		while (index < path.size()) {
			Point next = path.get(index);

			boolean valid = validPath(prev, next);

			if (valid) {
				output.set(output.size() - 1, next);
			} else {
				output.add(next);
			}

			prev = output.get(output.size() - 2);

			index++;
		}

		return output;
	}

	/**
	 * Grow a rapidly exploring random tree from a starting point to a
	 * destination.
	 * 
	 * @param start
	 *            starting point
	 * @param end
	 *            destination point
	 * @return the tree that includes the destination as the last vertex, and
	 *         the starting point as the first vertex
	 */
	private Graph rrt(Point start, Point end) {
		Graph output = new Graph();
		output.addVertex(start);

		do {
			PointFromVertex random = randomGeneration(output, end);
			Point next = random.point;

			int fromVertex = random.vertex;

			int bestVertex = -1;
			double bestDistance = Double.MAX_VALUE;

			for (int vertex = 0; vertex < output.sizeV(); vertex++) {
				Point currentVertex = output.getVertex(vertex);
				double currentDistance = currentVertex.distance(next);

				if ((currentDistance < RADIUS) && (vertex != fromVertex)) {
					bestVertex = -1;
					break;
				}

				if (currentDistance < bestDistance) {
					if (validPath(currentVertex, next)) {
						bestVertex = vertex;
						bestDistance = currentDistance;
					}
				}
			}

			if (bestVertex != -1) {
				int nextVertex = output.addVertex(next);
				output.addEdge(bestVertex, nextVertex, 1);
				output.setParent(nextVertex, bestVertex);

				if (validPath(next, end)) {
					int finalVertex = output.addVertex(end);
					output.addEdge(nextVertex, finalVertex, 1);
					output.setParent(finalVertex, nextVertex);
					break;
				}
			}
		} while (true);

		return output;
	}

	/**
	 * Add a newly detected obstacle to the path planner
	 * 
	 * @param position
	 *            position of the obstacle
	 * @return if adding succeeded
	 */
	public boolean addObstacle(Point position) {
		return obstacles.add(position);
	}

	/**
	 * Get all obstacle currently registered inside the path planner
	 * 
	 * @return all obstacle currently registered inside the path planner
	 */
	public ArrayList<Point> getObstacle() {
		return obstacles;
	}

	/**
	 * Determine if a path if valid. This path is a straight line created by two
	 * points
	 * 
	 * @param start
	 *            starting point of the path
	 * @param end
	 *            ending point of the path
	 * @return if the path is valid (no obstacle on the way)
	 */
	private boolean validPath(Point start, Point end) {
		for (Point obstacle : obstacles) {
			if (obstacle.distanceToLine(start.lineThrough(end)) < 2 * RADIUS) {
				Point intersection = Point.intersection(obstacle.lineThrough(start.angle(end) + Math.PI / 2), start.lineThrough(end));
				if (intersection.belongsToSegment(start, end) || start.distance(obstacle) < 2 * RADIUS || end.distance(obstacle) < 2 * RADIUS) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Return if a point is a valid point for the robot to be in. This only
	 * checks if the point collides with a known obstacle
	 * 
	 * @param p
	 *            the interested point that will be check
	 * @return if the input is a valid point for the robot to be in
	 */
	public boolean validPoint(Point p) {
		for (Point obstacle : obstacles) {
			if (p.distance(obstacle) < 2 * RADIUS) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Randomly generate a Point on the field. Currently, the random agent will
	 * pick a random vertex from the graph, and created a random point that is 2
	 * * RADIUS to 4 * RADIUS away from the selected vertex (with random angle).
	 * If the randomed point is not in the field, randomization is repeated.
	 * 
	 * @param g
	 *            the existing graph representing the tree
	 * @param end
	 *            the destination. May or may not be used by the generator
	 * @return the newly random Point on the field. This point is guaranteed to
	 *         be inside the field using FieldInfo.isInside()
	 */
	private PointFromVertex randomGeneration(Graph g, Point end) {
		Point random;
		int selectedVertex;
		do {
			int size = g.sizeV();
			selectedVertex = randomAgent.nextInt(size);

			Point selectedPoint = g.getVertex(selectedVertex);
			double radius = Util.randBetween(2 * RADIUS, 4 * RADIUS);
			double angle = Math.random() * Math.PI * 2;

			random = selectedPoint.getFrontPoint(angle, radius);
			if (FieldInfo.isInside(random)) {
				break;
			}
		} while (true);

		PointFromVertex output = new PointFromVertex(random, selectedVertex);
		return output;
	}

	/**
	 * Object wrap to represent a point generated by the random agent in method
	 * randomGeneration(). This contains information about the random point, and
	 * the vertex from which the point is randomed from.
	 * 
	 * @author ptruon4
	 * 
	 */
	private class PointFromVertex {
		private final Point point;
		private final int vertex;

		private PointFromVertex(Point point, int vertex) {
			this.point = point;
			this.vertex = vertex;
		}
	}
}