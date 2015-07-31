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
 * You can get a copy of the license terms in licenses/LICENSE.
 */
package eu.artemis.demanes.impl.SunSPOT.fuzzyReasoner;

/**
 * Class for error messages description.
 * 
 * @author Ra&uacute;l del Toro Matamoros
 * @version 1.0.0
 */
public final class ERRORMSG {
        
    private static String LAST_ERRORMSG = "No error";
    
    public static final String DELIM = " - ";
    
    public static final String CONFIG_FILE = "Reasoning function configuration file error";
    public static final String CONFIG_FILE_KEYNUMBERS = CONFIG_FILE + DELIM + "Invalid number of properties";
    public static final String CONFIG_FILE_KEYNOTFOUND = CONFIG_FILE + DELIM + "Property not found";
    public static final String CONFIG_FILE_VALUE_NOTVALID = CONFIG_FILE + DELIM + "Invalid value for key";
    public static final String CONFIG_FILE_POINTS_ROW_HEADER_NOTFOUND = CONFIG_FILE + DELIM + "Invalid or not found header for points array";
    public static final String CONFIG_FILE_POINTS_COLUMN = CONFIG_FILE + DELIM + "Points array has missing column data";
    public static final String CONFIG_FILE_POINTS_VALUE_NOTVALID = CONFIG_FILE + DELIM + "Invalid value in points array";
    public static final String CONFIG_FILE_POINTS_VALUES_UNSORTED = CONFIG_FILE + DELIM + "Unsorted value in points array";
    
    public static final String REASONING_FUNCTION = "Reasoning function error";
    public static final String VARIABLE_ID_NOTFOUND = REASONING_FUNCTION + DELIM + "Variable ID was not found";
    public static final String VARIABLE_ERROR_ACCESS = REASONING_FUNCTION + DELIM + "Variable ID is not accessible";

    
    public static void setErrorMSG(String ErrorMsg){
        LAST_ERRORMSG = ErrorMsg;
    }
    public static void ErrorPrint(String ErrorMsg){
        System.err.println(ErrorMsg);
    }
    
    public static String getLastErrorMSG(){
        return LAST_ERRORMSG;
    }
}
