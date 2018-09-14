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
        this.itemList.add(0, new MealItem(-1, context.getString(R.string.extra_carbs), 0));
        this.context = context;
        itemListCopy = new ArrayList<>();
        itemListCopy.addAll(itemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_food_entry,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MealItem meal = itemList.get(position);

        holder.nameTextView.setText(meal.getName());

        if(meal.getId() == -1){
            holder.carbsTextView.setVisibility(View.INVISIBLE);
            holder.perPortionText.setVisibility(View.INVISIBLE);
        } else
            holder.carbsTextView.setText(new StringBuilder(String.valueOf(meal.getCarbs()) + "g " + context.getString(R.string.carbs)));

        holder.addMealItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        TextView carbsTextView;
        TextView perPortionText;
        ImageView addMealItemView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.food_name);
            carbsTextView = itemView.findViewById(R.id.food_carbs);
            perPortionText = itemView.findViewById(R.id.per_portion_text);
            addMealItemView = itemView.findViewById(R.id.add_meal_item);
        }
    }
}
