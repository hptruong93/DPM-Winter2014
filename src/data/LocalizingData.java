package data;

import utilities.Point;
import data.DualUSData.DualUSPackage;

/**
 * LocalizingData.java
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
* Encapsulation of Localizing Data
* Contains the following information:
* 1) distance read by the ultrasonic sensor
* 2) angle at which the ultrasonic sensor reads data
*    This usually coincides with the actual angle of the robot
*    but they need not be the same
* 3) position of the robot at which the distance was read by the ultrasonic sensor
* 4) Which ultrasonic sensor reads the distance: DualUSData.LEFT_SIDE or DualUSData.RIGHT_SIDE
*/
public class LocalizingData implements DataProvider {

	private LocalizePackage data;

	public LocalizingData(DualUSPackage data, Point sensorPosition, Point robotPosition, double angle) {
		this.data = new LocalizePackage(data.getReading(), data.getSide(), sensorPosition, robotPosition, angle);
	}
	
	@Override
	public LocalizePackage getData() {
		return data;
	}

	/**
	* Hidden internal structure of how data is stored
	*/
	public static class LocalizePackage {
		private int distance;
		private int side;
		private double angle;
		private Point sensorPosition, robotPosition;
		
		private LocalizePackage(int distance, int side, Point sensorPosition, Point robotPosition, double angle) {
			this.distance = distance;
			this.angle = angle;
			this.sensorPosition = sensorPosition;
			this.robotPosition = robotPosition;
			this.side = side;
		}
		
		public int getSide() {
			return side;
		}
		
		public int getDistance() {
			return this.distance;
		}
		
		public Point getSensorPosition() {
			return this.sensorPosition;
		}
		
		public Point getRobotPosition() {
			return this.robotPosition;
		}
		
		public double getAngle() {
			return this.angle;
		}		
	}
}