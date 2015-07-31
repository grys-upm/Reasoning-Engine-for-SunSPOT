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
package eu.artemis.demanes.impl.SunSPOT.reconfiguration.actions;

import com.sun.spot.core.resources.Resources;
import com.sun.spot.core.util.Properties;
import com.sun.spot.ieee_802_15_4_radio.IRadioPolicyManager;
import com.sun.squawk.util.MathUtils;
import eu.artemis.demanes.datatypes.ANES_BUNDLE;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.datatypes.ANES_URN_Exception;
import eu.artemis.demanes.exceptions.ActionInvocationException;
import eu.artemis.demanes.exceptions.NonExistentKeyException;
import eu.artemis.demanes.impl.SunSPOT.common.DEMANESResources;
import eu.artemis.demanes.impl.SunSPOT.common.PTActionProperties;
import eu.artemis.demanes.impl.SunSPOT.common.RadioProperties;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import eu.artemis.demanes.reconfiguration.Action;
import eu.artemis.demanes.reconfiguration.Actuator;
import java.util.Vector;

/**
 * PowerTransmissionActuator
 * 
 * @author Néstor Lucas Martínez <nestor.lucas@upm.es>
 * @version 1.0
 */
public class PowerTransmissionActuator implements Actuator, ResetActuator {

    private Action updatePowerTransmission;
    private Vector actions;

    private final Logger logger;

    public PowerTransmissionActuator(Properties contextProperties) {
        this.actions = new Vector();

        this.logger = SystemContext.getLogger();

        // Create the required actions for this actuator
        updatePowerTransmission = new updatePowerTransmissionAction(contextProperties);

        // Add the recently created actions to the actions list
        this.actions.addElement(updatePowerTransmission);
    }

    /**
     * Get the list of actions provided by this actuator.
     * 
     * @return A Vector containing the list of actions.
     */
    public Vector getActions() {
        return this.actions;
    }
    
    /**
     * Reset the transmission power to the initial value.
     */
    public void reset() {
        ((ResetActuator) updatePowerTransmission).reset();
    }
    
    /**
     * Implements the action to update the transmission power.
     */
    private static class updatePowerTransmissionAction implements Action, ResetActuator {

        private static IRadioPolicyManager radioPolicyManager;
        private static int radioChannel;
        private static final String DEFAULT_URN = DEMANESResources.PTActionURN;
        private static String DEFAULT_CR0;

        private static ANES_URN urn;
        private static double cr;
        private static double CR0;
        
        private Logger logger;

        private updatePowerTransmissionAction(Properties contextProperties) {
            logger = SystemContext.getLogger();
            
            radioPolicyManager = (IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class);
            radioChannel = RadioProperties.getRadioChannel();

            DEFAULT_CR0 = Double.toString(RadioProperties.getMinimumPTXIndex(radioChannel));
            CR0 = Double.parseDouble(contextProperties.getProperty(PTActionProperties.CR0_PROPERTY, DEFAULT_CR0));            

            try {
                urn = new ANES_URN(DEFAULT_URN);
            } catch (ANES_URN_Exception ex) {
                urn = null;
                if (logger.getLevel().intValue() <= Level.DEBUG.intValue()) {
                    ex.printStackTrace();
                }
            }

            cr = 0;            
        }        
        
        /**
         * Resets the Power Transmission Actuator.
         */
        public void reset() {
            cr = 0;
        }
        
        /**
         * Get the {@code ANES_URN} for this actuator.
         * 
         * @return The {@code ANES_URN}.
         */
        public ANES_URN getActionID() {
            return updatePowerTransmissionAction.urn;
        }

        /**
         * Invokes the actuator, thus executing the corresponding action.
         * 
         * @param arguments The set of arguments required for the action.
         * @throws ActionInvocationException  
         */
        public void invoke(ANES_BUNDLE arguments) throws ActionInvocationException {
            double delta_cr;
            double CRsplat;
            int powerTransmissionIndex;

            logger.debug("PTAction invoked... Actual cr = " + cr);

            if (arguments.containsKey(DEMANESResources.DELTA_CR_KEY)) {
                try {
                    delta_cr = ((Double) arguments.get(DEMANESResources.DELTA_CR_KEY)).doubleValue();
                } catch (NonExistentKeyException ex) {
                    delta_cr = Double.NaN;
                }

                logger.debug("PTAction delta_cr argument: " + delta_cr);

                if (delta_cr != Double.NaN) {
                    // Update cr
                    cr += delta_cr;

                    // Update CR
                    CRsplat = cr + CR0;

                    // calculate actual power transmission
                    powerTransmissionIndex = (int) MathUtils.round(CRsplat);

                    if (powerTransmissionIndex > RadioProperties.getMaximumPTXIndex(radioChannel)) {
                        powerTransmissionIndex = RadioProperties.getMaximumPTXIndex(radioChannel);
                        cr -= delta_cr;
                    }

                    if (powerTransmissionIndex < RadioProperties.getMinimumPTXIndex(radioChannel)) {
                        powerTransmissionIndex = RadioProperties.getMinimumPTXIndex(radioChannel);
                        cr -= delta_cr;
                    }

                    radioPolicyManager.setOutputPower(RadioProperties.getPTXatIndex(radioChannel, powerTransmissionIndex));

                    logger.debug("PTAction new CR* = " + CRsplat + " rounded to " + powerTransmissionIndex);
                    logger.debug("PTAction output power read: " + radioPolicyManager.getOutputPower());
                }
            }
        }
    }
}
