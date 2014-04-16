package pollerWithHandler;

import lejos.nxt.remote.RemoteMotor;

/**
 * TestOfFaith.java
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
 * This class provides a mean to use a RegulatedMotor as a touch sensor
 *
 */
public class TestOfFaith {
	
	private static final int SPEED = 50;
	private static final int CHECK_DURATION = 100;
	private static final int TEST_DURATION = 2000;
	private static final int TOLERANCE = 3;
	private RemoteMotor hand;
	
	public TestOfFaith(RemoteMotor hand) {
		this.hand = hand;
		hand.setSpeed(SPEED);
	}
	
	/**
	 * Test if there is space in front.
	 * The idea is to set stall threshold of the motor to be a relative low value.
	 * After that, have the sensor rotated toward the direction of testing. If there are
	 * HEAVY obstacles on the sensor's movement, the sensor will be stalled and obstacle will
	 * be detected.
	 * 
	 * The name comes from the fact that once this test is used, no other sensor (ultrasonic or color) could provide
	 * useful information about the obstacle in front. This is similar to feel the environment without actually sensing it
	 * since other sensors cannot provide any useful information (i.e. the robot cannot sense the environment around it).
	 * Yet it still want to test its faith that its current sensors are reliable and their provided information is correct.
	 * One can rely on his senses, but if this sensing means are so unreliable, he must have a mean to test his faith on them.
	 * 
	 * @return if there is nothing in the way of the motor.
	 */
	public boolean testOfFaith() {
		hand.setStallThreshold(TOLERANCE, CHECK_DURATION);
		int original = hand.getTachoCount();
		
		boolean output = true;
		long start = System.currentTimeMillis();
		hand.backward();
		while (true) {
			if (hand.isStalled()) {
				output = false;
				break;
			}
			
			if (System.currentTimeMillis() - start > TEST_DURATION) {
				break;
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		hand.stop();
		hand.setStallThreshold(Integer.MAX_VALUE, CHECK_DURATION);
		hand.rotate(-hand.getTachoCount() + original);
		return output;
	}
}
