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
import pt.it.porto.mydiabetes.data.Task;


/**
 * Created by Diogo on 11/05/2016.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>{

    Context c;
    private ArrayList<Task> taskList;
    private int numberElements = 0;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout view;
        public ViewHolder(LinearLayout v) {
            super(v);
            view = v;
        }
    }

    public TaskListAdapter(ArrayList<Task> taskList, Context c) {
        this.taskList = taskList;
        this.c = c;
        this.numberElements = taskList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_advice_row, parent, false);
        v.setBackgroundColor(Color.parseColor("#eeeeee"));
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Task identity = taskList.get(position);

//        if(identity.getUrgency()>7){
//            holder.view.setBackgroundColor(Color.RED);
//        }else{
//            if(identity.getUrgency()>5){
//                holder.view.setBackgroundColor(Color.YELLOW);
//            }else{
//                holder.view.setBackgroundColor(Color.GREEN);
//            }
//        }

        LinearLayout textHolder = (LinearLayout) holder.view.getChildAt(0);
        //textHolder.setBackgroundColor(Color.WHITE);
        TextView rowText = (TextView) textHolder.getChildAt(0);
        rowText.setText(identity.getSummaryText());

        //holder.view.setText(taskList.get(position).getNotificationText());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setMessage(taskList.get(holder.getAdapterPosition()).getExpandedText());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void remove(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
