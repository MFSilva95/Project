package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.ui.activities.ViewPhoto;
import pt.it.porto.mydiabetes.utils.ImageUtils;


public class CarbsRegister extends LinearLayout {
    public static final String ARG_CARBS = "ARG_CARBS";
    private TextInputLayout carbs_input;
    private ImageButton image_button;
    private CarbsRec carbsData;
    private Uri imgUri;
    private Bitmap b;
    private final static int IMAGE_VIEW = 3;

    public CarbsRegister(Context context) {
        super(context);
        init();
    }

    public CarbsRegister(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CarbsRegister(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        carbsData = new CarbsRec();
        inflate(getContext(), R.layout.meal_content_edit, this);
        this.carbs_input = (TextInputLayout) findViewById(R.id.meal_txt);
        this.image_button = (ImageButton) findViewById(R.id.iv_MealDetail_Photo);
    }
    public boolean canSave(){
        try{
            Integer.parseInt(carbs_input.getEditText().getText().toString());
        }catch (Exception e){
            carbs_input.setError(getContext().getString(R.string.glicInputError));
            carbs_input.requestFocus();
            return false;
        }
        return true;
    }
    public void fill_parameters(Bundle savedIns){
        carbsData = savedIns.getParcelable(ARG_CARBS);
        carbs_input.getEditText().setText(carbsData.getCarbsValue());
        //image_button.
    }
    private void setMealListeners(){
        ImageView imageView = (ImageView) findViewById(R.id.iv_MealDetail_Photo);
        if (imageView == null) {
            return;
        }
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
        TextView carbsTextView = (TextView) findViewById(R.id. meal);
        carbsTextView.addTextChangedListener(getCarbsTW());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUri != null) {
                    final Intent intent = new Intent(getContext(), ViewPhoto.class);
                    Bundle argsToPhoto = new Bundle();
                    argsToPhoto.putString("Path", imgUri.getPath());
                    argsToPhoto.putInt("Id", -1);
                    intent.putExtras(argsToPhoto);
                    //startActivityForResult(intent, IMAGE_VIEW);
                }
            }
        });
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
                }
            }
        };
        return carbsTW;
    }
}