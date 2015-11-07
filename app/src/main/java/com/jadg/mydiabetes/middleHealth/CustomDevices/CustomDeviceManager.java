package com.jadg.mydiabetes.middleHealth.customDevices;

import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.IEventManager;

import java.util.HashMap;
import java.util.Map;

public class CustomDeviceManager
{
	private static final String TAG = "CustomDeviceManager";

	private Map<ICustomDevice, Thread> mThreads;
	private Glucometer mGlucometer;

	public CustomDeviceManager()
	{
		mThreads = new HashMap<>();
		mGlucometer = new Glucometer();
	}

	public void initialize(IEventManager eventManager)
	{
		Log.d(TAG, "initialize()");

		// Initialize glucometer:
		initializeDevice(eventManager, mGlucometer);
	}
	public void shutdown()
	{
		Log.d(TAG, "shutdown()");

		shutdownDevice(mGlucometer);
	}

	public void setEraseAllDataOption(boolean value)
	{
		// Set option value for the glucometer:
		mGlucometer.setEraseAllDataOption(value);
	}

	private void initializeDevice(IEventManager eventManager, ICustomDevice device)
	{
		// Set event handler:
		device.setEventHandler(eventManager);

		// Start a new thread to handle the device logic::
		Thread thread = new Thread(device);
		thread.start();

		// Add thread to the map:
		mThreads.put(device, thread);
	}
	private void shutdownDevice(ICustomDevice device)
	{
		// Stop the device:
		device.stop();

		// Remove the respective thread from the map and interrupt it:
		mThreads.remove(device).interrupt();
	}
}