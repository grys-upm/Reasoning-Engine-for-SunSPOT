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
 * A ObserverRegistry is used on the side of the ORAMediator to combine all
 * Observers that provide input for the reconfiguration. By adding
 * {@linkplain Observers} the user can provide the means by which the system can
 * be observed.
 * </p>
 * 
 * @see Observer
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface ObserverRegistry {

	/**
	 * <p>
	 * Add a {@linkplain Observer} object to the collection of observers by
	 * which the ReasoningEngine can monitor the system. If the observer has
	 * already been registered before, nothing changes.
	 * </p>
	 * 
	 * @param a
	 *            the Observer to register.
	 */
	public void registerObserver(Observer o);

	/**
	 * <p>
	 * Remove a {@linkplain Observer} object from the collection of observers.
	 * This function should be called before the observer is destroyed. If the
	 * object was not registered before, nothing happens.
	 * </p>
	 * 
	 * @param a
	 *            the observer that should be unregistered.
	 */
	public void unregisterObserver(Observer o);

}