package pt.it.porto.mydiabetes.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.ValueShape;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.charts.data.ChartData;
import pt.it.porto.mydiabetes.ui.charts.data.Weight;
import pt.it.porto.mydiabetes.ui.dialogs.DateRangeDialog;
import pt.it.porto.mydiabetes.ui.fragments.ChartFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;

public abstract class AbstractChartActivity extends BaseActivity implements ChartFragment.OnFragmentInteractionListener, DateRangeDialog.TimeUpdate {

	public static final int MAX_VALUES_IN_GRAPH = 100;
	public static final int[] CHART_LINE_COLORS = {Color.RED, R.color.holo_blue_dark, Color.DKGRAY};

	public static final String EXTRAS_CHART_DATA = "chartData";
	public static final String EXTRAS_TIME_START = "time_start";
	public static final String EXTRAS_TIME_END = "time_end";

	private Calendar timeStart;
	private Calendar timeEnd;
	private ChartData chartData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);
		ActionBar actionbar = getSupportActionBar();
		if (actionbar != null) {
			actionbar.setDisplayHomeAsUpEnabled(true);
		}

		timeStart = Calendar.getInstance();
		timeStart.roll(Calendar.WEEK_OF_YEAR, false);
		timeEnd = Calendar.getInstance();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			chartData = extras.getParcelable(EXTRAS_CHART_DATA);
			if (extras.containsKey(EXTRAS_TIME_START)) {
				timeStart = (Calendar) extras.getSerializable(EXTRAS_TIME_START);
			}
			if (extras.containsKey(EXTRAS_TIME_END)) {
				timeEnd = (Calendar) extras.getSerializable(EXTRAS_TIME_END);
			}
			chartData.setStartDate(DateUtils.getFormattedDate(getTimeStart()));
			chartData.setEndDate(DateUtils.getFormattedDate(getTimeEnd()));
		} else {
			chartData = new Weight(this);
			chartData.setStartDate(DateUtils.getFormattedDate(getTimeStart()));
			chartData.setEndDate(DateUtils.getFormattedDate(getTimeEnd()));
		}

		if (savedInstanceState != null) {
			chartData = savedInstanceState.getParcelable(EXTRAS_CHART_DATA);
			timeStart = (Calendar) savedInstanceState.getSerializable(EXTRAS_TIME_START);
			timeEnd = (Calendar) savedInstanceState.getSerializable(EXTRAS_TIME_END);
		}

		setupContent();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(EXTRAS_CHART_DATA, chartData);
		outState.putSerializable(EXTRAS_TIME_START, timeStart);
		outState.putSerializable(EXTRAS_TIME_END, timeEnd);
	}

	public void setTimes(Calendar start, Calendar end) {
		this.timeStart = start;
		this.timeEnd = end;
		chartData.setStartDate(DateUtils.getFormattedDate(getTimeStart()));
		chartData.setEndDate(DateUtils.getFormattedDate(getTimeEnd()));
		updateTimeRange();
		setupContent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chart_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.select_dates) {
			DateRangeDialog dialog = DateRangeDialog.newInstance(timeStart, timeEnd, chartData);
			dialog.show(getSupportFragmentManager(), null);
			return true;
		}
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupContent() {
		ChartFragment fragment = (ChartFragment) getSupportFragmentManager().findFragmentById(R.id.chart_fragment);
		fragment.setListAdapter(getRecyclerViewAdapter());
		List<Line> chartLines = getChartLines();
		fragment.setChartData(chartLines);
		fragment.setSelectItemToListCalculation(getSelectItemToListCalculation(chartLines));
		fragment.setName(getName());
		fragment.endSetup();
	}

	public Line getLine() {
		Line line = new Line();
		line.setColor(Color.RED);
		line.setShape(ValueShape.CIRCLE);
		line.setFilled(false);
		line.setHasLabels(true);
		line.setHasLines(true);
		line.setCubic(false);
		return line;
	}

	public ChartData getChartData() {
		return chartData;
	}

	public Calendar getTimeStart() {
		return timeStart;
	}

	public Calendar getTimeEnd() {
		return timeEnd;
	}

	public abstract RecyclerView.Adapter getRecyclerViewAdapter();

	public abstract List<Line> getChartLines();

	public abstract String getName();

	public abstract void updateTimeRange();

	public ChartFragment.SelectItemToListCalculation getSelectItemToListCalculation(List<Line> chartLines) {
		return new DefaultSelectItemToListCalculation(chartLines);
	}

	private static class DefaultSelectItemToListCalculation implements ChartFragment.SelectItemToListCalculation {
		private final int numberOfValues;

		public DefaultSelectItemToListCalculation(List<Line> chartLines) {
			this.numberOfValues = chartLines.get(0).getValues().size();
		}

		@Override
		public int getListPosition(int line, int positionInLine) {
			return numberOfValues - positionInLine - 1;
		}
	}
}
