package com.jadg.mydiabetes.database;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

@SuppressLint("UseSparseArrays")
public class DB_Read {

	final Context myContext;
	final SQLiteDatabase myDB;
	
	public DB_Read(Context context) {
		super();
		// TODO Auto-generated constructor stub
		DB_Handler db = new DB_Handler(context);
		this.myContext = context;
		this.myDB = db.getReadableDatabase();
	}
	
	public void close() {
		myDB.close();
		Log.d("Close", "DB_Read");
	}

	
	public boolean MyData_HasData(){
		Cursor cursor = myDB.rawQuery("SELECT * FROM UserInfo", null);
		if (cursor.getCount() > 0) {
			return true;
		}
		else{
			return false;
		}
	}
	
	public Object[] MyData_Read() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM UserInfo", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		Object[] obj = new Object[11];
		if (cursor.getCount() > 0) {
	        cursor.moveToFirst();
	        //Id
	        obj[0] = Integer.parseInt(cursor.getString(0));
	        //Name
	        obj[1] = cursor.getString(1);
	        //Diabetes Type
	        obj[2] = cursor.getString(2);
	        //Insulin Ratio
	        obj[3] = Double.parseDouble(cursor.getString(3));
	        //Carbs Ratio
	        obj[4] = Double.parseDouble(cursor.getString(4));
	        //Lower Range
	        obj[5] = Double.parseDouble(cursor.getString(5));
	        //Higher Range
	        obj[6] = Double.parseDouble(cursor.getString(6));
	        //Birth Date
	        obj[7] = cursor.getString(7);
	        //Gender
	        obj[8] = cursor.getString(8);
	        //Height
	        obj[9] = Double.parseDouble(cursor.getString(9));
	        //DateTimeUpdate
	        obj[10] = cursor.getString(10);
	        
	        cursor.close();
	        return obj;
		}
		else {
			cursor.close();
			return null;
		}
	}
	
	
	//--------------- TAG ------------------------
	public int Tag_GetIdByName(String name){
		int n;
		Cursor cursor = myDB.rawQuery("SELECT Id FROM Tag where Name='" + name + "'", null);
		cursor.moveToFirst();
		n = cursor.getInt(0);
		cursor.close();
		return n;
	}

	public TagDataBinding Tag_GetById(int id){
		TagDataBinding tag = new TagDataBinding();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Tag where Id='" + id + "'", null);
		cursor.moveToFirst();
		tag.setId(cursor.getInt(0));
		tag.setName(cursor.getString(1));
		tag.setStart(cursor.getString(2));
		tag.setEnd(cursor.getString(3));
		cursor.close();
		return tag;
	}
	
	public TagDataBinding Tag_GetByTime(String time){
		TagDataBinding tag = new TagDataBinding();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Tag WHERE  "
							+ "(TimeStart < TimeEnd AND '" + time + "' >= TimeStart AND '" + time + "' <= TimeEnd)" +
							"OR "
							+ "(TimeStart > TimeEnd AND('" + time + "' >= TimeStart OR '" + time + "' <= TimeEnd ))"
							+ ";", null);
		//Cursor cursor = myDB.rawQuery("SELECT * FROM Tag where Id='" + id + "'", null);
		Log.d("tag by time", String.valueOf(cursor.getCount()));
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			tag.setId(cursor.getInt(0));
			tag.setName(cursor.getString(1));
			tag.setStart(cursor.getString(2));
			tag.setEnd(cursor.getString(3));
		}
		else{
			cursor = myDB.rawQuery("SELECT * FROM Tag WHERE  "
					+ "TimeStart IS NULL AND TimeEnd IS NULL ;", null);
			cursor.moveToFirst();
			tag.setId(cursor.getInt(0));
			tag.setName(cursor.getString(1));
			tag.setStart(cursor.getString(2));
			tag.setEnd(cursor.getString(3));
		}
		cursor.close();
		return tag;
	}
	
	public ArrayList<TagDataBinding> Tag_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Tag", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<TagDataBinding> tags = new ArrayList<TagDataBinding>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			TagDataBinding t;
			do{
				t = new TagDataBinding();
				t.setId(cursor.getInt(0));
				t.setName(cursor.getString(1));
				t.setStart(cursor.getString(2));
				t.setEnd(cursor.getString(3));
				tags.add(t);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return tags;
		}
		else {
			cursor.close();
			return tags;
		}
	}
	
	
	//------------------- DISEASE ---------------------
	public ArrayList<DiseaseDataBinding> Disease_GetAll() {
		ArrayList<DiseaseDataBinding> AllReads = new ArrayList<DiseaseDataBinding>();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Disease", null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			DiseaseDataBinding tmp;
			do{
				tmp = new DiseaseDataBinding();
				tmp.setId(cursor.getInt(0));
				tmp.setName(cursor.getString(1));
				AllReads.add(tmp);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return AllReads;
		}
		else {
			cursor.close();
			return AllReads;
		}
	}
	
	public int Disease_GetIdByName(String name){
		int n;
		Cursor cursor = myDB.rawQuery("SELECT Id FROM Disease where Name='" + name + "'", null);
		cursor.moveToFirst();
		n = cursor.getInt(0);
		cursor.close();
		return n;
	}
	
	public boolean Disease_ExistName(String name){
		Cursor cursor = myDB.rawQuery("SELECT Name FROM Disease where Name='" + name + "'", null);
		if (cursor.getCount() > 0) {
			return true;
		}else{
			return false;
		}
	}
	
	
	//------------------ GLYCEMIA -------------------
	public HashMap<Integer, String[]> Glycemia_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		String[] row;
		HashMap<Integer, String[]> glycemias = new HashMap<Integer, String[]>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				row = new String[3];
				row[0] = cursor.getString(2); //Value
				row[1] = cursor.getString(3); //DateTime
				row[2] = cursor.getString(4); //Id_Tag
				//row[3] = cursor.getString(5); //Id_Note
				glycemias.put(cursor.getInt(0), row);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return glycemias;
		}
		else {
			cursor.close();
			return null;
		}
	}

	public ArrayList<GlycemiaDataBinding> Glycemia_GetByDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose WHERE DateTime > '" 
										+ from + " 00:00:00' AND DateTime < '" 
										+ to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<GlycemiaDataBinding> allreads = new ArrayList<GlycemiaDataBinding>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			GlycemiaDataBinding tmp;
			do{
				tmp = new GlycemiaDataBinding();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				String t = cursor.getString(3);
				tmp.setDate(t.split(" ")[0]);
				tmp.setTime(t.split(" ")[1]);
				tmp.setIdTag(cursor.getInt(4));
				tmp.setIdNote((!cursor.isNull(5)) ? cursor.getInt(5) : -1);
				
				allreads.add(tmp);
				
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return allreads;
		}
		else {
			cursor.close();
			return allreads;
		}
	}

	public GlycemiaDataBinding Glycemia_GetById(int id){
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodGlucose where Id='" + id + "'", null);
		cursor.moveToFirst();
		GlycemiaDataBinding g = new GlycemiaDataBinding();
		
		g.setId(cursor.getInt(0));
		g.setIdUser(cursor.getInt(1));
		g.setValue(cursor.getDouble(2));
		String t = cursor.getString(3);
		g.setDate(t.split(" ")[0]);
		g.setTime(t.split(" ")[1]);
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
			
			do{
				row = new String[4];
				row[0] = cursor.getString(1); //Name
				row[1] = cursor.getString(2); //Type
				row[2] = cursor.getString(3); //Action
				row[3] = String.valueOf(cursor.getDouble(4)); //Duration
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
		}
	}

	public boolean Insulin_HasInsulins(){
		Cursor cursor = myDB.rawQuery("SELECT * FROM Insulin", null);
		if (cursor.getCount() > 0) {
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public HashMap<Integer, String> Insulin_GetAllNames() {
		Cursor cursor = myDB.rawQuery("SELECT Id, Name FROM Insulin", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		HashMap<Integer, String> insulins = new HashMap<Integer, String>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				insulins.put(cursor.getInt(0), cursor.getString(1));
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return insulins;
		}
		else {
			cursor.close();
			return null;
		}
	}

	public InsulinDataBinding Insulin_GetByName(String name){
		InsulinDataBinding insulin = new InsulinDataBinding();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Insulin where Name='" + name + "'", null);
		cursor.moveToFirst();
		insulin.setId(cursor.getInt(0));
		insulin.setName(cursor.getString(1));
		insulin.setType(cursor.getString(2));
		insulin.setAction(cursor.getString(3));
		insulin.setDuration(cursor.getDouble(4));
		
		cursor.close();
		return insulin;
	}

	public ArrayList<InsulinRegDataBinding> InsulinReg_GetByDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT Id, Id_User, Id_BloodGlucose, Id_Insulin, DateTime, Target_BG, Value, Id_Tag, Id_Note FROM Reg_Insulin WHERE DateTime > '" 
										+ from + " 00:00:00' AND DateTime < '" 
										+ to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<InsulinRegDataBinding> allreads = new ArrayList<InsulinRegDataBinding>();
		InsulinRegDataBinding insulin;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				Log.d("le insulin", String.valueOf(cursor.getPosition()));
				insulin = new InsulinRegDataBinding();
				insulin.setId(cursor.getInt(0));
				insulin.setIdUser(cursor.getInt(1));
				insulin.setIdBloodGlucose((!cursor.isNull(2)) ? cursor.getInt(2) : -1);
				insulin.setIdInsulin(cursor.getInt(3));
				String t = cursor.getString(4);
				insulin.setDate(t.split(" ")[0]);
				insulin.setTime(t.split(" ")[1]);
				insulin.setTargetGlycemia(cursor.getDouble(5));
				insulin.setInsulinUnits(cursor.getDouble(6));
				insulin.setIdTag(cursor.getInt(7));
				insulin.setIdNote((!cursor.isNull(8)) ? cursor.getInt(8) : -1);
				allreads.add(insulin);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return allreads;
		}
		else {
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

	public InsulinDataBinding Insulin_GetById(int id){
		InsulinDataBinding insulin = new InsulinDataBinding();
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

	public InsulinRegDataBinding InsulinReg_GetById(int id) {
		//Cursor cursor = myDB.rawQuery("SELECT i.Id, Id_Insulin, i.DateTime, Target_BG, i.Value, g.value, i.Id_Tag, g.Id FROM Reg_Insulin as i LEFT JOIN Reg_BloodGlucose as g ON Id_BloodGlucose=g.Id WHERE i.Id='" + id + "';", null);
		Cursor cursor = myDB.rawQuery("SELECT Id, Id_User, Id_BloodGlucose, Id_Insulin, DateTime, Target_BG, Value, Id_Tag, Id_Note FROM Reg_Insulin WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		//String[] row = new String[7];
		
		InsulinRegDataBinding insulin = new InsulinRegDataBinding();
		insulin.setId(cursor.getInt(0));
		insulin.setIdUser(cursor.getInt(1));
		insulin.setIdBloodGlucose((!cursor.isNull(2)) ? cursor.getInt(2) : -1);
		insulin.setIdInsulin(cursor.getInt(3));
		String t = cursor.getString(4);
		insulin.setDate(t.split(" ")[0]);
		insulin.setTime(t.split(" ")[1]);
		insulin.setTargetGlycemia(cursor.getDouble(5));
		insulin.setInsulinUnits(cursor.getDouble(6));
		insulin.setIdTag(cursor.getInt(7));
		insulin.setIdNote((!cursor.isNull(8)) ? cursor.getInt(8) : -1);
		/*
		row[0] = cursor.getString(1); //id insulin
		row[1] = cursor.getString(2); //DateTime
		row[2] = cursor.getString(3); //target bg
		row[3] = cursor.getString(4); //insulin value
		row[4] = cursor.getString(5); //glycemia value
		row[5] = cursor.getString(6); //id tag
		row[6] = cursor.getString(7); //id Blood Glucose
		 */
		cursor.close();
		return insulin;
		
	}
	
	
	
	//----------------- EXERCISES --------------
	public boolean Exercise_ExistName(String name){
		Cursor cursor = myDB.rawQuery("SELECT Name FROM Exercise where Name='" + name + "'", null);
		if (cursor.getCount() > 0) {
			return true;
		}else{
			return false;
		}
	}
	
	public int Exercise_GetIdByName(String name){
		int n;
		Cursor cursor = myDB.rawQuery("SELECT Id FROM Exercise where Name='" + name + "'", null);
		cursor.moveToFirst();
		n = cursor.getInt(0);
		cursor.close();
		return n;
	}

	public String Exercise_GetNameById(int id){
		String n;
		Cursor cursor = myDB.rawQuery("SELECT Name FROM Exercise where Id='" + id + "'", null);
		cursor.moveToFirst();
		n = cursor.getString(0);
		cursor.close();
		return n;
	}


	public HashMap<Integer, String> Exercise_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Exercise", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		HashMap<Integer, String> exercises = new HashMap<Integer, String>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				exercises.put(cursor.getInt(0), cursor.getString(1));
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return exercises;
		}
		else {
			cursor.close();
			return null;
		}
	}
	
	public ArrayList<ExerciseRegDataBinding> ExerciseReg_GetByDate(String from, String to) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Exercise WHERE StartDateTime > '" 
										+ from + " 00:00:00' AND StartDateTime < '" 
										+ to + " 23:59:59' ORDER BY StartDateTime DESC;", null);
		ArrayList<ExerciseRegDataBinding> exs = new ArrayList<ExerciseRegDataBinding>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			ExerciseRegDataBinding tmp;
			do{
				
				tmp = new ExerciseRegDataBinding();
				tmp.setId(cursor.getInt(0));
				tmp.setId_User(cursor.getInt(1));
				tmp.setExercise(cursor.getString(2));
				tmp.setDuration(cursor.getInt(3));
				tmp.setEffort(cursor.getString(4));
				tmp.setDate(cursor.getString(5).split(" ")[0]);
				tmp.setTime(cursor.getString(5).split(" ")[1]);
				tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
				exs.add(tmp);
				
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return exs;
		}
		else {
			cursor.close();
			return exs;
		}
	}
	
	public ExerciseRegDataBinding ExerciseReg_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT Id, Id_User, Exercise, Duration, Effort, StartDateTime, Id_Note FROM Reg_Exercise WHERE Id='" + id + "';", null);
		ExerciseRegDataBinding ex = new ExerciseRegDataBinding();
		cursor.moveToFirst();
		
		ex.setId(cursor.getInt(0));
		ex.setId_User(cursor.getInt(1));
		ex.setExercise(cursor.getString(2));
		ex.setDuration(cursor.getInt(3));
		ex.setEffort(cursor.getString(4));
		String t = cursor.getString(5);
		ex.setDate(t.split(" ")[0]);
		ex.setTime(t.split(" ")[1]);
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
			
			do{
				row = new String[2];
				row[0] = cursor.getString(1); //Name
				row[1] = cursor.getString(2); //Units
				medicines.put(cursor.getInt(0), row);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return medicines;
		}
		else {
			cursor.close();
			return null;
		}
	}

	
	//-------------- CARBS ------------------
	public ArrayList<CarbsDataBinding> CarboHydrate_GetBtDate(String from, String to){
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_CarboHydrate WHERE  DateTime > '" 
										+ from + " 00:00:00' AND DateTime < '" 
										+ to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<CarbsDataBinding> allreads = new ArrayList<CarbsDataBinding>();
		CarbsDataBinding tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				tmp = new CarbsDataBinding();
				tmp.setId(cursor.getInt(0));
				tmp.setId_User(cursor.getInt(1));
				tmp.setCarbsValue(cursor.getDouble(2));
				tmp.setPhotoPath(cursor.getString(3));
				tmp.setDate(cursor.getString(4).split(" ")[0]);
				tmp.setTime(cursor.getString(4).split(" ")[1]);
				tmp.setId_Tag(cursor.getInt(5));
				
				allreads.add(tmp);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return allreads;
		}
		else {
			cursor.close();
			return allreads;
		}
	}
	
	public CarbsDataBinding CarboHydrate_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_CarboHydrate WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		
		CarbsDataBinding tmp = new CarbsDataBinding();
		tmp.setId(cursor.getInt(0));
		tmp.setId_User(cursor.getInt(1));
		tmp.setCarbsValue(cursor.getDouble(2));
		tmp.setPhotoPath(cursor.getString(3));
		tmp.setDate(cursor.getString(4).split(" ")[0]);
		tmp.setTime(cursor.getString(4).split(" ")[1]);
		tmp.setId_Tag(cursor.getInt(5));
		tmp.setId_Note((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
		cursor.close();
		return tmp;
		
	}
	
	//---------------------- BLOODPRESSURE ---------------
	public ArrayList<BloodPressureDataBinding> BloodPressure_GetBtDate(String from, String to){
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodPressure WHERE  DateTime > '" 
										+ from + " 00:00:00' AND DateTime < '" 
										+ to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<BloodPressureDataBinding> allreads = new ArrayList<BloodPressureDataBinding>();
		BloodPressureDataBinding tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				tmp = new BloodPressureDataBinding();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setSystolic(cursor.getInt(2));
				tmp.setDiastolic(cursor.getInt(3));
				tmp.setDate(cursor.getString(4).split(" ")[0]);
				tmp.setTime(cursor.getString(4).split(" ")[1]);
				tmp.setIdTag(cursor.getInt(5));
				tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return allreads;
		}
		else {
			cursor.close();
			return allreads;
		}
	}
	
	public BloodPressureDataBinding BloodPressure_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_BloodPressure WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		
		BloodPressureDataBinding tmp = new BloodPressureDataBinding();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setSystolic(cursor.getInt(2));
		tmp.setDiastolic(cursor.getInt(3));
		tmp.setDate(cursor.getString(4).split(" ")[0]);
		tmp.setTime(cursor.getString(4).split(" ")[1]);
		tmp.setIdTag(cursor.getInt(5));
		tmp.setIdNote((!cursor.isNull(6)) ? cursor.getInt(6) : -1);
				
		cursor.close();
		return tmp;
		
	}
	
	//----------- CHOLESTEROL ---------------
	public ArrayList<CholesterolDataBinding> Cholesterol_GetBtDate(String from, String to){
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Cholesterol WHERE  DateTime > '" 
										+ from + " 00:00:00' AND DateTime < '" 
										+ to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<CholesterolDataBinding> allreads = new ArrayList<CholesterolDataBinding>();
		CholesterolDataBinding tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				tmp = new CholesterolDataBinding();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDate(cursor.getString(3).split(" ")[0]);
				tmp.setTime(cursor.getString(3).split(" ")[1]);
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return allreads;
		}
		else {
			cursor.close();
			return allreads;
		}
	}
	
	public CholesterolDataBinding Cholesterol_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Cholesterol WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		
		CholesterolDataBinding tmp = new CholesterolDataBinding();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setValue(cursor.getDouble(2));
		tmp.setDate(cursor.getString(3).split(" ")[0]);
		tmp.setTime(cursor.getString(3).split(" ")[1]);
		tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				
		cursor.close();
		return tmp;
		
	}
	
	
	//--------------- WEIGHT -----------------
	public ArrayList<WeightDataBinding> Weight_GetBtDate(String from, String to){
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Weight WHERE  DateTime > '" 
										+ from + " 00:00:00' AND DateTime < '" 
										+ to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<WeightDataBinding> allreads = new ArrayList<WeightDataBinding>();
		WeightDataBinding tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				tmp = new WeightDataBinding();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDate(cursor.getString(3).split(" ")[0]);
				tmp.setTime(cursor.getString(3).split(" ")[1]);
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return allreads;
		}
		else {
			cursor.close();
			return allreads;
		}
	}
	
	public WeightDataBinding Weight_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Weight WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		
		WeightDataBinding tmp = new WeightDataBinding();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setValue(cursor.getDouble(2));
		tmp.setDate(cursor.getString(3).split(" ")[0]);
		tmp.setTime(cursor.getString(3).split(" ")[1]);
		tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				
		cursor.close();
		return tmp;
		
	}
	
	
	//------------------- HbA1c
	public ArrayList<HbA1cDataBinding> HbA1c_GetBtDate(String from, String to){
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_A1c WHERE  DateTime > '" 
										+ from + " 00:00:00' AND DateTime < '" 
										+ to + " 23:59:59' ORDER BY DateTime DESC;", null);
		ArrayList<HbA1cDataBinding> allreads = new ArrayList<HbA1cDataBinding>();
		HbA1cDataBinding tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				tmp = new HbA1cDataBinding();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setValue(cursor.getDouble(2));
				tmp.setDate(cursor.getString(3).split(" ")[0]);
				tmp.setTime(cursor.getString(3).split(" ")[1]);
				tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return allreads;
		}
		else {
			cursor.close();
			return allreads;
		}
	}
	
	public HbA1cDataBinding HbA1c_GetById(int id) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_A1c WHERE Id='" + id + "';", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		cursor.moveToFirst();
		
		HbA1cDataBinding tmp = new HbA1cDataBinding();
		tmp.setId(cursor.getInt(0));
		tmp.setIdUser(cursor.getInt(1));
		tmp.setValue(cursor.getDouble(2));
		tmp.setDate(cursor.getString(3).split(" ")[0]);
		tmp.setTime(cursor.getString(3).split(" ")[1]);
		tmp.setIdNote((!cursor.isNull(4)) ? cursor.getInt(4) : -1);
				
		cursor.close();
		return tmp;
		
	}

	
	//----------------------- DISEASE REG
	public ArrayList<DiseaseRegDataBinding> DiseaseReg_GetByDate(String from, String to){
		Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Disease WHERE  StartDate > '" 
										+ from + " 00:00:00' AND StartDate < '" 
										+ to + " 23:59:59' ORDER BY StartDate DESC;", null);
		ArrayList<DiseaseRegDataBinding> allreads = new ArrayList<DiseaseRegDataBinding>();
		DiseaseRegDataBinding tmp;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				tmp = new DiseaseRegDataBinding();
				tmp.setId(cursor.getInt(0));
				tmp.setIdUser(cursor.getInt(1));
				tmp.setDisease(cursor.getString(2));
				tmp.setStartDate(cursor.getString(3));
				tmp.setEndDate((!cursor.isNull(4)) ? cursor.getString(4) : null);
				tmp.setIdNote((!cursor.isNull(5)) ? cursor.getInt(5) : -1);
				allreads.add(tmp);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return allreads;
		}
		else {
			cursor.close();
			return allreads;
		}
	}
	
	public DiseaseRegDataBinding DiseaseReg_GetById(int id) {
			Cursor cursor = myDB.rawQuery("SELECT * FROM Reg_Disease WHERE Id='" + id + "';", null);
			Log.d("Cursor", String.valueOf(cursor.getCount()));
			cursor.moveToFirst();
			
			DiseaseRegDataBinding tmp = new DiseaseRegDataBinding();
			tmp.setId(cursor.getInt(0));
			tmp.setIdUser(cursor.getInt(1));
			tmp.setDisease(cursor.getString(2));
			tmp.setStartDate(cursor.getString(3));
			tmp.setEndDate((!cursor.isNull(4)) ? cursor.getString(4) : null);
			tmp.setIdNote((!cursor.isNull(5)) ? cursor.getInt(5) : -1);
					
			cursor.close();
			return tmp;
			
		}
	
	
	//-------------- TARGETS GLYCEMIA -----------------
	public double Target_GetTargetByTime(String time){
		Cursor cursor = myDB.rawQuery("SELECT * FROM BG_Target WHERE  "
										+ "(TimeStart < TimeEnd AND '" + time + "' >= TimeStart AND '" + time + "' <= TimeEnd)" +
										"OR "
										+ "(TimeStart > TimeEnd AND('" + time + "' >= TimeStart OR '" + time + "' <= TimeEnd ))"
										+ ";", null);
		double d = 0;
		Log.d("Cursor targets", String.valueOf(cursor.getCount()));
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			d = cursor.getDouble(4);
		}
		cursor.close();
		
		return d;
	}

	public ArrayList<TargetDataBinding> Target_GetAll() {
		Cursor cursor = myDB.rawQuery("SELECT * FROM BG_Target", null);
		Log.d("Cursor", String.valueOf(cursor.getCount()));
		ArrayList<TargetDataBinding> targets = new ArrayList<TargetDataBinding>();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			TargetDataBinding t;
			do{
				t = new TargetDataBinding();
				t.setId(cursor.getInt(0));
				t.setName(cursor.getString(1));
				t.setStart(cursor.getString(2));
				t.setEnd(cursor.getString(3));
				t.setTarget(cursor.getDouble(4));
				targets.add(t);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			return targets;
		}
		else {
			cursor.close();
			return targets;
		}
	}
	
	
	public TargetDataBinding Target_GetById(int id){
		TargetDataBinding target = new TargetDataBinding();
		Cursor cursor = myDB.rawQuery("SELECT * FROM BG_Target where Id='" + id + "'", null);
		cursor.moveToFirst();
		target.setId(cursor.getInt(0));
		target.setName(cursor.getString(1));
		target.setStart(cursor.getString(2));
		target.setEnd(cursor.getString(3));
		target.setTarget(cursor.getDouble(4));
		cursor.close();
		return target;
	}
	
	
	//--------------- NOTES -------------------
	public NoteDataBinding Note_GetById(int id){
		NoteDataBinding n = new NoteDataBinding();
		Cursor cursor = myDB.rawQuery("SELECT * FROM Note WHERE Id='" + id + "';", null);
		cursor.moveToFirst();
		n.setId(cursor.getInt(0));
		n.setNote(cursor.getString(1));
		cursor.close();
		return n;
	}




	/*
	 * LOGBOOK by zeornelas
	 */
	
	public ArrayList<LogbookDataBinding> getLogbook(String from, String to) {
		ArrayList<LogbookDataBinding> lb = new ArrayList<LogbookDataBinding>();
		LogbookDataBinding row;
		CarbsDataBinding ch;
		InsulinRegDataBinding ins;
		GlycemiaDataBinding bg;
		
		Cursor cursor = myDB.rawQuery("SELECT ins.datetime, ins.Id_user, ins.Id_Tag, ins.Id_Note, ch.Id, ch.value, ch.PhotoPath, ins.Id, ins.Id_Insulin, ins.Id_BloodGlucose,ins.Target_BG,ins.Value, bg.Value" + 
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
		
		//Log.d("LOGBOOK", String.valueOf(cursor.getCount()));
		int a = cursor.getCount();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			do{
				row = new LogbookDataBinding();
				ch = new CarbsDataBinding();
				ins = new InsulinRegDataBinding();
				bg = new GlycemiaDataBinding();
				
				String t = cursor.getString(0);

				if(!cursor.isNull(4) && !cursor.isNull(7) && !cursor.isNull(9)){ //refeicao completa
					ch.setDate(t.split(" ")[0]);
					ch.setTime(t.split(" ")[1]);
					ins.setDate(t.split(" ")[0]);
					ins.setTime(t.split(" ")[1]);
					bg.setDate(t.split(" ")[0]);
					bg.setTime(t.split(" ")[1]);
					
					ch.setId_User(cursor.getInt(1));
					ins.setIdUser(cursor.getInt(1));
					bg.setIdUser(cursor.getInt(1));
				
					ch.setId_Tag(cursor.getInt(2));
					ins.setIdTag(cursor.getInt(2));
					bg.setIdTag(cursor.getInt(2));
				
					ch.setId_Note((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					ins.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					bg.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);

					ch.setId(cursor.getInt(4));
					ch.setCarbsValue(cursor.getDouble(5));
					ch.setPhotoPath(cursor.getString(6));
					
					ins.setId(cursor.getInt(7));
					ins.setIdInsulin(cursor.getInt(8));
					ins.setIdBloodGlucose(cursor.getInt(9));
					ins.setTargetGlycemia(cursor.getDouble(10));
					ins.setInsulinUnits(cursor.getDouble(11));
					
					bg.setId(cursor.getInt(9));
					bg.setValue(cursor.getDouble(12));
					
					row.set_bg(bg);
					row.set_ch(ch);
					row.set_ins(ins);
				}else if(!cursor.isNull(4) && cursor.isNull(7) && cursor.isNull(9)){ //so hidratos carbono
					ch.setDate(t.split(" ")[0]);
					ch.setTime(t.split(" ")[1]);
					ch.setId_User(cursor.getInt(1));
					ch.setId_Tag(cursor.getInt(2));
					ch.setId_Note((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					ch.setId(cursor.getInt(4));
					ch.setCarbsValue(cursor.getDouble(5));
					ch.setPhotoPath(cursor.getString(6));
					
					row.set_bg(null);
					row.set_ch(ch);
					row.set_ins(null);
				}else if(cursor.isNull(4) && !cursor.isNull(7) && !cursor.isNull(9)){ //insulina com parametro da glicemia
					ins.setDate(t.split(" ")[0]);
					ins.setTime(t.split(" ")[1]);
					bg.setDate(t.split(" ")[0]);
					bg.setTime(t.split(" ")[1]);

					ins.setIdUser(cursor.getInt(1));
					bg.setIdUser(cursor.getInt(1));

					ins.setIdTag(cursor.getInt(2));
					bg.setIdTag(cursor.getInt(2));

					ins.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					bg.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);

					ins.setId(cursor.getInt(7));
					ins.setIdInsulin(cursor.getInt(8));
					ins.setIdBloodGlucose(cursor.getInt(9));
					ins.setTargetGlycemia(cursor.getDouble(10));
					ins.setInsulinUnits(cursor.getDouble(11));
					
					bg.setId(cursor.getInt(9));
					bg.setValue(cursor.getDouble(12));
					
					row.set_bg(bg);
					row.set_ch(null);
					row.set_ins(ins);
				}else if(cursor.isNull(4) && cursor.isNull(7) && !cursor.isNull(9)){ //so glicemia
					bg.setDate(t.split(" ")[0]);
					bg.setTime(t.split(" ")[1]);
					bg.setIdUser(cursor.getInt(1));
					bg.setIdTag(cursor.getInt(2));
					bg.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					bg.setId(cursor.getInt(9));
					bg.setValue(cursor.getDouble(12));
					
					row.set_bg(bg);
					row.set_ch(null);
					row.set_ins(null);
				}else if(cursor.isNull(4) && !cursor.isNull(7) && cursor.isNull(9)){ //so insulina
					ins.setDate(t.split(" ")[0]);
					ins.setTime(t.split(" ")[1]);
					ins.setIdUser(cursor.getInt(1));
					ins.setIdTag(cursor.getInt(2));
					ins.setIdNote((!cursor.isNull(3)) ? cursor.getInt(3) : -1);
					ins.setId(cursor.getInt(7));
					ins.setIdInsulin(cursor.getInt(8));
					ins.setIdBloodGlucose(cursor.getInt(9));
					ins.setTargetGlycemia(cursor.getDouble(10));
					ins.setInsulinUnits(cursor.getDouble(11));
					
					row.set_bg(null);
					row.set_ch(null);
					row.set_ins(ins);
				}
				
				
				lb.add(row);
				cursor.moveToNext();
			}
			while(!cursor.isAfterLast());
			cursor.close();
			//Log.d("LOGBOOK", String.valueOf(lb.size()));
			return lb;
		}
		else {
			cursor.close();
			//Log.d("LOGBOOK", String.valueOf(lb.size()));
			return lb;
		}
		
	}	
}

