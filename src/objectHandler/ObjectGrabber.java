package objectHandler;

import odometry.WheelDriver;
import lejos.robotics.RegulatedMotor;

/**
 * ObjectGrabber.java
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
 * Detail description of how the object grabber works.
 * 
 */
public class ObjectGrabber implements ObjectHandler {

	private static final int DEFAULT_SPEED_ARM = 50;
	private static final int DEFAULT_SPEED_HAND = 40;
	private static final int WAIT_TIME = 1500;
	private static final int SWEEP_ROTATION = 110;
	private static final int ARM_ROTATION = SWEEP_ROTATION + 30;
	private RegulatedMotor hand, arm;
	private WheelDriver wheelDriver;
	private boolean finished;
	private boolean first;
	private int originalHandPosition;

	public ObjectGrabber(RegulatedMotor arm, RegulatedMotor hand,
			WheelDriver wheelDriver) {
		this.arm = arm;
		this.hand = hand;
		this.wheelDriver = wheelDriver;

		arm.setSpeed(DEFAULT_SPEED_ARM);
		hand.setSpeed(DEFAULT_SPEED_HAND);

		first = true;
		originalHandPosition = hand.getTachoCount();
		// originalArmPosition = arm.getTachoCount();
	}

	/**
	 * Robot will turn left 30 degree, reach arm down, go back a small distance,
	 * turn right 45 degree, and go back another small distance, then
	 * attempt at grabbing the object. If wheelDriver is null, there will be no
	 * motion of the robot, only arm and hand motions are involved.
	 */
	@Override
	public void handleObject() {
		finished = false;
		//hand.setStallThreshold(5, 100);

		if (wheelDriver != null) {
			wheelDriver.turn(Math.toRadians(30));
		}

		hand.stop();
		openHand();

		if (wheelDriver != null) {
			arm.rotate(SWEEP_ROTATION);
			wheelDriver.setSpeed(WheelDriver.SPEED_SLOW);
			wheelDriver.turn(-Math.toRadians(45));
			wheelDriver.setSpeed(WheelDriver.SPEED_NORMAL);
			arm.rotate(ARM_ROTATION - SWEEP_ROTATION);
			wheelDriver.backward(2);
		} else {
			arm.rotate(ARM_ROTATION);
		}

		hand.rotate(-100, true);
		try {
			Thread.sleep(WAIT_TIME);
		} catch (InterruptedException e) {
		}

		if (wheelDriver != null) {
			wheelDriver.backward(8);
		}

		arm.rotate(-ARM_ROTATION);
		arm.stop();
		arm.flt();

		finished = true;
	}

	/**
	 * Release the object if the claw is holding one. If not, this simply open the claw.
	 * @see openHand() method
	 */
	@Override
	public void releaseObject() {
		finished = false;

		arm.rotate(90);

		openHand();
		try {
			Thread.sleep(WAIT_TIME);
		} catch (InterruptedException e) {
		}

		arm.rotate(-90);
		arm.stop();
		arm.flt();
		finished = true;
	}

	/**
	 * Return if the grabber has finished handling the object
	 */
	@Override
	public boolean finishCurrentTask() {
		return finished;
	}

	/**
	 * This method open the hand to its original position.
	 */
	private void openHand() {
		if (!first) {
			hand.forward();
			while (Math.abs(hand.getTachoCount() - originalHandPosition + 5) > 5) {
			}
			hand.stop();
		} else {
			first = false;
		}
	}
}