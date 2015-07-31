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

/**
 * Observation type for use in the {@code ObservationFactory}.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class ObservationType {
    
    /** Node degree observation descriptive string. */
    public static final String NODE_DEGREE_OBSERVATION_STRING = "Node Degree Observation";
    /** Battery level observation descriptive string. */
    public static final String BATTERY_LEVEL_OBSERVATION_STRING = "Battery Level Observation";
    
    /** Node degree observation code. */
    public static final int NODE_DEGREE_OBSERVATION_CODE = 0xA1;
    /** Battery level observation code. */
    public static final int BATTERY_LEVEL_OBSERVATION_CODE = 0xA2;

    /** Node degree observation type. */
    public static final ObservationType NODE_DEGREE_OBSERVATION = new ObservationType(NODE_DEGREE_OBSERVATION_STRING, NODE_DEGREE_OBSERVATION_CODE);
    /** Battery level observation type. */
    public static final ObservationType BATTERY_LEVEL_OBSERVATION = new ObservationType(BATTERY_LEVEL_OBSERVATION_STRING, BATTERY_LEVEL_OBSERVATION_CODE);

    private String name;
    private int code;

    /**
     * Protected constructor of an observation with a descriptive string
     * {@code name} and identificative {@code code}.
     * 
     * @param name The descriptive string of the observation.
     * @param code The identificative code of the observation.
     */
    protected ObservationType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj The reference object with which to compare.
     * @return True if this object is the same as the obj argument, false otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final ObservationType other = (ObservationType) obj;
        if (this.code != other.code) {
            return false;
        }
        
        return true;
    }

    /**
     * <p>Returns a hash code value for the object. This method is supported 
     * for the benefit of hash tables such as those provided by HashMap.
     * </p>
     * 
     * <p>The general contract of hashCode is:</p>
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during an
     * execution of a Java application, the hashCode method must consistently
     * return the same integer, provided no information used in equals
     * comparisons on the object is modified. This integer need not remain
     * consistent from one execution of an application to another execution of
     * the same application.</li>
     * <li>If two objects are equal according to the equals(Object) method,
     * then calling the hashCode method on each of the two objects must produce
     * the same integer result.</li>
     * <li>It is not required that if two objects are unequal according to the
     * equals(java.lang.Object) method, then calling the hashCode method on each
     * of the two objects must produce distinct integer results. However, the
     * programmer should be aware that producing distinct integer results for
     * unequal objects may improve the performance of hash tables.</li>
     * </ul>
     * 
     * <p>As much as is reasonably practical, the hashCode method defined by
     * class Object does return distinct integers for distinct objects. (This
     * is typically implemented by converting the internal address of the object
     * into an integer, but this implementation technique is not required by the
     * JavaTM programming language.)
     * </p>
     * 
     * @return A hash code value for this object.
     */
    public int hashCode() {
        return this.code;
    }   

    /**
     * Returns a string representation of the object, in this case the
     * descriptive string of the observation type.
     * 
     * @return A string representation of the object.
     */
    public String toString() {
        return this.name;
    }    
}
