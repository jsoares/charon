/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.qos;

/**
 *
 * @author mindstorm
 */
public class ServiceClassManager {

    /**
     * Number of service classes
     */
    public final static int NUMBER_OF_CLASSES = 3;
    /**
     * ID of zombie class
     */
    public final static byte CLASS_ZOMBIE = 0;
    /**
     * ID of sensing class
     */
    public final static byte CLASS_SENSING = 1;
    /**
     * ID of alarm class
     */
    public final static byte CLASS_ALARM = 2;
    /**
     * Class objects
     */
    private static ServiceClass[] classes = {
        new ZombieData(), // 0
        new MonitoringData(), // 1
        new AlarmData() // 2
    };

    public static ServiceClass getClass(int classID) {
        return classes[classID];
    }
}
