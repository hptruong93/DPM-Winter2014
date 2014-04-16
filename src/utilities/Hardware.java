package utilities;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteNXT;
import lejos.nxt.remote.RemoteSensorPort;

/**
 * Hardware.java
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
 * This class contains all information about the robot hardware. The information in this class
 * should be related to the robot, and clearly distinguished from FieldInfo information.
 * @author ptruon4
 *
 */
public class Hardware {
	
	/**
	 * This is a static class. No instance of this should be created.
	 * This class describes the hardware design of the robot and the sensors/ motor configurations. 
	 */
	private Hardware() {}
	
	public static final float LEFT_WHEEL_RADIUS = 2.02f;
	public static final float RIGHT_WHEEL_RADIUS = LEFT_WHEEL_RADIUS;
	public static float WHEEL_WIDTH = 18.50f;  
	
	public static final float ROBOT_RADIUS = 20;
	
	public static final int BAND_CENTRE = 25;

	/**
	 * Motors
	 */
	public static final NXTRegulatedMotor leftWheel = Motor.B;
	public static final NXTRegulatedMotor rightWheel = Motor.C;
	public static NXTRegulatedMotor arm = Motor.A;
	public static RemoteMotor hand;
	
	/**
	 * UltraSonic sensors information
	 */
	public static RemoteSensorPort leftUltraSonic;
	public static RemoteSensorPort rightUltraSonic;
	public static final float COLOR_SENSOR_TO_CENTRE_ACROSS = 9.8f;
	public static final float COLOR_SENSOR_TO_CENTRE_UP = 4.2f;
	
	/**
	 * Color sensors information
	 */
	public static final SensorPort colorPortFront = SensorPort.S3;
	public static RemoteMotor frontSensorMotor;
	public static SensorPort colorPortLeft = SensorPort.S1;
	public static SensorPort colorPortRight = SensorPort.S2;
	public static final float US_SENSOR_TO_CENTRE_UP = 6.5f;
	public static final float US_SENSOR_TO_CENTRE_ACROSS = 6f;
	
	/**
	 * Initialization once the master is connected to the slave. This method should only be called once in the beginning
	 * when system boots up
	 * @param slave the RemoteNXT that the master has RS485 connection to
	 */
	public static void init(RemoteNXT slave) {
		frontSensorMotor = slave.A;
		hand = slave.B;
		leftUltraSonic = slave.S1;
		rightUltraSonic = slave.S2;
	}
}