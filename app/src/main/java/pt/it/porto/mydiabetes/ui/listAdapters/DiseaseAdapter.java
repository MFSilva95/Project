package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import pt.it.porto.mydiabetes.ui.activities.DiseasesDetail;
import pt.it.porto.mydiabetes.R;

import java.util.ArrayList;


public class DiseaseAdapter extends BaseAdapter {

    private ArrayList<DiseaseDataBinding> _data;
    Context _c;

    public DiseaseAdapter(ArrayList<DiseaseDataBinding> data, Context c) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_disease_row, null);
        }

        LinearLayout lLayout = (LinearLayout)v.findViewById(R.id.DiseasesRow);

        TextView tagName = (TextView) v.findViewById(R.id.list_diseaseName);

        final DiseaseDataBinding disease = _data.get(position);
        String _id = "" + disease.getId();
        tagName.setTag(_id);
        tagName.setText(disease.getName());


        lLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(v.getContext(), DiseasesDetail.class);
                Bundle args = new Bundle();
                args.putString("Id", String.valueOf(disease.getId()));
                args.putString("Name", String.valueOf(disease.getName()));

                intent.putExtras(args);
                v.getContext().startActivity(intent);
            }
        });

        return v;
    }

}