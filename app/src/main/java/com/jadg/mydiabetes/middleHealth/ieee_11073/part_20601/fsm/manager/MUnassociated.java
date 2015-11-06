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

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Device11073;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.DeviceConfig;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.DeviceConfigCreator;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.ManagerConfig;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.Event;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.EventType;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.messages.MessageFactory;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Tools;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Values;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_104zz.manager.DS_Extended;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_104zz.manager.DeviceSpecializationFactory;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AarqApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ApduType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataProto;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataProtoList;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataReqModeCapab;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.PhdAssociationInformation;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.fsm.StateHandler;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.fsm.Unassociated;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.MDS;

import java.util.Iterator;

public final class MUnassociated extends Unassociated {

	public MUnassociated(StateHandler handler) {
		super(handler);
	}

	@Override
	public synchronized void process(ApduType apdu) {
		if (apdu.isAarqSelected()){
			associating(apdu.getAarq());
		} else if (apdu.isAareSelected() || apdu.isRlrqSelected() || apdu.isPrstSelected()){
			state_handler.send(MessageFactory.AbrtApdu_UNDEFINED());
		}
	}

	@Override
	public synchronized boolean processEvent(Event event) {
		if (event.getTypeOfEvent() == EventType.IND_TRANS_DESC) {
			System.err.println("2.2) IND Transport disconnect. Should indicate to application layer...");
			state_handler.changeState(new MDisconnected(state_handler));
			return true;
		} else
			return false;

		//else: Ignore
		/*
		}else if (event.getTypeOfEvent() == EventType.REQ_ASSOC_REL){
			System.err.println("2.6) REQ AssocRel. Should not happen. Ignore");
		}else if (event.getTypeOfEvent() == EventType.REQ_ASSOC_ABORT) {
			System.err.println("2.7) REQ AssocAbort. Should not happen. Ignore");
		}else{
			System.err.println("Warning: unexpected event (" + event.getTypeOfEvent() + ") arrived in disconnected state.");
		}
		*/
	}



	//----------------------------------PRIVATE--------------------------------------------------------

	//Associating state is omitted, its functionality is implemented in next private method
	private void associating(AarqApdu aarq) {
		if (areArraysEquals(aarq.getAssoc_version().getValue().getValue(),ManagerConfig.assoc_version)) {
			DataProtoList dpl = aarq.getData_proto_list();
			Iterator<DataProto> i = dpl.getValue().iterator();
			boolean selected = false;
			DataProto dp=null;
			while (!selected && i.hasNext()) {
				dp = i.next();
				/* Current version only provides support for standard data exchange protocol */
				if (dp.getData_proto_id().getValue().intValue() == ASN1_Values.DATA_PROTO_ID_20601){
					try {
						/* Get PhdAssociationInformation data structure*/
						PhdAssociationInformation phd = ASN1_Tools.decodeData(dp.getData_proto_info(),
																			PhdAssociationInformation.class,
																			Device11073.MDER_DEFUALT);
						if (isValidPhdAssociationInformation(phd)){
							processDeviceConnection(phd);
						}else{
							state_handler.send(MessageFactory.AareRejectApdu_NO_COMMON_PARAMETER());
						}
						selected = true;
					} catch (Exception e) {
						/* Could not get the PhdAssociationInformation: Search another DataProto in the list*/
						e.printStackTrace();
					}
				}
				/*
				 * else if (dp.getData_proto_id().getValue().intValue() == ASN1_Values.DATA_PROTO_ID_EXTERNAL)
					System.err.println("Warning: DATA_PROTO_ID_EXTERNAL configuration is not supported.");
				 */
			}

			if (!selected) {
				/* Reject because there is not common data protocol found in DataProtoList sent from the agent */
				state_handler.send(MessageFactory.AareRejectApdu_NO_COMMON_PROTOCOL());
			}

		}else{
			state_handler.send(MessageFactory.AareRejectApdu_UNSUPPORTED_ASSOC_VERSION());
		}
	}


	private boolean areArraysEquals (byte[] a1, byte[] a2) {
		if (a1.length != a2.length)
			return false;
		for (int i =0; i< a1.length; i++ ) {
			if (a1[i]!=a2[i])
				return false;
		}
		return true;
	}

	private boolean isValidPhdAssociationInformation (PhdAssociationInformation phd) {

		/* Check protocol version (only version 1) */
		if (!areArraysEquals(phd.getProtocol_version().getValue().getValue(),ManagerConfig.protocol_version)){
			/*TODO: If there is not a common protocol version, the manager shall reject the association
			request and set the protocolVersion to all zeros.
			*/
			return false;
		}

		/* Check if MDER encoding rules bit is set */
		int bytes = 0x000000FF & phd.getEncoding_rules().getValue().getValue()[0];
		bytes = (bytes << 8) | phd.getEncoding_rules().getValue().getValue()[1];
		if ((bytes & ASN1_Values.ENC_MDER) != ASN1_Values.ENC_MDER){
			/* Not support MDER */
			return false;
		}

		/* check nomenclature version */
		bytes = phd.getEncoding_rules().getValue().getValue()[0];
		bytes = (bytes << 24);
		if ((bytes & ASN1_Values.NOMENCLATURE_VERSION1) != ASN1_Values.NOMENCLATURE_VERSION1){
			return false;
		}

		/* Check functional units */
		if ((phd.getFunctional_units().getValue().getValue()[0] | 0x00 )!=0){
			//Not supported this functionality
			return false;
		}

		/* Check system type*/
		long flags = 0;
		for (int i=0; i<phd.getSystem_type().getValue().getValue().length; i++){
			flags = (flags << 8) | (0x00000000000000FFL & phd.getSystem_type().getValue().getValue()[i]);
		}
		if ((flags & ASN1_Values.SYS_TYPE_MANAGER) == ASN1_Values.SYS_TYPE_MANAGER) {
			return false;
		}else if ((flags & ASN1_Values.SYS_TYPE_AGENT) != ASN1_Values.SYS_TYPE_AGENT){
			return false;
		}

		DataReqModeCapab drmc = phd.getData_req_mode_capab();
		if (!(drmc.getData_req_init_agent_count()==0 || drmc.getData_req_init_agent_count()==1)){
			 /* maximum number of parallel agent initiated data requests/ flows.
			  * Shall currently be set only to 0 or 1
			  */
			return false;
		}

		/* Check AttributeList */
		/*TODO: Get the information of services used to access object attributes
		Iterator<AVA_Type> i = phd.getOption_list().getValue().iterator();
		AVA_Type ava;
		while (i.hasNext()) {
			ava = i.next();
		}
		*/
		return true;
	}

	private DeviceConfig getDeviceConfiguration (PhdAssociationInformation phd, int data_proto_id) {
		DeviceConfigCreator dev_conf = new DeviceConfigCreator();
		dev_conf.setDataProtoId(data_proto_id);
		dev_conf.setPhdId(phd.getDev_config_id().getValue().intValue());
		dev_conf.setProtocolVersion(1);

		/* Check encoding rules */
		int bytes = 0x000000FF & phd.getEncoding_rules().getValue().getValue()[0];
		bytes = (bytes << 8) | phd.getEncoding_rules().getValue().getValue()[1];
		String srules = Device11073.MDER_DEFUALT;
		if ((bytes & ASN1_Values.ENC_PER) == ASN1_Values.ENC_PER)
			srules = "PER";
		//else  if ((bytes & ASN1_Values.ENC_XER) == ASN1_Values.ENC_XER)
		//	srules = "XER"; //Not supported by binary notes
		dev_conf.setEncondigRules(srules);
		dev_conf.setNomenclatureVersion(1);

		DataReqModeCapab drmc = phd.getData_req_mode_capab();
		/*Data req mode flags*/
		dev_conf.setDataReqModeFlags(drmc.getData_req_mode_flags().getValue().getValue());

		dev_conf.setDataReqInitAgentCount(drmc.getData_req_init_agent_count());
		// Maximum number of parallel manager initiated data requests:
		dev_conf.setDataReqInitManagerCount(drmc.getData_req_init_manager_count());

		return dev_conf.getDeviceConfig();
	}

	private void processDeviceConnection(PhdAssociationInformation phd){
		int id = phd.getDev_config_id().getValue().intValue();
		if ((ASN1_Values.CONF_ID_STANDARD_CONFIG_START <= id) && (id <= ASN1_Values.CONF_ID_STANDARD_CONFIG_END)){
			// Standard Configuration
			processStandardConfiguration(phd);
			//processExtendedConfiguration(phd);
		}else if ((ASN1_Values.CONF_ID_EXTENDED_CONFIG_START <= id) && (id <= ASN1_Values.CONF_ID_EXTENDED_CONFIG_END)){
			//Extended configuration
			processExtendedConfiguration(phd);
		}
	}

	private void processExtendedConfiguration(PhdAssociationInformation phd) {
		DeviceConfig dev_conf = getDeviceConfiguration(phd, ASN1_Values.DATA_PROTO_ID_20601);
		DS_Extended extMDS = DeviceSpecializationFactory.getExtendedMDS(phd.getSystem_id(),phd.getDev_config_id(), state_handler);
		//Set device config
		extMDS.setDeviceConfig(dev_conf);
		//Set MDS Object
		state_handler.setMDS(extMDS);
		//Send AareApdu Accepted unknown config and transit to configuring state
		state_handler.send(MessageFactory.AareApdu_20601_ACCEPTED_UNKNOWN_CONFIG(dev_conf));
		state_handler.changeState(new WaitingForConfig(state_handler));

		extMDS.GET();
	}

	private void processStandardConfiguration(PhdAssociationInformation phd) {

		DeviceConfig dev_conf = getDeviceConfiguration(phd, ASN1_Values.DATA_PROTO_ID_20601);
		int id = dev_conf.getPhdId();

		if ((400 <= id) && (id <= 499)){
			processExtendedConfiguration(phd);
			/*
			acceptStandardAssociation(
					DeviceSpecializationFactory.getOxymeter10404(
							phd.getSystem_id(), phd.getDev_config_id()), dev_conf);
			*/
		}else if ((700 <= id) && (id <= 799)){ // Blood Pressure
			processExtendedConfiguration(phd);
		}else if ((800 <= id) && (id <= 899)){ //Thermometer
			processExtendedConfiguration(phd);
			/*
			acceptStandardAssociation(
					DeviceSpecializationFactory.getThermometer10408(
							phd.getSystem_id(), phd.getDev_config_id()), dev_conf);
			*/
		}else if ((1500 <= id) && (id <= 1599)){
			processExtendedConfiguration(phd);
			/*
			acceptStandardAssociation(
					DeviceSpecializationFactory.getWeighingScale10415(phd.getSystem_id(), phd.getDev_config_id()),dev_conf);
			*/
		}else if ((1700 <= id) && (id <= 1799)){
			System.out.println("Glucose is not yet supported");
		}else if ((4100 <= id) && (id <= 4199)){
			System.out.println("Cardio/Strength is not yet supported");
		}else if ((4200 <= id) && (id <= 4299)){
			System.out.println("Cardio/Strength is not yet supported");
		}else if ((7100 <= id) && (id <= 7199)){ // Activity Hub
			processExtendedConfiguration(phd);
		}
	}

	private void acceptStandardAssociation (MDS mds, DeviceConfig dev_conf){
		//Set device config
		mds.setDeviceConfig(dev_conf);
		//Set MDS Object
		state_handler.setMDS(mds);


		//Send AareApdu Accepted and transit to Operating
		state_handler.send(MessageFactory.AareApdu_20601_ACCEPTED(
				state_handler.getMDS().getDeviceConf()));
		state_handler.changeState(new MOperating(state_handler));
	}
}
