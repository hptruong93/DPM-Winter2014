package odometry;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import utilities.FieldInfo;
import utilities.Hardware;
import utilities.Point;
import utilities.Util;
import data.DualColorData;
import data.DualUSData;

/**
 * Odometer.java
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
 * Odometer of the robot. The odometer follows Cartesian coordinate convention.
 * x axis is the horizontal axis, with right is the increasing direction. y axis
 * is the vertical axis, with up is the increasing direction. theta is the
 * conventional counter-clockwise angle calculated from x axis.
 * Note that theta is always calculated and used in radian.
 * 
 * 
 */
public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	private final NXTRegulatedMotor leftWheel, rightWheel;
	private float prevTachoLeft, prevTachoRight, tachoLeft, tachoRight;

	// default constructor
	public Odometer(NXTRegulatedMotor leftWheel, NXTRegulatedMotor rightWheel) {
		x = FieldInfo.INIT_POSITION.getX();
		y = FieldInfo.INIT_POSITION.getY();
		theta = FieldInfo.INIT_ANGLE;
		lock = new Object();
		this.leftWheel = leftWheel;
		this.rightWheel = rightWheel;
	}

	/**
	 * Update the odometer variables using algorithm provided in the slide in
	 * lab 2.
	 */
	@Override
	public void run() {
		while (true) {
			// put (some of) your odometer code here

			prevTachoLeft = tachoLeft;
			prevTachoRight = tachoRight;

			tachoLeft = (float) Math.toRadians(leftWheel.getTachoCount());
			tachoRight = (float) Math.toRadians(rightWheel.getTachoCount());

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!

				// Vector describing the instantaneous movement of the robot
				// in the interval.
				MoveVector vector = new MoveVector();

				double distanceLeft = (tachoLeft - prevTachoLeft) * Hardware.LEFT_WHEEL_RADIUS;
				double distanceRight = (tachoRight - prevTachoRight) * Hardware.RIGHT_WHEEL_RADIUS;
				double dDistance = (distanceRight - distanceLeft);

				double averageDistance = (distanceLeft + distanceRight) / 2;

				double newTheta = theta + dDistance / Hardware.WHEEL_WIDTH;

				vector.x = averageDistance * Math.cos(newTheta);
				vector.y = averageDistance * Math.sin(newTheta);

				x += vector.x;
				y += vector.y;

				theta = Util.fix2Pi(newTheta);
			}

			try {
				Thread.sleep(ODOMETER_PERIOD);
			} catch (InterruptedException e) {
				// there is nothing to be done here because it is not
				// expected that the odometer will be interrupted by
				// another thread
			}
		}
	}

	/**
	 * Get unpackaged odometry data
	 * 
	 * @param position
	 *            double array that position information will be written into
	 * @param update
	 *            boolean array indicating if the information should be
	 *            extracted or ignored
	 */
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	/**
	 * 
	 * @return position of the robot in Point format
	 */
	public Point getPosition() {
		Point output;
		synchronized (lock) {
			output = new Point(x, y);
		}

		return output;
	}

	/**
	 * Get the current ultrasonic sensor position of the robot.
	 * 
	 * @param side
	 *            side of the sensor (DualUSData.LEFT_SIDE or
	 *            DualUSData.RIGHT_SIDE)
	 * @return position of the sensor in Point format
	 */
	public Point getUSSensorPosition(int side) {
		Point output = getPosition();
		output = output.getFrontPoint(theta, Hardware.US_SENSOR_TO_CENTRE_UP);

		if (side == DualUSData.LEFT_SIDE) {
			output = output.getFrontPoint(theta - Math.PI / 2, Hardware.US_SENSOR_TO_CENTRE_ACROSS);
		} else if (side == DualUSData.LEFT_SIDE) {
			output = output.getFrontPoint(theta + Math.PI / 2, Hardware.US_SENSOR_TO_CENTRE_ACROSS);
		}
		return output;
	}

	/**
	 * Get the current color sensor position of the robot.
	 * 
	 * @param side
	 *            side of the sensor (DualColorData.LEFT_SIDE or
	 *            DualColorData.RIGHT_SIDE)
	 * @return position of the sensor in Point format
	 */
	public Point getColorSensorPosition(int side) {
		double xOffset = 0, yOffset = 0;

		if (side == DualColorData.LEFT_SIDE) {
			xOffset = -Math.sin(theta) * Hardware.COLOR_SENSOR_TO_CENTRE_ACROSS + Math.cos(theta) * Hardware.COLOR_SENSOR_TO_CENTRE_UP;
			yOffset = Math.cos(theta) * Hardware.COLOR_SENSOR_TO_CENTRE_ACROSS + Math.sin(theta) * Hardware.COLOR_SENSOR_TO_CENTRE_UP;
		} else if (side == DualColorData.RIGHT_SIDE) {
			xOffset = Math.sin(theta) * Hardware.COLOR_SENSOR_TO_CENTRE_ACROSS + Math.cos(theta) * Hardware.COLOR_SENSOR_TO_CENTRE_UP;
			yOffset = -Math.cos(theta) * Hardware.COLOR_SENSOR_TO_CENTRE_ACROSS + Math.sin(theta) * Hardware.COLOR_SENSOR_TO_CENTRE_UP;
		}
		Point sensor = new Point(this.x + xOffset, this.y + yOffset);
		return sensor;
	}

	/**
	 * This method provides thread-safe access to the x coordinate
	 * @return x value of the odometer
	 */
	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	/**
	 * This method provides thread-safe access to the y coordinate
	 * @return y value of the odometer
	 */
	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	/**
	 * This method provides thread-safe access to the angle theta
	 * @return angle theta value of the odometer
	 */
	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	/**
	 * This method provides thread-safe access to the angle theta
	 * @return angle theta value of the odometer in degree
	 */
	public double getThetaInDegree() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return Math.toDegrees(result);
	}

	/**
	 * 
	 * @param distance
	 *            the distance that will be checked.
	 * @return if the point "distance" away in front of the robot is outside the
	 *         field.
	 */
	public boolean isSeeingWall(double distance) {
		Point front = getPosition().getFrontPoint(theta, distance);

		if (!FieldInfo.isInside(front)) {
			return true;
		} else {
			Sound.beep();
			return false;
		}
	}

	/**
	 * Test if robot is seeing wall
	 * @return if 30cm in front of the robot is a point outside the field.
	 */
	public boolean isSeeingWall() {
		return isSeeingWall(30);
	}

	/**
	 * Provide thread-safe update of the odometry information of the robot
	 * @param position the position information: x, y and theta
	 * @param update boolean array indicating which value should be updated
	 */
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}
	
	/**
	 * Provide thread-safe update of the x coordinate information of the robot
	 * @param x new x position of the robot
	 */
	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	/**
	 * Provide thread-safe update of the y coordinate information of the robot
	 * @param y new y position of the robot
	 */
	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	/**
	 * Provide thread-safe update of the theta information of the robot
	 * @param theta new theta position of the robot
	 */
	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}

	/** Supported class structure to calculate odometer information.
	 * @author ptruon4
	 *
	 */
	private class MoveVector {
		double x, y;

		private MoveVector() {
		}
	}
}