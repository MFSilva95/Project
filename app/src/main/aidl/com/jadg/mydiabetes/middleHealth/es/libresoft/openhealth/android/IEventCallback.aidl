package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidMeasure;

oneway interface IEventCallback
{
	void deviceConnected(String systemID);
	void deviceChangeStatus(String systemID, String previousState, String newState);
	void measureReceived(String systemID, in AndroidMeasure measure);
	void deviceDisconnected(String systemID);
}