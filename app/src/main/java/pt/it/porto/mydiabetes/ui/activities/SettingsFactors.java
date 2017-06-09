package pt.it.porto.mydiabetes.ui.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import info.abdolahi.CircularMusicProgressBar;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.register.PersonalDataFragment;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class SettingsFactors extends BaseActivity {

    private UserInfo myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_factors);
        // Show the Up button in the action bar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputIsValid()) {
                    DB_Write rdb = new DB_Write(getBaseContext());
                    rdb.MyData_Save(getMyDataFromActivity());
                    rdb.close();
                    finish();
                } else {
                    //toast message
                    Toast.makeText(getBaseContext(), getString(R.string.mydata_before_saving), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Read MyData From DB
        DB_Read db_read = new DB_Read(this);
        myData = db_read.MyData_Read();
        setMyDataFromDB(myData);
        db_read.close();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //corrige erro ao gravar
    // os spinners não são verificados porque incialmente têm sempre valor
    public boolean inputIsValid() {
        EditText[] obj = new EditText[4];
        obj[0] = (EditText) findViewById(R.id.et_MyData_InsulinRatio);
        obj[1] = (EditText) findViewById(R.id.et_MyData_CarbsRatio);
        obj[2] = (EditText) findViewById(R.id.et_MyData_LowerRange);
        obj[3] = (EditText) findViewById(R.id.et_MyData_HigherRange);

        for (EditText aux : obj) {
            if (aux.getText().toString().trim().length() == 0) {
                aux.setError(getString(R.string.error_field_required));
                return false;
            }
        }
        return true;
    }

    public UserInfo getMyDataFromActivity() {
        EditText iRatio = (EditText) findViewById(R.id.et_MyData_InsulinRatio);
        EditText cRatio = (EditText) findViewById(R.id.et_MyData_CarbsRatio);
        EditText lRange = (EditText) findViewById(R.id.et_MyData_LowerRange);
        EditText hRange = (EditText) findViewById(R.id.et_MyData_HigherRange);

        myData.setInsulinRatio(Integer.parseInt(iRatio.getText().toString()));
        myData.setCarbsRatio(Integer.parseInt(cRatio.getText().toString()));
        myData.setLowerRange(Integer.parseInt(lRange.getText().toString()));
        myData.setHigherRange(Integer.parseInt(hRange.getText().toString()));


        return myData;
    }

    public void setMyDataFromDB(UserInfo obj) {
        if (obj != null) {
            EditText iRatio = (EditText) findViewById(R.id.et_MyData_InsulinRatio);
            EditText cRatio = (EditText) findViewById(R.id.et_MyData_CarbsRatio);
            EditText lRange = (EditText) findViewById(R.id.et_MyData_LowerRange);
            EditText hRange = (EditText) findViewById(R.id.et_MyData_HigherRange);

            iRatio.setText(String.format(LocaleUtils.MY_LOCALE, "%d", obj.getInsulinRatio()));
            cRatio.setText(String.format(LocaleUtils.MY_LOCALE, "%d", obj.getCarbsRatio()));
            lRange.setText(String.format(LocaleUtils.MY_LOCALE, "%d", obj.getLowerRange()));
            hRange.setText(String.format(LocaleUtils.MY_LOCALE, "%d", obj.getHigherRange()));
        }
    }


}
