package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.charts.data.Logbook;

public class CarbsChartList extends LogbookChartList {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras == null) {
				extras = new Bundle();
			}
			Logbook data = new Logbook(this);
			data.toggleFilter(1);
			data.toggleFilter(2);
			data.toggleExtra(1);
			data.toggleExtra(2);
			extras.putParcelable(MultiDataChartActivity.EXTRAS_CHART_DATA, data);
			getIntent().putExtras(extras);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public String getName() {
		return getString(R.string.title_activity_carbo_hydrate);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.carbo_hydrate, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuItem_CarboHydrate_Add) {
			Intent intent = new Intent(this, CarboHydrateDetail.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
