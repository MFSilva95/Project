package com.jadg.mydiabetes.middleHealth.customDevices;

import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.EventManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CustomDeviceProvider
{
	private static final String TAG = "CustomDeviceProvider";

	private ICustomDevice[] mCustomDevices =
			{
					new Glucometer()
			};
	private HashMap<String,ICustomDevice> deviceList;
	private ArrayList<Thread> threadList;

	public CustomDeviceProvider()
	{
		Log.d(TAG, "CustomDeviceProvider()");

		deviceList = new HashMap<>();
		threadList = new ArrayList<>();

		for(ICustomDevice customDevice : mCustomDevices)
		{
			String systemId = customDevice.getSystemId();
			if(systemId == null)
				systemId = UUID.randomUUID().toString();

			deviceList.put(systemId, customDevice);
		}
	}

	public void initializeCustomDevices(EventManager eventManager)
	{
		Log.d(TAG, "initializeCustomDevices()");

		for(ICustomDevice device : deviceList.values())
		{
			device.setEventHandler(eventManager);
			Thread thread = new Thread(device);
			thread.start();
			threadList.add(thread);
		}
	}
	
	public void stopCustomDevices()
	{
		Log.d(TAG, "stopCustomDevices()");

		for(ICustomDevice device : deviceList.values())
			device.stop();

		for(Thread device : threadList)
			device.interrupt();
	}
	
	public List<String> invokeCommand(String systemId, List<String> args)
	{
		Log.d(TAG, "invokeCommand()");

		if(!deviceList.containsKey(systemId))
			return null;

		return deviceList.get(systemId).invokeCommand(args);
	}
}