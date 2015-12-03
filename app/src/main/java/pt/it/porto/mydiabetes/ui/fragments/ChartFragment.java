package pt.it.porto.mydiabetes.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import pt.it.porto.mydiabetes.ui.charts.BodyExpandFrameLayout;
import pt.it.porto.mydiabetes.ui.charts.BodyOverlapHeaderGesture;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.formatter.AxisValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.charts.BodyExpandFrameLayout;
import pt.it.porto.mydiabetes.ui.charts.BodyOverlapHeaderGesture;

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
	private static final String TAG = "ChartFragment";

	private String name;

	private OnFragmentInteractionListener mListener;

	private View listItemSelected;

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
	LineChartView chart;
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
				removeSelection();
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

	public void setChartData(List<Line> lines) {
//		numberOfElementsInGraph = lines.get(0)..size();
		chart = (LineChartView) getView().findViewById(R.id.chart);

		chart.setLineChartData(new LineChartData(lines));

		chart.setZoomType(ZoomType.HORIZONTAL);
		Viewport tempViewPort = new Viewport(chart.getMaximumViewport());
		float dx = tempViewPort.width() / 3;
		tempViewPort.inset(dx, 0);
		tempViewPort.offset(dx, 0);
		chart.setCurrentViewport(tempViewPort);

		chart.getLineChartData().setAxisXBottom(new Axis().setMaxLabelChars(10).setHasLines(true).setInside(false).setHasSeparationLine(false)
				.setTextColor(Color.RED).setFormatter(new AxisValueFormatter() {
					public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");

					@Override
					public int formatValueForManualAxis(char[] chars, AxisValue axisValue) {
						return 0;
					}

					@Override
					public int formatValueForAutoGeneratedAxis(char[] chars, float v, int i) {
						char[] label = dateFormat.format(new Date((long) v)).toCharArray();
						System.arraycopy(label, 0, chars, chars.length - label.length, label.length);
//				Log.d(TAG, String.valueOf(chars));
						return label.length;
					}
				}));


		chart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
			@Override
			public void onValueSelected(int i, int i1, PointValue pointValue) {
				if (listItemSelected != null && bodyOverlapHeaderGesture.isExpanded()) {
					removeSelection();
					bodyOverlapHeaderGesture.collapse();
					return;
				}

//				Log.d("ChartSelect", String.valueOf(numberOfElementsInGraph - h.getXIndex() - 1));
//				Log.d("ChartSelect", "lineIndex: " + String.valueOf(i) + " pointIndex: " + String.valueOf(i1) + " numberOfElements: "+String.valueOf(numberOfElementsInGraph));
				listView.smoothScrollToPosition(i1);
				RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(i1);
				if (holder != null && holder.itemView != null) {
					removeSelection();
					selectItem(holder.itemView);
				}
				bodyOverlapHeaderGesture.expand();
			}

			@Override
			public void onValueDeselected() {
				if (listItemSelected != null) {
					removeSelection();
					bodyOverlapHeaderGesture.collapse();
				}
			}
		});
	}


	private void removeSelection() {
		if (listItemSelected != null) {
			listItemSelected.setHovered(false);
			listItemSelected.setPressed(false);
			listItemSelected = null;
		}
//		chart.highlightValue(null);
	}

	private void selectItem(View newView) {
		listItemSelected = newView;
		listItemSelected.setHovered(true);
		listItemSelected.setPressed(true);
	}

	public void endSetup() {
		setupScrool();
	}

	public void setName(String name) {
		this.name = name;
		((AppCompatActivity) mListener).getSupportActionBar().setTitle(name);
	}
}
