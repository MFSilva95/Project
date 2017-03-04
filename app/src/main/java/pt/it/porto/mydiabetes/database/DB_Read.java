package pt.it.porto.mydiabetes.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.Disease;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.HbA1cRec;
import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.InsulinTarget;
import pt.it.porto.mydiabetes.data.LogbookData;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.data.WeightRec;

@SuppressLint("UseSparseArrays")
public class DB_Read {

	final Context myContext;
	final SQLiteDatabase myDB;

	public DB_Read(Context context) {
		super();
		DB_Handler db = new DB_Handler(context);
		this.myContext = context;
		this.myDB = db.getReadableDatabase();
	}

	public void close() {
		myDB.close();
		Log.d("Close", "DB_Read");
	}


	public boolean MyData_HasData() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM UserInfo", null);
		boolean result = cursor.getCount() > 0;
		cursor.close();
		return result;
	}

	public UserInfo MyData_Read() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM UserInfo", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			UserInfo userInfo = new UserInfo(cursor);
			cursor.close();
			return userInfo;
		} else {
			cursor.close();
			return null;
		}
	}

	public int getId() {
		Cursor cursor = myDB.rawQuery("SELECT " + MyDiabetesContract.UserInfo.COLUMN_NAME_ID + " FROM UserInfo", null);
		int val = -1;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			val = Integer.parseInt(cursor.getString(0));
		}
		cursor.close();
		return val;
	}


	//--------------- TAG ------------------------
	public int Tag_GetIdByName(String name) {
		int n;
		Cursor cursor = myDB.rawQuery("SELECT Id FROM Tag where Name='" + name + "'", null);
		cursor.moveToFirst();
		n = cursor.getInt(0);
		cursor.close();
		return n;
	}

	public Tag Tag_GetById(int id) {
		Tag tag = new Tag();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Tag where Id='" + id + "'", null);
		cursor.moveToFirst();
		tag.setId(cursor.getInt(0));
		tag.setName(cursor.getString(1));
		tag.setStart(cursor.getString(2));
		tag.setEnd(cursor.getString(3));
		cursor.close();
		return tag;
	}

	public Tag Tag_GetByTime(String time) {
		Tag tag = new Tag();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Tag WHERE  " + "(TimeStart < TimeEnd AND '" + time + "' >= TimeStart AND '" + time + "' <= TimeEnd)" +
				"OR " + "(TimeStart > TimeEnd AND('" + time + "' >= TimeStart OR '" + time + "' <= TimeEnd ))" + ";", null);
		//Cursor cursor = myDB.rawQuery("SELECT * FROM Tag where Id='" + id + "'", null);
		Log.d("tag by time", String.valueOf(cursor.getCount()));
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			tag.setId(cursor.getInt(0));
			tag.setName(cursor.getString(1));
			tag.setStart(cursor.getString(2));
			tag.setEnd(cursor.getString(3));
		} else {
			cursor = myDB.rawQuery("SELECT * FROM Tag WHERE  " + "TimeStart IS NULL AND TimeEnd IS NULL ;", null);
			cursor.moveToFirst();
			tag.setId(cursor.getInt(0));
			tag.setName(cursor.getString(1));
			tag.setStart(cursor.getString(2));
			tag.setEnd(cursor.getString(3));
		}
		cursor.close();
		return tag;
	}

	public ArrayList<Tag> Tag_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Tag", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<Tag> tags = new ArrayList<Tag>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Tag t;
			do {
				t = new Tag();
				t.setId(cursor.getInt(0));
				t.setName(cursor.getString(1));
				t.setStart(cursor.getString(2));
				t.setEnd(cursor.getString(3));
				tags.add(t);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return tags;
		} else {
			cursor.close();
			return tags;
		}
	}


	//------------------- DISEASE ---------------------
	public ArrayList<Disease> Disease_GetAll() {
		ArrayList<Disease> AllReads = new ArrayList<Disease>();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Disease", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Disease tmp;
			do {
				tmp = new Disease();
				tmp.setId(cursor.getInt(0));
				tmp.setName(cursor.getString(1));
				AllReads.add(tmp);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return AllReads;
		} else {
			cursor.close();
			return AllReads;
		}
	}

	public int Disease_GetIdByName(String name) {
		int n;
		Cursor cursor = myDB.rawQuery("SELECT Id FROM Disease where Name='" + name + "'", null);
		cursor.moveToFirst();
		n = cursor.getInt(0);
		cursor.close();
		return n;
	}

	public boolean Disease_ExistName(String name) {
		Cursor cursor = myDB.rawQuery("SELECT Name FROM Disease where Name='" + name + "'", null);
		return cursor.getCount() > 0;
	}


	//------------------ GLYCEMIA -------------------
	public HashMap<Integer, String[]> Glycemia_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		String[] row;
		HashMap<Integer, String[]> glycemias = new HashMap<Integer, String[]>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				row = new String[3];
				row[0] = cursor.getString(2); //Value
				row[1] = cursor.getString(3); //DateTime
				row[2] = cursor.getString(4); //Id_Tag
				//row[3] = cursor.getString(5); //Id_Note
				glycemias.put(cursor.getInt(0), row);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return glycemias;
		} else {
			cursor.close();
			return null;
		}
	}

	public ArrayList<GlycemiaRec> Glycemia_GetByDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose WHERE DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<GlycemiaRec> allreads = new ArrayList<GlycemiaRec>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			GlycemiaRec tmp;
			do {
				tmp = new GlycemiaRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getInt(2));
				String t = cursor.getString(3);
				tmp.setDateTime(t);
				tmp.setIdTag(cursor.getInt(4));
				tmp.setIdNote((!cursor.isNull(5)) ? cursor.getInt(5) : -1);

				allreads.add(tmp);

				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return allreads;
		} else {
			cursor.close();
			return allreads;
		}
	}

	@Nullable
	public GlycemiaRec Glycemia_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose where Id='" + id + "'", null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		GlycemiaRec g = new GlycemiaRec();
		g.setId(cursor.getInt(0));
		g.setIdUser(cursor.getInt(1));
		g.setValue(cursor.getInt(2));
		String t = cursor.getString(3);
		g.setDateTime(t);
		g.setIdTag(cursor.getInt(4));
		g.setIdNote((!cursor.isNull(5)) ? cursor.getInt(5) : -1);

		cursor.close();

		return g;
	}


	//---------------------- INSULIN ------------------------------
	public HashMap<Integer, String[]> Insulin_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Insulin", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		HashMap<Integer, String[]> insulins = new HashMap<Integer, String[]>();
		String[] row;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				row = new String[4];
				row[0] = cursor.getString(1); //Name
				row[1] = cursor.getString(2); //Type
				row[2] = cursor.getString(3); //Action
				row[3] = String.valueOf(cursor.getDouble(4)); //Duration
				insulins.put(cursor.getInt(0), row);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return insulins;
		} else {
			cursor.close();
			return null;
		}
	}

	public boolean Insulin_HasInsulins() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Insulin", null);
		return cursor.getCount() > 0;

	}

	public boolean Insulin_NameExists(String name) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Insulin where name='" + name + "'", null);
		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * TODO: Correct the assumption regarding the praseable in in a text field
	 * Check to see what was the previous usage
	 *
	 * @param name
	 * @return
	 */
	public int Insulin_GetActionTypeByName(String name) {
		Cursor cursor = myDB.rawQuery("SELECT action  FROM Insulin where name ='" + name + "'", null);
		int retVal = -1;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			try {
				retVal = Integer.parseInt(cursor.getString(0));
			} catch (NumberFormatException nfe) {
				// retVal will get -1
				Log.e("DB_Read", "Insulin_GetActionTypeByName: Read a text that was not a number from action" + nfe);
			}
		}
		return retVal;
	}

	public HashMap<Integer, String> Insulin_GetAllNames() {
		Cursor cursor = myDB.rawQuery("SELECT Id, Name FROM Insulin", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		HashMap<Integer, String> insulins = new HashMap<Integer, String>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				insulins.put(cursor.getInt(0), cursor.getString(1));
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return insulins;
		} else {
			cursor.close();
			return null;
		}
	}

	@Nullable
	public Insulin Insulin_GetByName(String name) {
		Insulin insulin = new Insulin();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Insulin where Name='" + name + "'", null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}
		insulin.setId(cursor.getInt(0));
		insulin.setName(cursor.getString(1));
		insulin.setType(cursor.getString(2));
		insulin.setAction(cursor.getString(3));
		insulin.setDuration(cursor.getDouble(4));

		cursor.close();
		return insulin;
	}

	public int[] InsulinReg_GetLastHourAndQuantity() {
		Cursor cursor = myDB.rawQuery("SELECT ins.action, reg.DateTime, strftime('%H',reg.DateTime), strftime('%M',reg.DateTime), reg.Value FROM Reg_Insulin as reg, Insulin as ins WHERE  reg.Id_Insulin=ins.Id And reg.DateTime > DateTime('now','-5 HOURS') order by 2 DESC", null);

		int[] result = {-1, -1, -1, -1};

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();


			result[0] = Integer.parseInt(cursor.getString(0));
			result[1] = cursor.getInt(2);
			result[2] = cursor.getInt(3);
			result[3] = cursor.getInt(4);

		}


		cursor.close();
		return result;
	}

	public int[] InsulinReg_GetLastHourAndQuantity(String time, String date) {
		Cursor cursor;
		if (time.equalsIgnoreCase("now")) {
			cursor = myDB.rawQuery("SELECT ins.action, reg.DateTime, strftime('%H',reg.DateTime), strftime('%M',reg.DateTime), reg.Value " +
					"FROM Reg_Insulin as reg, Insulin as ins " +
					"WHERE  reg.Id_Insulin=ins.Id " +
					"And reg.DateTime > DateTime('now', 'localtime','-5 HOURS') " +
					"AND reg.DateTime < Datetime('now', 'localtime', '-1 MINUTE')" +
					"order by 2 DESC", null);
		} else {
			cursor = myDB.rawQuery("SELECT ins.action, reg.DateTime, strftime('%H',reg.DateTime), strftime('%M',reg.DateTime), reg.Value" +
					" FROM Reg_Insulin as reg, Insulin as ins WHERE reg.Id_Insulin=ins.Id AND " +
					"reg.DateTime > Datetime(date('" + (date == null ? "now" : date) + "'), '" + time + "','-5 HOURS') " +
					"AND reg.DateTime <= Datetime(date('" + (date == null ? "now" : date) + "'), '" + time + "', '-1 MINUTE')" // fix selecting it self as a iob
					+ "order by 2 DESC", null);
		}
		int[] result = {-1, -1, -1, -1};

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();


			result[0] = Integer.parseInt(cursor.getString(0));
			result[1] = Integer.parseInt(cursor.getString(2));
			result[2] = Integer.parseInt(cursor.getString(3));
			result[3] = cursor.getInt(4);

		}


		cursor.close();
		return result;
	}

	public ArrayList<InsulinRec> InsulinReg_GetByDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT Id, Id_User, Id_BloodGlucose, Id_Insulin, DateTime, Target_BG, Value, Id_Tag, Id_Note FROM Reg_Insulin WHERE DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<InsulinRec> allreads = new ArrayList<InsulinRec>();
		InsulinRec insulin;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				Log.d("le insulin", String.valueOf(cursor.getPosition()));
				insulin = new InsulinRec();
				insulin.setId(cursor.getInt(0));
				insulin.setIdUser(cursor.getInt(1));
				insulin.setIdBloodGlucose((!cursor.isNull(2)) ? cursor.getInt(2) : -1);
				insulin.setIdInsulin(cursor.getInt(3));
				String t = cursor.getString(4);
				insulin.setDateTime(t);
				insulin.setTargetGlycemia(cursor.getInt(5));
				insulin.setInsulinUnits(cursor.getFloat(6));
				insulin.setIdTag(cursor.getInt(7));
				insulin.setIdNote((!cursor.isNull(8)) ? cursor.getInt(8) : -1);
				allreads.add(insulin);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return allreads;
		} else {
			cursor.close();
			return allreads;
		}
				
		
		/*
		
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		Log.d("From", from);
		Log.d("To", to);
		String[] row;
		HashMap<Integer, String[]> insulins = new HashMap<Integer, String[]>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				row = new String[6];
				row[0] = cursor.getString(1); //id insulin
				row[1] = cursor.getString(2); //DateTime
				row[2] = cursor.getString(3); //target bg
				row[3] = cursor.getString(4); //insulin value
				row[4] = cursor.getString(5); //glycemia value
				row[5] = cursor.getString(6); //id tag
				insulins.put(cursor.getInt(0), row);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return insulins;
		}
		else {
			cursor.close();
			return null;
		}*/
	}

	public Insulin Insulin_GetById(int id) {
		Insulin insulin = new Insulin();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Insulin where Id='" + id + "'", null);
		cursor.moveToFirst();
		insulin.setId(cursor.getInt(0));
		insulin.setName(cursor.getString(1));
		insulin.setType(cursor.getString(2));
		insulin.setAction(cursor.getString(3));
		insulin.setDuration(cursor.getDouble(4));

		cursor.close();
		return insulin;
	}

	@Nullable
	public InsulinRec InsulinReg_GetById(int id) {
		//Cursor cursor = myDB.rawQuery("SELECT i.Id, Id_Insulin, i.DateTime, Target_BG, i.Value, g.value, i.Id_Tag, g.Id FROM Reg_Insulin as i LEFT JOIN Reg_BloodGlucose as g ON Id_BloodGlucose=g.Id WHERE i.Id='" + id + "';", null);
		Cursor cursor = myDB.rawQuery("SELECT Id, Id_User, Id_BloodGlucose, Id_Insulin, DateTime, Target_BG, Value, Id_Tag, Id_Note FROM Reg_Insulin WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		InsulinRec insulin = new InsulinRec();
		insulin.setId(cursor.getInt(0));
		insulin.setIdUser(cursor.getInt(1));
		insulin.setIdBloodGlucose((!cursor.isNull(2)) ? cursor.getInt(2) : -1);
		insulin.setIdInsulin(cursor.getInt(3));
		String t = cursor.getString(4);
		insulin.setDateTime(t);
		insulin.setTargetGlycemia(cursor.getInt(5));
		insulin.setInsulinUnits(cursor.getFloat(6));
		insulin.setIdTag(cursor.getInt(7));
		insulin.setIdNote((!cursor.isNull(8)) ? cursor.getInt(8) : -1);
		cursor.close();
		return insulin;

	}


	//----------------- EXERCISES --------------
	public boolean Exercise_ExistName(String name) {
		Cursor cursor = myDB.rawQuery("SELECT Name FROM Exercise where Name='" + name + "'", null);
		return cursor.getCount() > 0;
	}

	public int Exercise_GetIdByName(String name) {
		int n;
		Cursor cursor = myDB.rawQuery("SELECT Id FROM Exercise where Name='" + name + "'", null);
		cursor.moveToFirst();
		n = cursor.getInt(0);
		cursor.close();
		return n;
	}

	public String Exercise_GetNameById(int id) {
		String n;
		Cursor cursor = myDB.rawQuery("SELECT Name FROM Exercise where Id='" + id + "'", null);
		cursor.moveToFirst();
		n = cursor.getString(0);
		cursor.close();
		return n;
	}

	@Nullable
	public ExerciseRec getLastExercice() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Exercise;", null);
		cursor.moveToLast();
		if (cursor.getCount()==0) {
			return null;
		}
		ExerciseRec tmp = new ExerciseRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setExercise(cursor.getString(2));
		tmp.setDuration(cursor.getInt(3));
		tmp.setEffort(cursor.getString(4));
		tmp.setDateTime(cursor.getString(5));
		tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);

		cursor.close();
		return tmp;

	}


	public HashMap<Integer, String> Exercise_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Exercise", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		HashMap<Integer, String> exercises = new HashMap<Integer, String>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				exercises.put(cursor.getInt(0), cursor.getString(1));
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return exercises;
		} else {
			cursor.close();
			return null;
		}
	}

	public ArrayList<ExerciseRec> ExerciseReg_GetByDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Exercise WHERE StartDateTime > '" + from + " 00:00:00' AND StartDateTime < '" + to + " 23:59:59' ORDER BY StartDateTime DESC;", null);
		ArrayList<ExerciseRec> exs = new ArrayList<ExerciseRec>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			ExerciseRec tmp;
			do {

				tmp = new ExerciseRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setExercise(cursor.getString(2));
				tmp.setDuration(cursor.getInt(3));
				tmp.setEffort(cursor.getString(4));
				tmp.setDateTime(cursor.getString(5));
				tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
				exs.add(tmp);

				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return exs;
		} else {
			cursor.close();
			return exs;
		}
	}

	@Nullable
	public ExerciseRec ExerciseReg_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT Id, Id_User, Exercise, Duration, Effort, StartDateTime, Id_Note FROM Reg_Exercise WHERE Id='" + id + "';", null);
		ExerciseRec ex = new ExerciseRec();
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		ex.setId(cursor.getInt(0));
		ex.setIdUser(cursor.getInt(1));
		ex.setExercise(cursor.getString(2));
		ex.setDuration(cursor.getInt(3));
		ex.setEffort(cursor.getString(4));
		String t = cursor.getString(5);
		ex.setDateTime(t);
		ex.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);

		cursor.close();
		return ex;

	}


	//------------------- MEDICINE ------------------------------
	public HashMap<Integer, String[]> Medicine_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Medicine", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		HashMap<Integer, String[]> medicines = new HashMap<Integer, String[]>();
		String[] row;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				row = new String[2];
				row[0] = cursor.getString(1); //Name
				row[1] = cursor.getString(2); //Units
				medicines.put(cursor.getInt(0), row);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return medicines;
		} else {
			cursor.close();
			return null;
		}
	}


	//-------------- CARBS ------------------
	public ArrayList<CarbsRec> CarboHydrate_GetBtDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_CarboHydrate WHERE  DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<CarbsRec> allreads = new ArrayList<CarbsRec>();
		CarbsRec tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				tmp = new CarbsRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setCarbsValue(cursor.getInt(2));
				tmp.setPhotoPath(cursor.getString(3));
				tmp.setDateTime(cursor.getString(4));
				tmp.setIdTag(cursor.getInt(5));

				allreads.add(tmp);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return allreads;
		} else {
			cursor.close();
			return allreads;
		}
	}

	@Nullable
	public CarbsRec CarboHydrate_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_CarboHydrate WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		CarbsRec tmp = new CarbsRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setCarbsValue(cursor.getInt(2));
		tmp.setPhotoPath(cursor.getString(3));
		tmp.setDateTime(cursor.getString(4));
		tmp.setIdTag(cursor.getInt(5));
		tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
		cursor.close();
		return tmp;

	}

	public CarbsRec getCarbsAtThisTime(int userId, String time) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_CarboHydrate WHERE Id_User=" + String.valueOf(userId) + " AND DateTime='" + time + "';", null);
		CarbsRec tmp = null;

		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			tmp = new CarbsRec();
			tmp.setId(cursor.getInt(0));
			tmp.setIdUser(cursor.getInt(1));
			tmp.setCarbsValue(cursor.getInt(2));
			tmp.setPhotoPath(cursor.getString(3));
			tmp.setDateTime(cursor.getString(4));
			tmp.setIdTag(cursor.getInt(5));
			tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
			cursor.close();

		}
		return tmp;
	}

	//---------------------- BLOODPRESSURE ---------------
	public ArrayList<BloodPressureRec> BloodPressure_GetBtDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodPressure WHERE  DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<BloodPressureRec> allreads = new ArrayList<BloodPressureRec>();
		BloodPressureRec tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				tmp = new BloodPressureRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setSystolic(cursor.getInt(2));
				tmp.setDiastolic(cursor.getInt(3));
				tmp.setDateTime(cursor.getString(4));
				tmp.setIdTag(cursor.getInt(5));
				tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return allreads;
		} else {
			cursor.close();
			return allreads;
		}
	}

	@Nullable
	public BloodPressureRec BloodPressure_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodPressure WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		BloodPressureRec tmp = new BloodPressureRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setSystolic(cursor.getInt(2));
		tmp.setDiastolic(cursor.getInt(3));
		tmp.setDateTime(cursor.getString(4));
		tmp.setIdTag(cursor.getInt(5));
		tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);

		cursor.close();
		return tmp;

	}

	@Nullable
	public BloodPressureRec getLastBloodPressure() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodPressure;", null);
		cursor.moveToLast();
		if (cursor.getCount()==0) {
			return null;
		}

		BloodPressureRec tmp = new BloodPressureRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setSystolic(cursor.getInt(2));
		tmp.setDiastolic(cursor.getInt(3));
		tmp.setDateTime(cursor.getString(4));
		tmp.setIdTag(cursor.getInt(5));
		tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);

		cursor.close();
		return tmp;

	}




	//----------- CHOLESTEROL ---------------
	public ArrayList<CholesterolRec> Cholesterol_GetBtDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Cholesterol WHERE  DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<CholesterolRec> allreads = new ArrayList<CholesterolRec>();
		CholesterolRec tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				tmp = new CholesterolRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return allreads;
		} else {
			cursor.close();
			return allreads;
		}
	}

	@Nullable
	public CholesterolRec Cholesterol_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Cholesterol WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		CholesterolRec tmp = new CholesterolRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setValue(cursor.getDouble(2));
		tmp.setDateTime(cursor.getString(3));
		tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);

		cursor.close();
		return tmp;

	}

	@Nullable
	public CholesterolRec getLastCholesterol() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Cholesterol;", null);
		cursor.moveToLast();
		if (cursor.getCount()==0) {
			return null;
		}

		CholesterolRec tmp = new CholesterolRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setValue(cursor.getDouble(2));
		tmp.setDateTime(cursor.getString(3));
		tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);

		cursor.close();
		return tmp;

	}

	//--------------- WEIGHT -----------------
	public ArrayList<WeightRec> Weight_GetBtDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Weight WHERE  DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<WeightRec> allreads = new ArrayList<WeightRec>();
		WeightRec tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				tmp = new WeightRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return allreads;
		} else {
			cursor.close();
			return allreads;
		}
	}

	@Nullable
	public WeightRec Weight_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Weight WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		if (cursor.getCount()==0) {
			return null;
		}

		WeightRec tmp = new WeightRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setValue(cursor.getDouble(2));
		tmp.setDateTime(cursor.getString(3));
		tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);

		cursor.close();
		return tmp;

	}

	@Nullable
	public WeightRec getLastWeight() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Weight", null);
		cursor.moveToLast();
		if (cursor.getCount()==0) {
			return null;
		}

		WeightRec tmp = new WeightRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setValue(cursor.getDouble(2));
		tmp.setDateTime(cursor.getString(3));
		tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);

		cursor.close();
		return tmp;

	}


	//------------------- HbA1c
	public ArrayList<HbA1cRec> HbA1c_GetBtDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_A1c WHERE  DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<HbA1cRec> allreads = new ArrayList<HbA1cRec>();
		HbA1cRec tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				tmp = new HbA1cRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return allreads;
		} else {
			cursor.close();
			return allreads;
		}
	}

	@Nullable
	public HbA1cRec HbA1c_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_A1c WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		HbA1cRec tmp = new HbA1cRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setValue(cursor.getDouble(2));
		tmp.setDateTime(cursor.getString(3));
		tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);

		cursor.close();
		return tmp;

	}

	@Nullable
	public HbA1cRec getLastHbA1c() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_A1c", null);
		cursor.moveToLast();
		if (cursor.getCount()==0) {
			return null;
		}

		HbA1cRec tmp = new HbA1cRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setValue(cursor.getDouble(2));
		tmp.setDateTime(cursor.getString(3));
		tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);

		cursor.close();
		return tmp;

	}


	//----------------------- DISEASE REG
	public ArrayList<DiseaseRec> DiseaseReg_GetByDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Disease WHERE  StartDate > '" + from + " 00:00:00' AND StartDate < '" + to + " 23:59:59' ORDER BY StartDate DESC;", null);
		ArrayList<DiseaseRec> allreads = new ArrayList<DiseaseRec>();
		DiseaseRec tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				tmp = new DiseaseRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setDisease(cursor.getString(2));
				tmp.setStartDate(cursor.getString(3));
				tmp.setEndDate((!cursor.isNull(4)) ? cursor.getString(4) : null);
				tmp.setIdNote((!cursor.isNull(5)) ? cursor.getInt(5) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return allreads;
		} else {
			cursor.close();
			return allreads;
		}
	}

	@Nullable
	public DiseaseRec DiseaseReg_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Disease WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}

		DiseaseRec tmp = new DiseaseRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setDisease(cursor.getString(2));
		tmp.setStartDate(cursor.getString(3));
		tmp.setEndDate((!cursor.isNull(4)) ? cursor.getString(4) : null);
		tmp.setIdNote((!cursor.isNull(5)) ? cursor.getInt(5) : -1);

		cursor.close();
		return tmp;

	}

	@Nullable
	public DiseaseRec getLastDisease() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Disease;", null);
		cursor.moveToLast();
		if (cursor.getCount()==0) {
			return null;
		}
		DiseaseRec tmp = new DiseaseRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setDisease(cursor.getString(2));
		tmp.setStartDate(cursor.getString(3));
		tmp.setEndDate(cursor.getString(4));
		tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);

		cursor.close();
		return tmp;

	}


	//-------------- TARGETS GLYCEMIA -----------------
	public boolean Target_HasTargets() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM BG_Target", null);
		return cursor.getCount() > 0;

	}


	public double Target_GetTargetByTime(String time) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM BG_Target WHERE  " + "(TimeStart < TimeEnd AND '" + time + "' >= TimeStart AND '" + time + "' <= TimeEnd)" +
				"OR " + "(TimeStart > TimeEnd AND('" + time + "' >= TimeStart OR '" + time + "' <= TimeEnd ))" + ";", null);
		double d = 0;
		Log.d("Cursor targets", String.valueOf(cursor.getCount()));
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			d = cursor.getDouble(4);
		}
		cursor.close();

		return d;
	}

	public ArrayList<InsulinTarget> Target_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM BG_Target", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<InsulinTarget> targets = new ArrayList<InsulinTarget>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			InsulinTarget t;
			do {
				t = new InsulinTarget();
				t.setId(cursor.getInt(0));
				t.setName(cursor.getString(1));
				t.setStart(cursor.getString(2));
				t.setEnd(cursor.getString(3));
				t.setTarget(cursor.getDouble(4));
				targets.add(t);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return targets;
		} else {
			cursor.close();
			return targets;
		}
	}

	@Nullable
	public InsulinTarget Target_GetById(int id) {
		InsulinTarget target = new InsulinTarget();
		Cursor cursor = myDB.rawQuery("SELECT * FROM BG_Target where Id='" + id + "'", null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}
		target.setId(cursor.getInt(0));
		target.setName(cursor.getString(1));
		target.setStart(cursor.getString(2));
		target.setEnd(cursor.getString(3));
		target.setTarget(cursor.getDouble(4));
		cursor.close();
		return target;
	}


	//--------------- NOTES -------------------
	@Nullable
	public Note Note_GetById(int id) {
		Note n = new Note();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Note WHERE Id='" + id + "';", null);
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			return null;
		}
		n.setId(cursor.getInt(0));
		n.setNote(cursor.getString(1));
		cursor.close();
		return n;
	}




	/*
	 * LOGBOOK by zeornelas
	 */

	public ArrayList<LogbookData> getLogbook(String from, String to) {
		ArrayList<LogbookData> lb = new ArrayList<LogbookData>();
		LogbookData row;
		CarbsRec ch;
		InsulinRec ins;
		GlycemiaRec bg;
		
		/*Cursor cursor = myDB.rawQuery("SELECT ins.datetime, ins.Id_user, ins.Id_Tag, ins.Id_Note, ch.Id, ch.value, ch.PhotoPath, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose,ins.Target_BG,ins.Value, bg.Value" +
			" from Reg_CarboHydrate as ch, Reg_Insulin as ins, Reg_BloodGlucose as bg"+ 
			" where ch.datetime >'"+ from + " 00:00:00' AND ch.datetime < '"+ to + " 23:59:59'" +
			" AND ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" + 
			" AND ch.datetime = ins.datetime AND ch.Id_User=ins.Id_User AND ins.Id_BloodGlucose is not NULL AND bg.Id = ins.Id_BloodGlucose"+
			" UNION" +
			" SELECT ins.DateTime, ins.Id_User, ins.Id_Tag, ins.Id_Note, null, null, null, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose, ins.Target_BG, ins.value, bg.value"+
			" FROM Reg_Insulin as ins, Reg_BloodGlucose as bg"+
			" WHERE ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" +
			" AND bg.datetime >'"+ from + " 00:00:00' AND bg.datetime < '"+ to + " 23:59:59'" + 
			" AND bg.Id=ins.Id_BloodGlucose AND ins.Id not in"+
			" (SELECT ins.Id"+
			" from Reg_CarboHydrate as ch, Reg_Insulin as ins" +
			" where ch.datetime >'"+ from + " 00:00:00' AND ch.datetime < '"+ to + " 23:59:59'" +
			" AND ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" + 
			" AND ch.datetime = ins.datetime AND ch.Id_User=ins.Id_User AND ins.Id_BloodGlucose is not NULL" +
			" Union" +
			" SELECT ins.Id" +
			" from Reg_Insulin as ins" +
			" where ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" +
			" AND ins.Id_BloodGlucose is NULL)" +
			" UNION" +
			" SELECT bg.DateTime, bg.Id_User, bg.Id_Tag, bg.Id_Note, null, null, null, null, null, bg.Id, null, null, bg.value" +
			" FROM Reg_BloodGlucose as bg" +
			" WHERE bg.datetime >'"+ from + " 00:00:00' AND bg.datetime < '"+ to + " 23:59:59'" +
			" AND id not in" +
			" (SELECT ins.Id_BloodGlucose" +
			" from Reg_CarboHydrate as ch, Reg_Insulin as ins" +
			" where ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" +
			" AND ch.datetime >'"+ from + " 00:00:00' AND ch.datetime < '"+ to + " 23:59:59'" + 
			" AND ch.datetime = ins.datetime AND ch.Id_User=ins.Id_User AND ins.Id_BloodGlucose is not NULL" +
			" Union" +
			" SELECT ins.Id_BloodGlucose" +
			" from Reg_Insulin as ins" +
			" where ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" +
			" AND ins.Id_BloodGlucose is not NULL)"+
			" UNION" +
			" SELECT ch.DateTime, ch.Id_User, ch.Id_Tag, ch.Id_Note, ch.Id, ch.Value, ch.PhotoPath, null, null, null, null, null, null" +
			" FROM Reg_CarboHydrate as ch" +
			" WHERE ch.datetime >'"+ from + " 00:00:00' AND ch.datetime < '"+ to + " 23:59:59'" + 
			" AND id not in" +
			" (SELECT ch.Id" +
			" from Reg_CarboHydrate as ch, Reg_Insulin as ins" +
			" where ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" +
			" AND ch.datetime >'"+ from + " 00:00:00' AND ch.datetime < '"+ to + " 23:59:59'" + 
			" AND ch.datetime = ins.datetime AND ch.Id_User=ins.Id_User AND ins.Id_BloodGlucose is not NULL)" +
			" UNION" +
			" SELECT ins.DateTime, ins.DateTime, ins.Id_Tag, ins.Id_Note, null, null, null, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose, ins.Target_BG, ins.Value, null" +
			" FROM Reg_Insulin as ins" +
			" WHERE ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" +
			" AND id not in" + 
			" (SELECT ins.Id" +
			" from Reg_CarboHydrate as ch, Reg_Insulin as ins" +
			" where ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" +
			" AND ch.datetime >'"+ from + " 00:00:00' AND ch.datetime < '"+ to + " 23:59:59'" +
			" AND ch.datetime = ins.datetime AND ch.Id_User=ins.Id_User AND ins.Id_BloodGlucose is not NULL" +
			" Union" +
			" SELECT ins.Id" +
			" from Reg_Insulin as ins" +
			" where ins.datetime >'"+ from + " 00:00:00' AND ins.datetime < '"+ to + " 23:59:59'" +
			" AND ins.Id_BloodGlucose is not NULL)" +
			" ORDER BY datetime DESC",null);
		*/


		Cursor cursor = myDB.rawQuery("SELECT ins.datetime, ins.Id_user, ins.Id_Tag, ins.Id_Note, ch.Id, ch.value, ch.PhotoPath, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose,ins.Target_BG,ins.Value, bg.Value" +
				" from Reg_CarboHydrate as ch, Reg_Insulin as ins, Reg_BloodGlucose as bg" +
				" where ch.datetime >'" + from + " 00:00:00' AND ch.datetime < '" + to + " 23:59:59'" +
				" AND ins.datetime >'" + from + " 00:00:00' AND ins.datetime < '" + to + " 23:59:59'" +
				" AND ch.datetime = ins.datetime AND ch.Id_User=ins.Id_User AND ins.Id_BloodGlucose is not NULL AND bg.Id = ins.Id_BloodGlucose" +
				" UNION" +
				" SELECT ins.DateTime, ins.Id_User, ins.Id_Tag, ins.Id_Note, null, null, null, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose, ins.Target_BG, ins.value, bg.value" +
				" FROM Reg_Insulin as ins, Reg_BloodGlucose as bg" +
				" WHERE ins.datetime >'" + from + " 00:00:00' AND ins.datetime < '" + to + " 23:59:59'" +
				" AND bg.datetime >'" + from + " 00:00:00' AND bg.datetime < '" + to + " 23:59:59'" +
				" AND bg.Id=ins.Id_BloodGlucose AND ins.DateTime not in" +
				" (SELECT ch.DateTime From Reg_CarboHydrate as ch)" +
				" UNION" +
				" SELECT bg.DateTime, bg.Id_User, bg.Id_Tag, bg.Id_Note, null, null, null, null, null, bg.Id, null, null, bg.value" +
				" FROM Reg_BloodGlucose as bg" +
				" WHERE bg.datetime >'" + from + " 00:00:00' AND bg.datetime < '" + to + " 23:59:59'" +
				" AND bg.datetime not in" +
				" (SELECT datetime from Reg_Insulin " +
				" union" +
				" SELECT datetime from Reg_CarboHydrate)" +
				" UNION" +
				" SELECT ch.DateTime, ch.Id_User, ch.Id_Tag, ch.Id_Note, ch.Id, ch.Value, ch.PhotoPath, null, null, null, null, null, null" +
				" FROM Reg_CarboHydrate as ch" +
				" WHERE ch.datetime >'" + from + " 00:00:00' AND ch.datetime < '" + to + " 23:59:59'" +
				" AND ch.datetime not in" +
				" (SELECT datetime from Reg_Insulin " +
				"union" +
				" SELECT datetime from Reg_BloodGlucose)" +
				" UNION" +
				" SELECT ins.DateTime, ins.DateTime, ins.Id_Tag, ins.Id_Note, null, null, null, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose, ins.Target_BG, ins.Value, null" +
				" FROM Reg_Insulin as ins" +
				" WHERE ins.datetime >'" + from + " 00:00:00' AND ins.datetime < '" + to + " 23:59:59'" +
				" AND datetime not in" +
				" (SELECT datetime from Reg_BloodGlucose" +
				" union" +
				" SELECT ch.DateTime From Reg_CarboHydrate as ch)" +
				" UNION" +
				" SELECT ins.datetime, ins.Id_user, ins.Id_Tag, ins.Id_Note, ch.Id, ch.value, ch.PhotoPath, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose,ins.Target_BG,ins.Value, null" +
				" from Reg_CarboHydrate as ch, Reg_Insulin as ins" +
				" where ch.datetime >'" + from + " 00:00:00' AND ch.datetime < '" + to + " 23:59:59'" +
				" AND ins.datetime >'" + from + " 00:00:00' AND ins.datetime < '" + to + " 23:59:59'" +
				" AND ch.datetime = ins.datetime AND ins.datetime not in " +
				" (SELECT datetime from Reg_BloodGlucose)" +
				" UNION" +
				" SELECT ch.datetime, ch.Id_user, ch.Id_Tag, ch.Id_Note, ch.Id, ch.value, ch.PhotoPath, null, null, bg.Id, null, null, bg.value" +
				" from Reg_CarboHydrate as ch, Reg_BloodGlucose as bg" +
				" where ch.datetime >'" + from + " 00:00:00' AND ch.datetime < '" + to + " 23:59:59'" +
				" AND bg.datetime >'" + from + " 00:00:00' AND bg.datetime < '" + to + " 23:59:59'" +
				" AND ch.datetime = bg.datetime AND ch.datetime not in " +
				" (SELECT datetime from Reg_Insulin)" +
				" ORDER BY datetime DESC", null);


		//Log.d("LOGBOOK", String.valueOf(cursor.getCount()));
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				row = new LogbookData();
				ch = new CarbsRec();
				ins = new InsulinRec();
				bg = new GlycemiaRec();

				String t = cursor.getString(0);

				if (!cursor.isNull(4) && !cursor.isNull(7) && !cursor.isNull(9)) { //refeicao completa
					ch.setDateTime(t);
					ins.setDateTime(t);
					bg.setDateTime(t);

					ch.setIdUser(cursor.getInt(1));
					ins.setIdUser(cursor.getInt(1));
					bg.setIdUser(cursor.getInt(1));

					ch.setIdTag(cursor.getInt(2));
					ins.setIdTag(cursor.getInt(2));
					bg.setIdTag(cursor.getInt(2));

					ch.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					ins.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					bg.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);

					ch.setId(cursor.getInt(4));
					ch.setCarbsValue(cursor.getInt(5));
					ch.setPhotoPath(cursor.getString(6));

					ins.setId(cursor.getInt(7));
					ins.setIdInsulin(cursor.getInt(8));
					ins.setIdBloodGlucose(cursor.getInt(9));
					ins.setTargetGlycemia(cursor.getInt(10));
					ins.setInsulinUnits(cursor.getFloat(11));

					bg.setId(cursor.getInt(9));
					bg.setValue(cursor.getInt(12));

					row.setGlycemiaReg(bg);
					row.setCarbsReg(ch);
					row.setInsulinReg(ins);
				} else if (!cursor.isNull(4) && cursor.isNull(7) && cursor.isNull(9)) { //so hidratos carbono
					ch.setDateTime(t);
					ch.setIdUser(cursor.getInt(1));
					ch.setIdTag(cursor.getInt(2));
					ch.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					ch.setId(cursor.getInt(4));
					ch.setCarbsValue(cursor.getInt(5));
					ch.setPhotoPath(cursor.getString(6));

					row.setGlycemiaReg(null);
					row.setCarbsReg(ch);
					row.setInsulinReg(null);
				} else if (cursor.isNull(4) && !cursor.isNull(7) && !cursor.isNull(9)) { //insulina com parametro da glicemia
					ins.setDateTime(t);
					bg.setDateTime(t);

					ins.setIdUser(cursor.getInt(1));
					bg.setIdUser(cursor.getInt(1));

					ins.setIdTag(cursor.getInt(2));
					bg.setIdTag(cursor.getInt(2));

					ins.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					bg.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);

					ins.setId(cursor.getInt(7));
					ins.setIdInsulin(cursor.getInt(8));
					ins.setIdBloodGlucose(cursor.getInt(9));
					ins.setTargetGlycemia(cursor.getInt(10));
					ins.setInsulinUnits(cursor.getFloat(11));

					bg.setId(cursor.getInt(9));
					bg.setValue(cursor.getInt(12));

					row.setGlycemiaReg(bg);
					row.setCarbsReg(null);
					row.setInsulinReg(ins);
				} else if (cursor.isNull(4) && cursor.isNull(7) && !cursor.isNull(9)) { //so glicemia
					bg.setDateTime(t);
					bg.setIdUser(cursor.getInt(1));
					bg.setIdTag(cursor.getInt(2));
					bg.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					bg.setId(cursor.getInt(9));
					bg.setValue(cursor.getInt(12));

					row.setGlycemiaReg(bg);
					row.setCarbsReg(null);
					row.setInsulinReg(null);
				} else if (cursor.isNull(4) && !cursor.isNull(7) && cursor.isNull(9)) { //so insulina
					ins.setDateTime(t);
					ins.setIdUser(cursor.getInt(1));
					ins.setIdTag(cursor.getInt(2));
					ins.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					ins.setId(cursor.getInt(7));
					ins.setIdInsulin(cursor.getInt(8));
					ins.setIdBloodGlucose(cursor.getInt(9));
					ins.setTargetGlycemia(cursor.getInt(10));
					ins.setInsulinUnits(cursor.getFloat(11));

					row.setGlycemiaReg(null);
					row.setCarbsReg(null);
					row.setInsulinReg(ins);
				} else if (!cursor.isNull(4) && !cursor.isNull(7) && cursor.isNull(9)) { //so hidratos e insulina
					ins.setDateTime(t);
					ins.setIdUser(cursor.getInt(1));
					ins.setIdTag(cursor.getInt(2));
					ins.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					ins.setId(cursor.getInt(7));
					ins.setIdInsulin(cursor.getInt(8));
					ins.setIdBloodGlucose(cursor.getInt(9));
					ins.setTargetGlycemia(cursor.getInt(10));
					ins.setInsulinUnits(cursor.getFloat(11));

					ch.setDateTime(t);
					ch.setIdUser(cursor.getInt(1));
					ch.setIdTag(cursor.getInt(2));
					ch.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					ch.setId(cursor.getInt(4));
					ch.setCarbsValue(cursor.getInt(5));
					ch.setPhotoPath(cursor.getString(6));

					row.setGlycemiaReg(null);
					row.setCarbsReg(ch);
					row.setInsulinReg(ins);
				} else if (!cursor.isNull(4) && cursor.isNull(7) && !cursor.isNull(9)) { //so hidratos e glicemia
					ch.setDateTime(t);
					ch.setIdUser(cursor.getInt(1));
					ch.setIdTag(cursor.getInt(2));
					ch.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					ch.setId(cursor.getInt(4));
					ch.setCarbsValue(cursor.getInt(5));
					ch.setPhotoPath(cursor.getString(6));

					bg.setDateTime(t);
					bg.setIdUser(cursor.getInt(1));
					bg.setIdTag(cursor.getInt(2));
					bg.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					bg.setId(cursor.getInt(9));
					bg.setValue(cursor.getInt(12));

					row.setGlycemiaReg(bg);
					row.setCarbsReg(ch);
					row.setInsulinReg(null);
				}


				lb.add(row);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			//Log.d("LOGBOOK", String.valueOf(lb.size()));
			return lb;
		} else {
			cursor.close();
			//Log.d("LOGBOOK", String.valueOf(lb.size()));
			return lb;
		}

	}
}

