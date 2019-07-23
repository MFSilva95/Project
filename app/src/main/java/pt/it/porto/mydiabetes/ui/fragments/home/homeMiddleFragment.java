package pt.it.porto.mydiabetes.ui.fragments.home;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cdev.achievementview.AchievementView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.Note;

import pt.it.porto.mydiabetes.data.UserInfo;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.activities.Home;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.createMeal.db.DataBaseHelper;
import pt.it.porto.mydiabetes.ui.createMeal.utils.LoggedMeal;
import pt.it.porto.mydiabetes.ui.listAdapters.HomeAdapter;
import pt.it.porto.mydiabetes.utils.BadgeUtils;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.HomeElement;
import pt.it.porto.mydiabetes.widget;

/**
 * Created by parra on 21/02/2017.
 */

public class homeMiddleFragment extends Fragment {
    final int WAIT_REGISTER = 123;
    //Number of last days shown
    final int NUMBER_OF_DAYS = 7;
    final String TAG = "homeFrag";
    private FloatingActionButton fab;
    private View listEmpty;
    private int noteId;


    private boolean deleteMode;

    private ArrayList<HomeElement> toDeleteList = new ArrayList<>();

    private RecyclerView homeRecyclerView;
    private List<HomeElement> logBookList;

    private AchievementView achievementView;
    private AchievementView achievementViewSecondary;
    private AchievementView achievementViewStreak;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_WeightDetail_Delete:
                deleteRegister();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public static homeMiddleFragment newInstance() {
//        homeMiddleFragment fragment = new homeMiddleFragment();
//        return fragment;
//    }

    public homeMiddleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        deleteMode = false;
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("onActivityResult");
        if (requestCode == WAIT_REGISTER && resultCode == Home.CHANGES_OCCURRED) {
            updateHomeList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("onCreateView");
        View layout = inflater.inflate(R.layout.fragment_home_middle, container, false);
        //yapDroid = YapDroid.newInstance(this);
        homeRecyclerView = (RecyclerView) layout.findViewById(R.id.HomeListDisplay);
        fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        listEmpty = layout.findViewById(R.id.home_empty);

        achievementView = layout.findViewById(R.id.achievement_view);
        achievementViewSecondary = layout.findViewById(R.id.achievement_view_secondary);
        achievementViewStreak = layout.findViewById(R.id.achievement_view_streak);

        setFabClickListeners();
        fillHomeList();
        updateHomeList();
        return layout;
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private void updateHomeList() {
        System.out.println("updateHomeList()");
        logBookList = new LinkedList<>();
        //fillAdviceList();
        fillDays();
        if (logBookList.size() == 0) {
        //if (hasNoLogElements(logBookList)) {
            listEmpty.setVisibility(View.VISIBLE);
            listEmpty.bringToFront();
        } else {
            listEmpty.setVisibility(View.GONE);

            // Widget update section
            triggerWidget();
        }

        logBookList.add(new HomeElement(HomeElement.Type.SPACE, ""));
        logBookList.add(new HomeElement(HomeElement.Type.SPACE, ""));
        ((HomeAdapter) homeRecyclerView.getAdapter()).updateList(logBookList);
        homeRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private void triggerWidget() {
        Intent intent = new Intent(getContext(), widget.class);

        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] ids = AppWidgetManager.getInstance(getActivity().getApplication()).getAppWidgetIds(new ComponentName(getActivity().getApplication(), widget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        getContext().sendBroadcast(intent);
    }

    private void fillHomeList() {
        logBookList = new LinkedList<>();
        //fillTaskList();
        //fillAdviceList();
        fillDays();
        if (logBookList.size() == 0) {
        //if (hasNoLogElements(logBookList)) {
            listEmpty.setVisibility(View.VISIBLE);
            listEmpty.bringToFront();
        } else {
            listEmpty.setVisibility(View.GONE);
        }

        logBookList.add(new HomeElement(HomeElement.Type.SPACE, ""));
        logBookList.add(new HomeElement(HomeElement.Type.SPACE, ""));

        HomeAdapter homeAdapter = new HomeAdapter(logBookList, new MiddleFragRegCallBackImpl());
        homeRecyclerView.setAdapter(homeAdapter);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void dbRead(Calendar calendar) {
        DB_Read db = new DB_Read(getContext());
        String date = DateUtils.getFormattedDate(calendar);
        long currentTime = System.currentTimeMillis();
        LinkedList<HomeElement> list = db.getLogBookFromStartDate(date);
        if (list !=null && list.size() > 0 ) {
            CharSequence dateText = "";
            for (HomeElement elem : list) {

                CharSequence newDateText = android.text.format.DateUtils.getRelativeTimeSpanString(getDateInMillis(elem.getFormattedDate()), currentTime, android.text.format.DateUtils.DAY_IN_MILLIS);
                if (dateText.equals(newDateText)) {
                    Resources res = getResources(); //assuming in an activity for example, otherwise you can provide a context.
                    String[] dayTimes = res.getStringArray(R.array.daytimes);
                    elem.setTag_name(dayTimes[elem.getTag_id()-1]);
                    this.logBookList.add(elem);
                } else {
                    dateText = newDateText;
                    this.logBookList.add(new HomeElement(HomeElement.Type.HEADER, dateText.toString()));
                    this.logBookList.add(elem);
                }
            }
        }
        db.close();
    }

    public void fillDays() {

        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.add(Calendar.DAY_OF_MONTH, -NUMBER_OF_DAYS);
        dbRead(calendar);
    }

    private void setFabClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(fab.getContext(), NewHomeRegistry.class);
                startActivityForResult(intent, WAIT_REGISTER);
            }
        });
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                System.out.println("visivel entra aqui");
                if (true) {
                    //achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.healthBadgeWon));
                    //Home.winBadge = false;
                }
            }
        }
    }

    @Override
    public void onResume() {
        updateStreakValues();
        DB_Read db = new DB_Read(getContext());
        if(db!=null){
            boolean winHealthBadge = BadgeUtils.addHealthBadge(getContext(), db);
            if (winHealthBadge) achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.healthBadgeWon));
            if (NewHomeRegistry.winBadge) {
                achievementView.show(this.getString(R.string.congratsMessage1), this.getString(R.string.logBadgeWon));
            }

            if (NewHomeRegistry.winDaily) {
                if (NewHomeRegistry.winBadge) {
                    achievementViewSecondary.setVisibility(View.VISIBLE);
                    achievementViewSecondary.show(this.getString(R.string.congratsMessage1), this.getString(R.string.dailyBadgeWon));
                } else {
                    achievementView.setVisibility(View.GONE);
                    achievementViewSecondary.show(this.getString(R.string.congratsMessage1), this.getString(R.string.dailyBadgeWon));
                }
            }
            System.out.println("CURRENT_STREAK: "+db.MyData_Read().getCurrentStreak());
            if (NewHomeRegistry.winStreak) {
                int points = 100 * db.MyData_Read().getCurrentStreak();

                //show daily goal streak winning as a notification
                if (!NewHomeRegistry.winBadge && !NewHomeRegistry.winDaily) {
                    achievementView.setVisibility(View.GONE);
                    achievementViewSecondary.setVisibility(View.GONE);
                } else if (!NewHomeRegistry.winDaily) {
                    achievementViewSecondary.setVisibility(View.GONE);
                } else if (!NewHomeRegistry.winBadge) {
                    achievementView.setVisibility(View.GONE);
                }
                achievementViewStreak.setVisibility(View.VISIBLE);
                achievementViewStreak.show(this.getString(R.string.congratsStreak), points+" "+this.getString(R.string.streakGoalWon));
            }
            NewHomeRegistry.winBadge = false;
            NewHomeRegistry.winDaily = false;
            NewHomeRegistry.winStreak = false;
        }
        db.close();

        super.onResume();
        updateHomeList();
    }

    public void updateStreakValues() {
        DB_Read rdb = new DB_Read(getContext());
        UserInfo myData = rdb.MyData_Read();
        Boolean changes = false;
        int currentStreak = myData.getCurrentStreak();
        int maxStreak = myData.getMaxStreak();

        // set current streak 0 if yesterday daily goal was not complete
        if (!rdb.checkDailyGoalWin(1) && !rdb.checkDailyGoalWin(0) && currentStreak != 0) {
            changes = true;
            myData.setCurrentStreak(0);
        }
        // update maxStreak if currentStreak is better than maxStreak
        if (currentStreak > maxStreak) {
            changes = true;
            myData.setMaxStreak(currentStreak);
        }
        // save new updates
        if (changes) {
            DB_Write wdb = new DB_Write(getContext());
            wdb.MyData_Save(myData);
            wdb.close();
        }
    }

    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            Log.d("Exception date.", e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public interface MiddleFragRegCallBack {
        void updateHomeList(Intent intent);
        void addToDelete(HomeElement args);
        void removeToDelete(HomeElement args);
        boolean isInDeleteMode();
    }

    public class MiddleFragRegCallBackImpl implements MiddleFragRegCallBack {

        @Override
        public void updateHomeList(Intent intent) {
            //startActivity(intent);
            startActivityForResult(intent, WAIT_REGISTER);
        }

        @Override
        public void addToDelete(HomeElement elem) {
            setDeleteMode(true);
            toDeleteList.add(elem);
            Log.i(TAG, "addToDelete: SIZE NOW:" + toDeleteList.size());
        }

        @Override
        public void removeToDelete(HomeElement elem) {
            toDeleteList.remove(elem);
            if (toDeleteList.size() == 0) {
                setDeleteMode(false);
            }
        }
        @Override
        public boolean isInDeleteMode() {
            return deleteMode;
        }
    }

    private void deleteRegister() {
        final Context c = getContext();
        new AlertDialog.Builder(c)
                .setTitle(getString(R.string.deleteReading))
                .setPositiveButton(getString(R.string.positiveButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        DB_Read db_read = new DB_Read(c);
                        DB_Write reg = new DB_Write(c);
                        DataBaseHelper dbHelper = new DataBaseHelper(c);
                        LoggedMeal meal;
                        try {
                            for (HomeElement elem : toDeleteList) {
                                int glyID;
                                int carbsID;
                                int insuID;

                                if ((glyID = elem.getGlycemiaId()) != -1) {
                                    glycemiaData = db_read.Glycemia_GetById(glyID);
                                    if(glycemiaData!=null){
                                        setNoteId(glycemiaData.getIdNote());
                                        reg.Glycemia_Delete(glyID);
                                    }
                                }
                                if ((carbsID = elem.getCarbsId()) != -1) {
                                    carbsData = db_read.CarboHydrate_GetById(carbsID);
                                    if(carbsData!=null){
                                        setNoteId(carbsData.getIdNote());
                                        reg.Carbs_Delete(carbsID);
                                        if(carbsData.getMealId() != -1) {
                                            dbHelper.deleteMeal(carbsData.getMealId());
                                        }
                                    }
                                }
                                if ((insuID = elem.getInsulinId()) != -1) {
                                    insulinData = db_read.InsulinReg_GetById(insuID);
                                    if(insulinData!=null){
                                        setNoteId(insulinData.getIdNote());
                                        reg.Insulin_Delete(insuID);
                                    }
                                }
                                if ((noteId != -1)) {
                                    reg.Note_Delete(noteId);
                                }
                                if(elem.getRecordID()!=-1){
                                    reg.Record_Delete(elem.getRecordID());
                                }
                                logBookList.remove(elem);
                            }
                            reg.close();
                            db_read.close();
                            updateHomeList();
                            toDeleteList.clear();
                            setDeleteMode(false);
                            ((Home) getActivity()).notifyPageAdapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(c, getString(R.string.deleteException), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.negativeButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
    }

    private boolean hasNoLogElements(List<HomeElement> logBookList) {
        for(HomeElement elem:logBookList){
            if(elem.getDisplayType().toString().equals(HomeElement.Type.LOGITEM.toString())){
                return false;
            }
        }
        return true;
    }


    private void setNoteId(int noteId) {
        if (noteId != -1) {
            this.noteId = noteId;
        }
    }

    private void setDeleteMode(boolean bool) {

        deleteMode = bool;
        if (bool) {
            if(actionMode == null){
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ToolbarActionModeCallback());
            }
        } else {
            actionMode.finish();
            actionMode=null;
            for (int i = 0; i < toDeleteList.size(); i++) {
                toDeleteList.get(i).setPressed(false);
            }
            toDeleteList.clear();
            ((HomeAdapter) homeRecyclerView.getAdapter()).updateList(logBookList);
            homeRecyclerView.getAdapter().notifyDataSetChanged();

        }


    }


    @Nullable
    private GlycemiaRec glycemiaData;
    @Nullable
    private CarbsRec carbsData;
    @Nullable
    private InsulinRec insulinData;
    @Nullable
    private Note noteData;

    private ActionMode actionMode;

    class ToolbarActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.weight_detail_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.findItem(R.id.menuItem_WeightDetail_Delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.menuItem_WeightDetail_Delete) {
                deleteRegister();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            setDeleteMode(false);
        }
    }
}
