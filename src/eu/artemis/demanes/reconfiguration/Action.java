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

/**
 * <p>
 * An action is the unit of reconfiguration actuation. The user must use Actions
 * to modify the system in any way the Actions allow it to. If there is no
 * Action object that enables the modification of the system, then it cannot be
 * modified.
 * </p>
 * 
 * <p>
 * Every Action has a unique identifier that denotes the type of action that it
 * performs. This identifier must be specific not only for the type of Action
 * that it performs, but also on what specific part of the system it will
 * perform this Action.
 * </p>
 * 
 * <p>
 * Once an Action exists, and has been made available to modify the system, the
 * user may call the {@link #invoke(ANES_BUNDLE)} method. This will do the
 * actual modification, possibly influenced by a set of arguments.
 * </p>
 * 
 * @see Observation
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface Action {

	/**
	 * <p>
	 * Returns the unique identifier of this Action.
	 * </p>
	 * 
	 * <p>
	 * Every Action is uniquely identifiable via a ANES_URN identifier. By
	 * obtaining this identifier, the user should be able to know what the
	 * Action will do if invoked, an what (sub)component of the system is
	 * influenced by it.
	 * </p>
	 * 
	 * @return An ANES_URN uniquely identifying the action
	 */
	public ANES_URN getActionID();

	/**
	 * <p>
	 * Invokes the action
	 * </p>
	 * 
	 * <p>
	 * As a generic way to have Actions modify the system, this method must
	 * always be called in order to have the Action perform it's modification on
	 * the system as it is intended to do. Any other functions of the Action may
	 * exist but will not modify the rest of the system.
	 * </p>
	 * 
	 * @param arguments
	 *            The arguments may indicate a specification of how the action
	 *            should be invoked. For instance in order to modify a parameter
	 *            of a component, the new parameter value can be one of the
	 *            elements in the argument list.
	 * @throws ActionInvocationException
	 *             If at some point in the invocation of the Action an error
	 *             occurs which disallows the proper modification of the system,
	 *             an ActionInvocationException is thrown indicating that the
	 *             invocation failed.
	 */
	public void invoke(ANES_BUNDLE arguments) throws ActionInvocationException;

}

/**
 * <h2>Assumptions:</h2>
 * 
 * <p>
 * Because the Reasoner will never get the actual Action, but will only invoke
 * actions via the ActionProvider interface, there is no use implementing other
 * functions that would provide information to the Reasoner.
 * </p>
 */
