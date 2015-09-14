/*
Copyright (C) 2008-2009  Santiago Carot Nemesio
email: scarot@libresoft.es

This program is a (FLOS) free libre and open source implementation
of a multiplatform manager device written in java according to the
ISO/IEEE 11073-20601. Manager application is designed to work in
DalvikVM over android platform.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package ieee_11073.part_20601.fsm.manager;

import java.io.ByteArrayOutputStream;

import org.bn.CoderFactory;
import org.bn.IEncoder;

import es.libresoft.openhealth.events.Event;
import es.libresoft.openhealth.events.EventType;
import es.libresoft.openhealth.messages.MessageFactory;
import es.libresoft.openhealth.utils.ASN1_Tools;
import es.libresoft.openhealth.utils.ASN1_Values;
import ieee_11073.part_10101.Nomenclature;
import ieee_11073.part_20601.asn1.ApduType;
import ieee_11073.part_20601.asn1.ConfigReport;
import ieee_11073.part_20601.asn1.ConfigReportRsp;
import ieee_11073.part_20601.asn1.DataApdu;
import ieee_11073.part_20601.asn1.EventReportArgumentSimple;
import ieee_11073.part_20601.asn1.EventReportResultSimple;
import ieee_11073.part_20601.asn1.HANDLE;
import ieee_11073.part_20601.asn1.INT_U32;
import ieee_11073.part_20601.asn1.PrstApdu;
import ieee_11073.part_20601.asn1.RelativeTime;
import ieee_11073.part_20601.asn1.DataApdu.MessageChoiceType;
import ieee_11073.part_20601.fsm.Configuring;
import ieee_11073.part_20601.fsm.StateHandler;

public final class CheckingConfig extends Configuring {

	public CheckingConfig (StateHandler handler) {
		super(handler);
	}

	@Override
	public synchronized String getStateName() {
		return "CheckingConfig";
	}

	@Override
	public synchronized void process(ApduType apdu) {
		if (apdu.isPrstSelected()){
			process_PrstApdu(apdu.getPrst());
		}else if (apdu.isRlrqSelected()) {
			//The manager has received a request to release the association
			state_handler.send(MessageFactory.RlreApdu_NORMAL());
			state_handler.changeState(new MUnassociated(state_handler));
		}else if(apdu.isAarqSelected() || apdu.isAareSelected() || apdu.isRlreSelected()){
			state_handler.send(MessageFactory.AbrtApdu_UNDEFINED());
			state_handler.changeState(new MUnassociated(state_handler));
		}else if(apdu.isAbrtSelected()){
			state_handler.changeState(new MUnassociated(state_handler));
		}
	}

	@Override
	public synchronized boolean processEvent(Event event) {
		if (event.getTypeOfEvent() == EventType.IND_TRANS_DESC) {
			System.err.println("2.2) IND Transport disconnect. Should indicate to application layer...");
			state_handler.changeState(new MDisconnected(state_handler));
		}else if (event.getTypeOfEvent() == EventType.IND_TIMEOUT) {
			state_handler.send(MessageFactory.AbrtApdu_CONFIGURATION_TIMEOUT());
			state_handler.changeState(new MUnassociated(state_handler));
		}else if (event.getTypeOfEvent() == EventType.REQ_ASSOC_REL){
			state_handler.send(MessageFactory.RlrqApdu_NORMAL());
			state_handler.changeState(new MDisassociating(state_handler));
		}else if (event.getTypeOfEvent() == EventType.REQ_ASSOC_ABORT){
			state_handler.send(MessageFactory.AbrtApdu_UNDEFINED());
			state_handler.changeState(new MUnassociated(state_handler));
		} else
			return false;

		return true;
	}

	/**
	 * This method set the current state to CheckingConfig State (if it is not yet)
	 * and shall check the provided data to determinate if it should transit to Operating
	 * state or not.
	 * @param data
	 */
	public void checkNotiConfig (DataApdu data) {
		state_handler.changeState(this);
		MessageChoiceType msg = data.getMessage();
		try {
			checkingConfig (data, getConfigResponse(msg.getRoiv_cmip_confirmed_event_report()));
		} catch (Exception e) {
			//TODO: Send Response Error same as roiv_cmip_confirmed_event_repor function of WaitingForConfig state
			e.printStackTrace();
			System.err.println("TODO: Send Response Error");
		}
	}

	//---------------------------------------------PRIVATE--------------------------------------------------------------
	private void process_PrstApdu(PrstApdu prst){
		try {
			/*
			 * The DataApdu and the related structures in A.10 shall use encoding rules
			 * as negotiated during the association procedure as described in 8.7.3.1.
			 */
			processDataApdu(ASN1_Tools.decodeData(prst.getValue(),
					DataApdu.class,
					this.state_handler.getMDS().getDeviceConf().getEncondigRules()));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error getting DataApdu encoded with " +
					this.state_handler.getMDS().getDeviceConf().getEncondigRules() +
					". The connection will be released.");
			state_handler.send(MessageFactory.RlrqApdu_NORMAL());
			state_handler.changeState(new MDisassociating(state_handler));
		}
	}

	private void processDataApdu(DataApdu data) {
		MessageChoiceType msg = data.getMessage();
		//Process the message received
		if (msg.isRoiv_cmip_confirmed_event_reportSelected()) {
			roiv_cmip_confirmed_event_repor(data);
		}else if (msg.isRoiv_cmip_event_reportSelected()) {
			// The agent only sends event report messages. This should never happen
			state_handler.send(MessageFactory.ROER_NO_SUCH_ACTION_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
			state_handler.changeState(new MUnassociated(state_handler));
		}else if (msg.isRoiv_cmip_getSelected()) {
			// The agent only sends event report messages. This should never happen
			state_handler.send(MessageFactory.ROER_NO_SUCH_ACTION_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
			state_handler.changeState(new MUnassociated(state_handler));
		}else if (msg.isRoiv_cmip_setSelected()) {
			// The agent only sends event report messages. This should never happen
			state_handler.send(MessageFactory.ROER_NO_SUCH_ACTION_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
			state_handler.changeState(new MUnassociated(state_handler));
		}else if (msg.isRoiv_cmip_confirmed_setSelected()) {
			// The agent only sends event report messages. This should never happen
			state_handler.send(MessageFactory.ROER_NO_SUCH_ACTION_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
			state_handler.changeState(new MUnassociated(state_handler));
		}else if (msg.isRoiv_cmip_actionSelected()){
			// The agent only sends event report messages. This should never happen
			state_handler.send(MessageFactory.ROER_NO_SUCH_ACTION_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
			state_handler.changeState(new MUnassociated(state_handler));
		}else if (msg.isRoiv_cmip_confirmed_actionSelected()) {
			// The agent only sends event report messages. This should never happen
			state_handler.send(MessageFactory.ROER_NO_SUCH_ACTION_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
			state_handler.changeState(new MUnassociated(state_handler));
		}else if (msg.isRors_cmip_confirmed_event_reportSelected()){
			//TODO:
			System.out.println(">> TODO: Rors_cmip_confirmed_event_report");
		}else if (msg.isRors_cmip_getSelected()){
			//TODO:
			System.out.println(">> TODO: Rors_cmip_get");
		}else if (msg.isRors_cmip_confirmed_setSelected()){
			//TODO:
			System.out.println(">> TODO: Rors_cmip_confirmed_set");
		}else if (msg.isRors_cmip_confirmed_actionSelected()){
			//TODO:
			System.out.println(">> TODO: Rors_cmip_confirmed_action");
		}else if (msg.isRoerSelected()){
			//TODO:
			System.out.println(">> TODO: Roer");
		}else if (msg.isRorjSelected()){
			//TODO:
			System.out.println(">> TODO: Rorj");
		}
	}

	private void roiv_cmip_confirmed_event_repor(DataApdu data){
		try {
			//(A.10.3 EVENT REPORT service)
			EventReportArgumentSimple event = data.getMessage().getRoiv_cmip_confirmed_event_report();
			if (event.getObj_handle().getValue().getValue() == 0){
				//obj-handle is 0 to represent the MDS
				process_MDS_Object_Event(data);
			}else{
				//TODO: handle representing a scanner or PM-store object.
				System.err.println("Warning: Received Handle=" + event.getObj_handle().getValue().getValue() + " in CheckingConfig state. Ignore.");
			}
		}catch (Exception e){
			//TODO: Send Response Error
			e.printStackTrace();
			System.err.println("TODO: Send Response Error");
		}
	}

	private void process_MDS_Object_Event(DataApdu data) throws Exception{
		// The agent is sending measurements before a configuration is agreed to.
		MessageChoiceType msg = data.getMessage();
		EventReportArgumentSimple event = msg.getRoiv_cmip_confirmed_event_report();
		if (Nomenclature.MDC_NOTI_CONFIG == event.getEvent_type().getValue().getValue().intValue()){
			// If config report, then Tx abrt (page 148), not state transition are
			// indicated in Manager state table, but in page 71 (8.8.5 Error Conditions)
			// says: "if agents or manager receives or send an Association Abort message at
			// any time, it shall transition to the Unassociated state."
			state_handler.send(MessageFactory.AbrtApdu_UNDEFINED());
			state_handler.changeState(new MUnassociated(state_handler));
		} else {
			// If not a config report, then Tx roer (no-such-object-instance)
			state_handler.send(MessageFactory.ROER_NO_SUCH_OBJECT_INSTANCE_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
		}
	}

	private ConfigReportRsp getConfigResponse (EventReportArgumentSimple event) throws Exception{
		ConfigReport config = ASN1_Tools.decodeData(
				event.getEvent_info(),
				ConfigReport.class,
				state_handler.getMDS().getDeviceConf().getEncondigRules());
		//Send event to MDS class
		return state_handler.getMDS().MDS_Configuration_Event(config);
	}

	private void checkingConfig (DataApdu data, ConfigReportRsp response) throws Exception{
		state_handler.send(composeResponse(data,response));
		//Set next state
		if (response.getConfig_result().getValue() == ASN1_Values.CONF_RESULT_ACCEPTED_CONFIG)
			state_handler.changeState(new MOperating(state_handler));
		else state_handler.changeState(new WaitingForConfig(state_handler));
	}

	private ApduType composeResponse (DataApdu data, ConfigReportRsp response) throws Exception {
		DataApdu confRsp = new DataApdu();
		MessageChoiceType msgRsp = new MessageChoiceType();
		EventReportResultSimple eventRRS = new EventReportResultSimple();

		HANDLE mdsHandle = (HANDLE)state_handler.getMDS().getAttribute(Nomenclature.MDC_ATTR_ID_HANDLE).getAttributeType();
		//Handle of the MDS object
		eventRRS.setObj_handle(mdsHandle);
		//TODO: See this issue (Default set event time to 0)
		RelativeTime rt = new RelativeTime();
		rt.setValue(new INT_U32(0L));
		eventRRS.setCurrentTime(rt);
		//The event-type of the result shall be a copy of the event-type from the invocation.
		eventRRS.setEvent_type(data.getMessage().getRoiv_cmip_confirmed_event_report().getEvent_type());

		ByteArrayOutputStream output= new ByteArrayOutputStream();
		//Parse data using negotiated encoding rules
		IEncoder<ConfigReportRsp> encoderConfRsp;
		encoderConfRsp = CoderFactory.getInstance().newEncoder(
					state_handler.getMDS().getDeviceConf().getEncondigRules());
		encoderConfRsp.encode(response, output);
		eventRRS.setEvent_reply_info(output.toByteArray());

		msgRsp.selectRors_cmip_confirmed_event_report(eventRRS);

		confRsp.setInvoke_id(data.getInvoke_id());
		confRsp.setMessage(msgRsp);
		return MessageFactory.composeApdu (confRsp, state_handler.getMDS().getDeviceConf());
	}

}
