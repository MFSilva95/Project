package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
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
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
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
import pt.it.porto.mydiabetes.utils.AdviceAlertReceiver;
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


        adviceList = (RecyclerView) findViewById(R.id.AdviceHomeList);
        fillAdviceList(adviceList);

    }

    public void fillAdviceList(RecyclerView lv) {

        ArrayList<Advice> adviceList = new ArrayList<Advice>();

        //getAllYapAdvices -> put them into adviceList.

        /*for(RawAdvice advice:advicesFromYap){
            Advice newAdvice = new Advice(cenas do conselho);
            if(newAdvice.getType().equals(Advice.AdviceTypes.ALERT)){
                setupAlarm(advice);
            }
            adviceList.add(newAdvice);
        }*/

        String[] temp = {"AVISO IMPORTANTE LOL","Meal","10:s"};

        Advice myAdvice1 = new Advice("5 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "normal", temp, 9);
        Advice myAdvice2 = new Advice("1 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "ALERT", temp, 6);
        setupAlarm(myAdvice2);
        Advice myAdvice3 = new Advice("2 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "question", temp, 2);
        Advice myAdvice4 = new Advice("4 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "normal", temp, 4);
        Advice myAdvice5 = new Advice("3 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "alert", temp, 3);

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

    }

    private void setupAlarm(Advice currentAdvice) {

        AlarmManager alm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AdviceAlertReceiver.class);

        Bundle extras = new Bundle();
        extras.putString("RegistryClassName", currentAdvice.getRegistryType());
        extras.putString("NotificationText", currentAdvice.getNotificationText());
        intent.putExtras(extras);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        //alm.set(AlarmManager.RTC_WAKEUP, currentAdvice.getTime().getTimeInMillis(), alarmIntent);

        long timeTest = System.currentTimeMillis() + 5 * 1000;
        alm.set(AlarmManager.RTC_WAKEUP, timeTest, alarmIntent);
    }
}


