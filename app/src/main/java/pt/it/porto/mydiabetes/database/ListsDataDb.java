package pt.it.porto.mydiabetes.database;

import android.database.Cursor;
import android.util.Log;

import java.util.LinkedList;

import pt.it.porto.mydiabetes.data.LogBookEntry;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.utils.DbUtils;

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

	public LinkedList<LogBookEntry> getLogBookByDate(String date) {
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
				"WHERE datetime LIKE '%" + date + "%'" +
				"ORDER BY datetime DESC");

		LinkedList<LogBookEntry> logBookEntries = new LinkedList<LogBookEntry>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			LogBookEntry tmp;
			int pox = 0;
			do {
				tmp = new LogBookEntry(
						cursor.getString(pox++),
						cursor.getString(pox++),
						cursor.getInt(pox++),
						cursor.getFloat(pox++),
						cursor.getString(pox++),
						cursor.getInt(pox++),
						cursor.getInt(pox++),
						cursor.getInt(pox++),
						cursor.getInt(pox));
				logBookEntries.add(tmp);

				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return logBookEntries;
		} else {
			cursor.close();
			return logBookEntries;
		}
	}


	public Cursor getWeightList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME};

		return storage.query(MyDiabetesContract.Regist.Weight.TABLE_NAME, rows, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + ">= ? AND " + MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + "<= ?", new String[]{startDate, endDate}, null, null, "DateTime DESC");
	}

	public Cursor getWeightList(String endDate, int numberOfItems) {
		String[] rows = new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME};
		return storage.query(MyDiabetesContract.Regist.Weight.TABLE_NAME, rows, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + "<= ?", new String[]{endDate}, null, null, "DateTime DESC", numberOfItems);
	}


	public Cursor getInsulinRegList(String startDate, String endDate) {
		Cursor cursor = storage.rawQuery("SELECT * " +
				"FROM" +
				"(SELECT Reg_Insulin.Id, Reg_Insulin.DateTime, Tag.Name, Reg_Insulin.Value, Insulin.Name, -1" +
				" FROM Reg_Insulin, Tag, Insulin" +
				" WHERE Reg_Insulin.Id_Tag = Tag.Id" +
				" AND Insulin.Id = Reg_Insulin.Id_Insulin" +
				" UNION " +
				"SELECT Reg_Insulin.Id, Reg_Insulin.DateTime, Tag.Name, Reg_Insulin.Value, Insulin.Name, Reg_BloodGlucose.Value" +
				" FROM Reg_Insulin, Tag, Insulin, Reg_BloodGlucose" +
				" WHERE Reg_Insulin.Id_Tag = Tag.Id" +
				" AND Reg_Insulin.Id_BloodGlucose= Reg_BloodGlucose.Id" +
				" AND Insulin.Id = Reg_Insulin.Id_Insulin" +
				")" +
				" WHERE DateTime > '" + startDate + " 00:00:00' AND DateTime < '" + endDate + " 23:59:59'" +
				" GROUP By Id" +
				" ORDER BY DateTime DESC");

		return cursor;
	}

	public Cursor getInsulinRegList(String endDate, int numberOfItems) {
		Cursor cursor = storage.rawQuery("SELECT * " +
				"FROM" +
				"(SELECT Reg_Insulin.Id, Reg_Insulin.DateTime, Tag.Name, Reg_Insulin.Value, Insulin.Name, -1" +
				" FROM Reg_Insulin, Tag, Insulin" +
				" WHERE Reg_Insulin.Id_Tag = Tag.Id" +
				" AND Insulin.Id = Reg_Insulin.Id_Insulin" +
				" UNION " +
				"SELECT Reg_Insulin.Id, Reg_Insulin.DateTime, Tag.Name, Reg_Insulin.Value, Insulin.Name, Reg_BloodGlucose.Value" +
				" FROM Reg_Insulin, Tag, Insulin, Reg_BloodGlucose" +
				" WHERE Reg_Insulin.Id_Tag = Tag.Id" +
				" AND Reg_Insulin.Id_BloodGlucose= Reg_BloodGlucose.Id" +
				" AND Insulin.Id = Reg_Insulin.Id_Insulin" +
				")" +
				" WHERE DateTime < '" + endDate + " 23:59:59'" +
				" GROUP By Id" +
				" ORDER BY DateTime DESC" +
				" LIMIT " + String.valueOf(numberOfItems));

		return cursor;
	}

	public Cursor getCarbsRegList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_ID, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_VALUE, MyDiabetesContract.Tag.COLUMN_NAME_NAME};
		Cursor cursor = storage.rawQuery("SELECT " + DbUtils.toString(rows) +
				" FROM " + DbUtils.toString(new String[]{MyDiabetesContract.Regist.CarboHydrate.TABLE_NAME, MyDiabetesContract.Tag.TABLE_NAME}) +
				" WHERE " + MyDiabetesContract.Tag.COLUMN_NAME_ID + "=" + MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_TAG_ID +
				" AND " + MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME + " > '" + startDate + " 00:00:00'" +
				" AND " + MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME + " < '" + endDate + " 23:59:59'" +
				" ORDER BY " + MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME + " DESC");
		return cursor;
	}

	public Cursor getCarbsRegList(String endDate, int numberOfItems) {
		String[] rows = new String[]{MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_ID, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_VALUE, MyDiabetesContract.Tag.COLUMN_NAME_NAME};
		Cursor cursor = storage.rawQuery("SELECT " + DbUtils.toString(rows) +
				" FROM " + DbUtils.toString(new String[]{MyDiabetesContract.Regist.CarboHydrate.TABLE_NAME, MyDiabetesContract.Tag.TABLE_NAME}) +
				" WHERE " + MyDiabetesContract.Tag.COLUMN_NAME_ID + "=" + MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_TAG_ID +
				" AND " + MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME + " < '" + endDate + " 23:59:59'" +
				" ORDER BY " + MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME + " DESC" +
				" LIMIT " + String.valueOf(numberOfItems));
		return cursor;
	}

	public Cursor getGlycemiaList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_ID, MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_VALUE, MyDiabetesContract.Tag.COLUMN_NAME_NAME};
		Cursor cursor = storage.rawQuery("SELECT " + DbUtils.toString(rows) +
				" FROM " + DbUtils.toString(new String[]{MyDiabetesContract.Regist.BloodGlucose.TABLE_NAME, MyDiabetesContract.Tag.TABLE_NAME}) +
				" WHERE " + MyDiabetesContract.Tag.COLUMN_NAME_ID + "=" + MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_TAG_ID +
				" AND " + MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_DATETIME + " > '" + startDate + " 00:00:00'" +
				" AND " + MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_DATETIME + " < '" + endDate + " 23:59:59'" +
				" ORDER BY " + MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_DATETIME + " DESC");
		return cursor;
	}

	public Cursor getGlycemiaList(String endDate, int numberOfItems) {
		String[] rows = new String[]{MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_ID, MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_VALUE, MyDiabetesContract.Tag.COLUMN_NAME_NAME};
		Cursor cursor = storage.rawQuery("SELECT " + DbUtils.toString(rows) +
				" FROM " + DbUtils.toString(new String[]{MyDiabetesContract.Regist.BloodGlucose.TABLE_NAME, MyDiabetesContract.Tag.TABLE_NAME}) +
				" WHERE " + MyDiabetesContract.Tag.COLUMN_NAME_ID + "=" + MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_TAG_ID +
				" AND " + MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_DATETIME + " < '" + endDate + " 23:59:59'" +
				" ORDER BY " + MyDiabetesContract.Regist.BloodGlucose.COLUMN_NAME_DATETIME + " DESC" +
				" LIMIT " + String.valueOf(numberOfItems));
		return cursor;
	}


	public Cursor getExerciseRegList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.Exercise.COLUMN_NAME_ID, MyDiabetesContract.Regist.Exercise.COLUMN_NAME_START_DATETIME, MyDiabetesContract.Regist.Exercise.COLUMN_NAME_EXERCISE_NAME, MyDiabetesContract.Regist.Exercise.COLUMN_NAME_DURATION, MyDiabetesContract.Regist.Exercise.COLUMN_NAME_EFFORT};
		Cursor cursor = storage.rawQuery("SELECT " + DbUtils.toString(rows) +
				" FROM " + MyDiabetesContract.Regist.Exercise.TABLE_NAME +
				" WHERE " + MyDiabetesContract.Regist.Exercise.COLUMN_NAME_START_DATETIME + " > '" + startDate + " 00:00:00'" +
				" AND " + MyDiabetesContract.Regist.Exercise.COLUMN_NAME_START_DATETIME + " < '" + endDate + " 23:59:59'" +
				" ORDER BY " + MyDiabetesContract.Regist.Exercise.COLUMN_NAME_START_DATETIME + " DESC");
		return cursor;
	}

	public Cursor getBloodPressureRegList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.BloodPressure.COLUMN_NAME_ID, MyDiabetesContract.Regist.BloodPressure.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.BloodPressure.COLUMN_NAME_SYSTOLIC, MyDiabetesContract.Regist.BloodPressure.COLUMN_NAME_DIASTOLIC};
		Cursor cursor = storage.rawQuery("SELECT " + DbUtils.toString(rows) +
				" FROM " + MyDiabetesContract.Regist.BloodPressure.TABLE_NAME +
				" WHERE " + MyDiabetesContract.Regist.BloodPressure.COLUMN_NAME_DATETIME + " > '" + startDate + " 00:00:00'" +
				" AND " + MyDiabetesContract.Regist.BloodPressure.COLUMN_NAME_DATETIME + " < '" + endDate + " 23:59:59'" +
				" ORDER BY " + MyDiabetesContract.Regist.BloodPressure.COLUMN_NAME_DATETIME + " DESC");
		return cursor;
	}

	public Cursor getCholesterolRegList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.Cholesterol.COLUMN_NAME_ID, MyDiabetesContract.Regist.Cholesterol.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.Cholesterol.COLUMN_NAME_VALUE};
		Cursor cursor = storage.rawQuery("SELECT " + DbUtils.toString(rows) +
				" FROM " + MyDiabetesContract.Regist.Cholesterol.TABLE_NAME +
				" WHERE " + MyDiabetesContract.Regist.Cholesterol.COLUMN_NAME_DATETIME + " > '" + startDate + " 00:00:00'" +
				" AND " + MyDiabetesContract.Regist.Cholesterol.COLUMN_NAME_DATETIME + " < '" + endDate + " 23:59:59'" +
				" ORDER BY " + MyDiabetesContract.Regist.Cholesterol.COLUMN_NAME_DATETIME + " DESC");
		return cursor;
	}

	public Cursor getHbA1cRegList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.A1c.COLUMN_NAME_ID, MyDiabetesContract.Regist.A1c.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.A1c.COLUMN_NAME_VALUE};
		Cursor cursor = storage.rawQuery("SELECT " + DbUtils.toString(rows) +
				" FROM " + MyDiabetesContract.Regist.A1c.TABLE_NAME +
				" WHERE " + MyDiabetesContract.Regist.A1c.COLUMN_NAME_DATETIME + " > '" + startDate + " 00:00:00'" +
				" AND " + MyDiabetesContract.Regist.A1c.COLUMN_NAME_DATETIME + " < '" + endDate + " 23:59:59'" +
				" ORDER BY " + MyDiabetesContract.Regist.A1c.COLUMN_NAME_DATETIME + " DESC");
		return cursor;
	}

	public Cursor getDiseaseRegList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.Disease.COLUMN_NAME_ID, MyDiabetesContract.Regist.Disease.COLUMN_NAME_DISEASE, MyDiabetesContract.Regist.Disease.COLUMN_NAME_DATE_START, MyDiabetesContract.Regist.Disease.COLUMN_NAME_DATE_END};
		Cursor cursor = storage.rawQuery("SELECT " + DbUtils.toString(rows) +
				" FROM " + MyDiabetesContract.Regist.Disease.TABLE_NAME +
				" WHERE " + MyDiabetesContract.Regist.Disease.COLUMN_NAME_DATE_START + " > '" + startDate + " 00:00:00'" +
				" AND " + MyDiabetesContract.Regist.Disease.COLUMN_NAME_DATE_START + " < '" + endDate + " 23:59:59'" +
				" ORDER BY " + MyDiabetesContract.Regist.Disease.COLUMN_NAME_DATE_START + " DESC");
		return cursor;
	}

		private Cursor getItemsList(String table, String[] rows, String startDate, String endDate) {
		return storage.query(table, rows, "DateTime >= ? AND DateTime <= ?", new String[]{startDate, endDate}, null, null, "DateTime DESC");
	}

	private Cursor getItemsList(String table, String[] rows, String endDate, int numberOfItems) {
		return storage.query(table, rows, "DateTime <= ?", new String[]{endDate}, null, null, "DateTime DESC", numberOfItems);
	}

}
