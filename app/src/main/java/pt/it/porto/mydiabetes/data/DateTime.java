package pt.it.porto.mydiabetes.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.util.Calendar;

import pt.it.porto.mydiabetes.utils.DateUtils;

public class DateTime implements Parcelable, Comparable<DateTime> {
	public static final Creator<DateTime> CREATOR = new Creator<DateTime>() {
		@Override
		public DateTime createFromParcel(Parcel in) {
			return new DateTime(in);
		}

		@Override
		public DateTime[] newArray(int size) {
			return new DateTime[size];
		}
	};
	private Calendar dateTime;

	public DateTime() {
	}

	protected DateTime(Parcel in) {
		dateTime = (Calendar) in.readSerializable();
	}

	public DateTime(DateTime oldDateTime) {
		if (oldDateTime == null) {
			return;
		}
		this.dateTime = oldDateTime.getDateTime();
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
	public int compareTo(@NonNull DateTime another) {
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
		if (!(o instanceof DateTime)) {
			return false;
		}
		Calendar otherDateTime = ((DateTime) o).getDateTime();
		return dateTime.get(Calendar.YEAR) == otherDateTime.get(Calendar.YEAR) &&
				dateTime.get(Calendar.DAY_OF_YEAR) == otherDateTime.get(Calendar.DAY_OF_YEAR) &&
				dateTime.get(Calendar.HOUR_OF_DAY) == otherDateTime.get(Calendar.HOUR_OF_DAY) &&
				dateTime.get(Calendar.MINUTE) == otherDateTime.get(Calendar.MINUTE) &&
				dateTime.get(Calendar.SECOND) == otherDateTime.get(Calendar.SECOND);
	}

	@Override
	public String toString() {
		return "DateTime{" +
				"dateTime=" + dateTime +
				'}';
	}
}
