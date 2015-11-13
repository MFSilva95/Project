package com.jadg.mydiabetes.middleHealth.controller.connections;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.ICustomDeviceManager;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces.CustomDeviceManagerService;

public class CustomDeviceManagerConnection extends AbstractConnection
{
	public static final String TAG = "CustomDeviceManagerConn";

	private ICustomDeviceManager mCustomDeviceManager;
	private boolean mEraseAllOptionValue;

	public boolean initialize(Context context, boolean eraseAllOptionValue)
	{
		if(!super.initialize(context, com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.MiddleHealth.class, CustomDeviceManagerService.TAG))
			return false;

		mEraseAllOptionValue = eraseAllOptionValue;

		return true;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		Log.d(TAG, "onServiceConnected()");

		mCustomDeviceManager = ICustomDeviceManager.Stub.asInterface(service);

		setOptions();
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		Log.d(TAG, "onServiceDisconnected()");
		mCustomDeviceManager = null;
	}

	private boolean setOptions()
	{
		try
		{
			mCustomDeviceManager.setEraseAllDataOption(mEraseAllOptionValue);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
			Log.d(TAG, "setOptions() - Failed to set options' values!");
			return false;
		}

		return true;
	}
}
