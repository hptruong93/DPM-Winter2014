package pollerWithHandler;

import lejos.nxt.Sound;
import pollerContinuous.Poller;
import data.DataProcessor;
import data.DataProvider;

/**
 * PollerWithHandler.java
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
 * Abstract class for Poller with Handler.
 * Poller will have a DataProcessor that it will pass the
 * data to. Each poller of this type will have a different type of data to
 * be passed. Therefore it is important that the DataProcessor
 * knows where it is receiving data from to process correctly.
 * This class will pass on DualUSData to the controller
 *
 */
public abstract class PollerWithHandler extends Poller {

	private final Object lock;
	protected DataProcessor handler;
	
	public PollerWithHandler(DataProcessor processor) {
		lock = new Object();
		this.handler = processor;
	}
	
	@Override
	protected final void pollActivities() {
		synchronized (lock) {
			try {
			handler.handleData(getData());
			} catch (Exception e) {
				Sound.twoBeeps();
				Sound.twoBeeps();
				Sound.twoBeeps();
				Sound.twoBeeps();
			}
		}
	}
	
	/**
	 * Change the unit that will process data received from this poller
	 * @param newManager the new unit that will process data collected from this poller
	 */
	public void changeDataProcessor(DataProcessor newManager) {
		this.handler = newManager;
	}
	
	/**
	 * Abstract method. The implementation depends on the type of sensor/ system of sensors
	 * used.
	 * @return a DataProvider containing all the information that the Poller want to provide
	 */
	public abstract DataProvider getData();
	
	@Override
	protected final int readValue() {
		//Backward compatible so we keep it here, but not meant to be used
		throw new IllegalStateException("Cannot use this method for this Poller!");
	}
}
