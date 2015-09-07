package healthData;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AndroidHealthDevice implements Parcelable
{
	private String powerStatus;
	private int batteryLevel;
	private String macAddress;
	private String systId;
	private String manufacturer;
	private String modelNumber;
	private boolean is11073;
	private ArrayList<Integer> systemTypeIds;
	private ArrayList<String> systemTypes;

	public static final Parcelable.Creator<AndroidHealthDevice> CREATOR = new Parcelable.Creator<AndroidHealthDevice>()
	{
	    public AndroidHealthDevice createFromParcel(Parcel in) {
	        return new AndroidHealthDevice(in);
	    }

	    public AndroidHealthDevice[] newArray(int size) {
	        return new AndroidHealthDevice[size];
	    }
	};

	@SuppressWarnings("unchecked")
	private AndroidHealthDevice(Parcel in)
	{
		powerStatus = in.readString();
		batteryLevel = in.readInt();
		macAddress = in.readString();
		systId = in.readString();
		manufacturer = in.readString();
		modelNumber = in.readString();
		is11073 = (Boolean) in.readValue(null);
		systemTypeIds = in.readArrayList(Integer.class.getClassLoader());
		systemTypes = in.readArrayList(String.class.getClassLoader());
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(powerStatus);
		dest.writeInt(batteryLevel);
		dest.writeString(macAddress);
		dest.writeString(systId);
		dest.writeString(manufacturer);
		dest.writeString(modelNumber);
		dest.writeValue(is11073);
		dest.writeList(systemTypeIds);
		dest.writeList(systemTypes);
	}

	public AndroidHealthDevice(String powerStatus, int batteryLevel, String mac, String systId, String manufacturer, String modelNumber, boolean is11073, ArrayList<Integer> systemTypeIds, ArrayList<String> systemTypes)
	{
		this.powerStatus = powerStatus;
		this.batteryLevel = batteryLevel;
		this.macAddress = mac;
		this.systId = systId;
		this.manufacturer = manufacturer;
		this.modelNumber = modelNumber;
		this.is11073 = is11073;
		this.systemTypeIds = systemTypeIds;
		this.systemTypes = systemTypes;
	}
	
	public String getPowerStatus() {return this.powerStatus;}
	public int getBatteryLever() {return this.batteryLevel;}
	public String getMacAddress () {return this.macAddress;}
	public String getSystemId () {return this.systId;}
	public String getManufacturer () {return this.manufacturer;}
	public String getModelNumber () {return this.modelNumber;}
	public boolean is11073 (){return this.is11073;}
	public ArrayList<Integer> getSystemTypeIds () {return this.systemTypeIds;}
	public ArrayList<String> getSystemTypes () {return this.systemTypes;}
}
