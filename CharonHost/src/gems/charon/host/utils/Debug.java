/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.host.utils;

/**
 * Allows applications to print debug messages
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class Debug {

    /**
     * Lowest debug level, enables all messages
     */
    public static final int ALL = 0;
    /**
     * Very detailed information used for debugging
     */
    public static final int DEBUG = 1;
    /**
     * Significant error events
     */
    public static final int ERROR = 2;
    /**
     * Informational messages
     */
    public static final int INFO = 3;
    /**
     * Disables all messages
     */
    public static final int OFF = 4;

    /**
     * Prints the given debug message at the DEBUG level
     * @param msg message to print
     */
    public static void printDebug(String msg) {
        print(DEBUG, "DEBUG: " + msg);
    }

    /**
     * Prints the given debug message at the ERROR level
     * @param msg message to print
     */
    public static void printError(String msg) {
        print(ERROR, "ERROR: " + msg);
    }

    /**
     * Prints the given debug message at the INFO level
     * @param msg message to print
     */
    public static void printInfo(String msg) {
        print(INFO, "INFO: " + msg);
    }

    /**
     * Prints the given debug message at the given level
     * @param level level to use
     * @param msg message to print
     */    
    private static void print(int level, String msg) {
        if (level >= Config.DEBUG_LEVEL) {
            System.out.println(msg);
        }
    }
}
