package com.jadg.mydiabetes.database;

import java.util.ArrayList;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.TargetBG_detail;


public class TargetAdapter extends BaseAdapter {

	private ArrayList<TargetDataBinding> _data;
    Context _c;
    
    public TargetAdapter (ArrayList<TargetDataBinding> data, Context c){
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
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_target_row, null);
		}


		RelativeLayout rLayout = (RelativeLayout)v.findViewById(R.id.TargetBGRow);



		TextView targetName = (TextView)v.findViewById(R.id.list_targetName);
		TextView targetStart = (TextView)v.findViewById(R.id.list_targetStart);
		TextView targetEnd = (TextView)v.findViewById(R.id.list_targetEnd);
		TextView targetvalue = (TextView)v.findViewById(R.id.list_targetValue);


		final TargetDataBinding target = _data.get(position);
		String _id = ""+target.getId();
		targetName.setTag(_id);
		targetName.setText(target.getName());
		targetStart.setText(target.getStart());
		targetEnd.setText(target.getEnd());
		targetvalue.setText(String.valueOf(target.getTarget()));




		rLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), TargetBG_detail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(target.getId()));

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});


		return v;
	}
	
}
