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
import com.sun.spot.ieee_802_15_4_radio.util.IEEEAddress;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;

/**
 * Properties used by the system.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 */
public class SystemProperties {
    private final static ESpot espot = (ESpot) Resources.lookup(ESpot.class);

    // PROPERTIES NAMES
    /** The SunSPOT property name for its IEEE address, "spot.address". */
    public final static String IEEE_ADDRESS = "spot.address";
    
    /** The SunSPOT property name for the NSS part for construction any URN, "urn.nss.spotpart". */
    public final static String SPOT_URN_NSS = "urn.nss.spotpart";
    
    /** The log level property name, "log.level". */
    public final static String LOG_LEVEL = "log.level";
    
    /** The log destination property name, "log.destination". */
    public final static String LOG_DESTINATION = "log.destination";
    
    /** Base station address. */
    public final static String BASESTATION_ADDR_PROPERTY = "basestation.address";
    
    /** Base station port. */
    public final static String BASESTATION_PORT = "basestation.port";
    
    /** Execution context. */
    public final static String EXECUTION_CONTEXT = "execution.context";
    
    /** Remote management port .*/
    public final static String REMOTE_MANAGEMENT_PORT = "remotemng.port";
    
    
    // DEFAULT VALUES
    /** The SunSPOT IEEE Address. */
    public final static String IEEE_ADDRESS_VALUE = IEEEAddress.toDottedHex(espot.getIEEEAddress());
    
    /** The default value for the NSS part for construction any URN, "SunSPOT" + last four bytes of IEEE address. */
    public final static String SPOT_URN_NSS_VALUE = "sunspot" + IEEE_ADDRESS_VALUE.substring(IEEE_ADDRESS_VALUE.length() - 4);
    
    /** The default log level (ALL) for the whole application. */
    public final static String LOG_LEVEL_VALUE = Level.ALL.toString();
    
    /** The default log destination (STDOUT) for the whole application. */
    public final static String LOG_DESTINATION_VALUE = String.valueOf(Logger.SYSTEM_OUTPUT);
            
    /** The first LED index for the LEDs array. */
    public final static int FIRST_LED = 0;
    
    /** The last LED index for the LEDs array. */
    public final static int LAST_LED = 7;   
    
    /** Default base station address for Den Haag video. */
    public final static String DEFAULT_BS_ADDRESS = "0014.4F01.0000.7B23";
    
    /** Default base station address in {@code Long} format. */
    public final static long DEFAULT_BS_ADDRESSS_LONG = IEEEAddress.toLong(DEFAULT_BS_ADDRESS);
    
    /** Default base station port. */
    public final static int DEFAULT_BS_PORT = 100;
    
    /** Execution context: CMS. */
    public final static byte EXECUTION_CONTEXT_CMS = 0x01;
    
    /** Execution context: Experimental. */
    public final static byte EXECUTION_CONTEXT_EXPERIMENTAL = 0x02;
    
    /** Default execution context. */
    public final static byte DEFAULT_EXECUTION_CONTEXT = EXECUTION_CONTEXT_CMS;
    
    /** Default remote management port. */
    public final static int DEFAULT_REMOTE_MANAGEMENT_PORT = 201;
}
