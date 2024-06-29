/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.utils.collections;

/**
 * Generic wrapper element for linked queues
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class LinkedQueueElement {

    /**
     * Object contained in this node
     */
    public Object payload;
    /**
     * References the next element on the queue
     */
    public LinkedQueueElement next;
}