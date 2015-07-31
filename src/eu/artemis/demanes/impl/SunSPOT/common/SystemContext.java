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
 * You can get a copy of the license terms in licenses/LICENSE.
 */
package eu.artemis.demanes.impl.SunSPOT.common;

import com.sun.spot.core.resources.Resources;
import com.sun.spot.espot.peripheral.ESpot;
import eu.artemis.demanes.impl.SunSPOT.communications.CommunicationManager;
import eu.artemis.demanes.impl.SunSPOT.reconfiguration.actions.PowerTransmissionActuator;
import eu.artemis.demanes.impl.SunSPOT.utils.LEDMarquee.Marquee;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;

/**
 * System Context defines all the objects in the system context that are
 * accesible to other objects.
 *
 * @author N&eacute;stor Lucas Mart&iacute;nez
 */
public class SystemContext {
    private static final CommunicationManager communicationManager = new CommunicationManager();
    private static final Logger logger = new Logger();
    private static final Marquee marquee = new Marquee();
    
    private static final boolean USBStatus = ((ESpot) Resources.lookup(ESpot.class)).getUsbPowerDaemon().isUsbPowered();
    private static final Level logLevel = Level.INFO;
    
    private PowerTransmissionActuator ptact;

    /**
     * 
     */
    public SystemContext() {        
        marquee.start();
    }    
    
    /**
     * Get the communication manager of the system.
     * 
     * @return The Communication Manager of the system.
     */
    public CommunicationManager getCommunicationManager() {
        return this.communicationManager;
    }
    
    /**
     * Get the logger of the system.
     * 
     * @return The logger of the system.
     */
    public static Logger getLogger() {
        return logger;
    }
    
    /**
     * Get the LED Marquee of the system.
     * 
     * @return The LED Marquee.
     */
    public static Marquee getMarquee() {
        return marquee;
    }
    
    /**
     * Check if the system is USB connected.
     * 
     * @return Tru if the mote is USB powered, false otherwise.
     */
    public static boolean isUSBConnected() {
        return USBStatus;
    }
    
    /**
     * Check if debug mode is active.
     * 
     * @return True if debug mode is active, false otherwise.
     */
    public static boolean isDebugActive() {
        return (logLevel.intValue() <= Level.DEBUG.intValue());
    }
    
    /**
     * Set the Transmission Power Actuator for the system.
     * 
     * @param ptact The transmission power actuator for the system.
     */
    public void setPTACT(PowerTransmissionActuator ptact) {
        this.ptact = ptact;
    }
    
    /**
     * Get the Transmission Power Actuator of the system.
     * 
     * @return The transmision power actuator of the system.
     */
    public PowerTransmissionActuator getPTACT() {
        return this.ptact;
    }
}
