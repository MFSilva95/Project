package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.charts.data.Logbook;

public class GlycemiaChartList extends LogbookChartList {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras == null) {
				extras = new Bundle();
			}
			Logbook data = new Logbook(this);
			data.toggleFilter(0);
			data.toggleFilter(2);
			extras.putParcelable(MultiDataChartActivity.EXTRAS_CHART_DATA, data);
			getIntent().putExtras(extras);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public String getName() {
		return getString(R.string.title_activity_glycemia);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.glycemia, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuItem_Glycemia) {
			Intent intent = new Intent(this, GlycemiaDetail.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
