/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.data;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class BundleID {

    long address;
    int sequenceNumber;

    public BundleID(long address, int sequenceNumber) {
        this.address = address;
        this.sequenceNumber = sequenceNumber;
    }

    public long getAddress() {
        return this.address;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        BundleID bundle = (BundleID) obj;
        return address == bundle.address && sequenceNumber == bundle.sequenceNumber;
    }

    public int hashCode() {
        int hash = 5484;
        hash = 31 * hash + (int) (address ^ (address >>> 32));
        hash = 31 * hash + sequenceNumber;
        return hash;
    }
    
    public int compareTo(BundleID bundle) {
        if(this.equals(bundle)) {
            return 0;
        }
        if(address == bundle.address) {
            return sequenceNumber - bundle.sequenceNumber;
        }
        return (int) (address - bundle.address);
    }
    
    public String toString() {
        return "BundleID:(" + address + "," + sequenceNumber + ")";
    }
}
