package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsDataBinding;


public class CarboHydrate extends Activity {

	ListView carbsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carbo_hydrate);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		FillDates();

		carbsList = (ListView) findViewById(R.id.CarbsActivityList);
		fillListView(carbsList);

		EditText datefrom = (EditText) findViewById(R.id.et_Carbs_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Carbs_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(carbsList);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		dateto.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(carbsList);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.carbo_hydrate, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrate_Add:
				Intent intent = new Intent(this, CarboHydrateDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void FillDates() {
		EditText dateago = (EditText) findViewById(R.id.et_Carbs_DataFrom);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -3);

		dateago.setText(DatePickerFragment.getFormatedDate(calendar));

		EditText datenow = (EditText) findViewById(R.id.et_Carbs_DataTo);
		calendar = Calendar.getInstance();
		datenow.setText(DatePickerFragment.getFormatedDate(calendar));
	}

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Carbs_DataFrom,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Carbs_DataTo,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void fillListView(ListView lv) {
		EditText datefrom = (EditText) findViewById(R.id.et_Carbs_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Carbs_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<CarbsDataBinding> allcarbs = rdb.CarboHydrate_GetBtDate(datefrom.getText().toString(), dateto.getText().toString());
		rdb.close();

		lv.setAdapter(new CarbsAdapter(allcarbs, this));


	}
}
