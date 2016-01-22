package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalc;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalc.CalcListener;
import pt.it.porto.mydiabetes.ui.listAdapters.TagDataBinding;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

public abstract class BaseMealActivity extends Activity implements CalcListener {

	// activity flags start for result
	public final static int IMAGE_CAPTURE = 2;
	public final static int IMAGE_VIEW = 3;
	private static final String GENERATED_IMAGE_URI = "generated_image_uri";
	private EditText insulinIntake;
	private EditText glycemia;
	private EditText carbs;
	private EditText target;
	private EditText time;
	private EditText date;

	private Uri imgUri;
	private Bitmap b;
	private Uri generatedImageUri;


	private boolean showAddGlycemiaTarget;
	private InsulinCalc fragmentInsulinCalcs;
	private boolean expandInsulinCalcsAuto = false;
	private InsulinCalculator insulinCalculator = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_meal);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		insulinIntake = (EditText) findViewById(R.id.et_MealDetail_InsulinUnits);
		target = (EditText) findViewById(R.id.et_MealDetail_TargetGlycemia);
		glycemia = (EditText) findViewById(R.id.et_MealDetail_Glycemia);
		carbs = (EditText) findViewById(R.id.et_MealDetail_Carbs);
		time = (EditText) findViewById(R.id.et_MealDetail_Hora);
		date = (EditText) findViewById(R.id.et_MealDetail_Date);


		fillTagSpinner();
		fillDateHour(null, null);
		fillInsulinSpinner();
		setupClickListeners();
		setupMealImage();
		setUpdateListeners();
		setupInsulinCalculator();

//		if (((ToggleButton) findViewById(R.id.bt_insulin_calc_info)).isChecked()) {
//			fragmentInsulinCalcs=new InsulinCalc();
//			getFragmentManager().beginTransaction().replace(R.id.fragment_calcs, fragmentInsulinCalcs).commit();
//			getFragmentManager().executePendingTransactions();
//			this.fragmentInsulinCalcs= (InsulinCalc)  getFragmentManager().findFragmentById(R.id.fragment_calcs);
//		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(GENERATED_IMAGE_URI, generatedImageUri);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(GENERATED_IMAGE_URI)) {
			generatedImageUri = savedInstanceState.getParcelable(GENERATED_IMAGE_URI);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_CANCELED && requestCode == IMAGE_CAPTURE) {
			setImageUri(generatedImageUri);
		} else if (requestCode == IMAGE_VIEW) {
			//se tivermos apagado a foto dá result code -1
			//se voltarmos por um return por exemplo o resultcode é 0
			if (resultCode == -1) {
				setImageUri(null);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setPhaseOfDayByValue(String value) {
		DB_Read rdb = new DB_Read(getApplicationContext());
		String result = rdb.Tag_GetByTime(value).getName();
		rdb.close();
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		SpinnerAdapter adapter = spinner.getAdapter();
		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItem(position).equals(result)) {
				spinner.setSelection(position);
				return;
			}
		}
	}

	private void setupMealImage() {
		ImageView imageView = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
		if (imgUri == null) {
			imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.newphoto, null));
		} else {
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int) (displaymetrics.heightPixels * 0.1);
			int width = (int) (displaymetrics.widthPixels * 0.1);
			b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
			imageView.setImageBitmap(b);
		}
	}

	private void setupClickListeners() {
		findViewById(R.id.iv_MealDetail_Photo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imgUri != null) {
					final Intent intent = new Intent(getBaseContext(), ViewPhoto.class);
					Bundle argsToPhoto = new Bundle();
					argsToPhoto.putString("Path", imgUri.getPath());
					argsToPhoto.putInt("Id", -1);
					intent.putExtras(argsToPhoto);
					startActivityForResult(intent, IMAGE_VIEW);
				} else {
					final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
					startActivityForResult(intent, IMAGE_CAPTURE);
				}
			}
		});

		findViewById(R.id.addTargetObjective).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addGlycemiaObjective();
			}
		});

		findViewById(R.id.bt_insulin_calc_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleInsulinCalcDetails(v);
			}
		});
	}

	private void setUpdateListeners() {
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
				glycemiaTargetChanged(target, text);
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
				glycemiaChanged(glycemia, text);
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
				int val = 0;
				if (!text.isEmpty()) {
					try {
						val = Integer.parseInt(s.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				insulinCalculator.setCarbs(val);
				setInsulinIntake();
				carbsChanged(carbs, text);
			}
		});


		time.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				setPhaseOfDayByValue(s.toString());
				timeChanged(time, s.toString());
			}
		});

		date.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				dateChanged(date, s.toString());
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
				String text = s.toString();
//				float val = 0;
//				if (!text.isEmpty()) {
//					try {
//						val = Float.parseFloat(s.toString());
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					}
//				}
				insulinIntakeChanged(insulinIntake, text);
			}

		});
	}

	protected abstract void glycemiaTargetChanged(EditText view, String text);

	protected abstract void glycemiaChanged(EditText view, String text);

	protected abstract void carbsChanged(EditText view, String text);

	protected abstract void insulinIntakeChanged(EditText view, String text);

	protected abstract void dateChanged(EditText view, String text);

	protected abstract void timeChanged(EditText view, String text);

	void showUpdateIndicator(EditText view, boolean valueChanged) {
		if (valueChanged) {
			view.setBackgroundResource(R.drawable.edit_text_holo_dark_changed);
		} else {
			view.setBackgroundResource(R.drawable.default_edit_text_holo_dark);
		}
	}

	void fillDateHour(String date, String time) {
		Calendar c = Calendar.getInstance();
		this.date.setText(date == null ? DatePickerFragment.getFormatedDate(c) : date);
		this.date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);
			}
		});

		this.time.setText(time == null ? TimePickerFragment.getFormatedDate(c) : time);
		this.time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showTimePickerDialog(v);
			}
		});
		setPhaseOfDayByValue(time == null ? TimePickerFragment.getFormatedDate(c) : time);
	}

	private void fillTagSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		ArrayList<String> allTags = new ArrayList<>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<TagDataBinding> t = rdb.Tag_GetAll();
		rdb.close();


		if (t != null) {
			for (TagDataBinding i : t) {
				allTags.add(i.getName());
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allTags);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	private void showDatePickerDialog(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_MealDetail_Data,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	private void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_MealDetail_Hora,
				TimePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}


	private void fillInsulinSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
		ArrayList<String> allInsulins = new ArrayList<>();
		DB_Read rdb = new DB_Read(this);
		HashMap<Integer, String> val = rdb.Insulin_GetAllNames();
		rdb.close();

		if (val != null) {
			for (int i : val.keySet()) {
				allInsulins.add(val.get(i));
			}
		} else {
			insulinsNotFound();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allInsulins);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

	}

	abstract void insulinsNotFound();

	private Uri getImageUri() {
		// Store image in /MyDiabetes
		File file = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes", new Date().getTime() + ".jpg");
		File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
		if (!dir.exists()) {
			if (dir.mkdir()) {
				// unable to create directory
				// todo report and recover
			}
		}
		this.generatedImageUri = Uri.fromFile(file);
		return generatedImageUri;
	}

	public void setImageUri(Uri data) {
		imgUri = data;
		ImageView img = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
		if (imgUri == null) {
			img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.newphoto, null));
		} else {
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int) (displaymetrics.heightPixels * 0.1);
			int width = (int) (displaymetrics.widthPixels * 0.1);
			b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
			img.setImageBitmap(b);
		}
	}

	private void addGlycemiaObjective() {
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

	public void showCalcs() {
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

	public void hideCalcs() {
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

	private void toggleInsulinCalcDetails(View view) {
		expandInsulinCalcsAuto = false;
		if (((ToggleButton) view).isChecked()) {
			showCalcs();
		} else {
			hideCalcs();
		}
	}

	void setInsulinIntake() {
		if (!shouldSetInsulin()) {
			return;
		}
		if (expandInsulinCalcsAuto || isFragmentShowing()) {
			showCalcs();
		}

		float insulin = insulinCalculator.getInsulinTotal(true, true);
		insulinIntake.setText(String.valueOf(insulin > 0 ? insulin : 0));
	}

	boolean shouldSetInsulin() {
		return true;
	}

	private boolean isFragmentShowing() {
//		FragmentManager fragmentManager = getFragmentManager();
//		Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_calcs);
//		if (fragment != null) {
//			fragmentInsulinCalcs = (InsulinCalc) fragment;
//			return true;
//		}
//		return false;
		return fragmentInsulinCalcs != null;
	}

	public void setGlycemiaTarget(int val) {
		target.setText(String.format(LocaleUtils.MY_LOCALE, "%d", val));
		if (val != 0) {
			findViewById(R.id.addTargetObjective).setVisibility(View.GONE);
		} else {
			findViewById(R.id.addTargetObjective).setVisibility(View.VISIBLE);
		}
	}

	abstract InsulinCalculator getInsulinCalculator();

	public void setupInsulinCalculator() {
		this.insulinCalculator = getInsulinCalculator();
		if (insulinCalculator.getCarbs() > 0) {
			carbs.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinCalculator.getCarbs()));
		}
		if (insulinCalculator.getGlycemia() > 0) {
			glycemia.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinCalculator.getGlycemia()));
		}
		if (insulinCalculator.getInsulinTarget() > 0) {
			target.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinCalculator.getInsulinTarget()));
		}
		setGlycemiaTarget(insulinCalculator.getInsulinTarget());
		float insulinTotal = insulinCalculator.getInsulinTotal(true, true);
		if (insulinTotal > 0) {
			insulinIntake.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinTotal));
		}
	}

	public Uri getImgUri() {
		return imgUri;
	}

	public float getInsulinIntake() {
		return Float.parseFloat(insulinIntake.getText().toString());
	}

	public String getTime() {
		return time.getText().toString();
	}

	public String getDate() {
		return date.getText().toString();
	}

	public String getPhaseOfDay() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		return spinner.getSelectedItem().toString();
	}

	public String getNote() {
		EditText note = (EditText) findViewById(R.id.et_MealDetail_Notes);
		return note.getText().toString();
	}

	void setNote(String note) {
		((EditText) findViewById(R.id.et_MealDetail_Notes)).setText(note);
	}

	public boolean canSave() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (carbs.getText().toString().isEmpty()) {
			carbs.requestFocus();
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
		}else if(glycemia.getText().toString().isEmpty()){
			glycemia.requestFocus();
			imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
		}else {
			return true;
		}
		return false;
	}

	public String getInsulin() {
		Spinner insulin = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
		return insulin.getSelectedItem().toString();
	}

	@Override
	public void setup() {
		fragmentInsulinCalcs = (InsulinCalc) getFragmentManager().findFragmentById(R.id.fragment_calcs);
		showCalcs();
	}

	public void setInsulin(String insulinName, float value) {
		insulinIntake.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", value));

		if (insulinName != null) {
			Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
			SpinnerAdapter adapter = spinner.getAdapter();
			for (int position = 0; position < adapter.getCount(); position++) {
				if (adapter.getItem(position).equals(insulinName)) {
					spinner.setSelection(position);
					return;
				}
			}
		}
	}
}
