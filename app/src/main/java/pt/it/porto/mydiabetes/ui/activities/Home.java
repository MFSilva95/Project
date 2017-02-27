package pt.it.porto.mydiabetes.ui.activities;

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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;


    private ListView logbookList;
    private RecyclerView homeList;

    ArrayList<Task> taskListFromYap = new ArrayList<>();
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewHomeRegistry.class);
                startActivity(intent);
            }
        });

        logbookList = (ListView) findViewById(R.id.LogbookActivityList);

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


