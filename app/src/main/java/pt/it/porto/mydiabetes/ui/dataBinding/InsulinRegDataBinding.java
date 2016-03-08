package pt.it.porto.mydiabetes.ui.dataBinding;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class InsulinRegDataBinding extends DateTimeDataBinding implements Parcelable {

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
	private int targetGlycemia;
	private float insulinUnits;
	private int idTag;
	private int idNote=-1;


	public InsulinRegDataBinding() {
	}

	public InsulinRegDataBinding(@Nullable InsulinRegDataBinding oldInsulinReg) {
		super(oldInsulinReg);
		if (oldInsulinReg == null) {
			return;
		}
		id = oldInsulinReg.getId();
		idUser = oldInsulinReg.getIdUser();
		idBloodGlucose = oldInsulinReg.getIdBloodGlucose();
		idInsulin = oldInsulinReg.getIdInsulin();
		targetGlycemia = oldInsulinReg.getTargetGlycemia();
		insulinUnits = oldInsulinReg.getInsulinUnits();
		idTag = oldInsulinReg.getIdTag();
		idNote = oldInsulinReg.getIdNote();
	}

	protected InsulinRegDataBinding(Parcel in) {
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
		if (o == null && idBloodGlucose == -1) {
			return true;
		} else if (o == null) {
			return false;
		} else {
			if (!(o instanceof InsulinRegDataBinding)) {
				return false;
			}
			InsulinRegDataBinding otherInsulinReg = (InsulinRegDataBinding) o;
			if (id == otherInsulinReg.getId() &&
					idUser == otherInsulinReg.getIdUser() &&
					idBloodGlucose == otherInsulinReg.getIdBloodGlucose() &&
					idInsulin == otherInsulinReg.getIdInsulin() &&
					targetGlycemia == otherInsulinReg.getTargetGlycemia() &&
					Float.compare(insulinUnits, otherInsulinReg.getInsulinUnits()) == 0 &&
					idTag == otherInsulinReg.getIdTag() &&
					idNote == otherInsulinReg.getIdNote()) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String toString() {
		return "InsulinRegDataBinding{" +
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
