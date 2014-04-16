package utilities;

/**
 * Point.java
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
 * This class encapsulate information and provide useful utilities to process 2D points.
 * This class is immutable.
 * @author ptruon4
 *
 */
public class Point {
	private double x;
	private double y;

	/**
	 * Constructor
	 * @param x x value of the point on the plane
	 * @param y y value of the point on the plane
	 * @throws IllegalArgumentException if x or y is NaN
	 */
	public Point(double x, double y) {
		if (x == Double.NaN || y == Double.NaN) {
			throw new IllegalArgumentException("NaN Point!!!");
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * 
	 * @param position
	 *            current position coordinate
	 * @param dest
	 *            destination coordinate
	 * @return angle in radian created by the segment starting from the other
	 *         point, ending at this point, and the x-axis
	 */
	public double angle(Point other) {
		if (Util.equal(other.x, x)) {
			if (other.y > y) {
				return Math.PI / 2;
			} else {
				return -Math.PI / 2;
			}
		}

		double m = (other.y - y) / (other.x - x);
		if (other.x > x)
			return Math.atan(m);
		return Math.atan(m) + Math.PI;
	}

	/**
	 * 
	 * @param other
	 *            other point
	 * @return line in form ax + by + c = 0 that passes through the two points
	 */
	public double[] lineThrough(Point other) {
		double dx = this.x - other.x;
		if (dx == 0) {
			return new double[] { 1, 0, x };
		} else {
			double dy = this.y - other.y;
			return new double[] { dy, -dx, dx * y - dy * x };
		}
	}

	/**
	 * 
	 * @param line
	 *            line information in form ax + by + c = 0
	 * @return distance from the point to the line
	 */
	public double distanceToLine(double[] line) {
		return Math.abs(line[0] * x + line[1] * y + line[2]) / Math.sqrt(line[0] * line[0] + line[1] * line[1]);
	}

	/**
	 * 
	 * @param other
	 *            other point
	 * @return distance from this point to the other point
	 */
	public double distance(Point other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
	}

	/**
	 * 
	 * @param other
	 *            coordinate of other point (2D)
	 * @return distance from this point to the other point
	 */
	public double distance(double[] other) {
		return Math.sqrt(Math.pow(this.x - other[0], 2) + Math.pow(this.y - other[1], 2));
	}

	/**
	 * Calculate the mid point of the segment created by this point and other
	 * @param other the other end of the segment. This point is one end
	 * @return Mid point of the segment
	 */
	public Point midPoint(Point other) {
		return new Point(0.5 * (x + other.x), 0.5 * (y + other.y));
	}
	
	/**
	 * Get the intersection between a line and a circle
	 * @param line line information in the form ax + by + c = 0
	 * @param centre centre of the circle
	 * @param radius radius of the circle
	 * @return array of intersections between line and the circle
	 */
	public static Point[] intersectionLineCircle(double[] line, Point centre, double radius) {
		/**
		 * H is the projection of centre on line
		 */
		double lineAngle = getLineAngle(line);
		Point H = intersection(centre.lineThrough(lineAngle + Math.PI/2), line);
		double OH = H.distance(centre);
		if (Util.equal(OH, radius)) {//One intersection - tangent line
			return new Point[] {H};
		} else if (OH > radius) {//No intersection
			return new Point[] {};
		} else {//Two intersection
			/**
			 * Two points of intersection are A and B (order does not matter)
			 */
			double AH = Math.sqrt(radius * radius - OH * OH);
			return new Point[] {H.getFrontPoint(lineAngle, AH), H.getFrontPoint(lineAngle, -AH)}; 
		}
	}
	
	/**
	 * 
	 * @param line line information in the form ax + by + c = 0
	 * @return the angle of the line
	 */
	public static double getLineAngle(double[] line) {
		if (Util.equal(line[1], 0)) {
			return Math.PI/2;
		} else {
			return Math.atan(-line[0]/line[1]);
		}
	}
	
	/**
	 * Check if this point belongs to the line segment created by joining start and end 
	 * @param start start of the segment
	 * @param end end point of the segment
	 * @return if this point belongs to the segment
	 */
	public boolean belongsToSegment(Point start, Point end) {
		return Util.equal(this.distance(start) + this.distance(end), start.distance(end)); 
	}
	
	/**
	 * Determine intersection between two lines in form ax + by + c = 0
	 * @param line1 information about line 1 in form ax + by + c = 0
	 * @param line2 information about line 2 in form ax + by + c = 0
	 * @return intersection between two lines. Null if there is no intersection or infinite intersections
	 */
	public static Point intersection(double[] line1, double[] line2) {
		double a1 = line1[0], a2 = line2[0], b1 = line1[1], b2 = line2[1], c1 = -line1[2], c2 = -line2[2];
		double det =a1 * b2 - a2 * b1; 
		if (Util.equal(det, 0)) {
			return null;
		} else {
			return new Point((b2 * c1 - b1 * c2)/det, (a1 * c2 - a2 * c1)/det);
		}
	}
	
	/**
	 * Create a line passing through this point, having an angle of input
	 * @param angle angle of the line
	 * @return line in form ax + by + c = 0
	 */
	public double[] lineThrough(double angle) {
		Point temp = this.getFrontPoint(angle, 1);
		return this.lineThrough(temp);
	}
	
	/**
	 * 
	 * @param angle
	 *            the angle at which this point is facing
	 * @param distance
	 *            the distance in front
	 * @return a point that is "distance" away from this point, facing in the
	 *         "angle" direction
	 */
	public Point getFrontPoint(double angle, double distance) {
		return new Point(x + distance * Math.cos(angle), y + distance * Math.sin(angle));
	}

	/**
	 * Rotate this point around a pivot
	 * @param pivot the pivot of rotation
	 * @Param angle the angle in radian that the point will be rotated. Counterclockwise is positive.
	 * @return a point resulted in the rotation of this point
	 */
	public Point getRotated(Point pivot, double angle) {
		double currentAngle = pivot.angle(this);
		currentAngle += angle;
		return pivot.getFrontPoint(currentAngle, pivot.distance(this));
	}
	
	/**
	 * Check if two points are virtually the same on the plane
	 * This is effectively the equals method, but overriding equals would mean
	 * overriding hashCode, and computing hashCode for a Point is rather more
	 * complicated.
	 * @param other other point that will be compared to this one
	 * @return if both x and y of other point are very close to this point
	 * @see Util.equal for the close definition
	 */
	public boolean theSame(Point other) {
		return Util.equal(x, other.x) && Util.equal(y, other.y);
	}

	/**
	 * Provide a deep copy of a point.
	 * @return a deep copy of the current point
	 */
	@Override
	public Point clone() {
		return new Point(x, y);
	}

	/**
	 * @return x value of the point
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return y value of the point
	 */
	public double getY() {
		return y;
	}

	/**
	 * 
	 * @param object
	 *            the coordinate of the object
	 * @return the closest grid intersection to the object If the object is more
	 *         than 20cm away from the closest grid intersection, null will be
	 *         returned
	 */
	public static Point getGridClosest(Point object) {
		double min = Double.MAX_VALUE;
		double xmin = 0, ymin = 0;
		for (int x = 0; x < FieldInfo.WIDTH - 1; x++) {
			for (int y = 0; y < FieldInfo.LENGTH - 1; y++) {
				double distance = object.distance(new double[] { FieldInfo.TILE_WIDTH * x, FieldInfo.TILE_WIDTH * y });
				if (distance < min) {
					xmin = FieldInfo.TILE_WIDTH * x;
					ymin = FieldInfo.TILE_WIDTH * y;
					min = distance;
				}
			}
		}

		if (min > 20) {
			return null;
		}

		return new Point(xmin, ymin);
	}

	/**
	 * Utility method to provide a String representation of the current point.
	 * @return a String representation of the current point
	 */
	@Override
	public String toString() {
		return "x = " + x + ", y = " + y;
	}
}
