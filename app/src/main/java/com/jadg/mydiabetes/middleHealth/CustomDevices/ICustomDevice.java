package com.jadg.mydiabetes.middleHealth.customDevices;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.IEventManager;

import java.util.List;

public interface ICustomDevice extends Runnable
{
	void setEventHandler(IEventManager eventManager);
	String getSystemId();
	void stop();
	List<String> invokeCommand(List<String> args);
}
