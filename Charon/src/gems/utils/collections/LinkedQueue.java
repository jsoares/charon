/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.utils.collections;

/**
 * Simple generic linked queue
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class LinkedQueue implements Queue {

    int size = 0;
    private LinkedQueueElement first;
    private LinkedQueueElement last;

    /**
     * Creates a new linked queue
     */
    public LinkedQueue() {
    }

    public void put(LinkedQueueElement node) {
        if (first == null) {
            // List empty, new node is first and last
            first = node;
            last = node;
        } else {
            // Else, new node is last
            last.next = node;
            last = node;
        }
        size++;
    }

    public LinkedQueueElement get() {
        if (size <= 0) {
            return null;
        }
        LinkedQueueElement ret = first;
        first = first.next;
        size--;
        return ret;
    }

    public LinkedQueueElement peek() {
        return first;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        first = null;
        last = null;
        size = 0;
    }
}

