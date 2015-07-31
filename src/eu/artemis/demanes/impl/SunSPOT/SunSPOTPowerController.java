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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * You can get a copy of the license terms in licences/LICENSE.
 * 
 */
package eu.artemis.demanes.impl.SunSPOT;

import com.sun.spot.core.resources.Resources;
import com.sun.spot.core.resources.transducers.ISwitch;
import com.sun.spot.core.resources.transducers.ISwitchListener;
import com.sun.spot.core.resources.transducers.LEDColor;
import com.sun.spot.core.resources.transducers.SwitchEvent;
import com.sun.spot.core.util.Properties;
import com.sun.spot.multihop.radio.LowPan;
import com.sun.spot.multihop.radio.mhrp.lqrp.LQRPManager;
import eu.artemis.demanes.impl.SunSPOT.common.RadioProperties;
import eu.artemis.demanes.impl.SunSPOT.common.ReconfigurationContext;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.common.SystemProperties;
import eu.artemis.demanes.impl.SunSPOT.communications.RemoteManagement;
import eu.artemis.demanes.impl.SunSPOT.communications.RoutingListener;
import eu.artemis.demanes.impl.SunSPOT.fuzzyReasoner.PowerScalingControllerFactory;
import eu.artemis.demanes.impl.SunSPOT.reconfiguration.ORAMediatorForSunSPOT;
import eu.artemis.demanes.impl.SunSPOT.reconfiguration.PowerScalingMonitor.PowerScalingMonitor;
import eu.artemis.demanes.impl.SunSPOT.reconfiguration.actions.PowerTransmissionActuator;
import eu.artemis.demanes.impl.SunSPOT.utils.LEDMarquee.Marquee;
import eu.artemis.demanes.impl.SunSPOT.utils.LEDMarquee.MarqueeAddMessageException;
import eu.artemis.demanes.impl.SunSPOT.utils.LEDMarquee.MarqueeMessage;
import eu.artemis.demanes.reconfiguration.ORAMediator;
import eu.artemis.demanes.reconfiguration.Reasoner;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 *
 * The manifest specifies this class as MIDlet-1, which means it will be
 * selected for execution.
 *
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @author Yuanjiang Huang
 * @version 1.0.0
 */
public class SunSPOTPowerController extends MIDlet implements ISwitchListener {

    private static Properties reconfigurationProperties;
    private SystemContext context;
    private Integer marqueeMessageID;
    private MarqueeMessage midletMessage;

    protected void startApp() throws MIDletStateChangeException {

        // The switches each have a tag: "SW1" for the left, and "SW2" for the right
        ISwitch sw1 = (ISwitch) Resources.lookup(ISwitch.class, "SW1");
        ISwitch sw2 = (ISwitch) Resources.lookup(ISwitch.class, "SW2");

        sw1.addISwitchListener(this);
        sw2.addISwitchListener(this);

        // Initalize private attributes
        midletMessage = new MarqueeMessage();

        // This is just a guide for the basic deployment of components
        // STEP 1.1
        // Define the context variables the system.
        // NOTE: The asignment of properties can be done in a separate class.
        reconfigurationProperties = init();

        // STEP 1.2
        // Create a System context.
        context = new SystemContext();

        // STEP 1.3
        // First midlet marquee
        try {
            midletMessage.setMessageAtPosition(0, LEDColor.GREEN);
            SystemContext.getMarquee().setStatusIndicator(Marquee.STATUS_IS_STANDBY);
            marqueeMessageID = (Integer) SystemContext.getMarquee().addMessage(midletMessage);
            SystemContext.getMarquee().setUpdatingInterval(1);
        } catch (MarqueeAddMessageException ex) {
            SystemContext.getMarquee().setStatusIndicator(Marquee.STATUS_IS_NOT_READY);
        }

        // STEP 1.4
        // If connected to an USB port then print license and dump record store history
        midletMessage.setMessageAtPosition(0, LEDColor.WHITE);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);
        if (SystemContext.isUSBConnected()) {
            printLicense();
            dumpRecordHistory();
        }
        midletMessage.setMessageAtPosition(0, LEDColor.GREEN);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);
        
        // STEP 2
        midletMessage.setMessageAtPosition(1, LEDColor.WHITE);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);
        // Instantiate the components of the system.
        // NOTE: Please, pay attention to the order of creation of the objects.
        // For the reconfiguration components...

        // STEP 2.1
        // Create the observers...
        PowerScalingMonitor powerScalingMonitor = new PowerScalingMonitor(context, reconfigurationProperties);

        // ... and the actuators
        PowerTransmissionActuator ptactuator = new PowerTransmissionActuator(reconfigurationProperties);
        context.setPTACT(ptactuator);

        // Notify user through marquee
        midletMessage.setMessageAtPosition(1, LEDColor.GREEN);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);

        // STEP 2.2
        // Create the reasoner
        midletMessage.setMessageAtPosition(2, LEDColor.WHITE);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);

        //PowerScalingController pscontroller = new PowerScalingController(reconfigurationContext);
        //Reasoner reasoner = PowerScalingControllerFactory.getInstance("PTjFuzzyLogic", reconfigurationContext);
        //Reasoner reasoner = PowerScalingControllerFactory.getInstance("PTSimple", reconfigurationContext);
        Reasoner reasoner = PowerScalingControllerFactory.getInstance("PTInterp1DFDM", reconfigurationProperties);

        midletMessage.setMessageAtPosition(2, LEDColor.GREEN);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);

        // Create the trigger policy...
        // ... already done because in our case is the same as the PowerScalingMonitor
        midletMessage.setMessageAtPosition(3, LEDColor.WHITE);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);

        // Create the mediator        
        // This can also be recoded for using a Mediator factory
        // This can be modified to pass some context information if required.
        ORAMediator mediator = new ORAMediatorForSunSPOT();

        midletMessage.setMessageAtPosition(3, LEDColor.GREEN);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);

        // STEP 3
        // Register and start the components of the system.
        // For the reconfiguration components...
        midletMessage.setMessageAtPosition(4, LEDColor.WHITE);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);

        // Register the observers in the mediator
        mediator.registerObserver(powerScalingMonitor);

        // Register the actuators in the mediator
        mediator.registerActuator(ptactuator);

        // Register the reasoner in the mediator;
        mediator.setReasoner(reasoner);

        // Register the trigger policy in the mediator;
        mediator.setTriggeringPolicy(powerScalingMonitor); // At this point the mediator starts the trigger policy after setting it.

        midletMessage.setMessageAtPosition(4, LEDColor.GREEN);
        SystemContext.getMarquee().updateMessage(marqueeMessageID, midletMessage);

        // Everything is launched an running properly
        SystemContext.getMarquee().setStatusIndicator(Marquee.STATUS_IS_READY);
        SystemContext.getMarquee().setUpdatingInterval(Marquee.MINIMUM_INTERVAL);
        SystemContext.getMarquee().clearMarquee();

        // This is just for blinking in routing activities
        RoutingListener rl = new RoutingListener();
        LQRPManager.getInstance().addLQRPListener(rl);
        LowPan.getInstance().addDataEventListener(rl);
        
        // STEP 4
        // Launch remote management thread
        (new Thread(new RemoteManagement(reconfigurationProperties))).start();
    }

    protected void pauseApp() {
        // This is not currently called by the Squawk VM
    }

    /**
     * Called if the MIDlet is terminated by the system. It is not called if
     * MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true the MIDlet must cleanup and release all
     * resources.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException { }

    private Properties init() {
        // STEP 1
        // Sets the spot to use the minimum power of transmission
        //((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).setOutputPower(RadioProperties.MINIMUM_POWER_TRANSMISSION);

        // STEP 2
        // Create default properties
        ReconfigurationContext reconfigurationContext = new ReconfigurationContext();
        reconfigurationProperties = new Properties(reconfigurationContext.getProperties());
                
        // STEP 3.1
        // Add system properties
        if (System.getProperty(SystemProperties.SPOT_URN_NSS) != null) {
            reconfigurationProperties.setProperty(SystemProperties.SPOT_URN_NSS, System.getProperty(SystemProperties.SPOT_URN_NSS));
        }

        if (System.getProperty(SystemProperties.LOG_LEVEL) != null) {
            reconfigurationProperties.setProperty(SystemProperties.LOG_LEVEL, System.getProperty(SystemProperties.LOG_LEVEL));
        }

        if (System.getProperty(SystemProperties.LOG_DESTINATION) != null) {
            reconfigurationProperties.setProperty(SystemProperties.LOG_DESTINATION, System.getProperty(SystemProperties.LOG_DESTINATION));
        }
        
        // STEP 3.2
        // Add other system properties
        reconfigurationProperties.setProperty(SystemProperties.BASESTATION_ADDR_PROPERTY, SystemProperties.DEFAULT_BS_ADDRESS);
        reconfigurationProperties.setProperty(SystemProperties.BASESTATION_PORT, Integer.toString(SystemProperties.DEFAULT_BS_PORT));
        reconfigurationProperties.setProperty(SystemProperties.EXECUTION_CONTEXT, Integer.toString(SystemProperties.DEFAULT_EXECUTION_CONTEXT));
        reconfigurationProperties.setProperty(SystemProperties.REMOTE_MANAGEMENT_PORT, Integer.toString(SystemProperties.DEFAULT_REMOTE_MANAGEMENT_PORT));

        // STEP 4
        // Add running environment properties
        reconfigurationProperties.setProperty("reset", "true");
        reconfigurationProperties.setProperty("ptx", Integer.toString(RadioProperties.MINIMUM_POWER_TRANSMISSION));
        reconfigurationProperties.setProperty("reasoning.status", "started");
        
        // STEP 5
        // Load properties file
        InputStream reconfFile = this.getClass().getResourceAsStream("/reconfiguration.conf");
        
        if (reconfFile != null) {
            try {
                reconfigurationProperties.load(reconfFile);
            } catch (IOException ex) {
                // Do nothing
            }
        }
        
        // STEP 6
        // Update calculated properties
        reconfigurationProperties = reconfigurationContext.updateProperties(reconfigurationProperties);

        // STEP 7
        // Return
        return reconfigurationProperties;                
    }

    private void printLicense() {
        System.out.println("Copyright 2014-2015 Universidad Politécnica de Madrid (UPM).\n"
                + "Some parts Copyright 2013-2015 DEMANES.\n"
                + "\n"
                + "Authors:\n"
                + "   José-Fernan Martínez Ortega\n"
                + "   Vicente Hernández Díaz\n"
                + "   Néstor Lucas Martínez\n"
                + "   Yuanjiang Huang\n"
                + "   Raúl del Toro Matamoros\n"
                + "\n"
                + "Parts copyrighted by Universidad Politécnica de Madrid (UPM) are distributed\n"
                + "under a dual license scheme:\n"
                + "\n"
                + " - For academic uses: Licensed under GNU Affero General Public License as\n"
                + "                      published by the Free Software Foundation, either\n"
                + "                      version 3 of the License, or (at your option) any\n"
                + "                      later version.\n"
                + " \n"
                + " - For any other use: Licensed under the Apache License, Version 2.0.\n"
                + "\n"
                + "Parts copyrighted by DEMANES are distributed under the Apache License, Version 2.0.\n"
                + "\n"
                + "Unless required by applicable law or agreed to in writing, software\n"
                + "distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                + "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                + " \n"
                + "You can get a copy of the license terms in licences/LICENSE.\n"
                + "\n.");
    }
    
    private void dumpRecordHistory() {
        synchronized (this) {
            String[] recordStoreList = RecordStore.listRecordStores();
            if ((recordStoreList != null) && (recordStoreList.length > 0)) {
                for (int recordStoreIndex = 0; recordStoreIndex < recordStoreList.length; recordStoreIndex++) {
                    try {
                        RecordStore recordStore = RecordStore.openRecordStore(recordStoreList[recordStoreIndex], false);
                        System.out.println("Accessing record store \"" + recordStoreList[recordStoreIndex] + "\" with " + recordStore.getNumRecords() + " records");
                        RecordEnumeration recordEnumeration = recordStore.enumerateRecords(null, null, true);
                        while (recordEnumeration.hasNextElement()) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(recordEnumeration.nextRecord());
                            DataInputStream dis = new DataInputStream(bais);

                            byte[] in = new byte[dis.available()];
                            dis.read(in);
                            System.out.println(new String(in));
                        }
                        System.out.println("Read " + recordStore.getNumRecords() + " records");
                        recordStore.closeRecordStore();
                    } catch (RecordStoreException ex) {
                        System.err.println("Exception trying to access record store " + recordStoreList[recordStoreIndex]);
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                System.out.println("No record stores found!!!");
            }
        }
    }

    private void deleteRecordHistory() {
        synchronized (this) {
            String[] recordStoreList = RecordStore.listRecordStores();
            if ((recordStoreList != null) && (recordStoreList.length
                    > 0)) {
                for (int recordStoreIndex = 0; recordStoreIndex < recordStoreList.length; recordStoreIndex++) {
                    try {
                        RecordStore.deleteRecordStore(recordStoreList[recordStoreIndex]);
                    } catch (RecordStoreException ex) {
                        System.err.println("Exception trying to access record store " + recordStoreList[recordStoreIndex]);
                        ex.printStackTrace();
                    }
                }
            } else {
                System.out.println("No record stores found!!!");
            }
        }
    }

    public void switchPressed(SwitchEvent evt) {
        if (SystemContext.isUSBConnected()) {
            deleteRecordHistory();
            System.out.println("Records deleted");
        }
    }

    public void switchReleased(SwitchEvent evt) {

    }
}
