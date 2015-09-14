package com.jadg.mydiabetes.database;


public class BloodPressureDataBinding {

	private int id;
	private int idUser;
	private int systolic;
	private int diastolic;
	private String date;
	private String time;
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
