package pt.it.porto.mydiabetes.ui.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.text.ParseException;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListsDataDb;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.WeightAdapter;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class Weight extends BaseOldActivity {

	ListView weightList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weight);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		FillDates();
		weightList = (ListView) findViewById(R.id.WeightActivityList);
		fillListView(weightList);

		EditText datefrom = (EditText) findViewById(R.id.et_Weight_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Weight_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(weightList);
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
				fillListView(weightList);
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
		getMenuInflater().inflate(R.menu.weight, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_Weight:
				Intent intent = new Intent(this, WeightDetail.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Weight_DataFrom,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Weight_DataTo,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void FillDates() {
		EditText dateago = (EditText) findViewById(R.id.et_Weight_DataFrom);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -8);

		dateago.setText(DatePickerFragment.getFormatedDate(calendar));

		EditText datenow = (EditText) findViewById(R.id.et_Weight_DataTo);
		calendar = Calendar.getInstance();
		datenow.setText(DatePickerFragment.getFormatedDate(calendar));
	}

	public void fillListView(ListView lv) {
		EditText datefrom = (EditText) findViewById(R.id.et_Weight_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Weight_DataTo);
		ListsDataDb db = new ListsDataDb(MyDiabetesStorage.getInstance(this));
		Cursor cursor = db.getWeightList(datefrom.getText().toString(), dateto.getText().toString());
		if (cursor.getCount() == 0) {
			cursor = db.getWeightList(dateto.getText().toString(), 20);
			cursor.moveToLast();
			try {
				datefrom.setText(DateUtils.getFormattedDate(DateUtils.parseDateTime(cursor.getString(2))));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		lv.setAdapter(new WeightAdapter(cursor, this));
	}
}
