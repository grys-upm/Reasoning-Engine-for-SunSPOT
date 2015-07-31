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

import com.sun.spot.core.resources.transducers.LEDColor;
import com.sun.spot.ieee_802_15_4_radio.util.IEEEAddress;
import com.sun.spot.multihop.radio.IDataEventListener;
import com.sun.spot.multihop.radio.mhrp.interfaces.ILQRPEventListener;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;

/**
 * {@code RoutingListener} implements a listener for routing events, so they
 * can be notified to the user through a LED marquee.
 *
 * @author N&eacute;stor Lucas Mart&iacute;nez
 */
public class RoutingListener implements ILQRPEventListener, IDataEventListener {

    private static final LEDColor RREQ_COLOR = LEDColor.WHITE;
    private static final LEDColor RREP_COLOR = LEDColor.GREEN;
    private static final LEDColor RERR_COLOR = LEDColor.RED;
    private static final LEDColor DATA_COLOR = LEDColor.BLUE;

    /**
     * 
     */
    public RoutingListener() { }

    /**
     * Method called when a RREQ is sent.
     * 
     * @param originator Route request originator.
     * @param destination Route request destination.
     * @param hopCount Number of hops to the destination.
     * @param routeCost Cost to the destination.
     */
    public void RREQSent(long originator, long destination, int hopCount, double routeCost) {
        SystemContext.getMarquee().blinkActivity1(RREQ_COLOR);
    }

    /**
     * Method called when a RREP is sent.
     * 
     * @param originator Route request originator.
     * @param destination Route request destination.
     * @param hopCount Number of hops to the destination.
     * @param routeCost Cost to the destination.
     */
    public void RREPSent(long originator, long destination, int hopCount, double routeCost) {
        SystemContext.getMarquee().blinkActivity1(RREP_COLOR);
    }

    /**
     * Method called when a RERR is sent.
     * 
     * @param originator Route request originator.
     * @param destination Route request destination.
     */
    public void RERRSent(long originator, long destination) {
        SystemContext.getMarquee().blinkActivity1(RERR_COLOR);
    }

    /**
     * Method called when a RREQ is received.
     * 
     * @param originator Route request originator.
     * @param destination Route request destination.
     * @param lastHop Route request last hop.
     * @param hopCount Number of hops to the destination.
     * @param routeCost Cost to the destination.
     */
    public void RREQReceived(long originator, long destination, long lastHop, int hopCount, double routeCost) {
        SystemContext.getMarquee().blinkActivity2(RREQ_COLOR);
    }

    /**
     * Method called when a RREP is received.
     * 
     * @param originator Route request originator.
     * @param destination Route request destination.
     * @param lastHop Route request last hop.
     * @param hopCount Number of hops to the destination.
     * @param routeCost Cost to the destination.
     */
    public void RREPReceived(long originator, long destination, long lastHop, int hopCount, double routeCost) {
        SystemContext.getMarquee().blinkActivity2(RREP_COLOR);
    }

    /**
     * Method called when a RERR is received.
     * 
     * @param originator Route request originator.
     * @param destination Route request destination.
     */
    public void RERRReceived(long originator, long destination) {
        SystemContext.getMarquee().blinkActivity2(RERR_COLOR);
    }

    /**
     * Method that is called when data is forwarded through this physical node.
     * 
     * @param lastHop previous node on the multi hop path from which the packet was received.
     * @param nextHop next node on the multi hop path to which the packet is sent.
     * @param originator original sender of the packet.
     * @param destination final destination of the packet.
     */
    public void notifyForward(long lastHop, long nextHop, long originator, long destination) {
        SystemContext.getMarquee().blinkActivity1(DATA_COLOR);
        SystemContext.getMarquee().blinkActivity2(DATA_COLOR);

        SystemContext.getLogger().log(Level.DEBUG, "Forwarding message from " + IEEEAddress.toDottedHex(originator)
                + " to " + IEEEAddress.toDottedHex(destination)
                + " received from " + IEEEAddress.toDottedHex(lastHop)
                + " and being forwarded through " + IEEEAddress.toDottedHex(nextHop));
    }

}
