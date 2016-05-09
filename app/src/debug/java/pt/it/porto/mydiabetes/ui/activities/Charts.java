package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.ui.charts.data.*;
import pt.it.porto.mydiabetes.ui.charts.data.Weight;

public class Charts extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charts);
		getSupportActionBar();
	}

	public void singleData(View view) {
		Intent intent = new Intent(this, SingleDataChartActivity.class);
		startActivity(intent);
	}

	public void multiData(View view) {
		Intent intent = new Intent(this, MultiDataChartActivity.class);
		startActivity(intent);
	}


	public void weight(View view) {
		Intent intent = new Intent(this, MultiDataChartActivity.class);
		Bundle bundle=new Bundle();
		bundle.putParcelable(MultiDataChartActivity.EXTRAS_CHART_DATA, new Weight(this));
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void carbs(View view) {
		Intent intent = new Intent(this, MultiDataChartActivity.class);
		Bundle bundle=new Bundle();
		bundle.putParcelable(MultiDataChartActivity.EXTRAS_CHART_DATA, new Carbs(this));
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
