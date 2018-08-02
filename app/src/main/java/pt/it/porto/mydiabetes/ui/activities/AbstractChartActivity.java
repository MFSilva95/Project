package pt.it.porto.mydiabetes.ui.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.ValueShape;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.charts.data.ChartData;
import pt.it.porto.mydiabetes.ui.charts.data.Weight;
import pt.it.porto.mydiabetes.ui.fragments.ChartFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;

public abstract class AbstractChartActivity extends BaseActivity implements ChartFragment.OnFragmentInteractionListener {
	private ActionBar actionBar;

	public static final int MAX_VALUES_IN_GRAPH = 100;
	public static final int[] CHART_LINE_COLORS = {Color.RED, R.color.holo_blue_dark, Color.DKGRAY};

	public static final String EXTRAS_CHART_DATA = "chartData";
	public static final String EXTRAS_TIME_START = "time_start";
	public static final String EXTRAS_TIME_END = "time_end";

	private Calendar timeStart;
	private Calendar timeEnd;
	private ChartData chartData;

	private EditText dateFrom;
	private EditText dateTo;

	public FloatingActionButton fab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		timeStart = Calendar.getInstance();
//		timeStart.roll(Calendar.WEEK_OF_YEAR, false);
		timeStart.roll(Calendar.YEAR, false);
		timeEnd = Calendar.getInstance();

		dateFrom = (EditText) findViewById(R.id.et_DataFrom);

		dateFrom.setText(DateUtils.getFormattedDate(timeStart));
		dateFrom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setStart();
			}
		});
		dateTo = (EditText) findViewById(R.id.et_DataTo);
		dateTo.setText(DateUtils.getFormattedDate(timeEnd));
		dateTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setEnd();
			}
		});


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
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void setupContent() {
		ChartFragment fragment = (ChartFragment) getSupportFragmentManager().findFragmentById(R.id.chart_fragment);
		actionBar.setTitle(getName());
		fragment.setListAdapter(getRecyclerViewAdapter());
		List<Line> chartLines = getChartLines();
		fragment.setChartData(chartLines);
		fragment.setSelectItemToListCalculation(getSelectItemToListCalculation(chartLines));
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

	@Override
	public void onResume() {
		super.onResume();
		setupContent();
		updateTimeRange();
	}

	private void setEnd() {
		DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if (timeEnd.get(Calendar.YEAR) != year || timeEnd.get(Calendar.MONTH) != monthOfYear || timeEnd.get(Calendar.DAY_OF_MONTH) != dayOfMonth) {
					timeEnd.set(year, monthOfYear, dayOfMonth);
					dateTo.setText(DateUtils.getFormattedDate(timeEnd));
					setTimes(timeStart, timeEnd);
				}
			}
		}, timeEnd.get(Calendar.YEAR), timeEnd.get(Calendar.MONTH), timeEnd.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	public void setStart() {
		DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				if (timeStart.get(Calendar.YEAR) != year || timeStart.get(Calendar.MONTH) != monthOfYear || timeStart.get(Calendar.DAY_OF_MONTH) != dayOfMonth) {
					timeStart.set(year, monthOfYear, dayOfMonth);
					dateFrom.setText(DateUtils.getFormattedDate(timeStart));
					setTimes(timeStart, timeEnd);
				}
			}
		}, timeStart.get(Calendar.YEAR), timeStart.get(Calendar.MONTH), timeStart.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}
}
