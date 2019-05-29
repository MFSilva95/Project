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
import pt.it.porto.mydiabetes.data.Sensitivity;
import pt.it.porto.mydiabetes.data.Tag;
import pt.it.porto.mydiabetes.ui.activities.DayFaseDetail;
import pt.it.porto.mydiabetes.ui.activities.Sensitivity_detail;


public class SensitivityListAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<Sensitivity> _data;

	public SensitivityListAdapter(ArrayList<Sensitivity> data, Context c) {
		_data = data;
		_c = c;
	}


	@Override
	public int getCount() {
		if(_data!=null){return _data.size();}else{return 0;}
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
			v = vi.inflate(R.layout.list_insu_ratio_row, parent, false);
		}


		LinearLayout rLayout = (LinearLayout) v.findViewById(R.id.TargetSenseRow);

		TextView insuRatio = (TextView) v.findViewById(R.id.list_targetValue);
		TextView tagName = (TextView) v.findViewById(R.id.list_targetName);
		TextView tagStart = (TextView) v.findViewById(R.id.list_targetStart);
		//TextView tagEnd = (TextView) v.findViewById(R.id.list_targetEnd);


		Sensitivity tag = _data.get(position);

		rLayout.setTag(tag);
		tagName.setText(tag.getName());
		tagStart.setText(tag.getStart());
		//tagEnd.setText(tag.getEnd());
		insuRatio.setText(tag.getSensitivity()+"");


		rLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), Sensitivity_detail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(((Sensitivity) v.getTag()).getId()));
				Log.i("cenas", "onClick: SENSI ID-> "+String.valueOf(((Sensitivity) v.getTag()).getId()));
				args.putParcelable(DayFaseDetail.DATA, ((Sensitivity) v.getTag()));

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});


		return v;
	}

}
