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
package eu.artemis.demanes.impl.SunSPOT.reconfiguration;

import com.sun.spot.core.util.Properties;
import eu.artemis.demanes.datatypes.ANES_BUNDLE;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.exceptions.ActionInvocationException;
import eu.artemis.demanes.exceptions.InexistentActionID;
import eu.artemis.demanes.exceptions.InexistentObservationID;
import eu.artemis.demanes.exceptions.ObservationInvocationException;
import eu.artemis.demanes.impl.SunSPOT.common.MediatorProperties;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import eu.artemis.demanes.reconfiguration.Action;
import eu.artemis.demanes.reconfiguration.ActionProvider;
import eu.artemis.demanes.reconfiguration.Actuator;
import eu.artemis.demanes.reconfiguration.ORAMediator;
import eu.artemis.demanes.reconfiguration.Observation;
import eu.artemis.demanes.reconfiguration.ObservationProvider;
import eu.artemis.demanes.reconfiguration.Observer;
import eu.artemis.demanes.reconfiguration.Reasoner;
import eu.artemis.demanes.reconfiguration.TriggerPolicy;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * An object of class {@code ORAMediatorForSunSPOT} acts as a mediator
 * between the {@link TriggerPolicy}, the {@link Reasoner}, a set of
 * {@link Observer} objects and a set of {@link Actuator} objects.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class ORAMediatorForSunSPOT implements ORAMediator, ActionProvider, ObservationProvider {

    private static final String UPDATE_LIST_DYNAMIC_BEHAVIOUR = "dynamic";
    private static final String UPDATE_LIST_STATIC_BEHAVIOUR = "static";

    private static final String DEFAULT_UPDATE_LIST_BEHAVIOUR = UPDATE_LIST_STATIC_BEHAVIOUR;

    private Reasoner reasoner;
    private TriggerPolicy triggerPolicy;
    private Hashtable actionMap;
    private Hashtable observationMap;
    private Vector actuators;
    private Vector observers;
    private Vector actions;
    private Vector observations;

    private Logger logger;

    private String updateListBehaviour;

    /**
     * Public constructor.
     */
    public ORAMediatorForSunSPOT() {
        this.reasoner = null;
        this.triggerPolicy = null;
        this.actionMap = new Hashtable();
        this.observationMap = new Hashtable();
        this.actuators = new Vector();
        this.observers = new Vector();
        this.actions = new Vector();
        this.observations = new Vector();
        this.logger = SystemContext.getLogger();

        this.updateListBehaviour = DEFAULT_UPDATE_LIST_BEHAVIOUR;
    }

    /**
     * Public constructor using a set of reasoning {@code properties}.
     * 
     * @param properties The reasoning properties.
     */
    public ORAMediatorForSunSPOT(Properties properties) {
        this();

        this.updateListBehaviour = properties.getProperty(MediatorProperties.UPDATE_LIST_BEHAVIOUR_PROPERTY, DEFAULT_UPDATE_LIST_BEHAVIOUR);
    }

    /**
     * Set the {@link Reasoner} associated to this ORA Mediator.
     * 
     * @param reasoner The reasoner.
     */
    public void setReasoner(Reasoner reasoner) {
        logger.info("Setting reasoner " + reasoner.getClass().getName());

        // First unregister the current reasoner from the triggerPolicy
        if (this.triggerPolicy != null) {
            this.triggerPolicy.unregisterTriggerable(this.reasoner);
        }

        // Set the actuation provider of the reasoner to the current ORAMediator
        if (reasoner != null) {
            reasoner.setActuationProvider((ActionProvider) this);
            reasoner.setObservationProvider((ObservationProvider) this);
        }

        // Set current reasoner
        this.reasoner = reasoner;

        // Register the reasoner with the TriggerPolicy
        if (this.triggerPolicy != null) {
            this.triggerPolicy.registerTriggerable(reasoner);
        }
    }

    /**
     * Set the {@link TriggerPolicy} associated to this ORA Mediator.
     * 
     * @param triggerPolicy The TriggerPolicy
     */
    public void setTriggeringPolicy(TriggerPolicy triggerPolicy) {
        logger.info("Setting trigger policy " + triggerPolicy.getClass().getName());

        // First unregister the current reasoner from the old triggerPolicy
        if (this.triggerPolicy != null) {
            this.triggerPolicy.stop();
            this.triggerPolicy.unregisterTriggerable(this.reasoner);
        }

        // Set current reasoner
        this.triggerPolicy = triggerPolicy;

        // Register the reasoner with the new TriggerPolicy
        if (this.triggerPolicy != null) {
            this.triggerPolicy.registerTriggerable(this.reasoner);
            this.triggerPolicy.start();
        }
    }

    /**
     * Get a list of {@link Action} objects registered in this ORA Mediator.
     * 
     * @return Array of ANES_URN of the registered {@link Action} objects.
     */
    public Vector getActions() {

        if (this.updateListBehaviour.equalsIgnoreCase(UPDATE_LIST_DYNAMIC_BEHAVIOUR)) {
            actions.removeAllElements();

            Enumeration actuatorList = actuators.elements();

            // Loop through the actuator array.
            while (actuatorList.hasMoreElements()) {
                Actuator actuator = (Actuator) actuatorList.nextElement();

                Enumeration actionList = actuator.getActions().elements();

                // For each actuator, get the list of actuations.
                while (actionList.hasMoreElements()) {
                    Action action = (Action) actionList.nextElement();

                    // Add the action to the action map.
                    // The key is the urn string, and the value is the action itself
                    logger.debug("Adding action to map: " + action.getActionID().toString() + " -> " + action.getClass().getName());
                    observationMap.put(action.getActionID(), action);

                    // Add the observation ANES_URN to the observation array
                    actions.addElement(action.getActionID());
                }
            }
        }
        return this.actions;
    }

    /**
     * Invoke a resource {@link Action} identified by its {@link ANES_URN}
     * passing the {@link ANES_BUNDLE} set of arguments.
     * 
     * @param id The urn of the invoked {@link Action} resource.
     * @param arguments The set of arguments for the action.
     * @throws InexistentActionID If no action is registered with the requested id.
     * @throws ActionInvocationException If there is any other exception when invoking the {@link Action}
     */
    public void invoke(ANES_URN id, ANES_BUNDLE arguments) throws InexistentActionID, ActionInvocationException {
        logger.debug("Requested invocation for urn " + id.toString().trim());

        if (this.updateListBehaviour.equalsIgnoreCase(UPDATE_LIST_DYNAMIC_BEHAVIOUR)) {
            // Update the observations array.
            getActions();
        }

        if (actionMap.containsKey(id)) {
            logger.debug("Requested invocation urn found... processing...");
            try {
                ((Action) actionMap.get(id)).invoke(arguments);
            } catch (Exception e) {
                throw new ActionInvocationException(id, e);
            }
        } else {
            logger.debug("Requested invocation urn NOT FOUND... throwing exception");
            throw new InexistentActionID(id);
        }

    }

    /**
     * Get the list of registered {@link Observation} objects in this ORA
     * Mediator.
     * 
     * @return Array of ANES_URN of the registered {@link Observation} objects.
     */
    public Vector getObservations() {
        if (this.updateListBehaviour.equalsIgnoreCase(UPDATE_LIST_DYNAMIC_BEHAVIOUR)) {
            observations.removeAllElements();

            Enumeration observerList = observers.elements();

            // Loop through the observer array.
            while (observerList.hasMoreElements()) {
                Observer observer = (Observer) observerList.nextElement();

                Enumeration observationList = observer.getObservations().elements();

                // For each observer, get the list of observations.
                while (observationList.hasMoreElements()) {
                    Observation observation = (Observation) observationList.nextElement();

                    // Add the observation to the observation map.
                    // The key is the urn string, and the value is the observation itself
                    logger.debug("Adding observation to map: " + observation.getObservationID().toString() + " -> " + observation.getClass().getName());
                    observationMap.put(observation.getObservationID(), observation);

                    // Add the observation ANES_URN to the observation array
                    observations.addElement(observation.getObservationID());
                }
            }
        }
        return this.observations;
    }

    /**
     * Get value observed by the {@link Observation} identified by the
     * {@link ANES_URN} passed as its id.
     * 
     * @param id The {@link ANES_URN} for the queried {@link Observation}
     * @return The value provided by the {@link Observation}.
     * @throws InexistentObservationID If no observation corresponds to the requested ID.
     * @throws ObservationInvocationException  If there is any other exception querying the observation.
     */
    public Object getValue(ANES_URN id) throws InexistentObservationID, ObservationInvocationException {
        logger.debug("Requested getValue for urn " + id.toString().trim());

        if (this.updateListBehaviour.equalsIgnoreCase(UPDATE_LIST_DYNAMIC_BEHAVIOUR)) {
            // Update the observations array.
            getObservations();
        }

        if (observationMap.containsKey(id)) {
            logger.debug("Requested getValue urn found... processing...");
            try {
                return ((Observation) observationMap.get(id)).getValue();
            } catch (Exception e) {
                throw new ObservationInvocationException(id, e);
            }
        } else {
            logger.debug("Requested getValue urn NOT FOUND... throwing exception");
            throw new InexistentObservationID(id);
        }
    }

    /**
     * Register an {@link Observer} in the ORA Mediator.
     * 
     * @param observer The {@link Observer} to be registered.
     */
    public void registerObserver(Observer observer) {

        if (!observers.contains(observer)) {
            logger.info("Registering observer: " + observer.getClass().getName());
            this.observers.addElement(observer);

            Enumeration observationList = observer.getObservations().elements();

            while (observationList.hasMoreElements()) {
                Observation observation = (Observation) observationList.nextElement();

                logger.info("Adding observation to map: " + observation.getObservationID().toString() + " -> " + observation.getClass().getName());
                observationMap.put(observation.getObservationID(), observation);
                observations.addElement(observation.getObservationID());
            }
        } else {
            logger.info("Registering observer: Observer " + observer.getClass().getName() + " already registered.");
        }
    }

    /**
     * Removes an {@link Observer} from the ORA Mediator.
     * 
     * @param observer The {@link Observer} to be removed.
     */
    public void unregisterObserver(Observer observer) {
        if (observers.contains(observer)) {
            logger.info("Unregistering observer: " + observer.getClass().getName());

            Enumeration observationList = observer.getObservations().elements();

            while (observationList.hasMoreElements()) {
                Observation observation = (Observation) observationList.nextElement();

                if (observationMap.containsKey(observation.getObservationID())) {
                    logger.info("Removing observation from map: " + observation.getObservationID().toString() + " -> " + observation.getClass().getName());
                    observationMap.remove(observation.getObservationID());                    
                }
                
                if (observations.contains(observation)) {
                    observations.removeElement(observation);
                }
            }

            this.observers.removeElement(observer);
        } else {
            logger.info("Unregistering observer: Observer " + observer.getClass().getName() + " is not registered.");
        }
    }
    
    /**
     * Register an {@link Actuator} in the ORA Mediator.
     * 
     * @param actuator The {@link Actuator} to be registered.
     */
    public void registerActuator(Actuator actuator) {
        if (!actuators.contains(actuator)) {
            logger.info("Registering actuator: " + actuator.getClass().getName());
            this.actuators.addElement(actuator);

            Enumeration actionList = actuator.getActions().elements();

            while (actionList.hasMoreElements()) {
                Action action = (Action) actionList.nextElement();

                logger.info("Adding action to map: " + action.getActionID().toString() + " -> " + action.getClass().getName());
                actionMap.put(action.getActionID(), action);
                actions.addElement(action.getActionID());
            }
        } else {
            logger.info("Registering actuator: Actuator " + actuator.getClass().getName() + " already registered.");
        }
    }
    
    /**
     * Removes an {@link Actuator} from the ORA Mediator.
     * 
     * @param actuator The {@link Actuator} to be removed.
     */
    public void unregisterActuator(Actuator actuator) {
        if (actuators.contains(actuator)) {
            logger.info("Unregistering actuator: " + actuator.getClass().getName());

            Enumeration actionList = actuator.getActions().elements();

            while (actionList.hasMoreElements()) {
                Action action = (Action) actionList.nextElement();

                if (actionMap.containsKey(action.getActionID())) {
                    logger.info("Removing action from map: " + action.getActionID().toString() + " -> " + action.getClass().getName());
                    actionMap.remove(action.getActionID());                    
                }
                
                if (actions.contains(action)) {
                    actions.removeElement(action);
                }
            }

            this.actuators.removeElement(actuator);
        } else {
            logger.info("Unregistering actuator: Actuator " + actuator.getClass().getName() + " is not registered.");
        }
    }
}