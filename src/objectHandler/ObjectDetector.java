package objectHandler;

import lejos.nxt.ColorSensor.Color;
import utilities.Display;
import data.ColorData;
import data.DataProcessor;
import data.DataProvider;

/**
 * ObjectDetector.java
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
 * Distinguish objects with different colors: cyan, yellow, blue, red, white, and block obstacle.
 * The detector requires several attempts to be able
 * to confidently decide the type of an object. If the process is taking too long, the detector concludes
 * that it is seeing air.
 * 
 * The only limitation known is that the object detector cannot distinguish between
 * empty space and WHITE block.
 *
 */
public class ObjectDetector implements DataProcessor {

	public static final int RANGE = 12;
	public static final int UNKNOWN = -2;
	public static final int DETECTING = -1;
	public static final int CYAN = 0;
	public static final int YELLOW = 1;
	public static final int BLUE = 2;
	public static final int RED = 3;
	public static final int WHITE = 4;
	public static final int AIR = 5;
	public static final int BLOCK = 6;
	
	private byte[] count;
	private static final String[] TYPE_NAMES = { "CYAN", "YELLOW", "BLUE", "RED", "WHITE", "AIR", "BLOCK" };

	private static final int CONFIDENCE_LEVEL = 5;
	private static final int DETERMINING_TIME = 20;

	private int type, timeTakenToDetermine;

	public ObjectDetector() {
		type = DETECTING;
		count = new byte[TYPE_NAMES.length];
	}

	/**
	 * The robot receives ColorData, then use some ratios between red, green and blue values to
	 * increase its confidence level about what it is detecting. Once the confidence level of a type
	 * is greater than the threshold, the detector will decide the type. If it is taking to long to determine,
	 * the detector will put UNKNOWN as the type. Otherwise client will receive DETECTING as the type.
	 */
	@Override
	public void handleData(DataProvider data) {
		try {
			timeTakenToDetermine++;

			//If the detector takes to long do decide, it will answer BLOCK
			if (timeTakenToDetermine >= DETERMINING_TIME) {
				resetType();
				type = UNKNOWN;
				return;
			}

			Color read = ((ColorData) data).getData();
			int red = read.getRed();
			int green = read.getGreen();
			int blue = read.getBlue();
			
			// Print debug
//			Display.printDebug("R  " + red, 2);
//			Display.printDebug("G  " + green, 3);
//			Display.printDebug("B  " + blue, 4);

			//competition floor color value changes	
			int sum = red + green + blue;
			double redSum = (double) red / sum;
//			double greenSum = (double) green / sum; //Not used
			double blueSum = (double)blue / sum;

//			double redGreen = (double)red / green; //Not used
//			double greenBlue = (double)green / blue; //Not used
			double redBlue = (double)red / blue;
			
			int select = DETECTING;
			if (redSum > 0.42) {
				select = RED;
			} else if (blueSum > 0.36 && redSum < 0.3) {
				select = BLUE;
			} else if (blueSum < 0.315) {
				 select = YELLOW;
			} else if (redBlue < 1.032) {
				select = CYAN;
			} else {
				select = WHITE;
			}
			//end competition floor changes
			
			//For debugging purpose
//			if (select != DETECTING) {
//				Display.printDebug(TYPE_NAMES[select], 5);
//			} else {
//				Display.printDebug("DETECTING", 5);
//			}
			
			count[select]++;
			if (count[select] > CONFIDENCE_LEVEL) {
				type = select;
			} else {
				type = DETECTING;
			}
		} catch (Exception e) {
			Display.printDebug("Cast error!", 7);
			return;
		}
	}

	/**
	 * Reset the object detection process.
	 */
	public void resetType() {
		for (int i = 0; i < count.length; i++) {
			count[i] = 0;
		}
		type = DETECTING;
		timeTakenToDetermine = 0;
	}

	/**
	 * 
	 * @return current type that the detector is detecting
	 */
	public int getType() {
		return type;
	}
}