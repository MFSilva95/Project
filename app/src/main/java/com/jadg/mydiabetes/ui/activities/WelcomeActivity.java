package com.jadg.mydiabetes.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.ui.fragments.register.AddInsulinsFragment;
import com.jadg.mydiabetes.ui.fragments.register.FactorsFragment;
import com.jadg.mydiabetes.ui.fragments.register.OnFormEnd;
import com.jadg.mydiabetes.ui.fragments.register.PersonalDataFragment;


/**
 * A login screen that offers login via email/password.
 */
public class WelcomeActivity extends BaseActivity implements OnFormEnd {

	// save state
	private final static String BUNDLE_CURRENT_FRAGMENT = "current_fragment";
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
	private View mLoginFormView;
	private LinearLayout pageIndicators;
	private int currentFragment = 0;
	private Fragment[] fragmentPages = new Fragment[]{new PersonalDataFragment(), new FactorsFragment(), new AddInsulinsFragment()};

	// user inserted data
	private Bundle data = new Bundle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		mLoginFormView = findViewById(R.id.login_form);
		pageIndicators = (LinearLayout) findViewById(R.id.page_indicator);

		if (savedInstanceState != null) {
			currentFragment = savedInstanceState.getInt(BUNDLE_CURRENT_FRAGMENT, 0);
			data = savedInstanceState.getBundle(BUNDLE_DATA);
			if(data==null){
				data=new Bundle();
			}
			if (currentFragment != 0) {
				setPageIndicator(0, currentFragment);
			}
		} else {
			getSupportFragmentManager().beginTransaction().add(R.id.content_fragment, fragmentPages[currentFragment]).commit();
		}
		Button nextButton = (Button) findViewById(R.id.nextBT);
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				next();
			}
		});
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(BUNDLE_CURRENT_FRAGMENT, currentFragment);
		outState.putBundle(BUNDLE_DATA, data);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void next() {
		fragmentPages[currentFragment]=getSupportFragmentManager().findFragmentById(R.id.content_fragment);
		((RegistryFragamentPage) fragmentPages[currentFragment]).saveData(data);
		if (((RegistryFragamentPage) fragmentPages[currentFragment]).allFieldsAreValid()) {
			if (currentFragment + 1 == fragmentPages.length) {
				// we are in the last fragment page
				// save data and exit
			} else {
				// moves to next page
				getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
						.remove(fragmentPages[currentFragment])
						.add(R.id.content_fragment, fragmentPages[currentFragment + 1])
						.commit();
				setPageIndicator(currentFragment, currentFragment + 1);
				currentFragment++;
			}
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
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
	}

	@Override
	public void formFillEnded() {
		next();
	}

	public interface RegistryFragamentPage {
		boolean allFieldsAreValid();
		void saveData(Bundle container);
	}

}

