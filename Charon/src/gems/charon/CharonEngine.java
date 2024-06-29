/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.peripheral.radio.LowPan;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.RadioPolicyManager;
import com.sun.spot.peripheral.radio.shrp.SingleHopManager;
import gems.charon.exceptions.BufferFullException;
import gems.charon.exceptions.NotSynchronizedException;
import gems.charon.data.BundleID;
import gems.charon.data.BundleStore;
import gems.charon.data.RoutingTable;
import gems.charon.messages.Bundle;
import gems.charon.qos.ServiceClassManager;
import gems.charon.time.RoundGenerator;
import gems.charon.time.RoundObserver;
import gems.charon.time.TimeKeeper;
import gems.charon.utils.Config;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main module for the Charon system, responsible for coordinating the system 
 * and interfacing with application
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class CharonEngine implements RoundObserver {

    /**
     * Singleton instance
     */
    static CharonEngine instance;
    /**
     * Routing module
     */
    private final RoutingEngine router;
    /**
     * Forwarding module
     */
    private final ForwardingEngine forwarder;
    /**
     * Own IEEE address
     */
    private final long ownAddress;
    /**
     * Currently available routes
     */
    protected final RoutingTable routes;
    /**
     * Buffer holding all bundles
     */
    protected final BundleStore buffer;
    /**
     * Known nodes' data
     */
    protected final Hashtable nodes;
    /**
     * Radio policy manager
     */
    private final IRadioPolicyManager radioManager;
    /**
     * Local sequence number
     */
    private int seqNum = 0;
    /**
     * Boolean value indicating the system state
     */
    private int state = STATE_OFF;
    protected final static int STATE_ON = 0;
    protected final static int STATE_OFF = 1;
    protected final static int STATE_PAUSED = 2;
    private int numberOfRounds = 0; //XXX: remove after debug
    private Timer roundTimer;

    /**
     * Private constructor to guarantee Singleton pattern
     */
    private CharonEngine() {
        ownAddress = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        router = new RoutingEngine(this);
        forwarder = new ForwardingEngine(this);
        radioManager = Spot.getInstance().getRadioPolicyManager();
        routes = new RoutingTable(ServiceClassManager.NUMBER_OF_CLASSES);
        buffer = new BundleStore(Config.BUFFER_CAPACITY);
        nodes = new Hashtable();
        RoundGenerator.attach(this);
        roundTimer = new Timer();
    }

    /**
     * Singleton getInstance() method
     * @return the single instance
     */
    public static CharonEngine getInstance() {
        if (instance == null) {
            instance = new CharonEngine();
            instance.start();
        }
        return instance;
    }

    /**
     * Returns the system's state
     * @return system state
     */
    public int getState() {
        return state;
    }

    /**
     * Starts the system
     */
    public synchronized void start() {
        if (state != STATE_OFF) {
            return;
        }
        state = STATE_ON;
        LowPan.getInstance().setRoutingManager(new SingleHopManager());
        router.start();
        forwarder.start();
        RoundGenerator.start();
    }

    /**
     * Stops the system
     */
    public synchronized void stop() {
        if (state == STATE_OFF) {
            return;
        }
        state = STATE_OFF;
        router.stop();
        forwarder.stop();
        RoundGenerator.stop();
    }

    /**
     * Pauses the system
     */
    private synchronized void pause() {
        if (state != STATE_ON) {
            return;
        }
        state = STATE_PAUSED;
        radioManager.setRxOn(false);
    }

    /**
     * Resumes the system
     */
    private synchronized void resume() {
        if (state != STATE_PAUSED) {
            return;
        }
        state = STATE_ON;
        radioManager.setRxOn(true);
        notifyAll();
    }

    /**
     * Adds a bundle to the sending queue
     * @param bundle bundle to send
     * @param stream bundle's stream
     * @param serviceClass bundle's service class
     * @return bundle's sequence number
     * @throws gems.charon.exceptions.NotSynchronizedException if the system is not yet synchronized
     * @throws gems.charon.exceptions.BufferFullException if the buffer is full
     */
    protected int send(Bundle bundle, byte stream, byte serviceClass) throws NotSynchronizedException, BufferFullException {
        if (!TimeKeeper.isSet()) {
            throw new NotSynchronizedException();
        }
        bundle.setTimestamp(TimeKeeper.getTime());
        bundle.setID(new BundleID(ownAddress, seqNum++));
        bundle.setServiceClass(serviceClass);
        bundle.setStream(stream);
        addBundle(bundle);
        return bundle.getID().getSequenceNumber();
    }

    /**
     * Adds a bundle to the queue
     * @param bundle
     * @throws gems.charon.exceptions.BufferFullException
     */
    protected void addBundle(Bundle bundle) throws BufferFullException {
        buffer.put(bundle);
    }

    /**
     * Triggered by RoundGenerator when a round starts
     */
    public void newRound() {
        if (Config.USE_ROUNDS) {
            resume();
            roundTimer.schedule(new RoundStopTask(), Config.ROUND_DURATION);
        } 
    }

    /**
     * Task triggered when round comes to an end
     */
    private class RoundStopTask extends TimerTask {

        public void run() {
            pause();
            System.gc(); // run gc now that the system is not being used
        }
    }
}
