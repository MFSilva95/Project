package pt.it.porto.mydiabetes.ui.fragments.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.Task;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.ListsDataDb;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.CholesterolDetail;
import pt.it.porto.mydiabetes.ui.activities.DiseaseDetail;
import pt.it.porto.mydiabetes.ui.activities.ExerciseDetail;
import pt.it.porto.mydiabetes.ui.activities.GlycemiaDetail;
import pt.it.porto.mydiabetes.ui.activities.InsulinDetail;
import pt.it.porto.mydiabetes.ui.activities.MealActivity;
import pt.it.porto.mydiabetes.ui.activities.WeightDetail;
import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;
import pt.it.porto.mydiabetes.ui.listAdapters.HomeAdapter;
import pt.it.porto.mydiabetes.ui.usability.HomeTouchHelper;

/**
 * Created by parra on 21/02/2017.
 */

public class homeMiddleFragment extends Fragment{
    private YapDroid yapDroid;


    private FloatingActionButton miniFab1;
    private FloatingActionButton miniFab2;
    private FloatingActionButton miniFab3;

    boolean fabOpen = false;

    private FloatingActionButton fab;


    private LinearLayout fabContainer_v;


    private float offset1;
    private float offset2;
    private float offset3;


    private static final String TRANSLATION_Y = "translationY";

    private static final String ROTATION = "rotation";

    ArrayList<Task> taskListFromYap = new ArrayList<>();
    //ArrayList<Task> receiverTaskList = new ArrayList<>();
    ArrayList<Advice> receiverAdviceList = new ArrayList<>();

    private ListView logbookList;
    private RecyclerView homeList;

    public static homeMiddleFragment newInstance(){
        homeMiddleFragment fragment = new homeMiddleFragment();
        return fragment;
    }

    public homeMiddleFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_home_middle, container, false);



        //yapDroid = YapDroid.newInstance(this);

        DB_Read read = new DB_Read(getContext());
        if (!read.MyData_HasData()) {
            ShowDialogAddData();
            read.close();
            //return;
        }
        read.close();
        homeList = (RecyclerView) layout.findViewById(R.id.HomeListDisplay);

        fabContainer_v = (LinearLayout) layout.findViewById(R.id.fab_container_v);


        fab = (FloatingActionButton) layout.findViewById(R.id.fab);

        miniFab1 = (FloatingActionButton) layout.findViewById(R.id.mini_fab1);
        miniFab2 = (FloatingActionButton) layout.findViewById(R.id.mini_fab2);
        miniFab3 = (FloatingActionButton) layout.findViewById(R.id.mini_fab3);




        logbookList = (ListView) layout.findViewById(R.id.LogbookActivityList);

        setFabClickListeners();
        setOffsets();
        fillDates();
        fillHomeList();

        return layout;
    }

    private void fillHomeList() {

        fillTaskList();
        fillAdviceList();

        ListsDataDb db = new ListsDataDb(MyDiabetesStorage.getInstance(getContext()));
        Cursor cursor = db.getAllLogbookListWithin(10);
        //HomeAdapter homeAdapter = new HomeAdapter(receiverAdviceList, taskListFromYap, cursor,this, yapDroid);
        HomeAdapter homeAdapter = new HomeAdapter(receiverAdviceList, taskListFromYap, cursor,getContext());

        ItemTouchHelper.Callback callback = new HomeTouchHelper(homeAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(homeList);
        homeList.setAdapter(homeAdapter);
        homeList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setFabClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(fabOpen){
                    disableFloationActionButtonOptions();
                    fabOpen = false;
                }else{
                    enableFloationActionButtonOptions();
                    fabOpen = true;
                }
            }
        });
        miniFab1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GlycemiaDetail.class);
                startActivity(intent);
            }
        });
        miniFab2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InsulinDetail.class);
                startActivity(intent);
            }
        });
        miniFab3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MealActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setOffsets(){
        fabContainer_v.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer_v.getViewTreeObserver().removeOnPreDrawListener(this);
                offset1 = fab.getY() - miniFab1.getY();
                miniFab1.setTranslationY(offset1);
                offset2 = fab.getY() - miniFab2.getY();
                miniFab2.setTranslationY(offset2);
                offset3 = fab.getY() - miniFab3.getY();
                miniFab3.setTranslationY(offset3);
                return true;
            }
        });
    }


    /**
     * @param view
     * @param ang How many degrees to rotate
     * @return
     */
    private Animator createRotationAnimator(View view, float ang) {
        float rotation = fab.getRotation();
        return ObjectAnimator.ofFloat(view, ROTATION, rotation, rotation + ang)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
    private Animator createCollapseAnimatorY(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
    private Animator createExpandAnimatorY(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private void disableFloationActionButtonOptions() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                createRotationAnimator(fab, 45f),
                createCollapseAnimatorY(miniFab1, offset1),
                createCollapseAnimatorY(miniFab2, offset2),
                createCollapseAnimatorY(miniFab3, offset3));


        animatorSet.start();
    }
    private void enableFloationActionButtonOptions() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                createRotationAnimator(fab, -45f),
                createExpandAnimatorY(miniFab1, offset1),
                createExpandAnimatorY(miniFab2, offset2),
                createExpandAnimatorY(miniFab3, offset3));
        animatorSet.start();
    }

    private void fillTaskList() {
        //taskListFromYap = yapDroid.getYapMultipleTasks();
        Task task1 = new Task();
        task1.setSummaryText("Fazer exercicio hoje!");
        task1.setExpText("Hoje fiquei de fazer exercicio. O gim está à minha espera!");
        task1.setUrg(5);

        Task task2 = new Task();
        task2.setSummaryText("Actualizar dados!");
        task2.setExpText("Fazer a sincronização da bomba com a aplicação!");
        task2.setUrg(3);

        taskListFromYap = new ArrayList<>();
        taskListFromYap.add(task1);
        taskListFromYap.add(task2);
    }
    public void fillAdviceList() {
        //receiverAdviceList.addAll(yapDroid.getAllEndAdvices(getApplicationContext()));
        Advice task1 = new Advice();
        task1.setSummaryText("Fazer exercicio hoje!");
        task1.setExpandedText("Hoje fiquei de fazer exercicio. O gim está à minha espera!");
        task1.setUrgency(5);

        Advice task2 = new Advice();
        task2.setSummaryText("Actualizar dados!");
        task2.setExpandedText("Fazer a sincronização da bomba com a aplicação!");
        task2.setUrgency(3);

        ArrayList<Advice> adviceList = new ArrayList<>();
        adviceList.add(task1);
        adviceList.add(task2);
        receiverAdviceList.addAll(adviceList);
        Collections.sort(receiverAdviceList);

    }

    public void fillDates() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -3);
        calendar = Calendar.getInstance();
    }

    public void ShowDialogAddData() {
        Intent intent = new Intent(getContext(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }



}
