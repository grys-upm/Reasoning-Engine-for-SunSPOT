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

import java.util.Hashtable;

/**
 * @author Ra&uacute;l del Toro Matamoros
 * @version 1.0.0
 */
public class Variable {
    public static final String VAR_VALUE = "VarValue";
    public static final String VAR_VALUE_NORM = "VarValueNorm";
    public static final String VAR_KNORM = "VarKnorm";
    public static final String VAR_NUMBER = "VarNumber";

    private Hashtable Prop;
    
    public Variable(String VarType){
        this.Prop = new Hashtable();
        
        this.Prop.put(KEY.VAR_ID, "VAR_ID");
        this.Prop.put(KEY.VAR_MIN, new Double(Double.NEGATIVE_INFINITY));
        this.Prop.put(KEY.VAR_MAX, new Double(Double.POSITIVE_INFINITY));
        // "IN" or "OUT"
        this.Prop.put(KEY.VAR_TYPE, VarType); 
        this.Prop.put(VAR_VALUE, new Double(Double.NaN));
        this.Prop.put(VAR_VALUE_NORM, new Double(Double.NaN));
        this.Prop.put(VAR_KNORM, new Double(1.0));
        this.Prop.put(VAR_NUMBER, new Integer(1));
    }

    public void setVarID(String Var_ID){
        this.Prop.put(KEY.VAR_ID, new String(Var_ID));
    }

    public void setKnorm(Object Knorm){
        this.Prop.put(VAR_KNORM, Double.valueOf((Knorm.toString()).trim()));
    }

    public void setKnorm(double Knorm){
        this.Prop.put(VAR_KNORM, new Double(Knorm));
    }

    public double getValue(){
        return ((Double)this.Prop.get(VAR_VALUE)).doubleValue();
    }

    public double getValueNorm(){
        return ((Double)this.Prop.get(VAR_VALUE_NORM)).doubleValue();
    }

    public void setValue(Object VarValue){
        this.Prop.put(VAR_VALUE, Double.valueOf((VarValue.toString()).trim()));
    }

    public void setValue(double VarValue){
        this.Prop.put(VAR_VALUE, Double.valueOf(VarValue));
    }

    public void setNumber(int VarNumber){
        this.Prop.put(VAR_NUMBER, new Integer(VarNumber));
    }

    public void setValueNorm(Double VarValue){
        this.Prop.put(VAR_VALUE_NORM, new Double(VarValue.doubleValue()));
    }

    public void setValueNorm(double VarValue){
        this.Prop.put(VAR_VALUE_NORM, new Double(VarValue));
    }

    public void normVar(){
        double Knorm;
        Double var_value;

        Knorm = ((Double)this.Prop.get(VAR_KNORM)).doubleValue();
        var_value = (Double)this.Prop.get(VAR_VALUE);

        if (!var_value.isNaN())
            this.Prop.put(VAR_VALUE_NORM, Double.valueOf((var_value.doubleValue()) * Knorm));
    }

    public void denormVar(){
        double Knorm;
        Double var_value;

        Knorm = ((Double)this.Prop.get(VAR_KNORM)).doubleValue();
        var_value = (Double)this.Prop.get(VAR_VALUE_NORM);

        if (!var_value.isNaN())
            this.Prop.put(VAR_VALUE, Double.valueOf((var_value.doubleValue()) * Knorm));
    }

    public void put(String key, String value) {
        this.Prop.put(key, Double.valueOf(value));
    }
}
