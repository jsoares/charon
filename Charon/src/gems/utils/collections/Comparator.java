/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package gems.utils.collections;

/**
 * A Comparator is an object used to compare two objects, returning an integer
 * representing the relative order between the two.
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public interface Comparator {

    /**
     * Compares its two arguments for order. Returns a negative integer, zero,
     * or a positive integer as the first argument is less than, equal to, or
     * greater than the second.
     * @param o1 object to be compared
     * @param o2 object to be compared
     * @return a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.
     */
    int compare(Object o1, Object o2);
}
