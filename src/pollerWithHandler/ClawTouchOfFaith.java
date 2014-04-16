package pollerWithHandler;

import lejos.nxt.remote.RemoteMotor;
import lejos.robotics.RegulatedMotor;

/**
 * ClawTouchOfFaith.java
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
 * Specific implementation of TestOfFaith on the Hardware.arm motor.
 * @see TestOfFaith.java for detailed descriptions
 *
 */
public class ClawTouchOfFaith {
	
	private static final int SPEED = 50;
	private static final int CHECK_DURATION = 100;
	private static final int TEST_DURATION = 2700;
	private static final int TOLERANCE = 4;
	private RegulatedMotor arm;
	private int tolerance, testDuration;
	
	public ClawTouchOfFaith(RegulatedMotor arm) {
		this.arm = arm;
		this.tolerance = TOLERANCE;
		this.testDuration = TEST_DURATION;
		arm.setSpeed(SPEED);
	}
	
	/**
	 * Customized constructor so that client can specify testing constraints
	 * @param arm the testing motor
	 * @param tolerance tolerance stalling threshold
	 * @param testDuration test duration in milliseconds
	 */
	public ClawTouchOfFaith(RemoteMotor arm, int tolerance, int testDuration) {
		this.arm = arm;
		this.tolerance = tolerance;
		this.testDuration = testDuration;
		arm.setSpeed(SPEED);
	}
	
	/**
	 * Test if there is space in front.
	 * @return if there is nothing in the way of the motor.
	 */
	public boolean testOfFaith() {
		arm.setStallThreshold(tolerance, CHECK_DURATION);
		int original = arm.getTachoCount();
		
		boolean output = true;
		long start = System.currentTimeMillis();
		arm.forward();
		while (true) {
			if (arm.isStalled()) {
				output = false;
				break;
			}
			
			if (System.currentTimeMillis() - start > testDuration) {
				break;
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		arm.stop();
		arm.setStallThreshold(Integer.MAX_VALUE, CHECK_DURATION);
		arm.rotate(-arm.getTachoCount() + original);

		return output;
	}
}
