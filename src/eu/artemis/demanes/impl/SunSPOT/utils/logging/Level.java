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

/**
 * The Level class defines a set of standard logging levels that can be used to
 * control logging output. The logging Level objects are ordered and are
 * specified by ordered integers. Enabling logging at a given level also enables
 * logging at all higher levels.
 *
 * <p>
 * Clients should normally use the predefined Level constants such as
 * Level.SEVERE.
 * </p>
 *
 * <p>
 * The levels in descending order are:
 * </p>
 *
 * <ul>
 * <li>SEVERE (highest value)</li>
 * <li>WARNING</li>
 * <li>INFO</li>
 * <li>CONFIG</li>
 * <li>FINE</li>
 * <li>FINER</li>
 * <li>FINEST (lowest value)</li>
 * </ul>
 *
 * <p>
 * In addition there is a level OFF that can be used to turn off logging, and a
 * level ALL that can be used to enable logging of all messages.
 * </p>
 *
 * <p>
 * It is possible for third parties to define additional logging levels by
 * subclassing Level. In such cases subclasses should take care to chose unique
 * integer level values and to ensure that they maintain the Object uniqueness
 * property across serialization by defining a suitable readResolve method.
 * </p>
 *
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class Level {

    private final String name;
    private final int value;

    /**
     * ALL indicates that all messages should be logged.
     */
    public static Level ALL = new Level("ALL", 0);
    /**
     * FINEST indicates a highly detailed tracing message.
     */
    public static Level FINEST = new Level("FINEST", 1);
    /**
     * FINER indicates a fairly detailed tracing message.
     */
    public static Level FINER = new Level("FINER", 2);
    /**
     * FINE is a message level providing tracing information.
     */
    public static Level FINE = new Level("FINE", 3);
    /**
     * DEBUG is a message level for debugging information.
     */
    public static Level DEBUG = new Level("DEBUG", 4);
    /**
     * CONFIG is a message level for static configuration messages.
     */
    public static Level CONFIG = new Level("CONFIG", 5);
    /**
     * INFO is a message level for informational messages.
     */
    public static Level INFO = new Level("INFO", 6);
    /**
     * WARNING is a message level indicating a potential problem.
     */
    public static Level WARNING = new Level("WARNING", 7);
    /**
     * SEVERE is a message level indicating a serious failure.
     */
    public static Level SEVERE = new Level("SEVERE", 8);
    /**
     * OFF is a special level that can be used to turn off logging.
     */
    public static Level OFF = new Level("OFF", Integer.MAX_VALUE);

    /**
     * Create a named Level with a given integer value.
     *
     * Note that this constructor is "protected" to allow subclassing. In
     * general clients of logging should use one of the constant Level objects
     * such as SEVERE or FINEST. However, if clients need to add new logging
     * levels, they may subclass Level and define new constants.
     *
     * @param name the name of the Level, for example "SEVERE".
     * @param value an integer value for the level.
     */
    protected Level(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Generate a hashcode.
     *
     * @return a hashcode based on the level value.
     */
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 41 * hash + this.value;
        return hash;
    }

    /**
     * Compare two objects for value equality.
     *
     * @param ox the reference object with which to compare.
     * @return true if and only if the two objects have the same level value.
     */
    public boolean equals(Object ox) {
        if (this.getClass().equals(ox.getClass())) {
            return ((Level) ox).getName().equalsIgnoreCase(this.name) && ((Level) ox).intValue() == this.value;
        } else {
            return false;
        }
    }

    /**
     * Return the non-localized string name of the Level.
     *
     * @return non-localized name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the integer value for this level. This integer value can be used for
     * efficient ordering comparisons between Level objects.
     *
     * @return the integer value for this level.
     */
    public int intValue() {
        return this.value;
    }

    /**
     * Returns a string representation of this Level.
     *
     * @return the non-localized name of the Level, for example "INFO".
     */
    public String toString() {
        return this.name;
    }
}
