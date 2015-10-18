/*
Copyright (C) 2010 GSyC/LibreSoft, Universidad Rey Juan Carlos.

Author: Santiago Carot Nemesio <scarot@libresoft.es>

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

package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils;

import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.BITS_32;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.BasicNuObsValue;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.BasicNuObsValueCmp;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U16;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U32;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.OID_Type;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.jadg.mydiabetes.middleHealth.org.bn.CoderFactory;
import com.jadg.mydiabetes.middleHealth.org.bn.IDecoder;

import com.jadg.mydiabetes.middleHealth.es.libresoft.mdnf.FloatType;
import com.jadg.mydiabetes.middleHealth.es.libresoft.mdnf.SFloatType;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Measure;

public class RawDataExtractor {
	private int index;
	private byte[] raw;

	public RawDataExtractor (byte[] raw_data){
		raw = raw_data;
		index = 0;
	}

	public byte[] getData (int len){
		if ((index + len)>raw.length)
				return null;
		byte[] data = new byte[len];
		for (int i = 0; i<len; i++)
			data[i]=raw[index++];
		return data;
	}

	public static void decodeRawData(Measure m, int attrId, byte[] data, String eRules) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		//Decode AttrValMap using accorded enc_rules
		IDecoder decoder = CoderFactory.getInstance().newDecoder(eRules);
		switch (attrId){
		case Nomenclature.MDC_ATTR_NU_VAL_OBS_BASIC:
			INT_U16 iu = decoder.decode(input, INT_U16.class);
			SFloatType ft = new SFloatType(iu.getValue());
			System.out.println("Measure: " + ft.doubleValueRepresentation());
			m.add(ft.doubleValueRepresentation());
		case Nomenclature.MDC_ATTR_NU_VAL_OBS_SIMP:
			INT_U32 iu2 = decoder.decode(input, INT_U32.class);
			FloatType ft2 = new FloatType(iu2.getValue());
			System.out.println("Measure: " + ft2.doubleValueRepresentation());
			m.add(ft2.doubleValueRepresentation());
		case Nomenclature.MDC_ATTR_TIME_STAMP_ABS:
			/*
			 * The absolute time data type specifies the time of day with a resolution of 1/100
			 * of a second. The hour field shall be reported in 24-hr time notion (i.e., from 0 to 23).
			 * The values in the structure shall be encoded using binary coded decimal (i.e., 4-bit
			 * nibbles). For example, the year 1996 shall be represented by the hexadecimal value 0x19
			 * in the century field and the hexadecimal value 0x96 in the year field. This format is
			 * easily converted to character- or integer-based representations. See AbsoluteTime
			 * structure for details.
			 */
			final String rawDate = ASN1_Tools.getHexString(data);
			final String source = rawDate.substring(0, 4) + "/" + /*century + year(first 2Bytes)*/
					rawDate.substring(4, 6) + "/" +   /* month next 2B*/
					rawDate.substring(6, 8) + " " +   /* day next 2B */
					rawDate.substring(8, 10) + ":" +  /* hour next 2B */
					rawDate.substring(10, 12) + ":" + /* minute next 2B */
					rawDate.substring(12, 14) + ":" + /* second next 2B */
					rawDate.substring(14); /* frac-sec last 2B */
			SimpleDateFormat sdf =  new SimpleDateFormat("yy/MM/dd HH:mm:ss:SS");
			Date d = sdf.parse(source);
			System.out.println("date: " + d);
			m.setTimestamp(d.getTime());
		case Nomenclature.MDC_ATTR_NU_CMPD_VAL_OBS_BASIC:
			System.out.println("MDC_ATTR_NU_CMPD_VAL_OBS_BASIC");
			BasicNuObsValueCmp cmp_val = decoder.decode(input, BasicNuObsValueCmp.class);
			Iterator<BasicNuObsValue> it = cmp_val.getValue().iterator();
			while (it.hasNext()) {
				BasicNuObsValue value = it.next();
				SFloatType ms = new SFloatType(value.getValue().getValue());
				
				System.out.println("Measure: " + ms.doubleValueRepresentation());
				m.add(ms.doubleValueRepresentation());
			}
			
		case Nomenclature.MDC_ATTR_TIME_PD_MSMT_ACTIVE:
			INT_U32 iu3 = decoder.decode(input, INT_U32.class);
			FloatType ft3 = new FloatType(iu3.getValue());
			System.out.println("Measure: " + ft3.doubleValueRepresentation());
			m.add(ft3.doubleValueRepresentation());
		case Nomenclature.MDC_ATTR_ENUM_OBS_VAL_SIMP_OID:
			OID_Type oid = decoder.decode(input, OID_Type.class);
			
			System.out.println("Measure oid_type: " + oid.getValue().getValue());
			System.out.println("TODO: deal with measure oid types...");
		case Nomenclature.MDC_ATTR_ENUM_OBS_VAL_SIMP_BIT_STR:
			BITS_32 bits32 = decoder.decode(input, BITS_32.class);
			System.out.println("Measure: " + ASN1_Tools.getHexString(bits32.getValue().getValue()));
			System.out.println("TODO: deal with measures in bytes");
		}
		throw new Exception ("Attribute " + attrId + " unknown.");
	}
}
