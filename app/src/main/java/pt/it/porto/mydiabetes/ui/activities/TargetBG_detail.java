package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.dataBinding.TargetDataBinding;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class TargetBG_detail extends Activity {

	public static final String BUNDLE_GOAL = "GOAL";
	public static final String BUNDLE_ID = "Id";
	public static final String BUNDLE_DATA = "Data";

	int idTarget = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target_bg_detail);
		// Show the Up button in the action bar.
		getActionBar();


		Bundle args = getIntent().getExtras();
		if (args != null) {
			TargetDataBinding toFill = null;
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
				EditText to = (EditText) findViewById(R.id.et_TargetBG_HourTo);
				to.setText(toFill.getEnd());
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
			inflater.inflate(R.menu.target_bg_detail_edit, menu);
		} else {
			inflater.inflate(R.menu.target_bg_detail, menu);
		}

		//getSupportMenuInflater().inflate(R.menu.tag_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_TargetBGDetail_Save:
				AddNewTarget();
				//Intent data = new Intent();
				//data.putExtra("tabPosition", 2);
				//setResult(RESULT_OK, data);
				//NavUtils.navigateUpFromSameTask(this);

				return true;
			case R.id.menuItem_TargetBGDetail_EditSave:
				UpdateTarget();
				return true;
			case R.id.menuItem_TargetBGDetail_Delete:
				DeleteTarget();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}


	public void showTimePickerDialogFrom(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_TargetBG_HourFrom,
				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void showTimePickerDialogTo(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_TargetBG_HourTo,
				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void AddNewTarget() {
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
		if (hourTo.getText().toString().equals("")) {
			hourTo.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		DB_Write wdb = new DB_Write(this);

		TargetDataBinding target = new TargetDataBinding();


		target.setName(name.getText().toString());

		if (!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")) {
			target.setStart(hourFrom.getText().toString());
			target.setEnd(hourTo.getText().toString());
		}

		target.setTarget(Double.valueOf(value.getText().toString()));

		wdb.Target_Add(target);
		wdb.close();
		finish();
	}

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
		if (hourTo.getText().toString().equals("")) {
			hourTo.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hourTo, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		DB_Write wdb = new DB_Write(this);

		TargetDataBinding target = new TargetDataBinding();

		target.setId(idTarget);
		target.setName(name.getText().toString());

		if (!hourFrom.getText().toString().equals("") && !hourTo.getText().toString().equals("")) {
			target.setStart(hourFrom.getText().toString());
			target.setEnd(hourTo.getText().toString());
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
