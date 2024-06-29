/*
 * SunSpotHostApplication.java
 *
 * Created on 5/Jul/2009 19:45:49;
 */
package demo;

import com.sun.spot.peripheral.radio.RadioFactory;
import gems.charon.host.CharonConnection;
import gems.charon.host.messages.Bundle;
import gems.charon.host.utils.Debug;
import gems.charon.host.utils.Printer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Sample Sun SPOT host application
 */
public class DemoBasestation {

    CharonConnection connection;
    HashMap stats;
    Logger logger;
    BufferedWriter commonLog;
    DateFormat dateFormat;

    public void run() throws IOException {
        // WARNING: reduce transmit power
        //RadioFactory.getRadioPolicyManager().setOutputPower(-30);

        connection = new CharonConnection(0);
        stats = new HashMap();
        logger = new Logger();
        dateFormat = DateFormat.getTimeInstance();

        Bundle bundle;
        int numberOfCycles = 0;
        while (true) {
            try {
                bundle = connection.receive();
                long latency = System.currentTimeMillis() - bundle.getTimestamp();
                String address = bundle.getAddress();
                try {
                    byte type = bundle.readByte();
                    if (type == 0x0) {
                        int temperature = bundle.readInt();
                        int light = bundle.readInt();
                        System.out.format("Message from %s  |  Timestamp %s  |  Temperature %2.2f C  |  Light %3d %n",  Printer.getShortAddress(address), dateFormat.format(new Date(bundle.getTimestamp())), valueToCelsius(temperature), light);
                    } else if (type == 0x1) {
                        System.out.println("Alarm received: switch pressed");
                    }
                } catch (IOException ex) {
                    Debug.printError("Can't decode packet!");
                }
                NodeInfo node = getNode(address);
                node.addBundle(bundle, latency);
                logger.logBundle(bundle, latency);
            } catch (InterruptedException ex) {
            }
            if (++numberOfCycles % 40 == 0) {
                printStats();
            }
        }
    }

    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) throws IOException {
        DemoBasestation app = new DemoBasestation();
//        try {
//            new Console();
//        } catch (IOException e) {
//        }

        app.run();
    }

    private NodeInfo getNode(String address) {
        // Take care of indexing & printing
        if (!stats.containsKey(address)) {
            stats.put(address, new NodeInfo());
        }
        NodeInfo node = (NodeInfo) stats.get(address);
        return node;
    }

    private void printStats() {
        System.out.println("-- Packet delivery stats --");
        System.out.println("Node: (Latest/Lost/Delivery Ratio/Latency)");
        Iterator e = stats.entrySet().iterator();
        while (e.hasNext()) {
            Entry entry = (Entry) e.next();
            String address = (String) entry.getKey();
            NodeInfo info = (NodeInfo) entry.getValue();
            System.out.println(Printer.getShortAddress(address) + ": (" + info.getLatest() + "/" + info.getLost() + "/" + info.getReliability() + "/" + info.getAverageLatency() + ")");
        }
    }

    private double valueToCelsius(int val) {
        return (val >= 512) ? (val - 1024.0) / 4.0 : val / 4.0;
    }
}
