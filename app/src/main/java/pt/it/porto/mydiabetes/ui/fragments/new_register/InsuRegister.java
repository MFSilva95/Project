package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcView;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;


public class InsuRegister extends LinearLayout {
    public static final String ARG_INSULIN = "ARG_INSULIN";
    private TextInputLayout insulin_input;
    private InsulinRec insuData;
    private boolean isManual;
    private InsulinCalculator insulinCalculator;
    private View insuInfo;
    private FrameLayout insuInfoContent;
    protected InsulinCalcView fragmentInsulinCalcsFragment;
    private boolean calcShowing;
    private boolean useIOB;
    private int iRatio;
    private int cRatio;



    public InsuRegister(Context context, int iRatio, int cRatio) {
        super(context);
        this.iRatio = iRatio;
        this.cRatio = cRatio;
        init();
    }

    public InsuRegister(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InsuRegister(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void addContent(LinearLayout view) {
        insuInfoContent.addView(view, 0);//contentLayout.getChildCount() - 1);
    }
    private void removeContent(LinearLayout view) {
        insuInfoContent.removeView(view);//contentLayout.getChildCount() - 1);
    }

    private void init() {
        FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(getContext()));
        inflate(getContext(), R.layout.insulin_content_edit, this);

        fragmentInsulinCalcsFragment = new InsulinCalcView(getContext(), iRatio, cRatio);
        useIOB = featuresDB.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD);
        calcShowing = false;
        insuData = new InsulinRec();
        isManual = false;
        insulinCalculator = new InsulinCalculator(getContext());
        insulin_input = (TextInputLayout) findViewById(R.id.insulin_admin);
        insuInfo = findViewById(R.id.bt_insulin_calc_info);
        insuInfoContent = (FrameLayout) findViewById(R.id.fragment_calcs);
        fillInsulinSpinner();
        setInsulinListeners();
    }
    public boolean canSave(){
        try{
            Integer.parseInt(insulin_input.getEditText().getText().toString());
        }catch (Exception e){
            insulin_input.setError(getContext().getString(R.string.glicInputError));
            insulin_input.requestFocus();
            return false;
        }
        return true;
    }
    public void fill_parameters(InsulinRec rec){
        this.insuData = rec;
        insertInsulinData(rec.getInsulinUnits());
    }
    private TextWatcher getInsulinTW(){
        TextWatcher ins = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setIsManual(true);
                TextInputLayout insulinInputLayout = (TextInputLayout) findViewById(R.id.insulin_admin);
                float insuValue;
                try{
                    String insuText = insulinInputLayout.getEditText().getText().toString();
                    insuValue = Float.parseFloat(insuText);
                }catch (Exception e){
                    insulinInputLayout.setError("");
                    return;
                }
                insuData.setInsulinUnits(insuValue);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
        return ins;
    }
    public void setIsManual(boolean bool){
        this.isManual = bool;
    }
    private void fillInsulinSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
        ArrayList<String> allInsulins = new ArrayList<>();
        DB_Read rdb = new DB_Read(getContext());
        HashMap<Integer, String> val = rdb.Insulin_GetAllNames();
        rdb.close();

        if (val != null) {
            for (int i : val.keySet()) {
                allInsulins.add(val.get(i));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allInsulins);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (spinner != null) {
                spinner.setAdapter(adapter);
            }
        }
    }
    private void setInsulinListeners(){

        if (insuInfo != null) {
            insuInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleInsulinCalcDetails(v);
                }
            });
        }
        insulin_input.getEditText().addTextChangedListener(getInsulinTW());

    }
    private void toggleInsulinCalcDetails(View view) {
        if (!isFragmentShowing()) {
            showCalcs();
        } else {
            hideCalcs();
        }
    }
    private boolean isFragmentShowing() {
        return calcShowing;
    }
    private void showCalcs() {

        addContent(fragmentInsulinCalcsFragment);
        calcShowing = true;
        ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
        if (calcInsulinInfo != null) {
            calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_info_outline_grey_900_24dp));
        }

        fragmentInsulinCalcsFragment.setCorrectionGlycemia(insulinCalculator.getInsulinGlycemia());
        fragmentInsulinCalcsFragment.setCorrectionCarbs(insulinCalculator.getInsulinCarbs());
        fragmentInsulinCalcsFragment.setResult(insulinCalculator.getInsulinTotal(useIOB), insulinCalculator.getInsulinTotal(useIOB, true));
        fragmentInsulinCalcsFragment.setInsulinOnBoard(insulinCalculator.getInsulinOnBoard());
        /*insulinCalculator.setListener(new InsulinCalculator.InsulinCalculatorListener() {
            @Override
            public void insulinOnBoardChanged(InsulinCalculator calculator) {
                if (fragmentInsulinCalcsFragment != null) {
                    showCalcs();
                }
            }
        });*/
    }
    private void hideCalcs() {

        removeContent(fragmentInsulinCalcsFragment);
        calcShowing = false;

        ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
        if (calcInsulinInfo != null) {
            calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_information_outline_grey600_24dp));
        }

    }
    private void insertInsulinData(float insulinUnits){

        TextView insuTxt = insulin_input.getEditText();
        insuTxt.removeTextChangedListener(getInsulinTW());
        insuTxt.requestFocus();
        insuTxt.setText(insulinUnits+"");
        insuTxt.addTextChangedListener(getInsulinTW());
    }

}