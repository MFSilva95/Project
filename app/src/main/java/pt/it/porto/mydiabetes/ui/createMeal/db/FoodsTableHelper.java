package pt.it.porto.mydiabetes.ui.createMeal.db;

public class FoodsTableHelper {
    public static final String TABLE_NAME = "foods";

    public static final String COLUMN_ID = "food_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GROUP = "food_group";
    public static final String COLUMN_ENERGYJ = "energy_j";
    public static final String COLUMN_ENERGYC = "energy_c";
    public static final String COLUMN_LIPIDS = "lipids";
    public static final String COLUMN_CH = "carbs";
    public static final String COLUMN_SALT = "salt";
    public static final String COLUMN_PROT = "proteins";
    public static final String COLUMN_CHOLESTEROL = "cholesterol";


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT NOT NULL,"
                    + COLUMN_GROUP + " TEXT,"
                    + COLUMN_ENERGYJ + " TEXT NOT NULL,"
                    + COLUMN_ENERGYC + " TEXT NOT NULL,"
                    + COLUMN_LIPIDS + " TEXT NOT NULL,"
                    + COLUMN_CH + " TEXT NOT NULL,"
                    + COLUMN_SALT + " TEXT NOT NULL,"
                    + COLUMN_PROT + " TEXT NOT NULL,"
                    + COLUMN_CHOLESTEROL + " TEXT NOT NULL"
                    + ")";

    public FoodsTableHelper() {}
}
