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
import pt.it.porto.mydiabetes.ui.activities.CarboHydrateDetail;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class CarbsAdapter extends BaseAdapter {

	private Cursor cursor;
	private Context context;

	public CarbsAdapter(Cursor cursor, Context c) {
		this.cursor = cursor;
		context = c;
	}


	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public CarbsItem getItem(int position) {
		cursor.moveToPosition(position);
		int pox = 0;
		return new CarbsItem(cursor.getInt(pox++), cursor.getString(pox++), cursor.getInt(pox++), cursor.getString(pox));

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_carbs_row, parent, false);
			v.setTag(new ViewHolder(v));
		}

		ViewHolder viewHolder = (ViewHolder) v.getTag();


		CarbsItem carb = getItem(position);

		viewHolder.id = carb.id;
		viewHolder.carbvalue.setText(String.valueOf(carb.value));

		viewHolder.date.setText(carb.getFormattedDate());
		viewHolder.hour.setText(carb.getFormattedTime());

		viewHolder.tag.setText(carb.tag);


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), CarboHydrateDetail.class);
				Bundle args = new Bundle();
				args.putInt("Id", ((ViewHolder) v.getTag()).id); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}

		});


		return v;
	}


	private class CarbsItem {
		int id;
		int value;
		Calendar dateTime;
		String tag;

		public CarbsItem(int id, String dateTime, int value, String tag) {
			this.id = id;
			try {
				this.dateTime = DateUtils.parseDateTime(dateTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.value = value;
			this.tag = tag;
		}

		public String getFormattedDate() {
			return DateUtils.getFormattedDate(dateTime);
		}

		public String getFormattedTime() {
			return DateUtils.getFormattedTime(dateTime);
		}
	}

	private class ViewHolder {
		TextView carbvalue;
		TextView date;
		TextView hour;
		TextView tag;

		int id;

		ViewHolder(View view) {
			carbvalue = (TextView) view.findViewById(R.id.tv_list_carbs_value);
			date = (TextView) view.findViewById(R.id.tv_list_carbs_data);
			hour = (TextView) view.findViewById(R.id.tv_list_carbs_hora);
			tag = (TextView) view.findViewById(R.id.tv_list_carbs_tag);
		}

	}
}
