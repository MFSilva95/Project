package pt.it.porto.mydiabetes.ui.dataBinding;


import android.os.Parcel;
import android.os.Parcelable;

public class TagDataBinding implements Parcelable {

	public static final Creator<TagDataBinding> CREATOR = new Creator<TagDataBinding>() {
		@Override
		public TagDataBinding createFromParcel(Parcel in) {
			return new TagDataBinding(in);
		}

		@Override
		public TagDataBinding[] newArray(int size) {
			return new TagDataBinding[size];
		}
	};
	private String name;
	private int id;
	private String start;
	private String end;

	public TagDataBinding() {
	}

	protected TagDataBinding(Parcel in) {
		name = in.readString();
		id = in.readInt();
		start = in.readString();
		end = in.readString();
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


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(id);
		dest.writeString(start);
		dest.writeString(end);
	}
}
