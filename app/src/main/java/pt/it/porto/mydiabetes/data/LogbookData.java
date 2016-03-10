package pt.it.porto.mydiabetes.data;

import android.os.Parcel;
import android.os.Parcelable;

public class LogbookData implements Parcelable {

	public static final Creator<LogbookData> CREATOR = new Creator<LogbookData>() {
		@Override
		public LogbookData createFromParcel(Parcel in) {
			return new LogbookData(in);
		}

		@Override
		public LogbookData[] newArray(int size) {
			return new LogbookData[size];
		}
	};
	private CarbsRec ch;
	private InsulinRec ins;
	private GlycemiaRec bg;

	public LogbookData() {
	}

	protected LogbookData(Parcel in) {
		ch = in.readParcelable(CarbsRec.class.getClassLoader());
		ins = in.readParcelable(InsulinRec.class.getClassLoader());
		bg = in.readParcelable(GlycemiaRec.class.getClassLoader());
	}

	public CarbsRec getCarbsReg() {
		return ch;
	}

	public void setCarbsReg(CarbsRec ch) {
		this.ch = ch;
	}

	public InsulinRec getInsulinReg() {
		return ins;
	}

	public void setInsulinReg(InsulinRec ins) {
		this.ins = ins;
	}

	public GlycemiaRec getGlycemiaReg() {
		return bg;
	}

	public void setGlycemiaReg(GlycemiaRec bg) {
		this.bg = bg;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(ch, flags);
		dest.writeParcelable(ins, flags);
		dest.writeParcelable(bg, flags);
	}
}
