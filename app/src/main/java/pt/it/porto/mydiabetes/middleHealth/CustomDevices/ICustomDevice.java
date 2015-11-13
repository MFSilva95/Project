package pt.it.porto.mydiabetes.middleHealth.customDevices;

import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.events.IEventManager;

import java.util.List;

public interface ICustomDevice extends Runnable
{
	void setEventHandler(IEventManager eventManager);
	String getSystemId();
	void stop();
	List<String> invokeCommand(List<String> args);
}
