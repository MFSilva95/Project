package com.jadg.mydiabetes.middlehealth;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import es.libresoft.openhealth.android.AndroidHealthDevice;
import es.libresoft.openhealth.android.AndroidMeasure;
import es.libresoft.openhealth.android.IApplicationRegister;
import es.libresoft.openhealth.android.IDevice;
import es.libresoft.openhealth.android.IEventCallback;
import es.libresoft.openhealth.android.MiddleHealth;

public class DevicesReader
{
	private Activity mActivity;
	private IApplicationRegister mAppRegister;
	private IDevice mDevice;

	public DevicesReader(Activity activity)
	{
		mActivity = activity;
	}

	public void initialize()
	{
		Intent applicationRegisterService = new Intent(IApplicationRegister.class.getName());
		mActivity.bindService(applicationRegisterService, mApplicationRegisterConnection, Context.BIND_AUTO_CREATE);

		Intent deviceService = new Intent(IDevice.class.getName());
		mActivity.bindService(deviceService, mDeviceConnection, Context.BIND_AUTO_CREATE);

		mActivity.startService(new Intent(mActivity, MiddleHealth.class));
	}

	private ServiceConnection mApplicationRegisterConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			System.out.println("Service connected");

			mAppRegister = IApplicationRegister.Stub.asInterface(service);

			try
			{
				mAppRegister.registerApplication(mCallbacks);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			System.out.println("service disconnected");
		}
		
	};
	
	private ServiceConnection mDeviceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mDevice = IDevice.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
		}
	};

	/************************************************************
	 * Deal with callbacks
	 ************************************************************/
	private final IEventCallback.Stub mCallbacks = new IEventCallback.Stub()
	{
		@Override
		public void deviceConnected(String systemID) throws RemoteException
		{
			System.out.println("FROM ACTIVITY: DEVICE CONNECTED!!");
			System.out.println("MYCLIENT: now reading device...");
			AndroidHealthDevice dev = mDevice.getDeviceInfo(systemID);
			System.out.println("MYCLIENT: powerStatus -> " + dev.getPowerStatus());
			System.out.println("MYCLIENT: batteryLevel -> " + dev.getBatteryLever());
			System.out.println("MYCLIENT: macAddress -> " + dev.getMacAddress());
			System.out.println("MYCLIENT: manufacturer -> " + dev.getManufacturer());
			System.out.println("MYCLIENT: modelNumber -> " + dev.getModelNumber());
			System.out.println("MYCLIENT: is11073 -> " + dev.is11073());
			System.out.println("MYCLIENT: systemTypeIds -> ");
			for(int id : dev.getSystemTypeIds()){
				System.out.println("MYCLIENT: " + id);
			}
			System.out.println("MYCLIENT: System types -> ");
			for(String name : dev.getSystemTypes()){
				System.out.println("MYCLIENT: " + name);
			}
		}
		
		@Override
		public void deviceChangeStatus(String systemID, String prevState, String newState) throws RemoteException
		{
			System.out.println("FROM ACTIVITY: STATE CHANGE " + prevState + " -> " + newState);
		}
		
		@Override
		public void MeasureReceived(String systemID, AndroidMeasure m) throws RemoteException
		{
			System.out.println("MYCLIENT: systemID -> " + systemID);
			System.out.println("MYCLIENT: measureID -> " + m.getMeasureId());
			System.out.println("MYCLIENT: measureName -> " + m.getMeasureName());
			System.out.println("MYCLIENT: unitId -> " + m.getUnitId());
			System.out.println("MYCLIENT: unitName -> " + m.getUnitName());
			System.out.println("MYCLIENT: values -> ");
			for(double v : m.getValues()){
				System.out.println("MYCLIENT: " + v);
			};
			System.out.print("MYCLIENT: metricIds -> ");
			for(int v: m.getMetricIds()){
				System.out.println(v + " ");
			}
			System.out.println();
			System.out.print("MYCLIENT: metricNames -> ");
			for(String n: m.getMetricNames()){
				System.out.print("MYCLIENT: " + n );
			}
			System.out.println();
		}

		@Override
		public void deviceDisconnected(String systemID) throws RemoteException
		{
			System.out.println("FROM ACTIVITY: DEVICE DISCONNECTED");
		}
	};
}
