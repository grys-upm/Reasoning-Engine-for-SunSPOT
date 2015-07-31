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
package eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery;

/**
 * Represents the type of neighbor discovery protocol.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class NeighborDiscoveryType {

    /** String description for the Node Degree Manager V1. */
    public static final String NODE_DEGREE_MANAGER_V1_STRING = "Node Degree Manager V1 (Guard Interval)";
    /** String description for the Node Degree Manager V2. */
    public static final String NODE_DEGREE_MANAGER_V2_STRING = "Node Degree Manager V2 (Incremental Management)";
    /** String description for the outdated Basic Neighbor Discovery. */
    public static final String BASIC_NEIGHBOR_DISCOVERY_STRING = "Basic Neighbor Discovery Manager V1";
    /** String description for the On Demand Node Degree. */
    public static final String ON_DEMAND_NODE_DEGREE_STRING = "On Demand Node Degree Manager V1";

    /** Code for the Node Degree Manager V1. */
    public static final int NODE_DEGREE_MANAGER_V1_CODE = 0x01;
    /** Code for the Node Degree Manager V2. */
    public static final int NODE_DEGREE_MANAGER_V2_CODE = 0x02;
    /** Code for the outdated Basic Neighbor Discovery. */
    public static final int BASIC_NEIGHBOR_DISCOVERY_CODE = 0x03;
    /** Code for the On Demand Node Degree. */
    public static final int ON_DEMAND_NODE_DEGREE_CODE = 0x04;

    /** Neighbor Discovery Type for the Node Degree Manager V1. */
    public static final NeighborDiscoveryType NODE_DEGREE_MANAGER_V1 = new NeighborDiscoveryType(NODE_DEGREE_MANAGER_V1_STRING, NODE_DEGREE_MANAGER_V1_CODE);
    /** Neighbor Discovery Type for the Node Degree Manager Vw. */
    public static final NeighborDiscoveryType NODE_DEGREE_MANAGER_V2 = new NeighborDiscoveryType(NODE_DEGREE_MANAGER_V2_STRING, NODE_DEGREE_MANAGER_V2_CODE);
    /** Neighbor Discovery Type for the outdated Basic Neighbor Discovery. */    
    public static final NeighborDiscoveryType BASIC_NEIGHBOR_DISCOVERY = new NeighborDiscoveryType(BASIC_NEIGHBOR_DISCOVERY_STRING, BASIC_NEIGHBOR_DISCOVERY_CODE);
    /** Neighbor Discovery Type for the On Demand Node Degree. */
    public static final NeighborDiscoveryType ON_DEMAND_NODE_DEGREE = new NeighborDiscoveryType(ON_DEMAND_NODE_DEGREE_STRING, ON_DEMAND_NODE_DEGREE_CODE);

    private final String name;
    private int code;

    /**
     * Creates a new Neighbor Discovery Type.
     * 
     * @param name Name descriptor for the Neighbor Discovery Type.
     * @param code Unique code number used to identify the Neighbor Discovery Type.
     */
    protected NeighborDiscoveryType(String name, int code) {
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

        final NeighborDiscoveryType other = (NeighborDiscoveryType) obj;
        if (this.code != other.code) {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code value for the object.
     * 
     * @return A hash code value for this object.
     */
    public int hashCode() {
        return this.code;
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return A string representation of the object.
     */
    public String toString() {
        return this.name;
    }
}
