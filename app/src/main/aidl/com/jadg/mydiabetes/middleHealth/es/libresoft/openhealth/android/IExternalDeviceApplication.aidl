package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidMeasure;

interface IExternalDeviceApplication{
	void deviceConnected(in AndroidHealthDevice hd);
	void deviceDisconnected(String systID);
	void uploadMeasure(String sysID, in AndroidMeasure ms);
	void deviceChangeStatus(String sysID, String prevState,String newState);
	
}