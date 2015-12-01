package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces;

import android.os.RemoteException;
import android.util.Log;

import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.HealthDevice;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.RecentDeviceInformation;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidMeasure;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.EventManager;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.IExternalDeviceApplication;

public class ExternalDeviceApplicationService extends IExternalDeviceApplication.Stub
{
	public static final String TAG = "ExternalDeviceApplicati";

	private EventManager mEventManager;

	public ExternalDeviceApplicationService(EventManager eventManager)
	{
		mEventManager = eventManager;
	}

	@Override
	public void deviceConnected(AndroidHealthDevice androidHealthDevice) throws RemoteException
	{
		Log.d(TAG, "mExternalApplication.deviceConnected()");

		HealthDevice device = new HealthDevice();
		device.setSystId(androidHealthDevice.getSystemId());
		device.setBatteryLevel(androidHealthDevice.getBatteryLever());
		device.setAddress(androidHealthDevice.getMacAddress());
		device.setManufacturer(androidHealthDevice.getManufacturer());
		device.setModel(androidHealthDevice.getModelNumber());
		device.setPowerStatus(androidHealthDevice.getPowerStatus());
		RecentDeviceInformation.addDevice(device);

		mEventManager.deviceConnected(androidHealthDevice.getSystemId(), null);
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

		mEventManager.uploadMeasure(systemId, measure);
	}
}
