package com.jadg.mydiabetes.middleHealth.controller.connections;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.MiddleHealth;

public class MiddleHealthService implements ServiceConnection
{
	private static final String TAG = "MiddleHealthService";

	private Messenger mMessageReceiver;
	private Messenger mMessageSender;

	public MiddleHealthService(Messenger messageReceiver)
	{
		mMessageReceiver = messageReceiver;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service)
	{
		Log.d(TAG, "onServiceConnected()");

		Message msg = Message.obtain(null, MiddleHealth.MSG_REG_CLIENT);
		msg.replyTo = mMessageReceiver;

		mMessageSender = new Messenger(service);
		try
		{
			mMessageSender.send(msg);
		}
		catch (RemoteException e)
		{
			Log.d(TAG, "onServiceConnected() - Unable to register client to service.");
			e.printStackTrace();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name)
	{
		Log.d(TAG, "onServiceDisconnected()");
		mMessageSender = null;
	}
}
