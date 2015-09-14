package middleHealth.es.libresoft.openhealth.android;

import middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import middleHealth.es.libresoft.openhealth.android.AndroidMeasure;

oneway interface IEventCallback{
	void deviceConnected(String systemID);
	void deviceChangeStatus(String systemID, String prevState, String newState);
	void MeasureReceived(String systemID,in AndroidMeasure m);
	void deviceDisconnected(String systemID);
}