package data;

import lejos.nxt.ColorSensor.Color;

/**
 * ColorData.java
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
* Encapsulation of ColorData Data
* Contains the following information:
* 1) Color object read from the color sensor
* 
* This is usually used to pass three components red,
* green, blue, but more information can be encapsulated in the
* object Color
*/
public class ColorData implements DataProvider {

	Color data;
	public ColorData(Color data) {
		this.data = data;
	}
	
	@Override
	public Color getData() {
		return data;
	}
}