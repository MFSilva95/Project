package pt.it.porto.mydiabetes.ui.charts.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.AbstractChartActivity;
import pt.it.porto.mydiabetes.utils.DateUtils;

public abstract class ChartData implements Parcelable {

	public static final int DATA_TYPE_WEIGHT = 1;
	public static final int DATA_TYPE_CARBS = 2;
	public static final int DATA_TYPE_LOGBOOK = 3;

	private String startDate;
	private String endDate;

	ChartData(Context context) {
		super();
		Calendar calendar = Calendar.getInstance();
		startDate = DateUtils.getFormattedDate(calendar);
		calendar.roll(Calendar.WEEK_OF_YEAR, false);
		endDate = DateUtils.getFormattedDate(calendar);
	}

	ChartData(Parcel source) {
		startDate = source.readString();
		endDate = source.readString();
	}

	public abstract boolean hasFilters();

	abstract String[] getFilterList();

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getStartDate() {
		return startDate;
	}


	public abstract Cursor getCursor(Context context);

	public abstract ArrayList<String> getTables();

	public void setupFilter(Context context, ListView list) {
		list.setAdapter(new Adapter(context));
	}

	public abstract void toggleFilter(int pox);

	public abstract boolean isFilterActive(int pox);

	public abstract int[] getIcons();

	class Adapter extends ArrayAdapter<String> {

		public Adapter(Context context) {
			super(context, R.layout.list_item_filter, R.id.text);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			((CheckedTextView) view).setChecked(isFilterActive(position));
			return view;
		}

		@Override
		public String getItem(int position) {
			return getFilterList()[position];
		}

		@Override
		public int getCount() {
			return getFilterList().length;
		}
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(startDate);
		dest.writeString(endDate);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ChartData> CREATOR = new Creator<ChartData>() {
		@Override
		public ChartData createFromParcel(Parcel source) {
			return ChartData.getConcreteClass(source);
		}

		@Override
		public ChartData[] newArray(int size) {
			return new ChartData[size];
		}
	};

	private static ChartData getConcreteClass(Parcel source) {
		switch (source.readInt()) {
			case DATA_TYPE_WEIGHT:
				return new Weight(source);
			case DATA_TYPE_CARBS:
				return new Carbs(source);
			case DATA_TYPE_LOGBOOK:
				return new Logbook(source);
			default:
				return null;
		}
	}
}
