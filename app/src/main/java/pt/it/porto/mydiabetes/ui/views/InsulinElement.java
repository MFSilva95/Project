package pt.it.porto.mydiabetes.ui.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;


public class InsulinElement extends CardView {

	public static final int MODE_CREATE = 0;
	public static final int MODE_EDIT = 1;
	public static final int MODE_VIEW = 2;


	private LinearLayout layoutShow;
	private LinearLayout layoutEdit;
	private LinearLayout editButtons;
	private LinearLayout createButtons;
	private InsulinData data;
	private ElementChangesListener listener;
	private RadioGroup adminMethod;

	public InsulinElement(Context context) {
		super(context);
	}

	public InsulinElement(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InsulinElement(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@SuppressLint("WrongViewCast")
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.layoutEdit = (LinearLayout) findViewById(R.id.insulin_edit);
		this.layoutShow = (LinearLayout) findViewById(R.id.insulin_show);
		this.editButtons = (LinearLayout) layoutEdit.findViewById(R.id.buttons_edit);
		this.createButtons = (LinearLayout) layoutEdit.findViewById(R.id.buttons_create);
		setupOkButton((Button) layoutEdit.findViewById(R.id.buttons_create).findViewById(R.id.okButton));
		setupOkButton((Button) layoutEdit.findViewById(R.id.buttons_edit).findViewById(R.id.okButton));
		setupCancelButton((Button) layoutEdit.findViewById(R.id.buttons_edit).findViewById(R.id.cancelButton));
		setupRemoveButton((Button) layoutEdit.findViewById(R.id.buttons_create).findViewById(R.id.cancelButton));
		setupRemoveButton((Button) layoutEdit.findViewById(R.id.buttons_edit).findViewById(R.id.removeButton));

		layoutEdit.findViewById(R.id.name).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (!b) {
					data.name = ((EditText) view).getText().toString();
				}
			}
		});


		adminMethod = layoutEdit.findViewById(R.id.admininistration_method_insert);
		adminMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				// clears error state if needed


				if (adminMethod.getCheckedRadioButtonId() != -1)
				{
					((RadioButton) adminMethod.getChildAt(1)).setError(null);
					View selected = adminMethod.findViewById(adminMethod.getCheckedRadioButtonId());
					int pos = adminMethod.indexOfChild(selected);
					data.administrationMethod = pos+"";
				}

			}
		});



		((Spinner) layoutEdit.findViewById(R.id.insulin_type)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				data.action = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
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


	public void setData(InsulinData data) {
		this.data = data;
		setupUI();
		setDataOnUI();
		setErrors();
	}

	private void setErrors() {
		switch (data.error) {
			case InsulinData.ERROR_EMPTY_NAME:
				((TextView) layoutEdit.findViewById(R.id.name)).setError(getContext().getString(R.string.error_field_required));
				break;
			case InsulinData.ERROR_REPEATED_NAME:
				((TextView) layoutEdit.findViewById(R.id.name)).setError(getContext().getString(R.string.error_repeated_name));
				break;
			case InsulinData.ERROR_EMPTY_ADMINISTRATION_METHOD:

				//((TextView) layoutEdit.findViewById(R.id.admininistration_method)).setError(getContext().getString(R.string.error_field_required));
				((RadioButton) adminMethod.getChildAt(1)).setError(getContext().getString(R.string.error_field_required));

				break;
		}
	}

	@SuppressLint("WrongViewCast")
	private void setDataOnUI() {
		View currentView = null;
		if (data.visibilityState == MODE_EDIT || data.visibilityState == MODE_CREATE) {
			currentView = layoutEdit;
			((Spinner) currentView.findViewById(R.id.insulin_type)).setSelection(data.action);

			((RadioButton) adminMethod.getChildAt(1)).setError(null);

		} else {
			currentView = layoutShow;
			((TextView) currentView.findViewById(R.id.insulin_type)).setText(getInsulinActionType(data.action));
			RadioButton selected = (RadioButton) adminMethod.getChildAt(Integer.parseInt(data.administrationMethod));
			((TextView) currentView.findViewById(R.id.admininistration_method_show)).setText(selected.getText());
		}

		((TextView) currentView.findViewById(R.id.name)).setText(data.name);
		((TextView) currentView.findViewById(R.id.name)).setError(null);

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

	private String getInsulinActionType(int type) {
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
			data.name = ((TextView) layoutEdit.findViewById(R.id.name)).getText().toString();

			adminMethod = (RadioGroup) layoutEdit.findViewById(R.id.admininistration_method_insert);
			if (adminMethod.getCheckedRadioButtonId() != -1) {
				((RadioButton) adminMethod.getChildAt(1)).setError(null);
				View selected = adminMethod.findViewById(adminMethod.getCheckedRadioButtonId());
				int pos = adminMethod.indexOfChild(selected);
				data.administrationMethod = pos+"";
			}

			data.action = ((Spinner) layoutEdit.findViewById(R.id.insulin_type)).getSelectedItemPosition();
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
		void dataUpdated(InsulinData data);

		void viewRemoved(int pox);
	}
}