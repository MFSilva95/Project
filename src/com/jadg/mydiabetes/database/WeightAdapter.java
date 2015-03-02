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

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.WeightDetail;


public class WeightAdapter extends BaseAdapter {

	private ArrayList<WeightDataBinding> _data;
    Context _c;
    
    public WeightAdapter (ArrayList<WeightDataBinding> data, Context c){
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
           v = vi.inflate(R.layout.list_weight_row, null);
        }
 
        TextView data = (TextView)v.findViewById(R.id.tv_list_weight_data);
        TextView hora = (TextView)v.findViewById(R.id.tv_list_weight_hora);
        TextView value = (TextView)v.findViewById(R.id.tv_list_weight_value);
	   
        final ImageButton viewdetail = (ImageButton)v.findViewById(R.id.ib_list_weight_detail);
	   
        final WeightDataBinding bp = _data.get(position);
        final String _id = "" + bp.getId();
        
        data.setText(bp.getDate());
        hora.setText(bp.getTime());
        value.setText(String.valueOf(bp.getValue()));
        
        viewdetail.setTag(_id);
	   
	   
	   
        viewdetail.setOnClickListener(new View.OnClickListener() {
	
		@Override
		public void onClick(final View v) {
			Intent intent = new Intent(v.getContext(), WeightDetail.class);
			Bundle args = new Bundle();
			args.putString("Id", _id);
			
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
        });
        
	    return v;
	}
	
}
