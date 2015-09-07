package mydiabetes;

import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by JPMMa on 07/09/2015.
 */
public class BluetoothHDPManagerTest
{
	private static final String sBLOOD_PRESSURE_DATA_SAMPLE_FILENAME = "src/test/test_resources/bloodPressureSampleData.txt";

	@Test
	public void readBloodPressureDataSample()
	{
		File file = new File(sBLOOD_PRESSURE_DATA_SAMPLE_FILENAME);

		Assert.assertTrue(file.exists());
		Assert.assertTrue(file.canRead());

		try
		{
			FileInputStream inputStream = new FileInputStream(file);

			final byte data[] = new byte[8192];
			while (inputStream.read(data) > -1)
			{
				/*BluetoothHDPChannel cHDP = new BluetoothHDPChannel(fd);
				Agent a = new Agent();
				a.addChannel(cHDP);
				a.setAddress(device.getAddress());
				System.out.println("added a new Agent!");*/
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Assert.fail();
		}
	}
}