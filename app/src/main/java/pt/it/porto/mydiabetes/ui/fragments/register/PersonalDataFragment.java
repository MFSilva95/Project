package pt.it.porto.mydiabetes.ui.fragments.register;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;

import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class PersonalDataFragment extends Fragment implements WelcomeActivity.RegistryFragmentPage {

	public static final int DEFAULT_BIRTHDAY_YEAR = 1980;
	public static final int DEFAULT_BIRTHDAY_MONTH = 5;
	public static final int DEFAULT_BIRTHDAY_DAY = 15;
	private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 10;

	private static final int RC_CODE_PICKER = 2000;
	private static final int REQUEST_TAKE_PHOTO = 6;

	private int THUMBSIZE = 350;




	private CircleImageView profileImage;
	private List<Image> images = new ArrayList<>();
	private String filename = "profilePhoto.png";
	private View layout = null;
	private EditText mNameView;
	private EditText mHeightView;
	private EditText mDateView;
	private GregorianCalendar birthdayDate;
	private RadioGroup mGenderGroup;
	private Bitmap bmp;
	private String userImgFileName = "profilePhoto";
	private Uri currentImageUri;

	private ScrollView scrollView;



	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment RegistPersonalDataFragment.
	 */
	public static PersonalDataFragment newInstance() {
		PersonalDataFragment fragment = new PersonalDataFragment();
		return fragment;
	}

	public PersonalDataFragment() {
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

		if(requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT){
			if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
					+ ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
				dispatchTakePictureIntent();
			}else{
				Toast.makeText(getContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            setProfilePhoto();
        }

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setProfilePhoto() {

		try {

			InputStream input = this.getContext().getContentResolver().openInputStream(currentImageUri);
			if (input == null) {
				Toast.makeText(getContext(),"Unable to save photo",Toast.LENGTH_LONG).show();
			}
			Bitmap pic_bitmap = BitmapFactory.decodeStream(input);
			File photoFile = null;
			try {
				photoFile = createImageFile();
				try (FileOutputStream out = new FileOutputStream(photoFile)) {
					pic_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
					out.flush();
					out.close();

					displayImg(photoFile.getAbsolutePath());

				} catch (IOException e) {
					e.printStackTrace();
				}


			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			Toast.makeText(getContext(),"Unable to save photo",Toast.LENGTH_LONG).show();
		}
	}


	private File createImageFile() throws IOException {
		// Create an image file name
		//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = userImgFileName;
		File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		return image;
	}



	private void displayImg(String path){

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		profileImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, THUMBSIZE, THUMBSIZE, false));

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		DB_Read read = new DB_Read(this.getContext());
		UserInfo user_info = read.MyData_Read();
		read.close();

		// Inflate the layout for this fragment
		layout = inflater.inflate(R.layout.fragment_register_personal_data, container, false);
		scrollView = (ScrollView) layout.findViewById(R.id.scrollview);

		mNameView = (EditText) layout.findViewById(R.id.name);
		mHeightView = (EditText) layout.findViewById(R.id.height);
		mDateView = (EditText) layout.findViewById(R.id.birthdate);
		setDate(DEFAULT_BIRTHDAY_YEAR, DEFAULT_BIRTHDAY_MONTH, DEFAULT_BIRTHDAY_DAY);
		Editable text = mDateView.getText();
		mDateView.setText(null);
		mDateView.setHint(text);
		mDateView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showDatePickerDialog();
			}
		});

		mGenderGroup = (RadioGroup) layout.findViewById(R.id.gender_group);
		mGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				// clears error state if needed
				((RadioButton) mGenderGroup.getChildAt(1)).setError(null);
			}
		});

		profileImage = (CircleImageView) layout.findViewById(R.id.profile_image);
		profileImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA + Manifest.permission.WRITE_EXTERNAL_STORAGE + Manifest.permission.READ_EXTERNAL_STORAGE)
						== PackageManager.PERMISSION_GRANTED) {
					dispatchTakePictureIntent();
				}else{
					requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
				}
			}



		});

		if(user_info!=null){
			if(user_info.getUsername()!=null){mNameView.setText(user_info.getUsername());}
			int gender;
			if((gender = user_info.getGender())!=-1){
				switch (gender){
					case 1:((RadioButton) layout.findViewById(R.id.radioButtonM)).setChecked(true);break;
					case 0:((RadioButton) layout.findViewById(R.id.radioButtonF)).setChecked(true);break;
				}
			}
			String date;
			if((date = user_info.getBirthday())!=null){mDateView.setText(date);}
			Double height;
			if((height = user_info.getHeight()) != null){mHeightView.setText(height.toString());}
		}


		return layout;
	}

	private void dispatchTakePictureIntent(){
		currentImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg"));
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
		startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	}




	@Override
	public boolean allFieldsAreValid() {
		// Reset errors.
		mNameView.setError(null);
		mHeightView.setError(null);
		mDateView.setError(null);
		((RadioButton) mGenderGroup.getChildAt(1)).setError(null);

		// Store values at the time of the login attempt.
		String name = mNameView.getText().toString();
		String height = mHeightView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Checks if name isn't empty
		if (TextUtils.isEmpty(name)) {
			mNameView.setError(getString(R.string.error_field_required));
			focusView = mNameView;
			cancel = true;
		}

		// Check if gender is selected
		if (mGenderGroup.getCheckedRadioButtonId() == -1) {
			((RadioButton) mGenderGroup.getChildAt(1)).setError(getString(R.string.error_field_required));
			focusView = mGenderGroup;
			cancel = true;
		}

		// Check for a valid height, if the user entered one.
		if (!isHeightValid(height)) {
			mHeightView.setError(getString(R.string.error_invalid_height));
			focusView = mHeightView;
			cancel = true;
		}

		// Check birthdate
		if (TextUtils.isEmpty(mDateView.getText())) {
			mDateView.setError(getString(R.string.error_field_required));
			focusView = mDateView;
			cancel = true;
		}


		if (cancel) {
			// There was an error;
			// form field with an error.
			focusView.requestFocus();
		}
		return !cancel;
	}

	@Override
	public void saveData(Bundle container) {

        int radioButtonID = mGenderGroup.getCheckedRadioButtonId();
        View radioButton = mGenderGroup.findViewById(radioButtonID);
        int idx = mGenderGroup.indexOfChild(radioButton);

		container.putString(WelcomeActivity.USER_DATA_NAME, mNameView.getText().toString());
		container.putInt(WelcomeActivity.USER_DATA_GENDER, idx);
		container.putString(WelcomeActivity.USER_DATA_HEIGHT, mHeightView.getText().toString());
		container.putString(WelcomeActivity.USER_DATA_BIRTHDAY_DATE, new StringBuilder(10).append(birthdayDate.get(Calendar.DAY_OF_MONTH))
				.append('-').append(birthdayDate.get(Calendar.MONTH)+1).append('-').append(birthdayDate.get(Calendar.YEAR)).toString());
	}


	public static boolean isHeightValid(String height) {
		float val = 0;
		try {
			val = Float.parseFloat(height);
		} catch (NumberFormatException e) {
			return false;
		}
		return (!TextUtils.isEmpty(height) && val > 0 && val < 3) || (!TextUtils.isEmpty(height) && val>120 && val < 300) ;
	}

	public void showDatePickerDialog() {

		final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int day) {
				setDate(year, month, day);
				allFieldsAreValid();
			}
		}, birthdayDate.get(Calendar.YEAR), birthdayDate.get(Calendar.MONTH), birthdayDate.get(Calendar.DAY_OF_MONTH));

		datePickerDialog.show();
	}

	private void setDate(int year, int month, int day) {
		birthdayDate = new GregorianCalendar(year, month, day);
		StringBuilder displayDate = new StringBuilder(18);
		displayDate.append(birthdayDate.get(Calendar.DAY_OF_MONTH));
		displayDate.append(" ");
		displayDate.append(birthdayDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
		displayDate.append(' ');
		displayDate.append(birthdayDate.get(Calendar.YEAR));
		mDateView.setText(displayDate.toString());
	}



	@Override
	public int getSubtitle() {
		return R.string.subtitle_personal_data;
	}

}
