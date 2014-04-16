package data;

/**
 * DualUSData.java
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
 * Encapsulate US reading read by both left and right us sensors.
 * This should be used by the PathPlanner (CoordinateManager) to plan robot's next motion.
 */

public class DualUSData implements DataProvider {

	public static final int LEFT_SIDE = 0;
	public static final int RIGHT_SIDE = 1;
	private DualUSPackage data;
	
	public DualUSData(int reading, int side) {
		data = new DualUSPackage(reading, side);
	}
	
	@Override
	public DualUSPackage getData() {
		return data;
	}

	public static class DualUSPackage {
		
		private int reading;
		private int side;
		
		private DualUSPackage(int reading, int side) {
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