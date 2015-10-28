package com.jadg.mydiabetes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;

import com.jadg.mydiabetes.ui.listAdapters.BloodPressureDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.CarbsDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.CholesterolDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.DiseaseDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.DiseaseRegDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.ExerciseDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.ExerciseRegDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.GlycemiaDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.HbA1cDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.InsulinDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.InsulinRegDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.NoteDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.TagDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.TargetDataBinding;
import com.jadg.mydiabetes.ui.listAdapters.WeightDataBinding;


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

	public void close() {
		myDB.close();
		Log.d("Close", "DB_Write");
	}

	public void MyData_Save(Object[] obj) {
		Cursor cursor = myDB.rawQuery("SELECT * FROM UserInfo WHERE Id=" + obj[0].toString(), null);

		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			ContentValues toUpdate = new ContentValues();
			toUpdate.put("Name", obj[1].toString());
			toUpdate.put("DType", obj[2].toString());
			toUpdate.put("InsulinRatio", Double.parseDouble(obj[3].toString()));
			toUpdate.put("CarbsRatio", Double.parseDouble(obj[4].toString()));
			toUpdate.put("LowerRange", Double.parseDouble(obj[5].toString()));
			toUpdate.put("HigherRange", Double.parseDouble(obj[6].toString()));
			toUpdate.put("BDate", obj[7].toString());
			toUpdate.put("Gender", obj[8].toString());
			toUpdate.put("Height", Float.parseFloat(obj[9].toString()));
			Time now = new Time(Time.getCurrentTimezone());
			now.setToNow();
			toUpdate.put("DateTimeUpdate", now.format("%Y-%m-%d %H:%M:%S"));
			myDB.update("UserInfo", toUpdate, "Id=" + Integer.parseInt(cursor.getString(0)), null);
		} else {
			ContentValues toInsert = new ContentValues();
			toInsert.put("Name", obj[1].toString());
			toInsert.put("DType", obj[2].toString());
			toInsert.put("InsulinRatio", Double.parseDouble(obj[3].toString()));
			toInsert.put("CarbsRatio", Double.parseDouble(obj[4].toString()));
			toInsert.put("LowerRange", Double.parseDouble(obj[5].toString()));
			toInsert.put("HigherRange", Double.parseDouble(obj[6].toString()));
			toInsert.put("BDate", obj[7].toString());
			toInsert.put("Gender", obj[8].toString());
			toInsert.put("Height", Float.parseFloat(obj[9].toString()));
			Time now = new Time(Time.getCurrentTimezone());
			now.setToNow();
			toInsert.put("DateTimeUpdate", now.format("%Y-%m-%d %H:%M:%S"));
			myDB.insert("UserInfo", null, toInsert);
		}
		Log.d("Guardou", "Preferencias");
	}

	//--------------- TAG ----------------------
	public void Tag_Add(String tag) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", tag);
		myDB.insert("Tag", null, toInsert);
	}

	public void Tag_Add(TagDataBinding t) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", t.getName());
		toInsert.put("TimeStart", t.getStart());
		toInsert.put("TimeEnd", t.getEnd());
		myDB.insert("Tag", null, toInsert);
	}

	public void Tag_Update(TagDataBinding t) {
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

	public void Disease_Update(DiseaseDataBinding i) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Name", i.getName());

		myDB.update("Disease", toUpdate, "Id=" + i.getId(), null);
		Log.d("Update", "Disease");
	}

	//--------------- GLYCEMIA -------------------
	public int Glycemia_Save(GlycemiaDataBinding obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getIdUser());
		toInsert.put("Value", obj.getValue());
		String datetime = obj.getDate() + " " + obj.getTime();
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", obj.getIdTag());
		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}

		Log.d("Guardou", "Reg_BloodGlucose");
		return (int) myDB.insert("Reg_BloodGlucose", null, toInsert);
	}

	public void Glycemia_Update(GlycemiaDataBinding obj) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Id_User", obj.getIdUser());
		toUpdate.put("Value", obj.getValue());
		String datetime = obj.getDate() + " " + obj.getTime();
		toUpdate.put("DateTime", datetime);
		toUpdate.put("Id_Tag", obj.getIdTag());
		if (obj.getIdNote() > 0) {
			toUpdate.put("Id_Note", obj.getIdNote());
		}

		myDB.update("Reg_BloodGlucose", toUpdate, "Id=" + obj.getId(), null);

		Log.d("Update", "Reg_BloodGlucose");
	}

	public void Glycemia_Delete(int id) {
		DB_Read rdb = new DB_Read(myContext);
		int idNote = rdb.Glycemia_GetById(id).getIdNote();
		rdb.close();
		myDB.delete("Reg_BloodGlucose", "Id=" + id, null);
		Log.d("Delete", "Reg_BloodGlucose");
		Note_Delete(idNote);
	}

	//------------------ INSULIN ----------------------
	public void Insulin_Add(Object[] obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", obj[0].toString());
		toInsert.put("Type", obj[1].toString());
		toInsert.put("Action", obj[2].toString());
		toInsert.put("Duration", Double.parseDouble(obj[3].toString()));

		myDB.insert("Insulin", null, toInsert);

		Log.d("Guardou", "Insulin");
	}

	public void Insulin_Add(InsulinDataBinding i) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", i.getName());
		toInsert.put("Type", i.getType());
		toInsert.put("Action", i.getAction());
		toInsert.put("Duration", i.getDuration());

		myDB.insert("Insulin", null, toInsert);

		Log.d("Guardou", "Insulin");
	}

	public void Insulin_Update(InsulinDataBinding i) {
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

	public int Insulin_Save(InsulinRegDataBinding obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getIdUser());
		toInsert.put("Id_Insulin", obj.getIdInsulin());
		if (obj.getIdBloodGlucose() > 0) {
			toInsert.put("Id_BloodGlucose", obj.getIdBloodGlucose());
		}
		String datetime = obj.getDate() + " " + obj.getTime();
		toInsert.put("DateTime", datetime);
		toInsert.put("Target_BG", obj.getTargetGlycemia());
		toInsert.put("Value", obj.getInsulinUnits());
		toInsert.put("Id_Tag", obj.getIdTag());
		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}

		return (int) myDB.insert("Reg_Insulin", null, toInsert);
	}

	public void Insulin_Update(InsulinRegDataBinding obj) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Id_User", obj.getIdUser());
		toUpdate.put("Id_Insulin", obj.getIdInsulin());
		if (obj.getIdBloodGlucose() > 0) {
			toUpdate.put("Id_BloodGlucose", obj.getIdBloodGlucose());
		} else {
			toUpdate.putNull("Id_BloodGlucose");
		}
		String datetime = obj.getDate() + " " + obj.getTime();
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
		DB_Read rdb = new DB_Read(myContext);
		int idNote = rdb.InsulinReg_GetById(id).getIdNote();
		int idGlycemia = rdb.InsulinReg_GetById(id).getIdBloodGlucose();
		rdb.close();
		myDB.delete("Reg_Insulin", "Id=" + id, null);
		if (idGlycemia > 0) {
			Glycemia_Delete(idGlycemia);
		}
		Note_Delete(idNote);
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

	public void Exercise_Update(ExerciseDataBinding i) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("Name", i.getName());

		myDB.update("Exercise", toUpdate, "Id=" + i.getId(), null);
		Log.d("Update", "Disease");
	}

	//------------------- EXERCISE REG ---------------------------

	public int Exercise_Save(ExerciseRegDataBinding obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getId_User());
		toInsert.put("Exercise", obj.getExercise());
		toInsert.put("Duration", obj.getDuration());
		toInsert.put("Effort", obj.getEffort());
		String datetime = obj.getDate() + " " + obj.getTime();
		toInsert.put("StartDateTime", datetime);
		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}

		return (int) myDB.insert("Reg_Exercise", null, toInsert);
	}

	public void Exercise_Delete(int id) {
		DB_Read rdb = new DB_Read(myContext);
		int idNote = rdb.ExerciseReg_GetById(id).getIdNote();
		rdb.close();
		myDB.delete("Reg_Exercise", "Id=" + id, null);
		Note_Delete(idNote);
	}

	public void Exercise_Update(ExerciseRegDataBinding obj) {

		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getId_User());
		toInsert.put("Exercise", obj.getExercise());
		toInsert.put("Duration", obj.getDuration());
		toInsert.put("Effort", obj.getEffort());
		String datetime = obj.getDate() + " " + obj.getTime();
		toInsert.put("StartDateTime", datetime);
		if (obj.getIdNote() > 0) {
			toInsert.put("Id_Note", obj.getIdNote());
		}

		myDB.update("Reg_Exercise", toInsert, "Id=" + obj.getId(), null);
	}

	//------------------- MEDICINE -------------------------------
	public void Medicine_Add(Object[] obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", obj[0].toString());
		toInsert.put("Units", obj[1].toString());

		myDB.insert("Medicine", null, toInsert);

		Log.d("Guardou", "Medicine");
	}

	public void Medicine_Remove(int id) {
		myDB.delete("Medicine", "id=" + id, null);
	}

	//----------------- CARBS ------------------------------
	public void Carbs_Save(CarbsDataBinding obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getId_User());
		toInsert.put("Value", obj.getCarbsValue());
		toInsert.put("PhotoPath", obj.getPhotoPath());
		String datetime = obj.getDate() + " " + obj.getTime();
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", obj.getId_Tag());
		if (obj.getId_Note() > 0) {
			toInsert.put("Id_Note", obj.getId_Note());
		}
		myDB.insert("Reg_CarboHydrate", null, toInsert);
	}

	public void Carbs_Update(CarbsDataBinding obj) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", obj.getId_User());
		toInsert.put("Value", obj.getCarbsValue());
		toInsert.put("PhotoPath", obj.getPhotoPath());
		String datetime = obj.getDate() + " " + obj.getTime();
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", obj.getId_Tag());
		if (obj.getId_Note() > 0) {
			toInsert.put("Id_Note", obj.getId_Note());
		}

		myDB.update("Reg_CarboHydrate", toInsert, "Id=" + obj.getId(), null);
		Log.d("Reg_CarboHydrate", "actualizado");
	}

	public void Carbs_Delete(int id) {
		myDB.delete("Reg_CarboHydrate", "Id=" + id, null);
		Log.d("Delete", "Reg_CarboHydrate");
	}


	public void Carbs_DeletePhoto(int id) {
		ContentValues toUpdate = new ContentValues();
		toUpdate.put("PhotoPath", "");

		myDB.update("Reg_CarboHydrate", toUpdate, "Id=" + id, null);
	}


	//-------------- BLOODPRESSURE --------------

	public void BloodPressure_Save(BloodPressureDataBinding bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Systolic", bp.getSystolic());
		toInsert.put("Diastolic", bp.getDiastolic());
		String datetime = bp.getDate() + " " + bp.getTime();
		toInsert.put("DateTime", datetime);
		toInsert.put("Id_Tag", bp.getIdTag());
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.insert("Reg_BloodPressure", null, toInsert);
	}

	public void BloodPressure_Update(BloodPressureDataBinding bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Systolic", bp.getSystolic());
		toInsert.put("Diastolic", bp.getDiastolic());
		String datetime = bp.getDate() + " " + bp.getTime();
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
	public int Note_Add(NoteDataBinding note) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Note", note.getNote());

		return (int) myDB.insert("Note", null, toInsert);
	}

	public void Note_Update(NoteDataBinding note) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Note", note.getNote());

		myDB.update("Note", toInsert, "Id=" + note.getId(), null);
	}

	public void Note_Delete(int id) {
		myDB.delete("Note", "Id=" + id, null);
	}

	//--------------- CHOLESTEROL ------------
	public void Cholesterol_Save(CholesterolDataBinding bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = bp.getDate() + " " + bp.getTime();
		toInsert.put("DateTime", datetime);
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.insert("Reg_Cholesterol", null, toInsert);
	}

	public void Cholesterol_Update(CholesterolDataBinding bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = bp.getDate() + " " + bp.getTime();
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
	public void Weight_Save(WeightDataBinding bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = bp.getDate() + " " + bp.getTime();
		toInsert.put("DateTime", datetime);
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.insert("Reg_Weight", null, toInsert);
	}

	public void Weight_Update(WeightDataBinding bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = bp.getDate() + " " + bp.getTime();
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
	public void DiseaseReg_Save(DiseaseRegDataBinding disease) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", disease.getIdUser());
		toInsert.put("Disease", disease.getDisease());
		toInsert.put("StartDate", disease.getStartDate());
		if (disease.getEndDate() != null) {
			toInsert.put("EndDate", disease.getEndDate());
		}
		if (disease.getIdNote() > 0) {
			toInsert.put("Id_Note", disease.getIdNote());
		}

		myDB.insert("Reg_Disease", null, toInsert);
	}

	public void DiseaseReg_Update(DiseaseRegDataBinding disease) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", disease.getIdUser());
		toInsert.put("Disease", disease.getDisease());
		toInsert.put("StartDate", disease.getStartDate());
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
	public void HbA1c_Save(HbA1cDataBinding bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = bp.getDate() + " " + bp.getTime();
		toInsert.put("DateTime", datetime);
		if (bp.getIdNote() > 0) {
			toInsert.put("Id_Note", bp.getIdNote());
		}

		myDB.insert("Reg_A1c", null, toInsert);
	}

	public void HbA1c_Update(HbA1cDataBinding bp) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Id_User", bp.getIdUser());
		toInsert.put("Value", bp.getValue());
		String datetime = bp.getDate() + " " + bp.getTime();
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

	public void Target_Add(TargetDataBinding t) {
		ContentValues toInsert = new ContentValues();
		toInsert.put("Name", t.getName());
		toInsert.put("TimeStart", t.getStart());
		toInsert.put("TimeEnd", t.getEnd());
		toInsert.put("Value", t.getTarget());
		myDB.insert("BG_Target", null, toInsert);
	}

	public void Target_Update(TargetDataBinding t) {
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
			InsulinRegDataBinding ins = rdb.InsulinReg_GetById(insToUpdate);

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

}
