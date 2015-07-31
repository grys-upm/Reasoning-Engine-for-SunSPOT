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

import com.sun.spot.core.peripheral.TimeoutException;
import com.sun.spot.core.util.PrettyPrint;
import com.sun.spot.multihop.io.j2me.radiogram.Radiogram;
import com.sun.spot.multihop.io.j2me.radiogram.RadiogramConnection;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;

/**
 * OnDemandNeighborDiscoveryCommunicationsListener executes a thread for
 * listening neighbor discovery requests from other neighbors.
 *
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class OnDemandNeighborDiscoveryCommunicationsListener implements Runnable {

    private Vector listeners;
    private int port;

    private boolean active;
    private Logger logger;

    /**
     * Creates a new OnDemandNeighborDiscoveryCommunicationsListener on port
     * <i>port</i>
     *
     * @param port Port used for listening neighbor discovery protocol messages.
     */
    public OnDemandNeighborDiscoveryCommunicationsListener(int port) {
        this.listeners = new Vector();
        this.port = port;
        this.active = true;
        this.logger = SystemContext.getLogger();
    }

    /**
     * Gets the port used in this listener.
     *
     * @return The port used in this listener.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets a new port for listening to neighbor requests.
     *
     * @param port The new port.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Indicate whether this listener is active or not.
     *
     * @return True if the listener is active, false otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the active status for this listener.
     *
     * @param active Sets the status for this listener, true for active, false
     * otherwise.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Adds a listener for parsing new neigbor discovery messages.
     *
     * @param listener The listener for new messages.
     */
    public void addListener(OnDemandNeighborDiscoveryListener listener) {
        listeners.addElement(listener);
    }

    /**
     * Removes a listener
     *
     * @param listener The listener to be removed.
     */
    public void removeListener(OnDemandNeighborDiscoveryListener listener) {
        listeners.removeElement(listener);
    }

    /**
     * The run method is used to be executed as a separate thread, providing a
     * loop for listening for new neighbor discovery messages and parsing them.
     */
    public void run() {
        RadiogramConnection connection = null;
        Radiogram incomingDatagram;

        while (active) {
            try {
                if (connection == null) {
                    connection = (RadiogramConnection) Connector.open("radiogram://:" + this.port);
                }
                incomingDatagram = (Radiogram) connection.newDatagram(connection.getMaximumLength());

                connection.receive(incomingDatagram);
                Radiogram receivedDatagram = incomingDatagram;
                logger.debug("NeighboursDiscoveryCommunicationsListener.run: Received incoming datagram from " + incomingDatagram.getAddress());
                logger.debug(PrettyPrint.prettyPrint(receivedDatagram.getData()));

                Enumeration listenerList = listeners.elements();
                while (listenerList.hasMoreElements()) {
                    ((OnDemandNeighborDiscoveryListener) listenerList.nextElement()).notify(receivedDatagram);
                }

            } catch (TimeoutException exception) {
                // Do nothing
            } catch (IOException exception) {
                logger.warning("NeighboursDiscoveryCommunicationsListener.run: Exception. Activate DEBUG Level for printing the stack trace.");
                if (SystemContext.isDebugActive() & SystemContext.isUSBConnected()) {
                    exception.printStackTrace();
                }
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ex) {
                if (SystemContext.isDebugActive() & SystemContext.isUSBConnected()) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
