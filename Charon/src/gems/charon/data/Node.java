/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.data;

import gems.charon.utils.Config;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class Node {

    long address;
    long edd = 0;
    long ict = Config.DEFAULT_ICT;
    long lastContact = 0;
    boolean baseStation;

    public Node(long address, boolean baseStation) {
        this.address = address;
        this.baseStation = baseStation;
    }

    public boolean isBaseStation() {
        return baseStation;
    }

    public long getAddress() {
        return this.address;
    }

    public void registerContact(long currentTime, long newEDD) {
        if (lastContact != 0) {
            ict = ((ict * (10 - Config.ICT_UPDATE_WEIGHT)) + ((currentTime - lastContact) * Config.ICT_UPDATE_WEIGHT)) / 10;
        }
        edd = newEDD;
        lastContact = currentTime;
    }

    public long getEDD() {
        return edd;
    }

    public long getICT() {
        return ict;
    }

    public long getLastContact() {
        return lastContact;
    }
}
