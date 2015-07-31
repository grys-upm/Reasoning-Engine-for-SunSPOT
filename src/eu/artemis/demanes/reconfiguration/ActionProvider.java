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

import eu.artemis.demanes.datatypes.ANES_BUNDLE;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.exceptions.ActionInvocationException;
import eu.artemis.demanes.exceptions.InexistentActionID;
import java.util.Vector;

/**
 * <p>
 * The ActionProvider is the interface through which an object can obtain the
 * available actions to reconfigure the system. The purpose of it is to provide
 * a generic interface from which the reconfiguration process can be controlled.
 * </p>
 * 
 * <p>
 * If an object is an ActionProvider, this means that it is responsible for
 * invoking the correct {@linkplain Action} object, identified by it's
 * {@linkplain ANES_URN}.
 * </p>
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface ActionProvider {

	/**
	 * <p>
	 * Query the Actions that can be invoked using this ActionProvider
	 * </p>
	 * 
	 * <p>
	 * The {@linkplain #invoke(ANES_URN, ANES_BUNDLE)} function requires a
	 * ANES_URN argument which identifies which action to invoke. By using this
	 * function the user gets an array which indicate what the relevant
	 * ANES_URNs can be used as a valid argument.
	 * </p>
	 * 
	 * <p>
	 * If there are no possible actions that can be invokes, this function will
	 * return null.
	 * </p>
	 * 
	 * @return an Array of ANES_URNs that can be invoked using this
	 *         ActionProvider.
	 */
	Vector getActions();

	/**
	 * <p>
	 * Invokes a specific {@linkplain Action} in order to modify the system
	 * </p>
	 * 
	 * <p>
	 * By calling this function, the user can specify that an Action with a
	 * specific identifier should be invoked. This results internally in
	 * invoking the corresponding Action.
	 * </p>
	 * 
	 * @param id
	 *            In order to resolve which Action to invoke, the identity of
	 *            the corresponding Action should be provided. To obtain a list
	 *            of available actions from this ActionProvider, use the
	 *            {@linkplain #getActions()} function.
	 * @param arguments
	 *            The arguments may indicate a specification of how the action
	 *            should be invoked. For instance in order to modify a parameter
	 *            of a component, the new parameter value can be one of the
	 *            elements in the argument list.
	 * @throws InexistentActionID
	 *             If the provided id can not be resolved by the ActionProvider,
	 *             an exception is thrown, indicating that the this identifier
	 *             does not exist. (As far as this ActionProvider concerns)
	 * @throws ActionInvocationException
	 *             Is thrown if the invoked action cannot successfully complete.
	 * 
	 * @see Action#invoke(ANES_BUNDLE)
	 */
	void invoke(ANES_URN id, ANES_BUNDLE arguments) throws InexistentActionID,
			ActionInvocationException;

}