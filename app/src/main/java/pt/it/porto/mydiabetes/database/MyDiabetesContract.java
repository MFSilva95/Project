package pt.it.porto.mydiabetes.database;

public class MyDiabetesContract {

	public static class Insulin {
		public static final String TABLE_NAME = "Insulin";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_NAME = "Name";
		public static final String COLUMN_NAME_TYPE = "Type";
		public static final String COLUMN_NAME_ACTION = "Action";
		public static final String COLUMN_NAME_DURATION = "Duration";

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

	public static class UserInfo {
		public static final String TABLE_NAME = "UserInfo";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_NAME = "Name";
		public static final String COLUMN_NAME_DIABETES_TYPE = "DType";
		public static final String COLUMN_NAME_RATIO_INSULIN = "InsulinRatio";
		public static final String COLUMN_NAME_RATIO_CARBS = "CarbsRatio";
		public static final String COLUMN_NAME_RANGE_LOWER = "LowerRange";
		public static final String COLUMN_NAME_RANGE_HIGHER = "HigherRange";
		public static final String COLUMN_NAME_BIRTHDATE = "BDate";
		public static final String COLUMN_NAME_GENDER = "Gender";
		public static final String COLUMN_NAME_HEIGHT = "Height";
		public static final String COLUMN_NAME_LAST_UPDATE = "DateTimeUpdate";
	}

	public static class Tag {

		public static final String TABLE_NAME = "Tag";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_NAME = "Name";
		public static final String COLUMN_NAME_TIME_START = "TimeStart";
		public static final String COLUMN_NAME_TIME_END = "TimeEnd";
	}


	public static class Exercise {
		public static final String TABLE_NAME = "Exercise";
		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_NAME = "Name";
	}

	public static class Medicine {
		public static final String TABLE_NAME = "Medicine";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_NAME = "Name";
		public static final String COLUMN_NAME_UNITS = "Units";
	}

	public static class Disease {
		public static final String TABLE_NAME = "Disease";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_NAME = "Name";
	}

	public static class Feature {
		public static final String TABLE_NAME = "Feature";

		public static final String COLUMN_NAME_ID = "Id";
		public static final String COLUMN_NAME_NAME = "Name";
		public static final String COLUMN_NAME_ACTIVATED = "Activated";
	}

	public static class Regist {

		public static class Insulin {
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

		public static class Weight {
			public static final String TABLE_NAME = "Reg_Weight";

			public static final String COLUMN_NAME_ID = "Id";
			public static final String COLUMN_NAME_USER_ID = "Id_User";
			public static final String COLUMN_NAME_VALUE = "Value";
			public static final String COLUMN_NAME_DATETIME = "DateTime";
			public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
		}

		public static class Cholesterol {
			public static final String TABLE_NAME = "Reg_Cholesterol";

			public static final String COLUMN_NAME_ID = "Id";
			public static final String COLUMN_NAME_USER_ID = "Id_User";
			public static final String COLUMN_NAME_VALUE = "Value";
			public static final String COLUMN_NAME_DATETIME = "DateTime";
			public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
		}

		public static class CarboHydrate {
			public static final String TABLE_NAME = "Reg_CarboHydrate";

			public static final String COLUMN_NAME_ID = "Id";
			public static final String COLUMN_NAME_USER_ID = "Id_User";
			public static final String COLUMN_NAME_VALUE = "Value";
			public static final String COLUMN_NAME_PHOTO_PATH = "PhotoPath";
			public static final String COLUMN_NAME_DATETIME = "DateTime";
			public static final String COLUMN_NAME_TAG_ID = "Id_Tag";
			public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
		}

		public static class BloodGlucose {
			public static final String TABLE_NAME = "Reg_BloodGlucose";

			public static final String COLUMN_NAME_ID = "Id";
			public static final String COLUMN_NAME_USER_ID = "Id_User";
			public static final String COLUMN_NAME_VALUE = "Value";
			public static final String COLUMN_NAME_DATETIME = "DateTime";
			public static final String COLUMN_NAME_TAG_ID = "Id_Tag";
			public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
		}

		public static class Exercise {
			public static final String TABLE_NAME = "Reg_Exercise";

			public static final String COLUMN_NAME_ID = "Id";
			public static final String COLUMN_NAME_USER_ID = "Id_User";
			public static final String COLUMN_NAME_EXERCISE_NAME = "Exercise";
			public static final String COLUMN_NAME_DURATION = "Duration";
			public static final String COLUMN_NAME_EFFORT = "Effort";
			public static final String COLUMN_NAME_START_DATETIME = "StartDateTime";
			public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
		}

		public static class BloodPressure {
			public static final String TABLE_NAME = "Reg_BloodPressure";

			public static final String COLUMN_NAME_ID = "Id";
			public static final String COLUMN_NAME_USER_ID = "Id_User";
			public static final String COLUMN_NAME_SYSTOLIC = "Systolic";
			public static final String COLUMN_NAME_DIASTOLIC = "Diastolic";
			public static final String COLUMN_NAME_DATETIME = "DateTime";
			public static final String COLUMN_NAME_TAG_ID = "Id_Tag";
			public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
		}

		public static class Disease {
			public static final String TABLE_NAME = "Reg_Disease";

			public static final String COLUMN_NAME_ID = "Id";
			public static final String COLUMN_NAME_USER_ID = "Id_User";
			public static final String COLUMN_NAME_DISEASE = "Disease";
			public static final String COLUMN_NAME_DATE_START = "StartDate";
			public static final String COLUMN_NAME_DATE_END = "EndDate";
			public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
		}

		public static class Medication {
			public static final String TABLE_NAME = "Reg_Medication";

			public static final String COLUMN_NAME_ID = "Id";
			public static final String COLUMN_NAME_USER_ID = "Id_User";
			public static final String COLUMN_NAME_MEDICINE_ID = "Id_Medicine";
			public static final String COLUMN_NAME_DISEASE_ID = "Id_Disease";
			public static final String COLUMN_NAME_VALUE = "Value";
			public static final String COLUMN_NAME_DATETIME = "DateTime";
			public static final String COLUMN_NAME_LAST_UPDATE = "LastUpdate";
			public static final String COLUMN_NAME_TAG_ID = "Id_Tag";
			public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
		}

		public static class A1c {
			public static final String TABLE_NAME = "Reg_A1c";

			public static final String COLUMN_NAME_ID = "Id";
			public static final String COLUMN_NAME_USER_ID = "Id_User";
			public static final String COLUMN_NAME_VALUE = "Value";
			public static final String COLUMN_NAME_DATETIME = "DateTime";
			public static final String COLUMN_NAME_NOTE_ID = "Id_Note";
		}
	}


}
