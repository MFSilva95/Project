package pt.it.porto.mydiabetes.database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Random;

import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;


@RunWith(AndroidJUnit4.class)
public class DB_LogbookEntries {

	private Context mMockContext;

	@Before
	public void setUp() {
		mMockContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
	}

	@Test
	public void testCreateLogbookEntries() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		int[] times_of_day = {8, 12, 17, 21};
		int[] insulin_targets = {100, 110, 120, 120};

		for (int i = 0; i < 7; i++) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - i);
			for (int j = 0; j < 4; j++) {
				calendar.set(Calendar.HOUR_OF_DAY, times_of_day[j]);
				GlycemiaRec glycemia = new GlycemiaRec();
				glycemia.setIdUser(1);
				glycemia.setId(1);
				glycemia.setValue(100 + new Random().nextInt(20) - 5);
				glycemia.setIdTag(1);
				glycemia.setDateTime(calendar);
				dbWrite.Glycemia_Save(glycemia);

				InsulinRec insulinRec = new InsulinRec();
				insulinRec.setIdUser(1);
				insulinRec.setId(1);
				insulinRec.setIdInsulin(1);
				insulinRec.setIdTag(1);
				insulinRec.setDateTime(calendar);
				insulinRec.setInsulinUnits(3f + (new Random().nextInt(10) / 10f) - 2f);
				insulinRec.setTargetGlycemia(insulin_targets[j]);

				dbWrite.Insulin_Save(insulinRec);


				CarbsRec carbsRec = new CarbsRec();
				carbsRec.setIdUser(1);
				carbsRec.setId(1);
				carbsRec.setCarbsValue(10 + new Random().nextInt(20) - 5);
				carbsRec.setPhotoPath(null);
				carbsRec.setIdTag(1);
				carbsRec.setIdNote(-1);
				carbsRec.setDateTime(calendar);
				dbWrite.Carbs_Save(carbsRec);
			}
		}


		dbRead.close();
		dbWrite.close();
	}

}