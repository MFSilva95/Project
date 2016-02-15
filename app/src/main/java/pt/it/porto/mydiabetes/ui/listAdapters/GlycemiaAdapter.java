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
import pt.it.porto.mydiabetes.ui.activities.GlycemiaDetail;
import pt.it.porto.mydiabetes.ui.dataBinding.GlycemiaDataBinding;


public class GlycemiaAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<GlycemiaDataBinding> _data;

	public GlycemiaAdapter(ArrayList<GlycemiaDataBinding> data, Context c) {
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
			v = vi.inflate(R.layout.list_glycemia_row, parent, false);
		}

		TextView data = (TextView) v.findViewById(R.id.tv_list_glicemia_data);
		TextView hora = (TextView) v.findViewById(R.id.tv_list_glicemia_hora);
		TextView value = (TextView) v.findViewById(R.id.tv_list_glicemia_value);
		TextView tag = (TextView) v.findViewById(R.id.tv_list_glicemia_tag);

		GlycemiaDataBinding glycemia = _data.get(position);
		String _id = "" + glycemia.getId();
		data.setText(glycemia.getFormattedDate());
		hora.setText(glycemia.getFormattedTime());
		value.setTag(_id);
		value.setText(String.valueOf(glycemia.getValue()));

		DB_Read rdb = new DB_Read(_c);
		tag.setText(rdb.Tag_GetById(glycemia.getIdTag()).getName());
		rdb.close();

		v.setTag(_id);

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), GlycemiaDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", (String) v.getTag()); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}

		});


		return v;
	}

}
