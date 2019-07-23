package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class WeightDetail extends BaseActivity {

	int idWeight = 0;
	int idNote = 0;

	public static boolean winBadge = false;
	public static boolean winDaily = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weight_detail);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		Bundle args = getIntent().getExtras();
		if (args != null) {
			DB_Read rdb = new DB_Read(this);
			idWeight  = args.getInt("Id");
			WeightRec toFill = rdb.Weight_GetById(idWeight);

			EditText value = (EditText) findViewById(R.id.et_WeightDetail_Value);
			value.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", toFill.getValue()));
			EditText data = (EditText) findViewById(R.id.et_WeightDetail_Data);
			data.setText(toFill.getFormattedDate());
			EditText hora = (EditText) findViewById(R.id.et_WeightDetail_Hora);
			hora.setText(toFill.getFormattedTime());
			EditText note = (EditText) findViewById(R.id.et_WeightDetail_Notes);
			if (toFill.getIdNote() != -1) {
				Note n = rdb.Note_GetById(toFill.getIdNote());
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
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.weight_detail_delete, menu);
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UpdateWeightRead();
				}
			});
		} else {
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddWeightRead();
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
			case R.id.menuItem_WeightDetail_Delete:
				DeleteWeightRead();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void FillDateHour() {
		EditText date = (EditText) findViewById(R.id.et_WeightDetail_Data);
		final Calendar calendar = Calendar.getInstance();
		date.setText(DateUtils.getFormattedDate(calendar));

		EditText hour = (EditText) findViewById(R.id.et_WeightDetail_Hora);
		hour.setText(DateUtils.getFormattedTime(calendar));
	}

	public void showDatePickerDialog(View v) {
		android.support.v4.app.DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_WeightDetail_Data,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void showTimePickerDialog(View v) {
		android.support.v4.app.DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_WeightDetail_Hora,
				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}

	public void AddWeightRead() {
		EditText value = (EditText) findViewById(R.id.et_WeightDetail_Value);
		EditText data = (EditText) findViewById(R.id.et_WeightDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_WeightDetail_Hora);
		EditText note = (EditText) findViewById(R.id.et_WeightDetail_Notes);

		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		DB_Write wdb = new DB_Write(this);

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getUserId();

		WeightRec weight = new WeightRec();

		if (!note.getText().toString().equals("")) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			weight.setIdNote(wdb.Note_Add(n));
		}


		weight.setIdUser(idUser);
		weight.setValue(Double.parseDouble(value.getText().toString()));
		weight.setDateTime(data.getText().toString(), hora.getText().toString()+":" + Calendar.getInstance().get(Calendar.SECOND));
		wdb.Weight_Save(weight);

		winBadge = BadgeUtils.addWeightBadge(getBaseContext(), rdb);
		winDaily = BadgeUtils.addDailyBadge(getBaseContext(), rdb, wdb);
		LevelsPointsUtils.addPoints(getBaseContext(), LevelsPointsUtils.RECORD_POINTS, "weight", rdb);

		wdb.close();
		rdb.close();

		finish();
	}

	public void UpdateWeightRead() {
		EditText value = (EditText) findViewById(R.id.et_WeightDetail_Value);
		EditText data = (EditText) findViewById(R.id.et_WeightDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_WeightDetail_Hora);
		EditText note = (EditText) findViewById(R.id.et_WeightDetail_Notes);

		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		DB_Write wdb = new DB_Write(this);

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getUserId();

		WeightRec cho = new WeightRec();

		if (!note.getText().toString().equals("") && idNote == 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			cho.setIdNote(wdb.Note_Add(n));
		}
		if (idNote != 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}

		cho.setId(idWeight);
		cho.setIdUser(idUser);
		cho.setValue(Double.parseDouble(value.getText().toString()));
		cho.setDateTime(data.getText().toString(), hora.getText().toString()+":" + Calendar.getInstance().get(Calendar.SECOND));
		wdb.Weight_Update(cho);

		wdb.close();
		rdb.close();
		finish();
	}

	public void DeleteWeightRead() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.deleteReading))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Weight_Delete(idWeight);
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
