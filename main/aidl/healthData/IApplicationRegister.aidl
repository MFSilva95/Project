
/*
Interface for registering applications that wish to receive Health data.
The application shall provide a 'IEventCallback' interface stub for receiving callbacks about events
such as measurements.
*/


package healthData;

import healthData.IEventCallback;

interface IApplicationRegister
{
	void registerApplication(IEventCallback mc);
	void unregisterApplication(IEventCallback mc);
}