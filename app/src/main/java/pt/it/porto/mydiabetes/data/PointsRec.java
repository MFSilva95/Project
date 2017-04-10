package pt.it.porto.mydiabetes.data;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class PointsRec extends DateTime {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private int idUser;
    private int value;
    private String origin;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }



    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }


    @Override
    public String toString() {
        return "BloodPressureRec{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", points=" + value +
                ", origin=" + origin +
                '}';
    }

}
