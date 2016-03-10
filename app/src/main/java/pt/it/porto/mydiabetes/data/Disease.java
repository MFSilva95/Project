package pt.it.porto.mydiabetes.data;


import android.os.Parcel;
import android.os.Parcelable;

public class Disease implements Parcelable {

	public static final Creator<Disease> CREATOR = new Creator<Disease>() {
		@Override
		public Disease createFromParcel(Parcel in) {
			return new Disease(in);
		}

		@Override
		public Disease[] newArray(int size) {
			return new Disease[size];
		}
	};
	private String name;
	private int id;

	public Disease() {
	}

	protected Disease(Parcel in) {
		name = in.readString();
		id = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(id);
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
}
