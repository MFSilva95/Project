package pt.it.porto.mydiabetes.ui.fragments.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.ui.views.GlycemiaObjectivesData;
import pt.it.porto.mydiabetes.ui.views.GlycemiaObjetivesElement;
import pt.it.porto.mydiabetes.utils.ArraysUtils;
import pt.it.porto.mydiabetes.utils.OnSwipeTouchListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFormEnd} interface
 * to handle interaction events.
 * Use the {@link AddGlycemiaObjectivesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGlycemiaObjectivesFragment extends Fragment implements WelcomeActivity.RegistryFragmentPage {

	private static final String TAG = AddGlycemiaObjectivesFragment.class.getCanonicalName();

	private static final String STATE_ITEMS = "items";

	private OnFormEnd mListener;
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
		title.setText("Novo Objetivo de Glicemia");

		list.setAdapter(new InsulinAdapter());
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setItemAnimator(new DefaultItemAnimator());
		if (savedInstanceState != null) {
			items = (ArrayList) savedInstanceState.getSerializable(STATE_ITEMS);
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
		items.add(new GlycemiaObjectivesData(items.size()));
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
				mListener.deactivateNextButton();
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
			items.add(new GlycemiaObjectivesData(0));
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

		// validate time intervals
		ArrayList<Integer> startTimes = new ArrayList<>(items.size());
		ArrayList<Integer> intervalDuration = new ArrayList<>(items.size());
		String[] temp;
		for (int i = 0; i < items.size(); i++) {
			temp = items.get(i).getStartTime().split(":");
			int startTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);

			temp = items.get(i).getEndTime().split(":");
			int endTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
			int duration = endTime - startTime;
			boolean shouldCancel = true;
			for (int p = 0; p < startTimes.size(); p++) {
				if (startTimes.get(p) <= startTime && startTimes.get(p) + intervalDuration.get(p) >= startTime) {
					// startTime inside a previews interval
					items.get(i).setInvalid(GlycemiaObjectivesData.ERROR_START_TIME_OVERLAPS);
				} else if (startTimes.get(p) <= endTime && startTimes.get(p) + intervalDuration.get(p) >= endTime) {
					// endTime inside a interval
					items.get(i).setInvalid(GlycemiaObjectivesData.ERROR_END_TIME_OVERLAPS);
				} else if (duration <= 0 && ArraysUtils.min(startTimes) < endTime) {
					// endTime in the next day
					// compares if endTime will be after a startTime of other interval
					// if true than it should fail
					items.get(i).setInvalid(GlycemiaObjectivesData.ERROR_END_TIME_OVERLAPS);
				} else {
					shouldCancel = false;
				}
				if (shouldCancel) {
					cancel = true;
					list.getAdapter().notifyItemChanged(i);
					list.scrollToPosition(i);
					break;
				}
			}
			if (duration < 0) {
				// endTime in the next day
				// adds interval from 0 to endTime
				startTimes.add(0);
				intervalDuration.add(endTime);
				duration += 24 * 60;
				// to add interval from startTime to 24+
			}
			startTimes.add(startTime);
			intervalDuration.add(duration);
		}

		return !cancel;
	}

	@Override
	public void saveData(Bundle container) {
		MyDiabetesStorage storage = MyDiabetesStorage.getInstance(getContext());
		for (int i = 0; i < items.size(); i++) {
			boolean failed = storage.addGlycemiaObjective(items.get(i).getDescription(), items.get(i).getStartTime(), items.get(i).getEndTime(), items.get(i).getObjective());
			if (!failed) {
				// Do something
				// Or maybe not! This is used in welcome screen, in there it cannot fail since there arent repeated insulin names
				Log.d(TAG, "Failed to add glycemia objective! Already exists?");
			}
		}
	}

	@Override
	public int getSubtitle() {
		return R.string.title_activity_target_bg_detail;
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
			LayoutInflater layoutInflator = getLayoutInflater(null);
			return new GlycemiaHolder(layoutInflator.inflate(R.layout.list_item_new_glycemia_objective, parent, false));
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
				if (mListener != null) {
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
