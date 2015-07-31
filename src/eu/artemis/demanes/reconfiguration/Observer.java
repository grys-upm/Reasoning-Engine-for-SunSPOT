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
 * An object having the the Observer interface (so it is an observer) is the
 * interface to obtain {@linkplain Observations}s. Every system component which
 * provides information as input for reconfiguration should come with at least
 * one observer.
 * </p>
 * 
 * <p>
 * Observers are made by the designers of the corresponding system component,
 * and can provide one or more {@linkplain Observations}s which indicate what
 * the status is of the component. The observer is merely the interface to
 * observations that correspond to a component, and should not contain the
 * functionality to monitor the component itself.
 * </p>
 * 
 * @see Actuator
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface Observer {

	/**
	 * <p>
	 * In order for the user to obtain all {@linkplain Observations}s which
	 * provide information about the system component, this function returns an
	 * array of observations which are available.
	 * </p>
	 * 
	 * <p>
	 * If there are no observations available this function returns null
	 * </p>
	 * 
	 * @return an Array or Observations that may be used to obtain information
	 *         about the system
	 */
	public Vector getObservations();

}