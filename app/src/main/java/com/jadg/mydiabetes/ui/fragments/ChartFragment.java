package com.jadg.mydiabetes.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.ui.charts.BodyExpandFrameLayout;
import com.jadg.mydiabetes.ui.charts.BodyOverlapHeaderGesture;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_NAME = "param1";

	private String name;

	private OnFragmentInteractionListener mListener;


	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param name the name of the chart
	 * @return A new instance of fragment ChartFragment.
	 */
	public static ChartFragment newInstance(String name) {
		ChartFragment fragment = new ChartFragment();
		Bundle args = new Bundle();
		args.putString(ARG_NAME, name);
		fragment.setArguments(args);
		return fragment;
	}

	public ChartFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			name = getArguments().getString(ARG_NAME);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_chart, container, false);
	}

	public void onItemSelected(int position) {
		if (mListener != null) {
			mListener.onItemSelected(position);
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener) {
			mListener = (OnFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}


	RecyclerView listView;
	LineChart chart;
	private BodyOverlapHeaderGesture bodyOverlapHeaderGesture;
	private int numberOfElementsInGraph;


	private void setupScrool() {
		bodyOverlapHeaderGesture = new BodyOverlapHeaderGesture(chart, listView) {
			@Override
			public void onExpand() {
			}

			@Override
			public void onCollapse() {
			}
		};

		((BodyExpandFrameLayout) getView()).setBodyOverlapHeaderGesture(bodyOverlapHeaderGesture);

		chart.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if (bodyOverlapHeaderGesture.isExpanded()) {
					bodyOverlapHeaderGesture.collapse();
					return true;
				} else {
					return false;
				}

			}
		});
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		void onItemSelected(int position);
	}

	public void setListAdapter(RecyclerView.Adapter adapter) {
		listView = (RecyclerView) getView().findViewById(R.id.list_vals);
		listView.setHasFixedSize(true);
		listView.setAdapter(adapter);
		listView.setLayoutManager(new LinearLayoutManager(getContext()));
		listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).build());
	}

	public void setChartData(LineData data) {
		numberOfElementsInGraph = data.getXValCount();
		chart = (LineChart) getView().findViewById(R.id.chart);

		chart.setData(data);

//		chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
		chart.getXAxis().setEnabled(false);
		chart.getAxisLeft().setEnabled(false);
		chart.getAxisRight().setEnabled(false);

		chart.getAxisRight().setDrawGridLines(false);
		chart.getAxisLeft().setDrawGridLines(false);
		chart.getAxisRight().setStartAtZero(false);
		chart.getAxisLeft().setStartAtZero(false);
		chart.setDrawGridBackground(false);

		chart.getXAxis().setAvoidFirstLastClipping(true);
		chart.getXAxis().setDrawGridLines(false);

		chart.getLegend().setEnabled(false);

		chart.setVisibleXRangeMaximum(5); // allow 5 values to be displayed at once on the x-axis, not more
		chart.setVisibleXRangeMinimum(2); // allow 5 values to be displayed at once on the x-axis, not less
		chart.setDragOffsetX(10);


//		chart.setBackgroundColor(Color.WHITE);
		chart.setBackgroundColor(Color.TRANSPARENT);
//		chart.setViewPortOffsets(0, 20, 0, 0);

		chart.setTouchEnabled(true);
//
		chart.setDragEnabled(true);
//		chart.setScaleEnabled(false);
		chart.setScaleYEnabled(false);
		chart.setScaleXEnabled(true);
//		chart.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));


		chart.getXAxis().setValueFormatter(new XAxisValueFormatter() {
			DateFormat format = new SimpleDateFormat("hh:mm:ss");

			@Override
			public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
				return format.format(new Date(Long.valueOf(original)));
			}
		});


		chart.setDescription(null);
		chart.setExtraTopOffset(10f);

		chart.setDrawBorders(false);

		chart.animateY(2000);

		chart.invalidate();
		chart.moveViewToX(chart.getLineData().getXValCount() - 5);

		chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

			View previewsSelected;

			@Override
			public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
				if(previewsSelected!=null && bodyOverlapHeaderGesture.isExpanded()){
					previewsSelected.setHovered(false);
					previewsSelected.setPressed(false);
					bodyOverlapHeaderGesture.collapse();
					previewsSelected=null;
					chart.highlightValue(null);
					return;
				}
				Log.d("ChartSelect", String.valueOf(numberOfElementsInGraph - h.getXIndex() - 1));
				listView.smoothScrollToPosition(numberOfElementsInGraph - h.getXIndex() - 1);
				RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(numberOfElementsInGraph - h.getXIndex() - 1);
				if (holder != null && holder.itemView != null) {
					if (previewsSelected != null) {
						previewsSelected.setHovered(false);
						previewsSelected.setPressed(false);
					}
					holder.itemView.setHovered(true);
					holder.itemView.setPressed(true);
					previewsSelected = holder.itemView;
				}
				bodyOverlapHeaderGesture.expand();
			}

			@Override
			public void onNothingSelected() {
				if (previewsSelected != null) {
					previewsSelected.setHovered(false);
					previewsSelected.setPressed(false);
					bodyOverlapHeaderGesture.collapse();
					previewsSelected=null;
				}
			}
		});
	}


	public void endSetup() {
		setupScrool();
	}

	public void setName(String name) {
		this.name = name;
		((AppCompatActivity) mListener).getSupportActionBar().setTitle(name);
	}
}
