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
package eu.artemis.demanes.impl.SunSPOT.common;

import com.sun.spot.core.resources.Resources;
import com.sun.spot.core.util.Properties;
import com.sun.spot.espot.peripheral.ESpot;

/**
 *
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class ReconfigurationContext {

    private static final int DEFAULT_K_DELTA_ND = 1;
    private static final int DEFAULT_ND_INIT = 1;
    private static final int DEFAULT_ND_REF = 1;
    private static final double DEFAULT_K_CR = 1.0;
    private static final double DEFAULT_E_CR_FACTOR = 0.2;
    private static final String DEFAULT_E_ND_ID = "e_nd";
    private static final String DEFAULT_E_E_ID = "e_E";
    private static final String DEFAULT_DU_ND_ID = "du_nd";
    private static final String DEFAULT_DU_CR_ID = "du_cr";
    private static final String DEFAULT_FDM_PRIM_CONFIG = "/CR_FDM_ND_v1.ini";
    private static final String DEFAULT_FDM_SEC_CONFIG = "/NDr_FDM_E_v1.ini";
    private static final int DEFAULT_TRIGGER_INTERVAL = 2000;
    private static final int DEFAULT_KSI_ND = 1;
    private static final int DEFAULT_ND_MIN = 1;
    private static final int DEFAULT_DELTA_ND_MIN = 1;
    private static final int DEFAULT_DELTA_CR_MIN = 1;

    private Properties defaultProperties;

    public ReconfigurationContext() {
        defaultProperties = new Properties();

        defaultProperties.setProperty(ReasonerProperties.E_CR_FACTOR_PROPERTY, Double.toString(DEFAULT_E_CR_FACTOR));
        defaultProperties.setProperty(ReasonerProperties.KdeltaND_PROPERTY, Integer.toString(DEFAULT_K_DELTA_ND));
        defaultProperties.setProperty(ReasonerProperties.NDRefPROPERTY, Integer.toString(DEFAULT_ND_REF));
        defaultProperties.setProperty(ReasonerProperties.NDinitPROPERTY, Integer.toString(DEFAULT_ND_REF));
        defaultProperties.setProperty(ReasonerProperties.KCR_PROPERTY, Double.toString(DEFAULT_K_CR));
        
        defaultProperties.setProperty(ReasonerProperties.FDM_PRIM_CONFIG_PROPERTY, "/CR_FDM_ND_v1.ini");
        defaultProperties.setProperty(ReasonerProperties.FDM_SEC_CONFIG_PROPERTY, "/NDr_FDM_E_v1.ini");
        defaultProperties.setProperty(ReasonerProperties.e_E_ID_PROPERTY, DEFAULT_E_E_ID);
        defaultProperties.setProperty(ReasonerProperties.e_ND_ID_PROPERTY, DEFAULT_E_ND_ID);
        defaultProperties.setProperty(ReasonerProperties.du_CR_ID_PROPERTY, DEFAULT_DU_CR_ID);
        defaultProperties.setProperty(ReasonerProperties.du_ND_ID_PROPERTY, DEFAULT_DU_ND_ID);
        
        defaultProperties.setProperty(TriggerPolicyProperties.KSI_ND_PROPERTY, Integer.toString(DEFAULT_KSI_ND));
        defaultProperties.setProperty(TriggerPolicyProperties.ND_MIN_PROPERTY, Integer.toString(DEFAULT_ND_MIN));
        defaultProperties.setProperty(TriggerPolicyProperties.CR_MAX_PROPERTY, Integer.toString(RadioProperties.getMaximumPTXIndex(RadioProperties.getRadioChannel())));
        defaultProperties.setProperty(TriggerPolicyProperties.CR_MIN_PROPERTY, Integer.toString(RadioProperties.getMinimumPTXIndex(RadioProperties.getRadioChannel())));
        defaultProperties.setProperty(TriggerPolicyProperties.DELTA_ND_MIN_PROPERTY, Integer.toString(DEFAULT_DELTA_ND_MIN));
        defaultProperties.setProperty(TriggerPolicyProperties.DELTA_CR_MIN_PROPERTY, Integer.toString(DEFAULT_DELTA_CR_MIN));
        defaultProperties.setProperty(TriggerPolicyProperties.TRIGGER_INTERVAL_PROPERTY, Integer.toString(DEFAULT_TRIGGER_INTERVAL));        
        
        updateProperties(defaultProperties);
    }

    public Properties getProperties() {
        return this.defaultProperties;
    } 
    
    public Properties updateProperties(Properties properties) {
        double E_cr;
        double E_cr_factor;
        double kE;
        double k_ND;
        int kdeltaND;
        int ND_Ref;
        int ND_R;
        
        E_cr_factor = Double.parseDouble(properties.getProperty(ReasonerProperties.E_CR_FACTOR_PROPERTY, Double.toString(DEFAULT_E_CR_FACTOR)));
        ND_Ref = Integer.parseInt(properties.getProperty(ReasonerProperties.NDRefPROPERTY, Integer.toString(DEFAULT_ND_REF)));
        kdeltaND = Integer.parseInt(properties.getProperty(ReasonerProperties.KdeltaND_PROPERTY, Integer.toString(DEFAULT_K_DELTA_ND)));
        
        E_cr = (((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getMaximumCapacity()) * E_cr_factor;
        kE = 2.0 / E_cr;
        k_ND = 2.0 / ND_Ref;
        ND_R = ND_Ref + kdeltaND;
        
        properties.setProperty(ReasonerProperties.E_CR_PROPERTY, Double.toString(E_cr));
        properties.setProperty(ReasonerProperties.KE_PROPERTY, Double.toString(kE));
        properties.setProperty(ReasonerProperties.ND_R_PROPERTY, Integer.toString(ND_R));
        properties.setProperty(ReasonerProperties.KND_PROPERTY, Double.toString(k_ND));
        
        return properties;
    }
}
