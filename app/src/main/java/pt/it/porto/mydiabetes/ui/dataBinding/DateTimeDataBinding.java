package pt.it.porto.mydiabetes.ui.dataBinding;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.util.Calendar;

import pt.it.porto.mydiabetes.utils.DateUtils;

public class DateTimeDataBinding implements Parcelable, Comparable<DateTimeDataBinding> {
	public static final Creator<DateTimeDataBinding> CREATOR = new Creator<DateTimeDataBinding>() {
		@Override
		public DateTimeDataBinding createFromParcel(Parcel in) {
			return new DateTimeDataBinding(in);
		}

		@Override
		public DateTimeDataBinding[] newArray(int size) {
			return new DateTimeDataBinding[size];
		}
	};
	private Calendar dateTime;

	public DateTimeDataBinding() {
	}

	protected DateTimeDataBinding(Parcel in) {
		dateTime = (Calendar) in.readSerializable();
	}

	public DateTimeDataBinding(DateTimeDataBinding oldDateTimeData) {
		if (oldDateTimeData == null) {
			return;
		}
		this.dateTime = oldDateTimeData.getDateTime();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(dateTime);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void setDateTime(Calendar dateTime) {
		this.dateTime = dateTime;
	}

	public void setDateTime(String date, String time) {
		try {
			Calendar tmp = DateUtils.getDateTime(date, time);
			if (!DateUtils.isSameTime(this.dateTime, tmp)) {
				this.dateTime = tmp;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Calendar getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		try {
			Calendar tmp = DateUtils.parseDateTime(dateTime);
			if (!DateUtils.isSameTime(this.dateTime, tmp)) {
				this.dateTime = tmp;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(@NonNull DateTimeDataBinding another) {
		if (DateUtils.isSameTime(dateTime, another.getDateTime())) {
			return 0;
		} else {
			return dateTime.compareTo(another.getDateTime());
		}
	}

	/**
	 * Gets the formatted date to the UI
	 *
	 * @return the date properly formatted
	 */
	public String getFormattedDate() {
		return DateUtils.getFormattedDate(dateTime);
	}

	/**
	 * Gets the formatted time to the UI
	 *
	 * @return the time properly formatted
	 */
	public String getFormattedTime() {
		return DateUtils.getFormattedTime(dateTime);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof DateTimeDataBinding)) {
			return false;
		}
		Calendar otherDateTime = ((DateTimeDataBinding) o).getDateTime();
		return dateTime.get(Calendar.YEAR) == otherDateTime.get(Calendar.YEAR) &&
				dateTime.get(Calendar.DAY_OF_YEAR) == otherDateTime.get(Calendar.DAY_OF_YEAR) &&
				dateTime.get(Calendar.HOUR_OF_DAY) == otherDateTime.get(Calendar.HOUR_OF_DAY) &&
				dateTime.get(Calendar.MINUTE) == otherDateTime.get(Calendar.MINUTE) &&
				dateTime.get(Calendar.SECOND) == otherDateTime.get(Calendar.SECOND);
	}

	@Override
	public String toString() {
		return "DateTimeDataBinding{" +
				"dateTime=" + dateTime +
				'}';
	}
}
