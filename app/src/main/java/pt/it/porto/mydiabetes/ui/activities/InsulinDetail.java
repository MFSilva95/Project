package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class InsulinDetail extends BaseActivity implements InsulinCalcFragment.CalcListener {

	int id_BG = 0;
	int idNote = 0;
	int idIns = 0;
	ArrayList<String> allInsulins;

	private InsulinCalculator insulinCalculator = null;
	private InsulinCalcFragment fragmentInsulinCalcsFragment;
	private EditText insulinIntake;
	private boolean useIOB;
	private boolean changeCausedByOtherInput=false;

	public static void SelectSpinnerItemByValue(Spinner spnr, String value) {
		SpinnerAdapter adapter = spnr.getAdapter();
		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItem(position).equals(value)) {
				spnr.setSelection(position);
				return;
			}
		}
	}

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


		insulinCalculator = new InsulinCalculator(this);
		insulinIntake = (EditText) findViewById(R.id.et_InsulinDetail_InsulinUnits);


		getActionBar();
		FillTagSpinner();
		FillInsulinSpinner();
		EditText hora = (EditText) findViewById(R.id.et_InsulinDetail_Hora);

		Bundle args = getIntent().getExtras();
		if (args != null) {
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			idIns = Integer.parseInt(id);
			InsulinRec toFill = rdb.InsulinReg_GetById(Integer.parseInt(id));

			Spinner tagSpinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Tag);
			SelectSpinnerItemByValue(tagSpinner, rdb.Tag_GetById(toFill.getIdTag()).getName());
			Spinner insulinSpinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Insulin);
			SelectSpinnerItemByValue(insulinSpinner, rdb.Insulin_GetById(toFill.getIdInsulin()).getName());

			EditText data = (EditText) findViewById(R.id.et_InsulinDetail_Data);
			data.setText(toFill.getFormattedDate());
			hora.setText(toFill.getFormattedTime());
			insulinCalculator.setTime(this, toFill.getFormattedTime(), toFill.getFormattedDate());

			EditText target = (EditText) findViewById(R.id.et_InsulinDetail_TargetGlycemia);
			target.setText(String.valueOf(toFill.getTargetGlycemia()));
			insulinCalculator.setGlycemiaTarget(toFill.getTargetGlycemia());

			insulinIntake.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", toFill.getInsulinUnits()));
			EditText note = (EditText) findViewById(R.id.et_InsulinDetail_Notes);

			//If exists glycemia read fill fields
			EditText glycemia = (EditText) findViewById(R.id.et_InsulinDetail_Glycemia);
			id_BG = toFill.getIdBloodGlucose();
			if (id_BG != -1) {
				id_BG = toFill.getIdBloodGlucose();
				GlycemiaRec g = rdb.Glycemia_GetById(id_BG);
				glycemia.setText(String.valueOf(g.getValue()));
				insulinCalculator.setGlycemia(g.getValue());
			}

			if (toFill.getIdNote() != -1) {
				Note n = new Note();
				n = rdb.Note_GetById(toFill.getIdNote());
				note.setText(n.getNote());
				idNote = n.getId();
			}

			// check if there is a record of carbs at that time
			CarbsRec carbs = rdb.getCarbsAtThisTime(toFill.getIdUser(), DateUtils.formatToDb(toFill.getDateTime())); // this is wrong in so many levels...
			// let's take a bit to talk about dates
			// in sqlite dates don't exist!
			// they are strings. let's say: 2016-01-09 11:09
			// if we save the above date as: 2016-01-09 11:9 huge problems can happen when latter on we go to compare them!
			// 2016-01-09 11:9 > 2016-01-09 11:10 is true! because sqlite will compare strings char by char
			if (carbs != null) {
				insulinCalculator.setCarbs(carbs.getCarbsValue());
			}

			rdb.close();
		} else {
			FillDateHour();
			setTargetByHour();
			setTagByTime();


			EditText target = (EditText) findViewById(R.id.et_InsulinDetail_TargetGlycemia);
			EditText glycemia = (EditText) findViewById(R.id.et_InsulinDetail_Glycemia);

			target.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					changeCausedByOtherInput=true;
					String text = s.toString();
					int val = 0;
					if (!text.isEmpty()) {
						try {
							val = Integer.parseInt(s.toString());
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					insulinCalculator.setGlycemiaTarget(val);
					setInsulinIntake();
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
					changeCausedByOtherInput=true;
					String text = s.toString();
					int val = 0;
					if (text.isEmpty()) {
						val = 0;
					} else {
						try {
							val = Integer.parseInt(s.toString());
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					insulinCalculator.setGlycemia(val);
					setInsulinIntake();
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
					setTargetByHour();
					setTagByTime();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			insulinIntake.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if(changeCausedByOtherInput){
						changeCausedByOtherInput=false;
					}else{
						stopAutoUpdateInsulinIntake();
					}

				}
			});
		}

		FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(this));
		useIOB = featuresDB.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD);

	}

	void setInsulinIntake() {
		if (fragmentInsulinCalcsFragment != null) {
			showCalcs();
		}
		if(!shouldUpdateInsulinIntake()){
			return;
		}

		float insulin = insulinCalculator.getInsulinTotal(useIOB, true);
		insulinIntake.setText(String.valueOf(insulin > 0 ? insulin : 0));
	}


	boolean autoUpdate = true;

	private boolean shouldUpdateInsulinIntake() {
		return autoUpdate;
	}

	private void stopAutoUpdateInsulinIntake() {
		this.autoUpdate = false;
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
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_InsulinDetail_Hora,
				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
		((TimePickerFragment) newFragment).setListener(new TimePickerFragment.TimePickerChangeListener() {
			@Override
			public void onTimeSet(String time) {
				setTargetByHour();
				setTagByTime();
				Calendar calendar = DateUtils.getTimeCalendar(time);
				if (calendar != null) {
					insulinCalculator.setTime(getApplicationContext(), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), null);
				}
			}
		});
		newFragment.show(getFragmentManager(), "timePicker");
	}

	public void FillTagSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_InsulinDetail_Tag);
		ArrayList<String> allTags = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<Tag> t = rdb.Tag_GetAll();
		rdb.close();


		if (t != null) {
			for (Tag i : t) {
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
		date.setText(DateUtils.getFormattedDate(calendar));

		EditText hour = (EditText) findViewById(R.id.et_InsulinDetail_Hora);
		hour.setText(DateUtils.getFormattedTime(calendar));

		insulinCalculator.setTime(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), null);
	}

	public void setTargetByHour() {
		EditText target = (EditText) findViewById(R.id.et_InsulinDetail_TargetGlycemia);
		EditText hora = (EditText) findViewById(R.id.et_InsulinDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		double d = rdb.Target_GetTargetByTime(hora.getText().toString());
		if (d != 0) {
			findViewById(R.id.addTargetObjective).setVisibility(View.GONE);
			target.setText(String.valueOf((int) d));
			insulinCalculator.setGlycemiaTarget((int) d);
		} else {
			findViewById(R.id.addTargetObjective).setVisibility(View.VISIBLE);
		}
		rdb.close();
	}

	public void setTagByTime() {
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
						Intent intent = new Intent(c, SettingsInsulins.class);
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
						Intent intent = new Intent(c, SettingsGlycemia.class);
						intent.putExtra("tabPosition", 1);
						startActivity(intent);
						finish();
					}
				}).show();
	}


	public void addGlycemiaObjective(View view) {
		Intent intent = new Intent(this, TargetBG_detail.class);
		String goal = ((EditText) findViewById(R.id.et_InsulinDetail_TargetGlycemia)).getText().toString();
		if (!TextUtils.isEmpty(goal)) {
			float target = Float.parseFloat(goal);
			Bundle bundle = new Bundle();
			bundle.putFloat(TargetBG_detail.BUNDLE_GOAL, target);
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	public void toggleInsulinCalcDetails(View view) {
		if (((ToggleButton) view).isChecked()) {
			showCalcs();
		} else {
			hideCalcs();
		}
	}

	public void showCalcs() {
		if (fragmentInsulinCalcsFragment == null) {
			FragmentManager fragmentManager = getFragmentManager();
			Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_calcs);
			if (fragment != null) {
				fragmentInsulinCalcsFragment = (InsulinCalcFragment) fragment;
			} else {
				fragmentInsulinCalcsFragment = InsulinCalcFragment.newInstance((int) insulinCalculator.getGlycemiaRatio(), (int) insulinCalculator.getCarbsRatio());
				fragmentManager.beginTransaction()
						.add(R.id.fragment_calcs, fragmentInsulinCalcsFragment)
						.commit();
				fragmentManager.executePendingTransactions();

				ScaleAnimation animation = new ScaleAnimation(1, 1, 0, 1, Animation.ABSOLUTE, Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, 0);
				animation.setDuration(700);
				findViewById(R.id.fragment_calcs).startAnimation(animation);
				((ToggleButton) findViewById(R.id.bt_insulin_calc_info)).setChecked(true);
			}
		}

		fragmentInsulinCalcsFragment.setCorrectionGlycemia(insulinCalculator.getInsulinGlycemia());
		fragmentInsulinCalcsFragment.setCorrectionCarbs(insulinCalculator.getInsulinCarbs());
		fragmentInsulinCalcsFragment.setResult(insulinCalculator.getInsulinTotal(useIOB), insulinCalculator.getInsulinTotal(useIOB, true));
		fragmentInsulinCalcsFragment.setInsulinOnBoard(insulinCalculator.getInsulinOnBoard());
		insulinCalculator.setListener(new InsulinCalculator.InsulinCalculatorListener() {
			@Override
			public void insulinOnBoardChanged(InsulinCalculator calculator) {
				if (fragmentInsulinCalcsFragment != null) {
					showCalcs();
				}
				setInsulinIntake();
			}
		});
	}

	public void hideCalcs() {
		if (fragmentInsulinCalcsFragment != null) {
			ScaleAnimation animation = new ScaleAnimation(1, 1, 1, 0, Animation.ABSOLUTE, Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, 0);
			animation.setDuration(700);
			findViewById(R.id.fragment_calcs).startAnimation(animation);
			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					getFragmentManager().beginTransaction()
							.remove(fragmentInsulinCalcsFragment)
							.commit();
					fragmentInsulinCalcsFragment = null;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});
			insulinCalculator.setListener(null);
		}
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
		int idUser = rdb.getId();

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

		InsulinRec ins = new InsulinRec();

		int idnote = 0;

		if (!note.getText().toString().equals("")) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			idnote = reg.Note_Add(n);
			ins.setIdNote(idnote);
		}

		if (!glycemia.getText().toString().equals("")) {
			GlycemiaRec gly = new GlycemiaRec();

			gly.setIdUser(idUser);
			gly.setValue(Integer.parseInt(glycemia.getText().toString()));
			gly.setDateTime(data.getText().toString(), hora.getText().toString());
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
		ins.setDateTime(data.getText().toString(), hora.getText().toString());
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
		int idUser = rdb.getId();

		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);

		//Get id of selected insulin
		String insulin = insulinSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", insulin);
		int idInsulin = rdb.Insulin_GetByName(insulin).getId();


		DB_Write reg = new DB_Write(this);

		InsulinRec ins = new InsulinRec();


		int idnote = 0;
		if (!note.getText().toString().equals("") && idNote == 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			idnote = reg.Note_Add(n);
			ins.setIdNote(idnote);
		}
		if (idNote != 0) {
			Note n = new Note();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			reg.Note_Update(n);
		}

		int idglycemia = 0;

		if (id_BG <= 0 && !glycemia.getText().toString().equals("")) {
			GlycemiaRec gly = new GlycemiaRec();

			gly.setIdUser(idUser);
			gly.setValue(Integer.parseInt(glycemia.getText().toString()));
			gly.setDateTime(data.getText().toString(), hora.getText().toString());
			gly.setIdTag(idTag);
			if (idnote > 0) {
				gly.setIdNote(idnote);
			}

			idglycemia = reg.Glycemia_Save(gly);
		}
		if (id_BG > 0) {
			GlycemiaRec gly = new GlycemiaRec();
			gly.setId(id_BG);
			gly.setIdUser(idUser);
			gly.setValue((!glycemia.getText().toString().equals("")) ? Integer.parseInt(glycemia.getText().toString()) : 0);
			gly.setDateTime(data.getText().toString(), hora.getText().toString());
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
		ins.setDateTime(data.getText().toString(), hora.getText().toString());
		ins.setTargetGlycemia(Integer.parseInt(target.getText().toString()));
		ins.setInsulinUnits(Float.parseFloat(insulinunits.getText().toString()));
		ins.setIdTag(idTag);


		reg.Insulin_Update(ins);
		rdb.close();
		reg.close();

		goUp();

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

	@Override
	public void setup() {
		fragmentInsulinCalcsFragment = (InsulinCalcFragment) getFragmentManager().findFragmentById(R.id.fragment_calcs);
		showCalcs();
	}
}
