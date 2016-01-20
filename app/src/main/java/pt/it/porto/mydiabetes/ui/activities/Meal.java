package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.middleHealth.utils.Utils;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalc;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.NoteDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.TagDataBinding;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;


public class Meal extends BaseOldActivity {

	private static final String SHOW_INSULIN_CALCS_EXPANDED = "SHOW_INSULIN_CALCS_EXPANDED";
	//photo variables - start
	final private int IMAGE_CAPTURE = 2;
	final private int IMAGE_VIEW = 3;
	ArrayList<String> allInsulins;
	Uri imgUri;
	Bitmap b;
	//photo variables - end

	//variavel que contem o id da insulina seleccionada
	int action_type = -1;

	private EditText insulinunits;
	private EditText glycemia;
	private EditText carbs;
	private EditText target;

	private InsulinCalculator insulinCalculator;

	private boolean expandInsulinCalcsAuto = false;
	private InsulinCalc fragmentInsulinCalcs;

	public static void SelectSpinnerItemByValue(Spinner spnr, String value) {
		SpinnerAdapter adapter = (SpinnerAdapter) spnr.getAdapter();
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
		setContentView(R.layout.activity_meal);
		// Show the Up button in the action bar.
		getActionBar();

		DB_Read read = new DB_Read(this);


		if (!read.Insulin_HasInsulins()) {
			ShowDialogAddInsulin();
		}
		read.close();

		FillDateHour();
		FillTagSpinner();
		SetTagByTime();
		SetTargetByHour();
		FillInsulinSpinner();


		EditText hora = (EditText) findViewById(R.id.et_MealDetail_Hora);
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


		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		rdb.close();
		double iRatio = Double.valueOf(obj[3].toString());
		double cRatio = Double.valueOf(obj[4].toString());
		insulinCalculator = new InsulinCalculator((int) iRatio, (int) cRatio);
		setupLasInsulin();

		insulinunits = (EditText) findViewById(R.id.et_MealDetail_InsulinUnits);
		target = (EditText) findViewById(R.id.et_MealDetail_TargetGlycemia);
		glycemia = (EditText) findViewById(R.id.et_MealDetail_Glycemia);
		carbs = (EditText) findViewById(R.id.et_MealDetail_Carbs);


		target.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				if (text.isEmpty()) {
					insulinCalculator.setGlycemiaTarget(0);
				} else {
					try {
						insulinCalculator.setGlycemiaTarget(Integer.parseInt(s.toString()));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				setInsulinIntake();
			}
		});

		glycemia.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				if (text.isEmpty()) {
					insulinCalculator.setGlycemia(0);
				} else {
					try {
						insulinCalculator.setGlycemia(Integer.parseInt(s.toString()));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				setInsulinIntake();
			}
		});

		carbs.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				if (text.isEmpty()) {
					insulinCalculator.setCarbs(0);
				} else {
					try {
						insulinCalculator.setCarbs(Integer.parseInt(s.toString()));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				setInsulinIntake();
			}
		});

		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String name = parent.getSelectedItem().toString();
				DB_Read read = new DB_Read(Meal.this);
				action_type = read.Insulin_GetActionTypeByName(name);
				read.close();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		if (!expandInsulinCalcsAuto) {
			expandInsulinCalcsAuto = pt.it.porto.mydiabetes.database.Preferences.showFeatureForFirstTime(this, SHOW_INSULIN_CALCS_EXPANDED);
		}

		handleExtras();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.meal, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_MealDetail_Save:
				VerifySaveReads();
				//Toast.makeText(this, "Clicado em gravar", Toast.LENGTH_LONG).show();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void FillDateHour() {
		EditText date = (EditText) findViewById(R.id.et_MealDetail_Data);
		final Calendar c = Calendar.getInstance();
		date.setText(DatePickerFragment.getFormatedDate(c));

		EditText hour = (EditText) findViewById(R.id.et_MealDetail_Hora);
		hour.setText(TimePickerFragment.getFormatedDate(c));
	}

	public void FillTagSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
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

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_MealDetail_Data,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_MealDetail_Hora,
				TimePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void SetTagByTime() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		EditText hora = (EditText) findViewById(R.id.et_MealDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		SelectSpinnerItemByValue(tagSpinner, name);
	}

	public void ShowDialogAddInsulin() {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.title_activity_info))
				.setMessage(getString(R.string.meal_alert_insulin))
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
				.setMessage(getString(R.string.meal_alert_target))
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

	public void end() {
		finish();
	}

	public void SetTargetByHour() {
		EditText target = (EditText) findViewById(R.id.et_MealDetail_TargetGlycemia);
		EditText hora = (EditText) findViewById(R.id.et_MealDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		double d = rdb.Target_GetTargetByTime(hora.getText().toString());
		if (d != 0) {
			target.setText(String.valueOf(d));
			findViewById(R.id.addTargetObjective).setVisibility(View.GONE);
		} else {
			findViewById(R.id.addTargetObjective).setVisibility(View.VISIBLE);
		}
		rdb.close();
	}

	public void FillInsulinSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
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

	//-------------------------------------------
	public void AddGlycemiaRead() {
		Spinner TagSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		EditText glycemia = (EditText) findViewById(R.id.et_MealDetail_Glycemia);
		EditText data = (EditText) findViewById(R.id.et_MealDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_MealDetail_Hora);
		EditText note = (EditText) findViewById(R.id.et_MealDetail_Notes);

		//Get id of user
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		//Get id of selected tag
		String tag = TagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();

		DB_Write reg = new DB_Write(this);
		GlycemiaDataBinding gly = new GlycemiaDataBinding();

		if (!note.getText().toString().equals("")) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			gly.setIdNote(reg.Note_Add(n));
		}


		gly.setIdUser(idUser);
		gly.setValue(Double.parseDouble(glycemia.getText().toString()));
		gly.setDate(data.getText().toString());
		gly.setTime(hora.getText().toString());
		gly.setIdTag(idTag);


		reg.Glycemia_Save(gly);
		reg.close();

	}

	public void AddCarbsRead() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		EditText carbs = (EditText) findViewById(R.id.et_MealDetail_Carbs);
		EditText data = (EditText) findViewById(R.id.et_MealDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_MealDetail_Hora);
		EditText photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
		EditText note = (EditText) findViewById(R.id.et_MealDetail_Notes);

		//Get id of user
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		DB_Write reg = new DB_Write(this);
		CarbsDataBinding carb = new CarbsDataBinding();

		if (!note.getText().toString().equals("")) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			carb.setId_Note(reg.Note_Add(n));
		}


		carb.setId_User(idUser);
		carb.setCarbsValue(Double.parseDouble(carbs.getText().toString()));
		carb.setId_Tag(idTag);
		carb.setPhotoPath(photopath.getText().toString()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
		carb.setDate(data.getText().toString());
		carb.setTime(hora.getText().toString());


		reg.Carbs_Save(carb);
		reg.close();
	}

	public void AddInsulinRead() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		Spinner insulinSpinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
		EditText glycemia = (EditText) findViewById(R.id.et_MealDetail_Glycemia);
		EditText data = (EditText) findViewById(R.id.et_MealDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_MealDetail_Hora);
		EditText target = (EditText) findViewById(R.id.et_MealDetail_TargetGlycemia);
		EditText insulinunits = (EditText) findViewById(R.id.et_MealDetail_InsulinUnits);
		EditText note = (EditText) findViewById(R.id.et_MealDetail_Notes);


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
			gly.setValue(Double.parseDouble(glycemia.getText().toString()));
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
		ins.setTargetGlycemia(Double.parseDouble(target.getText().toString()));
		ins.setInsulinUnits(Double.parseDouble(insulinunits.getText().toString()));
		ins.setIdTag(idTag);


		reg.Insulin_Save(ins);

		reg.close();
		rdb.close();
	}


	//PHOTO - START

	//-----------------------------------------
	public void VerifySaveReads() {
		EditText insulinunits = (EditText) findViewById(R.id.et_MealDetail_InsulinUnits);
		EditText target = (EditText) findViewById(R.id.et_MealDetail_TargetGlycemia);
		EditText glycemia = (EditText) findViewById(R.id.et_MealDetail_Glycemia);
		EditText carbs = (EditText) findViewById(R.id.et_MealDetail_Carbs);

		//tem de ter um target inserido
		DB_Read read = new DB_Read(this);
		if (!read.Target_HasTargets()) {
			read.close();
			ShowDialogAddTarget();
			return;
		}


		if (glycemia.getText().toString().equals("")) {
			glycemia.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (carbs.getText().toString().equals("")) {
			carbs.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (target.getText().toString().equals("")) {
			target.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		if (insulinunits.getText().toString().equals("")) {
			insulinunits.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(insulinunits, InputMethodManager.SHOW_IMPLICIT);
			return;
		}
		//spinner das insulinas tem de ter valores
		if (allInsulins.isEmpty()) {
			ShowDialogAddInsulin();
			return;
		}


		//AddGlycemiaRead();
		AddCarbsRead();
		AddInsulinRead();
		NavUtils.navigateUpFromSameTask(this);
	}

	public Uri setImageUri() {
		// Store image in /MyDiabetes
		File file = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes", new Date().getTime() + ".jpg");
		File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return Uri.fromFile(file);
	}

	public void TakePhoto(View v) {
		EditText photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
		if (!photopath.getText().toString().equals("")) {
			final Intent intent = new Intent(this, ViewPhoto.class);
			Bundle argsToPhoto = new Bundle();
			argsToPhoto.putString("Path", photopath.getText().toString());
			argsToPhoto.putInt("Id", -1);
			intent.putExtras(argsToPhoto);
			startActivityForResult(intent, IMAGE_VIEW);
		} else {
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
			startActivityForResult(intent, IMAGE_CAPTURE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		EditText photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
		ImageView img = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
		if (resultCode != Activity.RESULT_CANCELED && requestCode == IMAGE_CAPTURE) {
			imgUri = data.getData();
			Toast.makeText(getApplicationContext(), getString(R.string.photoSaved) + " " + imgUri.getPath(), Toast.LENGTH_LONG).show();
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int) (displaymetrics.heightPixels * 0.1);
			int width = (int) (displaymetrics.widthPixels * 0.1);
			b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
			img.setImageBitmap(b);
			photopath.setText(imgUri.getPath());
		} else if (requestCode == IMAGE_VIEW) {
			Log.d("Result:", resultCode + "");
			//se tivermos apagado a foto dá result code -1
			//se voltarmos por um return por exemplo o resultcode é 0
			if (resultCode == -1) {
				photopath.setText("");
				img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.newphoto, null));
				imgUri = null;
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (imgUri != null) {
			outState.putString("cameraImageUri", imgUri.toString());
		}
		outState.putBoolean(SHOW_INSULIN_CALCS_EXPANDED, expandInsulinCalcsAuto);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("cameraImageUri")) {
			imgUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
			EditText photopath = (EditText) findViewById(R.id.et_MealDetail_Photo);
			ImageView img = (ImageView) findViewById(R.id.iv_MealDetail_Photo);

			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int) (displaymetrics.heightPixels * 0.1);
			int width = (int) (displaymetrics.widthPixels * 0.1);
			b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
			img.setImageBitmap(b);
			photopath.setText(imgUri.getPath());
		}
		if (savedInstanceState.containsKey(SHOW_INSULIN_CALCS_EXPANDED)) {
			expandInsulinCalcsAuto = savedInstanceState.getBoolean(SHOW_INSULIN_CALCS_EXPANDED);
		}
	}

	private void setupLasInsulin() {
		DB_Read read = new DB_Read(this);
		int[] lastInsulin = read.InsulinReg_GetLastHourAndQuantity();
		read.close();

		int minuteOriginal = lastInsulin[1] * 60 + lastInsulin[2];
		int insulinType = lastInsulin[0];
		int insulinDose = lastInsulin[3];

		insulinCalculator.setLastInsulin(insulinDose, minuteOriginal, insulinType);
	}

	private void handleExtras() {
		// Check if there are any extras in the intent:
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("value") && extras.containsKey("timestamp")) {
			// Fill the glycemia text box:
			double glycemiaValue = extras.getDouble("value");
			EditText glycemiaEditText = (EditText) findViewById(R.id.et_MealDetail_Glycemia);
			glycemiaEditText.setText(String.valueOf(glycemiaValue));

			// Fill the timestamp text box:
			long timestamp = extras.getLong("timestamp");
			Date date = Utils.getDateFromTimestamp(timestamp);
			fillDate(date);
			fillHour(date);
		}
	}

	private void fillDate(Date date) {
		// Get edit text for date:
		EditText dateEditText = (EditText) findViewById(R.id.et_MealDetail_Data);

		// Fill the text box for date:
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		dateEditText.setText(dateString);
	}

	private void fillHour(Date date) {
		// Get edit text for the hours:
		EditText hourEditText = (EditText) findViewById(R.id.et_MealDetail_Hora);

		// Fill the text box for the hours:
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String timeString = formatter.format(date);
		hourEditText.setText(timeString);
	}

	public void addGlycemiaObjective(View view) {
		Intent intent = new Intent(this, TargetBG_detail.class);
		String goal = ((EditText) findViewById(R.id.et_MealDetail_TargetGlycemia)).getText().toString();
		if (!TextUtils.isEmpty(goal)) {
			float target = Float.parseFloat(goal);
			Bundle bundle = new Bundle();
			bundle.putFloat(TargetBG_detail.BUNDLE_GOAL, target);
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	private void showCalcs() {
		if (fragmentInsulinCalcs == null) {
			FragmentManager fragmentManager = getFragmentManager();
			Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_calcs);
			if (fragment != null) {
				fragmentInsulinCalcs = (InsulinCalc) fragment;
			} else {
				fragmentInsulinCalcs = InsulinCalc.newInstance((int) insulinCalculator.getGlycemiaRatio(), (int) insulinCalculator.getCarbsRatio());
				fragmentManager.beginTransaction()
						.add(R.id.fragment_calcs, fragmentInsulinCalcs)
						.commit();
				fragmentManager.executePendingTransactions();

				ScaleAnimation animation = new ScaleAnimation(1, 1, 0, 1, Animation.ABSOLUTE, Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, 0);
				animation.setDuration(700);
				findViewById(R.id.fragment_calcs).startAnimation(animation);
				((ToggleButton) findViewById(R.id.bt_insulin_calc_info)).setChecked(true);
			}
		}
		fragmentInsulinCalcs.setCorrectionGlycemia(insulinCalculator.getInsulinGlycemia());
		fragmentInsulinCalcs.setCorrectionCarbs(insulinCalculator.getInsulinCarbs());
		fragmentInsulinCalcs.setResult(insulinCalculator.getInsulinTotal(true), insulinCalculator.getInsulinTotal(true, true));
		float insulinOnBoard = insulinCalculator.getInsulinOnBoard();
		if (insulinOnBoard > 0) {
			fragmentInsulinCalcs.setInsulinOnBoard(insulinOnBoard);
		}
	}


	private void hideCalcs() {
		if (fragmentInsulinCalcs != null) {
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
							.remove(fragmentInsulinCalcs)
							.commit();
					fragmentInsulinCalcs = null;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}
			});
		}
	}

	public void toggleInsulinCalcDetails(View view) {
		expandInsulinCalcsAuto = false;
		if (((ToggleButton) view).isChecked()) {
			showCalcs();
		} else {
			hideCalcs();
		}
	}

	private void setInsulinIntake() {
		if (expandInsulinCalcsAuto || isFragmentShowing()) {
			showCalcs();
		}

		float insulin = insulinCalculator.getInsulinTotal(true, true);
		insulinunits.setText(String.valueOf(insulin > 0 ? insulin : 0));
	}

	private boolean isFragmentShowing() {
		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_calcs);
		if (fragment != null) {
			fragmentInsulinCalcs = (InsulinCalc) fragment;
			return true;
		}
		return false;
	}
}
