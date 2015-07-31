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
package eu.artemis.demanes.impl.SunSPOT.communications;

import com.sun.spot.ieee_802_15_4_radio.util.IEEEAddress;
import com.sun.spot.multihop.io.j2me.radiogram.Radiogram;
import com.sun.spot.multihop.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.multihop.radio.NoRouteException;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;

/**
 * Manager for the communications. All communications with other resources
 * should be done through this class.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class CommunicationManager {

    /** Unicast communication mode. */
    public static final int MODE_UNICAST = 0x00;
    /** Anycast communication mode. NOT IMPLEMENTED. */
    public static final int MODE_ANYCAST = 0x01;
    /** Multicast communication mode. NOT IMPLEMENTED. */
    public static final int MODE_MULTICAST = 0x02;
    /** Broadcast communication mode. */
    public static final int MODE_BROADCAST = 0x03;

    /** Message priority normal. */
    public static final int PRIORITY_NORMAL = 0x00;

    /** Message importance normal. */
    public static final int IMPORTANCE_NORMAL = 0x00;
    
    /**
     * 
     */
    public CommunicationManager() { }

    /**
     * Sends a message to a specified destination using the specified
     * mode, priority and importance.
     * 
     * @param message The message to be sent.
     * @param size The size of the message to be sent.
     * @param mode The mode to be used for the communication.
     * @param destination The message destination.
     * @param priority The message priority.
     * @param importance The message importance.
     * @return True if the message is sent, false otherwise.
     */
    public boolean send(ByteArrayOutputStream message, int size, int mode, String destination, int priority, int importance) {
        RadiogramConnection connection = null;
        Radiogram radiogram;
        RadiogramAddress destinationAddress;

        destinationAddress = resolveDestination(destination);

        try {
            switch (mode) {
                case MODE_BROADCAST:
                    connection = (RadiogramConnection) Connector.open("radiogram://broadcast:" + destinationAddress.getPort());
                    radiogram = (Radiogram) connection.newDatagram(connection.getMaximumLength());
                    break;
                case MODE_UNICAST:
                    connection = (RadiogramConnection) Connector.open("radiogram://" + destinationAddress.getAddressAsString() + ":" + destinationAddress.getPort());
                    radiogram = (Radiogram) connection.newDatagram(connection.getMaximumLength());
                    break;               
                    
                default: // Default mode is unicast
                    connection = (RadiogramConnection) Connector.open("radiogram://broadcast:" + destinationAddress.getPort());
                    radiogram = (Radiogram) connection.newDatagram(connection.getMaximumLength());
            }

            radiogram.reset();
            radiogram.write(message.toByteArray());
            connection.send(radiogram);
            return true;
        } catch (NoRouteException ex) {
            SystemContext.getLogger().warning("--- No route found to: " + destinationAddress.getAddressAsString());
        } catch (IOException ex) {
            if (SystemContext.isUSBConnected() & SystemContext.isDebugActive()) {
                ex.printStackTrace();
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException ex) {
                    if (SystemContext.isUSBConnected() & SystemContext.isDebugActive()) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

    /**
     * Resolves the specified destination address.
     * 
     * @param destination The destination address to be resolved.
     * @return The resolved address.
     */
    public RadiogramAddress resolveDestination(String destination) {
        RadiogramAddress radiogramAddress;

        radiogramAddress = new RadiogramAddress();

        radiogramAddress.setPort(Integer.parseInt(destination.substring(destination.lastIndexOf(':') + 1)));
        radiogramAddress.setAddress(IEEEAddress.toLong(destination.substring(0, destination.lastIndexOf(':'))));

        return radiogramAddress;
    }

    /**
     * Inner class implementing a RadiogramAddress.
     */
    class RadiogramAddress {

        private long address;
        private int port;

        /**
         * Get the MAC address as a {@code Long} object.
         * 
         * @return The MAC address.
         */
        public long getAddressAsLong() {
            return address;
        }

        /**
         * Get the MAC address as a {@code String}.
         * 
         * @return The MAC address.
         */
        public String getAddressAsString() {
            return IEEEAddress.toDottedHex(this.address);
        }

        /**
         * Set the MAC address specified as a {@code Long} object.
         * 
         * @param address The MAC address.
         */
        public void setAddress(long address) {
            this.address = address;
        }

        /**
         * Set the MAC address specified as a {@code String}.
         * 
         * @param address The MAC address.
         */
        public void setAddress(String address) {
            this.address = IEEEAddress.toLong(address);
        }

        /**
         * Get the port.
         * 
         * @return The port.
         */
        public int getPort() {
            return port;
        }

        /**
         * Set the port.
         * 
         * @param port The port.
         */
        public void setPort(int port) {
            this.port = port;
        }
    }
}
