package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import pt.it.porto.mydiabetes.BuildConfig;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.Advice;
import pt.it.porto.mydiabetes.data.CarbsRec;
import pt.it.porto.mydiabetes.data.Day;
import pt.it.porto.mydiabetes.data.GlycemiaRec;
import pt.it.porto.mydiabetes.data.InsulinRec;
import pt.it.porto.mydiabetes.data.LogBookEntry;
import pt.it.porto.mydiabetes.data.Task;
import pt.it.porto.mydiabetes.ui.activities.DetailLogbookActivity;
import pt.it.porto.mydiabetes.utils.HomeElement;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

public class LogBookAdapter extends BaseAdapter {
    Context _c;
    private LinkedList<LogBookEntry> logBookEntries;

    public LogBookAdapter(LinkedList<LogBookEntry> logBookEntries, Context c) {
        this.logBookEntries = logBookEntries;
        _c = c;
    }

    @Override
    public int getCount() {
        return logBookEntries.size();
    }

    @Override
    public LogBookEntry getItem(int position) {
        return logBookEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView  == null) {
            convertView  = LayoutInflater.from(_c).inflate(R.layout.list_logbook_row, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();
        LogBookEntry current = getItem(position);
        viewHolder.item = current;
        viewHolder.hora.setText(current.getFormattedTime());
        viewHolder.tag.setText(current.getTag());
        if (current.getInsulinId() != -1) {
            viewHolder.insulinValue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", current.getInsulinVal()));
            viewHolder.insulinName.setText(current.getInsulinName());
            viewHolder.insulinValue.setVisibility(View.VISIBLE);
            viewHolder.insulinName.setVisibility(View.VISIBLE);
        } else {
            viewHolder.insulinValue.setVisibility(View.INVISIBLE);
            viewHolder.insulinName.setVisibility(View.INVISIBLE);
        }
        if (current.getGlycemiaId() != -1) {
            viewHolder.gvalue.setText(String.valueOf(current.getGlycemia()));
            viewHolder.gvalue.setVisibility(View.VISIBLE);
            viewHolder.gtag.setVisibility(View.VISIBLE);
        } else {
            viewHolder.gvalue.setVisibility(View.INVISIBLE);
            viewHolder.gtag.setVisibility(View.INVISIBLE);
        }
        if (current.getCarbsId() != -1) {
            viewHolder.cvalue.setText(String.valueOf(current.getCarbs()));
            viewHolder.cvalue.setVisibility(View.VISIBLE);
            viewHolder.ctag.setVisibility(View.VISIBLE);
        } else {
            viewHolder.cvalue.setVisibility(View.INVISIBLE);
            viewHolder.ctag.setVisibility(View.INVISIBLE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailLogbookActivity.class);
                Bundle args = new Bundle();
                LogBookEntry logbookDataBinding = ((ViewHolder) v.getTag()).item;
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

        return convertView;
    }


    public static class ViewHolder {
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
        LogBookEntry item;

        public ViewHolder(View view) {
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