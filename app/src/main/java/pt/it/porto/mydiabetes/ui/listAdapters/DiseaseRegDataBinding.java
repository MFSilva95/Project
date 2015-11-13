package pt.it.porto.mydiabetes.ui.listAdapters;


public class DiseaseRegDataBinding {

	private int id;
	private int idUser;
	private String disease;
	private String startdate;
	private String enddate;
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
	
}
