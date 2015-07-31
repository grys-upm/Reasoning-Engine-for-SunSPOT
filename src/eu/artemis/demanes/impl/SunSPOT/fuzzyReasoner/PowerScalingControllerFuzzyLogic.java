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
import eu.artemis.demanes.impl.SunSPOT.datatypes.SunSPOTBundle;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import eu.artemis.demanes.reconfiguration.ActionProvider;
import eu.artemis.demanes.reconfiguration.ObservationProvider;
import eu.artemis.demanes.reconfiguration.Reasoner;

/**
 *
 * This object class models the fuzzy logic controller that will make decisions
 * whenever <b><i>trigger</i></b> is called.
 *
 * @author Vicente Hern&aacute;ndez D&iacute;az
 * @author Yuanjiang Huang
 * <br>UNIVERSIDAD POLITECNICA DE MADRID (UPM)
 * <br>DEMANES 2014
 * @version 1.0
 */

public class PowerScalingControllerFuzzyLogic implements Reasoner{
    private ActionProvider actionsProv = null;
    private ObservationProvider obsProv = null;
    private Properties props = null;
    private double kE;
    private double kdeltaND;
    private int ND;
    private double k_ND;
    private double k_CR;
    private final Logger logger;

    // delta_cr is the output of the whole controller
    private double delta_cr;

    /**
     * Public constructor using reconfiguration properties {@code props}.
     * 
     * @param props The reconfiguration properties.
     */
    public PowerScalingControllerFuzzyLogic(Properties props) {

        logger = SystemContext.getLogger();

        if (props == null) {
            logger.log(Level.DEBUG,"PowerScalingController: No properties specified.");
            throw new IllegalArgumentException("PowerScalingController: No properties specified.");
        }

        this.props = props;

        this.delta_cr = 0;

        //Extract mandatory properties from parameter into class attributtes.
        String property = props.getProperty(ReasonerProperties.KE_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG,"PowerScalingController: No kE provided");
            throw new IllegalArgumentException("PowerScalingController: No kE provided");
        }
        try {
            kE = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG,"PowerScalingController: The value for kE is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for kE is not a double: " + property);
        }

        property = props.getProperty(ReasonerProperties.KdeltaND_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG,"PowerScalingController: No kdeltaND provided");
            throw new IllegalArgumentException("PowerScalingController: No kdeltaND provided");
        }
        try {
            kdeltaND = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG,"PowerScalingController: The value for kdeltaND is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for kdeltaND is not a double: " + property);
        }

        property = props.getProperty(ReasonerProperties.NDinitPROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG,"PowerScalingController: No ND provided");
            throw new IllegalArgumentException("PowerScalingController: No ND provided");
        }
        try {
            ND = Integer.parseInt(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG,"PowerScalingController: The value for ND is not an integer: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for ND is not an integer: " + property);
        }

        property = props.getProperty(ReasonerProperties.KND_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG,"PowerScalingController: No k_ND provided");
            throw new IllegalArgumentException("PowerScalingController: No k_ND provided");
        }
        try {
            k_ND = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG,"PowerScalingController: The value for k_ND is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for k_ND is not a double: " + property);
        }

        property = props.getProperty(ReasonerProperties.KCR_PROPERTY);
        if (property == null) {
            logger.log(Level.DEBUG,"PowerScalingController: No k_CR provided");
            throw new IllegalArgumentException("PowerScalingController: No k_CR provided");
        }
        try {
            k_CR = Double.parseDouble(property);
        } catch (NumberFormatException e) {
            logger.log(Level.DEBUG,"PowerScalingController: The value for k_CR is not a double: " + property);
            throw new IllegalArgumentException("PowerScalingController: The value for k_CR is not a double: " + property);
        }
        logger.log(Level.DEBUG, "PowerScalingController: An instance of PowerScalingController has been successfully instantiated.");
        logger.log(Level.DEBUG, "PowerScalingController: Controller attributes: ND-> "+ND+", kE->"+kE+", kdeltaND->"+kdeltaND+", k_CR->"+k_CR+", k_ND->"+k_ND+").");
        
        
        // Extraer de las propiedades el nombre del fichero .ini del primary loop
        // Extraer de las propiedades el nombre del fichero .ini del secondary loop
        // Abres el InputStream #1 y el InputStream #2
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
            logger.log(Level.DEBUG, "PowerScalingController.trigger: No value provided." );
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
                logger.log(Level.DEBUG, "PowerScalingController.trigger: A BL_EVENT has been triggered. Value: " + value);
                int errorND = this.runSecondaryLoop(error);
                logger.log(Level.DEBUG, "PowerScalingController.trigger: Output from secondary loop (ND error): " + errorND);
                this.runPrimaryLoop(errorND);
                logger.log(Level.DEBUG, "PowerScalingController.trigger: Output from primary loop (deltaCR): " + this.delta_cr);
            }
            if (urn.equals(ANES_URN.create(Events.ND_EVENT))) {
                if (!(value instanceof Integer)) {
                    logger.log(Level.DEBUG, "PowerScalingController.trigger: The error is not an integer object.");
                    throw new IllegalArgumentException("PowerScalingController.trigger: The error is not an integer object.");
                }
                logger.log(Level.DEBUG, "PowerScalingController.trigger: A ND_EVENT has been triggered. Value: " + value);
                int error = ((Integer) value).intValue();
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
            logger.log(Level.DEBUG, "PowerScalingController.trigger: No ActionID: "+ex.getMessage());
            throw new IllegalStateException("PowerScalingController.trigger: No ActionID: "+ex.getMessage());
        } catch (ActionInvocationException ex) {
            logger.log(Level.DEBUG, "PowerScalingController.trigger: Exception when invoking action: "+ex.getMessage());
            throw new IllegalStateException("PowerScalingController.trigger: Exception when invoking action: "+ex.getMessage());
        }

    }

    private double FDM(double val) {
        if (val >= 0.5) {
            return 1;
        }

        if (val <= -0.5) {
            return -1;
        }

        if ((val >= -0.25) && (val <= 0.25)) {
            return 0;
        }

        if (val > -0.5) {
            return 4 * val + 1;
        }

        if (val < 0.5) {
            return 4 * val - 1;
        }

        throw new IllegalArgumentException("Error in val parameter for FDM: " + val);
    }

    private int getND() throws ANES_URN_Exception, InexistentObservationID, ObservationInvocationException {
        Object o = obsProv.getValue(new ANES_URN(DEMANESResources.NDObservationURN));
        if ( !(o instanceof Integer))
            throw new IllegalStateException("The observed node degree is not an integer.");
        return ((Integer)o).intValue();
    }

    private void runPrimaryLoop(int error) {

        double e1 = error * k_ND;
        double delta_u1 = FDM(e1);

        // set the output
        this.delta_cr = delta_u1 * k_CR;
        
    }

    private int runSecondaryLoop(double error) {
        /*
         1.- Calculate e2=ke * eE. 
         eE is the parameter value.
         ke is a property.
         */
        /*
         In the future, we should replace by a FuzzyDM implementation.
         */
        double e2 = error * kE;

        /*
         2.- Calculate increment of U2 = the output from FDM2.
         */
        double incrU2 = this.FDM(e2);

        /*
         3.- Calculate increment of Node Degree = increment of U2 * KincrND.
         */
        int incrND = (int) Math.ceil(incrU2 * kdeltaND);

        /*
         4.- We get the new value for the node degree reference.
         */
        int NDRef = ND + incrND;
        props.setProperty(ReasonerProperties.ND_R_PROPERTY, Integer.toString(NDRef));

        try {
            /*
            5.- We need to get ND observation to run the Primary Loop
            */
            return NDRef - getND();
            
            /*
            End of FuzzyDM implementation
            */
        } catch (ANES_URN_Exception ex) {
            throw new IllegalStateException("Error in URN: "+DEMANESResources.BLObservationURN+ "Message: "+ex.getMessage());
        } catch (InexistentObservationID ex) {
            throw new IllegalStateException("No existent ObservationID: "+ex.getMessage());
        } catch (ObservationInvocationException ex) {
            throw new IllegalStateException("Error when observing the node degree: "+ex.getMessage());
        }
    }

}
