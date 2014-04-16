package odometry;

import lejos.nxt.NXTRegulatedMotor;
import main.ReactiveThread;
import utilities.FieldInfo;
import utilities.Hardware;
import utilities.Point;
import utilities.Util;

/**
 * WheelDriver.java
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
 * This class provides a driver to control the wheels to bring the robot to certain destination.
 * It has no responsibility to ensure that there is no obstacle/ wall on the way, with an exception of backward().
 *
 */
public class WheelDriver extends ReactiveThread {

	private boolean travelling, turning;
	private static Object lock = new Object();

	private static final int PERIOD = 20;
	public static final double SMALL_STOP_THRESHOLD = 1;
	public static final double LARGE_STOP_THRESHOLD = 2;
	
	private static final double TOLERANCE_ANGLE = Math.toRadians(0.5);
	private static final double MAXIMUM_TOLERANCE_ANGLE = Math.toRadians(20);

	public static final int SPEED_NORMAL = 300;
	public static final int SPEED_SLOW = 200;
	public static final int SPEED_VERY_SLOW = 150;

	private Odometer odometer;
	private NXTRegulatedMotor leftMotor, rightMotor;
	private int speed;
	private double stopThreshold;

	public static double xDestination, yDestination;

	public WheelDriver(Odometer odometer, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this.odometer = odometer;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;

		leftMotor.stop(true);
		rightMotor.stop();

		stopThreshold = SMALL_STOP_THRESHOLD;
		
		setSpeed(SPEED_NORMAL);
	}

	/**
	 * @see ReactiveThread#run() Adjust the moving angle of the robot towards
	 *      the destination. If current angle and desired angle are close,
	 *      adjust the wheel in a p style so that the robot heads towards
	 *      destination. If current angle and desired angle are too far away,
	 *      stop the robot and turn to the desired angle.
	 */
	@Override
	public void run() {
		while (true) {
			if (enable) {
				if ((Util.close(xDestination, odometer.getX(), stopThreshold)) && (Util.close(yDestination, odometer.getY(), stopThreshold))) {
					if (travelling) {
						rightMotor.stop(true);
						leftMotor.stop();
						travelling = false;
					}
				} else {
					double angleDest = Util.angle(new double[] { odometer.getX(), odometer.getY() }, new double[] { xDestination, yDestination });
					angleDest = Util.fix2Pi(angleDest);

					double turn = Util.calculateTurnAngle(odometer.getTheta(), angleDest);

					if (Math.abs(turn) > MAXIMUM_TOLERANCE_ANGLE) {
						turnTo(angleDest);
					} else if (Math.abs(turn) > TOLERANCE_ANGLE) {
						this.adjust(Math.toDegrees(turn) * 2);
					} else {
						adjust(0);
					}
				}
			}

			try {
				Thread.sleep(PERIOD);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Simply change the robot destination. Once WheelDriver is active (run
	 * method), the robot will adjust its path towards destination.
	 */
	public void travelTo(double x, double y) {
		synchronized (lock) {
			travelling = true;
			xDestination = x;
			yDestination = y;

			if ((!Util.close(x, odometer.getX())) || (!Util.close(y, odometer.getY()))) {
				forwardForever();
			}
		}
	}

	/**
	 * Simply change the robot destination. Once WheelDriver is active (run
	 * method), the robot will adjust its path towards destination.
	 */
	public void travelTo(Point destination) {
		travelTo(destination.getX(), destination.getY());
	}

	/**
	 * Set robot in a straight-forward motion, but still allow other method to
	 * change the robot moving angle.
	 */
	private void forwardForever() {
		leftMotor.forward();
		rightMotor.forward();
	}

	/**
	 * @param distance
	 *            : how much forward the robot will travel. Simply change
	 *            robot's destination
	 */
	public void forward(double distance) {
		double[] dest = Util.getFrontCoordinate(new double[] { odometer.getX(), odometer.getY() }, odometer.getTheta(), distance);
		travelTo(dest[0], dest[1]);
	}

	/**
	 * @param distance
	 *            : how much forward the robot will travel. Should only use to go back a small
	 *            distance Return once the robot is at destination
	 */
	public void forcedForward(double distance) {
		if (!FieldInfo.isInside(odometer.getPosition().getFrontPoint(odometer.getTheta(), distance))) {
			return;
		}
		setEnable(false);

		stopRobot();
		travelling = true;
		rightMotor.rotate(convertDistance(Hardware.RIGHT_WHEEL_RADIUS, distance), true);
		leftMotor.rotate(convertDistance(Hardware.LEFT_WHEEL_RADIUS, distance), false);
		travelling = false;
		stopRobot();

		setEnable(true);
	}
	
	/**
	 * @param distance
	 *            : how much backward the robot will travel. Should only use to go back a small
	 *            distance Return once the robot is at destination
	 */
	public void backward(double distance) {
		if (!FieldInfo.isInside(odometer.getPosition().getFrontPoint(odometer.getTheta(), -distance))) {
			return;
		}
		setEnable(false);

		stopRobot();
		travelling = true;
		rightMotor.rotate(-convertDistance(Hardware.RIGHT_WHEEL_RADIUS, distance), true);
		leftMotor.rotate(-convertDistance(Hardware.LEFT_WHEEL_RADIUS, distance), false);
		travelling = false;
		stopRobot();

		setEnable(true);
	}

	/**
	 * Force the robot to stop all movement and turn to the desired angle.
	 * Return once the robot is at the desired angle.
	 * Robot's position after it turned will be set as destination.
	 * 
	 * @param theta
	 *            : desired angle of the robot
	 */
	public void turnTo(double theta) {
		double turn = Math.toDegrees(Util.calculateTurnAngle(odometer.getTheta(), theta));

		if (Util.equal(turn, 0))
			return;

		synchronized (lock) {
			setEnable(false);
			turning = true;

			setSpeed(speed);
			leftMotor.rotate(-convertAngle(Hardware.LEFT_WHEEL_RADIUS, Hardware.WHEEL_WIDTH, turn), true);
			rightMotor.rotate(convertAngle(Hardware.RIGHT_WHEEL_RADIUS, Hardware.WHEEL_WIDTH, turn), false);

			turning = false;
			setEnable(true);
		}
	}

	/**
	 * Turn the robot to facing a certain point
	 * @param p
	 */
	public void turnTo(Point p) {
		turnTo(odometer.getPosition().angle(p));
	}
	
	/**
	 * Start turning clockwise at the provided speed forever.
	 * 
	 * @param speed
	 *            the speed at which the wheels turn
	 */
	public void turnClockWise(int speed) {
		stopRobot();
		setSpeed(speed);
		leftMotor.forward();
		rightMotor.backward();
	}

	/**
	 * Start turning counter clockwise at the provided speed forever.
	 * 
	 * @param speed
	 *            the speed at which the wheels turn
	 */
	public void turnCounterClockWise(int speed) {
		stopRobot();
		setSpeed(speed);
		leftMotor.backward();
		rightMotor.forward();
	}

	/**
	 * Turn an angle from the current angle of the robot
	 * 
	 * @param theta
	 *            : how much should the robot turn
	 */
	public void turn(double theta) {
		turnTo(odometer.getTheta() + theta);
	}

	/**
	 * Set the robot destination to its current position. Stop any motion of the
	 * robot.
	 */
	public void stopRobot() {
		xDestination = odometer.getX();
		yDestination = odometer.getY();
		leftMotor.stop(true);
		rightMotor.stop();
	}

	/**
	 * Set the left and right wheel of the robot so that it adjust the motion.
	 * Negative factor will make robot turn left and positive factor will make
	 * robot turn right. Zero is straight motion. The larger the factor is, the
	 * larger angular velocity of the robot is.
	 * 
	 * @param factor
	 *            : the factor that control wheel speeds difference
	 */
	private void adjust(double factor) {// Turn left --> factor > 0, turn right
		int leftSpeed = (int) (SPEED_NORMAL - 30 * Math.signum(factor) - factor);
		int rightSpeed = (int) (SPEED_NORMAL + 30 * Math.signum(factor) + factor);

		leftMotor.forward();
		rightMotor.forward();

		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
	}

	/**
	 * Set speed of both wheel of the robot
	 * 
	 * @param speed
	 *            : speed of robot's left AND right wheel
	 */
	public void setSpeed(int speed) {
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		this.speed = speed;
	}

	/**
	 * Calculate the angle created by the segment joining the destination and the robot's position, and the x axis.
	 * @return the angle created by the segment joining the destination and the robot's position, and the x axis 
	 */
	public double calculateAngleDest() {
		return Util.angle(new double[] { odometer.getX(), odometer.getY() }, new double[] { xDestination, yDestination });
	}

	/**
	 * Retrieve robot's current destination in point format
	 * @return robot's current destination in point format
	 */
	public Point getDestination() {
		return new Point(xDestination, yDestination);
	}

	/**
	 * Change the stopping threshold for the driver. If the robot is stopThreshold away from the destination,
	 * WheelDriver will stop attempting to drive the robot.
	 * @param stopThreshold new stopThreshold. Recommended values are SMALL_STOP_THRESHOLD and LARGE_STOP_THRESHOLD
	 */
	public void setStopThreshold(double stopThreshold) {
		this.stopThreshold = stopThreshold;
	}
	
	/**
	 * Memento implementation to save destination. 
	 * @return current destination information
	 */
	public DestinationInfo saveDestination() {
		return new DestinationInfo(xDestination, yDestination);
	}

	/**
	 * Memento implementation to load destination.
	 * @param info a saved destination information in the past
	 */
	public void retrieveDestination(DestinationInfo info) {
		info.retrieve(this);
	}

	/**
	 * Memento implementation to save destination.
	 * @author ptruon4
	 *
	 */
	public static class DestinationInfo {
		private double x, y;

		private DestinationInfo(double x, double y) {
			this.x = x;
			this.y = y;
		}

		private void retrieve(WheelDriver container) {
			container.travelTo(x, y);
		}
	}

	/**
	 * 
	 * @return if the robot has reached its destination.
	 */
	public boolean isNavigating() {
		return (travelling) || (turning);
	}

	/**
	 * 
	 * @return if the robot has finish turning
	 */
	public boolean isTurning() {
		return turning;
	}

	/**
	 * 
	 * @param radius
	 *            : wheel radius
	 * @param distance
	 *            : how much the wheel would go
	 * @return how much the wheel should turn to go forward distance
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * Calculate how much the robot wheel should turn for the robot to achieve the desired angle in the end
	 * @param radius wheel radius
	 * @param width width between two wheels of the robot
	 * @param angle the angle that the robot needs to turn
	 * @return the amount that the wheel should turn
	 */
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}