package com.jadg.mydiabetes.middleHealth.controller.connections;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IDevice;

public class DeviceService implements ServiceConnection
{
	private static final String TAG = "DeviceService";

	private IDevice mDevice;

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		Log.d(TAG, "onServiceConnected()");

		mDevice = IDevice.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		Log.d(TAG, "onServiceDisconnected()");

		mDevice = null;
	}

	public AndroidHealthDevice getDeviceInfo(String systemId) throws RemoteException
	{
		Log.d(TAG, "getDeviceInfo()");

		return mDevice.getDeviceInfo(systemId);
	}
}
