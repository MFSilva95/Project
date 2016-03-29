package pt.it.porto.mydiabetes.data;


public class Note {

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
}
