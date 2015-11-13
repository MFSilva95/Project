package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.HealthDevice;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.Measure;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.RecentDeviceInformation;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.events.IEventManager;

public class EventManager implements IEventManager
{
	private static final String TAG = "EventManager";

	private RemoteCallbackList<IEventCallback> mRemoteCallbackList;

	public EventManager(RemoteCallbackList<IEventCallback> remoteCallbackList)
	{
		mRemoteCallbackList = remoteCallbackList;
	}

	@Override
	public void deviceConnected(String systemId, HealthDevice device)
	{
		Log.d(TAG, "deviceConnected()");

		if(device != null)
			RecentDeviceInformation.addDevice(device);

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
		Log.d(TAG, "deviceDisconnected()");

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
	public void deviceChangeStatus(String systemId, String previousState, String newState)
	{
		Log.d(TAG, "deviceChangeStatus()");

		int size = mRemoteCallbackList.beginBroadcast();
		for(int i = 0; i < size; i++)
		{
			try
			{
				mRemoteCallbackList.getBroadcastItem(i).deviceChangeStatus(systemId, previousState, newState);
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
		Log.d(TAG, "receivedMeasure()");

		if (systemId == null)
		{
			Log.d(TAG, "receivedMeasure() - systemId was null");
			return;
		}

		// Create android measure:
		AndroidMeasure androidMeasure = new AndroidMeasure(measure.getMeasureId(), measure.getMeasureName(), measure.getUnitId(), measure.getUnitName(), measure.getTimestamp(), measure.getValues(), measure.getMetricIds(), measure.getMetricNames());

		int size = mRemoteCallbackList.beginBroadcast();
		for(int i = 0; i < size; i++)
		{
			try
			{
				mRemoteCallbackList.getBroadcastItem(i).measureReceived(systemId, androidMeasure);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}
		mRemoteCallbackList.finishBroadcast();
	}

	public void uploadMeasure(String systemId, AndroidMeasure measure)
	{
		Log.d(TAG, "uploadMeasure()");

		final int N = mRemoteCallbackList.beginBroadcast();
		for(int i = 0; i < N; i++)
		{
			try
			{
				mRemoteCallbackList.getBroadcastItem(i).measureReceived(systemId, measure);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}
		mRemoteCallbackList.finishBroadcast();
	}
}
