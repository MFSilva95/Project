package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

public abstract class FormActivity extends BaseActivity implements InsulinCalcFragment.CalcListener {
	public final static int IMAGE_CAPTURE = 2;
	public final static int IMAGE_VIEW = 3;
	private static final String GENERATED_IMAGE_URI = "generated_image_uri";
	private static final String CALCS_OPEN = "calcs open";
	// sections of UI list
	public static int SECTION_DATETIME = R.id.section_dateTime;
	public static int SECTION_GLICEMIA = R.id.section_glicemia;
	public static int[] SECTION_TARGET_GLICEMIA = new int[]{R.id.glycemia_obj, R.id.addTargetObjective};
	public static int SECTION_CARBS = R.id.section_carbs;
	public static int SECTION_INSULIN = R.id.section_insulin;

	// fragment with cacls of insulin
	protected InsulinCalcFragment fragmentInsulinCalcsFragment;
	protected InsulinCalculator insulinCalculator = null;
	boolean useIOB = true;
	private TextView time;
	private TextView date;
	private EditText insulinIntake;
	private EditText target;
	private Uri generatedImageUri;
	private Uri imgUri;
	private Bitmap b;
	private boolean expandInsulinCalcsAuto = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);
		LinearLayout adviceFrame = (LinearLayout) findViewById(R.id.adviceFrame);
		adviceFrame.setVisibility(View.GONE);
		/*if(activityAdvice==null){
			LinearLayout adviceFrame = (LinearLayout) findViewById(R.id.adviceFrame);
			adviceFrame.setVisibility(View.INVISIBLE);
		}else{
			TextView advice = (TextView) findViewById(R.id.advice);

			advice.setText(this.activityAdvice.getExpandedText());
		}*/

		time = (TextView) findViewById(R.id.time);
		date = (TextView) findViewById(R.id.date);
		insulinIntake = (EditText) findViewById(R.id.insulin_intake);
		target = (EditText) findViewById(R.id.glycemia_target);

		fillDateHour(null, null);
		fillTagSpinner();
		fillInsulinSpinner();
		setupMealImage();
		setupClickListeners();

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
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

	public Uri getImgUri() {
		return imgUri;
	}

	public String getTime() {
		return time.getText().toString();
	}

	public String getDate() {
		return date.getText().toString();
	}

	void imageRemoved() {
		setImageUri(null);
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
		Spinner spinner = (Spinner) findViewById(R.id.tag);
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
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.date,
				DateUtils.getDateCalendar(((TextView) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	private void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.time,
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

	public void setPhaseOfDayByValue(String value) {
		DB_Read rdb = new DB_Read(this);
		String result = rdb.Tag_GetByTime(value).getName();
		rdb.close();
		Spinner spinner = (Spinner) findViewById(R.id.tag);
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

	private void addGlycemiaObjective() {
		Intent intent = new Intent(this, TargetBG_detail.class);
		EditText targetGlycemia = ((EditText) findViewById(R.id.glycemia_target));
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

	private boolean isFragmentShowing() {
		return fragmentInsulinCalcsFragment != null;
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

	protected abstract boolean shouldSetInsulin();

	@Nullable
	public String getPhaseOfDay() {
		Spinner spinner = (Spinner) findViewById(R.id.tag);
		if (spinner != null) {
			return spinner.getSelectedItem().toString();
		}
		return null;
	}

	@Nullable
	public String getNote() {
		EditText note = (EditText) findViewById(R.id.notes);
		if (note != null) {
			return note.getText().toString();
		}
		return null;
	}

	void setNote(String note) {
		EditText notes = ((EditText) findViewById(R.id.notes));
		if (notes != null) {
			notes.setText(note);
		}
	}

	@Nullable
	public String getInsulin() {
		Spinner insulin = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
		return insulin != null ? insulin.getSelectedItem().toString() : null;
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

	public float getInsulinIntake() {
		String insulinIn = insulinIntake.getText().toString();
		if (insulinIn.isEmpty()) {
			return 0;
		}
		return Float.parseFloat(insulinIntake.getText().toString());
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

	public void toggleInsulinCalcDetails(View view) {
		expandInsulinCalcsAuto = false;
		if (!isFragmentShowing()) {
			showCalcs();
		} else {
			hideCalcs();
		}
	}

	public boolean useIOB() {
		return useIOB;
	}

	public void setUseIOB(boolean useIOB) {
		this.useIOB = useIOB;
	}

	@Override
	public void setup() {
		fragmentInsulinCalcsFragment = (InsulinCalcFragment) getFragmentManager().findFragmentById(R.id.fragment_calcs);
		showCalcs();
	}

	/***
	 * Hide sections
	 */
	public void hideSection(int... sections) {
		for (int section : sections) {
			View view = findViewById(section);
			if (view != null) {
				view.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * Show section
	 *
	 * @param sections
	 */
	public void showSection(int... sections) {
		for (int section : sections) {
			View view = findViewById(section);
			if (view != null) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}
}
