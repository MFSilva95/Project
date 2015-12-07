package pt.it.porto.mydiabetes.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pt.it.porto.mydiabetes.R;

public class Charts extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charts);
		getSupportActionBar();
	}

	public void singleData(View view) {
		Intent intent=new Intent(this, SingleDataChartActivity.class);
		startActivity(intent);
	}

	public void multiData(View view) {
		Intent intent=new Intent(this, MultiDataChartActivity.class);
		startActivity(intent);
	}
}
