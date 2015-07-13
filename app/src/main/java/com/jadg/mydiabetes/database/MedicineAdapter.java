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


public class MedicineAdapter extends BaseAdapter {

	private ArrayList<MedicineDataBinding> _data;
    Context _c;
    
    public MedicineAdapter (ArrayList<MedicineDataBinding> data, Context c){
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
            v = vi.inflate(R.layout.list_medicine_row, null);
         }
 
         TextView mname = (TextView)v.findViewById(R.id.list_medicineName);
         TextView munit = (TextView)v.findViewById(R.id.list_medicineUnit);
         
         final ImageButton removemedicine = (ImageButton)v.findViewById(R.id.list_medicineRemove);
		   
         final MedicineDataBinding medicine = _data.get(position);
         final String _id = ""+medicine.getId();
         mname.setText(medicine.getName());
         mname.setTag(_id);
         munit.setText(medicine.getUnits());
         removemedicine.setTag(_id);
		   
		   
         removemedicine.setOnClickListener(new View.OnClickListener() {
	
	    	 @Override
	    	 public void onClick(final View v) {
	    		 new AlertDialog.Builder(v.getContext())
		    	    .setTitle("Eliminar medicamento?")
		    	    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
		    	         public void onClick(DialogInterface dialog, int whichButton) {
		    	        	//Falta verificar se não está associada a nenhuma entrada da DB
		    	        	 
		    	        	 DB_Write wdb = new DB_Write(v.getContext());
		    	        	 try {
		    	        		 wdb.Medicine_Remove(medicine.getId());
		    	        		 Log.d("to delete", String.valueOf(position));
			    	             _data.remove(position);
			    	             notifyDataSetChanged();
		    	        	 }catch (Exception e) {
		    	        		 Toast.makeText(v.getContext(), "Não pode eliminar este medicamento, referenciado em registos!", Toast.LENGTH_LONG).show();
		    	     		 }
		    	             wdb.close();
		    	             
		    	         }
		    	    })
		    	    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
		    	         public void onClick(DialogInterface dialog, int whichButton) {
		    	                // Do nothing.
		    	         }
		    	    }).show();
	    	 }
		   
         });
                                     
                        
        return v;
	}
	
}