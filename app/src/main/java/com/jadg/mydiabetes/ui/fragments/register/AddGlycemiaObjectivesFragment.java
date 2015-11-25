package com.jadg.mydiabetes.ui.fragments.register;

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

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.database.MyDiabetesStorage;
import com.jadg.mydiabetes.ui.activities.WelcomeActivity;
import com.jadg.mydiabetes.ui.views.GlycemiaObjectivesData;
import com.jadg.mydiabetes.ui.views.GlycemiaObjetivesElement;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFormEnd} interface
 * to handle interaction events.
 * Use the {@link AddGlycemiaObjectivesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGlycemiaObjectivesFragment extends Fragment implements WelcomeActivity.RegistryFragamentPage {

	private static final String TAG = AddGlycemiaObjectivesFragment.class.getCanonicalName();

	private static final String STATE_ITEMS = "items";

	private OnFormEnd mListener;
	private RecyclerView list;
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

		list.setAdapter(new InsulinAdapter());
		list.setLayoutManager(new LinearLayoutManager(getContext()));
		list.setItemAnimator(new DefaultItemAnimator());
		if (savedInstanceState == null) {
			addGlycemiaObjective();
		} else {
			items = (ArrayList) savedInstanceState.getSerializable(STATE_ITEMS);
		}

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
		// validate time intervals
		int[] startTimes = new int[items.size()];
		int[] intervalDuration = new int[items.size()];
		String[] temp;
		for (int i = 0; i < items.size(); i++) {
			temp = items.get(i).getStartTime().split(":");
			int startTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);

			temp = items.get(i).getEndTime().split(":");
			int endTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
			int duration = endTime - startTime;
			boolean shouldCancel = true;
			for (int p = 0; p < i; p++) {
				if (startTimes[p] <= startTime && startTimes[p] + intervalDuration[p] >= startTime) {
					// startTime inside a previews interval
					items.get(i).setInvalid(GlycemiaObjectivesData.ERROR_START_TIME_OVERLAPS);
				} else if (startTimes[p] <= endTime && startTimes[p] + intervalDuration[p] >= endTime) {
					// endTime inside a interval
					items.get(i).setInvalid(GlycemiaObjectivesData.ERROR_END_TIME_OVERLAPS);
				} else if (duration <= 0) {
					// endTime needs to be bigger than startTime
					items.get(i).setInvalid(GlycemiaObjectivesData.ERROR_END_TIME_BEFORE_START_TIME);
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
			startTimes[i] = startTime;
			intervalDuration[i] = endTime - startTimes[i];
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

	class ButtonHolder extends Holder {

		public ButtonHolder(View itemView, String text) {
			super(itemView);
			((Button) itemView).setText(text);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					addGlycemiaObjective();
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
				return new ButtonHolder(layoutInflator.inflate(R.layout.list_item_new_element_button, parent, false), getContext().getString(R.string.new_glycemia_objective));
			}
			return new GlycemiaHolder(layoutInflator.inflate(R.layout.list_item_new_glycemia_objective, parent, false));
		}

		@Override
		public int getItemViewType(int position) {
			return position == items.size() ? TYPE_FOOTER_BUTTON : TYPE_NEW_INSULIN;
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
			return items.size() + 1;
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
}
