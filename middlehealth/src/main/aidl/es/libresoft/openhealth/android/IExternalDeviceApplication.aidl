package es.libresoft.openhealth.android;

import es.libresoft.openhealth.android.AndroidHealthDevice;
import es.libresoft.openhealth.android.AndroidMeasure;

interface IExternalDeviceApplication{
	void deviceConnected(in AndroidHealthDevice hd);
	void deviceDisconnected(String systID);
	void uploadMeasure(String sysID, in AndroidMeasure ms);
	void deviceChangeStatus(String sysID, String prevState,String newState);
	
}