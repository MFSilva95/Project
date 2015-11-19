package com.jadg.mydiabetes.ui.views;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.jadg.mydiabetes.R;

import java.io.Serializable;

public class InsulinData implements Serializable, Parcelable {
	String name;
	String administrationMethod;
	int action;
	int visibilityState;
	int pox;

	public InsulinData(int pox, Context context) {
		this.pox = pox;
		this.name = context.getResources().getString(R.string.insulin) + " " + String.valueOf(pox+1);
	}

	protected InsulinData(Parcel in) {
		name = in.readString();
		administrationMethod = in.readString();
		action = in.readInt();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdministrationMethod() {
		return administrationMethod;
	}

	public void setAdministrationMethod(String administrationMethod) {
		this.administrationMethod = administrationMethod;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public static final Creator<InsulinData> CREATOR = new Creator<InsulinData>() {
		@Override
		public InsulinData createFromParcel(Parcel in) {
			return new InsulinData(in);
		}

		@Override
		public InsulinData[] newArray(int size) {
			return new InsulinData[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(name);
		parcel.writeString(administrationMethod);
		parcel.writeInt(action);
	}

	public int getPosition() {
		return pox;
	}

	public void setPosition(int position) {
		this.pox = position;
	}
}