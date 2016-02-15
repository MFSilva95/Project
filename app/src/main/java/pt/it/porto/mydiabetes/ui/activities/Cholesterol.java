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
import pt.it.porto.mydiabetes.ui.listAdapters.CholesterolAdapter;
import pt.it.porto.mydiabetes.ui.dataBinding.CholesterolDataBinding;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class Cholesterol extends Activity {

	ListView choList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cholesterol);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		FillDates();
		choList = (ListView) findViewById(R.id.CholesterolActivityList);
		fillListView(choList);

		EditText datefrom = (EditText) findViewById(R.id.et_Cholesterol_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Cholesterol_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(choList);
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
				fillListView(choList);
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
		getMenuInflater().inflate(R.menu.cholesterol, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_Cholesterol:
				Intent intent = new Intent(this, CholesterolDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Cholesterol_DataFrom,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Cholesterol_DataTo,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void FillDates() {
		EditText dateago = (EditText) findViewById(R.id.et_Cholesterol_DataFrom);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -180);

		dateago.setText(DateUtils.getFormattedDate(calendar));

		EditText datenow = (EditText) findViewById(R.id.et_Cholesterol_DataTo);
		calendar = Calendar.getInstance();
		datenow.setText(DateUtils.getFormattedDate(calendar));
	}

	public void fillListView(ListView lv) {
		EditText datefrom = (EditText) findViewById(R.id.et_Cholesterol_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Cholesterol_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<CholesterolDataBinding> allcho = rdb.Cholesterol_GetBtDate(datefrom.getText().toString(), dateto.getText().toString());
		rdb.close();
		lv.setAdapter(new CholesterolAdapter(allcho, this));
	}
}
