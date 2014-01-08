package com.jadg.mydiabetes.database;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jadg.mydiabetes.BloodPressureDetail;
import com.jadg.mydiabetes.R;


public class BloodPressureAdapter extends BaseAdapter {

	private ArrayList<BloodPressureDataBinding> _data;
    Context _c;
    
    public BloodPressureAdapter (ArrayList<BloodPressureDataBinding> data, Context c){
        _data = data;
        _c = c;
    }
	
	@Override
	public int getCount() {
		return _data.size();
	}

	@Override
	public Object getItem(int position) {
		return _data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
        if (v == null)
        {
           LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           v = vi.inflate(R.layout.list_bloodpressure_row, null);
        }
 
        TextView data = (TextView)v.findViewById(R.id.tv_list_bloodpressure_data);
        TextView hora = (TextView)v.findViewById(R.id.tv_list_bloodpressure_hora);
        TextView systolic = (TextView)v.findViewById(R.id.tv_list_bloodpressure_systolic_value);
        TextView diastolic = (TextView)v.findViewById(R.id.tv_list_bloodpressure_diastolic_value);
        TextView tag = (TextView)v.findViewById(R.id.tv_list_bloodpressure_tag);
	   
        final ImageButton viewdetail = (ImageButton)v.findViewById(R.id.ib_list_bloodpressure_detail);
	   
        final BloodPressureDataBinding bp = _data.get(position);
        final String _id = "" + bp.getId();
        
        DB_Read rdb = new DB_Read(_c);
        
        data.setText(bp.getDate());
        hora.setText(bp.getTime());
        systolic.setText(String.valueOf(bp.getSystolic()));
        diastolic.setText(String.valueOf(bp.getDiastolic()));
        
        TagDataBinding t = rdb.Tag_GetById(bp.getIdTag());
        rdb.close();
        tag.setText(t.getName());
        viewdetail.setTag(_id);
	   
	   
	   
        viewdetail.setOnClickListener(new View.OnClickListener() {
	
		@Override
		public void onClick(final View v) {
			Intent intent = new Intent(v.getContext(), BloodPressureDetail.class);
			Bundle args = new Bundle();
			args.putString("Id", _id);
			
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
        });
        
        rdb.close();
	    return v;
	}
	
}
