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

import com.sun.spot.multihop.io.j2me.radiogram.RadiogramConnection;
import com.sun.squawk.util.NotImplementedYetException;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import java.io.IOException;
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
public class NodeDegreeManagerV1 implements NeighborDiscoveryProtocol {

    public Vector getNeighborList() {
        Vector sources = this.neighboursInfo.sources;
        
        return sources;
    }

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
                do {
                    requestID = randomGenerator.nextLong();
                } while (requestID < 0);

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

        private long requestID;
        private long nodeDegreeOld;
        private boolean flag;
        private long nodeDegree;
        private final Vector sources;
        private final Object lock;

        public NDInformation() {
            flag = false;
            nodeDegreeOld = 0;
            requestID = -1;
            nodeDegree = 0;
            sources = new Vector();
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
//                if (!sources.contains(who)) {
//                    nodeDegree++;
//                    sources.addElement(who);
//                    logger.log(Level.DEBUG, "NDInformation.addNeighbour: New neighbour (" + who + ") added. There are " + nodeDegree + " neighbours.");
//                } else {
//                    logger.log(Level.DEBUG, "NDInformation.addNeighbour: " + who + " has sent more than one response.");
//                }
            }
        }

        public void addNeighbour(String who) {
            synchronized (lock) {
                if (!sources.contains(who)) {
                    nodeDegree++;
                    // -- BEGIN clean this up later
                    // if property.log.level <= FINE *** CHANGE LOG LEVELS POLICY
                    long nd = flag ? nodeDegreeOld : nodeDegree;
                    logger.setDestination(Logger.RECORD);
                    logger.debug("Adding " + who + " to neighbour list.");
                    logger.setDestination(Logger.SYSTEM_OUTPUT);

                    // -- END clean this up later
                    sources.addElement(who);
                    logger.log(Level.DEBUG, "NDInformation.addNeighbour: Request from (" + who + ") accepted. There are " + nodeDegree + " neighbours.");
                } else {
                    logger.log(Level.DEBUG, "NDInformation.addNeighbour: " + who + " has sent more than one response.");
                }
            }
        }

        public long getNodeDegree() {
            synchronized (lock) {
                long res;
                if (flag) {
                    res = this.nodeDegreeOld;
                } else {
                    res = this.nodeDegree;
                }
                logger.log(Level.DEBUG, "NDInforamtion.getNodeDegree: Number of neighbours: " + res + ".");
                return res;
            }
        }

        public void changeReqID(long reqID) {
            synchronized (lock) {
                nodeDegreeOld = nodeDegree;
                new Thread(
                        new Runnable() {
                            public void run() {
                                flag = true;
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException ex) {
                                }
                                flag = false;
                            }
                        }
                ) {
                }.start();
                if (reqID >= 0) {
                    requestID = reqID;
                    nodeDegree = 0;
                    sources.removeAllElements();
                    logger.log(Level.DEBUG, "NDInformation.changeReqID: New request with ID " + reqID + " registered.");
                } else {
                    logger.log(Level.DEBUG, "NDInformation.changeReqID: New request with ID " + reqID + " rejected.");
                }
            }
        }
    }

    private class NDTimer implements Runnable {

        private final long REQUEST_PERIOD = 5000;

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
                try {
                    Thread.sleep(REQUEST_PERIOD);
                } catch (InterruptedException ex) {
                    exit = true;
                }
            } while (!exit);
            logger.log(Level.DEBUG, "NDTimer: the timer for updating the node degree is stopped.");
        }
    }

    private final Logger logger = new Logger();
    private final NDInformation neighboursInfo;

    public NodeDegreeManagerV1() {
        neighboursInfo = new NDInformation();
        NeighborsDiscoveryMgr discoveryMgr = new NeighborsDiscoveryMgr(neighboursInfo);
        new Thread(new NDTimer(discoveryMgr)).start();

    }

    public long getNodeDegree() {
        return this.neighboursInfo.getNodeDegree();
    }

}
