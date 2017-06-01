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
    public static final int BADGES_MEDIUM_UNLOCK_LEVEL = 10;
    public static final int BADGES_ADVANCED_UNLOCK_LEVEL = 30;

    public static final int[] levels = {
            0, 500, 1000, 2000, 3000, 4000,
            5500, 7000, 8500, 10000, 12000,
            15000, 18000, 21000, 24000, 27000,
            30000, 34000, 38000, 42000, 44000,
            48000, 52000, 58000, 63000, 67000,
            71000, 76000, 81000, 86000, 91000,
            97000, 103000, 109000, 115000, 121000,
            128000, 135000, 142000, 149000, 156000,
            163000, 170000, 178000, 186000, 194000,
            202000, 210000, 220000, 230000
    };

    public static int getLevel(Context context, DB_Read db){
        //DB_Read db = new DB_Read(context);
        int userPoints = db.getTotalPoints();
        //db.close();

        int index;
        for(index=1 ; index<levels.length; index++){
            if(userPoints < levels[index]){
                return index;
            }
        }
        return levels.length;
    }

    public static int getPointsInLevel(int level){
        return levels[level-1];
    }

    public static int getPercentageLevels(Context context, DB_Read db){
        //DB_Read db = new DB_Read(context);
        int userPoints = db.getTotalPoints();
        //db.close();
        int percent = 0;
        int index = getLevel(context, db);
        if(index != levels.length){
            int max = levels[index] - levels[index-1];
            userPoints = userPoints - levels[index-1];
            percent = (userPoints * 100) / max;
        }
        else{
            percent = 100;
        }

        return percent;
    }


    public static int getPointsNextLevel(Context context, DB_Read db){
        int index = getLevel(context,db);
        if (index != levels.length) {
            return levels[index];
        }
        else{
            return levels[levels.length-1];
        }
    }


    //Points

    public static final int RECORD_POINTS = 100;
    public static final int BADGE_POINTS = 200;
    //public static int STREAK_NUMBER = 1;
    //public static final int REMOVE_POINTS = -50;

    public static void addPoints(Context context, int points, String origin, DB_Read db) {
        //DB_Read db = new DB_Read(context);
        int idUser = db.getId();
        int userPoints = db.getTotalPoints();
        //db.close();
        int index = getLevel(context, db);


        if((userPoints + points) >= levels[index-1]){
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
    }

    public static int getTotalPoints(Context context){
        DB_Read db = new DB_Read(context);
        int points = db.getTotalPoints();
        db.close();
        return points;
    }


}
