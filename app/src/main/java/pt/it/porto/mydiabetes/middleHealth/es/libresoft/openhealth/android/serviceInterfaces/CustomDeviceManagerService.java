package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces;

import android.os.RemoteException;
import android.util.Log;

import pt.it.porto.mydiabetes.middleHealth.customDevices.CustomDeviceManager;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.ICustomDeviceManager;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.events.IEventManager;

public class CustomDeviceManagerService extends ICustomDeviceManager.Stub
{
	public static final String TAG = "CustomDeviceManagerServ";

	private CustomDeviceManager mCustomDeviceManager;

	public CustomDeviceManagerService()
	{
		mCustomDeviceManager = new CustomDeviceManager();
	}

	public void initialize(IEventManager eventManager)
	{
		mCustomDeviceManager.initialize(eventManager);
	}
	public void shutdown()
	{
		mCustomDeviceManager.shutdown();
	}

	@Override
	public void setEraseAllDataOption(boolean value) throws RemoteException
	{
		Log.d(TAG, "setEraseAllDataOption() - setting option value to: " + value);

		mCustomDeviceManager.setEraseAllDataOption(value);
	}
}
