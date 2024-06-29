/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.host;

import gems.charon.host.messages.Bundle;

/**
 * Opportunistic connection, allowing only bundles to be sent using the 
 * Charon system
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class CharonConnection {

    /**
     * Stream ID, used to multiplex applications
     */
    private final byte stream;
    /**
     * Owner engine
     */
    private final CharonHostEngine engine;

    /**
     * Creates a new connection with the given stream ID
     * @param stream ID of the stream to be used (0 <= stream <= 255)
     */
    public CharonConnection(int stream) {
        if (stream < 0 || stream > 255) {
            throw new IllegalStateException("Invalid arguments");
        }
        this.stream = (byte) stream;
        this.engine = CharonHostEngine.getInstance();
    }

    public Bundle receive() throws InterruptedException {
        return engine.receive(stream);
    }
}
