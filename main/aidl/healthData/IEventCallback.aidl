package healthData;

import healthData.AndroidHealthDevice;
import healthData.AndroidMeasure;

oneway interface IEventCallback
{
	void deviceConnected(String systemID);
	void deviceChangeStatus(String systemID, String prevState, String newState);
	void MeasureReceived(String systemID,in AndroidMeasure m);
	void deviceDisconnected(String systemID);
}