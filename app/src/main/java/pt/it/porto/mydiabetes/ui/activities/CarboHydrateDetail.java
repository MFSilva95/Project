package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.listAdapters.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.NoteDataBinding;
import pt.it.porto.mydiabetes.ui.listAdapters.TagDataBinding;


public class CarboHydrateDetail extends Activity {

	//photo variables - start
	final private int CAPTURE_IMAGE = 2;
	Uri imgUri;
	Bitmap b;
	//photo variables - end
	int idNote = 0;
	int id_ch = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carbohydrate_detail);
		// Show the Up button in the action bar.
		getActionBar();
		FillTagSpinner();
		EditText hora = (EditText) findViewById(R.id.et_CarboHydrateDetail_Hora);

		Bundle args = getIntent().getExtras();
		if (args != null) {
			DB_Read rdb = new DB_Read(this);
			String id = args.getString("Id");
			id_ch = Integer.parseInt(args.getString("Id"));
			CarbsDataBinding toFill = rdb.CarboHydrate_GetById(Integer.parseInt(id));

			Spinner tagSpinner = (Spinner) findViewById(R.id.sp_CarboHydrateDetail_Tag);
			SelectSpinnerItemByValue(tagSpinner, rdb.Tag_GetById(toFill.getId_Tag()).getName());
			EditText carbs = (EditText) findViewById(R.id.et_CarboHydrateDetail_Value);
			carbs.setText(toFill.getCarbsValue().toString());
			EditText data = (EditText) findViewById(R.id.et_CarboHydrateDetail_Data);
			data.setText(toFill.getDate());
			Log.d("data reg carb", toFill.getDate());
			hora.setText(toFill.getTime());

			EditText note = (EditText) findViewById(R.id.et_CarboHydrateDetail_Notes);
			if (toFill.getId_Note() != -1) {
				NoteDataBinding n = new NoteDataBinding();
				n = rdb.Note_GetById(toFill.getId_Note());
				note.setText(n.getNote());
				idNote = n.getId();
			}

			EditText photopath = (EditText) findViewById(R.id.et_CarboHydrateDetail_Photo);
			if (!toFill.getPhotoPath().equals("")) {
				photopath.setText(toFill.getPhotoPath());
				Log.d("foto path", "foto: " + toFill.getPhotoPath());
				ImageView img = (ImageView) findViewById(R.id.iv_CarboHydrateDetail_Photo);
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				int height = (int) (displaymetrics.heightPixels * 0.1);
				int width = (int) (displaymetrics.widthPixels * 0.1);
				b = decodeSampledBitmapFromPath(toFill.getPhotoPath(), width, height);

				img.setImageBitmap(b);

			}

			Log.d("photopath", toFill.getPhotoPath());

			rdb.close();
		} else {
			FillDateHour();
			SetTagByTime();
			hora.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					SetTagByTime();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		}


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		Bundle args = getIntent().getExtras();
		if (args != null) {
			inflater.inflate(R.menu.carbo_hydrate_detail_edit, menu);
		} else {
			inflater.inflate(R.menu.carbo_hydrate_detail, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Bundle args = getIntent().getExtras();

		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrateDetail_Save:
				AddCarbsRead();
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrateDetail_Delete:
				DeleteCarbsRead(Integer.parseInt(args.getString("Id")));
				//NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menuItem_CarboHydrateDetail_EditSave:
				UpdateCarbsRead(Integer.parseInt(args.getString("Id")));
				//NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void SetTagByTime() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_CarboHydrateDetail_Tag);
		EditText hora = (EditText) findViewById(R.id.et_CarboHydrateDetail_Hora);
		DB_Read rdb = new DB_Read(this);
		String name = rdb.Tag_GetByTime(hora.getText().toString()).getName();
		rdb.close();
		SelectSpinnerItemByValue(tagSpinner, name);
	}

	public void FillDateHour() {
		EditText date = (EditText) findViewById(R.id.et_CarboHydrateDetail_Data);
		final Calendar calendar = Calendar.getInstance();
		date.setText(DatePickerFragment.getFormatedDate(calendar));

		EditText hour = (EditText) findViewById(R.id.et_CarboHydrateDetail_Hora);
		hour.setText(TimePickerFragment.getFormatedDate(calendar));
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_CarboHydrateDetail_Data,
				DatePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.et_CarboHydrateDetail_Hora,
				TimePickerFragment.getCalendar(((EditText) v).getText().toString()));
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void FillTagSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_CarboHydrateDetail_Tag);
		ArrayList<String> allTags = new ArrayList<String>();
		DB_Read rdb = new DB_Read(this);
		ArrayList<TagDataBinding> t = rdb.Tag_GetAll();
		rdb.close();


		if (t != null) {
			for (TagDataBinding i : t) {
				allTags.add(i.getName());
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allTags);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	public void AddCarbsRead() {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_CarboHydrateDetail_Tag);
		EditText carbs = (EditText) findViewById(R.id.et_CarboHydrateDetail_Value);
		EditText data = (EditText) findViewById(R.id.et_CarboHydrateDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_CarboHydrateDetail_Hora);
		EditText photopath = (EditText) findViewById(R.id.et_CarboHydrateDetail_Photo);
		EditText note = (EditText) findViewById(R.id.et_CarboHydrateDetail_Notes);

		//adicionado por zeornelas
		//para obrigar a colocar o valor dos hidratos e nao crashar
		if (carbs.getText().toString().equals("")) {
			carbs.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
			return;
		}


		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		DB_Write reg = new DB_Write(this);
		CarbsDataBinding carb = new CarbsDataBinding();

		if (!note.getText().toString().equals("")) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			carb.setId_Note(reg.Note_Add(n));
		}


		carb.setId_User(idUser);
		carb.setCarbsValue(Double.parseDouble(carbs.getText().toString()));
		carb.setId_Tag(idTag);
		carb.setPhotoPath(photopath.getText().toString()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
		carb.setDate(data.getText().toString());
		carb.setTime(hora.getText().toString());


		reg.Carbs_Save(carb);
		reg.close();
		goUp();
	}


	//PHOTO - START

	public Uri setImageUri() {
		// Store image in /MyDiabetes
		File file = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes", new Date().getTime() + ".jpg");
		File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
		if (!dir.exists()) {
			dir.mkdir();
		}
		imgUri = Uri.fromFile(file);
		return imgUri;
	}


	public void TakePhoto(View v) {
		EditText photopath = (EditText) findViewById(R.id.et_CarboHydrateDetail_Photo);
		if (!photopath.getText().toString().equals("")) {
			final Intent intent = new Intent(this, ViewPhoto.class);
			Bundle argsToPhoto = new Bundle();
			argsToPhoto.putString("Path", photopath.getText().toString());
			argsToPhoto.putInt("Id", id_ch);
			intent.putExtras(argsToPhoto);
			startActivityForResult(intent, 101010);
		} else {
			final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
			startActivityForResult(intent, CAPTURE_IMAGE);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		EditText photopath = (EditText) findViewById(R.id.et_CarboHydrateDetail_Photo);
		ImageView img = (ImageView) findViewById(R.id.iv_CarboHydrateDetail_Photo);
		if (resultCode != Activity.RESULT_CANCELED) {
			if (requestCode == CAPTURE_IMAGE) {
				Toast.makeText(getApplicationContext(), getString(R.string.photoSaved) + " " + imgUri.getPath(), Toast.LENGTH_LONG).show();
				DisplayMetrics displaymetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
				int height = (int) (displaymetrics.heightPixels * 0.1);
				int width = (int) (displaymetrics.widthPixels * 0.1);
				b = decodeSampledBitmapFromPath(imgUri.getPath(), width, height);

				img.setImageBitmap(b);
				photopath.setText(imgUri.getPath());

			} else if (requestCode == 101010) {
				Log.d("Result:", resultCode + "");
				//se tivermos apagado a foto dá result code -1
				//se voltarmos por um return por exemplo o resultcode é 0
				if (resultCode == -1) {
					photopath.setText("");
					img.setImageDrawable(getResources().getDrawable(R.drawable.newphoto));
				}
			} else {
				super.onActivityResult(requestCode, resultCode, data);
			}
		}

	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (imgUri != null) {
			outState.putString("cameraImageUri", imgUri.toString());
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey("cameraImageUri")) {
			imgUri = Uri.parse(savedInstanceState.getString("cameraImageUri"));
			EditText photopath = (EditText) findViewById(R.id.et_CarboHydrateDetail_Photo);
			ImageView img = (ImageView) findViewById(R.id.iv_CarboHydrateDetail_Photo);

			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = (int) (displaymetrics.heightPixels * 0.1);
			int width = (int) (displaymetrics.widthPixels * 0.1);
			b = decodeSampledBitmapFromPath(imgUri.getPath(), width, height);

			img.setImageBitmap(b);
			photopath.setText(imgUri.getPath());
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}


	public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//BitmapFactory.decodeResource(res, resId, options);
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return adjustImageOrientation(BitmapFactory.decodeFile(path, options), path);
	}


	private static Bitmap adjustImageOrientation(Bitmap image, String picturePath) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(picturePath);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			int rotate = 0;
			switch (exifOrientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;

				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;

				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
			}

			if (rotate != 0) {
				int w = image.getWidth();
				int h = image.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap & convert to ARGB_8888, required by tess
				image = Bitmap.createBitmap(image, 0, 0, w, h, mtx, false);

			}
		} catch (IOException e) {
			return null;
		}
		return image.copy(Bitmap.Config.ARGB_8888, true);
	}


	//PHOTO - END


	public static void SelectSpinnerItemByValue(Spinner spnr, String value) {
		SpinnerAdapter adapter = (SpinnerAdapter) spnr.getAdapter();
		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItem(position).equals(value)) {
				spnr.setSelection(position);
				return;
			}
		}
	}

	public void DeleteCarbsRead(final int id) {
		final Context c = this;
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.deleteReading))
				.setPositiveButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//Falta verificar se não está associada a nenhuma entrada da DB
						DB_Write wdb = new DB_Write(c);
						try {
							wdb.Carbs_Delete(id);
							goUp();
						} catch (Exception e) {
							Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
						}
						wdb.close();

					}
				})
				.setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				}).show();
	}

	public void goUp() {
		NavUtils.navigateUpFromSameTask(this);
	}

	public void UpdateCarbsRead(int id) {
		Spinner tagSpinner = (Spinner) findViewById(R.id.sp_CarboHydrateDetail_Tag);
		EditText carbs = (EditText) findViewById(R.id.et_CarboHydrateDetail_Value);
		EditText data = (EditText) findViewById(R.id.et_CarboHydrateDetail_Data);
		EditText hora = (EditText) findViewById(R.id.et_CarboHydrateDetail_Hora);
		EditText photopath = (EditText) findViewById(R.id.et_CarboHydrateDetail_Photo);
		EditText note = (EditText) findViewById(R.id.et_CarboHydrateDetail_Notes);

		if (carbs.getText().toString().equals("")) {
			carbs.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(carbs, InputMethodManager.SHOW_IMPLICIT);
			return;
		}

		//Get id of user 
		DB_Read rdb = new DB_Read(this);
		Object[] obj = rdb.MyData_Read();
		int idUser = Integer.valueOf(obj[0].toString());

		//Get id of selected tag
		String tag = tagSpinner.getSelectedItem().toString();
		Log.d("selected Spinner", tag);
		int idTag = rdb.Tag_GetIdByName(tag);
		rdb.close();
		DB_Write reg = new DB_Write(this);
		CarbsDataBinding carb = new CarbsDataBinding();

		if (!note.getText().toString().equals("") && idNote == 0) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			carb.setId_Note(reg.Note_Add(n));
		}
		if (idNote != 0) {
			NoteDataBinding n = new NoteDataBinding();
			n.setNote(note.getText().toString());
			n.setId(idNote);
			reg.Note_Update(n);
		}

		carb.setId(id);
		carb.setId_User(idUser);
		carb.setCarbsValue(Double.parseDouble(carbs.getText().toString()));
		carb.setId_Tag(idTag);
		carb.setPhotoPath(photopath.getText().toString()); // /data/MyDiabetes/yyyy-MM-dd HH.mm.ss.jpg
		carb.setDate(data.getText().toString());
		carb.setTime(hora.getText().toString());


		reg.Carbs_Update(carb);
		reg.close();
		goUp();
	}
}
