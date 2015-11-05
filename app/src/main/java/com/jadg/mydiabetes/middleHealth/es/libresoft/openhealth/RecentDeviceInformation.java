package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth;

import android.util.Log;

import java.util.HashMap;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.Event;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.EventType;

public class RecentDeviceInformation
{
	private static final String TAG = "RecentDeviceInformation";

	private static HashMap<String,HealthDevice> myHealthDevices;
	private static HashMap<String,Agent> myAgents; //all recent 11073 agents
	
	public static void init()
	{
		myAgents = new HashMap<>();
		myHealthDevices = new HashMap<>();
	}
	
	public static void addAgent(Agent agent)
	{
		myAgents.put(agent.getSystem_id(), agent);
	}
	public static void addDevice(HealthDevice dev)
	{
		myHealthDevices.put(dev.getSystId(), dev);
	}

	public static boolean containsAgent(String systemId)
	{
		return myAgents.containsKey(systemId);
	}
	
	public static boolean containsDevice(String systemId)
	{
		return myHealthDevices.containsKey(systemId);
	}
	
	public static HealthDevice getHealthDevice(String systemId)
	{
		if(!myHealthDevices.containsKey(systemId))
		{
			Log.d(TAG, "getHealthDevice() - Returning null");
			return null;
		}

		return myHealthDevices.get(systemId);
	}
	
	public static Agent getAgent(String systemId)
	{
		if(myAgents.containsValue(systemId))
			return myAgents.get(systemId);

		if(myAgents.size() == 1)
			return myAgents.values().iterator().next();

		return null;
	}
	public static void removeAgent(String systemId)
	{
		myAgents.remove(systemId);
	}
	
	public static void abortAgents()
	{
		if(myAgents == null)
			return;

		// Send abort signal to all agents and free all the resources:
		for (Agent agent : myAgents.values())
		{
			agent.sendEvent(new Event(EventType.REQ_ASSOC_ABORT));
			agent.freeResources();
		}
	}
}