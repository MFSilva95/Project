package pt.it.porto.mydiabetes.data;


import android.os.Parcel;
import android.os.Parcelable;

public class Sensitivity implements Parcelable {

    public static final Creator<Sensitivity> CREATOR = new Creator<Sensitivity>() {
        @Override
        public Sensitivity createFromParcel(Parcel in) {
            return new Sensitivity(in);
        }

        @Override
        public Sensitivity[] newArray(int size) {
            return new Sensitivity[size];
        }
    };

    private int id;
    private int user_id;
    //	private int tag_id;
    private double sensitivity;
    private String name;
    private String start;
    private String end;

    public Sensitivity() {
    }

    protected Sensitivity(Parcel in) {
        id = in.readInt();
        user_id = in.readInt();
//		tag_id = in.readInt();
        sensitivity = in.readDouble();
        name = in.readString();
        start = in.readString();
        end = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(user_id);
//		dest.writeInt(tag_id);
        dest.writeDouble(sensitivity);
        dest.writeString(name);
        dest.writeString(start);
        dest.writeString(end);
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int id) {
        this.user_id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//	public int getTagId() {
//		return tag_id;
//	}
//
//	public void setTagId(int id) {
//		this.tag_id = id;
//	}

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Double sensitivity) {
        this.sensitivity = sensitivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sensitivity)) return false;

        Sensitivity that = (Sensitivity) o;

        if (id != that.id) return false;
//		if (tag_id != that.tag_id) return false;
        if (Double.compare(that.sensitivity, sensitivity) != 0) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        return end != null ? end.equals(that.end) : that.end == null;

    }

    @Override
    public String toString() {
        return "SENSE{" +
                "id=" + id +
//				"tag_id="+tag_id+
                ", name='" + name + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", sensitivity=" + sensitivity +
                '}';
    }
}