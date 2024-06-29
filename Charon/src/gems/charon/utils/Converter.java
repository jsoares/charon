/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.utils;

import com.sun.squawk.util.Arrays;
import java.io.IOException;
import gems.charon.messages.*;
import javax.microedition.io.Datagram;
import gems.charon.exceptions.MessageConversionException;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class Converter {

    /**
     * Number of bytes on the generic message header
     */
    private static final int HEADER_SIZE = 1;

    /**
     * Prevents the class from being instanced
     */
    private Converter() {
    }

    /**
     * Marshalls a message into a datagram payload
     * @param message message to marshall
     * @param datagram datagram to fill
     * @throws gems.charon.exceptions.MessageConversionException if the conversion fails
     */
    public static void messageToDatagram(Message message, Datagram datagram) throws MessageConversionException {
        // Write header
        try {
            datagram.reset();
            datagram.writeByte(message.getMessageType());
        } catch (IOException ex) {
            throw new MessageConversionException();
        }
        // Write data
        try {
            datagram.write(message.marshal());
        } catch (IOException ex) {
            throw new MessageConversionException();
        }
    }

    /**
     * Unmarshalls a datagram payload into a message
     * @param message message to fill
     * @param datagram datagram from which to extract data
     * @throws gems.charon.exceptions.MessageConversionException if the conversion fails
     */
    public static void datagramToMessage(Message message, Datagram datagram) throws MessageConversionException {
        short type;
        // Check type
        try {
            type = datagram.readByte();
        } catch (IOException ex) {
            throw new MessageConversionException();
        }
        if (type != message.getMessageType()) {
            throw new MessageConversionException();
        }
        // Read data
        //very inneficient - an alternative is to have the message
        //unmarshal drop the first X bytes
        byte[] data = datagram.getData();
        byte[] copy = Arrays.copy(data, HEADER_SIZE, data.length - HEADER_SIZE,
                0, data.length - HEADER_SIZE);
        message.unmarshal(copy);
    }
}
