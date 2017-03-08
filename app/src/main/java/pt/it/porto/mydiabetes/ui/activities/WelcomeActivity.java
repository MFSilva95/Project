package pt.it.porto.mydiabetes.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import pt.it.porto.mydiabetes.R;

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
					if (SettingsImportExport.restoreBackup(getApplicationContext())) {
						// jump to home
						Intent intent = new Intent(getBaseContext(), Home.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), R.string.restore_backup_error, Toast.LENGTH_LONG).show();
					}
				}
			});
		}

		mViewPager.blockSwipeRight(true);
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



}

