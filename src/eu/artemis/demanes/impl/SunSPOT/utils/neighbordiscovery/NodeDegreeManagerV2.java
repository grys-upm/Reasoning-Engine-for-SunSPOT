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
package eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery;

import com.sun.spot.core.util.Utils;
import com.sun.spot.multihop.io.j2me.radiogram.RadiogramConnection;
import com.sun.squawk.util.NotImplementedYetException;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 *
 * @author Vicente Hern&acute;ndez D&iacute;az
 * @version 1.0.0
 *
 * Modified by N&eacute;stor Lucas Mart&iacute;nez for communication
 * pervasiveness.
 */
public class NodeDegreeManagerV2 implements NeighborDiscoveryProtocol {

    public void doNeighborDiscovery() {
        throw new NotImplementedYetException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setContext(SystemContext context) {
        //throw new java.lang.UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class NeighborsDiscoveryMgr implements Runnable {

        /**
         * Message of type Neighbor Discovery Request.
         */
        private final byte ND_REQUEST = 0;
        /**
         * Message of type Neighbor Discovery Response.
         */
        private final byte ND_RESPONSE = 1;
        /**
         * Message of type Neighbor Discovery Response Acknowledge.
         */
        private final byte ND_RESPONSE_ACK = 2;

        /**
         * Port used for this particular Neighbor Discovery Protocol.
         */
        private final int NEIGHBORS_DISCOVERY_PORT = 200;

        /**
         * Information regarding the Node Degree (the number of available
         * neighbors at any given moment).
         */
        private final NDInformation nodeDegreeInformation;

        /**
         * Random number generator.
         */
        private final Random randomGenerator;

        public NeighborsDiscoveryMgr(NDInformation nodeDegreeInformation) {
            this.nodeDegreeInformation = nodeDegreeInformation;
            randomGenerator = new Random();
            logger.log(Level.DEBUG, "NeighboursDiscoveryManager: A new NeighboursDiscoveryMgr instantiated.");
            new Thread(this).start();
        }

        public void requestForNeighbors() {
            try {
                long requestID;
                requestID = randomGenerator.nextLong();

                if (requestID < 0) {
                    requestID *= -1;
                }

                nodeDegreeInformation.changeReqID(requestID);
                RadiogramConnection con = (RadiogramConnection) Connector.open("radiogram://broadcast:" + NEIGHBORS_DISCOVERY_PORT);
                con.setMaxBroadcastHops(1);
                Datagram datagram = con.newDatagram(con.getMaximumLength());
                datagram.writeByte(ND_REQUEST);
                datagram.writeLong(requestID);
                con.send(datagram);
                logger.log(Level.DEBUG, "NeighboursDiscoveryManager.requestForNeighbours: Requesting neighbours reponses.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private synchronized void sendMessage(String to, int type, long reqID) {
            RadiogramConnection connection = null;
                       
            try {
                if (connection == null) {
                    connection = (RadiogramConnection) Connector.open("radiogram://" + to + ":" + NEIGHBORS_DISCOVERY_PORT);
                }

                Datagram datagram = connection.newDatagram(connection.getMaximumLength());
                datagram.writeByte(type);
                datagram.writeLong(reqID);
                connection.send(datagram);
                connection.close();
                logger.log(Level.DEBUG, "NeighborDiscoveryManager.sendMessage: A message (" + ((type == this.ND_RESPONSE) ? "RESPONSE" : ((type == this.ND_RESPONSE_ACK) ? "RESPONSE_ACK" : "UNKNOWN")) + ") sent to: " + to + " with reqID " + reqID + ".");
            } catch (IOException ex) {
                logger.log(Level.DEBUG, "NeighborDiscoveryManager.sendMessage: Exception trying to send a message (" + ((type == this.ND_RESPONSE) ? "RESPONSE" : ((type == this.ND_RESPONSE_ACK) ? "RESPONSE_ACK" : "UNKNOWN")) + ") to: " + to + " with reqID " + reqID + ".");
                ex.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (IOException ex) {
                        logger.log(Level.DEBUG, "NeighborDiscoveryManager.sendMessage: ERROR trying to close the connection");
                    }
                }
            }
        }

        public void run() {
            Hashtable requests = new Hashtable();
            RadiogramConnection connection = null;

            boolean exit = false;

            do {
                try {
                    if (connection == null) {
                        connection = (RadiogramConnection) Connector.open("radiogram://:" + this.NEIGHBORS_DISCOVERY_PORT);
                        connection.setTimeout(-1);
                    }
                    Datagram datagram = connection.newDatagram(connection.getMaximumLength());
                    logger.log(Level.DEBUG, "NeighboursDiscoveryManager.run: Waiting for messages from neighbours.");

                    connection.receive(datagram);
                    byte op = datagram.readByte();
                    long reqID = datagram.readLong();
                    logger.log(Level.DEBUG, "NeighboursDiscoveryManager.run: Message from neighbours: From: " + datagram.getAddress() + ", Op: " + op + ", ReqID: " + reqID);
                    switch (op) {
                        case ND_REQUEST:
                            logger.log(Level.DEBUG, "NeighboursDiscoveryManager.run: A neighbour request received: " + datagram.getAddress() + ", " + reqID + ".");
                            requests.put(datagram.getAddress(), new Long(reqID));
                            sendMessage(datagram.getAddress(), this.ND_RESPONSE, reqID);
                            break;
                        case ND_RESPONSE:
                            logger.log(Level.DEBUG, "NeighboursDiscoveryManager.run: A neighbour response received: " + datagram.getAddress() + ", " + reqID + ".");
                            nodeDegreeInformation.addNeighbour(datagram.getAddress(), reqID);
                            sendMessage(datagram.getAddress(), this.ND_RESPONSE_ACK, reqID);
                            break;
                        case ND_RESPONSE_ACK:
                            logger.log(Level.DEBUG, "NeighboursDiscoveryManager.run: A neighbour response ack received: " + datagram.getAddress() + ", " + reqID + ".");
                            Object aux = requests.get(datagram.getAddress());
                            if (aux != null) {
                                long valor = ((Long) aux).longValue();
                                if (valor == reqID) {
                                    nodeDegreeInformation.addNeighbour(datagram.getAddress());
                                }
                            } else {
                                logger.log(Level.DEBUG, "NeighboursDiscoveryManager.run: A neighbour response ack received but there is no previous request.");
                            }
                            break;
                    }
                } catch (IOException exception) {
                    logger.log(Level.WARNING, "NeighboursDiscoveryManager.run: Exception. Activate DEBUG Level for printing the stack trace.");
                    if (logger.getLevel().equals(Level.DEBUG)) {
                        exception.printStackTrace();
                    }
                }
            } while (!exit);
        }
    }

    private class NDInformation {

        private static final long EXPIRY_TIME = 10000;

        private long requestID;
        private final Hashtable neighborTable;
        private final Object lock;

        public NDInformation() {
            requestID = -1;
            neighborTable = new Hashtable();
            lock = new Object();
            logger.log(Level.DEBUG, "NDInformation: A new NDInformation object instantiated.");
        }

        public void addNeighbour(String who, long reqID) {
            synchronized (lock) {
                if (requestID == -1) {
                    logger.log(Level.DEBUG, "NDInformation.addNeighbour: Response from " + who + " rejected as no previous request was sent.");
                    return;
                }

                if (requestID != reqID) {
                    logger.log(Level.DEBUG, "NDInformation.addNeighbour: Response from " + who + " rejected reqIDs don't match.");
                    return;
                }

                this.addNeighbour(who);
            }
        }

        public void addNeighbour(String who) {
            synchronized (lock) {
                long timestamp = System.currentTimeMillis();
                if (neighborTable.containsKey(who)) {
                    logger.log(Level.DEBUG, "NDInformation.addNeighbour: Request from (" + who + ") accepted. There are " + neighborTable.size() + " neighbours.");
                } else {
                    logger.log(Level.DEBUG, "NDInformation.addNeighbour: " + who + " has sent more than one response.");
                }

                neighborTable.put(who, Long.valueOf(timestamp));
                displayInformation();
            }
        }

        public void displayInformation() {
            long nd = neighborTable.size();

            StringBuffer neighborsCSV = new StringBuffer();
            Enumeration neighbors = neighborTable.keys();

            while (neighbors.hasMoreElements()) {
                neighborsCSV.append(neighbors.nextElement());
                if (neighbors.hasMoreElements()) {
                    neighborsCSV.append(",");
                }
            }
            logger.setDestination(Logger.RECORD);
            logger.debug(neighborsCSV.toString());
            logger.setDestination(Logger.SYSTEM_OUTPUT);
        }

        public long getNodeDegree() {
            synchronized (lock) {
                long nodeDegree = (long) neighborTable.size();
                logger.log(Level.DEBUG, "NDInforamtion.getNodeDegree: Number of neighbours: " + nodeDegree + ".");
                return nodeDegree;
            }
        }

        public Vector getNeighborList() {
            synchronized (lock) {
                Vector neighborList = new Vector();

                Enumeration neighbors = neighborTable.keys();

                while (neighbors.hasMoreElements()) {
                    neighborList.addElement(neighbors.nextElement());
                }

                return neighborList;
            }
        }

        public void changeReqID(long reqID) {
            synchronized (lock) {
                if (reqID >= 0) {
                    requestID = reqID;
                    logger.log(Level.DEBUG, "NDInformation.changeReqID: New request with ID " + reqID + " registered.");
                } else {
                    logger.log(Level.DEBUG, "NDInformation.changeReqID: New request with ID " + reqID + " rejected.");
                }

                // Update neighbor table
                long timestamp = System.currentTimeMillis();
                Enumeration neighbors = neighborTable.keys();

                logger.debug("NDInformation.changeReqID: updating neighborTable");
                while (neighbors.hasMoreElements()) {
                    Object key = neighbors.nextElement();
                    long lastTimestamp = ((Long) neighborTable.get(key)).longValue();
                    if ((timestamp - lastTimestamp) > EXPIRY_TIME) {
                        logger.debug("NDInformation.changeReqID: neighbor " + key + " has been in the table since " + lastTimestamp + " and is now EXPIRED (" + timestamp + ")");
                        neighborTable.remove(key);
                    } else {
                        logger.debug("NDInformation.changeReqID: neighbor " + key + " has been in the table since " + lastTimestamp + " and is now ALIVE (" + timestamp + ")");

                    }
                }
            }
        }
    }

    private class NDTimer implements Runnable {

        private final long REQUEST_PERIOD = 10000;

        private final NeighborsDiscoveryMgr neighboursDiscoveryMgr;

        public NDTimer(NeighborsDiscoveryMgr neighboursDiscoveryMgr) {
            if (neighboursDiscoveryMgr == null) {
                logger.log(Level.DEBUG, "NDTimer: no manager for discovering neighbours has been provided.");
                throw new IllegalArgumentException("NDTimer: no manager for discovering neighbours has been provided.");
            }
            this.neighboursDiscoveryMgr = neighboursDiscoveryMgr;
            logger.log(Level.DEBUG, "NDTimer: a new timer for updating the node degree has been started.");
        }

        public void run() {
            boolean exit = false;
            logger.log(Level.DEBUG, "NDTimer: Timer started.");
            do {
                this.neighboursDiscoveryMgr.requestForNeighbors();
                logger.log(Level.DEBUG, "NDTimer: a new request for neighbours has been invoked.");
                Utils.sleep(REQUEST_PERIOD);
            } while (!exit);
            logger.log(Level.DEBUG, "NDTimer: the timer for updating the node degree is stopped.");
        }
    }

    private final Logger logger = new Logger();
    private final NDInformation neighborsInfo;

    public NodeDegreeManagerV2() {
        neighborsInfo = new NDInformation();
        NeighborsDiscoveryMgr discoveryMgr = new NeighborsDiscoveryMgr(neighborsInfo);
        new Thread(new NDTimer(discoveryMgr)).start();

    }

    public long getNodeDegree() {
        return this.neighborsInfo.getNodeDegree();
    }

    public Vector getNeighborList() {
        return this.neighborsInfo.getNeighborList();
    }

}
