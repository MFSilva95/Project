package pt.it.porto.mydiabetes.ui.listAdapters;


import android.os.Parcel;
import android.os.Parcelable;

public class ExerciseDataBinding implements Parcelable {

	public static final Creator<ExerciseDataBinding> CREATOR = new Creator<ExerciseDataBinding>() {
		@Override
		public ExerciseDataBinding createFromParcel(Parcel in) {
			return new ExerciseDataBinding(in);
		}

		@Override
		public ExerciseDataBinding[] newArray(int size) {
			return new ExerciseDataBinding[size];
		}
	};
	private String name;
	private int id;

	public ExerciseDataBinding() {
	}

	protected ExerciseDataBinding(Parcel in) {
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
