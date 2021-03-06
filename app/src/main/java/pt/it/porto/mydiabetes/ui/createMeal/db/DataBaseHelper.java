package pt.it.porto.mydiabetes.ui.createMeal.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pt.it.porto.mydiabetes.ui.createMeal.utils.LoggedMeal;
import pt.it.porto.mydiabetes.ui.createMeal.utils.MealItem;


public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "meals.db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FoodsTableHelper.CREATE_TABLE);
        db.execSQL(LoggedMealsTableHelper.CREATE_TABLE);
        db.execSQL(MealItemsTableHelper.CREATE_TABLE);

        db.setLocale(new Locale("pt_PT"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + FoodsTableHelper.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LoggedMealsTableHelper.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MealItemsTableHelper.TABLE_NAME);

        onCreate(db);
    }

    public boolean loadInitialData(Context context) throws IOException {

        AssetManager assetManager = context.getAssets();
        InputStream assetFile = assetManager.open("data.csv");
        BufferedReader reader = null;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            reader = new BufferedReader(new InputStreamReader(assetFile));
            String line;
            ContentValues contentValues = new ContentValues();
            String[] values;
            long id_returned;
            while ((line = reader.readLine()) != null) {
                values = line.split(",");

                contentValues.put(FoodsTableHelper.COLUMN_NAME, values[0]);
                contentValues.put(FoodsTableHelper.COLUMN_GROUP, values[1]);
                contentValues.put(FoodsTableHelper.COLUMN_ENERGYJ, values[2]);
                contentValues.put(FoodsTableHelper.COLUMN_ENERGYC, values[3]);
                contentValues.put(FoodsTableHelper.COLUMN_LIPIDS, values[4]);
                contentValues.put(FoodsTableHelper.COLUMN_CH, values[5]);
                contentValues.put(FoodsTableHelper.COLUMN_SALT, values[6]);
                contentValues.put(FoodsTableHelper.COLUMN_PROT, values[7]);
                contentValues.put(FoodsTableHelper.COLUMN_CHOLESTEROL, values[8]);

                id_returned = db.insert(FoodsTableHelper.TABLE_NAME, null, contentValues);

                if (id_returned == -1) {
                    //Insertion has failed
                    db.endTransaction();
                    return false;
                }
            }

            db.setTransactionSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            db.endTransaction();
        }
        return true;
    }

    public int insertMeal(LoggedMeal meal){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long meal_id = -1;

        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put(LoggedMealsTableHelper.COLUMN_NAME, meal.getName());
            contentValues.put(LoggedMealsTableHelper.COLUMN_EXTRA_CARBS, meal.getExtraCarbs());
            contentValues.put(LoggedMealsTableHelper.COLUMN_PHOTO_PATH, meal.getThumbnailPath());

            if(meal.isFavourite())
                contentValues.put(LoggedMealsTableHelper.COLUMN_IS_FAVOURITE, 1);
            if(meal.isRegistered())
                contentValues.put(LoggedMealsTableHelper.COLUMN_IS_REGISTERED, 1);

            meal_id = db.insert(LoggedMealsTableHelper.TABLE_NAME, null, contentValues);

            if(meal_id == -1) {
                db.endTransaction();
                return -1;
            }

            contentValues.clear();
            for(MealItem item : meal.getItemList()){
                contentValues.put(MealItemsTableHelper.COLUMN_MEAL_ID, meal_id);
                contentValues.put(MealItemsTableHelper.COLUMN_FOOD_ID, item.getId());
                contentValues.put(MealItemsTableHelper.COLUMN_QUANTITY, item.getQuantity());

                if(db.insert(MealItemsTableHelper.TABLE_NAME, null, contentValues) == -1) {
                    db.endTransaction();
                    return -1;
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
        return (int)meal_id;
    }

    public boolean deleteMeal(int meal_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try{
            if(meal_id != -1){
                if(db.delete(LoggedMealsTableHelper.TABLE_NAME, LoggedMealsTableHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(meal_id)}) == -1){
                    db.endTransaction();
                    return false;
                }

                if(db.delete(MealItemsTableHelper.TABLE_NAME, MealItemsTableHelper.COLUMN_MEAL_ID + " = ?", new String[]{String.valueOf(meal_id)}) == -1){
                    db.endTransaction();
                    return false;
                }

            } else{
                db.endTransaction();
                return false;
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
        return true;
    }

    public List<MealItem> getFoodList(){
        List<MealItem> foodList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + FoodsTableHelper.TABLE_NAME + " ORDER BY " +
                FoodsTableHelper.COLUMN_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int food_id = cursor.getInt(cursor.getColumnIndex(FoodsTableHelper.COLUMN_ID));
                String food_name = cursor.getString(cursor.getColumnIndex(FoodsTableHelper.COLUMN_NAME));
                Float food_carbs = Float.valueOf(cursor.getString(cursor.getColumnIndex(FoodsTableHelper.COLUMN_CH)).replaceAll("[^.0-9]", ""));

                Float food_lipids = Float.valueOf(cursor.getString(cursor.getColumnIndex(FoodsTableHelper.COLUMN_LIPIDS)).replaceAll("[^.0-9]", ""));
                Float food_protein = Float.valueOf(cursor.getString(cursor.getColumnIndex(FoodsTableHelper.COLUMN_PROT)).replaceAll("[^.0-9]", ""));

                foodList.add(new MealItem(food_id,food_name, food_carbs, food_lipids, food_protein));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return foodList;
    }

    private MealItem getMealItem(long item_id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FoodsTableHelper.TABLE_NAME,
                new String[]{FoodsTableHelper.COLUMN_ID, FoodsTableHelper.COLUMN_NAME, FoodsTableHelper.COLUMN_CH, FoodsTableHelper.COLUMN_LIPIDS, FoodsTableHelper.COLUMN_PROT},
                FoodsTableHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(item_id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        int food_id = cursor.getInt(cursor.getColumnIndex(FoodsTableHelper.COLUMN_ID));
        String food_name = cursor.getString(cursor.getColumnIndex(FoodsTableHelper.COLUMN_NAME));
        Float food_carbs = Float.valueOf(cursor.getString(cursor.getColumnIndex(FoodsTableHelper.COLUMN_CH)).replaceAll("[^.0-9]", ""));
        Float food_lipids = Float.valueOf(cursor.getString(cursor.getColumnIndex(FoodsTableHelper.COLUMN_LIPIDS)).replaceAll("[^.0-9]", ""));
        Float food_protein = Float.valueOf(cursor.getString(cursor.getColumnIndex(FoodsTableHelper.COLUMN_PROT)).replaceAll("[^.0-9]", ""));

        cursor.close();

        return new MealItem(food_id, food_name, food_carbs,food_lipids,food_protein);
    }

    private List<MealItem> getMealItemList(long meal_id){
        List<MealItem> mealItemList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + MealItemsTableHelper.TABLE_NAME + " WHERE " +
                MealItemsTableHelper.COLUMN_MEAL_ID + " = " + meal_id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long item_id = cursor.getLong(cursor.getColumnIndex(MealItemsTableHelper.COLUMN_FOOD_ID));
                MealItem item = getMealItem(item_id);
                item.setQuantity(cursor.getInt(cursor.getColumnIndex(MealItemsTableHelper.COLUMN_QUANTITY)));

                mealItemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return mealItemList;
    }


    public LoggedMeal getLoggedMeal_by_id(int id){

        String selectQuery = "SELECT * FROM " + LoggedMealsTableHelper.TABLE_NAME +
                " WHERE " + LoggedMealsTableHelper.COLUMN_ID + " = '" + id + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            int meal_id = cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_NAME));
            int carbs = cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_EXTRA_CARBS));
            String timestamp = cursor.getString(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_TIMESTAMP));
            String thumbnail_path = cursor.getString(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_PHOTO_PATH));
            boolean favourite = (cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_IS_FAVOURITE)) == 1);
            boolean registered = (cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_IS_REGISTERED)) == 1);
            LoggedMeal meal = new LoggedMeal(getMealItemList(meal_id));
            meal.setId(meal_id);
            meal.setName(name);
            meal.setExtraCarbs(carbs);
            meal.setTimestamp(timestamp);
            meal.setThumbnailPath(thumbnail_path);
            meal.setFavourite(favourite);
            meal.setRegistered(registered);

            return meal;

        }

        cursor.close();
        db.close();
        return null;
    }

    public List<LoggedMeal> getLoggedMealList(){
        List<LoggedMeal> mealList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + LoggedMealsTableHelper.TABLE_NAME + " ORDER BY " +
                LoggedMealsTableHelper.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int meal_id = cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_NAME));
                int carbs = cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_EXTRA_CARBS));
                String timestamp = cursor.getString(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_TIMESTAMP));
                String thumbnail_path = cursor.getString(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_PHOTO_PATH));
                boolean favourite = (cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_IS_FAVOURITE)) == 1);
                boolean registered = (cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_IS_REGISTERED)) == 1);

                LoggedMeal meal = new LoggedMeal(getMealItemList(meal_id));
                meal.setId(meal_id);
                meal.setName(name);
                meal.setExtraCarbs(carbs);
                meal.setTimestamp(timestamp);
                meal.setThumbnailPath(thumbnail_path);
                meal.setFavourite(favourite);
                meal.setRegistered(registered);

                mealList.add(meal);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return mealList;
    }

    public LoggedMeal getMeal(int meal_id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(LoggedMealsTableHelper.TABLE_NAME,
                new String[]{LoggedMealsTableHelper.COLUMN_ID, LoggedMealsTableHelper.COLUMN_NAME, LoggedMealsTableHelper.COLUMN_EXTRA_CARBS, LoggedMealsTableHelper.COLUMN_TIMESTAMP,LoggedMealsTableHelper.COLUMN_PHOTO_PATH,LoggedMealsTableHelper.COLUMN_IS_FAVOURITE,LoggedMealsTableHelper.COLUMN_IS_REGISTERED},
                LoggedMealsTableHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(meal_id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        String name = cursor.getString(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_NAME));
        int carbs = cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_EXTRA_CARBS));
        String timestamp = cursor.getString(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_TIMESTAMP));
        String thumbnail_path = cursor.getString(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_PHOTO_PATH));
        boolean favourite = (cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_IS_FAVOURITE)) == 1);
        boolean registered = (cursor.getInt(cursor.getColumnIndex(LoggedMealsTableHelper.COLUMN_IS_REGISTERED)) == 1);

        LoggedMeal meal = new LoggedMeal(getMealItemList(meal_id));
        meal.setId(meal_id);
        meal.setName(name);
        meal.setExtraCarbs(carbs);
        meal.setTimestamp(timestamp);
        meal.setThumbnailPath(thumbnail_path);
        meal.setFavourite(favourite);
        meal.setRegistered(registered);

        cursor.close();
        db.close();

        return meal;
    }

    public int updateMeal(LoggedMeal meal){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put(LoggedMealsTableHelper.COLUMN_PHOTO_PATH, meal.getThumbnailPath());
            contentValues.put(LoggedMealsTableHelper.COLUMN_NAME, meal.getName());
            contentValues.put(LoggedMealsTableHelper.COLUMN_EXTRA_CARBS, meal.getExtraCarbs());
            if (meal.isRegistered())
                contentValues.put(LoggedMealsTableHelper.COLUMN_IS_REGISTERED, 1);
            else
                contentValues.put(LoggedMealsTableHelper.COLUMN_IS_REGISTERED, 0);

            db.update(LoggedMealsTableHelper.TABLE_NAME, contentValues,
                    LoggedMealsTableHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(meal.getId())});


            if (db.delete(MealItemsTableHelper.TABLE_NAME, MealItemsTableHelper.COLUMN_MEAL_ID + " = ?", new String[]{String.valueOf(meal.getId())}) == -1) {
                db.endTransaction();
                return -1;
            }


            contentValues.clear();
            for (MealItem item : meal.getItemList()) {
                contentValues.put(MealItemsTableHelper.COLUMN_MEAL_ID, meal.getId());
                contentValues.put(MealItemsTableHelper.COLUMN_FOOD_ID, item.getId());
                contentValues.put(MealItemsTableHelper.COLUMN_QUANTITY, item.getQuantity());

                if (db.insert(MealItemsTableHelper.TABLE_NAME, null, contentValues) == -1) {
                    db.endTransaction();
                    return -1;
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
        return meal.getId();
    }
}
