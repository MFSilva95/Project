package pt.it.porto.mydiabetes.ui.createMeal.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.createMeal.adapters.CreateMealListAdapter;
import pt.it.porto.mydiabetes.ui.createMeal.db.DataBaseHelper;
import pt.it.porto.mydiabetes.ui.createMeal.utils.LoggedMeal;
import pt.it.porto.mydiabetes.ui.createMeal.utils.MealItem;
import pt.it.porto.mydiabetes.ui.createMeal.utils.RecyclerItemTouchHelper;


public class CreateMealActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    static final int REQUEST_MEAL_ITEM = 0;
    static final int REQUEST_TAKE_PHOTO = 1;

    public TextView mealTotalCarbsView;
    public TextView emptyListMessageView;

    private FloatingActionButton addMealItemButton;
    private RecyclerView mealItemListView;
    private ImageView photoView;
    private CreateMealListAdapter mAdapter;

    private List<MealItem> currentMealItemList;
    private String currentMealPhotoPath;
    private String currentMealName;
    private int currentMealId;     // -1 If not saved

    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);

        dbHelper = new DataBaseHelper(this);
        currentMealItemList = new ArrayList<>();
        currentMealId = -1;

        addMealItemButton = (FloatingActionButton)findViewById(R.id.add_item_fab);
        addMealItemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectMealActivity.class);
                startActivityForResult(intent,REQUEST_MEAL_ITEM);
            }
        });

        mealTotalCarbsView = (TextView)findViewById(R.id.meal_total_carbs);
        emptyListMessageView = (TextView)findViewById(R.id.empty_message);
        photoView = (ImageView)findViewById(R.id.photo_view);

        if(getIntent().hasExtra("meal_id"))
            createExistingMeal(getIntent().getExtras().getInt("meal_id"));

        mAdapter = new CreateMealListAdapter(currentMealItemList, this);
        mealItemListView = (RecyclerView)findViewById(R.id.meal_items_list);
        mealItemListView.setLayoutManager(new LinearLayoutManager(this));
        mealItemListView.setHasFixedSize(true);
        mealItemListView.setNestedScrollingEnabled(false);
        mealItemListView.setItemAnimator(new DefaultItemAnimator());
        mealItemListView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mealItemListView);
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
            case REQUEST_MEAL_ITEM:
                if(resultCode == RESULT_OK && data.hasExtra("meal_item")){
                    MealItem item = data.getExtras().getParcelable("meal_item");
                    mAdapter.addItem(item);
                } else if(resultCode == RESULT_OK && data.hasExtra("logged_meal")){
                    LoggedMeal meal = data.getExtras().getParcelable("logged_meal");
                    for(MealItem item : meal.getItemList()){
                        mAdapter.addItem(item);
                    }

                    currentMealPhotoPath = meal.getThumbnailPath();
                    currentMealId = meal.getId();
                    currentMealName = meal.getName();
                }

                break;
            case REQUEST_TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    currentMealId = -1;
                    Toast.makeText(this, "Photo attached", Toast.LENGTH_SHORT).show();
                } else{
                    currentMealPhotoPath = null;
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_meal, menu);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Create Your Meal");
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

    private void createExistingMeal(int meal_id){
        if(meal_id != -1){
            LoggedMeal meal = dbHelper.getLoggedMeal(meal_id);
            currentMealId = meal.getId();
            currentMealName = meal.getName();
            currentMealPhotoPath = meal.getThumbnailPath();
            currentMealItemList.addAll(meal.getItemList());

            updateTotalCarbsDisplay();
        }
    }

    public void updateTotalCarbsDisplay(){
        float total_carbs = 0;
        for(MealItem m : currentMealItemList)
            total_carbs = total_carbs + m.getCarbs();

        mealTotalCarbsView.setText(new StringBuilder(String.format(Locale.US,"%.1f", total_carbs) + "g"));

        if(currentMealItemList.size() > 0)
            emptyListMessageView.setVisibility(View.GONE);
        else
            emptyListMessageView.setVisibility(View.VISIBLE);

        currentMealId = -1;
    }

    private boolean validate(){
        if(currentMealItemList.size() == 0){
            Toast.makeText(this, "Empty list!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void deleteItemFromMeal(int positon){
        final MealItem deletedItem = currentMealItemList.get(positon);
        final int deletedIndex = positon;

        mAdapter.removeItem(positon);

        Snackbar snackbar = Snackbar.make(addMealItemButton,"Item removed", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.restoreItem(deletedItem, deletedIndex);
            }
        });

        snackbar.getView().setBackgroundColor(this.getResources().getColor(R.color.primary));
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.show();
    }

    private void setMealPhoto(ImageView mPhotoView) {
        // Get the dimensions of the View
        int targetW, targetH;

        mPhotoView.measure(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        targetW = mPhotoView.getMeasuredWidth();
        targetH = 180;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentMealPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentMealPhotoPath, bmOptions);
        mPhotoView.setImageBitmap(bitmap);
    }

    private void deleteAll(){
        final List<MealItem> itemListRemoved = new ArrayList<>();
        itemListRemoved.addAll(currentMealItemList);

        mAdapter.removeAll();

        Snackbar snackbar = Snackbar.make(addMealItemButton,"Item removed", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
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
            setMealPhoto(mealPhotoView);
        } else{
            mealPhotoView.setVisibility(View.GONE);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setView(view)
                .setTitle("Save Meal")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentMealName = editText.getText().toString();
                        saveMeal();
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        showSoftKeyboard(view,this);

    }

    private void showSoftKeyboard(View view, Activity activity) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void saveMeal(){
        if(!validate())
            return;

        LoggedMeal meal = new LoggedMeal(currentMealItemList);
        meal.setThumbnailPath(currentMealPhotoPath);
        meal.setName(currentMealName);
        meal.setFavourite(true);
        currentMealId = dbHelper.insertMeal(meal);
        if(currentMealId != -1) {
            Toast.makeText(this, "Meal has been saved and added to your favourites", Toast.LENGTH_SHORT).show();
        }
    }

    private void logMeal(){
        if(!validate())
            return;

        if(currentMealId == -1) {
            LoggedMeal meal = new LoggedMeal(currentMealItemList);
            meal.setThumbnailPath(currentMealPhotoPath);
            currentMealId = dbHelper.insertMeal(meal);
        }

        Intent intent = new Intent();
        intent.putExtra("meal_id", currentMealId);
        intent.putExtra("total_carbs", mAdapter.getTotalCarbsValue());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
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
}
