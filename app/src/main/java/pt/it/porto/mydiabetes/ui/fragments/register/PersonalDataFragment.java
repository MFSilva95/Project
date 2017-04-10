package pt.it.porto.mydiabetes.ui.fragments.register;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;

import com.esafirm.imagepicker.model.Image;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class PersonalDataFragment extends Fragment implements WelcomeActivity.RegistryFragmentPage {

	public static final int DEFAULT_BIRTHDAY_YEAR = 1980;
	public static final int DEFAULT_BIRTHDAY_MONTH = 5;
	public static final int DEFAULT_BIRTHDAY_DAY = 15;

	private static final int RC_CODE_PICKER = 2000;



	private CircleImageView profileImage;
	private ArrayList<Image> images = new ArrayList<>();
	private String filename = "profilePhoto.png";
	private View layout = null;
	private EditText mNameView;
	private EditText mHeightView;
	private EditText mDateView;
	private GregorianCalendar birthdayDate;
	private RadioGroup mGenderGroup;
	private Bitmap bmp;



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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		layout = inflater.inflate(R.layout.fragment_register_personal_data, container, false);
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

				Intent intent = new Intent(getContext(), ImagePickerActivity.class);

				intent.putExtra(ImagePicker.EXTRA_FOLDER_MODE, true);
				intent.putExtra(ImagePicker.EXTRA_MODE, ImagePicker.MODE_SINGLE);
				intent.putExtra(ImagePicker.EXTRA_SHOW_CAMERA, false);
				intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGES, images);
				intent.putExtra(ImagePicker.EXTRA_FOLDER_TITLE, "Album");
				intent.putExtra(ImagePicker.EXTRA_IMAGE_TITLE, "Tap to select images");
				intent.putExtra(ImagePicker.EXTRA_IMAGE_DIRECTORY, "Camera");

				startActivityForResult(intent, RC_CODE_PICKER);
			}



		});

		return layout;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		final int THUMBSIZE = 250;
		if (requestCode == RC_CODE_PICKER && resultCode == RESULT_OK && data != null) {
			images = data.getParcelableArrayListExtra(ImagePicker.EXTRA_SELECTED_IMAGES);
			bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(images.get(0).getPath()), THUMBSIZE, THUMBSIZE);
			ContextWrapper cw = new ContextWrapper(getContext());
			// path to /data/data/yourapp/app_data/imageDir
			File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
			// Create imageDir
			File mypath=new File(directory,filename);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(mypath);
				// Use the compress method on the BitMap object to write image to the OutputStream
				bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			profileImage.setImageURI(Uri.parse(mypath.getAbsolutePath()));
		}

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
		container.putString(WelcomeActivity.USER_DATA_NAME, mNameView.getText().toString());
		container.putString(WelcomeActivity.USER_DATA_GENDER, ((RadioButton) mGenderGroup.findViewById(mGenderGroup.getCheckedRadioButtonId())).getText().toString().toLowerCase());
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
		return !TextUtils.isEmpty(height) && val > 0 && val < 3;
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
