package middleHealth.es.libresoft.openhealth;

import java.util.HashMap;
import java.util.Iterator;


import middleHealth.es.libresoft.openhealth.events.Event;
import middleHealth.es.libresoft.openhealth.events.EventType;

public class RecentDeviceInformation { 
	private static HashMap<String,HealthDevice> myHealthDevices;
	private static HashMap<String,Agent> myAgents; //all recent 11073 agents
	
	public static void init(){
		myAgents = new HashMap<String, Agent>();
		myHealthDevices = new HashMap<String, HealthDevice>();
	}
	
	public static void addAgent(Agent agent){
		myAgents.put(agent.getSystem_id(), agent);
	}
	public static void addDevice(HealthDevice dev){
		myHealthDevices.put(dev.getSystId(), dev);
	}
	
	
	public static boolean containsAgent(String sysID){
		return myAgents.containsKey(sysID);
	}
	
	public static boolean containsDevice(String sysID){
		return myHealthDevices.containsKey(sysID);
	}
	
	public static HealthDevice getHealthDevice(String sysID){
		if(myHealthDevices.containsKey(sysID)){
			System.out.println("MYSERVICE: returning an okay device");
			return myHealthDevices.get(sysID);
		}
		System.out.println("MYSERVICE: returning a null device...");
		return null;
	}
	
	public static Agent getAgent(String sysID){
		if(myAgents.containsValue(sysID)){
			return myAgents.get(sysID);			
		}else if(myAgents.size()==1){
			return myAgents.values().iterator().next();
		}
		else{
			return null;
		}
		
	}
	public static void removeAgent(String sysID){
		myAgents.remove(sysID);
	}
	
	public static void abortAgents(){
		Iterator<Agent> iterator = myAgents.values().iterator();
		Agent agent;
		//Send abort signal to all agents
		while (iterator.hasNext()){
			agent = iterator.next();
			agent.sendEvent(new Event(EventType.REQ_ASSOC_ABORT));
		}

		//Free resources taken by agents
		iterator = myAgents.values().iterator();
		while (iterator.hasNext()){
			agent = iterator.next();
			agent.freeResources();
		}

		
	}
	
	
	
	
	
	
	
	
}
