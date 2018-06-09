package pt.it.porto.mydiabetes.ui.createMeal.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.createMeal.adapters.MealDetailListAdapter;
import pt.it.porto.mydiabetes.ui.createMeal.utils.LoggedMeal;


public class LoggedMealDetail extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBarLayout collapseToolbar;
    private NestedScrollView scrollView;

    private ImageView mealPhotoView;
    private TextView mealDateView;
    private TextView mealTotalCarbsView;
    private RecyclerView mealItemListView;

    private MealDetailListAdapter mAdapter;

    LoggedMeal meal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        collapseToolbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        scrollView = (NestedScrollView) findViewById(R.id.scrollview_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mealPhotoView = (ImageView) findViewById(R.id.logged_meal_photo);
        mealDateView = (TextView) findViewById(R.id.meal_date);
        mealTotalCarbsView = (TextView) findViewById(R.id.meal_carbs);
        mealItemListView = (RecyclerView) findViewById(R.id.meal_items);

        meal = getIntent().getExtras().getParcelable("logged_meal");

        if(meal.getThumbnailPath() != null)
            setMealPhoto(mealPhotoView, meal.getThumbnailPath());
        else{
            collapseToolbar.setExpanded(false);
            scrollView.setNestedScrollingEnabled(false);
        }

        mealDateView.setText(meal.getTimestamp());
        mealTotalCarbsView.setText(String.valueOf(meal.getTotalCarbs()));

        mealItemListView.setHasFixedSize(true);
        mealItemListView.setLayoutManager(new LinearLayoutManager(this));
        mealItemListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new MealDetailListAdapter(meal.getItemList(),this);
        mealItemListView.setAdapter(mAdapter);

    }

    private void setMealPhoto(ImageView mImageView, String mCurrentPhotoPath) {
        // Get the dimensions of the View

        mImageView.measure(CollapsingToolbarLayout.LayoutParams.MATCH_PARENT, CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT);
        int targetW = mImageView.getMeasuredWidth();
        int targetH = 350;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_meal_detail, menu);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent();

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add:
                intent.putExtra("logged_meal", meal);
                intent.putExtra("delete", false);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            case R.id.delete:
                intent.putExtra("logged_meal", meal);
                intent.putExtra("delete", true);
                setResult(RESULT_OK, intent);
                finish();
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
