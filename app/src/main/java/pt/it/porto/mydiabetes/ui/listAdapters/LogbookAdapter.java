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
		}

		TextView data = (TextView) v.findViewById(R.id.tv_list_logbookreg_data);
		TextView hora = (TextView) v.findViewById(R.id.tv_list_logbookreg_hora);
		TextView insulinValue = (TextView) v.findViewById(R.id.tv_list_logbookreg_insulin_value);
		TextView insulinName = (TextView) v.findViewById(R.id.tv_list_logbookreg_insulin);
		TextView gvalue = (TextView) v.findViewById(R.id.tv_list_logbookreg_glycemia_value);
		TextView gtag = (TextView) v.findViewById(R.id.tv_list_logbookreg_glycemia);
		TextView cvalue = (TextView) v.findViewById(R.id.tv_list_logbookreg_carbs_value);
		TextView ctag = (TextView) v.findViewById(R.id.tv_list_logbookreg_carbs_title);
		TextView tag = (TextView) v.findViewById(R.id.tv_list_logbookreg_tag);

		LogbookItem logbook_datab = getItem(position);

		v.setTag(logbook_datab);

		data.setText(logbook_datab.getFormattedDate());
		hora.setText(logbook_datab.getFormattedTime());
		tag.setText(logbook_datab.tag);

		insulinValue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", logbook_datab.insulinVal));
		insulinName.setText(logbook_datab.insulinName);

		gvalue.setText(String.valueOf(logbook_datab.glycemia));
		cvalue.setText(String.valueOf(logbook_datab.carbs));

		if (logbook_datab.insulinId != -1) {
			insulinValue.setVisibility(View.VISIBLE);
			insulinName.setVisibility(View.VISIBLE);
		} else {
			insulinValue.setVisibility(View.INVISIBLE);
			insulinName.setVisibility(View.INVISIBLE);
		}
		if (logbook_datab.glycemiaId != -1) {
			gvalue.setVisibility(View.VISIBLE);
			gtag.setVisibility(View.VISIBLE);
		} else {
			gvalue.setVisibility(View.INVISIBLE);
			gtag.setVisibility(View.INVISIBLE);
		}
		if (logbook_datab.carbsId != -1) {
			cvalue.setVisibility(View.VISIBLE);
			ctag.setVisibility(View.VISIBLE);
		} else {
			cvalue.setVisibility(View.INVISIBLE);
			ctag.setVisibility(View.INVISIBLE);
		}

		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), DetailLogbookActivity.class);
				Bundle args = new Bundle();
				LogbookItem logbookDataBinding = (LogbookItem) v.getTag();
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

	class LogbookItem {
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

}