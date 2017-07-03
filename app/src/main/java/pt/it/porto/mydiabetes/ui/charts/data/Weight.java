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
public class Weight extends ChartData {
	public static final String TABLE = MyDiabetesContract.Regist.Weight.TABLE_NAME;
	public static final String VALUE = MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE;
	public static final String DATETIME = MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME;

	private ArrayList<String> tables;
	private ArrayList<String> values;
	private ArrayList<String> datetime;

	public Weight(Context context) {
		super(context);
	}

	public Weight(Parcel source) {
		super(source);
	}

	@Override
	public void toggleFilter(int pox) {
	}

	@Override
	public boolean isFilterActive(int pox) {
		return false;
	}

	@Override
	public int[] getIcons() {
		return new int[]{R.drawable.weight};
	}

	@Override
	public boolean hasFilters() {
		return false;
	}

	@Override
	protected String[] getFilterList() {
		return new String[0];
	}

	private void initData() {
		tables = new ArrayList<>(1);
		tables.add(TABLE);
		values = new ArrayList<>(1);
		values.add(VALUE);
		datetime = new ArrayList<>(1);
		datetime.add(DATETIME);
	}

	public Cursor getCursor(Context context) {
		initData();
		return new ListDataSource(MyDiabetesStorage.getInstance(context))
				.getMultiData(tables.toArray(new String[tables.size()]),
						values.toArray(new String[tables.size()]),
						datetime.toArray(new String[tables.size()]),
						null, getStartDate(), getEndDate(), 100);
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
		dest.writeInt(DATA_TYPE_WEIGHT);
		super.writeToParcel(dest, flags);
	}
}
