/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.utils.collections;

/**
 * Generic linked queue, using a comparator to maintain order. The sorting is
 * done on insertion, making it a costly operation.
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class SortedLinkedQueue implements Queue {

    private LinkedQueueElement first = null;
    int size = 0;
    private Comparator comparator;

    /**
     * Creates a linked queue, sorted on the given comparator
     * @param comparator comparator to use for sorting 
     */
    public SortedLinkedQueue(Comparator comparator) {
        this.comparator = comparator;
    }

    public void put(LinkedQueueElement node) {

        // Else, insert in order
        LinkedQueueElement current = first;
        LinkedQueueElement previous = null;

        while ((current != null) && (comparator.compare(node.payload, current.payload) >= 0)) {
            previous = current;
            current = current.next;
        }

        if (previous == null) {
            // Empty list OR insert first
            node.next = first;
            first = node;
        } else {
            previous.next = node;
            node.next = current;
        }

        size++;
        //XXX printQueue();
    }

    public LinkedQueueElement get() {
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
        size = 0;
    }

    private void printQueue() {
        System.out.println("Printing queue, size " + size);

        LinkedQueueElement elm = first;

        while (elm != null) {
            System.out.println("Element: " + elm.payload.toString());
            elm = elm.next;
        }
    }
}

