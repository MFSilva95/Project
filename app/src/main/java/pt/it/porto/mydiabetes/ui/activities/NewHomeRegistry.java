package pt.it.porto.mydiabetes.ui.activities;

/**
 * Created by Diogo on 22/02/2017.
 */


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.LayoutInflater;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.Note;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.createMeal.activities.CreateMealActivity;
import pt.it.porto.mydiabetes.ui.createMeal.db.DataBaseHelper;
import pt.it.porto.mydiabetes.ui.createMeal.utils.LoggedMeal;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcView;
import pt.it.porto.mydiabetes.ui.fragments.new_register.CarbsRegister_Input_Interface;
import pt.it.porto.mydiabetes.ui.fragments.new_register.GlycaemiaRegister_Input_Interface;
import pt.it.porto.mydiabetes.ui.fragments.new_register.InsuRegister_Input_Interface;
import pt.it.porto.mydiabetes.ui.fragments.new_register.NoteRegister_Input_Interface;
import pt.it.porto.mydiabetes.ui.listAdapters.StringSpinnerAdapter;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.HomeElement;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

import static pt.it.porto.mydiabetes.utils.DateUtils.ISO8601_FORMAT_SECONDS;

public class NewHomeRegistry extends BaseActivity{

    private String TAG = "newREG";

    private static final String CALCS_OPEN = "calcs open";
    private static final String GENERATED_IMAGE_URI = "generated_image_uri";

    private static final String CARBS = "CARBS";
    private static final String INSULIN = "INSULIN";
    private static final String GLICAEMIA = "GLICAEMIA";
    private static final String NOTE = "NOTE";
    private static final String PLUS = "PLUS";

    private static final String ARG_CARBS = "ARG_CARBS";
    private static final String ARG_INSULIN = "ARG_INSULIN";
    private static final String ARG_BLOOD_GLUCOSE = "ARG_BLOOD_GLUCOSE";
    private static final String ARG_NOTE = "ARG_NOTE";
    private static final String ARG_TAG_INDEX = "ARG_TAG_INDEX";
    private static final String ARG_RECORD_ID = "ARG_RECORD_ID";

    private static final String ARG_IS_RECORD_UPDATE = "ARG_IS_RECORD_UPDATE";
    private static final String ARG_BUTTONS_DELETE_LIST = "ARG_BUTTONS_DELETE_LIST";
    private static final String ARG_BUTTONS_UPDATE_LIST = "ARG_BUTTONS_UPDATE_LIST";
    private static final String ARG_CALENDAR = "ARG_CALENDAR";

    private static final int REQUEST_TAKE_PHOTO = 1;
    //private static final int IMAGE_CAPTURE = 2;
    private static final int REQUEST_CREATE_MEAL = 2;
    private static final int IMAGE_VIEW = 3;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static final int BLOOD_GLUCOSE = 102;

    private LinearLayout bottomSheetViewgroup;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout contentLayout;

    private CarbsRegister_Input_Interface carbsRegisterInputInterface;
    private InsuRegister_Input_Interface insuRegisterInputInterface;
    private GlycaemiaRegister_Input_Interface glycaemiaRegisterInputInterface;
    private NoteRegister_Input_Interface noteRegisterInputInterface;

    @Nullable
    private GlycemiaRec glycemiaData;
    @Nullable
    private CarbsRec carbsData;
    @Nullable
    private InsulinRec insulinData;
    @Nullable
    private Note noteData;


    private ArrayList<String> buttons;
    //private ArrayList<String> buttonsUpdate;
    private ArrayList<String> delete_buttons;


    private Calendar registerDate;
    private TextView registerDateTextV;
    private TextView registerTimeTextV;

    protected InsulinCalcView fragmentInsulinCalcsFragment;
    protected InsulinCalculator insulinCalculator = null;




    private Uri generatedImageUri;
    private Uri imgUri;
    private int recordId=-1;

    private int noteId;
    private String mCurrentPhotoPath;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private Spinner spinner;
    private ArrayList<Tag> t;

    private LoggedMeal mCurrentMeal = null;

    int iRatio;
    int cRatio;


    private boolean isRecordUpdate = false;



    @Override
    public void finishAfterTransition() {
        contentLayout.setAlpha(0);
        super.finishAfterTransition();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CREATE_MEAL && resultCode == RESULT_OK){
            if(data.hasExtra("meal")){
                mCurrentMeal = data.getExtras().getParcelable("meal");
                carbsRegisterInputInterface.setImage(mCurrentMeal.getThumbnailPath());
                //carbsRegisterInputInterface.setImage();
                carbsRegisterInputInterface.setCarbsMealID(mCurrentMeal.getId());
                carbsRegisterInputInterface.setMealCarbs(mCurrentMeal.getTotalCarbs(true));
            }

        }
        if(requestCode == BLOOD_GLUCOSE && resultCode == RESULT_OK){
            glycaemiaRegisterInputInterface.updateObjective();
        }


        else if (requestCode == IMAGE_VIEW) {
            //se tivermos apagado a foto dá result code -1
            //se voltarmos por um return por exemplo o resultcode é 0
            if (resultCode == -1) {
                //imageRemoved();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            hideBottomSheet();
        } else {
            goBack();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /**
         * save current record state for recovery
         */
        //outState.putParcelable(GENERATED_IMAGE_URI, generatedImageUri);
        String date = DateUtils.getFormattedDate(registerDate)+" "+DateUtils.getFormattedTimeSec(registerDate); //save the currente record date and time
        save_current_input_data();//save the state of each one of the record windows

        outState.putBoolean(ARG_IS_RECORD_UPDATE, isRecordUpdate);
        //outState.putStringArrayList(ARG_BUTTONS_UPDATE_LIST, buttonsUpdate);
        outState.putStringArrayList(ARG_BUTTONS_DELETE_LIST, delete_buttons);
        outState.putString(ARG_CALENDAR, date);

        spinner = (Spinner) findViewById(R.id.tag_spinner);
        int idTag = (spinner.getSelectedItemPosition());//rdb.Tag_GetIdByName(tag);

        outState.putParcelable(ARG_BLOOD_GLUCOSE, glycemiaData);
        outState.putParcelable(ARG_INSULIN, insulinData);
        outState.putParcelable(ARG_CARBS, carbsData);
        outState.putParcelable(ARG_NOTE, noteData);
        outState.putInt(ARG_TAG_INDEX, idTag);
        outState.putInt(ARG_RECORD_ID, recordId);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //logSave("NewHomeRegistry");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState != null && savedInstanceState.containsKey(GENERATED_IMAGE_URI)) {
//            generatedImageUri = savedInstanceState.getParcelable(GENERATED_IMAGE_URI);
//        }
        /*if (savedInstanceState != null && savedInstanceState.getBoolean(CALCS_OPEN, false)) {
            ImageButton calcInsulinInfo = ((ImageButton) findViewById(R.id.bt_insulin_calc_info));
            if (calcInsulinInfo != null) {
                calcInsulinInfo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_info_outline_grey_900_24dp));
            }
            fragmentInsulinCalcsFragment = new InsulinCalcFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_calcs, fragmentInsulinCalcsFragment).commit();
            getFragmentManager().executePendingTransactions();
            this.fragmentInsulinCalcsFragment = (InsulinCalcFragment) getFragmentManager().findFragmentById(R.id.fragment_calcs);
            showCalcs();
        }*/
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_vars();
        init_listeners();
        buttons.add(PLUS);
        setupBottomSheet();
        /*
         If register from old reg
         */
        Bundle args = getIntent().getExtras();
        if(args != null){
            fillParameters(args, true);
        }else{
            if(savedInstanceState!=null){
                fillParameters(savedInstanceState, false);
            }else{
                Calendar time = Calendar.getInstance();
                setDate(time);
                setTime(time);
            }
        }
    }
    private void init_vars(){
        setContentView(R.layout.activity_add_event);
        //FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(this));

        //useIOB = featuresDB.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD);
        contentLayout = findViewById(R.id.content_panel);
        registerDateTextV = findViewById(R.id.registryDate);
        registerTimeTextV = findViewById(R.id.registerTime);
        spinner = findViewById(R.id.tag_spinner);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        bottomSheetViewgroup = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        bottomSheetBehavior.setHideable(true);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        registerDate = Calendar.getInstance();

        DB_Read rdb = new DB_Read(this);
        t = rdb.Tag_GetAll();

        //Log.i(TAG, "init_vars: "+t.toString());
        //String[] allTags = new String[t.size()];
        iRatio = rdb.Sensitivity_GetCurrent(DateUtils.getFormattedTimeSec(registerDate));
        cRatio = rdb.Ratio_GetCurrent(DateUtils.getFormattedTimeSec(registerDate));
        //UserInfo obj = rdb.MyData_Read();
        //iRatio = obj.getInsulinRatio();
        //cRatio = obj.getCarbsRatio();
        rdb.close();

//        if (t != null) {
//            for (int x=0;x<t.size();x++) {
//                allTags[x] = t.get(x).getName();
//            }
//        }

        Resources res = getResources(); //assuming in an activity for example, otherwise you can provide a context.
        String[] allTags = res.getStringArray(R.array.daytimes);

        spinner.setAdapter(new StringSpinnerAdapter(this, allTags));
        updateTagSpinner();

        buttons = new ArrayList<>();
        //buttonsUpdate = new ArrayList<>();
        delete_buttons = new ArrayList<>();

        insulinCalculator = new InsulinCalculator(this,registerDate);
        imgUri = null;
        noteId = -1;

        carbsData = new CarbsRec();
        glycemiaData = new GlycemiaRec();
        insulinData = new InsulinRec();
        noteData = new Note();

        Pair<Integer, Integer> displayM = getDisplayMetrics();
        carbsRegisterInputInterface = new CarbsRegister_Input_Interface(this, new NewHomeRegCallImpl(),displayM.first, displayM.second);
        glycaemiaRegisterInputInterface = new GlycaemiaRegister_Input_Interface(this, registerDate, new NewHomeRegCallImpl());
        noteRegisterInputInterface = new NoteRegister_Input_Interface(this);
        bottomSheetViewgroup.findViewById(R.id.bs_notes).setEnabled(false);
    }

    private void save() {
        try{
            save_current_input_data();
            if(isRecordUpdate){
                update_record();
            }else{
                save_record();
            }
            //validateInfo_Save();
            finish();
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
    }
    private void verify_note_conditions()throws Exception{
        if(buttons.contains(NOTE)){
            if(buttons.size()<=2){
                noteRegisterInputInterface.setErrorMessage(getString(R.string.error_note));
                throw new Exception();
            }
        }
    }
    private void verify_carbs_conditions()throws Exception{
        if(buttons.contains(CARBS)){
            if(!carbsRegisterInputInterface.validate()){
                carbsRegisterInputInterface.setErrorMessage(getString(R.string.error_carbs));
                throw new Exception();}
        }
    }
    private void verify_glucose_conditions()throws Exception{
        if(buttons.contains(GLICAEMIA)){
            if(!glycaemiaRegisterInputInterface.validate()){
                glycaemiaRegisterInputInterface.setErrorMessage(getString(R.string.error_glucose));
                throw new Exception();
            }
        }
    }
    private void verify_insulin_conditions()throws Exception{
        if(buttons.contains(INSULIN)){
            if(!insuRegisterInputInterface.validate()){
                insuRegisterInputInterface.setErrorMessage(getString(R.string.error_insulin));
                throw new Exception();
            }
        }
    }

    private void update_record () throws Exception{

        spinner = findViewById(R.id.tag_spinner);
        int idTag = (spinner.getSelectedItemPosition());

        DB_Read rdb = new DB_Read(this);
        int idUser = rdb.getUserId();
        DB_Write reg = new DB_Write(this);

        verify_note_conditions();
        verify_carbs_conditions();
        verify_glucose_conditions();
        verify_insulin_conditions();

        if(delete_buttons.contains(NOTE)){
            reg.Note_Delete(noteData.getId());
            noteData = null;
        }
        if(delete_buttons.contains(CARBS)){
            reg.Carbs_Delete(carbsData.getId());
            carbsData = null;
        }
        if(delete_buttons.contains(GLICAEMIA)){
            reg.Glycemia_Delete(glycemiaData.getId());
            glycemiaData = null;
        }
        if(delete_buttons.contains(INSULIN)){
            reg.Insulin_Delete(insulinData.getId());
            insulinData = null;
        }

        reg.Record_Update(recordId,idUser, registerDate, idTag);

        DataBaseHelper dbHelper = new DataBaseHelper(this);

        for(String field:buttons){
            try {
                switch (field){
                    case CARBS:
                        if(carbsData != null){
                            carbsData.setIdTag(idTag);
                            carbsData.setIdUser(idUser);
                            carbsData.setDateTime(registerDate);
                            if(mCurrentMeal != null){
                                if((mCurrentMeal.getItemList().size() != 0) || (mCurrentMeal.getThumbnailPath() != null)){
                                    mCurrentMeal.setRegistered(true);
                                    mCurrentMeal.setExtraCarbs(carbsRegisterInputInterface.getCarbs() - mCurrentMeal.getTotalCarbs(false));

                                    if (mCurrentMeal.getId() != -1) {
                                        dbHelper.updateMeal(mCurrentMeal);
                                    }
                                    else {
                                        mCurrentMeal.setId(dbHelper.insertMeal(mCurrentMeal));
                                    }

                                    carbsData.setMealId(mCurrentMeal.getId());
                                } else{
                                    if (mCurrentMeal.getId() != -1) {
                                        LoggedMeal prev_meal = dbHelper.getMeal(mCurrentMeal.getId());
                                        prev_meal.setRegistered(false);
                                        dbHelper.updateMeal(prev_meal);
                                        carbsData.setMealId(-1);
                                    }
                                }
                            }

                            if(noteData != null) {
                                carbsData.setIdNote(noteData.getId());
                            }else{
                                carbsData.setIdNote(-1);
                            }

                            if (carbsData.getId()!=-1) {
                                reg.Carbs_Update(carbsData);
                            } else {
                                carbsData.setId(reg.Carbs_Save(carbsData));
                            }
                            reg.Record_Update_Carbs(recordId, carbsData.getId());
                        }
                        break;
                    case GLICAEMIA:
                        if(glycemiaData != null){
                            glycemiaData.setIdTag(idTag);
                            glycemiaData.setIdUser(idUser);
                            glycemiaData.setDateTime(registerDate);

                            if(noteData != null) {
                                glycemiaData.setIdNote(noteData.getId());
                            }else{
                                glycemiaData.setIdNote(-1);
                            }
                            if (glycemiaData.getId()!=-1) {
                                reg.Glycemia_Update(glycemiaData);
                            } else {
                                glycemiaData.setId(reg.Glycemia_Save(glycemiaData));
                            }
                            reg.Record_Update_Glycaemia(recordId, glycemiaData.getId());
                        }
                        break;
                    case INSULIN:
                        if(insulinData != null){
                            insulinData.setIdTag(idTag);
                            insulinData.setIdUser(idUser);
                            insulinData.setDateTime(registerDate);

                            if(noteData != null) {
                                insulinData.setIdNote(noteData.getId());
                            }else{
                                insulinData.setIdNote(-1);
                            }
                            if(insulinData.getId()!=-1){
                                reg.Insulin_Update(insulinData);
                            }else{
                                insulinData.setId(reg.Insulin_Save(insulinData));
                            }
                            reg.Record_Update_Insulin(recordId,insulinData.getId());
                        }
                        break;
                    case NOTE:
                        if (noteData != null) {
                            if(noteData.getId()!=-1){
                                reg.Note_Update(noteData);
                            }else{
                                noteData.setId(reg.Note_Add(noteData));
                            }
                            reg.Record_Update_Note(recordId,noteData.getId());
                        }
                        break;
                }


            }catch (Exception e){
                throw e;
            }
        }
        //reg.Record_Update(recordId,idUser, registerDate, idTag); carbsData.getUserId(),insulinData.getUserId(),glycemiaData.getUserId(),noteData.getUserId());

        //Log.i(TAG, "validateInfo_Save: BEGIN");
        //BadgeUtils.addLogBadge(getBaseContext(), rdb, reg);
        //BadgeUtils.addDailyBadge(getBaseContext(), rdb, reg);
        //LevelsPointsUtils.addPoints(getBaseContext(), LevelsPointsUtils.RECORD_POINTS, "log", rdb);
        setResult(Home.CHANGES_OCCURRED, this.getIntent());
        //Log.i(TAG, "validateInfo_Save: END");
        rdb.close();
        reg.close();
    }
    private void save_record () throws Exception{
        spinner = findViewById(R.id.tag_spinner);
        int idTag = (spinner.getSelectedItemPosition()+1);

        DB_Read rdb = new DB_Read(this);
        int idUser = rdb.getUserId();
        DB_Write reg = new DB_Write(this);

        verify_note_conditions();
        verify_carbs_conditions();
        verify_glucose_conditions();
        verify_insulin_conditions();

        recordId = reg.Record_Add(idUser, registerDate, idTag);

        DataBaseHelper dbHelper = new DataBaseHelper(this);

        if(delete_buttons.contains(NOTE)){
            reg.Note_Delete(noteData.getId());
            noteData = null;
        }
        if(delete_buttons.contains(CARBS)){
            reg.Carbs_Delete(carbsData.getId());
            carbsData = null;
        }
        if(delete_buttons.contains(GLICAEMIA)){
            reg.Glycemia_Delete(glycemiaData.getId());
            glycemiaData = null;
        }
        if(delete_buttons.contains(INSULIN)){
            reg.Insulin_Delete(insulinData.getId());
            insulinData = null;
        }

        for(String field:buttons){
            try {
                switch (field){
                    case CARBS:
                        if(carbsData != null){
                            carbsData.setIdTag(idTag);
                            carbsData.setIdUser(idUser);
                            carbsData.setDateTime(registerDate);
                            if(mCurrentMeal != null){
                                if((mCurrentMeal.getItemList().size() != 0) || (mCurrentMeal.getThumbnailPath() != null)){
                                    mCurrentMeal.setRegistered(true);
                                    mCurrentMeal.setExtraCarbs(carbsRegisterInputInterface.getCarbs() - mCurrentMeal.getTotalCarbs(false));

                                    if (mCurrentMeal.getId() != -1) {
                                        dbHelper.updateMeal(mCurrentMeal);
                                    }
                                    else {
                                        mCurrentMeal.setId(dbHelper.insertMeal(mCurrentMeal));
                                    }

                                    carbsData.setMealId(mCurrentMeal.getId());
                                } else{
                                    if (mCurrentMeal.getId() != -1) {
                                        LoggedMeal prev_meal = dbHelper.getMeal(mCurrentMeal.getId());
                                        prev_meal.setRegistered(false);
                                        dbHelper.updateMeal(prev_meal);
                                        carbsData.setMealId(-1);
                                    }
                                }
                            }

                            if(noteData != null) {
                                carbsData.setIdNote(noteData.getId());
                            }else{
                                carbsData.setIdNote(-1);
                            }
                            carbsData.setId(reg.Carbs_Save(carbsData));
                            reg.Record_Update_Carbs(recordId, carbsData.getId());
                        }
                        break;
                    case GLICAEMIA:
                        if(glycemiaData != null){
                            glycemiaData.setIdTag(idTag);
                            glycemiaData.setIdUser(idUser);
                            glycemiaData.setDateTime(registerDate);

                            if(noteData != null) {
                                glycemiaData.setIdNote(noteData.getId());
                            }else{
                                glycemiaData.setIdNote(-1);
                            }
                            glycemiaData.setId(reg.Glycemia_Save(glycemiaData));
                            reg.Record_Update_Glycaemia(recordId, glycemiaData.getId());
                        }
                        break;
                    case INSULIN:
                        if(insulinData != null){
                            insulinData.setIdTag(idTag);
                            insulinData.setIdUser(idUser);
                            insulinData.setDateTime(registerDate);

                            if(noteData != null) {
                                insulinData.setIdNote(noteData.getId());
                            }else{
                                insulinData.setIdNote(-1);
                            }
                            insulinData.setId(reg.Insulin_Save(insulinData));
                            reg.Record_Update_Insulin(recordId, insulinData.getId());
                        }
                        break;
                    case NOTE:
                        if (noteData != null) {
                            noteData.setId(reg.Note_Add(noteData));
                            reg.Record_Update_Note(recordId,noteData.getId());
                        }
                        break;
                }
            }catch (Exception e){
                Log.i(TAG, "save_record: "+e.getMessage());
                throw e;
            }
        }

        Log.i(TAG, "validateInfo_Save: BEGIN");
        BadgeUtils.addLogBadge(getBaseContext(), rdb, reg);
        BadgeUtils.addDailyBadge(getBaseContext(), rdb, reg);
        LevelsPointsUtils.addPoints(getBaseContext(), LevelsPointsUtils.RECORD_POINTS, "log", rdb);//bug here
        setResult(Home.CHANGES_OCCURRED, this.getIntent());
        Log.i(TAG, "validateInfo_Save: END");
        rdb.close();
        reg.close();
    }

    private void insertNoteMenu(){
        noteRegisterInputInterface = new NoteRegister_Input_Interface(this);
        addContent(noteRegisterInputInterface);
        buttons.add(0,NOTE);
    }
    private void insertGlicMenu(){
        /*Advice newAdvice = YapDroid.newInstance(v.getContext()).getSingleAdvice("Start", "",v.getContext());
                    if(newAdvice!=null){
                        addContent(R.layout.dialog_exp_advice);
                        setAdviceText();
                    }*/
        glycaemiaRegisterInputInterface = new GlycaemiaRegister_Input_Interface(this,registerDate, glycaemiaRegisterInputInterface.getCallBack());
        addContent(glycaemiaRegisterInputInterface);
        bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(true);
        buttons.add(0, GLICAEMIA);
        bottomSheetViewgroup.findViewById(R.id.bs_notes).setEnabled(true);
        requestKeyboard(glycaemiaRegisterInputInterface);
    }
    private void insertCarbsMenu(){
        //Advice advice1 = new Advice();
        //advice1.setSummaryText("You have recently exercised. You should compensate it with carbohydrates.");
        //advice1.setExpandedText("You have recently exercised. You should compensate it with carbohydrates.");
        //advice1.setUrgency(5);
        //advice1.setType("NORMAL");
        //Advice newAdvice = //YapDroid.newInstance(v.getContext()).getSingleAdvice("Start", "",v.getContext());
                    /*if(advice1!=null){
                        addContentAt(new AdviceRegister(this, advice1),0);
                        //setAdviceText();
                    }*/

        Pair<Integer,Integer> displayM = getDisplayMetrics();
        carbsRegisterInputInterface = new CarbsRegister_Input_Interface(this, carbsRegisterInputInterface.getCallBack(),displayM.first,displayM.second);
        addContent(carbsRegisterInputInterface);
        bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(true);
        buttons.add(0, CARBS);
        bottomSheetViewgroup.findViewById(R.id.bs_notes).setEnabled(true);
        requestKeyboard(carbsRegisterInputInterface);
    }
    private void insertInsulinMenu(boolean reqKey){
        /*Advice newAdvice = YapDroid.newInstance(v.getContext()).getSingleAdvice("Start", "",v.getContext());
                    if(newAdvice!=null){
                        addContent(R.layout.dialog_exp_advice);
                        setAdviceText();
                    }*/
        insuRegisterInputInterface = new InsuRegister_Input_Interface(this, iRatio, cRatio);
        addContent(insuRegisterInputInterface);
        insuRegisterInputInterface.updateInsuCalc(insulinCalculator, false);
        buttons.add(0, INSULIN);
        bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(true);
        bottomSheetViewgroup.findViewById(R.id.bs_notes).setEnabled(true);
        if(reqKey){requestKeyboard(insuRegisterInputInterface);}
    }

    private void setCarbsPressed(View v){
        insertCarbsMenu();
        v.getAnimation();
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(true);
            }
        }, 100L);
        hideBottomSheet();
        if(buttons.contains(GLICAEMIA) && !buttons.contains(INSULIN)){insertInsulinSuggestion(CARBS);}
    }
    private void setNotePressed(View v){
        if(buttons.size()>1){
            insertNoteMenu();
            v.getAnimation();
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomSheetViewgroup.findViewById(R.id.bs_notes).setPressed(true);
                }
            }, 100L);
            hideBottomSheet();
        }
    }
    private void setGlicPressed(View v){
        insertGlicMenu();
        v.getAnimation();
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(true);
            }
        }, 100L);
        hideBottomSheet();
        if(buttons.contains(CARBS) && !buttons.contains(INSULIN)){insertInsulinSuggestion(GLICAEMIA);}
    }
    private void setInsuPressed(View v){
        insertInsulinMenu(true);
        v.getAnimation();
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(true);
            }
        }, 100L);
        hideBottomSheet();
    }

    private void insertInsulinSuggestion(String field){

        if(!buttons.contains(INSULIN)){
            if(isRecordUpdate){
                delete_buttons.remove(INSULIN);
            }
            insertInsulinMenu(false);
            bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(true);
            hideBottomSheet();
            insuRegisterInputInterface.updateInsuCalc(insulinCalculator,false);
            if(field.equals(CARBS)){
                carbsRegisterInputInterface.requestCarbsFocus();
            }else{
                glycaemiaRegisterInputInterface.requestGlicFocus();
            }
        }
    }
    private void setupBottomSheet() {

        Button button = (Button) bottomSheetViewgroup.findViewById(R.id.bs_notes);
        if(buttons.size()==0){
            button.setEnabled(false);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logSave("NewHomeRegistry:Notes");
                if (buttons.contains(NOTE)) {
                    if(isRecordUpdate){
                        delete_buttons.add(NOTE);
                    }
                    removeContent(noteRegisterInputInterface);
                    buttons.remove(NOTE);
                    bottomSheetViewgroup.findViewById(R.id.bs_notes).setPressed(false);
                } else {
                    if(isRecordUpdate){
                        delete_buttons.remove(NOTE);
                    }
                    setNotePressed(v);
                }
            }
        });

        //
        bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logSave("NewHomeRegistry:Glycaemia");
                if (buttons.contains(GLICAEMIA)) {
                    if(isRecordUpdate){
                        delete_buttons.add(GLICAEMIA);
                    }
                    removeContent(glycaemiaRegisterInputInterface);
                    buttons.remove(GLICAEMIA);
                    bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(false);
                } else {
                    if(isRecordUpdate){
                        delete_buttons.remove(GLICAEMIA);
                    }
                    setGlicPressed(v);
                }
            }
        });
        bottomSheetViewgroup.findViewById(R.id.bs_meal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logSave("NewHomeRegistry:Carbs");
                if (buttons.contains(CARBS)) {
                    if(isRecordUpdate){
                        delete_buttons.add(CARBS);
                    }
                    removeContent(carbsRegisterInputInterface);
                    buttons.remove(CARBS);
                    bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(false);
                } else {
                    if(isRecordUpdate){
                        delete_buttons.remove(CARBS);
                    }
                    setCarbsPressed(v);
                }
            }
        });

        bottomSheetViewgroup.findViewById(R.id.bs_insulin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logSave("NewHomeRegistry:Insulin");
                if (buttons.contains(INSULIN)) {
                    if(isRecordUpdate){
                        delete_buttons.add(INSULIN);
                    }
                    removeContent(insuRegisterInputInterface);
                    buttons.remove(INSULIN);
                    bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(false);
                } else {
                    if(isRecordUpdate){
                        delete_buttons.remove(INSULIN);
                    }
                    setInsuPressed(v);
                }
            }
        });
    }

    public void logSave (String activity) {
        DB_Read db = new DB_Read(getBaseContext());
        int idUser = db.getUserId();
        db.close();
        if(idUser != -1){
            DB_Write dbwrite = new DB_Write(getBaseContext());
            dbwrite.Log_Save(idUser,activity);
            dbwrite.close();
        }
    }


    public String getDate() {
        return registerDateTextV.getText().toString();
    }
    public String getTime() {
        return registerTimeTextV.getText().toString();
    }
//    private void setImgURI(Uri newUri){
//        carbsRegisterInputInterface.setUri(newUri);
//        //imgUri = newUri;
//    }
//    private void imageRemoved() {
//        setImgURI(null);
//    }

    private void fill_recover(Bundle args) throws Exception {

        DB_Read db_read = new DB_Read(this);

        if(args.containsKey(ARG_CALENDAR)){

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(ISO8601_FORMAT_SECONDS);
            try {
                cal.setTime(sdf.parse(args.getString(ARG_CALENDAR)));// all done
            } catch (ParseException e) {
                e.printStackTrace();
            }
            registerDate = cal;
            setDate(registerDate);
            setTime(registerDate);
        }

        if (args.containsKey(ARG_CARBS)) {
            carbsData = args.getParcelable(ARG_CARBS);
            if (carbsData != null) {
                if(carbsData.getCarbsValue()>0){
                    buttons.add(CARBS);
                    insertCarbsMenu();
                    carbsRegisterInputInterface.fill_parameters(carbsData);

                    setNoteId(carbsData.getIdNote());
                    if(carbsData.getDateTime()!=null){
                        registerDate = carbsData.getDateTime();
                    }
                }
            }
        }
        if (args.containsKey(ARG_BLOOD_GLUCOSE)) {
            glycemiaData = args.getParcelable(ARG_BLOOD_GLUCOSE);
            if (glycemiaData != null) {
                if(glycemiaData.getValue()>0){
                    buttons.add(GLICAEMIA);
                    insertGlicMenu();
                    glycaemiaRegisterInputInterface.fill_parameters(glycemiaData);

                    setNoteId(glycemiaData.getIdNote());
                    if(glycemiaData.getDateTime()!=null){
                        registerDate = glycemiaData.getDateTime();
                    }
                }
            }
        }
        if (args.containsKey(ARG_INSULIN)) {
            insulinData = args.getParcelable(ARG_INSULIN);
            if (insulinData != null) {
                if(insulinData.getInsulinUnits()>0){
                    buttons.add(INSULIN);
                    insertInsulinMenu(false);
                    insuRegisterInputInterface.fill_parameters(insulinData);
                    setNoteId(insulinData.getIdNote());
                    if(insulinData.getDateTime()!=null){
                        registerDate = insulinData.getDateTime();
                    }
                }
            }
        }
        if (args.containsKey(ARG_NOTE)) {
            noteData = args.getParcelable(ARG_NOTE);
            if(noteData!=null){
                if(!noteData.getNote().equals("")){
                    buttons.add(NOTE);
                    insertNoteMenu();
                    noteRegisterInputInterface.fill_parameters(noteData.getNote());
                }
            }
        }

        insulinCalculator = new InsulinCalculator(this, registerDate);
        insulinCalculator.setCarbs(carbsData != null ? carbsData.getCarbsValue() : 0);
        insulinCalculator.setGlycemia(glycemiaData != null ? glycemiaData.getValue() : 0);
        insulinCalculator.setGlycemiaTarget(insulinData != null ? insulinData.getTargetGlycemia() : 0);

        if (registerDate != null) {
            insulinCalculator.setTime(this, registerDate);
        }
        hideBottomSheet();
        hideKeyboard();
        db_read.close();
    }
    private void fill_update(Bundle args) throws Exception {


        DB_Read db_read = new DB_Read(this);

        if(args.containsKey(ARG_RECORD_ID)){
            recordId = args.getInt(ARG_RECORD_ID);
        }

        if (args.containsKey(ARG_CARBS)) {
            carbsData = args.getParcelable(ARG_CARBS);
            if (carbsData != null) {
                carbsData = db_read.CarboHydrate_GetById(carbsData.getId());
                if (carbsData != null) {
                    //buttonsUpdate.add(CARBS);
                    insertCarbsMenu();
                    carbsRegisterInputInterface.fill_parameters(carbsData);
                    String imgPath = carbsData.getPhotoPath();
                    setNoteId(carbsData.getIdNote());
                    registerDate = carbsData.getDateTime();
                    DataBaseHelper dbHelper = new DataBaseHelper(this);
                    mCurrentMeal = dbHelper.getLoggedMeal_by_id(carbsData.getMealId());
                }
            }
        }
        if (args.containsKey(ARG_BLOOD_GLUCOSE)) {
            glycemiaData = args.getParcelable(ARG_BLOOD_GLUCOSE);
            if (glycemiaData != null) {
                glycemiaData = db_read.Glycemia_GetById(glycemiaData.getId());
                if (glycemiaData != null) {
                    //buttonsUpdate.add(GLICAEMIA);
                    insertGlicMenu();
                    glycaemiaRegisterInputInterface.fill_parameters(glycemiaData);

                    setNoteId(glycemiaData.getIdNote());
                    registerDate = glycemiaData.getDateTime();
                }
            }
        }
        if (args.containsKey(ARG_INSULIN)) {
            insulinData = args.getParcelable(ARG_INSULIN);
            if (insulinData != null) {
                insulinData = db_read.InsulinReg_GetById(insulinData.getId());
                if (insulinData != null) {
                    //buttonsUpdate.add(INSULIN);
                    insertInsulinMenu(false);
                    insuRegisterInputInterface.updateInsuCalc(insulinCalculator,true);
                    insuRegisterInputInterface.fill_parameters(insulinData);
                    setNoteId(insulinData.getIdNote());
                    registerDate = insulinData.getDateTime();
                }
            }
        }
        if(args.containsKey("note_id")){
            noteId = args.getInt("note_id");
        }
        if (noteId != -1) {
            noteData = db_read.Note_GetById(noteId);
            if(noteData!=null){
                noteData.setId(noteId);//args.getParcelable(ARG_NOTE);
                insertNoteMenu();
                noteRegisterInputInterface.fill_parameters(noteData.getNote());
            }

        }
        db_read.close();
        setDate(registerDate);
        setTime(registerDate);

        insulinCalculator = new InsulinCalculator(this, registerDate);
        insulinCalculator.setCarbs(carbsData != null ? carbsData.getCarbsValue() : 0);
        insulinCalculator.setGlycemia(glycemiaData != null ? glycemiaData.getValue() : 0);
        insulinCalculator.setGlycemiaTarget(insulinData != null ? insulinData.getTargetGlycemia() : 0);

        if (registerDate != null) {
            insulinCalculator.setTime(this, registerDate);
        }
        hideBottomSheet();
        hideKeyboard();

        db_read.close();

    }

    private void fillParameters(Bundle args, boolean isUpdate){


        //CALLED UPON ROTATION

//        if (args.containsKey(ARG_BUTTONS_UPDATE_LIST)) {
//            this.buttonsUpdate = args.getStringArrayList(ARG_BUTTONS_UPDATE_LIST);
//        }
        isRecordUpdate = isUpdate;

        if (args.containsKey(ARG_IS_RECORD_UPDATE)){
            this.isRecordUpdate = args.getBoolean(ARG_IS_RECORD_UPDATE);
        }
        if (args.containsKey(ARG_BUTTONS_DELETE_LIST)) {
            this.delete_buttons = args.getStringArrayList(ARG_BUTTONS_DELETE_LIST);
        }
        if(args.containsKey("tag")){
            int index = ((StringSpinnerAdapter) spinner.getAdapter()).getItemPosition(args.getString("tag"));
            if(index>=0){
                spinner.setSelection(index);
            }
        }
        if(isUpdate){
            try {
                fill_update(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                fill_recover(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Nullable
    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        findViewById(R.id.toolbar).startActionMode(callback);
        return super.startActionMode(callback);
    }

///    @Override
////    protected void onPostResume() {
////        super.onPostResume();
////        if (sentToSettings) {
////            dispatchTakePictureIntent();
////        }
////    }




    //            if (ActivityCompat.checkSelfPermission(NewHomeRegistry.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) + ActivityCompat.checkSelfPermission(NewHomeRegistry.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    //Got Permission
//                    try {
//                        dispatchTakePictureIntent();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        dispatchTakePictureIntent();
//    }
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
            }
        }
    }


    private void dispatchTakePictureIntent() throws IOException {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Intent takePictureIntent = ImagePicker.cameraOnly().getIntent(this);

        //Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();

            // Continue only if the File was successfully created
            if (photoFile != null) {

                if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                    Uri photoURI = FileProvider.getUriForFile(NewHomeRegistry.this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }else{
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

//    private void dispatchTakePictureIntent() throws IOException {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                return;
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//
//                if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
//                    Uri photoURI = FileProvider.getUriForFile(NewHomeRegistry.this,
//                            BuildConfig.APPLICATION_ID + ".provider",
//                            photoFile);
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                }else{
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                }
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
//    }


    public static void copy19plus(String src_path, File dst) throws IOException {
        File src = new File(src_path);
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static void copyUnder19(String src_path, File dst) throws IOException {
        File src = new File(src_path);
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image;
        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            // Create an image file name
            File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
            if (!dir.exists()) {
                dir.mkdir();
            }
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    dir      /* directory */
            );
            mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        }else{
            //File file = new File(Environment.getExternalStorageDirectory()+ "/MyDiabetes", new Date().getTime() + ".jpg");
            image  = new File(Environment.getExternalStorageDirectory()+"/MyDiabetes", imageFileName+".jpg");
            mCurrentPhotoPath = image.getAbsolutePath();
        }
        return image;
    }

    public interface NewHomeRegCallBack{
        void checkPermissions();
        void addGlycaemiaObjective(Context c);
        void addCarbsImage(Context context, Uri thisImgUri);
        void updateInsulinCalc();
        void createCustomMeal(Context context);
    }
    class NewHomeRegCallImpl implements NewHomeRegCallBack{

        @Override
        public void checkPermissions() {
            //dispatchTakePictureIntent();


            if (ContextCompat.checkSelfPermission(NewHomeRegistry.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // if (ActivityCompat.shouldShowRequestPermissionRationale(NewHomeRegistry.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(NewHomeRegistry.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                //}
            } else {
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void addGlycaemiaObjective(Context c) {
            Intent intent = new Intent(c, TargetBG_detail.class);
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
            }//updateObjective()
            startActivityForResult(intent, BLOOD_GLUCOSE);
            //startActivity(intent);
        }

        @Override
        public void addCarbsImage(Context context, Uri thisImgUri) {
            if (thisImgUri != null) {
                Intent intent = new Intent(context, ViewPhoto.class);
                Bundle argsToPhoto = new Bundle();
                argsToPhoto.putString("Path", thisImgUri.getPath());
                argsToPhoto.putInt("Id", -1);
                intent.putExtras(argsToPhoto);
                startActivityForResult(intent, IMAGE_VIEW);
            }else {
                checkPermissions();
            }
        }

        @Override
        public void updateInsulinCalc() {
            insulinCalculator.setCarbs(carbsRegisterInputInterface !=null? carbsRegisterInputInterface.getCarbs():0);
            insulinCalculator.setGlycemia(glycaemiaRegisterInputInterface !=null? glycaemiaRegisterInputInterface.getGlycemia():0);
            insulinCalculator.setGlycemiaTarget(glycaemiaRegisterInputInterface !=null? glycaemiaRegisterInputInterface.getGlycemiaTarget():0);
            if(insuRegisterInputInterface !=null)
                insuRegisterInputInterface.updateInsuCalc(insulinCalculator,insuRegisterInputInterface.isManual());
        }
        @Override
        public void createCustomMeal(Context context) {
            Intent intent = new Intent(context, CreateMealActivity.class);

            if(mCurrentMeal != null) {
                intent.putExtra("meal_obj", mCurrentMeal);
            }

            intent.putExtra("reg_carbs", carbsRegisterInputInterface.getCarbs());
            startActivityForResult(intent, REQUEST_CREATE_MEAL);
        }
    }
    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    private void requestKeyboard(View view) {
        view.requestFocus();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }
    private void showBottomSheet() {
        hideKeyboard();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
    private void showTimePickerDialog(View v) {

        DialogFragment newFragment = TimePickerFragment.getTimePickerFragment(R.id.registerTime, DateUtils.getTimeCalendar(((TextView) v).getText().toString()));
        ((TimePickerFragment) newFragment).setListener(new TimePickerFragment.TimePickerChangeListener() {
            @Override
            public void onTimeSet(String time) {
                Calendar calendar = DateUtils.getTimeCalendar(time);
                if (calendar != null) {
                    setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),calendar.get(Calendar.MINUTE));
                    String timeString = DateUtils.getFormattedTime(registerDate);
                    registerTimeTextV.setText(timeString);
                    updateTagSpinner();
                    if(insuRegisterInputInterface !=null){
                        Log.i(TAG, "onTimeSet: "+DateUtils.getFormattedTime(registerDate));
                        insuRegisterInputInterface.updateRatioCalc(registerDate);
                    }
                    if(buttons.contains(GLICAEMIA)){
                        glycaemiaRegisterInputInterface.updateObjective();
                    }
                }
            }
        });
        newFragment.show(getFragmentManager(), "timePicker");
    }
    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = DatePickerFragment.getDatePickerFragment(
                R.id.registryDate,
                new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        setDate(year, month, day);
                    }
                }, DateUtils.getDateCalendar(((TextView) v).getText().toString()));
        newFragment.show(getFragmentManager(), "DatePicker");
    }
    private void setDate(Calendar c) {
        registerDate.set(Calendar.YEAR,c.get(Calendar.YEAR));
        registerDate.set(Calendar.MONTH,c.get(Calendar.MONTH));
        registerDate.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH));
        registerDateTextV.setText(DateUtils.getFormattedDate(registerDate));
    }
    private void setTime(Calendar c){
        registerDate.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
        registerDate.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
        registerDate.set(Calendar.SECOND, c.get(Calendar.SECOND));
        registerTimeTextV.setText(DateUtils.getFormattedTime(registerDate));
    }


    private void setDate(int year, int month, int day) {
        registerDate.set(Calendar.YEAR,year);
        registerDate.set(Calendar.MONTH,month);
        registerDate.set(Calendar.DAY_OF_MONTH,day);
        registerDateTextV.setText(DateUtils.getFormattedDate(registerDate));
    }
    private void setTime(int hour, int minute, int second){
        registerDate.set(Calendar.HOUR_OF_DAY, hour);
        registerDate.set(Calendar.MINUTE, minute);
        registerDate.set(Calendar.SECOND, second);

        StringBuilder displayTime = new StringBuilder(18);
        displayTime.append(registerDate.get(Calendar.HOUR_OF_DAY));
        displayTime.append(":");
        displayTime.append(registerDate.get(Calendar.MINUTE));
        registerTimeTextV.setText(displayTime);
    }
    private void addContent(LinearLayout view) {
        contentLayout.addView(view, contentLayout.getChildCount() - 1);//contentLayout.getChildCount() - 1);
        Button button = (Button) bottomSheetViewgroup.findViewById(R.id.bs_notes);
        button.setEnabled(true);
    }
    private void addContentAt(LinearLayout view, int pos) {
        contentLayout.addView(view, pos);//contentLayout.getChildCount() - 1);
    }
    private void addContentAt(int layout, int pos) {
        contentLayout.addView(LayoutInflater.from(this).inflate(layout, contentLayout, false), pos);//contentLayout.getChildCount() - 1);
    }
    private void removeContent(LinearLayout view) {
        contentLayout.removeView(view);
        Button button = (Button) bottomSheetViewgroup.findViewById(R.id.bs_notes);
        if(buttons.size()<=1){
            button.setEnabled(false);
        }
    }

    private void save_current_input_data(){
        int idTag = (spinner.getSelectedItemPosition());
//        Log.i(TAG, "save_current_input_data: -> -> "+idTag);

        if (glycaemiaRegisterInputInterface != null ) {
            glycemiaData  = glycaemiaRegisterInputInterface.save_read();
            glycemiaData.setIdTag(idTag);
        }
        if (insuRegisterInputInterface != null ) {
            insulinData = insuRegisterInputInterface.save_read();
            insulinData.setIdTag(idTag);
        }
        if (carbsRegisterInputInterface != null ) {
            carbsData = carbsRegisterInputInterface.save_read();
            carbsData.setIdTag(idTag);
        }
        if(noteRegisterInputInterface != null){
            noteData = noteRegisterInputInterface.save_read();
        }
    }

    private void updateTagSpinner() {
        Tag displayTag = getCurrentTag(t, registerDate);
        if(displayTag!=null){
            int index = displayTag.getId()-1;
            //int index = ((StringSpinnerAdapter) spinner.getAdapter()).getItemPosition(displayTag.getName());
            if(index>=0){
                spinner.setSelection(index);
//                Log.i(TAG, "save_current_input_data: -> -> "+index);
            }
        }
    }
    private Tag getCurrentTag(ArrayList<Tag> t, Calendar registerDate) {

        String timeString[] = DateUtils.getFormattedTime(registerDate).split(":");
        int currentTime = Integer.parseInt(timeString[0], 10) * 60 + Integer.parseInt(timeString[1]);

//        for(Tag tag: t){
        for(int index = 0;index<t.size();index++){
            Tag tag = t.get(index);
            Tag nextTag = t.get((index+1)%t.size());
            if(tag.getStart()!=null) {
                String[] temp_start = tag.getStart().split(":");
                String[] temp_end = nextTag.getStart().split(":");
                //String[] temp_end = tag.getEnd().split(":");
                int startTagTime = Integer.parseInt(temp_start[0], 10) * 60 + Integer.parseInt(temp_start[1]);
                int h = Integer.parseInt(temp_end[0],10);
                if(h==0){ h = 24;}
                int endTagTime = h * 60 + Integer.parseInt(temp_end[1]);

                if(currentTime >= startTagTime && currentTime <= endTagTime){
                    return tag;
                }
                //int endTagTime = Integer.parseInt(temp_end[0], 10) * 60 + Integer.parseInt(temp_end[1]);
//                if (timeOverlaps(currentTime, startTagTime, endTagTime)) {
//                    return tag;
//                }
            }
        }
        return null;
    }
    private void goBack(){

        save_current_input_data();
        if((carbsData != null && carbsData.getCarbsValue() > 0) || (insulinData != null && insulinData.getInsulinUnits() > 0) || (glycemiaData != null && glycemiaData.getValue() >  0)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(NewHomeRegistry.this);
            builder1.setTitle(getString(R.string.exit_dialog_title));
            builder1.setMessage(getString(R.string.exit_dialog_description));
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    getString(R.string.positiveButton),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });

            builder1.setNegativeButton(
                    getString(R.string.negativeButton),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }
        else{
            finish();
        }
    }
    private void init_listeners(){
        registerDateTextV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        registerTimeTextV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        (findViewById(R.id.bt_add_more_content)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED){
                    hideBottomSheet();
                }else{
                    hideKeyboard();
                    showBottomSheet();
                }
            }
        });
    }
    private void setNoteId(int noteId){
        if(noteId!=-1){
            this.noteId = noteId;
        }
    }
    private Pair<Integer, Integer> getDisplayMetrics(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (int) (displaymetrics.heightPixels * 0.1);
        int width = (int) (displaymetrics.widthPixels * 0.1);
        return new Pair<>(height,width);
    }

}