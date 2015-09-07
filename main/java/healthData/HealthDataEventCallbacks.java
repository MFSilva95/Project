package healthData;

import android.os.RemoteException;
import android.util.Log;

public class HealthDataEventCallbacks extends IEventCallback.Stub
{
	private static final String sTAG = "HealthDataEventCallback";
	private IDevice mDevice;

	@Override
	public void deviceConnected(String systemID) throws RemoteException
	{
		if(mDevice == null)
		{
			Log.d(sTAG, "deviceConnected() - device is not set");
			return;
		}

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

	public void setDevice(IDevice device)
	{
		mDevice = device;
	}
}
