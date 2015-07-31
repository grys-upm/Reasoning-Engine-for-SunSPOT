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

import java.util.Vector;

/**
 * <p>
 * An object implementing the Actuator interface (so it is an actuator) is the
 * interface to obtain {@linkplain Action}s. Every system component which is
 * reconfigurable should come with at least one actuator.
 * </p>
 * 
 * <p>
 * Actuators are made by the designers of system component, and can provide one
 * or more {@linkplain Action}s by which the components can be changed. The
 * actuator is merely the interface to actions that correspond to a component,
 * and should not contain the functionality to change the component itself.
 * </p>
 * 
 * @see Observer
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface Actuator {

	/**
	 * <p>
	 * In order for the user to obtain all {@linkplain Action}s by which a
	 * system component may be changed, this function returns an array of
	 * actions which are available.
	 * </p>
	 * 
	 * <p>
	 * If there are no actions available this function returns null
	 * </p>
	 * 
	 * @return an Array or Actions that may be invoked to reconfigure the system
	 */
	public Vector getActions();

}