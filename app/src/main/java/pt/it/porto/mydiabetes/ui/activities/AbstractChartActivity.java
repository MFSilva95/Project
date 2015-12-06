package pt.it.porto.mydiabetes.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.ValueShape;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.fragments.ChartFragment;

public abstract class AbstractChartActivity extends BaseActivity implements ChartFragment.OnFragmentInteractionListener {

	public static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
	public static final int MAX_VALUES_IN_GRAPH = 100;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);
		getSupportActionBar();

		setupContent();
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

	public abstract RecyclerView.Adapter getRecyclerViewAdapter();

	public abstract List<Line> getChartLines();

	public abstract String getName();

	public ChartFragment.SelectItemToListCalculation getSelectItemToListCalculation(List<Line> chartLines) {
		return new DefaultSelectItemToListCalculation(chartLines);
	}

	private static class DefaultSelectItemToListCalculation implements ChartFragment.SelectItemToListCalculation{
		private final int numberOfValues;

		public DefaultSelectItemToListCalculation(List<Line> chartLines) {
			this.numberOfValues=chartLines.get(0).getValues().size();
		}

		@Override
		public int getListPosition(int line, int positionInLine) {
			return numberOfValues-positionInLine-1;
		}
	}
}
