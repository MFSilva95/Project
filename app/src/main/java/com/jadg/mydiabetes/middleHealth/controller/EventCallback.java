package com.jadg.mydiabetes.middleHealth.controller;

import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.controller.connections.DeviceService;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidMeasure;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;

public class EventCallback extends IEventCallback.Stub
{
	private static final String TAG = "EventCallback";

	private DeviceService mDeviceService;

	public EventCallback(DeviceService deviceService)
	{
		mDeviceService = deviceService;
	}

	@Override
	public void deviceConnected(String systemID) throws RemoteException
	{
		Log.d(TAG, "mCallbacks.deviceConnected()");

		Log.d(TAG, "FROM ACTIVITY: DEVICE CONNECTED!!");
		Log.d(TAG, "MYCLIENT: now reading device...");

		AndroidHealthDevice dev = mDeviceService.getDeviceInfo(systemID);
		Log.d(TAG, "MYCLIENT: powerStatus -> " + dev.getPowerStatus());
		Log.d(TAG, "MYCLIENT: batteryLevel -> " + dev.getBatteryLever());
		Log.d(TAG, "MYCLIENT: macAddress -> " + dev.getMacAddress());
		Log.d(TAG, "MYCLIENT: manufacturer -> " + dev.getManufacturer());
		Log.d(TAG, "MYCLIENT: modelNumber -> " + dev.getModelNumber());
		Log.d(TAG, "MYCLIENT: is11073 -> " + dev.is11073());

		Log.d(TAG, "MYCLIENT: systemTypeIds -> ");
		for(int id : dev.getSystemTypeIds())
			Log.d(TAG, "MYCLIENT: " + id);

		Log.d(TAG, "MYCLIENT: System types -> ");
		for(String name : dev.getSystemTypes())
			Log.d(TAG, "MYCLIENT: " + name);
	}

	@Override
	public void deviceDisconnected(String systemID) throws RemoteException
	{
		Log.d(TAG, "mCallbacks.deviceDisconnected()");
	}

	@Override
	public void deviceChangeStatus(String systemID, String previous, String newState) throws RemoteException
	{
		Log.d(TAG, "mCallbacks.deviceChangeStatus() - State changed from " + previous + " to " + newState);
	}

	@Override
	public void MeasureReceived(String systemID, AndroidMeasure m) throws RemoteException
	{
		Log.d(TAG, "mCallbacks.MeasureReceived()");

		Log.d(TAG, "MYCLIENT: systemID -> " + systemID);
		Log.d(TAG, "MYCLIENT: measureID -> " + m.getMeasureId());
		Log.d(TAG, "MYCLIENT: measureName -> " + m.getMeasureName());
		Log.d(TAG, "MYCLIENT: unitId -> " + m.getUnitId());
		Log.d(TAG, "MYCLIENT: unitName -> " + m.getUnitName());

		Log.d(TAG, "MYCLIENT: values -> ");
		for(double v : m.getValues())
			Log.d(TAG, "MYCLIENT: " + v);

		Log.d(TAG, "MYCLIENT: metricIds -> ");
		for(int v: m.getMetricIds())
			Log.d(TAG, v + " ");

		Log.d(TAG, "MYCLIENT: metricNames -> ");
		for(String n : m.getMetricNames())
			Log.d(TAG, "MYCLIENT: " + n);
	}
}
