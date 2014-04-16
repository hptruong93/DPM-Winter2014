package main;

import lejos.nxt.LCD;
import lejos.nxt.comm.LCPResponder;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.RS485;

/**
 * Create an LCP responder to handle LCP requests. Allow the
 * User to choose between Bluetooth, USB and RS485 protocols.
 * 
 * @author Andy Shaw
 *
 */
public class Receiver
{
    /**
     * Our local Responder class so that we can over-ride the standard
     * behaviour. We modify the disconnect action so that the thread will
     * exit.
     */
    static class Responder extends LCPResponder
    {
        Responder(NXTCommConnector con)
        {
            super(con);
        }

        protected void disconnect()
        {
            super.disconnect();
            super.shutdown();
        }
    }

    public static void main(String[] args) throws Exception
    {
        String[] connectionStrings = new String[]{"RS485"};
        NXTCommConnector[] connectors = {RS485.getConnector()};

        int connectionType = 0;
        LCD.clear();
        LCD.clear();
        LCD.drawString("Type: " + connectionStrings[connectionType], 0, 0);
        LCD.drawString("Running...", 0, 1);
        Responder resp = new Responder(connectors[connectionType]);
        resp.start();
        resp.join();
        LCD.drawString("Closing...  ", 0, 1);
    }
}

