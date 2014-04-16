package data;

/**
 * DataProcessor.java
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
* Provide interface for processor to process data acquired from pollers
*/

public interface DataProcessor {
	/**
	* Define how processor should handle data acquired from poller
	*/
	public abstract void handleData(DataProvider data);
}