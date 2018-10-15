package pt.it.porto.mydiabetes.ui.fragments.home;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.features.ReturnMode;
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
import java.util.LinkedList;
import java.util.List;

import info.abdolahi.CircularMusicProgressBar;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BadgeRec;
import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.Badges;
import pt.it.porto.mydiabetes.ui.activities.MyData;
import pt.it.porto.mydiabetes.ui.createMeal.activities.CreateMealActivity;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.ImageUtils;
import pt.it.porto.mydiabetes.utils.LevelsPointsUtils;

import static android.app.Activity.RESULT_OK;

/**
 * Created by parra on 21/02/2017.
 */

public class homeRightFragment extends Fragment  {

    private UserInfo myData;
    //private CircleImageView userImg;
    private String userImgFileName = "profilePhoto";
    private String imgUriString;
    private View layout;
    final int THUMBSIZE = 350;

    private int EXTERNAL_CAMERA_PERMISSION_CONSTANT = 991;

    private ImageButton helpButton;
    private ImageView beginnerBadge;
    private TextView beginnerBadgesText;
    private ImageView mediumBadge;
    private TextView mediumBadgesText;
    private ImageView advancedBadge;
    private TextView advancedBadgesText;
    private ImageView currentBadge;

    private Uri currentImageUri;

    private TextView levelText;
    private TextView pointsText;
    private CircularMusicProgressBar mCircleView;

    private int countBeginner;
    private int countMedium;
    private int countAdvanced;
    //private int countDaily;
    private BadgeRec dailyBadge;

    private static final int REQUEST_TAKE_PHOTO = 6;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 7;

    private Bitmap bmp;
    private List<Image> images = new ArrayList<>();

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
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(getContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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


        DB_Read read = new DB_Read(getContext());

        int points = LevelsPointsUtils.getPercentageLevels(getContext(),read);
        int lvl = LevelsPointsUtils.getLevel(getContext(),read);
        //Log.i("cenas", "LEVEL: "+lvl);
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


        if(resultCode == RESULT_OK){
            setProfilePhoto();

            DB_Read rdb = new DB_Read(getContext());
            BadgeUtils.addPhotoBadge(getContext(), rdb);
            rdb.close();
            updateMedals();
            //Toast.makeText(this, "Photo attached", Toast.LENGTH_SHORT).show();
        }

        //
        //if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && data != null) {



            //File profileImg = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes", userImgFileName);

//            if(images.size()>0){
            //if(profileImg.exists()){

//                bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(images.get(0).getPath()), THUMBSIZE, THUMBSIZE);
                //bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(profileImg.getPath()), THUMBSIZE, THUMBSIZE);
//                ContextWrapper cw = new ContextWrapper(getContext());
//                // path to /data/data/yourapp/app_data/imageDir
//
//                //File directory = cw.getDir(Environment.getExternalStorageDirectory()+"/MyDiabetes/imageDir",Context.MODE_PRIVATE);
//                File directory = new File(Environment.getExternalStorageDirectory()+"/MyDiabetes");
//                if(!directory.exists()){
//                    directory.mkdirs();
//                }
//
//                //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//                // Create imageDir
//                File mypath = new File(directory, userImgFileName);
//                FileOutputStream fos = null;
//
//                try {
//                    fos = new FileOutputStream(mypath);
//                    // Use the compress method on the BitMap object to write image to the OutputStream
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                bmp = BitmapFactory.decodeFile(mypath.getPath());
//
//                for(Image img: images){
//                    //File file = new File(images.get(0).getPath());
//                    File file = new File(img.getPath());
//                    deleteFileFromMediaStore(this.getContext().getContentResolver(), file);
//                    //boolean deleted = file.delete();
//                }





            //}

        //}
        super.onActivityResult(requestCode, resultCode, data);
    }

    public  void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[] {canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
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
        File profile_img = new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg");
        if(profile_img.exists()){
            Bitmap bmp = BitmapFactory.decodeFile(profile_img.getAbsolutePath());
            mCircleView.setImageBitmap(Bitmap.createScaledBitmap(bmp, THUMBSIZE, THUMBSIZE, false));
        }

        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA + Manifest.permission.WRITE_EXTERNAL_STORAGE + Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }else{
                    requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }

            }
        });
    }
    private void dispatchTakePictureIntent(){
        currentImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString()+"/MyDiabetes/"+ userImgFileName+".jpg"));
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    private Uri getImgURI(){
            File file = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes", userImgFileName);
            File dir = new File(Environment.getExternalStorageDirectory() + "/MyDiabetes");
            if (!dir.exists()) {
                if (dir.mkdir()) {
                // unable to create directory
                // todo report and recover
                }
            }
        Uri generatedImageUri = Uri.fromFile(file);
        return generatedImageUri;
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

            InputStream input = this.getContext().getContentResolver().openInputStream(currentImageUri);
            if (input == null) {
                Toast.makeText(getContext(),"Unable to save photo",Toast.LENGTH_LONG).show();
            }
            Bitmap pic_bitmap = BitmapFactory.decodeStream(input);
            File photoFile = null;
            try {
                photoFile = createImageFile();
                try (FileOutputStream out = new FileOutputStream(photoFile)) {
                    pic_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                    out.flush();
                    out.close();

                    displayImg(photoFile.getAbsolutePath());

                    Log.i("cenas", "setProfilePhoto: -> "+photoFile.getAbsolutePath());



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


}
