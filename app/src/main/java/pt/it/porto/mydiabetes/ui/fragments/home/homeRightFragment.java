package pt.it.porto.mydiabetes.ui.fragments.home;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import info.abdolahi.CircularMusicProgressBar;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.Badges;
import pt.it.porto.mydiabetes.ui.activities.MyData;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

import static android.app.Activity.RESULT_OK;

/**
 * Created by parra on 21/02/2017.
 */

public class homeRightFragment extends Fragment  {

    private UserInfo myData;
    //private CircleImageView userImg;
    private String userImgFileName = "profilePhoto.png";
    private SharedPreferences mPrefs;
    private String imgUriString;
    private View layout;

    private ImageButton helpButton;
    private ImageView beginnerBadge;
    private TextView beginnerBadgesText;
    private ImageView mediumBadge;
    private TextView mediumBadgesText;
    private ImageView advancedBadge;
    private TextView advancedBadgesText;
    private ImageView currentBadge;

    private TextView levelText;
    private TextView pointsText;
    private CircularMusicProgressBar mCircleView;

    private int countBeginner;
    private int countMedium;
    private int countAdvanced;
    //private int countDaily;
    private BadgeRec dailyBadge;

    private static final int RC_CODE_PICKER = 2000;
    private Bitmap bmp;
    private List<Image> images = new ArrayList<>();

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


        DB_Read read = new DB_Read(getContext());

        int points = LevelsPointsUtils.getPercentageLevels(getContext(),read);
        int lvl = LevelsPointsUtils.getLevel(getContext(),read);
        Log.i("cenas", "LEVEL: "+lvl);
        int nextLvlPoints = LevelsPointsUtils.getPointsNextLevel(getContext(),read);
        int totalPoints = LevelsPointsUtils.getTotalPoints(getContext(), read);

        myData = read.MyData_Read();
        //LinkedList<BadgeRec> list = read.Badges_GetAll();

        countBeginner = read.Badges_GetCount("beginner");
        countMedium = read.Badges_GetCount("medium");
        countAdvanced = read.Badges_GetCount("advanced");
        //countDaily = read.Badges_GetCount("daily");
        dailyBadge = read.getLastDailyMedal();

        read.close();


        mCircleView = (CircularMusicProgressBar) layout.findViewById(R.id.circleView);
        mCircleView.setValue(points);

        levelText = (TextView) layout.findViewById(R.id.numberLevel);
        levelText.setText(lvl+"");
        pointsText = (TextView) layout.findViewById(R.id.numberPoints);
        pointsText.setText(totalPoints+" / "+nextLvlPoints);


        beginnerBadge = (ImageView) layout.findViewById(R.id.beginnerBadge);
        beginnerBadgesText = (TextView) layout.findViewById(R.id.beginnerBadgesText);
        mediumBadgesText = (TextView) layout.findViewById(R.id.beginnerBadgesText);

        if(countBeginner>0){
            beginnerBadge.setImageResource(R.drawable.medal_gold_beginner);
        }


        if( lvl >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            mediumBadge = (ImageView) layout.findViewById(R.id.mediumBadge);
            mediumBadge.setImageResource(R.drawable.medal_gold_medium);
            mediumBadgesText = (TextView) layout.findViewById(R.id.mediumBadgesText);

            if(lvl >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
                advancedBadge = (ImageView) layout.findViewById(R.id.advancedBadge);
                advancedBadge.setImageResource(R.drawable.medal_gold_advanced);
                advancedBadgesText = (TextView) layout.findViewById(R.id.advancedBadgesText);
            }
        }

        helpButton = (ImageButton) layout.findViewById(R.id.helpButton);
        currentBadge = (ImageView) layout.findViewById(R.id.currentBadge);

        setImage();
        setMyDataFromDB(myData);
        updateMedals();

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

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialStyledDialog.Builder(getContext())
                        .setTitle(getString(R.string.badge_help_dialog_title))
                        .setDescription(getString(R.string.badge_help_dialog_desc))
                        .setStyle(Style.HEADER_WITH_ICON)
//                        .setIcon(R.drawable.medal_gold_record_a)
                        .withDialogAnimation(true)
                        .withDarkerOverlay(true)
                        .withIconAnimation(false)
                        .setCancelable(true)
                        .setPositiveText(R.string.okButton)
                        .show();
            }
        });

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int THUMBSIZE = 350;
        if (requestCode == RC_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = ImagePicker.getImages(data);//ImagePicker.EXTRA_SELECTED_IMAGES);
            bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(images.get(0).getPath()), THUMBSIZE, THUMBSIZE);
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
            mCircleView.setImageBitmap(bmp);
            DB_Read rdb = new DB_Read(getContext());
            BadgeUtils.addPhotoBadge(getContext(), rdb);
            rdb.close();
            updateMedals();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {
        super.onResume();

        DB_Read read = new DB_Read(getContext());

        int percentageLvL = LevelsPointsUtils.getPercentageLevels(getContext(),read);
        int pointsToNextLvL = LevelsPointsUtils.getPointsNextLevel(getContext(),read);
        myData = read.MyData_Read();

        countBeginner = read.Badges_GetCount("beginner");
        countMedium = read.Badges_GetCount("medium");
        countAdvanced = read.Badges_GetCount("advanced");
        dailyBadge = read.getLastDailyMedal();

        String numberMedals_total = LevelsPointsUtils.getTotalPoints(getContext(), read)+" / "+pointsToNextLvL;

        read.close();

        mCircleView.setValue(percentageLvL);
        pointsText.setText(numberMedals_total);
        setMyDataFromDB(myData);
        updateMedals();
    }

    private void updateMedals() {
        if(dailyBadge != null){
            String medalType = "medal_"+dailyBadge.getMedal()+"_"+dailyBadge.getType();
            currentBadge.setImageResource(getContext().getResources().getIdentifier(medalType,"drawable",getContext().getPackageName()));
        }
        if(countBeginner > 0){
            beginnerBadge.setImageResource(R.drawable.medal_gold_beginner);
            beginnerBadgesText.setText(countBeginner+"/23");
        }
        if(countMedium > 0){
            mediumBadge.setImageResource(R.drawable.medal_gold_medium);
            mediumBadgesText.setText(countMedium+"/21");
        }
        if(countAdvanced > 0){
            advancedBadge.setImageResource(R.drawable.medal_gold_advanced);
            advancedBadgesText.setText(countAdvanced+"/21");
        }

    }

    private void setImage() {
        mCircleView = (CircularMusicProgressBar) layout.findViewById(R.id.circleView);

        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, userImgFileName);
        if (mypath.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(mypath.getPath());
            mCircleView.setImageBitmap(bmp);
        }

        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=ImagePicker.create(homeRightFragment.this)
                        //.returnAfterFirst(true)
                        .returnMode(ReturnMode.ALL)
                        .folderMode(true)
                        .showCamera(true)
                        .limit(1)
                        //.folderTitle("Album")
                        .toolbarFolderTitle("Album")
                        //.imageTitle("Tap to select images")
                        //.toolbarImageTitle("")
                        .single()
                        .imageDirectory("Camera")
                        .getIntent(homeRightFragment.this.getContext());

                startActivityForResult(intent, RC_CODE_PICKER);
            }
        });
    }

    public void setMyDataFromDB(UserInfo obj) {
        if (obj != null) {
            TextView name = (TextView) layout.findViewById(R.id.name);
            Calendar bday = DateUtils.getDateCalendar(obj.getBirthday());
            int age = DateUtils.getAge(bday);
            name.setTag(obj.getId());
            name.setText(obj.getUsername()+", "+age);
        }
    }

}
