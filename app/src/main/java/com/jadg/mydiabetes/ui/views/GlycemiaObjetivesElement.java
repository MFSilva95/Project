package com.jadg.mydiabetes.ui.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jadg.mydiabetes.R;

public class GlycemiaObjetivesElement extends CardView {

	public static final int MODE_CREATE = 0;
	public static final int MODE_EDIT = 1;
	public static final int MODE_VIEW = 2;


	private LinearLayout layoutShow;
	private LinearLayout layoutEdit;
	private LinearLayout editButtons;
	private LinearLayout createButtons;
	private GlycemiaObjectivesData data;
	private ElementChangesListener listener;

	public GlycemiaObjetivesElement(Context context) {
		super(context);
	}

	public GlycemiaObjetivesElement(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GlycemiaObjetivesElement(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@SuppressLint("WrongViewCast")
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}
		this.layoutEdit = (LinearLayout) findViewById(R.id.glycemia_objective_edit);
		this.layoutShow = (LinearLayout) findViewById(R.id.glycemia_objective_show);
		this.editButtons = (LinearLayout) layoutEdit.findViewById(R.id.buttons_edit);
		this.createButtons = (LinearLayout) layoutEdit.findViewById(R.id.buttons_create);
		setupOkButton((Button) layoutEdit.findViewById(R.id.buttons_create).findViewById(R.id.okButton));
		setupOkButton((Button) layoutEdit.findViewById(R.id.buttons_edit).findViewById(R.id.okButton));
		setupCancelButton((Button) layoutEdit.findViewById(R.id.buttons_edit).findViewById(R.id.cancelButton));
		setupRemoveButton((Button) layoutEdit.findViewById(R.id.buttons_create).findViewById(R.id.cancelButton));
		setupRemoveButton((Button) layoutEdit.findViewById(R.id.buttons_edit).findViewById(R.id.removeButton));

		((TextView) layoutEdit.findViewById(R.id.description)).setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					data.description = ((EditText) view).getText().toString();
				}
			}
		});

		((TextView) layoutEdit.findViewById(R.id.time_start)).setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					data.startTime = ((EditText) view).getText().toString();
				}
			}
		});
		((TextView) layoutEdit.findViewById(R.id.time_end)).setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					data.endTime = ((EditText) view).getText().toString();
				}
			}
		});
		((TextView) layoutEdit.findViewById(R.id.glycemia_objective)).setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					data.setObjective(((EditText) view).getText().toString());
				}
			}
		});

		((TextView) layoutEdit.findViewById(R.id.time_start)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker timePicker, int hour, int minute) {
						((TextView) layoutEdit.findViewById(R.id.time_start)).setText(String.format("%s:%s", String.valueOf(hour), String.valueOf(minute)));

					}
				}, 12, 0, true).show();
			}
		});

		((TextView) layoutEdit.findViewById(R.id.time_end)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker timePicker, int hour, int minute) {
						((TextView) layoutEdit.findViewById(R.id.time_end)).setText(String.format("%s:%s", String.valueOf(hour), String.valueOf(minute)));

					}
				}, 12, 0, true).show();
			}
		});
	}

	private void setupRemoveButton(Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				removeView();
			}
		});
	}

	private void setupCancelButton(Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setMode(MODE_VIEW);
			}
		});
	}


	public void setData(GlycemiaObjectivesData data) {
		this.data = data;
		setupUI();
		setDataOnUI();
		setErrors();
	}

	private void setErrors() {
		if (data.errors[GlycemiaObjectivesData.NO_ERROR]) {
			return;
		}
		if (data.errors[GlycemiaObjectivesData.ERROR_EMPTY_DESCRIPTION]) {
			((TextView) layoutEdit.findViewById(R.id.description)).setError(getContext().getString(R.string.error_field_required));
		}

		if (data.errors[GlycemiaObjectivesData.ERROR_EMPTY_DESCRIPTION]) {
		}
		if (data.errors[GlycemiaObjectivesData.ERROR_REPEATED_DESCRIPTION]) {
			((TextView) layoutEdit.findViewById(R.id.description)).setError(getContext().getString(R.string.error_repeated_description));
		}

		if (data.errors[GlycemiaObjectivesData.ERROR_EMPTY_OBJECTIVE]) {
			((TextView) layoutEdit.findViewById(R.id.glycemia_objective)).setError(getContext().getString(R.string.error_field_required));
		}

		if (data.errors[GlycemiaObjectivesData.ERROR_EMPTY_START_TIME]) {
			((TextView) layoutEdit.findViewById(R.id.time_start)).setError(getContext().getString(R.string.error_field_required));
		}

		if (data.errors[GlycemiaObjectivesData.ERROR_EMPTY_END_TIME]) {
			((TextView) layoutEdit.findViewById(R.id.time_end)).setError(getContext().getString(R.string.error_field_required));
		}

		if (data.errors[GlycemiaObjectivesData.ERROR_START_TIME_OVERLAPS]) {
			((TextView) layoutEdit.findViewById(R.id.time_start)).setError(getContext().getString(R.string.error_start_time_overlaps));
		}

		if (data.errors[GlycemiaObjectivesData.ERROR_END_TIME_OVERLAPS]) {
			((TextView) layoutEdit.findViewById(R.id.time_end)).setError(getContext().getString(R.string.error_end_time_overlaps));
		}

		if (data.errors[GlycemiaObjectivesData.ERROR_END_TIME_BEFORE_START_TIME]) {
			((TextView) layoutEdit.findViewById(R.id.time_end)).setError(getContext().getString(R.string.error_end_time_before_start_time));
		}
	}

	private void setDataOnUI() {
		View currentView = null;
		if (data.visibilityState == MODE_EDIT || data.visibilityState == MODE_CREATE) {
			currentView = layoutEdit;
			((TextView) currentView.findViewById(R.id.time_start)).setText(data.startTime);
			((TextView) currentView.findViewById(R.id.time_start)).setError(null);
			((TextView) currentView.findViewById(R.id.time_end)).setText(data.endTime);
			((TextView) currentView.findViewById(R.id.time_end)).setError(null);
		} else {
			currentView = layoutShow;
			((TextView) currentView.findViewById(R.id.time))
					.setText(String.format(getContext().getString(R.string.glycemia_objective_time_start_end), data.startTime, data.endTime));
		}
		((TextView) currentView.findViewById(R.id.description)).setText(data.description);
		((TextView) currentView.findViewById(R.id.description)).setError(null);
		((TextView) currentView.findViewById(R.id.glycemia_objective)).setText(data.getObjectiveAsString());
		((TextView) currentView.findViewById(R.id.glycemia_objective)).setError(null);
	}

	private void setupUI() {
		switch (data.visibilityState) {
			case MODE_VIEW:
				layoutShow.setVisibility(VISIBLE);
				layoutEdit.setVisibility(GONE);
				layoutShow.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						setMode(MODE_EDIT);
					}
				});
				break;
			case MODE_EDIT:
				layoutShow.setVisibility(INVISIBLE);
				layoutEdit.setVisibility(VISIBLE);
				layoutEdit.findViewById(R.id.buttons_edit).setVisibility(VISIBLE);
				layoutEdit.findViewById(R.id.buttons_create).setVisibility(GONE);
				break;
			case MODE_CREATE:
				layoutShow.setVisibility(INVISIBLE);
				layoutEdit.setVisibility(VISIBLE);
				layoutEdit.findViewById(R.id.buttons_edit).setVisibility(GONE);
				layoutEdit.findViewById(R.id.buttons_create).setVisibility(VISIBLE);
				break;
		}
		// reset alphas
		layoutShow.setAlpha(1);
		layoutEdit.setAlpha(1);
	}

	private String getFormatedTime(int type) {
		return getContext().getResources().getStringArray(R.array.insulin_action)[type];
	}

	private void setupOkButton(Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				saveData();
				if (data.isValid()) {
					setMode(MODE_VIEW);
				} else {
					setErrors();
				}
			}
		});
	}

	@SuppressLint("WrongViewCast")
	private void saveData() {
		if (data.visibilityState == MODE_EDIT || data.visibilityState == MODE_CREATE) {
			data.description = ((TextView) layoutEdit.findViewById(R.id.description)).getText().toString();
			data.startTime = ((TextView) layoutEdit.findViewById(R.id.time_start)).getText().toString();
			data.endTime = ((TextView) layoutEdit.findViewById(R.id.time_end)).getText().toString();
			data.setObjective(((TextView) layoutEdit.findViewById(R.id.glycemia_objective)).getText().toString());

			data.visibilityState = data.visibilityState != MODE_VIEW ? MODE_EDIT : MODE_VIEW;
			if (listener != null) {
				listener.dataUpdated(data);
			}
		}
	}

	private void cancelEdit() {

	}

	private void removeView() {

		if (listener != null) {
			listener.viewRemoved(data.pox);
		}
	}

	public void setMode(int mode) {
		if (mode != this.data.visibilityState) {
			this.data.visibilityState = mode;
			if (this.data.visibilityState == MODE_EDIT) {
				ObjectAnimator show = ObjectAnimator.ofFloat(layoutEdit, "alpha", 0, 1);
				show.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {
						layoutEdit.setVisibility(VISIBLE);
					}

					@Override
					public void onAnimationEnd(Animator animator) {

					}

					@Override
					public void onAnimationCancel(Animator animator) {

					}

					@Override
					public void onAnimationRepeat(Animator animator) {

					}
				});

				ObjectAnimator hide = ObjectAnimator.ofFloat(layoutShow, "alpha", 1, 0);
				hide.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {

					}

					@Override
					public void onAnimationEnd(Animator animator) {
						layoutShow.setVisibility(INVISIBLE);
						layoutEdit.findViewById(R.id.buttons_create).setVisibility(GONE);
						layoutEdit.findViewById(R.id.buttons_edit).setVisibility(VISIBLE);
					}

					@Override
					public void onAnimationCancel(Animator animator) {

					}

					@Override
					public void onAnimationRepeat(Animator animator) {

					}
				});
				AnimatorSet set = new AnimatorSet();
				set.play(show).with(hide);
				set.start();

			} else {
				ObjectAnimator show = ObjectAnimator.ofFloat(layoutShow, "alpha", 0, 1);
				show.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {
						layoutShow.setVisibility(VISIBLE);
					}

					@Override
					public void onAnimationEnd(Animator animator) {

					}

					@Override
					public void onAnimationCancel(Animator animator) {

					}

					@Override
					public void onAnimationRepeat(Animator animator) {

					}
				});

				ObjectAnimator hide = ObjectAnimator.ofFloat(layoutEdit, "alpha", 1, 0);
				hide.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {

					}

					@Override
					public void onAnimationEnd(Animator animator) {
						layoutEdit.setVisibility(GONE);
					}

					@Override
					public void onAnimationCancel(Animator animator) {

					}

					@Override
					public void onAnimationRepeat(Animator animator) {

					}
				});
				AnimatorSet set = new AnimatorSet();
				set.play(show).with(hide);
				set.start();

				layoutShow.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						setMode(MODE_EDIT);
					}
				});
			}
			setDataOnUI();
		}
	}


	public void setListener(ElementChangesListener listener) {
		this.listener = listener;
	}

	public interface ElementChangesListener {
		void dataUpdated(GlycemiaObjectivesData data);

		void viewRemoved(int pox);
	}
}
