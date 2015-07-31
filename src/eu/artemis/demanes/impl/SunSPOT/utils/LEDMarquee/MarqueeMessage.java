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

import com.sun.spot.core.resources.transducers.LEDColor;

/**
 * A Marquee Message.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class MarqueeMessage {
    /** Message length */
    public static final int MESSAGE_LENGTH = 5;
    
    private static final byte STATIC = 0x01;
    private static final byte PROGRESS_BAR = 0x02;
    
    private LEDColor[] message;
    private byte messageType;
    
    /**
     * Public constructor of the LED marquee using a default blank message.
     */
    public MarqueeMessage() {
        this.message = new LEDColor[MESSAGE_LENGTH];
        for (int index=0; index < MESSAGE_LENGTH; index++)
            this.message[index] = new LEDColor(0,0,0);
    }
    
    /**
     * Public constructor of the LED marquee with a {@code message}.
     * 
     * @param message The initial message.
     */
    public MarqueeMessage(LEDColor[] message) {
        this.message = message;
    }

    /**
     * Get the message shown in the marquee.
     * 
     * @return The message.
     */
    public LEDColor[] getMessage() {
        return message;
    }

    /** 
     * Set the message to be shown in the marquee.
     * 
     * @param message The message.
     */
    public void setMessage(LEDColor[] message) {
        this.message = message;
    }   
    
    /**
     * Set a new indicator @code color} at postition {@code position} of the
     * message.
     * 
     * @param position The position.
     * @param color The color.
     */
    public void setMessageAtPosition(int position, LEDColor color) {
        this.message[position] = color;
    }
}
