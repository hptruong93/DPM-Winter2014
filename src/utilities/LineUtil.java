package utilities;

/**
 * LineUtil.java
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
 * This class provides utilities to deal with lines in general as well as grid lines in the
 * competition field
 * @author ptruon4
 *
 */
public class LineUtil {
	
	/**
	 * Array containing information about the horizontal (x) and vertical (y) grid lines on 
	 * the competition ground. The first dimension of the array indicates the line number (starting from 0),
	 * the second dimension of the array indicates the line equation of that line in the form ax + by + c =  0
	 */
	public static final double[][] xLines;
	public static final double[][] yLines;
	
	/**
	 * Static initialization of competition grid lines
	 */
	static {
		xLines = new double[FieldInfo.LENGTH][];
		
		for (int i = 0; i < xLines.length; i++) {
			xLines[i] = new double[] {0,1, -i * FieldInfo.TILE_WIDTH};
		}
		
		yLines = new double[FieldInfo.WIDTH][3];
		
		for (int i = 0; i < yLines.length; i++) {
			yLines[i] = new double[] {1, 0, -i * FieldInfo.TILE_WIDTH};
		}
	}
	
	public static double diff(int closest, double value) {
		return Math.abs(linePosition(closest) - value);
	}

	/**
	 * Find the line(s) among the xLines and yLines that would intersect the circle at two points
	 * @param centre centre of the circle
	 * @param radius radius of the circle
	 * @return array of line(s) that would intersect the circle at two points
	 */
	public static double[][] intersect(Point centre, double radius) {
		int xLineFound = -1, yLineFound = -1;
		
		for (int i = 0; i < xLines.length; i++) {
			Point[] intersection = Point.intersectionLineCircle(xLines[i], centre, radius);
			if (intersection.length == 2) {
				xLineFound = i;
				break;
			}
		}
		
		for (int i = 0; i < yLines.length; i++) {
			Point[] intersection = Point.intersectionLineCircle(yLines[i], centre, radius);
			if (intersection.length == 2) {
				yLineFound = i;
				break;
			}
		}
		
		if (xLineFound != -1 && yLineFound != -1) {//Both found
			return new double[][]{xLines[xLineFound], yLines[yLineFound]};
		} else if (xLineFound != -1) {//Found x
			return new double[][]{xLines[xLineFound]};
		} else if (yLineFound != -1) {//Found y
			return new double[][]{yLines[yLineFound]};
		} else {//Cannot find
			return new double[][] {};
		}
	}
	
	/**
	 * Find the closest grid line to a certain value. This will check both horizontal and vertical lines
	 * on the competition ground.
	 * @param x is the x or y position you want to check which line it is closest to
	 * @return the number of the line [0] closest and the distance to that line [1]
	 */
	public static double[] findClosest(double x) {
		double min = Double.MAX_VALUE;
		double[] output = new double[2];
		for (int i = 0; i < 20; i++) {
			double current = Math.abs(linePosition(i) - x); 
			if (current < min) {
				min = current;
				output[0] = i;
				output[1] = min;
			}
		}
		//if (output[0] == 0) return 1;
		return output;
	}

	/**
	 * Calculate coordinate value of a certain line
	 * @param lineNumber (from 0 to WIDTH or LENGTH) of the field
	 * @return coordinate representing the line (line 0 -> 0, 1 -> 30.48, 2 -> 40.96 ...)
	 */
	private static double linePosition(int lineNumber) {
		return 30.48 * lineNumber;
	}
}
