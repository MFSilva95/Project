package healthData;

import android.os.Parcel;
import android.os.Parcelable;

public class PM_Store implements Parcelable
{
	private int handler;
	private String systId;

	public static final Parcelable.Creator<PM_Store> CREATOR =
			new Parcelable.Creator<PM_Store>() {
	    public PM_Store createFromParcel(Parcel in) {
	        return new PM_Store(in);
	    }

	    public PM_Store[] newArray(int size) {
	        return new PM_Store[size];
	    }
	};

	private PM_Store (Parcel in) {
		handler = in.readInt();
		systId = in.readString();
	}

	public PM_Store(int pmHandler, String systemId) {
		handler = pmHandler;
		systId = systemId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(handler);
		dest.writeString(systId);
	}

	public int getPM_StoreHandler() {
		return handler;
	}

	public String getPM_StoreAgentId() {
		return systId;
	}
}
