package pt.it.porto.mydiabetes.ui.views;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class GlycemiaObjectivesData implements Serializable, Parcelable {

	public static final int NO_ERROR = 0;
	public static final int ERROR_REPEATED_DESCRIPTION = 1;
	public static final int ERROR_EMPTY_DESCRIPTION = 2;
	public static final int ERROR_EMPTY_OBJECTIVE = 3;
	public static final int ERROR_EMPTY_START_TIME = 4;
	public static final int ERROR_EMPTY_END_TIME = 5;
	public static final int ERROR_START_TIME_OVERLAPS = 6;
	public static final int ERROR_END_TIME_OVERLAPS = 7;
	public static final int ERROR_END_TIME_BEFORE_START_TIME = 8;

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
	boolean[] errors = new boolean[9];
	int pox;
	ArrayList<GlycemiaObjectivesData> otherObjs;

	public GlycemiaObjectivesData(int pox, ArrayList<GlycemiaObjectivesData> otherObj) {
		otherObjs = otherObj;
		this.pox = pox;
	}
	public GlycemiaObjectivesData(int objective, String startTime, String endTime) {
		this.objective = objective;
		this.startTime = startTime;
		this.endTime = endTime;
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

//	public String getEndTime() {
//		return endTime;
//	}

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
		errors = new boolean[9];
		if (TextUtils.isEmpty(description)) {
			error = ERROR_EMPTY_DESCRIPTION;
			errors[ERROR_EMPTY_DESCRIPTION] = true;
		}
		if (TextUtils.isEmpty(startTime)) {
			error = ERROR_EMPTY_START_TIME;
			errors[ERROR_EMPTY_START_TIME] = true;
		}
//		if (TextUtils.isEmpty(endTime)) {
//			error = ERROR_EMPTY_END_TIME;
//			errors[ERROR_EMPTY_END_TIME] = true;
//		}
//		if(errors[ERROR_EMPTY_START_TIME] == false && errors[ERROR_EMPTY_END_TIME] == false){
//			int Oerror = GlicObjTimesOverlap(startTime,endTime);
//			if(Oerror != NO_ERROR){
//				error = Oerror;
//				errors[Oerror] = true;
//			}
//		}
		if (objective == -1) {
			error = ERROR_EMPTY_OBJECTIVE;
			errors[ERROR_EMPTY_OBJECTIVE] = true;
		}
		errors[NO_ERROR] = error == NO_ERROR;
		return error == NO_ERROR;
	}


//	public int GlicObjTimesOverlap(String st,String et){
//
//		String[] temp;
//
//		temp = st.split(":");
//		int startTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
//		temp = et.split(":");
//		int endTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
//
//		for(int y=0;y<otherObjs.size();y++){
//
//			if(otherObjs.get(y)!=this){
//				temp = otherObjs.get(y).getStartTime().split(":");
//				int startTime2 = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
//				temp = otherObjs.get(y).getEndTime().split(":");
//				int endTime2 = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
//				int error = CheckOverlap(startTime, startTime2, endTime, endTime2);
//				if(error != NO_ERROR){return error;}}
//		}
//		return NO_ERROR;
//	}

//	public int getDuration(int start, int end){
//		if (start>end){return start-end;}
//		return end-start;
//	}

//	public int CheckOverlap(int s0, int s1, int e0, int e1){
//		int d0 = getDuration(s0,e0);
//		int d1 = getDuration(s1,e1);
//
//		if (s0 <= s1 && s0 + d0 >= s1) {
//			// startTime inside a previews interval
//			return ERROR_START_TIME_OVERLAPS;
//		} else if (s0 <= e1 && s0 + d0 >= e1) {
//			// endTime inside a interval
//			return ERROR_END_TIME_OVERLAPS;
//		} else if (d1 <= 0 && s0 < e1) {
//			// endTime in the next day
//			// compares if endTime will be after a startTime of other interval
//			// if true than it should fail
//			return ERROR_START_TIME_OVERLAPS;
//		}
//		return NO_ERROR;
//	}


	public void setInvalid(int reason) {
		visibilityState = GlycemiaObjetivesElement.MODE_EDIT;
		// then the problem is a repeated name
		error = reason;
		errors[reason] = true;
		errors[NO_ERROR] = false;
	}
}