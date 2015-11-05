package com.jadg.mydiabetes.ui.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Property;
import android.view.GestureDetector;
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

import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphsActivity extends Activity {


	private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

	TwoWayView listView;
	LineChart chart;
	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);

		setupContent();

		setupScrool();
	}

	private void setupScrool() {
		ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) listView.getLayoutParams();

		gestureDetector = new GestureDetector(this, new MyGestureDetector(chart, listView));
		listView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return gestureDetector.onTouchEvent(motionEvent);
			}
		});


	}


	private void setupContent() {
		listView = (TwoWayView) findViewById(R.id.list_vals);
		listView.setHasFixedSize(true);
		listView.setAdapter(new WeightAdapter(MyDiabetesStorage.getInstance(this).getAllWeights()));
		final Drawable divider = ContextCompat.getDrawable(this, R.drawable.divider);
		listView.addItemDecoration(new DividerItemDecoration(divider));

		chart = (LineChart) findViewById(R.id.chart);
		chart.setData(getData());

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

		chart.setVisibleXRangeMaximum(20); // allow 20 values to be displayed at once on the x-axis, not more
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

		chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//				listView.scrollToPosition(dataSetIndex);
			}

			@Override
			public void onNothingSelected() {

			}
		});
	}


	private LineData getData() {
		List<String> xs = new ArrayList<>(4);
		xs.add(String.valueOf(System.currentTimeMillis()));
		xs.add(String.valueOf(System.currentTimeMillis() + 1));
		xs.add(String.valueOf(System.currentTimeMillis() + 12 * 1000));
		xs.add(String.valueOf(System.currentTimeMillis() + 13 * 1000));
		LineData result = new LineData(xs);
		List<Entry> set = new ArrayList<>();

		set.add(new Entry(60, 0));
		set.add(new Entry(65, 1));
		set.add(new Entry(60, 2));
		set.add(new Entry(55, 3));
		LineDataSet lineSet = new LineDataSet(set, "");
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

		result.addDataSet(lineSet);
		result.setHighlightEnabled(true);


		return result;
	}

	private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 10;
		//		private static final int SWIPE_MIN_DISTANCE = 100;
		private static final int SWIPE_ERROR_MARGIN = 50;

		private View viewHeader;
		private View viewBody;
		private boolean isExpanded = false;
		private int collapseHeight;
		private int diffHeight;
		private int originalTopMargin;


		public MyGestureDetector(View viewHeader, View viewBody) {
			this.viewHeader = viewHeader;
			this.viewBody = viewBody;

			collapseHeight = viewBody.getHeight();
			diffHeight = viewHeader.getLayoutParams().height / 2;
			originalTopMargin = ((ViewGroup.MarginLayoutParams) viewBody.getLayoutParams()).topMargin;
		}

		private MotionEvent mLastOnDownEvent = null;

		@Override
		public boolean onDown(MotionEvent e) {
			//Android 4.0 bug means e1 in onFling may be NULL due to onLongPress eating it.
			mLastOnDownEvent = e;
			return super.onDown(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if (e1 == null)
				e1 = mLastOnDownEvent;
			if (e1 == null || e2 == null)
				return false;
			if ((e1.getY() - e2.getY()) > SWIPE_MIN_DISTANCE) {
//				listView.animate().translationY(-translateY).start();
				expand();
			} else if ((e1.getY() - e2.getY()) < -SWIPE_MIN_DISTANCE) {
//				listView.animate().translationY(0).start();
				collapse();
			}


			return super.

					onScroll(e1, e2, distanceX, distanceY);
		}

		public void expand() {
			if (!isExpanded) {
				isExpanded = true;
				if (collapseHeight == 0) {
					collapseHeight = viewBody.getHeight();
				}
				ObjectAnimator heightAnimator = ObjectAnimator.ofInt(viewBody, VIEW_LAYOUT_HEIGHT, collapseHeight, collapseHeight + diffHeight);
				ObjectAnimator marginAnimator = ObjectAnimator.ofInt(viewBody, VIEW_LAYOUT_MARGIN_TOP, originalTopMargin, diffHeight);
				AnimatorSet set = new AnimatorSet();
				set.play(heightAnimator).with(marginAnimator);
				set.start();
			}
		}

		public void collapse() {
			if (isExpanded) {
				isExpanded = false;
				ObjectAnimator heightAnimator = ObjectAnimator.ofInt(viewBody, VIEW_LAYOUT_HEIGHT, collapseHeight + diffHeight, collapseHeight);
				ObjectAnimator marginAnimator = ObjectAnimator.ofInt(viewBody, VIEW_LAYOUT_MARGIN_TOP, diffHeight, originalTopMargin);
				AnimatorSet set = new AnimatorSet();
				set.play(heightAnimator).with(marginAnimator);
				set.start();
			}
		}

		public boolean isExpanded() {
			return isExpanded;
		}

		Property<View, Integer> VIEW_LAYOUT_MARGIN_TOP = new Property<View, Integer>(Integer.class, "viewMarginTop") {

			public void set(View object, Integer value) {
				((ViewGroup.MarginLayoutParams) object.getLayoutParams()).topMargin = value.intValue();
				object.requestLayout();
			}

			public Integer get(View object) {
				return ((ViewGroup.MarginLayoutParams) object.getLayoutParams()).topMargin;
			}
		};

		Property<View, Integer> VIEW_LAYOUT_HEIGHT = new Property<View, Integer>(Integer.class, "viewLayoutHeight") {

			public void set(View object, Integer value) {
				object.getLayoutParams().height = value.intValue();
				object.requestLayout();
			}

			public Integer get(View object) {
				return object.getLayoutParams().height;
			}
		};

	}


	class WeightAdapter extends TwoWayView.Adapter<WeightAdapter.WeightViewHolder> {

		private Cursor cursor;

		public WeightAdapter(Cursor cursor) {
			this.cursor = cursor;
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
