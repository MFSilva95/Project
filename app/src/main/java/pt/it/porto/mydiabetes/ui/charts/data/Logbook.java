package pt.it.porto.mydiabetes.ui.charts.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.Arrays;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListDataSource;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;

@SuppressLint("ParcelCreator") // it is done in the class ChartData
public class Logbook extends ChartData {
	private String[] filters = new String[3];
	private boolean[] filterActive = new boolean[filters.length];
	private String[] extras = new String[3];
	private boolean[] extrasActive = new boolean[extras.length];
	private ArrayList<String> tables;

	public Logbook(Context context) {
		super(context);
		// filters
		filters[0] = context.getResources().getString(R.string.Carbs);
		filters[1] = context.getResources().getString(R.string.Glycemia);
		filters[2] = context.getResources().getString(R.string.Insulin);
		Arrays.fill(filterActive, true);

		// extras
		extras[0] = context.getString(R.string.show_labels);
		extras[1] = context.getString(R.string.show_hiperglicemia_limit);
		extras[2] = context.getString(R.string.show_hipoglicemia_limit);
		Arrays.fill(extrasActive, true);
	}

	public Logbook(Parcel source) {
		super(source);
		source.readStringArray(filters);
		source.readBooleanArray(filterActive);
		source.readStringArray(extras);
		source.readBooleanArray(extrasActive);
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
	public boolean hasExtras() {
		return true;
	}

	@Override
	String[] getExtrasList() {
		return extras;
	}

	@Override
	public void toggleExtra(int pox) {
		extrasActive[pox]=!extrasActive[pox];
	}

	@Override
	public boolean isExtraActive(int pox) {
		return extrasActive[pox];
	}

	@Override
	protected String[] getFilterList() {
		return filters;
	}

	public Cursor getCursor(Context context) {
		if (!filterActive[0] && !filterActive[1] && !filterActive[2]) {
			return null;
		}
		return new ListDataSource(MyDiabetesStorage.getInstance(context)).getLogbookData(true, true, true, getStartDate(), getEndDate(), -1);
	}

	@Override
	public ArrayList<String> getTables() {
		tables = new ArrayList<>(3);
		for (int i = 0; i < filterActive.length; i++) {
			if (filterActive[i]) {
				tables.add("");
			}
		}
		return tables;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(DATA_TYPE_LOGBOOK);
		super.writeToParcel(dest, flags);
		dest.writeStringArray(filters);
		dest.writeBooleanArray(filterActive);
		dest.writeStringArray(extras);
		dest.writeBooleanArray(extrasActive);
	}
}
