package com.jadg.mydiabetes.ui.views;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

public class GlycemiaObjectivesData implements Serializable, Parcelable {
	public static final int NO_ERROR = 0;
	public static final int ERROR_REPEATED_DESCRIPTION = 1;
	public static final int ERROR_EMPTY_DESCRIPTION = 2;
	public static final int ERROR_EMPTY_OBJECTIVE = 3;
	public static final int ERROR_EMPTY_START_TIME = 4;
	public static final int ERROR_EMPTY_END_TIME = 5;
	public static final Creator<GlycemiaObjectivesData> CREATOR = new Creator<GlycemiaObjectivesData>() {
		@Override
		public GlycemiaObjectivesData createFromParcel(Parcel in) {
			return new GlycemiaObjectivesData(in);
		}

		@Override
		public GlycemiaObjectivesData[] newArray(int size) {
			return new GlycemiaObjectivesData[size];
		}
	};
	String description;
	String startTime;
	String endTime;
	private int objective = -1;
	int visibilityState;
	int error = NO_ERROR;
	boolean[] errors = new boolean[6];
	int pox;


	public GlycemiaObjectivesData(int pox) {
		this.pox = pox;
	}

	protected GlycemiaObjectivesData(Parcel in) {
		description = in.readString();
		startTime = in.readString();
		endTime = in.readString();
		objective = in.readInt();
		visibilityState = in.readInt();
		error = in.readInt();
		pox = in.readInt();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public int getObjective() {
		return objective;
	}

	public String getObjectiveAsString() {
		if (objective == -1) {
			return null;
		} else {
			return String.valueOf(objective);
		}
	}

	public void setObjective(int objective) {
		this.objective = objective;
	}

	public void setObjective(String objective) {
		if (TextUtils.isEmpty(objective)) {
			this.objective = -1;
		} else {
			this.objective = Integer.parseInt(objective, 10);
		}
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(description);
		parcel.writeInt(objective);
	}

	public int getPosition() {
		return pox;
	}

	public void setPosition(int position) {
		this.pox = position;
	}

	public boolean isValid() {
		error = NO_ERROR;
		for (int i = 0; i < errors.length; i++) {
			errors[i] = false;
		}
		if (TextUtils.isEmpty(description)) {
			error = ERROR_EMPTY_DESCRIPTION;
			errors[ERROR_REPEATED_DESCRIPTION] = true;
		}
		if (TextUtils.isEmpty(startTime)) {
			error = ERROR_EMPTY_START_TIME;
			errors[ERROR_EMPTY_START_TIME] = true;
		}
		if (TextUtils.isEmpty(endTime)) {
			error = ERROR_EMPTY_END_TIME;
			errors[ERROR_EMPTY_END_TIME] = true;
		}
		if (objective == -1) {
			error = ERROR_EMPTY_OBJECTIVE;
			errors[ERROR_EMPTY_OBJECTIVE] = true;
		}
		errors[NO_ERROR] = error == NO_ERROR;
		return error == NO_ERROR;
	}

	public void setInvalid() {
		visibilityState = GlycemiaObjetivesElement.MODE_EDIT;
		if (isValid()) {
			// then the problem is a repeated name
			error = ERROR_REPEATED_DESCRIPTION;
			errors[ERROR_REPEATED_DESCRIPTION] = true;
			errors[NO_ERROR] = false;
		}
	}
}