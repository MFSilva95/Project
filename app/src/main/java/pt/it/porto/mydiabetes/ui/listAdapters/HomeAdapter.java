package pt.it.porto.mydiabetes.ui.listAdapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.Day;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.Task;
import pt.it.porto.mydiabetes.ui.activities.BloodPressure;
import pt.it.porto.mydiabetes.ui.activities.Cholesterol;
import pt.it.porto.mydiabetes.ui.activities.DetailLogbookActivity;
import pt.it.porto.mydiabetes.ui.activities.Disease;
import pt.it.porto.mydiabetes.ui.activities.Exercise;
import pt.it.porto.mydiabetes.ui.activities.HbA1c;
import pt.it.porto.mydiabetes.ui.activities.WeightChartList;
import pt.it.porto.mydiabetes.ui.charts.data.Weight;
import pt.it.porto.mydiabetes.utils.HomeElement;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>{

    Context c;
    private List<HomeElement> homeList;


    public HomeElement getFromHomeList(int index){
        return homeList.get(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout view;
        public ViewHolder(LinearLayout v) {
            super(v);
            view = v;
        }
    }

    public HomeAdapter(LinkedList<HomeElement> homeList) {
        this.homeList = new LinkedList<>();
        this.homeList.addAll(homeList);
    }

    public void updateList(LinkedList<HomeElement> homeList) {
        this.homeList = new LinkedList<>();
        this.homeList.addAll(homeList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View v = null;
        ViewHolder vh;
        switch (viewType) {
            case 0:
//                Log.i("________POSITION_____", ADVICE");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_advice_row, parent, false);
                vh = new ViewHolder((LinearLayout) v);
                return vh;

            case 1:
                // Log.i("________POSITION_____", "TASK");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_task_row, parent, false);
                vh = new ViewHolder((LinearLayout) v);
                v.setTag(vh);
                return vh;
            case 2:
                // Log.i("________POSITION_____", "HEADER");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_home_row, parent, false);
                vh = new ViewHolder((LinearLayout) v);
                v.setTag(vh);
                return vh;

            case 3:
                //Log.i("________POSITION_____", "EMPTY");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_home_row, parent, false);
                vh = new ViewHolder((LinearLayout) v);
                v.setTag(vh);
                return vh;
            case 4:
                // Log.i("________POSITION_____", "DAY");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_day_row, parent, false);
                vh = new ViewHolder((LinearLayout) v);
                v.setTag(vh);
                return vh;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (homeList.get(position).getDisplayType()){
            case ADVICE:
                return 0;
            case TASK:
                return 1;
            case HEADER:
                return 2;
            case SPACE:
                return 3;
            case DAY:
                return 4;
        }
        return 4;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HomeElement currentView = homeList.get(position);
        LinearLayout textHolder = null;
        TextView rowText = null;
        TextView myText = null;
        View v = holder.view;
        //Log.i("Home", "onBindViewHolder: "+currentView.getDisplayType()+" Pos:"+position);
        switch (currentView.getDisplayType()) {
            case HEADER:
                //its an header
                textHolder = (LinearLayout) v.findViewById(R.id.headerRowBackground);
                //textHolder = (LinearLayout) holder.view.getChildAt(0);
                //textHolder.setBackgroundColor(Color.parseColor("#abbbcb"));
                rowText = (TextView) textHolder.getChildAt(0);
                //rowText.setTextColor(ContextCompat.getColor(c, R.color.cardview_light_background));
                rowText.setText(currentView.getName());
                break;

            case ADVICE:
                //its an advice
                final Advice currentAdvice = (Advice) currentView;
                textHolder = (LinearLayout) v.findViewById(R.id.adviceRowBackground);
                myText = (TextView) v.findViewById(R.id.content);
                //textHolder = (LinearLayout) holder.view.getChildAt(0);
                //textHolder.setBackgroundColor(Color.parseColor("#abbbcb"));
                textHolder.setBackgroundColor(Color.parseColor("#cceeeeee"));
                //myText= (TextView) textHolder.getChildAt(1);

                if (currentAdvice.getUrgency() > 7) {
                    myText.setTextColor(Color.RED);
                }
                //textHolder.setBackgroundColor(Color.parseColor("#cceeeeee"));
//                rowText = (TextView) textHolder.getChildAt(0);
//                rowText.setText(currentAdvice.getSummaryText());
                myText.setText(currentAdvice.getSummaryText());

                holder.view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        // Get the layout inflater
                        LayoutInflater inflater = LayoutInflater.from(v.getContext());
                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        builder.setView(inflater.inflate(R.layout.dialog_exp_advice, null));

                        if (!currentAdvice.getAdviceType().equals("ALERT") && !currentAdvice.getAdviceType().equals("NORMAL")) {
                            builder.setNegativeButton(v.getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (currentAdvice.getAdviceType().equals("QUESTION")) {
                                        //myYapInstance.insertRule("has("+currentAdvice.getRegistryType()+")");
                                    }
                                    if (currentAdvice.getAdviceType().equals("SUGGESTION")) {
                                        Class<?> wantedAct = null;
                                        String classPath = "";
                                        try {
                                            classPath = "pt.it.porto.mydiabetes.ui.activities." + currentAdvice.getRegistryType();
                                            wantedAct = Class.forName(classPath);

                                        } catch (ClassNotFoundException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                        Intent intent = new Intent(c, wantedAct);
                                        c.startActivity(intent);
                                    }
                                    dialog.dismiss();
                                }
                            });
                        }

                        Dialog dialog = builder.create();
                        dialog.show();

                        TextView textView = (TextView) dialog.findViewById(R.id.popup_text);
                        textView.setText(currentAdvice.getExpandedText());

                    }
                });

                break;

            case SPACE:



                break;
            case TASK:
                //its a task
                final Task currentTask = (Task) currentView;
                textHolder = (LinearLayout) v.findViewById(R.id.taskRowBackground);
                //textHolder = (LinearLayout) holder.view.getChildAt(0);
                //textHolder.setBackgroundColor(Color.parseColor("#abbbcb"));
                textHolder.setBackgroundColor(Color.parseColor("#cceeeeee"));
                myText = (TextView) v.findViewById(R.id.content);
                //myText = (TextView) textHolder.getChildAt(1);
                if (currentTask.getUrgency() > 7) {
                    myText.setTextColor(Color.RED);
                }
                //textHolder.setBackgroundColor(Color.parseColor("#cceeeeee"));
                myText.setText(currentTask.getSummaryText());
//                rowText = (TextView) textHolder.getChildAt(1);
//                rowText.setText(currentTask.getSummaryText());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        // Get the layout inflater
                        LayoutInflater inflater = LayoutInflater.from(v.getContext());
                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        builder.setView(inflater.inflate(R.layout.dialog_exp_task, null));

                        Dialog dialog = builder.create();
                        dialog.show();

                        TextView textView = (TextView) dialog.findViewById(R.id.popup_task_text);
                        textView.setText(currentTask.getExpandedText());
                    }
                });
                break;

            case DAY:
                final Day currentDay = (Day) currentView;
                Log.e("DAY", currentDay.toString());
                showLogBook(currentDay, holder);
                showExercice(currentDay, holder);
                showDisease(currentDay, holder);
                showWeight(currentDay, holder);
                showBloodPressure(currentDay, holder);
                showCholesterol(currentDay, holder);
                showHbA1c(currentDay, holder);

                break;
        }
    }

    /*public void remove(int position) {
        //Log.i("HOME", "remove: POS:"+position);
        int headerPos = -1;
        HomeElement toBeRemoved = homeList.get(position);
        homeList.remove(position);
        notifyItemRemoved(position);

        if(toBeRemoved.getDisplayType().equals(HomeElement.Type.ADVICE)){
            nAdvices--;
            if(nAdvices==0){
                headerPos = homeList.indexOf(getHeader(c.getString(R.string.advices)));
                homeList.remove(headerPos);
                notifyItemRemoved(headerPos);
            }
        }
        if(toBeRemoved.getDisplayType().equals(HomeElement.Type.TASK)){
            nTasks--;
            if(nTasks==0){
                headerPos = homeList.indexOf(getHeader(c.getString(R.string.tasks)));
                homeList.remove(headerPos);
                notifyItemRemoved(headerPos);
            }
        }
    }
    public HomeElement getHeader(String title){
        for(HomeElement elem:homeList){
            if(elem.getDisplayType().equals(HomeElement.Type.HEADER) && elem.getName().equals(title)){
                return elem;
            }
        }
        return null;
    }*/

    @Override
    public int getItemCount() {
        return homeList.size();
    }

    private void showLogBook(Day currentDay, ViewHolder holder) {
        View v = holder.view;
        final ListView logbookRecords = (ListView) v.findViewById(R.id.logbookRecords);
        if(currentDay.getLogBookEntries().size() == 0 ){
            logbookRecords.setVisibility(View.GONE);
        }
        else{
            logbookRecords.setVisibility(View.VISIBLE);
            LogBookAdapter logBookAdapter = new LogBookAdapter(currentDay.getLogBookEntries(), v.getContext());
            logbookRecords.setAdapter(logBookAdapter);
            justifyListViewHeightBasedOnChildren(logbookRecords);
        }

    }

    public void justifyListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }



    private void showExercice(Day currentDay, ViewHolder holder) {
        View v = holder.view;
        final RelativeLayout exerciceRecords = (RelativeLayout) v.findViewById(R.id.exerciceRecords);
        exerciceRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Exercise.class);
                exerciceRecords.getContext().startActivity(intent);
            }
        });

        LinearLayout exercice_rec_one = (LinearLayout) exerciceRecords.findViewById(R.id.exercice_rec_one);
        LinearLayout exercice_rec_two = (LinearLayout) exerciceRecords.findViewById(R.id.exercice_rec_two);
        LinearLayout exercice_rec_three = (LinearLayout) exerciceRecords.findViewById(R.id.exercice_rec_three);
        TextView value1 = (TextView) exerciceRecords.findViewById(R.id.value1);
        TextView type1 = (TextView) exerciceRecords.findViewById(R.id.type1);
        TextView value2 = (TextView) exerciceRecords.findViewById(R.id.value2);
        TextView type2 = (TextView) exerciceRecords.findViewById(R.id.type2);
        TextView value3 = (TextView) exerciceRecords.findViewById(R.id.value3);
        TextView type3 = (TextView) exerciceRecords.findViewById(R.id.type3);
        if(currentDay.getExerciseList().size() == 0 ){
            exerciceRecords.setVisibility(View.GONE);
        }
        else{
            exerciceRecords.setVisibility(View.VISIBLE);
            switch(currentDay.getExerciseList().size()){
                case 1:
                    exercice_rec_one.setVisibility(View.VISIBLE);
                    exercice_rec_two.setVisibility(View.GONE);
                    exercice_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getExerciseList().get(0).getDuration()+"");
                    type1.setText(currentDay.getExerciseList().get(0).getExercise());
                    break;
                case 2:
                    exercice_rec_one.setVisibility(View.VISIBLE);
                    exercice_rec_two.setVisibility(View.VISIBLE);
                    exercice_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getExerciseList().get(0).getDuration()+"");
                    type1.setText(currentDay.getExerciseList().get(0).getExercise());
                    value2.setText(currentDay.getExerciseList().get(1).getDuration()+"");
                    type2.setText(currentDay.getExerciseList().get(1).getExercise());
                    break;
                case 3:
                    exercice_rec_one.setVisibility(View.VISIBLE);
                    exercice_rec_two.setVisibility(View.VISIBLE);
                    exercice_rec_three.setVisibility(View.VISIBLE);
                    value1.setText(currentDay.getExerciseList().get(0).getDuration()+"");
                    type1.setText(currentDay.getExerciseList().get(0).getExercise());
                    value2.setText(currentDay.getExerciseList().get(1).getDuration()+"");
                    type2.setText(currentDay.getExerciseList().get(1).getExercise());
                    value3.setText(currentDay.getExerciseList().get(2).getDuration()+"");
                    type3.setText(currentDay.getExerciseList().get(2).getExercise());
                    break;
            }
        }
    }

    private void showDisease(Day currentDay, ViewHolder holder) {
        View v = holder.view;
        final RelativeLayout diseaseRecords = (RelativeLayout) v.findViewById(R.id.diseaseRecords);
        diseaseRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Disease.class);
                diseaseRecords.getContext().startActivity(intent);
            }
        });

        LinearLayout disease_rec_one = (LinearLayout) diseaseRecords.findViewById(R.id.disease_rec_one);
        LinearLayout disease_rec_two = (LinearLayout) diseaseRecords.findViewById(R.id.disease_rec_two);
        LinearLayout disease_rec_three = (LinearLayout) diseaseRecords.findViewById(R.id.disease_rec_three);
        TextView value1 = (TextView) diseaseRecords.findViewById(R.id.value1);
        TextView value2 = (TextView) diseaseRecords.findViewById(R.id.value2);
        TextView value3 = (TextView) diseaseRecords.findViewById(R.id.value3);
        if(currentDay.getDiseaseList().size() == 0 ){
            diseaseRecords.setVisibility(View.GONE);
        }
        else{
            diseaseRecords.setVisibility(View.VISIBLE);
            switch(currentDay.getDiseaseList().size()){
                case 1:
                    disease_rec_one.setVisibility(View.VISIBLE);
                    disease_rec_two.setVisibility(View.GONE);
                    disease_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getDiseaseList().get(0).getDisease());
                    break;
                case 2:
                    disease_rec_one.setVisibility(View.VISIBLE);
                    disease_rec_two.setVisibility(View.VISIBLE);
                    disease_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getDiseaseList().get(0).getDisease());
                    value2.setText(currentDay.getDiseaseList().get(1).getDisease());
                    break;
                case 3:
                    disease_rec_one.setVisibility(View.VISIBLE);
                    disease_rec_two.setVisibility(View.VISIBLE);
                    disease_rec_three.setVisibility(View.VISIBLE);
                    value1.setText(currentDay.getDiseaseList().get(0).getDisease());
                    value2.setText(currentDay.getDiseaseList().get(1).getDisease());
                    value3.setText(currentDay.getDiseaseList().get(2).getDisease());
                    break;
            }
        }
    }

    private void showWeight(Day currentDay, ViewHolder holder) {
        View v = holder.view;
        final RelativeLayout weightRecords = (RelativeLayout) v.findViewById(R.id.weightRecords);
        weightRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), WeightChartList.class);
                weightRecords.getContext().startActivity(intent);
            }
        });

        LinearLayout weight_rec_one = (LinearLayout) weightRecords.findViewById(R.id.weight_rec_one);
        LinearLayout weight_rec_two = (LinearLayout) weightRecords.findViewById(R.id.weight_rec_two);
        LinearLayout weight_rec_three = (LinearLayout) weightRecords.findViewById(R.id.weight_rec_three);
        TextView value1 = (TextView) weightRecords.findViewById(R.id.value1);
        TextView value2 = (TextView) weightRecords.findViewById(R.id.value2);
        TextView value3 = (TextView) weightRecords.findViewById(R.id.value3);
        if(currentDay.getWeightList().size() == 0 ){
            weightRecords.setVisibility(View.GONE);
        }
        else{
            weightRecords.setVisibility(View.VISIBLE);
            switch(currentDay.getWeightList().size()){
                case 1:
                    weight_rec_one.setVisibility(View.VISIBLE);
                    weight_rec_two.setVisibility(View.GONE);
                    weight_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getWeightList().get(0).getValue()+"");
                    break;
                case 2:
                    weight_rec_one.setVisibility(View.VISIBLE);
                    weight_rec_two.setVisibility(View.VISIBLE);
                    weight_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getWeightList().get(0).getValue()+"");
                    value2.setText(currentDay.getWeightList().get(1).getValue()+"");
                    break;
                case 3:
                    weight_rec_one.setVisibility(View.VISIBLE);
                    weight_rec_two.setVisibility(View.VISIBLE);
                    weight_rec_three.setVisibility(View.VISIBLE);
                    value1.setText(currentDay.getWeightList().get(0).getValue()+"");
                    value2.setText(currentDay.getWeightList().get(1).getValue()+"");
                    value3.setText(currentDay.getWeightList().get(2).getValue()+"");
                    break;
            }
        }
    }

    private void showBloodPressure(Day currentDay, ViewHolder holder) {
            View v = holder.view;
            final RelativeLayout bloodPressureRecords = (RelativeLayout) v.findViewById(R.id.bloodPressureRecords);
            bloodPressureRecords.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), BloodPressure.class);
                    bloodPressureRecords.getContext().startActivity(intent);
                }
            });

            LinearLayout bloodPressure_rec_one = (LinearLayout) bloodPressureRecords.findViewById(R.id.bloodPressure_rec_one);
            LinearLayout bloodPressure_rec_two = (LinearLayout) bloodPressureRecords.findViewById(R.id.bloodPressure_rec_two);
            LinearLayout bloodPressure_rec_three = (LinearLayout) bloodPressureRecords.findViewById(R.id.bloodPressure_rec_three);
            TextView value1 = (TextView) bloodPressureRecords.findViewById(R.id.value1);
            TextView value2 = (TextView) bloodPressureRecords.findViewById(R.id.value2);
            TextView value3 = (TextView) bloodPressureRecords.findViewById(R.id.value3);
            if(currentDay.getBloodPressureList().size() == 0 ){
                bloodPressureRecords.setVisibility(View.GONE);
            }
            else{
                bloodPressureRecords.setVisibility(View.VISIBLE);
                switch(currentDay.getBloodPressureList().size()){
                    case 1:
                        bloodPressure_rec_one.setVisibility(View.VISIBLE);
                        bloodPressure_rec_two.setVisibility(View.GONE);
                        bloodPressure_rec_three.setVisibility(View.GONE);
                        value1.setText(currentDay.getBloodPressureList().get(0).getSystolic()+" / "+currentDay.getBloodPressureList().get(0).getDiastolic());
                        break;
                    case 2:
                        bloodPressure_rec_one.setVisibility(View.VISIBLE);
                        bloodPressure_rec_two.setVisibility(View.VISIBLE);
                        bloodPressure_rec_three.setVisibility(View.GONE);
                        value1.setText(currentDay.getBloodPressureList().get(0).getSystolic()+" / "+currentDay.getBloodPressureList().get(0).getDiastolic());
                        value2.setText(currentDay.getBloodPressureList().get(1).getSystolic()+" / "+currentDay.getBloodPressureList().get(1).getDiastolic());
                        break;
                    case 3:
                        bloodPressure_rec_one.setVisibility(View.VISIBLE);
                        bloodPressure_rec_two.setVisibility(View.VISIBLE);
                        bloodPressure_rec_three.setVisibility(View.VISIBLE);
                        value1.setText(currentDay.getBloodPressureList().get(0).getSystolic()+" / "+currentDay.getBloodPressureList().get(0).getDiastolic());
                        value2.setText(currentDay.getBloodPressureList().get(1).getSystolic()+" / "+currentDay.getBloodPressureList().get(1).getDiastolic());
                        value3.setText(currentDay.getBloodPressureList().get(2).getSystolic()+" / "+currentDay.getBloodPressureList().get(2).getDiastolic());
                        break;
                }
            }
    }

    private void showCholesterol(Day currentDay, ViewHolder holder) {
        View v = holder.view;
        final RelativeLayout cholesterolRecords = (RelativeLayout) v.findViewById(R.id.cholesterolRecords);
        cholesterolRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Cholesterol.class);
                cholesterolRecords.getContext().startActivity(intent);
            }
        });

        LinearLayout cholesterol_rec_one = (LinearLayout) cholesterolRecords.findViewById(R.id.cholesterol_rec_one);
        LinearLayout cholesterol_rec_two = (LinearLayout) cholesterolRecords.findViewById(R.id.cholesterol_rec_two);
        LinearLayout cholesterol_rec_three = (LinearLayout) cholesterolRecords.findViewById(R.id.cholesterol_rec_three);
        TextView value1 = (TextView) cholesterolRecords.findViewById(R.id.value1);
        TextView value2 = (TextView) cholesterolRecords.findViewById(R.id.value2);
        TextView value3 = (TextView) cholesterolRecords.findViewById(R.id.value3);
        if(currentDay.getCholesterolList().size() == 0 ){
            cholesterolRecords.setVisibility(View.GONE);
        }
        else{
            cholesterolRecords.setVisibility(View.VISIBLE);
            switch(currentDay.getCholesterolList().size()){
                case 1:
                    cholesterol_rec_one.setVisibility(View.VISIBLE);
                    cholesterol_rec_two.setVisibility(View.GONE);
                    cholesterol_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getCholesterolList().get(0).getValue().toString());
                    break;
                case 2:
                    cholesterol_rec_one.setVisibility(View.VISIBLE);
                    cholesterol_rec_two.setVisibility(View.VISIBLE);
                    cholesterol_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getCholesterolList().get(0).getValue().toString());
                    value2.setText(currentDay.getCholesterolList().get(1).getValue().toString());
                    break;
                case 3:
                    cholesterol_rec_one.setVisibility(View.VISIBLE);
                    cholesterol_rec_two.setVisibility(View.VISIBLE);
                    cholesterol_rec_three.setVisibility(View.VISIBLE);
                    value1.setText(currentDay.getCholesterolList().get(0).getValue().toString());
                    value2.setText(currentDay.getCholesterolList().get(1).getValue().toString());
                    value3.setText(currentDay.getCholesterolList().get(2).getValue().toString());
                    break;
            }
        }
    }

    private void showHbA1c(Day currentDay, ViewHolder holder) {
        View v = holder.view;
        final RelativeLayout hbA1cRecords = (RelativeLayout) v.findViewById(R.id.hbA1cRecords);
        hbA1cRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HbA1c.class);
                hbA1cRecords.getContext().startActivity(intent);
            }
        });

        LinearLayout hbA1c_rec_one = (LinearLayout) hbA1cRecords.findViewById(R.id.hbA1c_rec_one);
        LinearLayout hbA1c_rec_two = (LinearLayout) hbA1cRecords.findViewById(R.id.hbA1c_rec_two);
        LinearLayout hbA1c_rec_three = (LinearLayout) hbA1cRecords.findViewById(R.id.hbA1c_rec_three);
        TextView value1 = (TextView) hbA1cRecords.findViewById(R.id.value1);
        TextView value2 = (TextView) hbA1cRecords.findViewById(R.id.value2);
        TextView value3 = (TextView) hbA1cRecords.findViewById(R.id.value3);
        if(currentDay.getHbA1cList().size() == 0 ){
            hbA1cRecords.setVisibility(View.GONE);
        }
        else{
            hbA1cRecords.setVisibility(View.VISIBLE);
            switch(currentDay.getHbA1cList().size()){
                case 1:
                    hbA1c_rec_one.setVisibility(View.VISIBLE);
                    hbA1c_rec_two.setVisibility(View.GONE);
                    hbA1c_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getHbA1cList().get(0).getValue()+"");
                    break;
                case 2:
                    hbA1c_rec_one.setVisibility(View.VISIBLE);
                    hbA1c_rec_two.setVisibility(View.VISIBLE);
                    hbA1c_rec_three.setVisibility(View.GONE);
                    value1.setText(currentDay.getHbA1cList().get(0).getValue()+"");
                    value2.setText(currentDay.getHbA1cList().get(1).getValue()+"");
                    break;
                case 3:
                    hbA1c_rec_one.setVisibility(View.VISIBLE);
                    hbA1c_rec_two.setVisibility(View.VISIBLE);
                    hbA1c_rec_three.setVisibility(View.VISIBLE);
                    value1.setText(currentDay.getHbA1cList().get(0).getValue()+"");
                    value2.setText(currentDay.getHbA1cList().get(1).getValue()+"");
                    value3.setText(currentDay.getHbA1cList().get(2).getValue()+"");
                    break;
            }
        }
    }

}
