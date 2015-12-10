package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.LogbookAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.LogbookDataBinding;

public class Logbook extends Activity {

	ListView logbookList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logbook);

		FillDates();

		EditText datefrom = (EditText) findViewById(R.id.et_Logbook_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Logbook_DataTo);
		datefrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(logbookList);
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
				fillListView(logbookList);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});


		logbookList = (ListView) findViewById(R.id.LogbookActivityList);
		fillListView(logbookList);

	}

	public void FillDates() {
		EditText dateago = (EditText) findViewById(R.id.et_Logbook_DataFrom);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -3);
		dateago.setText(DatePickerFragment.getFormatedDate(calendar));

		EditText datenow = (EditText) findViewById(R.id.et_Logbook_DataTo);
		calendar = Calendar.getInstance();
		datenow.setText(DatePickerFragment.getFormatedDate(calendar));
	}


	public void fillListView(ListView lv) {

		EditText datefrom = (EditText) findViewById(R.id.et_Logbook_DataFrom);
		EditText dateto = (EditText) findViewById(R.id.et_Logbook_DataTo);
		DB_Read rdb = new DB_Read(this);
		ArrayList<LogbookDataBinding> lb = rdb.getLogbook(datefrom.getText().toString(), dateto.getText().toString());
		rdb.close();
		lv.setAdapter(new LogbookAdapter(lb, this));
	}

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Logbook_DataFrom,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Logbook_DataTo,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

}
