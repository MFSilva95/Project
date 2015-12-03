package pt.it.porto.mydiabetes.ui.recyclerviewAdapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListDataSource;

public class GenericMultiTypeAdapter extends RecyclerView.Adapter<GenericMultiTypeAdapter.Holder> {
	private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private Cursor cursor;
	private ArrayList<String> tables;
	private int[] resourceIcons;

	public GenericMultiTypeAdapter(Cursor cursor, String[] tables, int[] resourceIcons) {
		this.cursor = cursor;
		this.tables = new ArrayList<>(Arrays.asList(tables));
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
		} catch (ParseException e) {
			e.printStackTrace();
		}
		holder.icon.setImageResource(resourceIcons[tables.indexOf(cursor.getString(cursor.getColumnIndex(ListDataSource.ROW_TABLE_NAME)))]);
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
		TextView value;

		public Holder(View itemView) {
			super(itemView);
			date = (TextView) itemView.findViewById(R.id.txt_date);
			value = (TextView) itemView.findViewById(R.id.txt_value);
			icon = (CircleImageView) itemView.findViewById(R.id.img_icon);
			icon.setVisibility(View.VISIBLE);
		}
	}
}
