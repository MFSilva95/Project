package CustomDevices;

import java.util.List;

import es.libresoft.openhealth.events.EventManager;

public interface ICustomDevice extends Runnable{
	public void setEventHandler(EventManager ieManager);
	public String getSystemId();
	public void stop();
	public List<String> invokeCommand(List<String> args);
}
