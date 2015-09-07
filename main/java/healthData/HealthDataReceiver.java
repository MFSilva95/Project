package healthData;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class HealthDataReceiver
{
	private static final String sTAG = "HealthDataReceiver";

	private Activity mActivity;
	private IApplicationRegister mApplicationRegister;
	private IDevice mDevice;
	private HealthDataEventCallbacks mEventCallbacks;

	public HealthDataReceiver(Activity activity)
	{
		mActivity = activity;
		mEventCallbacks = new HealthDataEventCallbacks();
	}

	public boolean initialize()
	{
		Intent intent = new Intent(mActivity, IApplicationRegister.class);
		if(!mActivity.bindService(intent, mApplicationRegisterConnection, Context.BIND_AUTO_CREATE))
		{
			Log.d(sTAG, "initialize() - Failed to bind service with application register connection.");
			return false;
		}

		Intent intent2 = new Intent(mActivity, IDevice.class);
		if(!mActivity.bindService(intent2, mDeviceConnection, Context.BIND_AUTO_CREATE))
		{
			Log.d(sTAG, "initialize() - Failed to bind service with device connection.");
			return false;
		}

		return true;
	}

	private ServiceConnection mApplicationRegisterConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			Log.d(sTAG, "mApplicationRegisterConnection.onServiceConnected()");

			mApplicationRegister = IApplicationRegister.Stub.asInterface(service);

			try
			{
				mApplicationRegister.registerApplication(mEventCallbacks);
			}
			catch (RemoteException e)
			{
				Log.d(sTAG, "onServiceConnected() - Failed to register application.");
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mApplicationRegister = null;
		}
	};

	private ServiceConnection mDeviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			Log.d(sTAG, "mDeviceConnection.onServiceConnected()");

			mDevice = IDevice.Stub.asInterface(service);
			mEventCallbacks.setDevice(mDevice);
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mEventCallbacks.setDevice(null);
			mDevice = null;
		}
	};
}
