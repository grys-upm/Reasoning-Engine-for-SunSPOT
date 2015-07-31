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

import com.sun.spot.core.resources.Resources;
import com.sun.spot.core.resources.transducers.ITriColorLEDArray;
import com.sun.spot.core.resources.transducers.LEDColor;
import com.sun.spot.core.util.Utils;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * Note: Although some parts should be considered to be accessed in a
 * synchronized way allowing different callers for the marquee, the actual usage
 * does not require it.
 *
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class Marquee {

    /** Color used for signaling a "NOT READY" message. */
    public static final LEDColor STATUS_IS_NOT_READY = LEDColor.RED;
    /** Color used for signaling a "READY" message. */
    public static final LEDColor STATUS_IS_READY = LEDColor.GREEN;
    /** Color used for signaling a "STANDBY" message. */
    public static final LEDColor STATUS_IS_STANDBY = LEDColor.YELLOW;

    /** Minimum interval time in milliseconds for rotating the messages. */
    public static final long MINIMUM_INTERVAL = 3000;
    /** Blink interval time in milliseconds. */
    public static final long BLINK_INTERVAL = 200;

    private static final int STATUS_INDICATOR_LED_POSITION = 0;
    private static final int ACTIVITY_1_LED_POSITION = 1;
    private static final int ACTIVITY_2_LED_POSITION = 2;

    private Hashtable messages;
    private long updatingInterval;
    private boolean useStatusIndicator;
    private LEDColor statusIndicator;
    private int messageIdCounter;
    private boolean running;
    private boolean paused;

    private ITriColorLEDArray LEDs;

    private Thread engineThread;

    private SystemContext context;

    /**
     * Public constructor for a new marquee. The created marquee will have no
     * message and will be stopped until {@link eu.artemis.demanes.impl.SunSPOT.utils.LEDMarquee.Marquee#start()}
     * is called.
     */
    public Marquee() {
        this.messages = new Hashtable();
        this.updatingInterval = MINIMUM_INTERVAL;
        this.useStatusIndicator = true;
        this.statusIndicator = STATUS_IS_NOT_READY;
        this.messageIdCounter = 0;
        this.running = false;
        this.paused = false;

        this.LEDs = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    }

    /**
     * Get the updating interval time being used in the marquee.
     * 
     * @return The updating interval time.
     */
    public long getUpdatingInterval() {
        return updatingInterval;
    }

    /**
     * Set the updating interval time to be used in the marquee.
     * 
     * @param updatingInterval The updating interval time.
     */
    public void setUpdatingInterval(long updatingInterval) {
        this.updatingInterval = updatingInterval;
    }

    /**
     * Activates the status indicator.
     */
    public void activateStatusIndicator() {
        this.useStatusIndicator = true;
    }
    
    /**
     * Deactivates the status indicator.
     */
    public void deactivateStatusIndicator() {
        this.useStatusIndicator = false;
    }

    /**
     * Add a {@code message} to the marquee.
     * 
     * @param message The message.
     * @return The ID of the message for further references.
     * @throws MarqueeAddMessageException When the message can not be added to 
     *         the marquee.
     */
    public Object addMessage(MarqueeMessage message) throws MarqueeAddMessageException {
        if (messageIdCounter == Integer.MAX_VALUE) {
            throw new MarqueeAddMessageException("Reached maximum number of messages available.");
        }

        messageIdCounter++;
        Integer key = Integer.valueOf(messageIdCounter);
        messages.put(key, message);

        return (Object) key;
    }
    
    /**
     * Update a message referenced by {@code key} reference ID with the new
     * {@code message}.
     * 
     * @param key The reference key ID.
     * @param message The new message.
     */
    public void updateMessage(Object key, MarqueeMessage message) {
        messages.put(key, message);
    }

    /**
     * Remeve a message referenced by {@code key} refernce ID from the marquee.
     * 
     * @param key The reference key ID.
     */
    public void removeMessage(Object key) {
        messages.remove(key);
    }

    /**
     * Clears de marquee.
     */
    public void clearMarquee() {
        messages.clear();
        LEDs.setRGB(0, 0, 0);
    }

    /**
     * Starts the marquee.
     */
    public void start() {
        if (!this.running) {
            this.running = true;
            this.paused = false;
            this.LEDs.setOn();
            this.engineThread = new Thread(new Engine());
            this.engineThread.start();
        }
    }

    /**
     * Pauses the marquee.
     */
    public void pause() {
        paused = true;
    }

    /**
     * Unpauses the marquee.
     */
    public void unpause() {
        paused = false;
    }

    /**
     * Stops the marquee.
     */
    public void stop() {
        if (this.running) {
            this.paused = true;
            this.running = false;
        }
        try {
            this.engineThread.join();
            this.LEDs.setOff();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets the color of the status indicator according to {@code status}.
     * 
     * @param status The new status color for the status indicator.
     */
    public void setStatusIndicator(LEDColor status) {
        this.statusIndicator = status;
    }

    /**
     * Blinks the first activity LED indicator using the {@code activityColor}
     * color.
     * 
     * @param activityColor The color for the blink.
     */
    public void blinkActivity1(LEDColor activityColor) {
        LEDs.getLED(ACTIVITY_1_LED_POSITION).setColor(activityColor);
        try{Thread.sleep(BLINK_INTERVAL);} catch(InterruptedException ie){}
        LEDs.getLED(ACTIVITY_1_LED_POSITION).setRGB(0, 0, 0);
    }

    /**
     * Blinks the second activity LED indicator using the {@code activityColor}
     * color.
     * 
     * @param activityColor The color for the blink.
     */
    public void blinkActivity2(LEDColor activityColor) {
        LEDs.getLED(ACTIVITY_2_LED_POSITION).setColor(activityColor);
        try{Thread.sleep(BLINK_INTERVAL);} catch(InterruptedException ie){}
        LEDs.getLED(ACTIVITY_2_LED_POSITION).setRGB(0, 0, 0);
    }

    private class Engine implements Runnable {
        
        public void run() {
            MarqueeMessage message;

            LEDs.setOn();

            while (running) {
                while (!paused) {
                    try {
                        LEDs.getLED(STATUS_INDICATOR_LED_POSITION).setColor(statusIndicator);

                        if (!messages.isEmpty()) {
                            Enumeration messageList = messages.keys();
                            while (messageList.hasMoreElements()) {
                                message = (MarqueeMessage) messages.get(messageList.nextElement());
                                for (int index = 0; index < MarqueeMessage.MESSAGE_LENGTH; index++) {
                                    LEDs.getLED(index + 3).setColor(message.getMessage()[index]);
                                }
                                Utils.sleep(updatingInterval);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.sleep(Long.MAX_VALUE);
                    }
                }
            }
        }
    }
}
