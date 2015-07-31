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

import java.util.Vector;

/**
 * Defines the public methods for any neighbor discovery protocol used in the
 * slef-adaptive power controller.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public interface NeighborDiscoveryProtocol {
    /**
     * Gets the number of active neighbors (Node Degree).
     * 
     * @return The number of active neighbors.
     */
    public long getNodeDegree();
    
    /**
     * Gets the list of active neighbors.
     * 
     * @return The list of active neighbors.
     */
    public Vector getNeighborList();
    
    /**
     * Performs a neighbor discovery request.
     */
    public void doNeighborDiscovery();
}
