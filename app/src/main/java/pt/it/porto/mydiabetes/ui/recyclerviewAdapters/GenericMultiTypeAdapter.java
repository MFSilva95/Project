package pt.it.porto.mydiabetes.ui.recyclerviewAdapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.it.porto.mydiabetes.R;

public class GenericMultiTypeAdapter extends RecyclerView.Adapter<GenericMultiTypeAdapter.Holder> {
	private Cursor cursor;
	private HashMap<String, Integer> positions;
	private int[] resourceIcons;

	public GenericMultiTypeAdapter(Cursor cursor, ArrayList<String> tables, int[] resourceIcons) {
		this.cursor = cursor;
		positions = new HashMap<>(tables.size());
		for (int i = 0; i < tables.size(); i++) {
			positions.put(tables.get(i), i);
		}
		this.resourceIcons = resourceIcons;
	}

	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_multi_type, parent, false));
	}

	@Override
	public void onBindViewHolder(Holder holder, int position) {
		cursor.moveToPosition(position);
		holder.value.setText(cursor.getString(0));
		holder.date.setText(cursor.getString(1));
		holder.time.setText(cursor.getString(2));
		holder.icon.setImageResource(resourceIcons[positions.get(cursor.getString(3))]);
		if (!cursor.isNull(4)) {
			holder.extra.setText(cursor.getString(4));
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
