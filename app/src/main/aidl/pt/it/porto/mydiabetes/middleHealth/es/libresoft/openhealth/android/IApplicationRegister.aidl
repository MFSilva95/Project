package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.IEventCallback;

interface IApplicationRegister
{
	void registerApplication(IEventCallback eventCallback);
	void unregisterApplication(IEventCallback eventCallback);
}