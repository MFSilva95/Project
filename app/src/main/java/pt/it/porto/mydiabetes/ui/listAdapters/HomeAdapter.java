package pt.it.porto.mydiabetes.ui.listAdapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import pt.it.porto.mydiabetes.R;

import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;

//import pt.it.porto.mydiabetes.ui.activities.DetailLogbookActivity;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.DetailLogbookActivity;
import pt.it.porto.mydiabetes.ui.activities.Home;
import pt.it.porto.mydiabetes.ui.activities.NewHomeRegistry;
import pt.it.porto.mydiabetes.ui.fragments.home.homeMiddleFragment;
import pt.it.porto.mydiabetes.utils.HomeElement;
import pt.it.porto.mydiabetes.utils.LocaleUtils;
import pt.it.porto.mydiabetes.widget;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    Context c;
    private Cursor cursor;
    private List<HomeElement> homeList;
    private int nAdvices;
    private int nTasks;
    private static int indexSelected;
    private homeMiddleFragment.MiddleFragRegCallBackImpl callBack;



    public HomeElement getFromHomeList(int index) {
        return homeList.get(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView data;
        TextView hora;
        TextView insulinValue;
        TextView insulinName;
        TextView gvalue;
        TextView cvalue;
        TextView ctag;
        TextView tag;
        TextView gtag;
        RelativeLayout background;
        HomeElement item;

        public LinearLayout view;

        public ViewHolder(LinearLayout v, Boolean isLog) {
            super(v);
            view = v;
            if (isLog) {
                //data = (TextView) view.findViewById(R.id.tv_list_logbookreg_data);
                hora = (TextView) view.findViewById(R.id.tv_list_logbookreg_hora);
                insulinValue = (TextView) view.findViewById(R.id.tv_list_logbookreg_insulin_value);
                insulinName = (TextView) view.findViewById(R.id.tv_list_logbookreg_insulin);
                gvalue = (TextView) view.findViewById(R.id.tv_list_logbookreg_glycemia_value);
                gtag = (TextView) view.findViewById(R.id.tv_list_logbookreg_glycemia);
                cvalue = (TextView) view.findViewById(R.id.tv_list_logbookreg_carbs_value);
                ctag = (TextView) view.findViewById(R.id.tv_list_logbookreg_carbs_title);
                tag = (TextView) view.findViewById(R.id.tv_list_logbookreg_tag);
                background = (RelativeLayout) view.findViewById(R.id.logbookRecords);


                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        indexSelected = getLayoutPosition();
                        view.setSelected(true);
                        return true;
                    }
                });
            }
        }
    }


    public HomeAdapter(List<HomeElement> homeList,homeMiddleFragment.MiddleFragRegCallBackImpl callBack) {
        this.homeList = new LinkedList<>();
        this.homeList.addAll(homeList);
        this.callBack = callBack;
    }

    public void updateList(List<HomeElement> homeList) {
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
                // Log.i("________POSITION_____", "HEADER");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_home_row, parent, false);
                vh = new ViewHolder((LinearLayout) v, true);
                v.setTag(vh);
                return vh;
            case 1:
//                Log.i("________POSITION_____", "LOGBOOK!");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_logbook_row, parent, false);
                vh = new ViewHolder((LinearLayout) v, true);
                v.setTag(vh);
                return vh;
            case 2:
//                Log.i("________POSITION_____", ADVICE");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_advice_row, parent, false);
                vh = new ViewHolder((LinearLayout) v, false);
                return vh;
            case 3:
                // Log.i("________POSITION_____", "TASK");
//                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_task_row, parent, false);
                vh = new ViewHolder((LinearLayout) v, true);
                v.setTag(vh);
                return vh;
            case 4:
                //Log.i("________POSITION_____", "EMPTY");
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_home_row, parent, false);
                vh = new ViewHolder((LinearLayout) v, true);
                v.setTag(vh);
                return vh;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (homeList.get(position).getDisplayType()) {
            case HEADER:
                return 0;
            case LOGITEM:
                return 1;
            case ADVICE:
                return 2;
            case TASK:
                return 3;
            case SPACE:
                return 4;
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HomeElement currentView = homeList.get(position);
        LinearLayout textHolder = null;
        TextView rowText = null;
        TextView myText = null;
        View v = holder.view;
        switch (currentView.getDisplayType()) {

            case HEADER:
                textHolder = (LinearLayout) v.findViewById(R.id.headerRowBackground);
                rowText = (TextView) textHolder.getChildAt(0);
                rowText.setText(currentView.getName());
                break;
            case ADVICE:

//                textHolder = (LinearLayout) v.findViewById(R.id.adviceRowBackground);
//                myText = (TextView) v.findViewById(R.id.content);
//                textHolder.setBackgroundColor(Color.parseColor("#cceeeeee"));
//                holder.view.setOnClickListener(getAdviceClickListener(currentAdvice));
                break;
            case LOGITEM:
                v = holder.view;
                setLogItemAppearence(holder, position);
                v.setOnClickListener(getLogItemClickListener());
                v.setOnLongClickListener(getLogItemLongClickListener());
                break;
            case SPACE:
                break;
        }
    }

    private View.OnLongClickListener getLogItemLongClickListener() {

        View.OnLongClickListener onLongclick = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                HomeElement logbookDataBinding = ((ViewHolder) v.getTag()).item;
                if(logbookDataBinding.isPressed()){
                    logbookDataBinding.setPressed(false);
                    callBack.removeToDelete(logbookDataBinding);

                }else{
                    logbookDataBinding.setPressed(true);
                    callBack.addToDelete(logbookDataBinding);
                }
                notifyDataSetChanged();

                return true;
            }
        };
        return onLongclick;

    }
    private View.OnClickListener getLogItemClickListener(){
        View.OnClickListener onclick;
        onclick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HomeElement logbookDataBinding = ((ViewHolder) v.getTag()).item;
                if(callBack.isInDeleteMode()){
                    if(logbookDataBinding.isPressed()){
                        logbookDataBinding.setPressed(false);
                        callBack.removeToDelete(logbookDataBinding);

                    }else{
                        logbookDataBinding.setPressed(true);
                        callBack.addToDelete(logbookDataBinding);
                    }
                    notifyDataSetChanged();

                }else{
                    Intent intent = new Intent(v.getContext(), NewHomeRegistry.class);
                    Bundle args = new Bundle();
                    if(logbookDataBinding.getRecordID() != -1){
                        args.putInt(DetailLogbookActivity.ARG_RECORD_ID, logbookDataBinding.getRecordID());
                    }
                    if(logbookDataBinding.getTag_id()!=-1){
                        DB_Read read = new DB_Read(v.getContext());
                        args.putString("tag",read.Tag_GetNameById(logbookDataBinding.getTag_id()));
                        read.close();
                    }
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
                    if (logbookDataBinding.getNote_id() != -1) {
                        args.putInt("note_id", logbookDataBinding.getNote_id()); //ins id
                    }
                    intent.putExtras(args);
                    callBack.updateHomeList(intent);
                }
            }
        };
        return onclick;

    }
    private void setLogItemAppearence(ViewHolder holder, int position){

        HomeElement currentView = homeList.get(position);

        holder.item = currentView;
        holder.hora.setText(currentView.getFormattedTime());
        if(currentView.getTag_id()!=-1){

            DB_Read rdb = new DB_Read(holder.background.getContext());
            ArrayList<Tag> all_tags = rdb.Tag_GetAll();
            rdb.close();
            //Resources res = holder.view.getContext().getResources(); //assuming in an activity for example, otherwise you can provide a context.
            //String[] dayTimes = res.getStringArray(R.array.daytimes);
            holder.tag.setText(all_tags.get(currentView.getTag_id()-1).getName());
            //dayTimes[currentView.getTag_id()-1]);
        }
        LinearLayout imageTitleHolder = (LinearLayout) holder.view.findViewById(R.id.imageTitleHolder);

        holder.background.setBackgroundColor(!currentView.isPressed()?Color.TRANSPARENT:Color.LTGRAY);

        if(homeList.get(position-1).getDisplayType() == HomeElement.Type.HEADER && (position - 1) > -1){
            imageTitleHolder.setVisibility(View.VISIBLE);
        }
        else{
            imageTitleHolder.setVisibility(View.INVISIBLE);
        }
        if (currentView.getInsulinId() != -1) {
            holder.insulinValue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", currentView.getInsulinVal()));
            holder.insulinName.setText(currentView.getInsulinName());
            holder.insulinValue.setVisibility(View.VISIBLE);
            holder.insulinName.setVisibility(View.VISIBLE);
        } else {
            holder.insulinValue.setVisibility(View.INVISIBLE);
            holder.insulinName.setVisibility(View.INVISIBLE);
        }
        if (currentView.getGlycemiaId() != -1) {
            holder.gvalue.setText(String.valueOf(currentView.getGlycemia()));
            holder.gvalue.setVisibility(View.VISIBLE);
            holder.gtag.setVisibility(View.VISIBLE);

        } else {
            holder.gvalue.setVisibility(View.INVISIBLE);
            holder.gtag.setVisibility(View.INVISIBLE);
        }
        if (currentView.getCarbsId() != -1) {
            holder.cvalue.setText(String.valueOf(currentView.getCarbs()));
            holder.cvalue.setVisibility(View.VISIBLE);
            holder.ctag.setVisibility(View.VISIBLE);
        } else {
            holder.cvalue.setVisibility(View.INVISIBLE);
            holder.ctag.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return homeList.size();
    }
}