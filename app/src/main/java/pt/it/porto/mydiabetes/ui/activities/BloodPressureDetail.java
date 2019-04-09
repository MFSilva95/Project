package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;


public class BloodPressureDetail extends BaseActivity {

	int idBP = 0;
	int idNote = 0;
	@Override
	public String getRegType(){return "BloodPressure";}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blood_pressure_detail);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		FillTagSpinner();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idBP = Integer.parseInt(id);

			BloodPressureRec toFill = rdb.BloodPressure_GetById(Integer.parseInt(id));

			Spinner tagSpinner = (Spinner) findViewById(R.id.sp_BloodPressureDetail_Tag);
			SelectSpinnerItemByValue(tagSpinner, rdb.Tag_GetById(toFill.getIdTag()).getName());
			EditText systolic = (EditText) findViewById(R.id.et_BloodPressureDetail_Systolic);
			systolic.setText(String.valueOf(toFill.getSystolic()));
			EditText diastolic = (EditText) findViewById(R.id.et_BloodPressureDetail_Diastolic);
			diastolic.setText(String.valueOf(toFill.getDiastolic()));
			EditText data = (EditText) findViewById(R.id.et_BloodPressureDetail_Data);
			data.setText(toFill.getFormattedDate());
			EditText hora = (EditText) findViewById(R.id.et_BloodPressureDetail_Hora);
			hora.setText(toFill.getFormattedTime());
			EditText note = (EditText) findViewById(R.id.et_BloodPressureDetail_Notes);
			if (toFill.getIdNote() != -1) {
				Note n = new Note();
				n = rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote = n.getId();
			}
			rdb.close();
		} else {
			FillDateHour();
			SetTagByTime();
		}


		EditText hora = (EditText) findViewById(R.id.et_BloodPressureDetail_Hora);
		hora.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				SetTagByTime();
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
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.blood_pressure_detail_delete, menu);
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UpdateBloodPressureRead();
				}
			});
		} else {
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddBloodPressureRead();
				}
			});
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.menuItem_BloodPressureDetail_Delete:
				DeleteBloodPressureRead();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void FillDateHour() {
		EditText date = (EditText) findViewById(R.id.et_BloodPressureDetail_Data);
		final Calendar calendar = Calendar.getInstance();
		date.setText(DateUtils.getFormattedDate(calendar));

		EditText hour = (EditText) findViewById(R.id.et_BloodPressureDetail_Hora);
		hour.setText(DateUtils.getFormattedTime(calendar));
	}

	public void showDatePickerDialog(View v) {
		android.support.v4.app.DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_BloodPressureDetail_Data,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void showTimePickerDialog(View v) {
		android.support.v4.app.DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_BloodPressureDetail_Hora,
				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}

	public void FillTagSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_BloodPressureDetail_Tag);
		ArrayList<String> allTags = new ArrayList<>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<Tag> t = rdb.Tag_GetAll();
		rdb.close();


		if (t != null) {
			for (Tag i : t) {
				allTags.add(i.getName());
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text_layout, allTags);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	public static void SelectSpinnerItemByValue(Spinner spnr, String value) {
		SpinnerAdapter adapter = spnr.getAdapter();
		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItem(position).equals(value)) {
				spnr.setSelection(position);
				return;
			}
		}
	}

	public void SetTagByTime() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_BloodPressureDetail_Tag);
		EditText hora = (EditText) findViewById(R.id.et_BloodPressureDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		SelectSpinnerItemByValue(tagSpinner, name);
	}

	public void AddBloodPressureRead() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_BloodPressureDetail_Tag);
		EditText systolic = (EditText) findViewById(R.id.et_BloodPressureDetail_Systolic);
		EditText diastolic = (EditText) findViewById(R.id.et_BloodPressureDetail_Diastolic);
		EditText data = (EditText) findViewById(R.id.et_BloodPressureDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_BloodPressureDetail_Hora);
		EditText note = (EditText) findViewById(R.id.et_BloodPressureDetail_Notes);

		if (systolic.getText().toString().equals("")) {
			systolic.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(systolic, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (diastolic.getText().toString().equals("")) {
			diastolic.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(diastolic, InputMethodManager.SHOW_IMPLICIT);
			return;
		}


		DB_Write wdb = new DB_Write(this);

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getUserId();

		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
//		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);

		BloodPressureRec bp = new BloodPressureRec();

		if (!note.getText().toString().equals("")) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			bp.setIdNote(wdb.Note_Add(n));
		}


		bp.setIdUser(idUser);
		bp.setSystolic(Integer.parseInt(systolic.getText().toString()));
		bp.setDiastolic(Integer.parseInt(diastolic.getText().toString()));
		// Add the current seconds to differentiate (and correctly order) entries on the same minute
		bp.setDateTime(data.getText().toString(), hora.getText().toString()+":" + Calendar.getInstance().get(Calendar.SECOND));
//		Log.e("REG TIME FINAL", hora.getText().toString()+":"+Calendar.getInstance().get(Calendar.SECOND));
		bp.setIdTag(idTag);

		wdb.BloodPressure_Save(bp);
		BadgeUtils.addBpBadge(getBaseContext(), rdb);
		BadgeUtils.addDailyBadge(getBaseContext(), rdb, wdb);

		LevelsPointsUtils.addPoints(getBaseContext(), LevelsPointsUtils.RECORD_POINTS, "bp",rdb);

		wdb.close();
		rdb.close();
		finish();


	}

	public void UpdateBloodPressureRead() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_BloodPressureDetail_Tag);
		EditText systolic = (EditText) findViewById(R.id.et_BloodPressureDetail_Systolic);
		EditText diastolic = (EditText) findViewById(R.id.et_BloodPressureDetail_Diastolic);
		EditText data = (EditText) findViewById(R.id.et_BloodPressureDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_BloodPressureDetail_Hora);
		EditText note = (EditText) findViewById(R.id.et_BloodPressureDetail_Notes);

		if (systolic.getText().toString().equals("")) {
			systolic.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(systolic, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (diastolic.getText().toString().equals("")) {
			diastolic.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(diastolic, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		DB_Write wdb = new DB_Write(this);
		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getUserId();
		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
//		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);

		BloodPressureRec bp = new BloodPressureRec();

		if (!note.getText().toString().equals("") && idNote == 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			bp.setIdNote(wdb.Note_Add(n));
		}
		if (idNote != 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}

		bp.setId(idBP);
		bp.setIdUser(idUser);
		bp.setSystolic(Integer.parseInt(systolic.getText().toString()));
		bp.setDiastolic(Integer.parseInt(diastolic.getText().toString()));
		bp.setDateTime(data.getText().toString(), hora.getText().toString()+":" + Calendar.getInstance().get(Calendar.SECOND));
		bp.setIdTag(idTag);

		wdb.BloodPressure_Update(bp);

		wdb.close();
		rdb.close();
		finish();
	}

	public void DeleteBloodPressureRead() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.deleteReading))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.BloodPressure_Delete(idBP);
							finish();
						} catch (Exception e) {
							Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
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
}
