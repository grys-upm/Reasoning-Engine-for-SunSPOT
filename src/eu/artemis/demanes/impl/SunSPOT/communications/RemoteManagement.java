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
package eu.artemis.demanes.impl.SunSPOT.communications;

import com.sun.spot.core.util.Properties;
import com.sun.spot.multihop.io.j2me.radiogram.Radiogram;
import com.sun.spot.multihop.io.j2me.radiogram.RadiogramConnection;
import com.sun.squawk.util.StringTokenizer;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.common.SystemProperties;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import java.io.IOException;
import javax.microedition.io.Connector;

/**
 *
 * @author Néstor Lucas Martínez &lt;nestor.lucas@upm.es&gt;
 */
public class RemoteManagement implements Runnable {
    private final Logger logger;
    private final Properties reconfigurationProperties;
    private int rmport;
    
    public RemoteManagement(Properties properties) {
        this.logger = SystemContext.getLogger();
        this.reconfigurationProperties = properties;        
    }

    public void run() {
        rmport = Integer.parseInt(reconfigurationProperties.getProperty(SystemProperties.REMOTE_MANAGEMENT_PORT, Integer.toString(SystemProperties.DEFAULT_REMOTE_MANAGEMENT_PORT)));

        RadiogramConnection connection = null;
        Radiogram radiogram;

        while (true) {
            try {
                if (connection == null) {
                    connection = (RadiogramConnection) Connector.open("radiogram://:" + rmport);
                }

                radiogram = (Radiogram) connection.newDatagram(connection.getMaximumLength());
                radiogram.reset();

                connection.receive(radiogram);

                byte[] message = new byte[radiogram.getLength()];
                radiogram.readFully(message);
                String parameterizationString = new String(message);

                logger.info("Received parameterization radiogram:" + parameterizationString);

                // Parse parameters
                StringTokenizer parametersTokens = new StringTokenizer(parameterizationString, ",");
                while (parametersTokens.hasMoreElements()) {
                    String token = (String) parametersTokens.nextElement();
                    String property = token.substring(0, token.indexOf(':'));
                    String value = token.substring(token.indexOf(':') + 1, token.length());
                    logger.info("Parsing parameter " + property + " with value " + value);
                    reconfigurationProperties.setProperty(property, value);
                }

                reconfigurationProperties.setProperty("reset", "true");

            } catch (IOException ex) {
                logger.warning("ERROR: Parameterization communication error.");
            }
        }
    }

}
