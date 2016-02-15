package pt.it.porto.mydiabetes.ui.dataBinding;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class BloodPressureDataBinding extends DateTimeDataBinding {

	private int id;
	private int idUser;
	private int systolic;
	private int diastolic;
	private int idTag;
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

	public int getSystolic() {
		return systolic;
	}

	public void setSystolic(int systolic) {
		this.systolic = systolic;
	}

	public int getDiastolic() {
		return diastolic;
	}

	public void setDiastolic(int diastolic) {
		this.diastolic = diastolic;
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


}
