package com.jadg.mydiabetes.database;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.widget.Toast;

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


public class ExerciseAdapter extends BaseAdapter {

	private ArrayList<ExerciseDataBinding> _data;
    Context _c;
    
    public ExerciseAdapter (ArrayList<ExerciseDataBinding> data, Context c){
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
            v = vi.inflate(R.layout.list_exercise_row, null);
         }
 
           TextView exerciseName = (TextView)v.findViewById(R.id.list_exerciseName);
           final ImageButton exerciseRemove = (ImageButton)v.findViewById(R.id.list_exerciseRemove);
           
           final ExerciseDataBinding exercise = _data.get(position);
           String _id = ""+exercise.getId();
           exerciseName.setTag(_id);
           exerciseName.setText(exercise.getName());
           exerciseRemove.setTag(_id);
           exerciseRemove.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(v.getContext())
	    	    .setTitle("Eliminar exercicio?")
	    	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
	    	         public void onClick(DialogInterface dialog, int whichButton) {
	    	             //Falta verificar se não está associada a nenhuma entrada da DB
	    	        	 
	    	        	 DB_Write wdb = new DB_Write(v.getContext());
	    	        	 try {
	    	        		 wdb.Exercise_Remove(exercise.getId());
	    	        		 Log.d("to delete", String.valueOf(position));
		    	             _data.remove(position);
		    	             notifyDataSetChanged();
	    	        	 }catch (Exception e) {
	    	        		 Toast.makeText(v.getContext(), "Não pode eliminar este exercicio, referenciado em leituras!", Toast.LENGTH_LONG).show();
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
                                     
                        
        return v;
	}
	
}
