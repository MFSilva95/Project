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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import android.util.Log;
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
import java.io.IOException;
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
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.FeaturesDB;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.dialogs.DatePickerFragment;
import pt.it.porto.mydiabetes.ui.dialogs.TimePickerFragment;
import pt.it.porto.mydiabetes.ui.fragments.InsulinCalcView;
import pt.it.porto.mydiabetes.ui.fragments.new_register.CarbsRegister;
import pt.it.porto.mydiabetes.ui.fragments.new_register.GlycaemiaRegister;
import pt.it.porto.mydiabetes.ui.fragments.new_register.InsuRegister;
import pt.it.porto.mydiabetes.ui.fragments.new_register.NoteRegister;
import pt.it.porto.mydiabetes.ui.listAdapters.StringSpinnerAdapter;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.InsulinCalculator;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

import static pt.it.porto.mydiabetes.utils.DateUtils.ISO8601_FORMAT_SECONDS;

public class NewHomeRegistry extends AppCompatActivity{

    private String TAG = "newREG";

    private CarbsRegister carbsRegister;
    private InsuRegister insuRegister;
    private GlycaemiaRegister glycaemiaRegister;
    private NoteRegister noteRegister;


    private LinearLayout bottomSheetViewgroup;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout contentLayout;
    private ArrayList<String> buttons;
    private ArrayList<String> buttonsUpdate;
    private ArrayList<String> delete_buttons;
    private Boolean insulinManual = false;
    private Boolean getIsManual(){
        return insulinManual;
    }
    private void setIsManual(boolean b){
        insulinManual = b;
    }
    private static final int REQUEST_TAKE_PHOTO = 1;
    private Calendar registerDate;
    private TextView registerDateTextV;
    private TextView registerTimeTextV;
    protected InsulinCalcView fragmentInsulinCalcsFragment;
    protected InsulinCalculator insulinCalculator = null;
    private boolean useIOB = true;

    public final static int IMAGE_CAPTURE = 2;
    public final static int IMAGE_VIEW = 3;
    private static final String CALCS_OPEN = "calcs open";
    private static final String GENERATED_IMAGE_URI = "generated_image_uri";
    private Uri generatedImageUri;
    private Uri imgUri;
    private Bitmap b;
    private int noteId;
    private String mCurrentPhotoPath;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private Spinner spinner;
    private ArrayList<Tag> t;

    int iRatio;
    int cRatio;

    @Nullable
    private GlycemiaRec glycemiaData;
    @Nullable
    private CarbsRec carbsData;
    @Nullable
    private InsulinRec insulinData;
    @Nullable
    private Note noteData;

    public static final String CARBS = "CARBS";
    public static final String INSULIN = "INSULIN";
    public static final String GLICAEMIA = "GLICAEMIA";
    public static final String NOTE = "NOTE";
    public static final String PLUS = "PLUS";

    public static final String ARG_CARBS = "ARG_CARBS";
    public static final String ARG_INSULIN = "ARG_INSULIN";
    public static final String ARG_BLOOD_GLUCOSE = "ARG_BLOOD_GLUCOSE";
    public static final String ARG_NOTE = "ARG_NOTE";

    public static final String ARG_BUTTONS_DELETE_LIST = "ARG_BUTTONS_DELETE_LIST";
    public static final String ARG_BUTTONS_UPDATE_LIST = "ARG_BUTTONS_UPDATE_LIST";
    public static final String ARG_CALENDAR = "ARG_CALENDAR";



    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;


    @Override
    public void finishAfterTransition() {
        contentLayout.setAlpha(0);
        super.finishAfterTransition();
    }
    @Override
    public void supportFinishAfterTransition() {
        super.supportFinishAfterTransition();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(NewHomeRegistry.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) + ActivityCompat.checkSelfPermission(NewHomeRegistry.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setImgURI(Uri.parse(mCurrentPhotoPath));
            try {
                carbsRegister.setImage(imgUri, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == IMAGE_VIEW) {
            //se tivermos apagado a foto dá result code -1
            //se voltarmos por um return por exemplo o resultcode é 0
            if (resultCode == -1) {
                imageRemoved();
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
        outState.putParcelable(GENERATED_IMAGE_URI, generatedImageUri);

        //outState.putStringArrayList(ARG_BUTTONS_LIST, buttons);
        outState.putStringArrayList(ARG_BUTTONS_DELETE_LIST, delete_buttons);
        outState.putStringArrayList(ARG_BUTTONS_UPDATE_LIST, buttonsUpdate);
        String date = DateUtils.getFormattedDate(registerDate)+" "+DateUtils.getFormattedTimeSec(registerDate);
        outState.putString(ARG_CALENDAR, date);


        spinner = (Spinner) findViewById(R.id.tag_spinner);
        String tag = null;
        if (spinner != null) {
            tag = spinner.getSelectedItem().toString();
        }

        DB_Read rdb = new DB_Read(this);
        //int idUser = rdb.getId();
        int idTag = (spinner.getSelectedItemPosition()+1);//rdb.Tag_GetIdByName(tag);
        rdb.close();

        if (glycaemiaRegister != null ) {
            glycemiaData  = glycaemiaRegister.save_read();
            glycemiaData.setIdTag(idTag);
            if(glycemiaData.getValue()!=0){
                outState.putParcelable(ARG_BLOOD_GLUCOSE, glycemiaData);
            }
        }
        if (insuRegister != null ) {
            insulinData = insuRegister.save_read();
            insulinData.setIdTag(idTag);
            if(insulinData.getInsulinUnits()!=0){
                outState.putParcelable(ARG_INSULIN, insulinData);
            }
        }
        if (carbsRegister != null ) {
            carbsData = carbsRegister.save_read();
            carbsData.setIdTag(idTag);
            if(carbsData.getCarbsValue()!=0){
                outState.putParcelable(ARG_CARBS, carbsData);
            }

        }
        if(noteRegister != null){
            noteData = noteRegister.save_read();
            if(noteData.getNote()!=null){
                if(!noteData.getNote().equals("")){
                    outState.putParcelable(ARG_NOTE, noteData);
                }
            }
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(GENERATED_IMAGE_URI)) {
            generatedImageUri = savedInstanceState.getParcelable(GENERATED_IMAGE_URI);
        }
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
        FeaturesDB featuresDB = new FeaturesDB(MyDiabetesStorage.getInstance(this));

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        useIOB = featuresDB.isFeatureActive(FeaturesDB.FEATURE_INSULIN_ON_BOARD);
        contentLayout = (LinearLayout) findViewById(R.id.content_panel);
        registerDateTextV = (TextView) findViewById(R.id.registryDate);
        registerTimeTextV = (TextView) findViewById(R.id.registerTime);
        bottomSheetViewgroup = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewgroup);
        bottomSheetBehavior.setHideable(true);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        registerDate = Calendar.getInstance();

        DB_Read rdb = new DB_Read(this);
        t = rdb.Tag_GetAll();
        String[] allTags = new String[t.size()];
        UserInfo obj = rdb.MyData_Read();
        iRatio = obj.getInsulinRatio();
        cRatio = obj.getCarbsRatio();

        rdb.close();

        if (t != null) {
            for (int x=0;x<t.size();x++) {
                allTags[x] = t.get(x).getName();
            }
        }

        buttons = new ArrayList<>();
        buttonsUpdate = new ArrayList<>();
        delete_buttons = new ArrayList<>();

        insulinCalculator = new InsulinCalculator(this,registerDate);
        imgUri = null;
        noteId = -1;

        fab = (FloatingActionButton) findViewById(R.id.fab);
        spinner = ((Spinner) findViewById(R.id.tag_spinner));
        spinner.setAdapter(new StringSpinnerAdapter(this, allTags));

        updateTagSpinner();

        carbsData = new CarbsRec();
        glycemiaData = new GlycemiaRec();
        insulinData = new InsulinRec();
        noteData = new Note();

//        insuRegister= new InsuRegister(this, iRatio, cRatio);
//        insuRegister.updateInsuCalc(insulinCalculator);
        carbsRegister = new CarbsRegister(this, new NewHomeRegCallImpl());
        glycaemiaRegister = new GlycaemiaRegister(this, registerDate, new NewHomeRegCallImpl());
        noteRegister = new NoteRegister(this);
        bottomSheetViewgroup.findViewById(R.id.bs_notes).setEnabled(false);
    }

    private void updateTagSpinner() {
        Tag displayTag = getCurrentTag(t, registerDate);
        if(displayTag!=null){
            int index = ((StringSpinnerAdapter) spinner.getAdapter()).getItemPosition(displayTag.getName());
            if(index>=0){
                spinner.setSelection(index);
            }
        }
    }

    private Tag getCurrentTag(ArrayList<Tag> t, Calendar registerDate) {

        String timeString[] = DateUtils.getFormattedTime(registerDate).split(":");
        int currentTime = Integer.parseInt(timeString[0], 10) * 60 + Integer.parseInt(timeString[1]);

        Tag otherCase = null;
        for(Tag tag: t){
            if(tag.getStart()!=null) {
                String[] temp_start = tag.getStart().split(":");
                String[] temp_end = tag.getEnd().split(":");
                int startTagTime = Integer.parseInt(temp_start[0], 10) * 60 + Integer.parseInt(temp_start[1]);
                int endTagTime = Integer.parseInt(temp_end[0], 10) * 60 + Integer.parseInt(temp_end[1]);
                if (timeOverlaps(currentTime, startTagTime, endTagTime)) {
                    return tag;
                }
            }else{
                otherCase=tag;
            }
        }
        return otherCase;
    }

    private boolean timeOverlaps(int t, int s0, int e0) {
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

    private void goBack(){
        if(carbsRegister!=null){carbsData = carbsRegister.save_read();}
        if(insuRegister!=null){insulinData = insuRegister.save_read();}
        if(glycaemiaRegister!=null){glycemiaData = glycaemiaRegister.save_read();}

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
    private void deleteRegister(){
        final Context c = this;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.deleteReading))
                .setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Falta verificar se não está associada a nenhuma entrada da DB
                        //Rever porque não elimina o registo de glicemia
                        DB_Write reg = new DB_Write(c);
                        try {
                            //Weight_Delete();
                            if(glycemiaData!=null){setNoteId( glycemiaData.getIdNote() ); reg.Glycemia_Delete(glycemiaData.getId());}
                            if(carbsData!=null){setNoteId( carbsData.getIdNote() ); reg.Carbs_Delete(carbsData.getId());}
                            if(insulinData!=null){setNoteId( insulinData.getIdNote() ); reg.Insulin_Delete(insulinData.getId());}
                            if(noteId != -1){reg.Note_Delete(noteId);}

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
                        }
                        reg.close();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
    }

    private void save() {
        try{
            validateInfo_Save();
            finish();
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
    }
    private void validateInfo_Save()throws Exception{
        spinner = (Spinner) findViewById(R.id.tag_spinner);
        String tag = null;
        if (spinner != null) {
            tag = spinner.getSelectedItem().toString();
        }

        DB_Read rdb = new DB_Read(this);
        int idUser = rdb.getId();
        int idTag = rdb.Tag_GetIdByName(tag);


        DB_Write reg = new DB_Write(this);

        if(buttons.contains(NOTE)){
            if(buttons.size()<=2){
                noteRegister.setErrorMessage(getString(R.string.noteError));
                throw new Exception("Just note no cry");
            }
            noteData = noteRegister.save_read();
            if(noteData.getNote()!=null){
                if(!noteData.getNote().equals("")){

                    if (buttonsUpdate.contains(NOTE)) {
                        reg.Note_Update(noteData);
                    } else {
                        noteData.setId(reg.Note_Add(noteData));
                    }
                }
            }
        }
        if(delete_buttons.contains(NOTE)){
            reg.Note_Delete(noteData.getId());
            noteData = null;
        }
        if(delete_buttons.contains(CARBS)){
            reg.Carbs_Delete(carbsData.getId());
        }
        if(delete_buttons.contains(GLICAEMIA)){
            reg.Glycemia_Delete(glycemiaData.getId());
        }
        if(delete_buttons.contains(INSULIN)){
            reg.Insulin_Delete(insulinData.getId());
        }
        for(String field:buttons){
            try {
            switch (field){
                    case CARBS:
                        carbsData = carbsRegister.save_read();
                        if(!carbsRegister.validate()){throw new Exception();}
                        carbsData.setIdTag(idTag);
                        carbsData.setIdUser(idUser);
                        carbsData.setDateTime(registerDate);
                        if(noteData != null) {
                            if(noteData.getNote()!= null && noteData.getId()!= -1){
                                if (!noteData.getNote().equals("")) {
                                    carbsData.setIdNote(noteData.getId());
                                }
                            }
                        }else{
                            carbsData.setIdNote(-1);
                        }
                        Uri imgpath = carbsRegister.getImgUri();
                        if(imgpath!=null){
                            carbsData.setPhotoPath(imgpath.toString());
                        }
                        if (buttonsUpdate.contains(CARBS) && !delete_buttons.contains(CARBS)) {
                            reg.Carbs_Update(carbsData);
                        } else {
                            reg.Carbs_Save(carbsData);
                        }
                        break;
                    case GLICAEMIA:
                        glycemiaData = glycaemiaRegister.save_read();
                        if(!glycaemiaRegister.validate()){throw new Exception();}
                        glycemiaData.setIdTag(idTag);
                        glycemiaData.setIdUser(idUser);
                        glycemiaData.setDateTime(registerDate);
                        if(noteData != null) {
                            if(noteData.getNote()!= null && noteData.getId()!= -1){
                                if (!noteData.getNote().equals("")) {
                                    glycemiaData.setIdNote(noteData.getId());
                                }
                            }
                        }else{
                            glycemiaData.setIdNote(-1);
                        }
                        if (buttonsUpdate.contains(GLICAEMIA)&& !delete_buttons.contains(GLICAEMIA)) {
                            reg.Glycemia_Update(glycemiaData);
                        } else {
                            reg.Glycemia_Save(glycemiaData);
                        }
                        break;
                    case INSULIN:
                        insulinData = insuRegister.save_read();
                        if(!insuRegister.validate()){throw new Exception();}
                        insulinData.setIdTag(idTag);
                        insulinData.setIdUser(idUser);
                        insulinData.setDateTime(registerDate);
                        if(noteData != null) {
                            if(noteData.getNote()!= null && noteData.getId()!= -1){
                                if (!noteData.getNote().equals("")) {
                                    insulinData.setIdNote(noteData.getId());
                                }
                            }
                        }else{
                            insulinData.setIdNote(-1);
                        }
                        if(buttonsUpdate.contains(INSULIN) && !delete_buttons.contains(INSULIN)){
                            reg.Insulin_Update(insulinData);
                        }else{
                            reg.Insulin_Save(insulinData);
                        }
                        break;
                }
            }catch (Exception e){
               throw e;
            }
        }
        if(buttons.size()==1 && buttons.contains(NOTE)){
            reg.Note_Delete(noteData.getId());
        }
        Log.i(TAG, "validateInfo_Save: BEGIN");
        BadgeUtils.addLogBadge(getBaseContext(), rdb, reg);
        BadgeUtils.addDailyBadge(getBaseContext(), rdb, reg);
        LevelsPointsUtils.addPoints(getBaseContext(), LevelsPointsUtils.RECORD_POINTS, "log", rdb);
        setResult(Home.CHANGES_OCCURRED, this.getIntent());
        Log.i(TAG, "validateInfo_Save: END");
        rdb.close();
        reg.close();
    }

    private void insertNoteMenu(){
        noteRegister = new NoteRegister(this);
        addContent(noteRegister);
        buttons.add(0,NOTE);
    }
    private void insertGlicMenu(){
        /*Advice newAdvice = YapDroid.newInstance(v.getContext()).getSingleAdvice("Start", "",v.getContext());
                    if(newAdvice!=null){
                        addContent(R.layout.dialog_exp_advice);
                        setAdviceText();
                    }*/
        glycaemiaRegister = new GlycaemiaRegister(this,registerDate, glycaemiaRegister.getCallBack());
        addContent(glycaemiaRegister);
        bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(true);
        buttons.add(0, GLICAEMIA);
        bottomSheetViewgroup.findViewById(R.id.bs_notes).setEnabled(true);
        requestKeyboard(glycaemiaRegister);
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
        carbsRegister = new CarbsRegister(this, carbsRegister.getCallBack());
        addContent(carbsRegister);
        bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(true);
        buttons.add(0, CARBS);
        bottomSheetViewgroup.findViewById(R.id.bs_notes).setEnabled(true);
        requestKeyboard(carbsRegister);
    }
    private void insertInsulinMenu(boolean reqKey){
        /*Advice newAdvice = YapDroid.newInstance(v.getContext()).getSingleAdvice("Start", "",v.getContext());
                    if(newAdvice!=null){
                        addContent(R.layout.dialog_exp_advice);
                        setAdviceText();
                    }*/
        insuRegister = new InsuRegister(this, iRatio, cRatio);
        addContent(insuRegister);
        insuRegister.updateInsuCalc(insulinCalculator);
        buttons.add(0, INSULIN);
        bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(true);
        bottomSheetViewgroup.findViewById(R.id.bs_notes).setEnabled(true);
        if(reqKey){requestKeyboard(insuRegister);}
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
            if(buttonsUpdate.contains(INSULIN)){
                delete_buttons.remove(INSULIN);
            }
            insertInsulinMenu(false);
            bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(true);
            hideBottomSheet();
            insuRegister.updateInsuCalc(insulinCalculator);
            if(field.equals(CARBS)){
                carbsRegister.requestCarbsFocus();
            }else{
                glycaemiaRegister.requestGlicFocus();
            }
        }
    }
    private void setupBottomSheet() {

        Button button = (Button) bottomSheetViewgroup.findViewById(R.id.bs_notes);
        if(buttonsUpdate.size()==0 && buttons.size()==0){
            button.setEnabled(false);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains(NOTE)) {
                    if(buttonsUpdate.contains(NOTE)){
                        delete_buttons.add(NOTE);
                    }
                    removeContent(noteRegister);
                    buttons.remove(NOTE);
                    bottomSheetViewgroup.findViewById(R.id.bs_notes).setPressed(false);
                } else {
                    if(buttonsUpdate.contains(NOTE)){
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
                if (buttons.contains(GLICAEMIA)) {
                    if(buttonsUpdate.contains(GLICAEMIA)){
                        delete_buttons.add(GLICAEMIA);
                    }
                    removeContent(glycaemiaRegister);
                    buttons.remove(GLICAEMIA);
                    bottomSheetViewgroup.findViewById(R.id.bs_glicemia).setPressed(false);
                } else {
                    if(buttonsUpdate.contains(GLICAEMIA)){
                        delete_buttons.remove(GLICAEMIA);
                    }
                    setGlicPressed(v);
                }
            }
        });
        bottomSheetViewgroup.findViewById(R.id.bs_meal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains(CARBS)) {
                    if(buttonsUpdate.contains(CARBS)){
                        delete_buttons.add(CARBS);
                    }
                    removeContent(carbsRegister);
                    buttons.remove(CARBS);
                    bottomSheetViewgroup.findViewById(R.id.bs_meal).setPressed(false);
                } else {
                    if(buttonsUpdate.contains(CARBS)){
                        delete_buttons.remove(CARBS);
                    }
                    setCarbsPressed(v);
                }
            }
        });

        bottomSheetViewgroup.findViewById(R.id.bs_insulin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttons.contains(INSULIN)) {
                    if(buttonsUpdate.contains(INSULIN)){
                        delete_buttons.add(INSULIN);
                    }
                    removeContent(insuRegister);
                    buttons.remove(INSULIN);
                    bottomSheetViewgroup.findViewById(R.id.bs_insulin).setPressed(false);
                } else {
                    if(buttonsUpdate.contains(INSULIN)){
                        delete_buttons.remove(INSULIN);
                    }
                    setInsuPressed(v);
                }
            }
        });
    }

    public String getDate() {
        return registerDateTextV.getText().toString();
    }
    public String getTime() {
        return registerTimeTextV.getText().toString();
    }
    private void setImgURI(Uri newUri){
        carbsRegister.setUri(newUri);
        imgUri = newUri;}
    private void imageRemoved() {
        setImgURI(null);
    }
    private void fillParameters(Bundle args, boolean isUpdate){

        if (args.containsKey(ARG_BUTTONS_UPDATE_LIST)) {
            this.buttonsUpdate = args.getStringArrayList(ARG_BUTTONS_UPDATE_LIST);
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

        DB_Read db_read = new DB_Read(this);
        if(isUpdate){

            if (args.containsKey(ARG_CARBS)) {
                carbsData = args.getParcelable(ARG_CARBS);
                if (carbsData != null) {
                    carbsData = db_read.CarboHydrate_GetById(carbsData.getId());
                    if (carbsData != null) {
                        buttonsUpdate.add(CARBS);
                        insertCarbsMenu();
                        carbsRegister.fill_parameters(carbsData);
                        String imgPath = carbsData.getPhotoPath();
                        setNoteId(carbsData.getIdNote());
                        registerDate = carbsData.getDateTime();
                        if(imgPath!=null){imgUri = Uri.parse(imgPath);}
                        try {
                            carbsRegister.setImage(imgUri, this);
                        } catch (Exception e) {
                            //couldnt access storage
                        }


                    }
                }
            }
            if (args.containsKey(ARG_BLOOD_GLUCOSE)) {
                glycemiaData = args.getParcelable(ARG_BLOOD_GLUCOSE);
                if (glycemiaData != null) {
                    glycemiaData = db_read.Glycemia_GetById(glycemiaData.getId());
                    if (glycemiaData != null) {
                        buttonsUpdate.add(GLICAEMIA);
                        insertGlicMenu();
                        glycaemiaRegister.fill_parameters(glycemiaData);

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
                        buttonsUpdate.add(INSULIN);
                        insertInsulinMenu(false);
                        insuRegister.fill_parameters(insulinData);
                        insuRegister.updateInsuCalc(insulinCalculator);
                        setNoteId(insulinData.getIdNote());
                        registerDate = insulinData.getDateTime();
                    }
                }
            }
            if (noteId != -1) {
                noteData = db_read.Note_GetById(noteId);
                noteData.setId(noteId);//args.getParcelable(ARG_NOTE);
                Log.i(TAG, "fillParameters: UPDATE "+noteData.toString());
                if(noteData!=null){
                    buttonsUpdate.add(NOTE);
                    insertNoteMenu();
                    noteRegister.fill_parameters(noteData.getNote());
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
        }else{

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
                    buttons.add(CARBS);
                    insertCarbsMenu();

                    String imgPath = carbsData.getPhotoPath();
                    if(imgPath!=null){imgUri = Uri.parse(imgPath);}
                    carbsRegister.fill_parameters(carbsData);
                    try {
                        carbsRegister.setImage(imgUri, this);
                    } catch (Exception e) {
                        //
                    }

                    setNoteId(carbsData.getIdNote());
                    if(carbsData.getDateTime()!=null){
                        registerDate = carbsData.getDateTime();
                    }
                }
            }
            if (args.containsKey(ARG_BLOOD_GLUCOSE)) {
                glycemiaData = args.getParcelable(ARG_BLOOD_GLUCOSE);
                if (glycemiaData != null) {
                    buttons.add(GLICAEMIA);
                    insertGlicMenu();
                    glycaemiaRegister.fill_parameters(glycemiaData);

                    setNoteId(glycemiaData.getIdNote());
                    if(glycemiaData.getDateTime()!=null){
                        registerDate = glycemiaData.getDateTime();
                    }
                }
            }
            if (args.containsKey(ARG_INSULIN)) {
                insulinData = args.getParcelable(ARG_INSULIN);
                if (insulinData != null) {
                    buttons.add(INSULIN);
                    insertInsulinMenu(false);
                    insuRegister.fill_parameters(insulinData);
                    setNoteId(insulinData.getIdNote());
                    if(insulinData.getDateTime()!=null){
                        registerDate = insulinData.getDateTime();
                    }
                }
            }
            db_read.close();

            if (args.containsKey(ARG_NOTE)) {
                noteData = args.getParcelable(ARG_NOTE);
                if(noteData!=null){
                    buttons.add(NOTE);
                    insertNoteMenu();
                    noteRegister.fill_parameters(noteData.getNote());
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
        }
    }

    @Nullable
    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        findViewById(R.id.toolbar).startActionMode(callback);
        return super.startActionMode(callback);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(NewHomeRegistry.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) + ActivityCompat.checkSelfPermission(NewHomeRegistry.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Got Permission
                    try {
                        dispatchTakePictureIntent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                return;
            }
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

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image;
        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            // Create an image file name

            File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    // unable to create directory
                    // todo report and recover
                }
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
    }
    class NewHomeRegCallImpl implements NewHomeRegCallBack{

        @Override
        public void checkPermissions() {
            if (ContextCompat.checkSelfPermission(NewHomeRegistry.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(NewHomeRegistry.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(NewHomeRegistry.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(NewHomeRegistry.this, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(NewHomeRegistry.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }
//                else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false) || (permissionStatus.getBoolean(Manifest.permission.CAMERA,false))) {
//                    sentToSettings = true;
//                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    Uri uri = Uri.fromParts("package", getPackageName(), null);
//                    intent.setData(uri);
//                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//                } else {
//                    //just request the permission
//                    ActivityCompat.requestPermissions(NewHomeRegistry.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
//                }

//                SharedPreferences.Editor editor = permissionStatus.edit();
//                editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
//                editor.putBoolean(Manifest.permission.CAMERA,true);
//                editor.commit();
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
            }
            startActivity(intent);
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
            insulinCalculator.setCarbs(carbsRegister!=null?carbsRegister.getCarbs():0);
            insulinCalculator.setGlycemia(glycaemiaRegister!=null?glycaemiaRegister.getGlycemia():0);
            insulinCalculator.setGlycemiaTarget(glycaemiaRegister!=null?glycaemiaRegister.getGlycemiaTarget():0);
            if(insuRegister!=null)
                insuRegister.updateInsuCalc(insulinCalculator);
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
                    if(insuRegister!=null){
                        Log.i(TAG, "onTimeSet: "+DateUtils.getFormattedTime(registerDate));
                        insuRegister.updateRatioCalc(registerDate);
                    }
                    if(buttons.contains(GLICAEMIA)){
                        glycaemiaRegister.updateObjective();
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
}