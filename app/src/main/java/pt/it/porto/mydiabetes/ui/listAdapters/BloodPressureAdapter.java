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
import pt.it.porto.mydiabetes.database.DB_Read;
import pt.it.porto.mydiabetes.ui.activities.BloodPressureDetail;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class BloodPressureAdapter extends BaseAdapter {

	private Cursor cursor;
	Context _c;

	public BloodPressureAdapter(Cursor cursor, Context c) {
		this.cursor = cursor;
		_c = c;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public BloodPressureReg getItem(int position) {
		cursor.moveToPosition(position);
		int pox=0;
		return new BloodPressureReg(cursor.getInt(pox++), cursor.getString(pox++), cursor.getInt(pox++), cursor.getInt(pox));
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
			v = vi.inflate(R.layout.list_bloodpressure_row, parent, false);
			v.setTag(new ViewHolder(v));
		}

		ViewHolder viewHolder= (ViewHolder) v.getTag();

		BloodPressureReg bp = getItem(position);
		viewHolder.item=bp;

		viewHolder.date.setText(bp.getFormattedDate());
		viewHolder.time.setText(bp.getFormattedTime());
		viewHolder.systolic.setText(String.valueOf(bp.systolic));
		viewHolder.diastolic.setText(String.valueOf(bp.diastolic));


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), BloodPressureDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(((ViewHolder) v.getTag()).item.id));

				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		return v;
	}

	private class BloodPressureReg {
		int id;
		Calendar dateTime;
		int systolic;
		int diastolic;

		public BloodPressureReg(int id, String dateTime, int systolic, int diastolic) {
			this.id = id;
			try {
				this.dateTime = DateUtils.parseDateTime(dateTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.systolic = systolic;
			this.diastolic = diastolic;
		}


		public String getFormattedDate() {
			return DateUtils.getFormattedDate(dateTime);
		}

		public String getFormattedTime() {
			return DateUtils.getFormattedTime(dateTime);
		}
	}

	private class ViewHolder {
		TextView date;
		TextView time;
		TextView systolic;
		TextView diastolic;
		BloodPressureReg item;

		public ViewHolder(View view) {
			date = (TextView) view.findViewById(R.id.tv_list_bloodpressure_data);
			time = (TextView) view.findViewById(R.id.tv_list_bloodpressure_hora);
			systolic = (TextView) view.findViewById(R.id.tv_list_bloodpressure_systolic_value);
			diastolic = (TextView) view.findViewById(R.id.tv_list_bloodpressure_diastolic_value);
		}

	}

}
