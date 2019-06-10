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

import com.cdev.achievementview.AchievementView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;


public abstract class BaseListRangeActivity extends BaseActivity {

	ListView list;
	private AchievementView achievementView;

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
		achievementView = findViewById(R.id.achievement_view);
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
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_DataFrom,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_DataTo,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
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
		sendBadgeNotification();
		fillListView(list);
	}

	public void sendBadgeNotification() {
		// notification non daily
		if (ExerciseDetail.winBadge) {
			achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.exerciseBadgeWon));
			ExerciseDetail.winBadge = false;
		}
		if (DiseaseDetail.winBadge) {
			achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.diseaseBadgeWon));
			DiseaseDetail.winBadge = false;
		}
		if (BloodPressureDetail.winBadge) {
			achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.bpBadgeWon));
			BloodPressureDetail.winBadge = false;
		}
		if (CholesterolDetail.winBadge) {
			achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.cholesterolBadgeWon));
			CholesterolDetail.winBadge = false;
		}
		if (HbA1cDetail.winBadge) {
			achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.hba1cBadgeWon));
			HbA1cDetail.winBadge = false;
		}
		// notification daily
		if (ExerciseDetail.winDaily || DiseaseDetail.winDaily || BloodPressureDetail.winDaily || CholesterolDetail.winDaily || HbA1cDetail.winDaily) {
			achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.dailyBadgeWon));
			ExerciseDetail.winDaily = false;
			DiseaseDetail.winDaily = false;
			BloodPressureDetail.winDaily = false;
			CholesterolDetail.winDaily = false;
			HbA1cDetail.winDaily = false;
		}
	}
}
