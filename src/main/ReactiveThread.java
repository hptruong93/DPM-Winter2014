package main;
/**
 * ReactiveThread.java
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
 * Abstract description of a ReactiveThread. See below for definition of a reactive thread
 *
 */
public abstract class ReactiveThread extends Thread {

	/**
	 * Definition: ReactiveThreads are those which can be enabled and disabled.
	 */
	protected boolean enable;
	
	@Override
	public abstract void run();
	
	/**
	 * Enable or disable a reactive thread
	 * @param enable reactive status
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	/** 
	 * Check if thread is enabled or not
	 * @return if thread is enabled or not
	 */
	public final boolean isEnable() {
		return enable;
	}
}
