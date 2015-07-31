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

import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.exceptions.ObservationInvocationException;

/**
 * <p>
 * An Observation is the unit of monitoring the system for reconfiguration. The
 * user must use Observations to monitor the system in any way the Observation
 * allow it to. If there is no Observation object that enables the monitoring of
 * the system, then it cannot be monitored.
 * </p>
 * 
 * <p>
 * Every Observation has a unique identifier that denotes the type of
 * observation that it provides. This identifier must be specific not only for
 * the type of observation, but also what specific component of the system it
 * will observe.
 * </p>
 * 
 * <p>
 * Once an Observation exists, and has been made available to the system, the
 * user may call the {@link #getValue()} method. This will do the actual
 * observation and provide the value that represents the status.
 * </p>
 * 
 * @see Action
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface Observation {

	/**
	 * <p>
	 * Returns the unique identifier of this Observation.
	 * </p>
	 * 
	 * <p>
	 * Every Observation is uniquely identifiable via a {@linkplain ANES_URN}
	 * identifier. By obtaining this identifier, the user should be able to know
	 * what property the Observation will observe, an what (sub)component of the
	 * system it is observing.
	 * </p>
	 * 
	 * @return An ANES_URN uniquely identifying the observation
	 */
	public ANES_URN getObservationID();

	/**
	 * <p>
	 * Get the value of the observation
	 * </p>
	 * 
	 * <p>
	 * As a generic way to have Observations monitor the system, this method
	 * must always be called in order to have the Observation get the
	 * information from the system as it is intended to do. Any other functions
	 * of the Action may exist but will get information from the system
	 * component.
	 * </p>
	 * 
	 * @return an Object which represents the value of the observation. An
	 *         Observation may specify what type of Object is returned, but any
	 *         type of return value is possible.
	 * @throws ObservationInvocationException
	 *             If at some point in obtaining the information from the system
	 *             an error occurs, an ObservationInvocationException is thrown
	 *             indicating that the observation failed.
	 */
	public Object getValue() throws ObservationInvocationException;

}

/**
 * <h2>Assumptions</h2>
 * 
 * <p>
 * It is possible that the results of the observation are a buffered version of
 * the information. This means that calling {@link #getValue()} will not do the
 * observation itself, but the information is gathered at some intermediate
 * moment. This is a choice of the designer of the Observation, but will have to
 * be stated explicitly to the user.
 * </p>
 */
