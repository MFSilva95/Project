package pt.it.porto.mydiabetes.ui.listAdapters;


import android.os.Parcel;
import android.os.Parcelable;

public class InsulinRegDataBinding implements Parcelable {

	public static final Creator<InsulinRegDataBinding> CREATOR = new Creator<InsulinRegDataBinding>() {
		@Override
		public InsulinRegDataBinding createFromParcel(Parcel in) {
			return new InsulinRegDataBinding(in);
		}

		@Override
		public InsulinRegDataBinding[] newArray(int size) {
			return new InsulinRegDataBinding[size];
		}
	};
	private int id;
	private int idUser;
	private int idBloodGlucose = -1;
	private int idInsulin;
	private String date;
	private String time;
	private int targetGlycemia;
	private float insulinUnits;
	private int idTag;
	private int idNote;


	public InsulinRegDataBinding() {
	}

	protected InsulinRegDataBinding(Parcel in) {
		id = in.readInt();
		idUser = in.readInt();
		idBloodGlucose = in.readInt();
		idInsulin = in.readInt();
		date = in.readString();
		time = in.readString();
		targetGlycemia = in.readInt();
		insulinUnits = in.readFloat();
		idTag = in.readInt();
		idNote = in.readInt();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTargetGlycemia() {
		return targetGlycemia;
	}

	public void setTargetGlycemia(int targetGlycemia) {
		this.targetGlycemia = targetGlycemia;
	}

	public float getInsulinUnits() {
		return insulinUnits;
	}

	public void setInsulinUnits(float insulinUnits) {
		this.insulinUnits = insulinUnits;
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

	public int getIdBloodGlucose() {
		return idBloodGlucose;
	}

	public void setIdBloodGlucose(int idBloodGlucose) {
		this.idBloodGlucose = idBloodGlucose;
	}

	public int getIdInsulin() {
		return idInsulin;
	}

	public void setIdInsulin(int idInsulin) {
		this.idInsulin = idInsulin;
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
		dest.writeInt(idBloodGlucose);
		dest.writeInt(idInsulin);
		dest.writeString(date);
		dest.writeString(time);
		dest.writeInt(targetGlycemia);
		dest.writeFloat(insulinUnits);
		dest.writeInt(idTag);
		dest.writeInt(idNote);
	}
}
