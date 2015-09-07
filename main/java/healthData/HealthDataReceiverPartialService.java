package healthData;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import java.util.Iterator;
import java.util.List;

public class HealthDataReceiverPartialService
{
	private final RemoteCallbackList<IEventCallback> mCallbacks = new RemoteCallbackList<IEventCallback>();
	private CustomDeviceProvider cdProvider;

	private final IApplicationRegister.Stub mApplicationRegister = new IApplicationRegister.Stub()
	{
		@Override
		public void registerApplication(IEventCallback mc) throws RemoteException
		{
			if (mc != null)
				mCallbacks.register(mc);
		}

		@Override
		public void unregisterApplication(IEventCallback mc) throws RemoteException
		{
			if (mc != null)
				mCallbacks.unregister(mc);
		}
	};

	private final IDevice.Stub mDevice = new IDevice.Stub()
	{
		@Override
		public List<String> runCommand(String systemID,List<String> args) throws RemoteException
		{
			return cdProvider.invokeCommand(systemID, args);
		}

		@Override
		public AndroidHealthDevice getDeviceInfo(String systemId) throws RemoteException
		{
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
}
