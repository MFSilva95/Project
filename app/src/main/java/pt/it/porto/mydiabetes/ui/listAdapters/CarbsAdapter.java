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

import pt.it.porto.mydiabetes.ui.activities.CarboHydrateDetail;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;


public class CarbsAdapter extends BaseAdapter {

	private ArrayList<CarbsDataBinding> _data;
    Context _c;
    
    public CarbsAdapter (ArrayList<CarbsDataBinding> data, Context c){
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
            v = vi.inflate(R.layout.list_carbs_row, null);
         }
 
         TextView carbvalue = (TextView)v.findViewById(R.id.tv_list_carbs_value);
         TextView date = (TextView)v.findViewById(R.id.tv_list_carbs_data);
         TextView hour = (TextView)v.findViewById(R.id.tv_list_carbs_hora);
         TextView tag = (TextView)v.findViewById(R.id.tv_list_carbs_tag);
         
         final ImageButton viewdetail = (ImageButton)v.findViewById(R.id.ib_list_carbs_detail);
		   
         final CarbsDataBinding carb = _data.get(position);
         final String _id = ""+carb.getId();
         carbvalue.setText(String.valueOf(carb.getCarbsValue()));
         carbvalue.setTag(_id);
         
         date.setText(carb.getDate());
         hour.setText(carb.getTime());
         
         DB_Read rdb = new DB_Read(_c);
         tag.setText(rdb.Tag_GetById(carb.getId_Tag()).getName());
         rdb.close();
         viewdetail.setTag(_id);
         
         
         viewdetail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), CarboHydrateDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", _id); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
      	   
         });
                                     
                        
        return v;
	}
	
}
