/*
Copyright (C) 2008-2009  Santiago Carot Nemesio
email: scarot@libresoft.es
Copyright (C) 2008-2009  José Antonio Santos Cadenas
email: jcaden@libresoft.es

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

package middleHealth.ieee_11073.part_20601.fsm.manager;

import java.text.SimpleDateFormat;
import java.util.Date;

import middleHealth.ieee_11073.part_10101.Nomenclature;
import middleHealth.ieee_11073.part_20601.asn1.ApduType;
import middleHealth.ieee_11073.part_20601.asn1.DataApdu;
import middleHealth.ieee_11073.part_20601.asn1.EventReportArgumentSimple;
import middleHealth.ieee_11073.part_20601.asn1.PrstApdu;
import middleHealth.ieee_11073.part_20601.asn1.RelativeTime;
import middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoFixed;
import middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoMPFixed;
import middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoMPVar;
import middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoVar;
import middleHealth.ieee_11073.part_20601.asn1.SegmentDataEvent;
import middleHealth.ieee_11073.part_20601.asn1.DataApdu.MessageChoiceType;
import middleHealth.ieee_11073.part_20601.fsm.Operating;
import middleHealth.ieee_11073.part_20601.fsm.StateHandler;
import middleHealth.ieee_11073.part_20601.phd.dim.DIM;
import middleHealth.ieee_11073.part_20601.phd.dim.EpiCfgScanner;
import middleHealth.ieee_11073.part_20601.phd.dim.PM_Store;
import middleHealth.ieee_11073.part_20601.phd.dim.DimTimeOut;
import middleHealth.ieee_11073.part_20601.phd.dim.PeriCfgScanner;
import middleHealth.ieee_11073.part_20601.phd.dim.SET_Service;
import middleHealth.es.libresoft.openhealth.events.Event;
import middleHealth.es.libresoft.openhealth.events.EventType;
import middleHealth.es.libresoft.openhealth.events.application.ExternalEvent;
import middleHealth.es.libresoft.openhealth.events.application.GetPmStoreEvent;
import middleHealth.es.libresoft.openhealth.events.application.SetEvent;
import middleHealth.es.libresoft.openhealth.messages.MessageFactory;
import middleHealth.es.libresoft.openhealth.utils.ASN1_Tools;
import middleHealth.es.libresoft.openhealth.utils.ASN1_Values;
import middleHealth.ieee_11073.part_20601.phd.dim.manager.*;

public final class MOperating extends Operating {

	// Next interface is used to process the received PrstApdu depending of
	// the device configuration is 20601 or external

	private interface ProcessHandler {
		public void processPrstApdu(PrstApdu prst);
	};

	private ProcessHandler process;

	public MOperating(StateHandler handler) {
		super(handler);
		System.out.println("attempting to get MDS");
		state_handler.getMDS().GET();
		System.out.println("get MDS success");
		System.out.println("attempting to set the time");
		Date d = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-HH-mm-ss");
		String date = sdf.format(d);
		System.out.println("DATE: " + date);
		String[] parsedDate = date.split("-");
		state_handler.getMDS().Set_Time(20, 15, Integer.parseInt(parsedDate[0]), Integer.parseInt(parsedDate[1]), Integer.parseInt(parsedDate[2]), Integer.parseInt(parsedDate[3]), Integer.parseInt(parsedDate[4]), 0);
		System.out.println("time set SUCCESS");
		int data_proto_id = state_handler.getMDS().getDeviceConf().getDataProtoId();
		if (data_proto_id== ASN1_Values.DATA_PROTO_ID_20601){
			process = new ProcessHandler(){
				@Override
				public void processPrstApdu(PrstApdu prst) {
					process_20601_PrstApdu(prst);
				}
			};
		}else{
			//TODO: Add here support for data-proto-id-external...
			System.err.println("Data_Proto_id: " + data_proto_id +" is not supported");
		}
	}

	@Override
	public synchronized void process(ApduType apdu) {
		if (apdu.isPrstSelected()){
			process.processPrstApdu(apdu.getPrst());
		}else if (apdu.isRlrqSelected()) {
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
		if (event instanceof ExternalEvent)
			return processExternalEvent((ExternalEvent)event);

		if (event.getTypeOfEvent() == EventType.IND_TRANS_DESC) {
			System.err.println("2.2) IND Transport disconnect. Should indicate to application layer...");
			state_handler.changeState(new MDisconnected(state_handler));
		}else if (event.getTypeOfEvent() == EventType.IND_TIMEOUT) {
			state_handler.send(MessageFactory.AbrtApdu(event.getReason()));
			state_handler.changeState(new MUnassociated(state_handler));
		}else if (event.getTypeOfEvent() == EventType.REQ_ASSOC_REL){
			state_handler.send(MessageFactory.RlrqApdu_NORMAL());
			state_handler.changeState(new MDisassociating(state_handler));
		}else if (event.getTypeOfEvent() == EventType.REQ_ASSOC_ABORT){
			state_handler.send(MessageFactory.AbrtApdu_UNDEFINED());
			state_handler.changeState(new MUnassociated(state_handler));
		}else
			return false;

		return true;
	}

	//----------------------------------PRIVATE--------------------------------------------------------

	private boolean processExternalEvent(ExternalEvent event) {
		switch (event.getTypeOfEvent()) {
		case EventType.REQ_GET_PM_STORE:
			GetPmStoreEvent pmEvent = (GetPmStoreEvent) event;
			PM_Store pm_store = this.state_handler.getMDS().getPM_Store(pmEvent.getHandle());
			pm_store.GET();
			return true;
		case EventType.REQ_SET:
			SetEvent setEvent = (SetEvent) event;
			DIM obj = state_handler.getMDS().getObject(setEvent.getObjectHandle());
			try {
				SET_Service serv = (SET_Service) obj;
				serv.SET(setEvent.getAttribute());
			} catch (ClassCastException e) {
				System.err.println("Set cannot be done in object: " + setEvent.getObjectHandle().getValue().getValue() +
							" it does not implement a SET service");
			}
			return true;
		}

		return false;
	}

	private void process_20601_PrstApdu(PrstApdu prst){
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
		if (msg.isRoiv_cmip_event_reportSelected()) {
			//TODO:
			System.out.println(">> Roiv_cmip_event_report");
		}else if (msg.isRoiv_cmip_confirmed_event_reportSelected()) {
			System.out.println(">> Roiv_cmip_confirmed_event_report");
			roiv_cmip_confirmed_event_report(data);
		}else if (msg.isRoiv_cmip_getSelected()) {
			//TODO:
			System.out.println(">> Roiv_cmip_get");
		}else if (msg.isRoiv_cmip_setSelected()) {
			//TODO:
			System.out.println(">> Roiv_cmip_set");
		}else if (msg.isRoiv_cmip_confirmed_setSelected()) {
			//TODO:
			System.out.println(">> Roiv_cmip_confirmed_set");
		}else if (msg.isRoiv_cmip_actionSelected()){
			//TODO:
			System.out.println(">> Roiv_cmip_action");
		}else if (msg.isRoiv_cmip_confirmed_actionSelected()) {
			//TODO:
			System.out.println(">> Roiv_cmip_confirmed_action");
		}else if (msg.isRors_cmip_confirmed_event_reportSelected()){
			//TODO:
			System.out.println(">> Rors_cmip_confirmed_event_report");
		}else if (msg.isRors_cmip_getSelected()){
			//TODO:
			System.out.println(">> Rors_cmip_get");
		}else if (msg.isRors_cmip_confirmed_setSelected()){
			//TODO:
			System.out.println(">> Rors_cmip_confirmed_set");
		}else if (msg.isRors_cmip_confirmed_actionSelected()){
			//TODO:
			System.out.println(">> Rors_cmip_confirmed_action");
		}else if (msg.isRoerSelected()){
			//TODO:
			System.out.println(">> Roer");
		}else if (msg.isRorjSelected()){
			//TODO:
			System.out.println(">> Rorj");
		}

		DimTimeOut to = state_handler.retireTimeout(data.getInvoke_id().getValue());
		if (to != null)
			to.procResponse(data);

		//TODO search in the timeouts if is there one for this event
	}

	private void processSegmentDataEvent(EventReportArgumentSimple event) {
		PM_Store pmstore = this.state_handler.getMDS().getPM_Store(event.getObj_handle());
		if (pmstore == null)
			return;

		RelativeTime rt = event.getEvent_time();
		System.out.println("Relative Time: " + rt.getValue().getValue().intValue());

		try {
			SegmentDataEvent sde = ASN1_Tools.decodeData(event.getEvent_info(),
									SegmentDataEvent.class,
									this.state_handler.getMDS().getDeviceConf().getEncondigRules());
			pmstore.Segment_Data_Event(sde);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void processBufScannerEvent(EventReportArgumentSimple event) {
		try {
			PeriCfgScanner scanner = (PeriCfgScanner) state_handler.getMDS().getScanner(event.getObj_handle());
			switch (event.getEvent_type().getValue().getValue()) {
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_VAR:
				scanner.Buf_Scan_Report_Var();
				break;
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_FIXED:
				scanner.Buf_Scan_Report_Fixed();
				break;
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_GROUPED:
				scanner.Buf_Scan_Report_Grouped();
				break;
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_MP_VAR:
				scanner.Buf_Scan_Report_MP_Var();
				break;
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_MP_FIXED:
				scanner.Buf_Scan_Report_MP_Fixed();
				break;
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_MP_GROUPED:
				scanner.Buf_Scan_Report_MP_Grouped();
				break;
			}
		} catch(ClassCastException e) {
			System.err.println("Only Periodic Scanners can receive Buffered scanner events");
		}
	}

	private void processUnbufScannerEvent(EventReportArgumentSimple event) {
		try {
			EpiCfgScanner scanner = (EpiCfgScanner) state_handler.getMDS().getScanner(event.getObj_handle());
			switch (event.getEvent_type().getValue().getValue()) {
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_VAR:
				scanner.Unbuf_Scan_Report_Var();
				break;
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_FIXED:
				scanner.Unbuf_Scan_Report_Fixed();
				break;
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_GROUPED:
				scanner.Unbuf_Scan_Report_Grouped();
				break;
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_MP_VAR:
				scanner.Unbuf_Scan_Report_MP_Var();
				break;
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_MP_FIXED:
				scanner.Unbuf_Scan_Report_MP_Fixed();
				break;
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_MP_GROUPED:
				scanner.Unbuf_Scan_Report_MP_Grouped();
				break;
			}
		} catch(ClassCastException e) {
			System.err.println("Only Episodic Scanner can receive ");
		}
	}

	private void roiv_cmip_confirmed_event_report(DataApdu data) { //EventReportArgumentSimple event, MessageChoiceType msg){
		EventReportArgumentSimple event = data.getMessage().getRoiv_cmip_confirmed_event_report();
		//(A.10.3 EVENT REPORT service)
		if (event.getObj_handle().getValue().getValue().intValue() == 0){
			//obj-handle is 0 to represent the MDS
			process_MDS_Object_Event(event);
			this.state_handler.send(MessageFactory.PrstTypeResponse(data, state_handler.getMDS().getDeviceConf()));
		} else {
			switch (event.getEvent_type().getValue().getValue().intValue()) {
			case Nomenclature.MDC_NOTI_SEGMENT_DATA:
				processSegmentDataEvent(event);
				break;
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_VAR:
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_FIXED:
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_GROUPED:
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_MP_VAR:
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_MP_FIXED:
			case Nomenclature.MDC_NOTI_UNBUF_SCAN_REPORT_MP_GROUPED:
				processUnbufScannerEvent(event);
				break;
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_VAR:
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_FIXED:
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_GROUPED:
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_MP_VAR:
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_MP_FIXED:
			case Nomenclature.MDC_NOTI_BUF_SCAN_REPORT_MP_GROUPED:
				processBufScannerEvent(event);
				break;
			default:
				//TODO: handle representing a scanner or PM-store object.
				System.err.println("Warning: Received Handle=" + event.getObj_handle().getValue().getValue() + " in Operating state. Ignore.");
			}
		}
	}

	private void process_MDS_Object_Event(EventReportArgumentSimple event){
		switch (event.getEvent_type().getValue().getValue().intValue()){
			case Nomenclature.MDC_NOTI_CONFIG:
				//TODO:
				System.out.println("MDC_NOTI_CONFIG");
				break;
			case Nomenclature.MDC_NOTI_SCAN_REPORT_VAR:
				mdc_noti_scan_report_var(event.getEvent_info());
				break;
			case Nomenclature.MDC_NOTI_SCAN_REPORT_FIXED:
				mdc_noti_scan_report_fixed(event.getEvent_info());
				break;
			case Nomenclature.MDC_NOTI_SCAN_REPORT_MP_VAR:
				//TODO: work in progress
				mdc_noti_scan_report_mp_var(event.getEvent_info());
				System.out.println("MDC_NOTI_SCAN_REPORT_MP_VAR");
				break;
			case Nomenclature.MDC_NOTI_SCAN_REPORT_MP_FIXED:
				//TODO: work in progress
				System.out.println("MDC_NOTI_SCAN_REPORT_MP_FIXED!!11");
				mdc_noti_scan_report_mp_fixed(event.getEvent_info());
				System.out.println("MDC_NOTI_SCAN_REPORT_MP_FIXED!!22");
				break;
		}
	}

	private void mdc_noti_scan_report_mp_fixed(byte[] einfo){
		try{
			ScanReportInfoMPFixed srimpf = ASN1_Tools.decodeData(einfo, ScanReportInfoMPFixed.class, 
					this.state_handler.getMDS().getDeviceConf().getEncondigRules());
			System.out.println("has next " + srimpf.getScan_per_fixed().iterator().hasNext());
			System.out.println(this.state_handler.getMDS().getDeviceConf().getEncondigRules());
			((MDSManager)this.state_handler.getMDS()).MDS_Dynamic_Data_Update_MP_Fixed(srimpf);
			System.out.println("function was carried out");
		}catch(Exception e){
			System.out.println("Error decoding mdc_noti_scan_report_mp_fixed");
		}
	}
	private void mdc_noti_scan_report_mp_var(byte[] einfo){
		try {
			ScanReportInfoMPVar srimpv = ASN1_Tools.decodeData(einfo, ScanReportInfoMPVar.class, 
					this.state_handler.getMDS().getDeviceConf().getEncondigRules());
			
			this.state_handler.getMDS().MDS_Dynamic_Data_Update_MP_Var(srimpv);
		} catch (Exception e) {
			System.out.println("Error decoding mdc_noti_scan_report_mp_var");
		}
		
	}
	private void mdc_noti_scan_report_fixed (byte[] einfo){
		try {
			
			ScanReportInfoFixed srif = ASN1_Tools.decodeData(einfo,
					ScanReportInfoFixed.class,
					this.state_handler.getMDS().getDeviceConf().getEncondigRules());
			this.state_handler.getMDS().MDS_Dynamic_Data_Update_Fixed(srif);
		} catch (Exception e) {
			System.out.println("Error decoding mdc_noti_scan_report_fixed");
			e.printStackTrace();
		}

	}

	private void mdc_noti_scan_report_var(byte[] einfo) {
		try {
			ScanReportInfoVar sriv = ASN1_Tools.decodeData(einfo,
					ScanReportInfoVar.class,
					this.state_handler.getMDS().getDeviceConf().getEncondigRules());
			this.state_handler.getMDS().MDS_Dynamic_Data_Update_Var(sriv);
		} catch (Exception e) {
			System.out.println("Error decoding mdc_noti_scan_report_var");
			e.printStackTrace();
		}

	}
}
