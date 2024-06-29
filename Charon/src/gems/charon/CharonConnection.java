/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon;

import gems.charon.exceptions.BufferFullException;
import gems.charon.exceptions.NotSynchronizedException;
import gems.charon.messages.Bundle;
import gems.charon.qos.ServiceClassManager;
import gems.charon.utils.Config;

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
    private final CharonEngine engine;

    /**
     * Creates a new connection with the given stream ID
     * @param stream ID of the stream to be used (0 <= stream <= 255)
     */
    public CharonConnection(int stream) {
        if (stream < 0 || stream > 255) {
            throw new IllegalStateException("Invalid arguments");
        }
        this.stream = (byte) stream;
        this.engine = CharonEngine.getInstance();
    }

    /**
     * Creates and returns a new empty bundle
     * @return created bundle
     */
    public Bundle newBundle() {
        return new Bundle();
    }

    /**
     * Queues a bundle to be sent with normal priority
     * @param bundle bundle to be sent
     * @return sequence number of the sent bundle
     * @throws gems.charon.exceptions.NotSynchronizedException if the system is not yet synchronized
     * @throws gems.charon.exceptions.BufferFullException if the buffer is full
     */
    public int send(Bundle bundle) throws NotSynchronizedException, BufferFullException {
        return send(bundle, ServiceClassManager.CLASS_SENSING);
    }

    /**
     * Queues a bundle to be sent with alarm priority
     * @param bundle bundle to be sent
     * @return sequence number of the sent bundle
     * @throws gems.charon.exceptions.NotSynchronizedException if the system is not yet synchronized
     * @throws gems.charon.exceptions.BufferFullException if the buffer is full
     */
    public int sendAlarm(Bundle bundle) throws NotSynchronizedException, BufferFullException {
        return send(bundle, ServiceClassManager.CLASS_ALARM);
    }

    /**
     * Queues a bundle to be sent with the given priority
     * @param bundle bundle to be sent
     * @param serviceClass service class to use
     * @return sequence number of the sent bundle
     * @throws gems.charon.exceptions.NotSynchronizedException if the system is not yet synchronized
     * @throws gems.charon.exceptions.BufferFullException if the buffer is full
     */
    private int send(Bundle bundle, byte serviceClass) throws NotSynchronizedException, BufferFullException {
        return engine.send(bundle, stream, serviceClass);
    }
}
