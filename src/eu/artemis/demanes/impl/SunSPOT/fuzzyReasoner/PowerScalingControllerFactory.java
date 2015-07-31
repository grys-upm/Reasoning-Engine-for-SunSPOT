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
 * You can get a copy of the license terms in licenses/LICENSE.
 */
package eu.artemis.demanes.impl.SunSPOT.fuzzyReasoner;

import com.sun.spot.core.util.Properties;
import eu.artemis.demanes.reconfiguration.Reasoner;

/**
 * Factory to create an object of the specified Power Scaling Controller.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class PowerScalingControllerFactory {
    
    /**
     * Creates an instance of the requested Power Scaling Controller.
     * 
     * @param what Descriptor of the Power Scaling Controller to instantiate.
     * @param props Reconfiguration properties.
     * @return A Power Scaling Controller object as requested.
     */
    public static Reasoner getInstance( String what, Properties props ){
        Reasoner result = null;
        
        if ( what.equalsIgnoreCase("PTSimple"))
            result = new PowerScalingController(props);
        
        if ( what.equalsIgnoreCase("PTjFuzzyLogic"))
            result = new PowerScalingControllerFuzzyLogic(props);
        
        if ( what.equalsIgnoreCase("PTInterp1DFDM"))
            result = new PowerScalingControllerInterp1FDM(props);

        return result;
    }   
}
