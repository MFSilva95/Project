package pt.it.porto.mydiabetes.ui.fragments.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.ui.views.InsulinData;
import pt.it.porto.mydiabetes.ui.views.InsulinElement;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFormEnd} interface
 * to handle interaction events.
 * Use the {@link AddInsulinsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddInsulinsFragment extends Fragment implements WelcomeActivity.RegistryFragmentPage {

	private static final String TAG = AddInsulinsFragment.class.getCanonicalName();

	private static final String STATE_ITEMS = "items";

	private OnFormEnd mListener;
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
		if (savedInstanceState != null) {
			items = (ArrayList) savedInstanceState.getSerializable(STATE_ITEMS);
		}
		return layout;
	}

	private void addInsulin() {
		items.add(new InsulinData(items.size()));
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
			if (items.size() == 0) {
				mListener.desactivateNextButton();
			} else {
				mListener.activateNextButton();
			}
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
		boolean cancel = false;
		if (items.size() == 0) {
			items.add(new InsulinData(0));
			list.getAdapter().notifyItemInserted(0);
			return false;
		}
		ArrayList<String> names = new ArrayList<>(items.size());
		for (int i = 0; i < items.size(); i++) {
			if (!items.get(i).isValid() || names.contains(items.get(i).getName())) {
				items.get(i).setInvalid();
				list.getAdapter().notifyItemChanged(i);
				if (!cancel) {
					list.scrollToPosition(i);
					cancel = true;
				}
			} else {
				names.add(items.get(i).getName());
			}
		}
		return !cancel;
	}

	@Override
	public void saveData(Bundle container) {
		MyDiabetesStorage storage = MyDiabetesStorage.getInstance(getContext());
		for (int i = 0; i < items.size(); i++) {
			boolean failed = storage.addInsulin(items.get(i).getName(), items.get(i).getAdministrationMethod(), items.get(i).getAction());
			if (!failed) {
				// Do something
				// Or maybe not! This is used in welcome screen, in there it cannot fail since there arent repeated insulin names
				Log.d("AddInsulinFragment", "Failed to add! Already exists?");
			}
		}
	}

	@Override
	public int getSubtitle() {
		return R.string.preferences_insulins;
	}


	class Holder extends RecyclerView.ViewHolder {

		public Holder(View itemView) {
			super(itemView);
		}
	}

	class InsulinHolder extends Holder {
		InsulinElement view;

		public InsulinHolder(View itemView) {
			super(itemView);
			view = (InsulinElement) itemView;
		}
	}

	class ButtonHolder extends Holder {

		public ButtonHolder(View itemView, String text) {
			super(itemView);
			((Button) itemView).setText(text);
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
				return new ButtonHolder(layoutInflator.inflate(R.layout.list_item_new_element_button, parent, false), getContext().getString(R.string.new_insulin));
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
		holder.view.setData(data);
		holder.view.setListener(new InsulinElement.ElementChangesListener() {
			@Override
			public void dataUpdated(InsulinData data) {
//				items.add(data.getPosition(), data);
				if(mListener!=null) {
					mListener.activateNextButton();
				}
			}

			@Override
			public void viewRemoved(int pox) {
				items.remove(pox);
				for (int i = pox; i < items.size(); i++) {
					items.get(i).setPosition(items.get(i).getPosition() - 1);
				}
				list.getAdapter().notifyItemRemoved(pox);
			}
		});
	}
}
