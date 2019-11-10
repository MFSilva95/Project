package pt.it.porto.mydiabetes.ui.createMeal.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.createMeal.activities.CreateMealActivity;
import pt.it.porto.mydiabetes.ui.createMeal.utils.MealItem;


public class CreateMealListAdapter extends RecyclerView.Adapter<CreateMealListAdapter.ViewHolder> {
    private List<MealItem> foodList;
    private Context context;

    public CreateMealListAdapter(List<MealItem> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType!=-1){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_create_meal_item,parent,false);
            return new ViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_create_meal_extra_carbs_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        MealItem meal = foodList.get(position);
        if(meal.getId()== -1){ return -1;}
        return 0;
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MealItem meal = foodList.get(position);
        holder.foodName.setText(meal.getName());


        if(meal.getId()==-1){//carbs
            holder.foodCarbs.setText(new StringBuilder(String.format(Locale.US, "%.2f", meal.getCarbs()) + "g"));
        }else{//meal
            holder.foodPortion.setText(new StringBuilder(String.format(Locale.US, "%d", meal.getQuantity()) + "g"));
            holder.foodCarbs.setText(new StringBuilder(String.format(Locale.US, "%.2f", meal.getCarbs()) + "g"));
            holder.foodLipids.setText(new StringBuilder(String.format(Locale.US, "%.2f", meal.getLipids()) + "g"));
            holder.foodProtein.setText(new StringBuilder(String.format(Locale.US, "%.2f", meal.getProtein()) + "g"));
        }


        holder.editView.setVisibility(View.GONE);
        holder.deleteView.setVisibility(View.GONE);
        holder.deleteItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((CreateMealActivity) context).deleteItemFromMeal(holder.getAdapterPosition());

                holder.editView.setVisibility(View.GONE);
                holder.deleteView.setVisibility(View.GONE);

            }
        });


        holder.viewForeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.deleteView.getVisibility() == View.GONE || holder.editView.getVisibility() == View.GONE) {

                    holder.deleteView.setVisibility(View.VISIBLE);
                    holder.editView.setVisibility(View.VISIBLE);

                    holder.foodName.setMaxLines(30);
                    holder.foodName.setEllipsize(null);
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    for (int i = 0; i < foodList.size(); i++) {
                                        if (i != holder.getAdapterPosition())
                                            notifyItemChanged(i);
                                    }
                                }
                            },
                            50);

                    ((CreateMealActivity) context).scrollToPosition(holder.getAdapterPosition());

                } else {

                    holder.deleteView.setVisibility(View.GONE);
                    holder.editView.setVisibility(View.GONE);

                    holder.foodName.setMaxLines(1);
                    holder.foodName.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });

        holder.viewForeground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(meal);
                holder.deleteView.setVisibility(View.GONE);
                holder.editView.setVisibility(View.GONE);
                return true;
            }
        });

        holder.editItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public float getTotalCarbsValue(){
        float total_carbs = 0;
        for(MealItem m : foodList)
            total_carbs = total_carbs + m.getCarbs();

        return total_carbs;
    }

    public void addItem(MealItem item) {
        foodList.add(item);
        notifyItemInserted(foodList.indexOf(item));
        ((CreateMealActivity)context).notitfyItemListChange();
    }

    public void removeItem(int position) {
        foodList.remove(position);
        notifyItemRemoved(position);
        ((CreateMealActivity)context).notitfyItemListChange();
    }

    public void removeAll(){
        int n_items_removed = foodList.size();
        foodList.clear();
        notifyItemRangeRemoved(0,n_items_removed);
        ((CreateMealActivity)context).notitfyItemListChange();
    }

    public void restoreItem(MealItem item, int position) {
        foodList.add(position, item);
        notifyItemInserted(position);
        ((CreateMealActivity)context).notitfyItemListChange();
    }

    public void restoreAll(List<MealItem> itemList){
        foodList.addAll(itemList);
        notifyItemRangeInserted(0, foodList.size());
        ((CreateMealActivity)context).notitfyItemListChange();
    }

    public void updateItem(MealItem item){
        ((CreateMealActivity)context).notitfyItemListChange();
        notifyItemChanged(foodList.indexOf(item));
    }

    private void showDialog(final MealItem mealItem) {

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, R.style.AlertDialogCustom);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context.getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.select_portion_dialog, null);

        final NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5000);
        numberPicker.setWrapSelectorWheel(false);
        if(mealItem.getId()==-1){numberPicker.setValue((int) mealItem.getCarbs());}else{numberPicker.setValue(mealItem.getQuantity());}

        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final EditText editText = view.findViewById(R.id.number_input);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0)
                    numberPicker.setValue(Integer.valueOf(s.toString()));
            }
        });

        final NumberPicker typePicker = view.findViewById(R.id.gram_or_portion_picker);
        final String[] types = { context.getString(R.string.food_grams), context.getString(R.string.food_portion)};

        typePicker.setMinValue(0);
        typePicker.setMaxValue(types.length - 1);
        typePicker.setDisplayedValues(types);
        typePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker.OnValueChangeListener typePickerListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal == 1){
                    numberPicker.setValue(1);
                } else{
                    numberPicker.setValue(mealItem.getQuantity());
                }
            }
        };

        typePicker.setOnValueChangedListener(typePickerListener);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextThemeWrapper);
        alertDialogBuilder
                .setView(view)
                .setTitle(mealItem.getName())
                .setNeutralButton(context.getString(R.string.keyboard), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //turns keyboard on
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(context.getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mealItem.getId()==-1){
                            int quantity;
                            if(typePicker.getValue() == 0)
                                quantity = numberPicker.getValue();
                            else{
                                quantity = numberPicker.getValue() * 100;
                            }
                            mealItem.setCarbs(quantity);//setQuantity(quantity);

                        }else{
                            int quantity;
                            if(typePicker.getValue() == 0)
                                quantity = numberPicker.getValue();
                            else{
                                quantity = numberPicker.getValue() * 100;
                            }
                            mealItem.setQuantity(quantity);

                        }
                        updateItem(mealItem);

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberPicker.getVisibility() == View.INVISIBLE){
                    hideSoftKeyboard(editText,(CreateMealActivity)context);
                    numberPicker.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                } else{
                    numberPicker.setVisibility(View.INVISIBLE);
                    editText.setVisibility(View.VISIBLE);
                    showSoftKeyboard(editText,(CreateMealActivity)context);
                }
            }
        });
    }

    private void showSoftKeyboard(View view, Activity activity) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(View view, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CardView viewForeground;
        TextView foodName;
        TextView foodPortion;
        TextView foodCarbs;
        TextView foodLipids;
        TextView foodProtein;

        CardView editView;
        CardView deleteView;
        LinearLayout deleteItemView;
        LinearLayout editItemView;
        ImageView weightIcon_img;

        public ViewHolder(View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.food_name);
            foodPortion = itemView.findViewById(R.id.food_portion);
            foodCarbs = itemView.findViewById(R.id.food_carbs);
            foodLipids = itemView.findViewById(R.id.food_lipids);
            foodProtein = itemView.findViewById(R.id.food_protein);

            viewForeground = itemView.findViewById(R.id.foodCardView);
            deleteView = itemView.findViewById(R.id.cardViewEdit);
            editView = itemView.findViewById(R.id.cardViewRemove);
            deleteItemView = itemView.findViewById(R.id.delete_item);
            editItemView = itemView.findViewById(R.id.edit_item);
            weightIcon_img = itemView.findViewById(R.id.weight_icon);
        }
    }
}
