package pt.it.porto.mydiabetes.ui.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.database.ListDataSource;
import pt.it.porto.mydiabetes.database.MyDiabetesContract;
import pt.it.porto.mydiabetes.database.MyDiabetesStorage;
import pt.it.porto.mydiabetes.ui.recyclerviewAdapters.GenericMultiTypeAdapter;

public class ListTestActivity extends Activity {

	private ArrayList<String> tables;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_test);

		RecyclerView listView = (RecyclerView) findViewById(R.id.list_vals);

		listView.setHasFixedSize(true);
		listView.setAdapter(getAdapter());
		listView.setLayoutManager(new LinearLayoutManager(this));
		listView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());

	}


	public GenericMultiTypeAdapter getAdapter() {
		Calendar timeStart = Calendar.getInstance();
		timeStart.roll(Calendar.YEAR, false);

		Calendar timeEnd = Calendar.getInstance();


		String[] tables = new String[]{MyDiabetesContract.Regist.Insulin.TABLE_NAME + " JOIN " + MyDiabetesContract.Insulin.TABLE_NAME,
				MyDiabetesContract.Regist.CarboHydrate.TABLE_NAME};
		Cursor cursor = new ListDataSource(MyDiabetesStorage.getInstance(this))
				.getMultiData(tables,
						new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_VALUE, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_VALUE},
						new String[]{MyDiabetesContract.Regist.Insulin.COLUMN_NAME_DATETIME, MyDiabetesContract.Regist.CarboHydrate.COLUMN_NAME_DATETIME},
						new String[]{MyDiabetesContract.Insulin.COLUMN_NAME_NAME, null},
						AbstractChartActivity.dateFormat.format(timeStart.getTime()),
						AbstractChartActivity.dateFormat.format(timeEnd.getTime()), 100);
		this.tables = new ArrayList<>(Arrays.asList(tables));
		return new GenericMultiTypeAdapter(cursor, this.tables, new int[]{R.drawable.insulin, R.drawable.carbs});
	}
}
