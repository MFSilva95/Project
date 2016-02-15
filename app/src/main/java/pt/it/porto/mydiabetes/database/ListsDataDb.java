package pt.it.porto.mydiabetes.database;

import android.database.Cursor;

public class ListsDataDb {


	private MyDiabetesStorage storage;

	public ListsDataDb(MyDiabetesStorage storage) {
		this.storage = storage;
	}


	public Cursor getLogbookList(String startDate, String endDate) {
		Cursor cursor = storage.rawQuery("SELECT ins.datetime, ins.Id_user, ins.Id_Tag, ins.Id_Note, ch.Id, ch.value, ch.PhotoPath, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose,ins.Target_BG,ins.Value, bg.Value" +
				" from Reg_CarboHydrate as ch, Reg_Insulin as ins, Reg_BloodGlucose as bg" +
				" where ch.datetime >'" + startDate + " 00:00:00' AND ch.datetime < '" + endDate + " 23:59:59'" +
				" AND ins.datetime >'" + startDate + " 00:00:00' AND ins.datetime < '" + endDate + " 23:59:59'" +
				" AND ch.datetime = ins.datetime AND ch.Id_User=ins.Id_User AND ins.Id_BloodGlucose is not NULL AND bg.Id = ins.Id_BloodGlucose" +
				" UNION" +
				" SELECT ins.DateTime, ins.Id_User, ins.Id_Tag, ins.Id_Note, null, null, null, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose, ins.Target_BG, ins.value, bg.value" +
				" FROM Reg_Insulin as ins, Reg_BloodGlucose as bg" +
				" WHERE ins.datetime >'" + startDate + " 00:00:00' AND ins.datetime < '" + endDate + " 23:59:59'" +
				" AND bg.datetime >'" + startDate + " 00:00:00' AND bg.datetime < '" + endDate + " 23:59:59'" +
				" AND bg.Id=ins.Id_BloodGlucose AND ins.DateTime not in" +
				" (SELECT ch.DateTime From Reg_CarboHydrate as ch)" +
				" UNION" +
				" SELECT bg.DateTime, bg.Id_User, bg.Id_Tag, bg.Id_Note, null, null, null, null, null, bg.Id, null, null, bg.value" +
				" FROM Reg_BloodGlucose as bg" +
				" WHERE bg.datetime >'" + startDate + " 00:00:00' AND bg.datetime < '" + endDate + " 23:59:59'" +
				" AND bg.datetime not in" +
				" (SELECT datetime from Reg_Insulin " +
				" union" +
				" SELECT datetime from Reg_CarboHydrate)" +
				" UNION" +
				" SELECT ch.DateTime, ch.Id_User, ch.Id_Tag, ch.Id_Note, ch.Id, ch.Value, ch.PhotoPath, null, null, null, null, null, null" +
				" FROM Reg_CarboHydrate as ch" +
				" WHERE ch.datetime >'" + startDate + " 00:00:00' AND ch.datetime < '" + endDate + " 23:59:59'" +
				" AND ch.datetime not in" +
				" (SELECT datetime from Reg_Insulin " +
				"union" +
				" SELECT datetime from Reg_BloodGlucose)" +
				" UNION" +
				" SELECT ins.DateTime, ins.DateTime, ins.Id_Tag, ins.Id_Note, null, null, null, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose, ins.Target_BG, ins.Value, null" +
				" FROM Reg_Insulin as ins" +
				" WHERE ins.datetime >'" + startDate + " 00:00:00' AND ins.datetime < '" + endDate + " 23:59:59'" +
				" AND datetime not in" +
				" (SELECT datetime from Reg_BloodGlucose" +
				" union" +
				" SELECT ch.DateTime From Reg_CarboHydrate as ch)" +
				" UNION" +
				" SELECT ins.datetime, ins.Id_user, ins.Id_Tag, ins.Id_Note, ch.Id, ch.value, ch.PhotoPath, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose,ins.Target_BG,ins.Value, null" +
				" from Reg_CarboHydrate as ch, Reg_Insulin as ins" +
				" where ch.datetime >'" + startDate + " 00:00:00' AND ch.datetime < '" + endDate + " 23:59:59'" +
				" AND ins.datetime >'" + startDate + " 00:00:00' AND ins.datetime < '" + endDate + " 23:59:59'" +
				" AND ch.datetime = ins.datetime AND ins.datetime not in " +
				" (SELECT datetime from Reg_BloodGlucose)" +
				" UNION" +
				" SELECT ch.datetime, ch.Id_user, ch.Id_Tag, ch.Id_Note, ch.Id, ch.value, ch.PhotoPath, null, null, bg.Id, null, null, bg.value" +
				" from Reg_CarboHydrate as ch, Reg_BloodGlucose as bg" +
				" where ch.datetime >'" + startDate + " 00:00:00' AND ch.datetime < '" + endDate + " 23:59:59'" +
				" AND bg.datetime >'" + startDate + " 00:00:00' AND bg.datetime < '" + endDate + " 23:59:59'" +
				" AND ch.datetime = bg.datetime AND ch.datetime not in " +
				" (SELECT datetime from Reg_Insulin)" +
				" ORDER BY datetime DESC");

		return cursor;
	}


	public Cursor getWeightList(String startDate, String endDate) {
		String[] rows = new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE
				, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME};

		return storage.query(MyDiabetesContract.Regist.Weight.TABLE_NAME, rows,
				MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + ">= ? AND " + MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + "<= ?",
				new String[]{startDate, endDate}, null, null, "DateTime DESC");
	}

	public Cursor getWeightList(String endDate, int numberOfItems) {
		String[] rows = new String[]{MyDiabetesContract.Regist.Weight.COLUMN_NAME_ID, MyDiabetesContract.Regist.Weight.COLUMN_NAME_VALUE
				, MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME};
		return storage.query(MyDiabetesContract.Regist.Weight.TABLE_NAME, rows,
				MyDiabetesContract.Regist.Weight.COLUMN_NAME_DATETIME + "<= ?", new String[]{endDate}, null, null, "DateTime DESC", 20);
	}

	private Cursor getItemsList(String table, String[] rows, String startDate, String endDate) {
		return storage.query(table, rows, "DateTime >= ? AND DateTime <= ?", new String[]{startDate, endDate}, null, null, "DateTime DESC");
	}

	private Cursor getItemsList(String table, String[] rows, String endDate, int numberOfItems) {
		return storage.query(table, rows, "DateTime <= ?", new String[]{endDate}, null, null, "DateTime DESC", numberOfItems);
	}
}
