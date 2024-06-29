/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.utils;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class Config {
    //
    // Ports
    //

    /**
     * Communication port for beacons
     */
    public final static int BEACON_PORT = 101;
    /**
     * Communication port for messages
     */
    public final static int MESSAGE_PORT = 102;
    //
    // Other times
    //
    /**
     * Initial Inter Contact Time
     */
    public final static long DEFAULT_ICT = 259200000; // 3 days
    /**
     * Inter Contact Time update speed (1-10, WMA weight)
     */
    public final static int ICT_UPDATE_WEIGHT = 3; // 3 days    
    /**
     * Beacon period
     */
    public final static int BEACON_PERIOD = 500; // 1 second
    /**
     * Path expiration (2.5*BEACON_PERIOD)
     */
    public final static int PATH_EXPIRATION = 3 * BEACON_PERIOD;
    /**
     * Minimum interval between contacts to be considered for routing metric
     */
    public final static long MIN_CONTACT_INTERVAL = 10000;
    //
    // Other parameters
    //
    /**
     * Buffer capacity (# of bundles)
     */
    public final static int BUFFER_CAPACITY = 100;
    /**
     * Buffer cleanup interval
     */
    public final static int BUFFER_CLEANUP = 30000;
    //
    // Timekeeping & rounds
    //
    /**
     * Penalty for each synchronization step
     */
    public final static int STEP_PENALTY = 3600000;
    /**
     * Use rounds
     */
    public final static boolean USE_ROUNDS = true;
    /**
     * Round period
     */
    public final static int ROUND_PERIOD = 15000;
    /**
     * Round duration
     */
    public final static int ROUND_DURATION = 1500;
    /**
     * LED use switch
     */
    public final static boolean USE_LEDS = true;
    /**
     * Minimum allowed capacity on a destination node
     */
    public final static short MIN_CAPACITY = 2;
    /**
     * Slots reserved for alarm and own data
     */
    public final static short RESERVE_CAPACITY = 20;
    /**
     * Capacity share factor for route configuration
     */
    public final static short CAPACITY_SHARE = 3;
}
