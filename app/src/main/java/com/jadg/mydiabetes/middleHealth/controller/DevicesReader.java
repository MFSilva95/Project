package com.jadg.mydiabetes.middleHealth.controller;

import android.app.Activity;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.controller.connections.ApplicationRegisterConnection;
import com.jadg.mydiabetes.middleHealth.controller.connections.CustomDeviceManagerConnection;

public class DevicesReader
{
	private static final String TAG = "DevicesReader";

	private Activity mActivity;
	private ApplicationRegisterConnection mApplicationRegisterConnection;
	private CustomDeviceManagerConnection mCustomDeviceManagerConnection;

	public DevicesReader(Activity activity)
	{
		mActivity = activity;

		EventCallback eventCallback = new EventCallback(activity);
		mApplicationRegisterConnection = new ApplicationRegisterConnection(eventCallback);
		mCustomDeviceManagerConnection = new CustomDeviceManagerConnection();
	}

	public boolean initialize()
	{
		Log.d(TAG, "initialize()");

		// Initialize application register connection:
		if(!mApplicationRegisterConnection.initialize(mActivity))
			return false;

		// Initialize custom device manager connection:
		if(!mCustomDeviceManagerConnection.initialize(mActivity, false)) // TODO create a settings class to store this value
			return false;

		return true;
	}
	public void shutdown()
	{
		Log.d(TAG, "shutdown()");

		mCustomDeviceManagerConnection.shutdown();
		mApplicationRegisterConnection.shutdown();
	}
}