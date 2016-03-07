package database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Calendar;

import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dataBinding.CarbsDataBinding;
import pt.it.porto.mydiabetes.utils.DateUtils;


@RunWith(AndroidJUnit4.class)
public class DB_WriteTest {

	Context mMockContext;

	@Before
	public void setUp() {
		mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
	}

	@Test
	public void testMyData_Save() throws Exception {

		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);
		Object[] data = dbRead.MyData_Read();
		Log.d("Data", Arrays.toString(data));
		Assert.assertArrayEquals("Data is null as expected", null, data);

		Object[] newData = getMockUserData();
		dbWrite.MyData_Save(newData);
		data = dbRead.MyData_Read();

		Assert.assertArrayEquals("Data not equal", newData, data);
		dbRead.close();
		dbWrite.close();
	}

	private Object[] getMockUserData() {
		Calendar calendar = Calendar.getInstance();
		//// FIXME: 07/03/16 insulin type and gender shouldn't be a string, this can complete breakage if language changes
		return new Object[]{1, "Nome", "Tipo 1", 45.0, 50.0, 50.0, 150.0, "11-01-2011", "Homem", 1.85, DateUtils.formatToDb(calendar)};
	}

	@Test
	public void testCarbs_Save() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsDataBinding carbsDataBinding = new CarbsDataBinding();
		carbsDataBinding.setId_User(1);
		carbsDataBinding.setId(1);
		carbsDataBinding.setCarbsValue(10);
		carbsDataBinding.setPhotoPath("/");
		carbsDataBinding.setId_Tag(1);
		carbsDataBinding.setId_Note(-1);
		carbsDataBinding.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsDataBinding);

		CarbsDataBinding result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull(result);
		Log.d("test", "saved: " + carbsDataBinding.toString());
		Log.d("test", "returned: " + result.toString());
		Assert.assertTrue("Carb data not the same as saved", carbsDataBinding.equals(result));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_Update() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsDataBinding carbsDataBinding = new CarbsDataBinding();
		carbsDataBinding.setId_User(1);
		carbsDataBinding.setId(1);
		carbsDataBinding.setCarbsValue(10);
		carbsDataBinding.setPhotoPath("/");
		carbsDataBinding.setId_Tag(1);
		carbsDataBinding.setId_Note(-1);
		carbsDataBinding.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsDataBinding);

		carbsDataBinding.setCarbsValue(11);
		dbWrite.Carbs_Update(carbsDataBinding);

		CarbsDataBinding result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull(result);
		Log.d("test", "saved: " + carbsDataBinding.toString());
		Log.d("test", "returned: " + result.toString());
		Assert.assertTrue("Carb data not the same as saved", carbsDataBinding.equals(result));
		Assert.assertEquals("Carbs value different from the expected", carbsDataBinding.getCarbsValue(), result.getCarbsValue());

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_Delete() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsDataBinding carbsDataBinding = new CarbsDataBinding();
		carbsDataBinding.setId_User(1);
		carbsDataBinding.setId(1);
		carbsDataBinding.setCarbsValue(10);
		carbsDataBinding.setPhotoPath("/");
		carbsDataBinding.setId_Tag(1);
		carbsDataBinding.setId_Note(-1);
		carbsDataBinding.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsDataBinding);

		CarbsDataBinding result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull(result);
		dbWrite.Carbs_Delete(1);
		result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNull("Carbs value not deleted", result);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_DeletePhoto() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsDataBinding carbsDataBinding = new CarbsDataBinding();
		carbsDataBinding.setId_User(1);
		carbsDataBinding.setId(1);
		carbsDataBinding.setCarbsValue(10);
		carbsDataBinding.setPhotoPath("/");
		carbsDataBinding.setId_Tag(1);
		carbsDataBinding.setId_Note(-1);
		carbsDataBinding.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsDataBinding);

		dbWrite.Carbs_DeletePhoto(1);
		CarbsDataBinding result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull("Carbs in DB null", result);
		Assert.assertEquals("Photopath not deleted", "", result.getPhotoPath());

		dbRead.close();
		dbWrite.close();
	}

}