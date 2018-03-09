package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.DateTime;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.activities.TargetBG_detail;
import pt.it.porto.mydiabetes.ui.views.GlycemiaObjectivesData;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;


public class GlycaemiaRegister extends LinearLayout {
    private TextInputLayout glycaemia_input;
    private GlycemiaRec glycaemiaData;
    private TextInputLayout glycaemia_obj_input;
    private Calendar registerDate;
    private ImageButton plusButton;
    private ArrayList<GlycemiaObjectivesData> objList;

    public NewHomeRegistry.NewHomeRegCallBack getCallBack() {
        return callBack;
    }
    private NewHomeRegistry.NewHomeRegCallBack callBack;


    public GlycaemiaRegister(Context context, Calendar calendar, NewHomeRegistry.NewHomeRegCallBack call) {
        super(context);
        registerDate = calendar;
        callBack = call;
        init();
    }

    private void init() {
        glycaemiaData = new GlycemiaRec();
        inflate(getContext(), R.layout.glycemia_content_edit, this);
        this.glycaemia_input = (TextInputLayout) findViewById(R.id.glycemia_txt);
        this.glycaemia_obj_input = (TextInputLayout) findViewById(R.id.glycemia_obj);
        plusButton = (ImageButton) findViewById(R.id.insert_new_glic_objective);
        setGlycemiaListeners();
        requestFocus();
    }
    public void fill_parameters(GlycemiaRec glyrec){

        this.glycaemiaData = glyrec;
        insertGlicData(glyrec.getValue(),glyrec.getBG_target());
    }

    public boolean validate(){
        try{
            glycaemiaData.setValue( Integer.parseInt(glycaemia_input.getEditText().getText().toString()));
        }catch (Exception e){
            glycaemia_input.setError(getContext().getString(R.string.glicInputError));
            glycaemia_input.requestFocus();
            return false;
        }
        try{
            glycaemiaData.setObjective( Integer.parseInt(glycaemia_obj_input.getEditText().getText().toString()));
        }catch (Exception e){
            glycaemia_obj_input.setError(getContext().getString(R.string.glicInputError));
            glycaemia_obj_input.requestFocus();
            return false;
        }
        if(glycaemiaData.getValue()>900 || glycaemiaData.getValue()<30){
            glycaemia_input.setError(getContext().getString(R.string.glicInputError));
            glycaemia_input.requestFocus();
            return false;
        }
        /*if(glycaemiaData.getBG_target()>900 || glycaemiaData.getBG_target()<30){
            glycaemia_obj_input.setError(getContext().getString(R.string.glicInputError));
            glycaemia_obj_input.requestFocus();
            return false;
        }*/
        return true;
    }

    private void insertGlicData(int glicValue, int glicObjValue){

        TextView glicTxt = glycaemia_input.getEditText();
        glicTxt.requestFocus();
        glicTxt.setText(glicValue+"");
        //glicTxt.addTextChangedListener(getGlicTW());

        if(glicObjValue!=-1){
            TextView glicObjTxt = glycaemia_obj_input.getEditText();
            glicObjTxt.requestFocus();
            glicObjTxt.setText(glicObjValue+"");
            //glicObjTxt.addTextChangedListener(getGlicObjTW());
        }
    }
    private void insertGlicObjData(int glicObjValue){
        TextView glicObjTxt = glycaemia_obj_input.getEditText();
        glicObjTxt.requestFocus();
        if(glicObjValue!=-1) {
            glicObjTxt.setText(glicObjValue + "");
        }
        //glicObjTxt.addTextChangedListener(getGlicObjTW());
    }
    public void requestGlicFocus(){
        glycaemia_input.requestFocus();
    }
    public void setGlycemiaListeners(){
        glycaemia_input.getEditText().addTextChangedListener(getGlicTW());
        glycaemia_obj_input.getEditText().addTextChangedListener(getGlicObjTW());
        updateObjective();
    }

    public int GlicObjTimesOverlap(ArrayList<GlycemiaObjectivesData> objs, String currentTime){

        String[] temp;
        temp = currentTime.split(":");
        int startTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);

        for(GlycemiaObjectivesData objData: objs){
            temp = objData.getStartTime().split(":");
            int startTime2 = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
            temp = objData.getEndTime().split(":");
            int endTime2 = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);
            if(CheckOverlap(startTime2, endTime2, startTime)){return objData.getObjective();}
        }
        return -1;
    }

    public boolean CheckOverlap(int s0, int e0, int t){
        if ( e0 < s0){
            if(t < s0){
                t = t +1440;
            }
            e0 = e0 + 1440;
        }

        if(s0 < t && t < e0) {
            return true;
        }else{
            return false;
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
                        glycaemiaData.setObjective(-1);
                    }
                    callBack.updateInsulinCalc();
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

    public GlycemiaRec save_read(){
        return glycaemiaData;
    }

    public void updateObjective() {
        MyDiabetesStorage storage = MyDiabetesStorage.getInstance(getContext());
        try {
            objList = storage.getGlycemiaObjectives();
        } catch (Exception e) {
            e.printStackTrace();
            plusButton.setVisibility(View.VISIBLE);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addGlycemiaObjective();
                }
            });
        }
        int objective;
        objective = GlicObjTimesOverlap(objList, DateUtils.getFormattedTime(registerDate));

        if(objective == -1){
            plusButton.setVisibility(View.VISIBLE);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addGlycemiaObjective();
                }
            });
        }else{
            insertGlicObjData(objective);
            plusButton.setVisibility(View.INVISIBLE);
        }
    }
}