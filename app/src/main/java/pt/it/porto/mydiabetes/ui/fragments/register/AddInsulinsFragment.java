package pt.it.porto.mydiabetes.ui.fragments.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.ui.views.InsulinData;
import pt.it.porto.mydiabetes.ui.views.InsulinElement;
import pt.it.porto.mydiabetes.utils.OnSwipeTouchListener;


public class AddInsulinsFragment extends Fragment implements WelcomeActivity.RegistryFragmentPage {

	private static final String TAG = AddInsulinsFragment.class.getCanonicalName();

	private static final String STATE_ITEMS = "items";

	private RecyclerView list;
	private TextView title;
	private ArrayList<InsulinData> items = new ArrayList<>(3);
	private View layout;

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
		layout = inflater.inflate(R.layout.fragment_register_insulins, container, false);
		list = (RecyclerView) layout.findViewById(R.id.insulin_list);
		title = (TextView) layout.findViewById(R.id.title);
		title.setText("Novo Registo de Insulina");

		InsulinAdapter adapter = new InsulinAdapter();
		list.setAdapter(adapter);
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setItemAnimator(new DefaultItemAnimator());
		if (savedInstanceState != null) {
			items = (ArrayList) savedInstanceState.getSerializable(STATE_ITEMS);
		}

		FloatingActionButton myFab = (FloatingActionButton) layout.findViewById(R.id.floatingActionButton);
		myFab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addInsulin();
			}
		});

/*
		ItemTouchHelper.Callback callback=new SwipeHelper(adapter);
		ItemTouchHelper helper=new ItemTouchHelper(callback);
		helper.attachToRecyclerView(list);*/


		return layout;
	}

	private void addInsulin() {
		items.add(new InsulinData(items.size()));
		list.getAdapter().notifyItemInserted(items.size());
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


	class InsulinAdapter extends RecyclerView.Adapter<Holder> {

		@Override
		public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater layoutInflator = getLayoutInflater(null);
			return new InsulinHolder(layoutInflator.inflate(R.layout.listitem_new_insulin, parent, false));
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
			return items.size();
		}
	}


	private void setBinding(InsulinHolder holder, InsulinData data) {
		holder.view.setData(data);
		holder.view.setListener(new InsulinElement.ElementChangesListener() {
			@Override
			public void dataUpdated(InsulinData data) {
//				items.add(data.getPosition(), data);
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

	@Override
	public int getSubtitle() {
		return R.string.preferences_insulins;
	}

}
