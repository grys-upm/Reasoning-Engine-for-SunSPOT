/**
 * Copyright 2014-2015 Universidad Politécnica de Madrid (UPM).
 *
 * Authors:
 *    José-Fernan Martínez Ortega
 *    Vicente Hernández Díaz
 *    Néstor Lucas Martínez
 *    Yuanjiang Huang
 *    Raúl del Toro Matamoros
 * 
 * This software is distributed under a dual-license scheme:
 *
 * - For academic uses: Licensed under GNU Affero General Public License as
 *                      published by the Free Software Foundation, either
 *                      version 3 of the License, or (at your option) any
 *                      later version.
 * 
 * - For any other use: Licensed under the Apache License, Version 2.0.
 * 
 * You can get a copy of the license terms in licences/LICENSE.
 */
package eu.artemis.demanes.impl.SunSPOT.datatypes;

import eu.artemis.demanes.datatypes.ANES_BUNDLE;
import eu.artemis.demanes.exceptions.NonExistentKeyException;
import eu.artemis.demanes.exceptions.TypedRequestException;
import java.util.Hashtable;

/**
 * Implementation of the {@link ANES_BUNDLE} for the SunSPOT architecture.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @author Vicente Hern&aacute;ndez D&iacute;az
 * @version 1.0.0
 */
public class SunSPOTBundle implements ANES_BUNDLE{
    private Hashtable bundleMap;

    /**
     * Public constructor.
     * 
     * @see {@link ANES_BUNDLE}
     */
    public SunSPOTBundle() {
        this.bundleMap = new Hashtable();
    }
    
    /**
     * Creates and returns a copy of this object.
     * 
     * @return A clone of this instance.
     */
    public ANES_BUNDLE clone() {
        return this.clone();
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     * 
     * @param key Key whose presence in this map is to be tested.
     * @return True if this map contains a mapping for the specified key.
     */
    public boolean containsKey(String key) {
        return this.bundleMap.containsKey(key);
    }

    /**
     * Returns true if this map contains a mapping for the specified key and
     * the object linked is of the specified class.
     * 
     * @param key Key whose presence in this map is to be tested.
     * @param clazz Class whose type is to be tested.
     * @return True if this map contains a mapping for the specified key and matches the specified class.
     */
    public boolean containsKey(String key, Class clazz) {
        if (this.bundleMap.containsKey(key)) {
            if (this.bundleMap.get(key).getClass().equals(clazz)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * Returns the value to which the specified key is mapped.
     * 
     * @param key Key whose associated value is to be returned.
     * @return The value to which the specified key is mapped.
     * @throws NonExistentKeyException When key is not found in this map.
     */
    public Object get(String key) throws NonExistentKeyException {
        if (this.bundleMap.containsKey(key)) {
            return this.bundleMap.get(key);
        }
        else {
            throw new NonExistentKeyException(key);
        }
    }

    /**
     * Returns the value to which the specified key is mapped if it is of the
     * same class as the specified class.
     * 
     * @param key Key whose associated value is to be returned.
     * @param clazz Class whose type is to be checked.
     * @return The value to which the specified key is mapped.
     * @throws TypedRequestException When class does not match.
     * @throws NonExistentKeyException When key is not found in this map.
     */
    public Object get(String key, Class clazz) throws TypedRequestException, NonExistentKeyException {
        if (this.bundleMap.containsKey(key)) {
            if (this.bundleMap.get(key).getClass().equals(clazz)) {
                return this.bundleMap.get(key);
            }                
            else {
                throw new TypedRequestException(this.bundleMap.get(key).getClass(), clazz);
            }
        }
        else {
            throw new NonExistentKeyException(key);
        }
    }

    /**
     * Returns the type of the object associated to the specified key.
     * 
     * @param key Key whose associated value type is to be returned
     * @return The class of the objected associated to the specified key.
     * @throws NonExistentKeyException  When key is not found in the map.
     */
    public Class getType(String key) throws NonExistentKeyException {
        if (this.bundleMap.containsKey(key)) {
            return this.bundleMap.get(key).getClass();
        }
        else {
            throw new NonExistentKeyException(key);
        }
    }

    /**
     * Associates the specified value with the specified key in this map.
     * 
     * @param key Key with which the specified value is to be associated.
     * @param value Value to be associated with the specified key.
     */
    public void put(String key, Object value) {
        this.bundleMap.put(key, value);
    }
    
}
