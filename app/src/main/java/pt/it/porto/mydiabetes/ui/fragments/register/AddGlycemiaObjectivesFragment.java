package pt.it.porto.mydiabetes.ui.fragments.register;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.ui.views.GlycemiaObjectivesData;
import pt.it.porto.mydiabetes.ui.views.GlycemiaObjetivesElement;
import pt.it.porto.mydiabetes.utils.ArraysUtils;
import pt.it.porto.mydiabetes.utils.OnSwipeTouchListener;


public class AddGlycemiaObjectivesFragment extends Fragment implements WelcomeActivity.RegistryFragmentPage {

	private static final String TAG = AddGlycemiaObjectivesFragment.class.getCanonicalName();

	private static final String STATE_ITEMS = "items";

	private RecyclerView list;
	private TextView title;
	private ArrayList<GlycemiaObjectivesData> items = new ArrayList<>(3);

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment RegistPersonalDataFragment.
	 */
	public static AddGlycemiaObjectivesFragment newInstance() {
		AddGlycemiaObjectivesFragment fragment = new AddGlycemiaObjectivesFragment();
		return fragment;
	}

	public AddGlycemiaObjectivesFragment() {
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

		title = (TextView) layout.findViewById(R.id.title);
		title.setText(getString(R.string.new_glycemia_objective));

		list.setAdapter(new InsulinAdapter());
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setItemAnimator(new DefaultItemAnimator());
		if (savedInstanceState != null) {
            items = (ArrayList<GlycemiaObjectivesData>) savedInstanceState.getSerializable(STATE_ITEMS);
		}

		FloatingActionButton myFab = (FloatingActionButton) layout.findViewById(R.id.floatingActionButton);
		myFab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addGlycemiaObjective();
			}
		});


		return layout;
	}

	private void addGlycemiaObjective() {
		//items.add(new GlycemiaObjectivesData(items.size(), this.getContext()));
		items.add(new GlycemiaObjectivesData(items.size(), items));
		list.getAdapter().notifyItemInserted(items.size());
	}


	@Override
	public boolean allFieldsAreValid() {
		boolean cancel = false;
		if (items.size() == 0) {
			//items.add(new GlycemiaObjectivesData(0, this.getContext()));
			items.add(new GlycemiaObjectivesData(0, items));
			list.getAdapter().notifyItemInserted(0);
			return false;
		}
		ArrayList<String> names = new ArrayList<>(items.size());
		for (int i = 0; i < items.size(); i++) {

			if (!items.get(i).isValid() || names.contains(items.get(i).getDescription())) {
				items.get(i).setInvalid(GlycemiaObjectivesData.ERROR_REPEATED_DESCRIPTION);
				list.getAdapter().notifyItemChanged(i);
				if (!cancel) {
					list.scrollToPosition(i);
					cancel = true;
				}
			}
			names.add(items.get(i).getDescription());
		}
		if (cancel) {
			return false;
		}
		return !cancel;
	}

	public boolean validateGlicObjTimes(){
		return false;
	}


	@Override
	public void saveData(Bundle container) {
		MyDiabetesStorage storage = MyDiabetesStorage.getInstance(getContext());
		for (int i = 0; i < items.size(); i++) {
			boolean failed = storage.addGlycemiaObjective(items.get(i).getDescription(), items.get(i).getStartTime(), /*items.get(i).getEndTime(),*/ items.get(i).getObjective());
			if (!failed) {
				// Do something
				// Or maybe not! This is used in welcome screen, in there it cannot fail since there arent repeated insulin names
				Log.d(TAG, "Failed to add glycemia objective! Already exists?");
			}
		}
	}

	class Holder extends RecyclerView.ViewHolder {

		public Holder(View itemView) {
			super(itemView);
		}
	}

	class GlycemiaHolder extends Holder {
		GlycemiaObjetivesElement view;

		public GlycemiaHolder(View itemView) {
			super(itemView);
			view = (GlycemiaObjetivesElement) itemView;
		}
	}


	class InsulinAdapter extends RecyclerView.Adapter<Holder> {
		private static final int TYPE_NEW_INSULIN = 0;
		private static final int TYPE_FOOTER_BUTTON = 1;

		@Override
		public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater layoutInflater = getLayoutInflater();
			return new GlycemiaHolder(layoutInflater.inflate(R.layout.list_item_new_glycemia_objective, parent, false));
		}

		@Override
		public void onBindViewHolder(Holder holder, int position) {
			if (holder instanceof GlycemiaHolder) {
				GlycemiaObjectivesData data = items.get(position);
				setBinding((GlycemiaHolder) holder, data);
			}
		}

		@Override
		public int getItemCount() {
			return items.size();
		}
	}


	private void setBinding(GlycemiaHolder holder, GlycemiaObjectivesData data) {
		holder.view.setData(data);
		holder.view.setListener(new GlycemiaObjetivesElement.ElementChangesListener() {
			@Override
			public void dataUpdated(GlycemiaObjectivesData data) {
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
		return R.string.title_activity_target_bg_detail;
	}
}
