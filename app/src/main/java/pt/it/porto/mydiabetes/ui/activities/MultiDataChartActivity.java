package pt.it.porto.mydiabetes.ui.activities;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListDataSource;
import pt.it.porto.mydiabetes.database.MyDiabetesContract;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.recyclerviewAdapters.GenericMultiTypeAdapter;

public class MultiDataChartActivity extends AbstractChartActivity {
	private static final String TAG = MultiDataChartActivity.class.getCanonicalName();

	@Override
	public RecyclerView.Adapter getRecyclerViewAdapter() {
		String[] tables = new String[]{MyDiabetesContract.Regist.Insulin.TABLE_NAME, MyDiabetesContract.Regist.CarboHydrate.TABLE_NAME};
		return new GenericMultiTypeAdapter(new ListDataSource(MyDiabetesStorage.getInstance(this))
				.getMultiData(tables,
						new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_VALUE, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_VALUE},
						new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME}),
				tables, new int[]{R.drawable.insulin, R.drawable.carbs});
	}

	@Override
	public List<Line> getChartLines() {
		String[] tables = new String[]{MyDiabetesContract.Regist.Insulin.TABLE_NAME, MyDiabetesContract.Regist.CarboHydrate.TABLE_NAME};
		Cursor cursor = new ListDataSource(MyDiabetesStorage.getInstance(this))
				.getMultiDataToChart(tables,
						new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_VALUE, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_VALUE},
						new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME});
		cursor.moveToFirst();

		int numberOfElementsInGraph = cursor.getCount() > MAX_VALUES_IN_GRAPH ? MAX_VALUES_IN_GRAPH : cursor.getCount();

		List<PointValue> xss1 = new ArrayList<>(numberOfElementsInGraph * 3 / 4);
		List<PointValue> xss2 = new ArrayList<>(numberOfElementsInGraph * 3 / 4);

		String date;
		double value;
		String table;
		int i = numberOfElementsInGraph - 1;
		while (i >= 0) {
			date = cursor.getString(cursor.getColumnIndex(ListDataSource.ROW_DATETIME));
			float dateTimeStamp = 0;
			try {
				dateTimeStamp = iso8601Format.parse(date).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			value = cursor.getDouble(cursor.getColumnIndex(ListDataSource.ROW_VALUE));
			table = cursor.getString(cursor.getColumnIndex(ListDataSource.ROW_TABLE_NAME));
			if (table.equals(MyDiabetesContract.Regist.Insulin.TABLE_NAME)) {
				xss1.add(new PointValue(dateTimeStamp, (float) value));
			} else {
				xss2.add(new PointValue(dateTimeStamp, (float) value));
			}
			i--;
			cursor.moveToNext();
		}
		Log.d(TAG, "getChartLines: xss1 size: "+xss1.size());
		Log.d(TAG, "getChartLines: xss2 size: "+xss2.size());
		Line line1 = getLine();
		line1.setValues(xss1);

		Line line2 = getLine();
		line2.setColor(R.color.holo_blue_dark);
		line2.setValues(xss2);

		List<Line> lines = new ArrayList<>(1);
		lines.add(line1);
		lines.add(line2);
		return lines;
	}

	@Override
	public String getName() {
		return getString(R.string.title_activity_insulin);
	}

	@Override
	public void onItemSelected(int position) {

	}
}
