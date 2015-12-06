package pt.it.porto.mydiabetes.database;

import android.database.Cursor;

public class ListDataSource {
	public static final String ROW_VALUE = "Value";
	public static final String ROW_DATETIME = "DateTime";
	public static final String ROW_TABLE_NAME = "Table_name";

	private MyDiabetesStorage storage;

	public ListDataSource(MyDiabetesStorage storage) {
		this.storage = storage;
	}

	public Cursor getSimpleData(String table, String valueColumn, String dateTimeColumn, int limit) {
		return getMultiData(new String[]{table}, new String[]{valueColumn}, new String[]{dateTimeColumn}, limit);
	}

	public Cursor getMultiData(String[] tables, String[] valueColumns, String[] datetimeColumns) {
		return getMultiData(tables, valueColumns, datetimeColumns, -1);
	}

	public Cursor getMultiData(String[] tables, String[] valueColumns, String[] datetimeColumns, int limit) {
		StringBuilder query = new StringBuilder(100);
		query.append("SELECT * FROM (");
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
			query.append(" FROM ");
			query.append(tables[i]);
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
		return storage.rawQuert(query.toString());
	}
}
