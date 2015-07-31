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
 * A TriggerPolicy defines the temporal behavior of any {@linkplain Triggerable}
 * object. When the policy is started it will trigger it's registerd objects at
 * a specific time, defined by the implementation.
 * <p>
 * 
 * <p>
 * A TriggerPolicy can be started and stopped, and it can be suspended and
 * resumed. The specific behavior of these actions may be implementation
 * specific, but should adhere to the interface's definitions.
 * </p>
 * 
 * @see Triggerable
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 */
public interface TriggerPolicy {

	/**
	 * <p>
	 * Add a {@linkplain Triggerable} object to the collection of objects to
	 * trigger whenever the policy intends to do so. If the object has already
	 * been registered before, nothing changes.
	 * </p>
	 * 
	 * @param t
	 *            the object to trigger
	 */
	public void registerTriggerable(Triggerable t);

	/**
	 * <p>
	 * Resumes the trigger policy activity if it is suspended. Calling this
	 * function when the trigger policy is stopped, or started has no effect.
	 * Resuming may cause immediate firing of the triggerable object.
	 * </p>
	 */
	public void resume();

	/**
	 * <p>
	 * Runs the trigger policy. After this call, the trigger policy will
	 * determine the conditions and regularity in which the triggerable object
	 * will be fired. Starting may cause the immediate firing of the triggerable
	 * object.
	 * </p>
	 * 
	 * <p>
	 * If start is invoked and the trigger policy is not in stopped state, it
	 * will have no effect.
	 * </p>
	 */
	public void start();

	/**
	 * <p>
	 * Stops the functioning of the trigger policy. After this method is invoked
	 * there will be no firing of the triggerable object which was registered.
	 * Additionally the internal state of the Triggerpolicy (if any) is cleared.
	 * </p>
	 */
	public void stop();

	/**
	 * <p>
	 * Temporarily suspends the firing of the triggerable object. The normal
	 * functioning of the trigger policy must be resumed by using the resume()
	 * method.
	 * </p>
	 */
	public void suspend();

	/**
	 * <p>
	 * Remove a {@linkplain Triggerable} object from the collection of objects
	 * to trigger whenever the policy intends to do so. If the object was not
	 * registered before, nothing changes
	 * </p>
	 * 
	 * @param t
	 *            the object that no longer needs to be triggered
	 */
	public void unregisterTriggerable(Triggerable t);

}