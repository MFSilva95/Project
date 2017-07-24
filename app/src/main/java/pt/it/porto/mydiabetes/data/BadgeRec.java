package pt.it.porto.mydiabetes.data;


import android.annotation.SuppressLint;

@SuppressLint("ParcelCreator")
public class BadgeRec extends DateTime {

    private int id;
    private int idUser;

    private String textID;
    private String imgID;

    private String type;
    private String name;
    private String medal;



    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedal() {
        return medal;
    }

    public void setMedal(String medal) {
        this.medal = medal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BloodPressureRec{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", type=" + type +
                ", name=" + name +
                ", medal=" + medal +
                '}';
    }

}
