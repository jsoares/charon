/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.host.exceptions;

/**
 * Top-level exception for the opportunity system
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class CharonException extends Exception {

    /**
     * Creates a new exception
     */
    protected CharonException() {
    }

    /**
     * Creates a new exception with the given descriptive message
     * @param message descriptive message
     */
    protected CharonException(String message) {
        super(message);
    }
}
