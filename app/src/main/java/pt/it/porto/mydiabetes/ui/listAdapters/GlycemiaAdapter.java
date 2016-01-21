package pt.it.porto.mydiabetes.ui.listAdapters;

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

import pt.it.porto.mydiabetes.ui.activities.GlycemiaDetail;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;


public class GlycemiaAdapter extends BaseAdapter {

	private ArrayList<GlycemiaDataBinding> _data;
    Context _c;
    
    public GlycemiaAdapter (ArrayList<GlycemiaDataBinding> data, Context c){
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
            v = vi.inflate(R.layout.list_glycemia_row, null);
         }
 
           TextView data = (TextView)v.findViewById(R.id.tv_list_glicemia_data);
           TextView hora = (TextView)v.findViewById(R.id.tv_list_glicemia_hora);
           TextView value = (TextView)v.findViewById(R.id.tv_list_glicemia_value);
		   TextView tag = (TextView)v.findViewById(R.id.tv_list_glicemia_tag);
           final ImageButton viewdetail = (ImageButton)v.findViewById(R.id.ib_list_glicemia_detail);
           
           final GlycemiaDataBinding glycemia = _data.get(position);
           final String _id = ""+glycemia.getId();
           data.setText(glycemia.getDate());
           hora.setText(glycemia.getTime());
           value.setTag(_id);
           value.setText(String.valueOf(glycemia.getValue()));

		   DB_Read rdb = new DB_Read(_c);
		   tag.setText(rdb.Tag_GetById(glycemia.getIdTag()).getName());
		   rdb.close();

		   viewdetail.setTag(_id);



           
           
           viewdetail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), GlycemiaDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", _id); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
        	   
           });
                                     
                        
        return v;
	}
	
}
