package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;

interface IApplicationRegister
{
	void registerApplication(IEventCallback eventCallback);
	void unregisterApplication(IEventCallback eventCallback);
}