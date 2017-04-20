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
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.model.Image;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

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

    private LinearLayout mediumLayout;
    private LinearLayout advancedLayout;

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

        mCircleView = (CircularMusicProgressBar) layout.findViewById(R.id.circleView);
        mCircleView.setValue(LevelsPointsUtils.getPercentageLevels(getContext()));

        levelText = (TextView) layout.findViewById(R.id.numberLevel);
        levelText.setText(LevelsPointsUtils.getLevel(getContext())+"");
        pointsText = (TextView) layout.findViewById(R.id.numberPoints);
        pointsText.setText(LevelsPointsUtils.getTotalPoints(getContext())+" / "+LevelsPointsUtils.getPointsNextLevel(getContext()));

        mediumLayout = (LinearLayout) layout.findViewById(R.id.mediumLayout);
        advancedLayout = (LinearLayout) layout.findViewById(R.id.advancedLayout);

        if(LevelsPointsUtils.getLevel(getContext()) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            mediumLayout.setVisibility(View.VISIBLE);
        }
        else{
            mediumLayout.setVisibility(View.GONE);
        }
        if(LevelsPointsUtils.getLevel(getContext()) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            advancedLayout.setVisibility(View.VISIBLE);
        }
        else{
            advancedLayout.setVisibility(View.GONE);
        }

        helpButton = (ImageButton) layout.findViewById(R.id.helpButton);
        beginnerBadge = (ImageView) layout.findViewById(R.id.beginnerBadge);
        beginnerBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        beginnerBadgesText = (TextView) layout.findViewById(R.id.beginnerBadgesText);
        mediumBadge = (ImageView) layout.findViewById(R.id.mediumBadge);
        mediumBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        mediumBadgesText = (TextView) layout.findViewById(R.id.mediumBadgesText);
        advancedBadge = (ImageView) layout.findViewById(R.id.advancedBadge);
        advancedBadge.setColorFilter(ContextCompat.getColor(getContext(),R.color.ef_grey));
        advancedBadgesText = (TextView) layout.findViewById(R.id.advancedBadgesText);

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

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialStyledDialog.Builder(getContext())
                        .setTitle(getString(R.string.badge_help_dialog_title))
                        .setDescription(getString(R.string.badge_help_dialog_desc))
                        .setStyle(Style.HEADER_WITH_ICON)
                        .setIcon(R.drawable.medal_gold_record_a)
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
            images = data.getParcelableArrayListExtra(ImagePicker.EXTRA_SELECTED_IMAGES);
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

        if(LevelsPointsUtils.getLevel(getContext()) >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
            mediumLayout.setVisibility(View.VISIBLE);
        }
        else{
            mediumLayout.setVisibility(View.GONE);
        }
        if(LevelsPointsUtils.getLevel(getContext()) >= LevelsPointsUtils.BADGES_ADVANCED_UNLOCK_LEVEL){
            advancedLayout.setVisibility(View.VISIBLE);
        }
        else{
            advancedLayout.setVisibility(View.GONE);
        }

        levelText.setText(LevelsPointsUtils.getLevel(getContext())+"");
        mCircleView.setValue(LevelsPointsUtils.getPercentageLevels(getContext()));
        pointsText.setText(LevelsPointsUtils.getTotalPoints(getContext())+" / "+LevelsPointsUtils.getPointsNextLevel(getContext()));

        setMyDataFromDB(myData);
        updateMedals(list);
    }

    private void updateMedals(LinkedList<BadgeRec> list) {
        int countBeginner = 0;
        int countMedium = 0;
        int countAdvanced = 0;
        for (BadgeRec badge : list) {
            if(badge.getType().equals("beginner"))
                countBeginner++;
            if(badge.getType().equals("medium"))
                countMedium++;
            if(badge.getType().equals("advanced"))
                countAdvanced++;
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

        if(countBeginner > 0)
            beginnerBadge.clearColorFilter();
        if(countMedium > 0)
            mediumBadge.clearColorFilter();
        if(countAdvanced > 0)
            advancedBadge.clearColorFilter();

        beginnerBadgesText.setText(countBeginner+"/23");
        mediumBadgesText.setText(countMedium+"/21");
        advancedBadgesText.setText(countAdvanced+"/21");
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
                Intent intent = new Intent(getContext(), ImagePickerActivity.class);

                intent.putExtra(ImagePicker.EXTRA_FOLDER_MODE, true);
                intent.putExtra(ImagePicker.EXTRA_MODE, ImagePicker.MODE_SINGLE);
                intent.putExtra(ImagePicker.EXTRA_SHOW_CAMERA, true);
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
            Calendar bday = DateUtils.getDateCalendar(obj.getBirthday());
            int age = DateUtils.getAge(bday);
            name.setTag(obj.getId());
            name.setText(obj.getUsername()+", "+age);
        }
    }

}
