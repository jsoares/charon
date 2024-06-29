/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.time;

import gems.charon.utils.Config;

/**
 * Maintains a network-wide reference time. This is a minimal effort implementation,
 * ignoring drift effects or synchronization error
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class TimeKeeper {

    /**
     * Additional tolerance to prevent useless updates
     */
    private final static int AGE_TOLERANCE = 100;
    /**
     * Set to true if reference obtained
     */
    private static boolean set = false;
    /**
     * Difference between local and global times
     */
    private static long delta = 0;
    /**
     * Synchronization age at synchronization time
     */
    private static int age = 0;
    /**
     * Timestamp of last synchronization
     */
    private static long setTime = 0;

    /**
     * Prevents instantiation
     */
    private TimeKeeper() {
    }

    /**
     * Updates the time reference with new data
     * @param remoteTime new reference time
     * @param remoteAge new reference age
     */
    public static synchronized void updateTime(long remoteTime, int remoteAge) {
        long time = System.currentTimeMillis();
        if (remoteTime < 0 || remoteAge < 0) {
            return;
        }
        if (!set || (remoteAge + Config.STEP_PENALTY + AGE_TOLERANCE < age + (time - setTime))) {
            setTime = time;
            age = remoteAge + Config.STEP_PENALTY;
            delta = remoteTime - setTime;
            set = true;
        }
    }

    /**
     * Returns the current global time
     * @return global time
     */
    public static synchronized long getTime() {
        if (!set) {
            return -1;
        }
        return System.currentTimeMillis() + delta;
    }

    /**
     * Returns the current reference age
     * @return reference's age
     */
    public static synchronized int getAge() {
        if (!set) {
            return -1;
        }
        return age + (int) (System.currentTimeMillis() - setTime);
    }

    public static long localToGlobal(long localTime) {
        if (!set) {
            return -1;
        }
        return localTime + delta;
    }

    public static long globalToLocal(long globalTime) {
        if (!set) {
            return -1;
        }
        return globalTime - delta;
    }

    /**
     * Returns true if synchronized
     * @return true if synchronized
     */
    public static synchronized boolean isSet() {
        return set;
    }
}
