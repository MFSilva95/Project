package pt.it.porto.mydiabetes.data;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class CarbsRec extends DateTime implements Parcelable {

	public static final Creator<CarbsRec> CREATOR = new Creator<CarbsRec>() {
		@Override
		public CarbsRec createFromParcel(Parcel in) {
			return new CarbsRec(in);
		}

		@Override
		public CarbsRec[] newArray(int size) {
			return new CarbsRec[size];
		}
	};
	private int id = -1;
	private int id_User = -1;
	private int value = -1;
	private String photopath = null;
	private int idTag = -1;
	private int idNote = -1;
	private int mealId = -1;

	public CarbsRec() {
	}

	public CarbsRec(CarbsRec oldCarbs) {
		super(oldCarbs);
		if (oldCarbs == null) {
			return;
		}
		id = oldCarbs.getId();
		id_User = oldCarbs.getIdUser();
		value = oldCarbs.getCarbsValue();
		photopath = oldCarbs.getPhotoPath();
		idTag = oldCarbs.getIdTag();
		idNote = oldCarbs.getIdNote();
		mealId = oldCarbs.getMealId();
	}

	protected CarbsRec(Parcel in) {
		super(in);
		id = in.readInt();
		id_User = in.readInt();
		value = in.readInt();
		photopath = in.readString();
		idTag = in.readInt();
		idNote = in.readInt();
		mealId = in.readInt();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUser() {
		return id_User;
	}

	public void setIdUser(int id_User) {
		this.id_User = id_User;
	}

	public int getCarbsValue() {
		return value;
	}

	public void setCarbsValue(int value) {
		this.value = value;
	}

	public String getPhotoPath() {
		if(photopath!=null){
			File temp = new File(photopath);
			if(!temp.exists()){photopath=null; return null;}
		}
		return photopath;
	}

	public void setPhotoPath(String photopath) {
		this.photopath = photopath;
	}

	public boolean hasPhotoPath() {
		return photopath != null && !photopath.isEmpty();
	}


	public int getIdTag() {
		return idTag;
	}

	public void setIdTag(int id_Tag) {
		this.idTag = id_Tag;
	}

	public int getIdNote() {
		return idNote;
	}

	public void setIdNote(int id_Note) {
		this.idNote = id_Note;
	}

	public int getMealId() {
		return mealId;
	}

	public void setMealId(int mealId) {
		this.mealId = mealId;
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
		dest.writeInt(idTag);
		dest.writeInt(idNote);
		dest.writeInt(mealId);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null && value == 0 && (photopath == null || "".equals(photopath))) {
			return true;
		} else if (o == null) {
			return false;
		} else {
			if (!(o instanceof CarbsRec)) {
				return false;
			}
			CarbsRec otherCarbs = (CarbsRec) o;
			return value == otherCarbs.getCarbsValue() &&
					(otherCarbs.getPhotoPath() == null || otherCarbs.getPhotoPath().equals(photopath)) &&
					idTag == otherCarbs.getIdTag() &&
					((photopath != null && photopath.equals(otherCarbs.getPhotoPath())) || photopath == null && otherCarbs.getPhotoPath() == null) &&
					idNote == otherCarbs.getIdNote() &&
					id == otherCarbs.getId() &&
					super.equals(o);

		}
	}

	@Override
	public String toString() {
		return "CarbsRec{" +
				"id=" + id +
				", id_User=" + id_User +
				", value=" + value +
				", photopath='" + photopath + '\'' +
				", idTag=" + idTag +
				", idNote=" + idNote +
				", mealID=" + mealId +
				'}';
	}
}
