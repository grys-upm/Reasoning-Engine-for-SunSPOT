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

import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.ondemand.OnDemandNeighborDiscovery;

/**
 * <p>A NDPFactory is a factory method to provide a Neighbor Discovery Protocol
 * manager selected accordingly to the instance requested. See
 * {@link NeighborDiscoveryType} for further information regarding the current
 * available managers.
 * </p>
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 * @since 20140923
 * 
 */
public class NDPFactory {
    /**
     * A factory to create instances of requested neighbor discovery protocols. 
     * 
     * @param neighborDiscoveryType The type of neighbor discovery required.
     * @return The instantiated neighbor discovery protocol.
     * @throws NeighborDiscoveryFactoryException when the requested typr is not implemented.
     */
    public static NeighborDiscoveryProtocol getInstance(NeighborDiscoveryType neighborDiscoveryType) throws NeighborDiscoveryFactoryException {
        switch (neighborDiscoveryType.hashCode()) {
            case NeighborDiscoveryType.NODE_DEGREE_MANAGER_V1_CODE:
                return new NodeDegreeManagerV1();
            case NeighborDiscoveryType.ON_DEMAND_NODE_DEGREE_CODE:
                return new OnDemandNeighborDiscovery();
            default:
                throw new NeighborDiscoveryFactoryException(neighborDiscoveryType.toString());
        }
    }
}