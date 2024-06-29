/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.time;

import java.util.Timer;
import java.util.TimerTask;
import gems.charon.utils.Config;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Generates synchronous rounds according to the TimeKeeper reference
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class RoundGenerator {

    /**
     * Delay between round stop and schedule of next
     */
    static private final long CONTROL_DELAY = 500;
    /**
     * Vector of round observers
     */
    static Vector observers = new Vector();
    /**
     * Timer for the control task
     */
    static private Timer roundTimer;
    /**
     * Control task, used to schedule rounds
     */
    static private ControlTask controlTask;
    /**
     * Round start task, called at round start time
     */
    static private RoundStartTask roundStartTask;
    /**
     * Round period
     */
    static private long roundTime = Config.ROUND_PERIOD;

    /**
     * Prevents instantiation of class
     */
    private RoundGenerator() {
    }

    /**
     * Begins the round generation process, starting the control task
     */
    public static void start() {
        roundTimer = new Timer();
        controlTask = new ControlTask();
        roundStartTask = new RoundStartTask();
        roundTimer.schedule(controlTask, Config.ROUND_PERIOD);
    }

    /**
     * Stops the round generation process, killing the control task
     */
    public static void stop() {
        roundTimer.cancel();
    }

    /**
     * Adds a new observer to the notification list
     * @param observer observer to add
     */
    public static void attach(RoundObserver observer) {
        observers.addElement(observer);
    }

    /**
     * Notifies all observers
     */
    private static void notifyObservers() {
        Enumeration clients = observers.elements();
        while (clients.hasMoreElements()) {
            ((RoundObserver) clients.nextElement()).newRound();
        }
    }

    /**
     * Task used to control the module and schedule round starts
     */
    private static class ControlTask extends TimerTask {

        public void run() {
            long currentRound;
            long globalTime;
            long globalRoundStart;
            long localRoundStart;

            // abort if already scheduled or unsynched 
            if (!TimeKeeper.isSet()) {
                // Keep trying
                controlTask = new ControlTask();
                roundTimer.schedule(controlTask, Config.ROUND_PERIOD);
                return;
            }

            // find next round global start time
            synchronized (TimeKeeper.class) {
                globalTime = TimeKeeper.getTime();
                currentRound = globalTime / roundTime;
                globalRoundStart = (currentRound + 1) * roundTime;
                localRoundStart = TimeKeeper.globalToLocal(globalRoundStart);
            }

            // start timer if value is acceptable
            if (globalRoundStart - globalTime < 2 * roundTime) {
                try {
                    roundTimer.schedule(roundStartTask, new Date(localRoundStart));
                } catch (IllegalStateException e) {
                    // No problem, it just means it's already scheduled
                }
            }
        }
    }

    /**
     * Task triggered by round starts, responsible for subscriber notification
     */
    private static class RoundStartTask extends TimerTask {

        public void run() {
            notifyObservers();
            roundStartTask = new RoundStartTask();
            controlTask = new ControlTask();
            roundTimer.schedule(controlTask, Config.ROUND_DURATION + CONTROL_DELAY);
        }
    }
}
