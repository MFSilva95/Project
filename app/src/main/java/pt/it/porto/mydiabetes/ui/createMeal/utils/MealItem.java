package pt.it.porto.mydiabetes.ui.createMeal.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class MealItem implements Parcelable {
    private String name;
    private float carbs;
    private int id;
    private int quantity = 100;

    public MealItem(int id, String name, float carbs){
        this.id = id;
        this.name = name;
        this.carbs = carbs;
    }

    public String getName() {
        return name;
    }

    public float getCarbs() {
        float portions = (float) quantity / 100;
        return portions * carbs;
    }

    public int getId(){ return id; }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeFloat(this.carbs);
        dest.writeInt(this.id);
        dest.writeInt(this.quantity);
    }

    protected MealItem(Parcel in) {
        this.name = in.readString();
        this.carbs = in.readFloat();
        this.id = in.readInt();
        this.quantity = in.readInt();
    }

    public static final Parcelable.Creator<MealItem> CREATOR = new Parcelable.Creator<MealItem>() {
        @Override
        public MealItem createFromParcel(Parcel source) {
            return new MealItem(source);
        }

        @Override
        public MealItem[] newArray(int size) {
            return new MealItem[size];
        }
    };
}
