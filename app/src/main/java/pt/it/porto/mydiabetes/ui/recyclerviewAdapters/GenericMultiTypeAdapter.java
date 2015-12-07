package pt.it.porto.mydiabetes.ui.recyclerviewAdapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListDataSource;

public class GenericMultiTypeAdapter extends RecyclerView.Adapter<GenericMultiTypeAdapter.Holder> {
	private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");

	private Cursor cursor;
	private ArrayList<String> tables;
	private int[] resourceIcons;

	public GenericMultiTypeAdapter(Cursor cursor, ArrayList<String> tables, int[] resourceIcons) {
		this.cursor = cursor;
		this.tables = tables;
		this.resourceIcons = resourceIcons;
	}

	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_multi_type, parent, false));
	}

	@Override
	public void onBindViewHolder(Holder holder, int position) {
		cursor.moveToPosition(position);
		holder.value.setText(cursor.getString(cursor.getColumnIndex(ListDataSource.ROW_VALUE)));
		try {
			Date date = iso8601Format.parse(cursor.getString(cursor.getColumnIndex(ListDataSource.ROW_DATETIME)));
			holder.date.setText(DateFormat.getDateInstance().format(date));
			holder.time.setText(hourFormat.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		holder.icon.setImageResource(resourceIcons[tables.indexOf(cursor.getString(cursor.getColumnIndex(ListDataSource.ROW_TABLE_NAME)))]);
		String extras = cursor.getString(cursor.getColumnIndex(ListDataSource.ROW_EXTRAS));
		if (!TextUtils.isEmpty(extras)) {
			holder.extra.setText(extras);
			holder.extra.setVisibility(View.VISIBLE);
		} else {
			holder.extra.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemViewType(int position) {
		cursor.moveToPosition(position);
		return super.getItemViewType(position);
	}

	@Override
	public int getItemCount() {
		return cursor.getCount();
	}


	static class Holder extends RecyclerView.ViewHolder {
		CircleImageView icon;
		TextView date;
		TextView time;
		TextView value;
		TextView extra;

		public Holder(View itemView) {
			super(itemView);
			date = (TextView) itemView.findViewById(R.id.txt_date);
			value = (TextView) itemView.findViewById(R.id.txt_value);
			icon = (CircleImageView) itemView.findViewById(R.id.img_icon);
			time = (TextView) itemView.findViewById(R.id.txt_time);
			extra = (TextView) itemView.findViewById(R.id.txt_extra);
			icon.setVisibility(View.VISIBLE);
			time.setVisibility(View.VISIBLE);
		}
	}
}
