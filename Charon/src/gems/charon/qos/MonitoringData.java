/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.qos;

/**
 *
 * @author Jorge Soares
 */
public class MonitoringData implements ServiceClass {

    public boolean allowMultipleCopies() {
        return false;
    }

    public boolean useZombies() {
        return true;
    }

    public long getTTL() {
        return 259200000; // 3 days
    }

    public long getScore(int capacity, int battery, long edd) {
        if (capacity < 20) {
            // Reserve some space for alarms and own data
            return Integer.MIN_VALUE;
        }
        return -edd + capacity + battery;
    }
}
