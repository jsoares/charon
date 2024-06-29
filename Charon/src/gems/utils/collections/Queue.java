/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package gems.utils.collections;

/**
 * Generic interface for a Queue, providing the usual methods
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public interface Queue {
    /**
     * Removes all items from the queue
     */
    void clear();

    /**
     * Returns and removes the first element from the queue
     * @return first element
     */
    LinkedQueueElement get();

    /**
     * Checks if the queue is empty
     * @return true if queue is empty
     */
    boolean isEmpty();

    /**
     * Returns the first element from the queue without removing it
     * @return first element
     */
    LinkedQueueElement peek();

    /**
     * Adds a new element to the queue
     * @param node element to add
     */
    void put(LinkedQueueElement node);

    /**
     * Returns the size of the queue
     * @return number of elements in the queue
     */
    int size();
}
