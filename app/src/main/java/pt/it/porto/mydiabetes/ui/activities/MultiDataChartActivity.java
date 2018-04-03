package pt.it.porto.mydiabetes.ui.activities;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListDataSource;
import pt.it.porto.mydiabetes.ui.fragments.ChartFragment;
import pt.it.porto.mydiabetes.ui.recyclerviewAdapters.GenericMultiTypeAdapter;
import pt.it.porto.mydiabetes.utils.DateUtils;

public class MultiDataChartActivity extends AbstractChartActivity {
	private static final String TAG = MultiDataChartActivity.class.getCanonicalName();
	protected ChartFragment.SelectItemToListCalculation selectItemToListCalculator;

	@Override
	public String getRegType(){return null;}

	private Cursor cursor;
	protected ArrayList<String> tables;

	protected Cursor getCursor() {
		if (cursor == null) {
			initCursor();
		}
		return cursor;
	}

	private void initCursor() {
		cursor=getChartData().getCursor(this);
		tables=getChartData().getTables();
//		String[] tables = new String[]{MyDiabetesContract.Regist.Insulin.TABLE_NAME + " JOIN " + MyDiabetesContract.Insulin.TABLE_NAME,
//				MyDiabetesContract.Regist.CarboHydrate.TABLE_NAME};
//		cursor = new ListDataSource(MyDiabetesStorage.getInstance(this))
//				.getMultiData(tables,
//						new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_VALUE, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_VALUE},
//						new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME},
//						new String[]{MyDiabetesContract.Insulin.COLUMN_NAME_NAME, null},
//						dateFormat.format(getTimeStart().getTime()), dateFormat.format(getTimeEnd().getTime()), MAX_VALUES_IN_GRAPH);
//		this.tables = new ArrayList<>(Arrays.asList(tables));
	}


	@Override
	public RecyclerView.Adapter getRecyclerViewAdapter() {
		return new GenericMultiTypeAdapter(getCursor(),
				tables, getChartData().getIcons());
	}

	@Override
	public List<Line> getChartLines() {
		Cursor cursor = getCursor();
		cursor.moveToLast();

		int numberOfElementsInGraph = cursor.getCount() > MAX_VALUES_IN_GRAPH ? MAX_VALUES_IN_GRAPH : cursor.getCount();

		@SuppressWarnings("unchecked")
		List<PointValue> xss[] = new List[tables.size()];
		for (int i = 0; i < tables.size(); i++) {
			xss[i] = new ArrayList<>(numberOfElementsInGraph * 3 / 4);
		}

		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] positionInList = new ArrayList[xss.length];
		for (int i = 0; i < positionInList.length; i++) {
			positionInList[i] = new ArrayList<>(numberOfElementsInGraph);
		}

		String date;
		double value;
		int i = numberOfElementsInGraph - 1;
		int pox;
		while (i >= 0) {
			date = cursor.getString(cursor.getColumnIndex(ListDataSource.ROW_DATETIME));
			float dateTimeStamp = 0;
			try {
				dateTimeStamp = DateUtils.parseDateTime(date).getTime().getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			value = cursor.getDouble(cursor.getColumnIndex(ListDataSource.ROW_VALUE));
			pox = tables.indexOf(cursor.getString(cursor.getColumnIndex(ListDataSource.ROW_TABLE_NAME)));
			xss[pox].add(new PointValue(dateTimeStamp, (float) value));
			positionInList[pox].add(i);
			i--;
			cursor.moveToPrevious();
		}
		List<Line> lines = new ArrayList<>(xss.length);
		for (i = 0; i < xss.length; i++) {
			Line line = getLine();
			line.setValues(xss[i]);
			line.setColor(CHART_LINE_COLORS[i]);
			lines.add(line);
		}

		selectItemToListCalculator = new MyChartToListCalculator(positionInList);
		return lines;
	}

	@Override
	public String getName() {
		return getString(R.string.title_activity_insulin);
	}

	@Override
	public void updateTimeRange() {
		initCursor();
	}

	@Override
	public void onItemSelected(int position) {

	}

	@Override
	public ChartFragment.SelectItemToListCalculation getSelectItemToListCalculation(List<Line> chartLines) {
		return selectItemToListCalculator;
	}

	class MyChartToListCalculator implements ChartFragment.SelectItemToListCalculation {

		private ArrayList<Integer>[] listToPositions;

		public MyChartToListCalculator(ArrayList<Integer>[] listToPositions) {
			this.listToPositions = listToPositions;
		}

		@Override
		public int getListPosition(int line, int positionInLine) {
			return listToPositions[line].get(positionInLine);
		}
	}
}
