package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListsDataDb;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.listAdapters.BloodPressureAdapter;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class BloodPressure extends BaseListRangeActivity {

	@Override
	public String getRegType(){return "BloodPressure";}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blood_pressure, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_BloodPressure:
				Intent intent = new Intent(this, BloodPressureDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	String getBaseStartDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -8);
		return DateUtils.getFormattedDate(c);
	}

	@Override
	String getBaseEndDate() {
		Calendar c = Calendar.getInstance();
		return DateUtils.getFormattedDate(c);
	}

	@Override
	ListAdapter getListAdapter() {
		ListsDataDb db = new ListsDataDb(MyDiabetesStorage.getInstance(this));
		Cursor cursor = db.getBloodPressureRegList(getStartDate(), getEndDate());
		return new BloodPressureAdapter(cursor, this);
	}
}
