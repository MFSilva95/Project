package pt.it.porto.mydiabetes.ui.dataBinding;


import android.os.Parcel;
import android.os.Parcelable;

public class CarbsDataBinding extends DateTimeDataBinding implements Parcelable {

	public static final Creator<CarbsDataBinding> CREATOR = new Creator<CarbsDataBinding>() {
		@Override
		public CarbsDataBinding createFromParcel(Parcel in) {
			return new CarbsDataBinding(in);
		}

		@Override
		public CarbsDataBinding[] newArray(int size) {
			return new CarbsDataBinding[size];
		}
	};
	private int id;
	private int id_User;
	private int value;
	private String photopath;
	private int id_Tag;
	private int id_Note;

	public CarbsDataBinding() {
	}

	protected CarbsDataBinding(Parcel in) {
		super(in);
		id = in.readInt();
		id_User = in.readInt();
		value = in.readInt();
		photopath = in.readString();
		id_Tag = in.readInt();
		id_Note = in.readInt();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_User() {
		return id_User;
	}

	public void setId_User(int id_User) {
		this.id_User = id_User;
	}

	public int getCarbsValue() {
		return value;
	}

	public void setCarbsValue(int value) {
		this.value = value;
	}

	public String getPhotoPath() {
		return photopath;
	}

	public void setPhotoPath(String photopath) {
		this.photopath = photopath;
	}

	public boolean hasPhotoPath() {
		return photopath != null && !photopath.isEmpty();
	}


	public int getId_Tag() {
		return id_Tag;
	}

	public void setId_Tag(int id_Tag) {
		this.id_Tag = id_Tag;
	}

	public int getId_Note() {
		return id_Note;
	}

	public void setId_Note(int id_Note) {
		this.id_Note = id_Note;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(id);
		dest.writeInt(id_User);
		dest.writeInt(value);
		dest.writeString(photopath);
		dest.writeInt(id_Tag);
		dest.writeInt(id_Note);
	}
}
