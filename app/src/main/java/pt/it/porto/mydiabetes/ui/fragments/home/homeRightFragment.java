package pt.it.porto.mydiabetes.ui.fragments.home;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.AddEvent;
import pt.it.porto.mydiabetes.ui.activities.Badges;
import pt.it.porto.mydiabetes.ui.activities.MyData;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

import static android.app.Activity.RESULT_OK;

/**
 * Created by parra on 21/02/2017.
 */

public class homeRightFragment extends Fragment  {

    private UserInfo myData;
    private CircleImageView userImg;
    private String userImgFileName = "profilePhoto.png";
    private SharedPreferences mPrefs;
    private String imgUriString;
    private View layout;
    private TextView beginnerBadges;
    private TextView bronzeDailyBadges;
    private TextView silverDailyBadges;
    private TextView goldDailyBadges;
    private ImageView currentBadge;

    private static final int RC_CODE_PICKER = 2000;
    private Bitmap bmp;
    private ArrayList<Image> images = new ArrayList<>();

    public static pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment newInstance() {
        pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment fragment = new pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment();
        return fragment;
    }

    public homeRightFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_home_right, container, false);
        mPrefs = getContext().getSharedPreferences("label", 0);
        imgUriString = mPrefs.getString("userImgUri", null);

        beginnerBadges = (TextView) layout.findViewById(R.id.beginnerBadges);
        bronzeDailyBadges = (TextView) layout.findViewById(R.id.bronzeDailyBadges);
        silverDailyBadges = (TextView) layout.findViewById(R.id.silverDailyBadges);
        goldDailyBadges = (TextView) layout.findViewById(R.id.goldDailyBadges);

        currentBadge = (ImageView) layout.findViewById(R.id.currentBadge);

        currentBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));

        setImage();

        //Read MyData From DB
        DB_Read db_read = new DB_Read(getContext());
        myData = db_read.MyData_Read();
        LinkedList<BadgeRec> list = db_read.Badges_GetAll();
        db_read.close();

        setMyDataFromDB(myData);
        updateMedals(list);

        CardView personalInfo = (CardView) layout.findViewById(R.id.personalInfo);
        CardView badgesInfo = (CardView) layout.findViewById(R.id.badgesInfo);

        personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyData.class);
                startActivity(intent);
            }
        });

        badgesInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Badges.class);
                startActivity(intent);
            }
        });

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePicker.EXTRA_SELECTED_IMAGES);
            bmp = BitmapFactory.decodeFile(images.get(0).getPath());

            ContextWrapper cw = new ContextWrapper(getContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File mypath = new File(directory, userImgFileName);
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
            userImg.setImageBitmap(bmp);
            BadgeUtils.addPhotoBadge(getContext());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {
        super.onResume();
        //Read MyData From DB
        DB_Read db_read = new DB_Read(getContext());
        myData = db_read.MyData_Read();
        LinkedList<BadgeRec> list = db_read.Badges_GetAll();
        db_read.close();

        setMyDataFromDB(myData);
        updateMedals(list);
    }

    private void updateMedals(LinkedList<BadgeRec> list) {
        int countBeginner = 0;
        int countBronzeDaily = 0;
        int countSilverDaily = 0;
        int countGoldDaily = 0;
        int daily =0;
        for (BadgeRec badge : list) {
            if(badge.getType().equals("beginner"))
                countBeginner++;
            if(badge.getType().equals("daily")){
                daily++;
            }
            if(badge.getType().equals("daily") && !badge.getFormattedDate().equals(DateUtils.getFormattedDate(Calendar.getInstance()))){
                if(badge.getMedal().equals("bronze"))
                    countBronzeDaily++;
                if(badge.getMedal().equals("silver"))
                    countSilverDaily++;
                if(badge.getMedal().equals("gold"))
                    countGoldDaily++;
            }
            if(badge.getType().equals("daily") && badge.getFormattedDate().equals(DateUtils.getFormattedDate(Calendar.getInstance()))){
                if(badge.getMedal().equals("bronze")){
                    currentBadge.clearColorFilter();
                }
                if(badge.getMedal().equals("silver")){
                    currentBadge.clearColorFilter();
                    currentBadge.setImageResource(R.drawable.medal_silver_daily);}
                if(badge.getMedal().equals("gold")){
                    currentBadge.clearColorFilter();
                    currentBadge.setImageResource(R.drawable.medal_gold_daily);}
            }
        }
        Log.e("daily size", daily+"");
        beginnerBadges.setText(countBeginner+"/23");
        bronzeDailyBadges.setText(countBronzeDaily+" medalhas");
        silverDailyBadges.setText(countSilverDaily+" medalhas");
        goldDailyBadges.setText(countGoldDaily+" medalhas");
    }

    private void setImage() {
        userImg = (CircleImageView) layout.findViewById(R.id.profile_image);

        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, userImgFileName);
        if (mypath.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(mypath.getPath());
            userImg.setImageBitmap(bmp);
        }

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ImagePickerActivity.class);

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

    public void setMyDataFromDB(UserInfo obj) {
        if (obj != null) {
            TextView name = (TextView) layout.findViewById(R.id.name);
            TextView bDate = (TextView) layout.findViewById(R.id.age);

            name.setTag(obj.getId());
            name.setText(obj.getUsername());
            Calendar bday = DateUtils.getDateCalendar(obj.getBirthday());
            int age = DateUtils.getAge(bday);
            bDate.setText(age+" "+getString(R.string.years));
        }
    }

}
