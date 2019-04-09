package pt.it.porto.mydiabetes.database;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.data.BloodPressureRec;
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
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.utils.DateUtils;


@RunWith(AndroidJUnit4.class)
public class DB_WriteTest {

	Context mMockContext;

	@Before
	public void setUp() {
		mMockContext = new RenamingDelegatingContext(InstrumentationRegistry.getInstrumentation().getTargetContext(), "test_");
	}


	@Test
	public void testClose() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		dbWrite.close();
		try {
			dbWrite.Tag_Add("test");
		} catch (IllegalStateException e) {
			Assert.assertNotNull(e);
			return;
		}
		Assert.fail("Not closed properly");
	}

	@Test
	public void testMyData_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);
		UserInfo data = dbRead.MyData_Read();
		Assert.assertNull("Data is null as expected", data);

		UserInfo newData = getMockUserData();
		dbWrite.MyData_Save(newData);
		data = dbRead.MyData_Read();

		Assert.assertEquals("Data not equal", newData, data);
		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTag_Add() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);
		ArrayList<Tag> tags = dbRead.Tag_GetAll();
		int originalSize = tags.size();
		int lastId = tags.get(originalSize - 1).getId();

		dbWrite.Tag_Add("test");

		tags = dbRead.Tag_GetAll();

		Assert.assertEquals("Tag don't added: size mismatch", originalSize + 1, tags.size());
		Assert.assertEquals("Tag don't added: wrong id", tags.get(originalSize).getId(), lastId + 1);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTag_Add1() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);
		ArrayList<Tag> tags = dbRead.Tag_GetAll();
		int originalSize = tags.size();
		int lastId = tags.get(originalSize - 1).getId();

		Tag tag = new Tag();
		tag.setId(originalSize);
		tag.setName("test");
		tag.setStart("00:01");
		tag.setEnd("00:02");
		dbWrite.Tag_Add(tag);

		tags = dbRead.Tag_GetAll();

		Assert.assertEquals("Tag don't added: size mismatch", originalSize + 1, tags.size());
		Assert.assertEquals("Tag don't added: wrong id", tags.get(originalSize).getId(), lastId + 1);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTag_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);
		ArrayList<Tag> tags = dbRead.Tag_GetAll();
		int originalSize = tags.size();

		dbWrite.Tag_Add("test");
		Tag tag = dbRead.Tag_GetAll().get(originalSize);
		String newName = "test1";
		tag.setName(newName);
		dbWrite.Tag_Update(tag);

		tag = dbRead.Tag_GetAll().get(originalSize);

		Assert.assertEquals("Tag Not updated", newName, tag.getName());

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTag_Remove() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);
		ArrayList<Tag> tags = dbRead.Tag_GetAll();
		int originalSize = tags.size();

		Tag tag = dbRead.Tag_GetAll().get(originalSize - 2);
		dbWrite.Tag_Remove(tag.getId());

		tags = dbRead.Tag_GetAll();

		Assert.assertEquals("Tag Not removed", originalSize - 1, tags.size());

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testDisease_Add() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.Disease_Add("gripe");

		Assert.assertTrue("Disease not added", dbRead.Disease_ExistName("gripe"));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testDisease_Remove() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.Disease_Add("gripe");
		dbWrite.Disease_Remove(1);

		Assert.assertFalse("Disease not removed", dbRead.Disease_ExistName("gripe"));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testDisease_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.Disease_Add("gripe");
		Disease disease = dbRead.Disease_GetAll().get(0);
		String newName = "gripe1";
		disease.setName(newName);
		dbWrite.Disease_Update(disease);

		disease = dbRead.Disease_GetAll().get(0);

		Assert.assertEquals("Disease not updated", newName, disease.getName());
		Assert.assertEquals("Disease not updated: wrong id", 1, disease.getId());


		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testGlycemia_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		GlycemiaRec glycemia = new GlycemiaRec();
		glycemia.setIdUser(1);
		glycemia.setId(1);
		glycemia.setValue(100);
		glycemia.setIdTag(1);
		Calendar calendar = Calendar.getInstance();
		glycemia.setDateTime(calendar);
		dbWrite.Glycemia_Save(glycemia);

		GlycemiaRec returnedGlycemia = dbRead.Glycemia_GetById(1);

		Assert.assertEquals("glycemia not equals to returned", glycemia, returnedGlycemia);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testGlycemia_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);


		dbWrite.MyData_Save(getMockUserData());

		GlycemiaRec glycemia = new GlycemiaRec();
		glycemia.setIdUser(1);
		glycemia.setId(1);
		glycemia.setValue(100);
		glycemia.setIdTag(1);
		Calendar calendar = Calendar.getInstance();
		glycemia.setDateTime(calendar);
		dbWrite.Glycemia_Save(glycemia);

		glycemia.setValue(110);
		dbWrite.Glycemia_Update(glycemia);

		GlycemiaRec returnedGlycemia = dbRead.Glycemia_GetById(1);

		Assert.assertNotNull(returnedGlycemia);
		Assert.assertEquals("Glicemia not updated", glycemia.getValue(), returnedGlycemia.getValue());
		Assert.assertEquals("glycemia not equals to returned", glycemia, returnedGlycemia);


		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testGlycemia_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());


		GlycemiaRec glycemia = new GlycemiaRec();
		glycemia.setIdUser(1);
		glycemia.setId(1);
		glycemia.setValue(100);
		glycemia.setIdTag(1);
		Calendar calendar = Calendar.getInstance();
		glycemia.setDateTime(calendar);
		dbWrite.Glycemia_Save(glycemia);

		dbWrite.Glycemia_Delete(1);

		Assert.assertNull("Glicemia not removed", dbRead.Glycemia_GetAll());

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testInsulin_Add() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());


		Insulin insulin = new Insulin();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);

		Insulin returnedInsulin = dbRead.Insulin_GetByName("humalog");

		Assert.assertNotNull(returnedInsulin);
		Assert.assertEquals("Insulin not added", insulin, returnedInsulin);

		dbRead.close();
		dbWrite.close();
	}


	@Test
	public void testInsulin_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());


		Insulin insulin = new Insulin();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);
		insulin.setName("novalog");
		dbWrite.Insulin_Update(insulin);

		Insulin returnedInsulin = dbRead.Insulin_GetByName("humalog");
		Assert.assertNull(returnedInsulin);

		returnedInsulin = dbRead.Insulin_GetByName("novalog");
		Assert.assertNotNull(returnedInsulin);
		Assert.assertEquals("Insulin not added", insulin, returnedInsulin);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testInsulin_Remove() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());


		Insulin insulin = new Insulin();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);
		dbWrite.Insulin_Remove(1);

		Insulin returnedInsulin = dbRead.Insulin_GetByName(insulin.getName());
		Assert.assertNull("Insulin not removed", returnedInsulin);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testInsulin_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		Insulin insulin = new Insulin();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);

		InsulinRec insulinRec = new InsulinRec();
		insulinRec.setIdUser(1);
		insulinRec.setId(1);
		insulinRec.setIdInsulin(1);
		insulinRec.setIdTag(1);
		insulinRec.setDateTime(Calendar.getInstance());
		insulinRec.setInsulinUnits(3);
		insulinRec.setTargetGlycemia(100);

		dbWrite.Insulin_Save(insulinRec);

		InsulinRec returnedInsulinRec = dbRead.InsulinReg_GetById(1);

		Assert.assertNotNull("InsulinRec not added", returnedInsulinRec);
		Assert.assertEquals("InsulinRec not equal", insulinRec, returnedInsulinRec);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testInsulin_Update1() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		Insulin insulin = new Insulin();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);

		InsulinRec insulinRec = new InsulinRec();
		insulinRec.setIdUser(1);
		insulinRec.setId(1);
		insulinRec.setIdInsulin(1);
		insulinRec.setIdTag(1);
		insulinRec.setDateTime(Calendar.getInstance());
		insulinRec.setInsulinUnits(3);
		insulinRec.setTargetGlycemia(100);

		dbWrite.Insulin_Save(insulinRec);
		insulinRec.setInsulinUnits(4);
		dbWrite.Insulin_Update(insulinRec);


		InsulinRec returnedInsulinRec = dbRead.InsulinReg_GetById(1);

		Assert.assertNotNull("InsulinRec not added", returnedInsulinRec);
		Assert.assertEquals("InsulinRec not updated", insulinRec, returnedInsulinRec);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testInsulin_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		Insulin insulin = new Insulin();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);

		InsulinRec insulinRec = new InsulinRec();
		insulinRec.setIdUser(1);
		insulinRec.setId(1);
		insulinRec.setIdInsulin(1);
		insulinRec.setIdTag(1);
		insulinRec.setDateTime(Calendar.getInstance());
		insulinRec.setInsulinUnits(3);
		insulinRec.setTargetGlycemia(100);

		dbWrite.Insulin_Save(insulinRec);
		dbWrite.Insulin_Delete(1);


		InsulinRec returnedInsulinRec = dbRead.InsulinReg_GetById(1);

		Assert.assertNull("InsulinRec not deleted", returnedInsulinRec);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testExercise_Add() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.Exercise_Add("running");

		Assert.assertTrue("Exercise not added", dbRead.Exercise_ExistName("running"));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testExercise_Remove() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);

		dbWrite.Exercise_Add("running");
		dbWrite.Exercise_Delete(1);
		dbWrite.close();

		DB_Read dbRead = new DB_Read(mMockContext);
		Assert.assertNull("Exercise not removed", dbRead.ExerciseReg_GetById(1));

		dbRead.close();
	}

	@Test
	public void testExercise_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.Exercise_Add("running");


		Exercise exercise = new Exercise();
		exercise.setId(1);
		exercise.setName("running1");
		dbWrite.Exercise_Update(exercise);

		Assert.assertTrue("Exercise not added", dbRead.Exercise_ExistName("running1"));
		Assert.assertFalse("Exercise not added", dbRead.Exercise_ExistName("running"));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testExercise_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		dbWrite.Exercise_Add("running");

		ExerciseRec exerciseRec =new ExerciseRec();
		exerciseRec.setIdUser(1);
		exerciseRec.setId(1);
		exerciseRec.setDateTime(Calendar.getInstance());
		exerciseRec.setDuration(10);
		exerciseRec.setExercise("running");
		exerciseRec.setEffort(2);
		dbWrite.Exercise_Save(exerciseRec);

		Assert.assertEquals("Exercise reg not added", exerciseRec, dbRead.ExerciseReg_GetById(1));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testExercise_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		dbWrite.Exercise_Add("running");

		ExerciseRec exerciseRec =new ExerciseRec();
		exerciseRec.setId(1);
		exerciseRec.setDateTime(Calendar.getInstance());
		exerciseRec.setDuration(10);
		exerciseRec.setExercise("running");
		exerciseRec.setIdUser(1);
		exerciseRec.setEffort(2);
		dbWrite.Exercise_Save(exerciseRec);

		dbWrite.Exercise_Delete(1);

		Assert.assertNull("Exercise reg not added", dbRead.ExerciseReg_GetById(1));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testExercise_Update1() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		dbWrite.Exercise_Add("running");

		ExerciseRec exerciseRec =new ExerciseRec();
		exerciseRec.setId(1);
		exerciseRec.setDateTime(Calendar.getInstance());
		exerciseRec.setDuration(10);
		exerciseRec.setExercise("running");
		exerciseRec.setIdUser(1);
		exerciseRec.setEffort(2);
		dbWrite.Exercise_Save(exerciseRec);
		exerciseRec.setDuration(20);
		dbWrite.Exercise_Update(exerciseRec);

		ExerciseRec returnedExerciseRec = dbRead.ExerciseReg_GetById(1);
		Assert.assertNotNull(returnedExerciseRec);
		Assert.assertEquals("Exercise reg not updated", exerciseRec, returnedExerciseRec);
		Assert.assertEquals("Exercise reg not updated",20, returnedExerciseRec.getDuration());

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_Save() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsRec carbsRec = new CarbsRec();
		carbsRec.setIdUser(1);
		carbsRec.setId(1);
		carbsRec.setCarbsValue(10);
		carbsRec.setPhotoPath("/");
		carbsRec.setIdTag(1);
		carbsRec.setIdNote(-1);
		carbsRec.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsRec);

		CarbsRec result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull(result);
		Log.d("test", "saved: " + carbsRec.toString());
		Log.d("test", "returned: " + result.toString());
		Assert.assertTrue("Carb data not the same as saved", carbsRec.equals(result));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_Update() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsRec carbsRec = new CarbsRec();
		carbsRec.setIdUser(1);
		carbsRec.setId(1);
		carbsRec.setCarbsValue(10);
		carbsRec.setPhotoPath("/");
		carbsRec.setIdTag(1);
		carbsRec.setIdNote(-1);
		carbsRec.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsRec);

		carbsRec.setCarbsValue(11);
		dbWrite.Carbs_Update(carbsRec);

		CarbsRec result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull(result);
		Log.d("test", "saved: " + carbsRec.toString());
		Log.d("test", "returned: " + result.toString());
		Assert.assertTrue("Carb data not the same as saved", carbsRec.equals(result));
		Assert.assertEquals("Carbs value different from the expected", carbsRec.getCarbsValue(), result.getCarbsValue());

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_Delete() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsRec carbsRec = new CarbsRec();
		carbsRec.setIdUser(1);
		carbsRec.setId(1);
		carbsRec.setCarbsValue(10);
		carbsRec.setPhotoPath("/");
		carbsRec.setIdTag(1);
		carbsRec.setIdNote(-1);
		carbsRec.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsRec);

		CarbsRec result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull(result);
		dbWrite.Carbs_Delete(1);
		result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNull("Carbs value not deleted", result);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_DeletePhoto() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsRec carbsRec = new CarbsRec();
		carbsRec.setIdUser(1);
		carbsRec.setId(1);
		carbsRec.setCarbsValue(10);
		carbsRec.setPhotoPath("/");
		carbsRec.setIdTag(1);
		carbsRec.setIdNote(-1);
		carbsRec.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsRec);

		dbWrite.Carbs_DeletePhoto(1);
		CarbsRec result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull("Carbs in DB null", result);
		Assert.assertEquals("Photopath not deleted", "", result.getPhotoPath());

		dbRead.close();
		dbWrite.close();
	}


	@Test
	public void testBloodPressure_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		BloodPressureRec bp=new BloodPressureRec();
		bp.setId(1);
		bp.setDateTime(Calendar.getInstance());
		bp.setDiastolic(85);
		bp.setSystolic(110);
		bp.setIdTag(1);
		bp.setIdUser(1);
		dbWrite.BloodPressure_Save(bp);

		BloodPressureRec returnBp = dbRead.BloodPressure_GetById(1);

		Assert.assertNotNull(returnBp);
		Assert.assertEquals(bp, returnBp);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testBloodPressure_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		BloodPressureRec bp=new BloodPressureRec();
		bp.setId(1);
		bp.setDateTime(Calendar.getInstance());
		bp.setDiastolic(85);
		bp.setSystolic(110);
		bp.setIdTag(1);
		bp.setIdUser(1);
		dbWrite.BloodPressure_Save(bp);
		bp.setDiastolic(86);
		dbWrite.BloodPressure_Update(bp);

		BloodPressureRec returnBp = dbRead.BloodPressure_GetById(1);

		Assert.assertNotNull(returnBp);
		Assert.assertEquals(bp, returnBp);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testBloodPressure_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		BloodPressureRec bp=new BloodPressureRec();
		bp.setId(1);
		bp.setDateTime(Calendar.getInstance());
		bp.setDiastolic(85);
		bp.setSystolic(110);
		bp.setIdTag(1);
		bp.setIdUser(1);
		dbWrite.BloodPressure_Save(bp);
		dbWrite.BloodPressure_Delete(1);

		BloodPressureRec returnBp = dbRead.BloodPressure_GetById(1);

		Assert.assertNull(returnBp);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testNote_Add() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		Note note=new Note();
		note.setId(1);
		note.setNote("note");

		dbWrite.Note_Add(note);

		Note returnedNote = dbRead.Note_GetById(1);

		Assert.assertNotNull(returnedNote);
		Assert.assertEquals(note, returnedNote);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testNote_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		Note note=new Note();
		note.setId(1);
		note.setNote("note");

		dbWrite.Note_Add(note);
		note.setNote("note1");
		dbWrite.Note_Update(note);

		Note returnedNote = dbRead.Note_GetById(1);

		Assert.assertNotNull(returnedNote);
		Assert.assertEquals(note, returnedNote);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testNote_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		Note note=new Note();
		note.setId(1);
		note.setNote("note");

		dbWrite.Note_Add(note);
		dbWrite.Note_Delete(1);

		Note returnedNote = dbRead.Note_GetById(1);

		Assert.assertNull(returnedNote);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCholesterol_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CholesterolRec cholesterol=new CholesterolRec();
		cholesterol.setId(1);
		cholesterol.setIdUser(1);
		cholesterol.setDateTime(Calendar.getInstance());
		cholesterol.setValue(50.0);
		dbWrite.Cholesterol_Save(cholesterol);

		CholesterolRec returnedCholesterol = dbRead.Cholesterol_GetById(1);

		Assert.assertNotNull(returnedCholesterol);
		Assert.assertEquals(cholesterol, returnedCholesterol);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCholesterol_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CholesterolRec cholesterol=new CholesterolRec();
		cholesterol.setId(1);
		cholesterol.setIdUser(1);
		cholesterol.setDateTime(Calendar.getInstance());
		cholesterol.setValue(50.0);
		dbWrite.Cholesterol_Save(cholesterol);
		cholesterol.setValue(51.0);
		dbWrite.Cholesterol_Update(cholesterol);

		CholesterolRec returnedCholesterol = dbRead.Cholesterol_GetById(1);

		Assert.assertNotNull(returnedCholesterol);
		Assert.assertEquals(cholesterol, returnedCholesterol);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCholesterol_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CholesterolRec cholesterol=new CholesterolRec();
		cholesterol.setId(1);
		cholesterol.setIdUser(1);
		cholesterol.setDateTime(Calendar.getInstance());
		cholesterol.setValue(50.0);
		dbWrite.Cholesterol_Save(cholesterol);
		dbWrite.Cholesterol_Delete(1);

		CholesterolRec returnedCholesterol = dbRead.Cholesterol_GetById(1);

		Assert.assertNull(returnedCholesterol);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testWeight_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		WeightRec weight=new WeightRec();
		weight.setIdUser(1);
		weight.setId(1);
		weight.setValue(50.0);
		weight.setDateTime(Calendar.getInstance());
		dbWrite.Weight_Save(weight);

		WeightRec returnedWeight = dbRead.Weight_GetById(1);

		Assert.assertNotNull(returnedWeight);
		Assert.assertEquals(weight, returnedWeight);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testWeight_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		WeightRec weight=new WeightRec();
		weight.setIdUser(1);
		weight.setId(1);
		weight.setValue(50.0);
		weight.setDateTime(Calendar.getInstance());
		dbWrite.Weight_Save(weight);
		weight.setValue(51.0);
		dbWrite.Weight_Update(weight);

		WeightRec returnedWeight = dbRead.Weight_GetById(1);

		Assert.assertNotNull(returnedWeight);
		Assert.assertEquals(weight, returnedWeight);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testWeight_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		WeightRec weight=new WeightRec();
		weight.setIdUser(1);
		weight.setId(1);
		weight.setValue(50.0);
		weight.setDateTime(Calendar.getInstance());
		dbWrite.Weight_Save(weight);
		dbWrite.Weight_Delete(1);

		WeightRec returnedWeight = dbRead.Weight_GetById(1);

		Assert.assertNull(returnedWeight);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testDiseaseReg_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		DiseaseRec disease=new DiseaseRec();
		disease.setIdUser(1);
		disease.setId(1);
		disease.setDisease("gripe");
		disease.setDateTime(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.DiseaseReg_Save(disease);

		DiseaseRec returnedDisease = dbRead.DiseaseReg_GetById(1);

		Assert.assertNotNull(returnedDisease);
		Assert.assertEquals(disease, returnedDisease);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testDiseaseReg_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		DiseaseRec disease=new DiseaseRec();
		disease.setIdUser(1);
		disease.setId(1);
		disease.setDisease("gripe");
		disease.setDateTime(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.DiseaseReg_Save(disease);
		disease.setDisease("gripe1");
		dbWrite.DiseaseReg_Update(disease);

		DiseaseRec returnedDisease = dbRead.DiseaseReg_GetById(1);

		Assert.assertNotNull(returnedDisease);
		Assert.assertEquals(disease, returnedDisease);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testDiseaseReg_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		DiseaseRec disease=new DiseaseRec();
		disease.setIdUser(1);
		disease.setId(1);
		disease.setDisease("gripe");
		disease.setDateTime(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.DiseaseReg_Save(disease);
		dbWrite.DiseaseReg_Delete(1);

		DiseaseRec returnedDisease = dbRead.DiseaseReg_GetById(1);

		Assert.assertNull(returnedDisease);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testHbA1c_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		HbA1cRec hba1=new HbA1cRec();
		hba1.setId(1);
		hba1.setIdUser(1);
		hba1.setDateTime(Calendar.getInstance());
		hba1.setValue(10.0);
		dbWrite.HbA1c_Save(hba1);

		HbA1cRec returnedHba1 = dbRead.HbA1c_GetById(1);

		Assert.assertNotNull(returnedHba1);
		Assert.assertEquals(hba1, returnedHba1);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testHbA1c_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		HbA1cRec hba1=new HbA1cRec();
		hba1.setId(1);
		hba1.setIdUser(1);
		hba1.setDateTime(Calendar.getInstance());
		hba1.setValue(10.0);
		dbWrite.HbA1c_Save(hba1);
		hba1.setValue(11.0);
		dbWrite.HbA1c_Update(hba1);

		HbA1cRec returnedHba1 = dbRead.HbA1c_GetById(1);

		Assert.assertNotNull(returnedHba1);
		Assert.assertEquals(hba1, returnedHba1);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testHbA1c_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		HbA1cRec hba1=new HbA1cRec();
		hba1.setId(1);
		hba1.setIdUser(1);
		hba1.setDateTime(Calendar.getInstance());
		hba1.setValue(10.0);
		dbWrite.HbA1c_Save(hba1);
		dbWrite.HbA1c_Delete(1);

		HbA1cRec returnedHba1 = dbRead.HbA1c_GetById(1);

		Assert.assertNull(returnedHba1);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTarget_Remove() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		InsulinTarget target=new InsulinTarget();
		target.setId(1);
		target.setName("all day");
		target.setStart(DateUtils.formatToDb(Calendar.getInstance()));
		target.setEnd(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.Target_Add(target);
		dbWrite.Target_Remove(1);

		InsulinTarget returnedTarget = dbRead.Target_GetById(1);

		Assert.assertNull(returnedTarget);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTarget_Add() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		InsulinTarget target=new InsulinTarget();
		target.setId(1);
		target.setName("all day");
		target.setStart(DateUtils.formatToDb(Calendar.getInstance()));
		target.setEnd(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.Target_Add(target);

		InsulinTarget returnedTarget = dbRead.Target_GetById(1);

		Assert.assertNotNull(returnedTarget);
		Assert.assertEquals(target, returnedTarget);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTarget_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		InsulinTarget target=new InsulinTarget();
		target.setId(1);
		target.setName("all day");
		target.setStart(DateUtils.formatToDb(Calendar.getInstance()));
		target.setEnd(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.Target_Add(target);
		target.setName("all day 2");
		dbWrite.Target_Update(target);

		InsulinTarget returnedTarget = dbRead.Target_GetById(1);

		Assert.assertNotNull(returnedTarget);
		Assert.assertEquals(target, returnedTarget);

		dbRead.close();
		dbWrite.close();
	}


	@Test
	public void testBadges() throws Exception {


	}

	@Test
	public void testLogbook_Delete() throws Exception {


	}

	@Test
	public void testLogbook_DeleteOnSave() throws Exception {

	}


	static UserInfo getMockUserData() {
		Calendar calendar = Calendar.getInstance();
		return new UserInfo(1, "Nome", 1, 45, 50, 50, 150, "11-01-2011", 1, 1.85, DateUtils.formatToDb(calendar),100);
	}


}