package pt.it.porto.mydiabetes.data;


import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable{

	public static final Creator<Note> CREATOR = new Creator<Note>() {
		@Override
		public Note createFromParcel(Parcel in) {
			return new Note(in);
		}

		@Override
		public Note[] newArray(int size) {
			return new Note[size];
		}
	};
	private int id;
	private String note;

	protected Note(Parcel in) {
		id = in.readInt();
		note = in.readString();
	}
	public Note(){}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Note)) return false;

		Note that = (Note) o;

		if (id != that.id) return false;
		return note != null ? note.equals(that.note) : that.note == null;

	}

	@Override
	public String toString() {
		return "Note{" +
				"id=" + id +
				", note='" + note + '\'' +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		//super.writeToParcel(dest, flags);
		dest.writeInt(id);
		dest.writeString(note);
	}
}
