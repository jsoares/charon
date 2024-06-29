/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.qos;

/**
 *
 * @author mindstorm
 */
public class ZombieData implements ServiceClass {

    public boolean allowMultipleCopies() {
        return false;
    }

    public boolean useZombies() {
        return false;
    }

    public long getTTL() {
        return 43200000;
    }

    public long getScore(int capacity, int battery, long edd) {
        return 0;
    }
}
