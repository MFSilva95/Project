package com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces;

import android.os.RemoteException;
import android.util.Log;

import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Agent;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.RecentDeviceInformation;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.IAgentActionService;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.events.Event;

public class AgentActionService extends IAgentActionService.Stub
{
	public static final String TAG = "AgentActionService";

	@Override
	public void sendEvent(String systemId, int eventType) throws RemoteException
	{
		Log.d(TAG, "mAgentActionService.sendEvent()");

		Agent agent = RecentDeviceInformation.getAgent(systemId);
		if (agent == null)
			return;

		agent.sendEvent(new Event(eventType));
	}

	@Override
	public void getService(String systemId) throws RemoteException
	{
		Log.d(TAG, "mAgentActionService.getService()");
	}

	@Override
	public void setService(String systemId) throws RemoteException
	{
		Log.d(TAG, "mAgentActionService.setService()");
	}
}
