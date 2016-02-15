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
import pt.it.porto.mydiabetes.ui.activities.WeightDetail;
import pt.it.porto.mydiabetes.ui.dataBinding.WeightDataBinding;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class WeightAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<WeightDataBinding> _data;

	public WeightAdapter(ArrayList<WeightDataBinding> data, Context c) {
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
			v = vi.inflate(R.layout.list_weight_row, parent, false);
		}

		TextView data = (TextView) v.findViewById(R.id.tv_list_weight_data);
		TextView hora = (TextView) v.findViewById(R.id.tv_list_weight_hora);
		TextView value = (TextView) v.findViewById(R.id.tv_list_weight_value);


		WeightDataBinding bp = (WeightDataBinding) getItem(position);
		String _id = "" + bp.getId();

		data.setText(bp.getFormattedDate());
		hora.setText(bp.getFormattedTime());
		value.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", bp.getValue()));

		v.setTag(_id);


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), WeightDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", (String) v.getTag());

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		return v;
	}

}
