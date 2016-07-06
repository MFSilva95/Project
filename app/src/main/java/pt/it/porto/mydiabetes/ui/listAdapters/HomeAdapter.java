package pt.it.porto.mydiabetes.ui.listAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.Task;


/**
 * Created by Diogo on 11/05/2016.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>{

    Context c;
    private ArrayList<Advice> adviceList;
    private ArrayList<Task> taskList;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout view;
        public ViewHolder(LinearLayout v) {
            super(v);
            view = v;
        }
    }

    public HomeAdapter(ArrayList<Advice> adviceList, ArrayList<Task> taskList, Context c) {
        this.adviceList = adviceList;
        this.taskList = taskList;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_advice_row, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(position==0){//advice title
            holder.view.setTag("separador");
            LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
            TextView rowText = (TextView) textHolder.getChildAt(0);
            rowText.setText("CONSELHOS");
        }
        if(position>0 && position < adviceList.size()+1){//advice

            holder.view.setTag("");
            final int myPos = position-1;

            Advice identity = adviceList.get(myPos);

            if(identity.getUrgency()>7){
                holder.view.setBackgroundColor(Color.RED);
            }else{
                if(identity.getUrgency()>5){
                    holder.view.setBackgroundColor(Color.YELLOW);
                }else{
                    holder.view.setBackgroundColor(Color.GREEN);
                }
            }

            LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
            textHolder.setBackgroundColor(Color.WHITE);
            TextView rowText = (TextView) textHolder.getChildAt(0);
            rowText.setText(identity.getSummaryText());

            //holder.view.setText(adviceList.get(position).getNotificationText());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setMessage(adviceList.get(myPos).getExpandedText());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


        }
        if(position == adviceList.size()+1){//task separator
            holder.view.setTag("separador");
            LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
            TextView rowText = (TextView) textHolder.getChildAt(0);
            rowText.setText("TAREFAS");
        }
        if(position > adviceList.size()+1){//task

            holder.view.setTag("");
            final int taskPosition = position - adviceList.size()-2;
            Task identity = taskList.get(taskPosition);

            if(identity.getUrgency()>7){
                holder.view.setBackgroundColor(Color.RED);
            }else{
                if(identity.getUrgency()>5){
                    holder.view.setBackgroundColor(Color.YELLOW);
                }else{
                    holder.view.setBackgroundColor(Color.GREEN);
                }
            }



            LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
            textHolder.setBackgroundColor(Color.WHITE);
            TextView rowText = (TextView) textHolder.getChildAt(0);
            rowText.setText(identity.getSummaryText());

            //holder.view.setText(adviceList.get(position).getNotificationText());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setMessage(adviceList.get(taskPosition).getExpandedText());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


        }
    }

    public void remove(int position) {
        if( position < adviceList.size()+taskList.size()){
            if(position<adviceList.size()){
                adviceList.remove(position);
            }else{
                taskList.remove(position-adviceList.size());
            }
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return adviceList.size()+taskList.size()+2;
    }
}
