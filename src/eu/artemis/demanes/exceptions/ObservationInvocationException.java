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
package eu.artemis.demanes.exceptions;

import eu.artemis.demanes.datatypes.ANES_URN;

/**
 * An ObservationInvocationException is thrown to indicate that an error
 * occurred during the invocation of an {@linkplain Observation}. The original
 * exception is added as a cause.
 * 
 * @see eu.artemis.demanes.reconfiguration.Observation#getValue
 * 
 * @author leeuwencjv
 * @version 0.1
 * @since 9 apr. 2014
 * 
 */
public class ObservationInvocationException extends Exception {

	private static final long serialVersionUID = 4874935197610473240L;

	/**
	 * @param urn
	 *            the URN of the Observation that threw the exception when
	 *            invoking
	 * @param e
	 *            the original exception that occurred during invocation
	 */
	public ObservationInvocationException(ANES_URN urn, Exception e) {
		super("Error while invoking Observation with urn " + urn + "\n" + e.toString());
	}

}
