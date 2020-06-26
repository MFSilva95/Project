package pt.it.porto.mydiabetes.ui.fragments.new_register;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;


public class CarbsRegister_Input_Interface extends LinearLayout {
    private TextInputLayout carbs_input;
    private ImageButton image_button;
    private ImageButton create_meal_button;
    private CarbsRec carbsData;

    private Uri imgUri;
    private Bitmap b;


    public NewHomeRegistry.NewHomeRegCallBack getCallBack() {
        return callBack;
    }

    public void setErrorMessage(String errorMessage){
        this.carbs_input.setError(errorMessage);
    }

    private NewHomeRegistry.NewHomeRegCallBack callBack;

    public CarbsRegister_Input_Interface(Context context, NewHomeRegistry.NewHomeRegCallBack call) {
        super(context);
        callBack = call;
        init();
    }

    private void init() {
        carbsData = new CarbsRec();
        inflate(getContext(), R.layout.meal_content_edit, this);
        this.carbs_input = findViewById(R.id.meal_txt);
        this.image_button = findViewById(R.id.iv_MealDetail_Photo);
        this.create_meal_button = findViewById(R.id.create_meal);
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
    }
    private void setMealListeners(){

        create_meal_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.createCustomMeal(getContext());
            }
        });


        TextView carbsTextView = (TextView) findViewById(R.id.meal);
        carbsTextView.addTextChangedListener(getCarbsTW());
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

    public CarbsRec save_read(){
        return carbsData;
    }

    public int getCarbs() {
        return carbsData.getCarbsValue();
    }

    public void setImage(String imageUri){
        this.carbsData.setPhotoPath(imageUri);
    }
    public void setCarbsMealID(int mealID){
        this.carbsData.setMealId(mealID);
    }


    public NewHomeRegistry.MealType getTypeMeal() {
        return this.carbsData.getType_of_meal();
    }
    public void setTypeMeal(NewHomeRegistry.MealType typeMeal){
        carbsData.setType_of_meal(typeMeal);
    }
}