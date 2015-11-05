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
package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.messages;

import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AVA_Type;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AareApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.Abort_reason;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AbrtApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AbsoluteTime;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ActionArgumentSimple;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ApduType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AssociateResult;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AttributeIdList;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AttributeList;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AttributeModEntry;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ConfigId;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataProto;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataProtoId;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataReqModeCapab;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataReqModeFlags;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.EncodingRules;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ErrorResult;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.EventReportResultSimple;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.FLOAT_Type;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.FunctionalUnits;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.GetArgumentSimple;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.HANDLE;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U16;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U32;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U8;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.InvokeIDType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ModificationList;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ModifyOperator;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.NomenclatureVersion;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.OID_Type;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.PhdAssociationInformation;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ProtocolVersion;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.PrstApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.RelativeTime;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ReleaseRequestReason;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ReleaseResponseReason;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.RlreApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.RlrqApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.RoerErrorValue;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.SegmSelection;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.SetArgumentSimple;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.SetTimeInvoke;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.SystemType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.TrigSegmDataXferReq;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.Attribute;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.DIM;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.MDS;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.PM_Store;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.jadg.mydiabetes.middleHealth.org.bn.CoderFactory;
import com.jadg.mydiabetes.middleHealth.org.bn.IEncoder;
import com.jadg.mydiabetes.middleHealth.org.bn.types.BitString;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Device11073;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.DeviceConfig;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.ManagerConfig;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Tools;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Values;

public class MessageFactory {

	// RlrqApdu
	public static final ApduType RlreApdu_NORMAL() {
		return RlreApdu(ASN1_Values.REL_RES_RE_NORMAL);
	}

	private static final ApduType RlreApdu (int reason) {
		ApduType apdu = new ApduType();
		RlreApdu rlre = new RlreApdu();
		rlre.setReason(new ReleaseResponseReason(new Integer(reason)));
		apdu.selectRlre(rlre);
		return apdu;
	}

	// AarqApdu
	public static final ApduType AareApdu_20601_ACCEPTED(DeviceConfig dev_conf){
		return AareApdu_20601(ASN1_Values.AR_ACCEPTED, dev_conf);
	}

	public static final ApduType AareApdu_20601_ACCEPTED_UNKNOWN_CONFIG(DeviceConfig dev_conf){
		return AareApdu_20601(ASN1_Values.AR_ACCEPTED_UNKNOWN_CONFIG, dev_conf);
	}

	private static final ApduType AareApdu_20601(int assoc_result, DeviceConfig dev_conf){
		ApduType apdu = new ApduType();
		AareApdu aare = new AareApdu();
		AssociateResult ar = new AssociateResult(assoc_result);
		DataProto dp = new DataProto();
		DataProtoId dpi= new DataProtoId(ASN1_Values.DATA_PROTO_ID_20601);

		PhdAssociationInformation phd = generatePhdAssociationInformation(dev_conf);
		byte[] any = {};

		try {
			//MDER encoding rules shall always apply to the structures in A.8.
			any = ASN1_Tools.encodeData(phd, Device11073.MDER_DEFUALT);
		} catch (Exception e) { //Never Thrown
			e.printStackTrace();
		}

		dp.setData_proto_id(dpi);
		dp.setData_proto_info(any);
		aare.setResult(ar);
		aare.setSelected_data_proto(dp);
		apdu.selectAare(aare);

		return apdu;
	}

	private static final PhdAssociationInformation generatePhdAssociationInformation(DeviceConfig dev_conf){
		PhdAssociationInformation pai = new PhdAssociationInformation();
		ProtocolVersion pv = new ProtocolVersion();
		pv.setValue(new BitString (ManagerConfig.protocol_version));
		EncodingRules er = new EncodingRules(
				new BitString(dev_conf.getEncondigRulesToArray()));
		NomenclatureVersion nv = new NomenclatureVersion(
				new BitString(ManagerConfig.nomenclature_version));
		byte[] byteArray = {(byte)0, (byte)0, (byte)0, (byte)0};
		FunctionalUnits fn = new FunctionalUnits(new BitString(byteArray));
		SystemType st = new SystemType(
				new BitString(ManagerConfig.syste_type));
		ConfigId cid = new ConfigId(ASN1_Values.CONF_ID_MANAGER_CONFIG_RESPONSE);

		/* DataReqModeCapab should be zero in the response */
		DataReqModeCapab drmc = new DataReqModeCapab();
		DataReqModeFlags drmf = new DataReqModeFlags();
		byte[] drmfb = {(byte)0, (byte)0};
		drmf.setValue(new BitString(drmfb));
		drmc.setData_req_mode_flags(drmf);
		drmc.setData_req_init_agent_count(0);
		drmc.setData_req_init_manager_count(0);
		AttributeList al = new AttributeList();
		al.initValue();
		pai.setProtocol_version(pv);
		pai.setEncoding_rules(er);
		pai.setNomenclature_version(nv);
		pai.setFunctional_units(fn);
		pai.setSystem_type(st);
		pai.setSystem_id(ManagerConfig.Manager_Id);
		pai.setDev_config_id(cid);
		pai.setData_req_mode_capab(drmc);
		pai.setOption_list(al);

		return pai;
	}


	//ReleaseRequest Apdu
	public static final ApduType RlrqApdu_NORMAL() {
		return RlrqApdu(ASN1_Values.REL_REQ_RE_NORMAL);
	}

	public static final ApduType RlrqApdu_NO_MORE_CONFIGURATIONS() {
		return RlrqApdu(ASN1_Values.REL_REQ_RE_NO_MORE_CONFIGURATIONS);
	}

	public static final ApduType RlrqApdu_CONFIGURATION_CHANGED() {
		return RlrqApdu(ASN1_Values.REL_REQ_RE_CONFIGURATION_CHANGED);
	}

	private static final ApduType RlrqApdu (int reason){
		ApduType apdu = new ApduType();
		RlrqApdu rlrq = new RlrqApdu();
		rlrq.setReason(new ReleaseRequestReason(
				new Integer(reason)));
		apdu.selectRlrq(rlrq);
		return apdu;
	}

	//Rejected Apdu
	public static final ApduType AareRejectApdu_PERMANENT(){
		return AareRejectApdu(ASN1_Values.AR_REJECTED_PERMANENT);
	}

	public static final ApduType AareRejectApdu_TRANSIENT(){
		return AareRejectApdu(ASN1_Values.AR_REJECTED_TRANSIENT);
	}

	public static final ApduType AareRejectApdu_NO_COMMON_PROTOCOL(){
		return AareRejectApdu(ASN1_Values.AR_REJECTED_NO_COMMON_PROTOCOL);
	}

	public static final ApduType AareRejectApdu_NO_COMMON_PARAMETER(){
		return AareRejectApdu(ASN1_Values.AR_REJECTED_NO_COMMON_PARAMETER);
	}

	public static final ApduType AareRejectApdu_UNKNOWN(){
		return AareRejectApdu(ASN1_Values.AR_REJECTED_UNKNOWN);
	}

	public static final ApduType AareRejectApdu_UNAUTHORIZED(){
		return AareRejectApdu(ASN1_Values.AR_REJECTED_UNAUTHORIZED);
	}

	public static final ApduType AareRejectApdu_UNSUPPORTED_ASSOC_VERSION(){
		return AareRejectApdu(ASN1_Values.AR_REJECTED_UNSUPPORTED_ASSOC_VERSION);
	}

	private static final ApduType AareRejectApdu(int assoc_result) {
		//Create AARE response
		byte[] empty = {};
		ApduType apdu = new ApduType();
		AareApdu aare = new AareApdu();
		AssociateResult ar = new AssociateResult(assoc_result); /* assoc_result */
		DataProto dp = new DataProto();
		DataProtoId dpi= new DataProtoId(ASN1_Values.DATA_PROTO_ID_EMPTY); /* rejected */
		dp.setData_proto_id(dpi);
		dp.setData_proto_info(empty);
		aare.setResult(ar);
		aare.setSelected_data_proto(dp);
		apdu.selectAare(aare);
		return apdu;
	}

	/* Abort APDUs */
	public static final ApduType AbrtApdu(int reason) {
		switch (reason) {
		case ASN1_Values.ABRT_RE_BUFFER_OVERFLOW:
			return AbrtApdu_UNDEFINED();
		case ASN1_Values.ABRT_RE_RESPONSE_TIMEOUT:
			return AbrtApdu_RESPONSE_TIMEOUT();
		case ASN1_Values.ABRT_RE_CONFIGURATION_TIMEOUT:
			return AbrtApdu_CONFIGURATION_TIMEOUT();
		default:
			return AbrtApdu_UNDEFINED();
		}
	}
	public static final ApduType AbrtApdu_UNDEFINED() {
		return createAbrtApdu(ASN1_Values.ABRT_RE_UNDEFINED);
	}

	public static final ApduType AbrtApdu_BUFFER_OVERFLOW() {
		return createAbrtApdu(ASN1_Values.ABRT_RE_BUFFER_OVERFLOW);
	}

	public static final ApduType AbrtApdu_RESPONSE_TIMEOUT() {
		return createAbrtApdu(ASN1_Values.ABRT_RE_RESPONSE_TIMEOUT);
	}

	public static final ApduType AbrtApdu_CONFIGURATION_TIMEOUT() {
		return createAbrtApdu(ASN1_Values.ABRT_RE_CONFIGURATION_TIMEOUT);
	}

	private static final ApduType createAbrtApdu(int reason){
		ApduType apdu = new ApduType();
		AbrtApdu abrt = new AbrtApdu();
		Abort_reason abrt_reason = new Abort_reason(ASN1_Values.ABRT_RE_UNDEFINED);
		abrt.setReason(abrt_reason);
		apdu.selectAbrt(abrt);
		return apdu;
	}

	private static final int getChannelPreference (DataApdu da) {
		if (da.getMessage().isRoiv_cmip_event_reportSelected() ||
				da.getMessage().isRoiv_cmip_setSelected() ||
				da.getMessage().isRoiv_cmip_actionSelected() ||
				da.getMessage().isRoerSelected() ||
				da.getMessage().isRorjSelected())
			/* Not primary channel preference */
			return -1;
		else
			/* Primary channel preference */
			return 0;
	}

	//Confirmed measurement data transmission
	public static final ApduType PrstTypeResponse (DataApdu receivedData, DeviceConfig dev_conf) {
		//Create PRST response
		ApduType at = new ApduType();
		PrstApdu pa = new PrstApdu();

		//Get response from data
		DataApdu da = generateDataApdu(receivedData, dev_conf);
		at.setChannel(getChannelPreference(da));

		ByteArrayOutputStream output= new ByteArrayOutputStream();
		//Parse data using negotiated encoding rules
		IEncoder<DataApdu> encoderDataApdu;
		try {
			encoderDataApdu = CoderFactory.getInstance().newEncoder(dev_conf.getEncondigRules());
			encoderDataApdu.encode(da, output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pa.setValue(output.toByteArray());
		at.selectPrst(pa);
		return at;
	}

	private static final DataApdu generateDataApdu (DataApdu da, DeviceConfig dev_conf){

		DataApdu data = new DataApdu();
		InvokeIDType iit = new InvokeIDType(da.getInvoke_id().getValue()); //Mirrored from invocation
		DataApdu.MessageChoiceType msg = getMessageResponse(da);
		System.out.println("Invoke id type: " + iit.getValue());
		data.setInvoke_id(iit);
		data.setMessage(msg);
		return data;
	}

	private static final DataApdu.MessageChoiceType getMessageResponse (DataApdu da){
		DataApdu.MessageChoiceType msg = da.getMessage();
		//Process the message received
		if (msg.isRoiv_cmip_event_reportSelected()) {
			//TODO:
			System.out.println("(>> Roiv_cmip_event_report) FAIL: Manager Shall not confirm cmip_event report");
			//return rors_cmip_event_repor(da);
		}else if (msg.isRoiv_cmip_confirmed_event_reportSelected()) {
			return rors_cmip_confirmed_event_repor(da);
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
		return null;
	}
	/*
	private static final DataApdu.MessageChoiceType rors_cmip_event_repor (DataApdu da){
		DataApdu.MessageChoiceType msg = new DataApdu.MessageChoiceType();
		EventReportResultSimple errs = new EventReportResultSimple();

		HANDLE h = new HANDLE();
		h.setValue(new INT_U16(0)); //The MDS object

		if (da.getMessage().getRoiv_cmip_event_report().getEvent_time().getValue().getValue() != 0x00FFFFFFFFL)
			System.err.println("Warning: Agent supports Relative time. Response sent is not valid");

		RelativeTime rt = new RelativeTime();
		rt.setValue(new INT_U32(0L));

		byte[] byteArray = {(byte)0, (byte)0};
		errs.setObj_handle(h);
		errs.setCurrentTime(rt);

		//The event-type of the result shall be a copy of the event-type from the invocation
		errs.setEvent_type(da.getMessage().getRoiv_cmip_event_report().getEvent_type());
		errs.setEvent_reply_info(byteArray);

		msg.selectRors_cmip_confirmed_event_report(errs);
		return msg;
	}
	*/
	private static final DataApdu.MessageChoiceType rors_cmip_confirmed_event_repor (DataApdu da){
		DataApdu.MessageChoiceType msg = new DataApdu.MessageChoiceType();
		EventReportResultSimple errs = new EventReportResultSimple();

		HANDLE h = new HANDLE();
		h.setValue(new INT_U16(0)); //The MDS object

		if (da.getMessage().getRoiv_cmip_confirmed_event_report().getEvent_time().getValue().getValue() != 0x00FFFFFFFFL)
			System.err.println("Warning: Agent supports Relative time. Response sent is not valid");

		RelativeTime rt = new RelativeTime();
		rt.setValue(new INT_U32(0L));

		byte[] byteArray = {(byte)0, (byte)0};
		errs.setObj_handle(h);
		errs.setCurrentTime(rt);

		//The event-type of the result shall be a copy of the event-type from the invocation
		errs.setEvent_type(da.getMessage().
				getRoiv_cmip_confirmed_event_report().
				getEvent_type());
		errs.setEvent_reply_info(byteArray);

		msg.selectRors_cmip_confirmed_event_report(errs);
		return msg;
	}


	public static final ApduType ROER_NO_SUCH_OBJECT_INSTANCE_Apdu (DataApdu receivedData, DeviceConfig dev_conf){
		DataApdu data = generateRoerDataApdu(receivedData, ASN1_Values.ROER_NO_SUCH_OBJECT_INSTANCE);
		data.getMessage().getRoer().setParameter(new byte[]{});
		return composeApdu(data, dev_conf);
	}

	public static final ApduType ROER_NO_SUCH_ACTION_Apdu (DataApdu receivedData, DeviceConfig dev_conf){
		DataApdu data = generateRoerDataApdu(receivedData, ASN1_Values.ROER_NO_SUCH_ACTION);
		data.getMessage().getRoer().setParameter(new byte[]{});
		return composeApdu(data, dev_conf);
	}

	public static final ApduType ROER_INVALID_OBJECT_INSTANCE_Apdu (DataApdu receivedData, DeviceConfig dev_conf){
		DataApdu data = generateRoerDataApdu(receivedData, ASN1_Values.ROER_INVALID_OBJECT_INSTANCE);
		data.getMessage().getRoer().setParameter(new byte[]{});
		return composeApdu(data, dev_conf);
	}

	public static final ApduType ROER_PROTOCOL_VIOLATION_Apdu (DataApdu receivedData, DeviceConfig dev_conf){
		DataApdu data = generateRoerDataApdu(receivedData, ASN1_Values.ROER_PROTOCOL_VIOLATION);
		data.getMessage().getRoer().setParameter(new byte[]{});
		return composeApdu(data, dev_conf);
	}

	public static final ApduType ROER_NOT_ALLOWED_BY_OBJECT_Apdu (DataApdu receivedData, DeviceConfig dev_conf, OID_Type oid){
		DataApdu data = generateRoerDataApdu(receivedData, ASN1_Values.ROER_NOT_ALLOWED_BY_OBJECT);
		ByteArrayOutputStream output= new ByteArrayOutputStream();
		//Parse data using negotiated encoding rules
		IEncoder<OID_Type> encoderOid;
		try {
			encoderOid = CoderFactory.getInstance().newEncoder(dev_conf.getEncondigRules());
			encoderOid.encode(oid, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.getMessage().getRoer().setParameter(output.toByteArray());
		return composeApdu(data, dev_conf);
	}

	public static final ApduType ROER_ACTION_TIMEOUT_Apdu (DataApdu receivedData, DeviceConfig dev_conf, OID_Type oid){
		DataApdu data = generateRoerDataApdu(receivedData, ASN1_Values.ROER_ACTION_TIMEOUT);
		ByteArrayOutputStream output= new ByteArrayOutputStream();
		//Parse data using negotiated encoding rules
		IEncoder<OID_Type> encoderOid;
		try {
			encoderOid = CoderFactory.getInstance().newEncoder(dev_conf.getEncondigRules());
			encoderOid.encode(oid, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.getMessage().getRoer().setParameter(output.toByteArray());
		return composeApdu(data, dev_conf);
	}

	public static final ApduType ROER_ACTION_ABORTED_Apdu (DataApdu receivedData, DeviceConfig dev_conf, OID_Type oid){
		DataApdu data = generateRoerDataApdu(receivedData, ASN1_Values.ROER_ACTION_ABORTED);
		ByteArrayOutputStream output= new ByteArrayOutputStream();
		//Parse data using negotiated encoding rules
		IEncoder<OID_Type> encoderOid;
		try {
			encoderOid = CoderFactory.getInstance().newEncoder(dev_conf.getEncondigRules());
			encoderOid.encode(oid, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		data.getMessage().getRoer().setParameter(output.toByteArray());
		return composeApdu(data, dev_conf);
	}

	public static final ApduType composeApdu (DataApdu data, DeviceConfig dev_conf) {
		//Create PRST response
		ApduType at = new ApduType();
		PrstApdu pa = new PrstApdu();

		ByteArrayOutputStream output= new ByteArrayOutputStream();
		//Parse data using negotiated encoding rules
		IEncoder<DataApdu> encoderDataApdu;
		try {
			encoderDataApdu = CoderFactory.getInstance().newEncoder(dev_conf.getEncondigRules());
			encoderDataApdu.encode(data, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pa.setValue(output.toByteArray());
		at.selectPrst(pa);
		return at;
	}

	private static final DataApdu generateRoerDataApdu(DataApdu receivedData, int error_value){
		DataApdu data = new DataApdu();
		InvokeIDType iit = new InvokeIDType(receivedData.getInvoke_id().getValue()); //Mirrored from invocation
		DataApdu.MessageChoiceType msg = getRoerMessageResponse(receivedData, error_value);
		data.setInvoke_id(iit);
		data.setMessage(msg);
		return data;
	}

	private static final DataApdu.MessageChoiceType getRoerMessageResponse (DataApdu receivedData, int error_value){
		DataApdu.MessageChoiceType msg = new DataApdu.MessageChoiceType();
		ErrorResult roer = new ErrorResult();
		RoerErrorValue rev = new RoerErrorValue();
		rev.setValue(error_value);
		roer.setError_value(rev);
		msg.selectRoer(roer);
		return msg;
	}

	
	public static final DataApdu PrstRoivCmipSetTimeAction(MDS mds,int century, int year, int month, int day,int hour, int minute,int second, int sec_frac){
		DataApdu data = new DataApdu();
		data.setInvoke_id(new InvokeIDType(mds.getNextInvokeId()));
		HANDLE handle = (HANDLE) mds.getAttribute(Nomenclature.MDC_ATTR_ID_HANDLE).getAttributeType();
		if(handle == null){
			return null;
		}
		OID_Type oid = new OID_Type();
		oid.setValue(new INT_U16(Integer.valueOf(Nomenclature.MDC_ACT_SET_TIME)));
		AbsoluteTime at = new AbsoluteTime();
		at.setCentury(new INT_U8(century));
		at.setYear(new INT_U8(year));
		at.setMonth(new INT_U8(month));
		at.setDay(new INT_U8(day));
		at.setHour(new INT_U8(hour));
		at.setMinute(new INT_U8(minute));
		at.setSecond(new INT_U8(second));
		at.setSec_fractions(new INT_U8(sec_frac));
		
		SetTimeInvoke sti = new SetTimeInvoke();
		
		sti.setDate_time(at);
		FLOAT_Type f = new FLOAT_Type();
		f.setValue(new INT_U32((long)0));
		sti.setAccuracy(f);
		ActionArgumentSimple aas = genActionArgumentSimple(handle,oid,sti,mds.getDeviceConf().getEncondigRules());
		if(aas == null){
			return null;
		}
		DataApdu.MessageChoiceType msg = new DataApdu.MessageChoiceType();
		msg.selectRoiv_cmip_confirmed_action(aas);
		data.setMessage(msg);
		return data;
	}
	
	
	public static final DataApdu PrstRoivCmpGet(MDS mds, HANDLE handle) {
		DataApdu data = new DataApdu();
		DataApdu.MessageChoiceType msg = new DataApdu.MessageChoiceType();
		GetArgumentSimple gat = new GetArgumentSimple();

		gat.setObj_handle(handle);
		AttributeIdList att = new AttributeIdList();
		att.initValue();

		gat.setAttribute_id_list(att);
		msg.selectRoiv_cmip_get(gat);

		data.setInvoke_id(new InvokeIDType(mds.getNextInvokeId()));
		data.setMessage(msg);

		return data;
	}

	private static final <T> ActionArgumentSimple genActionArgumentSimple(HANDLE handle, OID_Type oid, T obj, String e_rules) {
		ActionArgumentSimple aas = new ActionArgumentSimple();
		aas.setObj_handle(handle);
		aas.setAction_type(oid);

		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IEncoder<T> encoderSegmSelection;

			encoderSegmSelection = CoderFactory.getInstance().newEncoder(e_rules);
			encoderSegmSelection.encode(obj, output);
			aas.setAction_info_args(output.toByteArray());

			return aas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final DataApdu PrstRoivCmipAction(PM_Store pmstore, SegmSelection ss) {
		DataApdu data = new DataApdu();

		data.setInvoke_id(new InvokeIDType(pmstore.getMDS().getNextInvokeId()));

		HANDLE handle = (HANDLE) pmstore.getAttribute(Nomenclature.MDC_ATTR_ID_HANDLE).getAttributeType();
		if (handle == null)
			return null;

		OID_Type oid = new OID_Type();
		oid.setValue(new INT_U16(new Integer(Nomenclature.MDC_ACT_SEG_GET_INFO)));

		ActionArgumentSimple aas = genActionArgumentSimple(handle, oid, ss, pmstore.getMDS().getDeviceConf().getEncondigRules());
		if (aas == null)
			return null;

		DataApdu.MessageChoiceType msg = new DataApdu.MessageChoiceType();
		msg.selectRoiv_cmip_confirmed_action(aas);
		data.setMessage(msg);

		return data;
	}

	public static final DataApdu PrstRoivCmipConfirmedAction(PM_Store pmstore, TrigSegmDataXferReq tsdxr) {
		DataApdu data = new DataApdu();

		data.setInvoke_id(new InvokeIDType(pmstore.getMDS().getNextInvokeId()));

		HANDLE handle = (HANDLE) pmstore.getAttribute(Nomenclature.MDC_ATTR_ID_HANDLE).getAttributeType();
		if (handle == null)
			return null;

		OID_Type oid = new OID_Type();
		oid.setValue(new INT_U16(new Integer(Nomenclature.MDC_ACT_SEG_TRIG_XFER)));

		ActionArgumentSimple aas = genActionArgumentSimple(handle, oid, tsdxr, pmstore.getMDS().getDeviceConf().getEncondigRules());
		if (aas == null)
			return null;

		DataApdu.MessageChoiceType msg = new DataApdu.MessageChoiceType();
		msg.selectRoiv_cmip_confirmed_action(aas);
		data.setMessage(msg);

		return data;
	}

	public static final AVA_Type AVAType(Attribute attr, String erules) {
		AVA_Type ava = new AVA_Type();
		OID_Type oid = new OID_Type();

		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IEncoder<Object> encoder = CoderFactory.getInstance().newEncoder(erules);

			oid.setValue(new INT_U16(attr.getAttributeID()));
			ava.setAttribute_id(oid);

			encoder.encode(attr.getAttributeType(), output);
			ava.setAttribute_value(output.toByteArray());
			return ava;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final DataApdu PrstRoivCmipSet(DIM obj, Map<Attribute, Integer> attrs) {
		DataApdu data = new DataApdu();
		DataApdu.MessageChoiceType msg = new DataApdu.MessageChoiceType();
		SetArgumentSimple setArgument = new SetArgumentSimple();
		HANDLE handle = (HANDLE) obj.getAttribute(Nomenclature.MDC_ATTR_ID_HANDLE).getAttributeType();
		ArrayList<AttributeModEntry> modList = new ArrayList<AttributeModEntry>();
		Iterator<Map.Entry<Attribute, Integer>> itAttr = attrs.entrySet().iterator();

		while (itAttr.hasNext()) {
			AttributeModEntry entry = new AttributeModEntry();
			Map.Entry<Attribute, Integer> mapEntry = itAttr.next();
			Attribute attr = mapEntry.getKey();
			AVA_Type ava = AVAType(attr, obj.getMDS().getDeviceConf().getEncondigRules());
			ModifyOperator mod = new ModifyOperator(mapEntry.getValue());

			entry.setAttribute(ava);
			entry.setModify_operator(mod);
			modList.add(entry);
		}

		setArgument.setObj_handle(handle);
		setArgument.setModification_list(new ModificationList(modList));

		msg.selectRoiv_cmip_set(setArgument);
		data.setInvoke_id(new InvokeIDType(obj.getMDS().getNextInvokeId()));
		data.setMessage(msg);
		return data;
	}
}
