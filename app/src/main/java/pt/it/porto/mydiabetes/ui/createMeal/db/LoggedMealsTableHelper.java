package pt.it.porto.mydiabetes.ui.createMeal.db;

public class LoggedMealsTableHelper {
    public static final String TABLE_NAME = "logged_meals";

    public static final String COLUMN_ID = "meal_id";
    public static final String COLUMN_NAME = "meal_name";
    public static final String COLUMN_TIMESTAMP = "meal_timestamp";
    public static final String COLUMN_PHOTO_PATH = "meal_photo";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_PHOTO_PATH + " TEXT"
                    + ")";

    public LoggedMealsTableHelper() {}
}
