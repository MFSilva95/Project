package com.jadg.mydiabetes.ui.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.database.MyDiabetesContract;
import com.jadg.mydiabetes.database.MyDiabetesStorage;
import com.jadg.mydiabetes.ui.charts.BodyExpandFrameLayout;
import com.jadg.mydiabetes.ui.charts.BodyOverlapHeaderGesture;

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class GraphsActivity extends BaseActivity {


	private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
	private static final int MAX_VALUES_IN_GRAPH = 100;

	TwoWayView listView;
	LineChart chart;
	private BodyOverlapHeaderGesture bodyOverlapHeaderGesture;
	private int numberOfElementsInGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);
		getSupportActionBar();

		setupContent();

		setupScrool();
	}

	private void setupScrool() {
		bodyOverlapHeaderGesture = new BodyOverlapHeaderGesture(chart, listView) {
			@Override
			public void onExpand() {
			}

			@Override
			public void onCollapse() {
			}
		};

		((BodyExpandFrameLayout) findViewById(R.id.frame)).setBodyOverlapHeaderGesture(bodyOverlapHeaderGesture);

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


	private void setupContent() {
		listView = (TwoWayView) findViewById(R.id.list_vals);
		listView.setHasFixedSize(true);
		listView.setAdapter(new WeightAdapter(MyDiabetesStorage.getInstance(this).getAllWeights(null)));
		final Drawable divider = ContextCompat.getDrawable(this, R.drawable.divider);
		listView.addItemDecoration(new DividerItemDecoration(divider));

		chart = (LineChart) findViewById(R.id.chart);
		chart.setData(convertToGraphPoints(
				MyDiabetesStorage.getInstance(this)
						.getAllWeights(new MyDiabetesStorage.QueryOptions()
								.setSortOrder(MyDiabetesStorage.QueryOptions.ORDER_DESC))));

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
//				Log.d("Formatter", String.valueOf(original));
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
//				listView.scrollToPosition(dataSetIndex);
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
//					listView.invalidate();
				}
//				listView.setHovered(true);
				bodyOverlapHeaderGesture.expand();
			}

			@Override
			public void onNothingSelected() {

			}
		});
	}


	private LineData convertToGraphPoints(Cursor cursor) {
		cursor.moveToFirst();
		numberOfElementsInGraph = cursor.getCount() > MAX_VALUES_IN_GRAPH ? MAX_VALUES_IN_GRAPH : cursor.getCount();

		String[] xs = new String[numberOfElementsInGraph];
		Entry[] set = new Entry[numberOfElementsInGraph];


		String date;
		double value;
		int i = numberOfElementsInGraph - 1;
		while (i >= 0) {
			date = cursor.getString(cursor.getColumnIndex(MyDiabetesContract.Reg_Weight.COLUMN_NAME_DATETIME));
			value = cursor.getDouble(cursor.getColumnIndex(MyDiabetesContract.Reg_Weight.COLUMN_NAME_VALUE));
			xs[i] = date;
			set[i] = new Entry((float) value, i);
			i--;
			cursor.moveToNext();
		}

		LineData result = new LineData(xs);
		LineDataSet lineSet = new LineDataSet(Arrays.asList(set), "");
		setupStyleLineSet(lineSet);

		result.addDataSet(lineSet);
		result.setHighlightEnabled(true);
		return result;
	}

	public void setupStyleLineSet(LineDataSet lineSet) {
		lineSet.setLineWidth(0f);
		lineSet.setDrawCircles(true);
		lineSet.setCircleSize(5f);
		lineSet.setFillFormatter(new DefaultFillFormatter());
		lineSet.setDrawFilled(true);

		lineSet.setDrawCubic(true);
		lineSet.setCubicIntensity(0.2f);

		lineSet.setValueTextSize(Utils.convertDpToPixel(6));

		lineSet.setFillColor(Color.RED);
		lineSet.setCircleColor(Color.RED);
	}


	class WeightAdapter extends TwoWayView.Adapter<WeightAdapter.WeightViewHolder> {

		private Cursor cursor;

		public WeightAdapter(Cursor cursor) {
			this.cursor = cursor;
//			setHasStableIds(true);
		}

		@Override
		public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_graph_weights, parent, false);
			return new WeightViewHolder(view);
		}

		@Override
		public void onBindViewHolder(WeightViewHolder holder, int position) {
			cursor.moveToPosition(position);
			Date date = null;
			try {
				date = iso8601Format.parse(cursor.getString(cursor.getColumnIndex(MyDiabetesContract.Reg_Weight.COLUMN_NAME_DATETIME)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			holder.date.setText(DateFormat.getDateInstance().format(date));
//			holder.date.setText(dateFormat.format(date));
			holder.value.setText(String.format("%.1f", (Double) cursor.getDouble(cursor.getColumnIndex(MyDiabetesContract.Reg_Weight.COLUMN_NAME_VALUE))));
		}

		@Override
		public void onAttachedToRecyclerView(RecyclerView recyclerView) {
			super.onAttachedToRecyclerView(recyclerView);
		}

		@Override
		public int getItemCount() {
			return cursor.getCount();
		}

		class WeightViewHolder extends RecyclerView.ViewHolder {
			TextView date;
			TextView value;

			public WeightViewHolder(View itemView) {
				super(itemView);
				date = (TextView) itemView.findViewById(R.id.txt_date);
				value = (TextView) itemView.findViewById(R.id.txt_weight);
			}
		}
	}
}
