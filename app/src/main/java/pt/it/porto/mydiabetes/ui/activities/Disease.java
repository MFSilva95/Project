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
import pt.it.porto.mydiabetes.ui.listAdapters.DiseaseRegAdapter;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class Disease extends BaseListRangeActivity {


	@Override
	public String getRegType(){return "Disease";}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getBaseContext(), DiseaseDetail.class);
				startActivity(intent);
			}
		});

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
