/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gems.charon.exceptions;

/**
 * Thrown if an action dependent on time synchronization is triggered before
 * a reference is acquired
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class NotSynchronizedException extends CharonException {

    /**
     * Creates a new exception
     */
    public NotSynchronizedException() {
        
    }
    
    /**
     * Creates a new exception with the given descriptive message
     * @param message descriptive message
     */
    public NotSynchronizedException(String message) {
        super(message);
    }    
    
}
