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
package eu.artemis.demanes.impl.SunSPOT.fuzzyReasoner;

import com.sun.spot.core.util.Properties;

/**
 * Class with the definition of basics keys for the configuration file
 * It also contains the description of the format for values.
 * 
 * @author Ra&uacte;l del Toro Matamoros
 * @version 1.0.0
 */
public class DefaultConfig {
    public Properties defaultProp;
    
    public DefaultConfig(){
        
        this.defaultProp = new Properties();
        
        this.defaultProp.setProperty(KEY.NUMBER_OF_INPUTS, "1");
        this.defaultProp.setProperty(KEY.NUMBER_OF_OUTPUTS, "1");
        this.defaultProp.setProperty(KEY.VAR_ID + KEY.VAR_DELIM + "1", "X");
        this.defaultProp.setProperty(KEY.VAR_TYPE + KEY.VAR_DELIM + "1", KEY.VAR_TYPE_IN);
        this.defaultProp.setProperty(KEY.VAR_MIN + KEY.VAR_DELIM + "1", "-1.0");
        this.defaultProp.setProperty(KEY.VAR_MAX + KEY.VAR_DELIM + "1", "1.0");
        this.defaultProp.setProperty(KEY.VAR_ID + KEY.VAR_DELIM + "2", "Y");
        this.defaultProp.setProperty(KEY.VAR_TYPE + KEY.VAR_DELIM + "2", KEY.VAR_TYPE_OUT);
        this.defaultProp.setProperty(KEY.VAR_MIN + KEY.VAR_DELIM + "2", "-1.0");
        this.defaultProp.setProperty(KEY.VAR_MAX + KEY.VAR_DELIM + "2", "1.0");
        this.defaultProp.setProperty(KEY.NUMBER_OF_POINTS, "2");
        //this.put("X", "-1.0, 1.0");
    }
    
    public Properties getProp(){
        return this.defaultProp;
    }
}
