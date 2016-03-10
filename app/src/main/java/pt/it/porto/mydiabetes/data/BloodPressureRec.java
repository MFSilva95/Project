package pt.it.porto.mydiabetes.data;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class BloodPressureRec extends DateTime {

	private int id;
	private int idUser;
	private int systolic;
	private int diastolic;
	private int idTag;
	private int idNote=-1;

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


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BloodPressureRec)) return false;
		if (!super.equals(o)) return false;

		BloodPressureRec that = (BloodPressureRec) o;

		if (id != that.id) return false;
		if (idUser != that.idUser) return false;
		if (systolic != that.systolic) return false;
		if (diastolic != that.diastolic) return false;
		if (idTag != that.idTag) return false;
		return idNote == that.idNote;

	}

	@Override
	public String toString() {
		return "BloodPressureRec{" +
				"id=" + id +
				", idUser=" + idUser +
				", systolic=" + systolic +
				", diastolic=" + diastolic +
				", idTag=" + idTag +
				", idNote=" + idNote +
				'}';
	}
}
