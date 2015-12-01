package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AndroidMeasure implements Parcelable{
	
	private int measureId;
	private String measureName;
	private int unitId;
	private String unitName;
	private long timestamp;
	private ArrayList<Double> values;
	private ArrayList<Integer> metricIds;
	private ArrayList<String> metricNames;
	
	
	public static final Creator<AndroidMeasure> CREATOR =
			new Creator<AndroidMeasure>() {
	    public AndroidMeasure createFromParcel(Parcel in) {
	        return new AndroidMeasure(in);
	    }

	    public AndroidMeasure[] newArray(int size) {
	        return new AndroidMeasure[size];
	    }
	};
	
	@SuppressWarnings("unchecked")
	private AndroidMeasure (Parcel in) {
		measureId = in.readInt();
		measureName = in.readString();
		unitId = in.readInt();
		unitName = in.readString();
		timestamp = in.readLong();
		values = in.readArrayList(Double.class.getClassLoader());
		metricIds = in.readArrayList(Integer.class.getClassLoader());
		metricNames = in.readArrayList(String.class.getClassLoader());
		
	}

	
	
	public AndroidMeasure(int measureId , String measureName , int unitId , String unitName , long timestamp , ArrayList<Double> values , ArrayList<Integer> metricIds , ArrayList<String> metricNames){
		this.measureId = measureId;
		this.measureName = measureName;
		this.unitId = unitId;
		this.unitName = unitName;
		this.timestamp = timestamp;
		this.values = values;
		this.metricIds = metricIds;
		this.metricNames = metricNames;
	}



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(measureId);
		dest.writeString(measureName);
		dest.writeInt(unitId);
		dest.writeString(unitName);
		dest.writeLong(timestamp);
		dest.writeList(values);
		dest.writeList(metricIds);
		dest.writeList(metricNames);
		
	}
	
	public int getMeasureId() {
		return measureId;
	}



	public String getMeasureName() {
		return measureName;
	}



	public int getUnitId() {
		return unitId;
	}



	public String getUnitName() {
		return unitName;
	}



	public long getTimestamp() {
		return timestamp;
	}



	public ArrayList<Double> getValues() {
		return values;
	}



	public ArrayList<Integer> getMetricIds() {
		return metricIds;
	}



	public ArrayList<String> getMetricNames() {
		return metricNames;
	}

	
	
}
