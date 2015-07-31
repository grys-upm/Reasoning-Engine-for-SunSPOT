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
package eu.artemis.demanes.impl.SunSPOT.reconfiguration.PowerScalingMonitor;

import com.sun.spot.core.resources.Resources;
import com.sun.spot.core.resources.transducers.LEDColor;
import com.sun.spot.core.util.Properties;
import com.sun.spot.core.util.Utils;
import com.sun.spot.espot.peripheral.ESpot;
import com.sun.spot.ieee_802_15_4_radio.IRadioPolicyManager;
import com.sun.spot.ieee_802_15_4_radio.util.IEEEAddress;
import com.sun.spot.multihop.radio.mhrp.lqrp.LQRPManager;
import com.sun.squawk.util.MathUtils;
import eu.artemis.demanes.datatypes.ANES_URN;
import eu.artemis.demanes.datatypes.ANES_URN_Exception;
import eu.artemis.demanes.exceptions.ObservationInvocationException;
import eu.artemis.demanes.impl.SunSPOT.common.DEMANESResources;
import eu.artemis.demanes.impl.SunSPOT.common.Events;
import eu.artemis.demanes.impl.SunSPOT.common.RadioProperties;
import eu.artemis.demanes.impl.SunSPOT.common.ReasonerProperties;
import eu.artemis.demanes.impl.SunSPOT.common.SystemContext;
import eu.artemis.demanes.impl.SunSPOT.common.SystemProperties;
import eu.artemis.demanes.impl.SunSPOT.common.TriggerPolicyProperties;
import eu.artemis.demanes.impl.SunSPOT.communications.CommunicationManager;
import eu.artemis.demanes.impl.SunSPOT.reconfiguration.actions.ResetActuator;
import eu.artemis.demanes.impl.SunSPOT.reconfiguration.observations.BatteryLevelObservation;
import eu.artemis.demanes.impl.SunSPOT.reconfiguration.observations.NodeDegreeObservation;
import eu.artemis.demanes.impl.SunSPOT.reconfiguration.observations.NodeDegreeObservationExtension;
import eu.artemis.demanes.impl.SunSPOT.utils.LEDMarquee.MarqueeAddMessageException;
import eu.artemis.demanes.impl.SunSPOT.utils.LEDMarquee.MarqueeMessage;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Level;
import eu.artemis.demanes.impl.SunSPOT.utils.logging.Logger;
import eu.artemis.demanes.reconfiguration.Observation;
import eu.artemis.demanes.reconfiguration.Observer;
import eu.artemis.demanes.reconfiguration.TriggerPolicy;
import eu.artemis.demanes.reconfiguration.Triggerable;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Power Scaling Monitor is a {@link TriggerPolicy} that fires the
 * {@link Reasoner} when certain conditions occur. At the same time it is also a
 * {@link Observer} that provides the required {@link Observations} for the
 * reasoning.
 *
 * @author Vicente Hern&aacute;ndez D&iacute;z
 * @author Yuanjiang Huang
 * @author N&eacute;stor Lucas Mart&iacute;nez
 * @version 1.0.0
 */
public class PowerScalingMonitor implements TriggerPolicy, Observer {

    public static final int STARTED = 1;
    public static final int CREATED = 2;
    public static final int SUSPENDED = 3;
    public static final int DIED = 4;

    private static final long DEFAULT_INTERVAL = 1000;

    private static final LEDColor ND_COLOR = LEDColor.TEAL;
    private static final LEDColor BATTERY_COLOR = LEDColor.ORANGE;
    private static final LEDColor PTX_COLOR = LEDColor.MAGENTA;

    private int state;

    private Vector triggerables;
    private Properties reconfigurationProperties;

    private long interval;
    private Observation ND;
    private Observation BL;
    private Observation reasonObservation;
    private Observation reasonObservationValue;

    private ANES_URN cause = null;
    private double error;
    private Vector observationList;

    private Logger logger;

    private int CR_at_previous_k;  // CR(k-1)

    private double ND_Error_at_previous_k; // e(k-1)
    private int dcr_at_previous_k;   // dcr(k-1)
    private int CR_at_previous_k2;   // CR(k-2)
    private int ND_R_previous_k;  // ND_R(k-1)
    private int NDRef_at_previous_k; // NDRef(k-1) -- Used only for updating the value after an external updating of the parameter

    // fixed parameters for the controller
    private double KSI_ND;
    private int ND_MIN;
    private int CR_MAX;
    private int delta_ND_min;
    private double k_e;
    private double alpha_KSI_ND;
    private int DELTA_CR_MIN;
    private double E_cr;

    private int ND_at_k;
    private int count;
    private boolean CR_at_previous_k2_is_ready;  // CR(k-2) is ready only after 2 rounds triggery policy. 
    private int triggeryRuleCount;

    private SystemContext context;

    private LQRPManager routingManager;

    private MarqueeMessage nodeDegreeMessage;
    private MarqueeMessage batteryLevelMessage;
    private MarqueeMessage transmissionPowerMessage;

    private Integer nodeDegreeMessageID;
    private Integer batteryLevelMessageID;
    private Integer transmissionPowerMessageID;

    public PowerScalingMonitor(SystemContext context, Properties reconfigurationProperties) {
        this.triggerables = new Vector();
        this.state = PowerScalingMonitor.CREATED;
        this.reconfigurationProperties = reconfigurationProperties;
        this.context = context;

        this.routingManager = LQRPManager.getInstance();

        this.logger = SystemContext.getLogger();

        this.CR_at_previous_k = Integer.MAX_VALUE;
        this.CR_at_previous_k2 = Integer.MAX_VALUE;
        dcr_at_previous_k = Integer.MAX_VALUE;
        this.count = 3;
        this.CR_at_previous_k2_is_ready = false; // CR(k-2)
        this.triggeryRuleCount = 1;  //record the triggery rule execution times

        String property;
        property = reconfigurationProperties.getProperty(TriggerPolicyProperties.ND_MIN_PROPERTY);
        ND_MIN = Integer.parseInt(property);

        property = reconfigurationProperties.getProperty(TriggerPolicyProperties.CR_MAX_PROPERTY);
        CR_MAX = Integer.parseInt(property);

        property = reconfigurationProperties.getProperty(TriggerPolicyProperties.DELTA_ND_MIN_PROPERTY);
        delta_ND_min = Integer.parseInt(property);

        property = reconfigurationProperties.getProperty(ReasonerProperties.KE_PROPERTY);
        k_e = Double.parseDouble(property);

        property = reconfigurationProperties.getProperty(TriggerPolicyProperties.KSI_ND_PROPERTY);
        KSI_ND = Double.parseDouble(property);

        property = reconfigurationProperties.getProperty(TriggerPolicyProperties.DELTA_CR_MIN_PROPERTY);
        DELTA_CR_MIN = Integer.parseInt(property);

        // obtain the critical battery level    
        property = reconfigurationProperties.getProperty(ReasonerProperties.E_CR_PROPERTY);
        E_cr = Double.parseDouble(property);

        // Read the ND_Ref property
        property = reconfigurationProperties.getProperty(ReasonerProperties.NDRefPROPERTY);
        NDRef_at_previous_k = Integer.parseInt(property);

        try {
            this.cause = ANES_URN.create(Events.BL_EVENT);
        } catch (ANES_URN_Exception ex) {
            this.cause = null;
        }
        error = 0;

        this.interval = Long.parseLong(reconfigurationProperties.getProperty(TriggerPolicyProperties.TRIGGER_INTERVAL_PROPERTY, String.valueOf(DEFAULT_INTERVAL)));
        logger.debug("Monitor interval set to " + interval + " milliseconds");

        observationList = new Vector();
        ND = new NodeDegreeObservation(reconfigurationProperties, DEMANESResources.NDObservationURN);
        ((NodeDegreeObservation) ND).start();
        BL = new BatteryLevelObservation(reconfigurationProperties, DEMANESResources.BLObservationURN);
        reasonObservation = new ReasonObservation();
        reasonObservationValue = new ReasonObservationValue();
        addObservation(ND);
        addObservation(BL);
        addObservation(reasonObservation);
        addObservation(reasonObservationValue);

        try {
            nodeDegreeMessage = new MarqueeMessage();
            batteryLevelMessage = new MarqueeMessage();
            transmissionPowerMessage = new MarqueeMessage();
            nodeDegreeMessageID = (Integer) SystemContext.getMarquee().addMessage(nodeDegreeMessage);
            batteryLevelMessageID = (Integer) SystemContext.getMarquee().addMessage(batteryLevelMessage);
            transmissionPowerMessageID = (Integer) SystemContext.getMarquee().addMessage(transmissionPowerMessage);
        } catch (MarqueeAddMessageException ex) {
            // DO NOTHING
        }

    }

    public void registerTriggerable(Triggerable t) {
        this.triggerables.addElement(t);
    }

    public void resume() {

        if (this.state == PowerScalingMonitor.SUSPENDED) {
            this.state = PowerScalingMonitor.STARTED;
        }

    }

    public void start() {

        if (this.state != PowerScalingMonitor.CREATED) {
            return;
        }

        this.state = PowerScalingMonitor.STARTED;

        new Thread(
                new Runnable() {
                    public void run() {
                        short sequenceNumber = 0;

                        showOnMarqueeBinary(ND_at_k, ND_COLOR, nodeDegreeMessage, nodeDegreeMessageID);
                        showOnMarqueeBinary(((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower() - RadioProperties.MINIMUM_POWER_TRANSMISSION, PTX_COLOR, transmissionPowerMessage, transmissionPowerMessageID);
                        showOnMarqueePercentage(((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getAvailableCapacity(),
                                ((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getMaximumCapacity(),
                                BATTERY_COLOR,
                                batteryLevelMessage,
                                batteryLevelMessageID);

                        logger.info("Status monitoring message sent!");
                        logger.info("Number of neighbors at k: " + ND_at_k);
                        logger.info("PTX at k: " + (((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower() - RadioProperties.MINIMUM_POWER_TRANSMISSION));

                        while (true) {
                            sequenceNumber++;
                            if (reconfigurationProperties.getProperty("reset").equalsIgnoreCase("true")) {
                                logger.info("#> RESETING transmission values");
                                reconfigurationProperties.setProperty("reset", "false");

                                int ptx = Integer.parseInt(reconfigurationProperties.getProperty("ptx"));

                                ((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).setOutputPower(ptx);
                                CR_at_previous_k = Integer.MAX_VALUE;
                                CR_at_previous_k2 = Integer.MAX_VALUE;
                                dcr_at_previous_k = Integer.MAX_VALUE;
                                count = 3;
                                CR_at_previous_k2_is_ready = false; // CR(k-2)

                                ((ResetActuator) context.getPTACT()).reset();
                            }

                            ((NodeDegreeObservation) ND).updateNodeDegree();

                            interval = Long.parseLong(reconfigurationProperties.getProperty(TriggerPolicyProperties.TRIGGER_INTERVAL_PROPERTY, String.valueOf(DEFAULT_INTERVAL)));
                            logger.debug("Monitor interval set to " + interval + " milliseconds");

                            Utils.sleep(interval - 500);

                            if (reconfigurationProperties.getProperty("reasoning.status").equalsIgnoreCase("started")) {

                                switch (getState()) {
                                    case PowerScalingMonitor.STARTED:
                                        if (triggerRules()) {
                                            ((Triggerable) triggerables.firstElement()).trigger();
                                        }
                                        break;
                                    case PowerScalingMonitor.DIED:
                                        state = PowerScalingMonitor.CREATED;
                                        break;
                                    case PowerScalingMonitor.SUSPENDED:
                                        break;
                                    case PowerScalingMonitor.CREATED:
                                        break;
                                    default:
                                        break;
                                }

                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            DataOutputStream daos = new DataOutputStream(baos);

                            String destination = reconfigurationProperties.getProperty(SystemProperties.BASESTATION_ADDR_PROPERTY, SystemProperties.DEFAULT_BS_ADDRESS);
                            String port = reconfigurationProperties.getProperty(SystemProperties.BASESTATION_PORT, Integer.toString(SystemProperties.DEFAULT_BS_PORT));

                            daos = getStatusMessage(sequenceNumber, destination);
                            recordStatus(sequenceNumber, destination);

                            showOnMarqueeBinary(ND_at_k, ND_COLOR, nodeDegreeMessage, nodeDegreeMessageID);
                            showOnMarqueeBinary(((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower() - RadioProperties.MINIMUM_POWER_TRANSMISSION, PTX_COLOR, transmissionPowerMessage, transmissionPowerMessageID);
                            showOnMarqueePercentage(((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getAvailableCapacity(),
                                    ((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getMaximumCapacity(),
                                    BATTERY_COLOR,
                                    batteryLevelMessage,
                                    batteryLevelMessageID);

                            (context.getCommunicationManager()).send(baos, 0, CommunicationManager.MODE_UNICAST, destination + ":" + port, 0, 0);
                            logger.info("Status monitoring message sent!");
                            logger.info("Number of neighbors at k: " + ND_at_k);
                            logger.info("PTX at k: " + (((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower() - RadioProperties.MINIMUM_POWER_TRANSMISSION));
                        }
                    }

                    private void showOnMarqueeBinary(int value, LEDColor color, MarqueeMessage message, Integer messageID) {
                        if (value > MathUtils.pow(2, MarqueeMessage.MESSAGE_LENGTH)) {
                            value = (byte) MathUtils.pow(2, MarqueeMessage.MESSAGE_LENGTH);
                        }
                        for (int index = 0; index < MarqueeMessage.MESSAGE_LENGTH; index++) {
                            int remainder = value % 2;

                            if (remainder == 1) {
                                message.setMessageAtPosition((MarqueeMessage.MESSAGE_LENGTH - 1) - index, color);
                            } else {
                                message.setMessageAtPosition((MarqueeMessage.MESSAGE_LENGTH - 1) - index, new LEDColor(0, 0, 0));
                            }
                            value = value / 2;
                        }
                        SystemContext.getMarquee().updateMessage(messageID, message);
                    }

                    private void showOnMarqueePercentage(double actual, double maximum, LEDColor color, MarqueeMessage message, Integer messageID) {
                        double percentage = actual * 100 / maximum;
                        int totalLights = 0;
                        if (percentage > 80) {
                            totalLights = 5;
                        } else if (percentage > 60) {
                            totalLights = 4;
                        } else if (percentage > 40) {
                            totalLights = 3;
                        } else if (percentage > 20) {
                            totalLights = 2;
                        } else if (percentage > 0) {
                            totalLights = 1;
                        } else {
                            totalLights = 0;
                        }

                        for (int index = 0; index < totalLights; index++) {
                            message.setMessageAtPosition(index, color);
                        }

                        SystemContext.getMarquee().updateMessage(messageID, message);
                    }
                }
        ).start();
    }

    private boolean triggerRules() {

        int ND_R_at_k = Integer.MAX_VALUE;
        int NDRef;
        String property = null;
        boolean result = false;

        try {

            // The nd_real indicates the current ND                              
            ND_at_k = Integer.parseInt(ND.getValue().toString());
            logger.debug("triggerRules: ND read as " + ND_at_k);

            // The bl_real indicates the current power transmission in dBm   
            double bl_real = Double.parseDouble(BL.getValue().toString());
            logger.debug("triggerRules: BL read as " + bl_real);

            // PLEASE, THE LOAD OF THE PARAMETERS MUST BE DONE IN A SEPARATE METHOD.
            // ALSO IT MUST BE SPLITTED INTO FIXED AND DYNAMIC PARAMETERS.
            // obtain the node degree reference    
            property = reconfigurationProperties.getProperty(ReasonerProperties.ND_R_PROPERTY);
            ND_R_at_k = Integer.parseInt(property);
            logger.debug("PSMonitor: Parsed property " + ReasonerProperties.ND_R_PROPERTY + " as " + ND_R_at_k);

            // The following lines load the parameters that the trigger rules need
            property = reconfigurationProperties.getProperty(TriggerPolicyProperties.KSI_ND_PROPERTY);
            double KSI_ND = Double.parseDouble(property);

            // --------------------------------------------------------------
            // CALCULATION of variables requiered for the triggering policy
            // ----------------------------------------------------------------
            // Node Degree error at k
            double e_ND = ND_R_at_k - ND_at_k;
            // Baterry Level error at k
            double e_BL = E_cr - bl_real;

            // Communication Range at k
            int CR_at_k = ((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower();

            logger.info("triggerRules: read CR_at_k = " + CR_at_k);
            // now use the index
            CR_at_k = RadioProperties.getPTXIndex(RadioProperties.getRadioChannel(), CR_at_k);

            logger.info("triggerRules: convert CR_at_k into index, now CR_at_k = " + CR_at_k);

            logger.info("Run the triggery rule: " + this.triggeryRuleCount);
            this.triggeryRuleCount++;

            // If previous Communication Range is Integer.MAX_VALUE, this means
            // that this is the first reading of the Communication Range, so the
            // previous value of the Communication Range should be set to the
            // actual one.
            // And other variables are initialized here. 
            if (CR_at_previous_k == Integer.MAX_VALUE) {

                CR_at_previous_k = CR_at_k;
                CR_at_previous_k2 = Integer.MAX_VALUE;
                ND_Error_at_previous_k = e_ND;
                ND_R_previous_k = ND_R_at_k;  // ND reference                
            }

            this.count--;
            if (this.count == 0) {
                this.CR_at_previous_k2_is_ready = true;
                logger.info("dcr_at_previous_k2 now available !"); //dcr_at_previous_k2 now is valid
            }
            if (this.CR_at_previous_k2_is_ready) {

                // To calcuate dcr = cr(k-1) - cr(k-2), use the index, other than real values
                dcr_at_previous_k = CR_at_previous_k - CR_at_previous_k2;

                logger.info("calcuate dcr_at_previous_k, CR_at_previous_k = " + CR_at_previous_k);
                logger.info("calcuate dcr_at_previous_k, CR_at_previous_k2 = " + CR_at_previous_k2);
                //  CR_at_previous_k2 = CR_at_previous_k; 
            }

            // Filtered Communication Range
            int CR_f = MathUtils.round((CR_at_k + CR_at_previous_k) / 2);

            CR_at_previous_k2 = CR_at_previous_k;
            CR_at_previous_k = CR_at_k;

            // Checking the rules in the order specified by the
            // reconfiguration algorithm v10.
            // ----------------------------------------------------------------
            // The first rule:
            // ----------------------------------------------------------------
            // Updating power transmission when the reference value has been adapted. 
            //(Rule No. 1 or secondary loop execution) 
            if ((Math.abs(e_ND) <= KSI_ND)
                    && (CR_f >= CR_MAX)
                    && ((ND_R_previous_k != ND_R_at_k) || (e_ND < ND_Error_at_previous_k))) {
                double previous_e_ND = e_ND;
                // e_ND = -1 * alpha_KSI_ND * KSI_ND_at_k;
                e_ND = -1 * (alpha_KSI_ND * DELTA_CR_MIN + DELTA_CR_MIN);

                logger.info("triggerRules 1st triggered");

                logger.info("triggerPolicy.SaturationRule1: e_ND adjusted to " + e_ND + " when CR_f is " + CR_f + " and previous e_ND was " + previous_e_ND + " with KSI_ND_at_k being " + KSI_ND);
            }

            // ----------------------------------------------------------------
            // The second rule: 
            // ----------------------------------------------------------------
            logger.info("ND_Error_at_previous_k = " + ND_Error_at_previous_k);
            logger.info("e_ND = " + e_ND);
            logger.info("dcr_at_previous_k = " + dcr_at_previous_k);
            logger.info("ND_Error_at_previous_k = " + ND_Error_at_previous_k);
            logger.info("dcr_at_previous_k = " + dcr_at_previous_k);
            logger.info("CR_at_previous_k = " + CR_at_previous_k);
            logger.info("CR_at_previous_k2 = " + CR_at_previous_k2);
            logger.info("DELTA_CR_MIN = " + DELTA_CR_MIN);
            logger.info("KSI_ND = " + KSI_ND);

            if (((ND_Error_at_previous_k * e_ND > 0) && (dcr_at_previous_k == 0))
                    || ((e_ND < -KSI_ND) && (ND_Error_at_previous_k > KSI_ND) && (dcr_at_previous_k == DELTA_CR_MIN))
                    || ((e_ND > KSI_ND) && (ND_Error_at_previous_k < -KSI_ND) && (dcr_at_previous_k == -DELTA_CR_MIN))) {

                result = false;

                logger.info("triggerRules 2nd triggered: DO NOTHING ");

            } else {
                // When the error in ND is greater than the tolerance at k or the
                // ND is below the minimum, then the primary loop must be triggered.
                if ((Math.abs(e_ND) > KSI_ND) || (ND_at_k < ND_MIN)) {
                    try {
                        this.cause = ANES_URN.create(Events.ND_EVENT);
                        this.error = e_ND;
                        result = true;
                        logger.info("triggerRules 2nd rule triggered: ND event");
                    } catch (ANES_URN_Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            // updating 
            ND_Error_at_previous_k = e_ND; // e(k-1) update at this point
            ND_R_previous_k = ND_R_at_k; // ND_R(k-1) update at this point

            // ----------------------------------------------------------------
            // The third rule: 
            // ----------------------------------------------------------------
            // When the battery reaches some critical value, then the secondary
            // loop must be executed.
            if (Math.abs(e_BL) <= 1.0 / k_e) {

                try {
                    this.cause = ANES_URN.create(Events.BL_EVENT);
                    this.error = e_BL;
                    result = true;
                    logger.info("triggerRules 3rd rule trigger");
                } catch (ANES_URN_Exception ex) {
                    ex.printStackTrace();
                }
            }

            // EXTRA RULE to update the ND_R value upon a modification of the ND_Ref property
            property = reconfigurationProperties.getProperty(ReasonerProperties.NDRefPROPERTY);
            NDRef = Integer.parseInt(property);
            if (NDRef != NDRef_at_previous_k) {
                try {
                    this.cause = ANES_URN.create(Events.BL_EVENT);
                    this.error = e_BL;
                    result = true;
                    logger.info("NDRef updated -> trigger 2nd loop for updating ND_r");
                } catch (ANES_URN_Exception ex) {
                    ex.printStackTrace();
                }
            }
            NDRef_at_previous_k = NDRef;

        } catch (ObservationInvocationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public void stop() {

        if (this.state == PowerScalingMonitor.STARTED) {
            this.state = PowerScalingMonitor.DIED;
        }

    }

    public void suspend() {

        if (this.state == PowerScalingMonitor.STARTED) {
            this.state = PowerScalingMonitor.SUSPENDED;
        }
    }

    public void unregisterTriggerable(Triggerable t) {

        for (int cursor = 0; cursor < triggerables.size(); cursor++) {
            if (((Triggerable) triggerables.elementAt(cursor)).equals(t)) {
                triggerables.removeElementAt(cursor);
                break;
            }
        }

    }

    public Vector getObservations() {
        return observationList;
    }

    private void addObservation(Observation observation) {
        this.observationList.addElement(observation);
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    private DataOutputStream getStatusMessage(int sequenceNumber, String destination) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);

        try {
            switch (Byte.parseByte(reconfigurationProperties.getProperty(SystemProperties.EXECUTION_CONTEXT, Integer.toString(SystemProperties.DEFAULT_EXECUTION_CONTEXT)))) {
                case SystemProperties.EXECUTION_CONTEXT_CMS:
                    String ABATurn = "urn:dmns:abat";
                    String MBATurn = "urn:dmns:mbat";
                    String NDEGurn = "urn:dmns:ndeg";
                    String NPTXurn = "urn:dmns:nptx";
                    String SYSIDurn = "urn:dmns:sysid";
                    String IEEELocalAddress = IEEEAddress.toDottedHex(((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getIEEEAddress());

                    daos.writeByte(0xA0);
                    daos.writeByte(0x02);
                    daos.writeShort(sequenceNumber);
                    daos.writeByte(0xFF);
                    daos.write("urn:dmns:log?".getBytes(), 0, 13);
                    daos.writeByte(SYSIDurn.length());
                    daos.write(SYSIDurn.getBytes(), 0, SYSIDurn.length());
                    daos.writeByte(IEEELocalAddress.length());
                    daos.write(IEEELocalAddress.getBytes(), 0, IEEELocalAddress.length());
                    daos.writeByte(ABATurn.length());
                    daos.write(ABATurn.getBytes(), 0, ABATurn.length());
                    daos.writeByte(0x08); // Length of double
                    daos.writeDouble((((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getAvailableCapacity()));
                    daos.writeByte(MBATurn.length());
                    daos.write(MBATurn.getBytes(), 0, MBATurn.length());
                    daos.writeByte(0x08);
                    daos.writeDouble((((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getMaximumCapacity()));
                    daos.writeByte(NDEGurn.length());
                    daos.write(NDEGurn.getBytes(), 0, NDEGurn.length());
                    daos.writeByte(0x04);
                    daos.writeInt(ND_at_k);
                    daos.writeByte(NPTXurn.length());
                    daos.write(NPTXurn.getBytes(), 0, NPTXurn.length());
                    daos.writeByte(1);
                    daos.writeByte(((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower());
                    break;
                case SystemProperties.EXECUTION_CONTEXT_EXPERIMENTAL:
                    daos.writeLong(sequenceNumber);
                    daos.writeLong(System.currentTimeMillis());
                    daos.writeByte(ND_at_k);
                    daos.writeDouble((((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getMaximumCapacity()));
                    daos.writeDouble((((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getAvailableCapacity()));
                    daos.writeByte(((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower());
                    daos.writeByte(Integer.parseInt(reconfigurationProperties.getProperty(ReasonerProperties.ND_R_PROPERTY)));
                    daos.writeByte(Integer.parseInt(reconfigurationProperties.getProperty(TriggerPolicyProperties.KSI_ND_PROPERTY)));
                    daos.writeDouble(Double.parseDouble(reconfigurationProperties.getProperty(ReasonerProperties.KCR_PROPERTY)));
                    daos.writeDouble(Double.parseDouble(reconfigurationProperties.getProperty(ReasonerProperties.E_CR_PROPERTY)));
                    routingManager.findRoute(IEEEAddress.toLong(destination), null, Long.valueOf(sequenceNumber));
                    Utils.sleep(500);
                    daos.writeLong(routingManager.getRouteInfo(IEEEAddress.toLong(destination)).nextHop);
                    break;
                default:
                    break;
            }
        } catch (IOException ex) {
            return null;
        }

        return daos;
    }

    public void recordStatus(int sequenceNumber, String destination) {
        try {
            Vector neighborList = ((NodeDegreeObservationExtension) ND).getNeighborList();
            StringBuffer csvdata = new StringBuffer();
            csvdata.append(sequenceNumber);
            csvdata.append(',');
            csvdata.append(System.currentTimeMillis());
            csvdata.append(',');
            csvdata.append(ND_at_k);
            csvdata.append(',');
            csvdata.append((((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getMaximumCapacity()));
            csvdata.append(',');
            csvdata.append((((ESpot) Resources.lookup(ESpot.class)).getPowerController().getBattery().getAvailableCapacity()));
            csvdata.append(',');
            csvdata.append(((IRadioPolicyManager) Resources.lookup(IRadioPolicyManager.class)).getOutputPower());
            csvdata.append(',');
            csvdata.append(Integer.parseInt(reconfigurationProperties.getProperty(ReasonerProperties.ND_R_PROPERTY)));
            csvdata.append(',');
            csvdata.append(Integer.parseInt(reconfigurationProperties.getProperty(TriggerPolicyProperties.KSI_ND_PROPERTY)));
            csvdata.append(',');
            csvdata.append(Double.parseDouble(reconfigurationProperties.getProperty(ReasonerProperties.KCR_PROPERTY)));
            csvdata.append(',');
            csvdata.append(Double.parseDouble(reconfigurationProperties.getProperty(ReasonerProperties.E_CR_PROPERTY)));
            csvdata.append(',');
            csvdata.append(IEEEAddress.toDottedHex(routingManager.getRouteInfo(IEEEAddress.toLong(destination)).nextHop));
            csvdata.append(',');
            for (int i = 0; i < neighborList.size(); i++) {
                csvdata.append((String) neighborList.elementAt(i));
                csvdata.append(',');
            }

            logger.setDestination(Logger.RECORD);
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            DataOutputStream daos2 = new DataOutputStream(baos2);
            daos2.write(csvdata.toString().getBytes());
            logger.log(Level.ALL, baos2);
            logger.setDestination(Logger.SYSTEM_OUTPUT);
        } catch (IOException ex) {
            // Do nothing
        }
    }

    // PRIVATE CLASSES
    private class ReasonObservation implements Observation {

        /**
         * Gets the ID of an observation.
         *
         * @return the ANES_URN of observation.
         */
        public ANES_URN getObservationID() {
            try {
                return ANES_URN.create(DEMANESResources.TriggerReasonURN);
            } catch (ANES_URN_Exception ex) {
                logger.log(Level.SEVERE, "NodeDegreeObservation.getObservationID: ANES_URN.create reported and error with DEMANESResources.BLObservationURN.");
                return null;
            }
        }

        /**
         * Get the reason {@link ANES_URN}.
         *
         * @return The reason {@link ANES_URN}.
         * @throws ObservationInvocationException
         */
        public Object getValue() throws ObservationInvocationException {
            return cause;
        }
    }

    private class ReasonObservationValue implements Observation {

        /**
         * Gets the ID of an observation.
         *
         * @return the ANES_URN of observation.
         */
        public ANES_URN getObservationID() {
            try {
                return ANES_URN.create(DEMANESResources.TriggerReasonValueURN);
            } catch (ANES_URN_Exception ex) {
                logger.log(Level.SEVERE, "NodeDegreeObservation.getObservationID: ANES_URN.create reported and error with DEMANESResources.BLObservationURN.");
                return null;
            }
        }

        /**
         * Get the reason error value.
         *
         * @return The reason error value.
         * @throws ObservationInvocationException
         */
        public Object getValue() throws ObservationInvocationException {
            return Double.valueOf(error);
        }
    }

}
