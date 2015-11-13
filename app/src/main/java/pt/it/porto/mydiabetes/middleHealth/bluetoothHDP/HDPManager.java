package pt.it.porto.mydiabetes.middleHealth.bluetoothHDP;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.bluetooth.BluetoothHealthCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.Agent;

public class HDPManager
{
	private static final String TAG = "HDPManager";

	private static final int SCALE = 0x100f;
	private static final int BLOOD_MONITOR = 0x1007;

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothHealth mBluetoothHealth;
	private BluetoothHealthAppConfiguration mBloodMonitorConfiguration;
	private BluetoothHealthAppConfiguration mScaleConfiguration;

	public boolean initialize(Context context)
	{
		Log.d(TAG, "initialize()");

		// Get default bluetooth adapter:
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if(!mBluetoothAdapter.getProfileProxy(context, mBluetoothServiceListener, BluetoothProfile.HEALTH))
		{
			Log.d(TAG, "getProfileProxy() failed!");
			return false;
		}

		return true;
	}

	public void shutdown()
	{
		Log.d(TAG, "shutdown()");

		if(mBluetoothHealth != null)
			mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEALTH, mBluetoothHealth);
	}

	private final BluetoothProfile.ServiceListener mBluetoothServiceListener = new BluetoothProfile.ServiceListener()
	{
		public void onServiceConnected(int profile, BluetoothProfile proxy)
		{
			Log.d(TAG, "mBluetoothServiceListener.onServiceConnected()");

			if (profile == BluetoothProfile.HEALTH)
			{
				mBluetoothHealth = (BluetoothHealth) proxy;

				if(!mBluetoothHealth.registerSinkAppConfiguration(TAG, BLOOD_MONITOR, mHealthCallback) ||
						!mBluetoothHealth.registerSinkAppConfiguration(TAG, SCALE, mHealthCallback))
				Log.d(TAG, "mBluetoothServiceListener.onServiceConnected() - Failed to register devices");
			}
		}

		@Override
		public void onServiceDisconnected(int profile)
		{
			Log.d(TAG, "mBluetoothServiceListener.onServiceDisconnected()");

			if(profile == BluetoothProfile.HEALTH)
			{
				mBluetoothHealth.unregisterAppConfiguration(mScaleConfiguration);
				mBluetoothHealth.unregisterAppConfiguration(mBloodMonitorConfiguration);
				mBluetoothHealth = null;
			}
		}
	};

	private final BluetoothHealthCallback mHealthCallback = new BluetoothHealthCallback()
	{
		public void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration config, int status)
		{
			Log.d(TAG, "mHealthCallback.onHealthAppConfigurationStatusChange() - status: " + status);
			if(status == BluetoothHealth.APP_CONFIG_REGISTRATION_FAILURE)
			{
				switch (config.getDataType())
				{
					case BLOOD_MONITOR:
						mBloodMonitorConfiguration = null;
						break;

					case SCALE:
						mScaleConfiguration = null;
						break;
				}

				Log.d(TAG, "mHealthCallback.onHealthAppConfigurationStatusChange() - Failed to register a device!");
			}
			else if (status == BluetoothHealth.APP_CONFIG_REGISTRATION_SUCCESS)
			{
				switch (config.getDataType())
				{
					case BLOOD_MONITOR:
						mBloodMonitorConfiguration = config;
						break;

					case SCALE:
						mScaleConfiguration = config;
						break;
				}
			}
		}

		public void onHealthChannelStateChange(BluetoothHealthAppConfiguration config, BluetoothDevice device, int prevState, int newState, ParcelFileDescriptor fd, int channelId)
		{
			Log.d(TAG, "mHealthCallback.onHealthChannelStateChange()");

			if(prevState == BluetoothHealth.STATE_CHANNEL_DISCONNECTED && newState == BluetoothHealth.STATE_CHANNEL_CONNECTED)
			{
				try
				{
					Agent a = new Agent();
					a.addChannel( new BluetoothHDPChannel(fd));
					a.setAddress(device.getAddress());

					Log.d(TAG, "mHealthCallback.onHealthChannelStateChange() - Added a new Agent!");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	};
}