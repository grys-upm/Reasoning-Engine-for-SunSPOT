/**
 * Copyright 2013-2015 DEMANES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the LicenseLicensed under the Apache License, Version 2.0.
 */
package eu.artemis.demanes.reconfiguration;

/**
 * <p>
 * The ORAMediator defines the interface trough which Observers, Reasoners,
 * Actuators and TriggerPolicies come together. This is the main component in
 * which the mediates these different reconfiguration modules.
 * </p>
 * 
 * <p>
 * ORAMediator stands for Observe - Reason - Act Mediator. It is the object in
 * which the cycle (similar to the MAPE cycle) is bound together. Hence it uses
 * the Mediator design pattern to combine the three modules. The TriggerPolicy
 * is not part of the acronym as it is not considered as a primary function of
 * the of the mediator, instead it is just a utility.
 * </p>
 * 
 * <p>
 * A ORAMediator is used to combine all interfaces that define the behavior of
 * the reconfiguration. By adding {@linkplain Observers} and
 * {@linkplain Actuators} the user can provide the means by which the system can
 * be observed and modified. The {@linkplain Reasoner} can be set to define how
 * the system can make a decision to take an action. And finally the
 * {@linkplain TriggeringPolicy} can be set to determine when the Reasoner
 * should make these decisions.
 * </p>
 * 
 * @see Actuator
 * @see Observer
 * @see Reasoner
 * @see TriggerPolicy
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface ORAMediator extends ObserverRegistry, ActuatorRegistry {

	/**
	 * <p>
	 * Set an object with the {@linkplain Reasoner} as the current reasoner.
	 * This reasoner object defines the behavior of the reconfiguration.
	 * </p>
	 * 
	 * <p>
	 * There can only be one reasoner at a time, so if this function is called
	 * when another reasoner is set, this function overwrites it.
	 * </p>
	 * 
	 * @param r
	 *            an Object with the Reasoner specifying how the decisions
	 *            should be made to reconfigure the system.
	 */
	public void setReasoner(Reasoner r);

	/**
	 * <p>
	 * Set an object with the {@linkplain TriggerPolicy} as the current
	 * triggering policy. This policy object defines the when the
	 * reconfiguration reasoner should be triggered. For example if the reasoner
	 * should be triggered periodically or if a threshold is reached.
	 * </p>
	 * 
	 * <p>
	 * There can only be one triggering policy at a time, so if this function is
	 * called when another triggering policy is set, this function overwrites
	 * it.
	 * </p>
	 * 
	 * @param r
	 *            an Object with the TriggerPolicy specifying when the reasoner
	 *            should be activated to reconfigure the system.
	 */
	public void setTriggeringPolicy(TriggerPolicy t);

}

/**
 * <h2>Assumptions</h2>
 * 
 * <p>
 * AFTER the setReasoner function is called or AFTER the setTriggeringPolicy is
 * called, the registerTriggerable() function is called on the TriggeringPolicy
 * with the Reasoner.
 * </p>
 * 
 * <p>
 * This makes sure that at all times, the Reasoner is registered as Triggerable
 * in the TriggeringPolicy.
 * </p>
 */
