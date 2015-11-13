package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.listAdapters.ExerciseDataBinding;

/**
 * Created by Zé on 29/07/2015.
 */
public class ExercisesDetail extends Activity {
    int idExercises = 0;
    String exerciseName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_detail);
        // Show the Up button in the action bar.
        getActionBar();

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String id = args.getString("Id");
            idExercises = Integer.parseInt(id);
            exerciseName = args.getString("Name");

            EditText name = (EditText) findViewById(R.id.et_Exercises_Nome);
            name.setText(exerciseName);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        Bundle args = getIntent().getExtras();
        if (args != null) {
            inflater.inflate(R.menu.insulins_detail_edit, menu);
        } else {
            inflater.inflate(R.menu.insulins_detail, menu);
        }

        //getSupportMenuInflater().inflate(R.menu.tag_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menuItem_InsulinsDetail_Save:
                AddNewExercise();
                return true;
            case R.id.menuItem_InsulinsDetail_EditSave:
                UpdateExercise();
                return true;
            case R.id.menuItem_InsulinsDetail_Delete:
                DeleteExercise();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void AddNewExercise() {
        EditText exercisename = (EditText) findViewById(R.id.et_Exercises_Nome);
        //adicionado por zeornelas
        //obriga a colocar os valores
        if (exercisename.getText().toString().equals("")) {
            exercisename.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(exercisename, InputMethodManager.SHOW_IMPLICIT);
            return;
        }

        // deal with the editable
        DB_Write wdb = new DB_Write(this);

        wdb.Exercise_Add(exercisename.getText().toString());
        wdb.close();
        finish();
    }

    public void UpdateExercise(){
        EditText exercisename = (EditText) findViewById(R.id.et_Exercises_Nome);
        //adicionado por zeornelas
        //obriga a colocar os valores
        if (exercisename.getText().toString().equals("")) {
            exercisename.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(exercisename, InputMethodManager.SHOW_IMPLICIT);
            return;
        }

        DB_Write wdb = new DB_Write(this);

        ExerciseDataBinding exercise = new ExerciseDataBinding();

        exercise.setId(idExercises);
        exercise.setName(exercisename.getText().toString());


        wdb.Exercise_Update(exercise);
        wdb.close();
        finish();

    }

    public void DeleteExercise(){
        final Context c = this;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_Exercise))
                .setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Falta verificar se não está associada a nenhuma entrada da DB

                        DB_Write wdb = new DB_Write(c);
                        wdb.Exercise_Remove(idExercises);
                        wdb.close();
                        Log.d("to delete exercise id: ", String.valueOf(idExercises));
                        finish();

                    }
                })
                .setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
    }

    public void goUp(){
        //NavUtils.navigateUpFromSameTask(this);
        finish();
    }
}
