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
import eu.artemis.demanes.exceptions.InexistentObservationID;
import eu.artemis.demanes.exceptions.ObservationInvocationException;
import java.util.Vector;

/**
 * <p>
 * The ObservationProvider is the interface through which an object can obtain
 * the available observations to monitor the system. The purpose of it is to
 * provide a generic interface from which the reconfiguration process can obtain
 * information from the system.
 * </p>
 *
 * <p>
 * If an object is an ObservationProvider, this means that it is responsible for
 * providing the correct {@linkplain Observation} value, identified by it's
 * {@linkplain ANES_URN}.
 * </p>
 *
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 *
 */
public interface ObservationProvider {

    /**
     * <p>
     * Query the Observations that can be obtained from this ObservationProvider
     * </p>
     *
     * <p>
     * The {@linkplain #getValue(ANES_URN)} function requires a ANES_URN
     * argument which identifies which observation to obtain. By using this
     * function the user gets an array which indicate what the relevant
     * ANES_URNs can be used as a valid argument.
     * </p>
     *
     * <p>
     * If there are no possible observations that can be obtained, this function
     * will return null.
     * </p>
     *
     * @return an Array of ANES_URNs that can be observed using this
     * ActionProvider.
     */
    Vector getObservations();

    /**
     * <p>
     * Get the value of a specific {@linkplain Observation} in order to monitor
     * the system
     * </p>
     *
     * <p>
     * By calling this function, the user can specify that an Observation with a
     * specific identifier should be invoked. This results internally in
     * invoking the corresponding Observation.
     * </p>
     *
     * @param id In order to resolve which Observation to invoke, the identity
     * of the corresponding Observation should be provided. To obtain a list of
     * available observations from this ObservationProvider, use the
     * {@linkplain #getObservations()} function.
     * @throws InexistentObservationID If the provided id can not be resolved by
     * the ObservationProvider, an exception is thrown, indicating that the this
     * identifier does not exist. (As far as this ObservationProvider concerns)
     * @throws ObservationInvocationException Is thrown if the observation
     * cannot successfully obtain it's information.
     *
     * @see Observation#getValue()
     */
    public Object getValue(ANES_URN id) throws InexistentObservationID,
            ObservationInvocationException;

}
