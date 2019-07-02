package pt.it.porto.mydiabetes.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.CarbsRatioData;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.DateTime;
import pt.it.porto.mydiabetes.data.Disease;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.HbA1cRec;
import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.InsulinTarget;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.data.PointsRec;
import pt.it.porto.mydiabetes.data.Sensitivity;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.data.TargetBGRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.HomeElement;
import pt.it.porto.mydiabetes.utils.RawRecord;

@SuppressLint("UseSparseArrays")
public class DB_Read {

	private String TAG = "DB_READ_PRINT:";

	final Context myContext;
	final SQLiteDatabase myDB;

	public DB_Read(Context context) {
		super();
		DB_Handler db = new DB_Handler(context);
		this.myContext = context;
		SQLiteDatabase myDB1;
		myDB1 = db.getReadableDatabase();
		this.myDB = myDB1;
	}
	public DB_Read(SQLiteDatabase myDB) {
		super();
		myContext = null;
		this.myDB = myDB;
	}
	public void close() {
		myDB.close();
		Log.d("Close", "DB_Read");
	}
	public boolean isEmpty(){
		Cursor cursor = myDB.rawQuery("SELECT Count(*) FROM sqlite_master", null);
		cursor.moveToFirst();
		boolean result = cursor.getInt(0) > 1;
		//Log.i("cenas", "isEmpty: "+cursor.getInt(0));
		//Log.i("cenas", "isEmpty: "+result);
		cursor.close();
		return !result;
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

	public int getUserId() {
		Cursor cursor = myDB.rawQuery("SELECT " + MyDiabetesContract.UserInfo.COLUMN_NAME_ID + " FROM UserInfo", null);
		int val = -1;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			val = Integer.parseInt(cursor.getString(0));
		}
		cursor.close();
		return val;
	}

	public int getCarbsRatio() {
		Cursor cursor = myDB.rawQuery("SELECT CarbsRatio FROM UserInfo", null);
		int val = -1;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			val = Integer.parseInt(cursor.getString(0));
		}
		cursor.close();
		return val;
	}

	public int getInsulinRatio() {
		Cursor cursor = myDB.rawQuery("SELECT InsulinRatio FROM UserInfo", null);
		int val = -1;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			val = Integer.parseInt(cursor.getString(0));
		}
		cursor.close();
		return val;
	}

	public Cursor getGlicObj() {
		Cursor cursor = myDB.rawQuery("SELECT " + MyDiabetesContract.BG_Target.COLUMN_NAME_ID+","+ MyDiabetesContract.BG_Target.COLUMN_NAME_TIME_START+","+MyDiabetesContract.BG_Target.COLUMN_NAME_TIME_END + " FROM "+MyDiabetesContract.BG_Target.TABLE_NAME, null);
		return cursor;
	}

	public boolean targetBGTimeExists(String start, String id) {
		Cursor cursor = myDB.rawQuery("SELECT Id "+
				"FROM BG_Target "+
				"WHERE TimeStart == '" + start +"' and Id != '"+id+ "' "+
				"ORDER BY TimeStart", null);
		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}
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

//	public String Tag_GetNameById(int id) {
//		Cursor cursor = myDB.rawQuery("SELECT * FROM Tag where Id='" + id + "'", null);
//		cursor.moveToFirst();
//		String name = cursor.getString(1);
//		cursor.close();
//		return name;
//	}

	/**
	 * Not ID but rather the index on a list ordered by timestart
	 * @param id
	 * @return
	 */
		public String Tag_GetNameById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Tag ORDER BY Id",null);
		cursor.moveToFirst();
		Log.i(TAG, "Tag_GetNameById: "+cursor.getCount());
		cursor.move(id-1);
		String name = cursor.getString(1);
		cursor.close();
		return name;
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
		Cursor cursor = myDB.rawQuery("SELECT * FROM Tag ORDER BY Id", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<Tag> tags = new ArrayList<>();
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
		ArrayList<Disease> AllReads = new ArrayList<>();
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
		HashMap<Integer, String[]> glycemias = new HashMap<>();
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

	public ArrayList<GlycemiaRec> GlycemiaRec_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		String[] row;
		ArrayList<GlycemiaRec> glycemiaRecs = new ArrayList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				GlycemiaRec newRec = new GlycemiaRec();
				newRec.setIdUser(cursor.getInt(1));
				newRec.setValue(cursor.getInt(2)); //Value
				newRec.setDateTime(cursor.getString(3)); //DateTime
				newRec.setIdTag(cursor.getInt(4)); //Id_Tag
				//row[3] = cursor.getString(5); //Id_Note
				glycemiaRecs.add(newRec);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return glycemiaRecs;
		} else {
			cursor.close();
			return null;
		}
	}

	public ArrayList<GlycemiaRec> Glycemia_GetByDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose WHERE DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<GlycemiaRec> allreads = new ArrayList<>();
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
		g.setObjective((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
		cursor.close();

		return g;
	}


	//---------------------- INSULIN ------------------------------


	public ArrayList<InsulinRec> InsulinRec_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Insulin", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<InsulinRec> insulinRecs = new ArrayList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				InsulinRec oldRec = new InsulinRec();
				oldRec.setIdUser(cursor.getInt(1));
				oldRec.setIdInsulin(cursor.getInt(2));
				oldRec.setDateTime(cursor.getString(4));
				oldRec.setInsulinUnits(cursor.getInt(6));
				oldRec.setIdTag(cursor.getInt(7));
				oldRec.setIdNote(cursor.getInt(8));
				insulinRecs.add(oldRec);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return insulinRecs;
		} else {
			cursor.close();
			return null;
		}
	}

	public ArrayList<Insulin> Insulins_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Insulin", null);
//		Log.i("cenas", "Insulins_GetAll-> ->: "+DatabaseUtils.dumpCursorToString(cursor));

		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<Insulin> insulinRecs = new ArrayList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				Insulin oldRec = new Insulin();
				oldRec.setName(cursor.getString(1));
				oldRec.setType(cursor.getString(2));
				oldRec.setAction(cursor.getString(3));
				//oldRec.setDuration(cursor.getDouble(4));

				Log.i("cenas", "Insulins_GetAll: Name: "+oldRec.getName()+" Type: "+oldRec.getType()+" ACTION: "+oldRec.getAction()+" DURATION: "+oldRec.getDuration());
				insulinRecs.add(oldRec);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return insulinRecs;
		} else {
			cursor.close();
			return null;
		}
	}

//	public ArrayList<Insulin> Insulins_GetAll() {
//		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Insulin", null);
//		Log.i("cenas", "Insulins_GetAll-> ->: "+DatabaseUtils.dumpCursorToString(cursor));
//
//		Log.d("Cursor", String.valueOf(cursor.getCount()));
//		ArrayList<Insulin> insulinRecs = new ArrayList<>();
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			do {
//				Insulin oldRec = new Insulin();
//				oldRec.setName(cursor.getString(1));
//				oldRec.setType(cursor.getString(2));
//				oldRec.setAction(cursor.getString(3));
//				oldRec.setDuration(cursor.getDouble(4));
//
//				Log.i("cenas", "Insulins_GetAll: Name: "+oldRec.getName()+" Type: "+oldRec.getType()+" ACTION: "+oldRec.getAction()+" DURATION: "+oldRec.getDuration());
//				insulinRecs.add(oldRec);
//				cursor.moveToNext();
//			} while (!cursor.isAfterLast());
//			cursor.close();
//			return insulinRecs;
//		} else {
//			cursor.close();
//			return null;
//		}
//	}

	public HashMap<Integer, String[]> Insulin_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Insulin", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		HashMap<Integer, String[]> insulins = new HashMap<>();
		String[] row;
		Log.i("cenas", "Insulin_GetAll: -------------------------------------------");
		Log.i("cenas", "Insulin_GetAll: "+DatabaseUtils.dumpCursorToString(cursor));
		Log.i("cenas", "Insulin_GetAll: -------------------------------------------");

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
		HashMap<Integer, String> insulins = new HashMap<>();
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
		ArrayList<InsulinRec> allreads = new ArrayList<>();
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
		if (cursor.getCount() == 0) {
			return null;
		}
		ExerciseRec tmp = new ExerciseRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setExercise(cursor.getString(2));
		tmp.setDuration(cursor.getInt(3));
		tmp.setEffort(cursor.getInt(4));
		tmp.setDateTime(cursor.getString(5));
		tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);

		cursor.close();
		return tmp;

	}

	public ArrayList<ExerciseRec> ExerciseReg_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Exercise", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<ExerciseRec> exRecList = new ArrayList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				ExerciseRec rec = new ExerciseRec();
				rec.setIdUser(cursor.getInt(1));
				rec.setExercise(cursor.getString(2));
				rec.setDuration(cursor.getInt(3));
				rec.setEffort(cursor.getInt(4));
				rec.setDateTime(cursor.getString(5));
				rec.setIdNote(cursor.getInt(6));
				exRecList.add(rec);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return exRecList;
		} else {
			cursor.close();
			return null;
		}
	}



	public HashMap<Integer, String> Exercise_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Exercise", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		HashMap<Integer, String> exercises = new HashMap<>();
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
		ex.setEffort(cursor.getInt(4));
		String t = cursor.getString(5);
		ex.setDateTime(t);
		ex.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);

		cursor.close();
		return ex;

	}

    @Nullable
    public int getNumberExerciceByDate(String day) {
        Cursor cursor = myDB.rawQuery("SELECT COUNT(*) FROM Reg_Exercise WHERE StartDateTime LIKE '%" + day + "%' ORDER BY StartDateTime DESC;", null);
        LinkedList<ExerciseRec> exs = new LinkedList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int number = cursor.getInt(0);
            cursor.close();
            return number;
        } else {
            cursor.close();
            return 0;
        }
    }


	@Nullable
	public boolean hasDailyMedal(String day, String type) {

		Log.i(TAG, "query: "+"SELECT count(*) FROM Record WHERE DateTime LIKE '%" + day + "%';");

		Cursor cursor = myDB.rawQuery("SELECT count(*) FROM Badges WHERE Type = 'daily' AND Medal = '"+type+"' LIKE '%" + day + "%';", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int nRecords = cursor.getInt(0);
			cursor.close();
			return nRecords>0;
		} else {
			cursor.close();
			return false;
		}
	}


    @Nullable
    public int getRecordsByDate(String day) {

        //Log.i(TAG, "query: "+"SELECT count(*) FROM Record WHERE DateTime LIKE '%" + day + "%';");

        Cursor cursor = myDB.rawQuery("SELECT count(*) FROM Record WHERE DateTime LIKE '%" + day + "%';", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int nRecords = cursor.getInt(0);
            cursor.close();
            return nRecords;
        } else {
            cursor.close();
            return 0;
        }
    }


	public LinkedList<GlycemiaRec> getLastXGlycaemias(int userId, int nRec) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose WHERE Id_User = "+ userId +" AND DateTime >= datetime('now', '-24 Hour')  ORDER BY DateTime LIMIT "+nRec+";", null);
		LinkedList<GlycemiaRec> exs = null;

		if (cursor.getCount() > 0) {
			exs = new LinkedList<>();
			cursor.moveToFirst();
			GlycemiaRec tmp;
			do {

				tmp = new GlycemiaRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getInt(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setObjective(cursor.getInt(6)	);
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

    public LinkedList<String> getLastRecord(int userId) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Record WHERE Id_User = "+ userId +" ORDER BY DateTime DESC LIMIT 1;", null);
		LinkedList<String> ll = null;

		if (cursor.getCount() > 0) {
			ll = new LinkedList<>();
			cursor.moveToFirst();
			int carbVal = getCarbsValByID(cursor.getInt(4));
			float insuVal = getInsuValByID(cursor.getInt(5));
			int glycVal = getGlycaemiaValByID(cursor.getInt(6));

			ll.add(cursor.getString(2));
			ll.add(String.valueOf(carbVal));
			ll.add(String.valueOf(insuVal));
			ll.add(String.valueOf(glycVal));

			return ll;
		} else {
			cursor.close();
			return ll;
		}
	}

    @Nullable
    public LinkedList<ExerciseRec> getExerciseFromStartDate(String startDate, int limit) {
        Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Exercise WHERE StartDateTime >='" + startDate + "' ORDER BY StartDateTime DESC LIMIT "+limit+";", null);
        LinkedList<ExerciseRec> exs = new LinkedList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            ExerciseRec tmp;
            do {

                tmp = new ExerciseRec();
                tmp.setId(cursor.getInt(0));
                tmp.setIdUser(cursor.getInt(1));
                tmp.setExercise(cursor.getString(2));
                tmp.setDuration(cursor.getInt(3));
                tmp.setEffort(cursor.getInt(4));
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

	//------------------- MEDICINE ------------------------------
	public HashMap<Integer, String[]> Medicine_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Medicine", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		HashMap<Integer, String[]> medicines = new HashMap<>();
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
		ArrayList<CarbsRec> allreads = new ArrayList<>();
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

	public HashMap<Integer, String[]> Carbs_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_CarboHydrate", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		String[] row;
		HashMap<Integer, String[]> glycemias = new HashMap<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				row = new String[3];
				row[0] = cursor.getString(2); //Value
				row[1] = cursor.getString(5); //DateTime
				row[2] = cursor.getString(6); //Id_Tag
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
		tmp.setMealId(cursor.getInt(7));
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
		ArrayList<BloodPressureRec> allreads = new ArrayList<>();
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

	public ArrayList<BloodPressureRec> BloodPressure_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodPressure;", null);
		ArrayList<BloodPressureRec> allreads = new ArrayList<>();
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
	public int getNumberBloodPressureByDate(String day) {
		Cursor cursor = myDB.rawQuery("SELECT COUNT(*) FROM Reg_BloodPressure WHERE DateTime LIKE '%" + day + "%' ORDER BY DateTime DESC;", null);
		LinkedList<BloodPressureRec> bloodPressureRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int number = cursor.getInt(0);
			cursor.close();
			return number;
		} else {
			cursor.close();
			return 0;
		}
	}

	@Nullable
	public LinkedList<BloodPressureRec> getBpFromStartDate(String startDate, int limit) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodPressure WHERE DateTime >='" + startDate + "' ORDER BY DateTime DESC LIMIT "+limit+";", null);
		LinkedList<BloodPressureRec> bloodPressureRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			BloodPressureRec tmp;
			do {
				tmp = new BloodPressureRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setSystolic(cursor.getInt(2));
				tmp.setDiastolic(cursor.getInt(3));
				tmp.setDateTime(cursor.getString(4));
				tmp.setIdTag(cursor.getInt(5));
				tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
				bloodPressureRecs.add(tmp);

				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return bloodPressureRecs;
		} else {
			cursor.close();
			return bloodPressureRecs;
		}
	}


	//----------- CHOLESTEROL ---------------
	public ArrayList<CholesterolRec> Cholesterol_GetBtDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Cholesterol WHERE  DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<CholesterolRec> allreads = new ArrayList<>();
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

	public ArrayList<CholesterolRec> Cholesterol_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Cholesterol;", null);
		ArrayList<CholesterolRec> allreads = new ArrayList<>();
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
	public int getNumberCholesterolByDate(String day) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Cholesterol WHERE DateTime LIKE '%" + day + "%' ORDER BY DateTime DESC;", null);
		LinkedList<CholesterolRec> cholesterolRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int number = cursor.getInt(0);
			cursor.close();
			return number;
		} else {
			cursor.close();
			return 0;
		}
	}

	@Nullable
	public LinkedList<CholesterolRec> getCholesterolFromStartDate(String startDate, int limit) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Cholesterol WHERE DateTime >='" + startDate + "' ORDER BY DateTime DESC LIMIT "+limit+";", null);
		LinkedList<CholesterolRec> cholesterolRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			CholesterolRec tmp;
			do {
				tmp = new CholesterolRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				cholesterolRecs.add(tmp);

				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return cholesterolRecs;
		} else {
			cursor.close();
			return cholesterolRecs;
		}
	}

	//--------------- WEIGHT -----------------
	public ArrayList<WeightRec> Weight_GetBtDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Weight WHERE  DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<WeightRec> allreads = new ArrayList<>();
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
		if (cursor.getCount() == 0) {
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
		if (cursor.getCount() == 0) {
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

	public ArrayList<WeightRec> Weight_GetAll() {
		ArrayList<WeightRec> AllReads = new ArrayList<>();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Weight", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			WeightRec tmp;
			do {
				tmp = new WeightRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
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

	@Nullable
	public int getNumberWeightByDate(String day) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Weight WHERE DateTime LIKE '%" + day + "%' ORDER BY DateTime DESC;", null);
		LinkedList<WeightRec> weightRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
            int number = cursor.getInt(0);
			cursor.close();
			return number;
		} else {
			cursor.close();
			return 0;
		}
	}

	@Nullable
	public LinkedList<WeightRec> getWeightFromStartDate(String startDate, int limit) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Weight WHERE DateTime >='" + startDate + "' ORDER BY DateTime DESC LIMIT "+limit+";", null);
		LinkedList<WeightRec> weightRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			WeightRec tmp;
			do {
				tmp = new WeightRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				weightRecs.add(tmp);

				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return weightRecs;
		} else {
			cursor.close();
			return weightRecs;
		}
	}

	//------------------- HbA1c
	public ArrayList<HbA1cRec> HbA1c_GetBtDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_A1c WHERE  DateTime > '" + from + " 00:00:00' AND DateTime < '" + to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<HbA1cRec> allreads = new ArrayList<>();
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

	public ArrayList<HbA1cRec> HbA1c_GetAll() {
		ArrayList<HbA1cRec> AllReads = new ArrayList<>();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_A1c", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			HbA1cRec tmp;
			do {
				tmp = new HbA1cRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
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

	@Nullable
	public int getNumberHbA1cByDate(String day) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_A1c WHERE DateTime LIKE '%" + day + "%' ORDER BY DateTime DESC;", null);
		LinkedList<HbA1cRec> hbA1cRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
            int number = cursor.getInt(0);
			cursor.close();
			return number;
		} else {
			cursor.close();
			return 0;
		}
	}

	@Nullable
	public LinkedList<HbA1cRec> getHbA1cFromStartDate(String startDate, int limit) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_A1c WHERE DateTime >='" + startDate + "' ORDER BY DateTime DESC LIMIT "+limit+";", null);
		LinkedList<HbA1cRec> hbA1cRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			HbA1cRec tmp;
			do {
				tmp = new HbA1cRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				hbA1cRecs.add(tmp);

				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return hbA1cRecs;
		} else {
			cursor.close();
			return hbA1cRecs;
		}
	}



	//----------------------- DISEASE REG

	@Nullable
	public ArrayList<DiseaseRec> DiseaseReg_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Disease;", null);
		ArrayList<DiseaseRec> list = new ArrayList<>();
		cursor.moveToFirst();
		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		}
		do {
			DiseaseRec tmp = new DiseaseRec();
			tmp.setId(cursor.getInt(0));
			tmp.setIdUser(cursor.getInt(1));
			tmp.setDisease(cursor.getString(2));
			tmp.setDateTime(cursor.getString(3));
			tmp.setEndDate((!cursor.isNull(4)) ? cursor.getString(4) : null);
			tmp.setIdNote((!cursor.isNull(5)) ? cursor.getInt(5) : -1);
			list.add(tmp);
		}while (!cursor.isAfterLast());
		cursor.close();
		return list;
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
		tmp.setDateTime(cursor.getString(3));
		tmp.setEndDate((!cursor.isNull(4)) ? cursor.getString(4) : null);
		tmp.setIdNote((!cursor.isNull(5)) ? cursor.getInt(5) : -1);

		cursor.close();
		return tmp;

	}

	@Nullable
	public DiseaseRec getLastDisease() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Disease;", null);
		cursor.moveToLast();
		if (cursor.getCount() == 0) {
			return null;
		}
		DiseaseRec tmp = new DiseaseRec();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setDisease(cursor.getString(2));
		tmp.setDateTime(cursor.getString(3));
		tmp.setEndDate(cursor.getString(4));
		tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);

		cursor.close();
		return tmp;

	}

	@Nullable
	public int getNumberDiseaseByDate(String day) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Disease WHERE StartDate LIKE '%" + day + "%' ORDER BY StartDate DESC;", null);
		LinkedList<DiseaseRec> diseaseRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
            int number = cursor.getInt(0);
			cursor.close();
			return number;
		} else {
			cursor.close();
			return 0;
		}
	}

	@Nullable
	public LinkedList<DiseaseRec> getDiseaseFromStartDate(String startDate, int limit) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Disease WHERE StartDate >='" + startDate + "' ORDER BY StartDate DESC LIMIT "+limit+";", null);
		LinkedList<DiseaseRec> diseaseRecs = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			DiseaseRec tmp;
			do {
				tmp = new DiseaseRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setDisease(cursor.getString(2));
				tmp.setDateTime(cursor.getString(3));
				tmp.setEndDate(cursor.getString(4));
				tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
				diseaseRecs.add(tmp);

				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return diseaseRecs;
		} else {
			cursor.close();
			return diseaseRecs;
		}
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



	public ArrayList<TargetBGRec> TargetBG_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM BG_Target", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<TargetBGRec> targets = new ArrayList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			TargetBGRec t;
			do {
				t = new TargetBGRec();
				t.setId(cursor.getInt(0));
				t.setName(cursor.getString(1));
				t.setTimeStart(cursor.getString(2));
				t.setTimeEnd(cursor.getString(3));
				t.setValue(cursor.getInt(4));
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

	public boolean Ratio_exists(int id) {
		Cursor cursor = myDB.rawQuery("SELECT Id FROM Ratio_Reg Where Id = "+id, null);
		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean Sensitivity_exists(int id) {
		Cursor cursor = myDB.rawQuery("SELECT Id FROM Sensitivity_Reg Where Id = "+id, null);
		ArrayList<Sensitivity> targets = null;
		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}
	public int Sensitivity_GetCurrent(String currentTime) {
		Cursor cursor = myDB.rawQuery("SELECT Value FROM Sensitivity_Reg Where TimeStart <= '"+currentTime+ "' ORDER BY TimeStart DESC limit 1", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
//			Log.i("cenas", "Sensitivity_GetCurrent: ->>>>>> "+currentTime+ " with value: "+cursor.getString(0));
			return Integer.parseInt(cursor.getString(0));
		} else {
			cursor.close();
			return -1;
		}
	}
	public int Ratio_GetCurrent(String currentTime) {
		Cursor cursor = myDB.rawQuery("SELECT Value FROM Ratio_Reg Where TimeStart <= '"+currentTime+ "' ORDER BY TimeStart DESC limit 1", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
//			Log.i("cenas", "RATIO_GetCurrent: ->>>>>> "+currentTime+ " with value: "+cursor.getString(0));
			return Integer.parseInt(cursor.getString(0));
		} else {
			cursor.close();
			return -1;
		}
	}

	public ArrayList<Sensitivity> Sensitivity_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT Id, Id_User, Value, Name, TimeStart, TimeEnd FROM Sensitivity_Reg ORDER BY TimeStart", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<Sensitivity> targets = null;
		if (cursor.getCount() > 0) {
			targets = new ArrayList<>();
			cursor.moveToFirst();
			Sensitivity t;
			do {
				t = new Sensitivity();
				t.setId(cursor.getInt(0));
				t.setUser_id(cursor.getInt(1));
				t.setSensitivity(cursor.getDouble(2));
				t.setName(cursor.getString(3));
				t.setStart(cursor.getString(4));
				t.setEnd(cursor.getString(5));
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
	public ArrayList<CarbsRatioData> Ratio_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT Id, Id_User, Value, Name, TimeStart, TimeEnd FROM Ratio_Reg ORDER BY TimeStart", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<CarbsRatioData> targets = null;
		if (cursor.getCount() > 0) {
			targets = new ArrayList<>();
			cursor.moveToFirst();
            CarbsRatioData t;
			do {
				t = new CarbsRatioData();
				t.setId(cursor.getInt(0));
				t.setUser_id(cursor.getInt(1));
				t.setValue(cursor.getDouble(2));
				t.setName(cursor.getString(3));
				t.setStart(cursor.getString(4));
				t.setEnd(cursor.getString(5));
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


	public Sensitivity Sensitivity_GetByID(String id) {
		Cursor cursor = myDB.rawQuery("SELECT Id, Value, Name, TimeStart, TimeEnd FROM Sensitivity_Reg Where Id = "+ id , null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Sensitivity t;
			t = new Sensitivity();
			t.setId(cursor.getInt(0));
			t.setSensitivity(cursor.getDouble(1));
			t.setName(cursor.getString(2));
			t.setStart(cursor.getString(3));
			t.setEnd(cursor.getString(4));
			cursor.close();
			return t;
		} else {
			cursor.close();
			return null;
		}
	}
	public CarbsRatioData Ratio_GetById(String id) {
		Cursor cursor = myDB.rawQuery("SELECT Id, Value, Name, TimeStart, TimeEnd FROM Ratio_Reg Where Id = "+ id, null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			CarbsRatioData t;
			t = new CarbsRatioData();
			t.setId(cursor.getInt(0));
			t.setValue(cursor.getDouble(1));
			t.setName(cursor.getString(2));
			t.setStart(cursor.getString(3));
			t.setEnd(cursor.getString(4));
			cursor.close();
			return t;
		} else {
			cursor.close();
			return null;
		}
	}


	public ArrayList<InsulinTarget> Target_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM BG_Target ORDER BY TimeStart", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<InsulinTarget> targets = new ArrayList<>();
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


		public int getLogBookCount(String startDate) {
		Cursor cursor = myDB.rawQuery("SELECT COUNT(*) " +
				"FROM Record "+
				"WHERE datetime >='" + startDate + "'",null);

			cursor.moveToFirst();
			return cursor.getInt(0);

	}



//	public int getLogBookCount(String startDate) {
//		Cursor cursor = myDB.rawQuery("SELECT COUNT(*) " +
//				"FROM (" +
//				"SELECT DISTINCT datetime, tag, carbs, insulinVal, insulinName, glycemia, carbsId, insulinId, glycemiaId" +
//				" FROM " +
//				"(" +
//				"SELECT Reg_CarboHydrate.DateTime as datetime, Tag.Name as tag, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
//				" FROM Reg_CarboHydrate, Tag, Reg_Insulin, Reg_BloodGlucose, Insulin" +
//				" WHERE Reg_CarboHydrate.DateTime = Reg_Insulin.DateTime" +
//				" AND Reg_CarboHydrate.DateTime = Reg_BloodGlucose.DateTime" +
//				" AND Tag.Id = Reg_CarboHydrate.Id_Tag AND Reg_Insulin.Id_Insulin = Insulin.Id" +
//				" UNION " +
//				"SELECT Reg_CarboHydrate.DateTime as datetime, Tag.Name as tag, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, -1 AS glycemia, -1 as glycemiaId" +
//				" FROM Reg_CarboHydrate, Tag, Reg_Insulin, Insulin" +
//				" WHERE Reg_CarboHydrate.DateTime = Reg_Insulin.DateTime" +
//				" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
//				" AND Tag.Id = Reg_CarboHydrate.Id_Tag AND Reg_Insulin.Id_Insulin = Insulin.Id" +
//				" UNION " +
//				"SELECT Reg_CarboHydrate.DateTime as datetime, Tag.Name as tag, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
//				" FROM Reg_CarboHydrate, Tag, Reg_BloodGlucose" +
//				" WHERE Reg_CarboHydrate.DateTime = Reg_BloodGlucose.DateTime " +
//				" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
//				" AND Tag.Id = Reg_CarboHydrate.Id_Tag" +
//				" UNION " +
//				"SELECT Reg_CarboHydrate.DateTime as datetime, Tag.Name as tag, Reg_CarboHydrate.Value as carbs, Reg_CarboHydrate.Id as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, -1 AS glycemia, -1 as glycemiaId" +
//				" FROM Reg_CarboHydrate, Tag" +
//				" WHERE Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
//				" AND Reg_CarboHydrate.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
//				" AND Tag.Id = Reg_CarboHydrate.Id_Tag" +
//				" UNION " +
//				"SELECT Reg_BloodGlucose.DateTime as datetime, Tag.Name as tag, -1 as carbs, -1 as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
//				" FROM Tag, Reg_Insulin, Reg_BloodGlucose, Insulin" +
//				" WHERE Reg_BloodGlucose.DateTime = Reg_Insulin.DateTime " +
//				" AND Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
//				" AND Tag.Id = Reg_BloodGlucose.Id_Tag AND Reg_Insulin.Id_Insulin = Insulin.Id" +
//				" UNION " +
//				"SELECT Reg_BloodGlucose.DateTime as datetime, Tag.Name as tag, -1 as carbs, -1 as carbsId, -1 AS insulinVal, '' AS insulinName, -1 as insulinId, Reg_BloodGlucose.Value AS glycemia, Reg_BloodGlucose.Id as glycemiaId" +
//				" FROM Tag, Reg_BloodGlucose" +
//				" WHERE " +
//				"Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
//				" AND Reg_BloodGlucose.DateTime NOT IN (SELECT Reg_Insulin.DateTime FROM Reg_Insulin)" +
//				" AND Tag.Id = Reg_BloodGlucose.Id_Tag" +
//				" UNION " +
//				"SELECT Reg_Insulin.DateTime as datetime, Tag.Name as tag, -1 as carbs, -1 as carbsId, Reg_Insulin.Value AS insulinVal, Insulin.Name AS insulinName, Reg_Insulin.Id as insulinId, -1 AS glycemia, -1 as glycemiaId" +
//				" FROM  Tag, Reg_Insulin, Reg_BloodGlucose, Insulin" +
//				" WHERE " +
//				"Reg_Insulin.DateTime NOT IN (SELECT Reg_CarboHydrate.DateTime FROM Reg_CarboHydrate)" +
//				" AND Reg_Insulin.DateTime NOT IN (SELECT Reg_BloodGlucose.DateTime FROM Reg_BloodGlucose)" +
//				" AND Tag.Id = Reg_Insulin.Id_Tag AND Reg_Insulin.Id_Insulin = Insulin.Id" +
//				")" +
//				"WHERE datetime >='" + startDate + "'" +
//				")",null);
//
//			cursor.moveToFirst();
//			return cursor.getInt(0);
//
//	}

	public LinkedList<HomeElement> getLogBookFromStartDate(String startDate) {
		Cursor cursor = myDB.rawQuery("SELECT Id, DateTime, Id_Tag, Id_Carbs, Id_Insulin, Id_BloodGlucose, Id_Note From Record WHERE datetime >='" +
				startDate + "' ORDER BY Record.DateTime DESC LIMIT 9", null);

		LinkedList<HomeElement> logBookEntries = new LinkedList<>();
		LinkedList<RawRecord> rawRecords = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			RawRecord tmp;
			do {
				tmp = new RawRecord(
						cursor.getInt(0),//ID
						cursor.getString(1),//DATETIME
						cursor.getInt(2),//ID TAG
						cursor.getInt(3),//CARBS
						cursor.getInt(4),//INSULIN
						cursor.getInt(5),//GLUCOSE
						cursor.getInt(6));//NOTE
				//insert id_note
//				Log.i(TAG, "--------------------------------");
//				Log.i(TAG, tmp.toString());
//				Log.i(TAG, "--------------------------------");
				rawRecords.add(tmp);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
		} else {
			cursor.close();
		}
//		Log.i(TAG, "number records = "+rawRecords.size());
		if(!rawRecords.isEmpty()){

			logBookEntries = new LinkedList<>();
			for(RawRecord record:rawRecords){

//			    String sqlCommand = create_sql_string(record);
//                Log.i(TAG, "sql_command: "+sqlCommand);

//				cursor = myDB.rawQuery(sqlCommand,null);

//				if (cursor.getCount() > 0) {
//					cursor.moveToFirst();

				String insuName = null;
				float insuValue = -1;

				Pair insuNameValue = getInsuNameValueByID(record.getId_insulin());
				if(insuNameValue!=null){
					insuName = (String) insuNameValue.first;
					insuValue = (float) insuNameValue.second;
				}

					HomeElement tmp;
//					do {
						try{
							//int recordId, String dateTime, int tag, int carbs, float insulinVal, String insulinName, int glycemia, int carbsId, int insulinId, int glycemiaId, int note_id
							tmp = new HomeElement(
									record.getId(),
									record.getFormattedDate(),
									record.get_tag(),
									getCarbsValByID(record.getId_carbs()),
									insuValue,
									insuName,
									getGlycaemiaValByID(record.getId_bloodglucose()),
									record.getId_carbs(),
									record.getId_insulin(),
									record.getId_bloodglucose(),
									record.getId_note());
							//insert id_note
							logBookEntries.add(tmp);
						}catch (Exception e){
							e.printStackTrace();
						}
//						cursor.moveToNext();
//					} while (!cursor.isAfterLast());
//					cursor.close();
//				} else {
//					cursor.close();
//				}
//				Log.i(TAG, "number records after = "+logBookEntries.size());
			}
		}
		return logBookEntries;
	}


	private int getGlycaemiaValByID(int id_bloodglucose) {
		int value = -1;

		Cursor cursor = myDB.rawQuery("SELECT Value FROM Reg_BloodGlucose WHERE Id = '"+id_bloodglucose+"';", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			value = cursor.getInt(0);
			cursor.close();
			return value;
		} else {
			cursor.close();
			return -1;
		}
	}


	private int getCarbsValByID(int id_carbs) {

		int value = -1;

		Cursor cursor = myDB.rawQuery("SELECT Value FROM Reg_CarboHydrate WHERE Id = "+id_carbs+";", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			value = cursor.getInt(0);
			cursor.close();
			return value;
		} else {
			cursor.close();
			return -1;
		}
	}

	private int getInsuValByID(int id_insulin) {

		int value = -1;

		Cursor cursor = myDB.rawQuery("SELECT Value FROM Reg_Insulin WHERE Id = "+id_insulin+";", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			value = cursor.getInt(0);
			cursor.close();
			return value;
		} else {
			cursor.close();
			return -1;
		}
	}

	private Pair getInsuNameValueByID(int ID) {

		int insu_ID = -1;
		float insu_value = -1;
		String name = null;

		Cursor cursor = myDB.rawQuery("SELECT Id_Insulin, Value FROM Reg_Insulin WHERE Id = "+ID+";", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			insu_ID = cursor.getInt(0);
			insu_value = cursor.getInt(1);

			if(insu_ID!=-1){
				cursor = myDB.rawQuery("SELECT Name FROM Insulin WHERE Id = "+insu_ID+";", null);
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					name = cursor.getString(0);
					cursor.close();
				}
			}
			cursor.close();
			return new Pair(name, insu_value);
		} else {
			cursor.close();
			return null;
		}


	}

	public LinkedList<BadgeRec> Badges_GetAll_NONDAILY() {
		LinkedList<BadgeRec> AllReads = new LinkedList<>();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Badges WHERE TYPE != 'daily' ORDER BY DateTime DESC;", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			BadgeRec tmp;
			do {
				tmp = new BadgeRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setDateTime(cursor.getString(2));
				tmp.setType(cursor.getString(3));
				tmp.setName(cursor.getString(4));
				tmp.setMedal(cursor.getString(5));
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

	public LinkedList<BadgeRec> Badges_GetAll() {

		String now = DateUtils.getFormattedDate(Calendar.getInstance());
		LinkedList<BadgeRec> non_daily = Badges_GetAll_NONDAILY();
		LinkedList<BadgeRec> current_daily = getAllDailyBadgesByDate(now);
		System.out.println("BADGES_GEALL: "+non_daily);
		non_daily.addAll(current_daily);
		return non_daily;

//		LinkedList<BadgeRec> AllReads = new LinkedList<>();
//		Cursor cursor = myDB.rawQuery("SELECT * FROM Badges ORDER BY DateTime DESC;", null);
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//			BadgeRec tmp;
//			do {
//				tmp = new BadgeRec();
//				tmp.setId(cursor.getInt(0));
//				tmp.setIdUser(cursor.getInt(1));
//				tmp.setDateTime(cursor.getString(2));
//				tmp.setType(cursor.getString(3));
//				tmp.setName(cursor.getString(4));
//				tmp.setMedal(cursor.getString(5));
//				AllReads.add(tmp);
//				cursor.moveToNext();
//			} while (!cursor.isAfterLast());
//			cursor.close();
//			return AllReads;
//		} else {
//			cursor.close();
//			return AllReads;
//		}
	}


	public ArrayList<BadgeRec> getAllMedals() {
		ArrayList<BadgeRec> AllReads = new ArrayList<>();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Badges;", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			BadgeRec tmp;
			do {
				tmp = new BadgeRec();
				tmp.setIdUser(cursor.getInt(1));
				tmp.setDateTime(cursor.getString(2));
				tmp.setType(cursor.getString(3));
				tmp.setName(cursor.getString(4));
				tmp.setMedal(cursor.getString(5));
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

	public LinkedList<BadgeRec> Badges_GetBadgeList(String difficulty) {
		LinkedList<BadgeRec> AllReads = new LinkedList<>();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Badges WHERE TYPE = "+difficulty+" ORDER BY DateTime DESC;", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			BadgeRec tmp;
			do {
				tmp = new BadgeRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setDateTime(cursor.getString(2));
				tmp.setType(cursor.getString(3));
				tmp.setName(cursor.getString(4));
				tmp.setMedal(cursor.getString(5));
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

	public BadgeRec getLastDailyMedal(){
		//Cursor cursor = myDB.rawQuery("SELECT * FROM Badges WHERE TYPE = 'daily' and DATETIME >= date('now');", null);
		String now = DateUtils.getFormattedDate(Calendar.getInstance());
		Cursor cursor = myDB.rawQuery("SELECT * FROM Badges WHERE TYPE = 'daily' AND DateTime LIKE '%" + now + "%' ORDER BY DateTime DESC;", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			BadgeRec tmp;

			tmp = new BadgeRec();
			tmp.setId(cursor.getInt(0));
			tmp.setIdUser(cursor.getInt(1));
			tmp.setDateTime(cursor.getString(2));
			tmp.setType(cursor.getString(3));
			tmp.setName(cursor.getString(4));
			tmp.setMedal(cursor.getString(5));

			cursor.close();
			return tmp;
		} else {
			cursor.close();
			return null;
		}
	}

	public int Badges_GetCount(String difficulty) {
		LinkedList<BadgeRec> AllReads = new LinkedList<>();
		Cursor cursor;
		if(difficulty.equals("daily")){
			cursor = myDB.rawQuery("SELECT Count(*) FROM Badges WHERE TYPE = 'daily' and DATETIME >= date('now');", null);
		}else{
			cursor = myDB.rawQuery("SELECT Count(*) FROM Badges WHERE TYPE = '"+difficulty+"' ORDER BY DateTime DESC;", null);
		}

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			cursor.close();
			return count;
		} else {
			cursor.close();
			return 0;
		}
	}

	public ArrayList<CarbsRec> CarbsRec_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_CarboHydrate", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<CarbsRec> carbsRecs = new ArrayList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				CarbsRec oldRec = new CarbsRec();
				oldRec.setIdUser(cursor.getInt(1));
				oldRec.setCarbsValue(cursor.getInt(2));
				oldRec.setPhotoPath(cursor.getString(3));
				oldRec.setDateTime(cursor.getString(4));
				oldRec.setIdTag(cursor.getInt(5));
				oldRec.setIdNote(cursor.getInt(6));
				carbsRecs.add(oldRec);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return carbsRecs;
		} else {
			cursor.close();
			return null;
		}
	}


	public LinkedList<BadgeRec> getAllMedals(String name) {
		LinkedList<BadgeRec> AllReads = new LinkedList<>();
		Cursor cursor = myDB.rawQuery("SELECT Type, Medal FROM Badges Where Name='"+name+"'ORDER BY DateTime DESC;", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			BadgeRec tmp;
			do {
				tmp = new BadgeRec();
				tmp.setType(cursor.getString(0));
				tmp.setMedal(cursor.getString(1));
//				tmp = new BadgeRec();
//				tmp.setId(cursor.getInt(0));
//				tmp.setIdUser(cursor.getInt(1));
//				tmp.setDateTime(cursor.getString(2));
//				tmp.setType(cursor.getString(3));
//				tmp.setName(cursor.getString(4));
//				tmp.setMedal(cursor.getString(5));
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

	public Boolean hasMedal(String name) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Badges Where Name = '"+name+"';", null);
		return cursor.getCount() != 0;
	}

    @Nullable
    public int getNumberOfAllDailyBadgesByDate(String day) {
        Cursor cursor = myDB.rawQuery("SELECT COUNT(*) FROM Badges WHERE Type = 'daily' AND DateTime LIKE '%" + day + "%' ORDER BY DateTime DESC;", null);
        LinkedList<BadgeRec> AllReads = new LinkedList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int number = cursor.getInt(0);
            cursor.close();
            return number;
        } else {
            cursor.close();
            return 0;
        }
    }

	@Nullable
	public LinkedList<BadgeRec> getAllDailyBadgesByDate(String day) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Badges WHERE Type = 'daily' AND DateTime LIKE '%" + day + "%' ORDER BY DateTime DESC;", null);
		LinkedList<BadgeRec> AllReads = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			BadgeRec tmp;
			do {
				tmp = new BadgeRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setDateTime(cursor.getString(2));
				tmp.setType(cursor.getString(3));
				tmp.setName(cursor.getString(4));
				tmp.setMedal(cursor.getString(5));
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

	public int getTotalPoints() {
		Cursor cursor = myDB.rawQuery("SELECT SUM(Value) FROM Points;", null);
		cursor.moveToLast();
		if (cursor.getCount() == 0) {
			return 0;
		}
		int points = cursor.getInt(0);
		cursor.close();
		return points;
	}

	public int Points_get_num_reg() {
		Cursor cursor = myDB.rawQuery("SELECT COUNT(*) FROM Points;", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getInt(0);
		}else{
			return -1;
		}
	}

	public ArrayList<PointsRec> PointsReg_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Points;", null);
		ArrayList<PointsRec> AllPoints = new ArrayList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			PointsRec tmp;
			do {
				tmp = new PointsRec();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setDateTime(cursor.getString(2));
				tmp.setValue(cursor.getInt(3));
				tmp.setOrigin(cursor.getString(4));
				AllPoints.add(tmp);
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return AllPoints;
		} else {
			cursor.close();
			return AllPoints;
		}
	}

    @Nullable
    public PointsRec getFirstPointToReachLevel(int points) {
        Cursor cursor = myDB.rawQuery("SELECT Points1.Id, Points1.Id_User, Points1.DateTime, Points1.Value, Points1.Origin, SUM(Points2.Value) AS SUMMATORY " +
                "FROM Points as Points1 " +
                "INNER JOIN Points as Points2 ON Points1.id >= Points2.id GROUP BY Points1.id, Points1.Value " +
                "HAVING SUMMATORY >= "+points+" "+
                "ORDER BY Points1.id " +
                "LIMIT 1;", null);
        cursor.moveToLast();
        if (cursor.getCount() == 0) {
            return null;
        }
        PointsRec tmp = new PointsRec();
        tmp.setId(cursor.getInt(0));
        tmp.setIdUser(cursor.getInt(1));
        tmp.setDateTime(cursor.getString(2));
        tmp.setValue(cursor.getInt(3));
        tmp.setOrigin(cursor.getString(4));

        cursor.close();
        return tmp;

    }

	public boolean sensitivityTimeStartExists(String start, String id) {
		Cursor cursor = myDB.rawQuery("SELECT Id "+
				"FROM Sensitivity_Reg "+
				"WHERE TimeStart == '" + start +"' and Id != '"+id+ "' "+
				"ORDER BY TimeStart", null);
		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean ratioTimeStartExists(String start, String id) {
		Cursor cursor = myDB.rawQuery("SELECT Id "+
				"FROM Ratio_Reg "+
				"WHERE TimeStart == '" + start +"' and Id != '"+id+ "' "+
				"ORDER BY TimeStart", null);
		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean tagTimeStartExists(String start, String id) {
		Cursor cursor = myDB.rawQuery("SELECT Id "+
				"FROM Tag "+
				"WHERE TimeStart == '" + start +"' and Id != '"+id+ "' "+
				"ORDER BY TimeStart", null);
		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public LinkedList<String> countSensOverlap(String start, String end, int id) {
		Cursor cursor = myDB.rawQuery("SELECT Name, TimeStart, TimeEnd "+
				"FROM Sensitivity_Reg "+
				"WHERE TimeStart >= '" + start +"' and TimeEnd <= '"+end+"' and TimeStart < '"+end+"' and Id != "+id+" "+
				"ORDER BY TimeStart", null);
		cursor.moveToLast();
		LinkedList<String> AllReads = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				AllReads.add(cursor.getString(0));
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return AllReads;
		} else {
			cursor.close();
			return null;
		}
	}

	public String getNextRatioTime(CarbsRatioData target) {
		Cursor cursor = myDB.rawQuery("SELECT TimeEnd "+
				"FROM Ratio_Reg "+
				"WHERE TimeStart > '" + target.getEnd() + "' "+
				"ORDER BY TimeStart limit 1", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Sensitivity t;
			t = new Sensitivity();
			t.setEnd(cursor.getString(0));
			cursor.close();
			return t.getEnd();
		} else {
			cursor.close();
			return null;
		}
	}

	public String getNextSensTime(Sensitivity target) {

	    //search the starting time of the next tag
        //we want the closest hour to our given starting hour
        //if the clock starts before 00h and the next time is after 00h this query will return null
        //in that case we're be searching for the starting hour

		Cursor cursor = myDB.rawQuery("SELECT TimeStart "+ //"SELECT TimeEnd "+
				"FROM Sensitivity_Reg "+
				"WHERE TimeStart >'" + target.getStart()+"' "+ //.getEnd() + "' "+
				"ORDER BY TimeStart limit 1", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			//Sensitivity t;
			//t = new Sensitivity();
			//t.setEnd(cursor.getString(0));
			//cursor.close();
			//return t.getEnd();
			return cursor.getString(0);
		} else {//second attempt
            cursor = myDB.rawQuery("SELECT TimeStart "+ //"SELECT TimeEnd "+
                    "FROM Sensitivity_Reg "+
                    //"WHERE TimeStart >'" + target.getStart()+"' "+ //.getEnd() + "' "+
                    "ORDER BY TimeStart limit 1", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                //Sensitivity t;
                //t = new Sensitivity();
                //t.setEnd(cursor.getString(0));
                //cursor.close();
                //return t.getEnd();
                return cursor.getString(0);
            } else {
                cursor.close();
                return null;
            }
		}
	}

	public Sensitivity getPreviousRatio(Sensitivity target) {
		Cursor cursor = myDB.rawQuery("SELECT *"+
				"FROM Sensitivity_Reg "+
				"WHERE TimeStart < '" + target.getStart() + "' "+
				"ORDER BY TimeStart DESC limit 1", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Sensitivity t;
			t = new Sensitivity();
			t.setId(cursor.getInt(0));
			t.setUser_id(cursor.getInt(1));
			t.setSensitivity(cursor.getDouble(2));
			t.setName(cursor.getString(3));
			t.setStart(cursor.getString(4));
			t.setEnd(cursor.getString(5));
			cursor.close();
			return t;
		} else {
			cursor.close();
			return null;
		}
	}

	public Sensitivity getNextRatio(Sensitivity target) {
		Cursor cursor = myDB.rawQuery("SELECT *"+
				"FROM Sensitivity_Reg "+
				"WHERE TimeEnd > '" + target.getEnd() + "' "+
				"ORDER BY TimeStart limit 1", null);
		cursor.moveToLast();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			Sensitivity t;
			t = new Sensitivity();
			t.setId(cursor.getInt(0));
			t.setUser_id(cursor.getInt(1));
			t.setSensitivity(cursor.getDouble(2));
			t.setName(cursor.getString(3));
			t.setStart(cursor.getString(4));
			t.setEnd(cursor.getString(5));
			cursor.close();
			return t;
		} else {
			cursor.close();
			return null;
		}
	}


	public LinkedList<Integer> countRatioOverlap(String start, String end,int id) {
		Cursor cursor = myDB.rawQuery("SELECT Id, TimeStart, TimeEnd "+
				"FROM Ratio_Reg "+
				"WHERE TimeStart >= '" + start +"' and TimeEnd <= '"+end+"' and TimeStart < '"+end+"' and Id != "+id+" "+
				"ORDER BY TimeStart", null);
		cursor.moveToLast();
		LinkedList<Integer> AllReads = new LinkedList<>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				AllReads.add(cursor.getInt(0));
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return AllReads;
		} else {
			cursor.close();
			return null;
		}
	}

	public CarbsRatioData getPreviousRatio(CarbsRatioData target) {
		Cursor cursor = myDB.rawQuery("SELECT *"+
				"FROM Ratio_Reg "+
				"WHERE TimeStart < '" + target.getStart() + "' "+
				"ORDER BY TimeStart DESC limit 1", null);
		cursor.moveToLast();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			CarbsRatioData t;
			t = new CarbsRatioData();
			t.setId(cursor.getInt(0));
			t.setUser_id(cursor.getInt(1));
			t.setValue(cursor.getDouble(2));
			t.setName(cursor.getString(3));
			t.setStart(cursor.getString(4));
			t.setEnd(cursor.getString(5));
			cursor.close();
			return t;
		} else {
			cursor.close();
			return null;
		}
	}

	public CarbsRatioData getNextRatio(CarbsRatioData target) {
		Cursor cursor = myDB.rawQuery("SELECT *"+
				"FROM Ratio_Reg "+
				"WHERE TimeEnd > '" + target.getEnd() + "' "+
				"ORDER BY TimeStart limit 1", null);
		cursor.moveToLast();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			CarbsRatioData t;
			t = new CarbsRatioData();
			t.setId(cursor.getInt(0));
			t.setUser_id(cursor.getInt(1));
			t.setValue(cursor.getDouble(2));
			t.setName(cursor.getString(3));
			t.setStart(cursor.getString(4));
			t.setEnd(cursor.getString(5));
			cursor.close();
			return t;
		} else {
			cursor.close();
			return null;
		}
	}
    private String create_sql_string(RawRecord record){

        String sqlCommand = "SELECT ";
        String selectCarbs;
        String fromCarbs;
        String whereCarbs;

        String selectInsu;
        String fromInsu;
        String whereInsu;

        String selectGluc;
        String fromGluc;
        String whereGluc;

        //carbs, insu, insuname, glycemia
        if(record.getId_carbs()!=-1){//TODO '' != -1 <- reparar
            selectCarbs = " Reg_CarboHydrate.Value ";
            fromCarbs = " Reg_CarboHydrate ";
            whereCarbs = " Reg_CarboHydrate.Id ='"+record.getId_carbs()+"' ";

            if(record.getId_insulin()!=-1){//TODO '' != -1 <- reparar
                selectInsu = ", Reg_Insulin.Value, Insulin.Name ";
                fromInsu = ", Reg_Insulin, Insulin ";
                whereInsu = " and Reg_Insulin.Id = '"+record.getId_insulin()+"' and Reg_Insulin.Id_Insulin = Insulin.Id ";

                if(record.getId_bloodglucose()!=-1){//TODO '' != -1 <- reparar
                    selectGluc = " , Reg_BloodGlucose.Value ";
                    fromGluc = ", Reg_BloodGlucose ";
                    whereGluc = " and Reg_BloodGlucose.Id = '"+record.getId_bloodglucose()+"' ";

                }else{
                    selectGluc = " , -1 ";
                    fromGluc = " ";
                    whereGluc = " ";
                }
            }else{
                selectInsu = " , -1, -1 ";
                fromInsu = " ";
                whereInsu = " ";

                if(record.getId_bloodglucose()!=-1){//TODO '' != -1 <- reparar
                    selectGluc = " , Reg_BloodGlucose.Value ";
                    fromGluc = " , Reg_BloodGlucose ";
                    whereGluc = " and Reg_BloodGlucose.Id = '"+record.getId_bloodglucose()+"' ";
                }else{
                    selectGluc = " , -1 ";
                    fromGluc = " ";
                    whereGluc = " ";
                }
            }
        }else{
            selectCarbs = "-1";
            fromCarbs = " ";
            whereCarbs = " ";

            if(record.getId_insulin()!=-1){//TODO '' != -1 <- reparar
                selectInsu = " ,Reg_Insulin.Value, Insulin.Name ";
                fromInsu = " Reg_Insulin, Insulin ";
                whereInsu = " Reg_Insulin.Id = '"+record.getId_insulin()+"' and Reg_Insulin.Id_Insulin = Insulin.Id ";

                if(record.getId_bloodglucose()!=-1){//TODO '' != -1 <- reparar
                    selectGluc = " , Reg_BloodGlucose.Value ";
                    fromGluc = " ,Reg_BloodGlucose ";
                    whereGluc = " and Reg_BloodGlucose.Id = '"+record.getId_bloodglucose()+"' ";
                }else{
                    selectGluc = " , -1 ";
                    fromGluc = " ";
                    whereGluc = " ";
                }
            }else{
				selectInsu = " , -1, -1 ";
                fromInsu = " ";
                whereInsu = " ";

                if(record.getId_bloodglucose()!=-1){//TODO '' != -1 <- reparar
                    selectGluc = " , Reg_BloodGlucose.Value ";
                    fromGluc = " Reg_BloodGlucose ";
                    whereGluc = " Reg_BloodGlucose.Id = '"+record.getId_bloodglucose()+"' ";
                }else{
                    selectGluc = " , -1 ";
                    fromGluc = " ";
                    whereGluc = " ";
                }
            }
        }


        sqlCommand+=selectCarbs+selectInsu+selectGluc+" From "+fromCarbs+fromInsu+fromGluc+" Where "+whereCarbs+whereInsu+whereGluc+ ";";
        return sqlCommand;
    }

	public ArrayList<Integer> getLastGlycaemias() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose WHERE DateTime > DateTime('now','start of day') AND DateTime < DateTime('now','start of day','+24 HOURS');", null);
		ArrayList<Integer> glycemiaList = new ArrayList<>();

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				glycemiaList.add(Integer.valueOf(cursor.getString(2)));
				cursor.moveToNext();
			} while (!cursor.isAfterLast());
			cursor.close();
			return glycemiaList;
		}

		else {
			cursor.close();
			return glycemiaList;
		}
	}

	public int getGlicoRegNumber() {
		Cursor cursor = myDB.rawQuery("SELECT Count(*) from Reg_BloodGlucose", null);
		return cursor.getCount();
	}


	// (Rui Carvalho) Get glycemia quality values for the current or the past seven days
	public ArrayList<Integer> getPersonalInfo(int index) {
		ArrayList<Integer> personalInfo = new ArrayList<>();
		Cursor cursor = null;
		//index is used to obtain the values from the current day or last week, where 0 represent today and 1 represent the last week values

		// get today values
		if (index == 0) {
			cursor = myDB.rawQuery("SELECT sub.a, AVG((today.Value - sub.a) * (today.Value - sub.a)) as var, hypo.hypoglycemia, hyper.hyperglycemia From Reg_BloodGlucose," +
					"(SELECT * from Reg_BloodGlucose Where Reg_BloodGlucose.DateTime >= DateTime('now','localtime','start of day') AND Reg_BloodGlucose.DateTime < DateTime('now','localtime','start of day','+1 days')) AS today," +
					"(SELECT AVG(Value) as a From Reg_BloodGlucose Where Reg_BloodGlucose.DateTime >= DateTime('now','start of day') AND Reg_BloodGlucose.DateTime < DateTime('now','localtime','start of day','+1 days')) AS sub," +
					"(SELECT Count(*) as hypoglycemia From Reg_BloodGlucose Where Reg_BloodGlucose.Value < 70 and Reg_BloodGlucose.DateTime >= DateTime('now','localtime','start of day') AND Reg_BloodGlucose.DateTime < DateTime('now','localtime','start of day','+24 hours')) as hypo," +
					"(SELECT Count(*) as hyperglycemia From Reg_BloodGlucose Where Reg_BloodGlucose.Value > 180 and Reg_BloodGlucose.DateTime >= DateTime('now','localtime','start of day') AND Reg_BloodGlucose.DateTime < DateTime('now','localtime','start of day','+24 hours')) as hyper", null);
		}
		// get last week values
		if (index == 1) {
			cursor = myDB.rawQuery("SELECT sub.a, AVG((today.Value - sub.a) * (today.Value - sub.a)) as var, hypo.hypoglycemia, hyper.hyperglycemia From Reg_BloodGlucose," +
					"(SELECT * from Reg_BloodGlucose Where Reg_BloodGlucose.DateTime >= DateTime('now','localtime','start of day','-7 days') AND Reg_BloodGlucose.DateTime < DateTime('now','localtime','start of day')) AS today," +
					"(SELECT AVG(Value) as a From Reg_BloodGlucose Where Reg_BloodGlucose.DateTime >= DateTime('now','start of day','-7 days') AND Reg_BloodGlucose.DateTime < DateTime('now','localtime','start of day')) AS sub," +
					"(SELECT Count(*) as hypoglycemia From Reg_BloodGlucose Where Reg_BloodGlucose.Value < 70 and Reg_BloodGlucose.DateTime >= DateTime('now','localtime','start of day','-7 days') AND Reg_BloodGlucose.DateTime < DateTime('now','localtime','start of day')) as hypo," +
					"(SELECT Count(*) as hyperglycemia From Reg_BloodGlucose Where Reg_BloodGlucose.Value > 180 and Reg_BloodGlucose.DateTime >= DateTime('now','localtime','start of day','-7 days') AND Reg_BloodGlucose.DateTime < DateTime('now','localtime','start of day')) as hyper", null);
		}

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			if (cursor.getString(0) != null) {
				personalInfo.add(cursor.getInt(0));
				personalInfo.add((int) (Math.sqrt(cursor.getDouble(1))));
				personalInfo.add(cursor.getInt(2));
				personalInfo.add(cursor.getInt(3));
			}
			cursor.close();
			return personalInfo;
		}

		else {
			cursor.close();
			personalInfo = null;
			return personalInfo;
		}
	}

	// (Rui Carvalho) Number of glycaemia records by day
	public int getGlyRecordsNumberByDay(int date) {
		Cursor cursor;
		if (date == 0) {
			cursor = myDB.rawQuery("select Count(*) from Reg_BloodGlucose where DateTime >= DateTime('now','localtime','start of day') and DateTime < DateTime('now','localtime','start of day','+1 days')", null);
		}
		else {
			int date2 = date - 1;
			cursor = myDB.rawQuery("select Count(*) from Reg_BloodGlucose where DateTime >= DateTime('now','localtime','start of day','-" + date + " days') and DateTime < DateTime('now','localtime','start of day','-" + date2 + " days')", null);
		}
		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	public ArrayList<Integer> getXDaysGlycAverageAndVariability(int days) {
		ArrayList<Integer> averageAndVariability = new ArrayList<>();
		// validate if every day on "days" range has at least 3 blood glucose records
		for (int i=1; i<=days; i++) {
			if (getGlyRecordsNumberByDay(i) >= 6) {

			} else {
				averageAndVariability.add(-1);
				averageAndVariability.add(-1);
				return averageAndVariability;
			}
		}

		Cursor cursor = myDB.rawQuery("SELECT average.a, AVG((val.Value - average.a) * (val.Value - average.a)) as var From Reg_BloodGlucose," +
				"(SELECT * from Reg_BloodGlucose Where DateTime >= DateTime('now','localtime','start of day','-"+days+" days') AND DateTime < DateTime('now','localtime','start of day')) AS val," +
				"(SELECT AVG(Value) as a From Reg_BloodGlucose Where DateTime >= DateTime('now','start of day','-"+days+" days') AND DateTime < DateTime('now','localtime','start of day')) AS average", null);
		cursor.moveToFirst();

		int mean = cursor.getInt(0);
		int sd = (int) (Math.sqrt(cursor.getDouble(1)));
		int cv = (int) (sd*100/mean);
		averageAndVariability.add(mean);
		averageAndVariability.add(cv);
		return averageAndVariability;
	}

	public int getXDaysTimeInRange(int days, int hypo, int hyper) {
		// validate if every day on "days" range has at least 3 blood glucose records
		for (int i=1; i<=days; i++) {
			if (getGlyRecordsNumberByDay(i) >= 6) {

			} else {
				return -1;
			}
		}
		Cursor cursor = myDB.rawQuery("select a, b from Reg_BloodGlucose," +
				"(select count(*) as a from Reg_BloodGlucose where Value >= "+hypo+" and Value <= "+hyper+" and DateTime >= DateTime('now','localtime','start of day','-"+days+" days') and DateTime < DateTime('now','localtime','start of day')) as a,"+
				"(select count(*) as b from Reg_BloodGlucose where DateTime >= DateTime('now','localtime','start of day','-"+days+" days') and DateTime < DateTime('now','localtime','start of day')) as b", null);
		cursor.moveToFirst();

		if (cursor.getInt(3) > 0) {
			return (int) ((cursor.getInt(0)/cursor.getInt(1))*100);
		} else {
			return -1;
		}
	}

	public int getRecordsNumbersByDate(long startDays, long endDays) {
		Cursor cursor = myDB.rawQuery("select Count(*) from Reg_BloodGlucose where DateTime >= DateTime('now','localtime','start of day','-" + startDays + " days') and DateTime < DateTime('now','localtime','start of day','-" + endDays + " days')", null);
		cursor.moveToFirst();
		System.out.println("Value: "+cursor.getInt(0));
		return cursor.getInt(0);
	}

	//at least two health badges needed in beginner to change difficulty to medium
	public boolean getBeginnerHealthBadgesWon() {
		Cursor cursor = myDB.rawQuery("select * from Badges where Type = 'beginner' and Name = 'health'", null);
		int nMedals = cursor.getCount();
		System.out.println("HEALTH BADGES: "+cursor.getCount());
		if (nMedals >= 2) return true;
		else return false;
	}

	//at least two health badges needed in medium to change difficulty to advanced
	public boolean getMediumHealthBadgesWon() {
		Cursor cursor = myDB.rawQuery("select * from Badges where Type = 'medium' and Name = 'health'", null);
		int nMedals = cursor.getCount();
		System.out.println("HEALTH BADGES: "+cursor.getCount());
		if (nMedals >= 2) return true;
		else return false;
	}
}
