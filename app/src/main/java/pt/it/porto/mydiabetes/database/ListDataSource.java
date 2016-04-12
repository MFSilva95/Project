package pt.it.porto.mydiabetes.database;

import android.database.Cursor;
import android.text.TextUtils;

public class ListDataSource {
	public static final String ROW_VALUE = "Value";
	public static final String ROW_DATE = "Date";
	public static final String ROW_TIME = "Time";
	public static final String ROW_DATETIME = "DateTime";
	public static final String ROW_EXTRAS = "Extras";
	public static final String ROW_TABLE_NAME = "Table_name";
	public static final String ROW_ID = "Id";

	private MyDiabetesStorage storage;

	public ListDataSource(MyDiabetesStorage storage) {
		this.storage = storage;
	}

	public Cursor getSimpleData(String table, String valueColumn, String dateTimeColumn, String dateStart, String dateEnd, int limit) {
		return getMultiData(new String[]{table}, new String[]{valueColumn}, new String[]{dateTimeColumn}, null, dateStart, dateEnd, limit);
	}

	public Cursor getMultiData(String[] tables, String[] valueColumns, String[] datetimeColumns) {
		return getMultiData(tables, valueColumns, datetimeColumns, null, null, null, -1);
	}

	public Cursor getMultiData(String[] tables, String[] valueColumns, String[] datetimeColumns, String[] extras, String dateStart, String dateEnd, int limit) {
		StringBuilder query = new StringBuilder(100);
		query.append("SELECT ");
		query.append(ROW_VALUE);
		query.append(", strftime('%Y-%m-%d', ");
		query.append(ROW_DATETIME);
		query.append(") AS ");
		query.append(ROW_DATE);
		query.append(", strftime('%H:%M', ");
		query.append(ROW_DATETIME);
		query.append(") AS ");
		query.append(ROW_TIME);
		query.append(", ");
		query.append(ROW_TABLE_NAME);
		if (extras != null) {
			query.append(", ");
			query.append(ROW_EXTRAS);
		}
		query.append(", ");
		query.append(ROW_DATETIME);
		query.append(", ");
		query.append(ROW_ID);
		query.append(" FROM (");
		for (int i = 0; i < tables.length; i++) {
			query.append("SELECT ");
			query.append(valueColumns[i]);
			query.append(" AS ");
			query.append(ROW_VALUE);
			query.append(", ");
			query.append(datetimeColumns[i]);
			query.append(" AS ");
			query.append(ROW_DATETIME);
			query.append(", \"");
			query.append(tables[i]);
			query.append("\" AS ");
			query.append(ROW_TABLE_NAME);
			query.append(", ");
			query.append("Id");
			query.append(" AS ");
			query.append(ROW_ID);
			if (extras != null) {
				query.append(", ");
				if (!TextUtils.isEmpty(extras[i])) {
					query.append(extras[i]);
				} else {
					query.append("NULL");
				}
				query.append(" AS ");
				query.append(ROW_EXTRAS);
			}
			query.append(" FROM ");
			query.append(tables[i]);
			if (!TextUtils.isEmpty(dateStart) || !TextUtils.isEmpty(dateEnd)) {
				query.append(" WHERE ");
				if (!TextUtils.isEmpty(dateStart)) {
					query.append(ROW_DATETIME);
					query.append(">='");
					query.append(dateStart);
					query.append("'");
				}
				if (!TextUtils.isEmpty(dateStart) && !TextUtils.isEmpty(dateEnd)) {
					query.append(" AND ");
				}
				if (!TextUtils.isEmpty(dateEnd)) {
					query.append(ROW_DATETIME);
					query.append("<='");
					query.append(dateEnd);
					query.append("'");
				}
			}
			if (i != tables.length - 1) {
				query.append(" UNION ");
			}
		}
		query.append(") ORDER BY ");
		query.append(ROW_DATETIME);
		query.append(" DESC ");
		if (limit > 0) {
			query.append("LIMIT ");
			query.append(limit);
		}
		return storage.rawQuery(query.toString());
	}

	public Cursor getLogbookData(boolean showCarbs, boolean showGlycemia, boolean showInsulins, String dateStart, String dateEnd, int limit) {
		String query = "SELECT DISTINCT datetime, carbs, insulinVal, insulinName, glycemia, carbsId, insulinId, glycemiaId" +
				" FROM " +
				"(";
		if (showCarbs) {
			query += "SELECT Reg_CarboHydrate.DateTime as datetime, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
					" FROM Reg_CarboHydrate, Reg_Insulin, Reg_BloodGlucose, Insulin" +
					" WHERE Reg_CarboHydrate.DateTime = Reg_Insulin.DateTime" +
					" AND Reg_CarboHydrate.DateTime = Reg_BloodGlucose.DateTime" +
					" AND Reg_Insulin.Id_Insulin = Insulin.Id" +
					" UNION " +
					"SELECT Reg_CarboHydrate.DateTime as datetime, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, -1 AS glycemia, -1 as glycemiaId" +
					" FROM Reg_CarboHydrate, Reg_Insulin, Insulin" +
					" WHERE Reg_CarboHydrate.DateTime = Reg_Insulin.DateTime" +
					" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
					" AND Reg_Insulin.Id_Insulin = Insulin.Id" +
					" UNION " +
					"SELECT Reg_CarboHydrate.DateTime as datetime, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
					" FROM Reg_CarboHydrate, Reg_BloodGlucose" +
					" WHERE Reg_CarboHydrate.DateTime = Reg_BloodGlucose.DateTime " +
					" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
					" UNION " +
					"SELECT Reg_CarboHydrate.DateTime as datetime, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, -1 AS glycemia, -1 as glycemiaId" +
					" FROM Reg_CarboHydrate" +
					" WHERE Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
					" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)";
		}
		if (showGlycemia) {
			if (showCarbs) {
				query += " UNION ";
			}
			query += "SELECT Reg_BloodGlucose.DateTime as datetime, -1 as carbs, -1 as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
					" FROM Reg_Insulin, Reg_BloodGlucose, Insulin" +
					" WHERE Reg_BloodGlucose.DateTime = Reg_Insulin.DateTime " +
					" AND Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
					" AND Reg_Insulin.Id_Insulin = Insulin.Id" +
					" UNION " +
					"SELECT Reg_BloodGlucose.DateTime as datetime, -1 as carbs, -1 as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
					" FROM Reg_BloodGlucose" +
					" WHERE " +
					"Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
					" AND Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)";
		}
		if (showInsulins) {
			if (showCarbs || showGlycemia) {
				query += " UNION ";
			}
			query += "SELECT Reg_Insulin.DateTime as datetime, -1 as carbs, -1 as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, -1 AS glycemia, -1 as glycemiaId" +
					" FROM  Reg_Insulin, Reg_BloodGlucose, Insulin" +
					" WHERE " +
					"Reg_Insulin.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
					" AND Reg_Insulin.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
					" AND Reg_Insulin.Id_Insulin = Insulin.Id";
		}
		query += ")";
		query += "WHERE datetime > '" + dateStart + " 00:00:00' AND datetime < '" + dateEnd + " 23:59:59'" +
				"ORDER BY datetime DESC";


		return storage.rawQuery(query);
	}

	public Cursor getLogbookDataHeaders(boolean showCarbs, boolean showGlycemia, boolean showInsulins, String dateStart, String dateEnd, int limit) {
		String query = "SELECT DISTINCT strftime('%Y-%m', datetime) as A, count(datetime)" +
				" FROM " +
				"(";
		if (showCarbs) {
			query += "SELECT Reg_CarboHydrate.DateTime as datetime, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
					" FROM Reg_CarboHydrate, Reg_Insulin, Reg_BloodGlucose, Insulin" +
					" WHERE Reg_CarboHydrate.DateTime = Reg_Insulin.DateTime" +
					" AND Reg_CarboHydrate.DateTime = Reg_BloodGlucose.DateTime" +
					" AND Reg_Insulin.Id_Insulin = Insulin.Id" +
					" UNION " +
					"SELECT Reg_CarboHydrate.DateTime as datetime, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, -1 AS glycemia, -1 as glycemiaId" +
					" FROM Reg_CarboHydrate, Reg_Insulin, Insulin" +
					" WHERE Reg_CarboHydrate.DateTime = Reg_Insulin.DateTime" +
					" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
					" AND Reg_Insulin.Id_Insulin = Insulin.Id" +
					" UNION " +
					"SELECT Reg_CarboHydrate.DateTime as datetime, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
					" FROM Reg_CarboHydrate, Reg_BloodGlucose" +
					" WHERE Reg_CarboHydrate.DateTime = Reg_BloodGlucose.DateTime " +
					" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
					" UNION " +
					"SELECT Reg_CarboHydrate.DateTime as datetime, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, -1 AS glycemia, -1 as glycemiaId" +
					" FROM Reg_CarboHydrate" +
					" WHERE Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
					" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)";
		}
		if (showGlycemia) {
			if (showCarbs) {
				query += " UNION ";
			}
			query += "SELECT Reg_BloodGlucose.DateTime as datetime, -1 as carbs, -1 as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
					" FROM Reg_Insulin, Reg_BloodGlucose, Insulin" +
					" WHERE Reg_BloodGlucose.DateTime = Reg_Insulin.DateTime " +
					" AND Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
					" AND Reg_Insulin.Id_Insulin = Insulin.Id" +
					" UNION " +
					"SELECT Reg_BloodGlucose.DateTime as datetime, -1 as carbs, -1 as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
					" FROM Reg_BloodGlucose" +
					" WHERE " +
					"Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
					" AND Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)";
		}
		if (showInsulins) {
			if (showCarbs || showGlycemia) {
				query += " UNION ";
			}
			query += "SELECT Reg_Insulin.DateTime as datetime, -1 as carbs, -1 as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, -1 AS glycemia, -1 as glycemiaId" +
					" FROM  Reg_Insulin, Reg_BloodGlucose, Insulin" +
					" WHERE " +
					"Reg_Insulin.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
					" AND Reg_Insulin.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
					" AND Reg_Insulin.Id_Insulin = Insulin.Id";
		}
		query += ")";
		query += "WHERE datetime > '" + dateStart + " 00:00:00' AND datetime < '" + dateEnd + " 23:59:59'" +
				" GROUP BY A "+
				"ORDER BY A DESC";

		return storage.rawQuery(query);
	}
}
