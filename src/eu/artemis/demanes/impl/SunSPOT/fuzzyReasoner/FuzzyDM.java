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
package eu.artemis.demanes.impl.SunSPOT.fuzzyReasoner;

import com.sun.spot.core.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * FuzzyDM
 * 
 * This is an interface to a decision making function.
 * 
 * @author Raúl del Toro Matamoros
 * @version 1.0.0
 */
public interface FuzzyDM {
	
    /**
     * Load Inference System from a storage device
     *
     * @param resourceID
     * @throws java.io.IOException
     */
    
    public void load(InputStream resourceID) throws IOException; 

    public void load(Properties config) throws IOException; 
    
    /*
     * Returns IDs or names of input variables
     */
    public Enumeration getInputsID(); 

    /*
     * Returns IDs or names of output variables
     */
    public Enumeration getOutputsID();

    /*
     * Set input variable
     */
    public boolean setInput(String inputID, Object inputVal); 

    /*
     * Set input variable
     */
    public boolean setInput(String inputID, double inputVal); 

    /*
     * Get output variable
     */
    public Object getOutput(String outputID); 

    /*
     * Get inputs variable set
     */
    public Hashtable getInputsSet(); 

    /*
     * Get outputs variable set
     */
    public Hashtable getOutputsSet(); 

    /*
     * Set normalization factor for the input and output variables
     */
    public boolean setNormFactors(Hashtable normFactors); 
    
    public boolean setNormFactors(String inputID, double inputVal);

    /*
     * Normalize input variables
     * It returns normalized values
     */
    //public void normInputs(); 

    /*
     * Denormalize output variables
     * It returns denormalized values
     */
    //public void denormOutputs(); 

    /*
     * Perform inference calculations
     */
    public void evaluate(); 
}
