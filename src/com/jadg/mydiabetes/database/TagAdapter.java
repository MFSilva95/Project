package com.jadg.mydiabetes.database;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jadg.mydiabetes.R;
import com.jadg.mydiabetes.TagDetail;


public class TagAdapter extends BaseAdapter {

	private ArrayList<TagDataBinding> _data;
    Context _c;
    
    public TagAdapter (ArrayList<TagDataBinding> data, Context c){
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
			v = vi.inflate(R.layout.list_tag_row, null);
		}


		RelativeLayout rLayout = (RelativeLayout)v.findViewById(R.id.FaseDiaRow);



		TextView tagName = (TextView)v.findViewById(R.id.list_tagName);
		TextView tagStart = (TextView)v.findViewById(R.id.list_tagStart);
		TextView tagEnd = (TextView)v.findViewById(R.id.list_tagEnd);
		

		final TagDataBinding tag = _data.get(position);
		String _id = ""+tag.getId();
		tagName.setTag(_id);
		tagName.setText(tag.getName());
		tagStart.setText(tag.getStart());
		tagEnd.setText(tag.getEnd());
		
		Log.d("id tag", String.valueOf(tag.getId()));



		rLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), TagDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(tag.getId()));

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});



		

		return v;
	}
	
}
