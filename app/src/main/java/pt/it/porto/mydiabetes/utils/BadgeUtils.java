package pt.it.porto.mydiabetes.utils;

import android.content.Context;
import android.util.Log;

import com.github.pierry.simpletoast.SimpleToast;

import java.util.Calendar;
import java.util.LinkedList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.HbA1cRec;
import pt.it.porto.mydiabetes.data.PointsRec;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;

/**
 * Created by parra on 02/04/2017.
 */

public class BadgeUtils {
    static final private int LOG_BRONZE_RECORDS_B = 10;
    static final private int LOG_SILVER_RECORDS_B = 20;
    static final private int LOG_GOLD_RECORDS_B = 30;

    static final private int LOG_BRONZE_RECORDS_M = 50;
    static final private int LOG_SILVER_RECORDS_M = 100;
    static final private int LOG_GOLD_RECORDS_M = 150;

    static final private int LOG_BRONZE_RECORDS_A = 200;
    static final private int LOG_SILVER_RECORDS_A = 250;
    static final private int LOG_GOLD_RECORDS_A = 300;

    static final private int EXERCISE_BRONZE_RECORDS_B = 3;
    static final private int EXERCISE_SILVER_RECORDS_B = 10;
    static final private int EXERCISE_GOLD_RECORDS_B = 20;

    static final private int EXERCISE_BRONZE_RECORDS_M = 30;
    static final private int EXERCISE_SILVER_RECORDS_M = 50;
    static final private int EXERCISE_GOLD_RECORDS_M = 70;

    static final private int EXERCISE_BRONZE_RECORDS_A = 90;
    static final private int EXERCISE_SILVER_RECORDS_A = 120;
    static final private int EXERCISE_GOLD_RECORDS_A = 150;

    static final private int DISEASE_BRONZE_RECORDS_B = 1;
    static final private int DISEASE_SILVER_RECORDS_B = 2;
    static final private int DISEASE_GOLD_RECORDS_B = 3;

    static final private int DISEASE_BRONZE_RECORDS_M = 4;
    static final private int DISEASE_SILVER_RECORDS_M = 5;
    static final private int DISEASE_GOLD_RECORDS_M = 6;

    static final private int DISEASE_BRONZE_RECORDS_A = 7;
    static final private int DISEASE_SILVER_RECORDS_A = 8;
    static final private int DISEASE_GOLD_RECORDS_A = 9;


    static final private int WEIGHT_BRONZE_RECORDS_B = 3;
    static final private int WEIGHT_SILVER_RECORDS_B = 10;
    static final private int WEIGHT_GOLD_RECORDS_B = 20;

    static final private int WEIGHT_BRONZE_RECORDS_M = 40;
    static final private int WEIGHT_SILVER_RECORDS_M = 60;
    static final private int WEIGHT_GOLD_RECORDS_M = 80;

    static final private int WEIGHT_BRONZE_RECORDS_A = 90;
    static final private int WEIGHT_SILVER_RECORDS_A = 120;
    static final private int WEIGHT_GOLD_RECORDS_A = 150;

    static final private int BP_BRONZE_RECORDS_B = 1;
    static final private int BP_SILVER_RECORDS_B = 3;
    static final private int BP_GOLD_RECORDS_B = 5;

    static final private int BP_BRONZE_RECORDS_M = 10;
    static final private int BP_SILVER_RECORDS_M = 20;
    static final private int BP_GOLD_RECORDS_M = 30;

    static final private int BP_BRONZE_RECORDS_A = 40;
    static final private int BP_SILVER_RECORDS_A = 50;
    static final private int BP_GOLD_RECORDS_A = 60;

    static final private int CHOLESTEROL_BRONZE_RECORDS_B = 1;
    static final private int CHOLESTEROL_SILVER_RECORDS_B = 2;
    static final private int CHOLESTEROL_GOLD_RECORDS_B = 3;

    static final private int CHOLESTEROL_BRONZE_RECORDS_M = 4;
    static final private int CHOLESTEROL_SILVER_RECORDS_M = 5;
    static final private int CHOLESTEROL_GOLD_RECORDS_M = 6;

    static final private int CHOLESTEROL_BRONZE_RECORDS_A = 7;
    static final private int CHOLESTEROL_SILVER_RECORDS_A = 8;
    static final private int CHOLESTEROL_GOLD_RECORDS_A = 9;

    static final private int HBA1C_BRONZE_RECORDS_B = 1;
    static final private int HBA1C_SILVER_RECORDS_B = 2;
    static final private int HBA1C_GOLD_RECORDS_B = 3;

    static final private int HBA1C_BRONZE_RECORDS_M = 4;
    static final private int HBA1C_SILVER_RECORDS_M = 5;
    static final private int HBA1C_GOLD_RECORDS_M = 6;

    static final private int HBA1C_BRONZE_RECORDS_A = 7;
    static final private int HBA1C_SILVER_RECORDS_A = 8;
    static final private int HBA1C_GOLD_RECORDS_A = 9;

    static final private int DAILY_BRONZE_RECORDS = 5;
    static final private int DAILY_SILVER_RECORDS = 10;
    static final private int DAILY_GOLD_RECORDS = 15;


    public static void addPhotoBadge(Context context, DB_Read db) {

        int idUser = db.getUserId();
        boolean flag = db.hasMedal("photo");

        if (!flag) {
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("photo");
            badge.setMedal("single");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
    }

    public static void addExportBadge(Context context, DB_Read db) {

        int idUser = db.getUserId();

        boolean flag = db.hasMedal("export");
        

        if (!flag) {
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("export");
            badge.setMedal("single");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
    }

    public static void addLogBadge(Context context, DB_Read db, DB_Write dbwrite) {

//        Log.i("rawr", "addLogBadge:  BEGIN");

        int idUser = db.getUserId();
        //LinkedList<HomeElement> logBookEntries = new LinkedList<>();
        int numberElements;
        //LinkedList<BadgeRec> list = db.Badges_GetAll();
        LinkedList<BadgeRec> list = db.getAllMedals("log");

        boolean flagBronzeB = false;
        boolean flagSilverB = false;
        boolean flagGoldB = false;

        boolean flagBronzeM = false;
        boolean flagSilverM = false;
        boolean flagGoldM = false;

        boolean flagBronzeA = false;
        boolean flagSilverA = false;
        boolean flagGoldA = false;

        for (BadgeRec rec : list) {
            if (rec.getType().equals("beginner")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeB = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverB = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldB = true;
            }

            if (rec.getType().equals("medium")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeM = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverM = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldM = true;
            }

            if (rec.getType().equals("advanced")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeA = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverA = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldA = true;
            }
        }


//        for (BadgeRec rec : list) {
//            if (rec.getName().equals("log") && rec.getType().equals("beginner")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeB = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverB = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldB = true;
//            }
//
//            if (rec.getName().equals("log") && rec.getType().equals("medium")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeM = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverM = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldM = true;
//            }
//
//            if (rec.getName().equals("log") && rec.getType().equals("advanced")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeA = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverA = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldA = true;
//            }
//        }

//        if(LevelsPointsUtils.getLevel(context) < LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
//            PointsRec pnt = db.getFirstPointToReachLevel(0);
//            logBookEntries = db.getLogBookFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),LOG_GOLD_RECORDS_B);
//        }
//        if(LevelsPointsUtils.getLevel(context) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL && LevelsPointsUtils.getLevel(context) < LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
//            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL);
//            PointsRec pnt = db.getFirstPointToReachLevel(points);
//            logBookEntries = db.getLogBookFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),LOG_GOLD_RECORDS_M);
//        }
//        if(LevelsPointsUtils.getLevel(context) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
//            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL);
//            PointsRec pnt = db.getFirstPointToReachLevel(points);
//            logBookEntries = db.getLogBookFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),LOG_GOLD_RECORDS_A);
//        }

        //   nº registos = x; if x > y -> if(no medal a) -> medal(a)

        int numberReg = 0;
        int userLvl = LevelsPointsUtils.getLevel(context, db);

        if( userLvl < LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            PointsRec pnt = db.getFirstPointToReachLevel(0);
            //logBookEntries = db.getLogBookFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),LOG_GOLD_RECORDS_B); // this costs 200ms
            numberReg = db.getLogBookCount(pnt.getFormattedDate()+" "+pnt.getFormattedTime());
        }else{
            if( userLvl < LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
                int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL);
                PointsRec pnt = db.getFirstPointToReachLevel(points);
                numberReg = db.getLogBookCount(pnt.getFormattedDate()+" "+pnt.getFormattedTime());
                //logBookEntries = db.getLogBookFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),LOG_GOLD_RECORDS_M);
            }else{
                if( userLvl >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
                    int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL);
                    PointsRec pnt = db.getFirstPointToReachLevel(points);
                    numberReg = db.getLogBookCount(pnt.getFormattedDate()+" "+pnt.getFormattedTime());
                    //logBookEntries = db.getLogBookFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),LOG_GOLD_RECORDS_A);
                }
            }
        }
//        Log.i("rawr", "addLogBadge:                 - > "+numberReg);

        //BEGINNER
        //if(logBookEntries.size()>=LOG_BRONZE_RECORDS_B && !flagBronzeB){
        if(numberReg >= LOG_BRONZE_RECORDS_B && !flagBronzeB){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("log");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //if(logBookEntries.size()>=LOG_SILVER_RECORDS_B && !flagSilverB){
        if(numberReg >= LOG_SILVER_RECORDS_B && !flagSilverB){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("log");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //if(logBookEntries.size()>=LOG_GOLD_RECORDS_B && !flagGoldB){
        if(numberReg >= LOG_GOLD_RECORDS_B && !flagGoldB){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("log");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //MEDIUM
        //if(logBookEntries.size()>=LOG_BRONZE_RECORDS_M && !flagBronzeM && LevelsPointsUtils.getLevel(context) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
        if(numberReg >= LOG_BRONZE_RECORDS_M && !flagBronzeM && userLvl >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("log");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //if(logBookEntries.size()>=LOG_SILVER_RECORDS_M && !flagSilverM && LevelsPointsUtils.getLevel(context) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
        if(numberReg >= LOG_SILVER_RECORDS_M && !flagSilverM && userLvl >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("log");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //if(logBookEntries.size()>=LOG_GOLD_RECORDS_M && !flagGoldM && LevelsPointsUtils.getLevel(context) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
        if(numberReg >= LOG_GOLD_RECORDS_M && !flagGoldM && userLvl >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("log");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //ADVANCED
        //if(logBookEntries.size()>=LOG_BRONZE_RECORDS_A && !flagBronzeA && LevelsPointsUtils.getLevel(context) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
        if(numberReg >=LOG_BRONZE_RECORDS_A && !flagBronzeA && userLvl >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("log");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //if(logBookEntries.size()>=LOG_SILVER_RECORDS_A && !flagSilverA && LevelsPointsUtils.getLevel(context) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
        if(numberReg >= LOG_SILVER_RECORDS_A && !flagSilverA && userLvl >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("log");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //if(logBookEntries.size()>=LOG_GOLD_RECORDS_A && !flagGoldA && LevelsPointsUtils.getLevel(context) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
        if(numberReg >= LOG_GOLD_RECORDS_A && !flagGoldA && userLvl >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("log");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
//        Log.i("rawr", "addLogBadge:  END");
    }

    public static void addExerciseBadge(Context context, DB_Read db) {

        LinkedList<ExerciseRec> exerciseEntries = new LinkedList<>();
        //LinkedList<BadgeRec> list = db.Badges_GetAll();
        LinkedList<BadgeRec> list = db.getAllMedals("exercise");
        int idUser = db.getUserId();

        boolean flagBronzeB = false;
        boolean flagSilverB = false;
        boolean flagGoldB = false;

        boolean flagBronzeM = false;
        boolean flagSilverM = false;
        boolean flagGoldM = false;

        boolean flagBronzeA = false;
        boolean flagSilverA = false;
        boolean flagGoldA = false;

        for (BadgeRec rec : list) {
            if (rec.getType().equals("beginner")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeB = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverB = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldB = true;
            }

            if (rec.getType().equals("medium")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeM = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverM = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldM = true;
            }

            if (rec.getType().equals("advanced")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeA = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverA = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldA = true;
            }
        }

//        for (BadgeRec rec : list) {
//            if (rec.getName().equals("exercise") && rec.getType().equals("beginner")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeB = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverB = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldB = true;
//            }
//
//            if (rec.getName().equals("exercise") && rec.getType().equals("medium")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeM = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverM = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldM = true;
//            }
//
//            if (rec.getName().equals("exercise") && rec.getType().equals("advanced")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeA = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverA = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldA = true;
//            }
//        }
        if(LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            PointsRec pnt = db.getFirstPointToReachLevel(0);
            exerciseEntries = db.getExerciseFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),EXERCISE_GOLD_RECORDS_B);

        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL && LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            exerciseEntries = db.getExerciseFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),EXERCISE_GOLD_RECORDS_M);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            exerciseEntries = db.getExerciseFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),EXERCISE_GOLD_RECORDS_A);
        }
        
        //BEGINNER
        if(exerciseEntries.size()>=EXERCISE_BRONZE_RECORDS_B && !flagBronzeB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("exercise");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(exerciseEntries.size()>=EXERCISE_SILVER_RECORDS_B && !flagSilverB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("exercise");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(exerciseEntries.size()>=EXERCISE_GOLD_RECORDS_B && !flagGoldB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("exercise");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //MEDIUM
        if(exerciseEntries.size()>=EXERCISE_BRONZE_RECORDS_M && !flagBronzeM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("exercise");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(exerciseEntries.size()>=EXERCISE_SILVER_RECORDS_M && !flagSilverM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("exercise");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(exerciseEntries.size()>=EXERCISE_GOLD_RECORDS_M && !flagGoldM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("exercise");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //ADVANCED
        if(exerciseEntries.size()>=EXERCISE_BRONZE_RECORDS_A && !flagBronzeA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("exercise");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(exerciseEntries.size()>=EXERCISE_SILVER_RECORDS_A && !flagSilverA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("exercise");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(exerciseEntries.size()>=EXERCISE_GOLD_RECORDS_A && !flagGoldA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("exercise");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }

    }

    public static void addDiseaseBadge(Context context, DB_Read db) {

        int idUser = db.getUserId();
        //LinkedList<BadgeRec> list = db.Badges_GetAll();
        LinkedList<BadgeRec> list = db.getAllMedals("disease");

        boolean flagBronzeB = false;
        boolean flagSilverB = false;
        boolean flagGoldB = false;

        boolean flagBronzeM = false;
        boolean flagSilverM = false;
        boolean flagGoldM = false;

        boolean flagBronzeA = false;
        boolean flagSilverA = false;
        boolean flagGoldA = false;

        for (BadgeRec rec : list) {
            if (rec.getType().equals("beginner")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeB = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverB = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldB = true;
            }

            if (rec.getType().equals("medium")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeM = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverM = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldM = true;
            }

            if (rec.getType().equals("advanced")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeA = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverA = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldA = true;
            }
        }
//        for (BadgeRec rec : list) {
//            if (rec.getName().equals("disease") && rec.getType().equals("beginner")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeB = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverB = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldB = true;
//            }
//
//            if (rec.getName().equals("disease") && rec.getType().equals("medium")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeM = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverM = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldM = true;
//            }
//
//            if (rec.getName().equals("disease") && rec.getType().equals("advanced")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeA = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverA = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldA = true;
//            }
//        }
        LinkedList<DiseaseRec> diseaseEntries = new LinkedList<>();
        if(LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            PointsRec pnt = db.getFirstPointToReachLevel(0);
            diseaseEntries = db.getDiseaseFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),DISEASE_GOLD_RECORDS_B);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL && LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            diseaseEntries = db.getDiseaseFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),DISEASE_GOLD_RECORDS_M);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            diseaseEntries = db.getDiseaseFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),DISEASE_GOLD_RECORDS_A);
        }


        if(diseaseEntries.size()>=DISEASE_BRONZE_RECORDS_B && !flagBronzeB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("disease");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(diseaseEntries.size()>=DISEASE_SILVER_RECORDS_B && !flagSilverB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("disease");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(diseaseEntries.size()>=DISEASE_GOLD_RECORDS_B && !flagGoldB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("disease");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //MEDIUM
        if(diseaseEntries.size()>=DISEASE_BRONZE_RECORDS_M && !flagBronzeM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("disease");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(diseaseEntries.size()>=DISEASE_SILVER_RECORDS_M && !flagSilverM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("disease");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(diseaseEntries.size()>=DISEASE_GOLD_RECORDS_M && !flagGoldM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("disease");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //ADVANCED
        if(diseaseEntries.size()>=DISEASE_BRONZE_RECORDS_A && !flagBronzeA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("disease");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(diseaseEntries.size()>=DISEASE_SILVER_RECORDS_A && !flagSilverA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("disease");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(diseaseEntries.size()>=DISEASE_GOLD_RECORDS_A && !flagGoldA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("disease");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        

    }

    public static void addWeightBadge(Context context, DB_Read db) {

        //LinkedList<BadgeRec> list = db.Badges_GetAll();
        LinkedList<BadgeRec> list = db.getAllMedals("weight");

        int idUser = db.getUserId();
        boolean flagBronzeB = false;
        boolean flagSilverB = false;
        boolean flagGoldB = false;

        boolean flagBronzeM = false;
        boolean flagSilverM = false;
        boolean flagGoldM = false;

        boolean flagBronzeA = false;
        boolean flagSilverA = false;
        boolean flagGoldA = false;

        for (BadgeRec rec : list) {
            if (rec.getType().equals("beginner")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeB = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverB = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldB = true;
            }

            if (rec.getType().equals("medium")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeM = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverM = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldM = true;
            }

            if (rec.getType().equals("advanced")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeA = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverA = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldA = true;
            }
        }

//        for (BadgeRec rec : list) {
//            if (rec.getName().equals("weight") && rec.getType().equals("beginner")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeB = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverB = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldB = true;
//            }
//
//            if (rec.getName().equals("weight") && rec.getType().equals("medium")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeM = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverM = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldM = true;
//            }
//
//            if (rec.getName().equals("weight") && rec.getType().equals("advanced")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeA = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverA = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldA = true;
//            }
//        }
        LinkedList<WeightRec> weightEntries = new LinkedList<>();
        if(LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            PointsRec pnt = db.getFirstPointToReachLevel(0);
            weightEntries = db.getWeightFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),WEIGHT_GOLD_RECORDS_B);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL && LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            weightEntries = db.getWeightFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),WEIGHT_GOLD_RECORDS_M);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            weightEntries = db.getWeightFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),WEIGHT_GOLD_RECORDS_A);
        }


        if(weightEntries.size()>=WEIGHT_BRONZE_RECORDS_B && !flagBronzeB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("weight");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(weightEntries.size()>=WEIGHT_SILVER_RECORDS_B && !flagSilverB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("weight");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(weightEntries.size()>=WEIGHT_GOLD_RECORDS_B && !flagGoldB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("weight");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }

        //MEDIUM
        if(weightEntries.size()>=WEIGHT_BRONZE_RECORDS_M && !flagBronzeM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("weight");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(weightEntries.size()>=WEIGHT_SILVER_RECORDS_M && !flagSilverM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("weight");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(weightEntries.size()>=WEIGHT_GOLD_RECORDS_M && !flagGoldM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("weight");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //ADVANCED
        if(weightEntries.size()>=WEIGHT_BRONZE_RECORDS_A && !flagBronzeA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("weight");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(weightEntries.size()>=WEIGHT_SILVER_RECORDS_A && !flagSilverA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("weight");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(weightEntries.size()>=WEIGHT_GOLD_RECORDS_A && !flagGoldA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("weight");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        
    }

    public static void addBpBadge(Context context, DB_Read db) {

//        LinkedList<BadgeRec> list = db.Badges_GetAll();
        LinkedList<BadgeRec> list = db.getAllMedals("bp");

        int idUser = db.getUserId();
        boolean flagBronzeB = false;
        boolean flagSilverB = false;
        boolean flagGoldB = false;

        boolean flagBronzeM = false;
        boolean flagSilverM = false;
        boolean flagGoldM = false;

        boolean flagBronzeA = false;
        boolean flagSilverA = false;
        boolean flagGoldA = false;

        for (BadgeRec rec : list) {
            if (rec.getType().equals("beginner")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeB = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverB = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldB = true;
            }

            if (rec.getType().equals("medium")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeM = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverM = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldM = true;
            }

            if (rec.getType().equals("advanced")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeA = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverA = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldA = true;
            }
        }
//        for (BadgeRec rec : list) {
//            if (rec.getName().equals("bp") && rec.getType().equals("beginner")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeB = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverB = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldB = true;
//            }
//
//            if (rec.getName().equals("bp") && rec.getType().equals("medium")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeM = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverM = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldM = true;
//            }
//
//            if (rec.getName().equals("bp") && rec.getType().equals("advanced")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeA = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverA = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldA = true;
//            }
//        }

        LinkedList<BloodPressureRec> bpEntries = new LinkedList<>();
        if(LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            PointsRec pnt = db.getFirstPointToReachLevel(0);
            bpEntries = db.getBpFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),BP_GOLD_RECORDS_B);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL && LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            bpEntries = db.getBpFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),BP_GOLD_RECORDS_M);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            bpEntries = db.getBpFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),BP_GOLD_RECORDS_A);
        }

        if(bpEntries.size()>=BP_BRONZE_RECORDS_B && !flagBronzeB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("bp");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(bpEntries.size()>=BP_SILVER_RECORDS_B && !flagSilverB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("bp");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(bpEntries.size()>=BP_GOLD_RECORDS_B && !flagGoldB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("bp");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }

        //MEDIUM
        if(bpEntries.size()>=BP_BRONZE_RECORDS_M && !flagBronzeM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("bp");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(bpEntries.size()>=BP_SILVER_RECORDS_M && !flagSilverM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("bp");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(bpEntries.size()>=BP_GOLD_RECORDS_M && !flagGoldM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("bp");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //ADVANCED
        if(bpEntries.size()>=BP_BRONZE_RECORDS_A && !flagBronzeA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("bp");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(bpEntries.size()>=BP_SILVER_RECORDS_A && !flagSilverA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("bp");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();

            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(bpEntries.size()>=BP_GOLD_RECORDS_A && !flagGoldA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("bp");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        
    }

    public static void addCholesterolBadge(Context context, DB_Read db) {

//        LinkedList<BadgeRec> list = db.Badges_GetAll();
        LinkedList<BadgeRec> list = db.getAllMedals("cholesterol");
        int idUser = db.getUserId();
        boolean flagBronzeB = false;
        boolean flagSilverB = false;
        boolean flagGoldB = false;

        boolean flagBronzeM = false;
        boolean flagSilverM = false;
        boolean flagGoldM = false;

        boolean flagBronzeA = false;
        boolean flagSilverA = false;
        boolean flagGoldA = false;
        for (BadgeRec rec : list) {
            if (rec.getType().equals("beginner")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeB = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverB = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldB = true;
            }

            if (rec.getType().equals("medium")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeM = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverM = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldM = true;
            }

            if (rec.getType().equals("advanced")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeA = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverA = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldA = true;
            }
        }
//        for (BadgeRec rec : list) {
//            if (rec.getName().equals("cholesterol") && rec.getType().equals("beginner")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeB = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverB = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldB = true;
//            }
//
//            if (rec.getName().equals("cholesterol") && rec.getType().equals("medium")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeM = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverM = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldM = true;
//            }
//
//            if (rec.getName().equals("cholesterol") && rec.getType().equals("advanced")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeA = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverA = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldA = true;
//            }
//        }

        LinkedList<CholesterolRec> cholesterolEntries = new LinkedList<>();
        if(LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            PointsRec pnt = db.getFirstPointToReachLevel(0);
            cholesterolEntries = db.getCholesterolFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),CHOLESTEROL_GOLD_RECORDS_B);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL && LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            cholesterolEntries = db.getCholesterolFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),CHOLESTEROL_GOLD_RECORDS_M);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            cholesterolEntries = db.getCholesterolFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),CHOLESTEROL_GOLD_RECORDS_A);
        }

        if(cholesterolEntries.size()>=CHOLESTEROL_BRONZE_RECORDS_B && !flagBronzeB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("cholesterol");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(cholesterolEntries.size()>=CHOLESTEROL_SILVER_RECORDS_B && !flagSilverB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("cholesterol");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(cholesterolEntries.size()>=CHOLESTEROL_GOLD_RECORDS_B && !flagGoldB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("cholesterol");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //MEDIUM
        if(cholesterolEntries.size()>=CHOLESTEROL_BRONZE_RECORDS_M && !flagBronzeM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("cholesterol");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(cholesterolEntries.size()>=CHOLESTEROL_SILVER_RECORDS_M && !flagSilverM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("cholesterol");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(cholesterolEntries.size()>=CHOLESTEROL_GOLD_RECORDS_M && !flagGoldM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("cholesterol");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //ADVANCED
        if(cholesterolEntries.size()>=CHOLESTEROL_BRONZE_RECORDS_A && !flagBronzeA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("cholesterol");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(cholesterolEntries.size()>=CHOLESTEROL_SILVER_RECORDS_A && !flagSilverA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("cholesterol");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(cholesterolEntries.size()>=CHOLESTEROL_GOLD_RECORDS_A && !flagGoldA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("cholesterol");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        
    }

    public static void addHba1cBadge(Context context, DB_Read db) {

//        LinkedList<BadgeRec> list = db.Badges_GetAll();
        LinkedList<BadgeRec> list = db.getAllMedals("hba1c");
        int idUser = db.getUserId();
        boolean flagBronzeB = false;
        boolean flagSilverB = false;
        boolean flagGoldB = false;

        boolean flagBronzeM = false;
        boolean flagSilverM = false;
        boolean flagGoldM = false;

        boolean flagBronzeA = false;
        boolean flagSilverA = false;
        boolean flagGoldA = false;

        for (BadgeRec rec : list) {
            if (rec.getType().equals("beginner")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeB = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverB = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldB = true;
            }

            if (rec.getType().equals("medium")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeM = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverM = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldM = true;
            }

            if (rec.getType().equals("advanced")) {
                if(rec.getMedal().equals("bronze"))
                    flagBronzeA = true;
                if(rec.getMedal().equals("silver"))
                    flagSilverA = true;
                if(rec.getMedal().equals("gold"))
                    flagGoldA = true;
            }
        }

//        for (BadgeRec rec : list) {
//            if (rec.getName().equals("hba1c") && rec.getType().equals("beginner")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeB = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverB = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldB = true;
//            }
//
//            if (rec.getName().equals("hba1c") && rec.getType().equals("medium")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeM = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverM = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldM = true;
//            }
//
//            if (rec.getName().equals("hba1c") && rec.getType().equals("advanced")) {
//                if(rec.getMedal().equals("bronze"))
//                    flagBronzeA = true;
//                if(rec.getMedal().equals("silver"))
//                    flagSilverA = true;
//                if(rec.getMedal().equals("gold"))
//                    flagGoldA = true;
//            }
//        }

        LinkedList<HbA1cRec> hba1cEntries = new LinkedList<>();
        if(LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            PointsRec pnt = db.getFirstPointToReachLevel(0);
            hba1cEntries = db.getHbA1cFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),HBA1C_GOLD_RECORDS_B);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL && LevelsPointsUtils.getLevel(context, db) < LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            hba1cEntries = db.getHbA1cFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),HBA1C_GOLD_RECORDS_M);
        }
        if(LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            int points = LevelsPointsUtils.getPointsInLevel(LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL);
            PointsRec pnt = db.getFirstPointToReachLevel(points);
            hba1cEntries = db.getHbA1cFromStartDate(pnt.getFormattedDate()+" "+pnt.getFormattedTime(),HBA1C_GOLD_RECORDS_A);
        }

        if(hba1cEntries.size()>=HBA1C_BRONZE_RECORDS_B && !flagBronzeB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("hba1c");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(hba1cEntries.size()>=HBA1C_SILVER_RECORDS_B && !flagSilverB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("hba1c");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(hba1cEntries.size()>=HBA1C_GOLD_RECORDS_B && !flagGoldB){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("beginner");
            badge.setName("hba1c");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //MEDIUM
        if(hba1cEntries.size()>=HBA1C_BRONZE_RECORDS_M && !flagBronzeM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("hba1c");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(hba1cEntries.size()>=HBA1C_SILVER_RECORDS_M && !flagSilverM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("hba1c");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(hba1cEntries.size()>=HBA1C_GOLD_RECORDS_M && !flagGoldM && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("medium");
            badge.setName("hba1c");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        //ADVANCED
        if(hba1cEntries.size()>=HBA1C_BRONZE_RECORDS_A && !flagBronzeA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("hba1c");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(hba1cEntries.size()>=HBA1C_SILVER_RECORDS_A && !flagSilverA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("hba1c");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        if(hba1cEntries.size()>=HBA1C_GOLD_RECORDS_A && !flagGoldA && LevelsPointsUtils.getLevel(context, db) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            DB_Write dbwrite = new DB_Write(context);
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("advanced");
            badge.setName("hba1c");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);

            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            dbwrite.close();
            SimpleToast.info(context, context.getString(R.string.received_a_medal), "{fa-trophy}");
        }
        
    }

    public static void addDailyBadge(Context context, DB_Read db, DB_Write dbwrite) {

        int idUser = db.getUserId();

        int numberOfDailyMedalsToday = db.getNumberOfAllDailyBadgesByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        //boolean hasDailyBronze = db.hasDailyMedal(DateUtils.getFormattedDate(Calendar.getInstance()),"bronze");
        //boolean hasDailySilver = db.hasDailyMedal(DateUtils.getFormattedDate(Calendar.getInstance()),"silver");
        //boolean hasDailyGold  = db.hasDailyMedal(DateUtils.getFormattedDate(Calendar.getInstance()),"gold");

        int recordListNumber = db.getRecordsByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        int exerciseList = db.getNumberExerciceByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        int bpList = db.getNumberBloodPressureByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        int cholesterolList = db.getNumberCholesterolByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        int weightList = db.getNumberWeightByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        int hbA1cList = db.getNumberHbA1cByDate(DateUtils.getFormattedDate(Calendar.getInstance()));
        int diseaseList = db.getNumberDiseaseByDate(DateUtils.getFormattedDate(Calendar.getInstance()));

        int size = exerciseList + bpList + cholesterolList + weightList + hbA1cList + diseaseList + recordListNumber; //+ logList.size();
        //Log.i("rawr", "addDailyBadge: "+size);
        if(size >= DAILY_BRONZE_RECORDS && numberOfDailyMedalsToday<=0){//!hasDailyBronze){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("daily");
            badge.setName("log");
            badge.setMedal("bronze");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_daily_medal), "{fa-trophy}");
        }
        if(size >= DAILY_SILVER_RECORDS && numberOfDailyMedalsToday<=1){//!hasDailySilver){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("daily");
            badge.setName("log");
            badge.setMedal("silver");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_daily_medal), "{fa-trophy}");
        }
        if(size >= DAILY_GOLD_RECORDS && numberOfDailyMedalsToday<=2){//!hasDailyGold){
            BadgeRec badge = new BadgeRec();
            badge.setIdUser(idUser);
            badge.setDateTime(Calendar.getInstance());
            badge.setType("daily");
            badge.setName("log");
            badge.setMedal("gold");
            dbwrite.Badge_Save(badge);
            LevelsPointsUtils.addPoints(context, LevelsPointsUtils.BADGE_POINTS, "badge", db);
            SimpleToast.info(context, context.getString(R.string.received_a_daily_medal), "{fa-trophy}");
        }
    }

}
