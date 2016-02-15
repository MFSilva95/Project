package pt.it.porto.mydiabetes.ui.dataBinding;

import android.os.Parcel;
import android.os.Parcelable;

public class LogbookDataBinding implements Parcelable {

	public static final Creator<LogbookDataBinding> CREATOR = new Creator<LogbookDataBinding>() {
		@Override
		public LogbookDataBinding createFromParcel(Parcel in) {
			return new LogbookDataBinding(in);
		}

		@Override
		public LogbookDataBinding[] newArray(int size) {
			return new LogbookDataBinding[size];
		}
	};
	private CarbsDataBinding ch;
	private InsulinRegDataBinding ins;
	private GlycemiaDataBinding bg;

	public LogbookDataBinding() {
	}

	protected LogbookDataBinding(Parcel in) {
		ch = in.readParcelable(CarbsDataBinding.class.getClassLoader());
		ins = in.readParcelable(InsulinRegDataBinding.class.getClassLoader());
		bg = in.readParcelable(GlycemiaDataBinding.class.getClassLoader());
	}

	public CarbsDataBinding get_ch() {
		return ch;
	}

	public void set_ch(CarbsDataBinding ch) {
		this.ch = ch;
	}

	public InsulinRegDataBinding get_ins() {
		return ins;
	}

	public void set_ins(InsulinRegDataBinding ins) {
		this.ins = ins;
	}

	public GlycemiaDataBinding get_bg() {
		return bg;
	}

	public void set_bg(GlycemiaDataBinding bg) {
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
