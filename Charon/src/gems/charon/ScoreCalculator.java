/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon;

import gems.charon.messages.Beacon;
import gems.charon.qos.ServiceClassManager;
import gems.charon.utils.Config;

/**
 *
 * @author Jorge Soares
 */
public class ScoreCalculator {

    protected static long getScore(int serviceClass, Beacon beacon) {
        if (beacon.isBaseStation()) {
            return Integer.MAX_VALUE;
        }
        return getScore(serviceClass, beacon.getCapacity(),
                beacon.getPower(), beacon.getEDD());
    }

    protected static long getScore(int serviceClass, int capacity,
            int battery, long edd) {
        // Return bad score if no space left
        if (capacity / Config.CAPACITY_SHARE <= Config.MIN_CAPACITY) {
            return Integer.MIN_VALUE;
        }
        return ServiceClassManager.getClass(serviceClass).getScore(capacity, battery, edd);
    }
}
