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
package eu.artemis.demanes.impl.SunSPOT.utils.logging;

import com.sun.spot.core.util.PrettyPrint;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;

/**
 * Logging operations are done through this class.
 *
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class Logger {

    /**
     * SYSTEM_OUTPUT indicates that the log is going to be done to System.out.
     */
    public static final byte SYSTEM_OUTPUT = 0x01;
    /**
     * SYSTEM_ERROR indicates that the log is going to be done to System.err.
     */
    public static final byte SYSTEM_ERROR = 0x02;
    /**
     * RECORD indicates that the log is going to be stored in an internal
     * RecordStore.
     */
    public static final byte RECORD = 0x10;
    /**
     * DISABLED indicates that the loggin is disabled.
     */
    public static final byte DISABLED = 0x00;
    /**
     * REMOTE indicates that the log is going to be casted to a remote logging
     * facility.
     */
    public static final byte REMOTE = 0x20;
    
    private static final Level DEFAULT_LEVEL = Level.ALL;
    private static final byte DEFAULT_DESTINATION = SYSTEM_OUTPUT;
    private static final String DEFAULT_RECORD_NAME = "spotlog";
    private static final String DEFAULT_DEBUG_RECORD_NAME = "debuglog";
    
    private static final String DELIMITER = ": ";
    private static final boolean CREATE_RECORD_IF_NECESSARY = true;
    
    private byte destination;
    private String name;
    private Level level;
    
    private TimeZone timeZone;
    
    private int numberOfRecords;
    
    private SystemContext context;

    /**
     * Public constructor with a default configuration.
     *
     * The logger will be initially configured for ALL messages done to
     * System.out.
     */
    public Logger() {
        this.destination = DEFAULT_DESTINATION;
        this.name = DEFAULT_RECORD_NAME;
        this.level = DEFAULT_LEVEL;
        this.timeZone = TimeZone.getTimeZone("GMT+1");
        this.numberOfRecords = 0;
    }
    
    /**
     * Public constructor with default configuration using {@code name} as the
     * name for the internal record storage.
     *
     * The logger will be initially configured for ALL messages done to
     * System.out.
     * 
     * @param name The name of the record store.
     */
    public Logger(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Public constructor with default configuration using {@code destination}
     * as the destination for the logs.
     * 
     * @param destination The destination for the logs.
     */
    public Logger(byte destination) {
        this();
        this.destination = destination;
    }
    
    /**
     * Public constructor with default configuration using {@code name} as the
     * name for the record store, and {@code destination} for the destination of
     * the logs.
     * 
     * @param name The name of the record store.
     * @param destination The destination for the logs.
     */
    public Logger(String name, byte destination) {
        this();
        this.destination = destination;
        this.name = name;
    }
    
    /**
     * Public constructor with default configuration using {@code name} as the
     * name for the record store, {@code destination} as the destination for the
     * logs, and {@code level} as the minimum level of the logs to be recorded.
     * 
     * @param name The name of the record store.
     * @param destination The destination for the logs.
     * @param level The minimum level of the logs to be recorded.
     */
    public Logger(String name, byte destination, Level level) {
        this();
        this.destination = destination;
        this.name = name;
        this.level = level;
    }
    
    /**
     * Set the name of the record store.
     * 
     * @param name The name of the record store.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Get the name of the record store.
     * 
     * @return The name of the record store.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Set the destination of the log messages.
     * 
     * @param destination The destination of the log messages.
     */
    public void setDestination(byte destination) {
        this.destination = destination;
    }
    
    /**
     * Get the destination of the log messages.
     * 
     * @return The detination of the log messages.
     */
    public byte getDestination() {
        return this.destination;
    }
    
    /**
     * Set the logging level.
     * 
     * @param level The logging level.
     */
    public void setLevel(Level level) {
        this.level = level;
    }
    
    /**
     * Get the logging level.
     * 
     * @return The logging level.
     */
    public Level getLevel() {
        return this.level;
    }
    
    /**
     * Set the level to logging all.
     */
    public void setAll() {
        this.level = Level.ALL;
    }
    
    /**
     * Set off the logging.
     */
    public void setOff() {
        this.level = Level.OFF;
    }
    
    /**
     * Log a message of level {@code FINEST}.
     * 
     * @param msg The message.
     */
    public void finest(String msg) {
        this.log(Level.FINEST, msg);
    }
     
    /**
     * Log a message of level {@code FINER}.
     * 
     * @param msg The message.
     */   
    public void finer(String msg) {
        this.log(Level.FINER, msg);
    }
    
    /**
     * Log a message of level {@code FINE}.
     * 
     * @param msg The message.
     */    
    public void fine(String msg) {
        this.log(Level.FINE, msg);
    }
    
    /**
     * Log a message of level {@code DEBUG}.
     * 
     * @param msg The message.
     */    
    public void debug(String msg) {
        this.log(Level.DEBUG, msg);
    }
    
    /**
     * Log a message of level {@code CONFIG}.
     * 
     * @param msg The message.
     */    
    public void config(String msg) {
        this.log(Level.CONFIG, msg);
    }
    
    /**
     * Log a message of level {@code INFO}.
     * 
     * @param msg The message.
     */    
    public void info(String msg) {
        this.log(Level.INFO, msg);
    }
     
    /**
     * Log a message of level {@code WARNING}.
     * 
     * @param msg The message.
     */   
    public void warning(String msg) {
        this.log(Level.WARNING, msg);
    }
    
    /**
     * Log a message of level {@code SEVERE}.
     * 
     * @param msg The message.
     */    
    public void severe(String msg) {
        this.log(Level.SEVERE, msg);
    }
    
    /**
     * Log a message of level {@code level}.
     * 
     * @param level The level.
     * @param msg The message.
     */   
    public void log(Level level, String msg) {
        
        if (level.intValue() >= this.level.intValue()) {
            Date timestamp = Calendar.getInstance().getTime();
            StringBuffer logBuffer = new StringBuffer();
            logBuffer.append(timestamp.toString());
            logBuffer.append(DELIMITER);
            logBuffer.append(level.getName());
            logBuffer.append(DELIMITER);
            logBuffer.append(msg);
            
            String logMessage = logBuffer.toString();
            switch (this.destination) {
                case SYSTEM_OUTPUT:
                    System.out.println(logMessage);
                    break;
                case SYSTEM_ERROR:
                    System.err.println(logMessage);
                    break;
                case RECORD:
                    RecordStore recordStore;
                    try {
                        recordStore = RecordStore.openRecordStore(name, CREATE_RECORD_IF_NECESSARY);
                        System.out.println("Trying to record a message with size " + logMessage.length());
                        recordStore.addRecord(logMessage.getBytes(), 0, logMessage.length());
                        numberOfRecords++;
                        System.out.println("Updated number of records is " + numberOfRecords);
                        recordStore.closeRecordStore();
                    } catch (RecordStoreFullException exception) {
                        System.err.println("WARNING. Record store is full");
                    } catch (RecordStoreException ex) {
                        if (this.level.intValue() <= Level.DEBUG.intValue()) {
                            System.out.println("An error has ocurred trying to store a log in the record store named \"" + name + "\"");
                            ex.printStackTrace();
                        } else {
                            System.out.println("You shoulden't be reading this... contact with yourself and try to solve this mesh!!!");
                        }
                    }
                    break;
                case REMOTE:
                    // Not implemented
                    break;
                case DISABLED:
                    // Do nothing
                    break;
                default:
                    System.out.println(logMessage);
                    break;
            }
        }
    }
    
    /**
     * Log a byte array of level {@code level}.
     * 
     * @param level The level.
     * @param baos The byte array message.
     */   
    public void log(Level level, ByteArrayOutputStream baos) {
        
        if (level.intValue() >= this.level.intValue()) {
            Date timestamp = Calendar.getInstance().getTime();

            switch (this.destination) {
                case SYSTEM_OUTPUT:
                    PrettyPrint.prettyPrint(baos.toByteArray());
                    break;
                case SYSTEM_ERROR:
                    PrettyPrint.prettyPrint(baos.toByteArray());
                    break;
                case RECORD:
                    RecordStore recordStore;
                    try {
                        recordStore = RecordStore.openRecordStore(name, CREATE_RECORD_IF_NECESSARY);
                        System.out.println("Trying to record a message with size " + baos.size());
                        recordStore.addRecord(baos.toByteArray(), 0, baos.size());
                        numberOfRecords++;
                        System.out.println("Updated number of records is " + numberOfRecords);
                        recordStore.closeRecordStore();
                    } catch (RecordStoreFullException exception) {
                        System.err.println("WARNING. Record store is full");
                    } catch (RecordStoreException ex) {
                        if (this.level.intValue() <= Level.DEBUG.intValue()) {
                            System.out.println("An error has ocurred trying to store a log in the record store named \"" + name + "\"");
                            ex.printStackTrace();
                        } else {
                            System.out.println("You shoulden't be reading this... contact with yourself and try to solve this mesh!!!");
                        }
                    }
                    break;
                case REMOTE:
                    // Not implemented
                    break;
                case DISABLED:
                    // Do nothing
                    break;
                default:
                    System.out.println("DESTINATION NOT IMPLEMENTED. DEFAULT TO STDOUT");
                    PrettyPrint.prettyPrint(baos.toByteArray());
                    break;
            }
        }
    }
}
