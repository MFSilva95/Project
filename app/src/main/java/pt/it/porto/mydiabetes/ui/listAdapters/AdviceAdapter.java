package pt.it.porto.mydiabetes.ui.listAdapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;


import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Advice;


/**
 * Created by Diogo on 11/05/2016.
 */
public class AdviceAdapter extends RecyclerView.Adapter<AdviceAdapter.ViewHolder>{

    Context c;
    private ArrayList<Advice> adviceList;
    private int numberElements = 0;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView view;
        public ViewHolder(TextView v) {
            super(v);
            view = v;
        }
    }

    public AdviceAdapter(ArrayList<Advice> adviceList, Context c) {
        this.adviceList = adviceList;
        this.c = c;
        this.numberElements = adviceList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_advice_row, parent, false);
        ViewHolder vh = new ViewHolder((TextView) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.view.setText(adviceList.get(position).getNotificationText());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setMessage(adviceList.get(position).getExpandedText());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void remove(int position) {
        adviceList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return adviceList.size();
    }
}
