package pt.it.porto.mydiabetes.ui.createMeal.db;

public class MealItemsTableHelper {

    public static final String TABLE_NAME = "meal_items";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MEAL_ID = "logged_meal_id";
    public static final String COLUMN_FOOD_ID = "item_id";
    public static final String COLUMN_QUANTITY = "item_quantity";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_MEAL_ID + " INTEGER,"
                    + COLUMN_FOOD_ID + " INTEGER,"
                    + COLUMN_QUANTITY + " INTEGER, "
                    + " FOREIGN KEY(" + COLUMN_MEAL_ID + ")" + " REFERENCES " + LoggedMealsTableHelper.TABLE_NAME + "(" + LoggedMealsTableHelper.COLUMN_ID + "),"
                    + " FOREIGN KEY(" + COLUMN_FOOD_ID + ")" + " REFERENCES " + FoodsTableHelper.TABLE_NAME + "(" + FoodsTableHelper.COLUMN_ID + ")"
                    + ")";
}
