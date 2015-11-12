package com.jadg.mydiabetes.ui.fragments.register;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.ui.activities.WelcomeActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


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
	private EditText mNameView;
	private EditText mHeightView;
	private EditText mDateView;
	private RadioGroup mGenderGroup;

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
		View layout = inflater.inflate(R.layout.fragment_register_personal_data, container, false);
		mNameView = (EditText) layout.findViewById(R.id.name);


		mGenderGroup = (RadioGroup) layout.findViewById(R.id.gender_group);
		mGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				// clears error state if needed
				((RadioButton) mGenderGroup.getChildAt(1)).setError(null);
			}
		});
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
		mNameView.setError(null);
		mHeightView.setError(null);

		// Store values at the time of the login attempt.
		String name = mNameView.getText().toString();
		String height = mHeightView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Checks if name isn't empty
		if (TextUtils.isEmpty(name)) {
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
			cancel = true;
		}

		// Check if gender is selected
		if (mGenderGroup.getCheckedRadioButtonId() == -1) {
			((RadioButton) mGenderGroup.getChildAt(1)).setError(getString(R.string.error_field_required));
			focusView = mGenderGroup;
			cancel = true;
		}



		if (cancel) {
			// There was an error;
			// form field with an error.
			focusView.requestFocus();
		}
		return cancel;
	}

}
