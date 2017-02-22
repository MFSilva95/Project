package pt.it.porto.mydiabetes.ui.activities;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.utils.CustomViewPager;
import pt.it.porto.mydiabetes.ui.listAdapters.homePageAdapter;


public class Home extends BaseActivity {

    private static final String SELECTED_ITEM = "arg_selected_item";

    private static final String TAG = "Home";
    private static final int RC_CODE_PICKER = 2000;
    Bitmap bmp;
    private ArrayList<Image> images = new ArrayList<>();
    private CircleImageView userImg;
    private String filename = "profilePhoto.png";


    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private CustomViewPager mViewPager;
    private PagerAdapter adapter;
    private BottomNavigationView bottomNavigationView;


    @Override
    public String getRegType() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        mViewPager = (CustomViewPager) super.findViewById(R.id.content_home_fragment);
        adapter = new homePageAdapter(super.getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.blockSwipeRight(true);
        mViewPager.blockSwipeLeft(true);
        mViewPager.setCurrentItem(1);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //----------------------nav

        DrawerLayout.DrawerListener Dlistener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
            }

            @Override
            public void onDrawerOpened(View view) {
                userImg = (CircleImageView) findViewById(R.id.profile_image);

                ContextWrapper cw = new ContextWrapper(getBaseContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                // Create imageDir
                File mypath = new File(directory, filename);
                if (mypath.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(mypath.getPath());
                    userImg.setImageBitmap(bmp);
                }

                userImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), ImagePickerActivity.class);

                        intent.putExtra(ImagePicker.EXTRA_FOLDER_MODE, true);
                        intent.putExtra(ImagePicker.EXTRA_MODE, ImagePicker.MODE_SINGLE);
                        intent.putExtra(ImagePicker.EXTRA_SHOW_CAMERA, false);
                        intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGES, images);
                        intent.putExtra(ImagePicker.EXTRA_FOLDER_TITLE, "Album");
                        intent.putExtra(ImagePicker.EXTRA_IMAGE_TITLE, "Tap to select images");
                        intent.putExtra(ImagePicker.EXTRA_IMAGE_DIRECTORY, "Camera");

                        startActivityForResult(intent, RC_CODE_PICKER);
                    }
                });
            }

            @Override
            public void onDrawerClosed(View view) {
            }

            @Override
            public void onDrawerStateChanged(int i) {
            }
        };

        drawerLayout.addDrawerListener(Dlistener);
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
                        intent = new Intent(getApplicationContext(), Preferences.class);
                        startActivity(intent);
                        return true;
                    case R.id.diabetesData:
                        intent = new Intent(getApplicationContext(), SettingsInsulin.class);
                        startActivity(intent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        setupBottomNavigationView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePicker.EXTRA_SELECTED_IMAGES);
            bmp = BitmapFactory.decodeFile(images.get(0).getPath());
            userImg.setImageBitmap(bmp);


            ContextWrapper cw = new ContextWrapper(getBaseContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File mypath = new File(directory, filename);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

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

    private void setupBottomNavigationView() {
        // Get the menu from our navigationBottomView.
        Menu bottomNavigationViewMenu = bottomNavigationView.getMenu();
        // Uncheck the first menu item (the default item which is always checked by the support library is at position 0).
        bottomNavigationViewMenu.findItem(R.id.action_favorites).setChecked(false);
        // Check the wished first menu item to be shown to the user.
        bottomNavigationViewMenu.findItem(R.id.action_schedules).setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Menu bottomNavigationMenu = bottomNavigationView.getMenu();
                for (int i = 0; i < bottomNavigationMenu.size(); i++) {
                    if (item.getItemId() != bottomNavigationMenu.getItem(i).getItemId()) {
                        bottomNavigationMenu.getItem(i).setChecked(false);
                    }
                }

                switch (item.getItemId()) {
                    case R.id.action_favorites :
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.action_schedules :
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.action_music :
                        mViewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });
    }




}


