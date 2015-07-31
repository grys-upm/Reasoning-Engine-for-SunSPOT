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
package eu.artemis.demanes.impl.SunSPOT.utils.LEDMarquee;

/**
 * Thrown when an error occurs trying to add a new message to the marquee.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class MarqueeAddMessageException extends Exception {
    
    /**
     * Constructs a {@code MarqueeAddMessageException}.
     */
    public MarqueeAddMessageException() {
        super("Exception trying to add a message to the marquee.");
    }

    /**
     * Constructs a {@code MarqueeAddMessageException} with the specified
     * detail message.
     * 
     * @param s The detail message.
     */    
    public MarqueeAddMessageException(String s) {
        super("Exception trying to add a message to the marquee: " + s);
    }
    
}
