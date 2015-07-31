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
package eu.artemis.demanes.datatypes;

import eu.artemis.demanes.exceptions.NonExistentKeyException;
import eu.artemis.demanes.exceptions.TypedRequestException;

/**
 * ANES_BUNDLE (for SunSPOT)
 * 
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 0.2
 */

public interface ANES_BUNDLE {

	public ANES_BUNDLE clone();

	public boolean containsKey(String key);

	public boolean containsKey(String key, Class clazz);

	public Object get(String key) throws NonExistentKeyException;

	public Object get(String key, Class clazz) throws TypedRequestException,
			NonExistentKeyException;

	public Class getType(String key) throws NonExistentKeyException;

	public void put(String key, Object value);

}
