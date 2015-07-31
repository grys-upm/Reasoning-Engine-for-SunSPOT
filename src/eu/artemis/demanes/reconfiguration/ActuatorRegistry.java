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
 * An ActuatorRegistry is used to combine all actuators that control the
 * behavior of the reconfiguration. By adding {@linkplain Actuators} the user
 * can provide the means by which the system can be modified.
 * </p>
 * 
 * @see Actuator
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface ActuatorRegistry {

	/**
	 * <p>
	 * Add a {@linkplain Actuator} object to the collection of actuators by
	 * which the ReasoningEngine can modify the system. If the actuator has
	 * already been registered before, nothing changes.
	 * </p>
	 * 
	 * @param a
	 *            the Actuator to register.
	 */
	public void registerActuator(Actuator a);

	/**
	 * <p>
	 * Remove a {@linkplain Actuator} object from the collection of actuators.
	 * This function should be called before the actuator is destroyed. If the
	 * object was not registered before, nothing happens.
	 * </p>
	 * 
	 * @param a
	 *            the object that no longer can be used.
	 */
	public void unregisterActuator(Actuator a);
}