package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;


public class NoteRegister extends LinearLayout {
    public static final String ARG_INSULIN = "ARG_INSULIN";
    private TextInputLayout insulin_input;
    private InsulinRec insuData;
    private boolean isManual;
    private InsulinCalculator insulinCalculator;


    public NoteRegister(Context context) {
        super(context);
        init();
    }

    public NoteRegister(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteRegister(Context context, AttributeSet attrs, int defStyle) {
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
}