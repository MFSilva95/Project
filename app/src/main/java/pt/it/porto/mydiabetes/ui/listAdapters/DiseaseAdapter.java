package pt.it.porto.mydiabetes.ui.listAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.activities.DiseaseDetail;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class DiseaseAdapter extends BaseAdapter {

	Context _c;
	private Cursor cursor;

	public DiseaseAdapter(Cursor cursor, Context c) {
		this.cursor = cursor;
		_c = c;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public DiseaseReg getItem(int position) {
		cursor.moveToPosition(position);
		int pox = 0;
		return new DiseaseReg(cursor.getInt(pox++), cursor.getString(pox++), cursor.getString(pox++), cursor.getString(pox));
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).id;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_diseasereg_row, parent, false);
			v.setTag(new ViewHolder(v));
		}
		ViewHolder viewHolder = (ViewHolder) v.getTag();

		DiseaseReg dis = getItem(position);
		viewHolder.item = dis;

		viewHolder.diseaseName.setText(dis.diseaseName);
		viewHolder.timeStart.setText(dis.getFormattedDate());
		if (dis.timeEnd == null) {
			viewHolder.timeEnd.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.timeEnd.setText(dis.timeEnd);
			viewHolder.timeEnd.setVisibility(View.VISIBLE);
		}

		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), DiseaseDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(((ViewHolder) v.getTag()).item.id));

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		return v;
	}

	private class DiseaseReg {
		int id;
		String diseaseName;
		Calendar timeStart;
		String timeEnd;

		public DiseaseReg(int id, String diseaseName, String timeStart, String timeEnd) {
			this.id = id;
			this.diseaseName = diseaseName;
			try {
				this.timeStart = DateUtils.parseDateTime(timeStart);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.timeEnd = timeEnd;
		}

		public String getFormattedDate() {
			return DateUtils.getFormattedDate(timeStart);
		}

		public String getFormattedTime() {
			return DateUtils.getFormattedTime(timeStart);
		}
	}

	private class ViewHolder {
		TextView diseaseName;
		TextView timeStart;
		TextView timeEnd;
		DiseaseReg item;

		public ViewHolder(View view) {
			diseaseName = (TextView) view.findViewById(R.id.tv_list_diseasereg_disease);
			timeStart = (TextView) view.findViewById(R.id.tv_list_diseasereg_startdate);
			timeEnd = (TextView) view.findViewById(R.id.tv_list_diseasereg_enddate);
		}
	}
}
