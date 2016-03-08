package pt.it.porto.mydiabetes.ui.dataBinding;


import android.os.Parcel;
import android.os.Parcelable;

public class GlycemiaDataBinding extends DateTimeDataBinding implements Parcelable {

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
	private int idTag;
	private int idNote = -1;

	public GlycemiaDataBinding() {
	}

	public GlycemiaDataBinding(GlycemiaDataBinding oldGlicemia) {
		super(oldGlicemia);
		if (oldGlicemia == null) {
			return;
		}
		id = oldGlicemia.getId();
		idUser = oldGlicemia.getIdUser();
		value = oldGlicemia.getValue();
		idTag = oldGlicemia.getIdTag();
		idNote = oldGlicemia.getIdNote();
	}

	protected GlycemiaDataBinding(Parcel in) {
		super(in);
		id = in.readInt();
		idUser = in.readInt();
		value = in.readInt();
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
	}

	@Override
	public boolean equals(Object o) {
		if (o == null && value == 0) {
			return true;
		} else if (o == null) {
			return false;
		} else {
			if (!(o instanceof GlycemiaDataBinding)) {
				return false;
			}
			GlycemiaDataBinding otherGlicemia = (GlycemiaDataBinding) o;
			if (value == otherGlicemia.getValue() && id == otherGlicemia.getId() &&
					idTag == otherGlicemia.getIdTag() &&
					idNote == otherGlicemia.getIdNote() &&
					super.equals(o)) {
				return true;
			} else {
				return false;
			}
		}
	}
}

