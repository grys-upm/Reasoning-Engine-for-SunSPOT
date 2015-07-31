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

package eu.artemis.demanes.impl.SunSPOT.utils.neighbordiscovery.pdu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Neighbor Discovery PDU is a representation of a PDU for the Neighbor Discovery
 * Protocol.
 * 
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class NeighborDiscoveryPDU {

    private static final String PROTOCOL_ID = "NDP";

    private static final byte VERSION = 0x02;

    public static final byte NDPDU_REQUEST = 0x01;
    public static final byte NDPDU_RESPONSE = 0x02;
    public static final byte NDPDU_RESPONSE_ACK = 0x03;
    
    private byte typeOfPDU;
    private long requestID;

    /**
     * Public constructor. Creates a new PDU of type <i>typeOfPDU</i> with a
     * request ID <i>requestID</i>.
     * 
     * @param typeOfPDU Type of the Neighbor Discovery PDU
     * @param requestID Request ID
     */
    public NeighborDiscoveryPDU(byte typeOfPDU, long requestID) {
        this.typeOfPDU = typeOfPDU;
        this.requestID = requestID;
    }   
    
    /**
     * Creates a newly allocated byte array for a Neighbor Discovery PDU of type
     * <i>typeOfPDU</i> and request ID <i>requestID</i>.
     * 
     * @param typeOfPDU Type of PDU
     * @param requestID Request ID
     * @return The newly allocated byte array representing the PDU
     * @throws NeighborDiscoveryPDUException 
     */
    public static byte[] toByteArray(byte typeOfPDU, long requestID) throws NeighborDiscoveryPDUException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try {
            dos.write(PROTOCOL_ID.getBytes());
            dos.writeByte(VERSION);
            dos.writeByte(typeOfPDU);
            dos.writeLong(requestID);
        } catch (IOException exception) {            
            throw new NeighborDiscoveryPDUException("generatePDU", "Error trying to write the PDU to a byte array");
        }

        return baos.toByteArray();
    }

    /**
     * Get the type of instanced PDU
     * 
     * @return The type of PDU
     */
    public byte getTypeOfPDU() {
        return typeOfPDU;
    }

    /**
     * Set the type of the instanced PDU
     * 
     * @param typeOfPDU the type of PDU
     */
    public void setTypeOfPDU(byte typeOfPDU) {
        this.typeOfPDU = typeOfPDU;
    }

    /**
     * Get the request ID of the instanced PDU
     * 
     * @return the request ID
     */
    public long getRequestID() {
        return requestID;
    }

    /**
     * Set the request ID of the instanced PDU
     * 
     * @param requestID the request ID
     */
    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }
    
    /**
     * Parse a byte array containing a Neighbor Discovery PDU.
     * 
     * @param pdu the byte array containing the Neighbor Discovery PDU
     * @return the instanced NeighborDiscoveryPDU
     * @throws NeighborDiscoveryPDUException if the byte array does not comply with the Neighbor Discovery PDU
     */
    public static NeighborDiscoveryPDU parsePDU(byte[] pdu) throws NeighborDiscoveryPDUException {        
        final String methodName = "parsePDU";
        
        String protocolID;
        byte version;
        byte typeOfPDU;
        long requestID;
        
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(pdu));
        
        byte[] pidArray = new byte[NeighborDiscoveryPDU.PROTOCOL_ID.length()];
        
        try {
            dis.read(pidArray, 0, NeighborDiscoveryPDU.PROTOCOL_ID.length());
        } catch (IOException ex) {
            throw new NeighborDiscoveryPDUException(methodName, "Unable to read the protocol ID.");
        }
        
        protocolID = new String(pidArray);
        
        if (!protocolID.equalsIgnoreCase(NeighborDiscoveryPDU.PROTOCOL_ID)) {
            throw new NeighborDiscoveryPDUException(methodName, "Protocol ID mismatch. Expected \"" + NeighborDiscoveryPDU.PROTOCOL_ID + "\" and found " + protocolID);
        }
        
        try {
            version = dis.readByte();
        } catch (IOException ex) {
            throw new NeighborDiscoveryPDUException(methodName, "Unable to read the protocol version.");
        }
        
        if (version != NeighborDiscoveryPDU.VERSION) {
            throw new NeighborDiscoveryPDUException(methodName, "Protocol version mismatch.");
        }
        
        try {
            typeOfPDU = dis.readByte();
        } catch (IOException ex) {
            throw new NeighborDiscoveryPDUException(methodName, "Unable to read the type of PDU.");
        }
        
        if (typeOfPDU != NeighborDiscoveryPDU.NDPDU_REQUEST) {
            if (typeOfPDU != NeighborDiscoveryPDU.NDPDU_RESPONSE) {
                if (typeOfPDU != NeighborDiscoveryPDU.NDPDU_RESPONSE_ACK) {
                    throw new NeighborDiscoveryPDUException(methodName, "Unrecognized PDU type.");
                }
            }
        }
        
        try {
            requestID = dis.readLong();
        } catch (IOException ex) {
            throw new NeighborDiscoveryPDUException(methodName, "Unable to read the request ID.");
        }
        
        if (requestID < 0) {
            throw new NeighborDiscoveryPDUException(methodName, "Invalid request ID.");
        }
        
        return new NeighborDiscoveryPDU(typeOfPDU, requestID);
    }
}
