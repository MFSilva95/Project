package pt.it.porto.mydiabetes.ui.createMeal.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class LoggedMeal implements Parcelable {
    private String name;
    private String timestamp;
    private List<MealItem> itemList;
    private String thumbnailPath;
    private boolean favourite;
    private boolean registered;
    private int id;


    public LoggedMeal(List<MealItem> itemList){
        this.itemList = itemList;
        this.id = -1;
    }

    public List<MealItem> getItemList(){
        return itemList;
    }

    public float getTotalCarbs() {
        float total_carbs = 0;

        for(MealItem m : itemList)
            total_carbs = total_carbs + m.getCarbs();

        return total_carbs;
    }

    public String getThumbnailPath(){
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavourite(){
        return favourite;
    }

    public void setFavourite(boolean favourite){
        this.favourite = favourite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoggedMeal that = (LoggedMeal) o;
        return id == that.id;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.timestamp);
        dest.writeTypedList(this.itemList);
        dest.writeString(this.thumbnailPath);
        dest.writeByte(this.favourite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.registered ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
    }

    protected LoggedMeal(Parcel in) {
        this.name = in.readString();
        this.timestamp = in.readString();
        this.itemList = in.createTypedArrayList(MealItem.CREATOR);
        this.thumbnailPath = in.readString();
        this.favourite = in.readByte() != 0;
        this.registered = in.readByte() != 0;
        this.id = in.readInt();
    }

    public static final Creator<LoggedMeal> CREATOR = new Creator<LoggedMeal>() {
        @Override
        public LoggedMeal createFromParcel(Parcel source) {
            return new LoggedMeal(source);
        }

        @Override
        public LoggedMeal[] newArray(int size) {
            return new LoggedMeal[size];
        }
    };
}
