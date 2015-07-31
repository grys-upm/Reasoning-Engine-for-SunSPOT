/**
 * Copyright 2014-2015 Universidad Politécnica de Madrid (UPM).
 *
 * Authors:
 *    José-Fernan Martínez Ortega
 *    Vicente Hernández Díaz
 *    Néstor Lucas Martínez
 *    Yuanjiang Huang
 *    Raúl del Toro Matamoros
 * 
 * This software is distributed under a dual-license scheme:
 *
 * - For academic uses: Licensed under GNU Affero General Public License as
 *                      published by the Free Software Foundation, either
 *                      version 3 of the License, or (at your option) any
 *                      later version.
 * 
 * - For any other use: Licensed under the Apache License, Version 2.0.
 * 
 * You can get a copy of the license terms in licences/LICENSE.
 */
package eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.ondemand;

import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * OnDemandNeighborTableMAnager implements the table used to store the active
 * neighbors detected by the Neighbor Discovery Protocol.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class OnDemandNeighborTableManager {

    private static final long DEFAULT_EXPIRY_TIME = 5000;

    private Hashtable neighborTable;
    private Vector neighborList;
    private long expiryTime;
    private StringBuffer neighborsCSV;

    private final Logger logger;
    private final Object lock;

    /**
     * Creates a new instance of the neighbor table manager using the default
     * expiry time for new entries.
     */
    public OnDemandNeighborTableManager() {
        this.neighborTable = new Hashtable();
        this.neighborList = new Vector();
        this.expiryTime = DEFAULT_EXPIRY_TIME;
        this.neighborsCSV = new StringBuffer();
        this.lock = new Object();
        this.logger = SystemContext.getLogger();
    }

    /**
     * Creates a new instance of the neighbor table manager using the expiry
     * time especified by <i>expiryTime</i>.
     * 
     * @param expiryTime The expiry time for new entries in the table.
     */
    public OnDemandNeighborTableManager(long expiryTime) {
        this();
        this.expiryTime = expiryTime;
    }

    /**
     * Sets a new expiry time.
     * 
     * @param expiryTime The new expiry time.
     */
    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    /**
     * Gets the current expiry time.
     * 
     * @return The current expiry time.
     */
    public long getExpiryTime() {
        return this.expiryTime;
    }

    /**
     * Gets the number of acive neighbors registered in the table.
     * 
     * @return The number of active neighbors.
     */
    public int getNeighborCount() {
        synchronized (lock) {
            logNeighborInformation();

            return this.neighborTable.size();
        }
    }

    /**
     * Retrives the list of active neighbors.
     * 
     * @return The list of active neighbors.
     */
    public Vector getNeighborList() {
        synchronized (lock) {
            this.neighborList.removeAllElements();
            Enumeration neighbors = this.neighborTable.keys();

            while (neighbors.hasMoreElements()) {
                neighborList.addElement(neighbors.nextElement());
            }

            return neighborList;
        }
    }

    /**
     * Adds a neighbor to the table.
     * 
     * @param neighborAddress The address of the neighbor
     */
    public void addNeighbor(String neighborAddress) {
        synchronized (lock) {
            long timestamp = System.currentTimeMillis();
            if (neighborTable.containsKey(neighborAddress)) {
                logger.log(Level.DEBUG, "OnDemandNeighborTableManager.addNeighbour: Request from (" + neighborAddress + ") accepted. There are " + neighborTable.size() + " neighbours.");
            } else {
                logger.log(Level.DEBUG, "OnDemandNeighborTableManager.addNeighbour: " + neighborAddress + " has sent more than one response.");
            }

            neighborTable.put(neighborAddress, Long.valueOf(timestamp));
        }
    }

    /**
     * Performs a maintenance check of the neighbor table, removing those
     * neighbors which their last alive update is greater than the expiry time.
     */
    public void maintainNeighborTable() {
        synchronized (lock) {
            long actualTimestamp = System.currentTimeMillis();
            Enumeration neighbors = neighborTable.keys();

            while (neighbors.hasMoreElements()) {
                Object key = neighbors.nextElement();
                long recordedTimestamp = ((Long) neighborTable.get(key)).longValue();
                long timeAlive = actualTimestamp - recordedTimestamp;

                if (timeAlive > this.expiryTime) {
                    logger.debug("OnDemandNeighborTableManager.maintainNeighborTable: Time for neighbor " + key + " has expired (" + timeAlive + "). REMOVING");
                    neighborTable.remove(key);
                }
            }
        }
    }

    /**
     * Logs neighbor information. For debug purposes only.
     */
    public void logNeighborInformation() {
        long nd = neighborTable.size();

        neighborsCSV.setLength(0);
        Enumeration neighbors = neighborTable.keys();

        while (neighbors.hasMoreElements()) {
            neighborsCSV.append((String) neighbors.nextElement());
            if (neighbors.hasMoreElements()) {
                neighborsCSV.append(",");
            }
        }

        if (nd > 0) {
            if (SystemContext.isDebugActive()) {
                byte originalDestination = logger.getDestination();
                logger.setDestination(Logger.RECORD);
                logger.debug(neighborsCSV.toString());
                logger.setDestination(originalDestination);
            }
        }
    }

    /**
     * Clears the neighbor table.
     */
    public void clearTable() {
        synchronized (lock) {
            this.neighborTable.clear();
        }
    }
}
