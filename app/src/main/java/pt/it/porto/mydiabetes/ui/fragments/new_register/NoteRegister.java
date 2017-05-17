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
    public static final String ARG_NOTE = "ARG_NOTE";
    private TextInputLayout note_input;


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
        inflate(getContext(), R.layout.note_content_edit, this);
        this.note_input = (TextInputLayout) findViewById(R.id.note_input_txt);
    }
    public boolean canSave(){
        return true;
    }
    public void fill_parameters(String text){
        note_input.getEditText().requestFocus();
        note_input.getEditText().setText(text);
    }
}