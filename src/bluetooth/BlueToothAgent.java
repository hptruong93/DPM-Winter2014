package bluetooth;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import navigator.FlagFinder;
import utilities.FieldInfo;
import utilities.Point;

/**
 * BlueToothAgent.java
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
 * Modular implementation for bluetooth initialization.
 * This class is a static class. No instance of this class should be instantiated
 */
public class BlueToothAgent {
	
	/**
	 * Private constructor to prevent instantiation
	 */
	private BlueToothAgent() {}
	
	/**
	 * Get bluetooth connection and fill in the provided information for FieldInfo
	 */
	public static void init() {
		BluetoothConnection conn = new BluetoothConnection();

		// as of this point the bluetooth connection is closed again, and you
		// can pair to another NXT (or PC) if you wish

		// example usage of Tranmission class
		Transmission t = conn.getTransmission();
		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			FieldInfo.role = t.role;
			// if we're the green robot
			if (FieldInfo.role == PlayerRole.GREEN) {
				FieldInfo.ourFlagZoneLowerLeft_X = t.greenZoneLL_X;
				FieldInfo.ourFlagZoneLowerLeft_Y = t.greenZoneLL_Y;
				FieldInfo.ourFlagZoneUpperRight_X = t.greenZoneUR_X;
				FieldInfo.ourFlagZoneUpperRight_Y = t.greenZoneUR_Y;

				FieldInfo.theirFlagZoneLowerLeft_X = t.redZoneLL_X;
				FieldInfo.theirFlagZoneLowerLeft_Y = t.redZoneLL_Y;
				FieldInfo.theirFlagZoneUpperRight_X = t.redZoneUR_X;
				FieldInfo.theirFlagZoneUpperRight_Y = t.redZoneUR_Y;

				FieldInfo.ourGoalZone_X = t.greenDZone_X;
				FieldInfo.ourGoalZone_Y = t.greenDZone_Y;
				FieldInfo.theirGoalZone_X = t.redDZone_X;
				FieldInfo.theirGoalZone_Y = t.redDZone_Y;
				FieldInfo.ourFlagColor = t.greenFlag;
				FieldInfo.theirFlagColor = t.redFlag;
			}
			// otherwise we're team red
			else if (FieldInfo.role == PlayerRole.RED) {
				FieldInfo.ourFlagZoneLowerLeft_X = t.redZoneLL_X;
				FieldInfo.ourFlagZoneLowerLeft_Y = t.redZoneLL_Y;
				FieldInfo.ourFlagZoneUpperRight_X = t.redZoneUR_X;
				FieldInfo.ourFlagZoneUpperRight_Y = t.redZoneUR_Y;

				FieldInfo.theirFlagZoneLowerLeft_X = t.greenZoneLL_X;
				FieldInfo.theirFlagZoneLowerLeft_Y = t.greenZoneLL_Y;
				FieldInfo.theirFlagZoneLowerLeft_X = t.greenZoneUR_X;
				FieldInfo.theirFlagZoneLowerLeft_Y = t.greenZoneUR_Y;

				FieldInfo.ourGoalZone_X = t.redDZone_X;
				FieldInfo.ourGoalZone_Y = t.redDZone_Y;
				FieldInfo.theirGoalZone_X = t.greenDZone_X;
				FieldInfo.theirGoalZone_Y = t.greenDZone_Y;
				FieldInfo.ourFlagColor = t.redFlag;
				FieldInfo.theirFlagColor = t.greenFlag;
			}
			// otherwise corrupted results
			else {
				LCD.drawString("Corrupt Transmission", 0, 5);
				do {
					Sound.twoBeeps();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.exit(1);
					}
				} while (true);
			}

			FieldInfo.LOWER_LEFT = new Point(FieldInfo.theirFlagZoneLowerLeft_X * FieldInfo.TILE_WIDTH, FieldInfo.theirFlagZoneLowerLeft_Y
					* FieldInfo.TILE_WIDTH);
			FieldInfo.UPPER_RIGHT = new Point(FieldInfo.theirFlagZoneUpperRight_X * FieldInfo.TILE_WIDTH, FieldInfo.theirFlagZoneUpperRight_Y
					* FieldInfo.TILE_WIDTH);
			FieldInfo.DROP_OFF_ZONE = new Point(FieldInfo.ourGoalZone_X + FieldInfo.HALF_TILE, FieldInfo.ourGoalZone_Y + FieldInfo.HALF_TILE);
			FieldInfo.PROHIBITED_ZONE = new Point(FieldInfo.theirGoalZone_X + FieldInfo.HALF_TILE, FieldInfo.theirGoalZone_Y + FieldInfo.HALF_TILE);

			FieldInfo.STARTING_CORNER = t.startingCorner.getId();
			FlagFinder.FLAG_COLOR = FieldInfo.COLOR_TRANSLATION[FieldInfo.ourFlagColor];
		}
	}
}