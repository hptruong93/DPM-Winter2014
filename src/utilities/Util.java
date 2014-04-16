package utilities;

import java.util.ArrayList;

/**
 * Util.java
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
 * This is a static class. No instance of this should be created.
 * This class provides multiple supporting functions and methods.
 * @author ptruon4
 *
 */
public class Util {
	
	/**
	 * Private constructor to prevent instance construction
	 */
	private Util() {}
	
	/**
	 * Convert a double to its String representation for debugging purpose
	 * @param x the double value that will be converted
	 * @param places number of decimal places that will be rounded of the input x
	 * @return String representation of x, with appropriate number of decimal places
	 */
	public static String doubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;

		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";

		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long) x;
			if (t < 0)
				t = -t;

			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}

			result += stack;
		}

		// put the decimal, if needed
		if (places > 0) {
			result += ".";

			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long) x);
			}
		}

		return result;
	}
	
	/**
	 * Convert an int to array of 4 bytes. Big-endian format 
	 * @param number int number that will be converted
	 * @return array of 4 bytes representing the number
	 */
	public static byte[] toArray(int number) {
		byte[] output = new byte[4];
		output[3] = (byte) (number & 0xFF);
		number = number >> 4;
		output[2] = (byte) (number & 0xFF);
		number = number >> 4;
		output[1] = (byte) (number & 0xFF);
		number = number >> 4;
		output[0] = (byte) (number & 0xFF);
		return output;
	}
	
	/**
	 * Determine if a double is between two other double
	 * @param value the double that will be compared
	 * @param left left value (lower threshold)
	 * @param right right value (upper threshold), this must not be smaller than the lower threshold
	 * @return if value is between left and right
	 */
	public static boolean between (double value, double left, double right) {
		return value > left && value < right;
	}
	
	/**
	 * Compare two double and check if they are virtually equal (very close to each other)
	 * @param a first double
	 * @param b second double
	 * @return if a is virtually equal to b
	 */
	public static boolean equal(double a, double b) {
		return Math.abs(a - b) < 0.00001;
	}
	
	/**
	 * Compare two double and check if they are close (not very close though)
	 * @param a first double
	 * @param b second double
	 * @return if a is close to b
	 */
	public static boolean close(double a, double b) {
		return Math.abs(a - b) < 1;
	}
	
	/**
	 * Compare two double and check if they are close (not very close though). Threshold of close
	 * is given.
	 * @param a first double
	 * @param b second double
	 * @param threshold threshold value that defines "close"
	 * @return if a is close to b, given the threshold value
	 */
	public static boolean close(double a, double b, double threshold) {
		return Math.abs(a - b) < threshold;
	}
	
	/**
	 * Using the standard random to random two double between two bounds.
	 * @param low Lower bound for random
	 * @param high upper bound for random. This must not be smaller than lower bound
	 * @return a random double between low and high.
	 */
	public static double randBetween(double low, double high) {
		return low + Math.random() * (high - low);
	}
	
	/**
	 * 
	 * @param position current coordinate
	 * @param angle current orientation
	 * @return the position of the point in front, with distance is 'distance' away from current position
	 */
	public static double[] getFrontCoordinate(double[] position, double angle, double distance) {
		return new double[] {position[0] + distance * Math.cos(angle), position[1] + distance * Math.sin(angle) };
	}
	
	/**
	 * 
	 * @param position current position coordinate
	 * @param dest destination coordinate
	 * @return angle from the current position to the destination in radian.
	 */
	public static double angle(double[] position, double[] dest) {
		if (equal(dest[0], position[0])) {
			if (dest[1] > position[1]) {
				return Math.PI/2;
			} else {
				return -Math.PI/2;
			}
		}

		double m = (dest[1] - position[1]) / (dest[0] - position[0]);
		if (dest[0] > position[0]) return Math.atan(m); 
		return Math.atan(m) + Math.PI;
	}
	
	/**
	 * 
	 * @param a coordinate of first point
	 * @param b coordinate of second point
	 * @return distance between 2 points
	 */
	public static double distance(double[] a, double[] b) {
		return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
	}
	
	/**
	 * Calculate the least angle that robot has to turn
	 * @param currentTheta current angle
	 * @param destTheta destination angle
	 * @return the least angle that one has to turn to read destination angle
	 */
	public static double calculateTurnAngle(double currentTheta, double destTheta) {
		double fixedDestTheta = fix2Pi(destTheta);

		double choice = fixedDestTheta - currentTheta;
		choice = fix2Pi(choice);

		if (choice > Math.PI) {//To right
			return -(Math.PI * 2 - choice);
		} else {
			return choice;
		}
	}
	
	/**
	* Ensure that angle theta will always be from 0 to 2pi
	*/
	public static double fix2Pi(double angle) {
		final double PI_2 = 2 * Math.PI;
		if (angle >= PI_2)
			return angle - PI_2;
		if (angle <= 0)
			return angle + PI_2;
		return angle;
	}

	/**
	* Ensure that angle theta will always be from -pi to pi
	*/
	public static double fixPI(double angle) {
		if (angle > Math.PI * 2) {
			while (angle > Math.PI * 2) angle -= 2 * Math.PI;
		} else if (angle < 0) {
			while (angle < 0) angle += 2 * Math.PI;
		}
		
		if (angle > Math.PI) angle = angle - 2 * Math.PI;
		
		return angle;
	}
	
	/**
	 * Find the closest point among a set of points given the current position
	 * @param odometer the current position of the robot 
	 * @param points a set of points that will be considered
	 * @return the closest point among the provided set of points to the robot
	 */
	public static Point closest(Point currentPosition, ArrayList<Point> points) {
		double min = 1000000;
		Point out = null;
		for (Point p : points) {
			double d = p.distance(currentPosition);
			if (d < min) {
				min = d;
				out = p;
			}
		}
		return out;
	}
}
