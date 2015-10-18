package com.jadg.mydiabetes.middleHealth.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Messenger;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.controller.connections.ApplicationRegisterService;
import com.jadg.mydiabetes.middleHealth.controller.connections.DeviceService;
import com.jadg.mydiabetes.middleHealth.controller.connections.MiddleHealthService;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.MiddleHealth;

public class DevicesReader
{
	private static final String TAG = "DevicesReader";

	private Activity mActivity;
	private MiddleHealthService mMiddleHealthConnection;
	private ApplicationRegisterService mApplicationRegisterConnection;
	private DeviceService mDeviceConnection;

	public DevicesReader(Activity activity)
	{
		mActivity = activity;
	}

	public boolean initialize()
	{
		Log.d(TAG, "initialize()");

		if(!initializeDeviceInterface())
			return false;

		if(!initializeApplicationRegisterInterface(mDeviceConnection))
			return false;

		return true;
	}
	public void shutdown()
	{
		Log.d(TAG, "shutdown()");

		mActivity.unbindService(mDeviceConnection);
		mActivity.unbindService(mApplicationRegisterConnection);
	}

	private boolean initializeDeviceInterface()
	{
		mDeviceConnection = new DeviceService();

		Intent deviceInterface = new Intent(mActivity, com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.MiddleHealth.class);
		deviceInterface.setAction("IDevice");
		if(!mActivity.bindService(deviceInterface, mDeviceConnection, Context.BIND_AUTO_CREATE))
			return false;

		return true;
	}

	private boolean initializeApplicationRegisterInterface(DeviceService deviceConnection)
	{
		EventCallback eventCallback = new EventCallback(deviceConnection);
		mApplicationRegisterConnection = new ApplicationRegisterService(eventCallback);

		Intent applicationRegisterIntent = new Intent(mActivity, com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.MiddleHealth.class);
		applicationRegisterIntent.setAction("IApplicationRegister");
		if(!mActivity.bindService(applicationRegisterIntent, mApplicationRegisterConnection, Context.BIND_AUTO_CREATE))
			return false;

		return true;
	}
}
