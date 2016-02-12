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
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class Glycemia extends Activity {

	ListView glycemiasList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glycemia);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		FillDates();


		EditText datefrom = (EditText) findViewById(R.id.et_Glycemia_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Glycemia_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(glycemiasList);
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
				fillListView(glycemiasList);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		glycemiasList = (ListView) findViewById(R.id.GlycemiasActivityList);

		fillListView(glycemiasList);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.glycemia, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_Glycemia:
				Intent intent = new Intent(this, GlycemiaDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void fillListView(ListView lv) {
		EditText datefrom = (EditText) findViewById(R.id.et_Glycemia_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Glycemia_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<GlycemiaDataBinding> allGlycemias = rdb.Glycemia_GetByDate(datefrom.getText().toString(), dateto.getText().toString());
		rdb.close();
		lv.setAdapter(new GlycemiaAdapter(allGlycemias, this));
	}

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Glycemia_DataFrom, DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Glycemia_DataTo, DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void FillDates() {
		EditText datenow = (EditText) findViewById(R.id.et_Glycemia_DataTo);
		Calendar calendar = Calendar.getInstance();
		if (datenow.getText().length() == 0) {
			datenow.setText(DateUtils.getFormattedDate(calendar));
		}

		EditText dateago = (EditText) findViewById(R.id.et_Glycemia_DataFrom);
		calendar.add(Calendar.DAY_OF_YEAR, -3);
		if (dateago.getText().length() == 0) {
			dateago.setText(DateUtils.getFormattedDate(calendar));
		}
	}
}