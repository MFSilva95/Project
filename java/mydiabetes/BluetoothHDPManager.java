package mydiabetes;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class BluetoothHDPManager
{
	private static final String sTAG = "BluetoothHealthActivity";

	private static final int sHEALTH_PROFILE_BLOOD_MONITOR = 0x1007;
	private static final int sHEALTH_PROFILE_SCALE = 0x100f;

	private static final int sREQUEST_ENABLE_BT = 1;

	private final Handler mIncomingHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case BluetoothHDPService.sSTATUS_HEALTH_APP_REGISTER:
					Log.d(sTAG, "handleMessage() - Application registered.");
					break;

				case BluetoothHDPService.sSTATUS_HEALTH_APP_UNREGISTER:
					Log.d(sTAG, "handleMessage() - Application unregistered.");
					break;

				case BluetoothHDPService.sSTATUS_READ_DATA:
					Log.d(sTAG, "handleMessage() - Reading data.");
					break;

				case BluetoothHDPService.sSTATUS_READ_DATA_DONE:
					Log.d(sTAG, "handleMessage() - Reading data done.");
					break;

				case BluetoothHDPService.sSTATUS_CREATE_CHANNEL:
					Log.d(sTAG, "handleMessage() - Channel created.");
					break;

				case BluetoothHDPService.sSTATUS_DESTROY_CHANNEL:
					Log.d(sTAG, "handleMessage() - Channel destroyed.");
					break;

				case BluetoothHDPService.sMSG_INITIALIZE_SERVICE:
					Log.d(sTAG, "handleMessage() - Service initialized.");
					registerHealthApp(sHEALTH_PROFILE_BLOOD_MONITOR);
					break;

				default:
					super.handleMessage(msg);
			}
		}
	};

	private Activity mActivity;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice[] mAllBondedDevices;
	private BluetoothDevice mDevice;
	private int mDeviceIndex = 0;
	private Messenger mHealthService;
	private boolean mHealthServiceBound;
	private final Messenger mMessenger = new Messenger(mIncomingHandler);

	public BluetoothHDPManager(Activity activity)
	{
		mActivity = activity;
	}

	public void onCreate()
	{
		// Check for Bluetooth availability on the Android platform:
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null)
		{
			Log.d(sTAG, "Bluetooth adapter is not available!");
			mActivity.finish();
			return;
		}

		mHealthServiceBound = false;

		mActivity.registerReceiver(mReceiver, initIntentFilter());
	}

	public void onDestroy()
	{
		if(mHealthServiceBound)
			mActivity.unbindService(mConnection);

		mActivity.unregisterReceiver(mReceiver);
	}

	public void onStart()
	{
		// If Bluetooth is not on, request that it be enabled.
		if (!mBluetoothAdapter.isEnabled())
		{
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			mActivity.startActivityForResult(enableIntent, sREQUEST_ENABLE_BT);
		}
		else
		{
			initialize();
		}
	}

	public void onActivityResult(int requestCode, int resultCode)
	{
		switch (requestCode)
		{
			case sREQUEST_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK)
				{
					initialize();
				}
				else
				{
					mActivity.finish();
					return;
				}
				break;
		}
	}

	private ServiceConnection mConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mHealthServiceBound = true;
			Message msg = Message.obtain(null, BluetoothHDPService.sMSG_REGISTER_CLIENT);
			msg.replyTo = mMessenger;

			mHealthService = new Messenger(service);
			try
			{
				mHealthService.send(msg);
			}
			catch (RemoteException e)
			{
				Log.d(sTAG, "onServiceConnected() - Unable to register client to service.");
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName name)
		{
			mHealthService = null;
			mHealthServiceBound = false;
		}
	};

	public void setDevice(int position)
	{
		mDevice = this.mAllBondedDevices[position];
		mDeviceIndex = position;
	}

	public void registerHealthApp(int dataType)
	{
		sendMessage(BluetoothHDPService.sMSG_REGISTER_HEALTH_APP, dataType);
	}

	public void unregisterHealthApp()
	{
		sendMessage(BluetoothHDPService.sMSG_UNREGISTER_HEALTH_APP, 0);
	}

	private void connectChannel()
	{
		sendMessageWithDevice(BluetoothHDPService.sMSG_CONNECT_CHANNEL);
	}

	private void disconnectChannel()
	{
		sendMessageWithDevice(BluetoothHDPService.sMSG_DISCONNECT_CHANNEL);
	}

	private void initialize()
	{
		// Starts health service.
		Intent intent = new Intent(mActivity, BluetoothHDPService.class);
		mActivity.startService(intent);
		if(!mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE))
		{
			Log.d(sTAG, "initialize() - Failed to bind service.");
			return;
		}
	}

	// Intent filter and broadcast receive to handle Bluetooth on event.
	private IntentFilter initIntentFilter()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		return filter;
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
			{
				if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) == BluetoothAdapter.STATE_ON)
				{
					initialize();
				}
			}
		}
	};

	private void sendMessage(int what, int value)
	{
		if (mHealthService == null)
		{
			Log.d(sTAG, "sendMessage() - Health Service not connected.");
			return;
		}

		try
		{
			mHealthService.send(Message.obtain(null, what, value, 0));
		}
		catch (RemoteException e)
		{
			Log.d(sTAG, "sendMessage() - Unable to reach service.");
			e.printStackTrace();
		}
	}

	// Sends an update message, along with an HDP BluetoothDevice object, to
	// {@link BluetoothHDPService}.  The BluetoothDevice object is needed by the channel creation
	// method.
	private void sendMessageWithDevice(int what)
	{
		if (mHealthService == null)
		{
			Log.d(sTAG, "sendMessageWithDevice() - Health Service not connected.");
			return;
		}

		try
		{
			mHealthService.send(Message.obtain(null, what, mDevice));
		}
		catch (RemoteException e)
		{
			Log.d(sTAG, "sendMessageWithDevice() - Unable to reach service.");
			e.printStackTrace();
		}
	}

	/**
	 * Dialog to display a list of bonded Bluetooth devices for user to select from.  This is
	 * needed only for channel connection initiated from the application.
	 */
	/*public static class SelectDeviceDialogFragment extends DialogFragment
	{
		public static SelectDeviceDialogFragment newInstance(String[] names, int position)
		{
			SelectDeviceDialogFragment frag = new SelectDeviceDialogFragment();
			Bundle args = new Bundle();
			args.putStringArray("names", names);
			args.putInt("position", position);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			String[] deviceNames = getArguments().getStringArray("names");
			int position = getArguments().getInt("position", -1);
			if (position == -1) position = 0;
			return new AlertDialog.Builder(getActivity())
					.setTitle(R.string.select_device)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									((BluetoothHDPActivity) getActivity()).connectChannel();
								}
							})
					.setSingleChoiceItems(deviceNames, position,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									((BluetoothHDPActivity) getActivity()).setDevice(which);
								}
							}
					)
					.create();
		}
	}*/

	private String[] getDeviceNames()
	{
		mAllBondedDevices = mBluetoothAdapter.getBondedDevices().toArray(new BluetoothDevice[0]);
		int deviceCount = mAllBondedDevices.length;
		String[] deviceNames = new String[deviceCount];

		if (mAllBondedDevices.length > 0)
		{

			if (mDeviceIndex < deviceCount)
				mDevice = mAllBondedDevices[mDeviceIndex];
			else
			{
				mDeviceIndex = 0;
				mDevice = mAllBondedDevices[0];
			}

			int i = 0;
			for (BluetoothDevice device : mAllBondedDevices)
				deviceNames[i++] = device.getName();

			//SelectDeviceDialogFragment deviceDialog = SelectDeviceDialogFragment.newInstance(deviceNames, mDeviceIndex);
			//deviceDialog.show(getFragmentManager(), "deviceDialog");
		}

		return deviceNames;
	}

	private static void log(String message)
	{
		System.out.println(message);
	}

	private static void error(String message)
	{
		System.err.println(message);
	}
}
