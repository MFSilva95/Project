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
import pt.it.porto.mydiabetes.utils.DateUtils;

public class Logbook extends Activity {

	ListView logbookList;
	private EditText dateFrom;
	private EditText dateTo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logbook);
		dateFrom = (EditText) findViewById(R.id.et_Logbook_DataFrom);
		dateTo = (EditText) findViewById(R.id.et_Logbook_DataTo);
		logbookList = (ListView) findViewById(R.id.LogbookActivityList);

		FillDates();

		dateFrom.addTextChangedListener(new TextWatcher() {
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
		dateTo.addTextChangedListener(new TextWatcher() {
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

		fillListView(logbookList);

	}

	public void FillDates() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -3);
		if(dateFrom.getText().length()==0) {
			dateFrom.setText(DateUtils.getFormattedDate(calendar));
		}

		calendar = Calendar.getInstance();
		if(dateTo.getText().length()==0) {
			dateTo.setText(DateUtils.getFormattedDate(calendar));
		}
	}


	public void fillListView(ListView lv) {
		DB_Read rdb = new DB_Read(this);
		ArrayList<LogbookDataBinding> lb = rdb.getLogbook(dateFrom.getText().toString(), dateTo.getText().toString());
		rdb.close();
		lv.setAdapter(new LogbookAdapter(lb, this));
	}

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Logbook_DataFrom,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Logbook_DataTo,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

}
