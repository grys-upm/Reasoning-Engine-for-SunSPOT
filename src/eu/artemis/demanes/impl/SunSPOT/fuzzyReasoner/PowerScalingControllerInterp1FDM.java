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

import com.sun.spot.core.resources.Resources;
import com.sun.spot.core.util.Properties;
import com.sun.spot.ieee_802_15_4_radio.IRadioPolicyManager;
import com.sun.squawk.util.NotImplementedYetException;
import eu.artemis.demanes.datatypes.ANES_BUNDLE;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.datatypes.ANES_URN_Exception;
import eu.artemis.demanes.exceptions.ActionInvocationException;
import eu.artemis.demanes.exceptions.InexistentActionID;
import eu.artemis.demanes.exceptions.InexistentObservationID;
import eu.artemis.demanes.exceptions.ObservationInvocationException;
import eu.artemis.demanes.impl.SunSPOT.common.DEMANESResources;
import eu.artemis.demanes.impl.SunSPOT.common.Events;
import eu.artemis.demanes.impl.SunSPOT.common.ReasonerProperties;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.common.SystemProperties;
import eu.artemis.demanes.impl.SunSPOT.datatypes.SunSPOTBundle;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import eu.artemis.demanes.reconfiguration.ActionProvider;
import eu.artemis.demanes.reconfiguration.ObservationProvider;
import eu.artemis.demanes.reconfiguration.Reasoner;
import java.io.IOException;

/**
 *
 * This object class models the fuzzy logic controller that will make decisions
 * whenever <b><i>trigger</i></b> is called.
 *
 * @author Vicente Hern&aacute;ndez D&iacute;az
 * @author Yuanjiang Huang
 * @author Ra&uacute;l del Toro Matamoros
 * <br>UNIVERSIDAD POLITECNICA DE MADRID (UPM)
 * <br>DEMANES 2014
 * @version 1.0
 */
public class PowerScalingControllerInterp1FDM implements Reasoner {

    private ActionProvider actionsProv = null;
    private ObservationProvider obsProv = null;
    private Properties props = null;
    private double kE;
    private double kdeltaND;
    private int ND;
    private double k_ND;
    private double k_CR;
    private final Logger logger;
    private FuzzyDM FDM_Prim_Loop;
    private FuzzyDM FDM_Sec_Loop;

    // delta_cr is the output of the whole controller
    private double delta_cr;

    /**
     * Public constructor using reconfiguration properties {@code props}.
     * 
     * @param props The reconfiguration properties.
     */    
    public PowerScalingControllerInterp1FDM(Properties props) {

        logger = SystemContext.getLogger();

        if (props == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No properties specified.");
            throw new IllegalArgumentException("PowerScalingController: No properties specified.");
        }

        this.props = props;

        this.delta_cr = 0;

        //Extract mandatory properties from parameter into class attributtes.
        String property = props.getProperty(ReasonerProperties.KE_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No kE provided");
            throw new IllegalArgumentException("PowerScalingController: No kE provided");
        }
        try {
            kE = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG, "PowerScalingController: The value for kE is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for kE is not a double: " + property);
        }

        property = props.getProperty(ReasonerProperties.KdeltaND_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No kdeltaND provided");
            throw new IllegalArgumentException("PowerScalingController: No kdeltaND provided");
        }
        try {
            kdeltaND = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG, "PowerScalingController: The value for kdeltaND is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for kdeltaND is not a double: " + property);
        }

        property = props.getProperty(ReasonerProperties.NDinitPROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No ND provided");
            throw new IllegalArgumentException("PowerScalingController: No ND provided");
        }
        try {
            ND = Integer.parseInt(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG, "PowerScalingController: The value for ND is not an integer: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for ND is not an integer: " + property);
        }

        property = props.getProperty(ReasonerProperties.KND_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No k_ND provided");
            throw new IllegalArgumentException("PowerScalingController: No k_ND provided");
        }
        try {
            k_ND = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG, "PowerScalingController: The value for k_ND is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for k_ND is not a double: " + property);
        }

        property = props.getProperty(ReasonerProperties.KCR_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No k_CR provided");
            throw new IllegalArgumentException("PowerScalingController: No k_CR provided");
        }
        try {
            k_CR = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG, "PowerScalingController: The value for k_CR is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for k_CR is not a double: " + property);
        }

        property = props.getProperty(ReasonerProperties.FDM_PRIM_CONFIG_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No FDM_Prim_Prop_File provided");
            throw new IllegalArgumentException("PowerScalingController: No FDM_Prim_Prop_File provided");
        }
        try {
            FDM_Prim_Loop = new Interp1DFuzzyDM(property);
        } catch (IOException ex) {
            logger.log(Level.DEBUG, "PowerScalingController: " + ex.getMessage());
            throw new IllegalArgumentException("PowerScalingController: " + ex.getMessage());
        }
        property = props.getProperty(ReasonerProperties.FDM_SEC_CONFIG_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No FDM_Sec_Prop_File provided");
            throw new IllegalArgumentException("PowerScalingController: No FDM_Sec_Prop_File provided");
        }
        try {
            FDM_Sec_Loop = new Interp1DFuzzyDM(property);
            System.err.print(FDM_Sec_Loop);
        } catch (IOException ex) {
            String errmsg = "PowerScalingController: " + ex.getMessage();
            logger.log(Level.DEBUG, errmsg);
            throw new IllegalArgumentException(errmsg);
        }
        property = props.getProperty(ReasonerProperties.du_CR_ID_PROPERTY);
        if (property == null) {
            String errmsg = "PowerScalingController: No " + ReasonerProperties.du_CR_ID_PROPERTY + " provided";
            logger.log(Level.DEBUG, errmsg);
            throw new IllegalArgumentException(errmsg);
        }
        property = props.getProperty(ReasonerProperties.du_ND_ID_PROPERTY);
        if (property == null) {
            String errmsg = "PowerScalingController: No " + ReasonerProperties.du_ND_ID_PROPERTY + " provided";
            logger.log(Level.DEBUG, errmsg);
            throw new IllegalArgumentException(errmsg);
        }
        property = props.getProperty(ReasonerProperties.e_E_ID_PROPERTY);
        if (property == null) {
            String errmsg = "PowerScalingController: No " + ReasonerProperties.e_E_ID_PROPERTY + " provided";
            logger.log(Level.DEBUG, errmsg);
            throw new IllegalArgumentException(errmsg);
        }
        property = props.getProperty(ReasonerProperties.e_ND_ID_PROPERTY);
        if (property == null) {
            String errmsg = "PowerScalingController: No " + ReasonerProperties.e_ND_ID_PROPERTY + " provided";
            logger.log(Level.DEBUG, errmsg);
            throw new IllegalArgumentException(errmsg);
        }
        logger.log(Level.DEBUG, "PowerScalingController: An instance of PowerScalingController has been successfully instantiated.");
        logger.log(Level.DEBUG, "PowerScalingController: Controller attributes: ND-> " + ND + ", kE->" + kE + ", kdeltaND->" + kdeltaND + ", k_CR->" + k_CR + ", k_ND->" + k_ND + ").");
    }

    /**
     * Set the {@link ActuationProvider} for this {@code Reasoner}.
     * 
     * @param ap The {@link ActuationProvider}
     */
    public void setActuationProvider(ActionProvider ap) {
        if (ap == null) {
            logger.log(Level.DEBUG, "PowerScalingController.setActuationProvider: Action provider is null.");
            throw new IllegalArgumentException("PowerScalingController.setActuationProvider: Action provider is null.");
        }
        actionsProv = ap;
        logger.log(Level.DEBUG, "PowerScalingController.setActuationProvider: An action provider has been registered.");
    }

    /**
     * Set the {@link ObservationProvider} for this {@code Reasoner}.
     * 
     * @param op The {@link ObservationProvider}
     */
    public void setObservationProvider(ObservationProvider op) {
        if (op == null) {
            logger.log(Level.DEBUG, "PowerScalingController.setObservationProvider: Observation provider is null.");
            throw new IllegalArgumentException("PowerScalingController.setObservationProvider: Observation provider is null.");
        }
        obsProv = op;
        logger.log(Level.DEBUG, "PowerScalingController.setObservationProvider: An observation provider has been registered.");
    }

    /**
     * Used to fire the {@link Reasoner}.
     */
    public void trigger() {
        try {            
            ANES_URN event = (ANES_URN) obsProv.getValue(ANES_URN.create(DEMANESResources.TriggerReasonURN));
            Object value = (Object) obsProv.getValue(ANES_URN.create(DEMANESResources.TriggerReasonValueURN));
            
            this.trigger(event, value);
        } catch (ANES_URN_Exception ex) {
            ex.printStackTrace();
        } catch (InexistentObservationID ex) {
            ex.printStackTrace();
        } catch (ObservationInvocationException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Used to fire the {@link Reasoner}. This is a proposed extension to
     * the DEMANES middleware reasoning middleware.
     * 
     * @param urn {@link ANES_URN} for the reason.
     * @param value Value of the reason.
     */
    public void trigger(ANES_URN urn, Object value) {
        // BEGIN values for recording at the end of the trigger.        
        long timestamp = System.currentTimeMillis();
        int nodeDegree = -1;
        double batteryLevel = -1;
        double delta_nd = Double.NaN;
        int transmissionPower = ((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower();

        try {
            nodeDegree = ((Integer) obsProv.getValue(ANES_URN.create(DEMANESResources.NDObservationURN))).intValue();
            batteryLevel = ((Double) obsProv.getValue(ANES_URN.create(DEMANESResources.BLObservationURN))).doubleValue();
        } catch (ANES_URN_Exception ex) {

        } catch (InexistentObservationID ex) {

        } catch (ObservationInvocationException ex) {

        }

        if (urn == null) {
            logger.log(Level.DEBUG, "PowerScalingController.trigger: No URN provided.");
            throw new IllegalArgumentException("PowerScalingController.trigger: No URN provided.");
        }

        try {
            if (!urn.equals(ANES_URN.create(Events.BL_EVENT))
                    && !urn.equals(ANES_URN.create(Events.ND_EVENT))) {
                logger.log(Level.DEBUG, "PowerScalingController.trigger: Invalid URN value: " + urn);
                throw new IllegalArgumentException("PowerScalingController.trigger: Invalid URN value: " + urn);
            }
        } catch (ANES_URN_Exception ex) {
            logger.log(Level.DEBUG, "PowerScalingController.trigger: ANES_URN.create failed when using Events enum values.");
            throw new IllegalStateException("PowerScalingController.trigger: ANES_URN.create failed when using Events enum values.");
        }

        if (value == null) {
            logger.log(Level.DEBUG, "PowerScalingController.trigger: No value provided.");
            throw new IllegalArgumentException("PowerScalingController.trigger: No value provided.");
        }
        if (this.actionsProv == null) {
            logger.log(Level.DEBUG, "PowerScalingController.trigger: The method trigger has been called but no action provider has been set yet");
            throw new IllegalStateException("PowerScalingController.trigger: The method trigger has been called but no action provider has been set yet");
        }
        if (this.obsProv == null) {
            logger.log(Level.DEBUG, "PowerScalingController.trigger: The method trigger has been called but no observation provider has been set yet");
            throw new IllegalStateException("PowerScalingController.trigger: The method trigger has been called but no observation provider has been set yet");
        }

        try {

            if (urn.equals(ANES_URN.create(Events.BL_EVENT))) {
                if (!(value instanceof Double)) {
                    logger.log(Level.DEBUG, "PowerScalingController.trigger: The error is not a double object.");
                    throw new IllegalArgumentException("PowerScalingController.trigger: The error is not a double object.");
                }
                double error = ((Double) value).doubleValue();
                logger.log(Level.DEBUG, "PowerScalingController.trigger: A BL_EVENT has been triggered. Value: " + value + " | Error: " + error);
                int errorND = this.runSecondaryLoop(error);
                // FOR LOGGING PURPOSES ONLY
                delta_nd = errorND + nodeDegree - ND;
                // FOR LOGGING PURPOSES ONLY
                logger.log(Level.DEBUG, "PowerScalingController.trigger: Output from secondary loop (ND error): " + errorND);
                this.runPrimaryLoop(errorND);
                logger.log(Level.DEBUG, "PowerScalingController.trigger: Output from primary loop (deltaCR): " + this.delta_cr);
            }
            if (urn.equals(ANES_URN.create(Events.ND_EVENT))) {
                if (!(value instanceof Double)) {
                    logger.log(Level.DEBUG, "PowerScalingController.trigger: The error is not an integer object.");
                    throw new IllegalArgumentException("PowerScalingController.trigger: The error is not an integer object.");
                }
                logger.log(Level.DEBUG, "PowerScalingController.trigger: A ND_EVENT has been triggered. Value: " + value);
                double error = ((Double) value).doubleValue();
                this.runPrimaryLoop(error);
                logger.log(Level.DEBUG, "PowerScalingController.trigger: Output from primary loop (deltaCR): " + this.delta_cr);
            }
        } catch (ANES_URN_Exception ex) {
            // This exception can not occur at this point as it has been tested.
        }

        ANES_BUNDLE bundle = new SunSPOTBundle();

        bundle.put(DEMANESResources.DELTA_CR_KEY, new Double(this.delta_cr));

        try {
            this.actionsProv.invoke(ANES_URN.create(DEMANESResources.PTActionURN), bundle);
        } catch (ANES_URN_Exception ex) {
            logger.log(Level.DEBUG, "PowerScalingController.trigger: ANES_URN.create failed when using DEMANESResources enum values.");
            throw new IllegalStateException("PowerScalingController.trigger: ANES_URN.create failed when using DEMANESResources enum values.");
        } catch (InexistentActionID ex) {
            logger.log(Level.DEBUG, "PowerScalingController.trigger: No ActionID: " + ex.getMessage());
            throw new IllegalStateException("PowerScalingController.trigger: No ActionID: " + ex.getMessage());
        } catch (ActionInvocationException ex) {
            logger.log(Level.DEBUG, "PowerScalingController.trigger: Exception when invoking action: " + ex.getMessage());
            throw new IllegalStateException("PowerScalingController.trigger: Exception when invoking action: " + ex.getMessage());
        }

        StringBuffer recordInformation = new StringBuffer();
        recordInformation.append(SystemProperties.IEEE_ADDRESS_VALUE);
        recordInformation.append(",");
        recordInformation.append(timestamp);
        recordInformation.append(",");
        recordInformation.append(nodeDegree);
        recordInformation.append(",");
        recordInformation.append(transmissionPower);
        recordInformation.append(",");
        recordInformation.append(batteryLevel);
        recordInformation.append(",");
        recordInformation.append(delta_cr);
        recordInformation.append(",");
        if (delta_nd != Double.NaN) {
            recordInformation.append(delta_nd);
        } else {
            recordInformation.append("NaN");
        }
        recordInformation.append(",");
        recordInformation.append(urn.toString());
        recordInformation.append(",");
        recordInformation.append(value.toString());

        synchronized (this) {
            logger.setDestination(Logger.RECORD);
            logger.info(recordInformation.toString());
            logger.setDestination(Logger.SYSTEM_OUTPUT);
        }
        logger.info(recordInformation.toString());
    }

    private int getND() throws ANES_URN_Exception, InexistentObservationID, ObservationInvocationException {
        Object o = obsProv.getValue(new ANES_URN(DEMANESResources.NDObservationURN));
        if (!(o instanceof Integer)) {
            throw new IllegalStateException("The observed node degree is not an integer.");
        }
        return ((Integer) o).intValue();
    }

    private void runPrimaryLoop(double error) {

        String e_ND_ID = this.props.getProperty(ReasonerProperties.e_ND_ID_PROPERTY);
        String du_CR_ID = this.props.getProperty(ReasonerProperties.du_CR_ID_PROPERTY);

        // read the property each time it needs. This is for the runtime reconfiguration of the parameters.
        String property = props.getProperty(ReasonerProperties.KND_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No k_ND provided");
            throw new IllegalArgumentException("PowerScalingController: No k_ND provided");
        }
        try {
            k_ND = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG, "PowerScalingController: The value for k_ND is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for k_ND is not a double: " + property);
        }

        property = props.getProperty(ReasonerProperties.KCR_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No k_CR provided");
            throw new IllegalArgumentException("PowerScalingController: No k_CR provided");
        }
        try {
            k_CR = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG, "PowerScalingController: The value for k_CR is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for k_CR is not a double: " + property);
        }

        // 1- Setting normalization factors
        if (!this.FDM_Prim_Loop.setNormFactors(e_ND_ID, this.k_ND)) {
            throw new IllegalStateException(ERRORMSG.VARIABLE_ERROR_ACCESS + ERRORMSG.DELIM + e_ND_ID);
        }
        if (!this.FDM_Prim_Loop.setNormFactors(du_CR_ID, this.k_CR)) {
            throw new IllegalStateException(ERRORMSG.VARIABLE_ERROR_ACCESS + ERRORMSG.DELIM + du_CR_ID);
        }

        // 2- Setting the input
        if (!this.FDM_Prim_Loop.setInput(e_ND_ID, error)) {
            throw new IllegalStateException(ERRORMSG.VARIABLE_ERROR_ACCESS + ERRORMSG.DELIM + e_ND_ID);
        }

        // 3- "Reasoning": normalization, inference process & denormalization 
        this.FDM_Prim_Loop.evaluate();

        // 4- Getting the output
        this.delta_cr = ((Double) this.FDM_Prim_Loop.getOutput(du_CR_ID)).doubleValue();

    }

    private int runSecondaryLoop(double error) {

        String e_E_ID = this.props.getProperty(ReasonerProperties.e_E_ID_PROPERTY);
        String du_ND_ID = this.props.getProperty(ReasonerProperties.du_ND_ID_PROPERTY);

        /* 
         1.- Setting normalization factors
         */
        if (!this.FDM_Sec_Loop.setNormFactors(e_E_ID, this.kE)) {
            throw new IllegalStateException(ERRORMSG.VARIABLE_ERROR_ACCESS + ERRORMSG.DELIM + e_E_ID);
        }
        if (!this.FDM_Sec_Loop.setNormFactors(du_ND_ID, this.kdeltaND)) {
            throw new IllegalStateException(ERRORMSG.VARIABLE_ERROR_ACCESS + ERRORMSG.DELIM + du_ND_ID);
        }

        /*
         2.- Setting the input
         */
        if (!this.FDM_Sec_Loop.setInput(e_E_ID, (double) error)) {
            throw new IllegalStateException(ERRORMSG.VARIABLE_ERROR_ACCESS + ERRORMSG.DELIM + e_E_ID);
        }

        /* 
         3.- "Reasoning" to calculate increment U2 = the output from FDM2
         Normalization -> inference process -> denormalization
         */
        this.FDM_Sec_Loop.evaluate();

        /*
         4.- Calculates increment of Node Degree = increment of U2 * KincrND.
         */
        int incrND = (int) Math.ceil(((Double) this.FDM_Sec_Loop.getOutput(du_ND_ID)).doubleValue());

        logger.info("PowerScalingController secondary loop incrND = " + incrND);

        /*
         5.- We get the new value for the node degree reference.
         */
        String property = props.getProperty(ReasonerProperties.NDRefPROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG, "PowerScalingController: No NDRef provided");
            throw new IllegalArgumentException("PowerScalingController: No NDRef provided");
        }
        try {
            ND = Integer.parseInt(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG, "PowerScalingController: The value for NDRef is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for NDRef is not a double: " + property);
        }

        int ND_R = ND + incrND;
        props.setProperty(ReasonerProperties.ND_R_PROPERTY, Integer.toString(ND_R));

        try {
            /*
             6.- We need to get ND observation to run the Primary Loop
             */
            return ND_R - getND();

            /*
             End of FuzzyDM implementation
             */
        } catch (ANES_URN_Exception ex) {
            throw new IllegalStateException("Error in URN: " + DEMANESResources.BLObservationURN + "Message: " + ex.getMessage());
        } catch (InexistentObservationID ex) {
            throw new IllegalStateException("No existent ObservationID: " + ex.getMessage());
        } catch (ObservationInvocationException ex) {
            throw new IllegalStateException("Error when observing the node degree: " + ex.getMessage());
        }
    }

}
