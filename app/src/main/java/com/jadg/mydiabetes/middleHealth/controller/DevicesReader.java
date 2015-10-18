package com.jadg.mydiabetes.middleHealth.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DevicesReader
{
	private static final String TAG = "DevicesReader";

	private Activity mActivity;
	private ApplicationRegisterService mApplicationRegisterConnection;

	public DevicesReader(Activity activity)
	{
		mActivity = activity;
	}

	public boolean initialize()
	{
		Log.d(TAG, "initialize()");

		if(!initializeApplicationRegisterService())
			return false;

		return true;
	}
	public void shutdown()
	{
		Log.d(TAG, "shutdown()");

		shutdownApplicationRegisterService();
	}

	private boolean initializeApplicationRegisterService()
	{
		EventCallback eventCallback = new EventCallback(mActivity);
		mApplicationRegisterConnection = new ApplicationRegisterService(eventCallback);

		Intent applicationRegisterIntent = new Intent(mActivity, com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.MiddleHealth.class);
		applicationRegisterIntent.setAction("IApplicationRegister");
		if(!mActivity.bindService(applicationRegisterIntent, mApplicationRegisterConnection, Context.BIND_AUTO_CREATE))
			return false;

		return true;
	}
	private void shutdownApplicationRegisterService()
	{
		mActivity.unbindService(mApplicationRegisterConnection);
	}
}