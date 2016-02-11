package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.InsulinDetail;
import pt.it.porto.mydiabetes.utils.LocaleUtils;


public class InsulinRegAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<InsulinRegDataBinding> _data;

	public InsulinRegAdapter(ArrayList<InsulinRegDataBinding> data, Context c) {
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
			v = vi.inflate(R.layout.list_insulinreg_row, parent, false);
		}

		TextView data = (TextView) v.findViewById(R.id.tv_list_insulinreg_data);
		TextView hora = (TextView) v.findViewById(R.id.tv_list_insulinreg_hora);
		TextView ivalue = (TextView) v.findViewById(R.id.tv_list_insulinreg_insulin_value);
		TextView gtag = (TextView) v.findViewById(R.id.tv_list_insulinreg_glycemia);
		TextView gvalue = (TextView) v.findViewById(R.id.tv_list_insulinreg_glycemia_value);
		TextView gunit = (TextView) v.findViewById(R.id.tv_list_insulinreg_glycemia_unit);
		TextView tag = (TextView) v.findViewById(R.id.tv_list_insulinreg_tag);
		TextView insulin = (TextView) v.findViewById(R.id.tv_list_insulinreg_insulin);

		InsulinRegDataBinding insulin_datab = _data.get(position);
		String _id = "" + insulin_datab.getId();
		Log.d("id da insulina", _id);
		data.setText(insulin_datab.getDate());
		hora.setText(insulin_datab.getTime());
		ivalue.setText(String.format(LocaleUtils.ENGLISH_LOCALE,"%.1f",insulin_datab.getInsulinUnits()));
		DB_Read rdb = new DB_Read(_c);
		if (insulin_datab.getIdBloodGlucose() != -1) {
			Log.d("if glycemia", "entrou");
			GlycemiaDataBinding glycemia = rdb.Glycemia_GetById(insulin_datab.getIdBloodGlucose());
			gvalue.setText(String.valueOf(glycemia.getValue()));
			gtag.setVisibility(View.VISIBLE);
			gvalue.setVisibility(View.VISIBLE);
			gunit.setVisibility(View.VISIBLE);
		} else {
			gvalue.setText("");
			gtag.setVisibility(View.GONE);
			gvalue.setVisibility(View.GONE);
			gunit.setVisibility(View.GONE);
		}
		InsulinDataBinding ins = rdb.Insulin_GetById(insulin_datab.getIdInsulin());
		insulin.setText(ins.getName());
		TagDataBinding t = rdb.Tag_GetById(insulin_datab.getIdTag());

		tag.setText(t.getName());
		v.setTag(_id);


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), InsulinDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", (String) v.getTag()); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		rdb.close();
		return v;
	}

}
