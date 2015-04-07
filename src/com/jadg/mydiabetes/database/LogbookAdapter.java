package com.jadg.mydiabetes.database;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class LogbookAdapter extends BaseAdapter{

	private ArrayList<LogbookDataBinding> _data;
    Context _c;

    public LogbookAdapter (ArrayList<LogbookDataBinding> data, Context c){
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
	
	/**
	 * FALTA ESTA PARTE!!!!!!!!!!!
	 */
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
