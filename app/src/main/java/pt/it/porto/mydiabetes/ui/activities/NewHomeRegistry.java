package pt.it.porto.mydiabetes.ui.activities;

/**
 * Created by Diogo on 22/02/2017.
 */


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


import java.util.ArrayList;
import java.util.Arrays;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.listAdapters.StringSpinnerAdapter;

public class NewHomeRegistry extends AppCompatActivity {

    LinearLayout bottomSheetViewgroup;
    BottomSheetBehavior bottomSheetBehavior;
    LinearLayout contentLayout;
    ArrayList<String> buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        bottomSheetViewgroup = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        contentLayout = (LinearLayout) findViewById(R.id.content_panel);
        bottomSheetViewgroup.setVisibility(View.INVISIBLE);
        buttons = new ArrayList<>();
        bottomSheetViewgroup.setVisibility(View.VISIBLE);

        /*((TextInputLayout) findViewById(R.id.insulin_obj)).getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do your stuff here
                    showBottomSheet();
                    hideKeyboard();
                }
                return false;
            }
        });*/

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.registry_toolbar);
        setSupportActionBar(toolbar);*/

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


    void setupBottomSheet() {
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
                    bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(false);
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
                    bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(false);
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
