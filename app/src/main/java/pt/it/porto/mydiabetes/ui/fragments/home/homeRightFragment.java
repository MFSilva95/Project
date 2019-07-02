package pt.it.porto.mydiabetes.ui.fragments.home;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cdev.achievementview.AchievementView;
import com.esafirm.imagepicker.model.Image;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import info.abdolahi.CircularMusicProgressBar;
import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.RankingService;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.database.Preferences;
import pt.it.porto.mydiabetes.ui.activities.Badges;
import pt.it.porto.mydiabetes.ui.activities.MyData;
import pt.it.porto.mydiabetes.ui.activities.SettingsImportExport;
import pt.it.porto.mydiabetes.ui.dialogs.FeatureWebSyncDialog;
import pt.it.porto.mydiabetes.ui.dialogs.RankWebSyncDialog;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.FileProvider;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * Created by parra on 21/02/2017.
 */

public class homeRightFragment extends Fragment {

    private UserInfo myData;
    private String userImgFileName = "profilePhoto";
    public static View layout;
    final int THUMBSIZE = 350;

    private ImageButton helpButton;
    private ImageView beginnerBadge;
    private TextView beginnerBadgesText;
    private ImageView mediumBadge;
    private TextView mediumBadgesText;
    private ImageView advancedBadge;
    private TextView advancedBadgesText;
    private ImageView currentBadge;

    private ImageButton helpButtonPersonal;
    private TextView averageText;
    private TextView variabilityText;
    private TextView dailyRecordNumber;
    private TextView streakText;
    private TextView streak_days;
    private TextView records_left;
    private RelativeLayout competitionSection;
    private Button hideShowCompetition;
    public static LinearLayout missingAccount;
    public static LinearLayout missingNetwork;
    private LinearLayout startRecordsMessage;
    private LinearLayout streakDaysMessage;
    private LinearLayout leftDaysMessage;
    private LinearLayout congratsMessage;

    public static TextView bestPoints;
    public static TextView bestStreak;
    public static TextView points_g;
    public static TextView streak_g;
    public static TextView points_w;
    public static TextView streak_w;
    public static TextView glycaemia_w;
    public static TextView hyperhypo_w;

    private Dialog showDialog;

    private Uri currentImageUri;

    private TextView levelText;
    private TextView pointsText;
    private CircularMusicProgressBar mCircleView;

    private int countBeginner;
    private int countMedium;
    private int countAdvanced;
    //private int countDaily;
    private BadgeRec dailyBadge;

    private AchievementView achievementView;


    private static final int REQUEST_TAKE_PHOTO = 6;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 7;
    private static final int RIGHT_FRAGMENT_PICTURE = 8;


    private Bitmap bmp;
    private List<Image> images = new ArrayList<>();

    private boolean winBadge = false;

    public static pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment newInstance() {
        pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment fragment = new pt.it.porto.mydiabetes.ui.fragments.home.homeRightFragment();
        return fragment;
    }

    public homeRightFragment() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT){
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this.getContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == RIGHT_FRAGMENT_PICTURE){
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                File profile_img = new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg");
                if(profile_img.exists()){
                    Bitmap bmp = BitmapFactory.decodeFile(profile_img.getAbsolutePath());
                    mCircleView.setImageBitmap(Bitmap.createScaledBitmap(bmp, THUMBSIZE, THUMBSIZE, false));
                }
            }else{
                Toast.makeText(this.getContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void medals_display_init(){
        DB_Read read = new DB_Read(getContext());

        int points = LevelsPointsUtils.getPercentageLevels(getContext(),read);
        int lvl = LevelsPointsUtils.getLevel(getContext(),read);

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


        if(lvl >= LevelsPointsUtils.BADGES_MEDIUM_UNLOCK_LEVEL){
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


        medals_display_init();

        setImage();
        setMyDataFromDB(myData);
        updateMedals();

        helpButtonPersonal = (ImageButton) layout.findViewById(R.id.helpButtonPersonal);
        CardView personalInfo = (CardView) layout.findViewById(R.id.personalInfo);
        CardView badgesInfo = (CardView) layout.findViewById(R.id.badgesInfo);
        final TextView clickToCreateAccount = (TextView) layout.findViewById(R.id.clickToCreateAccount);

        achievementView = layout.findViewById(R.id.achievement_view);

        competitionSection = (RelativeLayout) layout.findViewById(R.id.competitionSection);
        missingAccount = (LinearLayout) layout.findViewById(R.id.missingAccount);
        missingNetwork = (LinearLayout) layout.findViewById(R.id.missingNetwork);
        hideShowCompetition = (Button) layout.findViewById(R.id.hideShowCompetition);

        points_g = (TextView) layout.findViewById(R.id.points_g);
        streak_g = (TextView) layout.findViewById(R.id.streak_g);
        points_w = (TextView) layout.findViewById(R.id.points_w);
        streak_w = (TextView) layout.findViewById(R.id.streak_w);
        glycaemia_w = (TextView) layout.findViewById(R.id.glycaemia_w);
        hyperhypo_w = (TextView) layout.findViewById(R.id.hyperhypo_w);

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

        helpButtonPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialStyledDialog.Builder(getContext())
                        .setDescription(getString(R.string.badge_help_personal_dialog_desc))
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

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialStyledDialog.Builder(getContext())
                        .setTitle(getString(R.string.badge_help_dialog_title))
                        .setDescription(getString(R.string.badge_help_dialog_desc))
                        //.setStyle(Style.HEADER_WITH_TITLE)
//                        .setIcon(R.drawable.medal_gold_record_a)
                        .withDialogAnimation(true)
                        .withDarkerOverlay(true)
                        .withIconAnimation(false)
                        .setCancelable(true)
                        .setPositiveText(R.string.okButton)
                        .show();
            }
        });

        hideShowCompetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (competitionSection.getVisibility() == View.GONE) {
                    // necessário ver se a pessoa tem conta
                    // em caso de ter conta, ver se tem net para fazer o update
                    // antes de fazer o update ver se a data do ultimo update está dentro da semana corrente
                    // ou seja, para saber se fez já update para não estar a fazer muitas vezes

                    if (accountExistence()) {
                        // if account exists
                        missingAccount.setVisibility(View.GONE);
                    } else {
                        // account needed message with fast account creation
                        missingAccount.setVisibility(View.VISIBLE);
                    }

                    // JOBSCHEDULER
                    if (isTimeToRankUpdate(getContext()) && missingAccount.getVisibility() == View.GONE) {
                        missingNetwork.setVisibility(View.VISIBLE);
                        getRankings();
                    }

                    competitionSection.setVisibility(View.VISIBLE);
                    hideShowCompetition.setText(getContext().getString(R.string.competitionTitleHide));
                } else {
                    competitionSection.setVisibility(View.GONE);
                    hideShowCompetition.setText(getContext().getString(R.string.competitionTitleShow));
                }
            }
        });

        clickToCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RankWebSyncDialog webSyncDialog = new RankWebSyncDialog();
                webSyncDialog.show(getActivity().getSupportFragmentManager(), "editAccount");
                webSyncDialog.dismiss();
                Dialog dialog = webSyncDialog.getUserDataPopUp(getContext(), -1, -1);
            }
        });
        return layout;
    }

    public void getRankings() {
        JobScheduler jobScheduler;
        int MYJOBID = 1;
        jobScheduler = (JobScheduler)getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName jobService = new ComponentName(getActivity().getPackageName(), RankingService.class.getName());
        JobInfo jobInfo = new JobInfo.Builder(MYJOBID,jobService).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY). build();
        jobScheduler.schedule(jobInfo);
    }

    public boolean accountExistence() {
        if (Preferences.getUsername(getContext()) == null || Preferences.getPassword(getContext()) == null) {
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            setProfilePhoto();

            DB_Read rdb = new DB_Read(getContext());
            winBadge = BadgeUtils.addPhotoBadge(getContext(), rdb);
            rdb.close();
            updateMedals();
            if(winBadge){
                achievementView.show(getContext().getString(R.string.congratsMessage1), getContext().getString(R.string.photoBadgeWon));
                winBadge = false;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("ENTRA: ");
        competitionSection.setVisibility(View.GONE);
        hideShowCompetition.setText(getContext().getString(R.string.competitionTitleShow));
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
        setPersonalInfo();
        setRankInfo(getContext());
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

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            File profile_img = new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg");
            if(profile_img.exists()){
                Bitmap bmp = BitmapFactory.decodeFile(profile_img.getAbsolutePath());
                mCircleView.setImageBitmap(Bitmap.createScaledBitmap(bmp, THUMBSIZE, THUMBSIZE, false));
            }
        }else{
            //requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RIGHT_FRAGMENT_PICTURE);
        }



        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA + Manifest.permission.WRITE_EXTERNAL_STORAGE + Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    try {
                        dispatchTakePictureIntent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }

            }
        });
    }
    private void dispatchTakePictureIntent() throws IOException {
        File picFile = new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg");
        picFile.createNewFile(); // Eduardo was here :c
        if(android.os.Build.VERSION.SDK_INT>=24){
            currentImageUri = FileProvider.getUriForFile(this.getContext(), BuildConfig.APPLICATION_ID+".provider", picFile);
        }else{
            currentImageUri = Uri.fromFile(picFile);
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
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


    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = userImgFileName;
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void setProfilePhoto() {

        try {


            Bitmap pic_bitmap;
            if(android.os.Build.VERSION.SDK_INT>=28){
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContext().getContentResolver(), currentImageUri);
                pic_bitmap = ImageDecoder.decodeBitmap(source);
            }else{
                InputStream input = this.getContext().getContentResolver().openInputStream(currentImageUri);
                if (input == null) {
                    Toast.makeText(getContext(),"Unable to save photo",Toast.LENGTH_LONG).show();
                    return;
                }
                pic_bitmap = BitmapFactory.decodeStream(input);
            }

            File photoFile = null;
            try {
                photoFile = createImageFile();
                try (FileOutputStream out = new FileOutputStream(photoFile)) {
                    pic_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                    out.flush();
                    out.close();

                    displayImg(photoFile.getAbsolutePath());

                    //Log.i("cenas", "setProfilePhoto: -> "+photoFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Toast.makeText(getContext(),"Unable to save photo",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void displayImg(String path){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        mCircleView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, THUMBSIZE, THUMBSIZE, false));

//        if(thumbnailPhotoView.getVisibility() == View.GONE) {
//            cameraPlaceholder.setVisibility(View.GONE);
//            thumbnailPhotoView.setVisibility(View.VISIBLE);
//        }
    }

    private void setPersonalInfo() {

        averageText = (TextView) layout.findViewById(R.id.averageText);
        variabilityText = (TextView) layout.findViewById(R.id.variabilityText);

        DB_Read rdb = new DB_Read(getContext());
        ArrayList<Integer> infoToday = rdb.getPersonalInfo(0);
        rdb.close();
        if (infoToday.size() != 0) {

            int averageToday = infoToday.get(0);
            int variabiToday = (int) (infoToday.get(1)*100/infoToday.get(0));

            averageText.setText(String.valueOf(averageToday));
            variabilityText.setText(String.valueOf(variabiToday));

            // painting text given the quality of the values
            // average values
            if (averageToday < 70 || averageToday > 180) {
                averageText.setTextColor(getResources().getColor(R.color.red));
            } else if ((averageToday >= 70 && averageToday < 90) || (averageToday > 160 && averageToday <= 180)) {
                averageText.setTextColor(getResources().getColor(R.color.orange));
            } else {
                averageText.setTextColor(getResources().getColor(R.color.green));
            }

            // variability values
            if (variabiToday > 40) variabilityText.setTextColor(getResources().getColor(R.color.red));
            else if (variabiToday > 36 && variabiToday <= 40) variabilityText.setTextColor(getResources().getColor(R.color.orange));
            else variabilityText.setTextColor(getResources().getColor(R.color.green));
        }

        //bestPoints.setText(+" "+getResources().getQuantityString(R.plurals.numberOfDays, ));
        getStreakValue();
    }

    public void getStreakValue() {
        streakText = (TextView) layout.findViewById(R.id.streakText);
        streak_days = (TextView) layout.findViewById(R.id.streak_days);
        records_left = (TextView) layout.findViewById(R.id.records_left);
        dailyRecordNumber = (TextView) layout.findViewById(R.id.dailyRecordNumber);
        leftDaysMessage = (LinearLayout) layout.findViewById(R.id.leftDaysMessage);
        congratsMessage = (LinearLayout) layout.findViewById(R.id.congratsMessage);
        startRecordsMessage = (LinearLayout) layout.findViewById(R.id.startRecordsMessage);
        streakDaysMessage = (LinearLayout) layout.findViewById(R.id.streakDaysMessage);

        int recordGoal = 6;
        DB_Read rdb = new DB_Read(getContext());

        int todayRecords = rdb.getGlyRecordsNumberByDay(0);
        int yesterdayRecords = rdb.getGlyRecordsNumberByDay(1);
        rdb.close();

        Boolean changes = false;

        int streakDays = myData.getCurrentStreak();
        int maxStreak = myData.getMaxStreak();

        // reset streak if yesterday didn't complete daily goal
        if (yesterdayRecords < recordGoal) {
            streakDays = 0;
            myData.setCurrentStreak(streakDays);
            changes = true;
        }

        // in case of daily goal won
        if (todayRecords >= recordGoal) {
            changes = true;
            leftDaysMessage.setVisibility(View.GONE);
            congratsMessage.setVisibility(View.VISIBLE);
            streakDays++;
            // if current streak is the best streak
            if (streakDays > maxStreak) {
                maxStreak = streakDays;
                myData.setMaxStreak(maxStreak);
            }
            // update personal data values
            myData.setCurrentStreak(streakDays);
        } else {
            leftDaysMessage.setVisibility(View.VISIBLE);
            congratsMessage.setVisibility(View.GONE);
            records_left.setText((recordGoal - todayRecords) + " " + getResources().getQuantityString(R.plurals.numberOfGlyc, recordGoal - todayRecords));
        }

        if(changes){
            DB_Write wdb = new DB_Write(getContext());
            wdb.MyData_Save(myData);
            wdb.close();
        }

        if (streakDays == 0) {
            streakDaysMessage.setVisibility(View.GONE);
            startRecordsMessage.setVisibility(View.VISIBLE);
            streakText.setVisibility(View.GONE);
        } else {
            startRecordsMessage.setVisibility(View.GONE);
            streakDaysMessage.setVisibility(View.VISIBLE);
            streak_days.setText(streakDays + " " + getResources().getQuantityString(R.plurals.numberOfDays, streakDays));
            streakText.setVisibility(View.VISIBLE);
            streakText.setText("x"+streakDays);
        }
        dailyRecordNumber.setText(todayRecords+" / "+recordGoal);
    }

    public static boolean isTimeToRankUpdate(Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        Calendar c1 = Calendar.getInstance();
        Date today = c1.getTime();
        c1.add(Calendar.DATE, -8);
        pt.it.porto.mydiabetes.database.Preferences.saveLastRankUpdate(context, dateFormat.format(c1.getTime()));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);

        Date lastRankUpdate = Preferences.getLastRankUpdate(context);
        Date lastWeekEnd = c.getTime();
        c.add(Calendar.DATE,-7);
        Date lastWeekStart = c.getTime();
        c.add(Calendar.DATE, 14);
        Date newUpdate = c.getTime();

        if (today.after(lastRankUpdate) || lastRankUpdate == null) {
            pt.it.porto.mydiabetes.database.Preferences.saveLastRankUpdate(context, dateFormat.format(newUpdate));
            //get number of records in last week
            DB_Read rdb = new DB_Read(context);
            long diffStart = getDifferenceDays(lastWeekStart, today);
            long diffEnd = getDifferenceDays(lastWeekEnd, today);
            int records = rdb.getRecordsNumbersByDate(diffStart, diffEnd);
            rdb.close();
            //don't update layout in case of 0 records
            if (records > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static void setRankInfo(Context context) {
        bestPoints = (TextView) layout.findViewById(R.id.bestPoints);
        bestStreak = (TextView) layout.findViewById(R.id.bestStreak);
        String[] ranks = Preferences.getRankInfo(context);

        if (ranks[0] != null) {
            int value = (int) Math.round(Double.parseDouble(ranks[0]));
            if (value != -1) homeRightFragment.bestPoints.setText(String.valueOf(value));
            else homeRightFragment.bestPoints.setText(R.string.n_a);
        } else {
            homeRightFragment.bestPoints.setText(R.string.n_a);
        }
        if (ranks[1] != null) {
            int value = (int) Math.round(Double.parseDouble(ranks[1]));
            if (value != -1) homeRightFragment.bestStreak.setText(String.valueOf(value));
            else homeRightFragment.bestStreak.setText(R.string.n_a);
        } else {
            homeRightFragment.bestStreak.setText(R.string.n_a);
        }
        if (ranks[2] != null) {
            int value = (int) Math.round(Double.parseDouble(ranks[2]));
            if (value != -1) homeRightFragment.points_g.setText(String.valueOf(value));
            else homeRightFragment.points_g.setText(R.string.n_a);
        } else {
            homeRightFragment.points_g.setText(R.string.n_a);
        }
        if (ranks[3] != null) {
            int value = (int) Math.round(Double.parseDouble(ranks[3]));
            if (value != -1) homeRightFragment.streak_g.setText(String.valueOf(value));
            else homeRightFragment.streak_g.setText(R.string.n_a);
        } else {
            homeRightFragment.streak_g.setText(R.string.n_a);
        }
        if (ranks[4] != null) {
            int value = (int) Math.round(Double.parseDouble(ranks[4]));
            if (value != -1) homeRightFragment.points_w.setText(String.valueOf(value));
            else homeRightFragment.points_w.setText(R.string.n_a);
        } else {
            homeRightFragment.points_w.setText(R.string.n_a);
        }
        if (ranks[5] != null) {
            int value = (int) Math.round(Double.parseDouble(ranks[5]));
            if (value != -1) homeRightFragment.streak_w.setText(String.valueOf(value));
            else homeRightFragment.streak_w.setText(R.string.n_a);
        } else {
            homeRightFragment.streak_w.setText(R.string.n_a);
        }
        if (ranks[6] != null) {
            int value = (int) Math.round(Double.parseDouble(ranks[6]));
            if (value != -1) homeRightFragment.glycaemia_w.setText(String.valueOf(value));
            else homeRightFragment.glycaemia_w.setText(R.string.n_a);
        } else {
            homeRightFragment.glycaemia_w.setText(R.string.n_a);
        }
        if (ranks[7] != null) {
            int value = (int) Math.round(Double.parseDouble(ranks[7]));
            if (value != -1) homeRightFragment.hyperhypo_w.setText(String.valueOf(value));
            else homeRightFragment.hyperhypo_w.setText(R.string.n_a);
        } else {
            homeRightFragment.hyperhypo_w.setText(R.string.n_a);
        }
    }
}
