package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth;

import java.util.ArrayList;

public class HealthDevice {
	

	private String powerStatus;
	private int batteryLevel;
	private String systId;
	private String manufacturer;
	private String model;
	private String address;
	private boolean is11073;
	private ArrayList<Integer> systemTypeIds;
	private ArrayList<String>  systemTypes;
	
	
	public HealthDevice(){
		powerStatus = "Unknown";
		batteryLevel = -1;
		systId = "Unknown";
		manufacturer = "Unknown";
		model = "Unknown";
		address = "Unknown";
		is11073 = false;
		systemTypeIds = new ArrayList<Integer>();
		systemTypes   = new ArrayList<String>();
		
		
	}
	
	
	public void addTypeID(int typeId){
		systemTypeIds.add(typeId);
	}
	public void addType(String type){
		systemTypes.add(type);
	}
	public ArrayList<Integer> getSystemTypeIds(){
		return systemTypeIds;
	}
	public ArrayList<String> getSystemTypes(){
		return systemTypes;
	}
	
	public String getPowerStatus() {
		return powerStatus;
	}


	public void setPowerStatus(String powerStatus) {
		this.powerStatus = powerStatus;
	}


	public int getBatteryLevel() {
		return batteryLevel;
	}


	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}


	public String getSystId() {
		return systId;
	}


	public void setSystId(String systId) {
		this.systId = systId;
	}


	public String getManufacturer() {
		return manufacturer;
	}


	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}


	public String getModel() {
		return model;
	}


	public void setModel(String model) {
		this.model = model;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}

	public boolean is11073() {
		return is11073;
	}

	public void set11073(boolean is11073) {
		this.is11073 = is11073;
	}

	
	
}
