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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import com.sun.spot.core.util.Properties;
import com.sun.squawk.util.StringTokenizer;

/**
 *
 * @author Ra&uacute;l del Toro Matamoros
 * @version 1.0.0
 */
public final class Interp1DFuzzyDM implements FuzzyDM{
    
    // The minimum expected numbers of field stored in the function configuration file
    private int DEFAULT_NUM_CONFIG_FIELDS;

    // Basics keys for the function configuration file
    private Properties CONFIG_KEYS_DEFAULT;
    
    private Hashtable varInputs;
    private Hashtable varOutputs;
    
    private int NumInputs;
    private int NumOutputs;
    private int NumPoints;
    
    // It stores the points array as table of double[].
    // The key for each array is the VarID
    private Hashtable PointsArray;
    
    public Interp1DFuzzyDM(String PropFileName) throws IOException {

        // Initialization of the basics keys for the function configuration file
        DefaultConfig default_config = new DefaultConfig();
        this.CONFIG_KEYS_DEFAULT = new Properties(default_config.getProp());
        
        InputStream ResourceID = this.CONFIG_KEYS_DEFAULT.getClass().getResourceAsStream(PropFileName);
        
        //this.Configuration = new Hashtable();
        this.varInputs = new Hashtable();
        this.varOutputs = new Hashtable();
        
        this.PointsArray = new Hashtable();
        
        if (ResourceID != null)
            this.load(ResourceID);
    }
    
    public Interp1DFuzzyDM(InputStream ResourceID) throws IOException {

        // Initialization of the basics keys for the function configuration file
        DefaultConfig default_config = new DefaultConfig();
        this.CONFIG_KEYS_DEFAULT = new Properties(default_config.getProp());
        
        //this.Configuration = new Hashtable();
        this.varInputs = new Hashtable();
        this.varOutputs = new Hashtable();
        
        this.PointsArray = new Hashtable();
        
        this.load(ResourceID);
    }
    
    public Interp1DFuzzyDM(Properties config) throws IOException {

        // Initialization of the basics keys for the function configuration file
        DefaultConfig default_config = new DefaultConfig();
        this.CONFIG_KEYS_DEFAULT = new Properties(default_config.getProp());
        
        //this.Configuration = new Hashtable();
        this.varInputs = new Hashtable();
        this.varOutputs = new Hashtable();
        
        this.PointsArray = new Hashtable();
        
        this.load(config);
    }
    
    public void load(InputStream ResourceID) throws IOException{

        Properties config = new Properties();
        
        Enumeration prop_key;
        String prop_key_name;
        
        // Temporal table to store properties
        Hashtable config_temp = new Hashtable();
        
        DEFAULT_NUM_CONFIG_FIELDS = this.CONFIG_KEYS_DEFAULT.size();
        
        config.load(ResourceID);
        
        // Copy properties in a temporal Hashtable
        for (prop_key = config.propertyNames(); prop_key.hasMoreElements();){
            prop_key_name = prop_key.nextElement().toString();
            
            config_temp.put(prop_key_name, config.getProperty(prop_key_name));
        }
        
        // Check number of fields in the configuration file
       if (config_temp.size() < DEFAULT_NUM_CONFIG_FIELDS )
            throw new IOException(ERRORMSG.CONFIG_FILE_KEYNUMBERS);

        checkConfigProp(config_temp);
        
        if ((this.NumInputs + this.NumOutputs) > 0)
            checkVarProp(config_temp, this.NumInputs + this.NumOutputs);
        
        if (this.NumPoints > 0){
            checkPointsArray(config_temp, this.varInputs.keys());
            checkPointsArray(config_temp, this.varOutputs.keys());
        }
    }

    public void load(Properties config) throws IOException{

        Enumeration prop_key;
        String prop_key_name;
        
        // Temporal table to store properties
        Hashtable config_temp = new Hashtable();
        
        DEFAULT_NUM_CONFIG_FIELDS = this.CONFIG_KEYS_DEFAULT.size();
        
        // Copy properties to a temporal Hashtable
        for (prop_key = config.propertyNames(); prop_key.hasMoreElements();){
            prop_key_name = prop_key.nextElement().toString();
            
            config_temp.put(prop_key_name, config.getProperty(prop_key_name));
        }
        
        // Check number of fields in the configuration file
       if (config_temp.size() < DEFAULT_NUM_CONFIG_FIELDS )
            throw new IOException(ERRORMSG.CONFIG_FILE_KEYNUMBERS);

        checkConfigProp(config_temp);
        
        if ((this.NumInputs + this.NumOutputs) > 0)
            checkVarProp(config_temp, this.NumInputs + this.NumOutputs);
        
        if (this.NumPoints > 0){
            checkPointsArray(config_temp, this.varInputs.keys());
            checkSortedPointsArray((String)this.varInputs.keys().nextElement());
            checkPointsArray(config_temp, this.varOutputs.keys());
        }
    }

    /*
    * Function to check the description of the property keys in the configuration file 
    * and the number of properties.
    */
    private void checkConfigProp(Hashtable config_prop) throws IOException {
        int config_size = config_prop.size();
        int exp_config_size;
        String prop_key_name;
        Enumeration prop_key;
        
        // Checking properties description
        for (prop_key = this.CONFIG_KEYS_DEFAULT.keys(); prop_key.hasMoreElements();){
            prop_key_name = prop_key.nextElement().toString();
            
            checkProp(config_prop, prop_key_name, false);
        }
        
        // Checking number of properties
        // Storing configuration in memory
        try{
            this.NumInputs = Integer.parseInt(checkProp(config_prop, KEY.NUMBER_OF_INPUTS, true));
            this.NumOutputs = Integer.parseInt(checkProp(config_prop, KEY.NUMBER_OF_OUTPUTS, true));
            this.NumPoints = Integer.parseInt(checkProp(config_prop, KEY.NUMBER_OF_POINTS, true));
        } catch(NumberFormatException ex){
            throw new IOException(ERRORMSG.CONFIG_FILE_VALUE_NOTVALID);
        }
        
        exp_config_size = DEFAULT_NUM_CONFIG_FIELDS + 5 * (this.NumInputs + this.NumOutputs) - 8;
        
        if (config_size < exp_config_size){
            throw new IOException(ERRORMSG.CONFIG_FILE_KEYNUMBERS);
        }
    }
    
    // Checking properties for each variable and storing the values
    private void checkVarProp(Hashtable config_prop, int var_number) throws IOException {
        String prop_key_name, prop_value;
        Object prev_val;
        
        // Checking properties for the input and output variables
            for (int nvar = 1; nvar <= var_number; nvar++){
                String var_type;
                String var_id;

                prop_key_name = KEY.VAR_TYPE + KEY.VAR_DELIM + Integer.toString(nvar);
                var_type = checkProp(config_prop, prop_key_name, true);
                
                Variable singlevar = new Variable(var_type);
                
                String[] var_keys = {KEY.VAR_MIN, KEY.VAR_MAX};
                for (int key = 0; key < var_keys.length; key++){
                    prop_key_name = var_keys[key] + KEY.VAR_DELIM + Integer.toString(nvar);

                    prop_value = checkProp(config_prop, prop_key_name, true);
                    
                    try{
                    singlevar.put(var_keys[key], prop_value);
                    } catch(NumberFormatException ex){
                        throw new IOException(ERRORMSG.CONFIG_FILE_VALUE_NOTVALID + " - " + prop_value);
                    }
                }
                
                singlevar.setNumber(nvar);
                
                prop_key_name = KEY.VAR_ID + KEY.VAR_DELIM + Integer.toString(nvar);
                var_id = checkProp(config_prop, prop_key_name, true);
                
                singlevar.setVarID(var_id);
                
                prev_val = var_type.equals(KEY.VAR_TYPE_IN) ? this.varInputs.put(var_id, singlevar) : this.varOutputs.put(var_id, singlevar);
            }
    }
    
    private String checkProp(Hashtable config_prop, String prop_key_name, boolean removeprop) throws IOException{
        
        String prop_value = (String) config_prop.get(prop_key_name);
        
        if (prop_value == null){
            throw new IOException(ERRORMSG.CONFIG_FILE_KEYNOTFOUND + ERRORMSG.DELIM + prop_key_name);
        }
        
        if (removeprop)
            config_prop.remove(prop_key_name);
        
        return prop_value.trim();
    }

    // Checking and storing the points array
    private void checkPointsArray(Hashtable config_prop, Enumeration varIDs) throws IOException{
        double[] singlerow;
        double prev_value;
        int csize = this.NumPoints;
        StringTokenizer rowdata;
        String singlevalue;
        
        while (varIDs.hasMoreElements()){
            String rowid = varIDs.nextElement().toString();
            String stringdata = checkProp(config_prop, rowid, true);
            // Columns counter
            int ncol = 0;
            
            if (stringdata == null){
                throw new IOException(ERRORMSG.CONFIG_FILE_POINTS_ROW_HEADER_NOTFOUND);
            }
            
            rowdata = new StringTokenizer(stringdata, KEY.POINTS_ROW_DELIM);
            
            singlerow = new double[csize];
            
            while(rowdata.hasMoreTokens()){
                singlevalue = rowdata.nextToken();
                
                try{
                    singlerow[ncol] = Double.parseDouble(singlevalue.trim());
                    
                } catch(NumberFormatException ex){
                    throw new IOException(ERRORMSG.CONFIG_FILE_POINTS_VALUE_NOTVALID + ERRORMSG.DELIM + singlevalue);
                }
                
                ncol++;
            }

            if (ncol != csize)
                throw new IOException(ERRORMSG.CONFIG_FILE_POINTS_COLUMN + ERRORMSG.DELIM + stringdata);

            this.PointsArray.put(rowid, singlerow);
        }
    }
    
    // Checking if points array are sorted
    private void checkSortedPointsArray(String varID) throws IOException{
        double[] X;

        X = (double [])this.PointsArray.get(varID);
        
        for (int ncol = 1; ncol < X.length; ncol++){
            if ((X[ncol] - X[ncol - 1]) < 0){
                throw new IOException(ERRORMSG.CONFIG_FILE_POINTS_VALUES_UNSORTED + ERRORMSG.DELIM + varID);
            }
        }
    }
    
    public Enumeration getInputsID() {
        //To change body of generated methods, choose Tools | Templates.
        return this.varInputs.keys();
    }

    public Enumeration getOutputsID() {
        return this.varOutputs.keys();
    }

    public boolean setInput(String inputID, Object inputVal) {
        Variable single_var = ((Variable)this.varInputs.get(inputID));
        
        if (single_var != null)
            single_var.setValue(inputVal);
        else {
            //ERRORMSG.setErrorMSG(ERRORMSG.VARIABLE_ID_NOTFOUND);
            return false;
        }
        
        return true;
    }

    public boolean setInput(String inputID, double inputVal) {
        Variable single_var = ((Variable)this.varInputs.get(inputID));
        
        if (single_var != null)
            single_var.setValue(inputVal);
        else {
            //ERRORMSG.setErrorMSG(ERRORMSG.VARIABLE_ID_NOTFOUND);
            return false;
        }
        
        return true;
    }

    public Object getOutput(String outputID) {
        //return new Double(((Variable) this.varOutputs.get(outputID)).getValue());
        Variable single_var = ((Variable)this.varOutputs.get(outputID));
        
        if (single_var != null)
            return new Double(single_var.getValue());
        /*
        else
            ERRORMSG.ErrorPrint(ERRORMSG.VARIABLE_ID_NOTFOUND);
        */
        
        return null;
    }

    public Hashtable getInputsSet() {
        Hashtable result = new Hashtable();
        Enumeration varIDs;
        String varid;

        for (varIDs = this.varInputs.keys(); varIDs.hasMoreElements();){
            varid = (String)varIDs.nextElement();
            result.put(varid, new Double(((Variable) this.varInputs.get(varid)).getValue()));
        }
     
        return result;
    }

    public Hashtable getOutputsSet() {
        Hashtable result = new Hashtable();
        Enumeration varIDs;
        String varid;

        for (varIDs = this.varOutputs.keys(); varIDs.hasMoreElements();){
            varid = (String)varIDs.nextElement();
            result.put(varid, new Double(((Variable) this.varOutputs.get(varid)).getValue()));
        }
     
        return result;
    }

    public boolean setNormFactors(String varID, double normVal) {
        Variable varprop = (Variable) this.varInputs.get(varID);
        
        if (varprop == null)
            varprop = (Variable) this.varOutputs.get(varID);

        if (varprop != null)
            varprop.setKnorm(normVal);
        else {
            //ERRORMSG.setErrorMSG(ERRORMSG.VARIABLE_ID_NOTFOUND);
            return false;
        }
        
        return true;
    }

    public boolean setNormFactors(Hashtable normFactors) {
        Enumeration varIDs;
        Variable varprop;
        String varid;
        Object new_value;
        
        for (varIDs = normFactors.keys(); varIDs.hasMoreElements();){
            varid = varIDs.nextElement().toString();
            new_value = normFactors.get(varid);
            
            varprop = (Variable) this.varInputs.get(varid);
            if (varprop == null)
                varprop = (Variable) this.varOutputs.get(varid);
            
            if (varprop != null)
                varprop.setKnorm(new_value);
            else {
                //ERRORMSG.setErrorMSG(ERRORMSG.VARIABLE_ID_NOTFOUND);
                return false;
            }
        }
        return true;
    }

    /*
     * Normalize input variables
     */
    private void normInputs() {
        Enumeration varIDs;
        String varid;

        for (varIDs = this.varInputs.keys(); varIDs.hasMoreElements();){
            varid = varIDs.nextElement().toString();
            ((Variable) this.varInputs.get(varid)).normVar();
        }
    }

    /*
     * Denormalize output variables
     */
    private void denormOutputs() {
        Enumeration varIDs;
        String varid;

        for (varIDs = this.varOutputs.keys(); varIDs.hasMoreElements();){
            varid = varIDs.nextElement().toString();
            ((Variable) this.varOutputs.get(varid)).denormVar();
        }
    }

    public void evaluate() {
        // This function will interpolate the input based on X, Y points
        //double[] X = {0.0, 1.0, 3.0, 6.0};
        //double[] Y = {0.0, 1.0, 3.0, 6.0};
        double[] X;
        double[] Y;
        String outputid;
        int xsize = this.NumPoints;
        
        Enumeration output_keys;
        
        this.normInputs();
        
        for (output_keys = this.varOutputs.keys(); output_keys.hasMoreElements();){
        
            outputid = output_keys.nextElement().toString();
            
            Y = (double [])this.PointsArray.get(outputid);
            
            //for (Enumeration values = this.varInputs.elements(); values.hasMoreElements(); ) {
            String inputid = this.varInputs.keys().nextElement().toString();
            
            X = (double [])this.PointsArray.get(inputid);
            
            double xi = ((Variable)this.varInputs.elements().nextElement()).getValueNorm();
            //}
            double yi = Interp1D(xi, X, xsize, Y); 

            ((Variable) this.varOutputs.get(outputid)).setValueNorm(yi);
            
            ((Variable) this.varOutputs.get(outputid)).denormVar();
        }

        //this.denormOutputs();
        
        
    }
    
    // This function interpolates the input xi based on X, Y points array
    // X is a vector ordered increasingly
    private double Interp1D(double xi, double[] X, int xsize, double[] Y) {
        double yi = 0.0; 
        
        if (xi <= X[0]){
            yi = Y[0];
        }
        else if (xi >= X[xsize - 1]){
            yi = Y[xsize - 1];
        }
        else {
            for (int j = 0; j < xsize - 1; j++){
                if ((xi > X[j]) && (xi <= X[j + 1])) {
                    yi = Y[j] + (xi - X[j]) * (Y[j + 1] - Y[j]) / (X[j + 1] - X[j]);
                    break;
                }
            }
        }
        
        return yi;
    }
    
}
