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
import pt.it.porto.mydiabetes.ui.activities.GlycemiaDetail;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class GlycemiaRegAdapter extends BaseAdapter {

	private Cursor cursor;
	private Context context;

	public GlycemiaRegAdapter(Cursor cursor, Context c) {
		this.cursor = cursor;
		context = c;
	}


	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public GlycemiaItem getItem(int position) {
		cursor.moveToPosition(position);
		int pox = 0;
		return new GlycemiaItem(cursor.getInt(pox++), cursor.getString(pox++), cursor.getInt(pox++), cursor.getString(pox));
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
			v = vi.inflate(R.layout.list_glycemiareg_row, parent, false);
			v.setTag(new ViewHolder(v));
		}

		ViewHolder viewHolder = (ViewHolder) v.getTag();
		GlycemiaItem glycemia = getItem(position);

		viewHolder.id = glycemia.id;

		viewHolder.date.setText(glycemia.getFormattedDate());
		viewHolder.time.setText(glycemia.getFormattedTime());
		viewHolder.value.setTag(String.valueOf(glycemia.id));
		viewHolder.value.setText(String.valueOf(glycemia.value));
		viewHolder.tag.setText(glycemia.tag);


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				Intent intent = new Intent(v.getContext(), GlycemiaDetail.class);
				Bundle args = new Bundle();
				args.putInt("Id", ((ViewHolder) v.getTag()).id); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}

		});


		return v;
	}

	private class GlycemiaItem {
		int id;
		int value;
		Calendar dateTime;
		String tag;

		public GlycemiaItem(int id, String dateTime, int value, String tag) {
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
		TextView date;
		TextView time;
		TextView value;
		TextView tag;
		int id;

		public ViewHolder(View view) {
			date = (TextView) view.findViewById(R.id.tv_list_glicemia_data);
			time = (TextView) view.findViewById(R.id.tv_list_glicemia_hora);
			value = (TextView) view.findViewById(R.id.tv_list_glicemia_value);
			tag = (TextView) view.findViewById(R.id.tv_list_glicemia_tag);
		}


	}

}
