package com.jadg.mydiabetes.database;

import java.util.ArrayList;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jadg.mydiabetes.R;


public class DiseaseAdapter extends BaseAdapter {

	private ArrayList<DiseaseDataBinding> _data;
    Context _c;
    
    public DiseaseAdapter (ArrayList<DiseaseDataBinding> data, Context c){
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
            v = vi.inflate(R.layout.list_disease_row, null);
         }
 
           TextView tagName = (TextView)v.findViewById(R.id.list_diseaseName);
           final ImageButton tagRemove = (ImageButton)v.findViewById(R.id.list_diseaseRemove);
           
           final DiseaseDataBinding disease = _data.get(position);
           String _id = ""+disease.getId();
           tagName.setTag(_id);
           tagName.setText(disease.getName());
           tagRemove.setTag(_id);
           tagRemove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(v.getContext())
	    	    .setTitle("Eliminar doença?")
	    	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	    	         public void onClick(DialogInterface dialog, int whichButton) {
	    	             //Falta verificar se não está associada a nenhuma entrada da DB
	    	        	 
	    	        	 DB_Write wdb = new DB_Write(v.getContext());
	    	             wdb.Disease_Remove(disease.getId());
	    	             wdb.close();
	    	             Log.d("to delete", String.valueOf(position));
	    	             _data.remove(position);
	    	             notifyDataSetChanged();
	    	         }
	    	    })
	    	    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	         public void onClick(DialogInterface dialog, int whichButton) {
	    	                // Do nothing.
	    	         }
	    	    }).show();
			}
        	   
           });
                                     
                        
        return v;
	}
	
}
