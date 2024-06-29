/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gems.charon.host.utils;

import com.sun.spot.util.IEEEAddress;

/**
 * Provides methods to convert useful data to readable strings
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class Printer {
    /**
     * Returns a short string, representing the last 4 chars of a node's address
     * @param address node's address in string form
     * @return last 4 characters of the address
     */
    public static String getShortAddress(String address) {
        return address.substring(15);
    }
    
    /**
     * Returns a short string, representing the last 4 chars of a node's address
     * @param address node's address in numeric form
     * @return last 4 characters of the address
     */
    public static String getShortAddress(long address) {
        return getShortAddress(IEEEAddress.toDottedHex(address));
    }
}
