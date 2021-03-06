package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Insulin;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcView;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;


public class InsuRegister_Input_Interface extends LinearLayout {
    public static final String ARG_INSULIN = "ARG_INSULIN";
    private TextInputLayout insulin_input;
    private InsulinRec insuData;
    private boolean isManual;
    private View insuInfo;
    private FrameLayout insuInfoContent;
    protected InsulinCalcView fragmentInsulinCalcsFragment;
    private boolean calcShowing;
    private boolean useIOB;
    private float iRatio;
    private float cRatio;
    private NewHomeRegistry.MealType typeMeal;
    private Spinner insu_spinner;
    private InsulinCalculator calc;
    private TextWatcher insuWatcher;
    private Insulin insulin;


    public boolean isManual(){  return isManual;
    }
    public InsuRegister_Input_Interface(Context context, float iRatio, float cRatio) {
        super(context);
        this.iRatio = iRatio;
        this.cRatio = cRatio;
        init();
    }
    public void setErrorMessage(String errorMessage){
        this.insulin_input.setError(errorMessage);
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

        fragmentInsulinCalcsFragment = new InsulinCalcView(getContext());
        useIOB = featuresDB.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD);
        calcShowing = false;
        insuData = new InsulinRec();
        insulin_input = (TextInputLayout) findViewById(R.id.insulin_admin);
        insuInfo = findViewById(R.id.bt_insulin_calc_info);
        insuInfoContent = (FrameLayout) findViewById(R.id.fragment_calcs);
        fillInsulinSpinner();
        setInsulinListeners();
        isManual = false;
    }

    public void updateInsulin(Insulin insu){
        this.insulin = insu;
    }

    public void updateInsuCalc(InsulinCalculator calculator, boolean manual){
        this.calc = calculator;
        this.fragmentInsulinCalcsFragment.setInsulinCalculator(calculator);
        float totalInsu = calculator.getInsulinTotalFloat(false, true);
        if(!manual){
            insertInsulinData(totalInsu);
            if(totalInsu>0){
                insuData.setInsulinUnits(totalInsu);
            }
        }
    }

    public void setTypeMeal(NewHomeRegistry.MealType type){
        this.typeMeal = type;
    }
    public void updateRatioCalc(Calendar c){
        this.calc.updateRatios(c);
        this.fragmentInsulinCalcsFragment.setInsulinCalculator(calc);

    }
    public void fill_parameters(InsulinRec rec){
        this.insuData = rec;
        insertInsulinData(rec.getInsulinUnits());
    }
    public boolean validate(){
        try{
            save_read();
            insuData.setInsulinUnits( Float.parseFloat(insulin_input.getEditText().getText().toString()));
        }catch (Exception e){
            insulin_input.setError(getContext().getString(R.string.error_glicInput));
            insulin_input.requestFocus();
            return false;
        }
        if(insuData.getInsulinUnits()>900 || insuData.getInsulinUnits()<0){
            insulin_input.setError(getContext().getString(R.string.error_glicInput));
            insulin_input.requestFocus();
            return false;
        }
        return true;
    }
    private TextWatcher getInsulinTW(){
        insuWatcher = new TextWatcher() {
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
        return insuWatcher;
    }
    public void setIsManual(boolean bool){
        this.isManual = bool;
    }

    private void updateCurrentInsulin(){
        Spinner insuSpin = findViewById(R.id.sp_MealDetail_Insulin);
        DB_Read read = new DB_Read(this.getContext());
        Insulin currInsu = read.Insulin_GetByName(insuSpin.getSelectedItem().toString());
        read.close();
        this.insulin = currInsu;
        if(this.insulin!=null){
            calc.updateInsuType(Integer.parseInt(currInsu.getType()));
            this.fragmentInsulinCalcsFragment.setInsulinCalculator(calc);
        }
    }

    private void fillInsulinSpinner() {
        insu_spinner = (Spinner) findViewById(R.id.sp_MealDetail_Insulin);
        insu_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCurrentInsulin();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            if (insu_spinner != null) {
                insu_spinner.setAdapter(adapter);
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
            logSave("Open:InsulinCalcView");
            calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_info_outline_grey_900_24dp));
            if(calc != null){
                updateInsuCalc(calc, true);
            }
        }

    }
    private void hideCalcs() {

        removeContent(fragmentInsulinCalcsFragment);
        calcShowing = false;

        ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
        if (calcInsulinInfo != null) {
            logSave("Close:InsulinCalcView");
            calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_information_outline_grey600_24dp));
        }

    }

    public void logSave (String activity) {
        DB_Read db = new DB_Read(getContext());
        int idUser = db.getUserId();
        db.close();
        if(idUser != -1){
            DB_Write dbwrite = new DB_Write(getContext());
            dbwrite.Log_Save(idUser,activity);
            dbwrite.close();
        }
    }


    private void insertInsulinData(float insulinUnits){

        insulin_input.setHintEnabled(true);
        TextView insuTxt = insulin_input.getEditText();
        insuTxt.removeTextChangedListener(insuWatcher);
        //insuTxt.requestFocus();
        insuTxt.setText(insulinUnits+"");
        setIsManual(false);
        insuTxt.addTextChangedListener(getInsulinTW());
        //insuTxt.requestFocus();
    }

    public InsulinRec save_read(){

        if(insu_spinner != null){
            if(insu_spinner.getSelectedItem() != null){
                String insulin = insu_spinner.getSelectedItem().toString();
                DB_Read rdb = new DB_Read(getContext());
                int idInsulin = rdb.Insulin_GetByName(insulin).getId();
                rdb.close();
                insuData.setIdInsulin(idInsulin);
            }
        }

        return insuData;
    }


}