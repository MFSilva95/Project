package pt.it.porto.mydiabetes.ui.dataBinding;


import android.os.Parcel;
import android.os.Parcelable;

public class DiseaseDataBinding implements Parcelable {

	public static final Creator<DiseaseDataBinding> CREATOR = new Creator<DiseaseDataBinding>() {
		@Override
		public DiseaseDataBinding createFromParcel(Parcel in) {
			return new DiseaseDataBinding(in);
		}

		@Override
		public DiseaseDataBinding[] newArray(int size) {
			return new DiseaseDataBinding[size];
		}
	};
	private String name;
	private int id;

	public DiseaseDataBinding() {
	}

	protected DiseaseDataBinding(Parcel in) {
		name = in.readString();
		id = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(id);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
