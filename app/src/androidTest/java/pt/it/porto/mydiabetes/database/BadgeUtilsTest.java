package pt.it.porto.mydiabetes.database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import pt.it.porto.mydiabetes.data.PointsRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;


@RunWith(AndroidJUnit4.class)
public class BadgeUtilsTest {

	Context mMockContext;

	@Before
	public void setUp() {
		mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
	}


	@Test
	public void testAddLogBadge() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		PointsRec rec=new PointsRec();
		rec.setValue(10);
		rec.setId(1);
		rec.setIdUser(1);
		rec.setOrigin("oi");
		rec.setDateTime(Calendar.getInstance());
		dbWrite.Point_Save(rec);


		BadgeUtils.addLogBadge(mMockContext, dbRead, dbWrite);

		dbRead.close();
		dbWrite.close();
	}


	@Test
	public void testBadges() throws Exception {


	}

	@Test
	public void testLogbook_Delete() throws Exception {


	}

	@Test
	public void testLogbook_DeleteOnSave() throws Exception {

	}


	static UserInfo getMockUserData() {
		Calendar calendar = Calendar.getInstance();
		return new UserInfo(1, "Nome", "Tipo 1", 45, 50, 50, 150, "11-01-2011", "Masculino", 1.85, DateUtils.formatToDb(calendar));
	}


}