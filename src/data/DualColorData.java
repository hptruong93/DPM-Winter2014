package data;

/**
 * DualColorData.java
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
 * Encapsulate color reading read by both left and right color sensors.
 * This should be used by the OdometryCorrection to fix robot's movement angle.
 *
 */

public class DualColorData implements DataProvider {

	public static final int LEFT_SIDE = 0;
	public static final int RIGHT_SIDE = 1;
	private DualColorPackage data;
	
	public DualColorData(int value, int side) {
		data = new DualColorPackage(value, side);
	}
	
	@Override
	public DualColorPackage getData() {
		return data;
	}

	public static class DualColorPackage {
		
		private int reading;
		private int side;
		
		private DualColorPackage(int reading, int side) {
			this.reading = reading;
			this.side = side;
		}
		
		public int getReading() {
			return reading;
		}
		
		public int getSide() {
			return side;
		}
	}
}