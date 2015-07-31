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
package eu.artemis.demanes.impl.SunSPOT.reconfiguration.observations;

import com.sun.spot.core.util.Properties;
import eu.artemis.demanes.reconfiguration.Observation;

/**
 * An ObservationFactory is a factory method to provide an {@link Observation}
 * selected accordingly to the instance requested. See{@link ObservationType}
 * for further information regarding the current available observations.
 *  
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class ObservationFactory {

    /**
     * A factory to create instances of requested observations. 
     * 
     * @param observationType The type of observation to be created.
     * @param properties Properties used by the observation.
     * @param urn URN associated to the observation
     * @return The observation object.
     * @throws ObservationFactoryException
     */    
    public static Observation getInstance(ObservationType observationType, Properties properties, String urn) throws ObservationFactoryException {
        switch (observationType.hashCode()) {
            case ObservationType.NODE_DEGREE_OBSERVATION_CODE:
                return new NodeDegreeObservation(properties, urn);
            case ObservationType.BATTERY_LEVEL_OBSERVATION_CODE:
                return new BatteryLevelObservation(properties, urn);
            default:
                throw new ObservationFactoryException(observationType.toString());
        }
    }
}
