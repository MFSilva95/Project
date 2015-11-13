package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IApplicationRegister;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;

public class ApplicationRegisterService extends IApplicationRegister.Stub
{
	public static final String TAG = "ApplicationRegisterServ";

	private RemoteCallbackList<IEventCallback> mRemoteCallbackList;

	public ApplicationRegisterService(RemoteCallbackList<IEventCallback> remoteCallbackList)
	{
		mRemoteCallbackList = remoteCallbackList;
	}

	@Override
	public void registerApplication(IEventCallback eventCallback) throws RemoteException
	{
		Log.d(TAG, "registerApplication()");

		boolean result = mRemoteCallbackList.register(eventCallback);
		Log.d(TAG, "registerApplication() - Registering callback: " + result);
	}

	@Override
	public void unregisterApplication(IEventCallback eventCallback) throws RemoteException
	{
		Log.d(TAG, "unregisterApplication()");

		mRemoteCallbackList.unregister(eventCallback);
	}
}
