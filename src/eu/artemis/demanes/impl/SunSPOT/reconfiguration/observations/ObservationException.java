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

import eu.artemis.demanes.reconfiguration.Observation;

/**
 * Thrown when the {@code Observation} catches an error.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
class ObservationException extends Exception {

    /**
     * Constructs a {@code ObservationException} with the specified
     * detail message.
     * 
     * @param observation The detail message.
     */
    public ObservationException(String message, Observation observation) {
        super(observation.getClass().toString() + ": " + message);
    }
    
}
