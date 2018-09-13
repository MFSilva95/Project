package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.data.InsulinTarget;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.views.GlycemiaObjectivesData;
import pt.it.porto.mydiabetes.utils.ArraysUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class TargetBG_detail extends BaseActivity {

	public static final String BUNDLE_GOAL = "GOAL";
	public static final String BUNDLE_ID = "Id";
	public static final String BUNDLE_DATA = "Data";

	int idTarget = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target_bg_detail);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		Bundle args = getIntent().getExtras();
		if (args != null) {
			InsulinTarget toFill = null;
			if (args.containsKey(BUNDLE_DATA)) {
				toFill = args.getParcelable(BUNDLE_DATA);
			}

			if (args.containsKey(BUNDLE_ID)) {
				String id = args.getString("Id");
				DB_Read rdb = new DB_Read(this);
				toFill = rdb.Target_GetById(Integer.parseInt(id));
				rdb.close();
			}

			if (toFill != null) {
				idTarget = toFill.getId();

				EditText name = (EditText) findViewById(R.id.et_TargetBG_Nome);
				name.setText(toFill.getName());
				EditText from = (EditText) findViewById(R.id.et_TargetBG_HourFrom);
				from.setText(toFill.getStart());
//				EditText to = (EditText) findViewById(R.id.et_TargetBG_HourTo);
//				to.setText(toFill.getEnd());
//				to.setEnabled(false);
				EditText value = (EditText) findViewById(R.id.et_TargetBG_Glycemia);
				value.setText(String.valueOf((int) toFill.getTarget()));
			}

			if (args.containsKey(BUNDLE_GOAL)) {
				float goal = args.getFloat(BUNDLE_GOAL);
				((EditText) findViewById(R.id.et_TargetBG_Glycemia)).setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) goal));
			}

		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.target_bg_detail_delete, menu);
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UpdateTarget();
				}
			});
		} else {
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddNewTarget();
				}
			});
		}

		//getSupportMenuInflater().inflate(R.menu.tag_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menuItem_TargetBGDetail_Delete:
				DeleteTarget();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void showTimePickerDialogFrom(View v) {
		String currentTime = "";
		if(((EditText) v).getText().toString().equals("")){
			Calendar registerDate = Calendar.getInstance();
			currentTime = DateUtils.getFormattedTimeSec(registerDate);
		}
		else{
			currentTime = ((EditText) v).getText().toString();
		}
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_TargetBG_HourFrom,
				DateUtils.getTimeCalendar(currentTime));
		newFragment.show(getFragmentManager(), "timePicker");
		TextView errorLabel = (TextView) findViewById(R.id.targetGlicemiaErrorTV);
		errorLabel.setText("");
		errorLabel.setVisibility(View.GONE);
	}

//	public void showTimePickerDialogTo(View v) {
//		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_TargetBG_HourTo,
//				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
//		newFragment.show(getFragmentManager(), "timePicker");
//		TextView errorLabel = (TextView) findViewById(R.id.targetGlicemiaErrorTV);
//		errorLabel.setText("");
//		errorLabel.setVisibility(View.GONE);
//	}

	public void AddNewTarget() {
		EditText name = (EditText) findViewById(R.id.et_TargetBG_Nome);
		EditText hourFrom = (EditText) findViewById(R.id.et_TargetBG_HourFrom);
		//EditText hourTo = (EditText) findViewById(R.id.et_TargetBG_HourTo);
		EditText value = (EditText) findViewById(R.id.et_TargetBG_Glycemia);

		if (name.getText().toString().equals("")) {
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourFrom.getText().toString().equals("")) {
			hourFrom.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourFrom, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
//		if (hourTo.getText().toString().equals("")) {
//			hourTo.requestFocus();
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
//			return;
//		}
		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

//		if(GlicObjTimesOverlap(hourFrom.getText().toString(), hourTo.getText().toString())){
//
//			TextView errorLabel = (TextView) findViewById(R.id.targetGlicemiaErrorTV);
//			errorLabel.setVisibility(View.VISIBLE);
//			errorLabel.setText(R.string.targetOverlapError);
//			return;}

		DB_Write wdb = new DB_Write(this);
		InsulinTarget target = new InsulinTarget();
		target.setName(name.getText().toString());
//		if (!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")) {
		if (!hourFrom.getText().toString().equals("")) {
			target.setStart(hourFrom.getText().toString());
//			target.setEnd(hourTo.getText().toString());
		}

		target.setTarget(Double.valueOf(value.getText().toString()));
		wdb.Target_Add(target);
		wdb.close();
		setResult(RESULT_OK);
		finish();
	}

//	public boolean GlicObjTimesOverlap(String st, String et){
//
//		DB_Read read = new DB_Read(this);
//		Cursor cursor = read.getGlicObj();
//		// validate time intervals
//		ArrayList<Point> times = new ArrayList<>(cursor.getCount());
//		String[] temp;
//		cursor.moveToFirst();
//
//		temp = st.split(":");
//		int startTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
//		temp = et.split(":");
//		int endTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
//
//		for(int y=0;y<cursor.getCount();y++){
//			cursor.moveToPosition(y);
//			if(!cursor.getString(0).equals(idTarget+"")){
//				temp = cursor.getString(1).split(":");
//				int startTime2 = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
//				temp = cursor.getString(2).split(":");
//				int endTime2 = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
//				if(CheckOverlap(startTime, startTime2, endTime, endTime2)){return true;}
//			}
//		}
//		return false;
//	}

//	public int getDuration(int start, int end){
//		if (start>end){return start-end;}
//		return end-start;
//	}

//	public boolean CheckOverlap(int s0, int s1, int e0, int e1){
//		int d0 = getDuration(s0,e0);
//		int d1 = getDuration(s1,e1);
//
//		if (s0 <= s1 && s0 + d0 >= s1) {
//			// startTime inside a previews interval
//			return true;
//		} else if (s0 <= e1 && s0 + d0 >= e1) {
//			// endTime inside a interval
//			return true;
//		} else if (d1 <= 0 && s0 < e1) {
//			// endTime in the next day
//			// compares if endTime will be after a startTime of other interval
//			// if true than it should fail
//			return true;
//		}
//		return false;
//	}



	public void UpdateTarget() {
		EditText name = (EditText) findViewById(R.id.et_TargetBG_Nome);
		EditText hourFrom = (EditText) findViewById(R.id.et_TargetBG_HourFrom);
		EditText hourTo = (EditText) findViewById(R.id.et_TargetBG_HourTo);
		EditText value = (EditText) findViewById(R.id.et_TargetBG_Glycemia);

		if (name.getText().toString().equals("")) {
			name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (hourFrom.getText().toString().equals("")) {
			hourFrom.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourFrom, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
//		if (hourTo.getText().toString().equals("")) {
//			hourTo.requestFocus();
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
//			return;
//		}
		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

//		if(GlicObjTimesOverlap(hourFrom.getText().toString(), hourTo.getText().toString())){
//
//			TextView errorLabel = (TextView) findViewById(R.id.targetGlicemiaErrorTV);
//			errorLabel.setVisibility(View.VISIBLE);
//			errorLabel.setText(R.string.targetOverlapError);
//			return;}

		DB_Write wdb = new DB_Write(this);

		InsulinTarget target = new InsulinTarget();

		target.setId(idTarget);
		target.setName(name.getText().toString());

//		if (!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")) {
		if (!hourFrom.getText().toString().equals("")) {
			target.setStart(hourFrom.getText().toString());
			//target.setEnd(hourTo.getText().toString());
		}

		target.setTarget(Double.valueOf(value.getText().toString()));

		wdb.Target_Update(target);
		wdb.close();
		finish();

	}


	public void DeleteTarget() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.targetbg_info))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Target_Remove(idTarget);
							goUp();
						} catch (Exception e) {
							Toast.makeText(c, getString(R.string.targetbg_delete_exception), Toast.LENGTH_LONG).show();
						}
						wdb.close();

					}
				})
				.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				}).show();
	}

	public void goUp() {
		//NavUtils.navigateUpFromSameTask(this);
		finish();
	}
}
