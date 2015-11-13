package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces;

import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.HealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.RecentDeviceInformation;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IDevice;

import java.util.List;

public class DeviceService extends IDevice.Stub
{
	public static final String TAG = "DeviceService";

	@Override
	public List<String> runCommand(String systemID, List<String> args) throws RemoteException
	{
		Log.d(TAG, "mDevice.runCommand() - warning: not implemented!");

		return null;
	}

	@Override
	public AndroidHealthDevice getDeviceInfo(String systemId) throws RemoteException
	{
		Log.d(TAG, "mDevice.getDeviceInfo()");

		HealthDevice dev = RecentDeviceInformation.getHealthDevice(systemId);
		Log.d(TAG, "MYCLIENT: powerStatus -> " + dev.getPowerStatus());
		Log.d(TAG, "MYCLIENT: batteryLevel -> " + dev.getBatteryLevel());
		Log.d(TAG, "MYCLIENT: macAddress -> " + dev.getAddress());
		Log.d(TAG, "MYCLIENT: manufacturer -> " + dev.getManufacturer());
		Log.d(TAG, "MYCLIENT: modelNumber -> " + dev.getModel());
		Log.d(TAG, "MYCLIENT: is11073 -> " + dev.is11073());
		Log.d(TAG, "MYCLIENT: systemTypeIds -> ");

		for(int id : dev.getSystemTypeIds())
			Log.d(TAG, id + " ");

		Log.d(TAG, "MYCLIENT: System types -> ");
		for(String name : dev.getSystemTypes())
			Log.d(TAG, name + " ");

		return new AndroidHealthDevice(
				dev.getPowerStatus(),
				dev.getBatteryLevel(),
				dev.getAddress(),
				dev.getSystId(),
				dev.getManufacturer(),
				dev.getModel(),
				dev.is11073(),
				dev.getSystemTypeIds(),
				dev.getSystemTypes()
		);
	}
}
