package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.MyDiabetesContract;
import pt.it.porto.mydiabetes.ui.recyclerviewAdapters.GenericMultiTypeAdapter;

public class WeightChartList extends MultiDataChartActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		super.onCreate(savedInstanceState);
	}

	@Override
	public String getName() {
		return getString(R.string.title_activity_weight);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.weight, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==R.id.menuItem_Weight){
			Intent intent = new Intent(this, WeightDetail.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public RecyclerView.Adapter getRecyclerViewAdapter() {
		return new ListAdapter(getCursor(), tables, getChartData().getIcons());
	}

	class ListAdapter extends GenericMultiTypeAdapter {

		public ListAdapter(Cursor cursor, ArrayList<String> tables, int[] resourceIcons) {
			super(cursor, tables, resourceIcons);
		}

		@Override
		public void onBindViewHolder(Holder holder, int position) {
			super.onBindViewHolder(holder, position);
			if (cursor.getString(3).equals(MyDiabetesContract.Regist.Weight.TABLE_NAME)) {
				holder.setId(0, cursor.getInt(6));
			} else {
				holder.setId(1, cursor.getInt(6));
			}
			holder.onClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = null;
					switch (((Holder) v.getTag()).table) {
						case 0:
							intent = new Intent(v.getContext(), WeightDetail.class);
							break;
						case 1:
							intent = new Intent(v.getContext(), CarboHydrateDetail.class);
							break;
					}
					Bundle args = new Bundle();
					args.putInt("Id", ((Holder) v.getTag()).id);
					if (intent != null) {
						intent.putExtras(args);
						v.getContext().startActivity(intent);
					}
				}
			});
		}

	}
}
