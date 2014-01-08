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

import com.jadg.mydiabetes.InsulinsDetail;
import com.jadg.mydiabetes.R;


public class InsulinAdapter extends BaseAdapter {

	private ArrayList<InsulinDataBinding> _data;
    Context _c;
    
    public InsulinAdapter (ArrayList<InsulinDataBinding> data, Context c){
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
            v = vi.inflate(R.layout.list_insulin_row, null);
         }
 
         
         RelativeLayout rLayout = (RelativeLayout)v.findViewById(R.id.InsulinsRow);
         
         
         TextView iname = (TextView)v.findViewById(R.id.list_insulinName);
         TextView itype = (TextView)v.findViewById(R.id.list_insulinType);
         TextView iaction = (TextView)v.findViewById(R.id.list_insulinAction);
         //TextView iduration = (TextView)v.findViewById(R.id.list_insulinDuration);
         
         
		   
         final InsulinDataBinding insulin = _data.get(position);
         final String _id = ""+insulin.getId();
         iname.setText(insulin.getName());
         iname.setTag(_id);
         itype.setText(insulin.getType());
         iaction.setText(insulin.getAction());
         //iduration.setText(insulin.getDuration().toString());
         
		   
         
         
         
         rLayout.setOnClickListener(new View.OnClickListener() {

  			@Override
  			public void onClick(final View v) {
  				Intent intent = new Intent(v.getContext(), InsulinsDetail.class);
  				Bundle args = new Bundle();
  				args.putString("Id", String.valueOf(insulin.getId()));

  				intent.putExtras(args);
  				v.getContext().startActivity(intent);
  			}
  		});
         
         
		 /*  
         removeinsulin.setOnClickListener(new View.OnClickListener() {
	
	    	 @Override
	    	 public void onClick(final View v) {
	    		 new AlertDialog.Builder(v.getContext())
		    	    .setTitle("Eliminar insulina?")
		    	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
		    	         public void onClick(DialogInterface dialog, int whichButton) {
		    	             //Falta verificar se não está associada a nenhuma entrada da DB
		    	        	 
		    	        	 DB_Write wdb = new DB_Write(v.getContext());
		    	        	 try {
		    	        		 wdb.Insulin_Remove(insulin.getId());
		    	        		 Log.d("to delete", String.valueOf(position));
			    	             _data.remove(position);
			    	             notifyDataSetChanged();
		    	        	 }catch (Exception e) {
		    	        		 Toast.makeText(v.getContext(), "Não pode eliminar esta insulina, referenciada em leituras!", Toast.LENGTH_LONG).show();
		    	     		 }
		    	             wdb.close();
		    	             
		    	         }
		    	    })
		    	    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    	         public void onClick(DialogInterface dialog, int whichButton) {
		    	                // Do nothing.
		    	         }
		    	    }).show();
	    	 }
		   
         });
                */                     
                        
        return v;
	}
	
}
