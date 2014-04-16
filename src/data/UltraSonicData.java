package data;

/**
 * UltraSonicData.java
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
* Encapsulation of UltraSonicData Data
* Contains the following information:
* 1) distance reading of the ultrasonic sensor
*/
public class UltraSonicData implements DataProvider {

	private int reading;
	
	public UltraSonicData(int data) {
		this.reading = data;
	}
	
	@Override
	public Integer getData() {
		return reading;
	}

}
