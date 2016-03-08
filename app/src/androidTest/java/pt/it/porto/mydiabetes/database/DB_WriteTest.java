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
import java.util.Arrays;
import java.util.Calendar;

import pt.it.porto.mydiabetes.ui.dataBinding.BloodPressureDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.CholesterolDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.DiseaseDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.DiseaseRegDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.ExerciseDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.ExerciseRegDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.HbA1cDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.InsulinDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.NoteDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.TagDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.TargetDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.WeightDataBinding;
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
		Object[] data = dbRead.MyData_Read();
		Log.d("Data", Arrays.toString(data));
		Assert.assertArrayEquals("Data is null as expected", null, data);

		Object[] newData = getMockUserData();
		dbWrite.MyData_Save(newData);
		data = dbRead.MyData_Read();

		Assert.assertArrayEquals("Data not equal", newData, data);
		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTag_Add() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);
		ArrayList<TagDataBinding> tags = dbRead.Tag_GetAll();
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
		ArrayList<TagDataBinding> tags = dbRead.Tag_GetAll();
		int originalSize = tags.size();
		int lastId = tags.get(originalSize - 1).getId();

		TagDataBinding tagDataBinding = new TagDataBinding();
		tagDataBinding.setId(originalSize);
		tagDataBinding.setName("test");
		tagDataBinding.setStart("00:01");
		tagDataBinding.setEnd("00:02");
		dbWrite.Tag_Add(tagDataBinding);

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
		ArrayList<TagDataBinding> tags = dbRead.Tag_GetAll();
		int originalSize = tags.size();

		dbWrite.Tag_Add("test");
		TagDataBinding tag = dbRead.Tag_GetAll().get(originalSize);
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
		ArrayList<TagDataBinding> tags = dbRead.Tag_GetAll();
		int originalSize = tags.size();

		TagDataBinding tag = dbRead.Tag_GetAll().get(originalSize - 2);
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
		DiseaseDataBinding disease = dbRead.Disease_GetAll().get(0);
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

		GlycemiaDataBinding glycemia = new GlycemiaDataBinding();
		glycemia.setIdUser(1);
		glycemia.setId(1);
		glycemia.setValue(100);
		glycemia.setIdTag(1);
		Calendar calendar = Calendar.getInstance();
		glycemia.setDateTime(calendar);
		dbWrite.Glycemia_Save(glycemia);

		GlycemiaDataBinding returnedGlycemia = dbRead.Glycemia_GetById(1);

		Assert.assertEquals("glycemia not equals to returned", glycemia, returnedGlycemia);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testGlycemia_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);


		dbWrite.MyData_Save(getMockUserData());

		GlycemiaDataBinding glycemia = new GlycemiaDataBinding();
		glycemia.setIdUser(1);
		glycemia.setId(1);
		glycemia.setValue(100);
		glycemia.setIdTag(1);
		Calendar calendar = Calendar.getInstance();
		glycemia.setDateTime(calendar);
		dbWrite.Glycemia_Save(glycemia);

		glycemia.setValue(110);
		dbWrite.Glycemia_Update(glycemia);

		GlycemiaDataBinding returnedGlycemia = dbRead.Glycemia_GetById(1);

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


		GlycemiaDataBinding glycemia = new GlycemiaDataBinding();
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


		InsulinDataBinding insulin = new InsulinDataBinding();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);

		InsulinDataBinding returnedInsulin = dbRead.Insulin_GetByName("humalog");

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


		InsulinDataBinding insulin = new InsulinDataBinding();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);
		insulin.setName("novalog");
		dbWrite.Insulin_Update(insulin);

		InsulinDataBinding returnedInsulin = dbRead.Insulin_GetByName("humalog");
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


		InsulinDataBinding insulin = new InsulinDataBinding();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);
		dbWrite.Insulin_Remove(1);

		InsulinDataBinding returnedInsulin = dbRead.Insulin_GetByName(insulin.getName());
		Assert.assertNull("Insulin not removed", returnedInsulin);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testInsulin_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		InsulinDataBinding insulin = new InsulinDataBinding();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);

		InsulinRegDataBinding insulinReg = new InsulinRegDataBinding();
		insulinReg.setIdUser(1);
		insulinReg.setId(1);
		insulinReg.setIdInsulin(1);
		insulinReg.setIdTag(1);
		insulinReg.setDateTime(Calendar.getInstance());
		insulinReg.setInsulinUnits(3);
		insulinReg.setTargetGlycemia(100);

		dbWrite.Insulin_Save(insulinReg);

		InsulinRegDataBinding returnedInsulinReg = dbRead.InsulinReg_GetById(1);

		Assert.assertNotNull("InsulinReg not added", returnedInsulinReg);
		Assert.assertEquals("InsulinReg not equal", insulinReg, returnedInsulinReg);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testInsulin_Update1() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		InsulinDataBinding insulin = new InsulinDataBinding();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);

		InsulinRegDataBinding insulinReg = new InsulinRegDataBinding();
		insulinReg.setIdUser(1);
		insulinReg.setId(1);
		insulinReg.setIdInsulin(1);
		insulinReg.setIdTag(1);
		insulinReg.setDateTime(Calendar.getInstance());
		insulinReg.setInsulinUnits(3);
		insulinReg.setTargetGlycemia(100);

		dbWrite.Insulin_Save(insulinReg);
		insulinReg.setInsulinUnits(4);
		dbWrite.Insulin_Update(insulinReg);


		InsulinRegDataBinding returnedInsulinReg = dbRead.InsulinReg_GetById(1);

		Assert.assertNotNull("InsulinReg not added", returnedInsulinReg);
		Assert.assertEquals("InsulinReg not updated", insulinReg, returnedInsulinReg);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testInsulin_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		InsulinDataBinding insulin = new InsulinDataBinding();
		insulin.setId(1);
		insulin.setName("humalog");
		insulin.setDuration(1);
		insulin.setAction("longa");
		insulin.setType("Longa");

		dbWrite.Insulin_Add(insulin);

		InsulinRegDataBinding insulinReg = new InsulinRegDataBinding();
		insulinReg.setIdUser(1);
		insulinReg.setId(1);
		insulinReg.setIdInsulin(1);
		insulinReg.setIdTag(1);
		insulinReg.setDateTime(Calendar.getInstance());
		insulinReg.setInsulinUnits(3);
		insulinReg.setTargetGlycemia(100);

		dbWrite.Insulin_Save(insulinReg);
		dbWrite.Insulin_Delete(1);


		InsulinRegDataBinding returnedInsulinReg = dbRead.InsulinReg_GetById(1);

		Assert.assertNull("InsulinReg not deleted", returnedInsulinReg);

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


		ExerciseDataBinding exercise = new ExerciseDataBinding();
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

		ExerciseRegDataBinding exerciseRegDataBinding=new ExerciseRegDataBinding();
		exerciseRegDataBinding.setId_User(1);
		exerciseRegDataBinding.setId(1);
		exerciseRegDataBinding.setDateTime(Calendar.getInstance());
		exerciseRegDataBinding.setDuration(10);
		exerciseRegDataBinding.setExercise("running");
		exerciseRegDataBinding.setEffort("forte");
		dbWrite.Exercise_Save(exerciseRegDataBinding);

		Assert.assertEquals("Exercise reg not added",exerciseRegDataBinding, dbRead.ExerciseReg_GetById(1));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testExercise_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		dbWrite.Exercise_Add("running");

		ExerciseRegDataBinding exerciseRegDataBinding=new ExerciseRegDataBinding();
		exerciseRegDataBinding.setId(1);
		exerciseRegDataBinding.setDateTime(Calendar.getInstance());
		exerciseRegDataBinding.setDuration(10);
		exerciseRegDataBinding.setExercise("running");
		exerciseRegDataBinding.setId_User(1);
		exerciseRegDataBinding.setEffort("forte");
		dbWrite.Exercise_Save(exerciseRegDataBinding);

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

		ExerciseRegDataBinding exerciseRegDataBinding=new ExerciseRegDataBinding();
		exerciseRegDataBinding.setId(1);
		exerciseRegDataBinding.setDateTime(Calendar.getInstance());
		exerciseRegDataBinding.setDuration(10);
		exerciseRegDataBinding.setExercise("running");
		exerciseRegDataBinding.setId_User(1);
		exerciseRegDataBinding.setEffort("forte");
		dbWrite.Exercise_Save(exerciseRegDataBinding);
		exerciseRegDataBinding.setDuration(20);
		dbWrite.Exercise_Update(exerciseRegDataBinding);

		ExerciseRegDataBinding returnedExerciseReg = dbRead.ExerciseReg_GetById(1);
		Assert.assertNotNull(returnedExerciseReg);
		Assert.assertEquals("Exercise reg not updated",exerciseRegDataBinding, returnedExerciseReg);
		Assert.assertEquals("Exercise reg not updated",20, returnedExerciseReg.getDuration());

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_Save() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsDataBinding carbsDataBinding = new CarbsDataBinding();
		carbsDataBinding.setId_User(1);
		carbsDataBinding.setId(1);
		carbsDataBinding.setCarbsValue(10);
		carbsDataBinding.setPhotoPath("/");
		carbsDataBinding.setId_Tag(1);
		carbsDataBinding.setId_Note(-1);
		carbsDataBinding.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsDataBinding);

		CarbsDataBinding result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull(result);
		Log.d("test", "saved: " + carbsDataBinding.toString());
		Log.d("test", "returned: " + result.toString());
		Assert.assertTrue("Carb data not the same as saved", carbsDataBinding.equals(result));

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_Update() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsDataBinding carbsDataBinding = new CarbsDataBinding();
		carbsDataBinding.setId_User(1);
		carbsDataBinding.setId(1);
		carbsDataBinding.setCarbsValue(10);
		carbsDataBinding.setPhotoPath("/");
		carbsDataBinding.setId_Tag(1);
		carbsDataBinding.setId_Note(-1);
		carbsDataBinding.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsDataBinding);

		carbsDataBinding.setCarbsValue(11);
		dbWrite.Carbs_Update(carbsDataBinding);

		CarbsDataBinding result = dbRead.CarboHydrate_GetById(1);
		Assert.assertNotNull(result);
		Log.d("test", "saved: " + carbsDataBinding.toString());
		Log.d("test", "returned: " + result.toString());
		Assert.assertTrue("Carb data not the same as saved", carbsDataBinding.equals(result));
		Assert.assertEquals("Carbs value different from the expected", carbsDataBinding.getCarbsValue(), result.getCarbsValue());

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCarbs_Delete() {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CarbsDataBinding carbsDataBinding = new CarbsDataBinding();
		carbsDataBinding.setId_User(1);
		carbsDataBinding.setId(1);
		carbsDataBinding.setCarbsValue(10);
		carbsDataBinding.setPhotoPath("/");
		carbsDataBinding.setId_Tag(1);
		carbsDataBinding.setId_Note(-1);
		carbsDataBinding.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsDataBinding);

		CarbsDataBinding result = dbRead.CarboHydrate_GetById(1);
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

		CarbsDataBinding carbsDataBinding = new CarbsDataBinding();
		carbsDataBinding.setId_User(1);
		carbsDataBinding.setId(1);
		carbsDataBinding.setCarbsValue(10);
		carbsDataBinding.setPhotoPath("/");
		carbsDataBinding.setId_Tag(1);
		carbsDataBinding.setId_Note(-1);
		carbsDataBinding.setDateTime(Calendar.getInstance());
		dbWrite.Carbs_Save(carbsDataBinding);

		dbWrite.Carbs_DeletePhoto(1);
		CarbsDataBinding result = dbRead.CarboHydrate_GetById(1);
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

		BloodPressureDataBinding bp=new BloodPressureDataBinding();
		bp.setId(1);
		bp.setDateTime(Calendar.getInstance());
		bp.setDiastolic(85);
		bp.setSystolic(110);
		bp.setIdTag(1);
		bp.setIdUser(1);
		dbWrite.BloodPressure_Save(bp);

		BloodPressureDataBinding returnBp = dbRead.BloodPressure_GetById(1);

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

		BloodPressureDataBinding bp=new BloodPressureDataBinding();
		bp.setId(1);
		bp.setDateTime(Calendar.getInstance());
		bp.setDiastolic(85);
		bp.setSystolic(110);
		bp.setIdTag(1);
		bp.setIdUser(1);
		dbWrite.BloodPressure_Save(bp);
		bp.setDiastolic(86);
		dbWrite.BloodPressure_Update(bp);

		BloodPressureDataBinding returnBp = dbRead.BloodPressure_GetById(1);

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

		BloodPressureDataBinding bp=new BloodPressureDataBinding();
		bp.setId(1);
		bp.setDateTime(Calendar.getInstance());
		bp.setDiastolic(85);
		bp.setSystolic(110);
		bp.setIdTag(1);
		bp.setIdUser(1);
		dbWrite.BloodPressure_Save(bp);
		dbWrite.BloodPressure_Delete(1);

		BloodPressureDataBinding returnBp = dbRead.BloodPressure_GetById(1);

		Assert.assertNull(returnBp);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testNote_Add() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		NoteDataBinding note=new NoteDataBinding();
		note.setId(1);
		note.setNote("note");

		dbWrite.Note_Add(note);

		NoteDataBinding returnedNote = dbRead.Note_GetById(1);

		Assert.assertNotNull(returnedNote);
		Assert.assertEquals(note, returnedNote);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testNote_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		NoteDataBinding note=new NoteDataBinding();
		note.setId(1);
		note.setNote("note");

		dbWrite.Note_Add(note);
		note.setNote("note1");
		dbWrite.Note_Update(note);

		NoteDataBinding returnedNote = dbRead.Note_GetById(1);

		Assert.assertNotNull(returnedNote);
		Assert.assertEquals(note, returnedNote);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testNote_Delete() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		NoteDataBinding note=new NoteDataBinding();
		note.setId(1);
		note.setNote("note");

		dbWrite.Note_Add(note);
		dbWrite.Note_Delete(1);

		NoteDataBinding returnedNote = dbRead.Note_GetById(1);

		Assert.assertNull(returnedNote);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testCholesterol_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		CholesterolDataBinding cholesterol=new CholesterolDataBinding();
		cholesterol.setId(1);
		cholesterol.setIdUser(1);
		cholesterol.setDateTime(Calendar.getInstance());
		cholesterol.setValue(50.0);
		dbWrite.Cholesterol_Save(cholesterol);

		CholesterolDataBinding returnedCholesterol = dbRead.Cholesterol_GetById(1);

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

		CholesterolDataBinding cholesterol=new CholesterolDataBinding();
		cholesterol.setId(1);
		cholesterol.setIdUser(1);
		cholesterol.setDateTime(Calendar.getInstance());
		cholesterol.setValue(50.0);
		dbWrite.Cholesterol_Save(cholesterol);
		cholesterol.setValue(51.0);
		dbWrite.Cholesterol_Update(cholesterol);

		CholesterolDataBinding returnedCholesterol = dbRead.Cholesterol_GetById(1);

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

		CholesterolDataBinding cholesterol=new CholesterolDataBinding();
		cholesterol.setId(1);
		cholesterol.setIdUser(1);
		cholesterol.setDateTime(Calendar.getInstance());
		cholesterol.setValue(50.0);
		dbWrite.Cholesterol_Save(cholesterol);
		dbWrite.Cholesterol_Delete(1);

		CholesterolDataBinding returnedCholesterol = dbRead.Cholesterol_GetById(1);

		Assert.assertNull(returnedCholesterol);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testWeight_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		WeightDataBinding weight=new WeightDataBinding();
		weight.setIdUser(1);
		weight.setId(1);
		weight.setValue(50.0);
		weight.setDateTime(Calendar.getInstance());
		dbWrite.Weight_Save(weight);

		WeightDataBinding returnedWeight = dbRead.Weight_GetById(1);

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

		WeightDataBinding weight=new WeightDataBinding();
		weight.setIdUser(1);
		weight.setId(1);
		weight.setValue(50.0);
		weight.setDateTime(Calendar.getInstance());
		dbWrite.Weight_Save(weight);
		weight.setValue(51.0);
		dbWrite.Weight_Update(weight);

		WeightDataBinding returnedWeight = dbRead.Weight_GetById(1);

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

		WeightDataBinding weight=new WeightDataBinding();
		weight.setIdUser(1);
		weight.setId(1);
		weight.setValue(50.0);
		weight.setDateTime(Calendar.getInstance());
		dbWrite.Weight_Save(weight);
		dbWrite.Weight_Delete(1);

		WeightDataBinding returnedWeight = dbRead.Weight_GetById(1);

		Assert.assertNull(returnedWeight);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testDiseaseReg_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		DiseaseRegDataBinding disease=new DiseaseRegDataBinding();
		disease.setIdUser(1);
		disease.setId(1);
		disease.setDisease("gripe");
		disease.setStartDate(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.DiseaseReg_Save(disease);

		DiseaseRegDataBinding returnedDisease = dbRead.DiseaseReg_GetById(1);

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

		DiseaseRegDataBinding disease=new DiseaseRegDataBinding();
		disease.setIdUser(1);
		disease.setId(1);
		disease.setDisease("gripe");
		disease.setStartDate(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.DiseaseReg_Save(disease);
		disease.setDisease("gripe1");
		dbWrite.DiseaseReg_Update(disease);

		DiseaseRegDataBinding returnedDisease = dbRead.DiseaseReg_GetById(1);

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

		DiseaseRegDataBinding disease=new DiseaseRegDataBinding();
		disease.setIdUser(1);
		disease.setId(1);
		disease.setDisease("gripe");
		disease.setStartDate(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.DiseaseReg_Save(disease);
		dbWrite.DiseaseReg_Delete(1);

		DiseaseRegDataBinding returnedDisease = dbRead.DiseaseReg_GetById(1);

		Assert.assertNull(returnedDisease);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testHbA1c_Save() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		dbWrite.MyData_Save(getMockUserData());

		HbA1cDataBinding hba1=new HbA1cDataBinding();
		hba1.setId(1);
		hba1.setIdUser(1);
		hba1.setDateTime(Calendar.getInstance());
		hba1.setValue(10.0);
		dbWrite.HbA1c_Save(hba1);

		HbA1cDataBinding returnedHba1 = dbRead.HbA1c_GetById(1);

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

		HbA1cDataBinding hba1=new HbA1cDataBinding();
		hba1.setId(1);
		hba1.setIdUser(1);
		hba1.setDateTime(Calendar.getInstance());
		hba1.setValue(10.0);
		dbWrite.HbA1c_Save(hba1);
		hba1.setValue(11.0);
		dbWrite.HbA1c_Update(hba1);

		HbA1cDataBinding returnedHba1 = dbRead.HbA1c_GetById(1);

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

		HbA1cDataBinding hba1=new HbA1cDataBinding();
		hba1.setId(1);
		hba1.setIdUser(1);
		hba1.setDateTime(Calendar.getInstance());
		hba1.setValue(10.0);
		dbWrite.HbA1c_Save(hba1);
		dbWrite.HbA1c_Delete(1);

		HbA1cDataBinding returnedHba1 = dbRead.HbA1c_GetById(1);

		Assert.assertNull(returnedHba1);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTarget_Remove() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		TargetDataBinding target=new TargetDataBinding();
		target.setId(1);
		target.setName("all day");
		target.setStart(DateUtils.formatToDb(Calendar.getInstance()));
		target.setEnd(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.Target_Add(target);
		dbWrite.Target_Remove(1);

		TargetDataBinding returnedTarget = dbRead.Target_GetById(1);

		Assert.assertNull(returnedTarget);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTarget_Add() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		TargetDataBinding target=new TargetDataBinding();
		target.setId(1);
		target.setName("all day");
		target.setStart(DateUtils.formatToDb(Calendar.getInstance()));
		target.setEnd(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.Target_Add(target);

		TargetDataBinding returnedTarget = dbRead.Target_GetById(1);

		Assert.assertNotNull(returnedTarget);
		Assert.assertEquals(target, returnedTarget);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testTarget_Update() throws Exception {
		DB_Write dbWrite = new DB_Write(mMockContext);
		DB_Read dbRead = new DB_Read(mMockContext);

		TargetDataBinding target=new TargetDataBinding();
		target.setId(1);
		target.setName("all day");
		target.setStart(DateUtils.formatToDb(Calendar.getInstance()));
		target.setEnd(DateUtils.formatToDb(Calendar.getInstance()));
		dbWrite.Target_Add(target);
		target.setName("all day 2");
		dbWrite.Target_Update(target);

		TargetDataBinding returnedTarget = dbRead.Target_GetById(1);

		Assert.assertNotNull(returnedTarget);
		Assert.assertEquals(target, returnedTarget);

		dbRead.close();
		dbWrite.close();
	}

	@Test
	public void testLogbook_Delete() throws Exception {


	}

	@Test
	public void testLogbook_DeleteOnSave() throws Exception {

	}


	private Object[] getMockUserData() {
		Calendar calendar = Calendar.getInstance();
		//// FIXME: 07/03/16 insulin type and gender shouldn't be a string, this can complete breakage if language changes
		return new Object[]{1, "Nome", "Tipo 1", 45.0, 50.0, 50.0, 150.0, "11-01-2011", "Homem", 1.85, DateUtils.formatToDb(calendar)};
	}


}