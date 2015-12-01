package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.Log;

import pt.it.porto.mydiabetes.middleHealth.bluetoothHDP.HDPManager;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.RecentDeviceInformation;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces.*;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.events.InternalEventReporter;

public class MiddleHealth extends Service
{
	public static final String TAG = "MiddleHealth";

	private RemoteCallbackList<IEventCallback> mRemoteCallbackList;
	private HDPManager mHDPManager;
	private EventManager mEventManager;
	private AgentActionService mAgentActionService;
	private ApplicationRegisterService mApplicationRegisterService;
	private CustomDeviceManagerService mCustomDeviceProviderService;
	private DeviceService mDeviceService;
	private ExternalDeviceApplicationService mExternalDeviceApplicationService;
	private PmStoreActionService mPmStoreActionService;
	private ScannerActionService mScannerActionService;

	public MiddleHealth()
	{
		mRemoteCallbackList = new RemoteCallbackList<>();
		mHDPManager = new HDPManager();
		mEventManager = new EventManager(mRemoteCallbackList);
		mAgentActionService = new AgentActionService();
		mApplicationRegisterService = new ApplicationRegisterService(mRemoteCallbackList);
		mCustomDeviceProviderService = new CustomDeviceManagerService();
		mDeviceService = new DeviceService();
		mExternalDeviceApplicationService = new ExternalDeviceApplicationService(mEventManager);
		mPmStoreActionService = new PmStoreActionService();
		mScannerActionService = new ScannerActionService();
	}

	@Override
	public void onCreate()
	{
		Log.d(TAG, "onCreate()");

		super.onCreate();

		// Set default event manager:
		InternalEventReporter.setDefaultEventManager(mEventManager);

		// Initialize recent device information class:
		RecentDeviceInformation.init();

		// Initialize HDP Manager:
		mHDPManager.initialize(this);

		// Initialize custom device provider service:
		mCustomDeviceProviderService.initialize(mEventManager);
	}

	@Override
	public void onDestroy()
	{
		Log.d(TAG, "onDestroy()");

		// Shutdown custom device provider service:
		mCustomDeviceProviderService.shutdown();

		// Release resources of HDP Manager:
		mHDPManager.shutdown();

		// Abort all agents and free resources:
		RecentDeviceInformation.abortAgents();

		// Unregister callbacks
		mRemoteCallbackList.kill();

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		String action = intent.getAction();
		Log.d(TAG, "onBind() - Intent action: " + action);

		switch (action)
		{
			case AgentActionService.TAG:
				return mAgentActionService;

			case ApplicationRegisterService.TAG:
				return mApplicationRegisterService;

			case CustomDeviceManagerService.TAG:
				return mCustomDeviceProviderService;

			case DeviceService.TAG:
				return mDeviceService;

			case ExternalDeviceApplicationService.TAG:
				return mExternalDeviceApplicationService;

			case PmStoreActionService.TAG:
				return mPmStoreActionService;

			case ScannerActionService.TAG:
				return mScannerActionService;

			default:
				return null;
		}
	}
}