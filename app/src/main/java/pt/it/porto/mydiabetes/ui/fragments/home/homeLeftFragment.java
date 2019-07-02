package pt.it.porto.mydiabetes.ui.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.BloodPressureRec;
import pt.it.porto.mydiabetes.data.CholesterolRec;
import pt.it.porto.mydiabetes.data.DiseaseRec;
import pt.it.porto.mydiabetes.data.ExerciseRec;
import pt.it.porto.mydiabetes.data.HbA1cRec;
import pt.it.porto.mydiabetes.data.WeightRec;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.BloodPressure;
import pt.it.porto.mydiabetes.ui.activities.Cholesterol;
import pt.it.porto.mydiabetes.ui.activities.Disease;
import pt.it.porto.mydiabetes.ui.activities.Exercise;
import pt.it.porto.mydiabetes.ui.activities.HbA1c;
import pt.it.porto.mydiabetes.ui.activities.WeightChartList;




/**
 * Created by parra on 21/02/2017.
 */

public class homeLeftFragment extends Fragment {


    private WeightRec weightRec;
    private CholesterolRec cholesterolRec;
    private ExerciseRec exerciseRec;
    private DiseaseRec diseaseRec;
    private BloodPressureRec bloodPressureRec;
    private HbA1cRec hbA1cRec;

    private CardView layout1;
    private CardView layout2;
    private CardView layout3;
    private CardView layout4;
    private CardView layout5;
    private CardView layout6;

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

        layout1 = (CardView) layout.findViewById(R.id.foodConstrainLayout);
        layout2 = (CardView) layout.findViewById(R.id.DiseaseCardView);
        layout3 = (CardView) layout.findViewById(R.id.layout3);
        layout4 = (CardView) layout.findViewById(R.id.layout4);
        layout5 = (CardView) layout.findViewById(R.id.layout5);
        layout6 = (CardView) layout.findViewById(R.id.layout6);

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
        else{
            exerciseText.setText(getString(R.string.n_a));
            exerciseDate.setText("");
        }

        if(diseaseRec!= null){
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date date1 = myFormat.parse(diseaseRec.getFormattedDate());
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
            String time = diseaseRec.getFormattedDate()+" "+diseaseRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.DAY_IN_MILLIS);
            diseaseDate.setText(dateText+"");
        }
        else{
            diseaseText.setText(getString(R.string.n_a));
            diseaseDate.setText("");
        }


        if(bloodPressureRec != null){
            bloodPressureText.setText(bloodPressureRec.getSystolic()+"/"+bloodPressureRec.getDiastolic());
            String time = bloodPressureRec.getFormattedDate()+" "+bloodPressureRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.MINUTE_IN_MILLIS);
            bloodPressureDate.setText(dateText+"");
        }
        else{
            bloodPressureText.setText(getString(R.string.n_a));
            bloodPressureDate.setText("");
        }

        if(cholesterolRec != null){
            cholesterolText.setText(cholesterolRec.getValue()+"");
            String time = cholesterolRec.getFormattedDate()+" "+cholesterolRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.MINUTE_IN_MILLIS);
            cholesterolDate.setText(dateText+"");
        }
        else{
            cholesterolText.setText(getString(R.string.n_a));
            cholesterolDate.setText("");
        }

        if(hbA1cRec != null){
            hba1cText.setText(hbA1cRec.getValue()+"");
            String time = hbA1cRec.getFormattedDate()+" "+hbA1cRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.MINUTE_IN_MILLIS);
            hba1cDate.setText(dateText+"");
        }
        else{
            hba1cText.setText(getString(R.string.n_a));
            hba1cDate.setText("");
        }

        if(weightRec != null) {
            weightText.setText(weightRec.getValue() + "");
            String time = weightRec.getFormattedDate()+" "+weightRec.getFormattedTime();
            CharSequence dateText = DateUtils.getRelativeTimeSpanString(getDateInMillis(time), currentTime,DateUtils.MINUTE_IN_MILLIS);
            weightDate.setText(dateText+"");
        }
        else{
            weightText.setText(getString(R.string.n_a));
            weightDate.setText("");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        readDb();
    }
}
