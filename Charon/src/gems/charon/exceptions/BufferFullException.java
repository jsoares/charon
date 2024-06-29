/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gems.charon.exceptions;

/**
 * Thrown when a buffer insertion operation fails due to the buffer being full
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class BufferFullException extends CharonException {

    /**
     * Creates a new exception
     */
    public BufferFullException() {
        
    }
    
    /**
     * Creates a new exception with the given descriptive message
     * @param message descriptive message
     */
    public BufferFullException(String message) {
        super(message);
    }
    
}

