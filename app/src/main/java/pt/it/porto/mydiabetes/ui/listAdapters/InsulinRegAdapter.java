//package pt.it.porto.mydiabetes.ui.listAdapters;
//
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import java.text.ParseException;
//import java.util.Calendar;
//
//import pt.it.porto.mydiabetes.R;
////import pt.it.porto.mydiabetes.ui.activities.InsulinDetail;
//import pt.it.porto.mydiabetes.utils.DateUtils;
//import pt.it.porto.mydiabetes.utils.LocaleUtils;
//
//
//public class InsulinRegAdapter extends BaseAdapter {
//
//	Context context;
//	private Cursor cursor;
//
//	public InsulinRegAdapter(Cursor cursor, Context c) {
//		this.cursor = cursor;
//		this.context = c;
//	}
//
//	@Override
//	public int getCount() {
//		return cursor.getCount();
//	}
//
//	@Override
//	public InsulinRegItem getItem(int position) {
//		cursor.moveToPosition(position);
//		int pox = 0;
//		return new InsulinRegItem(cursor.getInt(pox++), cursor.getString(pox++), cursor.getString(pox++), cursor.getFloat(pox++), cursor.getString(pox++), cursor.getInt(pox));
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		View v = convertView;
//		if (v == null) {
//			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			v = vi.inflate(R.layout.list_insulinreg_row, parent, false);
//			v.setTag(new ViewHolder(v));
//		}
//
//		ViewHolder viewHolder = (ViewHolder) v.getTag();
//
//		InsulinRegItem data = getItem(position);
//		viewHolder.item=data;
//
//
//		viewHolder.date.setText(data.getFormattedDate());
//		viewHolder.time.setText(data.getFormattedTime());
//		viewHolder.insulinValue.setText(String.format(LocaleUtils.ENGLISH_LOCALE, "%.1f", data.insulinVal));
//
//		if (data.glycemia != -1) {
//			viewHolder.gvalue.setText(String.valueOf(data.glycemia));
//			viewHolder.gtag.setVisibility(View.VISIBLE);
//			viewHolder.gvalue.setVisibility(View.VISIBLE);
//			viewHolder.gunit.setVisibility(View.VISIBLE);
//		} else {
//			viewHolder.gvalue.setText("");
//			viewHolder.gtag.setVisibility(View.GONE);
//			viewHolder.gvalue.setVisibility(View.GONE);
//			viewHolder.gunit.setVisibility(View.GONE);
//		}
//		viewHolder.insulin.setText(data.insulinName);
//
//		viewHolder.tag.setText(data.tag);
//
//
//		v.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(v.getContext(), InsulinDetail.class);
//				Bundle args = new Bundle();
//				args.putString("Id", String.valueOf(((ViewHolder) v.getTag()).item.insulinRegId)); //Your id
//				intent.putExtras(args);
//				v.getContext().startActivity(intent);
//			}
//		});
//
//		return v;
//	}
//
//
//	private class InsulinRegItem {
//		int insulinRegId;
//		Calendar dateTime;
//		String tag;
//		float insulinVal;
//		String insulinName;
//		int glycemia;
//
//		public InsulinRegItem(int insulinRegId, String dateTime, String tag, float insulinVal, String insulinName, int glycemia) {
//			this.insulinRegId = insulinRegId;
//			try {
//				this.dateTime = DateUtils.parseDateTime(dateTime);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			this.tag = tag;
//			this.insulinVal = insulinVal;
//			this.insulinName = insulinName;
//			this.glycemia = glycemia;
//		}
//
//		public String getFormattedDate() {
//			return DateUtils.getFormattedDate(dateTime);
//		}
//
//		public String getFormattedTime() {
//			return DateUtils.getFormattedTime(dateTime);
//		}
//	}
//
//	private class ViewHolder {
//		TextView date;
//		TextView time;
//		TextView insulinValue;
//		TextView gtag;
//		TextView gvalue;
//		TextView gunit;
//		TextView tag;
//		TextView insulin;
//		InsulinRegItem item;
//
//		public ViewHolder(View view) {
//			date = (TextView) view.findViewById(R.id.tv_list_insulinreg_data);
//			time = (TextView) view.findViewById(R.id.tv_list_insulinreg_hora);
//			insulinValue = (TextView) view.findViewById(R.id.tv_list_insulinreg_insulin_value);
//			gtag = (TextView) view.findViewById(R.id.tv_list_insulinreg_glycemia);
//			gvalue = (TextView) view.findViewById(R.id.tv_list_insulinreg_glycemia_value);
//			gunit = (TextView) view.findViewById(R.id.tv_list_insulinreg_glycemia_unit);
//			tag = (TextView) view.findViewById(R.id.tv_list_insulinreg_tag);
//			insulin = (TextView) view.findViewById(R.id.tv_list_insulinreg_insulin);
//		}
//
//	}
//}
