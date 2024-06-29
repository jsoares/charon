/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.data;

import gems.charon.exceptions.BufferFullException;
import java.util.Hashtable;
import gems.utils.collections.LinkedQueue;
import gems.utils.collections.LinkedQueueElement;
import gems.utils.collections.Queue;
import gems.utils.collections.SortedLinkedQueue;
import gems.charon.messages.Bundle;
import gems.charon.messages.BundleAgeComparator;
import gems.charon.qos.ServiceClassManager;
import gems.charon.time.TimeKeeper;
import gems.charon.utils.Config;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class BundleStore {

    Hashtable table;
    Queue[] queues;
    Queue unused;
    int size;
    int capacity;
    long lastCleanup;

    public BundleStore(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.lastCleanup = System.currentTimeMillis();

        this.table = new Hashtable(capacity);

        this.queues = new Queue[ServiceClassManager.NUMBER_OF_CLASSES];
        for (int i = 0; i < ServiceClassManager.NUMBER_OF_CLASSES; i++) {
            queues[i] = new SortedLinkedQueue(new BundleAgeComparator());
        }

        this.unused = new LinkedQueue();
        for (int i = 0; i < capacity; i++) {
            unused.put(new LinkedQueueElement());
        }
    }

    public synchronized void put(Bundle bundle) throws BufferFullException {
        if (bundle == null) {
            return;
        }
        cleanUp();
        if (table.containsKey(bundle.getID())) {
            if (((Bundle) table.get(bundle.getID())).isZombie()) {
                // hack... we really should remove the ghost from queue
                table.remove(bundle.getID());
            } else {
                return;
            }
        }
        try {
            LinkedQueueElement element = newElement(bundle);
            table.put(bundle.getID(), bundle);
            queues[bundle.getServiceClass()].put(element);
        } catch (OutOfMemoryError e) {
            throw new BufferFullException();
        }
    }

    public synchronized Bundle get(int queueID) {
        if (queueID >= ServiceClassManager.NUMBER_OF_CLASSES || queues[queueID].isEmpty()) {
            return null;
        }
        LinkedQueueElement element = queues[queueID].get();
        Bundle bundle = (Bundle) element.payload;
        table.remove(bundle.getID());
        deleteElement(element);
        return bundle;
    }

    public synchronized boolean contains(Bundle bundle) {
        return table.containsKey(bundle.getID());
    }

    public synchronized int size() {
        return size;
    }

    public synchronized int size(int queueID) {
        return queues[queueID].size();
    }

    public synchronized boolean isEmpty(int queueID) {
        return queues[queueID].isEmpty();
    }

    public synchronized boolean isFull() {
        return unused.isEmpty();
    }

    public synchronized int free() {
        return capacity - size;
    }

    private LinkedQueueElement newElement(Bundle bundle) {
        if (unused.size() <= 0) {
            throw new OutOfMemoryError();
        }
        LinkedQueueElement element = unused.get();
        element.payload = bundle;
        element.next = null;
        size++;
        return element;
    }

    private void deleteElement(LinkedQueueElement element) {
        element.payload = null;
        element.next = null;
        unused.put(element);
        size--;
    }

    private void cleanUp() {
        long time = TimeKeeper.getTime();
        long localTime = System.currentTimeMillis();

        if (localTime < lastCleanup + Config.BUFFER_CLEANUP) {
            return;
        }
        lastCleanup = localTime;

        // Clean up old messages
        for (int i = 0; i < ServiceClassManager.NUMBER_OF_CLASSES; i++) {
            long ttl = ServiceClassManager.getClass(i).getTTL();
            while (!queues[i].isEmpty()) {
                LinkedQueueElement elm = queues[i].peek();
                if (((Bundle) elm.payload).getTimestamp() + ttl < time) {
                    get(i);
                } else {
                    break;
                }
            }
        }

        // Clean up zombies
        while (free() < Config.RESERVE_CAPACITY * 2 && !queues[ServiceClassManager.CLASS_ZOMBIE].isEmpty()) {
            get(ServiceClassManager.CLASS_ZOMBIE);
        }
    }
}