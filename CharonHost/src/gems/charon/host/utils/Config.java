/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.host.utils;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class Config {

    /**
     * Communication port for beacons
     */
    public final static int BEACON_PORT = 101;
    /**
     * Communication port for messages
     */
    public final static int MESSAGE_PORT = 102;
    /**
     * Basestation beacon period
     */
    public final static int BS_BEACON_PERIOD = 100;
    /**
     * Do not deliver duplicates to the applications
     */
    public static boolean PREVENT_DUPLICATES = true;
    /**
     * Debug level
     */
    public final static byte DEBUG_LEVEL = Debug.OFF;
}
