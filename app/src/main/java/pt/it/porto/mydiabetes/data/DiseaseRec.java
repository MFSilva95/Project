package pt.it.porto.mydiabetes.data;


public class DiseaseRec {

	private int id;
	private int idUser;
	private String disease;
	private String startdate;
	private String enddate;
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

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getStartDate() {
		return startdate;
	}

	public void setStartDate(String startdate) {
		this.startdate = startdate;
	}

	public String getEndDate() {
		return enddate;
	}

	public void setEndDate(String enddate) {
		this.enddate = enddate;
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
		if (!(o instanceof DiseaseRec)) return false;

		DiseaseRec that = (DiseaseRec) o;

		if (id != that.id) return false;
		if (idUser != that.idUser) return false;
		if (idNote != that.idNote) return false;
		if (disease != null ? !disease.equals(that.disease) : that.disease != null) return false;
		if (startdate != null ? !startdate.equals(that.startdate) : that.startdate != null) return false;
		return enddate != null ? enddate.equals(that.enddate) : that.enddate == null;

	}

	@Override
	public String toString() {
		return "DiseaseRec{" +
				"id=" + id +
				", idUser=" + idUser +
				", disease='" + disease + '\'' +
				", startdate='" + startdate + '\'' +
				", enddate='" + enddate + '\'' +
				", idNote=" + idNote +
				'}';
	}
}
