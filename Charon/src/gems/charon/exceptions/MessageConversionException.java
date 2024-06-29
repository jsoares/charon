/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gems.charon.exceptions;

/**
 * Thrown if a message conversion operation fails
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class MessageConversionException extends CharonException {

    /**
     * Creates a new exception
     */
    public MessageConversionException() {
        
    }
    
    /**
     * Creates a new exception with the given descriptive message
     * @param message descriptive message
     */
    public MessageConversionException(String message) {
        super(message);
    }
    
}

