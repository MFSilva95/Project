package pt.it.porto.mydiabetes.ui.charts.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListDataSource;
import pt.it.porto.mydiabetes.database.MyDiabetesContract;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;

@SuppressLint("ParcelCreator") // it is done in the class ChartData
public class Carbs extends ChartData {
	public static final String TABLE = MyDiabetesContract.Regist.CarboHydrate.TABLE_NAME;
	public static final String VALUE = MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_VALUE;
	public static final String DATETIME = MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME;

	private String[] filters = new String[2];
	private boolean[] filterActive = new boolean[filters.length];
	private ArrayList<String> tables;
	private ArrayList<String> values;
	private ArrayList<String> datetime;
	private ArrayList<String> extras;

	public Carbs(Context context) {
		super(context);
		filters[0] = context.getResources().getString(R.string.Glycemia);
		filters[1] = context.getResources().getString(R.string.Insulin);
	}

	public Carbs(Parcel source) {
		super(source);
		source.readStringArray(filters);
		source.readBooleanArray(filterActive);
	}

	@Override
	public void toggleFilter(int pox) {
		filterActive[pox] = !filterActive[pox];
	}

	@Override
	public boolean isFilterActive(int pox) {
		return filterActive[pox];
	}

	@Override
	public int[] getIcons() {
//		return new int[]{R.drawable.carbs, R.drawable.glucose, R.drawable.insulin};
		return new int[]{R.drawable.log};
	}

	@Override
	public boolean hasFilters() {
		return true;
	}

	@Override
	protected String[] getFilterList() {
		return filters;
	}

	private void initData() {
		tables = new ArrayList<>(1);
		tables.add(TABLE);
		values = new ArrayList<>(1);
		values.add(VALUE);
		datetime = new ArrayList<>(1);
		datetime.add(DATETIME);
		extras = new ArrayList<>(1);
		extras.add(null);

		if (filterActive[0]) {
			tables.add(MyDiabetesContract.Regist.BloodGlucose.TABLE_NAME);
			values.add(MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_VALUE);
			datetime.add(MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_DATETIME);
			extras.add(null);
		}
		if (filterActive[1]) {
			tables.add(MyDiabetesContract.Regist.Insulin.TABLE_NAME + " JOIN " + MyDiabetesContract.Insulin.TABLE_NAME);
			values.add(MyDiabetesContract.Regist.Insulin.COLUMN_NAME_VALUE);
			datetime.add(MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME);
			extras.add(MyDiabetesContract.Insulin.COLUMN_NAME_NAME);
		}
	}

	public Cursor getCursor(Context context) {
		initData();
		return new ListDataSource(MyDiabetesStorage.getInstance(context))
				.getMultiData(tables.toArray(new String[tables.size()]),
						values.toArray(new String[tables.size()]),
						datetime.toArray(new String[tables.size()]),
						extras.toArray(new String[tables.size()]), getStartDate(), getEndDate(), 100);
	}

	@Override
	public ArrayList<String> getTables() {
		if (tables == null) {
			initData();
		}
		return tables;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(DATA_TYPE_CARBS);
		super.writeToParcel(dest, flags);
		dest.writeStringArray(filters);
		dest.writeBooleanArray(filterActive);
	}
}
