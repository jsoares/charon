/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import gems.charon.host.messages.Bundle;
import gems.charon.host.utils.Printer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

/**
 *
 * @author mindstorm
 */
public class Logger {

    String reference;
    HashMap logs;
    PrintStream mainLog;

    public Logger() throws IOException {
        logs = new HashMap();
        reference = "logs/" + System.currentTimeMillis();
        (new File(reference)).mkdir();
        mainLog = createStream("common");
    }

    private PrintStream getLog(long address) throws IOException {
        if (!logs.containsKey(address)) {
            logs.put(address, createStream(Printer.getShortAddress(address)));
        }
        return (PrintStream) logs.get(address);
    }

    private PrintStream createStream(String name) throws IOException {
        File newFile = new File(reference, name + ".csv");
        //newFile.createNewFile();
        return new PrintStream(new FileOutputStream(newFile), true);
    }

    public void logBundle(Bundle bundle, long latency) throws IOException {
        mainLog.println(Printer.getShortAddress(bundle.getID().getAddress()) + ";" + bundle.getID().getSequenceNumber() + ";" + bundle.getTimestamp() + ";" + latency);
        getLog(bundle.getID().getAddress()).println(bundle.getID().getSequenceNumber() + ";" + bundle.getTimestamp() + ";" + latency);
    }
}
