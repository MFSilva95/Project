package healthData;

import healthData.AndroidHealthDevice;
import healthData.IEventCallback;

interface IDevice
{
	AndroidHealthDevice getDeviceInfo(String systemId);
	List<String> runCommand(String systemId, in List<String> args);
}