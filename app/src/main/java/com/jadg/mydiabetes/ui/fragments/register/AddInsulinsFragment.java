package com.jadg.mydiabetes.ui.fragments.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
	private RecyclerView list;
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
		list = (RecyclerView) layout.findViewById(R.id.insulin_list);

		list.setAdapter(new InsulinAdapter());
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setItemAnimator(new DefaultItemAnimator());

//		Button button = (Button) layout.findViewById(R.id.new_insulin);
//		button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				addInsulin();
//			}
//		});
		if (savedInstanceState == null) {
			addInsulin();
		} else {
			items = (ArrayList) savedInstanceState.getSerializable(STATE_ITEMS);
//			updateInsulinUI();
		}

		return layout;
	}

	//	private void updateInsulinUI() {
//		for (int i = 0; i < items.size(); i++) {
//			Object obj = items.get(i);
//			if (!(obj instanceof InsulinData)) {
//				continue;
//			}
//			InsulinData item = (InsulinData) obj;
//			View view = getLayoutInflater(null).inflate(R.layout.listitem_new_insulin, list, true);
//			view.setTag(i);
//			((EditText) view.findViewById(R.id.name)).setText(item.name);
//			((EditText) view.findViewById(R.id.admininistration_method)).setText(item.administrationMethod);
//			((Spinner) view.findViewById(R.id.insulin_type)).setSelection(item.action);
//
//			((EditText) view.findViewById(R.id.name)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
//				@Override
//				public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//					if (i == EditorInfo.IME_ACTION_DONE) {
//						items.get((int) (((View) textView.getParent())).getTag()).name = textView.getText().toString();
//					}
//					return false;
//				}
//			});
//			view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//				@Override
//				public void onFocusChange(View view, boolean b) {
//					if (!b) {
//						items.get((int) view.getTag()).name = ((EditText) view.findViewById(R.id.name)).getText().toString();
//						items.get((int) view.getTag()).administrationMethod = ((EditText) view.findViewById(R.id.admininistration_method)).getText().toString();
//						items.get((int) view.getTag()).action = ((Spinner) view.findViewById(R.id.insulin_type)).getSelectedItemPosition();
//					}
//				}
//			});
//		}
//	}
//
	private void addInsulin() {
		items.add(new InsulinData());
		list.getAdapter().notifyItemInserted(items.size());
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

	class Holder extends RecyclerView.ViewHolder {

		public Holder(View itemView) {
			super(itemView);
		}
	}

	class InsulinHolder extends Holder {
		EditText name;
		EditText adminMethod;
		Spinner insulinType;
		InsulinData element;

		public InsulinHolder(View itemView) {
			super(itemView);
			name = (EditText) itemView.findViewById(R.id.name);
			adminMethod = (EditText) itemView.findViewById(R.id.admininistration_method);
			insulinType = (Spinner) itemView.findViewById(R.id.insulin_type);
		}
	}

	class ButtonHolder extends Holder {

		public ButtonHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					addInsulin();
				}
			});
		}
	}

	class InsulinAdapter extends RecyclerView.Adapter<Holder> {
		private static final int TYPE_NEW_INSULIN = 0;
		private static final int TYPE_FOOTER_BUTTON = 1;


		@Override
		public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater layoutInflator = getLayoutInflater(null);
			if (viewType == TYPE_FOOTER_BUTTON) {
				return new ButtonHolder(layoutInflator.inflate(R.layout.list_item_new_insulin_button, parent, false));
			}
			return new InsulinHolder(layoutInflator.inflate(R.layout.listitem_new_insulin, parent, false));
		}

		@Override
		public int getItemViewType(int position) {
			return position == items.size() ? TYPE_FOOTER_BUTTON : TYPE_NEW_INSULIN;
		}

		@Override
		public void onBindViewHolder(Holder holder, int position) {
			if (holder instanceof InsulinHolder) {
				InsulinData data = items.get(position);
				setBinding((InsulinHolder) holder, data);
			}
		}

		@Override
		public int getItemCount() {
			return items.size() + 1;
		}
	}


	private void setBinding(InsulinHolder holder, InsulinData data) {
		holder.name.setText(data.name);
		holder.name.setTag(data);
		holder.name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					((InsulinData) view.getTag()).name = ((EditText) view).getText().toString();
				}
			}
		});

		holder.adminMethod.setText(data.administrationMethod);
		holder.adminMethod.setTag(data);
		holder.adminMethod.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					((InsulinData) view.getTag()).administrationMethod = ((EditText) view).getText().toString();
				}
			}
		});

		holder.insulinType.setSelection(data.action);
		holder.insulinType.setTag(data);
		holder.insulinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				((InsulinData) ((View) view.getParent()).getTag()).action = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});
	}
}
