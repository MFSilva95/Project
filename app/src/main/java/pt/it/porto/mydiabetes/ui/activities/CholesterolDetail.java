package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
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
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class CholesterolDetail extends BaseActivity {

	int idCho = 0;
	int idNote = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cholesterol_detail);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		Bundle args = getIntent().getExtras();
		if (args != null) {
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idCho = Integer.parseInt(id);
			CholesterolRec toFill = rdb.Cholesterol_GetById(Integer.parseInt(id));

			EditText value = (EditText) findViewById(R.id.et_CholesterolDetail_Value);
			value.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", toFill.getValue()));
			EditText data = (EditText) findViewById(R.id.et_CholesterolDetail_Data);
			data.setText(toFill.getFormattedDate());
			EditText hora = (EditText) findViewById(R.id.et_CholesterolDetail_Hora);
			hora.setText(toFill.getFormattedTime());
			EditText note = (EditText) findViewById(R.id.et_CholesterolDetail_Notes);
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
			inflater.inflate(R.menu.cholesterol_detail_delete, menu);
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UpdateCholesterolRead();
				}
			});
		} else {
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddCholesterolRead();
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
			case R.id.menuItem_CholesterolDetail_Delete:
				DeleteCholesterolRead();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void FillDateHour() {
		EditText date = (EditText) findViewById(R.id.et_CholesterolDetail_Data);
		final Calendar calendar = Calendar.getInstance();
		date.setText(DateUtils.getFormattedDate(calendar));

		EditText hour = (EditText) findViewById(R.id.et_CholesterolDetail_Hora);
		hour.setText(DateUtils.getFormattedTime(calendar));
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_CholesterolDetail_Data,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_CholesterolDetail_Hora,
				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "timePicker");

	}

	public void AddCholesterolRead() {
		EditText value = (EditText) findViewById(R.id.et_CholesterolDetail_Value);
		EditText data = (EditText) findViewById(R.id.et_CholesterolDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_CholesterolDetail_Hora);
		EditText note = (EditText) findViewById(R.id.et_CholesterolDetail_Notes);

		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}


		DB_Write wdb = new DB_Write(this);

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getId();

		CholesterolRec cho = new CholesterolRec();

		if (!note.getText().toString().equals("")) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			cho.setIdNote(wdb.Note_Add(n));
		}


		cho.setIdUser(idUser);
		cho.setValue(Double.parseDouble(value.getText().toString()));
		cho.setDateTime(data.getText().toString(), hora.getText().toString()+":" + Calendar.getInstance().get(Calendar.SECOND));
		wdb.Cholesterol_Save(cho);

		BadgeUtils.addCholesterolBadge(getBaseContext());
        BadgeUtils.addDailyBadge(getBaseContext(), rdb, wdb);
		LevelsPointsUtils.addPoints(getBaseContext(), LevelsPointsUtils.RECORD_POINTS, "cholesterol");

		wdb.close();
		rdb.close();

		finish();
	}

	public void UpdateCholesterolRead() {
		EditText value = (EditText) findViewById(R.id.et_CholesterolDetail_Value);
		EditText data = (EditText) findViewById(R.id.et_CholesterolDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_CholesterolDetail_Hora);
		EditText note = (EditText) findViewById(R.id.et_CholesterolDetail_Notes);

		if (value.getText().toString().equals("")) {
			value.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(value, InputMethodManager.SHOW_IMPLICIT);
			return;
		}


		DB_Write wdb = new DB_Write(this);

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getId();

		CholesterolRec cho = new CholesterolRec();

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

		cho.setId(idCho);
		cho.setIdUser(idUser);
		cho.setValue(Double.parseDouble(value.getText().toString()));
		cho.setDateTime(data.getText().toString(), hora.getText().toString()+":" + Calendar.getInstance().get(Calendar.SECOND));

		wdb.Cholesterol_Update(cho);

		wdb.close();
		rdb.close();

		finish();
	}

	public void DeleteCholesterolRead() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.deleteReading))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Cholesterol_Delete(idCho);
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
