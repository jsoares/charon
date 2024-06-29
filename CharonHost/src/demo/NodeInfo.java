/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import gems.charon.host.messages.Bundle;
import java.util.HashSet;

/**
 *
 * @author mindstorm
 */
public class NodeInfo {

    private HashSet bundles = new HashSet();
    private long latest = 0;
    private long totalLatency = 0;

    public void addBundle(Bundle bundle, long latency) {
        if (bundles.add(bundle.getID())) {
            this.totalLatency += latency;
            if (bundle.getID().getSequenceNumber() > latest) {
                latest = bundle.getID().getSequenceNumber();
            }
        }
    }

    public long getAverageLatency() {
        return (totalLatency / bundles.size());
    }

    public float getReliability() {
        return ((float) bundles.size()) / ((float) latest + 1);
    }

    public long getLost() {
        return latest - bundles.size() + 1;
    }

    public long getLatest() {
        return latest;
    }

    public long getReceived() {
        return bundles.size();
    }
}
