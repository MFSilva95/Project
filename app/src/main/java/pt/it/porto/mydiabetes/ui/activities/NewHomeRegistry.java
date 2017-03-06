package pt.it.porto.mydiabetes.ui.activities;

/**
 * Created by Diogo on 22/02/2017.
 */


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.TimePicker;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.listAdapters.StringSpinnerAdapter;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class NewHomeRegistry extends AppCompatActivity {

    private LinearLayout bottomSheetViewgroup;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout contentLayout;
    private ArrayList<String> buttons;
    private Calendar registerDate;
    private TextView registerDateTextV;
    private TextView registerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        bottomSheetViewgroup = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        contentLayout = (LinearLayout) findViewById(R.id.content_panel);
        registerDateTextV = (TextView) findViewById(R.id.registryDate);
        registerTime = (TextView) findViewById(R.id.registerTime);

        bottomSheetViewgroup.setVisibility(View.INVISIBLE);
        buttons = new ArrayList<>();
        bottomSheetViewgroup.setVisibility(View.VISIBLE);


        registerDateTextV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				showDatePickerDialog(view);
            }
		});

        Calendar time = Calendar.getInstance();
        String currentTime = android.text.format.DateFormat.getTimeFormat(this.getApplicationContext()).format(new java.util.Date() );

        registerTime.setText( currentTime);
        registerTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.registry_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        (findViewById(R.id.bt_add_more_content)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                showBottomSheet();
            }
        });
        ((Spinner) findViewById(R.id.tag)).setAdapter(new StringSpinnerAdapter(this, getResources().getStringArray(R.array.daytimes)));
        setupBottomSheet();

        setDate(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH));
    }

    public void showTimePickerDialog(View v) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                final Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, selectedHour);
                c.set(Calendar.MINUTE, selectedMinute);
                String timeString = DateUtils.getFormattedTime(c);
                registerTime.setText( timeString);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    public void showDatePickerDialog(View v) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                v.getContext(),
                R.style.style_date_picker_dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        setDate(year, month, day);
                    }
                },
                registerDate.get(Calendar.YEAR),
                registerDate.get(Calendar.MONTH),
                registerDate.get(Calendar.DAY_OF_MONTH)){

                @Override
                protected void onCreate(Bundle savedInstanceState)
                {
                    super.onCreate(savedInstanceState);
                    int year = getContext().getResources()
                        .getIdentifier("android:id/year", null, null);
                    if(year != 0){  View yearPicker = findViewById(year);
                        if(yearPicker != null){
                            yearPicker.setVisibility(View.GONE);
                        }
                    }
                }
            };
        datePickerDialog.show();
    }

    private void setDate(int year, int month, int day) {
        registerDate = new GregorianCalendar(year, month, day);
        StringBuilder displayDate = new StringBuilder(18);
        displayDate.append(registerDate.get(Calendar.DAY_OF_MONTH));
        displayDate.append(" ");
        displayDate.append(registerDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        registerDateTextV.setText(displayDate.toString());
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
        contentLayout.addView(LayoutInflater.from(this).inflate(layout, contentLayout, false), 0);//contentLayout.getChildCount() - 1);
    }

    private void removeContent(int child) {
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
        bottomSheetViewgroup.setVisibility(View.VISIBLE);
    }

    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetViewgroup.setVisibility(View.GONE);
    }

    private void save() {
        finish();
    }


    private void requestKeyboard(View view) {
        view.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }


    private void setupBottomSheet() {
        //
        bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains("glicemia")) {
                    removeContent(buttons.indexOf("glicemia"));
                    buttons.remove("glicemia");
                    bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(false);
                } else {
                    addContent(R.layout.glycemia_content_edit);
                    v.getAnimation();
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(true);
                        }
                    }, 100L);
                    buttons.add(0, "glicemia");
                    hideBottomSheet();
                }
            }
        });

        bottomSheetViewgroup.findViewById(R.id.bs_meal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains("meal")) {
                    removeContent(buttons.indexOf("meal"));
                    buttons.remove("meal");
                    bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(false);
                } else {
                    addContent(R.layout.meal_content_edit);
                    v.getAnimation();
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(true);
                        }
                    }, 100L);
                    buttons.add(0, "meal");
                    hideBottomSheet();
                }
            }
        });

        bottomSheetViewgroup.findViewById(R.id.bs_insulin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains("insulin")) {
                    removeContent(buttons.indexOf("insulin"));
                    buttons.remove("insulin");
                    bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(false);
                } else {
                    addContent(R.layout.insulin_content_edit);
                    v.getAnimation();
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(true);
                        }
                    }, 100L);
                    buttons.add(0, "insulin");
                    hideBottomSheet();
                }
            }
        });
    }
}
