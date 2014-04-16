package utilities;

/**
 * Display.java
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
import odometry.Odometer;
import lejos.nxt.LCD;

/**
 * 
 * @author ptruon4 
 * This class acts as a debug class. Use this class by the
 * static accessor public static void printDebug() to print message to
 * screen. Default refresh rate is DISPLAY_PERIOD ms
 */
public class Display extends Thread {
	private static final long DISPLAY_PERIOD = 500;
	private Odometer odometer;
	private static String[] debug;
	private static final Object lock = new Object();

	public Display(Odometer odometer) {
		this.odometer = odometer;
		debug = new String[8];
		for (int i = 0; i < debug.length; i++) {
			debug[i] = " ";
		}
	}

	/**
	 * Regularly clear the screen and print out everything in debug buffer line
	 * by line Special values are x, y and theta obtained from the odometer. See
	 * method printDebug for lines reserved for odometry information.
	 */
	public void run() {
		long displayStart, displayEnd;
		double[] position = new double[3];

		// clear the display once
		LCD.clearDisplay();

		while (true) {
			displayStart = System.currentTimeMillis();

			LCD.clearDisplay();

			for (int i = 2; i < debug.length; i++) {
				LCD.drawString(debug[i], 0, i);
			}

			// get the odometry information
			odometer.getPosition(position, new boolean[] { true, true, true });

			// display odometry information
			LCD.drawString(Util.doubleToString(position[0], 2) + ", " + Util.doubleToString(position[1], 2), 0, 0);
			LCD.drawString("T: " + Util.doubleToString(Math.toDegrees(position[2]), 2), 0, 1);

			// throttle the OdometryDisplay
			displayEnd = System.currentTimeMillis();
			if (displayEnd - displayStart < DISPLAY_PERIOD) {
				try {
					Thread.sleep(DISPLAY_PERIOD - (displayEnd - displayStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that OdometryDisplay will be interrupted
					// by another thread
				}
			}
		}
	}

	/**
	 * Use this method to write message to LCD screen.
	 * 
	 * @param content
	 *            message that will be written on LCD
	 * @param line
	 *            line 2 - 7 on LCD screen. Line 0 & 1 are reserved for odometer
	 *            information.
	 */
	public static void printDebug(String content, int line) {
		synchronized (lock) {
			debug[line] = content + "";
		}
	}
}