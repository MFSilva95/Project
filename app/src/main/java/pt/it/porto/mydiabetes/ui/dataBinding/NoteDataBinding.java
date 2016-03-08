package pt.it.porto.mydiabetes.ui.dataBinding;


public class NoteDataBinding {

	private int id;
	private String note;

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
		if (!(o instanceof NoteDataBinding)) return false;

		NoteDataBinding that = (NoteDataBinding) o;

		if (id != that.id) return false;
		return note != null ? note.equals(that.note) : that.note == null;

	}

	@Override
	public String toString() {
		return "NoteDataBinding{" +
				"id=" + id +
				", note='" + note + '\'' +
				'}';
	}
}
