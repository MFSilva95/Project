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
package middleHealth.ieee_11073.part_20601.phd.dim.manager;

import java.util.Hashtable;
import java.util.Iterator;

import middleHealth.es.libresoft.openhealth.messages.MessageFactory;
import middleHealth.es.libresoft.openhealth.utils.ASN1_Tools;

import middleHealth.ieee_11073.part_10101.Nomenclature;
import middleHealth.ieee_11073.part_20601.asn1.ActionResultSimple;
import middleHealth.ieee_11073.part_20601.asn1.ApduType;
import middleHealth.ieee_11073.part_20601.asn1.AttributeList;
import middleHealth.ieee_11073.part_20601.asn1.DataApdu;
import middleHealth.ieee_11073.part_20601.asn1.GetResultSimple;
import middleHealth.ieee_11073.part_20601.asn1.HANDLE;
import middleHealth.ieee_11073.part_20601.asn1.INT_U16;
import middleHealth.ieee_11073.part_20601.asn1.InstNumber;
import middleHealth.ieee_11073.part_20601.asn1.InvokeIDType;
import middleHealth.ieee_11073.part_20601.asn1.OID_Type;
import middleHealth.ieee_11073.part_20601.asn1.SegmSelection;
import middleHealth.ieee_11073.part_20601.asn1.SegmentDataEvent;
import middleHealth.ieee_11073.part_20601.asn1.SegmentInfo;
import middleHealth.ieee_11073.part_20601.asn1.SegmentInfoList;
import middleHealth.ieee_11073.part_20601.asn1.TrigSegmDataXferReq;
import middleHealth.ieee_11073.part_20601.asn1.TrigSegmDataXferRsp;
import middleHealth.ieee_11073.part_20601.asn1.TrigSegmXferRsp;
import middleHealth.ieee_11073.part_20601.phd.dim.Attribute;
import middleHealth.ieee_11073.part_20601.phd.dim.DimTimeOut;
import middleHealth.ieee_11073.part_20601.phd.dim.InvalidAttributeException;
import middleHealth.ieee_11073.part_20601.phd.dim.PM_Store;
import middleHealth.ieee_11073.part_20601.phd.dim.TimeOut;

public class MPM_Store extends PM_Store {

	public MPM_Store(Hashtable<Integer, Attribute> attributeList)
			throws InvalidAttributeException {
		super(attributeList);
	}

	@Override
	public void Clear_Segments(SegmSelection ss) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Get_Segment_Info(SegmSelection ss) {
		try {
			DataApdu data = MessageFactory.PrstRoivCmipAction(this, ss);
			ApduType apdu = MessageFactory.composeApdu(data, getMDS().getDeviceConf());
			InvokeIDType invokeId = data.getInvoke_id();
			getMDS().getStateHandler().send(apdu);

			DimTimeOut to = new DimTimeOut(TimeOut.PM_STORE_TO_CA, invokeId.getValue(), getMDS().getStateHandler()) {

				@Override
				public void procResponse(DataApdu data) {

					if (!data.getMessage().isRors_cmip_confirmed_actionSelected()) {
						System.out.println("Error: Unexpected response format");
						return;
					}

					ActionResultSimple ars = data.getMessage().getRors_cmip_confirmed_action();
					OID_Type oid = ars.getAction_type();
					if (Nomenclature.MDC_ACT_SEG_GET_INFO != oid.getValue().getValue().intValue()) {
						System.out.println("Error: Unexpected response format");
						return;
					}

					try {
						SegmentInfoList sil = ASN1_Tools.decodeData(ars.getAction_info_args(),
													SegmentInfoList.class,
													getMDS().getDeviceConf().getEncondigRules());
						Iterator<SegmentInfo> i = sil.getValue().iterator();
						while (i.hasNext()) {
							SegmentInfo si = i.next();
							InstNumber in = si.getSeg_inst_no();
							AttributeList al = si.getSeg_info();

							Hashtable<Integer, Attribute> attribs;
							attribs = getAttributes(al, getMDS().getDeviceConf().getEncondigRules());
							MPM_Segment pm_segment = new MPM_Segment(attribs);
							addPM_Segment(pm_segment);

							System.out.println("Got PM_Segment " + in.getValue().intValue());

							TrigSegmDataXferReq tsdxr = new TrigSegmDataXferReq();
							tsdxr.setSeg_inst_no(in);
							Trig_Segment_Data_Xfer(tsdxr);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			to.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void Trig_Segment_Data_Xfer(TrigSegmDataXferReq tsdx) {
		DataApdu data = MessageFactory.PrstRoivCmipConfirmedAction(this, tsdx);
		ApduType apdu = MessageFactory.composeApdu(data, getMDS().getDeviceConf());
		InvokeIDType invokeId = data.getInvoke_id();
		getMDS().getStateHandler().send(apdu);

		DimTimeOut to = new DimTimeOut(TimeOut.PM_STORE_TO_CA, invokeId.getValue(), getMDS().getStateHandler()) {

			@Override
			public void procResponse(DataApdu data) {

				if (!data.getMessage().isRors_cmip_confirmed_actionSelected()) {
					System.err.println("Error: Unexpected response format");
					return;
				}

				ActionResultSimple ars = data.getMessage().getRors_cmip_confirmed_action();
				OID_Type oid = ars.getAction_type();
				if (Nomenclature.MDC_ACT_SEG_TRIG_XFER != oid.getValue().getValue().intValue()) {
					System.err.println("Error: Unexpected response format");
					return;
				}

				try {
					TrigSegmDataXferRsp rsp = ASN1_Tools.decodeData(ars.getAction_info_args(),
													TrigSegmDataXferRsp.class,
													getMDS().getDeviceConf().getEncondigRules());
					InstNumber in = rsp.getSeg_inst_no();
					TrigSegmXferRsp tsxr = rsp.getTrig_segm_xfer_rsp();
					int result = tsxr.getValue().intValue();
					if (result == 0)
						return;

					System.err.println("InstNumber " + in.getValue().intValue() + " error " + result);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		to.start();
	}

	private SegmSelection getAllSegments() {
		SegmSelection ss = new SegmSelection();
		ss.selectAll_segments(new INT_U16(new Integer(0)));
		return ss;
	}

	@Override
	public void GET() {
		try {
			HANDLE handle = (HANDLE) getAttribute(Nomenclature.MDC_ATTR_ID_HANDLE).getAttributeType();
			DataApdu data = MessageFactory.PrstRoivCmpGet(getMDS(), handle);
			ApduType apdu = MessageFactory.composeApdu(data, getMDS().getDeviceConf()); 
			InvokeIDType invokeId = data.getInvoke_id();
			getMDS().getStateHandler().send(apdu);

			DimTimeOut to = new DimTimeOut(TimeOut.PM_STORE_TO_GET, invokeId.getValue(), getMDS().getStateHandler()) {

				@Override
				public void procResponse(DataApdu data) {
					System.out.println("GOT_PMSOTRE invoke_id " + data.getInvoke_id().getValue().intValue());

					if (!data.getMessage().isRors_cmip_getSelected()) {
						System.out.println("TODO: Unexpected response format");
						return;
					}

					GetResultSimple grs = data.getMessage().getRors_cmip_get();
					HANDLE handle = (HANDLE) getAttribute(Nomenclature.MDC_ATTR_ID_HANDLE).getAttributeType();
					if (handle == null) {
						System.out.println("Error: Can't get HANDLE attribute in PM_STORE object");
						return;
					}

					if (grs.getObj_handle().getValue().getValue().intValue() != handle.getValue().getValue().intValue()) {
						System.out.println("TODO: Unexpected object handle, should be reserved value " +
																				handle.getValue().getValue().intValue());
						return;
					}

					try {
						Hashtable<Integer, Attribute> attribs;
						attribs = getAttributes(grs.getAttribute_list(), getMDS().getDeviceConf().getEncondigRules());
						Iterator<Integer> i = attribs.keySet().iterator();
						while (i.hasNext()) {
							Attribute attr = attribs.get(i.next());
							if (getAttribute(attr.getAttributeID()) != null) {
								addAttribute(attr);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					SegmSelection ss = getAllSegments();
					Get_Segment_Info(ss);
				}

			};
			to.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void Segment_Data_Event(SegmentDataEvent sde) {
		System.out.println("TODO: Process EventData");
	}

}
