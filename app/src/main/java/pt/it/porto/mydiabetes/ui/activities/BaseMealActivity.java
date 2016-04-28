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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcFragment;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcFragment.CalcListener;
import pt.it.porto.mydiabetes.ui.views.ExtendedEditText;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

public abstract class BaseMealActivity extends FormActivity implements CalcListener {

	// activity flags start for result
	public final static int IMAGE_CAPTURE = 2;
	public final static int IMAGE_VIEW = 3;
	private static final String GENERATED_IMAGE_URI = "generated_image_uri";
	private static final String CALCS_OPEN = "calcs open";
	protected InsulinCalculator insulinCalculator = null;
	protected InsulinCalcFragment fragmentInsulinCalcsFragment;
	boolean useIOB = true;
	Calendar dateTime = null;
	private EditText insulinIntake;
	private EditText glycemia;
	private EditText carbs;
	private EditText target;
	private TextView time;
	private TextView date;
	private Uri imgUri;
	private Bitmap b;
	private Uri generatedImageUri;
	private boolean showAddGlycemiaTarget;
	private boolean expandInsulinCalcsAuto = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		insulinIntake = (EditText) findViewById(R.id.et_MealDetail_InsulinUnits);
		target = (EditText) findViewById(R.id.et_MealDetail_TargetGlycemia);
		glycemia = (EditText) findViewById(R.id.glycemia);
		carbs = (EditText) findViewById(R.id.et_MealDetail_Carbs);
		time = (TextView) findViewById(R.id.et_MealDetail_Hora);
		date = (TextView) findViewById(R.id.et_MealDetail_Date);


		fillTagSpinner();
		fillDateHour(null, null);
		fillInsulinSpinner();
		setupClickListeners();
		setupMealImage();
		setUpdateListeners();
		setupInsulinCalculator();

		FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(this));
		useIOB = featuresDB.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(GENERATED_IMAGE_URI, generatedImageUri);
		outState.putBoolean(CALCS_OPEN, isFragmentShowing());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(GENERATED_IMAGE_URI)) {
			generatedImageUri = savedInstanceState.getParcelable(GENERATED_IMAGE_URI);
		}

		if (savedInstanceState != null && savedInstanceState.getBoolean(CALCS_OPEN, false)) {
			ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
			if (calcInsulinInfo != null) {
				calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_grey_900_24dp));
			}
			fragmentInsulinCalcsFragment = new InsulinCalcFragment();
			getFragmentManager().beginTransaction().replace(R.id.fragment_calcs, fragmentInsulinCalcsFragment).commit();
			getFragmentManager().executePendingTransactions();
			this.fragmentInsulinCalcsFragment = (InsulinCalcFragment) getFragmentManager().findFragmentById(R.id.fragment_calcs);
			showCalcs();
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
				imageRemoved();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	void imageRemoved() {
		setImageUri(null);
	}

	public void setPhaseOfDayByValue(String value) {
		DB_Read rdb = new DB_Read(getApplicationContext());
		String result = rdb.Tag_GetByTime(value).getName();
		rdb.close();
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		SpinnerAdapter adapter = null;
		if (spinner != null) {
			adapter = spinner.getAdapter();
		}
		for (int position = 0; position < (adapter != null ? adapter.getCount() : 0); position++) {
			if (adapter.getItem(position).equals(result)) {
				spinner.setSelection(position);
				return;
			}
		}
	}

	private void setupMealImage() {
		ImageView imageView = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
		if (imageView == null) {
			return;
		}
		if (imgUri == null) {
			imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
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
		ImageView imageView = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
		if (imageView == null) {
			return;
		}
		imageView.setOnClickListener(new View.OnClickListener() {
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

		View view = findViewById(R.id.addTargetObjective);
		if (view != null) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					addGlycemiaObjective();
				}
			});
		}

		view = findViewById(R.id.bt_insulin_calc_info);
		if (view != null) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					toggleInsulinCalcDetails(v);
				}
			});
		}
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
				if (!text.isEmpty()) {
//					try {
//						val = Float.parseFloat(s.toString());
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					}
				}
				insulinIntakeChanged(insulinIntake, text);
			}

		});
	}

	protected abstract void glycemiaTargetChanged(EditText view, String text);

	protected abstract void glycemiaChanged(EditText view, String text);

	protected abstract void carbsChanged(EditText view, String text);

	protected abstract void insulinIntakeChanged(EditText view, String text);

	protected abstract void dateChanged(TextView view, String text);

	protected abstract void timeChanged(TextView view, String text);

	void showUpdateIndicator(EditText view, boolean valueChanged) {
		if (valueChanged) {
			view.setBackgroundResource(R.drawable.edit_text_holo_dark_changed);
		} else {
			view.setBackgroundResource(R.drawable.default_edit_text_holo_dark);
		}
	}

	void fillDateHour(String date, String time) {
		Calendar c = Calendar.getInstance();
		this.date.setText(date == null ? DateUtils.getFormattedDate(c) : date);
		this.date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);
			}
		});

		this.time.setText(time == null ? DateUtils.getFormattedTime(c) : time);
		this.time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showTimePickerDialog(v);
			}
		});
		setPhaseOfDayByValue(time == null ? DateUtils.getFormattedTime(c) : time);
	}

	private void fillTagSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		ArrayList<String> allTags = new ArrayList<>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<Tag> t = rdb.Tag_GetAll();
		rdb.close();


		if (t != null) {
			for (Tag i : t) {
				allTags.add(i.getName());
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allTags);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (spinner != null) {
			spinner.setAdapter(adapter);
		}
	}

	private void showDatePickerDialog(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_MealDetail_Date,
				DateUtils.getDateCalendar(((TextView) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	private void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_MealDetail_Hora,
				DateUtils.getTimeCalendar(((TextView) v).getText().toString()));
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
		if (spinner != null) {
			spinner.setAdapter(adapter);
		}

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
		if (img == null) {
			return;
		}
		if (imgUri == null || !new File(imgUri.getPath()).exists()) {
			img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
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
		EditText targetGlycemia = ((EditText) findViewById(R.id.et_MealDetail_TargetGlycemia));
		String goal = null;
		if (targetGlycemia != null) {
			goal = targetGlycemia.getText().toString();
		}
		if (!TextUtils.isEmpty(goal)) {
			float target = Float.parseFloat(goal);
			Bundle bundle = new Bundle();
			bundle.putFloat(TargetBG_detail.BUNDLE_GOAL, target);
			intent.putExtras(bundle);
		}
		startActivity(intent);
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
				FrameLayout fragmentCalcs = (FrameLayout) findViewById(R.id.fragment_calcs);
				if (fragmentCalcs != null) {
					fragmentCalcs.startAnimation(animation);
				}
				ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
				if (calcInsulinInfo != null) {
					calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_grey_900_24dp));
				}
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
			FrameLayout fragmentCalcs = (FrameLayout) findViewById(R.id.fragment_calcs);
			if (fragmentCalcs != null) {
				fragmentCalcs.startAnimation(animation);
			}
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
			ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
			if (calcInsulinInfo != null) {
				calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_information_outline_grey600_24dp));
			}
		}
	}

	public void toggleInsulinCalcDetails(View view) {
		expandInsulinCalcsAuto = false;
		if (!isFragmentShowing()) {
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

		float insulin = insulinCalculator.getInsulinTotal(useIOB, true);
		insulinIntake.setText(String.valueOf(insulin > 0 ? insulin : 0));
	}

	boolean shouldSetInsulin() {
		return true;
	}

	private boolean isFragmentShowing() {
//		FragmentManager fragmentManager = getFragmentManager();
//		Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_calcs);
//		if (fragment != null) {
//			fragmentInsulinCalcsFragment = (InsulinCalcFragment) fragment;
//			return true;
//		}
//		return false;
		return fragmentInsulinCalcsFragment != null;
	}

	public void setGlycemiaTarget(int val) {
		target.setText(String.format(LocaleUtils.MY_LOCALE, "%d", val));
		View targetObjective = findViewById(R.id.addTargetObjective);
		if (targetObjective == null) {
			return;
		}
		if (val != 0) {
			targetObjective.setVisibility(View.GONE);
		} else {
			targetObjective.setVisibility(View.VISIBLE);
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
		float insulinTotal = insulinCalculator.getInsulinTotal(useIOB, true);
		if (insulinTotal > 0) {
			insulinIntake.setText(String.format(LocaleUtils.MY_LOCALE, "%d", (int) insulinTotal));
		}
	}

	public Uri getImgUri() {
		return imgUri;
	}

	public float getInsulinIntake() {
		String insulinIn = insulinIntake.getText().toString();
		if (insulinIn.isEmpty()) {
			return 0;
		}
		return Float.parseFloat(insulinIntake.getText().toString());
	}

	public String getTime() {
		return time.getText().toString();
	}

	public String getDate() {
		return date.getText().toString();
	}

	public Calendar getDateTime() {
		updateDateTime();
		return dateTime;
	}

	private void updateDateTime() {
		try {
			// only udpates dateTime if needed
			Calendar newDateTime = DateUtils.getDateTime(getDate(), getTime());
			if (DateUtils.isSameTime(dateTime, newDateTime)) {
				return;
			}
			dateTime = newDateTime;
			dateTime.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND)); // we add seconds to be possible distinguish in logbook multiple references in same minute
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Nullable
	public String getPhaseOfDay() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Tag);
		if (spinner != null) {
			return spinner.getSelectedItem().toString();
		}
		return null;
	}

	@Nullable
	public String getNote() {
		EditText note = (EditText) findViewById(R.id.et_MealDetail_Notes);
		if (note != null) {
			return note.getText().toString();
		}
		return null;
	}

	void setNote(String note) {
		EditText notes = ((EditText) findViewById(R.id.et_MealDetail_Notes));
		if (notes != null) {
			notes.setText(note);
		}
	}

	public boolean canSave() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (carbs.getText().toString().isEmpty()) {
			carbs.requestFocus();
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
		} else if (glycemia.getText().toString().isEmpty()) {
			glycemia.requestFocus();
			imm.showSoftInput(glycemia, InputMethodManager.SHOW_IMPLICIT);
		} else {
			return true;
		}
		return false;
	}

	@Nullable
	public String getInsulin() {
		Spinner insulin = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
		return insulin != null ? insulin.getSelectedItem().toString() : null;
	}

	@Override
	public void setup() {
		fragmentInsulinCalcsFragment = (InsulinCalcFragment) getFragmentManager().findFragmentById(R.id.fragment_calcs);
		showCalcs();
	}

	public void setInsulin(String insulinName, float value) {
		insulinIntake.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", value));

		if (insulinName != null) {
			Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
			SpinnerAdapter adapter = null;
			if (spinner != null) {
				adapter = spinner.getAdapter();
			}
			if (adapter == null) {
				return;
			}
			for (int position = 0; position < adapter.getCount(); position++) {
				if (adapter.getItem(position).equals(insulinName)) {
					spinner.setSelection(position);
					return;
				}
			}
		}
	}
}
