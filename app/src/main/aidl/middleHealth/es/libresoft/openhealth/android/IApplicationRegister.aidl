
/*
Interface for registering applications that wish to receive Health data.
The application shall provide a 'IEventCallback' interface stub for receiving callbacks about events
such as measurements.
*/


package middleHealth.es.libresoft.openhealth.android;

import middleHealth.es.libresoft.openhealth.android.IEventCallback;

interface IApplicationRegister{
	void registerApplication(IEventCallback mc);
	void unregisterApplication(IEventCallback mc);
}