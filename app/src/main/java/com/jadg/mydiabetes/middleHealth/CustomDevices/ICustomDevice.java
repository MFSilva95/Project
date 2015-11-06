package com.jadg.mydiabetes.middleHealth.customDevices;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.EventManager;

import java.util.List;

public interface ICustomDevice extends Runnable
{
	void setEventHandler(EventManager eventManager);
	String getSystemId();
	void stop();
	List<String> invokeCommand(List<String> args);
}
