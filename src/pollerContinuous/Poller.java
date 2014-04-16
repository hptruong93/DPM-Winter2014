package pollerContinuous;
import main.ReactiveThread;

/**
 * Poller.java
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
 * This is an abstract description of a Poller.
 *
 */
public abstract class Poller extends ReactiveThread {
	
	/**
	 * Describe in abstract manner the main activities of a poller.
	 */
	@Override
	public final void run() {
		while (true) {
			if (enable) {
				nextState();
				pollActivities();
			}

			try {
				Thread.sleep(getPeriod());
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Next turning state of the sensor to poll.
	 * Return when the sensor is in its next state.
	 */
	protected abstract void nextState();
	
	/**
	 * Describe the polling and processing read values.
	 */
	protected abstract void pollActivities();
	
	/**
	 * 
	 * @return How often does this Poller works in milliseconds.
	 */
	protected abstract int getPeriod();
	
	/**
	 * @deprecated
	 * @return value read by the sensor of the poller
	 */
	protected abstract int readValue();
}
