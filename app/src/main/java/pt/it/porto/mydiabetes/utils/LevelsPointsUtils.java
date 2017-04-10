package pt.it.porto.mydiabetes.utils;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;

import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.PointsRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;

/**
 * Created by parra on 07/04/2017.
 */

public class LevelsPointsUtils {

    //Levels
    public static final int[] levels = {
            0, 500, 1000,
            1500, 2000, 2500,
            3000, 3500, 4000,
            4500, 5000
    };

    public static int getLevel(Context context){
        DB_Read db = new DB_Read(context);
        int userPoints = db.getTotalPoints();
        db.close();

        int index;
        for(index=1 ; index<levels.length; index++){
            if(userPoints < levels[index]){
                return index;
            }
        }
        return levels.length - 1;
    }

    public static int getPercentageLevels(Context context){
        DB_Read db = new DB_Read(context);
        int userPoints = db.getTotalPoints();
        db.close();
        int index = getLevel(context);
        int max = levels[index] - levels[index-1];
        userPoints = userPoints - levels[index-1];
        int percent = (userPoints * 100) / max;
        Log.e("USERPOINTS", userPoints+"");
        Log.e("max", max+"");
        return percent;
    }


    public static int getPointsNextLevel(Context context){
        int index = getLevel(context);
        return levels[index];
    }


    //Points

    public static final int RECORD_POINTS = 50;
    public static final int BADGE_POINTS = 100;
    //public static int STREAK_NUMBER = 1;
    //public static final int REMOVE_POINTS = -50;

    public static void addPoints(Context context, int points, String origin) {
        DB_Read db = new DB_Read(context);
        int idUser = db.getId();
        int userPoints = db.getTotalPoints();
        db.close();
        int index = getLevel(context);


        if((userPoints + points) >= levels[index-1] && (userPoints + points) <= levels[levels.length - 1]){
            DB_Write dbwrite = new DB_Write(context);
            PointsRec newPointRec = new PointsRec();
            newPointRec.setIdUser(idUser);
            newPointRec.setDateTime(Calendar.getInstance());
            newPointRec.setValue(points);
            newPointRec.setOrigin(origin);
            dbwrite.Point_Save(newPointRec);
            dbwrite.close();
        }
        if((userPoints + points) < levels[index-1]){
            DB_Write dbwrite = new DB_Write(context);
            PointsRec newPointRec = new PointsRec();
            newPointRec.setIdUser(idUser);
            newPointRec.setDateTime(Calendar.getInstance());
            newPointRec.setValue(-Math.abs(levels[index-1] - userPoints));
            newPointRec.setOrigin(origin);
            dbwrite.Point_Save(newPointRec);
            dbwrite.close();
        }
        if((userPoints + points) > levels[levels.length - 1]){
            DB_Write dbwrite = new DB_Write(context);
            PointsRec newPointRec = new PointsRec();
            newPointRec.setIdUser(idUser);
            newPointRec.setDateTime(Calendar.getInstance());
            Log.e("MAX", (points - ((userPoints + points) - levels[levels.length - 1]))+"");
            newPointRec.setValue(points - ((userPoints + points) - levels[levels.length - 1]));
            newPointRec.setOrigin(origin);
            dbwrite.Point_Save(newPointRec);
            dbwrite.close();
        }
    }

    public static int getTotalPoints(Context context){
        DB_Read db = new DB_Read(context);
        int points = db.getTotalPoints();
        db.close();
        return points;
    }


}
