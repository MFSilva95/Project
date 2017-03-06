package pt.it.porto.mydiabetes.ui.fragments.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;


import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.HbA1cRec;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.database.ListsDataDb;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.activities.BloodPressure;
import pt.it.porto.mydiabetes.ui.activities.BloodPressureDetail;
import pt.it.porto.mydiabetes.ui.activities.Cholesterol;
import pt.it.porto.mydiabetes.ui.activities.CholesterolDetail;
import pt.it.porto.mydiabetes.ui.activities.Disease;
import pt.it.porto.mydiabetes.ui.activities.DiseaseDetail;
import pt.it.porto.mydiabetes.ui.activities.Exercise;
import pt.it.porto.mydiabetes.ui.activities.ExerciseDetail;
import pt.it.porto.mydiabetes.ui.activities.ExercisesDetail;
import pt.it.porto.mydiabetes.ui.activities.GlycemiaDetail;
import pt.it.porto.mydiabetes.ui.activities.HbA1c;
import pt.it.porto.mydiabetes.ui.activities.HbA1cDetail;
import pt.it.porto.mydiabetes.ui.activities.InsulinDetail;
import pt.it.porto.mydiabetes.ui.activities.MealActivity;
import pt.it.porto.mydiabetes.ui.activities.WeightChartList;
import pt.it.porto.mydiabetes.ui.activities.WeightDetail;

import static android.app.Activity.RESULT_OK;


/**
 * Created by parra on 21/02/2017.
 */

public class homeLeftFragment extends Fragment  {


    private WeightRec weightRec;
    private CholesterolRec cholesterolRec;
    private ExerciseRec exerciseRec;
    private DiseaseRec diseaseRec;
    private BloodPressureRec bloodPressureRec;
    private HbA1cRec hbA1cRec;


    private ImageButton button1;
    private ImageButton button2;
    private ImageButton button3;
    private ImageButton button4;
    private ImageButton button5;
    private ImageButton button6;

    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private LinearLayout layout4;
    private LinearLayout layout5;
    private LinearLayout layout6;

    private TextView exerciseText;
    private TextView exerciseDate;
    private TextView diseaseText;
    private TextView diseaseDate;
    private TextView weightText;
    private TextView weightDate;
    private TextView bloodPressureText;
    private TextView bloodPressureDate;
    private TextView cholesterolText;
    private TextView cholesterolDate;
    private TextView hba1cText;
    private TextView hba1cDate;

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

        button1 = (ImageButton) layout.findViewById(R.id.button1);
        button2 = (ImageButton) layout.findViewById(R.id.button2);
        button3 = (ImageButton) layout.findViewById(R.id.button3);
        button4 = (ImageButton) layout.findViewById(R.id.button4);
        button5 = (ImageButton) layout.findViewById(R.id.button5);
        button6 = (ImageButton) layout.findViewById(R.id.button6);

        layout1 = (LinearLayout) layout.findViewById(R.id.layout1);
        layout2 = (LinearLayout) layout.findViewById(R.id.layout2);
        layout3 = (LinearLayout) layout.findViewById(R.id.layout3);
        layout4 = (LinearLayout) layout.findViewById(R.id.layout4);
        layout5 = (LinearLayout) layout.findViewById(R.id.layout5);
        layout6 = (LinearLayout) layout.findViewById(R.id.layout6);

        exerciseText = (TextView) layout.findViewById(R.id.value1);
        exerciseDate = (TextView) layout.findViewById(R.id.date1);
        diseaseText = (TextView) layout.findViewById(R.id.value2);
        diseaseDate = (TextView) layout.findViewById(R.id.date2);
        weightText = (TextView)layout.findViewById(R.id.value3);
        weightDate = (TextView)layout.findViewById(R.id.date3);
        bloodPressureText = (TextView) layout.findViewById(R.id.value4);
        bloodPressureDate = (TextView) layout.findViewById(R.id.date4);
        cholesterolText = (TextView) layout.findViewById(R.id.value5);
        cholesterolDate = (TextView) layout.findViewById(R.id.date5);
        hba1cText = (TextView) layout.findViewById(R.id.value6);
        hba1cDate = (TextView) layout.findViewById(R.id.date6);

        readDb();
        setButtonListeners();

        return layout;
    }


    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            Log.d("Exception date.",e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private void setButtonListeners() {

        /*LayoutInflater inflater = (getActivity()).getLayoutInflater();
        final View customView1 = inflater.inflate(R.layout.activity_exercise_detail,null);

        final MaterialStyledDialog addExercice = new MaterialStyledDialog.Builder(getContext())
                .setIcon(R.drawable.ic_weight)
                .withDialogAnimation(true)
                .setTitle("Adicionar Exercício")
                .setHeaderColor(R.color.green_background)
                .setCustomView(customView1)
                .setPositiveText("Adicionar")
                .setNegativeText("Cancelar")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/javiersantos/MaterialStyledDialogs/issues")));
                    }
                }).build();
        */

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), Exercise.class);
                startActivity(intent);
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), Disease.class);
                startActivity(intent);
            }
        });

        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), WeightChartList.class);
                startActivity(intent);
            }
        });

        layout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), BloodPressure.class);
                startActivity(intent);
            }
        });

        layout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), Cholesterol.class);
                startActivity(intent);
            }
        });

        layout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), HbA1c.class);
                startActivity(intent);
            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), ExerciseDetail.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), DiseaseDetail.class);
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), WeightDetail.class);
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), BloodPressureDetail.class);
                startActivity(intent);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), CholesterolDetail.class);
                startActivity(intent);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addExercice.show();
                Intent intent = new Intent(view.getContext(), HbA1cDetail.class);
                startActivity(intent);
            }
        });

    }

    private void readDb(){
        long currentTime= System.currentTimeMillis();
        //Read MyData From DB
        DB_Read db = new DB_Read(getContext());
        weightRec = db.getLastWeight();
        cholesterolRec = db.getLastCholesterol();
        exerciseRec = db.getLastExercice();
        diseaseRec = db.getLastDisease();
        bloodPressureRec = db.getLastBloodPressure();
        hbA1cRec = db.getLastHbA1c();
        db.close();

        if(exerciseRec!= null){
            exerciseText.setText(exerciseRec.getDuration()+"");
            String time = exerciseRec.getFormattedDate()+" "+exerciseRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.MINUTE_IN_MILLIS);
            exerciseDate.setText(dateText+"");
        }

        if(diseaseRec!= null){
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date date1 = myFormat.parse(diseaseRec.getStartDate());
                Date date2;
                long diff;
                if(diseaseRec.getEndDate() == null){
                    diff = currentTime - date1.getTime();
                }else{
                    date2 = myFormat.parse(diseaseRec.getEndDate());
                    diff = date2.getTime() - date1.getTime();
                }

                diseaseText.setText(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+"");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String time = diseaseRec.getStartDate()+" 00:00";
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.DAY_IN_MILLIS);
            diseaseDate.setText(dateText+"");
        }

        if(bloodPressureRec != null){
            bloodPressureText.setText(bloodPressureRec.getSystolic()+"/"+bloodPressureRec.getDiastolic());
            String time = bloodPressureRec.getFormattedDate()+" "+bloodPressureRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.MINUTE_IN_MILLIS);
            bloodPressureDate.setText(dateText+"");
        }

        if(cholesterolRec != null){
            cholesterolText.setText(cholesterolRec.getValue()+"");
            String time = cholesterolRec.getFormattedDate()+" "+cholesterolRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.MINUTE_IN_MILLIS);
            cholesterolDate.setText(dateText+"");
        }


        if(hbA1cRec != null){
            hba1cText.setText(hbA1cRec.getValue()+"");
            String time = hbA1cRec.getFormattedDate()+" "+hbA1cRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.MINUTE_IN_MILLIS);
            hba1cDate.setText(dateText+"");
        }

        if(weightRec != null) {
            weightText.setText(weightRec.getValue() + "");
            String time = weightRec.getFormattedDate()+" "+weightRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.MINUTE_IN_MILLIS);
            weightDate.setText(dateText+"");
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        readDb();
    }
}
