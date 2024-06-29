/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.time;

/**
 * Observer-style interface for round notification
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public interface RoundObserver {

    /**
     * Called by the observed object when a new round is started
     */
    public void newRound();
}
