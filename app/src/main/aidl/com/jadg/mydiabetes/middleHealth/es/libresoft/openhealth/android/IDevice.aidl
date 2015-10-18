package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android;



import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidHealthDevice;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;

interface IDevice{
	AndroidHealthDevice getDeviceInfo(String systemId);
	List<String> runCommand(String systemId, in List<String> args);


}