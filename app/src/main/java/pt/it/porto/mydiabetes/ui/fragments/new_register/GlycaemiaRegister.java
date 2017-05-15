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

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.TargetBG_detail;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;


public class GlycaemiaRegister extends LinearLayout {
    public static final String ARG_INSULIN = "ARG_INSULIN";
    private TextInputLayout insulin_input;
    private InsulinRec insuData;
    private boolean isManual;
    private InsulinCalculator insulinCalculator;


    public GlycaemiaRegister(Context context) {
        super(context);
        init();
    }

    public GlycaemiaRegister(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GlycaemiaRegister(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        insuData = new InsulinRec();
        isManual = false;
        insulinCalculator = new InsulinCalculator(getContext());
        inflate(getContext(), R.layout.insulin_content_edit, this);
        this.insulin_input = (TextInputLayout) findViewById(R.id.insulin_admin);
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
    public void fill_parameters(Bundle savedIns){

        //image_button.
    }

    public void setGlycemiaListeners(){

        //String time = registerTimeTextV.getText().toString();
        MyDiabetesStorage storage = MyDiabetesStorage.getInstance(this);

        TextInputLayout glycValueT = (TextInputLayout) findViewById(R.id.glycemia_txt);
        glycValueT.getEditText().addTextChangedListener(getGlicTW());

        int objective = 0;
        TextInputLayout glycObjT = (TextInputLayout) findViewById(R.id.glycemia_obj);
        glycObjT.getEditText().addTextChangedListener(getGlicObjTW());

        try {
            objective = storage.getGlycemiaObjectives(DateUtils.getFormattedTime(registerDate));
            glycObjT.getEditText().setText(objective+"");
            insulinCalculator.setGlycemiaTarget(objective);
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
        Intent intent = new Intent(this, TargetBG_detail.class);
        EditText targetGlycemia = ((TextInputLayout) findViewById(R.id.glycemia_obj)).getEditText();
        String goal = null;
        if (targetGlycemia != null) {
            goal = targetGlycemia.getText().toString();
        }
        if (!TextUtils.isEmpty(goal)) {
            float target = Float.parseFloat(goal);
            Bundle bundle = new Bundle();
            bundle.putFloat(TargetBG_detail.BUNDLE_GOAL, target);
            intent.putExtras(bundle);
        }
        startActivity(intent);
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
                        insulinCalculator.setGlycemia(glycValue);
                        glycemiaData.setValue(glycValue);
                    }catch (NumberFormatException e){
                        // glycValueT.setError(R.string.glicInputError);
                        insulinCalculator.setGlycemia(0);
                        glycemiaData.setValue(0);
                    }
                    refreshCalcs();
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
                        insulinCalculator.setGlycemiaTarget(glycObjValue);
                        glycemiaData.setObjective(glycObjValue);
                    }catch (NumberFormatException e){
                        insulinCalculator.setGlycemiaTarget(0);
                        glycemiaData.setObjective(0);
                    }
                    refreshCalcs();
                }
            }
        };
        return objTW;
    }
}