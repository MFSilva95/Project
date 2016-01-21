package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.NoteDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.TagDataBinding;


public class InsulinDetail extends Activity {

	int id_BG = 0;
	int idNote = 0;
	int idIns = 0;
	ArrayList<String> allInsulins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insulin_detail);
		// Show the Up button in the action bar.
		DB_Read read = new DB_Read(this);
		if (!read.Insulin_HasInsulins()) {
			ShowDialogAddInsulin();
		}
		read.close();


		getActionBar();
		FillTagSpinner();
		FillInsulinSpinner();
		EditText hora = (EditText) findViewById(R.id.et_InsulinDetail_Hora);

		Bundle args = getIntent().getExtras();
		if (args != null) {
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idIns = Integer.parseInt(id);
			InsulinRegDataBinding toFill = rdb.InsulinReg_GetById(Integer.parseInt(id));

			Spinner tagSpinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Tag);
			SelectSpinnerItemByValue(tagSpinner, rdb.Tag_GetById(toFill.getIdTag()).getName());
			Spinner insulinSpinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Insulin);
			SelectSpinnerItemByValue(insulinSpinner, rdb.Insulin_GetById(toFill.getIdInsulin()).getName());

			EditText data = (EditText) findViewById(R.id.et_InsulinDetail_Data);
			data.setText(toFill.getDate());
			hora.setText(toFill.getTime());
			EditText target = (EditText) findViewById(R.id.et_InsulinDetail_TargetGlycemia);
			target.setText(String.valueOf(toFill.getTargetGlycemia()));
			EditText insulinunits = (EditText) findViewById(R.id.et_InsulinDetail_InsulinUnits);
			insulinunits.setText(String.valueOf(toFill.getInsulinUnits()));
			EditText note = (EditText) findViewById(R.id.et_InsulinDetail_Notes);

			//If exists glycemia read fill fields
			EditText glycemia = (EditText) findViewById(R.id.et_InsulinDetail_Glycemia);
			id_BG = toFill.getIdBloodGlucose();
			if (id_BG != -1) {
				id_BG = toFill.getIdBloodGlucose();
				GlycemiaDataBinding g = rdb.Glycemia_GetById(id_BG);
				glycemia.setText(String.valueOf(g.getValue()));
			}

			if (toFill.getIdNote() != -1) {
				NoteDataBinding n = new NoteDataBinding();
				n = rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote = n.getId();
			}

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

			rdb.close();
		} else {
			FillDateHour();
			SetTargetByHour();
			SetTagByTime();


			//Get id of user 
			DB_Read rdb = new DB_Read(this);
			Object[] obj = rdb.MyData_Read();
			final double iRatio = Double.valueOf(obj[3].toString());
			rdb.close();

			final EditText insulinunits = (EditText) findViewById(R.id.et_InsulinDetail_InsulinUnits);
			final EditText target = (EditText) findViewById(R.id.et_InsulinDetail_TargetGlycemia);
			final EditText glycemia = (EditText) findViewById(R.id.et_InsulinDetail_Glycemia);

			target.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (!target.getText().toString().equals("") && !glycemia.getText().toString().equals("")) {
						Double gli = Double.parseDouble(glycemia.getText().toString());
						Double tar = Double.parseDouble(target.getText().toString());
						Double result = (gli - tar) / iRatio;
						result = 0.5 * Math.round(result / 0.5);
						if (result < 0) {
							result = 0.0;
						}
						Log.d("resultado", result.toString());
						insulinunits.setText(String.valueOf(result));
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});


			glycemia.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (!target.getText().toString().equals("") && !glycemia.getText().toString().equals("")) {
						Double gli = Double.parseDouble(glycemia.getText().toString());
						Double tar = Double.parseDouble(target.getText().toString());
						Double result = (gli - tar) / iRatio;
						result = 0.5 * Math.round(result / 0.5);
						if (result < 0) {
							result = 0.0;
						}
						Log.d("resultado", result.toString());
						insulinunits.setText(String.valueOf(result));
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});


			hora.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					SetTargetByHour();
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


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.insulin_detail_edit, menu);
		} else {
			inflater.inflate(R.menu.insulin_detail, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_InsulinDetail_Save:
				AddInsulinRead();
				return true;
			case R.id.menuItem_InsulinDetail_Delete:
				DeleteInsulinRead();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_InsulinDetail_EditSave:
				UpdateInsulinRead();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_InsulinDetail_Data,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_InsulinDetail_Hora,
				TimePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void FillTagSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Tag);
		ArrayList<String> allTags = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<TagDataBinding> t = rdb.Tag_GetAll();
		rdb.close();


		if (t != null) {
			for (TagDataBinding i : t) {
				allTags.add(i.getName());
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allTags);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	public void FillInsulinSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Insulin);
		allInsulins = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		HashMap<Integer, String> val = rdb.Insulin_GetAllNames();
		rdb.close();

		if (val != null) {
			for (int i : val.keySet()) {
				allInsulins.add(val.get(i));
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allInsulins);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	public void FillDateHour() {
		EditText date = (EditText) findViewById(R.id.et_InsulinDetail_Data);
		final Calendar calendar = Calendar.getInstance();
		date.setText(DatePickerFragment.getFormatedDate(calendar));

		EditText hour = (EditText) findViewById(R.id.et_InsulinDetail_Hora);
		hour.setText(TimePickerFragment.getFormatedDate(calendar));
	}

	public void SetTargetByHour() {
		EditText target = (EditText) findViewById(R.id.et_InsulinDetail_TargetGlycemia);
		EditText hora = (EditText) findViewById(R.id.et_InsulinDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		double d = rdb.Target_GetTargetByTime(hora.getText().toString());
		if (d != 0) {
			target.setText(String.valueOf(d));
		}
		rdb.close();
	}

	public void SetTagByTime() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Tag);
		EditText hora = (EditText) findViewById(R.id.et_InsulinDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		SelectSpinnerItemByValue(tagSpinner, name);
	}

	public void ShowDialogAddInsulin() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.title_activity_info))
				.setMessage(getString(R.string.insulin_info_insulins))
				.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						Intent intent = new Intent(c, Preferences.class);
						intent.putExtra("tabPosition", 4);
						startActivity(intent);
						end();
					}
				}).show();
	}

	public void ShowDialogAddTarget() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.title_activity_info))
				.setMessage(getString(R.string.insulin_info_target))
				.setPositiveButton(getString(R.string.okButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						Intent intent = new Intent(c, Preferences.class);
						intent.putExtra("tabPosition", 1);
						startActivity(intent);
						finish();
					}
				}).show();
	}


	public void AddInsulinRead() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Tag);
		Spinner insulinSpinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Insulin);
		EditText glycemia = (EditText) findViewById(R.id.et_InsulinDetail_Glycemia);
		EditText data = (EditText) findViewById(R.id.et_InsulinDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_InsulinDetail_Hora);
		EditText target = (EditText) findViewById(R.id.et_InsulinDetail_TargetGlycemia);
		EditText insulinunits = (EditText) findViewById(R.id.et_InsulinDetail_InsulinUnits);
		EditText note = (EditText) findViewById(R.id.et_InsulinDetail_Notes);
		//tem de ter um target inserido
		DB_Read read = new DB_Read(this);
		if (!read.Target_HasTargets()) {
			read.close();
			ShowDialogAddTarget();
			return;
		}

		if (insulinunits.getText().toString().equals("")) {
			insulinunits.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(insulinunits, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);

		//Get id of selected insulin
		String insulin = insulinSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", insulin);
		int idInsulin = rdb.Insulin_GetByName(insulin).getId();


		int idGlycemia = 0;
		boolean hasGlycemia = false;
		DB_Write reg = new DB_Write(this);

		InsulinRegDataBinding ins = new InsulinRegDataBinding();

		int idnote = 0;

		if (!note.getText().toString().equals("")) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			idnote = reg.Note_Add(n);
			ins.setIdNote(idnote);
		}

		if (!glycemia.getText().toString().equals("")) {
			GlycemiaDataBinding gly = new GlycemiaDataBinding();

			gly.setIdUser(idUser);
			gly.setValue(Integer.parseInt(glycemia.getText().toString()));
			gly.setDate(data.getText().toString());
			gly.setTime(hora.getText().toString());
			gly.setIdTag(idTag);
			if (idnote > 0) {
				gly.setIdNote(idnote);
			}

			idGlycemia = reg.Glycemia_Save(gly);
			Log.d("id glycemia", String.valueOf(idGlycemia));
			hasGlycemia = true;
		}

		ins.setIdUser(idUser);
		ins.setIdInsulin(idInsulin);
		ins.setIdBloodGlucose(hasGlycemia ? idGlycemia : -1);
		ins.setDate(data.getText().toString());
		ins.setTime(hora.getText().toString());
		ins.setTargetGlycemia(Integer.parseInt(target.getText().toString()));
		ins.setInsulinUnits(Float.parseFloat(insulinunits.getText().toString()));
		ins.setIdTag(idTag);


		reg.Insulin_Save(ins);

		reg.close();
		rdb.close();

		goUp();
	}

	public void UpdateInsulinRead() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Tag);
		Spinner insulinSpinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Insulin);
		EditText glycemia = (EditText) findViewById(R.id.et_InsulinDetail_Glycemia);
		EditText data = (EditText) findViewById(R.id.et_InsulinDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_InsulinDetail_Hora);
		EditText target = (EditText) findViewById(R.id.et_InsulinDetail_TargetGlycemia);
		EditText insulinunits = (EditText) findViewById(R.id.et_InsulinDetail_InsulinUnits);
		EditText note = (EditText) findViewById(R.id.et_InsulinDetail_Notes);

		//tem de ter um target inserido
		DB_Read read = new DB_Read(this);
		if (!read.Target_HasTargets()) {
			read.close();
			ShowDialogAddTarget();
			return;
		}

		if (insulinunits.getText().toString().equals("")) {
			insulinunits.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(insulinunits, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (allInsulins.isEmpty()) {
			ShowDialogAddInsulin();
			return;
		}

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);

		//Get id of selected insulin
		String insulin = insulinSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", insulin);
		int idInsulin = rdb.Insulin_GetByName(insulin).getId();


		DB_Write reg = new DB_Write(this);

		InsulinRegDataBinding ins = new InsulinRegDataBinding();


		int idnote = 0;
		if (!note.getText().toString().equals("") && idNote == 0) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			idnote = reg.Note_Add(n);
			ins.setIdNote(idnote);
		}
		if (idNote != 0) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			reg.Note_Update(n);
		}

		int idglycemia = 0;

		if (id_BG <= 0 && !glycemia.getText().toString().equals("")) {
			GlycemiaDataBinding gly = new GlycemiaDataBinding();

			gly.setIdUser(idUser);
			gly.setValue(Integer.parseInt(glycemia.getText().toString()));
			gly.setDate(data.getText().toString());
			gly.setTime(hora.getText().toString());
			gly.setIdTag(idTag);
			if (idnote > 0) {
				gly.setIdNote(idnote);
			}

			idglycemia = reg.Glycemia_Save(gly);
		}
		if (id_BG > 0) {
			GlycemiaDataBinding gly = new GlycemiaDataBinding();
			gly.setId(id_BG);
			gly.setIdUser(idUser);
			gly.setValue((!glycemia.getText().toString().equals("")) ? Integer.parseInt(glycemia.getText().toString()) : 0);
			gly.setDate(data.getText().toString());
			gly.setTime(hora.getText().toString());
			gly.setIdTag(idTag);
			if (idnote > 0) {
				gly.setIdNote(idnote);
			}
			reg.Glycemia_Update(gly);

			Log.d("id glycemia", String.valueOf(id_BG));
		}

		ins.setId(idIns);
		ins.setIdUser(idUser);
		ins.setIdInsulin(idInsulin);
		ins.setIdBloodGlucose((id_BG > 0) ? id_BG : (idglycemia > 0) ? idglycemia : -1);
		ins.setDate(data.getText().toString());
		ins.setTime(hora.getText().toString());
		ins.setTargetGlycemia(Integer.parseInt(target.getText().toString()));
		ins.setInsulinUnits(Integer.parseInt(insulinunits.getText().toString()));
		ins.setIdTag(idTag);


		reg.Insulin_Update(ins);
		rdb.close();
		reg.close();

		goUp();

	}

	public static void SelectSpinnerItemByValue(Spinner spnr, String value) {
		SpinnerAdapter adapter = (SpinnerAdapter) spnr.getAdapter();
		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItem(position).equals(value)) {
				spnr.setSelection(position);
				return;
			}
		}
	}

	public void DeleteInsulinRead() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.deleteReading))
				.setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						//Rever porque não elimina o registo de glicemia
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Insulin_Delete(idIns);
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

	public void end() {
		finish();
	}
}
