package pt.it.porto.mydiabetes.ui.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import pt.it.porto.mydiabetes.R;

import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.utils.CustomViewPager;
import pt.it.porto.mydiabetes.ui.listAdapters.welcomePageAdapter;


/**
 * A login screen that offers login via email/password.
 */
public class WelcomeActivity extends BaseActivity {



	@Override
	public String getRegType() {
		return null;
	}

	// save state
	private final static String BUNDLE_CURRENT_FRAGMENT = "current_fragment";
	private final static String BUNDLE_SHOW_BUTTON_ACTIVE = "next_button_active";
	private final static String BUNDLE_DATA = "data";

	// constants to save data
	public static final String USER_DATA_NAME = "name";
	public static final String USER_DATA_HEIGHT = "height";
	public static final String USER_DATA_GENDER = "gender";
	public static final String USER_DATA_BIRTHDAY_DATE = "birthday_date";
	public static final String USER_DATA_DIABETES_TYPE = "diabetes_type";
	public static final String USER_DATA_SENSIBILITY_FACTOR = "sensibility_factor";
	public static final String USER_DATA_CARBS_RATIO = "carbs_ratio";
	public static final String USER_DATA_HYPOGLYCEMIA_LIMIT = "hypoglycemia_limit";
	public static final String USER_DATA_HYPERGLYCEMIA_LIMIT = "hyperglycemia_limit";

	private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
	private static final int REQUEST_PERMISSION_SETTING = 101;
	private boolean sentToSettings = false;
	private SharedPreferences permissionStatus;


	// UI references.
	private LinearLayout pageIndicators;
	private CustomViewPager mViewPager;
	private PagerAdapter adapter;
	private int currentFragment = 0;
	private int lastFragment = 0;

	// user inserted data
	private Bundle data = new Bundle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

		pageIndicators = (LinearLayout) findViewById(R.id.page_indicator);
		mViewPager = (CustomViewPager) super.findViewById(R.id.content_fragment);
		adapter = new welcomePageAdapter(super.getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mViewPager.setOffscreenPageLimit(3);

		if (savedInstanceState != null) {
			currentFragment = savedInstanceState.getInt(BUNDLE_CURRENT_FRAGMENT, 0);

			data = savedInstanceState.getBundle(BUNDLE_DATA);
			if (data == null) {
				data = new Bundle();
			}
			if (currentFragment != 0) {
				setPageIndicator(0, currentFragment);
			}
		}
		Button nextButton = (Button) findViewById(R.id.nextBT);
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				next();
			}
		});

		if (SettingsImportExport.hasBackup()) {
			Button button = (Button) findViewById(R.id.restoreDb);
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					checkPermissions();

				}
			});
		}

		//mViewPager.blockSwipeRight(true);
		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				hideKeyboard();
				lastFragment = currentFragment;
				currentFragment = position;
				setPageIndicator(lastFragment, currentFragment);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(BUNDLE_CURRENT_FRAGMENT, currentFragment);
		outState.putBundle(BUNDLE_DATA, data);
	}

	private void next() {
		hideKeyboard();
		Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.content_fragment + ":" + mViewPager.getCurrentItem());
		if (((RegistryFragmentPage) page).allFieldsAreValid()) {
			((RegistryFragmentPage) page).saveData(data);
			if (currentFragment + 1 == adapter.getCount()) {
				FeaturesDB db = new FeaturesDB(MyDiabetesStorage.getInstance(getBaseContext()));
				db.changeFeatureStatus(FeaturesDB.INITIAL_REG_DONE, true);
				Intent intent = new Intent(this, Home.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			} else {
				mViewPager.setCurrentItem(currentFragment+1);
			}
		}
	}


	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		View currentFocus = getCurrentFocus();
		if (currentFocus != null) {
			imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void setPageIndicator(int origin, int destiny) {
		View dest = pageIndicators.getChildAt(destiny);
		final View orig = pageIndicators.getChildAt(origin);

		dest.setBackgroundResource(R.drawable.dot_big);
		AnimatorSet animatorSet = new AnimatorSet();

		ObjectAnimator originAnimator = ObjectAnimator.ofFloat(orig, "alpha", (float) 1.0, (float) 0.2);
		ObjectAnimator destAnimator = ObjectAnimator.ofFloat(dest, "alpha", (float) 0.2, (float) 1.0);
		animatorSet.play(originAnimator).with(destAnimator);
		animatorSet.start();
		animatorSet.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {

			}

			@Override
			public void onAnimationEnd(Animator animator) {
				orig.setBackgroundResource(R.drawable.dot_small);
				orig.setAlpha(1);
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
	}


	public interface RegistryFragmentPage {
		boolean allFieldsAreValid();

		void saveData(Bundle container);

		int getSubtitle();
	}

	public void checkPermissions(){
		if (ActivityCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(WelcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				//Show Information about why you need the permission
				AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
				builder.setTitle("Need Storage Permission");
				builder.setMessage("This app needs storage permission.");
				builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.show();
			} else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
				//Previously Permission Request was cancelled with 'Dont Ask Again',
				// Redirect to Settings after showing Information about why you need the permission
				AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
				builder.setTitle("Need Storage Permission");
				builder.setMessage("This app needs storage permission.");
				builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						sentToSettings = true;
						Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						Uri uri = Uri.fromParts("package", getPackageName(), null);
						intent.setData(uri);
						startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
						Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.show();
			} else {
				//just request the permission
				ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
			}

			SharedPreferences.Editor editor = permissionStatus.edit();
			editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
			editor.commit();


		} else {
			//You already have the permission, just go ahead.
			proceedAfterPermission();
		}
	}

	private void proceedAfterPermission() {
		//We've got the permission, now we can proceed further
		if (SettingsImportExport.restoreBackup(getApplicationContext())) {
			// jump to home
			FeaturesDB db = new FeaturesDB(MyDiabetesStorage.getInstance(getBaseContext()));
			db.changeFeatureStatus(FeaturesDB.INITIAL_REG_DONE, true);
			Intent intent = new Intent(getBaseContext(), Home.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
		} else {
			Toast.makeText(getApplicationContext(), R.string.restore_backup_error, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				//The External Storage Write Permission is granted to you... Continue your left job...
				proceedAfterPermission();
			} else {
				if (ActivityCompat.shouldShowRequestPermissionRationale(WelcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					//Show Information about why you need the permission
					AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
					builder.setTitle("Need Storage Permission");
					builder.setMessage("This app needs storage permission");
					builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();


							ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);


						}
					});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					builder.show();
				} else {
					Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
				}
			}
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_PERMISSION_SETTING) {
			if (ActivityCompat.checkSelfPermission(WelcomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				//Got Permission
				proceedAfterPermission();
			}
		}
	}


	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (sentToSettings) {
			if (ActivityCompat.checkSelfPermission(WelcomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				//Got Permission
				proceedAfterPermission();
			}
		}
	}


}

