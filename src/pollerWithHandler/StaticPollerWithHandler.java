package pollerWithHandler;

import data.DataProcessor;

/**
 * StaticPollerWithHandler.java
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
 * This describes an abstract static poller which does not move.
 * The poller will use the inherited method public Object getData()
 * to pass data to the processor. Do NOT use the inherited method
 * protected final int readValue(). This is legacy code and has to be kept
 * because for backward compatibility.
 *
 */
public abstract class StaticPollerWithHandler extends PollerWithHandler {
	public StaticPollerWithHandler(DataProcessor processor) {
		super(processor);
	}

	@Override
	protected final void nextState() {
		//Static so do nothing
	}
}
