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
package eu.artemis.demanes.impl.SunSPOT.common;

/**
 * DEMANES {@code Obervation} and {@code Action} resource URN 
 * 
 * @author Vicente HErn&aacute;ndez D&iacute;
 * @version 1.0.0
 */
public class DEMANESResources {
    /** Battery Level Observation URN. */
    public static final String BLObservationURN = "urn:demanes:cmsdemo:observation:BLObservation";
    /** Node Degree Observation URN. */
    public static final String NDObservationURN = "urn:demanes:cmsdemo:observation:NDObservation";
    /** Power Transmission Action URN. */
    public static final String PTActionURN = "urn:demanes:cmsdemo:action:updatePowerTransmission";
    /** Trigger Reason Observation URN. */
    public static final String TriggerReasonURN = "urn:demanes:cmsdemo:observation:TriggerReason";
    /** Trigger Reason Value Observation URN. */
    public static final String TriggerReasonValueURN = "urn:demanes:cmsdemo:observation:TriggerReasonValue";
    
    /** Variable name for DELTA CR. */
    public static final String DELTA_CR_KEY = "delta_cr";
}
