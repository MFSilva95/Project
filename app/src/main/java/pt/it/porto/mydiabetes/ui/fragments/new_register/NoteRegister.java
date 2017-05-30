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
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;


public class NoteRegister extends LinearLayout {
    private TextInputLayout note_input;
    private Note note;


    public NoteRegister(Context context) {
        super(context);
        init();
    }

    public void setErrorMessage(String errorMessage){
        this.note_input.setError(errorMessage);
    }
    private void init() {
        inflate(getContext(), R.layout.note_content_edit, this);
        this.note = new Note();
        this.note_input = (TextInputLayout) findViewById(R.id.note_input_txt);
        note_input.getEditText().addTextChangedListener(getNoteTW());
    }
    public Note save_read(){
        return note;
    }
    public void fill_parameters(String text){
        note_input.getEditText().requestFocus();
        note_input.getEditText().setText(text);
    }
    private TextWatcher getNoteTW(){
        TextWatcher noteTW = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                note.setNote( editable.toString() );
            }
        };
        return noteTW;
    }
}