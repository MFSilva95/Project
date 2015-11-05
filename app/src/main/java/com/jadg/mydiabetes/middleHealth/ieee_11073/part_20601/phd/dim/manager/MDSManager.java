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


package com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.manager;

import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AVA_Type;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AbsoluteTime;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ApduType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AttrValMap;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AttrValMapEntry;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.BasicNuObsValue;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ConfigId;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ConfigObject;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ConfigReport;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ConfigReportRsp;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ConfigResult;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.DataApdu;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.GetResultSimple;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.HANDLE;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U16;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.InvokeIDType;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.MdsTimeInfo;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.MetricSpecSmall;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.OID_Type;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ObservationScan;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ObservationScanFixed;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.PowerStatus;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ProdSpecEntry;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ProductionSpec;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.RegCertData;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.RegCertDataList;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoFixed;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoMPFixed;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoMPVar;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoVar;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ScanReportPerFixed;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ScanReportPerVar;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.SystemModel;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.TYPE;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.TypeVer;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.TypeVerList;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.MetricIdList;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.Attribute;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.DIM;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.DimTimeOut;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.Enumeration;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.InvalidAttributeException;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.MDS;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.Numeric;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.TimeOut;

import java.util.Hashtable;
import java.util.Iterator;

import com.jadg.mydiabetes.middleHealth.es.libresoft.mdnf.SFloatType;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.HealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Measure;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.InternalEventReporter;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.messages.MessageFactory;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Values;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.DIM_Tools;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.RawDataExtractor;

public abstract class MDSManager extends MDS {

	/**
	 * Used only in extended configuration when the agent configuration is unknown
	 */
	public MDSManager (byte[] system_id, ConfigId devConfig_id){
		super(system_id,devConfig_id);
	}

	public MDSManager(Hashtable<Integer, Attribute> attributeList)
		throws InvalidAttributeException {
		super(attributeList);
	}

	@Override
	public ConfigReportRsp MDS_Configuration_Event(ConfigReport config) {
		int configId = config.getConfig_report_id().getValue();
		Iterator<ConfigObject> i = config.getConfig_obj_list().getValue().iterator();
		ConfigObject confObj;
		try{
			while (i.hasNext()){
				confObj = i.next();
				//Get Attributes
				Hashtable<Integer,Attribute> attribs = getAttributes(confObj.getAttributes(), getDeviceConf().getEncondigRules());
				//checkGotAttributes(attribs);

				//Generate attribute Handle:
				HANDLE handle = new HANDLE();
				handle.setValue(new INT_U16(new Integer
						(confObj.getObj_handle().getValue().getValue())));
				Attribute attr = new Attribute(Nomenclature.MDC_ATTR_ID_HANDLE,
						handle);
				//Set Attribute Handle to the list
				attribs.put(Nomenclature.MDC_ATTR_ID_HANDLE, attr);

				//checkGotAttributes(attribs);
				int classId = confObj.getObj_class().getValue().getValue();
				switch (classId) {
				case Nomenclature.MDC_MOC_VMS_MDS_SIMP : // MDS Class
					throw new Exception("Unsoportedd MDS Class");
				case Nomenclature.MDC_MOC_VMO_METRIC : // Metric Class
					throw new Exception("Unsoportedd Metric Class");
				case Nomenclature.MDC_MOC_VMO_METRIC_NU : // Numeric Class
					addNumeric(new Numeric(attribs));
					break;
				case Nomenclature.MDC_MOC_VMO_METRIC_SA_RT: // RT-SA Class
					throw new Exception("Unsoportedd RT-SA Class");
				case Nomenclature.MDC_MOC_VMO_METRIC_ENUM: // Enumeration Class
					addEnumeration(new Enumeration(attribs));
					break;
				case Nomenclature.MDC_MOC_VMO_PMSTORE: // PM-Store Class
					addPM_Store(new MPM_Store(attribs));
					break;
				case Nomenclature.MDC_MOC_PM_SEGMENT: // PM-Segment Class
					throw new Exception("Unsoportedd PM-Segment Class");
				case Nomenclature.MDC_MOC_SCAN: // Scan Class
					throw new Exception("Unsoportedd Scan Class");
				case Nomenclature.MDC_MOC_SCAN_CFG: // CfgScanner Class
					throw new Exception("Unsoportedd CfgScanner Class");
				case Nomenclature.MDC_MOC_SCAN_CFG_EPI: // EpiCfgScanner Class
					addScanner(new MEpiCfgScanner(attribs));
					break;
				case Nomenclature.MDC_MOC_SCAN_CFG_PERI: // PeriCfgScanner Class
					throw new Exception("Unsoportedd PeriCfgScanner Class");
				}
			}
			return generateConfigReportRsp(configId,
					ASN1_Values.CONF_RESULT_ACCEPTED_CONFIG);
		}catch (Exception e) {
			e.printStackTrace();
			clearObjectsFromMds();
			if ((ASN1_Values.CONF_ID_STANDARD_CONFIG_START <= configId) && (configId <= ASN1_Values.CONF_ID_STANDARD_CONFIG_END))
				//Error in standard configuration
				return generateConfigReportRsp(configId,
						ASN1_Values.CONF_RESULT_STANDARD_CONFIG_UNKNOWN);
			else return generateConfigReportRsp(configId,
					ASN1_Values.CONF_RESULT_UNSUPPORTED_CONFIG);
		}
	}

	@Override
	public void MDS_Dynamic_Data_Update_Fixed(ScanReportInfoFixed info) {
		try{
			String system_id = DIM_Tools.byteArrayToString(
					(byte[])getAttribute(Nomenclature.MDC_ATTR_SYS_ID).getAttributeType());

			Iterator<ObservationScanFixed> i= info.getObs_scan_fixed().iterator();
			ObservationScanFixed obs;
			while (i.hasNext()) {
				obs=i.next();

				//Get DIM from Handle_id
				Measure m = new Measure();
				
				DIM elem = getObject(obs.getObj_handle());
				AttrValMap avm = (AttrValMap)elem.getAttribute(Nomenclature.MDC_ATTR_ATTRIBUTE_VAL_MAP).getAttributeType();
				Iterator<AttrValMapEntry> it = avm.getValue().iterator();
				RawDataExtractor de = new RawDataExtractor(obs.getObs_val_data());
				addAttributesToReport(m,elem);
				while (it.hasNext()){
					AttrValMapEntry attr = it.next();
					int attrId = attr.getAttribute_id().getValue().getValue();
					int length = attr.getAttribute_len();
					try {
						System.out.println("MYHELPER: " + "atributeId is " + attrId);
						RawDataExtractor.decodeRawData(m,attrId,de.getData(length), this.getDeviceConf().getEncondigRules());
						System.out.println("Added measure success!!");
					}catch(Exception e){
						System.err.println("Error: Can NOT get attribute " + attrId);
						e.printStackTrace();
					}
				}
				System.out.println("receivedMeasure");
				InternalEventReporter.receivedMeasure(system_id, m);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void MDS_Dynamic_Data_Update_MP_Fixed(ScanReportInfoMPFixed info){
		try{
			System.out.println("ok i got into the MDSmanager function...");
			String system_id = DIM_Tools.byteArrayToString(
					(byte[])getAttribute(Nomenclature.MDC_ATTR_SYS_ID).getAttributeType());
			System.out.println("look at my systemid " + system_id);
			Iterator<ScanReportPerFixed> i = info.getScan_per_fixed().iterator();
			while(i.hasNext()){
				System.out.println("hey look it has a next!");
				ScanReportPerFixed srpf = i.next();
				Iterator<ObservationScanFixed> i2 = srpf.getObs_scan_fixed().iterator();
				ObservationScanFixed obs;
				while (i2.hasNext()) {
					obs=i2.next();
					Measure m = new Measure();
					//Get DIM from Handle_id
					DIM elem = getObject(obs.getObj_handle());
					AttrValMap avm = (AttrValMap)elem.getAttribute(Nomenclature.MDC_ATTR_ATTRIBUTE_VAL_MAP).getAttributeType();
					Iterator<AttrValMapEntry> it = avm.getValue().iterator();
					RawDataExtractor de = new RawDataExtractor(obs.getObs_val_data());
					addAttributesToReport(m,elem);
					while (it.hasNext()){
						AttrValMapEntry attr = it.next();
						int attrId = attr.getAttribute_id().getValue().getValue();
						int length = attr.getAttribute_len();
						try {
							System.out.println("MYHELPER: " + "atributeId is " + attrId);
							RawDataExtractor.decodeRawData(m,attrId,de.getData(length), this.getDeviceConf().getEncondigRules());
							System.out.println("Added measure success!!");
						}catch(Exception e){
							System.err.println("Error: Can NOT get attribute " + attrId);
							e.printStackTrace();
						}
					}
					System.out.println("receivedMeasure");
					
					InternalEventReporter.receivedMeasure(system_id, m);
				}
			}
		}catch(Exception e){
				System.err.println("Error parsing MDS_Dynamic_Data_Update_Fixed");
		}
	}
	
	@Override
	public void MDS_Dynamic_Data_Update_MP_Var(ScanReportInfoMPVar info){
		try{
			
		
			String system_id = DIM_Tools.byteArrayToString(
					(byte[])getAttribute(Nomenclature.MDC_ATTR_SYS_ID).getAttributeType());
			Iterator<ScanReportPerVar> i = info.getScan_per_var().iterator();
			while(i.hasNext()){
				// warning: we are ignoring personID in order to make it consistent with Dynamic_data_update_mp_var
				ScanReportPerVar srpv = i.next();
				Iterator<ObservationScan> i2= srpv.getObs_scan_var().iterator();
				ObservationScan obs;
	
				while (i2.hasNext()) {
					obs=i2.next();
					//Get Numeric from Handle_id
					Measure m = new Measure();
					Numeric numeric = getNumeric(obs.getObj_handle());
					addAttributesToReport(m,numeric);
					if (numeric == null)
						throw new Exception("Numeric class not found for handle: " + obs.getObj_handle().getValue().getValue());
	
					Iterator<AVA_Type> it = obs.getAttributes().getValue().iterator();
					while (it.hasNext()){
						AVA_Type att = it.next();
						Integer att_id = att.getAttribute_id().getValue().getValue();
						byte[] att_value = att.getAttribute_value();
						System.out.println("MYHELPER: " + "atributeId is " + att_id);
						RawDataExtractor.decodeRawData(m,att_id,att_value, this.getDeviceConf().getEncondigRules());
					}
					InternalEventReporter.receivedMeasure(system_id, m);
				}
			
			}
		}catch(Exception e){
			System.err.println("Error parsing MDS_Dynamic_Data_Update_Var");
			e.printStackTrace();
		}
	}
	
	@Override
	public void MDS_Dynamic_Data_Update_Var(ScanReportInfoVar info) {
		try{
			String system_id = DIM_Tools.byteArrayToString(
					(byte[])getAttribute(Nomenclature.MDC_ATTR_SYS_ID).getAttributeType());

			Iterator<ObservationScan> i= info.getObs_scan_var().iterator();
			ObservationScan obs;

			while (i.hasNext()) {
				obs=i.next();
				//Get Numeric from Handle_id
				Measure m = new Measure();
				Numeric numeric = getNumeric(obs.getObj_handle());
				addAttributesToReport(m,numeric);
				if (numeric == null)
					throw new Exception("Numeric class not found for handle: " + obs.getObj_handle().getValue().getValue());

				Iterator<AVA_Type> it = obs.getAttributes().getValue().iterator();
				while (it.hasNext()){
					AVA_Type att = it.next();
					Integer att_id = att.getAttribute_id().getValue().getValue();
					byte[] att_value = att.getAttribute_value();
					System.out.println("MYHELPER: " + "atributeId is " + att_id);
					RawDataExtractor.decodeRawData(m,att_id,att_value, this.getDeviceConf().getEncondigRules());
				}
				InternalEventReporter.receivedMeasure(system_id, m);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	/* Next method add additional info to report to application layer. Please, feel
	 * free to make changes. */
	private void addAttributesToReport (Measure m, DIM measure) {
		Attribute at;

		at = measure.getAttribute(Nomenclature.MDC_ATTR_ID_TYPE);
		TYPE type = (TYPE)at.getAttributeType();
		m.setMeasureId(type.getCode().getValue().getValue());
		System.out.println("MYHELPER: " + "inside addAttributesToReport with atributetype: " + type.getCode().getValue().getValue().intValue());
		at = measure.getAttribute(Nomenclature.MDC_ATTR_UNIT_CODE);
		if (at != null) {
			OID_Type unit_cod = (OID_Type)at.getAttributeType();
			m.setUnitId(unit_cod.getValue().getValue());
		}
		at = measure.getAttribute(Nomenclature.MDC_ATTR_ID_PHYSIO_LIST);
		if(at != null){
			MetricIdList specificMetrics = (MetricIdList) at.getAttributeType();
			Iterator<OID_Type> it = specificMetrics.getValue().iterator();
			while(it.hasNext()){
				OID_Type metricId = it.next();
				System.out.println("MDS: MY SPECIFIC METRIC ID: " + metricId.getValue().getValue());
				m.add(metricId.getValue().getValue());
			}
			
		}
		at = measure.getAttribute(Nomenclature.MDC_ATTR_ID_PHYSIO);
		if(at != null){
			OID_Type specificType = (OID_Type) at.getAttributeType();
			m.setMeasureId(specificType.getValue().getValue());
		}
	}

	//----------------------------------------PRIVATE-----------------------------------------------------------
	private HealthDevice checkGotAttributes(Hashtable<Integer,Attribute> attribs){
		HealthDevice dev = new HealthDevice();
		
		Iterator<Integer> i = attribs.keySet().iterator();
		while (i.hasNext()){
			int id = i.next();
			attribs.get(id);
			System.out.println("MDS: Checking attribute: " + DIM_Tools.getAttributeName(id) + " " + id);
			Attribute attr = attribs.get(id);
			switch (id){
			case Nomenclature.MDC_ATTR_ID_TYPE :
				TYPE t = (TYPE) attribs.get(new Integer(id)).getAttributeType();
				System.out.println("MDS: partition: " + t.getPartition().getValue());
				System.out.println("MDS: code: " + t.getCode().getValue().getValue());
				System.out.println("MDS: ok.");
				break;
			case Nomenclature.MDC_ATTR_TIME_ABS:
			case Nomenclature.MDC_ATTR_TIME_STAMP_ABS :
				AbsoluteTime time = (AbsoluteTime) attr.getAttributeType();
				System.out.println("MDS: century: " + Integer.toHexString(time.getCentury().getValue()));
				System.out.println("MDS: year: " + Integer.toHexString(time.getYear().getValue()));
				System.out.println("MDS: month: " + Integer.toHexString(time.getMonth().getValue()));
				System.out.println("MDS: day: "+ Integer.toHexString(time.getDay().getValue()));
				System.out.println("MDS: hour: " + Integer.toHexString(time.getHour().getValue()));
				System.out.println("MDS: minute: " + Integer.toHexString(time.getMinute().getValue()));
				System.out.println("MDS: second: " + Integer.toHexString(time.getSecond().getValue()));
				System.out.println("MDS: sec-fraction: " + Integer.toHexString(time.getSec_fractions().getValue()));
				break;
			case Nomenclature.MDC_ATTR_UNIT_CODE:
				OID_Type oid = (OID_Type)attribs.get(new Integer(id)).getAttributeType();
				System.out.println("MDS: oid: " + oid.getValue().getValue());
				System.out.println("MDS: ok.");
				break;
			case Nomenclature.MDC_ATTR_METRIC_SPEC_SMALL:
				MetricSpecSmall mss = (MetricSpecSmall)attribs.get(new Integer(id)).getAttributeType();
				//System.out.println("partition: " + getHexString(mss.getValue().getValue()));
				System.out.println("MDS: ok.");
				break;
			case Nomenclature.MDC_ATTR_NU_VAL_OBS_BASIC :
				BasicNuObsValue val = (BasicNuObsValue)attribs.get(new Integer(id)).getAttributeType();
				try {
						SFloatType sf = new SFloatType(val.getValue().getValue());
						System.out.println("MDS: BasicNuObsValue: " + sf.doubleValueRepresentation());
					} catch (Exception e) {
						e.printStackTrace();
					}
				System.out.println("MDS: ok.");
				break;
			case Nomenclature.MDC_ATTR_ATTRIBUTE_VAL_MAP:
				AttrValMap avm = (AttrValMap)attribs.get(new Integer(id)).getAttributeType();
				Iterator<AttrValMapEntry> iter = avm.getValue().iterator();
				while (iter.hasNext()){
					AttrValMapEntry entry = iter.next();
					System.out.println("MDS: --");
					System.out.println("MDS: attrib-id: " + entry.getAttribute_id().getValue().getValue());
					System.out.println("MDS: attrib-len: " + entry.getAttribute_len());
				}
				System.out.println("MDS: ok.");
				break;
			
			case Nomenclature.MDC_ATTR_SYS_TYPE:
				TypeVer systype = (TypeVer) attr.getAttributeType();
				dev.addTypeID(systype.getType().getValue().getValue());
				break;
			case Nomenclature.MDC_ATTR_SYS_TYPE_SPEC_LIST:
				//MARK
				TypeVerList sysTypes = (TypeVerList) attr.getAttributeType();
				Iterator<TypeVer> it = sysTypes.getValue().iterator();
				System.out.println("MDS: Spec. list values:");
				
				while (it.hasNext()) {
					dev.addTypeID(it.next().getType().getValue().getValue());
					//System.out.println("MDS: \t" + it.next().getType().getValue().getValue());
				}
				break;
				
			case Nomenclature.MDC_ATTR_DEV_CONFIG_ID:
				ConfigId configId = (ConfigId) attr.getAttributeType();
				System.out.println("MDS: Dev config id: " + configId.getValue());
				break;
			case Nomenclature.MDC_ATTR_SYS_ID:
				byte[] octet = (byte[]) attr.getAttributeType();
				String sysId = new String(octet);
				System.out.println("MDS: Sys id: " + sysId);
				dev.setSystId(sysId);
				break;
			case Nomenclature.MDC_ATTR_ID_MODEL:
				SystemModel systemModel = (SystemModel) attr.getAttributeType();
				String model_number = new String(systemModel.getModel_number());
				String manufacturer = new String(systemModel.getManufacturer());
				System.out.println("MDS: System manufactures: " + manufacturer);
				System.out.println("MDS: System model number: " + model_number);
				dev.setModel(model_number);
				dev.setManufacturer(manufacturer);
				break;
			case Nomenclature.MDC_ATTR_ID_HANDLE:
				HANDLE handle = (HANDLE) attr.getAttributeType();
				System.out.println("MDS: Id handle: " + handle.getValue().getValue());
				break;
			case Nomenclature.MDC_ATTR_REG_CERT_DATA_LIST:
				System.out.println("MDS: Reg cert. data list: ");
				RegCertDataList regList = (RegCertDataList) attr.getAttributeType();
				Iterator<RegCertData> regIt = regList.getValue().iterator();
				while (regIt.hasNext()) {
					RegCertData cert = regIt.next();
					System.out.println("MDS: \t" + cert.getAuth_body_and_struc_type().getAuth_body().getValue() +
								" " + cert.getAuth_body_and_struc_type().getAuth_body_struc_type().getValue());
				}
				break;
			case Nomenclature.MDC_ATTR_MDS_TIME_INFO:
				System.out.println("MDS: Mds time information:");
				MdsTimeInfo timeInfo = (MdsTimeInfo) attr.getAttributeType();
				byte[] capabilities = timeInfo.getMds_time_cap_state().getValue().getValue();
				System.out.print("MDS: \t");
				for (int i1 = 0; i1 < capabilities.length; i1++) {
					String binary = Integer.toBinaryString(capabilities[i1]);
					if (binary.length() > 8)
						binary = binary.substring(binary.length() - 8, binary.length());
					System.out.print("MDS: " + binary);
				}
				System.out.println();
				System.out.println("MDS: \t" + timeInfo.getTime_sync_protocol().getValue().getValue().getValue());
				System.out.println("MDS: \t" + timeInfo.getTime_sync_accuracy().getValue().getValue());
				System.out.println("MDS: \t" + timeInfo.getTime_resolution_abs_time());
				System.out.println("MDS: \t" + timeInfo.getTime_resolution_rel_time());
				System.out.println("MDS: \t" + timeInfo.getTime_resolution_high_res_time().getValue());
				break;
			case Nomenclature.MDC_ATTR_ID_PROD_SPECN:
				System.out.println("MDS: Production specification:");
				ProductionSpec ps = (ProductionSpec) attr.getAttributeType();
				Iterator<ProdSpecEntry> itps = ps.getValue().iterator();
				while (itps.hasNext()) {
					ProdSpecEntry pse = itps.next();
					System.out.println("MDS: \tSpec type: " + pse.getSpec_type());
					System.out.println("MDS: \tComponent id: " + pse.getComponent_id().getValue().getValue());
					System.out.println("MDS: \tProd spec: " + new String(pse.getProd_spec()));
				}
				break;
			case Nomenclature.MDC_ATTR_VAL_BATT_CHARGE:
				INT_U16 batt = (INT_U16) attr.getAttributeType();
				System.out.println("MDS: battery remaining: " + batt.getValue());
				dev.setBatteryLevel(batt.getValue());
				break;
			case Nomenclature.MDC_ATTR_POWER_STAT:
				PowerStatus power = (PowerStatus) attr.getAttributeType();
				byte[] bytes = power.getValue().getValue();
				StringBuilder sb = new StringBuilder(bytes.length * 2);
				   for(byte b: bytes)
				      sb.append(String.format("%02x", b & 0xff));
				String p = sb.toString();
				dev.setPowerStatus(p);
				System.out.println("MDS: powerStatus: " + p);
			default:
				System.out.println("MDS: >>>>>>>Id not implemented yet");
				break;
			}
			
		}
		dev.set11073(true);
		return dev;
	}

	/**
	 * Generate a response for configuration
	 * @param result Reponse configuration
	 * @return
	 */
	private ConfigReportRsp generateConfigReportRsp (int report_id, int result) {
		ConfigReportRsp configRsp = new ConfigReportRsp();
		ConfigId confId = new ConfigId (new Integer(report_id));
		ConfigResult confResult = new ConfigResult(new Integer(result));
		configRsp.setConfig_report_id(confId);
		configRsp.setConfig_result(confResult);
		return configRsp;
	}

	public void GET () {
		HANDLE handle = new HANDLE();
		handle.setValue(new INT_U16(0));
		DataApdu data = MessageFactory.PrstRoivCmpGet(this, handle);
		ApduType apdu = MessageFactory.composeApdu(data, getDeviceConf());

		try{
			InvokeIDType invokeId = data.getInvoke_id();
			getStateHandler().send(apdu);
			DimTimeOut to = new DimTimeOut(TimeOut.MDS_TO_GET, invokeId.getValue(), getStateHandler()) {

				@Override
				public void procResponse(DataApdu data) {
					System.out.println("Received response for get MDS");

					if (!data.getMessage().isRors_cmip_getSelected()) {
						//TODO: Unexpected response format
						System.out.println("TODO: Unexpected response format");
						return;
					}

					GetResultSimple grs = data.getMessage().getRors_cmip_get();

					if (grs.getObj_handle().getValue().getValue() != 0) {
						//TODO: Unexpected object handle, should be reserved value 0
						System.out.println("TODO: Unexpected object handle, should be reserved value 0");
						return;
					}

					try {
						Hashtable<Integer, Attribute> attribs;
						attribs = getAttributes(grs.getAttribute_list(), getDeviceConf().getEncondigRules());
						System.out.println("MYSERVICE: RUNNING CHECK ATTRIBUTES");
						HealthDevice dev = checkGotAttributes(attribs);
						System.out.println("MYSERVICE: CHECK ATTRIBUTES DONE");
						
						InternalEventReporter.gotAgentInfoFromMDS(dev);
						
						Iterator<Integer> i = attribs.keySet().iterator();
						while (i.hasNext()){
							int id = i.next();
							addAttribute(attribs.get(id));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			to.start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
