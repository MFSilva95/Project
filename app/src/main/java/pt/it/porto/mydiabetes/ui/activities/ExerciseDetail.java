package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;


public class ExerciseDetail extends BaseActivity {

	int idNote = 0;
	int idExercise = 0;

	public static boolean winBadge = false;
	public static boolean winDaily = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_detail);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();

		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		FillExerciseSpinner();
		FillEffortSpinner();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idExercise = Integer.parseInt(id);
			ExerciseRec toFill = rdb.ExerciseReg_GetById(Integer.parseInt(id));

			AutoCompleteTextView exerciseSpinner = (AutoCompleteTextView) findViewById(R.id.ac_ExerciseDetail_Exercise);
			exerciseSpinner.setText(toFill.getExercise());
			Spinner effortSpinner = (Spinner) findViewById(R.id.sp_ExerciseDetail_Effort);
//			SelectSpinnerItemByValue(effortSpinner, toFill.getEffort());
			SpinnerAdapter adapter = effortSpinner.getAdapter();
			effortSpinner.setSelection(toFill.getEffort());
			EditText duration = (EditText) findViewById(R.id.et_ExerciseDetail_Duration);
			duration.setText(String.valueOf(toFill.getDuration()));
			EditText data = (EditText) findViewById(R.id.et_ExerciseDetail_Data);
			data.setText(toFill.getFormattedDate());
			EditText hora = (EditText) findViewById(R.id.et_ExerciseDetail_Hora);
			hora.setText(toFill.getFormattedTime());

			EditText note = (EditText) findViewById(R.id.et_ExerciseDetail_Notes);
			if (toFill.getIdNote() != -1) {
				Note n = new Note();
				n = rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote = n.getId();
			}

			rdb.close();
		} else {
			FillDateHour();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.exercise_detail_delete, menu);
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UpdateExerciseRead(idExercise);
				}
			});
		} else {
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddExerciseRead();
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
			case R.id.menuItem_ExerciseDetail_Delete:
				DeleteExerciseRead(idExercise);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void FillDateHour() {
		EditText date = (EditText) findViewById(R.id.et_ExerciseDetail_Data);
		final Calendar calendar = Calendar.getInstance();
		date.setText(DateUtils.getFormattedDate(calendar));

		EditText hour = (EditText) findViewById(R.id.et_ExerciseDetail_Hora);
		hour.setText(DateUtils.getFormattedTime(calendar));
	}

	public void showDatePickerDialog(View v) {
		android.support.v4.app.DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_ExerciseDetail_Data,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void showTimePickerDialog(View v) {
		android.support.v4.app.DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_ExerciseDetail_Hora,
				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
		newFragment.show(getSupportFragmentManager(), "timePicker");

	}

	public void FillExerciseSpinner() {
		AutoCompleteTextView spinner = (AutoCompleteTextView) findViewById(R.id.ac_ExerciseDetail_Exercise);
		ArrayList<String> allExercises = new ArrayList<>();
		DB_Read rdb = new DB_Read(this);
		HashMap<Integer, String> val = rdb.Exercise_GetAll();
		rdb.close();

		if (val != null) {
			for (int i : val.keySet()) {
				allExercises.add(val.get(i));
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allExercises);
		spinner.setAdapter(adapter);
	}

	public void FillEffortSpinner() {
		Spinner sp_Exercise_Detail = (Spinner) findViewById(R.id.sp_ExerciseDetail_Effort);
		ArrayAdapter<CharSequence> adapter_sp_Exercise_Detail = ArrayAdapter.createFromResource(this, R.array.Effort, R.layout.spinner_text_layout);
		adapter_sp_Exercise_Detail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_Exercise_Detail.setAdapter(adapter_sp_Exercise_Detail);
	}

	public void AddExerciseRead() {
		AutoCompleteTextView exerciseSpinner = (AutoCompleteTextView) findViewById(R.id.ac_ExerciseDetail_Exercise);
		Spinner effortSpinner = (Spinner) findViewById(R.id.sp_ExerciseDetail_Effort);
		EditText duration = (EditText) findViewById(R.id.et_ExerciseDetail_Duration);
		EditText data = (EditText) findViewById(R.id.et_ExerciseDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_ExerciseDetail_Hora);
		EditText note = (EditText) findViewById(R.id.et_ExerciseDetail_Notes);

		//adicionado por zeornelas
		//para obrigar a colocar o valor dos hidratos e nao crashar
		if (exerciseSpinner.getText().toString().equals("")) {
			exerciseSpinner.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(exerciseSpinner, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (duration.getText().toString().equals("")) {
			duration.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(duration, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getUserId();

		DB_Write reg = new DB_Write(this);
		ExerciseRec ex = new ExerciseRec();


		//Get id of selected exercise
		String exercise = exerciseSpinner.getText().toString();
		Log.d("selected exercise", exercise);

		if (!rdb.Exercise_ExistName(exerciseSpinner.getText().toString())) {
			reg.Exercise_Add(exerciseSpinner.getText().toString());
		}


		if (!note.getText().toString().equals("")) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			ex.setIdNote(reg.Note_Add(n));
		}

		int effort = effortSpinner.getSelectedItemPosition();//.toString();

		ex.setIdUser(idUser);
		ex.setExercise(exerciseSpinner.getText().toString());
		ex.setDuration(Integer.parseInt(duration.getText().toString()));
		ex.setEffort(effort);
		// Add the current seconds to differentiate (and correctly order) entries on the same minute
		ex.setDateTime(data.getText().toString(), hora.getText().toString()+":" + Calendar.getInstance().get(Calendar.SECOND));
		reg.Exercise_Save(ex);

		winBadge = BadgeUtils.addExerciseBadge(getBaseContext(),rdb);
		winDaily = BadgeUtils.addDailyBadge(getBaseContext(), rdb, reg);
		LevelsPointsUtils.addPoints(getBaseContext(), LevelsPointsUtils.RECORD_POINTS, "exercise", rdb);

		reg.close();
		rdb.close();

		finish();
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

	public void DeleteExerciseRead(final int id) {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.deleteReading))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Exercise_Delete(id);
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


	public void UpdateExerciseRead(int id) {
		AutoCompleteTextView exerciseSpinner = (AutoCompleteTextView) findViewById(R.id.ac_ExerciseDetail_Exercise);
		Spinner effortSpinner = (Spinner) findViewById(R.id.sp_ExerciseDetail_Effort);
		EditText duration = (EditText) findViewById(R.id.et_ExerciseDetail_Duration);
		EditText data = (EditText) findViewById(R.id.et_ExerciseDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_ExerciseDetail_Hora);
		EditText note = (EditText) findViewById(R.id.et_ExerciseDetail_Notes);

		//adicionado por zeornelas
		//para obrigar a colocar o valor dos hidratos e nao crashar
		if (exerciseSpinner.getText().toString().equals("")) {
			exerciseSpinner.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(exerciseSpinner, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (duration.getText().toString().equals("")) {
			duration.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(duration, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		DB_Write wdb = new DB_Write(this);
		int idUser = rdb.getUserId();
		if (!rdb.Exercise_ExistName(exerciseSpinner.getText().toString())) {
			wdb.Exercise_Add(exerciseSpinner.getText().toString());
		}

		ExerciseRec toUpdate = new ExerciseRec();

		if (!note.getText().toString().equals("") && idNote == 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			toUpdate.setIdNote(wdb.Note_Add(n));
		}
		if (idNote != 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}

		toUpdate.setId(id);
		toUpdate.setIdUser(idUser);
		toUpdate.setDuration(Integer.parseInt(duration.getText().toString()));
		// Add the current seconds to differentiate (and correctly order) entries on the same minute
		toUpdate.setDateTime(data.getText().toString(), hora.getText().toString()+":" + Calendar.getInstance().get(Calendar.SECOND));
		toUpdate.setEffort(effortSpinner.getSelectedItemPosition());
		toUpdate.setExercise(exerciseSpinner.getText().toString());

		wdb.Exercise_Update(toUpdate);
		wdb.close();
		rdb.close();

		finish();
	}


}
