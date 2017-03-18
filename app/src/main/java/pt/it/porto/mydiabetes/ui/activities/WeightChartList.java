package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.utils.LocaleUtils;

public class WeightChartList extends SingleDataChartActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras == null) {
				extras = new Bundle();
			}
			extras.putParcelable(MultiDataChartActivity.EXTRAS_CHART_DATA, new pt.it.porto.mydiabetes.ui.charts.data.Weight(this));
			Calendar calendar = Calendar.getInstance();
			extras.putSerializable(MultiDataChartActivity.EXTRAS_TIME_END, calendar);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.add(Calendar.DAY_OF_YEAR, -8);
			extras.putSerializable(MultiDataChartActivity.EXTRAS_TIME_START, calendar2);
			getIntent().putExtras(extras);

		}

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.plus_btn));
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getBaseContext(), WeightDetail.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public String getName() {
		return getString(R.string.title_activity_weight);
	}

	@Override
	public RecyclerView.Adapter getRecyclerViewAdapter() {
		return new ListAdapter(getCursor());

	}

	class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder> {

		private Cursor cursor;

		public ListAdapter(Cursor cursor) {
			super();
			this.cursor = cursor;
		}

		@Override
		public void onBindViewHolder(Holder holder, int position) {
			cursor.moveToPosition(position);
			holder.id = cursor.getInt(5);
			holder.date.setText(cursor.getString(1));
			holder.time.setText(cursor.getString(2));
			holder.value.setText(String.format(LocaleUtils.MY_LOCALE, "%.1f", cursor.getFloat(0)));
			holder.onClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), WeightDetail.class);
					Bundle args = new Bundle();
					args.putInt("Id", ((Holder) v.getTag()).id);
					intent.putExtras(args);
					v.getContext().startActivity(intent);
				}
			});
		}

		@Override
		public ListAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
			return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_weight_row, parent, false));
		}


		@Override
		public int getItemCount() {
			return cursor.getCount();
		}

		class Holder extends RecyclerView.ViewHolder {
			TextView date;
			TextView time;
			TextView value;
			int id;

			public Holder(View itemView) {
				super(itemView);
				date = (TextView) itemView.findViewById(R.id.txt_date);
				time = (TextView) itemView.findViewById(R.id.txt_time);
				value = (TextView) itemView.findViewById(R.id.txt_value);
			}

			public void onClickListener(View.OnClickListener listener) {
				itemView.setOnClickListener(listener);
				itemView.setTag(this);
			}
		}

	}

}