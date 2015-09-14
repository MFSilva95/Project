package middleHealth.customDevices;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import middleHealth.es.libresoft.openhealth.events.EventManager;

public class CustomDeviceProvider {
	private String[] classNames = {
		"Glucometer"
		//add your custom device class name here...	
			
			
	};
	
	private HashMap<String,ICustomDevice> deviceList;
	private ArrayList<Thread> threadList;
	
	
	public CustomDeviceProvider(Context context){
		
		deviceList = new HashMap<String,ICustomDevice>();
		threadList = new ArrayList<Thread>();
		try {
			for(String className : classNames){
				ICustomDevice dev;
				dev = (ICustomDevice) Class.forName("CustomDevices." + className).newInstance();
				String sysID = dev.getSystemId();
				if(sysID==null){
					sysID = UUID.randomUUID().toString();
				}
				deviceList.put(sysID, dev);
				
			}
			
			
		} catch (Exception e) {
			System.out.println("Could not instanciate class");
		}
		/*
		Enumeration<URL> en = null;
		File packageName = null;
		try {
			en = CustomDeviceProvider.class.getClassLoader().getResources("middleHealth.CustomDevices");
			URL path = en.nextElement();
			packageName = new File(path.toURI());
		} catch (Exception e) {
			System.out.println(packageName);
			System.out.println("middleHealth.CustomDevices package not found...");
		}
		String[] classNames = packageName.list();
		for(String className : classNames){
			if(className.equals("CustomDeviceProvider.class") || className.equals("ICustomDevice.class")){
				continue;
			}
			ICustomDevice dev = null;
			try {
				dev = (ICustomDevice) Class.forName(className.split("\\.")[0]).newInstance();
			} catch (Exception e) {
				System.out.println("Could not instanciate class named " + className);
			}
			String systemId = dev.getSystemId();
			if(systemId == null){
				systemId = UUID.randomUUID().toString();
			}
			deviceThreads.put(systemId, dev);
		}
		*/
		
	}
	
	
	
	
	public void initCustomDevices(EventManager ieManager){
		for(ICustomDevice dev : deviceList.values()){
			dev.setEventHandler(ieManager);
			Thread devThread = new Thread(dev);
			devThread.start();
			threadList.add(devThread);
		}
	}
	
	public void stopCustomDevices(){
		for(ICustomDevice dev : deviceList.values()){
			dev.stop();
		}
		for(Thread dev : threadList){
			dev.interrupt();
		}
	}
	
	public List<String> invokeCommand(String SystemId,List<String> args){
		if(deviceList.containsKey(SystemId)){
			List<String> result = deviceList.get(SystemId).invokeCommand(args);
			return result;
		}	
		else{
			return null;
		}
	}
	
	 
}

