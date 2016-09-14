package pt.it.porto.mydiabetes.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.Task;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.ListsDataDb;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.listAdapters.HomeAdapter;
import pt.it.porto.mydiabetes.ui.usability.HomeTouchHelper;


public class Home extends BaseActivity {

    private static final String TAG = "Home";
    boolean fabOpen = false;

    private EditText dateFrom;
    private EditText dateTo;
    private FloatingActionButton fab;
    private FloatingActionButton phantom_fab;

    private float offset1;
    private float offset2;
    private float offset3;
    private float offset4;
    private float offset5;
    private float offset6;
    private float offset7;

    private static final String TRANSLATION_Y = "translationY";
    private static final String TRANSLATION_X = "translationX";
    private static final String ROTATION = "rotation";

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private LinearLayout fabContainer_v;
    private LinearLayout fabContainer_h;


    private FloatingActionButton miniFab1;
    private FloatingActionButton miniFab2;
    private FloatingActionButton miniFab3;
    private FloatingActionButton miniFab4;
    private FloatingActionButton miniFab5;
    private FloatingActionButton miniFab6;
    private FloatingActionButton miniFab7;

    private ListView logbookList;
    private RecyclerView homeList;

    ArrayList<Task> receiverTaskList = new ArrayList<>();
    ArrayList<Advice> receiverAdviceList = new ArrayList<>();

    SharedPreferences mPrefs;

    Uri defaultImgUri;

    private YapDroid yapDroid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        yapDroid = YapDroid.newInstance(this);

        mPrefs = getSharedPreferences("label", 0);

        String imgUriString = mPrefs.getString("userImgUri", "default");

        if(imgUriString.equals("default")){
            defaultImgUri = null;
        }else{
            defaultImgUri = Uri.parse(imgUriString);
        }


        DB_Read read = new DB_Read(this);
        if (!read.MyData_HasData()) {
            ShowDialogAddData();
            read.close();
            return;
        }
        read.close();
        homeList = (RecyclerView) findViewById(R.id.HomeListDisplay);

        fabContainer_v = (LinearLayout) findViewById(R.id.fab_container_v);
        fabContainer_h = (LinearLayout) findViewById(R.id.fab_container_h);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        phantom_fab = (FloatingActionButton) findViewById(R.id.fab_);
        miniFab1 = (FloatingActionButton) findViewById(R.id.mini_fab1);
        miniFab2 = (FloatingActionButton) findViewById(R.id.mini_fab2);
        miniFab3 = (FloatingActionButton) findViewById(R.id.mini_fab3);

        miniFab4 = (FloatingActionButton) findViewById(R.id.mini_fab4);
        miniFab5 = (FloatingActionButton) findViewById(R.id.mini_fab5);
        miniFab6 = (FloatingActionButton) findViewById(R.id.mini_fab6);
        miniFab7 = (FloatingActionButton) findViewById(R.id.mini_fab7);

//        dateFrom = (EditText) findViewById(R.id.et_Logbook_DataFrom);
//        dateTo = (EditText) findViewById(R.id.et_Logbook_DataTo);
        logbookList = (ListView) findViewById(R.id.LogbookActivityList);

        //setEditTextListeners();
        setFabClickListeners();
        setOffsets();
        fillDates();
        //fillListView(logbookList);

        fillHomeList();

        //----------------------nav
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        DrawerLayout.DrawerListener Dlistener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {

            }

            @Override
            public void onDrawerOpened(View view) {
                CircleImageView userImg = (CircleImageView) findViewById(R.id.profile_image);
                if(defaultImgUri!=null){
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), defaultImgUri);
                        userImg.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 60, 60, false));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                userImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 1);
                        //intent.setAction(Intent.ACTION_GET_CONTENT);
                        //startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

                        //Toast.makeText(v.getContext(), "Change Picture?", Toast.LENGTH_LONG).show();


                    }
                });
            }



            @Override
            public void onDrawerClosed(View view) {
                // your refresh code can be called from here
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        };
        drawerLayout.addDrawerListener(Dlistener);


/*
        */

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                /*if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);*/


                //Closing drawer on item click
                drawerLayout.closeDrawers();
                Intent intent;

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.userTasks:
                        intent = new Intent(getApplicationContext(), TaskListActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.userLogbook:
                        intent = new Intent(getApplicationContext(), LogbookChartList.class);
                        startActivity(intent);
                        return true;
                    case R.id.personalData:
                        intent = new Intent(getApplicationContext(), Preferences.class);
                        startActivity(intent);
                        return true;
                    case R.id.diabetesData:
                        intent = new Intent(getApplicationContext(), SettingsInsulin.class);
                        startActivity(intent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 1) {

            Uri resultUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                CircleImageView userImg = (CircleImageView) findViewById(R.id.profile_image);
                userImg.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 60, 60, false));

                SharedPreferences.Editor mEditor = mPrefs.edit();
                mEditor.putString("userImgUri", resultUri+"").commit();

            } catch (IOException e) {
                e.printStackTrace();
            }

            /*

            CircleImageView userImg = (CircleImageView) findViewById(R.id.profile_image);
            File imgFile = new  File(resulttUri.getPath());

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                userImg.setImageBitmap(myBitmap);

            }*/
        }
    }

    private void fillHomeList() {

        fillTaskList();
        fillAdviceList();

        ListsDataDb db = new ListsDataDb(MyDiabetesStorage.getInstance(this));
        Cursor cursor = db.getAllLogbookListWithin(10);
        HomeAdapter homeAdapter = new HomeAdapter(receiverAdviceList, receiverTaskList, cursor,this);

        ItemTouchHelper.Callback callback = new HomeTouchHelper(homeAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(homeList);
        homeList.setAdapter(homeAdapter);
        homeList.setLayoutManager(new LinearLayoutManager(this));
    }

   /* private void setEditTextListeners() {

        dateFrom.addTextChangedListener(new TextWatcher() {
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
    }*/
    private void setFabClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(fabOpen){
                    disableFloationActionButtonOptions();
                    fabOpen = false;
                }else{
                    enableFloationActionButtonOptions();
                    fabOpen = true;
                }
            }
        });
        miniFab1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GlycemiaDetail.class);
                startActivity(intent);
            }
        });
        miniFab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InsulinDetail.class);
                startActivity(intent);
            }
        });
        miniFab3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MealActivity.class);
                startActivity(intent);
            }
        });
        miniFab4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DiseaseDetail.class);
                startActivity(intent);
            }
        });
        miniFab5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CholesterolDetail.class);
                startActivity(intent);
            }
        });
        miniFab6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WeightDetail.class);
                startActivity(intent);
            }
        });
        miniFab7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExerciseDetail.class);
                startActivity(intent);
            }
        });
    }
    private void setOffsets(){
        fabContainer_v.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer_v.getViewTreeObserver().removeOnPreDrawListener(this);
                offset1 = fab.getY() - miniFab1.getY();
                miniFab1.setTranslationY(offset1);
                offset2 = fab.getY() - miniFab2.getY();
                miniFab2.setTranslationY(offset2);
                offset3 = fab.getY() - miniFab3.getY();
                miniFab3.setTranslationY(offset3);
                return true;
            }
        });
        fabContainer_h.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer_h.getViewTreeObserver().removeOnPreDrawListener(this);
                offset4 = phantom_fab.getX() - miniFab4.getX()-phantom_fab.getWidth()/2;
                miniFab4.setTranslationX(offset4);
                offset5 = phantom_fab.getX() - miniFab5.getX()-phantom_fab.getWidth()/2;
                miniFab5.setTranslationX(offset5);
                offset6 = phantom_fab.getX() - miniFab6.getX()-phantom_fab.getWidth()/2;
                miniFab6.setTranslationX(offset6);
                offset7 = phantom_fab.getX() - miniFab7.getX()-phantom_fab.getWidth()/2;
                miniFab7.setTranslationX(offset7);
                return true;
            }
        });
    }


    /**
     * @param view
     * @param ang How many degrees to rotate
     * @return
     */
    private Animator createRotationAnimator(View view, float ang) {
        float rotation = fab.getRotation();
        return ObjectAnimator.ofFloat(view, ROTATION, rotation, rotation + ang)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
    private Animator createTranslateAnimator(View view, float offset) {
        float position = view.getY();
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, position, position + offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
    private Animator createCollapseAnimatorY(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
    private Animator createCollapseAnimatorX(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_X, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
    private Animator createExpandAnimatorY(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
    private Animator createExpandAnimatorX(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_X, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private void disableFloationActionButtonOptions() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                createRotationAnimator(fab, 45f),
                createCollapseAnimatorY(miniFab1, offset1),
                createCollapseAnimatorY(miniFab2, offset2),
                createCollapseAnimatorY(miniFab3, offset3),
                createCollapseAnimatorX(miniFab4, offset4),
                createCollapseAnimatorX(miniFab5, offset5),
                createCollapseAnimatorX(miniFab6, offset6),
                createCollapseAnimatorX(miniFab7, offset7));

        animatorSet.start();
    }
    private void enableFloationActionButtonOptions() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                createRotationAnimator(fab, -45f),
                createExpandAnimatorY(miniFab1, offset1),
                createExpandAnimatorY(miniFab2, offset2),
                createExpandAnimatorY(miniFab3, offset3),
                createExpandAnimatorX(miniFab4, offset4),
                createExpandAnimatorX(miniFab5, offset5),
                createExpandAnimatorX(miniFab6, offset6),
                createExpandAnimatorX(miniFab7, offset7));
        animatorSet.start();
    }

    private void fillTaskList() {

        //ArrayList<String> taskListFromYap = yapDroid.getYapMultipleTasks();

        /*String[] temp2 = {"AVISO IMPORTANTE","MealActivity","10:s"};

        Task myTask1 = new Task("5 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 9);
        Task myTask2 = new Task("1 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 6);
        //setupAlarm(myTask2);
        Task myTask3 = new Task("2 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 2);
        Task myTask4 = new Task("4 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 4);
        Task myTask5 = new Task("3 - ", "Exercicio fisico é fundamental para uma boa gestão da diabetes", temp2, 3);
        */
        /*for(String taskString:taskListFromYap){

            String urgency = null;
            int urgencyValue = Integer.parseInt(urgency);
            String expTxt = null;
            String message = null;
            //String[] temp2 = {"AVISO IMPORTANTE","MealActivity","10:s"};

            Task newTask = new Task(message, expTxt, null, urgencyValue);
        }/
        /*
        receiverTaskList.add(myTask1);
        receiverTaskList.add(myTask2);
        receiverTaskList.add(myTask3);
        receiverTaskList.add(myTask4);
        receiverTaskList.add(myTask5);*/

        //Collections.sort(receiverTaskList);
        receiverTaskList = yapDroid.getYapMultipleTasks();
        /*TaskListAdapter taskAdapter = new TaskListAdapter(receiverTaskList, this);

        ItemTouchHelper.Callback callback = new TaskTouchHelper(taskAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(lv);
        lv.setAdapter(taskAdapter);
        lv.setLayoutManager(new LinearLayoutManager(this));*/
    }
    public void fillAdviceList() {



        //getAllYapAdvices -> put them into adviceList.

        /*for(RawAdvice advice:advicesFromYap){
            Advice newAdvice = new Advice(cenas do conselho);
            if(newAdvice.getType().equals(Advice.AdviceTypes.ALERT)){
                setupAlarm(advice);
            }
            adviceList.add(newAdvice);
        }*/

        String[] temp = {"AVISO IMPORTANTE PARA SI VEJA COM CUIDADO","MealActivity","10:s"};

        Advice myAdvice1 = new Advice(this, "5 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "normal", temp, 9);
        Advice myAdvice2 = new Advice(this, "1 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "ALERT", temp, 6);
        Advice myAdvice3 = new Advice(this, "1 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "ALERT", temp, 6);
        //Advice myAdvice3 = new Advice("2 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "question", temp, 2);
        Advice myAdvice4 = new Advice(this, "4 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "normal", temp, 4);
        Advice myAdvice5 = new Advice(this, "3 - Já fez exercicio hoje?", "Exercicio fisico é fundamental para uma boa gestão da diabetes", "alert", temp, 3);

        receiverAdviceList.add(myAdvice1);
        receiverAdviceList.add(myAdvice2);
        receiverAdviceList.add(myAdvice3);
        receiverAdviceList.add(myAdvice4);
        receiverAdviceList.add(myAdvice5);

        Collections.sort(receiverAdviceList);

    }



    //----------------------logbook

    public void fillDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
/*        if (dateFrom.getText().length() == 0) {
            dateFrom.setText(DateUtils.getFormattedDate(calendar));
        }*/

        calendar = Calendar.getInstance();
        /*if (dateTo.getText().length() == 0) {
            dateTo.setText(DateUtils.getFormattedDate(calendar));
        }*/
    }

    public void ShowDialogAddData() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item!=null){
            if(item.getTitle().equals("homeSettings")){
                if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }else{
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}


