package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getBaseContext(), BloodPressureDetail.class);
				startActivity(intent);
			}
		});

	}

	@Override
	String getBaseStartDate() {
		Calendar c = Calendar.getInstance();
//		c.add(Calendar.DAY_OF_YEAR, -8);
		c.add(Calendar.YEAR, -10);
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
