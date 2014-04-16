package testing;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.RS485;
import lejos.nxt.remote.RemoteNXT;
import utilities.Hardware;

/**
 * TestSlave.java
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
 * Testing connection between two bricks.
 * Receiver.java has to be run previously on the slave brick.
 * Once this starts, it will immediately try to connect to the slave brick through RS485
 * 
 * Two beeps followed by two beeps --> Successfully connected
 * Two beeps --> Error in connection
 * 
 * @author ptruon4
 *
 */
public class TestSlave {

	public static void main(String[] args) {
		initSlave();

		Sound.twoBeeps();
		Sound.twoBeeps();
		while (Button.waitForAnyPress() != Button.ID_ENTER)
			;
		System.exit(0);
	}

	/**
	 * Init the slave ports. 1 is the master nxt & 2 is the slave nxt. See
	 * Hardware.init() for slave ports.
	 */
	private static void initSlave() {
		try {
			RemoteNXT nxt = new RemoteNXT("TEAM16-2", RS485.getConnector());
			Thread.sleep(1000);
			Hardware.init(nxt);
		} catch (IOException ex) {
			System.exit(1);
		} catch (InterruptedException e) {
			System.exit(1);
		}
	}
}