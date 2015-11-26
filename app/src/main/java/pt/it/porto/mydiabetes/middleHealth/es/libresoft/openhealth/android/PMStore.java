package pt.it.porto.mydiabetes.middleHealth.es.libresoft.openhealth.android;

import android.os.Parcel;
import android.os.Parcelable;

public class PMStore implements Parcelable {
	private int handler;
	private String systId;

	public static final Creator<PMStore> CREATOR =
			new Creator<PMStore>() {
	    public PMStore createFromParcel(Parcel in) {
	        return new PMStore(in);
	    }

	    public PMStore[] newArray(int size) {
	        return new PMStore[size];
	    }
	};

	private PMStore(Parcel in) {
		handler = in.readInt();
		systId = in.readString();
	}

	public PMStore(int pmHandler, String systemId) {
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

	public int getPMStoreHandler() {
		return handler;
	}

	public String getPMStoreAgentId() {
		return systId;
	}
}
