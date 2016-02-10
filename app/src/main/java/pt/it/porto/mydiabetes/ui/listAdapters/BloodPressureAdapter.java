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
import pt.it.porto.mydiabetes.ui.activities.BloodPressureDetail;


public class BloodPressureAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<BloodPressureDataBinding> _data;

	public BloodPressureAdapter(ArrayList<BloodPressureDataBinding> data, Context c) {
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
			v = vi.inflate(R.layout.list_bloodpressure_row, parent, false);
		}

		TextView data = (TextView) v.findViewById(R.id.tv_list_bloodpressure_data);
		TextView hora = (TextView) v.findViewById(R.id.tv_list_bloodpressure_hora);
		TextView systolic = (TextView) v.findViewById(R.id.tv_list_bloodpressure_systolic_value);
		TextView diastolic = (TextView) v.findViewById(R.id.tv_list_bloodpressure_diastolic_value);
		TextView tag = (TextView) v.findViewById(R.id.tv_list_bloodpressure_tag);

		BloodPressureDataBinding bp = _data.get(position);
		String _id = "" + bp.getId();

		DB_Read rdb = new DB_Read(_c);

		data.setText(bp.getDate());
		hora.setText(bp.getTime());
		systolic.setText(String.valueOf(bp.getSystolic()));
		diastolic.setText(String.valueOf(bp.getDiastolic()));

		TagDataBinding t = rdb.Tag_GetById(bp.getIdTag());
		rdb.close();
		tag.setText(t.getName());
		v.setTag(_id);


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), BloodPressureDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", (String) v.getTag());

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		rdb.close();
		return v;
	}

}
