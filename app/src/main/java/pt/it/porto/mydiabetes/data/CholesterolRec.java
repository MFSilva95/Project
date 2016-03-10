package pt.it.porto.mydiabetes.data;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class CholesterolRec extends DateTime {

	private int id;
	private int idUser;
	private double value;
	private int idNote = -1;

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

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public int getIdNote() {
		return idNote;
	}

	public void setIdNote(int idNote) {
		this.idNote = idNote;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CholesterolRec)) return false;
		if (!super.equals(o)) return false;

		CholesterolRec that = (CholesterolRec) o;

		if (id != that.id) return false;
		if (idUser != that.idUser) return false;
		if (Double.compare(that.value, value) != 0) return false;
		return idNote == that.idNote;

	}

	@Override
	public String toString() {
		return "CholesterolRec{" +
				"id=" + id +
				", idUser=" + idUser +
				", value=" + value +
				", idNote=" + idNote +
				'}';
	}
}
