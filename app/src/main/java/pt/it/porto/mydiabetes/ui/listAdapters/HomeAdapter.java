package pt.it.porto.mydiabetes.ui.listAdapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.adviceSystem.yapDroid.YapDroid;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.Task;
import pt.it.porto.mydiabetes.ui.activities.DetailLogbookActivity;
import pt.it.porto.mydiabetes.utils.HomeElement;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


/**
 * Created by Diogo on 11/05/2016.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>{

    Context c;
    private Cursor cursor;
    private ArrayList<HomeElement> homeList;
    private YapDroid myYapInstance;

    public HomeElement getFromHomeList(int index){
        return homeList.get(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        TextView data;
        TextView hora;
        TextView insulinValue;
        TextView insulinName;
        TextView gvalue;
        TextView cvalue;
        TextView ctag;
        TextView tag;
        TextView gtag;
        HomeElement item;

        public LinearLayout view;

        public ViewHolder(LinearLayout v, Boolean isLog) {
            super(v);
            view = v;
            if(isLog){
                if (view.getTag().equals("logbookItem")) {
                    data = (TextView) view.findViewById(R.id.tv_list_logbookreg_data);
                    hora = (TextView) view.findViewById(R.id.tv_list_logbookreg_hora);
                    insulinValue = (TextView) view.findViewById(R.id.tv_list_logbookreg_insulin_value);
                    insulinName = (TextView) view.findViewById(R.id.tv_list_logbookreg_insulin);
                    gvalue = (TextView) view.findViewById(R.id.tv_list_logbookreg_glycemia_value);
                    gtag = (TextView) view.findViewById(R.id.tv_list_logbookreg_glycemia);
                    cvalue = (TextView) view.findViewById(R.id.tv_list_logbookreg_carbs_value);
                    ctag = (TextView) view.findViewById(R.id.tv_list_logbookreg_carbs_title);
                    tag = (TextView) view.findViewById(R.id.tv_list_logbookreg_tag);
                }
            }
        }
    }

    public HomeAdapter(ArrayList<Advice> adviceList, ArrayList<Task> taskList,Context c) {
        this.homeList = new ArrayList<>();
        if(adviceList.size()>0){
            this.homeList.add(new HomeElement(HomeElement.Type.HEADER, "ADVICES"));
            this.homeList.addAll(adviceList);
        }
        if(taskList.size()>0){
            this.homeList.add(new HomeElement(HomeElement.Type.HEADER, "TASKS"));
            this.homeList.addAll(taskList);
        }
        this.c = c;
    }
    public HomeAdapter(ArrayList<Advice> adviceList, ArrayList<Task> taskList, Cursor cursor, Context c, YapDroid myYap) {

        this.cursor = cursor;
        this.c = c;
        this.homeList = new ArrayList<>();
        this.myYapInstance = myYap;

        if(adviceList.size()>0){
            this.homeList.add(new HomeElement(HomeElement.Type.HEADER, "ADVICES"));
            this.homeList.addAll(adviceList);
        }
        if(taskList.size()>0){
            this.homeList.add(new HomeElement(HomeElement.Type.HEADER, "TASKS"));
            this.homeList.addAll(taskList);
        }
        if(cursor.getCount()>0){
            this.homeList.add(new HomeElement(HomeElement.Type.HEADER, "RECENT REGISTRIES"));
            this.homeList.addAll(cursorToList(cursor));
        }
        this.homeList.add(new HomeElement(HomeElement.Type.SPACE,""));
        this.homeList.add(new HomeElement(HomeElement.Type.SPACE,""));
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View v = null;
        ViewHolder vh;

        switch (viewType) {
            case 0:
//                Log.i("________POSITION_____", "OTHER");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_advice_row, parent, false);
                vh = new ViewHolder((LinearLayout) v, false);
                return vh;
            case 1:
//                Log.i("________POSITION_____", "LOGBOOK!");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_logbook_row, parent, false);
                vh = new ViewHolder((LinearLayout) v, true);
                v.setTag(vh);
                return vh;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(homeList.get(position).getDisplayType().equals(HomeElement.Type.LOGITEM)){return 1;}
        return 0;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HomeElement currentView = homeList.get(position);

        if(currentView.getDisplayType().equals(HomeElement.Type.HEADER)|| currentView.getDisplayType().equals(HomeElement.Type.SPACE)){
            if(currentView.getDisplayType().equals(HomeElement.Type.HEADER)){
                //its an header
                LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
                textHolder.setBackgroundColor(Color.parseColor("#abbbcb"));
                TextView rowText = (TextView) textHolder.getChildAt(0);
                rowText.setTextColor(ContextCompat.getColor(c, R.color.cardview_light_background));
                //textHolder.setBackgroundColor(Color.parseColor("#33333333"));
                rowText.setTypeface(Typeface.MONOSPACE);
                rowText.setText(currentView.getName());
            }
        }else{
            if(currentView.getDisplayType().equals(HomeElement.Type.LOGITEM)){
                //its a logbookItem
                View v = holder.view;
                HomeElement logbook_datab = homeList.get(position);
                holder.item = logbook_datab;
                holder.data.setText(logbook_datab.getFormattedDate());
                holder.hora.setText(logbook_datab.getFormattedTime());
                holder.tag.setText(logbook_datab.getTag());
                if (logbook_datab.getInsulinId() != -1) {
                    holder.insulinValue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", logbook_datab.getInsulinVal()));
                    holder.insulinName.setText(logbook_datab.getInsulinName());
                    holder.insulinValue.setVisibility(View.VISIBLE);
                    holder.insulinName.setVisibility(View.VISIBLE);
                } else {
                    holder.insulinValue.setVisibility(View.INVISIBLE);
                    holder.insulinName.setVisibility(View.INVISIBLE);
                }
                if (logbook_datab.getGlycemiaId() != -1) {
                    holder.gvalue.setText(String.valueOf(logbook_datab.getGlycemia()));
                    holder.gvalue.setVisibility(View.VISIBLE);
                    holder.gtag.setVisibility(View.VISIBLE);
                } else {
                    holder.gvalue.setVisibility(View.INVISIBLE);
                    holder.gtag.setVisibility(View.INVISIBLE);
                }
                if (logbook_datab.getCarbsId() != -1) {
                    holder.cvalue.setText(String.valueOf(logbook_datab.getCarbs()));
                    holder.cvalue.setVisibility(View.VISIBLE);
                    holder.ctag.setVisibility(View.VISIBLE);
                } else {
                    holder.cvalue.setVisibility(View.INVISIBLE);
                    holder.ctag.setVisibility(View.INVISIBLE);
                }
                v.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), DetailLogbookActivity.class);
                        Bundle args = new Bundle();
                        HomeElement logbookDataBinding = ((ViewHolder) v.getTag()).item;
                        if (logbookDataBinding.getGlycemiaId() != -1) {
                            GlycemiaRec glycemiaRec = new GlycemiaRec();
                            glycemiaRec.setId(logbookDataBinding.getGlycemiaId());
                            args.putString("bg", String.valueOf(glycemiaRec.getId())); //bg id
                            args.putParcelable(DetailLogbookActivity.ARG_BLOOD_GLUCOSE, glycemiaRec);
                        }
                        if (logbookDataBinding.getCarbsId() != -1) {
                            CarbsRec carbs = new CarbsRec();
                            carbs.setId(logbookDataBinding.getCarbsId());
                            args.putString("ch", String.valueOf(carbs.getId())); //ch id
                            args.putParcelable(DetailLogbookActivity.ARG_CARBS, carbs);
                        }
                        if (logbookDataBinding.getInsulinId() != -1) {
                            InsulinRec insulin = new InsulinRec();
                            insulin.setId(logbookDataBinding.getInsulinId());
                            args.putString("ins", String.valueOf(insulin.getId())); //ins id
                            args.putParcelable(DetailLogbookActivity.ARG_INSULIN, insulin);
                        }
                        intent.putExtras(args);
                        v.getContext().startActivity(intent);
                    }
                });

            }else{
                if(currentView.getDisplayType().equals(HomeElement.Type.ADVICE)){
                    //its an advice
                    final Advice currentAdvice = (Advice) currentView;

                    LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
                    textHolder.setBackgroundColor(Color.parseColor("#abbbcb"));
                    TextView myText = (TextView) textHolder.getChildAt(0);

                if(currentAdvice.getUrgency()>7){
                    myText.setTextColor(Color.RED);
                            //holder.view.setBackgroundColor(Color.RED);
//                }else{
//                    if(currentAdvice.getUrgency()>5){
//                        myText.setTextColor(Color.YELLOW);
//                       // holder.view.setBackgroundColor(Color.YELLOW);
//                    }else{
//                        myText.setTextColor(Color.GREEN);
//                        //holder.view.setBackgroundColor(Color.GREEN);
//                    }
                }

                    //LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
                    //textHolder.setBackgroundColor(Color.WHITE);
                    textHolder.setBackgroundColor(Color.parseColor("#cceeeeee"));
                    TextView rowText = (TextView) textHolder.getChildAt(0);
                    rowText.setText(currentAdvice.getSummaryText());

                    //holder.view.setText(adviceList.get(position).getNotificationText());
                    holder.view.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            // Get the layout inflater
                            LayoutInflater inflater = LayoutInflater.from(v.getContext());
                            // Inflate and set the layout for the dialog
                            // Pass null as the parent view because its going in the dialog layout
                            builder.setView(inflater.inflate(R.layout.dialog_new_advice, null));

                            if(!currentAdvice.getAdviceType().equals("ALERT") || !currentAdvice.getAdviceType().equals("NORMAL")){
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(currentAdvice.getAdviceType().equals("QUESTION")){
                                            myYapInstance.insertRule("has("+currentAdvice.getRegistryType()+")");
                                        }
                                        if(currentAdvice.getAdviceType().equals("SUGGESTION")){
                                            Class<?> wantedAct = null;
                                            String classPath = "";
                                            try {
                                                 classPath = "pt.it.porto.mydiabetes.ui.activities."+currentAdvice.getRegistryType();
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


                }else{
                    //its a task
                    final Task currentTask = (Task) currentView;


                    LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
                    textHolder.setBackgroundColor(Color.parseColor("#abbbcb"));
                    TextView myText = (TextView) textHolder.getChildAt(0);

                    if(currentTask.getUrgency()>7){
                        myText.setTextColor(Color.RED);}

                /*if(currentTask.getUrgency()>7){
                    holder.view.setBackgroundColor(Color.RED);
                }else{
                    if(currentTask.getUrgency()>5){
                        holder.view.setBackgroundColor(Color.YELLOW);
                    }else{
                        holder.view.setBackgroundColor(Color.GREEN);
                    }
                }*/

                    //LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
                    //textHolder.setBackgroundColor(Color.WHITE);
                    textHolder.setBackgroundColor(Color.parseColor("#cceeeeee"));
                    TextView rowText = (TextView) textHolder.getChildAt(0);
                    rowText.setText(". "+currentTask.getSummaryText());

                    //holder.view.setText(adviceList.get(position).getNotificationText());
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(c);
                            builder.setMessage(currentTask.getExpandedText());
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }
        }
    }

    public void remove(int position) {
        notifyItemRemoved(position);
        homeList.remove(position);
    }


    @Override
    public int getItemCount() {
        return homeList.size();
    }

    public HomeElement getItem(int position) {
        cursor.moveToPosition(position);
        int pox = 0;
        return new HomeElement(
                cursor.getString(pox++),
                cursor.getString(pox++),
                cursor.getInt(pox++),
                cursor.getFloat(pox++),
                cursor.getString(pox++),
                cursor.getInt(pox++),
                cursor.getInt(pox++),
                cursor.getInt(pox++),
                cursor.getInt(pox));
    }
    public ArrayList cursorToList(Cursor cursor){
        ArrayList<HomeElement> cursorList = new ArrayList<>();
        for(int index=0;index<cursor.getCount();index++){
            cursorList.add(getItem(index));
        }
        return cursorList;
    }

}
