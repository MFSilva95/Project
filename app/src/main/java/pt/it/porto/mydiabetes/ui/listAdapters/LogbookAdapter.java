
package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import pt.it.porto.mydiabetes.ui.activities.DetailLogbookActivity;
import pt.it.porto.mydiabetes.ui.activities.LogbookDetail;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;

import java.util.ArrayList;

public class LogbookAdapter extends BaseAdapter {

    private ArrayList<LogbookDataBinding> _data;
    Context _c;

    public LogbookAdapter(ArrayList<LogbookDataBinding> data, Context c) {
        _data = data;
        _c = c;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return _data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return _data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_logbook_row, null);
        }

        TextView data = (TextView) v.findViewById(R.id.tv_list_logbookreg_data);
        TextView hora = (TextView) v.findViewById(R.id.tv_list_logbookreg_hora);
        TextView ivalue = (TextView) v.findViewById(R.id.tv_list_logbookreg_insulin_value);
        TextView itag = (TextView) v.findViewById(R.id.tv_list_logbookreg_insulin);
        TextView gvalue = (TextView) v.findViewById(R.id.tv_list_logbookreg_glycemia_value);
        TextView gtag = (TextView) v.findViewById(R.id.tv_list_logbookreg_glycemia);
        TextView cvalue = (TextView) v.findViewById(R.id.tv_list_logbookreg_carbs_value);
        TextView ctag = (TextView) v.findViewById(R.id.tv_list_logbookreg_carbs_title);
        TextView tag = (TextView) v.findViewById(R.id.tv_list_logbookreg_tag);
        TextView insulin = (TextView) v.findViewById(R.id.tv_list_logbookreg_insulin);

        final ImageButton viewdetail = (ImageButton) v.findViewById(R.id.ib_list_logbookreg_detail);

        final LogbookDataBinding logbook_datab = _data.get(position);
        final CarbsDataBinding ch = logbook_datab.get_ch();
        final InsulinRegDataBinding ins = logbook_datab.get_ins();
        final GlycemiaDataBinding bg = logbook_datab.get_bg();
        DB_Read rdb = new DB_Read(_c);


        if (ch != null && ins != null && bg != null) {//refeicao completa
            data.setText(ins.getDate());
            hora.setText(ins.getTime());
            ivalue.setText(String.valueOf(ins.getInsulinUnits()));
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            insulin.setText(insulina.getName());
            gvalue.setText(String.valueOf(bg.getValue()));
            cvalue.setText(String.valueOf(ch.getCarbsValue()));
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            tag.setText(t.getName());
            final String _id = ch.getId() + "_" + ins.getId() + "_" + bg.getId();
            viewdetail.setTag(_id);

            ivalue.setVisibility(View.VISIBLE);
            itag.setVisibility(View.VISIBLE);
            gvalue.setVisibility(View.VISIBLE);
            gtag.setVisibility(View.VISIBLE);
            cvalue.setVisibility(View.VISIBLE);
            ctag.setVisibility(View.VISIBLE);
        } else if (ch != null && ins == null && bg == null) {//so hidratos carbono
            data.setText(ch.getDate());
            hora.setText(ch.getTime());
            ivalue.setText("");
            gvalue.setText("");
            cvalue.setText(String.valueOf(ch.getCarbsValue()));
            TagDataBinding t = rdb.Tag_GetById(ch.getId_Tag());
            tag.setText(t.getName());
            final String _id = ch.getId() + "_" + 0 + "_" + 0;
            viewdetail.setTag(_id);

            ivalue.setVisibility(View.GONE);
            itag.setVisibility(View.GONE);
            gvalue.setVisibility(View.GONE);
            gtag.setVisibility(View.GONE);
            cvalue.setVisibility(View.VISIBLE);
            ctag.setVisibility(View.VISIBLE);
        } else if (ch == null && ins != null && bg != null) {//insulina com parametro da glicemia
            data.setText(ins.getDate());
            hora.setText(ins.getTime());
            ivalue.setText(String.valueOf(ins.getInsulinUnits()));
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            insulin.setText(insulina.getName());
            gvalue.setText(String.valueOf(bg.getValue()));
            cvalue.setText("");
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            tag.setText(t.getName());
            final String _id = 0 + "_" + ins.getId() + "_" + bg.getId();
            viewdetail.setTag(_id);

            ivalue.setVisibility(View.VISIBLE);
            itag.setVisibility(View.VISIBLE);
            gvalue.setVisibility(View.VISIBLE);
            gtag.setVisibility(View.VISIBLE);
            cvalue.setVisibility(View.GONE);
            ctag.setVisibility(View.GONE);
        } else if (ch == null && ins == null && bg != null) {//so glicemia
            data.setText(bg.getDate());
            hora.setText(bg.getTime());
            ivalue.setTag("");
            gvalue.setText(String.valueOf(bg.getValue()));
            cvalue.setText("");
            TagDataBinding t = rdb.Tag_GetById(bg.getIdTag());
            tag.setText(t.getName());
            final String _id = 0 + "_" + 0 + "_" + bg.getId();
            viewdetail.setTag(_id);

            ivalue.setVisibility(View.GONE);
            itag.setVisibility(View.GONE);
            gvalue.setVisibility(View.VISIBLE);
            gtag.setVisibility(View.VISIBLE);
            cvalue.setVisibility(View.GONE);
            ctag.setVisibility(View.GONE);

        } else if (ch == null && ins != null && bg == null) {//so insulina
            data.setText(ins.getDate());
            hora.setText(ins.getTime());
            ivalue.setText(String.valueOf(ins.getInsulinUnits()));
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            insulin.setText(insulina.getName());
            gvalue.setText("");
            cvalue.setText("");
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            tag.setText(t.getName());
            final String _id = 0 + "_" + ins.getId() + "_" + 0;
            viewdetail.setTag(_id);

            ivalue.setVisibility(View.VISIBLE);
            itag.setVisibility(View.VISIBLE);
            gvalue.setVisibility(View.GONE);
            gtag.setVisibility(View.GONE);
            cvalue.setVisibility(View.GONE);
            ctag.setVisibility(View.GONE);
        } else if (ch != null && ins != null && bg == null) {//hidratos e insulina
            data.setText(ins.getDate());
            hora.setText(ins.getTime());
            ivalue.setText(String.valueOf(ins.getInsulinUnits()));
            InsulinDataBinding insulina = rdb.Insulin_GetById(ins.getIdInsulin());
            insulin.setText(insulina.getName());
            gvalue.setText("");
            cvalue.setText(String.valueOf(ch.getCarbsValue()));
            TagDataBinding t = rdb.Tag_GetById(ins.getIdTag());
            tag.setText(t.getName());
            final String _id = ch.getId() + "_" + ins.getId() + "_" + 0;
            viewdetail.setTag(_id);

            ivalue.setVisibility(View.VISIBLE);
            itag.setVisibility(View.VISIBLE);
            gvalue.setVisibility(View.GONE);
            gtag.setVisibility(View.GONE);
            cvalue.setVisibility(View.VISIBLE);
            ctag.setVisibility(View.VISIBLE);
        }else if (ch != null && ins == null && bg != null) {//hidratos e glicemia
            data.setText(ch.getDate());
            hora.setText(ch.getTime());
            ivalue.setText("");
            insulin.setText("");
            gvalue.setText(String.valueOf(bg.getValue()));
            cvalue.setText(String.valueOf(ch.getCarbsValue()));
            TagDataBinding t = rdb.Tag_GetById(ch.getId_Tag());
            tag.setText(t.getName());
            final String _id = ch.getId() + "_" + 0 + "_" + bg.getId();
            viewdetail.setTag(_id);

            ivalue.setVisibility(View.GONE);
            itag.setVisibility(View.GONE);
            gvalue.setVisibility(View.VISIBLE);
            gtag.setVisibility(View.VISIBLE);
            cvalue.setVisibility(View.VISIBLE);
            ctag.setVisibility(View.VISIBLE);
        }


        final String id_ch;
        final String id_ins;
        final String id_bg;

        if (ch != null){
            id_ch = "" + ch.getId();
            //viewdetail.setTag(id_ch);
        } else{
            id_ch = "";
        }

        if (ins != null){
            id_ins = "" + ins.getId();
            //viewdetail.setTag(id_ins);
        } else{
            id_ins = "";
        }

        if (bg != null){
            id_bg = "" + bg.getId();
            //viewdetail.setTag(id_bg);
        } else{
            id_bg = "";
        }


        viewdetail.setOnClickListener(new View.OnClickListener()

          {
              @Override
              public void onClick(final View v) {
                  Intent intent = new Intent(v.getContext(), LogbookDetail.class);
                  Bundle args = new Bundle();
                  args.putString("ch", id_ch); //ch id
                  args.putString("ins", id_ins); //ins id
                  args.putString("bg", id_bg); //bg id
                  args.putParcelable(DetailLogbookActivity.ARG_INSULIN, ins);
                  args.putParcelable(DetailLogbookActivity.ARG_BLOOD_GLUCOSE, bg);
                  args.putParcelable(DetailLogbookActivity.ARG_CARBS, ch);
                  intent.putExtras(args);
                  v.getContext().startActivity(intent);
              }
          }

        );

        rdb.close();
        return v;
    }

}