package pt.it.porto.mydiabetes.data;


import android.os.Parcel;
import android.os.Parcelable;

public class InsulinTarget implements Parcelable {

	public static final Creator<InsulinTarget> CREATOR = new Creator<InsulinTarget>() {
		@Override
		public InsulinTarget createFromParcel(Parcel in) {
			return new InsulinTarget(in);
		}

		@Override
		public InsulinTarget[] newArray(int size) {
			return new InsulinTarget[size];
		}
	};
	private int id;
	private String name;
	private String start;
	private String end;
	private double target;

	public InsulinTarget() {
	}

	protected InsulinTarget(Parcel in) {
		id = in.readInt();
		name = in.readString();
		start = in.readString();
		end = in.readString();
		target = in.readDouble();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(start);
		dest.writeString(end);
		dest.writeDouble(target);
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public double getTarget() {
		return target;
	}

	public void setTarget(Double target) {
		this.target = target;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof InsulinTarget)) return false;

		InsulinTarget that = (InsulinTarget) o;

		if (id != that.id) return false;
		if (Double.compare(that.target, target) != 0) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (start != null ? !start.equals(that.start) : that.start != null) return false;
		return end != null ? end.equals(that.end) : that.end == null;

	}

	@Override
	public String toString() {
		return "InsulinTarget{" +
				"id=" + id +
				", name='" + name + '\'' +
				", start='" + start + '\'' +
				", end='" + end + '\'' +
				", target=" + target +
				'}';
	}
}
