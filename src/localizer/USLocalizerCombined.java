package localizer;

/*
 * Author :		Juan Morency Trudel 	260481762
 * 				Vlad Kesler 			260501714	
 * Name : 		USLocalizer.java
 * Date : 		02/27/2014
 * Context:		ECSE 211 - Design Principles and Methods
 * Description: This class uses a wall corner to orient itself with the ultrasonic sensor
 * 				It only works properly when the corner is within a 30 cm x and y distance from the robot
 *				It also contains a method to mesure the angular width of an object.
 * 
 */

import lejos.nxt.UltrasonicSensor;
import odometry.Odometer;
import odometry.WheelDriverAdapter;


public class USLocalizerCombined {

	// variable declaration
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	public static double ROTATION_SPEED = 30;
	public static double ROTATION_SPEED_SLOW = 30;
	public final static int MARGIN_WALL = 2;
	public final static int MAX_DISTANCE_WALL = 35;
	private int filterControl, FILTER_OUT = 1;
	private int distance;
	private int initialX = 0, initialY = 0;

	// objects instanciation
	private Odometer odo;
	private WheelDriverAdapter driver;
	private UltrasonicSensor us;
	private LocalizationType locType;

	// constructor
	public USLocalizerCombined(Odometer odo, WheelDriverAdapter driver, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.us = us;
		this.locType = locType;
		this.driver = driver;
		// switch off the ultrasonic sensor
		us.off();
	}
	
	
	/**
	 * This is the method to call to actually do the localization
	 */
	public void doLocalization() {
//		int lineCount = 2; //Debug
		double angleA, angleB, angleAtemp1, angleBtemp1, angleAtemp2, angleBtemp2, newAngle;

		// sleep for 500ms for the sensor to get past the initial filter and get
		// a distance value
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}

		
		/*
		 * The robot should turn until it sees the wall, then look for the
		 * "rising edges:" the points where it no longer sees the wall.
		 */
		if (locType == LocalizationType.RISING_EDGE) {

			// rotate the robot until it sees a wall
			while (getFilteredData() >= MAX_DISTANCE_WALL + MARGIN_WALL) {
				driver.setRotationSpeed(ROTATION_SPEED);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
			// keep rotating until the robot sees no wall, then latch the angle
			while (getFilteredData() <= MAX_DISTANCE_WALL - MARGIN_WALL) {
				driver.setRotationSpeed(ROTATION_SPEED);
			}

			angleAtemp1 = odo.getThetaInDegree();
			while (getFilteredData() <= MAX_DISTANCE_WALL + MARGIN_WALL) {
				driver.setRotationSpeed(ROTATION_SPEED);
			}
			driver.setRotationSpeed(0);
			angleAtemp2 = odo.getThetaInDegree();

			// does a mean of the two recorded angles
			angleA = (angleAtemp1 + angleAtemp2) / 2;
			
			//Display.printDebug(Util.doubleToString(angleA, 2), lineCount++); //Debug
			// for display

			// switch direction and wait until it sees a wall
			while (getFilteredData() >= MAX_DISTANCE_WALL + MARGIN_WALL) {
				driver.setRotationSpeed(-ROTATION_SPEED);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}

			// keep rotating until the robot sees no wall, then latch the angle
			while (getFilteredData() <= MAX_DISTANCE_WALL - MARGIN_WALL) {
				driver.setRotationSpeed(-ROTATION_SPEED);
			}

			angleBtemp1 = odo.getThetaInDegree();
			while (getFilteredData() <= MAX_DISTANCE_WALL + MARGIN_WALL) {
				driver.setRotationSpeed(-ROTATION_SPEED);
			}
			driver.setRotationSpeed(0);
			angleBtemp2 = odo.getTheta();
			
			// does a mean of the two recorded angles
			angleB = (angleBtemp1 + angleBtemp2) / 2;
			


			if (angleA < angleB) {
				newAngle = 234 - Math.abs(angleA - (angleB - 360)) / 2;

			}

			else {
				newAngle = 234 - Math.abs(angleA - angleB) / 2;
				// check for negative values
			}
			if (newAngle < 0) {
				newAngle += 360;
			}

			// update the odometer position (example to follow:)
			odo.setPosition(new double[] { initialX, initialY, Math.toRadians(newAngle - 11) }, new boolean[] { true, true, true });

		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "falling edges:" the points where it no longer sees the wall.
			 * This is very similar to the RISING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			// rotate the robot until it sees no wall
			while (getFilteredData() < MAX_DISTANCE_WALL + MARGIN_WALL) {
				driver.setRotationSpeed(ROTATION_SPEED);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
			// keep rotating until the robot sees a wall, then latch the angle
			while (getFilteredData() >= MAX_DISTANCE_WALL + MARGIN_WALL) {
				driver.setRotationSpeed(ROTATION_SPEED);
			}

			angleAtemp1 = odo.getTheta();
			while (getFilteredData() >= MAX_DISTANCE_WALL - MARGIN_WALL) {
				driver.setRotationSpeed(ROTATION_SPEED);
			}
			driver.setRotationSpeed(0);
			angleAtemp2 = odo.getThetaInDegree();

			angleA = (angleAtemp1 + angleAtemp2) / 2;

			// switch direction and wait until it sees no wall
			while (getFilteredData() <= MAX_DISTANCE_WALL + MARGIN_WALL) {
				driver.setRotationSpeed(-ROTATION_SPEED);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}

			// keep rotating until the robot sees a wall, then latch the angle
			while (getFilteredData() >= MAX_DISTANCE_WALL + MARGIN_WALL) {
				driver.setRotationSpeed(-ROTATION_SPEED);
			}
			angleBtemp1 = odo.getThetaInDegree();

			while (getFilteredData() >= MAX_DISTANCE_WALL - MARGIN_WALL) {
				driver.setRotationSpeed(-ROTATION_SPEED);
			}
			driver.setRotationSpeed(0);
			angleBtemp2 = odo.getThetaInDegree();

			angleB = (angleBtemp1 + angleBtemp2) / 2;
			
//			Display.printDebug(Util.doubleToString(angleB, 2), lineCount++); //Debug
			// angleA is clockwise from angleB, so assume the average of the


			if (angleA < angleB) {
				newAngle = 50 - Math.abs(angleA - (angleB - 360)) / 2;

			}

			else {
				newAngle = 50 - Math.abs(angleA - angleB) / 2;
				// check for negative values
			}
			if (newAngle < 0) {
				newAngle += 360;
			}

			// update the odometer position (example to follow:)
			odo.setPosition(new double[] { initialX, initialY, Math.toRadians(newAngle + 30) - .05 }, new boolean[] { true, true, true });

			driver.setRotationSpeed(0);

		}
	}

	/**
	 * This method is used to get the data from the ultrasonic sensors.
	 * has a simple integrated filter
	 */
	public int getFilteredData() {

		int distance;

		// do a ping
		us.ping();

		// wait for the ping to complete
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}

		// there will be a delay here
		distance = us.getDistance();

		// rudimentary filter
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance == 255) {
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		this.distance = distance;
		return this.distance;
	}

	/**
	 * This method is to access the value of the distance form the US from other classes. 
	 */
	
	public int getDistance() {
		return this.distance;
	}


}