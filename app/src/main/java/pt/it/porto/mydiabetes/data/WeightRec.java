package pt.it.porto.mydiabetes.data;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class WeightRec extends DateTime {

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

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
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
		if (!(o instanceof WeightRec)) return false;
		if (!super.equals(o)) return false;

		WeightRec that = (WeightRec) o;

		if (id != that.id) return false;
		if (idUser != that.idUser) return false;
		if (idNote != that.idNote) return false;
		return Double.compare(value, that.getValue())==0;

	}

	@Override
	public String toString() {
		return "WeightRec{" +
				"id=" + id +
				", idUser=" + idUser +
				", value=" + value +
				", idNote=" + idNote +
				'}';
	}
}
