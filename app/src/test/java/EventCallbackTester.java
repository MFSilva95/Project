import android.content.Context;
import android.os.RemoteException;

import com.jadg.mydiabetes.middleHealth.controller.EventCallback;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.Measure;
import com.jadg.mydiabetes.middleHealth.es.libresoft.openhealth.android.AndroidMeasure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventCallbackTester
{
	@Mock
	private Context mMockContext;

	@Test
	public void readStringFromContext_LocalizedString() throws RemoteException
	{
		EventCallback eventCallback = new EventCallback(mMockContext);

		eventCallback.MeasureReceived("", null);
	}
}