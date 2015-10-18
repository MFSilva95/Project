

package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import com.jadg.mydiabetes.middleHealth.ieee_11073.part_10101.Nomenclature;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.HANDLE;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U16;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.OperationalState;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.Attribute;
import com.jadg.mydiabetes.middleHealth.ieee_11073.part_20601.phd.dim.InvalidAttributeException;

import java.util.Iterator;
import java.util.List;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Agent;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.HealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Measure;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.RecentDeviceInformation;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.Event;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.EventManager;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.InternalEventReporter;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.application.GetPmStoreEvent;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.application.SetEvent;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.utils.ASN1_Values;
import com.jadg.mydiabetes.middleHealth.customDevices.CustomDeviceProvider;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.bluetoothHDP.HDPManager;

public class MiddleHealth extends Service
{
	private HDPManager mHDPManager;
	private CustomDeviceProvider mCustomDeviceProvider;
	private RemoteCallbackList<IEventCallback> mRemoteCallbackList = new RemoteCallbackList<IEventCallback>();


	// -------- Service Overrides --------

	@Override
	public void onCreate()
	{
		Log.d(TAG, "onCreate()");

		super.onCreate();

		// Set default event manager:
		InternalEventReporter.setDefaultEventManager(mEventManager);

		// Create HDP Manager:
		mHDPManager = new HDPManager();
		mHDPManager.initialize(this);

		RecentDeviceInformation.init();

		mCustomDeviceProvider = new CustomDeviceProvider();
		mCustomDeviceProvider.initializeCustomDevices(mEventManager);
	}

	@Override
	public void onDestroy()
	{
		Log.d(TAG, "onDestroy()");

		// Terminate custom device provider:
		mCustomDeviceProvider.stopCustomDevices();

		// Abort all agents and free resources:
		RecentDeviceInformation.abortAgents();

		// Unregister callbacks
		mRemoteCallbackList.kill();

		// Release resources of HDP Manager:
		mHDPManager.shutdown();

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		String action = intent.getAction();
		Log.d(TAG, "onBind() - Intent action: " + action);
		if(action == null)
			return mMessenger.getBinder();

		if(action.equals("IApplicationRegister"))
			return mApplicationRegister;
		else if(action.equals("IDevice"))
			return mDevice;

		return null;
	}


	// -------- Interfaces --------

	private final IApplicationRegister.Stub mApplicationRegister = new IApplicationRegister.Stub()
	{
		@Override
		public void registerApplication(IEventCallback eventCallback) throws RemoteException
		{
			Log.d(TAG, "mApplicationRegister.registerApplication()");

			boolean result = mRemoteCallbackList.register(eventCallback);
			Log.d(TAG, "mApplicationRegister.registerApplication() - Registering callback: " + result);
		}

		@Override
		public void unregisterApplication(IEventCallback eventCallback) throws RemoteException
		{
			Log.d(TAG, "mApplicationRegister.unregisterApplication()");

			mRemoteCallbackList.unregister(eventCallback);
		}
	};
	
	private final IExternalDeviceApplication.Stub mExternalApplication = new IExternalDeviceApplication.Stub()
	{
		@Override
		public void deviceConnected(AndroidHealthDevice hd) throws RemoteException
		{
			Log.d(TAG, "mExternalApplication.deviceConnected()");

			HealthDevice dev = new HealthDevice();
			dev.setSystId(hd.getSystemId());
			dev.setBatteryLevel(hd.getBatteryLever());
			dev.setAddress(hd.getMacAddress());
			dev.setManufacturer(hd.getManufacturer());
			dev.setModel(hd.getModelNumber());
			dev.setPowerStatus(hd.getPowerStatus());
			RecentDeviceInformation.addDevice(dev);
			mEventManager.deviceConnected(hd.getSystemId(), null);
		}

		@Override
		public void deviceDisconnected(String systemId) throws RemoteException
		{
			Log.d(TAG, "mExternalApplication.deviceDisconnected()");

			mEventManager.deviceDisconnected(systemId);
		}

		@Override
		public void deviceChangeStatus(String systemId, String previousState, String newState) throws RemoteException
		{
			Log.d(TAG, "mExternalApplication.deviceChangeStatus()");

			mEventManager.deviceChangeStatus(systemId, previousState, newState);
		}

		@Override
		public void uploadMeasure(String systemId, AndroidMeasure measure) throws RemoteException
		{
			Log.d(TAG, "mExternalApplication.uploadMeasure()");

			final int N = mRemoteCallbackList.beginBroadcast();
			for(int i = 0; i < N; i++)
			{
				try
				{
					mRemoteCallbackList.getBroadcastItem(i).MeasureReceived(systemId, measure);
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
			mRemoteCallbackList.finishBroadcast();
		}
	};

	private final IDevice.Stub mDevice = new IDevice.Stub()
	{
		@Override
		public List<String> runCommand(String systemID,List<String> args) throws RemoteException
		{
			Log.d(TAG, "mDevice.runCommand()");

			return mCustomDeviceProvider.invokeCommand(systemID, args);
		}
		
		@Override
		public AndroidHealthDevice getDeviceInfo(String systemId) throws RemoteException
		{
			Log.d(TAG, "mDevice.getDeviceInfo()");

			HealthDevice dev = RecentDeviceInformation.getHealthDevice(systemId);
			Log.d(TAG, "MYCLIENT: powerStatus -> " + dev.getPowerStatus());
			Log.d(TAG, "MYCLIENT: batteryLevel -> " + dev.getBatteryLevel());
			Log.d(TAG, "MYCLIENT: macAddress -> " + dev.getAddress());
			Log.d(TAG, "MYCLIENT: manufacturer -> " + dev.getManufacturer());
			Log.d(TAG, "MYCLIENT: modelNumber -> " + dev.getModel());
			Log.d(TAG, "MYCLIENT: is11073 -> " + dev.is11073());
			Log.d(TAG, "MYCLIENT: systemTypeIds -> ");

			for(int id : dev.getSystemTypeIds())
				Log.d(TAG, id + " ");

			Log.d(TAG, "MYCLIENT: System types -> ");
			for(String name : dev.getSystemTypes())
				Log.d(TAG, name + " ");

			return new AndroidHealthDevice(
					dev.getPowerStatus(),
					dev.getBatteryLevel(),
					dev.getAddress(),
					dev.getSystId(),
					dev.getManufacturer(),
					dev.getModel(),
					dev.is11073(),
					dev.getSystemTypeIds(),
					dev.getSystemTypes()
			);
		}
	};

	private final IPM_StoreActionService.Stub mPMStoreActionService = new IPM_StoreActionService.Stub()
	{
		@Override
		public void getStorage(String systemId, List<PM_Store> pmStoreList) throws RemoteException
		{
			Log.d(TAG, "mPMStoreActionService.getStorage()");

			Agent agt = RecentDeviceInformation.getAgent(systemId);
			Iterator<Integer> i;

			if (agt == null || pmStoreList == null)
				return;

			i = agt.getPM_StoresHandlers();
			while(i.hasNext())
				pmStoreList.add(new PM_Store(i.next(), systemId));
		}

		@Override
		public void getPM_Store(PM_Store pmstore) throws RemoteException
		{
			Log.d(TAG, "mPMStoreActionService.getPM_Store()");

			Agent agt = RecentDeviceInformation.getAgent(pmstore.getPM_StoreAgentId());
			HANDLE handler = new HANDLE();
			handler.setValue(new INT_U16(new Integer(pmstore.getPM_StoreHandler())));

			Log.d(TAG, "Send Event");
			GetPmStoreEvent event = new GetPmStoreEvent(handler);
			agt.sendEvent(event);
		}
    };

    private final IScannerActionService.Stub mScannerService = new IScannerActionService.Stub()
	{
		@Override
		public void Set(Scanner scanner, boolean enable) throws RemoteException
		{
			Log.d(TAG, "mScannerService.Set()");

			Agent agt = RecentDeviceInformation.getAgent(scanner.getSystemId());
			HANDLE handle = new HANDLE();
			INT_U16 value = new INT_U16();
			value.setValue(scanner.getHandler());
			handle.setValue(value);
			OperationalState os = new OperationalState();
			if (enable)
				os.setValue(ASN1_Values.OP_STATE_ENABLED);
			else
				os.setValue(ASN1_Values.OP_STATE_DISABLED);

			try
			{
				Attribute attr = new Attribute(Nomenclature.MDC_ATTR_OP_STAT, os);
				SetEvent event = new SetEvent(handle, attr);
				agt.sendEvent(event);
			}
			catch (InvalidAttributeException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void getScanner(String systemId, List<Scanner> scannerList) throws RemoteException
		{
			Log.d(TAG, "mScannerService.getScanner()");

			Agent agt = RecentDeviceInformation.getAgent(systemId);
			Iterator<Integer> i;

			if (agt == null || scannerList == null)
				return;

			i = agt.getScannerHandlers();
			while(i.hasNext())
				scannerList.add(new Scanner(i.next(), systemId));
		}
	};
	
	private final IAgentActionService.Stub mAgentActionService = new IAgentActionService.Stub()
	{
			@Override
			public void getService(String systemId) throws RemoteException
			{
				Log.d(TAG, "mAgentActionService.getService()");
			}

			@Override
			public void sendEvent(String systemId, int eventType) throws RemoteException
			{
				Log.d(TAG, "mAgentActionService.sendEvent()");

				Agent agt = RecentDeviceInformation.getAgent(systemId);
				if (agt==null)
					return;
				agt.sendEvent(new Event(eventType));
			}

			@Override
			public void setService(String systemId) throws RemoteException
			{
				Log.d(TAG, "mAgentActionService.setService()");
			}
	};


	// -------- Event Manager --------

	private final EventManager mEventManager = new EventManager()
	{
		@Override
		public void deviceConnected(String systemId, HealthDevice device)
		{
			Log.d(TAG, "mEventManager.deviceConnected()");

			if(device != null)
				RecentDeviceInformation.addDevice(device);

			sendMessage(Message.obtain(null, MSG_DEVICE_CONNECTED));
			
			int size = mRemoteCallbackList.beginBroadcast();
			for(int i = 0; i < size; i++)
			{
				try
				{
					mRemoteCallbackList.getBroadcastItem(i).deviceConnected(systemId);
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
			mRemoteCallbackList.finishBroadcast();
		}

		@Override
		public void deviceDisconnected(String systemId)
		{
			Log.d(TAG, "mEventManager.deviceDisconnected()");

			sendMessage(Message.obtain(null, MSG_DEVICE_DISCONNECTED));

			final int size = mRemoteCallbackList.beginBroadcast();
			for(int i = 0; i < size; i++)
			{
				try
				{
					mRemoteCallbackList.getBroadcastItem(i).deviceDisconnected(systemId);
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
			mRemoteCallbackList.finishBroadcast();
		}

		@Override
		public void deviceChangeStatus(String system_id, String prevState, String newState)
		{
			Log.d(TAG, "mEventManager.deviceChangeStatus()");

			int size = mRemoteCallbackList.beginBroadcast();
			for(int i = 0; i < size; i++)
			{
				try
				{
					mRemoteCallbackList.getBroadcastItem(i).deviceChangeStatus(system_id, prevState, newState);
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
			mRemoteCallbackList.finishBroadcast();
		}

		@Override
		public void receivedMeasure(String systemId, Measure measure)
		{
			Log.d(TAG, "mEventManager.receivedMeasure()");

			if (systemId ==null)
			{
				Log.d(TAG, "mEventManager.receivedMeasure() - systemId was null");
				return;
			}

			AndroidMeasure androidMeasure = new AndroidMeasure(measure.getMeasureId(), measure.getMeasureName(), measure.getUnitId(), measure.getUnitName(), measure.getTimestamp(), measure.getValues(), measure.getMetricIds(), measure.getMetricNames());
			Log.d(TAG, "mEventManager.receivedMeasure() - Sending android measure");
			Message message = Message.obtain(null, MSG_MEASURE_RECEIVED);
			message.getData().putParcelable("data", androidMeasure);
			sendMessage(message);

			int size = mRemoteCallbackList.beginBroadcast();
			for(int i = 0; i < size; i++)
			{
				try
				{
					mRemoteCallbackList.getBroadcastItem(i).MeasureReceived(systemId, androidMeasure);
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
			mRemoteCallbackList.finishBroadcast();
		}
	};


	// -------- Messenger --------

	public static final int MSG_REG_CLIENT = 200;
	public static final int MSG_UNREG_CLIENT = 201;
	public static final int MSG_DEVICE_CONNECTED = 203;
	public static final int MSG_MEASURE_RECEIVED = 204;
	public static final int MSG_DEVICE_DISCONNECTED = 205;
	public static final int MSG_REGISTER_CALLBACK = 206;
	private Messenger mClient;
	private static final String TAG = "MiddleHealth";

	// Handles events sent by {@link HealthHDPActivity}.
	private class IncomingHandler extends Handler
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case MSG_REG_CLIENT:
					Log.d(TAG, "Client registered!");
					mClient = msg.replyTo;
					break;

				case MSG_UNREG_CLIENT:
					mClient = null;
					break;

				case MSG_REGISTER_CALLBACK:
					Log.d(TAG, "Registering callback: " + true);
					break;

				default:
					super.handleMessage(msg);
			}
		}
	}

	final Messenger mMessenger = new Messenger(new IncomingHandler());

	private void sendMessage(Message message)
	{
		if (mClient == null)
		{
			Log.d(TAG, "No clients registered.");
			return;
		}

		try
		{
			mClient.send(message);
		}
		catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}
}