package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.DiseasesDetail;


public class DiseaseAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<DiseaseDataBinding> _data;

	public DiseaseAdapter(ArrayList<DiseaseDataBinding> data, Context c) {
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
			v = vi.inflate(R.layout.list_disease_row, parent, false);
		}

		LinearLayout lLayout = (LinearLayout) v.findViewById(R.id.DiseasesRow);

		TextView tagName = (TextView) v.findViewById(R.id.list_diseaseName);

		DiseaseDataBinding disease = _data.get(position);
		lLayout.setTag(disease);
		tagName.setText(disease.getName());


		lLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), DiseasesDetail.class);
				Bundle args = new Bundle();
				args.putParcelable(DiseasesDetail.BUNDLE_DATA, (Parcelable) v.getTag());

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		return v;
	}

}
