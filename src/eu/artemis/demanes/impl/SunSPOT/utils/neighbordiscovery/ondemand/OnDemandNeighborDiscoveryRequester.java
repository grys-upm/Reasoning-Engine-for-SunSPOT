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

import com.sun.spot.core.util.Utils;
import com.sun.spot.multihop.io.j2me.radiogram.Radiogram;
import com.sun.spot.multihop.io.j2me.radiogram.RadiogramConnection;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.pdu.NeighborDiscoveryPDU;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.pdu.NeighborDiscoveryPDUException;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 * OnDemandNeighborDiscoveryRequester is used to manage the issue of a
 * neighbor discovery request.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class OnDemandNeighborDiscoveryRequester implements Runnable {
    private static final long DEFAULT_INTERVAL = 500;
    private static final int MAXIMUM_VALID_REQUEST_ID = 10;

    private long interval;
    private long requestID;
    private final int port;

    private Vector validRequestIDs;

    private Object lock;
    private Logger logger;
    private boolean active;

    private Random randomGenerator;

    /**
     * Creates a new Neighbor Discovery Requester on port <i>port</i>.
     * 
     * @param port Port for use in the Neighbor Discovery Protocol
     */
    public OnDemandNeighborDiscoveryRequester(int port) {
        this.interval = DEFAULT_INTERVAL;
        this.port = port;

        this.validRequestIDs = new Vector();

        this.lock = new Object();
        this.logger = SystemContext.getLogger();
        this.active = true;

        this.randomGenerator = new Random();
    }

    /**
     * Checks if the Neighbor Discovery Requester is alive.
     *
     * @return true if the Requester is alive. false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the Neighbor Discovery Requester.
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Run method for being run as a thread.
     */
    public void run() {
        while (active) {
            doRequest();
            Utils.sleep(interval);
        }
    }

    /**
     * Generates a new request identifier on demand. The request identifier is
     * always a positive long number.
     *
     * @return a new request identifier
     */
    private long generateRequestID() {
        long generatedRequestID;

        generatedRequestID = this.randomGenerator.nextLong();
        generatedRequestID = generatedRequestID >= 0 ? generatedRequestID : (-1 * generatedRequestID);

        synchronized (lock) {
            if (this.validRequestIDs.size() >= MAXIMUM_VALID_REQUEST_ID) {
                this.validRequestIDs.removeElement(this.validRequestIDs.firstElement());
                this.validRequestIDs.trimToSize();
            }
        }

        this.validRequestIDs.addElement(Long.valueOf(generatedRequestID));
        
        return generatedRequestID;
    }

    /**
     * Checks the validity of a passed by request ID.
     * 
     * @param requestID The request ID to be checked up.
     * @return True if the request ID is valid. False otherwise.
     */
    public boolean checkRequestID(long requestID) {
        synchronized (lock) {
            return this.validRequestIDs.contains(Long.valueOf(requestID));
        }
    }

    /**
     * Performs a neighbor request. 
     */
    public void doRequest() {
        RadiogramConnection connection = null;
        Radiogram requestDatagram;
        byte[] pduRequest;

        requestID = generateRequestID();
        try {
            pduRequest = NeighborDiscoveryPDU.toByteArray(NeighborDiscoveryPDU.NDPDU_REQUEST, requestID);

            if (connection == null) {
                connection = (RadiogramConnection) Connector.open("radiogram://broadcast:" + this.port);
            }

            requestDatagram = (Radiogram) connection.newDatagram(connection.getMaximumLength());
            requestDatagram.reset();
            requestDatagram.write(pduRequest);

            connection.send(requestDatagram);
            logger.debug("NeighborDiscoveryRequester.doRequest: Sent datagram of type REQUEST and requestID " + requestID);
        } catch (NeighborDiscoveryPDUException ex) {
            logger.warning("NeighborDiscoveryRequester.doRequest: A problem occurred while trying to parse a NDP PDU.");
        } catch (IOException ex) {
            logger.warning("NeighborDiscoveryRequester.doRequest: A problem occurred while trying to send a REQUEST.");
        }
    }
}
