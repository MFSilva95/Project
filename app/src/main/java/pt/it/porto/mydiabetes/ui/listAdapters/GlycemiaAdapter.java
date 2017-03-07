package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.TargetBG_detail;
import pt.it.porto.mydiabetes.data.InsulinTarget;


public class GlycemiaAdapter extends BaseAdapter {

	Context _c;
	private ArrayList<InsulinTarget> _data;

	public GlycemiaAdapter(ArrayList<InsulinTarget> data, Context c) {
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
			v = vi.inflate(R.layout.list_target_row, parent, false);
		}

		LinearLayout rLayout = (LinearLayout) v.findViewById(R.id.TargetBGRow);

		TextView targetName = (TextView) v.findViewById(R.id.list_targetName);
		TextView targetStart = (TextView) v.findViewById(R.id.list_targetStart);
		TextView targetEnd = (TextView) v.findViewById(R.id.list_targetEnd);
		TextView targetvalue = (TextView) v.findViewById(R.id.list_targetValue);


		InsulinTarget target = _data.get(position);
		rLayout.setTag(target);
		targetName.setText(target.getName());
		targetStart.setText(target.getStart());
		targetEnd.setText(target.getEnd());

		targetvalue.setText(String.valueOf((int) target.getTarget()));

		rLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), TargetBG_detail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(((InsulinTarget) v.getTag()).getId()));
				args.putParcelable(TargetBG_detail.BUNDLE_DATA, ((InsulinTarget) v.getTag()));

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		return v;
	}

}
