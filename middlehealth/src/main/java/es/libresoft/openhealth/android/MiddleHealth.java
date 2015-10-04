

package es.libresoft.openhealth.android;

import ieee_11073.part_10101.Nomenclature;
import ieee_11073.part_20601.asn1.HANDLE;
import ieee_11073.part_20601.asn1.INT_U16;
import ieee_11073.part_20601.asn1.OperationalState;
import ieee_11073.part_20601.phd.dim.Attribute;
import ieee_11073.part_20601.phd.dim.InvalidAttributeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.libresoft.openhealth.Agent;
import es.libresoft.openhealth.HealthDevice;
import es.libresoft.openhealth.Measure;
import es.libresoft.openhealth.RecentDeviceInformation;
import es.libresoft.openhealth.events.Event;
import es.libresoft.openhealth.events.EventManager;
import es.libresoft.openhealth.events.InternalEventReporter;
import es.libresoft.openhealth.events.application.GetPmStoreEvent;
import es.libresoft.openhealth.events.application.SetEvent;
import es.libresoft.openhealth.utils.ASN1_Values;
import CustomDevices.CustomDeviceProvider;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import bluetoothHDP.HDPManager;

public class MiddleHealth extends Service{
	
	private HDPManager mHDPmanager;
	private CustomDeviceProvider cdProvider;
	private final RemoteCallbackList<IEventCallback> mCallbacks = new RemoteCallbackList<IEventCallback>(); 
	
	public static final String droidEvent = "es.libresoft.openhealth.android.DRDROID_SERVICE";
	
	
	
	
	/************************************************************
	 * Binder
	 ************************************************************/
	@Override
	public IBinder onBind(Intent arg0) {
		// Select the interface to return
        if (IApplicationRegister.class.getName().equals(arg0.getAction())) {
            return mAppRegister;
        } else if (IDevice.class.getName().equals(arg0.getAction())){
        	return mDevice;
        }else if (IExternalDeviceApplication.class.getName().equals(arg0.getAction())){
        	return mExternalApplication;	
        }else if (IPM_StoreActionService.class.getName().equals(arg0.getAction())){
        	return mPMactionService;	
        }else if (IScannerActionService.class.getName().equals(arg0.getAction())){
        	return mScannerService;
        }else if (IAgentActionService.class.getName().equals(arg0.getAction())){
        	return mAgentActionService;	
        } else return mMessenger.getBinder();
	}
	
	
	
	
	
	/************************************************************
	 * Android callbacks
	 ************************************************************/
	@Override
	public void onCreate(){
		InternalEventReporter.setDefaultEventManager(ieManager);
		super.onCreate();
		System.out.println("MiddleHealth Service created!");
	}
	
	
	
	@Override
	public int onStartCommand(Intent intent,int Flags, int startId) {
		mHDPmanager = new HDPManager(this);
		RecentDeviceInformation.init();
		cdProvider = new CustomDeviceProvider(this.getApplicationContext());
		cdProvider.initCustomDevices(ieManager);
		System.out.println("Service started!");
		return super.onStartCommand(intent,Flags, startId);
	}
	
	public void onPause(){
		System.out.println("Service paused!");
	}

	public void onResume(){
		System.out.println("Service resumed!");
	}

	public void onStop(){
		System.out.println("Service Stopped!");
	}

	public void onDestroy(){
		//TODO: terminate HdpManager gracefully
		
		//abort all agents and free resources
		RecentDeviceInformation.abortAgents();
		
		//terminate custom Devices
		cdProvider.stopCustomDevices();
		
		//unregister callbacks
		mCallbacks.kill();
		super.onDestroy();
		System.out.println("Service destroyed");
	}
	
	
	
	
	
	
	
	
	
	
	/************************************************************
	 * All interface definitions
	 ************************************************************/
	private final IApplicationRegister.Stub mAppRegister = new IApplicationRegister.Stub() {

		@Override
		public void registerApplication(IEventCallback mc) throws RemoteException {
			mCallbacks.register(mc);
			
		}

		@Override
		public void unregisterApplication(IEventCallback mc) throws RemoteException {
			mCallbacks.unregister(mc);
			
		}
	};
	
	private final IExternalDeviceApplication.Stub mExternalApplication = new IExternalDeviceApplication.Stub() {
		
		@Override
		public void uploadMeasure(String system_id,AndroidMeasure m) throws RemoteException {
			final int N = mCallbacks.beginBroadcast();
			for(int i = 0; i < N; i++){
				try {
					mCallbacks.getBroadcastItem(i).MeasureReceived(system_id, m );
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			mCallbacks.finishBroadcast();
			
		}
		
		@Override
		public void deviceDisconnected(String systID) throws RemoteException {
			ieManager.deviceDisconnected(systID);
			
		}
		
		@Override
		public void deviceConnected(AndroidHealthDevice hd) throws RemoteException {
			HealthDevice dev = new HealthDevice();
			dev.setSystId(hd.getSystemId());
			dev.setBatteryLevel(hd.getBatteryLever());
			dev.setAddress(hd.getMacAddress());
			dev.setManufacturer(hd.getManufacturer());
			dev.setModel(hd.getModelNumber());
			dev.setPowerStatus(hd.getPowerStatus());
			RecentDeviceInformation.addDevice(dev);
			ieManager.deviceConnected(hd.getSystemId(), null);
			
		}
		
		@Override
		public void deviceChangeStatus(String sysId,String prevState,String newState) throws RemoteException {
			ieManager.deviceChangeStatus(sysId, prevState, newState);
			
		}
	};
	private final IDevice.Stub mDevice = new IDevice.Stub() {
		
		@Override
		public List<String> runCommand(String systemID,List<String> args) throws RemoteException {
			return cdProvider.invokeCommand(systemID, args);
		}
		
		@Override
		public AndroidHealthDevice getDeviceInfo(String systemId) throws RemoteException {
			HealthDevice dev = RecentDeviceInformation.getHealthDevice(systemId);
			System.out.println("MYCLIENT: powerStatus -> " + dev.getPowerStatus());
			System.out.println("MYCLIENT: batteryLevel -> " + dev.getBatteryLevel());
			System.out.println("MYCLIENT: macAddress -> " + dev.getAddress());
			System.out.println("MYCLIENT: manufacturer -> " + dev.getManufacturer());
			System.out.println("MYCLIENT: modelNumber -> " + dev.getModel());
			System.out.println("MYCLIENT: is11073 -> " + dev.is11073());
			System.out.println("MYCLIENT: systemTypeIds -> ");
			for(int id : dev.getSystemTypeIds()){
				System.out.print(id + " ");
			}
			System.out.println();
			System.out.print("MYCLIENT: System types -> ");
			for(String name : dev.getSystemTypes()){
				System.out.print(name + " ");
			}
			System.out.println();
			return new AndroidHealthDevice(
					dev.getPowerStatus(),
					dev.getBatteryLevel(),
					dev.getAddress(),
					dev.getSystId(),
					dev.getManufacturer(),
					dev.getModel(),
					dev.is11073(),
					dev.getSystemTypeIds(),
					dev.getSystemTypes());
		}
	};
	
	
	private final IPM_StoreActionService.Stub mPMactionService = new IPM_StoreActionService.Stub() {

		@Override
		public void getStorage(String systemId, List<PM_Store> pmStoreList) throws RemoteException {
			Agent agt = RecentDeviceInformation.getAgent(systemId);
			Iterator<Integer> i;

			if (agt == null || pmStoreList == null)
				return;

			i = agt.getPM_StoresHandlers();
			while(i.hasNext())
				pmStoreList.add(new PM_Store(i.next(), systemId));
		}

		@Override
		public void getPM_Store(PM_Store pmstore) throws RemoteException {
			Agent agt = RecentDeviceInformation.getAgent(pmstore.getPM_StoreAgentId());
			HANDLE handler = new HANDLE();
			handler.setValue(new INT_U16(new Integer(pmstore.getPM_StoreHandler())));

			System.out.println("Send Event");
			GetPmStoreEvent event = new GetPmStoreEvent(handler);
			agt.sendEvent(event);
		}
    };
	
	
    private final IScannerActionService.Stub mScannerService = new IScannerActionService.Stub() {

		@Override
		public void Set(Scanner scanner, boolean enable) throws RemoteException {
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

			try {
				Attribute attr = new Attribute(Nomenclature.MDC_ATTR_OP_STAT, os);
				SetEvent event = new SetEvent(handle, attr);
				agt.sendEvent(event);
			} catch (InvalidAttributeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void getScanner(String systemId, List<Scanner> scannerList)
				throws RemoteException {
			Agent agt = RecentDeviceInformation.getAgent(systemId);
			Iterator<Integer> i;

			if (agt == null || scannerList == null)
				return;

			i = agt.getScannerHandlers();
			while(i.hasNext())
				scannerList.add(new Scanner(i.next(), systemId));
		}
	};
	
	private final IAgentActionService.Stub mAgentActionService = new IAgentActionService.Stub() {

			@Override
			public void getService(String system_id) throws RemoteException {
				// TODO Auto-generated method stub
				System.out.println("get service invoke on " + system_id);
			}

			@Override
			public void sendEvent(String system_id, int eventType)
					throws RemoteException {
				System.out.println("disconnect service invoke on " + system_id);
				Agent agt = RecentDeviceInformation.getAgent(system_id);
				if (agt==null)
					return;
				agt.sendEvent(new Event(eventType));
			}

			@Override
			public void setService(String system_id) throws RemoteException {
				// TODO Auto-generated method stub
				System.out.println("set service invoke on " + system_id);
			}
	    };
	
	
	
	/************************************************************
	 * Event Manager
	 ************************************************************/

	private final EventManager ieManager = new EventManager(){

		@Override
		public void receivedMeasure(String system_id, Measure m) {
			
			if (system_id==null){
				System.out.println("SystemID was null");
				return;
			}

			AndroidMeasure androidMeasure = new AndroidMeasure(m.getMeasureId(), m.getMeasureName(), m.getUnitId(), m.getUnitName(), m.getTimestamp(), m.getValues(), m.getMetricIds(), m.getMetricNames());
			Log.d(TAG, "Sending android measure");
			Message message = Message.obtain(null, MSG_MEASURE_RECEIVED);
			message.getData().putParcelable("data", androidMeasure);
			sendMessage(message);

			final int N = mCallbacks.beginBroadcast();
			for(int i = 0; i < N; i++){
				try {
					mCallbacks.getBroadcastItem(i).MeasureReceived(system_id, androidMeasure);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			mCallbacks.finishBroadcast();
		}

		@Override
		public void deviceChangeStatus(String system_id, String prevState, String newState) {

			final int N = mCallbacks.beginBroadcast();
			for(int i = 0; i < N; i++){
				try {
					mCallbacks.getBroadcastItem(i).deviceChangeStatus(system_id, prevState, newState);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			mCallbacks.finishBroadcast();

			
		}

		@Override
		public void deviceConnected(String system_id, HealthDevice dev) {
			if(dev!=null){RecentDeviceInformation.addDevice(dev);}

			sendMessage(Message.obtain(null, MSG_DEVICE_CONNECTED));
			
			final int N = mCallbacks.beginBroadcast();
			for(int i = 0; i < N; i++){
				try {
					mCallbacks.getBroadcastItem(i).deviceConnected(system_id);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mCallbacks.finishBroadcast();
			
		}

		@Override
		public void deviceDisconnected(String system_id) {

			sendMessage(Message.obtain(null, MSG_DEVICE_DISCONNECTED));

			final int N = mCallbacks.beginBroadcast();
			for(int i = 0; i < N; i++){
				try {
					mCallbacks.getBroadcastItem(i).deviceDisconnected(system_id);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mCallbacks.finishBroadcast();
			
		}
		
		
	};


	public static final int MSG_REG_CLIENT = 200;
	public static final int MSG_UNREG_CLIENT = 201;
	public static final int MSG_DEVICE_CONNECTED = 203;
	public static final int MSG_MEASURE_RECEIVED = 204;
	public static final int MSG_DEVICE_DISCONNECTED = 205;
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
