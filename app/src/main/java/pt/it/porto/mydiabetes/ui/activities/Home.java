package pt.it.porto.mydiabetes.ui.activities;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.ListsDataDb;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.database.Usage;
import pt.it.porto.mydiabetes.middleHealth.myglucohealth.BluetoothChangesRegisterService;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.FeatureIOBDialog;
import pt.it.porto.mydiabetes.ui.dialogs.FeatureWebSyncDialog;
import pt.it.porto.mydiabetes.ui.listAdapters.AdviceAdapter;
import pt.it.porto.mydiabetes.ui.listAdapters.LogbookAdapter;
import pt.it.porto.mydiabetes.ui.usability.AdviceTouchHelper;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.SyncAlarm;


public class Home extends BaseOldActivity {

    private static final String TAG = "Home";


    ListView logbookList;
    RecyclerView adviceList;

    private EditText dateFrom;
    private EditText dateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

       // dateFrom = (EditText) findViewById(R.id.et_Logbook_DataFrom);
       // dateTo = (EditText) findViewById(R.id.et_Logbook_DataTo);

        //logbookList = (ListView) findViewById(R.id.LogbookActivityList);
        adviceList = (RecyclerView) findViewById(R.id.AdviceHomeList);

        //fillDates();
        fillAdviceList(adviceList);


       /* dateFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fillListView(logbookList);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dateTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fillListView(logbookList);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fillListView(logbookList);

        DB_Read read = new DB_Read(this);
        if (!read.MyData_HasData()) {
            ShowDialogAddData();
            read.close();
            return; // making sure no more code of the on create method is executed
        }
        read.close();

        */
        setupSyncAlarm();
        showNewFeatures();
        BluetoothChangesRegisterService.startService(this.getApplicationContext());

    }

    public void fillDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        if (dateFrom.getText().length() == 0) {
            dateFrom.setText(DateUtils.getFormattedDate(calendar));
        }

        calendar = Calendar.getInstance();
        if (dateTo.getText().length() == 0) {
            dateTo.setText(DateUtils.getFormattedDate(calendar));
        }
    }


    public void fillListView(ListView lv) {
        ListsDataDb db = new ListsDataDb(MyDiabetesStorage.getInstance(this));
        Cursor cursor = db.getLogbookList(dateFrom.getText().toString(), dateTo.getText().toString());
        lv.setAdapter(new LogbookAdapter(cursor, this));
    }

    public void fillAdviceList(RecyclerView lv) {

        ArrayList<Advice> adviceList = new ArrayList<Advice>();

        Advice myAdvice1 = new Advice("5 - Já fez exercicio hoje?","Exercicio fisico é fundamental para uma boa gestão da diabetes",5);
        Advice myAdvice2 = new Advice("1 - Já fez exercicio hoje?","Exercicio fisico é fundamental para uma boa gestão da diabetes",1);
        Advice myAdvice3 = new Advice("2 - Já fez exercicio hoje?","Exercicio fisico é fundamental para uma boa gestão da diabetes",2);
        Advice myAdvice4 = new Advice("4 - Já fez exercicio hoje?","Exercicio fisico é fundamental para uma boa gestão da diabetes",4);
        Advice myAdvice5 = new Advice("3 - Já fez exercicio hoje?","Exercicio fisico é fundamental para uma boa gestão da diabetes",3);

        adviceList.add(myAdvice1);
        adviceList.add(myAdvice2);
        adviceList.add(myAdvice3);
        adviceList.add(myAdvice4);
        adviceList.add(myAdvice5);

        Collections.sort(adviceList);

        AdviceAdapter adviceAdapter = new AdviceAdapter(adviceList, this);

        ItemTouchHelper.Callback callback = new AdviceTouchHelper(adviceAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(lv);
        lv.setAdapter(adviceAdapter);
        lv.setLayoutManager(new LinearLayoutManager(this));

        lv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView thisView = (TextView) v;
                System.out.println("CENAS!");
                Toast.makeText(v.getContext(),"cenas: "+thisView.getText(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showDatePickerDialogFrom(View v) {
        DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Logbook_DataFrom, DateUtils.getDateCalendar(((EditText) v).getText().toString()));
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    public void showDatePickerDialogTo(View v) {
        DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(R.id.et_Logbook_DataTo, DateUtils.getDateCalendar(((EditText) v).getText().toString()));
        newFragment.show(getFragmentManager(), "DatePicker");
    }


    private void setupSyncAlarm() {
        SharedPreferences preferences = pt.it.porto.mydiabetes.database.Preferences.getPreferences(this);
        Calendar calendar = Calendar.getInstance();
        AlarmManager alm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SyncAlarm.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        if (!preferences.contains(SyncAlarm.SYNC_ALARM_LAST_SYNC)) { // only sets it if needed
            Usage usage = new Usage(MyDiabetesStorage.getInstance(this));
            String date = usage.getOldestRegist();

            try {
                calendar.setTime(DateUtils.iso8601Format.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            calendar.roll(Calendar.DAY_OF_YEAR, 7);
            calendar.set(Calendar.HOUR_OF_DAY, 21); // Maybe change later?

            alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
        } else {
            calendar.setTimeInMillis(preferences.getLong(SyncAlarm.SYNC_ALARM_LAST_SYNC, System.currentTimeMillis()));
            calendar.roll(Calendar.DAY_OF_YEAR, 7);
            calendar.set(Calendar.HOUR_OF_DAY, 21); // Maybe change later?
            if (calendar.before(Calendar.getInstance())) {
                alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
            } else if (!preferences.contains(SyncAlarm.SYNC_ALARM_PREFERENCE)) {
                preferences.edit().putInt(SyncAlarm.SYNC_ALARM_PREFERENCE, 1).apply();
                alm.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);
            }
        }
    }


    public void ShowDialogAddData() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showNewFeatures() {
        if (BuildConfig.IOB_AVAILABLE && Preferences.showFeatureForFirstTime(this, FeaturesDB.FEATURE_INSULIN_ON_BOARD)) {
            FeatureIOBDialog dialog = new FeatureIOBDialog();
            dialog.setListener(new FeatureIOBDialog.ActivateFeatureDialogListener() {
                @Override
                public void useFeature() {
                    FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(getApplicationContext()));
                    featuresDB.changeFeatureStatus(FeaturesDB.FEATURE_INSULIN_ON_BOARD, true);
                }

                @Override
                public void notUseFeature() {
                    FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(getApplicationContext()));
                    featuresDB.changeFeatureStatus(FeaturesDB.FEATURE_INSULIN_ON_BOARD, false);
                }
            });
            dialog.show(getFragmentManager(), "newFeature");
        }
        if (BuildConfig.SYNC_AVAILABLE && Preferences.showFeatureForFirstTime(this, FeaturesDB.FEATURE_CLOUD_SYNC)) {
            FeatureWebSyncDialog dialog = new FeatureWebSyncDialog();
            dialog.show(getFragmentManager(), "newFeature_sync");
        }
    }
}


