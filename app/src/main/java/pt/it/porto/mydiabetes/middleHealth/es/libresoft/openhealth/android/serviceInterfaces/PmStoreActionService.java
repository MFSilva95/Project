package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.serviceInterfaces;

import android.os.RemoteException;
import android.util.Log;

import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.Agent;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.RecentDeviceInformation;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.IPMStoreActionService;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android.PMStore;
import pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.events.application.GetPmStoreEvent;
import pt.it.porto.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.HANDLE;
import pt.it.porto.mydiabetes.middleHealth.ieee_11073.part_20601.asn1.INT_U16;

import java.util.Iterator;
import java.util.List;

public class PmStoreActionService extends IPMStoreActionService.Stub
{
	public static final String TAG = "PmStoreActionService";

	@Override
	public void getStorage(String systemId, List<PMStore> pmStoreList) throws RemoteException
	{
		Log.d(TAG, "getStorage()");

		Agent agent = RecentDeviceInformation.getAgent(systemId);

		if (agent == null || pmStoreList == null)
		{
			Log.d(TAG, "getStorage() - Agent/pmStoreList is null!");
			return;
		}

		Iterator<Integer> i = agent.getPMStoresHandlers();
		while(i.hasNext())
			pmStoreList.add(new PMStore(i.next(), systemId));
	}

	@Override
	public void getPMStore(PMStore pmStore) throws RemoteException
	{
		Log.d(TAG, "getPMStore()");

		HANDLE handler = new HANDLE();
		handler.setValue(new INT_U16(pmStore.getPMStoreHandler()));

		Log.d(TAG, "Send Event");
		GetPmStoreEvent event = new GetPmStoreEvent(handler);

		Agent agent = RecentDeviceInformation.getAgent(pmStore.getPMStoreAgentId());
		if(agent == null)
		{
			Log.d(TAG, "getPMStore() - Agent is null!");
			return;
		}

		agent.sendEvent(event);
	}
}
