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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.AddEvent;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by parra on 21/02/2017.
 */

public class homeRightFragment extends Fragment  {

    private CircleImageView userImg;
    private String userImgFileName = "profilePhoto.png";
    SharedPreferences mPrefs;
    String imgUriString;
    View layout;


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

        setImage();

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
        }
        super.onActivityResult(requestCode, resultCode, data);
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

}
