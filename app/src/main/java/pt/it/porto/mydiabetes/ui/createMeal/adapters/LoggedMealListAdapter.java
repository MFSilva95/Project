package pt.it.porto.mydiabetes.ui.createMeal.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.createMeal.activities.LoggedMealDetail;
import pt.it.porto.mydiabetes.ui.createMeal.activities.SelectMealActivity;
import pt.it.porto.mydiabetes.ui.createMeal.utils.LoggedMeal;


public class LoggedMealListAdapter extends RecyclerView.Adapter<LoggedMealListAdapter.ViewHolder>{
    private static final int REQUEST_MEAL = 1;
    private static final int FILTER_ALL = 0;
    private static final int FILTER_REGISTERED = 1;
    private static final int FILTER_FAVOURITE = 2;

    private List<LoggedMeal> mealList;
    private List<LoggedMeal> mealListCopy;
    private Context context;

    public LoggedMealListAdapter(List<LoggedMeal> mealList, Context context) {
        this.context = context;
        this.mealList = mealList;
        mealListCopy = new ArrayList<>();
        mealListCopy.addAll(mealList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_logged_meals_entry,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final LoggedMeal meal = mealList.get(position);

        if(meal.getName() != null)
            holder.nameView.setText(meal.getName());
        else
            holder.nameView.setText(setLogTime(meal.getTimestamp()));

        holder.carbsTextView.setText(new StringBuilder(meal.getTotalCarbs(true) + "g"));
        holder.mealNumItemsView.setText(String.valueOf(meal.getItemList().size()));

        if(meal.isFavourite()) {
            holder.favReg.setVisibility(View.VISIBLE);
            holder.savedReg.setVisibility(View.INVISIBLE);
            holder.normalReg.setVisibility(View.INVISIBLE);
        }
        else if(meal.isRegistered()) {
            holder.savedReg.setVisibility(View.VISIBLE);
            holder.favReg.setVisibility(View.INVISIBLE);
            holder.normalReg.setVisibility(View.INVISIBLE);
        }
        else {
            holder.normalReg.setVisibility(View.VISIBLE);
            holder.savedReg.setVisibility(View.INVISIBLE);
            holder.favReg.setVisibility(View.INVISIBLE);
        }

        if(meal.getThumbnailPath() != null)
            setMealPhoto(holder.mealPhotoView, meal.getThumbnailPath());
        else
            holder.mealPhotoView.setImageResource(R.drawable.ic_meal);

        holder.itemLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoggedMealDetail.class);
                intent.putExtra("logged_meal", meal);
                ((SelectMealActivity)context).startActivityForResult(intent,REQUEST_MEAL);
                ((SelectMealActivity)context).overridePendingTransition(0,0);
            }
        });

        holder.addMealView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("logged_meal", meal);
                intent.putExtra("delete", false);
                ((SelectMealActivity)context).setResult(Activity.RESULT_OK, intent);
                ((SelectMealActivity)context).finish();
            }
        });
    }

    private String setLogTime(String timestamp){
        SimpleDateFormat current_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = current_format.parse(timestamp);
            return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    private void setMealPhoto(ImageView mImageView, String mCurrentPhotoPath) {
        // Get the dimensions of the View
        mImageView.measure(CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT, CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT);
        int targetW = mImageView.getMeasuredWidth();
        int targetH = mImageView.getMeasuredHeight();

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

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    public void filter(int which) {
        mealList.clear();

        switch (which){
            case FILTER_ALL:
                mealList.addAll(mealListCopy);
                break;
            case FILTER_FAVOURITE:
                for(LoggedMeal meal: mealListCopy)
                    if(meal.isFavourite())
                        mealList.add(meal);
                break;
            case FILTER_REGISTERED:
                for(LoggedMeal meal: mealListCopy)
                    if(meal.isRegistered())
                        mealList.add(meal);
                break;

        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public void removeMeal(int position) {
        mealList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreMeal(LoggedMeal meal, int position) {
        mealList.add(position, meal);
        notifyItemInserted(position);
    }

    public void addData(List<LoggedMeal> list){
        mealList.addAll(list);
        mealListCopy.addAll(mealList);
        notifyItemRangeInserted(0,mealList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout itemLayoutView;
        ImageView mealPhotoView;
        TextView nameView;
        TextView carbsTextView;
        TextView mealNumItemsView;
        ImageView addMealView;

        ImageView normalReg;
        ImageView favReg;
        ImageView savedReg;

        public ViewHolder(View itemView) {
            super(itemView);

            itemLayoutView = itemView.findViewById(R.id.logged_meal_list_item);
            mealPhotoView = itemView.findViewById(R.id.meal_photo);
            nameView = itemView.findViewById(R.id.meal_name);
            carbsTextView = itemView.findViewById(R.id.meal_carbs);
            mealNumItemsView = itemView.findViewById(R.id.meal_num_items);
            addMealView = itemView.findViewById(R.id.add_meal);

            normalReg = itemView.findViewById(R.id.normal_reg);
            favReg = itemView.findViewById(R.id.fav_reg);
            savedReg = itemView.findViewById(R.id.saved_reg);
        }


    }
}
