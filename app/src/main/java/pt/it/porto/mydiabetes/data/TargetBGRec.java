package pt.it.porto.mydiabetes.data;


import android.os.Parcel;
import android.os.Parcelable;

public class TargetBGRec implements Parcelable {

	public static final Creator<TargetBGRec> CREATOR = new Creator<TargetBGRec>() {
		@Override
		public TargetBGRec createFromParcel(Parcel in) {
			return new TargetBGRec(in);
		}

		@Override
		public TargetBGRec[] newArray(int size) {
			return new TargetBGRec[size];
		}
	};
	private int id;
	private String name;
	private String timeStart;
	private String timeEnd;
	private int value;

	public TargetBGRec() {
	}
	public TargetBGRec(TargetBGRec oldCarbs) {
		if (oldCarbs == null) {
			return;
		}
		id = oldCarbs.getId();
		name = oldCarbs.getName();
		timeStart = oldCarbs.getTimeStart();
		timeEnd = oldCarbs.getTimeEnd();
		value = oldCarbs.getValue();
	}

	protected TargetBGRec(Parcel in) {
		id = in.readInt();
		name = in.readString();
		timeStart = in.readString();
		timeEnd = in.readString();
		value = in.readInt();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}

	public String getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(String timeEnd) {
		this.timeEnd = timeEnd;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(timeStart);
		dest.writeString(timeEnd);
		dest.writeInt(value);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null && value == 0) {
			return true;
		} else if (o == null) {
			return false;
		} else {
			if (!(o instanceof TargetBGRec)) {
				return false;
			}
			TargetBGRec otherBG = (TargetBGRec) o;
			return value == otherBG.getValue() &&
					name.equals(otherBG.getName()) &&
					timeStart.equals(otherBG.getTimeStart()) &&
					timeEnd.equals(otherBG.getTimeEnd()) &&
					id == otherBG.getId() &&
					super.equals(o);

		}
	}

	@Override
	public String toString() {
		return "TARGET_REC{" +
				"id=" + id +
				", value=" + value +
				'}';
	}
}
