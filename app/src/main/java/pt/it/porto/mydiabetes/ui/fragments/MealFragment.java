package pt.it.porto.mydiabetes.ui.fragments;

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
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
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
import pt.it.porto.mydiabetes.ui.activities.TargetBG_detail;
import pt.it.porto.mydiabetes.ui.activities.ViewPhoto;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.dataBinding.TagDataBinding;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

/**
 * A fragment that contains the UI to add or edit a new meal.
 * <p><b>The activity needs to setup and provide the InsulinCalculator!</b></p>
 * <p/>
 * Activities that contain this fragment must implement the
 * {@link MealFragmentListener} interface
 * to handle interaction events.
 * Use the {@link MealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MealFragment extends Fragment {
	// activity flags start for result
	public final static int IMAGE_CAPTURE = 2;
	public final static int IMAGE_VIEW = 3;

	private static final String ARG_SHOW_ADD_GLYCEMIA_TARGET = "param1"; // optional argument to create fragment
	private static final String GENERATED_IMAGE_URI = "generated_image_uri";


	private MealFragmentListener mListener; // listener of events

	private EditText insulinIntake;
	private EditText glycemia;
	private EditText carbs;
	private EditText target;
	private EditText time;
	private EditText date;

	private boolean showAddGlycemiaTarget;
	private InsulinCalc fragmentInsulinCalcs;
	private boolean expandInsulinCalcsAuto = false;
	private InsulinCalculator insulinCalculator = null;

	private Uri imgUri;
	private Bitmap b;

	// support update indicator
	private InsulinCalculator oldInsulinCalculator = null; // the old insulincalculator to compare

	// listeners
	@Nullable
	private OnTextChange listenerGlycemiaChange;
	@Nullable
	private OnTextChange listenerCarbsChange;
	@Nullable
	private OnTextChange listenerGlycemiaTargetChange;
	@Nullable
	private OnTextChange listenerInsulinIntakeChange;
	@Nullable
	private OnTextChange listenerDateChangeChange;
	@Nullable
	private OnTextChange listenerTimeChangeChange;

	private Uri generatedImageUri;


	public MealFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment MealFragment.
	 */
	public static MealFragment newInstance(boolean showAddGlycemiaTarget) {
		MealFragment fragment = new MealFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_SHOW_ADD_GLYCEMIA_TARGET, showAddGlycemiaTarget);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			showAddGlycemiaTarget = getArguments().getBoolean(ARG_SHOW_ADD_GLYCEMIA_TARGET);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meal, container, false);

		insulinIntake = (EditText) view.findViewById(R.id.et_MealDetail_InsulinUnits);
		target = (EditText) view.findViewById(R.id.et_MealDetail_TargetGlycemia);
		glycemia = (EditText) view.findViewById(R.id.et_MealDetail_Glycemia);
		carbs = (EditText) view.findViewById(R.id.et_MealDetail_Carbs);
		time = (EditText) view.findViewById(R.id.et_MealDetail_Hora);
		date = (EditText) view.findViewById(R.id.et_MealDetail_Date);

		if (!showAddGlycemiaTarget) {
			view.findViewById(R.id.addTargetObjective).setVisibility(View.GONE);
		}
		fillDateHour();
		fillTagSpinner(view);
		fillInsulinSpinner(view);
		setupClickListeners(view);
		setupMealImage(view);
		setUpdateListeners();
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(GENERATED_IMAGE_URI, generatedImageUri);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(GENERATED_IMAGE_URI)) {
			generatedImageUri = savedInstanceState.getParcelable(GENERATED_IMAGE_URI);
		}
	}

	public void setPhaseOfDayByValue(String value) {
		View view = getView();
		if (view == null) {
			return;
		}
		Spinner spinner = (Spinner) view.findViewById(R.id.sp_MealDetail_Tag);
		SpinnerAdapter adapter = spinner.getAdapter();
		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItem(position).equals(value)) {
				spinner.setSelection(position);
				return;
			}
		}
	}

	private void setupMealImage(View view) {
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_MealDetail_Photo);
		if (imgUri == null) {
			imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.newphoto, null));
		} else {
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int) (displaymetrics.heightPixels * 0.1);
			int width = (int) (displaymetrics.widthPixels * 0.1);
			b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
			imageView.setImageBitmap(b);
		}
	}

	private void setupClickListeners(View view) {
		view.findViewById(R.id.iv_MealDetail_Photo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (imgUri != null) {
					final Intent intent = new Intent(getActivity(), ViewPhoto.class);
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

		view.findViewById(R.id.addTargetObjective).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addGlycemiaObjective();
			}
		});

		view.findViewById(R.id.bt_insulin_calc_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleInsulinCalcDetails(v);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_CANCELED && requestCode == MealFragment.IMAGE_CAPTURE) {
			setImageUri(generatedImageUri);
		} else if (requestCode == MealFragment.IMAGE_VIEW) {
			//se tivermos apagado a foto dá result code -1
			//se voltarmos por um return por exemplo o resultcode é 0
			if (resultCode == -1) {
				setImageUri(null);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				float val = 0;
				if (!text.isEmpty()) {
					try {
						val = Float.parseFloat(s.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				insulinCalculator.setGlycemiaTarget((int) val);
				setInsulinIntake();
				if (oldInsulinCalculator != null) {
					updateIndicator(target, Float.compare(oldInsulinCalculator.getInsulinTarget(), val) != 0);
				}
				if (listenerGlycemiaTargetChange != null) {
					listenerGlycemiaTargetChange.onTextChange(target, text);
				}
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
				if (oldInsulinCalculator != null) {
					updateIndicator(glycemia, ((int) oldInsulinCalculator.getGlycemia()) != val);
				}
				if (listenerGlycemiaChange != null) {
					listenerGlycemiaChange.onTextChange(glycemia, text);
				}
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
				if (oldInsulinCalculator != null) {
					updateIndicator(carbs, ((int) oldInsulinCalculator.getGlycemia()) != val);
				}
				if (listenerCarbsChange != null) {
					listenerCarbsChange.onTextChange(carbs, text);
				}
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
				if (listenerTimeChangeChange != null) {
					listenerTimeChangeChange.onTextChange(time, s.toString());
				}
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
				if (listenerDateChangeChange != null) {
					listenerDateChangeChange.onTextChange(date, s.toString());
				}
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
				float val = 0;
				if (!text.isEmpty()) {
					try {
						val = Float.parseFloat(s.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				if (oldInsulinCalculator != null) {
					updateIndicator(insulinIntake, ((int) oldInsulinCalculator.getInsulinTotal(true, true)) != val);
				}
				if (listenerInsulinIntakeChange != null) {
					listenerInsulinIntakeChange.onTextChange(insulinIntake, text);
				}
			}

		});
	}

	private void updateIndicator(EditText view, boolean valueChanged) {
		if (valueChanged) {
			view.setBackgroundResource(R.drawable.edit_text_holo_dark_changed);
		} else {
			view.setBackgroundResource(R.drawable.default_edit_text_holo_dark);
		}
	}

	private void fillDateHour() {
		Calendar c = Calendar.getInstance();
		date.setText(DateUtils.getFormattedDate(c));
		date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);
			}
		});

		time.setText(DateUtils.getFormattedTime(c));
		time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showTimePickerDialog(v);
			}
		});
	}

	private void fillTagSpinner(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.sp_MealDetail_Tag);
		ArrayList<String> allTags = new ArrayList<>();
		DB_Read rdb = new DB_Read(getActivity());
		ArrayList<TagDataBinding> t = rdb.Tag_GetAll();
		rdb.close();


		if (t != null) {
			for (TagDataBinding i : t) {
				allTags.add(i.getName());
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, allTags);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	private void showDatePickerDialog(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_MealDetail_Data,
				DateUtils.getDateCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	private void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_MealDetail_Hora,
				DateUtils.getTimeCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	private void finish() {
		if (mListener != null) {
			mListener.exit();
		}
	}

	public void end() {
		finish();
	}


	private void fillInsulinSpinner(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.sp_MealDetail_Insulin);
		ArrayList<String> allInsulins = new ArrayList<>();
		DB_Read rdb = new DB_Read(getActivity());
		HashMap<Integer, String> val = rdb.Insulin_GetAllNames();
		rdb.close();

		if (val != null) {
			for (int i : val.keySet()) {
				allInsulins.add(val.get(i));
			}
		} else {
			if (mListener != null) {
				mListener.insulinsNotFound();
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, allInsulins);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

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
		View view = getView();
		if (view == null) {
			return;
		}
		ImageView img = (ImageView) view.findViewById(R.id.iv_MealDetail_Photo);
		if (imgUri == null) {
			img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.newphoto, null));
		} else {
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int) (displaymetrics.heightPixels * 0.1);
			int width = (int) (displaymetrics.widthPixels * 0.1);
			b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
			img.setImageBitmap(b);
		}
	}

	public void save() {
		if (mListener != null) {
			mListener.saveData();
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof MealFragmentListener) {
			mListener = (MealFragmentListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement MealFragmentListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	private void addGlycemiaObjective() {
		Intent intent = new Intent(getActivity(), TargetBG_detail.class);
		View viewParent = getView();
		String goal = null;
		if (viewParent != null) {
			goal = ((EditText) getView().findViewById(R.id.et_MealDetail_TargetGlycemia)).getText().toString();
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
				View view = getView();
				if (view != null) {
					view.findViewById(R.id.fragment_calcs).startAnimation(animation);
					((ToggleButton) view.findViewById(R.id.bt_insulin_calc_info)).setChecked(true);
				}
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
			View view = getView();
			if (view != null) {
				view.findViewById(R.id.fragment_calcs).startAnimation(animation);
			}
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

	private void setInsulinIntake() {
		if (expandInsulinCalcsAuto || isFragmentShowing()) {
			showCalcs();
		}

		float insulin = insulinCalculator.getInsulinTotal(true, true);
		insulinIntake.setText(String.valueOf(insulin > 0 ? insulin : 0));
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

	public void setShowUpdateIndicator(boolean showUpdateIndicator) {
		if (showUpdateIndicator) {
			this.oldInsulinCalculator = insulinCalculator.clone();
		} else {
			this.oldInsulinCalculator = null;
		}
	}

	public void setGlycemiaListener(OnTextChange listener) {
		this.listenerGlycemiaChange = listener;
	}

	public void setCarbsListener(OnTextChange listener) {
		this.listenerCarbsChange = listener;
	}

	public void setGlycemiaTargetListener(OnTextChange listener) {
		this.listenerGlycemiaTargetChange = listener;
	}

	public void setInsulinIntakeListener(OnTextChange listener) {
		this.listenerInsulinIntakeChange = listener;
	}

	public void setDateChangeListener(OnTextChange listener) {
		this.listenerDateChangeChange = listener;
	}

	public void setTimeChangeListener(OnTextChange listener) {
		this.listenerTimeChangeChange = listener;
	}

	public void setGlycemiaTarget(float val) {
		target.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", val)); // using ENGLISH_LOCALE to have "." as number separator instead of ","
		View view = getView();
		if (view == null) {
			return;
		}
		if (val != 0) {
			view.findViewById(R.id.addTargetObjective).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.addTargetObjective).setVisibility(View.VISIBLE);
		}
	}

	public InsulinCalculator getInsulinCalculator() {
		return insulinCalculator;
	}

	public void setInsulinCalculator(InsulinCalculator insulinCalculator) {
		this.insulinCalculator = insulinCalculator;
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

	public String getPhaseOfDay(){
		View view = getView();
		if (view == null) {
			return null;
		}
		Spinner spinner = (Spinner) view.findViewById(R.id.sp_MealDetail_Tag);
		return spinner.getSelectedItem().toString();
	}

	public String getNote(){
		View view = getView();
		if (view == null) {
			return null;
		}
		EditText note = (EditText) view.findViewById(R.id.et_MealDetail_Notes);
		return note.getText().toString();
	}

	public boolean canSave(){
		return !carbs.getText().toString().isEmpty() && !glycemia.getText().toString().isEmpty();
	}

	public String getInsulin() {
		View view = getView();
		if (view == null) {
			return null;
		}
		Spinner insulin = (Spinner) view.findViewById(R.id.sp_MealDetail_Insulin);
		return insulin.getSelectedItem().toString();
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface MealFragmentListener {
		void saveData();

		void exit();

		void insulinsNotFound();
	}

	public interface OnTextChange {
		void onTextChange(View view, String text);
	}
}
