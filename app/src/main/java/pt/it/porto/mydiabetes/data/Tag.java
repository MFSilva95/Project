package pt.it.porto.mydiabetes.data;


import android.os.Parcel;
import android.os.Parcelable;

public class Tag implements Parcelable {

	public static final Creator<Tag> CREATOR = new Creator<Tag>() {
		@Override
		public Tag createFromParcel(Parcel in) {
			return new Tag(in);
		}

		@Override
		public Tag[] newArray(int size) {
			return new Tag[size];
		}
	};
	private String name;
	private int id;
	private String start;
	private String end;

	public Tag() {
	}

	protected Tag(Parcel in) {
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

	@Override
	public String toString() {
		return "Tag{" +
				"name='" + name + '\'' +
				", id=" + id +
				", start='" + start + '\'' +
				", end='" + end + '\'' +
				'}';
	}
}
