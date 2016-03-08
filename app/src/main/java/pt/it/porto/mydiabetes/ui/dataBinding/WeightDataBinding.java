package pt.it.porto.mydiabetes.ui.dataBinding;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class WeightDataBinding extends DateTimeDataBinding {

	private int id;
	private int idUser;
	private Double value;
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
		if (!(o instanceof WeightDataBinding)) return false;
		if (!super.equals(o)) return false;

		WeightDataBinding that = (WeightDataBinding) o;

		if (id != that.id) return false;
		if (idUser != that.idUser) return false;
		if (idNote != that.idNote) return false;
		return value != null ? value.equals(that.value) : that.value == null;

	}

	@Override
	public String toString() {
		return "WeightDataBinding{" +
				"id=" + id +
				", idUser=" + idUser +
				", value=" + value +
				", idNote=" + idNote +
				'}';
	}
}
