/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.host.messages;

import gems.charon.host.messages.Bundle;
import java.util.Comparator;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class BundleAgeComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        if (o1 == o2) {
            return 0;
        }

        long timestamp1 = ((Bundle) o1).getTimestamp();
        long timestamp2 = ((Bundle) o2).getTimestamp();

        if (timestamp1 < timestamp2) {
            return -1;
        } else if (timestamp1 > timestamp2) {
            return 1;
        }

        return 0;
    }
}
