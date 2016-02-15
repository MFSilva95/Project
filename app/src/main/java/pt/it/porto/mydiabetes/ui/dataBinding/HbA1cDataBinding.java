package pt.it.porto.mydiabetes.ui.dataBinding;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class HbA1cDataBinding extends DateTimeDataBinding {

	private int id;
	private int idUser;
	private double value;
	private int idNote;

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
}
