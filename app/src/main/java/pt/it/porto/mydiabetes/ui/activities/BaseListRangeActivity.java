package pt.it.porto.mydiabetes.ui.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;


public abstract class BaseListRangeActivity extends BaseActivity {

	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_list_range_activity);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		fillDates();
		list = (ListView) findViewById(R.id.list);
		fillListView(list);


		EditText dateFrom = (EditText) findViewById(R.id.et_DataFrom);
		EditText dateTo = (EditText) findViewById(R.id.et_DataTo);
		dateFrom.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				fillListView(list);
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
				fillListView(list);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void showDatePickerDialogFrom(View v) {
		android.support.v4.app.DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_DataFrom,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		android.support.v4.app.DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_DataTo,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void fillDates() {
		EditText dateAgo = (EditText) findViewById(R.id.et_DataFrom);
		dateAgo.setText(getBaseStartDate());

		EditText dateNow = (EditText) findViewById(R.id.et_DataTo);
		dateNow.setText(getBaseEndDate());
	}

	public void fillListView(ListView lv) {
		lv.setAdapter(getListAdapter());
		lv.setEmptyView(findViewById(R.id.list_empty));
	}

	public String getStartDate(){
		return ((EditText) findViewById(R.id.et_DataFrom)).getText().toString();
	}

	public String getEndDate(){
		return ((EditText) findViewById(R.id.et_DataTo)).getText().toString();
	}

	abstract String getBaseStartDate();
	abstract String getBaseEndDate();
	abstract ListAdapter getListAdapter();

	@Override
	public void onResume() {
		super.onResume();
		fillListView(list);
	}
}
