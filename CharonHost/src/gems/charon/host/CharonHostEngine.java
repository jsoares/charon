/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.host;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.ChannelBusyException;
import com.sun.spot.peripheral.radio.LowPan;
import com.sun.spot.peripheral.radio.shrp.SingleHopManager;
import gems.charon.host.exceptions.MessageConversionException;
import gems.charon.host.messages.Beacon;
import gems.charon.host.messages.Bundle;
import gems.charon.host.utils.Config;
import gems.charon.host.utils.Converter;
import gems.charon.host.utils.Debug;
import gems.charon.host.utils.Printer;
import gems.charon.host.messages.BundleAgeComparator;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.microedition.io.Connector;

/**
 *
 * @author mindstorm
 */
public class CharonHostEngine {

    private static CharonHostEngine instance;
    private boolean stop = false;
    private HashMap streams;
    private HashSet bundles;

    private CharonHostEngine() {
        streams = new HashMap();
        bundles = new HashSet();
    }

    public static CharonHostEngine getInstance() {
        if (instance == null) {
            instance = new CharonHostEngine();
            instance.start();
        }
        return instance;
    }

    public void start() {
        LowPan.getInstance().setRoutingManager(new SingleHopManager());
        BeaconThread beaconThread = new BeaconThread();
        beaconThread.setPriority(Thread.MAX_PRIORITY);
        beaconThread.start();
        new ServerThread().start();
    }

    public void stop() {
        stop = true;
    }

    public Bundle receive(int streamID) throws InterruptedException {
        SortedSet stream = getStreamSet(streamID);
        Bundle bundle;
        synchronized (stream) {
            if (stream.isEmpty()) {
                stream.wait();
            }
            bundle = (Bundle) stream.first();
            stream.remove(bundle);
        }
        return bundle;
    }

    private synchronized SortedSet getStreamSet(int streamID) {
        Integer id = new Integer(streamID);
        if (!streams.containsKey(id)) {
            streams.put(id, new TreeSet(new BundleAgeComparator()));
        }
        return (SortedSet) streams.get(id);
    }

    private void storeBundle(Bundle bundle) {
        if(bundles.contains(bundle.getID())) {
            return; // already received and delivered
        }

        SortedSet stream = getStreamSet((bundle.getStream()));
        synchronized (stream) {
            stream.add(bundle);
            stream.notify();
        }
        if(Config.PREVENT_DUPLICATES) {
            bundles.add(bundle.getID());
        }
    }

    private class BeaconThread extends Thread {

        public void run() {
            try {
                RadiogramConnection connection = (RadiogramConnection) Connector.open("radiogram://broadcast" + ":" + Config.BEACON_PORT);
                Radiogram radiogram = (Radiogram) connection.newDatagram(Radiogram.MAX_LENGTH);
                Beacon beacon = new Beacon();

                beacon.setBaseStation();
                connection.setMaxBroadcastHops(1);

                Debug.printInfo("BeaconThread connection open");
                while (!stop) {
                    try {
                        beacon.setTime(System.currentTimeMillis());
                        Converter.messageToDatagram(beacon, radiogram);
                        connection.send(radiogram);
                    } catch (ChannelBusyException ex) {
                    } catch (MessageConversionException ex) {
                        Debug.printError("Error converting beacon: " + ex);
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        Debug.printError("Error sending packet: " + ex);
                        ex.printStackTrace();
                    }
                    try {
                        sleep(Config.BS_BEACON_PERIOD);
                    } catch (InterruptedException ex) {
                        Debug.printError("Sleep interrupted: " + ex);
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                Debug.printError("Error opening connections: " + ex);
                ex.printStackTrace();
            }
        }
    }

    private class ServerThread extends Thread {

        public void run() {
            try {
                RadiogramConnection connection = (RadiogramConnection) Connector.open("radiogram://" + ":" + Config.MESSAGE_PORT);
                Radiogram radiogram = (Radiogram) connection.newDatagram(Radiogram.MAX_LENGTH);
                long address;
                Bundle bundle = new Bundle();
                Debug.printInfo("ServerThread connection open");
                while (!stop) {
                    try {
                        connection.receive(radiogram);
                        address = radiogram.getAddressAsLong();
                        bundle = new Bundle();
                        Converter.datagramToMessage(bundle, radiogram);
                        storeBundle(bundle);
                        Debug.printInfo("[" + System.currentTimeMillis() + "] Received (" + bundle + ") from " + Printer.getShortAddress(address));
                    } catch (MessageConversionException ex) {
                        Debug.printError("Error converting bundle: " + ex);
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        Debug.printError("Error receiving packet: " + ex);
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                Debug.printError("Error opening connections: " + ex);
                ex.printStackTrace();
            }
        }
    }
}
