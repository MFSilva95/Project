package pt.it.porto.mydiabetes.ui.createMeal.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.BaseActivity;
import pt.it.porto.mydiabetes.ui.createMeal.adapters.CreateMealListAdapter;
import pt.it.porto.mydiabetes.ui.createMeal.db.DataBaseHelper;
import pt.it.porto.mydiabetes.ui.createMeal.utils.LoggedMeal;
import pt.it.porto.mydiabetes.ui.createMeal.utils.MealItem;
import pt.it.porto.mydiabetes.ui.createMeal.utils.RecyclerItemTouchHelper;
import pt.it.porto.mydiabetes.utils.FileProvider;


public class CreateMealActivity extends BaseActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final int REQUEST_MEAL_ITEM = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int IMAGE_VIEW = 3;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 4;
    private float FPU = 0;

    private TextView mealTotalCarbsTextView;
    private TextView mealTotalLipidsTextView;
    private TextView mealTotalProteinTextView;

    private TextView emptyListMessageView;
    private ImageView cameraPlaceholder;
    private CircleImageView thumbnailPhotoView;

    private FloatingActionButton addMealItemButton;
    private RecyclerView mealItemListView;
    private CreateMealListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private List<MealItem> currentMealItemList;
    private String currentMealPhotoPath;
    private String currentMealName;
    private int currentMealId;     // -1 If is a new meal

    private int editTextCarbsReg;

    private boolean isUpdate = false;
    private DataBaseHelper dbHelper;
    private Uri currentImageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);

        dbHelper = new DataBaseHelper(this);
        currentMealItemList = new ArrayList<>();
        currentMealId = -1;

        addMealItemButton = findViewById(R.id.add_item_fab);
        mealTotalCarbsTextView = findViewById(R.id.meal_total_carbs);
        mealTotalLipidsTextView = findViewById(R.id.meal_total_lipids);
        mealTotalProteinTextView = findViewById(R.id.meal_total_protein);
        emptyListMessageView = findViewById(R.id.empty_message);
        thumbnailPhotoView = findViewById(R.id.photo_view);
        cameraPlaceholder = findViewById(R.id.camera_placeholder);

        initListeneres();

        mAdapter = new CreateMealListAdapter(currentMealItemList, this);
        mealItemListView = findViewById(R.id.meal_items_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mealItemListView.setLayoutManager(mLinearLayoutManager);
        mealItemListView.setHasFixedSize(true);
        mealItemListView.setNestedScrollingEnabled(false);
        mealItemListView.setItemAnimator(new DefaultItemAnimator());
        mealItemListView.setAdapter(mAdapter);

        if( getIntent().hasExtra("reg_carbs")){
            editTextCarbsReg = getIntent().getExtras().getInt("reg_carbs");
        }

        if(getIntent().hasExtra("meal_obj")) {
            LoggedMeal meal = getIntent().getExtras().getParcelable("meal_obj");

            createMeal(meal);
        } else{
            if(editTextCarbsReg > 0)
                mAdapter.addItem(new MealItem(-1, getString(R.string.extra_carbs), (float)editTextCarbsReg,0,0));
        }

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mealItemListView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT){
            if (ActivityCompat.checkSelfPermission(CreateMealActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ActivityCompat.checkSelfPermission(CreateMealActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }else{
                Toast.makeText(getBaseContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CreateMealListAdapter.ViewHolder) {

            deleteItemFromMeal(viewHolder.getAdapterPosition());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case EXTERNAL_STORAGE_PERMISSION_CONSTANT:
                if (ActivityCompat.checkSelfPermission(CreateMealActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ActivityCompat.checkSelfPermission(CreateMealActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }else{
                    Toast.makeText(getBaseContext(),R.string.all_permissions,Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_MEAL_ITEM:
                if(resultCode == RESULT_OK && data.hasExtra("meal_item")){
                    MealItem item = data.getExtras().getParcelable("meal_item");
                    if(item.getId() == -1){
                        for(MealItem i : currentMealItemList){
                            if(i.getId() == -1){
                                float new_value = i.getCarbs() + item.getCarbs();
                                i.setCarbs(new_value);
                                mAdapter.updateItem(i);
                                return;
                            }
                        }

                        mAdapter.addItem(item);
                    }
                    else
                        mAdapter.addItem(item);
                } else if(resultCode == RESULT_OK && data.hasExtra("logged_meal")){
                    LoggedMeal meal = data.getExtras().getParcelable("logged_meal");

                    currentMealPhotoPath = meal.getThumbnailPath();
                    if(currentMealPhotoPath != null) {
                        displayImg(currentMealPhotoPath);
                    }
                    currentMealName = meal.getName();

                    if(meal.getExtraCarbs() > 0){
                        currentMealItemList.add(new MealItem(-1, "H.Carb Extra", (float)meal.getExtraCarbs(),0,0));
                    } else if(meal.getExtraCarbs() < 0){
                        messageToUser();
                    }
                    currentMealItemList.addAll(meal.getItemList());
                    mAdapter.notifyDataSetChanged();
                    updateTotalCarbsDisplay();

                    if(!meal.isRegistered() && !meal.isFavourite()){
                        currentMealId = meal.getId();
                        isUpdate = true;
                    }
                }
                break;

            case REQUEST_TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    setMealPhoto(data);
                }
                break;

            case IMAGE_VIEW:
                if(resultCode == RESULT_OK){
                    currentMealPhotoPath = null;
                    thumbnailPhotoView.setVisibility(View.GONE);
                    cameraPlaceholder.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_meal, menu);

        if(isUpdate){
            MenuItem item = menu.findItem(R.id.save_meal);
            item.setVisible(false);
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getString(R.string.create_meal_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.log_meal:
                logMeal();
                return true;
            case R.id.save_meal:
                showSaveMealDialog();
                return true;
            case R.id.take_photo:
                dispatchTakePictureIntent();
                return true;
            case R.id.delete_all:
                deleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initListeneres(){
        addMealItemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectMealActivity.class);
                startActivityForResult(intent,REQUEST_MEAL_ITEM);
            }
        });

        thumbnailPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle argsToPhoto = new Bundle();
                argsToPhoto.putString("Path", currentMealPhotoPath);
                argsToPhoto.putInt("Id", -1);
            }
        });

        cameraPlaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

    }

    private void messageToUser(){
        mealTotalCarbsTextView.setTextColor(getResources().getColor(R.color.edittext_error_color));
        Snackbar snackbar = Snackbar.make(addMealItemButton,R.string.error_meal_act, 5000);
        snackbar.getView().setBackgroundColor(this.getResources().getColor(R.color.primary));
        snackbar.show();
    }

    private void createMeal(LoggedMeal meal){
        if(meal != null){
            currentMealName = meal.getName();
            currentMealPhotoPath = meal.getThumbnailPath();
            if(currentMealPhotoPath != null) {
                File img = new File(currentMealPhotoPath);
                if(img.exists()){displayImg(currentMealPhotoPath);}else{currentMealPhotoPath = null;}
            }

            for(MealItem item : meal.getItemList()){
                mAdapter.addItem(item);
            }

            int carbs_diff = editTextCarbsReg - meal.getTotalCarbs(false);
            if(carbs_diff > 0){
                mAdapter.addItem(new MealItem(-1, getString(R.string.extra_carbs), (float)carbs_diff,0,0));
            } else if(carbs_diff < 0){
                messageToUser();
            }

            if(meal.getId() != -1) {
                currentMealId = meal.getId();
                isUpdate = true;
            }
        }
    }

    public int getLipidHours(){
        int hours_check = 3;
        if(FPU>=2){hours_check = 4;}
        if(FPU>=3){hours_check = 5;}
        if(FPU>=4){hours_check = 8;}
        return hours_check;
    }

    private void updateTotalCarbsDisplay(){
        ImageView info_lipids;
        info_lipids = findViewById(R.id.info_lipids);
        ImageView info_protein;
        info_protein = findViewById(R.id.info_protein);

        info_lipids.setVisibility(View.INVISIBLE);
        info_protein.setVisibility(View.INVISIBLE);

        float total_carbs = 0;
        float total_lipids = 0;
        float total_protein = 0;

        for(MealItem m : currentMealItemList){
            total_carbs = total_carbs + m.getCarbs();
            total_lipids = total_lipids + m.getLipids();
            total_protein = total_protein + m.getProtein();
        }

        FPU = (total_protein* 4 + total_lipids * 9)/100;
        float CU = total_carbs/10;

        if(FPU >= 1){

            //if(total_lipids>total_protein){
                mealTotalLipidsTextView.setTextColor(getResources().getColor(R.color.edittext_error_color));
                info_lipids.setVisibility(View.VISIBLE);



                mealTotalLipidsTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String descriptionTxt = getResources().getString(R.string.meal_lipids_explain, getLipidHours());

                        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(view.getContext());
                        builder1.setTitle(getString(R.string.meal_lipids_description));
                        builder1.setMessage(descriptionTxt);
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                getString(R.string.okButton),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        builder1.show();

                    }
                });
                info_lipids.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String descriptionTxt = getResources().getString(R.string.meal_lipids_explain, getLipidHours());

                        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(view.getContext());
                        builder1.setTitle(getString(R.string.meal_lipids_description));
                        builder1.setMessage(descriptionTxt);
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                getString(R.string.okButton),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        builder1.show();
                    }
                });
//            }else{
//                mealTotalProteinTextView.setTextColor(getResources().getColor(R.color.md_edittext_error));
//                info_protein.setVisibility(View.VISIBLE);
//                info_protein.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        new MaterialStyledDialog.Builder(view.getContext())
//                                .setTitle(getString(R.string.meal_lipids_description))
//                                .setDescription(getString(R.string.meal_lipids_explain))
//                                .setStyle(Style.HEADER_WITH_ICON)
////                        .setIcon(R.drawable.medal_gold_record_a)
//                                .withDialogAnimation(true)
//                                .withDarkerOverlay(true)
//                                .withIconAnimation(false)
//                                .setCancelable(true)
//                                .setPositiveText(R.string.okButton)
//                                .show();
//                    }
//                });
//            }


        }else{
            mealTotalLipidsTextView.setTextColor(getResources().getColor(R.color.accent));
            mealTotalProteinTextView.setTextColor(getResources().getColor(R.color.accent));
            mealTotalLipidsTextView.setOnClickListener(null);
            mealTotalProteinTextView.setOnClickListener(null);
        }

        if(total_carbs <= editTextCarbsReg) {
            mealTotalCarbsTextView.setTextColor(getResources().getColor(R.color.accent));
        }

        mealTotalCarbsTextView.setText(new StringBuilder(String.format(Locale.US,"%.2f", total_carbs) + "g"));
        mealTotalLipidsTextView.setText(new StringBuilder(String.format(Locale.US,"%.2f", total_lipids) + "g"));
        mealTotalProteinTextView.setText(new StringBuilder(String.format(Locale.US,"%.2f", total_protein) + "g"));

        if(currentMealItemList.size() > 0)
            emptyListMessageView.setVisibility(View.GONE);
        else
            emptyListMessageView.setVisibility(View.VISIBLE);
    }

    public void scrollToPosition(int position){
        mLinearLayoutManager.scrollToPosition(position);
    }

    public void notitfyItemListChange(){
        updateTotalCarbsDisplay();
    }

    private boolean validate(){
        if(currentMealItemList.size() == 0){
            Snackbar snackbar = Snackbar.make(addMealItemButton,getString(R.string.empty_list_meal), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(this.getResources().getColor(R.color.primary));
            snackbar.show();
            return false;
        }

        return true;
    }

    private void saveMeal(){
        if(!validate())
            return;

        List<MealItem> mealFoodList = new ArrayList<>();
        int extra_carbs = 0;
        for(MealItem item : currentMealItemList){
            if(item.getId() == -1){
                extra_carbs = (int)item.getCarbs();
            } else{
                mealFoodList.add(item);
            }
        }

        LoggedMeal meal = new LoggedMeal(mealFoodList);
        meal.setThumbnailPath(currentMealPhotoPath);
        meal.setName(currentMealName);
        meal.setExtraCarbs(extra_carbs);
        meal.setFavourite(true);

        if(dbHelper.insertMeal(meal) != -1) {
            Snackbar snackbar = Snackbar.make(addMealItemButton,getString(R.string.meal_saved), Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(this.getResources().getColor(R.color.primary));
            snackbar.show();
        }
    }

    private void logMeal(){

        LoggedMeal meal;
        if(isUpdate){
            List<MealItem> mealFoodList = new ArrayList<>();
            int extra_carbs = 0;
            for(MealItem item : currentMealItemList){
                if(item.getId() == -1){
                    extra_carbs = (int)item.getCarbs();
                } else{
                    mealFoodList.add(item);
                }
            }
            meal = new LoggedMeal(mealFoodList);
            meal.setId(currentMealId);
            meal.setExtraCarbs(extra_carbs);
            meal.setThumbnailPath(currentMealPhotoPath);

        } else {
            List<MealItem> mealFoodList = new ArrayList<>();
            int extra_carbs = 0;
            for(MealItem item : currentMealItemList){
                if(item.getId() == -1){
                    extra_carbs = (int)item.getCarbs();
                } else{
                    mealFoodList.add(item);
                }
            }
            meal = new LoggedMeal(mealFoodList);
            meal.setExtraCarbs(extra_carbs);
            meal.setThumbnailPath(currentMealPhotoPath);
        }

        Intent intent = new Intent();
        intent.putExtra("meal", meal);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void deleteItemFromMeal(int positon){
        final MealItem deletedItem = currentMealItemList.get(positon);
        final int deletedIndex = positon;

        mAdapter.removeItem(positon);

        Snackbar snackbar = Snackbar.make(addMealItemButton,getString(R.string.item_removed), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.restoreItem(deletedItem, deletedIndex);
            }
        });

        snackbar.getView().setBackgroundColor(this.getResources().getColor(R.color.primary));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }



    private void setMealPhoto(Intent data) {

        try {

            InputStream input = this.getContentResolver().openInputStream(currentImageUri);
            if (input == null) {
                Toast.makeText(getBaseContext(),"Unable to save photo",Toast.LENGTH_LONG).show();
            }
            Bitmap pic_bitmap = BitmapFactory.decodeStream(input);
            File photoFile;
            try {
                photoFile = createImageFile();
                try (FileOutputStream out = new FileOutputStream(photoFile)) {
                    pic_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                    out.flush();
                    out.close();

                    displayImg(currentMealPhotoPath);

                    Log.i("cenas", "setMealPhoto: TESTE");

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
            Toast.makeText(getBaseContext(),"Unable to save photo",Toast.LENGTH_LONG).show();
        }
    }

    private void deleteAll(){
        final List<MealItem> itemListRemoved = new ArrayList<>(currentMealItemList);

        mAdapter.removeAll();

        Snackbar snackbar = Snackbar.make(addMealItemButton,getString(R.string.item_removed), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.restoreAll(itemListRemoved);
            }
        });

        snackbar.getView().setBackgroundColor(this.getResources().getColor(R.color.primary));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    private void showSaveMealDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view = layoutInflaterAndroid.inflate(R.layout.save_meal_dialog, null);

        final EditText editText = view.findViewById(R.id.meal_name_edittext);
        final ImageView mealPhotoView = view.findViewById(R.id.photo_preview);
        if(currentMealPhotoPath != null){
            displayImg(currentMealPhotoPath);
            //setMealPhoto(currentMealPhotoPath);
        } else{
            mealPhotoView.setVisibility(View.GONE);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setView(view)
                .setTitle(getString(R.string.save_meal))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentMealName = editText.getText().toString();
                        saveMeal();
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        showSoftKeyboard(editText,this);

    }

    private void showSoftKeyboard(View view, Activity activity) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void displayImg(String path){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        File img = new File(path);
        if(img.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            if(bitmap!=null){
                thumbnailPhotoView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));

                if(thumbnailPhotoView.getVisibility() == View.GONE) {
                    cameraPlaceholder.setVisibility(View.GONE);
                    thumbnailPhotoView.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void dispatchTakePictureIntent() {

        if(currentMealPhotoPath==null){

            if (ActivityCompat.checkSelfPermission(CreateMealActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    + ActivityCompat.checkSelfPermission(CreateMealActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //ask permission
                ActivityCompat.requestPermissions(CreateMealActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }
            else {
                //we have permissions
                // File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File imgLocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyDiabetes/");

                if (!imgLocation.exists()) {
                    imgLocation.mkdirs();
                }
                File f = new File(imgLocation + "image"+ Calendar.getInstance().getTime()+".jpg");
                if (f.exists()) {
                    f.delete();
                }
                try {
                    f.createNewFile();

                    if(android.os.Build.VERSION.SDK_INT>=24){
                        currentImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider", f);
                    }else{
                        currentImageUri = Uri.fromFile(f);
                    }

//                    currentImageUri = Uri.fromFile(f);
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//ImagePicker.cameraOnly().getIntent(this);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(),R.string.failed_import_img,Toast.LENGTH_LONG).show();
                }
            }
        }else{
            //show imgs
            displayImg(currentMealPhotoPath);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentMealPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static void copy19plus(String src_path, File dst) throws IOException {
        File src = new File(src_path);
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static void copyUnder19(String src_path, File dst) throws IOException {
        File src = new File(src_path);
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }
}
