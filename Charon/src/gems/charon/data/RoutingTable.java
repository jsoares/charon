/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gems.charon.data;

import gems.charon.utils.Config;

/**
 *
 * @author Jorge Soares <jorgesoares at ist.utl.pt>
 */
public class RoutingTable {

    Route[] routes;

    public RoutingTable(int number) {
        routes = new Route[number];
        for (int i = 0; i < number; i++) {
            routes[i] = new Route();
        }
    }

    public synchronized void updateRoute(int classID, long address, long score, long edd, boolean baseStation, short capacity) {
        // Notify forwarder
        this.notifyAll();
        // Set data
        routes[classID].address = address;
        routes[classID].score = score;
        routes[classID].edd = edd;
        routes[classID].expirationTime = System.currentTimeMillis() + Config.PATH_EXPIRATION;
        routes[classID].baseStation = baseStation;
        routes[classID].capacity = capacity;
    }

    public synchronized void updateAllRoutes(long address, long score, long edd, boolean baseStation, short capacity) {
        for (int i = 0; i < routes.length; i++) {
            updateRoute(i, address, score, edd, baseStation, capacity);
        }
    }

    public synchronized boolean hasRoute(int classID) {
        return routes[classID].address != -1;
    }

    public synchronized long getRoute(int classID) {
        return routes[classID].address;
    }

    public synchronized long getScore(int classID) {
        return routes[classID].score;
    }

    public synchronized long getEDD(int classID) {
        return routes[classID].edd;
    }

    public synchronized short getCapacity(int classID) {
        return routes[classID].capacity;
    }

    public synchronized short decrementCapacity(int classID, long address) {
        for (int i = 0; i < routes.length; i++) {
            if (routes[i].address == address) {
                routes[i].capacity--;
            }
        }
        return routes[classID].capacity;
    }

    public synchronized void removeRoute(int classID, long address) {
        if (routes[classID].address == address) {
            routes[classID].address = -1;
        }
    }

    public synchronized void clear() {
        for (int i = 0; i < routes.length; i++) {
            routes[i].address = -1;
        }
    }

    public synchronized void cleanup() {
        long time = System.currentTimeMillis();
        for (int i = 0; i < routes.length; i++) {
            if (routes[i].expirationTime < time || routes[i].capacity <= Config.MIN_CAPACITY) {
                routes[i].address = -1;
            }
        }
    }

    private class Route {

        long address;
        long score;
        long edd;
        long expirationTime;
        boolean baseStation;
        short capacity;
    }
}
