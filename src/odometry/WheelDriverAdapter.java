package odometry;

/**
 * WheelDriverAdapter.java
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
 * This class is created to connect the existing WheelDriver with Vlad and Juan's US localization code.
 * This merely provides the appropriate methods so that US localization works.
 *
 */
public class WheelDriverAdapter {
	
	private static final int STATIC = 0;
	private static final int COUNTER = 1;
	private static final int CLOCKWISE = 2;
	
	private WheelDriver driver;
	private int state;
	
	public WheelDriverAdapter(WheelDriver driver) {
		this.driver = driver;
		state = STATIC;
	}
	
	/**
	 * Set the robot to turn counter clockwise or clockwise depends on the sign of the parameter.
	 * If the robot is already in the appropriate motion, do nothing
	 * @param angleInDegree the sign of this will determine which direction the robot should turn. Its magnitude does not
	 * affect the turning rate of the robot
	 */
	public void setRotationSpeed(double angleInDegree) {
		int newState;
		if (angleInDegree > 0) {
			newState = COUNTER;
		} else if (angleInDegree == 0) {
			newState = STATIC;
		} else {
			newState = CLOCKWISE;
		}
		
		if (newState != state) {
			driver.stopRobot();
			if (newState != STATIC) {
				if (newState == CLOCKWISE) {
					driver.turnClockWise(WheelDriver.SPEED_NORMAL);
				} else if (newState == COUNTER) {
					driver.turnCounterClockWise(WheelDriver.SPEED_NORMAL);
				}
			}
			state = newState;
		}
	}
}
