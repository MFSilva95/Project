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
import pt.it.porto.mydiabetes.ui.listAdapters.DiseaseRegAdapter;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class Disease extends BaseListRangeActivity {


	@Override
	public String getRegType(){return "Disease";}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.disease, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuItem_Disease:
				Intent intent = new Intent(this, DiseaseDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	String getBaseStartDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -30);
		return DateUtils.getFormattedDate(calendar);
	}

	@Override
	String getBaseEndDate() {
		Calendar calendar = Calendar.getInstance();
		return DateUtils.getFormattedDate(calendar);
	}

	@Override
	ListAdapter getListAdapter() {
		ListsDataDb db = new ListsDataDb(MyDiabetesStorage.getInstance(this));
		Cursor cursor = db.getDiseaseRegList(getStartDate(), getEndDate());
		return new DiseaseRegAdapter(cursor, this);
	}
}
