package pt.it.porto.mydiabetes.ui.createMeal.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.createMeal.activities.SelectMealActivity;
import pt.it.porto.mydiabetes.ui.createMeal.adapters.LoggedMealListAdapter;
import pt.it.porto.mydiabetes.ui.createMeal.db.DataBaseHelper;
import pt.it.porto.mydiabetes.ui.createMeal.utils.LoggedMeal;

import static android.app.Activity.RESULT_OK;

public class LoggedMealListFragment extends Fragment {
    private static final int REQUEST_MEAL = 1;
    private static final int FILTER_ALL = 0;
    private static final int FILTER_FAVOURITE = 2;

    private SearchView searchView;
    private LoggedMealListAdapter mAdapter;

    private DataBaseHelper dbHelper;
    private List<LoggedMeal> mealList;

    boolean isUndo;

    public LoggedMealListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_logged_meal_list, container, false);

        isUndo = false;

        dbHelper = new DataBaseHelper(((SelectMealActivity)getActivity()));
        mealList = dbHelper.getLoggedMealList();

        final RecyclerView recyclerView = (RecyclerView)fragmentView.findViewById(R.id.logged_meal_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(((SelectMealActivity)getActivity())));
        //recyclerView.addItemDecoration(new DividerItemDecoration(((SelectMealActivity)getActivity()), DividerItemDecoration.VERTICAL));
        mAdapter = new LoggedMealListAdapter(mealList, ((SelectMealActivity)getActivity()));
        recyclerView.setAdapter(mAdapter);


        return fragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEAL && resultCode == RESULT_OK) {
            if(data.getExtras() != null) {
                final LoggedMeal meal = data.getExtras().getParcelable("logged_meal");
                final int deletedIndex = mealList.indexOf(meal);

                if(data.getExtras().getBoolean("delete")){
                    mAdapter.removeMeal(deletedIndex);

                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.view_pager),"Meal removed", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isUndo = true;
                            mAdapter.restoreMeal(meal, deletedIndex);
                        }
                    });

                    snackbar.addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if(!isUndo)
                                dbHelper.deleteMeal(meal);
                            else
                                isUndo = false;
                        }
                    });

                    snackbar.getView().setBackgroundColor(this.getResources().getColor(R.color.primary));
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();
                } else{
                    Intent intent = new Intent();
                    intent.putExtra("logged_meal", meal);
                    ((SelectMealActivity) getActivity()).setResult(Activity.RESULT_OK, intent);
                    ((SelectMealActivity) getActivity()).finish();
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter_logged_meals, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handler dos cliques em cada menu
        switch (item.getItemId()) {
            case R.id.fav_meals:
                mAdapter.filter(FILTER_FAVOURITE);
                return true;
            case R.id.all_logged_meals:
                mAdapter.filter(FILTER_ALL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
