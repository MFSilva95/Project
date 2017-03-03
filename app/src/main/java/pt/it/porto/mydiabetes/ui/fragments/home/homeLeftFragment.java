package pt.it.porto.mydiabetes.ui.fragments.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.database.ListsDataDb;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.CholesterolDetail;
import pt.it.porto.mydiabetes.ui.activities.DiseaseDetail;
import pt.it.porto.mydiabetes.ui.activities.ExerciseDetail;
import pt.it.porto.mydiabetes.ui.activities.GlycemiaDetail;
import pt.it.porto.mydiabetes.ui.activities.InsulinDetail;
import pt.it.porto.mydiabetes.ui.activities.MealActivity;
import pt.it.porto.mydiabetes.ui.activities.WeightDetail;

import pt.it.porto.mydiabetes.ui.activities.WelcomeActivity;

/**
 * Created by parra on 21/02/2017.
 */

public class homeLeftFragment extends Fragment  {

    private WeightRec weightRec;
    private CholesterolRec cholesterolRec;
    private ExerciseRec exerciseRec;
    private DiseaseRec diseaseRec;

    private FloatingActionButton miniFab4;
    private FloatingActionButton miniFab5;
    private FloatingActionButton miniFab6;
    private FloatingActionButton miniFab7;

    boolean fabOpen = false;
    private FloatingActionButton fab;
    private LinearLayout fabContainer_h;

    private float offset4;
    private float offset5;
    private float offset6;
    private float offset7;

    private static final String TRANSLATION_Y = "translationY";
    private static final String ROTATION = "rotation";


    public static pt.it.porto.mydiabetes.ui.fragments.home.homeLeftFragment newInstance() {
        pt.it.porto.mydiabetes.ui.fragments.home.homeLeftFragment fragment = new pt.it.porto.mydiabetes.ui.fragments.home.homeLeftFragment();
        return fragment;
    }

    public homeLeftFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_home_left, container, false);

        fabContainer_h = (LinearLayout) layout.findViewById(R.id.fab_container_h);
        fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        miniFab4 = (FloatingActionButton) layout.findViewById(R.id.mini_fab4);
        miniFab5 = (FloatingActionButton) layout.findViewById(R.id.mini_fab5);
        miniFab6 = (FloatingActionButton) layout.findViewById(R.id.mini_fab6);
        miniFab7 = (FloatingActionButton) layout.findViewById(R.id.mini_fab7);

        TextView weightText = (TextView)layout.findViewById(R.id.value3);
        TextView weightDate = (TextView)layout.findViewById(R.id.date3);
        TextView cholesterolText = (TextView) layout.findViewById(R.id.value5);
        TextView cholesterolDate = (TextView) layout.findViewById(R.id.date5);
        TextView exerciseText = (TextView) layout.findViewById(R.id.value1);
        TextView exerciseDate = (TextView) layout.findViewById(R.id.date1);
        TextView diseaseText = (TextView) layout.findViewById(R.id.value2);
        TextView diseaseDate = (TextView) layout.findViewById(R.id.date2);


        //Read MyData From DB
        DB_Read db = new DB_Read(getContext());
        weightRec = db.getLastWeight();
        cholesterolRec = db.getLastCholesterol();
        exerciseRec = db.getLastExercice();
        diseaseRec = db.getLastDisease();

        db.close();

        if(exerciseRec!= null){
            exerciseText.setText(exerciseRec.getDuration()+"");
            exerciseDate.setText(exerciseRec.getFormattedDate()+"");
        }

        if(diseaseRec!= null){
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date date2 = myFormat.parse(diseaseRec.getEndDate());
                Date date1 = myFormat.parse(diseaseRec.getStartDate());
                long diff = date2.getTime() - date1.getTime();
                diseaseText.setText(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+"");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            diseaseDate.setText(diseaseRec.getStartDate());
        }

        if(cholesterolRec != null){
            cholesterolText.setText(cholesterolRec.getValue()+"");
            cholesterolDate.setText(cholesterolRec.getFormattedDate()+"");
        }

        if(weightRec != null) {
            weightText.setText(weightRec.getValue() + "");
            weightDate.setText(weightRec.getFormattedDate() + "");
        }

        setFabClickListeners();
        setOffsets();

        return layout;
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

        miniFab4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DiseaseDetail.class);
                startActivity(intent);
            }
        });
        miniFab5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CholesterolDetail.class);
                startActivity(intent);
            }
        });
        miniFab6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WeightDetail.class);
                startActivity(intent);
            }
        });
        miniFab7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExerciseDetail.class);
                startActivity(intent);
            }
        });
    }
    private void setOffsets(){
        fabContainer_h.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fabContainer_h.getViewTreeObserver().removeOnPreDrawListener(this);
                offset4 = fab.getY() - miniFab4.getY();
                miniFab4.setTranslationY(offset4);
                offset5 = fab.getY() - miniFab5.getY();
                miniFab5.setTranslationY(offset5);
                offset6 = fab.getY() - miniFab6.getY();
                miniFab6.setTranslationY(offset6);
                offset7 = fab.getY() - miniFab7.getY();
                miniFab7.setTranslationY(offset7);
                return true;
            }
        });
    }

    private Animator createRotationAnimator(View view, float ang) {
        float rotation = fab.getRotation();
        return ObjectAnimator.ofFloat(view, ROTATION, rotation, rotation + ang)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }
    private void disableFloationActionButtonOptions() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                createRotationAnimator(fab, 45f),
                createCollapseAnimatorX(miniFab4, offset4),
                createCollapseAnimatorX(miniFab5, offset5),
                createCollapseAnimatorX(miniFab6, offset6),
                createCollapseAnimatorX(miniFab7, offset7));

        animatorSet.start();
    }
    private void enableFloationActionButtonOptions() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                createRotationAnimator(fab, -45f),
                createExpandAnimatorX(miniFab4, offset4),
                createExpandAnimatorX(miniFab5, offset5),
                createExpandAnimatorX(miniFab6, offset6),
                createExpandAnimatorX(miniFab7, offset7));
        animatorSet.start();
    }

    private Animator createCollapseAnimatorX(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private Animator createExpandAnimatorX(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }



}
