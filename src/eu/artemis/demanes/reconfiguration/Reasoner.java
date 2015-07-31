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
 * The Reasoner specifies the input and the output of the reasoner. It allows
 * for generalized methods for providing the information needed for the reasoner
 * (e.g. KPIs) and methods for providing it's output possibilities.
 * </p>
 * 
 * <p>
 * The input of the reasoner is via an object that provides the reasoner with
 * {@linkplain Observation}s. The output of a reasoner is via an object that
 * provides it with a set of {@linkplain Action}s.
 * </p>
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface Reasoner extends Triggerable {

	/**
	 * <p>
	 * Sets the object that will provide this Reasoner with it's actuators.
	 * </p>
	 * 
	 * <p>
	 * This function specifies that an {@linkplain ActionProvider} should be
	 * used as the object to get it's {@linkplain Action}s from. The actions can
	 * be obtained by the Reasoner using the ActionProvider's interface, and the
	 * obtained actions can be subsequently used to change anything in the
	 * system.
	 * </p>
	 * 
	 * <p>
	 * If there was previously another object set as it's action provider, this
	 * new object will overwrite it. There can only be one at a time.
	 * </p>
	 * 
	 * @param ap
	 *            The object to be used from now on in order to get actions from
	 * 
	 */
	public void setActuationProvider(ActionProvider ap);

	/**
	 * <p>
	 * Sets the object that will provide this Reasoner with it's observations.
	 * </p>
	 * 
	 * <p>
	 * By specifying that an {@linkplain ObservationProvider} should be used as
	 * the object to get it's {@linkplain Observation}s from, means that all the
	 * input of the Reasoner will have to come from this object. These
	 * observations can then be obtained by the Reasoner via the appropriate
	 * functions from the ObservationProvider interface.
	 * </p>
	 * 
	 * <p>
	 * If there was already another observation provider set, this new object
	 * will overwrite it. There can always only be one at a time.
	 * </p>
	 * 
	 * @param op
	 *            The object to be used from now on in order to get input from
	 * 
	 */
	public void setObservationProvider(ObservationProvider op);

}