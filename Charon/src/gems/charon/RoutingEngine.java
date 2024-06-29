/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.ChannelBusyException;
import com.sun.spot.peripheral.IBattery;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.peripheral.radio.RadioPolicy;
import java.io.IOException;
import javax.microedition.io.Connector;
import gems.charon.exceptions.MessageConversionException;
import gems.charon.data.Node;
import gems.charon.messages.Beacon;
import gems.charon.qos.ServiceClassManager;
import gems.charon.time.TimeKeeper;
import gems.charon.utils.Config;
import gems.charon.utils.Converter;
import java.io.InterruptedIOException;
import java.util.Enumeration;

/**
 * System module responsible for receiving and sending beacons, keeping track of contacts,
 * scores and routes.
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class RoutingEngine {

    /**
     * Owner engine
     */
    private final CharonEngine engine;
    /**
     * Beacon listening thread
     */
    private ListenThread listenThread;
    /**
     * Beacon sending thread
     */
    private BeaconThread beaconThread;
    /**
     * Battery object used to get energy info
     */
    private IBattery battery;
    /**
     * Port suffix for the connection string
     */
    private String portSuffix = ":" + Config.BEACON_PORT;

    /**
     * Creates a new RoutingEngine
     * @param engine owner engine
     */
    protected RoutingEngine(CharonEngine engine) {
        this.engine = engine;
        battery = Spot.getInstance().getPowerController().getBattery();
    }

    /**
     * Starts the forwarding engine and its threads
     */
    protected void start() {
        listenThread = new ListenThread();
        beaconThread = new BeaconThread();
        beaconThread.setPriority(Thread.MAX_PRIORITY);
        listenThread.setPriority(Thread.MAX_PRIORITY);
        listenThread.start();
        beaconThread.start();
    }

    /**
     * Stops the forwarding engine and its threads
     */
    protected void stop() {
        engine.routes.clear();
    }

    private void handleBeacon(Beacon beacon, long address) {
        TimeKeeper.updateTime(beacon.getTime(), beacon.getAge());
        registerContact(beacon, address); // Must be called before updateRoutes()
        updateRoutes(beacon, address);
    }

    private void updateRoutes(Beacon beacon, long address) {
        // Start by cleaning up old routes
        engine.routes.cleanup();

        // If basestation, just set route        
        if (beacon.isBaseStation()) {
            engine.routes.updateAllRoutes(address, Long.MAX_VALUE, 0, true, Short.MAX_VALUE);
            return;
        }

        // Do not proceed is there's not enough buffer space
        short usefulCapacity = (short) (beacon.getCapacity() / Config.CAPACITY_SHARE);
        if (usefulCapacity <= Config.MIN_CAPACITY) {
            return;
        }

        // Now check scores for each class
        for (byte i = ServiceClassManager.NUMBER_OF_CLASSES - 1; i >= 1; i--) {
            long newScore = ScoreCalculator.getScore(i, beacon);
            long newEDD = beacon.getEDD();

            if (newScore >= getOwnScore(i) && newEDD < getOwnEDD()) {
                if (!engine.routes.hasRoute(i) || (newScore >= engine.routes.getScore(i) && newEDD < engine.routes.getEDD(i))) {
                    engine.routes.updateRoute(i, address, newScore, newEDD, false, usefulCapacity);
                }
            }
        }
    }

    private void registerContact(Beacon beacon, long address) {
        Node node = (Node) engine.nodes.get(new Long(address));
        if (null == node) {
            node = new Node(address, beacon.isBaseStation());
            engine.nodes.put(new Long(address), node);
        }
        if (System.currentTimeMillis() - node.getLastContact() > Config.MIN_CONTACT_INTERVAL) {
            node.registerContact(System.currentTimeMillis(), beacon.getEDD());
        }
    }

    private long getOwnEDD() {
        long ownEDD = Integer.MAX_VALUE;
        long time = System.currentTimeMillis();
        synchronized (engine.nodes) {
            Enumeration nodes = engine.nodes.elements();
            while (nodes.hasMoreElements()) {
                Node node = (Node) nodes.nextElement();
                // Start with the known ETA
                long prediction = node.getEDD();
                // Add our observed ICT
                long ict = node.getICT();
                prediction += ict;
                // If current delay is larger than ICT, add it too
                long lastContact = node.getLastContact();
                if (time - lastContact > ict) {
                    prediction += time - lastContact;
                }
                // Updated ownETA if this is smaller
                if (prediction < ownEDD) {
                    ownEDD = prediction;
                }
            }
        }
        return ownEDD;
    }

    private long getOwnScore(int serviceClass) {
        return ScoreCalculator.getScore(serviceClass, engine.buffer.free(), battery.getBatteryLevel(), getOwnEDD());
    }

    /**
     * This thread periodically sends beacons announcing our presence and data
     */
    private class BeaconThread extends Thread {

        public void run() {
            try {
                RadiogramConnection connection = (RadiogramConnection) Connector.open("radiogram://broadcast" + portSuffix);
                Radiogram radiogram = (Radiogram) connection.newDatagram(Radiogram.MAX_LENGTH);
                Beacon beacon = new Beacon();

                connection.setMaxBroadcastHops(1);

                while (engine.getState() != CharonEngine.STATE_OFF) {
                    synchronized (engine) {
                        if (engine.getState() == CharonEngine.STATE_PAUSED) {
                            try {
                                engine.wait();
                            } catch (InterruptedException ex) {
                            }
                        }
                    }
                    try {
                        if (TimeKeeper.isSet()) {
                            fillHeader(beacon);
                            Converter.messageToDatagram(beacon, radiogram);
                            connection.send(radiogram);
                        }
                    } catch (ChannelBusyException ex) {
                    } catch (MessageConversionException ex) {
                    } catch (IOException ex) {
                    }
                    try {
                        sleep(Config.BEACON_PERIOD);
                    } catch (InterruptedException ex) {
                    }
                }
                connection.close();
            } catch (IOException ex) {
            }
        }

        /**
         * Fills the bundle header with control data
         * @param beacon
         */
        private void fillHeader(Beacon beacon) {
            beacon.setPower((byte) battery.getBatteryLevel());
            beacon.setCapacity((short) engine.buffer.free());
            beacon.setEDD(getOwnEDD());
            synchronized (TimeKeeper.class) {
                beacon.setTime(TimeKeeper.getTime());
                beacon.setAge(TimeKeeper.getAge());
            }
        }
    }

    /**
     * This thread creates a listening connection, receiving beacons and 
     * updating scores and routes
     */
    private class ListenThread extends Thread {

        public void run() {
            try {
                RadiogramConnection connection = (RadiogramConnection) Connector.open("radiogram://" + portSuffix);
                connection.setRadioPolicy(RadioPolicy.AUTOMATIC);
                Radiogram radiogram = (Radiogram) connection.newDatagram(Radiogram.MAX_LENGTH);
                long address;
                Beacon beacon = new Beacon();
                while (engine.getState() != CharonEngine.STATE_OFF) {
                    try {
                        radiogram.reset();
                        connection.receive(radiogram);
                        address = radiogram.getAddressAsLong();
                        Converter.datagramToMessage(beacon, radiogram);
                        handleBeacon(beacon, address);
                    } catch (MessageConversionException ex) {
                    } catch (InterruptedIOException ex) {
                        continue; //Cycles again, stops if 'stop' is set
                    } catch (IOException ex) {
                    }
                }
                connection.close();
            } catch (IOException ex) {
            }
        }
    }
}
