package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.views.GlycemiaObjectivesData;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class GlycaemiaRegister_Input_Interface extends LinearLayout {
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


    public GlycaemiaRegister_Input_Interface(Context context, Calendar calendar, NewHomeRegistry.NewHomeRegCallBack call) {
        super(context);
        registerDate = calendar;
        callBack = call;
        init();
    }
    public void setErrorMessage(String errorMessage){
        this.glycaemia_input.setError(errorMessage);
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
            glycaemia_input.setError(getContext().getString(R.string.error_glicInput));
            glycaemia_input.requestFocus();
            return false;
        }
        try{
            glycaemiaData.setObjective( Integer.parseInt(glycaemia_obj_input.getEditText().getText().toString()));
        }catch (Exception e){
            glycaemia_obj_input.setError(getContext().getString(R.string.error_glicInput));
            glycaemia_obj_input.requestFocus();
            return false;
        }
        if(glycaemiaData.getValue()>900 || glycaemiaData.getValue()<30){
            glycaemia_input.setError(getContext().getString(R.string.error_glicInput));
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

    /**
     * Find out the current objective, given the current time
     *
     * @param objs
     * @param currentTime
     * @return
     */
    public int GlicObjTimesOverlap(ArrayList<GlycemiaObjectivesData> objs, String currentTime){

        String[] temp;
        temp = currentTime.split(":");
        int current_startTime = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);

        for(int i=0;i<objs.size();i++){
            GlycemiaObjectivesData objData = objs.get(i);
            int next_index = (i+1)%objs.size();
            GlycemiaObjectivesData next_objData = objs.get(next_index);
            temp = objData.getStartTime().split(":");

            int startTime_objective = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);

            temp = next_objData.getStartTime().split(":");
            int endTime_objective = Integer.parseInt(temp[0], 10) * 60 + Integer.parseInt(temp[1]);


            if(current_startTime >= startTime_objective && current_startTime <= endTime_objective){
                return objData.getObjective();
            }
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