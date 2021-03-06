package pt.it.porto.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.CarbsRatioData;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.Disease;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.Exercise;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.HbA1cRec;
import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.InsulinTarget;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.data.Sensitivity;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.data.TargetBGRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class DB_Write {

	final Context myContext;
	final SQLiteDatabase myDB;

	public DB_Write(Context context) {
		super();
		// TODO Auto-generated constructor stub
		DB_Handler db = new DB_Handler(context);
		this.myContext = context;
		this.myDB = db.getWritableDatabase();
		this.myDB.execSQL("PRAGMA foreign_keys=ON;");
	}

	public DB_Write(SQLiteDatabase newDb) {
		super();
		// TODO Auto-generated constructor stub
		//DB_Handler db = new DB_Handler(context);
		//this.myContext = context;
		myContext = null;
		this.myDB = newDb;//db.getWritableDatabase();
		//this.myDB.execSQL("PRAGMA foreign_keys=ON;");
	}

	public void close() {
		myDB.close();
//		Log.d("Close", "DB_Write");
	}

	public void MyData_Save(UserInfo obj) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM UserInfo WHERE Id=" + obj.getId(), null);
		ContentValues contentValues =obj.getContentValues(this.myContext);
		//System.out.println("CONTENTVALUES: "+contentValues);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			myDB.update("UserInfo", contentValues, "Id=" + Integer.parseInt(cursor.getString(0)), null);
		} else {
			myDB.insert("UserInfo", null, contentValues);
		}
		//Log.d("Guardou", "Preferencias");
	}
	public void addFeature(ContentValues contentValues){

		myDB.insert("Feature", null, contentValues);

	}

	//--------------- TAG ----------------------
	public void Tag_Add(String tag) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", tag);
		myDB.insert("Tag", null, toInsert);
	}


	public void database_last_upload_update(String date){
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("DateTimeUpdate", date);

		myDB.update("UserInfo", toUpdate, null, null);

		Log.d("Update", "UserInfo");
	}

	public void Tag_Add(Tag t) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", t.getName());
		toInsert.put("TimeStart", t.getStart());
		toInsert.put("TimeEnd", t.getEnd());
		myDB.insert("Tag", null, toInsert);
	}

	public void Tag_Update(Tag t) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Name", t.getName());
		toUpdate.put("TimeStart", t.getStart());
		toUpdate.put("TimeEnd", t.getEnd());

		myDB.update("Tag", toUpdate, "Id=" + t.getId(), null);

		Log.d("Update", "Tag");
	}


	public void Tag_Remove(int id) {
		myDB.delete("Tag", "id=" + id, null);
	}


	//-------------- DISEASE ----------------
	public void Disease_Add(String disease) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", disease);
		myDB.insert("Disease", null, toInsert);
	}

	public void Disease_Remove(int id) {
		myDB.delete("Disease", "id=" + id, null);

	}

	public void Disease_Update(Disease i) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Name", i.getName());

		myDB.update("Disease", toUpdate, "Id=" + i.getId(), null);
		Log.d("Update", "Disease");
	}

	//--------------- GLYCEMIA -------------------
	public int Glycemia_Save(GlycemiaRec obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getIdUser());
		toInsert.put("Value", obj.getValue());
		String datetime = DateUtils.formatToDb(obj.getDateTime());
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", obj.getIdTag());
		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}
		if(obj.getBG_target()!=-1){
			toInsert.put("Target_BG", obj.getBG_target());
		}

		Log.d("Guardou", "Reg_BloodGlucose");
		return (int) myDB.insert("Reg_BloodGlucose", null, toInsert);
	}

	public void Glycemia_Update(GlycemiaRec obj) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Id_User", obj.getIdUser());
		toUpdate.put("Value", obj.getValue());
		String datetime = DateUtils.formatToDb(obj.getDateTime());
		toUpdate.put("DateTime", datetime);
		toUpdate.put("Id_Tag", obj.getIdTag());
		if (obj.getIdNote() > 0) {
			toUpdate.put("Id_Note", obj.getIdNote());
		}
        if(obj.getBG_target()!=-1){
            toUpdate.put("Target_BG", obj.getBG_target());
        }
		Log.i("cwnA", "Glycemia_Update: "+toUpdate.toString());
		myDB.update("Reg_BloodGlucose", toUpdate, "Id=" + obj.getId(), null);

		Log.d("Update", "Reg_BloodGlucose");
	}

	public void Glycemia_Delete(int id) {
		DB_Read rdb = new DB_Read(myContext);
//		int idNote = rdb.Glycemia_GetById(id).getIdNote();
		rdb.close();
		myDB.delete("Reg_BloodGlucose", "Id=" + id, null);
		//Log.d("Delete", "Reg_BloodGlucose");
		//Note_Delete(idNote);
	}


	public void TargetBG_Add(TargetBGRec i) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", i.getName());
		toInsert.put("TimeStart", i.getTimeStart());
		toInsert.put("TimeEnd", i.getTimeEnd());
		toInsert.put("Value", i.getValue());
		myDB.insert("BG_Target", null, toInsert);

		Log.d("Guardou", "BG_Target");
	}


	public void Insulin_Add(Insulin i) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", i.getName());
		toInsert.put("Type", i.getType());
		toInsert.put("Action", i.getAction());
		toInsert.put("Duration", i.getDuration());

		myDB.insert("Insulin", null, toInsert);

		Log.d("Guardou", "Insulin");
	}

	public void Insulin_Update(Insulin i) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Name", i.getName());
		toUpdate.put("Type", i.getType());
		toUpdate.put("Action", i.getAction());
		toUpdate.put("Duration", i.getDuration());

		myDB.update("Insulin", toUpdate, "Id=" + i.getId(), null);
		Log.d("Update", "Insulin");
	}

	public void Insulin_Remove(int id) {
		myDB.delete("Insulin", "id=" + id, null);
	}

	//------------------ INSULIN REG ----------------------

	public int Insulin_Save(InsulinRec obj) {
		ContentValues toInsert = new ContentValues();
		//toInsert.put("Id_User", obj.getIdUser());
		toInsert.put("Id_User", 1);
		toInsert.put("Id_Insulin", 1);
		String datetime = DateUtils.formatToDb(Calendar.getInstance());
		toInsert.put("DateTime", datetime);
		toInsert.put("Value", obj.getInsulinUnits());
		toInsert.put("Id_Tag", 1);

		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}
		if(obj.getTargetGlycemia()>0){
			toInsert.put("Target_BG", obj.getTargetGlycemia());
		}
		if (obj.getIdBloodGlucose() > 0) {
			toInsert.put("Id_BloodGlucose", obj.getIdBloodGlucose());
		}




		return (int) myDB.insert("Reg_Insulin", null, toInsert);
	}

	public void Insulin_Update(InsulinRec obj) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Id_User", obj.getIdUser());
		toUpdate.put("Id_Insulin", obj.getIdInsulin());
		if (obj.getIdBloodGlucose() > 0) {
			toUpdate.put("Id_BloodGlucose", obj.getIdBloodGlucose());
		} else {
			toUpdate.putNull("Id_BloodGlucose");
		}
		String datetime = DateUtils.formatToDb(obj.getDateTime());
		toUpdate.put("DateTime", datetime);
		toUpdate.put("Target_BG", obj.getTargetGlycemia());
		toUpdate.put("Value", obj.getInsulinUnits());
		toUpdate.put("Id_Tag", obj.getIdTag());
		if (obj.getIdNote() > 0) {
			toUpdate.put("Id_Note", obj.getIdNote());
		}

		myDB.update("Reg_Insulin", toUpdate, "Id=" + obj.getId(), null);

		Log.d("Reg_Insulin", "actualizado");
		//return (int) myDB.insert("Reg_Insulin", null, toInsert);
	}

	public void Insulin_Delete(int id) {
		//DB_Read rdb = new DB_Read(myContext);
		//int idNote = rdb.InsulinReg_GetById(id).getIdNote();
		//int idGlycemia = rdb.InsulinReg_GetById(id).getIdBloodGlucose();
		//rdb.close();
		myDB.delete("Reg_Insulin", "Id=" + id, null);
//		if (idGlycemia > 0) {
//			Glycemia_Delete(idGlycemia);
//		}
		//Note_Delete(idNote);
	}

	//------------------- EXERCISE ---------------------------
	public void Exercise_Add(String exercise) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", exercise);
		myDB.insert("Exercise", null, toInsert);
	}

	public void Exercise_Remove(int id) {
		myDB.delete("Exercise", "id=" + id, null);

	}

	public void Exercise_Update(Exercise i) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Name", i.getName());

		myDB.update("Exercise", toUpdate, "Id=" + i.getId(), null);
		Log.d("Update", "Disease");
	}

	//------------------- EXERCISE REG ---------------------------

	public int Exercise_Save(ExerciseRec obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getIdUser());
		toInsert.put("Exercise", obj.getExercise());
		toInsert.put("Duration", obj.getDuration());
		toInsert.put("Effort", obj.getEffort());
		String datetime = DateUtils.formatToDb(obj.getDateTime());
		toInsert.put("StartDateTime", datetime);
		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}

		return (int) myDB.insert("Reg_Exercise", null, toInsert);
	}

	public void Exercise_Delete(int id) {
		DB_Read rdb = new DB_Read(myContext);
		ExerciseRec tmp = rdb.ExerciseReg_GetById(id);
		if (tmp != null) {
			int idNote = tmp.getIdNote();
			Note_Delete(idNote);
		}
		rdb.close();
		myDB.delete("Reg_Exercise", "Id=?", new String[]{String.valueOf(id)});
	}

	public void Exercise_Update(ExerciseRec obj) {

		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getIdUser());
		toInsert.put("Exercise", obj.getExercise());
		toInsert.put("Duration", obj.getDuration());
		toInsert.put("Effort", obj.getEffort());
		String datetime = DateUtils.formatToDb(obj.getDateTime());
		toInsert.put("StartDateTime", datetime);
		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}

		myDB.update("Reg_Exercise", toInsert, "Id=" + obj.getId(), null);
	}

	//------------------- MEDICINE -------------------------------
	@Deprecated
	public void Medicine_Add(Object[] obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", obj[0].toString());
		toInsert.put("Units", obj[1].toString());

		myDB.insert("Medicine", null, toInsert);

		Log.d("Guardou", "Medicine");
	}

	@Deprecated
	public void Medicine_Remove(int id) {
		myDB.delete("Medicine", "id=" + id, null);
	}

	//----------------- CARBS ------------------------------

	/**
	 * Saves a carb regist
	 *
	 * @param obj
	 * @return the entry ID
	 */
	public int Carbs_Save(CarbsRec obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getIdUser());
		toInsert.put("Value", obj.getCarbsValue());
		toInsert.put("PhotoPath", obj.getPhotoPath());


		if (obj.getPhotoPath() != null) {
			PhotoSyncDb photoSyncDb = new PhotoSyncDb(MyDiabetesStorage.getInstance(myContext));
			photoSyncDb.addPhoto(obj.getPhotoPath());
		}
		if(obj.getMealType()!=-1){
			toInsert.put("mealType", obj.getMealType());
		}
		String datetime = DateUtils.formatToDb(obj.getDateTime());
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", obj.getIdTag());
		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}
		toInsert.put("Id_Meal", obj.getMealId());
		return (int) myDB.insert("Reg_CarboHydrate", null, toInsert);
	}

	public void Carbs_Update(CarbsRec obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getIdUser());
		toInsert.put("Value", obj.getCarbsValue());
		toInsert.put("PhotoPath", obj.getPhotoPath());
		//Log.i("update", "Carbs_Update: "+obj.getUserId());

		if(obj.getMealType()!=-1){
			toInsert.put("mealType", obj.getMealType());
		}
		DB_Read read = new DB_Read(myContext);
		CarbsRec old = read.CarboHydrate_GetById(obj.getId());

		PhotoSyncDb photoSyncDb = new PhotoSyncDb(MyDiabetesStorage.getInstance(myContext));
		if (obj.getPhotoPath() != null && old.getPhotoPath() == null) { // if was added
			photoSyncDb.addPhoto(obj.getPhotoPath());
		} else if (old.getPhotoPath() != null && obj.getPhotoPath() == null) { // if was deleted
			photoSyncDb.removePhoto(obj.getId());
		} else if (old.getPhotoPath() != null && !obj.getPhotoPath().equals(old.getPhotoPath())) { // if photo changed
			photoSyncDb.removePhoto(obj.getId());
			photoSyncDb.addPhoto(obj.getPhotoPath());
		}

		String datetime = DateUtils.formatToDb(obj.getDateTime());
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", obj.getIdTag());
		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}
		toInsert.put("Id_Meal", obj.getMealId());

		Log.i("update", "Carbs_Update_obj: "+ obj.toString());
		Log.i("update", "Carbs_Update_old: "+ old.toString());
		Log.i("update", "Carbs_Update: to insert"+ toInsert.toString());

		myDB.update("Reg_CarboHydrate", toInsert, "Id=" + obj.getId(), null);
		Log.d("Reg_CarboHydrate", "actualizado");
	}

	public void Carbs_Delete(int id) {
		PhotoSyncDb photoSyncDb = new PhotoSyncDb(MyDiabetesStorage.getInstance(myContext));
		photoSyncDb.removePhoto(id);

		myDB.delete("Reg_CarboHydrate", "Id=" + id, null);
		Log.d("Delete", "Reg_CarboHydrate");
	}


	public void Carbs_DeletePhoto(int id) {
		PhotoSyncDb photoSyncDb = new PhotoSyncDb(MyDiabetesStorage.getInstance(myContext));
		photoSyncDb.removePhoto(id);

		ContentValues toUpdate = new ContentValues();
		toUpdate.put("PhotoPath", "");

		myDB.update("Reg_CarboHydrate", toUpdate, "Id=" + id, null);
	}


	//-------------- BLOODPRESSURE --------------

	public void BloodPressure_Save(BloodPressureRec bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Systolic", bp.getSystolic());
		toInsert.put("Diastolic", bp.getDiastolic());
		String datetime = DateUtils.formatToDb(bp.getDateTime());
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", bp.getIdTag());
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.insert("Reg_BloodPressure", null, toInsert);
	}

	public void BloodPressure_Update(BloodPressureRec bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Systolic", bp.getSystolic());
		toInsert.put("Diastolic", bp.getDiastolic());
		String datetime = DateUtils.formatToDb(bp.getDateTime());
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", bp.getIdTag());
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.update("Reg_BloodPressure", toInsert, "Id=" + bp.getId(), null);
	}

	public void BloodPressure_Delete(int id) {
		DB_Read rdb = new DB_Read(myContext);
		int idNote = rdb.BloodPressure_GetById(id).getIdNote();
		rdb.close();
		myDB.delete("Reg_BloodPressure", "Id=" + id, null);
		Note_Delete(idNote);
	}

	//------------------- NOTES -------------------
	public int Note_Add(Note note) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Note", note.getNote());

		return (int) myDB.insert("Note", null, toInsert);
	}

	public void Note_Update(Note note) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Note", note.getNote());

		myDB.update("Note", toInsert, "Id=" + note.getId(), null);
	}

	public void Record_Delete(int recordID) {

		myDB.delete("Record", "id=" + recordID, null);
	}

	public void Note_Delete(int id) {
		Log.i("BD", "Note_Delete: id:"+id);
		myDB.delete("Note", "Id=" + id, null);
	}

	//--------------- CHOLESTEROL ------------
	public void Cholesterol_Save(CholesterolRec bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = DateUtils.formatToDb(bp.getDateTime());
		toInsert.put("DateTime", datetime);
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.insert("Reg_Cholesterol", null, toInsert);
	}

	public void Cholesterol_Update(CholesterolRec bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = DateUtils.formatToDb(bp.getDateTime());
		toInsert.put("DateTime", datetime);
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.update("Reg_Cholesterol", toInsert, "Id=" + bp.getId(), null);
	}

	public void Cholesterol_Delete(int id) {
		DB_Read rdb = new DB_Read(myContext);
		int idNote = rdb.Cholesterol_GetById(id).getIdNote();
		rdb.close();
		myDB.delete("Reg_Cholesterol", "Id=" + id, null);
		Note_Delete(idNote);
	}

	//--------------- WEIGHT ------------
	public void Weight_Save(WeightRec bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = DateUtils.formatToDb(bp.getDateTime());
		toInsert.put("DateTime", datetime);
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.insert("Reg_Weight", null, toInsert);
	}

	public void Weight_Update(WeightRec bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = DateUtils.formatToDb(bp.getDateTime());
		toInsert.put("DateTime", datetime);
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.update("Reg_Weight", toInsert, "Id=" + bp.getId(), null);
	}

	public void Weight_Delete(int id) {
		DB_Read rdb = new DB_Read(myContext);
		int idNote = rdb.Weight_GetById(id).getIdNote();
		rdb.close();
		myDB.delete("Reg_Weight", "Id=" + id, null);
		Note_Delete(idNote);
	}

	//--------------- REG DISEASE------------
	public void DiseaseReg_Save(DiseaseRec disease) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", disease.getIdUser());
		toInsert.put("Disease", disease.getDisease());
		String datetime = DateUtils.formatToDb(disease.getDateTime());
		toInsert.put("StartDate", datetime);
		if (disease.getEndDate() != null) {
			toInsert.put("EndDate", disease.getEndDate());
		}
		if (disease.getIdNote() > 0) {
			toInsert.put("Id_Note", disease.getIdNote());
		}

		myDB.insert("Reg_Disease", null, toInsert);
	}

	public void DiseaseReg_Update(DiseaseRec disease) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", disease.getIdUser());
		toInsert.put("Disease", disease.getDisease());
		String datetime = DateUtils.formatToDb(disease.getDateTime());
		toInsert.put("StartDate", datetime);
		if (disease.getEndDate() != null) {
			toInsert.put("EndDate", disease.getEndDate());
		}
		if (disease.getIdNote() > 0) {
			toInsert.put("Id_Note", disease.getIdNote());
		}

		myDB.update("Reg_Disease", toInsert, "Id=" + disease.getId(), null);
	}

	public void DiseaseReg_Delete(int id) {
		DB_Read rdb = new DB_Read(myContext);
		int idNote = rdb.DiseaseReg_GetById(id).getIdNote();
		rdb.close();
		myDB.delete("Reg_Disease", "Id=" + id, null);
		Note_Delete(idNote);
	}

	//--------------- HBA1C ------------
	public void HbA1c_Save(HbA1cRec bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = DateUtils.formatToDb(bp.getDateTime());
		toInsert.put("DateTime", datetime);
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.insert("Reg_A1c", null, toInsert);
	}

	public void HbA1c_Update(HbA1cRec bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = DateUtils.formatToDb(bp.getDateTime());
		toInsert.put("DateTime", datetime);
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.update("Reg_A1c", toInsert, "Id=" + bp.getId(), null);
	}

	public void HbA1c_Delete(int id) {
		DB_Read rdb = new DB_Read(myContext);
		int idNote = rdb.HbA1c_GetById(id).getIdNote();
		rdb.close();
		myDB.delete("Reg_A1c", "Id=" + id, null);
		Note_Delete(idNote);
	}

	//------------- TARGET BG --------------
	public void Target_Remove(int id) {
		myDB.delete("BG_Target", "id=" + id, null);
	}

	public void Target_Add(InsulinTarget t) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", t.getName());
		toInsert.put("TimeStart", t.getStart());
		toInsert.put("TimeEnd", t.getEnd());
		toInsert.put("Value", t.getTarget());
		myDB.insert("BG_Target", null, toInsert);
	}

	public void Target_Update(InsulinTarget t) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Name", t.getName());
		toUpdate.put("TimeStart", t.getStart());
		toUpdate.put("TimeEnd", t.getEnd());
		toUpdate.put("Value", t.getTarget());

		myDB.update("BG_Target", toUpdate, "Id=" + t.getId(), null);

		Log.d("Update", "Tag");
	}


	//added zeornelas
	//------------- Logbook --------------
	public void Logbook_Delete(int ch_id, int ins_id, int bg_id, int note_id) {
		if (ch_id != -1) {
			Carbs_Delete(ch_id);
		}
		if (ins_id != -1) {
			Insulin_Delete(ins_id);
		}
		if (bg_id != -1 && ins_id == -1) {
			Glycemia_Delete(bg_id);
		}
		if (note_id != -1) {
			Note_Delete(note_id);
		}
	}

	public void Logbook_DeleteOnSave(int ch_id, int ins_id, int bg_id, int insToUpdate) {
		if (ch_id != -1) {
			myDB.delete("Reg_CarboHydrate", "Id=" + ch_id, null);
			Log.d("Delete", "Reg_CarboHydrate");
		}
		if (ins_id != -1) {
			myDB.delete("Reg_Insulin", "Id=" + ins_id, null);
			Log.d("Delete", "Reg_Insulin");
		}
		if (insToUpdate != -1 && bg_id != -1) {
			DB_Read rdb = new DB_Read(myContext);
			InsulinRec ins = rdb.InsulinReg_GetById(insToUpdate);

			ins.setIdBloodGlucose(-1);
			Insulin_Update(ins);

			ins = rdb.InsulinReg_GetById(insToUpdate);
			Log.d("DEPOIS UPDATE", ins.getIdBloodGlucose() + "");

			rdb.close();
			myDB.delete("Reg_BloodGlucose", "Id=" + bg_id, null);
			Log.d("Delete", "Reg_BloodGlucose");
		} else if (bg_id != -1) {
			myDB.delete("Reg_BloodGlucose", "Id=" + bg_id, null);
			Log.d("Delete", "Reg_BloodGlucose");
		}
	}


	public void Log_Save(int id_user, String activity) {
		ContentValues toInsert = new ContentValues();
		String datetime = DateUtils.formatToDb(Calendar.getInstance());
		toInsert.put("Id_User", id_user);
		toInsert.put("DateTime", datetime);
		toInsert.put("Activity", activity);
		System.out.println(activity);
		myDB.insert("Activity_Log", null, toInsert);
	}

	public void Clicks_Save(int id_user, String activity, float xvalue, float yvalue) {
		ContentValues toInsert = new ContentValues();
		String datetime = DateUtils.formatToDb(Calendar.getInstance());
		toInsert.put("Id_User", id_user);
		toInsert.put("DateTime", datetime);
		toInsert.put("Activity", activity);
		toInsert.put("X_Value", xvalue);
		toInsert.put("Y_Value", yvalue);
		myDB.insert("Clicks_Log", null, toInsert);
	}

















	//------------- TARGET Sensitivity --------------
	public void Sensitivity_Reg_Remove(int id) {
		myDB.delete("Sensitivity_Reg", "id=" + id, null);

	}

	public void Sensitivity_Reg_Add(Sensitivity t) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", t.getUser_id());
		toInsert.put("Name", t.getName());
		toInsert.put("TimeStart", t.getStart());
		toInsert.put("TimeEnd", t.getEnd());
		toInsert.put("Value", t.getSensitivity());
		myDB.insert("Sensitivity_Reg", null, toInsert);
	}

	public void Sensitivity_Reg_Update(Sensitivity t) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Name", t.getName());
		toUpdate.put("TimeStart", t.getStart());
		toUpdate.put("TimeEnd", t.getEnd());
		toUpdate.put("Value", t.getSensitivity());
		int affect = myDB.update("Sensitivity_Reg", toUpdate, "Id=" + t.getId(), null);
		Log.i("cenas", "Sensitivity_Reg_Update:->->->->-> "+affect);
	}

	//------------- TARGET Ratio --------------
	public void Ratio_Reg_Remove(int id) {
		myDB.delete("Ratio_Reg", "id=" + id, null);

	}

	public void Ratio_Reg_Add(CarbsRatioData t) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", t.getName());
		toInsert.put("Id_User", t.getUser_id());
		toInsert.put("TimeStart", t.getStart());
		toInsert.put("TimeEnd", t.getEnd());
		toInsert.put("Value", t.getValue());
		myDB.insert("Ratio_Reg", null, toInsert);
	}

	public void Ratio_Reg_Update(CarbsRatioData t) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Name", t.getName());
		toUpdate.put("TimeStart", t.getStart());
		toUpdate.put("TimeEnd", t.getEnd());
		toUpdate.put("Value", t.getValue());
		myDB.update("Ratio_Reg", toUpdate, "Id=" + t.getId(), null);
	}
//Record(Id INTEGER PRIMARY KEY AUTOINCREMENT,
// Id_User INTEGER NOT NULL,
// DateTime DATETIME NOT NULL,
// Id_Tag INTEGER,
// Id_Carbs INTEGER,
// Id_Insulin INTEGER,
// Id_BloodGlucose INTEGER,
// Id_Note INTEGER);

	public int Record_Add(int user_id, Calendar datetimeC, int tag_id, int id_carbs, int id_glic, int id_insu) {
		String datetime = DateUtils.formatToDb(datetimeC);
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", user_id);
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", tag_id);
		toInsert.put("Id_Carbs", id_carbs);
		toInsert.put("Id_Insulin", id_insu);
		toInsert.put("Id_BloodGlucose", id_glic);
		toInsert.put("Id_Note", -1);
		return (int) myDB.insert("Record", null, toInsert);
	}


	public int Record_Add(int user_id, Calendar datetimeC, int tag_id) {
		String datetime = DateUtils.formatToDb(datetimeC);
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", user_id);
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", tag_id);
		toInsert.put("Id_Carbs", -1);
		toInsert.put("Id_Insulin", -1);
		toInsert.put("Id_BloodGlucose", -1);
		toInsert.put("Id_Note", -1);
		return (int) myDB.insert("Record", null, toInsert);
	}
	public void Record_Update_Carbs(int record_id, int carbs_id) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Id_Carbs", carbs_id);
		myDB.update("Record", toUpdate, "Id="+record_id,null);
	}
	public void Record_Update_Insulin(int record_id, int insu_id) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Id_Insulin", insu_id);
		myDB.update("Record", toUpdate, "Id="+record_id,null);
	}
	public void Record_Update_Glycaemia(int record_id, int glycaemia_id) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Id_BloodGlucose", glycaemia_id);
		myDB.update("Record", toUpdate, "Id="+record_id,null);
	}
	public void Record_Update_Note(int record_id, int note_id) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Id_Note", note_id);
		myDB.update("Record", toUpdate, "Id="+record_id,null);
	}



//	public void Record_Add(int user_id, Calendar datetimeC, int tag_id, int carbs_id, int insulin_id, int bloodgluc_id, int note_id) {
//		String datetime = DateUtils.formatToDb(datetimeC);
//		ContentValues toInsert = new ContentValues();
//		toInsert.put("Id_User", user_id);
//		toInsert.put("DateTime", datetime);
//		toInsert.put("Id_Tag", tag_id);
//		toInsert.put("Id_Carbs", carbs_id);
//		toInsert.put("Id_Insulin", insulin_id);
//		toInsert.put("Id_BloodGlucose", bloodgluc_id);
//		toInsert.put("Id_Note", note_id);
//		myDB.insert("Record", null, toInsert);
//	}

	public void Record_Update(int record_id, int user_id, Calendar datetimeC, int tag_id) {
		String datetime = DateUtils.formatToDb(datetimeC);
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Id_User", user_id);
		toUpdate.put("DateTime", datetime);
		toUpdate.put("Id_Tag", tag_id);
		int res = myDB.update("Record", toUpdate, "Id=" + record_id, null);
	}

//	public void Record_Update(int record_id, int user_id, Calendar datetimeC, int tag_id, int carbs_id, int insulin_id, int bloodgluc_id, int note_id) {
//		String datetime = DateUtils.formatToDb(datetimeC);
//		ContentValues toUpdate = new ContentValues();
//		toUpdate.put("Id_User", user_id);
//		toUpdate.put("DateTime", datetime);
//		toUpdate.put("Id_Tag", tag_id);
//		toUpdate.put("Id_Carbs", carbs_id);
//		toUpdate.put("Id_Insulin", insulin_id);
//		toUpdate.put("Id_BloodGlucose", bloodgluc_id);
//		toUpdate.put("Id_Note", note_id);
//		int res = myDB.update("Record", toUpdate, "Id=" + record_id, null);
//		//Log.i("debug", "Record_Update: -> "+res);
//	}

	public void recoverSensitivity(){
		try {
			String dropTable = "DROP TABLE Sensitivity_Reg;";
			myDB.execSQL(dropTable);
			String recoverSens = "CREATE TABLE IF NOT EXISTS Sensitivity_Reg(Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, Name TEXT NOT NULL, TimeStart DATETIME NOT NULL, TimeEnd DATETIME NOT NULL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));";
			myDB.execSQL(recoverSens);
		} catch (SQLException sqlE) {
				Log.e("DB Update adding column", sqlE.getLocalizedMessage());
			}
		initSensRacio(myDB);
	}
	public void recoverRatio(){
		try {
			String dropTable = "DROP TABLE Ratio_Reg;";
			myDB.execSQL(dropTable);
			String recoverSens = "CREATE TABLE IF NOT EXISTS Ratio_Reg(Id INTEGER PRIMARY KEY AUTOINCREMENT, Id_User INTEGER NOT NULL, Value REAL NOT NULL, Name TEXT NOT NULL, TimeStart DATETIME NOT NULL, TimeEnd DATETIME NOT NULL, FOREIGN KEY(Id_User) REFERENCES UserInfo(Id));";
			myDB.execSQL(recoverSens);
		} catch (SQLException sqlE) {
			Log.e("DB Update adding column", sqlE.getLocalizedMessage());
		}
		initRacio(myDB);
	}


	private void initSensRacio(SQLiteDatabase db){

		DB_Read dbRead = new DB_Read(db);
		ArrayList<Tag> tags = dbRead.Tag_GetAll();
		int id_user = dbRead.getUserId();
		int value = dbRead.getInsulinRatio();
		for(int index=0;index<tags.size()-1;index++){

			ContentValues toInsert = new ContentValues();
			toInsert.put("Id_User", id_user);
			toInsert.put("Value", value);
			toInsert.put("Name", tags.get(index).getName());
			toInsert.put("TimeStart", tags.get(index).getStart());
			toInsert.put("TimeEnd", tags.get(index).getEnd());

			db.insert("Sensitivity_Reg", null, toInsert);
		}
	}

	private void initRacio(SQLiteDatabase db){

		DB_Read dbRead = new DB_Read(db);
		ArrayList<Tag> tags = dbRead.Tag_GetAll();
		int id_user = dbRead.getUserId();
		int value = dbRead.getInsulinRatio();
		for(int index=0;index<tags.size()-1;index++){

			ContentValues toInsert = new ContentValues();
			toInsert.put("Id_User", id_user);
			toInsert.put("Value", value);
			toInsert.put("Name", tags.get(index).getName());
			toInsert.put("TimeStart", tags.get(index).getStart());
			toInsert.put("TimeEnd", tags.get(index).getEnd());

			db.insert("Ratio_Reg", null, toInsert);
		}
	}

}
