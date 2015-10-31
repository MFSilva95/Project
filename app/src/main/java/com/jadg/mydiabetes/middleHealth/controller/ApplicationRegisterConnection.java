package com.jadg.mydiabetes.middleHealth.controller;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.controller.EventCallback;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IApplicationRegister;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;

public class ApplicationRegisterConnection implements ServiceConnection
{
	private static final String TAG = "ApplicationRegisterServ";

	private IApplicationRegister mApplicationRegister;
	private EventCallback mEventCallback;

	public ApplicationRegisterConnection(EventCallback eventCallback)
	{
		mEventCallback = eventCallback;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		Log.d(TAG, "onServiceConnected()");

		mApplicationRegister = IApplicationRegister.Stub.asInterface(service);

		registerApplication(mEventCallback);
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		Log.d(TAG, "onServiceDisconnected()");
		mApplicationRegister = null;
	}

	public boolean registerApplication(IEventCallback eventCallback)
	{
		try
		{
			mApplicationRegister.registerApplication(eventCallback);
		}
		catch (RemoteException e)
		{
			Log.d(TAG, "registerApplication() - Unable to register application.");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean unregisterApplication(IEventCallback eventCallback)
	{
		try
		{
			mApplicationRegister.unregisterApplication(eventCallback);
		}
		catch (RemoteException e)
		{
			Log.d(TAG, "unregisterApplication() - Unable to unregister application.");
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
