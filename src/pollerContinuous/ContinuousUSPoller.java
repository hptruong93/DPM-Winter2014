package pollerContinuous;
import odometry.Odometer;
import lejos.nxt.UltrasonicSensor;

/**
 * ContinuousUSPoller.java
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
 * Continuous poller front poller with ultrasonic sensor as data reader. 
 *
 */
public class ContinuousUSPoller extends ContinuousPoller {

	private static final int PERIOD = 50;
	protected UltrasonicSensor ultraSonicSensor;

	public ContinuousUSPoller(UltrasonicSensor us, Odometer odometer) {
		super(odometer);
		this.ultraSonicSensor = us;
	}

	@Override
	protected int readValue() {
		return ultraSonicSensor.getDistance();
	}
	
	@Override
	protected int getPeriod() {
		return PERIOD;
	}
}
