package com.jadg.mydiabetes.middleHealth.controller.connections;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.controller.EventCallback;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IApplicationRegister;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces.ApplicationRegisterService;

public class ApplicationRegisterConnection extends AbstractConnection
{
	private static final String TAG = "ApplicationRegisterServ";

	private IApplicationRegister mApplicationRegister;
	private EventCallback mEventCallback;

	public ApplicationRegisterConnection(EventCallback eventCallback)
	{
		mEventCallback = eventCallback;
	}

	public boolean initialize(Context context)
	{
		return super.initialize(context, com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.MiddleHealth.class, ApplicationRegisterService.TAG);
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
