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
import pt.it.porto.mydiabetes.ui.activities.DetailLogbookActivity;
import pt.it.porto.mydiabetes.ui.dataBinding.CarbsDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.GlycemiaDataBinding;
import pt.it.porto.mydiabetes.ui.dataBinding.InsulinRegDataBinding;
import pt.it.porto.mydiabetes.utils.DateUtils;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

public class LogbookAdapter extends BaseAdapter {

	Context _c;
	private Cursor cursor;

	public LogbookAdapter(Cursor cursor, Context c) {
		this.cursor = cursor;
		this._c = c;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public LogbookItem getItem(int position) {
		cursor.moveToPosition(position);
		int pox = 0;
		return new LogbookItem(cursor.getString(pox++), cursor.getString(pox++), cursor.getInt(pox++), cursor.getFloat(pox++), cursor.getString(pox++), cursor.getInt(pox++), cursor.getInt(pox++), cursor.getInt(pox++), cursor.getInt(pox));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_logbook_row, parent, false);
			v.setTag(new ViewHolder(v));
		}

		ViewHolder viewHolder = (ViewHolder) v.getTag();


		LogbookItem logbook_datab = getItem(position);

		viewHolder.item = logbook_datab;

		viewHolder.data.setText(logbook_datab.getFormattedDate());
		viewHolder.hora.setText(logbook_datab.getFormattedTime());
		viewHolder.tag.setText(logbook_datab.tag);

		if (logbook_datab.insulinId != -1) {
			viewHolder.insulinValue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", logbook_datab.insulinVal));
			viewHolder.insulinName.setText(logbook_datab.insulinName);
			viewHolder.insulinValue.setVisibility(View.VISIBLE);
			viewHolder.insulinName.setVisibility(View.VISIBLE);
		} else {
			viewHolder.insulinValue.setVisibility(View.INVISIBLE);
			viewHolder.insulinName.setVisibility(View.INVISIBLE);
		}
		if (logbook_datab.glycemiaId != -1) {
			viewHolder.gvalue.setText(String.valueOf(logbook_datab.glycemia));
			viewHolder.gvalue.setVisibility(View.VISIBLE);
			viewHolder.gtag.setVisibility(View.VISIBLE);
		} else {
			viewHolder.gvalue.setVisibility(View.INVISIBLE);
			viewHolder.gtag.setVisibility(View.INVISIBLE);
		}
		if (logbook_datab.carbsId != -1) {
			viewHolder.cvalue.setText(String.valueOf(logbook_datab.carbs));
			viewHolder.cvalue.setVisibility(View.VISIBLE);
			viewHolder.ctag.setVisibility(View.VISIBLE);
		} else {
			viewHolder.cvalue.setVisibility(View.INVISIBLE);
			viewHolder.ctag.setVisibility(View.INVISIBLE);
		}

		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), DetailLogbookActivity.class);
				Bundle args = new Bundle();
				LogbookItem logbookDataBinding = ((ViewHolder) v.getTag()).item;
				if (logbookDataBinding.glycemiaId != -1) {
					GlycemiaDataBinding glycemiaDataBinding = new GlycemiaDataBinding();
					glycemiaDataBinding.setId(logbookDataBinding.glycemiaId);
					args.putString("bg", String.valueOf(glycemiaDataBinding.getId())); //bg id
					args.putParcelable(DetailLogbookActivity.ARG_BLOOD_GLUCOSE, glycemiaDataBinding);
				}
				if (logbookDataBinding.carbsId != -1) {
					CarbsDataBinding carbs = new CarbsDataBinding();
					carbs.setId(logbookDataBinding.carbsId);
					args.putString("ch", String.valueOf(carbs.getId())); //ch id
					args.putParcelable(DetailLogbookActivity.ARG_CARBS, carbs);
				}
				if (logbookDataBinding.insulinId != -1) {
					InsulinRegDataBinding insulin = new InsulinRegDataBinding();
					insulin.setId(logbookDataBinding.insulinId);
					args.putString("ins", String.valueOf(insulin.getId())); //ins id
					args.putParcelable(DetailLogbookActivity.ARG_INSULIN, insulin);
				}
				intent.putExtras(args);
				v.getContext().startActivity(intent);
			}
		});

		return v;
	}

	private class LogbookItem {
		Calendar dateTime;
		String tag;
		int carbs;
		float insulinVal;
		String insulinName;
		int glycemia;
		int carbsId;
		int insulinId;
		int glycemiaId;

		public LogbookItem(String dateTime, String tag, int carbs, float insulinVal, String insulinName, int glycemia, int carbsId, int insulinId, int glycemiaId) {
			try {
				this.dateTime = DateUtils.parseDateTime(dateTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.tag = tag;
			this.carbs = carbs;
			this.insulinVal = insulinVal;
			this.insulinName = insulinName;
			this.glycemia = glycemia;
			this.carbsId = carbsId;
			this.insulinId = insulinId;
			this.glycemiaId = glycemiaId;
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
		TextView insulinValue;
		TextView insulinName;
		TextView gvalue;
		TextView cvalue;
		TextView ctag;
		TextView tag;
		TextView gtag;

		public ViewHolder(View view) {
			data = (TextView) view.findViewById(R.id.tv_list_logbookreg_data);
			hora = (TextView) view.findViewById(R.id.tv_list_logbookreg_hora);
			insulinValue = (TextView) view.findViewById(R.id.tv_list_logbookreg_insulin_value);
			insulinName = (TextView) view.findViewById(R.id.tv_list_logbookreg_insulin);
			gvalue = (TextView) view.findViewById(R.id.tv_list_logbookreg_glycemia_value);
			gtag = (TextView) view.findViewById(R.id.tv_list_logbookreg_glycemia);
			cvalue = (TextView) view.findViewById(R.id.tv_list_logbookreg_carbs_value);
			ctag = (TextView) view.findViewById(R.id.tv_list_logbookreg_carbs_title);
			tag = (TextView) view.findViewById(R.id.tv_list_logbookreg_tag);
		}

		LogbookItem item;
	}
}