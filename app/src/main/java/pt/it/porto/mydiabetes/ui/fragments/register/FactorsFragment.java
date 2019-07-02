package pt.it.porto.mydiabetes.ui.fragments.register;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import java.io.File;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;


public class FactorsFragment extends Fragment implements WelcomeActivity.RegistryFragmentPage {


	private static final String TAG = FactorsFragment.class.getCanonicalName();
	private Spinner diabetesType;
	private EditText sensibilityFactor;
	private EditText bg_target_Factor;
	private EditText carbsRatio;
	private EditText hypoglycemiaLimit;
	private EditText hyperglycemiaLimit;
	private View layout = null;
	private String userImgFileName = "profilePhoto";
	private ScrollView scrollView;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment RegistPersonalDataFragment.
	 */
	public static FactorsFragment newInstance() {
		FactorsFragment fragment = new FactorsFragment();
		return fragment;
	}

	public FactorsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		DB_Read read = new DB_Read(this.getContext());
		UserInfo user_info = read.MyData_Read();
		read.close();

		layout = inflater.inflate(R.layout.fragment_register_factors, container, false);
		scrollView = (ScrollView) layout.findViewById(R.id.scrollview);

		diabetesType = (Spinner) layout.findViewById(R.id.diabetes_type);

		ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.diabetes_Type , R.layout.welcome_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		diabetesType.setAdapter(adapter);

		sensibilityFactor = (EditText) layout.findViewById(R.id.sensibility_factor);
		bg_target_Factor = (EditText) layout.findViewById(R.id.bg_target_Factor);
		carbsRatio = (EditText) layout.findViewById(R.id.carbs_ratio);
		hypoglycemiaLimit = (EditText) layout.findViewById(R.id.hypoglycemia_limit);
		hyperglycemiaLimit = (EditText) layout.findViewById(R.id.hyperglycemia_limit);


		if(user_info!=null){
			float carbsR;
			if((carbsR = user_info.getCarbsRatio())!=-1){carbsRatio.setText(carbsR+"");}
			float sensF;
			if((sensF = user_info.getInsulinRatio())!=-1){sensibilityFactor.setText(sensF+"");}
			int hypoV;
			if((hypoV = user_info.getLowerRange())!=-1){hypoglycemiaLimit.setText(hypoV+"");}
			int hyperV;
			if((hyperV = user_info.getHigherRange())!=-1){hyperglycemiaLimit.setText(hyperV+"");}
			int bg;
			if((bg=user_info.getTG())!=-1){bg_target_Factor.setText(bg+"");}
		}


		sensibilityFactor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					focusOnView(sensibilityFactor);
				}
			}
		});
		carbsRatio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					focusOnView(carbsRatio);
				}
			}
		});
		hypoglycemiaLimit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					focusOnView(hypoglycemiaLimit);
				}
			}
		});
		hyperglycemiaLimit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					focusOnView(hyperglycemiaLimit);
				}
			}
		});


		return layout;
	}

	@Override
	public boolean allFieldsAreValid() {
		// Reset errors.
		sensibilityFactor.setError(null);
		carbsRatio.setError(null);
		hypoglycemiaLimit.setError(null);
		hyperglycemiaLimit.setError(null);
		bg_target_Factor.setError(null);


		// Store values at the time of the login attempt.
		String sensibilityFactorVal = sensibilityFactor.getText().toString();
		String carbsRatioVal = carbsRatio.getText().toString();
		String hypoglycemiaLimitVal = hypoglycemiaLimit.getText().toString();
		String hyperglycemiaLimitVal = hyperglycemiaLimit.getText().toString();
		String targetBGVal = bg_target_Factor.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Checks if sensibility Factor is valid
		float val = getNumber(sensibilityFactorVal);

		if (TextUtils.isEmpty(sensibilityFactorVal) || Float.compare(val, -1) <= 0) {
			sensibilityFactor.setError(getString(R.string.error_field_required));
			focusView = sensibilityFactor;
			cancel = true;
		}

		// Checks if carbs Ratio is valid
		val = getNumber(carbsRatioVal);
		if (TextUtils.isEmpty(carbsRatioVal) || Float.compare(val, -1) <= 0) {
			carbsRatio.setError(getString(R.string.error_field_required));
			focusView = carbsRatio;
			cancel = true;
		}

		// Checks if hypoglycemia limit is valid
		val = getNumber(hypoglycemiaLimitVal);
		if (TextUtils.isEmpty(hypoglycemiaLimitVal) || Float.compare(val, -1) <= 0) {
			hypoglycemiaLimit.setError(getString(R.string.error_field_required));
			focusView = hypoglycemiaLimit;
			cancel = true;
		}else {
			if (val <= 40 || val >= 500) {
				hypoglycemiaLimit.setError(getString(R.string.error_invalid_parameter));
				focusView = hypoglycemiaLimit;
				cancel = true;
			}
		}

		// Checks if hyperglycemia limit is valid
		val = getNumber(hyperglycemiaLimitVal);
		if (TextUtils.isEmpty(hyperglycemiaLimitVal) || Float.compare(val, -1) <= 0) {
			hyperglycemiaLimit.setError(getString(R.string.error_field_required));
			focusView = hyperglycemiaLimit;
			cancel = true;
		}else{
			if(val<=40 || val >= 500){
				hyperglycemiaLimit.setError(getString(R.string.error_invalid_parameter));
				focusView = hyperglycemiaLimit;
				cancel = true;
			}
		}

		val = getNumber(targetBGVal);
		if (TextUtils.isEmpty(targetBGVal) || Float.compare(val, -1) <= 0) {
			bg_target_Factor.setError(getString(R.string.error_field_required));
			focusView = bg_target_Factor;
			cancel = true;
		}else{
			if(val<=40 || val >= 500){
			bg_target_Factor.setError(getString(R.string.error_invalid_parameter));
			focusView = bg_target_Factor;
			cancel = true;
			}
		}




		if (cancel) {
			// There was an error;
			// form field with an error.
			focusView.requestFocus();
		}
		return !cancel;
	}

	@Override
	public void saveData(Bundle container) {

		float height = Float.parseFloat(container.getString(WelcomeActivity.USER_DATA_HEIGHT));

		if(height>100){height = height/100;}

		float carbsR = Float.parseFloat(carbsRatio.getText().toString());
		float sensR = Float.parseFloat(sensibilityFactor.getText().toString());
		int bg_target = Integer.parseInt(bg_target_Factor.getText().toString(), 10);

		MyDiabetesStorage storage = MyDiabetesStorage.getInstance(getContext());
		boolean success = storage.addUserData(container.getString(WelcomeActivity.USER_DATA_NAME),
				diabetesType.getSelectedItemPosition(),
				sensR,
				carbsR,
				Integer.parseInt(hypoglycemiaLimit.getText().toString(), 10),
				Integer.parseInt(hyperglycemiaLimit.getText().toString(), 10),
				container.getString(WelcomeActivity.USER_DATA_BIRTHDAY_DATE),
				container.getInt(WelcomeActivity.USER_DATA_GENDER),
				height,bg_target);
		if(!success){
			Log.w(TAG, "Failed to save user data!");
		}

		File profile_img = new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg");

		DB_Read read = new DB_Read(getContext());
<<<<<<< HEAD

		boolean hasSensRatioData = read.hasSensData();
		boolean hasCarbsRatioData = read.hasRatioData();
		boolean hasTargetRatioData = read.hasTargetdata();

		if(profile_img.exists()){
				BadgeUtils.addPhotoBadge(getContext(), read);
		}
=======
		//if(profile_img.exists()){
		//		BadgeUtils.addPhotoBadge(getContext(), read);
		//}

>>>>>>> origin/widget_gamification


		if (!hasCarbsRatioData){storage.initRatioSens(carbsR, "Ratio_Reg");}
		if (!hasSensRatioData){storage.initRatioSens(sensR, "Sensitivity_Reg");}
		if (!hasTargetRatioData){storage.initTarget_bg(bg_target);}



		LevelsPointsUtils.addPoints(getContext(),0,"first", read);
		read.close();

	}

	private float getNumber(String val) {
		float result = -1;
		try {
			result = Float.parseFloat(val);
		} catch (NumberFormatException ignored) {
		}
		return result;
	}
	@Override
	public int getSubtitle() {
		return R.string.subtitle_diabetes_factors;
	}

	private final void focusOnView(final View view){
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.scrollTo(0, view.getBottom());
			}
		});
	}


}
