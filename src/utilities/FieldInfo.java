package utilities;

import java.util.ArrayList;

import objectHandler.ObjectDetector;
import odometry.Odometer;
import bluetooth.PlayerRole;

/**
 * FieldInfo.java
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
 *This class contains all field information.
 *This class is a static class and its contents must only be changed by blueToothInit.
 *Initial values set in the class are used for testings purpose. Bluetooth initialization will override these values. 
 * @author ptruon4
 */

public class FieldInfo {
	public static final int WIDTH = 12;
	public static final int LENGTH = 12;
	
	public static final double TILE_WIDTH = 30.48;
	public static final double HALF_TILE = TILE_WIDTH / 2;
	
	public static final Point FIELD_CENTRE = new Point(HALF_TILE * WIDTH, HALF_TILE * LENGTH);
	
	public static final Point INIT_POSITION = new Point(0,0);
	public static final double INIT_ANGLE = 0 * Math.toRadians(90);
	
	public static final int LIGHT_READING_LINE_TRESHOLD = -20;
	
	public static Point LOWER_LEFT = new Point(9 * TILE_WIDTH, 6 * TILE_WIDTH);
	public static Point UPPER_RIGHT = new Point( 11 * TILE_WIDTH, 8 * TILE_WIDTH);
	public static int STARTING_CORNER = 4;
	
	/**
	 * Private constructor to prevent construction
	 */
	private FieldInfo() {
	}
	
	private static final double LEFT_BOUND = -TILE_WIDTH + Hardware.ROBOT_RADIUS;
	private static final double RIGHT_BOUND = WIDTH * TILE_WIDTH - Hardware.ROBOT_RADIUS;
	
	private static final double TOP_BOUND = LENGTH * TILE_WIDTH - Hardware.ROBOT_RADIUS;
	private static final double BOT_BOUND = -TILE_WIDTH + Hardware.ROBOT_RADIUS;
	
	public static boolean isInside(Point point) {
		return point.getX() > LEFT_BOUND && point.getX() < RIGHT_BOUND && point.getY() > BOT_BOUND && point.getY() < TOP_BOUND;
	}
	
	/**
	 * Constants that will be changed by bluetooh initialization
	 */
	public static PlayerRole role;
	
	public static int ourFlagZoneLowerLeft_X;
	public static int ourFlagZoneLowerLeft_Y;
	public static int ourFlagZoneUpperRight_X;
	public static int ourFlagZoneUpperRight_Y;
	
	public static int theirFlagZoneLowerLeft_X;
	public static int theirFlagZoneLowerLeft_Y;
	public static int theirFlagZoneUpperRight_X;
	public static int theirFlagZoneUpperRight_Y;
	
	public static int ourGoalZone_X;
	public static int ourGoalZone_Y;
	public static int theirGoalZone_X;
	public static int theirGoalZone_Y;
	public static int ourFlagColor;
	public static int theirFlagColor;
	public static Point DROP_OFF_CORNER = new Point(1* TILE_WIDTH, 4* TILE_WIDTH);
	public static Point DROP_OFF_ZONE = new Point(DROP_OFF_CORNER.getX() + HALF_TILE, DROP_OFF_CORNER.getY() + HALF_TILE);
	public static Point PROHIBITED_ZONE = new Point(900, 900);
	
	/**
	 * This provides an array of constants to translate from the color code provided by the bluetooth system to the ObjectDetector system 
	 */
	public static final int[] COLOR_TRANSLATION = new int[] {ObjectDetector.UNKNOWN, ObjectDetector.CYAN, ObjectDetector.RED, ObjectDetector.YELLOW, ObjectDetector.WHITE, ObjectDetector
			.BLUE};

	/**
	 * Calculate zone positions that the robot can get to in order to start flag searching.
	 * These corners must be outside the zone, and they must be in the field. Moreover, they should be
	 * at least HALF_TILE away from the zone so that the robot will not mistake flags inside the zone with
	 * obstacles
	 * @param lowerLeft the lower left corner of the zone 
	 * @param upperRight the upper right corner of the zone
	 * @return an ArrayList of possible positions that the robot can get to in order to start flag searching
	 */
	public static ArrayList<Point> findZoneCorner(Point lowerLeft, Point upperRight) {
		ArrayList<Point> corners = new ArrayList<Point>();
	
		/**
		 * We are currently taking four outer corner of the zone.
		 */
		corners.add(new Point(lowerLeft.getX() - 0.5 * TILE_WIDTH, lowerLeft.getY() - 0.5 * TILE_WIDTH));
		corners.add(new Point(lowerLeft.getX() - 0.5 * TILE_WIDTH, upperRight.getY() + 0.5 * TILE_WIDTH));
		corners.add(new Point(upperRight.getX() + 0.5 * TILE_WIDTH, lowerLeft.getY() - 0.5 * TILE_WIDTH));
		corners.add(new Point(upperRight.getX() + 0.5 * TILE_WIDTH, upperRight.getY() + 0.5 * TILE_WIDTH));
	
		ArrayList<Point> fileteredCorners = new ArrayList<Point>();
		
		/**
		 * Start filtering out corners that are invalid (outside the field)
		 */
		for (int i = 0; i < corners.size(); i++) {
			if (isInside(corners.get(i))) {
				fileteredCorners.add(corners.get(i));
			}
		}
		
		return fileteredCorners;
	}
	
	/**
	 * Change odometer configuration depending on the starting corner
	 * @param odometer the robot's current odometer
	 * @param corner starting corner provided by the bluetooth
	 */
	public static void odometerTransformation(Odometer odometer, int corner) {
		if (corner == 1) {
			return;
		} else if (corner == 2) {
			odometer.setX(10 * TILE_WIDTH);
			odometer.setY(0);
			odometer.setTheta(Math.PI/2);
		} else if (corner == 3) {
			odometer.setX(10 * TILE_WIDTH);
			odometer.setY(10 * TILE_WIDTH);
			odometer.setTheta(Math.PI);
		} else if (corner == 4) {
			odometer.setX(0);
			odometer.setY(10 * TILE_WIDTH);
			odometer.setTheta(3*Math.PI/2);
		}
	}
}