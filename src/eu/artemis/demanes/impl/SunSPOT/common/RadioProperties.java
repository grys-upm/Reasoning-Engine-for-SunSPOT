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
import com.sun.spot.ieee_802_15_4_radio.IRadioPolicyManager;
import com.sun.squawk.util.Arrays;

/**
 *
 * @author Néstor Lucas Martínez &lt;nestor.lucas@upm.es&gt;
 */
public class RadioProperties {
    // CONSTANTS
    /** The sorted array of general available transmission powers. */
    private final static int[] availablePTX = {-32, -31, -30, -25, -22, -19, -17, -15, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0};
        
    /** The sorted array of general available transmission powers for channel 26. They are restricted due to FCC regulations. */
    private final static int[] availablePTXChannel26 = {-32, -31, -30, -25, -22, -19, -17, -15, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3};
    
    /** The minimum power of transmission in dBm. */
    public final static int MINIMUM_POWER_TRANSMISSION = -32;
    
    /** The maximum power of transmission in dBm. */
    public final static int MAXIMUM_POWER_TRANSMISSION = 0;      
    
    /** ERROR CONSTANT. */
    public final static int RADIO_PROPERTIES_ERROR = -1;
    
    /**
     * Static method to obtain the maximum index in the available transmission
     * powers array.
     * 
     * @param channel The channel for which the maximum index is required.
     * @return        The maximum index fir the requested channel.
     */
    public static int getMaximumPTXIndex(int channel) {
        if (channel == 26) {
            return availablePTXChannel26.length - 1;
        }
        else
            return availablePTX.length - 1;
    }
    
    /**
     * Static method to obtain the minimum index in the available transmission
     * powers array. It will always be 0, so this method is just to complement
     * the getMaximumPTXIndex.
     * 
     * @param channel The channel for which the maximum index is required.
     * @return        The maximum index fir the requested channel.
     */
    public static int getMinimumPTXIndex(int channel) {
        // The minimum index is always 0. It could have been coded as a constant,
        // but this way it keeps coherence with the getMaximumPTXIndex method.
        return 0;
    }
    
    /**
     * Static method to obtain the index of a given power of transmission
     * related to the REAL available ones.
     * 
     * @param channel The channel for which the request is done.
     * @param ptx     The transmission power which is asked for
     * @return        The index of the transmission power in the list
     */
    public static int getPTXIndex(int channel, int ptx) {
        // This must be improved to consider the chance of being invoked with values not listed.
        if (channel == 26) {
            return Arrays.binarySearch(availablePTXChannel26, ptx);
        }
        else {
            return Arrays.binarySearch(availablePTX, ptx);
        }
    }
    
    /**
     * Static method to obtain the transmission power given an index.
     * 
     * @param channel The chanel for which the request is done.
     * @param index   The index of the desired transmission power.
     * @return        The desire transmission power, or RADIO_PROPERTIES_ERROR if the index is out of bounds.
     */
    public static int getPTXatIndex(int channel, int index) {
        if (channel == 26) {
            if ((index < 0) || (index > availablePTXChannel26.length)) {
                return RADIO_PROPERTIES_ERROR;
            }
            return availablePTXChannel26[index];
        }
        else {
            if ((index < 0) || (index > availablePTX.length)) {
                return RADIO_PROPERTIES_ERROR;
            }
            return availablePTX[index];
        }
    }
    
    /**
     * Static method to obtain the actual radio channel in use.
     * 
     * @return The actual radio channel in use.
     */
    public static int getRadioChannel() {
        return ((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getChannelNumber();
    }
    
    /**
     * Static method to obtain the actual transmission power.
     * 
     * @return The actual transmission power.
     */
    public static int getPTX() {
        return ((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower();
    }
    
    /**
     * Static method to get the maximum available transmission power in
     * the specified channel.
     * 
     * @param channel The channel.
     * @return The maximum transmission power in the channel, in dBm.
     */
    public static int getMaximumPTX(int channel) {
        if (channel == 26) {
            return availablePTXChannel26[availablePTXChannel26.length - 1];
        }
        else {
            return availablePTX[availablePTX.length - 1];
        }
    }
    
    /**
     * Static method to get the minimum available transmission power in
     * the specified channel.
     *
     * @param channel The channel.
     * @return The minimum transmission power in the channel, in dBm.
     */
    public static int getMinimumPTX(int channel) {
        if (channel == 26) {
            return availablePTXChannel26[0];
        }
        else {
            return availablePTX[0];
        }        
    }
}
