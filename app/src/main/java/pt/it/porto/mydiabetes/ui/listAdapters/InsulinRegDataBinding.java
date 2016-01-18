package pt.it.porto.mydiabetes.ui.listAdapters;


public class InsulinRegDataBinding {

	private int id;
	private int idUser;
	private int idBloodGlucose=-1;
	private int idInsulin;
	private String date;
	private String time;
	private Double targetGlycemia;
	private Double insulinUnits;
	private int idTag;
	private int idNote;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Double getTargetGlycemia() {
		return targetGlycemia;
	}
	public void setTargetGlycemia(Double targetGlycemia) {
		this.targetGlycemia = targetGlycemia;
	}
	public Double getInsulinUnits() {
		return insulinUnits;
	}
	public void setInsulinUnits(Double insulinUnits) {
		this.insulinUnits = insulinUnits;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public int getIdBloodGlucose() {
		return idBloodGlucose;
	}
	public void setIdBloodGlucose(int idBloodGlucose) {
		this.idBloodGlucose = idBloodGlucose;
	}
	public int getIdInsulin() {
		return idInsulin;
	}
	public void setIdInsulin(int idInsulin) {
		this.idInsulin = idInsulin;
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
