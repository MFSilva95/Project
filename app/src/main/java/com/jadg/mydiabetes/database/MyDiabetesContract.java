package com.jadg.mydiabetes.database;

public class MyDiabetesContract {

	public static class Insulin {
		public static final String TABLE_NAME = "Insulin";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_NAME = "Name";
		public static final String COLUMN_NAME_TYPE = "Type";
		public static final String COLUMN_NAME_ACTION = "Action";
		public static final String COLUMN_NAME_DURATION = "Duration";

	}


	public static class Reg_Insulin {
		public static final String TABLE_NAME = "Reg_Insulin";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_USER_ID = "Id_User";
		public static final String COLUMN_NAME_INSULIN_ID = "Id_Insulin";
		public static final String COLUMN_NAME_BLOODGLUCOSE_ID = "Id_BloodGlucose";
		public static final String COLUMN_NAME_DATETIME = "DateTime";
		public static final String COLUMN_NAME_DURATION = "Duration";
		public static final String COLUMN_NAME_TARGET_BG = "Target_BG";
		public static final String COLUMN_NAME_VALUE = "Value";
		public static final String COLUMN_NAME_TAG_ID = "Id_Tag";
		public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
	}


	public static class Reg_Weight {
		public static final String TABLE_NAME = "Reg_Weight";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_USER_ID = "Id_User";
		public static final String COLUMN_NAME_VALUE = "Value";
		public static final String COLUMN_NAME_DATETIME = "DateTime";
		public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
	}


	public static class Note {
		public static final String TABLE_NAME = "Note";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_Note = "Note";
	}

	public static class BG_Target {
		public static final String TABLE_NAME = "BG_Target";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_NAME = "Name";
		public static final String COLUMN_NAME_TIME_START = "TimeStart";
		public static final String COLUMN_NAME_TIME_END = "TimeEnd";
		public static final String COLUMN_NAME_VALUE = "Value";
	}

}
