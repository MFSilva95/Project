package pt.it.porto.mydiabetes.ui.createMeal.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class LoggedMeal implements Parcelable {
    private String name;
    private int extra_carbs;
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

    public int getExtraCarbs() {
        return extra_carbs;
    }

    public void setExtraCarbs(int extra_carbs) {
        this.extra_carbs = extra_carbs;
    }

    public List<MealItem> getItemList(){
        return itemList;
    }

    public int getTotalCarbs(boolean with_extra) {
        float total_carbs = 0;

        for(MealItem m : itemList)
            total_carbs = total_carbs + m.getCarbs();

        if(with_extra)
            return Math.round(total_carbs + extra_carbs);
        else
            return Math.round(total_carbs);

    }

    public int getTotalLipids() {
        float total_lipids = 0;

        for(MealItem m : itemList){
            total_lipids = total_lipids + m.getLipids();
        }
        return Math.round(total_lipids);
    }

    public int getTotalProtein() {
        float total_protein = 0;

        for(MealItem m : itemList) {
            total_protein = total_protein + m.getProtein();
        }

        return Math.round(total_protein);
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
        dest.writeInt(this.extra_carbs);
        dest.writeString(this.timestamp);
        dest.writeTypedList(this.itemList);
        dest.writeString(this.thumbnailPath);
        dest.writeByte(this.favourite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.registered ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
    }

    protected LoggedMeal(Parcel in) {
        this.name = in.readString();
        this.extra_carbs = in.readInt();
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
