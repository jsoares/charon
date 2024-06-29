/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.qos;

/**
 *
 * @author Jorge Soares
 */
public class AlarmData implements ServiceClass {

    public boolean allowMultipleCopies() {
        return true;
    }

    public boolean useZombies() {
        return false;
    }

    public long getTTL() {
        return 43200000; // 12h
    }

    public long getScore(int capacity, int battery, long edd) {
        return 0;
    }
}
