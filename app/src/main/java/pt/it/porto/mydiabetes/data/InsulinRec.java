package pt.it.porto.mydiabetes.data;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class InsulinRec extends DateTime implements Parcelable {

	public static final Creator<InsulinRec> CREATOR = new Creator<InsulinRec>() {
		@Override
		public InsulinRec createFromParcel(Parcel in) {
			return new InsulinRec(in);
		}

		@Override
		public InsulinRec[] newArray(int size) {
			return new InsulinRec[size];
		}
	};
	private int id;
	private int idUser;
	private int idBloodGlucose = -1;
	private int idInsulin;
	private int targetGlycemia;
	private float insulinUnits;
	private int idTag;
	private int idNote=-1;


	public InsulinRec() {
	}

	public InsulinRec(@Nullable InsulinRec oldInsulinRec) {
		super(oldInsulinRec);
		if (oldInsulinRec == null) {
			return;
		}
		id = oldInsulinRec.getId();
		idUser = oldInsulinRec.getIdUser();
		idBloodGlucose = oldInsulinRec.getIdBloodGlucose();
		idInsulin = oldInsulinRec.getIdInsulin();
		targetGlycemia = oldInsulinRec.getTargetGlycemia();
		insulinUnits = oldInsulinRec.getInsulinUnits();
		idTag = oldInsulinRec.getIdTag();
		idNote = oldInsulinRec.getIdNote();
	}

	protected InsulinRec(Parcel in) {
		super(in);
		id = in.readInt();
		idUser = in.readInt();
		idBloodGlucose = in.readInt();
		idInsulin = in.readInt();
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
		super.writeToParcel(dest, flags);
		dest.writeInt(id);
		dest.writeInt(idUser);
		dest.writeInt(idBloodGlucose);
		dest.writeInt(idInsulin);
		dest.writeInt(targetGlycemia);
		dest.writeFloat(insulinUnits);
		dest.writeInt(idTag);
		dest.writeInt(idNote);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else {
			if (!(o instanceof InsulinRec)) {
				return false;
			}
			InsulinRec otherInsulinRec = (InsulinRec) o;
			return id == otherInsulinRec.getId() &&
					idUser == otherInsulinRec.getIdUser() &&
					idBloodGlucose == otherInsulinRec.getIdBloodGlucose() &&
					idInsulin == otherInsulinRec.getIdInsulin() &&
					targetGlycemia == otherInsulinRec.getTargetGlycemia() &&
					Float.compare(insulinUnits, otherInsulinRec.getInsulinUnits()) == 0 &&
					idTag == otherInsulinRec.getIdTag() &&
					idNote == otherInsulinRec.getIdNote() &&
					super.equals(o);
		}
	}

	@Override
	public String toString() {
		return "InsulinRec{" +
				"id=" + id +
				", idUser=" + idUser +
				", idBloodGlucose=" + idBloodGlucose +
				", idInsulin=" + idInsulin +
				", targetGlycemia=" + targetGlycemia +
				", insulinUnits=" + insulinUnits +
				", idTag=" + idTag +
				", idNote=" + idNote +
				'}';
	}
}
