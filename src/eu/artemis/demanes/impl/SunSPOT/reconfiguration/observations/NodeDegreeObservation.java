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

import com.sun.spot.core.util.Properties;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.datatypes.ANES_URN_Exception;
import eu.artemis.demanes.exceptions.ObservationInvocationException;
import eu.artemis.demanes.impl.SunSPOT.common.DEMANESResources;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.NDPFactory;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.NeighborDiscoveryFactoryException;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.NeighborDiscoveryProtocol;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.NeighborDiscoveryType;
import eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.ondemand.OnDemandNeighborDiscovery;
import eu.artemis.demanes.reconfiguration.Observation;
import java.util.Vector;

/**
 * Node Degree Observation provides the number of active neighbors.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class NodeDegreeObservation implements Observation, NodeDegreeObservationExtension {

    private final Logger logger;
    private NeighborDiscoveryProtocol nodeDegreeManager;

    /**
     * Public constructor for the Node Degree Observation.
     * 
     * @param properties The set of reconfiguration properties.
     * @param urn The urn for the observation.
     */
    public NodeDegreeObservation(Properties properties, String urn) {
        this.logger = SystemContext.getLogger();
        try {
            nodeDegreeManager = NDPFactory.getInstance(NeighborDiscoveryType.ON_DEMAND_NODE_DEGREE);
        } catch (NeighborDiscoveryFactoryException ex) {
            logger.severe("Unable to create the requested nodeDegreeManager");
        }
    }
    
    /**
     * Starts the on demand neighbor discovery required to keep updated the
     * table of active neighbors.
     */
    public void start() {
        ((OnDemandNeighborDiscovery) this.nodeDegreeManager).start();
    }
    
    /**
     * Get the observation {@link ANES_URN} urn.
     * 
     * @return The observatio ANES urn.
     */
    public ANES_URN getObservationID() {
        try {
            return ANES_URN.create(DEMANESResources.NDObservationURN);
        } catch (ANES_URN_Exception ex) {
            logger.log(Level.SEVERE, "NodeDegreeObservation.getObservationID: ANES_URN.create reported and error with DEMANESResources.NDObservationURN.");
            return null;
        }
    }

    /**
     * Get the observed value, in this case the number of active neighbors.
     * 
     * @return The observation value, that is, the number of active neighbors.
     * @throws ObservationInvocationException 
     */    
    public Object getValue() throws ObservationInvocationException {
        return new Integer((int) nodeDegreeManager.getNodeDegree());
    }

    /**
     * Updates the active neighbors table.
     */
    public void updateNodeDegree() {
        nodeDegreeManager.doNeighborDiscovery();
    }

    /**
     * Get the list of active neighbors.
     * 
     * @return The list of active neighbors.
     */
    public Vector getNeighborList() {
        return nodeDegreeManager.getNeighborList();
    }
}
