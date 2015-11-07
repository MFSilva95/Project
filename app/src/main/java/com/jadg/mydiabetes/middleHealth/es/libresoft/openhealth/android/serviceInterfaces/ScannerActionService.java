package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces;

import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Agent;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.RecentDeviceInformation;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IScannerActionService;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.Scanner;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.application.SetEvent;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Values;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.HANDLE;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U16;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.OperationalState;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.Attribute;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.InvalidAttributeException;

import java.util.Iterator;
import java.util.List;

public class ScannerActionService extends IScannerActionService.Stub
{
	public static final String TAG = "ScannerActionService";

	@Override
	public void getScanner(String systemId, List<Scanner> scannerList) throws RemoteException
	{
		Log.d(TAG, "getScanner()");

		Agent agt = RecentDeviceInformation.getAgent(systemId);
		Iterator<Integer> i;

		if (agt == null || scannerList == null)
			return;

		i = agt.getScannerHandlers();
		while(i.hasNext())
			scannerList.add(new Scanner(i.next(), systemId));
	}

	@Override
	public void setScanner(Scanner scanner, boolean enable) throws RemoteException
	{
		Log.d(TAG, "setScanner()");

		HANDLE handle = new HANDLE();
		INT_U16 value = new INT_U16();
		value.setValue(scanner.getHandler());
		handle.setValue(value);
		OperationalState os = new OperationalState();
		if (enable)
			os.setValue(ASN1_Values.OP_STATE_ENABLED);
		else
			os.setValue(ASN1_Values.OP_STATE_DISABLED);

		try
		{
			Attribute attr = new Attribute(Nomenclature.MDC_ATTR_OP_STAT, os);
			SetEvent event = new SetEvent(handle, attr);

			Agent agent = RecentDeviceInformation.getAgent(scanner.getSystemId());
			if(agent == null)
			{
				Log.d(TAG, "setScanner() - agent is null!");
				return;
			}

			agent.sendEvent(event);
		}
		catch (InvalidAttributeException e)
		{
			e.printStackTrace();
		}
	}
}
