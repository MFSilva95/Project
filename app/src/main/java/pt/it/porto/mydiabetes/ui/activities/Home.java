package pt.it.porto.mydiabetes.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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


    @Override
    public String getRegType() {
        return null;
    }

    private static final String TAG = "Home";

    private static final int CHANGES_OCCURRED = 1;
    private static final int NO_CHANGES_OCCURRED = 0;

    boolean fabOpen = false;

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

    ArrayList<Task> taskListFromYap = new ArrayList<>();
    //ArrayList<Task> receiverTaskList = new ArrayList<>();
    ArrayList<Advice> receiverAdviceList = new ArrayList<>();

    SharedPreferences mPrefs;

    Uri defaultImgUri;
    String imgUriString;

    private YapDroid yapDroid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //yapDroid = YapDroid.newInstance(this);

        mPrefs = getSharedPreferences("label", 0);

        imgUriString = mPrefs.getString("userImgUri", null);

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

        logbookList = (ListView) findViewById(R.id.LogbookActivityList);

        setFabClickListeners();
        setOffsets();
        fillDates();
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
                if (imgUriString != null) {
                    Bitmap newImg = loadImageFromStorage(imgUriString);
                    if (newImg != null) {
                        userImg.setImageBitmap(Bitmap.createScaledBitmap(newImg, 60, 60, false));
                    }
                } else {
                    userImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            try {
                                startActivityForResult(intent, 1);
                            } catch (Exception e) {
                                Log.i("IMGTEST", "ERROR:" + e.toString());
                            }
                        }
                    });
                }
            }


            @Override
            public void onDrawerClosed(View view) {

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


                drawerLayout.closeDrawers();
                Intent intent;

                switch (menuItem.getItemId()) {

                    case R.id.userTasks:
                        intent = new Intent(getApplicationContext(), TaskListActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.userLogbook:
                        intent = new Intent(getApplicationContext(), LogbookChartList.class);
                        startActivity(intent);
                        return true;
                    case R.id.personalData:
                        intent = new Intent(getApplicationContext(), MyData.class);
                        startActivity(intent);
                        return true;
                    case R.id.diabetesData:
                        intent = new Intent(getApplicationContext(), SettingsInsulin.class);
                        startActivity(intent);
                        return true;
                    case R.id.importAndExport:
                        intent = new Intent(getApplicationContext(), ImportExport.class);
                        startActivity(intent);
                        return true;
                    case R.id.preferences:
                        intent = new Intent(getApplicationContext(), Preferences.class);
                        startActivity(intent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });
    }


    Uri avatarURI;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Log.i(TAG, "onActivityResult: HELLO M8");

        switch (requestCode) {
            case 1:
                if (data != null) {
                    avatarURI = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), avatarURI);
                        if (bitmap != null) {
                            String imgPath = saveToInternalStorage(bitmap);
                            Bitmap newImg = loadImageFromStorage(imgPath);
                            CircleImageView userImg = (CircleImageView) findViewById(R.id.profile_image);
                            if (userImg != null) {
                                userImg.setImageBitmap(Bitmap.createScaledBitmap(newImg, 60, 60, false));
                            }
                            SharedPreferences.Editor mEditor = mPrefs.edit();
                            mEditor.putString("userImgUri", imgPath).commit();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
            case 3:
            case 4:
                if (resultCode == CHANGES_OCCURRED) {
                    fillHomeList();
                }
                break;
        }
    }

    private Bitmap loadImageFromStorage(String path) {
        Bitmap b = null;
        try {
            File f = new File(path, "profile.jpg");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("myprofileimg", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    ItemTouchHelper itemTouchHelper;

    private void fillHomeList() {


        fillTaskList();
        fillAdviceList();

        ListsDataDb db = new ListsDataDb(MyDiabetesStorage.getInstance(this));
        Cursor cursor = db.getAllLogbookListWithin(10);
        //HomeAdapter homeAdapter = new HomeAdapter(receiverAdviceList, taskListFromYap, cursor,this, yapDroid);
        HomeAdapter homeAdapter = new HomeAdapter(receiverAdviceList, taskListFromYap, cursor, this);

        if (itemTouchHelper != null) {
            itemTouchHelper.attachToRecyclerView(null);
        }
        ItemTouchHelper.Callback callback = new HomeTouchHelper(homeAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(homeList);
        homeList.setItemAnimator(new DefaultItemAnimator());
        homeList.setAdapter(homeAdapter);
        homeList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setFabClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fabOpen) {
                    disableFloationActionButtonOptions();
                    fabOpen = false;
                } else {
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
                startActivityForResult(intent, 2);
                //startActivity(intent);
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

    private void setOffsets() {
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
                offset4 = phantom_fab.getX() - miniFab4.getX() - phantom_fab.getWidth() / 2;
                miniFab4.setTranslationX(offset4);
                offset5 = phantom_fab.getX() - miniFab5.getX() - phantom_fab.getWidth() / 2;
                miniFab5.setTranslationX(offset5);
                offset6 = phantom_fab.getX() - miniFab6.getX() - phantom_fab.getWidth() / 2;
                miniFab6.setTranslationX(offset6);
                offset7 = phantom_fab.getX() - miniFab7.getX() - phantom_fab.getWidth() / 2;
                miniFab7.setTranslationX(offset7);
                return true;
            }
        });
    }


    /**
     * @param view
     * @param ang  How many degrees to rotate
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
        //taskListFromYap = yapDroid.getYapMultipleTasks();
        Task task1 = new Task();
        task1.setSummaryText("Fazer exercicio hoje!");
        task1.setExpText("Hoje fiquei de fazer exercicio. O gim está à minha espera!");
        task1.setUrg(5);

        Task task2 = new Task();
        task2.setSummaryText("Actualizar dados!");
        task2.setExpText("Fazer a sincronização da bomba com a aplicação!");
        task2.setUrg(3);

        taskListFromYap = new ArrayList<>();
        taskListFromYap.add(task1);
        taskListFromYap.add(task2);
    }

    public void fillAdviceList() {
        //receiverAdviceList.addAll(yapDroid.getAllEndAdvices(getApplicationContext()));
        Advice task1 = new Advice();
        task1.setSummaryText("Fazer exercicio hoje!");
        task1.setExpandedText("Hoje fiquei de fazer exercicio. O gim está à minha espera!");
        task1.setUrgency(5);

        Advice task2 = new Advice();
        task2.setSummaryText("Actualizar dados!");
        task2.setExpandedText("Fazer a sincronização da bomba com a aplicação!");
        task2.setUrgency(3);

        ArrayList<Advice> adviceList = new ArrayList<>();
        adviceList.add(task1);
        adviceList.add(task2);
        receiverAdviceList.clear();
        receiverAdviceList.addAll(adviceList);
        Collections.sort(receiverAdviceList);

    }

    public void fillDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        calendar = Calendar.getInstance();
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

        if (item != null) {
            if (item.getTitle().equals("homeSettings")) {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}


