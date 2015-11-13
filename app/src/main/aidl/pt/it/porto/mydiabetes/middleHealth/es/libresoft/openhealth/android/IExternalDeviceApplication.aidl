package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidMeasure;

interface IExternalDeviceApplication
{
	void deviceConnected(in AndroidHealthDevice healthDevice);
	void deviceDisconnected(String systemId);
	void deviceChangeStatus(String systemID, String previousState, String newState);
	void uploadMeasure(String systemId, in AndroidMeasure measure);
}