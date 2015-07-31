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

/**
 * Properties used by the Power Scaling Monitor ({@code TriggerPolicy}).
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 */
public class TriggerPolicyProperties {

    public static final String TRIGGER_INTERVAL_PROPERTY = "trigger.interval";

    // following parameters are used in the trigger rules
    public static final String KSI_ND_PROPERTY = "KSI_ND";
    public static final String ND_MIN_PROPERTY = "ND_MIN";
    public static final String CR_MAX_PROPERTY = "CR_MAX";
    public static final String CR_MIN_PROPERTY = "CR_MIN";
    public static final String DELTA_ND_MIN_PROPERTY = "DELTA_ND_MIN";
    public static final String DELTA_CR_MIN_PROPERTY = "DELTA_CR_MIN";
}
