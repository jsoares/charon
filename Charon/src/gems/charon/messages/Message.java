/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.messages;

import gems.charon.exceptions.MessageConversionException;

/**
 * Interface for Charon messages, primarily meant to allow standard
 * marshalling into datagrams.
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public interface Message {
    
    /**
     * Marshalls the message into a byte array containing all relevant data
     * @return message data as a byte array
     * @throws gems.charon.exceptions.MessageConversionException if message cannot be converted
     */
    public byte[] marshal() throws MessageConversionException;
    
    /**
     * Unmarshalls a message from a byte array created by the marshal() method
     * @param data message data as a byte array
     * @throws gems.charon.exceptions.MessageConversionException if message cannot be converted
     */
    public void unmarshal(byte[] data) throws MessageConversionException;

    /**
     * Returns a byte value representing the message type
     * @return message type
     */
    public byte getMessageType();
}
