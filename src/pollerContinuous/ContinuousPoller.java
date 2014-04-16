package pollerContinuous;

import java.util.ArrayList;

import odometry.Odometer;

/**
 * ContinuousPoller.java
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
 * Continuous poller will have its own sensor and continuously read from that sensor.
 * It will also save the angle at which it reads the value and all the values. 
 */
public abstract class ContinuousPoller extends Poller {

	protected ArrayList<Integer> results;
	protected ArrayList<Double> angle;
	
	protected Odometer odometer;
	protected Object lock;
	
	public ContinuousPoller(Odometer odometer) {
		this.odometer = odometer;
		this.lock = new Object();
		this.results = new ArrayList<Integer>();
		this.angle = new ArrayList<Double>();
	}
	
	/**
	 * Read and save data
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void pollActivities() {
		synchronized (lock) {
			results.add(readValue());
			angle.add(odometer.getTheta());
		}
	}

	/**
	 * Clear the data read by the poller.
	 */
	public final void clearData() {
		synchronized (lock) {
			results.clear();
			angle.clear();
		}
	}
	
	/**
	 * 
	 * @return the results arraylist. Client must not alter the data.
	 */
	public ArrayList<Integer> getResults() {
		return results;
	}
	
	/**
	 * 
	 * @return the angle arraylist. Client must not alter the data.
	 */
	public ArrayList<Double> getAngles() {
		return angle;
	}
	
	@Override
	protected final void nextState() {
		//Has only one state
	}
}
