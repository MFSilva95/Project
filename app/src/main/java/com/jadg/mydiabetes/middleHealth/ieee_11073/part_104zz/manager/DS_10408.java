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

package com.jadg.mydiabetes.middleHealth.ieee_11073.part_104zz.manager;

import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AttrValMap;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.AttrValMapEntry;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ConfigReport;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ConfigReportRsp;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.HANDLE;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U16;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.MetricSpecSmall;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.NomPartition;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.OID_Type;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoMPFixed;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.ScanReportInfoMPVar;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.TYPE;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.Attribute;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.InvalidAttributeException;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.Numeric;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.manager.MDSManager;

import java.util.Hashtable;

import com.jadg.mydiabetes.middleHealth.org.bn.types.BitString;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Values;

	/**
	 * This class defines the device specialization for the thermometer (IEEE Std 11073-10408),
	 * being a specific agent type, and it provides a description of the device concepts, its
	 * capabilities, and its implementation according to this standard
	 *
	 * @author sancane
	 */

public final class DS_10408 extends MDSManager {

		private static int ownMandatoryIds = Nomenclature.MDC_ATTR_SYS_TYPE_SPEC_LIST;

		public DS_10408(Hashtable<Integer, Attribute> attributeList)
				throws InvalidAttributeException {
			super(attributeList);

			/* Generate the Standard configuration for Numeric object */
			generateNumeric();

		}

		@Override
		protected void checkAttributes(
				Hashtable<Integer, Attribute> attributes)
				throws InvalidAttributeException {
			/* Check generic MDS attributes */
			super.checkAttributes(attributes);

			/*Check specific MDS mandatory attributes for the device specialization 10408 (Standard)*/
			if (!attributes.containsKey(ownMandatoryIds))
				throw new InvalidAttributeException("Attribute id " + ownMandatoryIds + " is not assigned.");
		}

		private void generateNumeric(){
			try {
				Hashtable<Integer,Attribute> mandatoryAttributes = new Hashtable<Integer,Attribute>();

				//from Part 10408: Handle=1
				HANDLE handle = new HANDLE();
				handle.setValue(new INT_U16(new Integer(1)));
				mandatoryAttributes.put(Nomenclature.MDC_ATTR_ID_HANDLE,
						new Attribute(Nomenclature.MDC_ATTR_ID_HANDLE,
										handle));

				//from Part 10408: TYPE {MDC_PART_SCADA, MDC_TEMP_BODY}
				TYPE type = new TYPE();
				OID_Type oid = new OID_Type();
				oid.setValue(new INT_U16(Nomenclature.MDC_TEMP_BODY));
				type.setPartition(new NomPartition(Nomenclature.MDC_PART_SCADA));
				type.setCode(oid);
				mandatoryAttributes.put(Nomenclature.MDC_ATTR_ID_TYPE,
						new Attribute( Nomenclature.MDC_ATTR_ID_TYPE,
										type));


				// from Part 10408: MSS_AVAIL_INTERMITTENT, MSS_AVAIL_STORED_DATA,
				// MSS_UPD_APREIODIC, MSS_MSMT_APREIODIC, MSS_ACC_AGENT_INITIATED
				MetricSpecSmall msm = new MetricSpecSmall();
				int mask = 0;
				mask = mask | ASN1_Values.MSS_AVAIL_INTERMITTENT | ASN1_Values.MSS_AVAIL_STORED_DATA |
						ASN1_Values.MSS_UPD_APREIODIC | ASN1_Values.MSS_MSMT_APREIODIC | ASN1_Values.MSS_ACC_AGENT_INITIATED;
				byte[] bs16 = new byte[2];
				bs16[1] = (byte)(mask & 0x000000FF);
				bs16[0] = (byte)((mask >> 8) & 0x000000FF);
				msm.setValue(new BitString(bs16));
				mandatoryAttributes.put(Nomenclature.MDC_ATTR_METRIC_SPEC_SMALL,
						new Attribute(Nomenclature.MDC_ATTR_METRIC_SPEC_SMALL,
										msm));

				/* Mandatory attributes for standard configuration: */
				//from Part 10408: Unit-Code=MDC_DIM_DEGC
				OID_Type unitOid = new OID_Type();
				unitOid.setValue(new INT_U16(Nomenclature.MDC_DIM_DEGC));
				mandatoryAttributes.put(Nomenclature.MDC_ATTR_UNIT_CODE,
						new Attribute(Nomenclature.MDC_ATTR_UNIT_CODE,
										unitOid));

				//from Part 10408: Attribute-Value-Map {(MDC_ATTR_NU_VAL_OBS_BASIC,2),(MDC_ATTR_TIME_STAMP_ABS,8)}
				AttrValMap avm = new AttrValMap();
				avm.initValue();
				AttrValMapEntry avme1 = new AttrValMapEntry();
				OID_Type attrId1 = new OID_Type();
				attrId1.setValue(new INT_U16(Nomenclature.MDC_ATTR_NU_VAL_OBS_BASIC));
				avme1.setAttribute_id(attrId1);
				avme1.setAttribute_len(2); //default length=2
				AttrValMapEntry avme2 = new AttrValMapEntry();
				OID_Type attrId2 = new OID_Type();
				attrId2.setValue(new INT_U16(Nomenclature.MDC_ATTR_TIME_STAMP_ABS));
				avme2.setAttribute_id(attrId2);
				avme2.setAttribute_len(8); //default length=8
				avm.add(avme1);
				avm.add(avme2);
				mandatoryAttributes.put(Nomenclature.MDC_ATTR_ATTRIBUTE_VAL_MAP,
						new Attribute(Nomenclature.MDC_ATTR_ATTRIBUTE_VAL_MAP,
										avm));

				Numeric numeric = new Numeric(mandatoryAttributes);
				addNumeric(numeric);
			} catch (InvalidAttributeException e) {/*Never thrown in standadard configuration*/
				e.printStackTrace();
			}

		}

		@Override
		public void MDS_DATA_REQUEST() {
			// TODO Auto-generated method stub

		}

		@Override
		public void Set_Time(int century,int year,int month,int day, int hour, int minutes, int seconds, int fractions) {
			// TODO Auto-generated method stub

		}

		@Override
		public ConfigReportRsp MDS_Configuration_Event(ConfigReport config) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void MDS_Dynamic_Data_Update_MP_Fixed(ScanReportInfoMPFixed info) {
			// TODO Auto-generated method stub
			System.out.println("DS_10408");

		}

		@Override
		public void MDS_Dynamic_Data_Update_MP_Var(ScanReportInfoMPVar info) {
			// TODO Auto-generated method stub

		}

		@Override
		public void GET() {
			// TODO Auto-generated method stub

		}
}
