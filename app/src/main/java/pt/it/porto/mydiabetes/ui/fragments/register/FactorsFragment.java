package pt.it.porto.mydiabetes.ui.fragments.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.utils.OnSwipeTouchListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFormEnd} interface
 * to handle interaction events.
 * Use the {@link FactorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FactorsFragment extends Fragment implements WelcomeActivity.RegistryFragmentPage {


	private static final String TAG = FactorsFragment.class.getCanonicalName();
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

		ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(), R.array.diabetes_Type , R.layout.welcome_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		diabetesType.setAdapter(adapter);

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
		MyDiabetesStorage storage = MyDiabetesStorage.getInstance(getContext());
		boolean success = storage.addUserData(container.getString(WelcomeActivity.USER_DATA_NAME),
				String.valueOf(diabetesType.getSelectedItemPosition()),
				Integer.parseInt(sensibilityFactor.getText().toString(), 10),
				Integer.parseInt(carbsRatio.getText().toString(), 10),
				Integer.parseInt(hypoglycemiaLimit.getText().toString(), 10),
				Integer.parseInt(hyperglycemiaLimit.getText().toString(), 10),
				container.getString(WelcomeActivity.USER_DATA_BIRTHDAY_DATE),
				container.getString(WelcomeActivity.USER_DATA_GENDER),
				Float.parseFloat(container.getString(WelcomeActivity.USER_DATA_HEIGHT)));
		if(!success){
			Log.w(TAG, "Failed to save user data!");
		}
	}

	@Override
	public int getSubtitle() {
		return R.string.subtitle_diabetes_factors;
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
