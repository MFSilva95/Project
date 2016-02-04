package pt.it.porto.mydiabetes.ui.listAdapters;


import android.os.Parcel;
import android.os.Parcelable;

public class GlycemiaDataBinding implements Parcelable {

	public static final Creator<GlycemiaDataBinding> CREATOR = new Creator<GlycemiaDataBinding>() {
		@Override
		public GlycemiaDataBinding createFromParcel(Parcel in) {
			return new GlycemiaDataBinding(in);
		}

		@Override
		public GlycemiaDataBinding[] newArray(int size) {
			return new GlycemiaDataBinding[size];
		}
	};
	private int id;
	private int idUser;
	private int value;
	private String date;
	private String time;
	private int idTag;
	private int idNote;

	public GlycemiaDataBinding() {
	}

	protected GlycemiaDataBinding(Parcel in) {
		id = in.readInt();
		idUser = in.readInt();
		value = in.readInt();
		date = in.readString();
		time = in.readString();
		idTag = in.readInt();
		idNote = in.readInt();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public int getIdTag() {
		return idTag;
	}

	public void setIdTag(int idTag) {
		this.idTag = idTag;
	}

	public int getIdNote() {
		return idNote;
	}

	public void setIdNote(int idNote) {
		this.idNote = idNote;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(idUser);
		dest.writeInt(value);
		dest.writeString(date);
		dest.writeString(time);
		dest.writeInt(idTag);
		dest.writeInt(idNote);
	}
}

