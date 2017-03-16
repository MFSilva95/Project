package pt.it.porto.mydiabetes.ui.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.adapters.StringSpinnerAdapter;


public class AddEvent extends AppCompatActivity {

	LinearLayout bottomSheetViewgroup;
	BottomSheetBehavior bottomSheetBehavior;
	LinearLayout contentLayout;
	boolean[] isShowing = new boolean[4];

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		bottomSheetViewgroup = (LinearLayout) findViewById(R.id.bottom_sheet);
		bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
		contentLayout = (LinearLayout) findViewById(R.id.content_panel);
		bottomSheetViewgroup.setVisibility(View.INVISIBLE);

		Arrays.fill(isShowing, false);

		Slide enterAnimation = new Slide(Gravity.TOP);
		enterAnimation.addListener(new Transition.TransitionListener() {
			@Override
			public void onTransitionStart(Transition transition) {

			}

			@Override
			public void onTransitionEnd(Transition transition) {
//				requestKeyboard(findViewById(R.id.glycemia_txt));
				bottomSheetViewgroup.setVisibility(View.VISIBLE);
				addContent(R.layout.glycemia_content_edit);
				isShowing[0] = true;
				requestKeyboard(findViewById(R.id.glycemia_txt));
				((TextInputLayout) findViewById(R.id.glycemia_obj)).getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							// do your stuff here
							showBottomSheet();
							hideKeyboard();
						}
						return false;
					}
				});
			}

			@Override
			public void onTransitionCancel(Transition transition) {

			}

			@Override
			public void onTransitionPause(Transition transition) {

			}

			@Override
			public void onTransitionResume(Transition transition) {

			}
		});
		getWindow().setEnterTransition(enterAnimation);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				save();
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		((ImageButton) findViewById(R.id.bt_add_more_content)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				showBottomSheet();
			}
		});


		((Spinner) findViewById(R.id.tag)).setAdapter(new StringSpinnerAdapter(this, getResources().getStringArray(R.array.daytimes)));

		setupBottomSheet();
	}

	@Override
	public void finishAfterTransition() {
		contentLayout.setAlpha(0);
		super.finishAfterTransition();
	}

	@Override
	public void supportFinishAfterTransition() {
		super.supportFinishAfterTransition();
	}

	private void addContent(int layout) {
		TransitionManager.beginDelayedTransition(contentLayout/*, new Slide(Gravity.BOTTOM)*/);
		contentLayout.addView(LayoutInflater.from(this).inflate(layout, contentLayout, false), contentLayout.getChildCount() - 1);


	}

	private void removeContent(int child) {
		TransitionManager.beginDelayedTransition(contentLayout/*, new Slide(Gravity.BOTTOM)*/);
		contentLayout.removeViewAt(child);
	}

	private void hideKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	@Override
	public void onBackPressed() {
		if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
			hideBottomSheet();
		} else {
			super.onBackPressed();
		}
	}

	private void showBottomSheet() {
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

//		view.setBackgroundResource(backgroundResource);
	}

	private void hideBottomSheet() {
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}

	private void save() {
		finish();
	}


	private void requestKeyboard(View view) {
		view.requestFocus();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
//		imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}


	void setupBottomSheet() {
		bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(true);
		bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isShowing[0]) {
					removeContent(0);
				} else {
					addContent(R.layout.glycemia_content_edit);
					v.getAnimation();
					v.postDelayed(new Runnable() {
						@Override
						public void run() {
							bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(true);
						}
					}, 100L);
				}
				isShowing[0] = !isShowing[0];
			}
		});

		bottomSheetViewgroup.findViewById(R.id.bs_meal).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isShowing[1]) {
					removeContent(1);
				} else {
					addContent(R.layout.meal_content_edit);
					v.getAnimation();
					v.postDelayed(new Runnable() {
						@Override
						public void run() {
							bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(true);
						}
					}, 100L);
				}
				isShowing[1] = !isShowing[1];
			}
		});
	}
}
