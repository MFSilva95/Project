package pt.it.porto.mydiabetes.ui.dataBinding;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class CholesterolDataBinding extends DateTimeDataBinding {

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
		if (!(o instanceof CholesterolDataBinding)) return false;
		if (!super.equals(o)) return false;

		CholesterolDataBinding that = (CholesterolDataBinding) o;

		if (id != that.id) return false;
		if (idUser != that.idUser) return false;
		if (Double.compare(that.value, value) != 0) return false;
		return idNote == that.idNote;

	}

	@Override
	public String toString() {
		return "CholesterolDataBinding{" +
				"id=" + id +
				", idUser=" + idUser +
				", value=" + value +
				", idNote=" + idNote +
				'}';
	}
}
