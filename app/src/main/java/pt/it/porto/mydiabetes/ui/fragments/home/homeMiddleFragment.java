package pt.it.porto.mydiabetes.ui.fragments.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.DB_Write;
import pt.it.porto.mydiabetes.ui.activities.Home;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.createMeal.db.DataBaseHelper;
import pt.it.porto.mydiabetes.ui.createMeal.utils.LoggedMeal;
import pt.it.porto.mydiabetes.ui.listAdapters.HomeAdapter;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.HomeElement;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_WeightDetail_Delete:
                deleteRegister();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static homeMiddleFragment newInstance() {
        homeMiddleFragment fragment = new homeMiddleFragment();
        return fragment;
    }

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
        if (requestCode == WAIT_REGISTER && resultCode == Home.CHANGES_OCCURRED) {
            updateHomeList();
        }
        updateHomeList();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home_middle, container, false);
        //yapDroid = YapDroid.newInstance(this);
        homeRecyclerView = (RecyclerView) layout.findViewById(R.id.HomeListDisplay);
        fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        listEmpty = layout.findViewById(R.id.home_empty);

        setFabClickListeners();
        fillHomeList();
        return layout;
    }

    private void updateHomeList() {
        logBookList = new LinkedList<>();
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
        ((HomeAdapter) homeRecyclerView.getAdapter()).updateList(logBookList);
        homeRecyclerView.getAdapter().notifyDataSetChanged();
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
                    elem.setTag_name(db.Tag_GetNameById(elem.getTag_id()));
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
                //startActivity(intent);
                startActivityForResult(intent, WAIT_REGISTER);
            }
        });

    }



    @Override
    public void onResume() {
        super.onResume();
        //updateHomeList();
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
                                logBookList.remove(elem);
                            }
                            reg.close();
                            db_read.close();
                            updateHomeList();
                            toDeleteList.clear();
                            setDeleteMode(false);
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
