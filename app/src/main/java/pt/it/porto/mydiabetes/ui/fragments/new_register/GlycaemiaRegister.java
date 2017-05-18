package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.activities.TargetBG_detail;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;


public class GlycaemiaRegister extends LinearLayout {
    public static final String ARG_BLOOD_GLUCOSE = "ARG_BLOOD_GLUCOSE";
    private TextInputLayout glycaemia_input;
    private GlycemiaRec glycaemiaData;
    private TextInputLayout glycaemia_obj_input;
    private Calendar registerDate;
    private NewHomeRegistry.NewHomeRegCallBack callBack;


    public GlycaemiaRegister(Context context, Calendar calendar, NewHomeRegistry.NewHomeRegCallBack call) {
        super(context);
        init();
        registerDate = calendar;
        callBack = call;
    }

    public GlycaemiaRegister(Context context, AttributeSet attrs, Calendar calendar, NewHomeRegistry.NewHomeRegCallBack call) {
        super(context, attrs);
        init();
        registerDate = calendar;
        callBack = call;
    }

    public GlycaemiaRegister(Context context, AttributeSet attrs, int defStyle, Calendar calendar, NewHomeRegistry.NewHomeRegCallBack call) {
        super(context, attrs, defStyle);
        init();
        registerDate = calendar;
        callBack = call;
    }

    private void init() {
        glycaemiaData = new GlycemiaRec();
        inflate(getContext(), R.layout.glycemia_content_edit, this);
        this.glycaemia_input = (TextInputLayout) findViewById(R.id.glycemia_txt);
        this.glycaemia_obj_input = (TextInputLayout) findViewById(R.id.glycemia_obj);
        setGlycemiaListeners();
    }
    public boolean canSave(){
        try{
            Integer.parseInt(glycaemia_input.getEditText().getText().toString());
        }catch (Exception e){
            glycaemia_input.setError(getContext().getString(R.string.glicInputError));
            glycaemia_input.requestFocus();
            return false;
        }
        try{
            Integer.parseInt(glycaemia_obj_input.getEditText().getText().toString());
        }catch (Exception e){
            glycaemia_obj_input.setError(getContext().getString(R.string.glicInputError));
            glycaemia_obj_input.requestFocus();
            return false;
        }
        return true;
    }
    public void fill_parameters(GlycemiaRec glyrec){

        this.glycaemiaData = glyrec;
        insertGlicData(glyrec.getValue(),glyrec.getBG_target());
    }

    private void insertGlicData(int glicValue, int glicObjValue){

        TextView glicTxt = glycaemia_input.getEditText();
        glicTxt.requestFocus();
        glicTxt.setText(glicValue+"");
        //glicTxt.addTextChangedListener(getGlicTW());

        TextView glicObjTxt = glycaemia_obj_input.getEditText();
        glicObjTxt.requestFocus();
        glicObjTxt.setText(glicObjValue+"");
        //glicObjTxt.addTextChangedListener(getGlicObjTW());
    }
    private void insertGlicObjData(int glicObjValue){
        TextView glicObjTxt = glycaemia_obj_input.getEditText();
        glicObjTxt.requestFocus();
        glicObjTxt.setText(glicObjValue+"");
        //glicObjTxt.addTextChangedListener(getGlicObjTW());
    }
    public void setGlycemiaListeners(){

        //String time = registerTimeTextV.getText().toString();
        MyDiabetesStorage storage = MyDiabetesStorage.getInstance(getContext());

        glycaemia_input.getEditText().addTextChangedListener(getGlicTW());
        glycaemia_obj_input.getEditText().addTextChangedListener(getGlicObjTW());
        int objective;
        try {
            objective = storage.getGlycemiaObjectives(DateUtils.getFormattedTime(registerDate));
            insertGlicObjData(objective);
        } catch (Exception e) {
            e.printStackTrace();
            ImageButton plusButton = (ImageButton) findViewById(R.id.insert_new_glic_objective);
            plusButton.setVisibility(View.VISIBLE);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addGlycemiaObjective();
                }
            });
        }
    }
    private void addGlycemiaObjective() {
        callBack.addGlycaemiaObjective(getContext());
    }

    private TextWatcher getGlicTW(){
        TextWatcher glicTW = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextInputLayout glycValueT = (TextInputLayout) findViewById(R.id.glycemia_txt);
                glycValueT.setError("");
                String glycString = editable.toString();
                if(glycString != null){
                    try{
                        int glycValue = Integer.parseInt(glycString);
                        glycaemiaData.setValue(glycValue);
                    }catch (NumberFormatException e){
                        glycaemiaData.setValue(0);
                    }
                    callBack.updateInsulinCalc();
                }
            }
        };
        return glicTW;
    }
    private TextWatcher getGlicObjTW(){
        TextWatcher objTW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String glycObjString = editable.toString();
                TextInputLayout glycObjT = (TextInputLayout) findViewById(R.id.glycemia_obj);
                glycObjT.setError("");
                if(glycObjString != null){
                    try{
                        int glycObjValue = Integer.parseInt(glycObjString);
                        glycaemiaData.setObjective(glycObjValue);
                    }catch (NumberFormatException e){
                        glycaemiaData.setObjective(0);
                    }
                    callBack.updateInsulinCalc();
                    //refreshCalcs();
                }
            }
        };
        return objTW;
    }

    public int getGlycemia() {
        return glycaemiaData.getValue();
    }

    public int getGlycemiaTarget() {
        return glycaemiaData.getBG_target();
    }
}