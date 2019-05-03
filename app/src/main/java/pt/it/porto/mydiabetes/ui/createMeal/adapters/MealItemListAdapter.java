package pt.it.porto.mydiabetes.ui.createMeal.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.createMeal.activities.SelectMealActivity;
import pt.it.porto.mydiabetes.ui.createMeal.utils.MealItem;


public class MealItemListAdapter extends RecyclerView.Adapter<MealItemListAdapter.ViewHolder> {

    private List<MealItem> itemList;
    private List<MealItem> itemListCopy;
    private Context context;

    public MealItemListAdapter(List<MealItem> itemList, Context context) {
        this.itemList = itemList;
        this.itemList.add(0, new MealItem(-1, context.getString(R.string.extra_carbs), 100, 0,0));
        this.context = context;
        itemListCopy = new ArrayList<>();
        itemListCopy.addAll(itemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType != -1){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_food_entry,parent,false);
            return new ViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_extra_carbs_entry,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealItem meal = itemList.get(position);

        holder.nameTextView.setText(meal.getName());

        if(meal.getId() != -1){//not extra CARBS
            holder.carbsTextView.setText(new StringBuilder(String.valueOf(meal.getCarbs()) + "g " + context.getString(R.string.carbs)));
            holder.lipidsTextView.setText(new StringBuilder(String.valueOf(meal.getLipids()) + "g " + context.getString(R.string.lipids)));
            holder.proteinTextView.setText(new StringBuilder(String.valueOf(meal.getProtein()) + "g " + context.getString(R.string.protein)));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void filter(String text) {
        itemList.clear();
        if(text.isEmpty()){
            itemList.addAll(itemListCopy);
        } else{
            itemList.add(itemListCopy.get(0));
            text = text.toLowerCase();
            for(MealItem meal: itemListCopy){
                if(meal.getName().toLowerCase().contains(text) && meal.getId() != -1){
                    itemList.add(meal);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MealItem meal = itemList.get(position);
        if(meal.getId()== -1){ return -1;}
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        TextView carbsTextView;
        TextView perPortionText;
        TextView lipidsTextView;
        TextView proteinTextView;
        ImageView addMealItemView;
        TextView perPortionText_lipids;
        TextView perPortionText_protein;


        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.food_name);
            carbsTextView = itemView.findViewById(R.id.food_carbs);
            perPortionText = itemView.findViewById(R.id.per_portion_text);
            addMealItemView = itemView.findViewById(R.id.add_meal_item);

            lipidsTextView = itemView.findViewById(R.id.food_lipids);
            proteinTextView = itemView.findViewById(R.id.food_protein);
            perPortionText_lipids = itemView.findViewById(R.id.per_portion_lipids);
            perPortionText_protein = itemView.findViewById(R.id.per_portion_text_protein);
        }
    }
}
