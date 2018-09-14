package pt.it.porto.mydiabetes.ui.createMeal.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.createMeal.utils.MealItem;


public class MealDetailListAdapter extends RecyclerView.Adapter<MealDetailListAdapter.ViewHolder> {

    private List<MealItem> mealItemList;
    private Context context;

    public MealDetailListAdapter(List<MealItem> mealItemList, Context context) {
        this.context = context;
        this.mealItemList = mealItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_meal_detail_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MealItem meal = mealItemList.get(position);

        holder.foodNameView.setText(meal.getName());
    }

    @Override
    public int getItemCount() {
        return mealItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView foodNameView;

        public ViewHolder(View itemView) {
            super(itemView);

            foodNameView = itemView.findViewById(R.id.food_name);
        }
    }
}
