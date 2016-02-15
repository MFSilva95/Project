package pt.it.porto.mydiabetes.ui.activities;

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

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dataBinding.NoteDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.WeightDataBinding;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class WeightDetail extends BaseOldActivity {

	int idWeight = 0;
	int idNote = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weight_detail);
		// Show the Up button in the action bar.
		getActionBar();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idWeight = Integer.parseInt(id);
			WeightDataBinding toFill = rdb.Weight_GetById(Integer.parseInt(id));

			EditText value = (EditText) findViewById(R.id.et_WeightDetail_Value);
			value.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", toFill.getValue()));
			EditText data = (EditText) findViewById(R.id.et_WeightDetail_Data);
			data.setText(toFill.getFormattedDate());
			EditText hora = (EditText) findViewById(R.id.et_WeightDetail_Hora);
			hora.setText(toFill.getFormattedTime());
			EditText note = (EditText) findViewById(R.id.et_WeightDetail_Notes);
			if (toFill.getIdNote() != -1) {
				NoteDataBinding n = rdb.Note_GetById(toFill.getIdNote());
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
			inflater.inflate(R.menu.weight_detail_edit, menu);
		} else {
			inflater.inflate(R.menu.weight_detail, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_WeightDetail_Save:
				AddWeightRead();
				return true;
			case R.id.menuItem_WeightDetail_Delete:
				DeleteWeightRead();
				return true;
			case R.id.menuItem_WeightDetail_EditSave:
				UpdateWeightRead();
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
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_WeightDetail_Data,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_WeightDetail_Hora,
				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "timePicker");

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
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		WeightDataBinding weight = new WeightDataBinding();

		if (!note.getText().toString().equals("")) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			weight.setIdNote(wdb.Note_Add(n));
		}


		weight.setIdUser(idUser);
		weight.setValue(Double.parseDouble(value.getText().toString()));
		weight.setDateTime(data.getText().toString(), hora.getText().toString());

		wdb.Weight_Save(weight);

		wdb.close();
		rdb.close();

		goUp();
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
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		WeightDataBinding cho = new WeightDataBinding();

		if (!note.getText().toString().equals("") && idNote == 0) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			cho.setIdNote(wdb.Note_Add(n));
		}
		if (idNote != 0) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}

		cho.setId(idWeight);
		cho.setIdUser(idUser);
		cho.setValue(Double.parseDouble(value.getText().toString()));
		cho.setDateTime(data.getText().toString(), hora.getText().toString());

		wdb.Weight_Update(cho);

		wdb.close();
		rdb.close();
		goUp();
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
							goUp();
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

	public void goUp() {
		NavUtils.navigateUpFromSameTask(this);
	}

}
