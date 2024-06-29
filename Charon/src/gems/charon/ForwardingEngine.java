/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.ChannelBusyException;
import com.sun.spot.peripheral.NoAckException;
import com.sun.spot.peripheral.NoRouteException;
import com.sun.spot.peripheral.radio.RadioPolicy;
import com.sun.spot.util.IEEEAddress;
import gems.charon.data.Node;
import gems.charon.exceptions.BufferFullException;
import java.io.IOException;
import javax.microedition.io.Connector;
import gems.charon.exceptions.MessageConversionException;
import gems.charon.messages.Bundle;
import gems.charon.qos.ServiceClassManager;
import gems.charon.utils.Config;
import gems.charon.utils.Converter;
import java.io.InterruptedIOException;

/**
 * System module responsible for receiving, storing and sending messages
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class ForwardingEngine {

    /**
     * Owneer engine
     */
    private final CharonEngine engine;
    /**
     * Transfer server thread
     */
    private ServerThread serverThread;
    /**
     * Transfer client thread
     */
    private ClientThread clientThread;
    /**
     * Port suffix for the connection string
     */
    private String portSuffix = ":" + Config.MESSAGE_PORT;

    /**
     * Creates a new ForwardingEngine
     * @param engine owner engine
     */
    protected ForwardingEngine(CharonEngine engine) {
        this.engine = engine;
    }

    /**
     * Starts the forwarding engine and its threads
     */
    protected void start() {
        serverThread = new ServerThread();
        clientThread = new ClientThread();
        serverThread.start();
        clientThread.start();
    }

    /**
     * Stops the forwarding engine and its threads
     */
    protected void stop() {
    }

    /**
     * This thread continuously checks for updated routes and, when available,
     * sends buffered bundles to its better-scored neighbour
     */
    private class ClientThread extends Thread {

        public void run() {
            while (engine.getState() != CharonEngine.STATE_OFF) {
                int i;
                long nextAddress = -1;

                // Get next hop
                synchronized (engine.routes) {
                    for (i = ServiceClassManager.NUMBER_OF_CLASSES - 1; i >= 0; i--) {
                        if (!engine.buffer.isEmpty(i)) {
                            nextAddress = engine.routes.getRoute(i);
                            if (nextAddress != -1) {
                                break;
                            }
                        }
                    }
                    // If no route found, wait on buffer
                    if (nextAddress == -1) {
                        try {
                            engine.routes.wait();
                        } catch (InterruptedException e) {
                            continue;
                        }
                    }
                }

                // If route found, start sending
                if (nextAddress != -1) {
                    sendBundles(nextAddress, i);
                }
            }
        }

        private void returnBundle(Bundle bundle) {
            // Weird exception, keep trying
            if (bundle != null) {
                try {
                    engine.buffer.put(bundle);
                } catch (BufferFullException exi) {
                }
            }
        }

        /**
         * Sends available bundles of class classID to the node with the given address
         * @param address address of the destination node
         * @param classID service class to process
         */
        private void sendBundles(long address, int classID) {
            String addressString = IEEEAddress.toDottedHex(address);
            boolean baseStation = ((Node) engine.nodes.get(new Long(address))).isBaseStation();
            try {
                RadiogramConnection connection = (RadiogramConnection) Connector.open("radiogram://" + addressString + portSuffix);
                Radiogram radiogram = (Radiogram) connection.newDatagram(Radiogram.MAX_LENGTH);
                Bundle bundle = null;
                while (engine.getState() == CharonEngine.STATE_ON && !engine.buffer.isEmpty(classID) && engine.routes.decrementCapacity(classID, address) > Config.MIN_CAPACITY) {
                    try {
                        bundle = engine.buffer.get(classID);
                        Converter.messageToDatagram(bundle, radiogram);
                        connection.send(radiogram);
                        if (!baseStation && ServiceClassManager.getClass(classID).useZombies()) {
                            bundle.setZombie();
                            engine.buffer.put(bundle);
                        }
                        Thread.sleep(5); // XXX: Check if useful, move value to Config
                    } catch (InterruptedException ex) {
                    } catch (BufferFullException ex) {
                    } catch (MessageConversionException ex) {
                    } catch (ChannelBusyException ex) {
                        // Let's try again
                        returnBundle(bundle);
                    } catch (NoAckException ex) {
                        // Let's try again
                        returnBundle(bundle);
                    } catch (NoRouteException ex) {
                        // Node gone, stop sending
                        returnBundle(bundle);
                        break;
                    } catch (IOException ex) {
                        returnBundle(bundle);
                    }
                }
                connection.close();
            } catch (IOException ex) {
                System.out.println("Error opening connection: " + ex);
            }
            engine.routes.removeRoute(classID, address);
        }
    }

    /**
     * This thread creates a listening connection, receiving and storing messages
     * sent by neighbouring nodes
     */
    private class ServerThread extends Thread {

        public void run() {
            try {
                RadiogramConnection connection = (RadiogramConnection) Connector.open("radiogram://" + portSuffix);
                connection.setRadioPolicy(RadioPolicy.AUTOMATIC);
                Radiogram radiogram = (Radiogram) connection.newDatagram(Radiogram.MAX_LENGTH);
                long address;
                Bundle bundle;
                while (engine.getState() != CharonEngine.STATE_OFF || connection.packetsAvailable()) {
                    try {
                        connection.receive(radiogram);
                        address = radiogram.getAddressAsLong();
                        bundle = new Bundle();
                        Converter.datagramToMessage(bundle, radiogram);
                        engine.addBundle(bundle);
                    } catch (MessageConversionException ex) {
                    } catch (InterruptedIOException ex) {
                        continue; // Cycles again, stops if 'stop' is set
                    } catch (IOException ex) {
                    } catch (BufferFullException ex) {
                        //XXX: do something
                    }
                }
                connection.close();
            } catch (IOException ex) {
            }

        }
    }
}
