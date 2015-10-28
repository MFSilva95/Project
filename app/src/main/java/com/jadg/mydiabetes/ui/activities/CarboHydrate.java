package com.jadg.mydiabetes.ui.activities;

import android.annotation.SuppressLint;
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

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.database.DB_Read;
import com.jadg.mydiabetes.ui.dialogs.DatePickerFragment;
import com.jadg.mydiabetes.ui.listAdapters.CarbsAdapter;
import com.jadg.mydiabetes.ui.listAdapters.CarbsDataBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


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


	@SuppressLint("SimpleDateFormat")
	public void FillDates() {
		EditText dateago = (EditText) findViewById(R.id.et_Carbs_DataFrom);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -3);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		Date newDate = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(newDate);

		dateago.setText(dateString);

		EditText datenow = (EditText) findViewById(R.id.et_Carbs_DataTo);
		c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		cal.set(year, month, day);
		newDate = cal.getTime();
		dateString = formatter.format(newDate);
		datenow.setText(dateString);
	}

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("textbox", R.id.et_Carbs_DataFrom);
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putInt("textbox", R.id.et_Carbs_DataTo);
		newFragment.setArguments(args);
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
