package com.jadg.mydiabetes.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jadg.mydiabetes.R;


/**
 * A login screen that offers login via email/password.
 */
public class WelcomeActivity extends BaseActivity {


	// UI references.
	private EditText mNameView;
	private EditText mHeightView;
	private View mProgressView;
	private View mLoginFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		// Set up the login form.
		mNameView = (EditText) findViewById(R.id.name);

		mHeightView = (EditText) findViewById(R.id.height);


		mLoginFormView = findViewById(R.id.login_form);
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void next() {
		// Reset errors.
		mNameView.setError(null);
		mHeightView.setError(null);

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

		// Check for a valid height, if the user entered one.
		if (!isHeightValid(height)) {
			mHeightView.setError(getString(R.string.error_invalid_height));
			focusView = mHeightView;
			cancel = true;
		}


		if (cancel) {
			// There was an error;
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
		}
	}

	private boolean isHeightValid(String height) {
		float val = 0;
		try {
			val = Float.parseFloat(height);
		} catch (NumberFormatException e) {
			return false;
		}
		return !TextUtils.isEmpty(height) && val > 0 && val < 3;
	}


}

