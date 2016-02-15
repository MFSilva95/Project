package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.CarboHydrateDetail;
import pt.it.porto.mydiabetes.ui.dataBinding.CarbsDataBinding;


public class CarbsAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<CarbsDataBinding> _data;

	public CarbsAdapter(ArrayList<CarbsDataBinding> data, Context c) {
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
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_carbs_row, parent, false);
		}

		TextView carbvalue = (TextView) v.findViewById(R.id.tv_list_carbs_value);
		TextView date = (TextView) v.findViewById(R.id.tv_list_carbs_data);
		TextView hour = (TextView) v.findViewById(R.id.tv_list_carbs_hora);
		TextView tag = (TextView) v.findViewById(R.id.tv_list_carbs_tag);

		CarbsDataBinding carb = _data.get(position);
		String _id = "" + carb.getId();
		carbvalue.setText(String.valueOf(carb.getCarbsValue()));
		carbvalue.setTag(_id);

		date.setText(carb.getFormattedDate());
		hour.setText(carb.getFormattedTime());

		DB_Read rdb = new DB_Read(_c);
		tag.setText(rdb.Tag_GetById(carb.getId_Tag()).getName());
		rdb.close();
		v.setTag(_id);


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), CarboHydrateDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", (String) v.getTag()); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}

		});


		return v;
	}

}
