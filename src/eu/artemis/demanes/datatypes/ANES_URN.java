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

import com.sun.spot.espot.peripheral.ota.URL;

/**
 * ANES_URN (for SunSPOT)
 *
 * @author DEMANES
 * @version 0.1
 * @since 27 nov. 2013
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 0.2
 */

public class ANES_URN {

    private static final String DEMANES_NID = "demanes";
    private static final String URN_PREFIX = "urn:";

    /**
     * Creates an instance of ANES_URN and throws a runtime exception if its
     * syntax is not valid.
     *
     * @param text The text of the URN
     * @return The URN created
     * @throws ANES_URN_Exception the exception generated if the syntax is not
     * correct.
     */
    public static ANES_URN create(final String text) throws ANES_URN_Exception {
        return new ANES_URN(text);
    }

    /**
     * Is it a valid ANES_URN?
     *
     * @param text The text to validate
     * @return Yes of no
     */
    public static boolean isValid(final String text) {
        return text.toLowerCase().startsWith(URN_PREFIX + DEMANES_NID);
    }
    
    /**
     * The basic information container for this type.
     */
    private final String urn;

    /**
     * Public constructor.
     *
     * @param text The text of the URN
     * @throws ANES_URN_Exception If syntax is not correct
     */
    public ANES_URN(final String text) throws ANES_URN_Exception {
        this.urn = text;
    }

    /**
     * Public constructor.
     *
     * @param nid The namespace ID
     * @param nss The namespace specific string
     */
    public ANES_URN(final String nid, final String nss) {
        this.urn = URN_PREFIX + nid + ":" + nss;
    }

    /**
     * @param urn
     * @return 
     */
    public int compareTo(final ANES_URN urn) {
        return this.urn.compareTo(urn.urn);
    }

    /**
     * @param obj
     * @return 
     */
    public boolean equals(final Object obj) {
        if (this.getClass().equals(obj.getClass())) {
            return this.urn.equalsIgnoreCase(((ANES_URN) obj).toString());
        } else {
            return false;
        }
    }

    /**
     * @return 
     */
    public int hashCode() {
        return this.urn.hashCode();
    }

    /**
     * Whether this URN has params?
     *
     * @return Has them?
     */
    public boolean hasParams() {
        return this.urn.indexOf("?") != -1;
    }

    /**
     * Is this ANES_URN empty?
     *
     * @return true if this ANES_URN is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.urn.length() == 0;
    }

    /**
     * Does it match the pattern?
     *
     * @param pattern The pattern to match
     * @return Yes of no
     */
    public boolean matches(final String pattern) {
        return this.urn.regionMatches(true, 0, pattern, 0, pattern.length());
    }

    /**
     * Get namespace ID.
     *
     * @return Namespace ID
     */
    public String nid() {
        if (this.urn.toLowerCase().startsWith(URN_PREFIX)) {
            return this.urn.substring(URN_PREFIX.length(), this.urn.indexOf(":", URN_PREFIX.length()));
        }
        else {
            return "";
        }
    }

    /**
     * Get namespace specific string.
     *
     * @return Namespace specific string
     */
    public String nss() {
        if (this.urn.toLowerCase().startsWith(URN_PREFIX + DEMANES_NID)) {
            return this.urn.substring((URN_PREFIX + DEMANES_NID).length(), this.urn.indexOf(":", (URN_PREFIX + DEMANES_NID).length()));
        }
        else {
            return "";
        }    }

    /**
     * Returns a string representation of this object.
     * 
     * @return A string representation of this object.
     */
    public String toString() {
        return this.urn;
    }

    /**
     * Convert it to URL.
     *
     * @return The URL
     */
    public URL toURL() {
        return new URL(this.urn);
    }

}
