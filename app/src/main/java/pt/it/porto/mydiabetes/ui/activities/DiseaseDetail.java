package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Disease;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;


public class DiseaseDetail extends BaseActivity {

	int idDisease = 0;
	int idNote = 0;

	@Override
	public String getRegType(){return "Disease";}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disease_detail);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		FillDiseaseAC();


		Bundle args = getIntent().getExtras();
		if (args != null) {
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idDisease = Integer.parseInt(id);
			DiseaseRec toFill = rdb.DiseaseReg_GetById(Integer.parseInt(id));

			AutoCompleteTextView diseaseSpinner = (AutoCompleteTextView) findViewById(R.id.ac_DiseaseRegDetail_Disease);
            EditText dataFrom = (EditText) findViewById(R.id.et_DiseaseRegDetail_DataFrom);
			EditText dataTo = (EditText) findViewById(R.id.et_DiseaseRegDetail_DataTo);
			EditText note = (EditText) findViewById(R.id.et_DiseaseRegDetail_Notes);
			diseaseSpinner.setText(toFill.getDisease());
			dataFrom.setText(toFill.getFormattedDate());
			dataTo.setText((toFill.getEndDate() != null) ? toFill.getEndDate() : "");
			if (toFill.getIdNote() != -1) {
				Note n = new Note();
				n = rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote = n.getId();
			}
			rdb.close();
		} else {
			FillDateFrom();
		}


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.disease_detail_delete, menu);
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					UpdateDiseaseRead();
				}
			});
		} else {
			FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
			fab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AddDiseaseRead();
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
			case R.id.menuItem_DiseaseRegDetail_Delete:
				DeleteDiseaseRead();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDatePickerDialogFrom(View v) {
		DialogFragment newFragment =  DatePickerFragment.getDatePickerFragment(R.id.et_DiseaseRegDetail_DataFrom,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showDatePickerDialogTo(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_DiseaseRegDetail_DataTo,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void FillDiseaseAC() {
		AutoCompleteTextView spinner = (AutoCompleteTextView) findViewById(R.id.ac_DiseaseRegDetail_Disease);
		ArrayList<String> allDiseases = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<Disease> val = rdb.Disease_GetAll();
		rdb.close();

		if (val != null) {
			for (Disease d : val) {
				allDiseases.add(d.getName());
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allDiseases);
		spinner.setAdapter(adapter);
	}

	public void FillDateFrom() {
		EditText date = (EditText) findViewById(R.id.et_DiseaseRegDetail_DataFrom);
		final Calendar calendar = Calendar.getInstance();
		date.setText(DateUtils.getFormattedDate(calendar));
	}

	public void AddDiseaseRead() {
		AutoCompleteTextView diseaseSpinner = (AutoCompleteTextView) findViewById(R.id.ac_DiseaseRegDetail_Disease);
		EditText dataFrom = (EditText) findViewById(R.id.et_DiseaseRegDetail_DataFrom);
		EditText dataTo = (EditText) findViewById(R.id.et_DiseaseRegDetail_DataTo);
		EditText note = (EditText) findViewById(R.id.et_DiseaseRegDetail_Notes);

		if (diseaseSpinner.getText().toString().equals("")) {
			diseaseSpinner.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(diseaseSpinner, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getId();

		//Get id of selected exercise
		String disease = diseaseSpinner.getText().toString();
		Log.d("selected disease", disease);
		DB_Write reg = new DB_Write(this);
		if (!rdb.Disease_ExistName(diseaseSpinner.getText().toString())) {
			reg.Disease_Add(diseaseSpinner.getText().toString());
		}

		DiseaseRec dis = new DiseaseRec();

		if (!note.getText().toString().equals("")) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			dis.setIdNote(reg.Note_Add(n));
		}

		dis.setIdUser(idUser);
		dis.setDisease(disease);
		Calendar calendar = Calendar.getInstance();
		dis.setDateTime(dataFrom.getText().toString(), DateUtils.getFormattedTime(calendar)+":" + Calendar.getInstance().get(Calendar.SECOND));
		dis.setEndDate((!dataTo.getText().toString().equals("")) ? dataTo.getText().toString() : null);
		reg.DiseaseReg_Save(dis);

		BadgeUtils.addDiseaseBadge(getBaseContext(), rdb);
		BadgeUtils.addDailyBadge(getBaseContext(), rdb, reg);
		LevelsPointsUtils.addPoints(getBaseContext(), LevelsPointsUtils.RECORD_POINTS, "disease", rdb);

		reg.close();
		rdb.close();

		finish();
	}

	public void DeleteDiseaseRead() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.deleteReading))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.DiseaseReg_Delete(idDisease);
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

	public void UpdateDiseaseRead() {
		AutoCompleteTextView diseaseSpinner = (AutoCompleteTextView) findViewById(R.id.ac_DiseaseRegDetail_Disease);
		EditText dataFrom = (EditText) findViewById(R.id.et_DiseaseRegDetail_DataFrom);
		EditText dataTo = (EditText) findViewById(R.id.et_DiseaseRegDetail_DataTo);
		EditText note = (EditText) findViewById(R.id.et_DiseaseRegDetail_Notes);

		if (diseaseSpinner.getText().toString().equals("")) {
			diseaseSpinner.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(diseaseSpinner, InputMethodManager.SHOW_IMPLICIT);
			return;
		}


		DB_Write wdb = new DB_Write(this);

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		int idUser = rdb.getId();

		DiseaseRec dis = new DiseaseRec();

		if (!note.getText().toString().equals("") && idNote == 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			dis.setIdNote(wdb.Note_Add(n));
		}
		if (idNote != 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			wdb.Note_Update(n);
		}

		//Get id of selected exercise
		String disease = diseaseSpinner.getText().toString();
		if (!rdb.Disease_ExistName(diseaseSpinner.getText().toString())) {
			wdb.Disease_Add(diseaseSpinner.getText().toString());
		}


		dis.setId(idDisease);
		dis.setIdUser(idUser);
		dis.setDisease(disease);
		Calendar calendar = Calendar.getInstance();
		dis.setDateTime(dataFrom.getText().toString(), DateUtils.getFormattedTime(calendar)+":" + Calendar.getInstance().get(Calendar.SECOND));
		dis.setEndDate((!dataTo.getText().toString().equals("")) ? dataTo.getText().toString() : null);

		wdb.DiseaseReg_Update(dis);

		wdb.close();
		rdb.close();

		finish();
	}
}
