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
package eu.artemis.demanes.impl.SunSPOT.reconfiguration.observations;

import com.sun.spot.core.resources.Resources;
import com.sun.spot.core.util.Properties;
import com.sun.spot.espot.peripheral.Battery;
import com.sun.spot.espot.peripheral.Battery8;
import com.sun.spot.espot.peripheral.ESpot;
import com.sun.spot.espot.peripheral.IPowerController;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.datatypes.ANES_URN_Exception;
import eu.artemis.demanes.exceptions.ObservationInvocationException;
import eu.artemis.demanes.impl.SunSPOT.common.DEMANESResources;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import eu.artemis.demanes.reconfiguration.Observation;

/**
 * {@code BatteryLevelObservation} provides the battery level.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class BatteryLevelObservation implements Observation {
    private ANES_URN urn;
    private Battery8 battery8;
    private Battery battery;
    private boolean rev6;
    private Logger logger;

    /**
     * Public constructor for {@code BatteryLevelObservation}.
     * 
     * @param properties The set of reconfiguration properties.
     * @param urn The urn for the observation.
     */    
    public BatteryLevelObservation(Properties properties, String urn) {
        ESpot espot = (ESpot) Resources.lookup(ESpot.class);
        IPowerController powerController = espot.getPowerController();
        try {
            this.battery8 = (Battery8) powerController.getBattery();
            this.rev6 = true;
        }
        catch (RuntimeException exception) {
            this.battery = (Battery) powerController.getBattery();
            this.rev6=false;
        }
        
        this.logger = SystemContext.getLogger();
    }

    /**
     * Gets the ID of an observation.
     * 
     * @return the ANES_URN of observation.
     */
    public ANES_URN getObservationID() {
        try {
            return ANES_URN.create(DEMANESResources.BLObservationURN);
        } catch (ANES_URN_Exception ex) {
            logger.log(Level.SEVERE, "NodeDegreeObservation.getObservationID: ANES_URN.create reported and error with DEMANESResources.BLObservationURN.");
            return null;
        }
    }

    /**
     * Get the batteries remaining capacity in mAh.
     * 
     * @return the capacity in milliampere-hour.
     * @throws ObservationInvocationException 
     */
    public Object getValue() throws ObservationInvocationException {
        if (rev6) {
            return Double.valueOf(this.battery8.getAvailableCapacity());
        } else {
            return Double.valueOf(this.battery.getAvailableCapacity());
        }
    }
}
