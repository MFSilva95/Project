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
package com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.fsm.manager;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.Event;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.EventType;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.messages.MessageFactory;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Tools;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Values;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ApduType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataApdu.MessageChoiceType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.EventReportArgumentSimple;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.PrstApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.fsm.Configuring;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.fsm.StateHandler;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.TimeOut;

import java.util.concurrent.Semaphore;

public final class WaitingForConfig extends Configuring {

	private TimeOut timeOut;

	private Semaphore abortMutex = new Semaphore(1); //Control concurrent access to the boolean variable "evaluateTimeout"
	boolean evaluateTimeout = true;

	public WaitingForConfig (StateHandler handler) {
		super(handler);
		resetTimerTask();
	}

	@Override
	public synchronized String getStateName() {
		return "WaitingForConfig";
	}

	@Override
	public synchronized void process(ApduType apdu) {
		if (apdu.isPrstSelected()){
			process_PrstApdu(apdu.getPrst());
		}else if (apdu.isRlrqSelected()) {
			//The manager has received a request to release the association
			timeOut.cancel();
			state_handler.send(MessageFactory.RlreApdu_NORMAL());
			state_handler.changeState(new MUnassociated(state_handler));
		}else if(apdu.isAarqSelected() || apdu.isAareSelected() || apdu.isRlreSelected()){
			timeOut.cancel();
			state_handler.send(MessageFactory.AbrtApdu_UNDEFINED());
			state_handler.changeState(new MUnassociated(state_handler));
		}else if(apdu.isAbrtSelected()){
			timeOut.cancel();
			state_handler.changeState(new MUnassociated(state_handler));
		}
	}

	@Override
	public synchronized boolean processEvent(Event event) {
		if (event.getTypeOfEvent() == EventType.IND_TRANS_DESC) {
			timeOut.cancel();
			System.err.println("2.2) IND Transport disconnect. Should indicate to application layer...");
			state_handler.changeState(new MDisconnected(state_handler));
		}else if (event.getTypeOfEvent() == EventType.IND_TIMEOUT) {
			state_handler.send(MessageFactory.AbrtApdu_CONFIGURATION_TIMEOUT());
			state_handler.changeState(new MUnassociated(state_handler));
		}else if (event.getTypeOfEvent() == EventType.REQ_ASSOC_REL){
			resetTimerTask();
			state_handler.send(MessageFactory.RlrqApdu_NORMAL());
			state_handler.changeState(new MDisassociating(state_handler));
		}else if (event.getTypeOfEvent() == EventType.REQ_ASSOC_ABORT){
			timeOut.cancel();
			state_handler.send(MessageFactory.AbrtApdu_UNDEFINED());
			state_handler.changeState(new MUnassociated(state_handler));
		}else
			return false;
		return true;
	}

//----------------------------------PRIVATE--------------------------------------------------------
	private void resetTimerTask () {
		if (timeOut != null)
			timeOut.cancel();
		timeOut = new TimeOut(TimeOut.TO_CONFIG, state_handler) {
			protected void expiredTimeout() {
				System.out.println("Timeout task running");
				try {
					abortMutex.acquire();
					if (evaluateTimeout) {
						Event event = new Event(EventType.IND_TIMEOUT);
						event.setReason(ASN1_Values.ABRT_RE_CONFIGURATION_TIMEOUT);
						evaluateTimeout = false;
						//Send timeout event to the eventQueue
						state_handler.sendEvent(event);
					}
					abortMutex.release();
				} catch (InterruptedException e) {
					//Thread is interrupted when was waiting for to gain access to the mutex context,
					//This situation appears when main project cancel this task when it is processing
					//an incoming report apdu response arrived from agent near to the timeout slot.
					;
				}
			}
		};
		timeOut.start();
	}


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
			//Not allowed
			state_handler.send(MessageFactory.ROER_NO_SUCH_OBJECT_INSTANCE_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
		}else if (msg.isRoiv_cmip_getSelected()) {
			//Not allowed
			state_handler.send(MessageFactory.ROER_NO_SUCH_OBJECT_INSTANCE_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
		}else if (msg.isRoiv_cmip_setSelected()) {
			//Not allowed
			state_handler.send(MessageFactory.ROER_NO_SUCH_OBJECT_INSTANCE_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
		}else if (msg.isRoiv_cmip_confirmed_setSelected()) {
			//Not allowed
			state_handler.send(MessageFactory.ROER_NO_SUCH_OBJECT_INSTANCE_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
		}else if (msg.isRoiv_cmip_actionSelected()){
			//Not allowed
			state_handler.send(MessageFactory.ROER_NO_SUCH_OBJECT_INSTANCE_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
		}else if (msg.isRoiv_cmip_confirmed_actionSelected()) {
			//Not allowed
			state_handler.send(MessageFactory.ROER_NO_SUCH_OBJECT_INSTANCE_Apdu(data,
					state_handler.getMDS().getDeviceConf()));
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
				System.err.println("Warning: Received Handle=" + event.getObj_handle().getValue().getValue() + " in WaitingForConfig state. Ignore.");
			}
		}catch (Exception e){
			//TODO: Send Response Error
			e.printStackTrace();
			System.err.println("TODO: Send Response Error");
		}
	}

	private void process_MDS_Object_Event(DataApdu data) throws Exception{
		MessageChoiceType msg = data.getMessage();
		EventReportArgumentSimple event = msg.getRoiv_cmip_confirmed_event_report();
		switch (event.getEvent_type().getValue().getValue().intValue()){
			case Nomenclature.MDC_NOTI_CONFIG:
				receivedConfigurationFromAgent (data);
				break;
			case Nomenclature.MDC_NOTI_SCAN_REPORT_VAR:
				//TODO:
				System.err.println("Warning: Received MDC_NOTI_SCAN_REPORT_VAR");
				break;
			case Nomenclature.MDC_NOTI_SCAN_REPORT_FIXED:
				//TODO:
				System.err.println("Warning: Received MDC_NOTI_SCAN_REPORT_FIXED");
				break;
			case Nomenclature.MDC_NOTI_SCAN_REPORT_MP_VAR:
				//TODO:
				System.err.println("Warning: Received MDC_NOTI_SCAN_REPORT_MP_VAR");
				break;
			case Nomenclature.MDC_NOTI_SCAN_REPORT_MP_FIXED:
				//TODO:
				System.err.println("Warning: Received MDC_NOTI_SCAN_REPORT_MP_FIXED");
				break;
		}

	}

	private void receivedConfigurationFromAgent (DataApdu data){
		try {
			abortMutex.acquire();
			if (evaluateTimeout){
				evaluateTimeout=false;
				//Release mutex
				abortMutex.release();
				CheckingConfig checking = new CheckingConfig(state_handler);
				//Elegant way to change of state without receive an APDU. Only providing
				//a previous processed apdu from another state to the new state
				checking.checkNotiConfig(data);
				//Cancel timeout if it did not do itself yet
				timeOut.cancel();
			}else {
				//Timeout has produced, ignore this apdu
				abortMutex.release();
			}

		} catch (InterruptedException e) {;}
	}
}
