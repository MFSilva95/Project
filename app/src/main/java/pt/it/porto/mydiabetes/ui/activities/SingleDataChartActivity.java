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
import pt.it.porto.mydiabetes.database.MyDiabetesContract;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.recyclerviewAdapters.GenericSingleTypeAdapter;

public class SingleDataChartActivity extends AbstractChartActivity {

	@Override
	public RecyclerView.Adapter getRecyclerViewAdapter() {
		return new GenericSingleTypeAdapter(new ListDataSource(MyDiabetesStorage.getInstance(this))
				.getSimpleData(MyDiabetesContract.Regist.Weight.TABLE_NAME, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE,
						MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME));
	}

	@Override
	public List<Line> getChartLines() {
		return convertToGraphPoints(MyDiabetesStorage.getInstance(this)
				.getAllWeights(new MyDiabetesStorage.QueryOptions().setSortOrder(MyDiabetesStorage.QueryOptions.ORDER_DESC)));
	}

	@Override
	public String getName() {
		return getString(R.string.title_activity_weight);
	}


	private List<Line> convertToGraphPoints(Cursor cursor) {
		cursor.moveToFirst();
		int numberOfElementsInGraph = cursor.getCount() > MAX_VALUES_IN_GRAPH ? MAX_VALUES_IN_GRAPH : cursor.getCount();

		List<PointValue> xss = new ArrayList<>(numberOfElementsInGraph);

		String date;
		double value;
		int i = numberOfElementsInGraph - 1;
		while (i >= 0) {
			date = cursor.getString(cursor.getColumnIndex(MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME));
			float dateTimeStamp = 0;
			try {
				dateTimeStamp = iso8601Format.parse(date).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			value = cursor.getDouble(cursor.getColumnIndex(MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE));
			xss.add(new PointValue(dateTimeStamp, (float) value));
			i--;
			cursor.moveToNext();
		}
		Line line = getLine();
		line.setValues(xss);
		List<Line> lines=new ArrayList<>(1);
		lines.add(line);
		return lines;
	}



	@Override
	public void onItemSelected(int position) {

	}
}
