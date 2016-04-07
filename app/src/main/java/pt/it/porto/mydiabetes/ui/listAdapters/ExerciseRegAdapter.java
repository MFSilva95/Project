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
import pt.it.porto.mydiabetes.ui.activities.ExerciseDetail;
import pt.it.porto.mydiabetes.utils.DateUtils;


public class ExerciseRegAdapter extends BaseAdapter {

	Context _c;
	private Cursor cursor;

	public ExerciseRegAdapter(Cursor cursor, Context c) {
		this.cursor = cursor;
		_c = c;
	}


	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public ExerciseReg getItem(int position) {
		cursor.moveToPosition(position);
		int pox = 0;
		return new ExerciseReg(cursor.getInt(pox++), cursor.getString(pox++), cursor.getString(pox++), cursor.getInt(pox++), cursor.getString(pox));
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
			v = vi.inflate(R.layout.list_exercisereg_row, parent, false);
			v.setTag(new ViewHolder(v));
		}
		ViewHolder viewHolder = (ViewHolder) v.getTag();


		ExerciseReg data = getItem(position);
		viewHolder.item = data;

		viewHolder.data.setText(data.getFormattedDate());
		viewHolder.hora.setText(data.getFormattedTime());
		viewHolder.exercise.setText(data.exercise);
		viewHolder.duration.setText(String.valueOf(data.duration));
		viewHolder.effort.setText(data.effort);


		v.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), ExerciseDetail.class);
				Bundle args = new Bundle();
				args.putString("Id", String.valueOf(((ViewHolder) v.getTag()).item.id)); //Your id
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}

		});


		return v;
	}

	private class ExerciseReg {
		int id;
		String exercise;
		Calendar dateTime;
		int duration;
		String effort;

		public ExerciseReg(int id, String dateTime, String exercise, int duration, String effort) {
			this.id = id;
			try {
				this.dateTime = DateUtils.parseDateTime(dateTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.exercise = exercise;
			this.duration = duration;
			this.effort = effort;
		}


		public String getFormattedDate() {
			return DateUtils.getFormattedDate(dateTime);
		}

		public String getFormattedTime() {
			return DateUtils.getFormattedTime(dateTime);
		}
	}

	private class ViewHolder {
		TextView data;
		TextView hora;
		TextView exercise;
		TextView duration;
		TextView effort;
		ExerciseReg item;

		public ViewHolder(View view) {
			data = (TextView) view.findViewById(R.id.tv_list_exercisereg_data);
			hora = (TextView) view.findViewById(R.id.tv_list_exercisereg_hora);
			exercise = (TextView) view.findViewById(R.id.tv_list_exercisereg_exercise);
			duration = (TextView) view.findViewById(R.id.tv_list_exercisereg_duration);
			effort = (TextView) view.findViewById(R.id.tv_list_exercisereg_effort);
		}

	}

}
