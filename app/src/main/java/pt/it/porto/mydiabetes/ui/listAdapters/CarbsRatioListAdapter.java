package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.data.CarbsRatioData;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.ui.activities.DayFaseDetail;
import pt.it.porto.mydiabetes.ui.activities.Ratio_detail;
import pt.it.porto.mydiabetes.ui.activities.SettingsCarbsRatio;


public class CarbsRatioListAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<CarbsRatioData> _data;

	public CarbsRatioListAdapter(ArrayList<CarbsRatioData> data, Context c) {
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
			v = vi.inflate(R.layout.list_carb_ratio_row, parent, false);
		}


		LinearLayout rLayout = (LinearLayout) v.findViewById(R.id.TargetCarbsRatioRow);

		TextView ratioValue = (TextView) v.findViewById(R.id.list_targetValue);
		TextView tagName = (TextView) v.findViewById(R.id.list_targetName);
		TextView tagStart = (TextView) v.findViewById(R.id.list_targetStart);
		//TextView tagEnd = (TextView) v.findViewById(R.id.list_targetEnd);


		CarbsRatioData tag = _data.get(position);
		Log.i("cenas", "getView: PARCEL: "+tag.toString());
		rLayout.setTag(tag);
		tagName.setText(tag.getName());
		tagStart.setText(tag.getStart());
//		tagEnd.setText(tag.getEnd());
		ratioValue.setText(tag.getValue()+"");


		rLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), Ratio_detail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(((CarbsRatioData) v.getTag()).getId()));

				args.putParcelable(DayFaseDetail.DATA, ((CarbsRatioData) v.getTag()));

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});


		return v;
	}

}