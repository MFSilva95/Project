package pt.it.porto.mydiabetes.ui.createMeal.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.List;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.createMeal.activities.SelectMealActivity;
import pt.it.porto.mydiabetes.ui.createMeal.adapters.MealItemListAdapter;
import pt.it.porto.mydiabetes.ui.createMeal.db.DataBaseHelper;
import pt.it.porto.mydiabetes.ui.createMeal.utils.MealItem;
import pt.it.porto.mydiabetes.ui.createMeal.utils.RecyclerTouchListener;


public class FoodListFragment extends Fragment {

    private SearchView searchView;
    private MealItemListAdapter mAdapter;

    private List<MealItem> foodList;
    private DataBaseHelper dbHelper;

    public FoodListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_food_list, container, false);

        dbHelper = new DataBaseHelper(((SelectMealActivity)getActivity()));

        RecyclerView recyclerView = (RecyclerView)fragmentView.findViewById(R.id.food_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(((SelectMealActivity)getActivity())));

        foodList = dbHelper.getFoodList();
        mAdapter = new MealItemListAdapter(foodList, ((SelectMealActivity)getActivity()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(((SelectMealActivity)getActivity()), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                if(foodList.get(position).getId() == -1)
                    showDialog2();
                else
                    showDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        return fragmentView;
    }

    private void showDialog(final int position) {

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(((SelectMealActivity)getActivity()).getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.select_portion_dialog, null);


        final MealItem mealItem = foodList.get(position);

        final NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5000);
        numberPicker.setWrapSelectorWheel(false);
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
        final String[] types = { getString(R.string.food_portion), getString(R.string.food_grams)};

        typePicker.setMinValue(0);
        typePicker.setMaxValue(types.length - 1);
        typePicker.setDisplayedValues(types);
        typePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


//        AlertDialog.Builder builder = new AlertDialog.Builder(contextThemeWrapper);
//        builder.setView(view);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextThemeWrapper);//new AlertDialog.Builder(((SelectMealActivity)getActivity()));
        alertDialogBuilder
                .setView(view)
                .setTitle(mealItem.getName())
                .setNeutralButton(getString(R.string.keyboard), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int quantity;
                        if(typePicker.getValue() == 1)
                            quantity = numberPicker.getValue();
                        else{
                            quantity = numberPicker.getValue() * 100;
                        }
                        mealItem.setQuantity(quantity);

                        Intent intent = new Intent();
                        intent.putExtra("meal_item", mealItem);
                        ((SelectMealActivity)getActivity()).setResult(Activity.RESULT_OK, intent);
                        ((SelectMealActivity)getActivity()).finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberPicker.getVisibility() == View.INVISIBLE){
                    hideSoftKeyboard(editText,((SelectMealActivity)getActivity()));
                    numberPicker.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                } else{
                    numberPicker.setVisibility(View.INVISIBLE);
                    editText.setVisibility(View.VISIBLE);
                    showSoftKeyboard(editText,((SelectMealActivity)getActivity()));
                }
            }
        });

    }

    private void showDialog2(){

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(((SelectMealActivity)getActivity()).getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.add_carbs_dialog, null);

        final NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5000);
        numberPicker.setWrapSelectorWheel(false);
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

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextThemeWrapper);//new AlertDialog.Builder(((SelectMealActivity)getActivity()));
        alertDialogBuilder
                .setView(view)
                .setTitle(getString(R.string.add_extra_carbs))
                .setNeutralButton(getString(R.string.keyboard), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("meal_item", new MealItem(-1,getString(R.string.extra_carbs),(float)numberPicker.getValue(),0,0));
                        ((SelectMealActivity)getActivity()).setResult(Activity.RESULT_OK, intent);
                        ((SelectMealActivity)getActivity()).finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberPicker.getVisibility() == View.INVISIBLE){
                    hideSoftKeyboard(editText,((SelectMealActivity)getActivity()));
                    numberPicker.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                } else{
                    numberPicker.setVisibility(View.INVISIBLE);
                    editText.setVisibility(View.VISIBLE);
                    showSoftKeyboard(editText,((SelectMealActivity)getActivity()));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_food, menu);

        MenuItem item = menu.findItem(R.id.meal_search);
        searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);

                return true;
            }
        });
    }
}
