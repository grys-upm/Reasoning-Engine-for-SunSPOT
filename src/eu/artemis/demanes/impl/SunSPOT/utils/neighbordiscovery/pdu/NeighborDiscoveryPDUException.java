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
package eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.pdu;

/**
 * An NeighborDiscoveryPDUException is thrown to indicate that an error occurred
 * during the generation or parsing of a {@linkplain NeighborDiscoveryPDU}. The
 * original exception is added as a cause.
 * 
 * @see eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.pdu.NeighborDiscoveryPDU#toByteArray
 * @see eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.pdu.NeighborDiscoveryPDU#parsePDU
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class NeighborDiscoveryPDUException extends Exception {
    
    public NeighborDiscoveryPDUException(String method, String reason) {
        super("NeighborDiscoveryPDU." + method + ": " + reason);
    }
    
}
