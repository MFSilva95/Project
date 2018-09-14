package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.utils.ImageUtils;


public class CarbsRegister_Input_Interface extends LinearLayout {
    private TextInputLayout carbs_input;
    private ImageButton image_button;
    private ImageButton create_meal_button;
    private CarbsRec carbsData;
    private int default_height;
    private int default_width;

    public Uri getImgUri() {
        return imgUri;
    }

    private Uri imgUri;
    private Bitmap b;

    public NewHomeRegistry.NewHomeRegCallBack getCallBack() {
        return callBack;
    }

    public void setErrorMessage(String errorMessage){
        this.carbs_input.setError(errorMessage);
    }

    private NewHomeRegistry.NewHomeRegCallBack callBack;

    public CarbsRegister_Input_Interface(Context context, NewHomeRegistry.NewHomeRegCallBack call, int default_height, int default_width) {
        super(context);
        this.default_height = default_height;
        this.default_width = default_width;
        callBack = call;
        init();
    }

    private void init() {
        carbsData = new CarbsRec();
        inflate(getContext(), R.layout.meal_content_edit, this);
        this.carbs_input = (TextInputLayout) findViewById(R.id.meal_txt);
        this.image_button = (ImageButton) findViewById(R.id.iv_MealDetail_Photo);
        this.create_meal_button = (ImageButton) findViewById(R.id.create_meal);
        setMealListeners();
        requestFocus();
    }

    public void requestCarbsFocus(){
        carbs_input.requestFocus();
    }

    public boolean validate(){
        try{
            carbsData.setCarbsValue( Integer.parseInt(carbs_input.getEditText().getText().toString()));
        }catch (Exception e){
            carbs_input.setError(getContext().getString(R.string.error_glicInput));
            carbs_input.requestFocus();
            return false;
        }
        if(carbsData.getCarbsValue()>900 || carbsData.getCarbsValue()<0){
            carbs_input.setError(getContext().getString(R.string.error_glicInput));
            carbs_input.requestFocus();
            return false;
        }
        return true;
    }

    public void fill_parameters(CarbsRec carbData) throws Exception {
        carbsData = carbData;
        carbs_input.getEditText().setText(""+carbsData.getCarbsValue());
        if(carbsData.getPhotoPath()!= null){
            imgUri = Uri.parse(carbsData.getPhotoPath());
        }
        ImageView imageView = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
        if (imgUri == null) {
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
        } else {
            setImage(imgUri);
        }
    }
    private void setMealListeners(){
        if (image_button == null) {
            return;
        }
        if (imgUri == null) {
            image_button.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
        } else {
            File temp = new File(imgUri.getPath());
            if(temp.exists()){
                DisplayMetrics displaymetrics = new DisplayMetrics();
                //getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = (int) (displaymetrics.heightPixels * 0.1);
                int width = (int) (displaymetrics.widthPixels * 0.1);
                b = ImageUtils.decodeSampledBitmapFromPath(imgUri.getPath(), width, height);
                carbsData.setPhotoPath(imgUri.getPath());
                image_button.setImageBitmap(b);
            }else{
                imgUri = null;
                image_button.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
            }
        }

        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.addCarbsImage(getContext(), imgUri);
            }
        });

        create_meal_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.createCustomMeal(getContext());
            }
        });


        TextView carbsTextView = (TextView) findViewById(R.id.meal);
        carbsTextView.addTextChangedListener(getCarbsTW());
    }

    public void setUri(Uri u){
        this.imgUri = u;
        if(u==null) {
            image_button.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_photo_camera_grey_600_24dp, null));
        } else {
            this.carbsData.setPhotoPath(imgUri.getPath());
            try {
                setImage(imgUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public void setMealCarbs(int total_carbs){
        carbsData.setCarbsValue(total_carbs);
        carbs_input.getEditText().setText(""+carbsData.getCarbsValue());
        callBack.updateInsulinCalc();
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

    public void setImage(Uri imageUri)throws Exception{
        this.carbsData.setPhotoPath(imageUri.getPath());
        this.imgUri=imageUri;
        if (imgUri != null) {

            File temp = new File(imageUri.getPath());
            if(temp.exists()){
                try {
                    b = ImageUtils.decodeSampledBitmapFromPath(imageUri.getPath(),default_height, default_width);
                }catch (Exception e){
                    e.printStackTrace();
                    //throw new Exception("cant access file");
                }
                image_button.setImageBitmap(b);
            }
        }
    }

}