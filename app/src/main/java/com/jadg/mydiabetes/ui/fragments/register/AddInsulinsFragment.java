package com.jadg.mydiabetes.ui.fragments.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.ui.activities.WelcomeActivity;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFormEnd} interface
 * to handle interaction events.
 * Use the {@link AddInsulinsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddInsulinsFragment extends Fragment implements WelcomeActivity.RegistryFragamentPage {

	private static final String TAG = AddInsulinsFragment.class.getCanonicalName();

	private static final String STATE_ITEMS = "items";

	private OnFormEnd mListener;
	private EditText mNameView;
	private EditText mHeightView;
	private RadioGroup mGenderGroup;
	private LinearLayout list;
	private ArrayList<InsulinData> items = new ArrayList<>(3);

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment RegistPersonalDataFragment.
	 */
	public static AddInsulinsFragment newInstance() {
		AddInsulinsFragment fragment = new AddInsulinsFragment();
		return fragment;
	}

	public AddInsulinsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STATE_ITEMS, items);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View layout = inflater.inflate(R.layout.fragment_register_insulins, container, false);
		list = (LinearLayout) layout.findViewById(R.id.insulin_list);

		Button button = (Button) layout.findViewById(R.id.new_insulin);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addInsulin();
			}
		});
		if (savedInstanceState == null) {
			addInsulin();
		} else {
			items = (ArrayList<InsulinData>) savedInstanceState.getSerializable(STATE_ITEMS);
			updateInsulinUI();
		}

		return layout;
	}

	private void updateInsulinUI() {
		for (int i = 0; i < items.size(); i++) {
			InsulinData item = items.get(i);
			View view = getLayoutInflater(null).inflate(R.layout.listitem_new_insulin, list, true);
			view.setTag(i);
			((EditText) view.findViewById(R.id.name)).setText(item.name);
			((EditText) view.findViewById(R.id.admininistration_method)).setText(item.administrationMethod);
			((Spinner) view.findViewById(R.id.insulin_type)).setSelection(item.action);

			((EditText) view.findViewById(R.id.name)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
					if (i == EditorInfo.IME_ACTION_DONE) {
						items.get((int) (((View) textView.getParent())).getTag()).name = textView.getText().toString();
					}
					return false;
				}
			});
			view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View view, boolean b) {
					if (!b) {
						items.get((int) view.getTag()).name = ((EditText) view.findViewById(R.id.name)).getText().toString();
						items.get((int) view.getTag()).administrationMethod = ((EditText) view.findViewById(R.id.admininistration_method)).getText().toString();
						items.get((int) view.getTag()).action = ((Spinner) view.findViewById(R.id.insulin_type)).getSelectedItemPosition();
					}
				}
			});
		}
	}

	private void addInsulin() {
		items.add(new InsulinData());
		View view = getLayoutInflater(null).inflate(R.layout.listitem_new_insulin, list, true);
		view.setTag(items.size() - 1);
		view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					items.get((int) view.getTag()).name = ((EditText) view.findViewById(R.id.name)).getText().toString();
					items.get((int) view.getTag()).administrationMethod = ((EditText) view.findViewById(R.id.admininistration_method)).getText().toString();
					items.get((int) view.getTag()).action = ((Spinner) view.findViewById(R.id.insulin_type)).getSelectedItemPosition();
				}
			}
		});
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
//		mNameView.setError(null);
//		mHeightView.setError(null);
//		mDateView.setError(null);
//		((RadioButton) mGenderGroup.getChildAt(1)).setError(null);

		boolean cancel = false;
		View focusView = null;

//		// Checks if name isn't empty
//		if (TextUtils.isEmpty(name)) {
//			mNameView.setError(getString(R.string.error_field_required));
//			focusView = mNameView;
//			cancel = true;
//		}

		// Check if gender is selected
//		if (mGenderGroup.getCheckedRadioButtonId() == -1) {
//			((RadioButton) mGenderGroup.getChildAt(1)).setError(getString(R.string.error_field_required));
//			focusView = mGenderGroup;
//			cancel = true;
//		}


		if (cancel) {
			// There was an error;
			// form field with an error.
			focusView.requestFocus();
		}
		return !cancel;
	}

	@Override
	public void saveData(Bundle container) {//TODO
	}


	static class InsulinData implements Serializable, Parcelable {
		String name;
		String administrationMethod;
		int action;

		public InsulinData() {
		}

		protected InsulinData(Parcel in) {
			name = in.readString();
			administrationMethod = in.readString();
			action = in.readInt();
		}

		public static final Creator<InsulinData> CREATOR = new Creator<InsulinData>() {
			@Override
			public InsulinData createFromParcel(Parcel in) {
				return new InsulinData(in);
			}

			@Override
			public InsulinData[] newArray(int size) {
				return new InsulinData[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel parcel, int i) {
			parcel.writeString(name);
			parcel.writeString(administrationMethod);
			parcel.writeInt(action);
		}
	}

}
