package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;

interface IDevice
{
	AndroidHealthDevice getDeviceInfo(String systemId);
	List<String> runCommand(String systemId, in List<String> args);
}