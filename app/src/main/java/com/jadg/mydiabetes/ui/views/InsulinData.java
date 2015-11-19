package com.jadg.mydiabetes.ui.views;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.jadg.mydiabetes.R;

import java.io.Serializable;

public class InsulinData implements Serializable, Parcelable {
	public static final int NO_ERROR = 0;
	public static final int ERROR_REPEATED_NAME = 1;
	public static final int ERROR_EMPTY_NAME = 2;
	public static final int ERROR_EMPTY_ADMINISTRATION_METHOD = 3;

	String name;
	String administrationMethod;
	int action;
	int visibilityState;
	int error = NO_ERROR;
	int pox;


	public InsulinData(int pox, Context context) {
		this.pox = pox;
		this.name = context.getResources().getString(R.string.insulin) + " " + String.valueOf(pox + 1);
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

	public boolean isValid() {
		error = NO_ERROR;
		if (TextUtils.isEmpty(name)) {
			error = ERROR_EMPTY_NAME;
			return false;
		}
		if (TextUtils.isEmpty(administrationMethod)) {
			error = ERROR_EMPTY_ADMINISTRATION_METHOD;
			return false;
		}
		return true;
	}

	public void setInvalid() {
		visibilityState = InsulinElement.MODE_EDIT;
		if (isValid()) {
			// then the problem is a repeated name
			error = ERROR_REPEATED_NAME;
		}
	}
}