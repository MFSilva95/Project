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
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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
import pt.it.porto.mydiabetes.ui.listAdapters.TagDataBinding;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;

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


	private ArrayList<String> allInsulins; // insulins in the database

	private MealFragmentListener mListener; // listener of events

	private EditText insulinunits;
	private EditText glycemia;
	private EditText carbs;
	private EditText target;

	private boolean showAddGlycemiaTarget;
	private InsulinCalc fragmentInsulinCalcs;
	private boolean expandInsulinCalcsAuto = false;
	private InsulinCalculator insulinCalculator = null;

	private Uri imgUri;
	private Bitmap b;


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

		insulinunits = (EditText) view.findViewById(R.id.et_MealDetail_InsulinUnits);
		target = (EditText) view.findViewById(R.id.et_MealDetail_TargetGlycemia);
		glycemia = (EditText) view.findViewById(R.id.et_MealDetail_Glycemia);
		carbs = (EditText) view.findViewById(R.id.et_MealDetail_Carbs);

		if (!showAddGlycemiaTarget) {
			view.findViewById(R.id.addTargetObjective).setVisibility(View.GONE);
		}
		fillDateHour(view);
		fillTagSpinner(view);
		setTagByTime(view);
		setTargetByHour(view);
		fillInsulinSpinner(view);
		setupClickListeners(view);
		setupMealImage(view);
		setUpdateListeners(view);
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

	private void selectSpinnerItemByValue(Spinner spinner, String value) {
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
				addGlycemiaObjective(v);
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

	private void setUpdateListeners(View view) {
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
					insulinCalculator.setInsulinTarget(0);
				} else {
					try {
						insulinCalculator.setInsulinTarget(Float.parseFloat(s.toString()));
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


		EditText hora = (EditText) view.findViewById(R.id.et_MealDetail_Hora);
		hora.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setTagByTime(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void fillDateHour(View view) {
		EditText date = (EditText) view.findViewById(R.id.et_MealDetail_Data);
		final Calendar c = Calendar.getInstance();
		date.setText(DatePickerFragment.getFormatedDate(c));
		date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);
			}
		});

		EditText hour = (EditText) view.findViewById(R.id.et_MealDetail_Hora);
		hour.setText(TimePickerFragment.getFormatedDate(c));
		hour.setOnClickListener(new View.OnClickListener() {
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
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	private void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_MealDetail_Hora,
				TimePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	private void setTagByTime(View viewArg) {
		View view = viewArg != null ? viewArg : getView();
		if (view == null) {
			return;
		}
		Spinner tagSpinner = (Spinner) view.findViewById(R.id.sp_MealDetail_Tag);
		EditText hora = (EditText) view.findViewById(R.id.et_MealDetail_Hora);
		DB_Read rdb = new DB_Read(getActivity());
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		selectSpinnerItemByValue(tagSpinner, name);
	}


	private void finish() {
		if (mListener != null) {
			mListener.exit();
		}
	}

	public void end() {
		finish();
	}


	private void setTargetByHour(View view) {
		EditText target = (EditText) view.findViewById(R.id.et_MealDetail_TargetGlycemia);
		EditText hora = (EditText) view.findViewById(R.id.et_MealDetail_Hora);
		DB_Read rdb = new DB_Read(getActivity());
		double d = rdb.Target_GetTargetByTime(hora.getText().toString());
		if (d != 0) {
			target.setText(String.valueOf(d));
			view.findViewById(R.id.addTargetObjective).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.addTargetObjective).setVisibility(View.VISIBLE);
		}
		rdb.close();
	}

	private void fillInsulinSpinner(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.sp_MealDetail_Insulin);
		allInsulins = new ArrayList<>();
		DB_Read rdb = new DB_Read(getActivity());
		HashMap<Integer, String> val = rdb.Insulin_GetAllNames();
		rdb.close();

		if (val != null) {
			for (int i : val.keySet()) {
				allInsulins.add(val.get(i));
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, allInsulins);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

	}

	private Uri generatedImageUri;

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
		save();
		mListener = null;
	}

	private void addGlycemiaObjective(View view) {
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

	public void setInsulinCalculator(InsulinCalculator insulinCalculator) {
		this.insulinCalculator = insulinCalculator;
		if (insulinCalculator.getCarbs() > 0) {
			carbs.setText(String.format("%d", (int) insulinCalculator.getCarbs()));
		}
		if (insulinCalculator.getGlycemia() > 0) {
			glycemia.setText(String.format("%d", (int) insulinCalculator.getGlycemia()));
		}
		if (insulinCalculator.getInsulinTarget() > 0) {
			target.setText(String.format("%d", (int) insulinCalculator.getInsulinTarget()));
		}
		float insulinTotal = insulinCalculator.getInsulinTotal(true, true);
		if (insulinTotal > 0) {
			insulinunits.setText(String.format("%d", (int) insulinTotal));
		}
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
	}


}
