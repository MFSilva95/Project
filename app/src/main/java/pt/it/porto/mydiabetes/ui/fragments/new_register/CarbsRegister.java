package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Calendar;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.activities.ViewPhoto;
import pt.it.porto.mydiabetes.utils.ImageUtils;


public class CarbsRegister extends LinearLayout {
    private TextInputLayout carbs_input;
    private ImageButton image_button;
    private CarbsRec carbsData;
    private Uri imgUri;
    private Bitmap b;

    public NewHomeRegistry.NewHomeRegCallBack getCallBack() {
        return callBack;
    }

    private NewHomeRegistry.NewHomeRegCallBack callBack;

    public CarbsRegister(Context context, NewHomeRegistry.NewHomeRegCallBack call) {
        super(context);
        callBack = call;
        init();
    }

    private void init() {
        carbsData = new CarbsRec();
        inflate(getContext(), R.layout.meal_content_edit, this);
        this.carbs_input = (TextInputLayout) findViewById(R.id.meal_txt);
        this.image_button = (ImageButton) findViewById(R.id.iv_MealDetail_Photo);

        setMealListeners();
    }

    public void requestCarbsFocus(){
        carbs_input.requestFocus();
    }

    public void fill_parameters(CarbsRec carbData){
        carbsData = carbData;
        carbs_input.getEditText().setText(""+carbsData.getCarbsValue());
        if(carbsData.getPhotoPath()!= null){
            imgUri = Uri.parse(carbData.getPhotoPath());
        }
        ImageView imageView = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
        if (imgUri == null) {
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
        } else {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            //getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = (int) (displaymetrics.heightPixels * 0.1);
            int width = (int) (displaymetrics.widthPixels * 0.1);
            b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
            imageView.setImageBitmap(b);
        }
        //image_button.
    }
    private void setMealListeners(){
        if (image_button == null) {
            return;
        }
        if (imgUri == null) {
            image_button.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
        } else {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            //getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = (int) (displaymetrics.heightPixels * 0.1);
            int width = (int) (displaymetrics.widthPixels * 0.1);
            b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
            image_button.setImageBitmap(b);
        }

        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.addCarbsImage(getContext(), imgUri);
            }
        });

        TextView carbsTextView = (TextView) findViewById(R.id.meal);
        carbsTextView.addTextChangedListener(getCarbsTW());
    }

    public void setUri(Uri u){
        this.imgUri = u;
        if(u==null) {
            image_button.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
        }
    }
    private TextWatcher getCarbsTW(){
        TextWatcher carbsTW = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String carbsS = editable.toString();
                if(carbsS != null){
                    try{
                        int carbs = Integer.parseInt(carbsS);
                        carbsData.setCarbsValue(carbs);
                    }catch (NumberFormatException e){
                        carbsData.setCarbsValue(0);
                    }
                        callBack.updateInsulinCalc();
                }
            }
        };
        return carbsTW;
    }

    private void insertCarbsData(int carbValue){
        final ImageView imageView = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
        if (imageView == null) {
            return;
        }
        if (imgUri != null) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = (int) (displaymetrics.heightPixels * 0.1);
            int width = (int) (displaymetrics.widthPixels * 0.1);
            b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
            imageView.setImageBitmap(b);
        }
        TextInputLayout carbsInput = (TextInputLayout) findViewById(R.id.meal_txt);
        TextView carbsTextView = carbsInput.getEditText();
        carbsTextView.requestFocus();
        carbsTextView.setText(carbValue+"");
        carbsTextView.addTextChangedListener(getCarbsTW());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.addCarbsImage(getContext(), imgUri);
            }
        });
    }

    public CarbsRec save_read(){
        return carbsData;
    }

    public int getCarbs() {
        return carbsData.getCarbsValue();
    }
}