package pt.it.porto.mydiabetes.ui.recyclerviewAdapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListDataSource;

public class GenericSingleTypeAdapter extends RecyclerView.Adapter<GenericSingleTypeAdapter.Holder> {
	private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private Cursor cursor;

	public GenericSingleTypeAdapter(Cursor cursor) {
		this.cursor = cursor;
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
		TextView date;
		TextView value;

		public Holder(View itemView) {
			super(itemView);
			date = (TextView) itemView.findViewById(R.id.txt_date);
			value = (TextView) itemView.findViewById(R.id.txt_value);
		}
	}
}
