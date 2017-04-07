package pt.it.porto.mydiabetes.utils;

import android.content.Context;
import android.util.Log;

import com.github.pierry.simpletoast.SimpleToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.Disease;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.HbA1cRec;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;

/**
 * Created by parra on 02/04/2017.
 */

public class BadgeUtils {
    static final private int LOG_BRONZE_RECORDS = 3;
    static final private int LOG_SILVER_RECORDS = 10;
    static final private int LOG_GOLD_RECORDS = 20;

    static final private int EXERCISE_BRONZE_RECORDS = 3;
    static final private int EXERCISE_SILVER_RECORDS = 10;
    static final private int EXERCISE_GOLD_RECORDS = 20;

    static final private int DISEASE_BRONZE_RECORDS = 1;
    static final private int DISEASE_SILVER_RECORDS = 2;
    static final private int DISEASE_GOLD_RECORDS = 3;

    static final private int WEIGHT_BRONZE_RECORDS = 3;
    static final private int WEIGHT_SILVER_RECORDS = 10;
    static final private int WEIGHT_GOLD_RECORDS = 20;

    static final private int BP_BRONZE_RECORDS = 1;
    static final private int BP_SILVER_RECORDS = 3;
    static final private int BP_GOLD_RECORDS = 5;

    static final private int CHOLESTEROL_BRONZE_RECORDS = 1;
    static final private int CHOLESTEROL_SILVER_RECORDS = 2;
    static final private int CHOLESTEROL_GOLD_RECORDS = 3;

    static final private int HBA1C_BRONZE_RECORDS = 1;
    static final private int HBA1C_SILVER_RECORDS = 2;
    static final private int HBA1C_GOLD_RECORDS = 3;

    static final private int DAILY_BRONZE_RECORDS = 2;
    static final private int DAILY_SILVER_RECORDS = 4;
    static final private int DAILY_GOLD_RECORDS = 6;


    public static void addPhotoBadge(Context context) {
        DB_Read db = new DB_Read(context);
        int idUser = db.getId();
        LinkedList<BadgeRec> list = db.Badges_GetAll();
        db.close();
        boolean flag = false;
        for (BadgeRec rec : list) {
            if (rec.getName().equals("photo")) {
                flag = true;
            }
        }
        if (!flag) {
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("photo");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
    }

    public static void addExportBadge(Context context) {
        DB_Read db = new DB_Read(context);
        int idUser = db.getId();
        LinkedList<BadgeRec> list = db.Badges_GetAll();
        db.close();
        boolean flag = false;
        for (BadgeRec rec : list) {
            if (rec.getName().equals("export")) {
                flag = true;
            }
        }
        if (!flag) {
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("export");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
    }

    public static void addLogBadge(Context context) {
        DB_Read db = new DB_Read(context);
        int idUser = db.getId();
        LinkedList<BadgeRec> list = db.Badges_GetAll();

        boolean flagBronze = false;
        boolean flagSilver = false;
        boolean flagGold = false;
        for (BadgeRec rec : list) {
            if (rec.getName().equals("log")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronze = true;
                if(rec.getMedal().equals("silver"))
                    flagSilver = true;
                if(rec.getMedal().equals("gold"))
                    flagGold = true;
            }
        }
        LinkedList<HomeElement> logBookEntries = db.getLogBookByLimit(LOG_GOLD_RECORDS);
        db.close();

        if(logBookEntries.size()>=LOG_BRONZE_RECORDS && !flagBronze){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("log");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(logBookEntries.size()>=LOG_SILVER_RECORDS && !flagSilver){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("log");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(logBookEntries.size()>=LOG_GOLD_RECORDS && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("log");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }

    }

    public static void addExerciseBadge(Context context) {
        DB_Read db = new DB_Read(context);
        LinkedList<BadgeRec> list = db.Badges_GetAll();
        int idUser = db.getId();

        boolean flagBronze = false;
        boolean flagSilver = false;
        boolean flagGold = false;
        for (BadgeRec rec : list) {
            if (rec.getName().equals("exercise")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronze = true;
                if(rec.getMedal().equals("silver"))
                    flagSilver = true;
                if(rec.getMedal().equals("gold"))
                    flagGold = true;
            }
        }
        HashMap<Integer, String> exerciseEntries = db.Exercise_GetAll();
        db.close();

        if(exerciseEntries.size()>=EXERCISE_BRONZE_RECORDS && !flagBronze){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("exercise");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(exerciseEntries.size()>=EXERCISE_SILVER_RECORDS && !flagSilver){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("exercise");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(exerciseEntries.size()>=EXERCISE_GOLD_RECORDS && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("exercise");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }

    }

    public static void addDiseaseBadge(Context context) {
        DB_Read db = new DB_Read(context);
        int idUser = db.getId();
        LinkedList<BadgeRec> list = db.Badges_GetAll();

        boolean flagBronze = false;
        boolean flagSilver = false;
        boolean flagGold = false;
        for (BadgeRec rec : list) {
            if (rec.getName().equals("disease")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronze = true;
                if(rec.getMedal().equals("silver"))
                    flagSilver = true;
                if(rec.getMedal().equals("gold"))
                    flagGold = true;
            }
        }
        ArrayList<Disease> diseaseEntries = db.Disease_GetAll();
        db.close();

        if(diseaseEntries.size()>=DISEASE_BRONZE_RECORDS && !flagBronze){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("disease");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(diseaseEntries.size()>=DISEASE_SILVER_RECORDS && !flagSilver){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("disease");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(diseaseEntries.size()>=DISEASE_GOLD_RECORDS && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("disease");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }

    }

    public static void addWeightBadge(Context context) {
        DB_Read db = new DB_Read(context);
        LinkedList<BadgeRec> list = db.Badges_GetAll();
        int idUser = db.getId();
        boolean flagBronze = false;
        boolean flagSilver = false;
        boolean flagGold = false;
        for (BadgeRec rec : list) {
            if (rec.getName().equals("weight")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronze = true;
                if(rec.getMedal().equals("silver"))
                    flagSilver = true;
                if(rec.getMedal().equals("gold"))
                    flagGold = true;
            }
        }
        ArrayList<WeightRec> weightEntries = db.Weight_GetAll();
        db.close();

        if(weightEntries.size()>=WEIGHT_BRONZE_RECORDS && !flagBronze){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("weight");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(weightEntries.size()>=WEIGHT_SILVER_RECORDS && !flagSilver){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("weight");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(weightEntries.size()>=WEIGHT_GOLD_RECORDS && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("weight");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }

    }

    public static void addBpBadge(Context context) {
        DB_Read db = new DB_Read(context);
        LinkedList<BadgeRec> list = db.Badges_GetAll();
        int idUser = db.getId();
        boolean flagBronze = false;
        boolean flagSilver = false;
        boolean flagGold = false;
        for (BadgeRec rec : list) {
            if (rec.getName().equals("bp")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronze = true;
                if(rec.getMedal().equals("silver"))
                    flagSilver = true;
                if(rec.getMedal().equals("gold"))
                    flagGold = true;
            }
        }
        ArrayList<BloodPressureRec> bpEntries = db.BloodPressure_GetAll();
        db.close();

        if(bpEntries.size()>=BP_BRONZE_RECORDS && !flagBronze){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("bp");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(bpEntries.size()>=BP_SILVER_RECORDS && !flagSilver){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("bp");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(bpEntries.size()>=BP_GOLD_RECORDS && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("bp");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
    }

    public static void addCholesterolBadge(Context context) {
        DB_Read db = new DB_Read(context);
        LinkedList<BadgeRec> list = db.Badges_GetAll();
        int idUser = db.getId();
        boolean flagBronze = false;
        boolean flagSilver = false;
        boolean flagGold = false;
        for (BadgeRec rec : list) {
            if (rec.getName().equals("cholesterol")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronze = true;
                if(rec.getMedal().equals("silver"))
                    flagSilver = true;
                if(rec.getMedal().equals("gold"))
                    flagGold = true;
            }
        }
        ArrayList<CholesterolRec> cholesterolEntries = db.Cholesterol_GetAll();
        db.close();

        if(cholesterolEntries.size()>=CHOLESTEROL_BRONZE_RECORDS && !flagBronze){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("cholesterol");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(cholesterolEntries.size()>=CHOLESTEROL_SILVER_RECORDS && !flagSilver){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("cholesterol");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(cholesterolEntries.size()>=CHOLESTEROL_GOLD_RECORDS && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("cholesterol");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
    }

    public static void addHba1cBadge(Context context) {
        DB_Read db = new DB_Read(context);
        LinkedList<BadgeRec> list = db.Badges_GetAll();
        int idUser = db.getId();
        boolean flagBronze = false;
        boolean flagSilver = false;
        boolean flagGold = false;
        for (BadgeRec rec : list) {
            if (rec.getName().equals("hba1c")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronze = true;
                if(rec.getMedal().equals("silver"))
                    flagSilver = true;
                if(rec.getMedal().equals("gold"))
                    flagGold = true;
            }
        }
        ArrayList<HbA1cRec> hba1cEntries = db.HbA1c_GetAll();
        db.close();

        if(hba1cEntries.size()>=HBA1C_BRONZE_RECORDS && !flagBronze){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("hba1c");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(hba1cEntries.size()>=HBA1C_SILVER_RECORDS && !flagSilver){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("hba1c");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
        if(hba1cEntries.size()>=HBA1C_GOLD_RECORDS && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("hba1c");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha", "{fa-trophy}");
        }
    }

    public static void addDailyBadge(Context context) {
        DB_Read db = new DB_Read(context);
        LinkedList<BadgeRec> list = db.getBadgesByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        int idUser = db.getId();
        boolean flagBronze = false;
        int idBronze = 0;
        boolean flagSilver = false;
        int idSilver = 0;
        boolean flagGold = false;
        for (BadgeRec rec : list) {
            if (rec.getType().equals("daily")) {
                if(rec.getMedal().equals("bronze")){
                    flagBronze = true;
                    idBronze = rec.getId();
                }
                if(rec.getMedal().equals("silver")){
                    flagSilver = true;
                    idSilver = rec.getId();
                }
                if(rec.getMedal().equals("gold")){
                    flagGold = true;
                }
            }
        }
        LinkedList<ExerciseRec> exerciseList = db.getExerciceByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        LinkedList<BloodPressureRec> bpList = db.getBloodPressureByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        LinkedList<CholesterolRec> cholesterolList = db.getCholesterolByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        LinkedList<WeightRec> weightList = db.getWeightByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        LinkedList<HbA1cRec> hbA1cList = db.getHbA1cByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        LinkedList<DiseaseRec> diseaseList = db.getDiseaseByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        LinkedList<HomeElement> logList = db.getLogBookByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        db.close();

        int size = exerciseList.size() + bpList.size() + cholesterolList.size() + weightList.size() + hbA1cList.size() + diseaseList.size() + logList.size();
        Log.e("EXERCICE", exerciseList.size()+"");
        Log.e("DISEASE", diseaseList.size()+"");
        Log.e("WEIGHT", weightList.size()+"");
        Log.e("BP", bpList.size()+"");
        Log.e("HBA1C", hbA1cList.size()+"");
        Log.e("CHOLESTEROL", cholesterolList.size()+"");
        Log.e("LOG", logList.size()+"");
        Log.e("SIZE", size+"");

        if(size >= DAILY_BRONZE_RECORDS && !flagBronze && !flagSilver && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("daily");
            badge.setName("all");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha diária", "{fa-trophy}");
        }
        if(size >= DAILY_SILVER_RECORDS && !flagSilver && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("daily");
            badge.setName("all");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            dbwrite.Badge_Remove(idBronze);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha diária", "{fa-trophy}");
        }
        if(size >= DAILY_GOLD_RECORDS && !flagGold){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("daily");
            badge.setName("all");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            dbwrite.Badge_Remove(idSilver);
            dbwrite.close();
            SimpleToast.info(context, "Recebeu uma medalha diária", "{fa-trophy}");
        }
    }



}
