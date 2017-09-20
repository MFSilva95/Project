package pt.it.porto.mydiabetes.data;


import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CarbsRatioData implements Parcelable {

	public static final Creator<CarbsRatioData> CREATOR = new Creator<CarbsRatioData>() {
		@Override
		public CarbsRatioData createFromParcel(Parcel in) {
			return new CarbsRatioData(in);
		}

		@Override
		public CarbsRatioData[] newArray(int size) {
			return new CarbsRatioData[size];
		}
	};
	private int id;
	private int user_id;
//	private int tag_id;
	private String name;
	private String start;
	private String end;
	private double value;

	public CarbsRatioData() {
	}

	protected CarbsRatioData(Parcel in) {
		id = in.readInt();
		user_id = in.readInt();
//		tag_id = in.readInt();
		value = in.readDouble();
		name = in.readString();
		start = in.readString();
		end = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(user_id);
//		dest.writeInt(tag_id);
		dest.writeDouble(value);
		dest.writeString(name);
		dest.writeString(start);
		dest.writeString(end);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int id) {
		this.user_id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getStartInMinutes() {
		String[] time = start.split(":");
		int hour = Integer.parseInt(time[0]);
		int minute = Integer.parseInt(time[1]);
		return ((hour*60)+minute);
	}

	public int getEndInMinutes() {
		String[] time = end.split(":");
		int hour = Integer.parseInt(time[0]);
		int start = Integer.parseInt(getStart().split(":")[0]);
		if(hour<start){hour+=24;}
		int minute = Integer.parseInt(time[1]);
		return ((hour*60)+minute);
	}


//	public int getTagId() {
//		return tag_id;
//	}
//
//	public void setTagId(int id) {
//		this.tag_id = id;
//	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CarbsRatioData)) return false;

		CarbsRatioData that = (CarbsRatioData) o;
		if (user_id != that.user_id) return false;
		if (id != that.id) return false;
//		if (tag_id != that.tag_id) return false;
		if (Double.compare(that.value, value) != 0) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (start != null ? !start.equals(that.start) : that.start != null) return false;
		return end != null ? end.equals(that.end) : that.end == null;

	}

	@Override
	public String toString() {
		return "CarbsSensData{" +
				" id=" + id +
//				", tag_id="+tag_id+
				", name='" + name + '\'' +
				", start='" + start + '\'' +
				", end='" + end + '\'' +
				", value=" + value +
				'}';
	}
}
