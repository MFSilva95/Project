package pt.it.porto.mydiabetes.ui.createMeal.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_create_meal_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final MealItem meal = foodList.get(position);

        holder.optionsView.setVisibility(View.GONE);

        holder.deleteItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((CreateMealActivity) context).deleteItemFromMeal(holder.getAdapterPosition());

                holder.optionsView.setVisibility(View.GONE);
            }
        });

        if(meal.getId() == -1){
            holder.itemLayout.setVisibility(View.INVISIBLE);
            holder.itemLayout2.setVisibility(View.VISIBLE);
            holder.foodName2.setText(meal.getName());
            holder.foodCarbs2.setText(new StringBuilder(meal.getCarbs() + "g"));

            holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.optionsView.getVisibility() == View.GONE) {
                        holder.optionsView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < foodList.size(); i++) {
                            if (i != holder.getAdapterPosition())
                                notifyItemChanged(i);
                        }

                        ((CreateMealActivity) context).scrollToPosition(holder.getAdapterPosition());

                    } else {
                        holder.optionsView.setVisibility(View.GONE);
                    }
                }
            });

            holder.viewForeground.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDialog2(meal);
                    holder.optionsView.setVisibility(View.GONE);
                    return true;
                }
            });

            holder.editItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog2(meal);
                }
            });

        } else {
            holder.itemLayout.setVisibility(View.VISIBLE);
            holder.itemLayout2.setVisibility(View.GONE);
            holder.foodName.setText(meal.getName());
            holder.foodPortion.setText(new StringBuilder(String.valueOf(meal.getQuantity()) + "g"));
            holder.foodCarbs.setText(new StringBuilder(String.format(Locale.US, "%.2f", meal.getCarbs()) + "g"));

            holder.viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.optionsView.getVisibility() == View.GONE) {
                        holder.optionsView.setVisibility(View.VISIBLE);
                        holder.foodName.setMaxLines(30);
                        holder.foodName.setEllipsize(null);
                        for (int i = 0; i < foodList.size(); i++) {
                            if (i != holder.getAdapterPosition())
                                notifyItemChanged(i);
                        }

                        ((CreateMealActivity) context).scrollToPosition(holder.getAdapterPosition());

                    } else {
                        holder.optionsView.setVisibility(View.GONE);
                        holder.foodName.setMaxLines(1);
                        holder.foodName.setEllipsize(TextUtils.TruncateAt.END);
                    }
                }
            });

            holder.viewForeground.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDialog(meal);
                    holder.optionsView.setVisibility(View.GONE);
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
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context.getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.select_portion_dialog, null);

        final NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5000);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(mealItem.getQuantity());
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
        final String types[] = { context.getString(R.string.food_grams), context.getString(R.string.food_portion)};

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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setView(view)
                .setTitle(mealItem.getName())
                .setNeutralButton(context.getString(R.string.keyboard), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                        int quantity;
                        if(typePicker.getValue() == 0)
                            quantity = numberPicker.getValue();
                        else{
                            quantity = numberPicker.getValue() * 100;
                        }
                        mealItem.setQuantity(quantity);

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

    private void showDialog2(final MealItem mealItem){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context.getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.add_carbs_dialog, null);

        final NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5000);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue((int)mealItem.getCarbs());
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setView(view)
                .setTitle(mealItem.getName())
                .setNeutralButton(context.getString(R.string.keyboard), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
                        mealItem.setCarbs(numberPicker.getValue());

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

        public ConstraintLayout viewBackground;
        public FrameLayout viewForeground;

        ConstraintLayout itemLayout, itemLayout2;
        TextView foodName;
        TextView foodPortion;
        TextView foodCarbs;
        TextView foodName2;
        TextView foodCarbs2;
        LinearLayout optionsView;
        LinearLayout deleteItemView;
        LinearLayout editItemView;

        public ViewHolder(View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.food_name);
            foodPortion = itemView.findViewById(R.id.food_portion);
            foodCarbs = itemView.findViewById(R.id.food_carbs);
            foodName2 = itemView.findViewById(R.id.food_name_2);
            foodCarbs2 = itemView.findViewById(R.id.food_carbs_2);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            optionsView = itemView.findViewById(R.id.item_options_buttons_layout);
            deleteItemView = itemView.findViewById(R.id.delete_item);
            editItemView = itemView.findViewById(R.id.edit_item);

            itemLayout = itemView.findViewById(R.id.layout1);
            itemLayout2 = itemView.findViewById(R.id.layout2);

        }
    }
}
