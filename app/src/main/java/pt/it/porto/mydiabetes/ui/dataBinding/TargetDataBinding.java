package pt.it.porto.mydiabetes.ui.dataBinding;


import android.os.Parcel;
import android.os.Parcelable;

public class TargetDataBinding implements Parcelable {

	public static final Creator<TargetDataBinding> CREATOR = new Creator<TargetDataBinding>() {
		@Override
		public TargetDataBinding createFromParcel(Parcel in) {
			return new TargetDataBinding(in);
		}

		@Override
		public TargetDataBinding[] newArray(int size) {
			return new TargetDataBinding[size];
		}
	};
	private int id;
	private String name;
	private String start;
	private String end;
	private double target;

	public TargetDataBinding() {
	}

	protected TargetDataBinding(Parcel in) {
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


}
