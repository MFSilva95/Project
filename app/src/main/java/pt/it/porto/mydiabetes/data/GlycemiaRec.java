package pt.it.porto.mydiabetes.data;


import android.os.Parcel;
import android.os.Parcelable;

public class GlycemiaRec extends DateTime implements Parcelable {

	public static final Creator<GlycemiaRec> CREATOR = new Creator<GlycemiaRec>() {
		@Override
		public GlycemiaRec createFromParcel(Parcel in) {
			return new GlycemiaRec(in);
		}
		@Override
		public GlycemiaRec[] newArray(int size) {
			return new GlycemiaRec[size];
		}
	};
	private int id;
	private int idUser;
	private int value;
	private int idTag;
	private int idNote = -1;
	private int bg_target = -1;

	public GlycemiaRec() {
	}

	public GlycemiaRec(GlycemiaRec oldGlicemia) {
		super(oldGlicemia);
		if (oldGlicemia == null) {
			return;
		}
		id = oldGlicemia.getId();
		idUser = oldGlicemia.getIdUser();
		value = oldGlicemia.getValue();
		idTag = oldGlicemia.getIdTag();
		idNote = oldGlicemia.getIdNote();
		bg_target = oldGlicemia.getBG_target();
	}

	protected GlycemiaRec(Parcel in) {
		super(in);
		id = in.readInt();
		idUser = in.readInt();
		value = in.readInt();
		idTag = in.readInt();
		idNote = in.readInt();
		bg_target = in.readInt();
	}

	public void setObjective(int value){
		this.bg_target = value;
	}

	public int getBG_target(){return bg_target;}

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
		super.writeToParcel(dest, flags);
		dest.writeInt(id);
		dest.writeInt(idUser);
		dest.writeInt(value);
		dest.writeInt(idTag);
		dest.writeInt(idNote);
        dest.writeInt(bg_target);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null && value == 0) {
			return true;
		} else if (o == null) {
			return false;
		} else {
			if (!(o instanceof GlycemiaRec)) {
				return false;
			}
			GlycemiaRec otherGlicemia = (GlycemiaRec) o;
			return value == otherGlicemia.getValue() && id == otherGlicemia.getId() &&
					idTag == otherGlicemia.getIdTag() &&
					idNote == otherGlicemia.getIdNote() &&
                    bg_target == otherGlicemia.getBG_target() &&
					super.equals(o);
		}
	}
}

