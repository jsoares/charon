/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.qos;

/**
 *
 * @author Jorge Soares
 */
public interface ServiceClass {

    public boolean allowMultipleCopies();

    public boolean useZombies();

    public long getTTL();

    public long getScore(int capacity, int battery, long edd);
}
