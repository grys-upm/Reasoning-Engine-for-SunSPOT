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

import com.sun.spot.core.util.Queue;
import com.sun.spot.core.util.Utils;
import com.sun.spot.multihop.io.j2me.radiogram.Radiogram;
import com.sun.spot.multihop.io.j2me.radiogram.RadiogramConnection;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.NeighborDiscoveryException;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.NeighborDiscoveryProtocol;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.pdu.NeighborDiscoveryPDU;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.pdu.NeighborDiscoveryPDUException;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 * OnDemandNeighborDiscovery implements a basic three-tiered protocol for doing
 * an on demand neighbor discovery.
 *
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class OnDemandNeighborDiscovery implements NeighborDiscoveryProtocol, OnDemandNeighborDiscoveryListener {

    /**
     * Default port for neighbor discovery communications.
     */
    public static final int DEFAULT_PORT = 200;
    /**
     * Default interval for the internal incoming message handler.
     */
    public static final long DEFAULT_HANDLER_INTERVAL = 100;

    private OnDemandNeighborTableManager neighborTableManager;
    private OnDemandNeighborDiscoveryRequester ndRequester;
    private OnDemandNeighborDiscoveryCommunicationsListener ndCommunicationsListener;
    private Thread ndCommunicationsListenerThread;
    private Vector incomingDataQueue;
    private boolean running;
    private Logger logger;

    private Queue NDRadiogramsQueue;
    private NDEngine ndMessageProcessor;
    private Thread ndMessageProcessorThread;
    private SystemContext context;

    /**
     * The main constructor for this class provides the basic functionality of
     * the three-tiered DEMANES/UPM Neighbor Discovery Protocol.
     */
    public OnDemandNeighborDiscovery() {
        this.neighborTableManager = new OnDemandNeighborTableManager();
        this.ndRequester = new OnDemandNeighborDiscoveryRequester(DEFAULT_PORT);

        this.logger = SystemContext.getLogger();

        this.NDRadiogramsQueue = new Queue();
    }

    /**
     * Starts the neighbor discovery daemon.
     */
    public void start() {
        this.running = true;

        this.ndCommunicationsListener = new OnDemandNeighborDiscoveryCommunicationsListener(DEFAULT_PORT);
        this.ndCommunicationsListener.addListener(this);
        this.ndMessageProcessor = new NDEngine();

        this.ndCommunicationsListenerThread = new Thread(this.ndCommunicationsListener);
        this.ndMessageProcessorThread = new Thread(this.ndMessageProcessor);

        this.ndCommunicationsListenerThread.start();
        this.ndMessageProcessorThread.start();
    }

    /**
     * Stops the neighbor discovery daemon.
     */
    public void stop() {
        this.running = false;
        this.ndCommunicationsListener.setActive(false);
        try {
            this.ndCommunicationsListenerThread.join();
            this.ndMessageProcessorThread.join();
        } catch (InterruptedException ex) {
            if (SystemContext.isDebugActive() & SystemContext.isUSBConnected()) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Indicate whether the neighbor discovery is running or not.
     *
     * @return True if the neighbor discovery is active, false otherwise.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets the neighbor discovery running state.
     *
     * @param handlerAlive True if active, false otherwise.
     */
    public void setRunning(boolean handlerAlive) {
        this.running = handlerAlive;
    }

    /**
     * Get the actual number of active neighbors (node degree).
     *
     * @return The number of current active neighbors.
     */
    public long getNodeDegree() {
        return this.neighborTableManager.getNeighborCount();
    }

    /**
     * Get the list if active neighbors.
     *
     * @return The list of current active neighbors.
     */
    public Vector getNeighborList() {
        return this.neighborTableManager.getNeighborList();
    }

    /**
     * Notifies the parsing method that a new neighbor discovery message has
     * been received.
     *
     * @param incomingData The unprocessed neighbor discovery message.
     */
    public void notify(Radiogram incomingData) {
        NDRadiogramsQueue.put(incomingData);
        logger.debug("OnDemandNeighborDiscovery.notify: added incoming datagram from " + incomingData.getAddress() + " to the queue");
    }

    /**
     * Performs a new neighbor discovery requests.
     */
    public void doNeighborDiscovery() {
        neighborTableManager.clearTable();
        ndRequester.doRequest();
    }

    /**
     * Private class that implements the neighbor discovery protocol.
     */
    private class NDEngine implements Runnable {

        public void run() {
            Radiogram message;
            while (running) {
                try {
                    if (!NDRadiogramsQueue.isEmpty()) {
                        logger.debug("OnDemandNeighborDiscoveryIncomingMessages.run: Processing queue...");
                        Radiogram incomingRadiogram = (Radiogram) NDRadiogramsQueue.get();

                        String address = incomingRadiogram.getAddress();

                        logger.debug("OnDemandNeighborDiscoveryIncomingMessages.run: Queue datagram from " + address);

                        try {
                            NeighborDiscoveryPDU ndPDU = NeighborDiscoveryPDU.parsePDU(incomingRadiogram.getData());
                            logger.debug("OnDemandNeighborDiscoveryIncomingMessages.run: Processing PDU of type " + ndPDU.getTypeOfPDU() + " from " + address);

                            switch (ndPDU.getTypeOfPDU()) {
                                case NeighborDiscoveryPDU.NDPDU_REQUEST:
                                    long incomingRequestID;
                                    incomingRequestID = ndPDU.getRequestID();
                                    logger.debug("OnDemandNeighborDiscoveryIncomingMessages.run: Recevied a REQUEST message from " + incomingRadiogram.getAddress() + " with requestID " + incomingRequestID);
                                    sendMessage(address, NeighborDiscoveryPDU.toByteArray(NeighborDiscoveryPDU.NDPDU_RESPONSE, incomingRequestID));
                                    break;
                                case NeighborDiscoveryPDU.NDPDU_RESPONSE:
                                    if (ndRequester.checkRequestID(ndPDU.getRequestID())) {
                                        logger.debug("OnDemandNeighborDiscoveryIncomingMessages.run: Received a RESPONSE message with VALID requestID " + ndPDU.getRequestID());
                                    } else {
                                        logger.debug("OnDemandNeighborDiscoveryIncomingMessages.run: Received a RESPONSE message with INVALID requestID " + ndPDU.getRequestID());
                                    }
                                    neighborTableManager.addNeighbor(address);
                                    break;
                                case NeighborDiscoveryPDU.NDPDU_RESPONSE_ACK:
                                    break;
                                default:
                                    throw new NeighborDiscoveryException("OnDemandNeighborDiscoveryIncomingMessages.run: Unrecognized PDU type.");
                            }
                        } catch (NeighborDiscoveryPDUException ex) {
                            logger.debug("OnDemandNeighborDiscoveryIncomingMessages.run: Processing PDU exception...");
                        } catch (NeighborDiscoveryException ex) {
                            logger.debug("OnDemandNeighborDiscoveryIncomingMessages.run: Unexpected exception...");
                        }
                    }
                    Utils.sleep(DEFAULT_HANDLER_INTERVAL);
                } catch (Exception ex) {
                    if (SystemContext.isDebugActive() & SystemContext.isUSBConnected()) {
                        ex.printStackTrace();
                    }
                    Utils.sleep(DEFAULT_HANDLER_INTERVAL);
                }
            }
        }

        private void sendMessage(String address, byte[] generatePDU) {
            RadiogramConnection connection = null;

            try {
                connection = (RadiogramConnection) Connector.open("radiogram://" + address + ":" + DEFAULT_PORT);
                Radiogram responseDatagram = (Radiogram) connection.newDatagram(connection.getMaximumLength());

                responseDatagram.write(generatePDU);

                connection.send(responseDatagram);
            } catch (IOException ex) {
                logger.debug("OnDemandNeighborDiscovery.sendMessage: Unexpected error trying to send datagram to " + address);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (IOException ex) {
                        logger.debug("OnDemandNeighborDiscovery.sendMessage: Error closing connection after send.");
                    }
                }
            }
        }

    }
}
