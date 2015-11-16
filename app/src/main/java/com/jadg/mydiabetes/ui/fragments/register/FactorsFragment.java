package com.jadg.mydiabetes.ui.fragments.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.ui.activities.WelcomeActivity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFormEnd} interface
 * to handle interaction events.
 * Use the {@link FactorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FactorsFragment extends Fragment implements WelcomeActivity.RegistryFragamentPage {


	private OnFormEnd mListener;
	private Spinner diabetesType;
	private EditText sensibilityFactor;
	private EditText carbsRatio;
	private EditText hypoglycemiaLimit;
	private EditText hyperglycemiaLimit;

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
		View layout = inflater.inflate(R.layout.fragment_register_factors, container, false);

		diabetesType = (Spinner) layout.findViewById(R.id.diabetes_type);
		sensibilityFactor = (EditText) layout.findViewById(R.id.sensibility_factor);
		carbsRatio = (EditText) layout.findViewById(R.id.carbs_ratio);
		hypoglycemiaLimit = (EditText) layout.findViewById(R.id.hypoglycemia_limit);
		hyperglycemiaLimit = (EditText) layout.findViewById(R.id.hyperglycemia_limit);

		return layout;
	}

	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.formFillEnded();
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFormEnd) {
			mListener = (OnFormEnd) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFormEnd");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	@Override
	public boolean allFieldsAreValid() {
		// Reset errors.
		sensibilityFactor.setError(null);
		carbsRatio.setError(null);
		hypoglycemiaLimit.setError(null);
		hyperglycemiaLimit.setError(null);


		// Store values at the time of the login attempt.
		String sensibilityFactorVal = sensibilityFactor.getText().toString();
		String carbsRatioVal = carbsRatio.getText().toString();
		String hypoglycemiaLimitVal = hypoglycemiaLimit.getText().toString();
		String hyperglycemiaLimitVal = hyperglycemiaLimit.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Checks if sensibility Factor is valid
		float val = getNumber(sensibilityFactorVal);
		if (TextUtils.isEmpty(sensibilityFactorVal) && Float.compare(val, -1) <= 0) {
			sensibilityFactor.setError(getString(R.string.error_field_required));
			focusView = sensibilityFactor;
			cancel = true;
		}

		// Checks if carbs Ratio is valid
		val = getNumber(carbsRatioVal);
		if (TextUtils.isEmpty(carbsRatioVal) && Float.compare(val, -1) <= 0) {
			carbsRatio.setError(getString(R.string.error_field_required));
			focusView = carbsRatio;
			cancel = true;
		}

		// Checks if hypoglycemia limit is valid
		val = getNumber(hypoglycemiaLimitVal);
		if (TextUtils.isEmpty(hypoglycemiaLimitVal) && Float.compare(val, -1) <= 0) {
			hypoglycemiaLimit.setError(getString(R.string.error_field_required));
			focusView = hypoglycemiaLimit;
			cancel = true;
		}

		// Checks if hyperglycemia limit is valid
		val = getNumber(hyperglycemiaLimitVal);
		if (TextUtils.isEmpty(hyperglycemiaLimitVal) && Float.compare(val, -1) <= 0) {
			hyperglycemiaLimit.setError(getString(R.string.error_field_required));
			focusView = hyperglycemiaLimit;
			cancel = true;
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
		container.putString(WelcomeActivity.USER_DATA_DIABETES_TYPE,String.valueOf(diabetesType.getSelectedItemPosition()));
		container.putString(WelcomeActivity.USER_DATA_SENSIBILITY_FACTOR,sensibilityFactor.getText().toString());
		container.putString(WelcomeActivity.USER_DATA_CARBS_RATIO, carbsRatio.getText().toString());
		container.putString(WelcomeActivity.USER_DATA_HYPOGLYCEMIA_LIMIT, hypoglycemiaLimit.getText().toString());
		container.putString(WelcomeActivity.USER_DATA_HYPERGLYCEMIA_LIMIT, hyperglycemiaLimit.getText().toString());
	}

	private float getNumber(String val) {
		float result = -1;
		try {
			result = Float.parseFloat(val);
		} catch (NumberFormatException ignored) {
		}
		return result;
	}
}
