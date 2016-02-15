package pt.it.porto.mydiabetes.database;

import android.database.Cursor;

public class ListsDataDb {


	private MyDiabetesStorage storage;

	public ListsDataDb(MyDiabetesStorage storage) {
		this.storage = storage;
	}


	public Cursor getLogbookList(String startDate, String endDate) {
		Cursor cursor = storage.rawQuery("SELECT DISTINCT datetime, tag, carbs, insulinVal, insulinName, glycemia, carbsId, insulinId, glycemiaId" +
				" FROM " +
				"(" +
				"SELECT Reg_CarboHydrate.DateTime as datetime, Tag.Name as tag, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
				" FROM Reg_CarboHydrate, Tag, Reg_Insulin, Reg_BloodGlucose, Insulin" +
				" WHERE Reg_CarboHydrate.DateTime = Reg_Insulin.DateTime" +
				" AND Reg_CarboHydrate.DateTime = Reg_BloodGlucose.DateTime" +
				" AND Tag.Id = Reg_CarboHydrate.Id_Tag AND Reg_Insulin.Id_Insulin = Insulin.Id" +
				" UNION " +
				"SELECT Reg_CarboHydrate.DateTime as datetime, Tag.Name as tag, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, -1 AS glycemia, -1 as glycemiaId" +
				" FROM Reg_CarboHydrate, Tag, Reg_Insulin, Insulin" +
				" WHERE Reg_CarboHydrate.DateTime = Reg_Insulin.DateTime" +
				" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
				" AND Tag.Id = Reg_CarboHydrate.Id_Tag AND Reg_Insulin.Id_Insulin = Insulin.Id" +
				" UNION " +
				"SELECT Reg_CarboHydrate.DateTime as datetime, Tag.Name as tag, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
				" FROM Reg_CarboHydrate, Tag, Reg_BloodGlucose" +
				" WHERE Reg_CarboHydrate.DateTime = Reg_BloodGlucose.DateTime " +
				" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
				" AND Tag.Id = Reg_CarboHydrate.Id_Tag" +
				" UNION " +
				"SELECT Reg_CarboHydrate.DateTime as datetime, Tag.Name as tag, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, -1 AS glycemia, -1 as glycemiaId" +
				" FROM Reg_CarboHydrate, Tag" +
				" WHERE Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
				" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
				" AND Tag.Id = Reg_CarboHydrate.Id_Tag" +
				" UNION " +
				"SELECT Reg_BloodGlucose.DateTime as datetime, Tag.Name as tag, -1 as carbs, -1 as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
				" FROM Tag, Reg_Insulin, Reg_BloodGlucose, Insulin" +
				" WHERE Reg_BloodGlucose.DateTime = Reg_Insulin.DateTime " +
				" AND Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
				" AND Tag.Id = Reg_BloodGlucose.Id_Tag AND Reg_Insulin.Id_Insulin = Insulin.Id" +
				" UNION " +
				"SELECT Reg_BloodGlucose.DateTime as datetime, Tag.Name as tag, -1 as carbs, -1 as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
				" FROM Tag, Reg_BloodGlucose" +
				" WHERE " +
				"Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
				" AND Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
				" AND Tag.Id = Reg_BloodGlucose.Id_Tag" +
				" UNION " +
				"SELECT Reg_Insulin.DateTime as datetime, Tag.Name as tag, -1 as carbs, -1 as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, -1 AS glycemia, -1 as glycemiaId" +
				" FROM  Tag, Reg_Insulin, Reg_BloodGlucose, Insulin" +
				" WHERE " +
				"Reg_Insulin.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
				" AND Reg_Insulin.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
				" AND Tag.Id = Reg_Insulin.Id_Tag AND Reg_Insulin.Id_Insulin = Insulin.Id" +
				")" +
				"WHERE datetime > '" + startDate + " 00:00:00' AND datetime < '" + endDate + " 23:59:59'" +
				"ORDER BY datetime DESC");



		return cursor;
	}


	public Cursor getWeightList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME};

		return storage.query(MyDiabetesContract.Regist.Weight.TABLE_NAME, rows, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + ">= ? AND " + MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + "<= ?", new String[]{startDate, endDate}, null, null, "DateTime DESC");
	}

	public Cursor getWeightList(String endDate, int numberOfItems) {
		String[] rows = new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME};
		return storage.query(MyDiabetesContract.Regist.Weight.TABLE_NAME, rows, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + "<= ?", new String[]{endDate}, null, null, "DateTime DESC", 20);
	}

	private Cursor getItemsList(String table, String[] rows, String startDate, String endDate) {
		return storage.query(table, rows, "DateTime >= ? AND DateTime <= ?", new String[]{startDate, endDate}, null, null, "DateTime DESC");
	}

	private Cursor getItemsList(String table, String[] rows, String endDate, int numberOfItems) {
		return storage.query(table, rows, "DateTime <= ?", new String[]{endDate}, null, null, "DateTime DESC", numberOfItems);
	}
}
