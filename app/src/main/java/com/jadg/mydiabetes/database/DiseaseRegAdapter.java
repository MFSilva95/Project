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

import com.jadg.mydiabetes.DiseaseDetail;
import com.jadg.mydiabetes.R;


public class DiseaseRegAdapter extends BaseAdapter {

	private ArrayList<DiseaseRegDataBinding> _data;
    Context _c;
    
    public DiseaseRegAdapter (ArrayList<DiseaseRegDataBinding> data, Context c){
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
           v = vi.inflate(R.layout.list_diseasereg_row, null);
        }
 
        TextView disease = (TextView)v.findViewById(R.id.tv_list_diseasereg_disease);
        TextView start = (TextView)v.findViewById(R.id.tv_list_diseasereg_startdate);
        TextView end = (TextView)v.findViewById(R.id.tv_list_diseasereg_enddate);
	   
        final ImageButton viewdetail = (ImageButton)v.findViewById(R.id.ib_list_diseasereg_detail);
	   
        final DiseaseRegDataBinding dis = _data.get(position);
        final String _id = "" + dis.getId();
        
        disease.setText(dis.getDisease());
        start.setText(dis.getStartDate());
        end.setText((dis.getEndDate()!=null) ? dis.getEndDate() : "");
        
        viewdetail.setTag(_id);
	   
	   
	   
        viewdetail.setOnClickListener(new View.OnClickListener() {
	
		@Override
		public void onClick(final View v) {
			Intent intent = new Intent(v.getContext(), DiseaseDetail.class);
			Bundle args = new Bundle();
			args.putString("Id", _id);
			
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
        });
        
	    return v;
	}
	
}
