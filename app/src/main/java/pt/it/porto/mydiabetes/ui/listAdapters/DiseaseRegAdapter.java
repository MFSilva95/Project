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
import pt.it.porto.mydiabetes.ui.activities.DiseaseDetail;
import pt.it.porto.mydiabetes.data.DiseaseRec;


public class DiseaseRegAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<DiseaseRec> _data;

	public DiseaseRegAdapter(ArrayList<DiseaseRec> data, Context c) {
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
			v = vi.inflate(R.layout.list_diseasereg_row, parent, false);
		}

		TextView disease = (TextView) v.findViewById(R.id.tv_list_diseasereg_disease);
		TextView start = (TextView) v.findViewById(R.id.tv_list_diseasereg_startdate);
		TextView end = (TextView) v.findViewById(R.id.tv_list_diseasereg_enddate);


		final DiseaseRec dis = _data.get(position);
		final String _id = "" + dis.getId();

		disease.setText(dis.getDisease());
		start.setText(dis.getStartDate());
		end.setText((dis.getEndDate() != null) ? dis.getEndDate() : "");

		v.setTag(_id);


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), DiseaseDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", (String) v.getTag());

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		return v;
	}

}
